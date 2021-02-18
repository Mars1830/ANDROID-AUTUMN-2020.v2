package com.example.game

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference


class ChessBoardView : FrameLayout, View.OnTouchListener {

    companion object {
        const val VIEW_TAG_INDICATOR = "VIEW_TAG_INDICATOR"
    }

    private var darkSquareColor: Int = Color.BLACK
    private var lightSquareColor: Int = Color.WHITE
    private lateinit var appContext: Context
    private var whitePlayer: Boolean = true

    private var clickedChessmanPosition: String = ""
    private var clickedActor: Actor? = null
    private var nextChessmanPosition: String = ""
    private var isChessmanMoving = false
    private var disabled = true
    public var Disabled
        get() = disabled
      set(value) {disabled = value }

    public var listener: Mover? = null


    private var squareWidth: Int = -1

    private lateinit var chessBoardDrawable: ChessBoardDrawable

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ChessBoardView)

        darkSquareColor = ta.getColor(R.styleable.ChessBoardView_dark_color, Color.BLACK)
        lightSquareColor = ta.getColor(R.styleable.ChessBoardView_light_color, Color.WHITE)
        chessBoardDrawable = ChessBoardDrawable(darkSquareColor, lightSquareColor)
        View(context).apply {
            background = ta.getDrawable(R.styleable.ChessBoardView_indicator)
                ?: ContextCompat.getDrawable(context, R.drawable.default_indicator)
            visibility = View.GONE
            tag = VIEW_TAG_INDICATOR
            addView(this)
        }

        ta.recycle()

        setOnTouchListener(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)

        background ?: run {
            chessBoardDrawable.setBounds(0, 0, width, width)
            background = chessBoardDrawable
        }

        if (squareWidth == -1) squareWidth = width / 8
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {

            MotionEvent.ACTION_DOWN -> {
                Log.d("CHESS", "onTouch_DOWN + state=" + event.metaState )
                if (!isChessmanMoving && clickedChessmanPosition.isNotEmpty()) {

                    val newX = 1 + (event.x / squareWidth).toInt()
                    val newY = 8 - (event.y / squareWidth).toInt()
                    val nextMove = ChessUtil.convertPositionToTag(newX, newY)
                    if (!disabled && (event.metaState == 1 ||  ValidateMove(nextMove)))  // validate only if not killing
                        nextChessmanPosition = ChessUtil.convertPositionToTag(newX, newY)
                }

                return true
            }

            MotionEvent.ACTION_UP -> {
                Log.d("CHESS", "onTouch_UP")
                if (!isChessmanMoving && nextChessmanPosition.isNotEmpty()) {
                    moveChessman()
                }

                return true
            }

            else -> return super.onTouchEvent(event)
        }
    }

    fun ValidateMove(nextMove: String): Boolean {
       return ValidateMove(nextMove, null)
    }

    fun ValidateMove(view: View, nextActor: Actor): Boolean {
        return ValidateMove(view.tag as String, nextActor)
    }

    fun ValidateMove(move:String, nextActor: Actor?): Boolean {
        val validator: StepValidator = StepValidator(this, clickedChessmanPosition, move, clickedActor!!, nextActor)
        val res:Boolean = validator.Validate(whitePlayer)
        if (!res)
            Toast.makeText(
                    appContext,
                    "Неверный ход",
                    Toast.LENGTH_SHORT
            ).show()
        return res
    }

    private fun moveChessman() {
        doMove(clickedChessmanPosition, nextChessmanPosition)
        listener?.DoMove(clickedChessmanPosition, nextChessmanPosition)

        clickedChessmanPosition = ""
        clickedActor = null
        nextChessmanPosition = ""

    }
    public fun doMove(currentPosition: String,  nextPosition:String) {
        Log.d("CHESS", "moveChessman")
        val chessmanView = findViewWithTag<ChessmanView>(currentPosition) ?: return

        isChessmanMoving = true

        findViewWithTag<View>(VIEW_TAG_INDICATOR).visibility = View.GONE

        val nextX = 1f * squareWidth * (ChessUtil.getXPositionFromTag(nextPosition) - 1)
        val nextY = 1f * squareWidth * (8 - ChessUtil.getYPositionFromTag(nextPosition))

        val xAxisAnim = ObjectAnimator.ofFloat(
            chessmanView,
            "translationX",
            chessmanView.translationX,
            nextX
        )
        val yAxisAnim = ObjectAnimator.ofFloat(
            chessmanView,
            "translationY",
            chessmanView.translationY,
            nextY
        )

        AnimatorSet().apply {
            playTogether(xAxisAnim, yAxisAnim)
            duration = 200L
            interpolator = LinearInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationCancel(animation: Animator?) = Unit
                override fun onAnimationStart(animation: Animator?) = Unit
                override fun onAnimationRepeat(animation: Animator?) = Unit
                override fun onAnimationEnd(animation: Animator?) {
                    isChessmanMoving = false
                }
            })
            start()
        }
        chessmanView.tag = nextPosition
    }

    public fun doRemove(position: String) {
        val v: View = findViewWithTag<View>(position)
        removeView(v)
    }

    private fun killChessman(prnt: View, chess:View) {
        if (!disabled && ValidateMove(chess, (chess as ChessmanView).chessman.actor)) {
            val chessCoords = IntArray(2)
            val parentCoords = IntArray(2)
            chess.getLocationOnScreen(chessCoords)
            prnt.getLocationOnScreen(parentCoords)
            val x = chessCoords[0] - parentCoords[0] + 10
            val y = chessCoords[1] - parentCoords[1] + 10
            val downTime = SystemClock.uptimeMillis()
            val eventTime = SystemClock.uptimeMillis()
            val metaState = 1 // kill
            (prnt as ViewGroup).removeView(chess)
            //emulate click
            var event = MotionEvent.obtain(
                downTime, eventTime,
                MotionEvent.ACTION_DOWN,
                x.toFloat(), y.toFloat(),
                metaState
            )
            prnt.dispatchTouchEvent(event)
            event = MotionEvent.obtain(
                downTime, eventTime,
                MotionEvent.ACTION_UP,
                x.toFloat(),
                y.toFloat(),
                metaState
            )
            prnt.dispatchTouchEvent(event)
        }
    }
    private fun addChessman(chessman: Chessman) {

        ChessmanView(context, chessman).apply {

            translationX = 1f * squareWidth * (chessman.xPosition() - 1)
            translationY = 1f * squareWidth * (8 - chessman.yPosition())
            addView(this, FrameLayout.LayoutParams(squareWidth, squareWidth))
            setOnClickListener {
                var prnt: View = this;
                if (!(prnt is ChessBoardView))
                    while ((!(prnt is ChessBoardView)) && prnt.parent != null) {
                        prnt = prnt.parent as View
                    }
                if (clickedChessmanPosition == tag as String) { // double click - undo
                    Log.d("CHESS", "UNDO")
                    isChessmanMoving = false
                    clickedChessmanPosition = ""
                    clickedActor = null
                    nextChessmanPosition = ""
                    val v: View = prnt.findViewWithTag<View>(VIEW_TAG_INDICATOR)
                    v.visibility = View.GONE
                    return@setOnClickListener
                }
                else  if (clickedChessmanPosition.length > 0) {
                    Log.d("CHESS", "KILL")
                    killChessman(prnt, this)
                    listener?.DoRemove(this.tag as String)
                    return@setOnClickListener
                }
                if (isChessmanMoving) return@setOnClickListener
                Log.d("CHESS", "onClick Chessman")
                val layprm = layoutParams as FrameLayout.LayoutParams
                val tX = translationX
                val tY = translationY
                val wp = ChessUtil.IsWhiteColor(this.chessman.actor)
                if (!disabled && ((wp && whitePlayer) || ((!wp) && (!whitePlayer)))) {
                    clickedChessmanPosition = tag as String
                    clickedActor = this.chessman.actor
                    with(prnt.findViewWithTag<View>(VIEW_TAG_INDICATOR)) {
                        this.layoutParams = layprm
                        this.translationX = tX
                        this.translationY = tY
                        visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    fun init(ctx:Context, wp:Boolean) {
        whitePlayer = wp
        appContext = ctx
            addChessman(Chessman("a7", Actor.BP))
            addChessman(Chessman("b7", Actor.BP))
            addChessman(Chessman("c7", Actor.BP))
            addChessman(Chessman("d7", Actor.BP))
            addChessman(Chessman("e7", Actor.BP))
            addChessman(Chessman("f7", Actor.BP))
            addChessman(Chessman("g7", Actor.BP))
            addChessman(Chessman("h7", Actor.BP))

            addChessman(Chessman("a8", Actor.BR))
            addChessman(Chessman("h8", Actor.BR))
            addChessman(Chessman("b8", Actor.BN))
            addChessman(Chessman("g8", Actor.BN))
            addChessman(Chessman("c8", Actor.BB))
            addChessman(Chessman("f8", Actor.BB))
            addChessman(Chessman("d8", Actor.BQ))
            addChessman(Chessman("e8", Actor.BK))

            addChessman(Chessman("a2", Actor.WP))
            addChessman(Chessman("b2", Actor.WP))
            addChessman(Chessman("c2", Actor.WP))
            addChessman(Chessman("d2", Actor.WP))
            addChessman(Chessman("e2", Actor.WP))
            addChessman(Chessman("f2", Actor.WP))
            addChessman(Chessman("g2", Actor.WP))
            addChessman(Chessman("h2", Actor.WP))

            addChessman(Chessman("a1", Actor.WR))
            addChessman(Chessman("h1", Actor.WR))
            addChessman(Chessman("b1", Actor.WN))
            addChessman(Chessman("g1", Actor.WN))
            addChessman(Chessman("c1", Actor.WB))
            addChessman(Chessman("f1", Actor.WB))
            addChessman(Chessman("d1", Actor.WQ))
            addChessman(Chessman("e1", Actor.WK))
        }


    }

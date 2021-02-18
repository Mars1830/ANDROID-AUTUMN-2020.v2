package com.example.game

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.ContextCompat

class ChessmanView : ImageView {

    lateinit var chessman: Chessman

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, chessman: Chessman) : super(context) {
        this.chessman = chessman
        val pieceResId =
                when (chessman.actor) {
                    Actor.WP -> R.drawable.wp
                    Actor.WB -> R.drawable.wb
                    Actor.WK -> R.drawable.wk
                    Actor.WN -> R.drawable.wn
                    Actor.WQ -> R.drawable.wq
                    Actor.WR -> R.drawable.wr

                    Actor.BP -> R.drawable.bp
                    Actor.BB -> R.drawable.bb
                    Actor.BK -> R.drawable.bk
                    Actor.BN -> R.drawable.bn
                    Actor.BQ -> R.drawable.bq
                    Actor.BR -> R.drawable.br
                }
        background = ContextCompat.getDrawable(context, pieceResId)
        tag = chessman.getTag()
    }

}
package com.example.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class UserFragment : Fragment() {
    private val usr =  FirebaseAuth.getInstance().currentUser
    private lateinit var profile : UserProfile
    private lateinit var cntr: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profile = UserProfile()    }

    private fun fill(u:UserProfile) {
        cntr.findViewById<TextView>(R.id.txt_Name).setText(u.Name)
        cntr.findViewById<TextView>(R.id.txt_email).setText(u.Email)
        cntr.findViewById<ImageView>(R.id.img_AvatarView).setImageResource(u.AvatarId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_user, container, false)
        cntr = v
        profile.ReadData (null, {fill(profile)})
        return v
    }

    public fun reload() {
        profile.ReadData (null, {fill(profile)})
    }

}
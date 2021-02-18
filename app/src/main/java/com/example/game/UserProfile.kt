package com.example.game

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

public class UserProfile {
    public var Name: String = ""
    public var Email: String = ""
    public var uid: String = ""
    public var AvatarId: Int = 0

    private val user = FirebaseAuth.getInstance().currentUser
    private val dbRef = FirebaseDatabase.getInstance().reference

    public fun ReadData(external: String?, fill:(u:UserProfile)-> Unit) {
        if (external.isNullOrBlank() && user != null) {
            uid = user!!.uid!!
        } else if (external != null)
            uid = external!!
        if (!uid.isNullOrBlank()) {
            val me = this
            val ref = dbRef.child("Users").child(uid)

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        try {
                            Name = p0.child("Name").value.toString()
                            Email = p0.child("Email").value.toString()
                            AvatarId = (p0.child("AvatarId").value as Long).toInt()
                            fill(me)
                        } catch (e: Exception) {
                            Log.i("CHESS", e.toString())
                        }
                    }
                }
            })
        }
    }

    public fun WriteDataIfNotExists() {
        if (!uid.isNullOrBlank()) {
            val ref = dbRef.child("Users").child(uid)
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (!p0.exists()) {
                        ref.child("Name").setValue(Name)
                        ref.child("Email").setValue(Email)
                        ref.child("AvatarId").setValue(AvatarId)
                    }
                }
            })
        }
    }

    public fun WriteData(external: String?, end:()->Unit?) {
        if (external.isNullOrBlank() && user != null) {
            uid = user!!.uid!!
        }
        else if (external != null)
            uid = external!!
        if (!uid.isNullOrBlank()) {
            val ref = dbRef.child("Users").child(uid)
            ref.setValue("s")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        try {
                            ref.child("Name").setValue(Name)
                            ref.child("Email").setValue(Email)
                            ref.child("AvatarId").setValue(AvatarId)
                            if (end != null)
                                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if (p0.exists()) {
                                            end()
                                        }
                                    }

                                })
                        } catch (e: Exception) {
                        }
                    }
                }
            })
        }
    }
}

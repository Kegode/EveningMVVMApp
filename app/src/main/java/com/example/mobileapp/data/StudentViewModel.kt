package com.example.mobileapp.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavController
import com.example.mobileapp.models.Student
import com.example.mobileapp.navigation.ROUTE_VIEW_STUDENT
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class StudentViewModel(var navController: NavController,var context: Context) {

    var authRepository: AuthViewModel

    init {
        authRepository= AuthViewModel(navController,context)
    }
    fun saveStudent(filePath: Uri,firstname: String,lastname: String,gender: String,desc: String,
                   ){
        var id = System.currentTimeMillis().toString()
        var storageReference = FirebaseStorage.getInstance().getReference()
            .child("Picture/$id")
        storageReference.putFile(filePath).addOnCompleteListener{
            if (it.isSuccessful){
                storageReference.downloadUrl.addOnSuccessListener {
                    var imageUrl = it.toString()
                    var houseData = Student(imageUrl, firstname, lastname, gender, desc, id)
                    var dbRef = FirebaseDatabase.getInstance().getReference()
                        .child("Students/$id")
                    dbRef.setValue(houseData)
                    Toast.makeText(context,"Student saved successfully",Toast.LENGTH_LONG).show()

                }
            }else{
                Toast.makeText(context,"${it.exception!!.message}",Toast.LENGTH_LONG).show()
            }
        }
    }
    fun viewStudent(student: MutableState<Student>,students: SnapshotStateList<Student>):
    SnapshotStateList<Student>{
        val ref = FirebaseDatabase.getInstance().getReference().child("Students")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                students.clear()
                for (snap in snapshot.children){
                    val value = snap.getValue(Student::class.java)
                    student.value=value!!
                    students.add(value)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_LONG).show()
            }
        })
        return students
    }
    fun updateStudent(
        context: Context,
        navController: NavController,
        filePath: Uri,
        firstname: String,
        lastname: String,
        gender: String,
        desc: String,
        id: String,
        currentImageUrl: String
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Students/$id")

        if (filePath != Uri.EMPTY) {
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("Picture/${UUID.randomUUID()}.jpg")

            imageRef.putFile(filePath)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val updatedStudent = Student(imageUrl, firstname, lastname, gender,
                            desc, id)

                        databaseReference.setValue(updatedStudent)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Update successful",
                                        Toast.LENGTH_SHORT).show()
                                    navController.navigate(ROUTE_VIEW_STUDENT)
                                } else {
                                    Toast.makeText(context, "Update failed: " +
                                            "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Image upload failed: ${exception.message}",
                        Toast.LENGTH_SHORT).show()
                }
        } else {

            val updatedStudent = Student(currentImageUrl, firstname, lastname, gender,desc,id)
            databaseReference.setValue(updatedStudent)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_VIEW_STUDENT)
                    } else {
                        Toast.makeText(context, "Update failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}
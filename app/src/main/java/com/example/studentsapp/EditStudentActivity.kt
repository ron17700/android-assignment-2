package com.example.studentsapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.studentsapp.model.Model
import com.example.studentsapp.model.Student

class EditStudentActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etId: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var cbChecked: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Views
        etName = findViewById(R.id.etName)
        etId = findViewById(R.id.etId)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        cbChecked = findViewById(R.id.cbChecked)

        val btnCancel: Button = findViewById(R.id.btnCancel)
        val btnDelete: Button = findViewById(R.id.btnDelete)
        val btnSave: Button = findViewById(R.id.btnSave)

        // Retrieve student details using the student_id from the Intent
        val originalStudentId = intent.getStringExtra("student_id") ?: return
        Model.shared.getStudentById(originalStudentId) { student ->
            etName.setText(student?.name)
            etId.setText(student?.id)
            etPhone.setText(student?.phone)
            etAddress.setText(student?.address)
            cbChecked.isChecked = student?.isChecked!!
        }

        // Cancel Button
        btnCancel.setOnClickListener {
            finish() // Close and go back to StudentDetailsActivity
        }

        // Delete Button
        btnDelete.setOnClickListener {
            // Confirm deletion
            AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete this student?")
                .setPositiveButton("Yes") { _, _ ->
                    Model.shared.deleteStudentById(originalStudentId) { success ->
                        if (success) {
                            Toast.makeText(
                                this@EditStudentActivity,
                                "Student deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                this@EditStudentActivity,
                                StudentsRecyclerViewActivity::class.java
                            )
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@EditStudentActivity,
                                "Failed to delete student",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Save Button
        btnSave.setOnClickListener {
            val updatedName = etName.text.toString().trim()
            val updatedId = etId.text.toString().trim()
            val updatedPhone = etPhone.text.toString().trim()
            val updatedAddress = etAddress.text.toString().trim()
            val updatedChecked = cbChecked.isChecked

            if (updatedName.isEmpty() || updatedId.isEmpty() || updatedPhone.isEmpty() || updatedAddress.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedStudent = Student(
                id = updatedId,
                name = updatedName,
                phone = updatedPhone,
                address = updatedAddress,
                isChecked = updatedChecked,
                avatarUrl = ""
            )
            Model.shared.updateStudent(originalStudentId, updatedStudent) { updateSuccess ->
                if (updateSuccess) {
                    Toast.makeText(
                        this@EditStudentActivity,
                        "Student updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Pass back the updated student_id
                    val resultIntent = Intent().apply {
                        putExtra("student_id", updatedId)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(
                        this@EditStudentActivity,
                        "Error updating student (duplicate ID)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Handle the back button
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
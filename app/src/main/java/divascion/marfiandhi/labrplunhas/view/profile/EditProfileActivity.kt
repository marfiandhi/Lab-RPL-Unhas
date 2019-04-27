package divascion.marfiandhi.labrplunhas.view.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import org.jetbrains.anko.*
import java.io.IOException
import java.io.OutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mUser: FirebaseUser
    private var gender = "Unknown"
    private var dialog: AlertBuilder<DialogInterface>? = null
    private lateinit var name: String
    private lateinit var nim: String
    private lateinit var nick: String
    private var progressDialog: ProgressDialog? = null
    private lateinit var layout: View
    private lateinit var mStorageRef: StorageReference
    private lateinit var mPw: String

    private lateinit var passwordText: EditText
    private val GALLERY = 1
    private val CAMERA = 2

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        user = intent.getParcelableExtra("user")
        loadView()
        setProgress()
        edit_btn_profile_pic.setOnClickListener { _ ->
            layout = layoutInflater.inflate(R.layout.confirm_password, null)
            passwordText = layout.findViewById(R.id.popup_password)
            dialog = alert{
                title = "Password Confirmation"
                customView = layout
                positiveButton("Confirm") {
                    if(TextUtils.isEmpty(passwordText.text.toString())) {
                        toast("Password wont be null")
                    } else {
                        mPw = passwordText.text.toString()
                        changeProfilePicture()
                    }
                }
            }
            dialog?.show()
        }
        edit_btn_email.setOnClickListener {
            toast("Change email still progress")
        }
        edit_btn_password.setOnClickListener {
            toast("Change password still progress")
        }
    }

    private fun changeProfilePicture() {
        val pictureDialog = AlertDialog.Builder(this)
        val dialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setTitle("Select an action")
        pictureDialog.setItems(dialogItems) {
                _, which ->
            when(which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA)
    }

    private fun setProgress() {
        progressDialog =  indeterminateProgressDialog("Saving change...")
        progressDialog ?.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.toolbar_save ->  {
                if(validateForm()) {
                    try {
                        alertBuilder()
                        dialog?.show()
                    } catch(e: IllegalStateException) {
                        Log.e("dialog", e.message.toString())
                        toast("${e.message}")
                    }
                }
            }
            else -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    private fun alertBuilder() {
        layout = layoutInflater.inflate(R.layout.confirm_password, null)
        passwordText = layout.findViewById(R.id.popup_password)
        dialog = alert{
            title = "Password Confirmation"
            customView = layout
            positiveButton("Confirm") {
                if(TextUtils.isEmpty(passwordText.text.toString())) {
                    toast("Password wont be null")
                } else {
                    val pw = passwordText.text.toString()
                    save(pw)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY) {
            if(data!=null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    edit_profile_pic.imageBitmap = bitmap
                    uploadPicture(contentURI!!)

                } catch(e: IOException) {
                    e.printStackTrace()
                    toast(e.message.toString())
                }
            }
        } else if(requestCode == CAMERA) {
            if(data!=null) {
                val thumbnail = data.extras?.get("data") as Bitmap
                edit_profile_pic.imageBitmap = thumbnail
                val path = data.data
                uploadPicture(path!!)
            }
        }
    }

    private fun uploadPicture(file: Uri) {
        showLoading()
        mStorageRef = FirebaseStorage.getInstance().reference
        val ref= mStorageRef.child("${mUser.uid}/profile.jpeg")
        val uploadTask= ref.putFile(file)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if(task.isSuccessful) {
                task.exception?.let { throw it }
            }
            return@Continuation ref.downloadUrl
        })
            .addOnFailureListener {
                toast(it.message.toString())
                hideLoading()
            }
            .addOnCompleteListener{task ->
                if(task.isSuccessful) {
                    val downloadUrl = task.result
                    val requestUpdateProfile =
                        UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build()
                    val tUser = User(mUser.email, user.name, user.nim, user.pbo, user.pp, user.male, user.role)
                    authProfileUpdate(requestUpdateProfile, mPw, tUser)
                }
            }
    }

    private fun validateForm() : Boolean {
        var valid = true

        this.name = edit_full_name.text.toString()
        if(TextUtils.isEmpty(this.name)) {
            edit_full_name.error = "Required."
            valid = false
        } else {
            edit_full_name.error = null
        }

        this.nick = edit_nickname.text.toString()
        if(TextUtils.isEmpty(nick)) {
            edit_nickname.error = "Required"
            valid = false
        } else {
            val char = nick.toCharArray()
            var error = false
            for(index in char.indices) {
                if(char[index]== ' ') {
                    error = true
                }
            }
            if(error) {
                edit_nickname.error = "Nickname cannot contain Space"
                valid = false
            } else {
                edit_nickname.error = null
            }
        }

        this.nim = edit_nim.text.toString()
        if(TextUtils.isEmpty(this.nim)) {
            edit_nim.error = "Required."
            valid = false
        } else {
            edit_nim.error = null
        }

        return valid
    }

    private fun save(pw: String) {
        showLoading()
        val requestUpdateProfile =
            UserProfileChangeRequest.Builder()
                .setDisplayName(nick)
                .build()
        val tUser = User(mUser.email, name, nim, user.pbo, user.pp, user.male, user.role)
        authProfileUpdate(requestUpdateProfile, pw, tUser)

    }

    private fun authProfileUpdate(req: UserProfileChangeRequest, pw: String, tUser: User){
        mUser.updateProfile(req)
            .addOnSuccessListener {_ ->
                val credentials = EmailAuthProvider.getCredential(user.email.toString(), pw)
                mUser.reauthenticateAndRetrieveData(credentials)
                    ?.addOnSuccessListener {
                        mAuth = FirebaseAuth.getInstance()
                        mUser = mAuth.currentUser!!
                        userDatabaseUpdate(tUser)
                    }?.addOnFailureListener {
                        hideLoading()
                        toast(it.message.toString())
                    }
            }.addOnFailureListener{
                hideLoading()
                toast(it.message.toString())
            }
    }

    private fun userDatabaseUpdate(user: User) {
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("user").child(mUser.uid).setValue(user)
            .addOnCompleteListener {
                this.user = user
                hideLoading()
                toast("Profile saved.")
                finish()
            }
            .addOnFailureListener{
                hideLoading()
                toast(it.message.toString())
            }
    }

    private fun showLoading() {
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
    }

    private fun loadView(){
        Picasso.get().load(mUser.photoUrl.toString())
            .placeholder(R.color.colorBlack).error(R.drawable.ic_launcher_foreground)
            .into(edit_profile_pic)
        edit_full_name.setText(user.name)
        edit_nickname.setText(mUser.displayName)
        gender = if(user.male!!) {
            "Male"
        } else {
            "Female"
        }
        edit_gender.setText(gender)
        edit_nim.setText(user.nim)
    }
}

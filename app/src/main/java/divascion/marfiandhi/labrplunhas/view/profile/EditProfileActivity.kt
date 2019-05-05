@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
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
import divascion.marfiandhi.labrplunhas.BuildConfig
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.User
import divascion.marfiandhi.labrplunhas.view.setting.ChangeEmailActivity
import divascion.marfiandhi.labrplunhas.view.setting.ChangePasswordActivity
import kotlinx.android.synthetic.main.activity_edit_profile.*
import org.jetbrains.anko.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mUser: FirebaseUser
    private var gender = "Unknown"
    private var dialog: AlertBuilder<DialogInterface>? = null
    private lateinit var name: String
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
                title = resources.getString(R.string.password_confirmation_txt)
                customView = layout
                positiveButton(resources.getString(R.string.confirm)) {
                    if(TextUtils.isEmpty(passwordText.text.toString())) {
                        toast(resources.getString(R.string.password_confirmation))
                    } else {
                        mPw = passwordText.text.toString()
                        changeProfilePicture()
                    }
                }
            }
            dialog?.show()
        }
        edit_btn_email.setOnClickListener {
            startActivity(intentFor<ChangeEmailActivity>())
        }
        edit_btn_password.setOnClickListener {
            startActivity(intentFor<ChangePasswordActivity>())
        }
    }

    private fun changeProfilePicture() {
        val pictureDialog = AlertDialog.Builder(this)
        val dialogItems = arrayOf(resources.getString(R.string.select_photo_gallery), resources.getString(R.string.capture_photo_camera))
        pictureDialog.setTitle(getString(R.string.select_an_action))
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
        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    private fun setProgress() {
        progressDialog =  indeterminateProgressDialog(resources.getString(R.string.saving_change))
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
            title = resources.getString(R.string.password_confirmation_txt)
            customView = layout
            positiveButton(resources.getString(R.string.confirm)) {
                if(TextUtils.isEmpty(passwordText.text.toString())) {
                    toast(resources.getString(R.string.password_confirmation))
                } else {
                    val pw = passwordText.text.toString()
                    save(pw)
                }
            }
        }
    }

    override fun onResume() {
        if(mUser.isEmailVerified || user.role=="admin") {
            super.onResume()
        } else {
            toast(getString(R.string.verify_email_prompt))
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY) {
            if(data!=null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    var size: Int = (300000000/bitmap.allocationByteCount)
                    if(size>100) {
                        size = 100
                    }
                    val path = saveImage(bitmap, size)
                    uploadPicture(path!!)
                    edit_profile_pic.imageBitmap = bitmap
                } catch(e: IOException) {
                    e.printStackTrace()
                    toast(e.message.toString())
                }
            }
        } else if(requestCode == CAMERA) {
            if(data!=null) {
                val thumbnail = data.extras?.get("data") as Bitmap
                val path = saveImage(thumbnail, 100)
                try {
                    uploadPicture(path!!)
                    edit_profile_pic.imageBitmap = thumbnail
                } catch(e: Exception) {
                    toast(getString(R.string.image_not_found))
                }
            } else {
                toast(getString(R.string.image_not_found))
                Log.e("path", "${data?.data}")
            }
        }
    }

    private fun saveImage(bitmap: Bitmap, quality: Int): Uri? {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR).toString()
        val month = calendar.get(Calendar.MONTH).toString()
        val date = calendar.get(Calendar.DATE)
        val millis = calendar.timeInMillis

        var stream: OutputStream? = null
        try {
            var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LabRPL")
            if(!file.exists()) {
                Log.e("Dir", "Making directory")
                file.mkdirs()
                Log.e("Dir", "Done create directory ${file.absolutePath}")
            } else {
                Log.e("Dir", "Not making any directory ${file.absolutePath}")
            }
            file = File(file, "Unhas_$year$month$date$millis.jpg")
            Log.e("Dir", "Making directory ${file.absolutePath}")
            stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            val photo = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
            this.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photo))
            try {
                stream.flush()
                stream.close()
            } catch(e: Exception) {
                Log.e("stream", e.message)
            }
            return Uri.fromFile(File(file.absolutePath))
        } catch(e: IOException) {
            Log.e("Photo", e.message)
            try {
                stream?.flush()
                stream?.close()
            } catch(e: Exception) {
                Log.e("stream", e.message)
            }
            return null
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
                    val tUser = User(mUser.email, user.name, user.nim, downloadUrl.toString() ,user.pbo, user.pp, user.male, user.role)
                    authProfileUpdate(requestUpdateProfile, mPw, tUser)
                }
            }
    }

    private fun validateForm() : Boolean {
        var valid = true

        this.name = edit_full_name.text.toString()
        if(TextUtils.isEmpty(this.name)) {
            edit_full_name.error = resources.getString(R.string.required)
            valid = false
        } else {
            edit_full_name.error = null
        }

        this.nick = edit_nickname.text.toString()
        if(TextUtils.isEmpty(nick)) {
            edit_nickname.error = resources.getString(R.string.required)
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
                edit_nickname.error = resources.getString(R.string.nickname_required)
                valid = false
            } else {
                edit_nickname.error = null
            }
        }

        return valid
    }

    private fun save(pw: String) {
        showLoading()
        val requestUpdateProfile =
            UserProfileChangeRequest.Builder()
                .setDisplayName(nick)
                .build()
        val tUser = User(mUser.email, name, user.nim, mUser.photoUrl.toString(), user.pbo, user.pp, user.male, user.role)
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
                        val nUser = User(tUser.email, tUser.name, tUser.nim, mUser.photoUrl.toString(), tUser.pbo, tUser.pp, tUser.male, tUser.role)
                        userDatabaseUpdate(nUser)
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
                toast(resources.getString(R.string.profile_saved))
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

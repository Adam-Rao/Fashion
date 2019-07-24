package ke.co.hardware.fashion

import android.Manifest
import android.app.Activity
import androidx.fragment.app.Fragment
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.Button
import android.os.Build
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import androidx.core.content.FileProvider
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var utility=ke.co.hardware.fashion.utility()
    val PERMISION_REQUEST = 10
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_CODE_W=2
    val PICK_IMAGE=3
    lateinit var imageView: ImageView
    var uriFilePath: Uri? = null
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        imageView = findViewById(R.id.picture_taken)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nvView)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        val photo = findViewById<Button>(R.id.photo)


        photo.setOnClickListener {

            if (checkPermission()) {
                openCamera();
            } else {
                requestPermission()
            }
        }

        imageView.setOnClickListener {
            photo.performClick()
        }
        if (savedInstanceState != null) {
            if (uriFilePath == null && savedInstanceState.getString("uri_file_path") != null) {
                utility=ke.co.hardware.fashion.utility()
                uriFilePath = Uri.parse(savedInstanceState.getString("uri_file_path"));
                utility.current_frag = savedInstanceState.getString("current_frag")
                utility.uriFilePath = Uri.parse(savedInstanceState.getString("uri_file_path"));
                Glide.with(this).load(uriFilePath).into(imageView)
            }
        }
        val line_up=findViewById<Button>(R.id.line_up)
        line_up.setOnClickListener {
            utility.current_frag="clothes"
            val transaction=supportFragmentManager.beginTransaction()

            val st=ke.co.hardware.fashion.shoes()
            st.setUtitity(utility)
            st.special=true
            st.arguments?.putString("current_frag",utility.current_frag)
            st.arguments?.putString("uri",utility.uriFilePath.toString())
            transaction.replace(R.id.flContent, st as Fragment,"shoes")
            //transaction.setCustomAnimations(R.anim.translateend1,R.anim.translateend1);
            transaction.addToBackStack(null)
            transaction.commit()

        }
        val galery=findViewById<Button>(R.id.galery)
        galery.setOnClickListener {

            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)
        }


    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISION_REQUEST)

    }

    override fun onSaveInstanceState(outState: Bundle?) {

        if (uriFilePath != null)
        {
            outState?.putString("uri_file_path", utility.uriFilePath.toString())
            outState?.putString("current_frag", utility.current_frag)

        };

        super.onSaveInstanceState(outState);
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            uriFilePath= Uri.fromFile(this)
            utility.uriFilePath=uriFilePath

        }

    }

    fun openCamera() {
       /* val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val uri = createImageFile()
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } catch (e: Exception) {
            //Log.e(FragmentActivity.TAG, FragmentActivity.TAG + ": " + e.toString())
        }

        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)

*/


        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            dispatchTakePictureIntent();
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_W);
        }



    }


    private fun dispatchTakePictureIntent() {

        val takePictureIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            val  photoFile = createImageFile();

            if (photoFile != null) {

                val photoURI = FileProvider.getUriForFile(this,
                "ke.co.hardware.fashion.fileProvider",
                photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
                )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISION_REQUEST -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                openCamera()

            }
            else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        /*showMessageOKCancel("You need to allow access permissions",
                            DialogInterface.OnClickListener { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermission()
                                }
                            })*/
                    }
                }
            }
            REQUEST_CODE_W->if(grantResults.size>0&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Glide.with(this).load(uriFilePath).into(imageView)

            /*val imageBitmap = data!!.extras.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)*/

        }
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return
            }
            val selectedImage = data.data



            utility.uriFilePath= selectedImage
            Glide.with(this).load(utility.uriFilePath).into(imageView)
        }

    }

    fun item_s_c(){
        val transaction=supportFragmentManager.beginTransaction()

        val st=ke.co.hardware.fashion.shoes()
        st.setUtitity(utility)
        st.arguments?.putString("current_frag",utility.current_frag)
        st.arguments?.putString("uri",utility.uriFilePath.toString())
        transaction.replace(R.id.flContent, st as Fragment,"shoes")
        //transaction.setCustomAnimations(R.anim.translateend1,R.anim.translateend1);
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun item_select(){
        val transaction=supportFragmentManager.beginTransaction()

       val st=ke.co.hardware.fashion.hair()

        st.arguments?.putString("current_frag",utility.current_frag)
        st.arguments?.putString("uri",utility.uriFilePath.toString())
        st.setUtitity(utility)
        transaction.replace(R.id.flContent, st as Fragment,"shoes")
        //transaction.setCustomAnimations(R.anim.translateend1,R.anim.translateend1);
        transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.shoes -> {
                utility.current_frag="shoes"
               item_s_c()
            }
            R.id.clothes -> {
                utility.current_frag="clothes"
                item_s_c()

            }
            R.id.makeup -> {
                utility.current_frag="makeup"
                item_select()
            }
            R.id.hair->{
                utility.current_frag="hair"
                item_select()
            }
            R.id.spectacles->
            {
                utility.current_frag="spectacles"
                item_select()
            }
            R.id.help->{

            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

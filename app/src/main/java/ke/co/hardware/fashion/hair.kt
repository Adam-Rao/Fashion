package ke.co.hardware.fashion


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_hair.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class hair : Fragment() {

    class shoes_{
        var shoess=ArrayList<HashMap<String,String>>()
    }

    lateinit var utility: utility
    public fun setUtitity(utility: utility){
        this.utility=utility
    }
    val PICK_IMAGE=3
    lateinit var v:View
    val items=ArrayList<item_adapter>()
    var mStorageRef: StorageReference? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            if ( savedInstanceState.getString("uri_file_path") != null) {
                utility=ke.co.hardware.fashion.utility()

                utility.current_frag = savedInstanceState.getString("current_frag")
                utility.uriFilePath = Uri.parse(savedInstanceState.getString("uri_file_path"));

            }
        }

        utility.fullpicture=false

        v= inflater.inflate(R.layout.fragment_hair, container, false)

        val line_up=v.findViewById<Button>(R.id.line_up)
        val db = FirebaseFirestore.getInstance()

        if(utility.current_frag.equals("hair")){
            line_up.setText("Hairstyles")
        }
        else if(utility.current_frag.equals("spectacles")){
            line_up.setText("Spectacles")
        }
        else
            line_up.setText("MakeUP")

        val recycle=v.findViewById<RecyclerView>(R.id.recycle_v)

        recycle.layoutManager= GridLayoutManager(context,2)
        recycle.addItemDecoration(GridItemDecoration(10, 2))
        line_up.setOnClickListener {
            utility.fullpicture=true
            recycle.adapter?.notifyDataSetChanged()
        }
        /*mStorageRef = FirebaseStorage.getInstance().getReference();

        val riversRef = mStorageRef!!.child("images/rivers.jpg")

        val localFile = File.createTempFile("images", "jpg")
        riversRef.getFile(localFile)
            .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot> {
                // Successfully downloaded data to local file
                // ...
            }).addOnFailureListener(OnFailureListener {
                // Handle failed download
                // ...
            })*/

        val docRef = db.collection("shopping").document(utility.current_frag)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null || document!!.data!=null ) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.data)
                    if(document.data==null){
                        // updateUI(mAuth.currentUser)
                    }
                    else{

                        val doc=document.toObject(shoes_::class.java) as shoes_
                        Log.d("documents",doc.shoess.toString())
                        val map1=doc.shoess
                        val temp= shoes.shoes_()
                        for(map in map1)
                            for(key in map.keys){

                                temp.shoess.add(hashMapOf(Pair(key,map[key]!!)))

                            }

                        val adapter=adapter(temp.shoess)
                        adapter.utility=utility
                        adapter.activity=activity!!

                        recycle.adapter=adapter
                        recycle.adapter?.notifyDataSetChanged()
                    }

                } else {
                    // updateUI(mAuth.currentUser)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
        val galery=v.findViewById<Button>(R.id.galery)
        galery.setOnClickListener {

            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)
        }

        return v
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return
            }
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = activity?.getContentResolver()?.query(
                selectedImage,
                filePathColumn, null, null, null
            )
            cursor?.moveToFirst()

            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            val picturePath = cursor?.getString(columnIndex!!)
            cursor?.close()

            utility.uriFilePath= Uri.parse(picturePath)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState?.putString("uri_file_path", utility.uriFilePath.toString())
        outState?.putString("current_frag", utility.current_frag)
        super.onSaveInstanceState(outState)
    }


}

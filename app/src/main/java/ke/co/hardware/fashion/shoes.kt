package ke.co.hardware.fashion


import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.FileDownloadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import android.content.Intent
import android.app.Activity
import android.graphics.BitmapFactory
import android.R.attr.bitmap
import android.provider.MediaStore
import android.R.attr.data
import android.net.Uri
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.fragment_shoes.*
import kotlinx.android.synthetic.main.fragment_shoes.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */


class shoes : Fragment() {
    class shoes_{
        var shoess=ArrayList<HashMap<String,String>>()
    }

   lateinit var utility: utility
    public fun setUtitity(utility: utility){
        this.utility=utility
    }
    var start=false
    val PICK_IMAGE=3
    var special=false
    lateinit var v:View
    val items=ArrayList<item_adapter>()
    lateinit var doc:shoes_
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

        if(special)
        {
            utility.fullpicture=true
            special=false
        }
        else
            utility.fullpicture=true
        v= inflater.inflate(R.layout.fragment_shoes, container, false)

        val db = FirebaseFirestore.getInstance()


        val recycle=v.findViewById<RecyclerView>(R.id.recycle_v)

        if(utility.current_frag=="shoes"){
            v.line_up.setText("shoes")
        }
        else{
            v.line_up.setText("clothes")
        }
        recycle.layoutManager= GridLayoutManager(context,2)
        recycle.addItemDecoration(GridItemDecoration(10, 2))
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

                        doc=document.toObject(shoes_::class.java) as shoes_
                        Log.d("documents",doc.shoess.toString())
                        val map1=doc.shoess
                        val temp=shoes_()
                        for(map in map1)
                            for(key in map.keys){

                                    temp.shoess.add(hashMapOf(Pair(key,map[key]!!)))

                            }

                        val adapter=adapter(temp.shoess)
                        adapter.utility=utility
                        adapter.activity=activity!!
                        recycle.adapter=adapter
                        recycle.adapter?.notifyDataSetChanged()
                        start=true
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

            val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)
        }
        v.male.setOnClickListener {
            if(start) {
                val adapter = recycle.adapter as adapter
                adapter.male_female = "m"
                adapter.notifyDataSetChanged()
                val map1 = doc.shoess
                val temp = shoes_()
                for (map in map1)
                    for (key in map.keys) {
                        println("Element at key $key = ${map[key]}")
                        if (key[0] == 'm') {
                            temp.shoess.add(hashMapOf(Pair(key, map[key]!!)))
                        }
                    }
                adapter.item = temp.shoess
                adapter.notifyDataSetChanged()
            }
        }
        v.female.setOnClickListener {
            if(start) {
                val adapter = recycle.adapter as adapter
                adapter.male_female = "f"
                adapter.notifyDataSetChanged()
                val map1 = doc.shoess
                val temp = shoes_()
                for (map in map1)
                    for (key in map.keys) {
                        println("Element at key $key = ${map[key]}")
                        if (key[1] == 'f') {
                            temp.shoess.add(hashMapOf(Pair(key, map[key]!!)))
                        }
                    }
                adapter.item = temp.shoess
                adapter.notifyDataSetChanged()
            }
        }
        val spinner =  v.findViewById<Spinner>(R.id.spinner);
        v.size.setOnClickListener {
            if(start) {
                val adapter = recycle.adapter as adapter
                adapter.size = "m"
                adapter.notifyDataSetChanged()
                val map1 = doc.shoess
                val temp = shoes_()
                for (map in map1)
                    for (key in map.keys) {
                        println("Element at key $key = ${map[key]}")
                        if (key[2].toString() == spinner.selectedItem.toString()) {
                            temp.shoess.add(hashMapOf(Pair(key, map[key]!!)))
                        }
                    }
                adapter.item = temp.shoess
                adapter.notifyDataSetChanged()
            }
        }



      val categories =  ArrayList<String>();
      categories.add("1");
      categories.add("2");
      categories.add("3");
      categories.add("4");
      categories.add("5");
      categories.add("6");
        categories.add("7");
        categories.add("8");


     val dataAdapter =  ArrayAdapter(context, android.R.layout.simple_spinner_item, categories);

      // Drop down layout style - list view with radio button
      dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
        var a=0

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(a==0)
                    a++
                else
                    size.performClick()
            }

        }


        v.line_up.setOnClickListener {
            utility.fullpicture=true
            recycle.adapter?.notifyDataSetChanged()
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
            utility.uriFilePath= selectedImage
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState?.putString("uri_file_path", utility.uriFilePath.toString())
        outState?.putString("current_frag", utility.current_frag)
        super.onSaveInstanceState(outState)

    }


}

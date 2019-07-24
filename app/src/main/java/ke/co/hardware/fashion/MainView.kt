package ke.co.hardware.fashion


import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_main_view.*
import kotlinx.android.synthetic.main.fragment_main_view.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainView : Fragment(){

    lateinit var v:View
    var image_s=""
    var image_m=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v= inflater.inflate(R.layout.fragment_main_view, container, false)

        val image_shop:ImageView=v.findViewById(R.id.image_shop)

        val iamge_me:ImageView=v.findViewById(R.id.image_me)


        Glide.with(context!!).load(image_s).into(image_shop)
        Glide.with(context!!).load(image_m).into(iamge_me)




        return v
    }



}

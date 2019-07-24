package ke.co.hardware.fashion

import android.app.Activity
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList
import ke.co.hardware.fashion.R
import kotlinx.android.synthetic.main.grid_picture.view.*

class PictureListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val text=itemView.text_detail
    val image:ImageView=itemView.image
    val my_image:ImageView=itemView.my_image



}
class item_adapter{
    var detail=""
    var uri=""
    var id=1
}
class adapter(var item:ArrayList<HashMap<String,String>>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var utility: utility
    var male_female=""
    var size=""
    lateinit var activity:FragmentActivity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PictureListViewHolder(LayoutInflater.from(parent.context).inflate(ke.co.hardware.fashion.R.layout.grid_picture, parent, false))
    }

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val picture = viewHolder as PictureListViewHolder
        //movieViewHolder.bindView(listOfMovies[position])


        val map=item.get(position)
        for(key in map.keys){
            println("Element at key $key = ${map[key]}")


            Glide.with(picture.itemView.context).load(map[key]).into(picture.image)
            if(utility.uriFilePath!=null)
                Glide.with(picture.itemView.context).load(utility.uriFilePath).into(picture.my_image)
            if(utility.fullpicture)
                picture.my_image.visibility=View.GONE
            picture.text.text=key
            if(utility.current_frag.equals("shoes")||utility.current_frag.equals("clothes"))
                picture.text.text=key.substring(3)

            picture.image.setOnClickListener {

                val transaction=activity.supportFragmentManager.beginTransaction()

                val st=ke.co.hardware.fashion.MainView()

                st.image_m=utility.uriFilePath.toString()
                st.image_s=map[key]!!
                transaction.replace(R.id.flContent, st as Fragment,"shoes")
                //transaction.setCustomAnimations(R.anim.translateend1,R.anim.translateend1);
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }



}
class GridItemDecoration(gridSpacingPx: Int, gridSize: Int) : RecyclerView.ItemDecoration() {
    private var mSizeGridSpacingPx: Int = gridSpacingPx
    private var mGridSize: Int = gridSize

    private var mNeedLeftSpacing = false

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val frameWidth = ((parent.width - mSizeGridSpacingPx.toFloat() * (mGridSize - 1)) / mGridSize).toInt()
        val padding = parent.width / mGridSize - frameWidth
        val itemPosition = (view.getLayoutParams() as RecyclerView.LayoutParams).viewAdapterPosition
        if (itemPosition < mGridSize) {
            outRect.top = 0
        } else {
            outRect.top = mSizeGridSpacingPx
        }
        if (itemPosition % mGridSize == 0) {
            outRect.left = 0
            outRect.right = padding
            mNeedLeftSpacing = true
        } else if ((itemPosition + 1) % mGridSize == 0) {
            mNeedLeftSpacing = false
            outRect.right = 0
            outRect.left = padding
        } else if (mNeedLeftSpacing) {
            mNeedLeftSpacing = false
            outRect.left = mSizeGridSpacingPx - padding
            if ((itemPosition + 2) % mGridSize == 0) {
                outRect.right = mSizeGridSpacingPx - padding
            } else {
                outRect.right = mSizeGridSpacingPx / 2
            }
        } else if ((itemPosition + 2) % mGridSize == 0) {
            mNeedLeftSpacing = false
            outRect.left = mSizeGridSpacingPx / 2
            outRect.right = mSizeGridSpacingPx - padding
        } else {
            mNeedLeftSpacing = false
            outRect.left = mSizeGridSpacingPx / 2
            outRect.right = mSizeGridSpacingPx / 2
        }
        outRect.bottom = 0
    }
}
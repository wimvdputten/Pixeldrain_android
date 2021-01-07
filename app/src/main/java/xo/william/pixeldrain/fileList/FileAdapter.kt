package xo.william.pixeldrain.fileList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import xo.william.pixeldrain.R

class FileAdapter :
    RecyclerView.Adapter<FileAdapter.MyViewHolder>() {
    private var files = emptyList<FileModel>() // Cached copy of words
    private var infoFiles = emptyList<InfoModel>() // Cached copy of words
    private var expandedPositon = -1

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val linearLayout: LinearLayout) :
        RecyclerView.ViewHolder(linearLayout)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileAdapter.MyViewHolder {
        // create a new view


//        val constraintlayout = LayoutInflater.from(parent.context)
//            .inflate(R.layout.file_item_view, parent, false) as ConstraintLayout

        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.file_item_view, parent, false) as LinearLayout;
        return MyViewHolder(linearLayout);
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val nameTextView = holder.linearLayout.findViewById<TextView>(R.id.nameTextView)
        val fileTypeTextView = holder.linearLayout.findViewById<TextView>(R.id.fileTypeTextView)
        val uploadDateTextView =
            holder.linearLayout.findViewById<TextView>(R.id.UploadDateTextView)

        setDetailVisibility(holder, position);


        val infoModel: InfoModel? =
            infoFiles.find { infoModel ->   if (!infoModel.equals(null)) infoModel.id.equals(files[position].id) else false }
        if (infoModel !== null) {
            loadImage(infoModel, holder)
            val text = infoModel.id + " (" + infoModel.views + ") "
            nameTextView.text = infoModel.thumbnail_href
        } else {
            nameTextView.text = files[position].name
        }
        fileTypeTextView.text = files[position].mime_type
        uploadDateTextView.text = files[position].date_uploaded
        handleExpand(holder, position)
    }

    fun setDetailVisibility(holder: MyViewHolder, position: Int) {
        val isExpanded = this.expandedPositon == position;
        val detailItemLayout = holder.linearLayout.findViewById<ConstraintLayout>(R.id.detailItemLayout);

        if (isExpanded) {
            detailItemLayout.visibility = View.VISIBLE
        } else {
            detailItemLayout.visibility = View.GONE
        }

    }
    private fun handleExpand(holder: MyViewHolder, position: Int) {
        val mainItemLayout = holder.linearLayout.findViewById<ConstraintLayout>(R.id.mainItemLayout)
        val isExpanded: Boolean = position == this.expandedPositon
        mainItemLayout.setOnClickListener{
            if (isExpanded){
                this.expandedPositon = -1;
            }else{
                this.expandedPositon = position;
            }

            notifyItemChanged(position)
        }
    }

    internal fun loadImage(infoModel: InfoModel, holder: MyViewHolder) {
        val imageview = holder.linearLayout.findViewById<ImageView>(R.id.fileThumbnail)
        val progressBar =
            holder.linearLayout.findViewById<ProgressBar>(R.id.fileThumbnailLoader)
        try {
        val urlString = infoModel.getThumbnailUrl()
            Glide.with(holder.linearLayout).load(urlString).fitCenter().into(imageview)

            progressBar.visibility = View.GONE
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            Log.e("loadImage", "error: " + infoModel.getThumbnailUrl() + "  " + e.message)
        }


    }

    internal fun setFiles(files: List<FileModel>) {
        this.files = files
        notifyDataSetChanged()
    }

    internal fun updateTitle(files: List<InfoModel>) {
        this.infoFiles = files
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = files.size
}
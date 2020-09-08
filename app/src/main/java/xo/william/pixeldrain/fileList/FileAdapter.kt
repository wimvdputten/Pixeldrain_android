package xo.william.pixeldrain.fileList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import xo.william.pixeldrain.R
import java.lang.Exception

class FileAdapter() :
    RecyclerView.Adapter<FileAdapter.MyViewHolder>() {
    private var files = emptyList<FileModel>() // Cached copy of words
    private var infoFiles = emptyList<InfoModel>() // Cached copy of words

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val constraintlayout: ConstraintLayout) :
        RecyclerView.ViewHolder(constraintlayout)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileAdapter.MyViewHolder {
        // create a new view
        val constraintlayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_item_view, parent, false) as ConstraintLayout
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(constraintlayout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val nameTextView = holder.constraintlayout.findViewById<TextView>(R.id.nameTextView)
        val fileTypeTextView = holder.constraintlayout.findViewById<TextView>(R.id.fileTypeTextView)
        val uploadDateTextView =
            holder.constraintlayout.findViewById<TextView>(R.id.UploadDateTextView)
        val infoModel: InfoModel? =
            infoFiles.find { infoModel -> infoModel.id.equals(files[position].id) }
        if (infoModel !== null) {
            loadImage(infoModel, holder);
            val text = infoModel.id + " (" + infoModel.views + ") ";
            nameTextView.setText(infoModel.thumbnail_href);
        } else {
            nameTextView.setText(files[position].name)
        }
        fileTypeTextView.setText(files[position].mime_type);
        uploadDateTextView.setText(files[position].date_uploaded);
    }

    internal fun loadImage(infoModel: InfoModel, holder: MyViewHolder) {
        val imageview = holder.constraintlayout.findViewById<ImageView>(R.id.fileThumbnail);
        val progressBar =
            holder.constraintlayout.findViewById<ProgressBar>(R.id.fileThumbnailLoader)
        val callback = object : Callback {
            override fun onSuccess() {
                progressBar.visibility = View.GONE;
            }

            override fun onError(e: Exception?) {
                progressBar.visibility = View.GONE;
            }
        }

        Picasso.get().load(infoModel.getThumbnailUrl()).resize(64, 64).centerCrop()
            .into(imageview, callback);

    }

    internal fun setFiles(files: List<FileModel>) {
        this.files = files;
        notifyDataSetChanged()
    }

    internal fun updateTitle(files: List<InfoModel>) {
        this.infoFiles = files;
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = files.size
}
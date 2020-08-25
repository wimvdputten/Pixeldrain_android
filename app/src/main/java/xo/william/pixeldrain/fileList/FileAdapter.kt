package xo.william.pixeldrain.fileList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import xo.william.pixeldrain.R

class FileAdapter() :
    RecyclerView.Adapter<FileAdapter.MyViewHolder>() {
    private var files = emptyList<FileModel>() // Cached copy of words


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

        nameTextView.setText(files[position].name)
        fileTypeTextView.setText(files[position].mime_type);
        uploadDateTextView.setText(files[position].date_uploaded);

    }

    internal fun setFiles(files: List<FileModel>) {
        this.files = files;
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = files.size
}
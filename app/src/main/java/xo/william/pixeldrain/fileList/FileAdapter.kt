package xo.william.pixeldrain.fileList

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import xo.william.pixeldrain.R
import xo.william.pixeldrain.repository.ClipBoard

class FileAdapter(private var context: Context) :
    RecyclerView.Adapter<FileAdapter.MyViewHolder>() {


    private var loadedFiles = emptyList<InfoModel>() // Cached copy of words
    private var loadedFilesHolder = emptyList<InfoModel>() // Cached copy of words

    private var expandedPositon = -1
    private var clipBoard: ClipBoard = ClipBoard(context)

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_item_view, parent, false) as LinearLayout;
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

        val infoModel: InfoModel = loadedFiles[position];

        loadImage(infoModel, holder);
        nameTextView.text = infoModel.name;
        fileTypeTextView.text = loadedFiles[position].mime_type
        uploadDateTextView.text = loadedFiles[position].date_upload


        setDetailVisibility(holder, position);
        handleExpand(holder, position)
        setOnClickListener(holder, position, infoModel)
    }

    fun setOnClickListener(holder: MyViewHolder, position: Int, infoModel: InfoModel?) {
       val downloadButton  = holder.linearLayout.findViewById<Button>(R.id.downloadButton);
        downloadButton.setOnClickListener{
            Toast.makeText(holder.linearLayout.context, "Download", Toast.LENGTH_LONG).show()
        }

        val copyButton = holder.linearLayout.findViewById<Button>(R.id.copyButton);
        copyButton.setOnClickListener{
            copyToClipBoard(infoModel);
        }

        val shareButton  = holder.linearLayout.findViewById<Button>(R.id.shareButton);
        shareButton.setOnClickListener{
            shareUrl(infoModel);
        }

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

    internal fun setFiles(files: List<InfoModel>) {
        this.loadedFiles = files.sortedByDescending { it.date_upload }
        this.loadedFilesHolder = loadedFiles;
        notifyDataSetChanged()
    }

    fun copyToClipBoard(infoModel: InfoModel?) {
        if (infoModel !== null) {
            clipBoard.copyToClipBoard(infoModel.getFileUrl());
        }
    }

    fun shareUrl(infoModel: InfoModel?){
        if (infoModel !== null){
            val intent: Intent = Intent()
                .setType("text/plain")
                .setAction(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, "Checkout this file on Pixeldrain: ${infoModel.getFileUrl()}")
            val shareIntent = Intent.createChooser(intent, "Share");
            context.startActivity(shareIntent);
        }
    }

    fun searchFiles(query: String?) {
        if (query !== null){
            loadedFiles = loadedFilesHolder.filter { it.name.contains(query, ignoreCase = true) }
        } else{
            loadedFiles = loadedFilesHolder;
        }

        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = loadedFiles.size


}
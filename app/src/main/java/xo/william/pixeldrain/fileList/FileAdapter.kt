package xo.william.pixeldrain.fileList

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xo.william.pixeldrain.FileViewActivity
import xo.william.pixeldrain.R
import xo.william.pixeldrain.model.FileViewModel
import xo.william.pixeldrain.repository.ClipBoard


class FileAdapter(private var context: Context, private var fileViewModel: FileViewModel) :
    RecyclerView.Adapter<FileAdapter.MyViewHolder>() {


    /**
     * loaded files
     */
    private var loadedFiles = emptyList<InfoModel>()

    /**
     * loaded files holder, holds files on search
     */
    private var loadedFilesHolder = emptyList<InfoModel>() // Cached copy of words

    private var expandedPosition = -1
    private var clipBoard: ClipBoard = ClipBoard(context)
    private val format = Json { ignoreUnknownKeys = true }


    class MyViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_item_view, parent, false) as LinearLayout
        return MyViewHolder(linearLayout)
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val nameTextView = holder.linearLayout.findViewById<TextView>(R.id.nameTextView)
        val fileTypeTextView = holder.linearLayout.findViewById<TextView>(R.id.fileTypeTextView)
        val uploadDateTextView = holder.linearLayout.findViewById<TextView>(R.id.uploadDateTextView)
        val viewsTextView = holder.linearLayout.findViewById<TextView>(R.id.viewsTextview)

        val infoModel: InfoModel = loadedFiles[position]

        loadImage(infoModel, holder)
        nameTextView.text = infoModel.name
        fileTypeTextView.text = infoModel.mime_type
        viewsTextView.text = "${infoModel.views} views"

        //basic date formatting
        val formattedDate = infoModel.date_upload.substring(0, 16).replace("T", " ")
        uploadDateTextView.text = formattedDate

        setDetailVisibility(holder, position)
        handleExpand(holder, position)
        setOnClickListener(holder, infoModel)
    }

    private fun setOnClickListener(holder: MyViewHolder, infoModel: InfoModel) {
        val thumbnail = holder.linearLayout.findViewById<ImageView>(R.id.fileThumbnail)

        thumbnail.setOnClickListener {
            val mimeType = infoModel.mime_type
            if (mimeType.contains("image") ||
                mimeType.contains("text") ||
                mimeType.contains("video") ||
                mimeType.contains("audio")
            ) {
                val intent = Intent(context, FileViewActivity::class.java)
                intent.putExtra("infoModel", format.encodeToString(infoModel))
                context.startActivity(intent)
            } else {
                val text = "This file type is not supported"
                Toast.makeText(holder.linearLayout.context,text,Toast.LENGTH_SHORT).show()
            }
        }

        val downloadButton = holder.linearLayout.findViewById<Button>(R.id.downloadButton)
        downloadButton.setOnClickListener {
            downloadFile(infoModel)
        }

        val copyButton = holder.linearLayout.findViewById<Button>(R.id.copyButton)
        copyButton.setOnClickListener {
            copyToClipBoard(infoModel)
        }

        val shareButton = holder.linearLayout.findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener {
            shareUrl(infoModel)
        }
        val deleteButton = holder.linearLayout.findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            openDeleteFileAlert(infoModel)
        }
    }

    private fun setDetailVisibility(holder: MyViewHolder, position: Int) {
        val isExpanded = this.expandedPosition == position
        val detailItemLayout =
            holder.linearLayout.findViewById<ConstraintLayout>(R.id.detailItemLayout)

        if (isExpanded) {
            detailItemLayout.visibility = View.VISIBLE
        } else {
            detailItemLayout.visibility = View.GONE
        }
    }

    private fun handleExpand(holder: MyViewHolder, position: Int) {
        val mainItemLayout = holder.linearLayout.findViewById<ConstraintLayout>(R.id.mainItemLayout)
        val isExpanded: Boolean = position == this.expandedPosition

        mainItemLayout.setOnClickListener {
            if (isExpanded) {
                this.expandedPosition = -1
            } else {
                this.expandedPosition = position
            }
            notifyItemChanged(position)
        }
    }

    private fun loadImage(infoModel: InfoModel, holder: MyViewHolder) {
        val imageView = holder.linearLayout.findViewById<ImageView>(R.id.fileThumbnail)
        val progressBar =
            holder.linearLayout.findViewById<ProgressBar>(R.id.fileThumbnailLoader)

        try {
            val urlString = infoModel.getThumbnailUrl()
            Glide.with(holder.linearLayout).load(urlString).fitCenter().into(imageView)
            progressBar.visibility = View.GONE
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
        }
    }

    internal fun setFiles(files: List<InfoModel>) {
        this.loadedFiles = files.sortedByDescending { it.date_upload }
        this.loadedFilesHolder = loadedFiles
        notifyDataSetChanged()
    }

    private fun copyToClipBoard(infoModel: InfoModel) {
            clipBoard.copyToClipBoard(infoModel.getShareUrl())
    }

    private fun shareUrl(infoModel: InfoModel) {
        val text = "Check this file out on PixelDrain: ${infoModel.getShareUrl()}"
        val intent: Intent = Intent()
            .setType("text/plain")
            .setAction(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, text)

        val shareIntent = Intent.createChooser(intent, "Share ${infoModel.name}")
        context.startActivity(shareIntent)
    }

    fun searchFiles(query: String?) {
        if (query !== null) {
            loadedFiles = loadedFilesHolder.filter { it.name.contains(query, ignoreCase = true) }
        } else {
            loadedFiles = loadedFilesHolder
        }
        notifyDataSetChanged()
    }

    private fun downloadFile(infoModel: InfoModel) {
        val url = "${infoModel.getFileUrl()}?download"
        val uris = Uri.parse(url)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }

    private fun openDeleteFileAlert(infoModel: InfoModel) {
        val builder = AlertDialog.Builder(context)
        var message: String

        builder.setTitle("Are you sure you want to delete ${infoModel.name}")
        if (infoModel.can_edit) {
            message = "After deleting this file no one will be able to see it."
        } else {
            message = "Warning this file was uploaded anonymous. "
            message += "This will only remove this file from the overview. "
            message += "People with the link can still view the file."
        }
        builder.setMessage(message)

        builder.setPositiveButton("Confirm") { _, _ ->
            fileViewModel.deleteFile(infoModel, this::showToast)
        }

        builder.setNeutralButton("Cancel") { _, _ ->
            // Do nothing
        }

        builder.create().show()
    }

    private fun showToast(message: String) {
        val handler = Handler(context.mainLooper)
        handler.post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = loadedFiles.size
}
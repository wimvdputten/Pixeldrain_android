package xo.william.pixeldrain

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_file_view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import xo.william.pixeldrain.fileList.InfoModel


class FileViewActivity : AppCompatActivity() {

    private val format = Json { ignoreUnknownKeys = true }

    private lateinit var infoModel:InfoModel;

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_view)
        setSupportActionBar(sub_toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val infoModelString: String? = intent.getStringExtra("infoModel")
        if (infoModelString !== null){
            infoModel = format.decodeFromString(infoModelString);
        }else{
            infoModel = InfoModel("")
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
        loadFile()
    }

    fun loadFile(){
        val type = infoModel.mime_type;
        if (type.contains("image")){
            loadImage();
        }
        if (type.contains("video")){
            loadVideo()
        }
    }

    fun loadImage(){
        val imageFile = findViewById<ImageView>(R.id.imageFile)
        val fileProgress = findViewById<ProgressBar>(R.id.fileProgressBar);
        try {
            val urlString = infoModel.getFileUrl()
            imageFile.visibility = View.VISIBLE
            imageFile.contentDescription = infoModel.name;
            Glide.with(this).load(urlString).fitCenter().into(imageFile)
            fileProgress.visibility = View.GONE
        } catch (e: Exception) {
            fileProgress.visibility = View.GONE
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show();
            Log.e("loadImage", "error: " + infoModel.getThumbnailUrl() + "  " + e.message)
        }
    }

    fun loadVideo(){
        val fileProgress = findViewById<ProgressBar>(R.id.fileProgressBar)
        fileProgress.visibility = View.GONE
        val videoExoFile = findViewById<PlayerView>(R.id.videoExoFile)
        val exoPlayer = SimpleExoPlayer.Builder(this).build()
        videoExoFile.player = exoPlayer;

        val mediaItem: MediaItem = MediaItem.fromUri(infoModel.getFileUrl())
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare()


        exoPlayer.play()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish();
        return super.onOptionsItemSelected(item)
    }
}
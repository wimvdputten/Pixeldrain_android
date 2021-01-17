package xo.william.pixeldrain

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.core.requests.CancellableRequest
import com.github.kittinunf.fuel.core.requests.tryCancel
import com.github.kittinunf.result.Result
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_file_view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import xo.william.pixeldrain.api.FuelService
import xo.william.pixeldrain.fileList.InfoModel


class FileViewActivity : AppCompatActivity() {

    private val format = Json { ignoreUnknownKeys = true }
    private lateinit var infoModel: InfoModel;

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var request: CancellableRequest

    private var textLiveData = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_view)
        setSupportActionBar(sub_toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val infoModelString: String? = intent.getStringExtra("infoModel")
        if (infoModelString !== null) {
            infoModel = format.decodeFromString(infoModelString);
        } else {
            infoModel = InfoModel("")
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        loadFile()
    }

    private fun loadFile() {
        val type = infoModel.mime_type;
        if (type.contains("image")) {
            loadImage();
        }
        if (type.contains("video") || type.contains("audio")) {
            loadVideo()
        }
        if (type.contains("text")) {
            loadText();
        }
    }

    private fun loadImage() {
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

    private fun loadVideo() {
        val fileProgress = findViewById<ProgressBar>(R.id.fileProgressBar)
        fileProgress.visibility = View.GONE
        val videoExoFile = findViewById<PlayerView>(R.id.videoExoFile)
        videoExoFile.visibility = View.VISIBLE
        exoPlayer = SimpleExoPlayer.Builder(this).build()
        videoExoFile.player = exoPlayer;

        val mediaItem: MediaItem = MediaItem.fromUri(infoModel.getFileUrl())
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare()

        exoPlayer.play()
    }

    private fun loadText() {
        request = FuelService().getFileText(infoModel.getFileUrl())
            .responseString() { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("text", "text ${result.get()}")
                        textLiveData.postValue(result.get())
                    }
                    is Result.Failure -> {
                        Log.d("text", "error ${result.error.exception.message}")
                        textLiveData.postValue(result.error.exception.message)
                    }
                }
            }

        textLiveData.observe(this, {
            val fileProgress = findViewById<ProgressBar>(R.id.fileProgressBar)
            val textFile = findViewById<TextView>(R.id.textFile)
            val textScrollView = findViewById<ScrollView>(R.id.textScrollView)
            fileProgress.visibility = View.GONE
            textFile.text = it
            textFile.visibility = View.VISIBLE
            textScrollView.visibility = View.VISIBLE
        })
    }


    override fun onBackPressed() {
        if (this::exoPlayer.isInitialized) {
            exoPlayer.release();
        }
        if (this::request.isInitialized) {
            request.tryCancel()
        }
        super.onBackPressed()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (this::exoPlayer.isInitialized) {
            exoPlayer.release();
        }
        if (this::request.isInitialized) {
            request.tryCancel()
        }
        finish();
        return super.onOptionsItemSelected(item)
    }
}
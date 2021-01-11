package xo.william.pixeldrain

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore.MediaColumns.DISPLAY_NAME
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import xo.william.pixeldrain.fileList.FileAdapter
import xo.william.pixeldrain.model.FileViewModel
import kotlinx.android.synthetic.main.activity_main.*
import xo.william.pixeldrain.R.id.action_login
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: FileAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var fileViewModel: FileViewModel;


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar));
        setRecyclerView();
        fileViewModel = ViewModelProvider(this).get(FileViewModel::class.java)
        fileViewModel.allFiles.observe(
            this,
            Observer { files -> files?.let { viewAdapter.setFiles(it) } })

        fileViewModel.infoFiles.observe(
            this,
            Observer { infoModel -> infoModel?.let { viewAdapter.updateTitle(infoModel) } }
        )

        main_actionButton.setOnClickListener {
            this.handleActionButton()
        }
    }

    fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = FileAdapter(this);

        recyclerView = findViewById<RecyclerView>(R.id.file_recyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    /**
     * Open file selection
     */
    fun handleActionButton() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }

    /**
     * Handle file selection
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            startUpload(selectedFile)
        }
    }

    fun startUpload(selectedFile: Uri?) {
        if (selectedFile !== null){
            var fileName = selectedFile.path

            //start progress
            val mainProgress = findViewById<ProgressBar>(R.id.main_progress)
            mainProgress.visibility = View.VISIBLE;

            //get fileName from uri
                val filePathColumn = arrayOf(DISPLAY_NAME)
                val cursor = contentResolver.query(selectedFile, filePathColumn, null, null, null)
                if (cursor !== null) {
                    cursor.moveToFirst()
                    fileName = cursor.getString(0);
                    cursor.close();
                }
                val stream = contentResolver.openInputStream(selectedFile);
                fileViewModel.uploadPost(stream, fileName, this::finishUpload);
        }
    }

    fun finishUpload(message: String?){
        val handler = Handler(this.getMainLooper())
        handler.post(Runnable {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })

        val mainProgress  =   findViewById<ProgressBar>(R.id.main_progress)
        mainProgress.visibility = View.INVISIBLE;
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        action_login -> {
            openLoginActivity()
            true;
        }
        else ->  super.onOptionsItemSelected(item);
    }

    fun openLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }
}
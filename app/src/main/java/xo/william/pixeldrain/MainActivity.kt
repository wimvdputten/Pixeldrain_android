package xo.william.pixeldrain

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore.MediaColumns.DISPLAY_NAME
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import xo.william.pixeldrain.R.id.*
import xo.william.pixeldrain.fileList.FileAdapter
import xo.william.pixeldrain.model.FileViewModel
import xo.william.pixeldrain.repository.SharedRepository

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: FileAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var fileViewModel: FileViewModel
    private lateinit var sharedRepository: SharedRepository
    private lateinit var loginButtonRef:MenuItem
    private lateinit var registerButton: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        sharedRepository = SharedRepository(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))
        setRecyclerView()
        fileViewModel = ViewModelProvider(this).get(FileViewModel::class.java)
        fileViewModel.setSharedResponse(sharedRepository)

        fileViewModel.loadedFiles.observe(
            this,
            Observer { files -> files?.let {
                    if (files.size > 0){
                        stopProgress(false)
                    }

                viewAdapter.setFiles(files)
            } })

        fileViewModel.dbFiles.observe(
            this,
            Observer { files -> files?.let {
                if (it.isEmpty() && !sharedRepository.isUserLogedIn()){
                    stopProgress(true)
                }
                fileViewModel.loadFiles(it)
            } })

        main_actionButton.setOnClickListener {
            this.handleActionButton()
        }
    }

    fun stopProgress(empty: Boolean) {
        initProgress.visibility = View.GONE
        if (empty){
            initText.visibility = View.VISIBLE
        }else{
            initText.visibility = View.GONE
        }
    }

    fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = FileAdapter(this)

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

        if (resultCode == 200){
            loginButtonRef.title = "Logout"
            registerButton.isVisible = false;
            fileViewModel.loadFilesFromApi(loadedFiles = fileViewModel.loadedFiles)
            Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show()
        }
    }

    fun startUpload(selectedFile: Uri?) {
        if (selectedFile !== null){
            var fileName = selectedFile.path

            //start progress
            val mainProgress = findViewById<ProgressBar>(R.id.main_progress)
            mainProgress.visibility = View.VISIBLE

            //get fileName from uri
                val filePathColumn = arrayOf(DISPLAY_NAME)
                val cursor = contentResolver.query(selectedFile, filePathColumn, null, null, null)
                if (cursor !== null) {
                    cursor.moveToFirst()
                    fileName = cursor.getString(0)
                    cursor.close()
                }
                val stream = contentResolver.openInputStream(selectedFile)

            if(sharedRepository.isUserLogedIn()){
                    fileViewModel.uploadPost(stream, fileName, sharedRepository.getAuthKey(),this::finishUpload)
                }else{
                    fileViewModel.uploadAnonPost(stream, fileName, this::finishUpload)
                }
        }
    }

    fun finishUpload(message: String?){
        val handler = Handler(this.mainLooper)
        handler.post(Runnable {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })

        val mainProgress  =   findViewById<ProgressBar>(R.id.main_progress)
        mainProgress.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_toolbar, menu)

        loginButtonRef = menu.findItem(action_login)
        registerButton = menu.findItem(action_register)
        if (sharedRepository.isUserLogedIn()){
            menu.findItem(action_login).title = "Logout"
            registerButton.isVisible = false
        }else{
            menu.findItem(action_login).title = "Login"
            registerButton.isVisible = true
        }


        val searchItem = menu.findItem(action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("search", "text ${query}")
                viewAdapter.searchFiles(query)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        searchView.setOnCloseListener {
            viewAdapter.searchFiles(null)
            false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        action_login -> {
            if (sharedRepository.isUserLogedIn()){
                sharedRepository.deleteToken()
                item.title = "Login"
                registerButton.isVisible = true;
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            }else{
                openLoginActivity()
            }
            true
        }
        action_register ->{
            openNewTabWindow()
            true
        }

        else ->  super.onOptionsItemSelected(item)
    }

    fun openLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, 200)
    }

    fun openNewTabWindow() {
        val uris = Uri.parse("https://pixeldrain.com/register")
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        this.startActivity(intents)
    }
}
package xo.william.pixeldrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import xo.william.pixeldrain.database.File
import xo.william.pixeldrain.fileList.DummyAdapter
import xo.william.pixeldrain.fileList.FileAdapter

import xo.william.pixeldrain.model.FileViewModel

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

        fileViewModel.testInfo.observe(
            this,
            Observer { info -> Log.i("Observe", info) })
    }

    fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = FileAdapter();

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

    fun insertIntoDB() {
        val antFile = DummyAdapter().getAntFile();
        val antFile2 = File(antFile.id, antFile.name, antFile.mime_type, antFile.date_uploaded, 666)
        fileViewModel.insert(antFile2);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_toolbar, menu)
        return true
    }
}
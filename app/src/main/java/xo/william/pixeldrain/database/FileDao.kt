package xo.william.pixeldrain.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FileDao {
    @Query("SELECT * FROM file")
    fun getAll(): LiveData<List<File>>

    @Query("SELECT * FROM file WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<File>

    @Insert
    fun insertAll(vararg users: File)

    @Insert
    fun insert(vararg user: File)

    @Query("DELETE FROM file WHERE id = (:id)")
    fun deleteById(id: String)

    @Delete
    fun delete(user: File)
}
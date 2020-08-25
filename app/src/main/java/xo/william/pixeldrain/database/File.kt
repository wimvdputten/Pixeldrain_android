package xo.william.pixeldrain.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class File(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val fileName: String,
    @ColumnInfo(name = "mime_type") val mimeType: String,
    @ColumnInfo(name = "date_upload") val dateUpload: String,
    @ColumnInfo(name = "size") val size: Int
)
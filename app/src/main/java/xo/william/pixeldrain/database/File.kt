package xo.william.pixeldrain.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class File(
    @PrimaryKey val id: String,
)
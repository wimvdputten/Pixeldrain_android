package xo.william.pixeldrain.repository


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

class ClipBoard(private var context: Context) {

    private val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun copyToClipBoard(text: String){
        try {
            val clipData = ClipData.newPlainText("url", text);
            clipBoard.setPrimaryClip(clipData);
            Toast.makeText(context, "Copied to clipboard: $text", Toast.LENGTH_SHORT).show();
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

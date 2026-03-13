package praeterii.radio.util

import android.content.Context
import praeterii.radio.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Exception.toErrorMessage(context: Context): String {
    return when (this) {
        is UnknownHostException -> context.getString(R.string.error_no_internet)
        is SocketTimeoutException -> context.getString(R.string.error_server_timeout)
        is IOException -> context.getString(R.string.error_network)
        else -> context.getString(R.string.error_unknown, this.message)
    }
}

package de.adorsys.android.smsparser

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object SmsTool {
    const val REQUEST_CODE_ASK_PERMISSIONS = 123

    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestSMSPermission(activity: Activity) {
        val permission = Manifest.permission.RECEIVE_SMS
        val hasSpecificPermission = ContextCompat.checkSelfPermission(activity, permission)
        if (hasSpecificPermission != PackageManager.PERMISSION_GRANTED && !activity.shouldShowRequestPermissionRationale(permission)) {
            activity.requestPermissions(arrayOf(permission),
                    REQUEST_CODE_ASK_PERMISSIONS)
        }
    }
}
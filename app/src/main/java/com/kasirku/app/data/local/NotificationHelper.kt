package com.kasirku.app.data.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kasirku.app.R

object NotificationHelper {

    private const val CHANNEL_ID = "kasirku_alerts"
    private const val CHANNEL_NAME = "KasirKu Alerts"
    private const val CHANNEL_DESC = "Notifikasi stok menipis dan pengingat shift"

    private const val LOW_STOCK_NOTIFICATION_ID = 1001
    private const val SHIFT_NOTIFICATION_ID = 1002

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showLowStockNotification(context: Context, count: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Stok Menipis")
            .setContentText("$count produk perlu restock segera")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Ada $count produk yang stoknya sudah menipis. Segera lakukan restock agar tidak kehabisan."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(LOW_STOCK_NOTIFICATION_ID, notification)
    }

    fun showShiftReminderNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Shift Belum Dibuka")
            .setContentText("Silakan buka shift terlebih dahulu sebelum bertransaksi")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(SHIFT_NOTIFICATION_ID, notification)
    }
}

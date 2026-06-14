package com.kasirku.app.ui.screens.receipt

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import java.io.OutputStream
import java.util.*

object BluetoothPrinter {
    private val PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun hasBluetoothPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Pre-Android 12, BLUETOOTH is a normal permission
        }
    }

    fun getPairedPrinters(context: Context): List<BluetoothDevice> {
        if (!hasBluetoothPermission(context)) return emptyList()
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        return try {
            adapter.bondedDevices?.filter { device ->
                device.bluetoothClass?.majorDeviceClass == 0x0600 // Printer class
            } ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    fun printReceipt(
        context: Context,
        device: BluetoothDevice,
        storeName: String,
        storeAddress: String,
        storePhone: String,
        invoice: String,
        date: String,
        cashier: String,
        items: List<String>,
        subtotal: String,
        discount: String,
        tax: String,
        total: String,
        paymentMethod: String,
        amountPaid: String,
        change: String,
        footer: String,
        onResult: (Boolean, String) -> Unit
    ) {
        Thread {
            try {
                val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(PRINTER_UUID)
                socket.connect()
                val os: OutputStream = socket.outputStream

                // ESC/POS commands
                val ESC = byteArrayOf(0x1B)
                val INIT = byteArrayOf(0x1B, 0x40)
                val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
                val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
                val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
                val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
                val CUT = byteArrayOf(0x1D, 0x56, 0x00)

                os.write(INIT)

                // Store header
                os.write(ALIGN_CENTER)
                os.write(BOLD_ON)
                os.write("$storeName\n".toByteArray())
                os.write(BOLD_OFF)
                os.write("$storeAddress\n".toByteArray())
                os.write("$storePhone\n".toByteArray())
                os.write("--------------------------------\n".toByteArray())

                // Invoice info
                os.write(ALIGN_LEFT)
                os.write("$invoice\n".toByteArray())
                os.write("$date\n".toByteArray())
                os.write("Kasir: $cashier\n".toByteArray())
                os.write("--------------------------------\n".toByteArray())

                // Items
                items.forEach { item ->
                    os.write("$item\n".toByteArray())
                }
                os.write("--------------------------------\n".toByteArray())

                // Totals
                os.write("Subtotal: $subtotal\n".toByteArray())
                if (discount.isNotEmpty() && discount != "Rp 0") {
                    os.write("Diskon: $discount\n".toByteArray())
                }
                os.write("Pajak: $tax\n".toByteArray())
                os.write(BOLD_ON)
                os.write("TOTAL: $total\n".toByteArray())
                os.write(BOLD_OFF)
                os.write("--------------------------------\n".toByteArray())

                // Payment
                os.write("$paymentMethod: $amountPaid\n".toByteArray())
                if (change.isNotEmpty() && change != "Rp 0") {
                    os.write("Kembalian: $change\n".toByteArray())
                }
                os.write("--------------------------------\n".toByteArray())

                // Footer
                os.write(ALIGN_CENTER)
                os.write("$footer\n\n".toByteArray())

                // Cut paper
                os.write(CUT)
                os.flush()
                os.close()
                socket.close()

                (context.mainExecutor as? java.util.concurrent.Executor)?.execute {
                    onResult(true, "Berhasil print!")
                } ?: onResult(true, "Berhasil print!")

            } catch (e: Exception) {
                onResult(false, "Gagal print: ${e.message}")
            }
        }.start()
    }
}

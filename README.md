<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat-square&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat-square&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Room-2.7.0-green?style=flat-square" />
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" />
</p>

<h1 align="center">KasirKu - Aplikasi Kasir POS</h1>

<p align="center">
  Aplikasi Point of Sale (POS) modern untuk Android, dibangun dengan Jetpack Compose & Material 3.<br>
  Dilengkapi dengan panel admin, manajemen kasir, laporan real-time, dan banyak lagi.
</p>

---

## Fitur Utama

### Mode Kasir
- **Login PIN** - Pilih kasir, masukkan PIN 4 digit
- **Dashboard POS** - Grid produk, filter kategori, pencarian, scan barcode
- **Keranjang** - Bottom sheet, edit quantity, guard stok otomatis
- **Pembayaran** - Tunai, QRIS, Transfer dengan kalkulasi kembalian
- **Kode Promo** - Input kode diskon (% atau nominal Rp)
- **Struk** - Share via WhatsApp, Print Bluetooth thermal printer

### Mode Admin / Owner
- **Panel Admin** - Dashboard dengan quick stats & menu navigasi
- **Kelola Produk** - CRUD produk, stok, barcode, harga modal, alert stok menipis
- **Kelola Kasir** - CRUD kasir, reset PIN
- **Kelola Kategori** - CRUD kategori produk (tambah, rename, hapus)
- **Kelola Promo** - CRUD kode diskon dengan batas pemakaian
- **Manajemen Shift** - Buka/tutup shift, saldo awal, setoran, riwayat
- **Laporan** - Grafik penjualan, profit/margin, top produk, breakdown pembayaran
- **Pengaturan Toko** - Nama, alamat, pajak, footer struk

### Fitur Umum
- **Dark Mode** - Light / Dark / Auto (ikuti sistem)
- **Onboarding** - Tutorial 3 slide untuk pengguna baru
- **Riwayat Transaksi** - Filter periode, search invoice, detail + profit per transaksi
- **Export Data** - CSV untuk spreadsheet, JSON untuk backup
- **Notifikasi** - Alert stok menipis & reminder shift
- **Animasi** - Transisi screen, splash pulsing, haptic feedback

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **UI** | Jetpack Compose + Material 3 |
| **State** | Kotlin Flow + StateFlow (MVVM) |
| **Database** | Room 2.7.0 (KSP) |
| **Navigation** | String-based state machine |
| **Async** | Kotlin Coroutines + Flow |
| **Image** | Coil 2.7.0 |
| **Barcode** | ML Kit Barcode Scanning + CameraX |
| **Print** | Bluetooth ESC/POS thermal printer |

## Screenshots

| Login | Dashboard POS | Admin Panel |
|:---:|:---:|:---:|
| Pilih kasir + PIN | Grid produk + keranjang | Menu admin lengkap |

| Laporan | Riwayat | Pembayaran |
|:---:|:---:|:---:|
| Grafik + profit | Filter + detail | Promo + metode bayar |

## Setup & Build

### Prerequisites
- Android Studio Ladybug+ (atau terbaru)
- JDK 17+
- Android SDK 36

### Clone & Build
```bash
git clone https://github.com/username/KasirKu.git
cd KasirKu
./gradlew assembleDebug
```

### Install ke Device
```bash
./gradlew installDebug
```

## Default Credentials

| Role | Nama | PIN |
|------|------|-----|
| **Admin** | Admin | `0000` |
| **Kasir** | Andi | `1234` |
| **Kasir** | Sari | `5678` |
| **Kasir** | Budi | `9012` |

## Project Structure

```
app/src/main/java/com/kasirku/app/
├── MainActivity.kt              # Entry point + navigation + theme
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt       # Room DB (v4, 5 entities)
│   │   ├── ProductDao.kt        # Product queries
│   │   ├── TransactionDao.kt    # Transaction + report queries
│   │   ├── UserDao.kt           # User/cashier CRUD
│   │   ├── PromoDao.kt          # Promo code CRUD
│   │   ├── ShiftDao.kt          # Shift management
│   │   ├── StoreConfig.kt       # SharedPreferences settings
│   │   └── NotificationHelper.kt # Push notifications
│   ├── model/
│   │   ├── Product.kt           # Product entity (barcode, stock, cost)
│   │   ├── Transaction.kt       # Transaction entity (profit, discount)
│   │   ├── Cashier.kt           # User entity (role: admin/cashier)
│   │   ├── CartItem.kt          # Cart item (discount support)
│   │   ├── PromoCode.kt         # Promo code entity
│   │   └── Shift.kt             # Shift entity
│   └── repository/              # Repository pattern
├── ui/
│   ├── theme/                   # Colors, Typography, Theme (light+dark)
│   ├── components/              # Reusable: CartBar, ProductCard, SalesChart
│   └── screens/
│       ├── onboarding/          # 3-slide onboarding
│       ├── splash/              # Animated splash screen
│       ├── login/               # Role-based PIN login
│       ├── dashboard/           # POS dashboard + cart bottom sheet
│       ├── payment/             # Payment + promo code
│       ├── receipt/             # Receipt + share + BT print
│       ├── history/             # Transaction history + filter + detail
│       ├── reports/             # Reports + chart + profit
│       ├── settings/            # Settings + dark mode + export
│       └── admin/               # Admin hub, products, cashiers, promos,
│                                  categories, shifts, store settings
└── viewmodel/
    └── KasirViewModel.kt        # Single ViewModel (all business logic)
```

## Data Flow

```
Admin tambah produk → Room DB → Flow re-emit → POS grid update real-time
Kasir tap produk → Cart (guard stok) → Payment → Konfirmasi
→ Stok fresh dari DB → Dikurangi → Transaksi INSERT
→ History, Laporan, Stock Alert → Semua auto-update
```

## License

MIT License - Bebas digunakan dan dimodifikasi.

---

<p align="center">
  Made with ❤️ using Jetpack Compose
</p>

# KasirKu Design System

> Exported from Google Stitch — Project ID: 7794272912288540750

## Brand
- **Name:** KasirKu
- **Tagline:** Aplikasi Kasir Pintar
- **Personality:** Professional, Efficient, Trustworthy
- **Target:** Kasir UMKM (warung, coffee shop, toko kelontong)

## Colors (Material 3 — Teal Theme)

| Token | Hex | Usage |
|-------|-----|-------|
| primary | #00595C | Main actions, headers, active states |
| onPrimary | #FFFFFF | Text on primary |
| primaryContainer | #0D7377 | Primary containers, brand color |
| onPrimaryContainer | #A2F5F9 | Text on primary container |
| secondary | #006A62 | Secondary actions |
| onSecondary | #FFFFFF | Text on secondary |
| secondaryContainer | #08FDEA | Secondary containers |
| tertiary | #504F4F | Tertiary elements |
| background | #F7FAFA | App background |
| onBackground | #181C1D | Text on background |
| surface | #F7FAFA | Surface color |
| surfaceContainerLowest | #FFFFFF | Cards |
| surfaceContainerLow | #F1F4F4 | Elevated surfaces |
| surfaceContainer | #EBEEEE | Medium containers |
| surfaceContainerHigh | #E6E9E9 | High containers |
| surfaceContainerHighest | #E0E3E3 | Highest containers |
| onSurface | #181C1D | Text on surface |
| onSurfaceVariant | #3E4949 | Secondary text |
| outline | #6E7979 | Borders |
| outlineVariant | #BEC9C9 | Subtle borders |
| error | #BA1A1A | Error states |
| onError | #FFFFFF | Text on error |

### Additional Brand Colors
| Name | Hex | Usage |
|------|-----|-------|
| Teal Primary | #0D7377 | Main brand color, primary buttons |
| Teal Light | #14A3A8 | Secondary teal, hover states |
| Teal Dark | #0A5C5F | Dark teal, splash gradient end |
| Success Green | #22C55E | Payment success, positive |
| Warning Amber | #F59E0B | Stock low, warnings |
| Danger Red | #EF4444 | Delete, errors |
| Accent Amber | #F59E0B | CTA highlights |

## Typography — Plus Jakarta Sans

| Level | Font | Size | Weight | Line Height | Letter Spacing |
|-------|------|------|--------|-------------|----------------|
| headline-lg | Plus Jakarta Sans | 24sp | 700 | 32sp | — |
| headline-md | Plus Jakarta Sans | 20sp | 600 | 28sp | — |
| headline-sm | Plus Jakarta Sans | 18sp | 600 | 24sp | — |
| body-lg | Plus Jakarta Sans | 16sp | 400 | 24sp | — |
| body-md | Plus Jakarta Sans | 14sp | 400 | 20sp | — |
| label-lg | Plus Jakarta Sans | 14sp | 600 | 20sp | 0.01em |
| label-md | Plus Jakarta Sans | 12sp | 500 | 16sp | — |
| price-display | Plus Jakarta Sans | 22sp | 700 | 28sp | — |

## Shapes

| Token | Radius | Usage |
|-------|--------|-------|
| sm | 4dp | Small chips, badges |
| DEFAULT | 8dp | Inputs, small buttons |
| md | 12dp | Cards, buttons (primary) |
| lg | 16dp | Large containers |
| xl | 24dp | Bottom sheets |
| full | 9999dp | Pill chips, avatars |

## Spacing

| Token | Value | Usage |
|-------|-------|-------|
| xs | 4dp | Tight spacing |
| sm | 8dp | Related elements |
| md | 16dp | Section padding, safe margin |
| lg | 24dp | Distinct sections |
| xl | 32dp | Large gaps |
| gutter | 12dp | Grid gutters |
| touch-target | 48dp | Minimum touch target |

## Elevation

| Level | Shadow | Usage |
|-------|--------|-------|
| Level 0 | None | Background |
| Level 1 | 0 2dp 8dp rgba(0,0,0,0.10) | Cards, product items |
| Level 2 | 0 8dp 24dp rgba(13,115,119,0.08) | Modals, bottom sheets |
| Level 3 | 0 12dp 32dp rgba(0,0,0,0.20) | Floating cart bar |

## Components

### Buttons
- **Primary:** Solid Teal (#0D7377), white text, 12dp corners, 48dp min height
- **Secondary:** White bg, 1dp teal border, teal text, 12dp corners
- **Text:** No background, teal text

### Category Chips
- Pill-shaped (full radius)
- Active: Teal bg, white text
- Inactive: Light gray bg, dark text

### Product Cards
- White bg, 12dp corners, Level 1 shadow
- Square image top, name (SemiBold), price (Teal, price-display style)
- Tappable entire card

### Floating Cart Bar
- Fixed above bottom nav
- Solid teal bg, white text
- Left: item count, Right: total price
- Level 3 elevation

### Bottom Navigation
- Fixed bottom, 4 items
- Active: Teal icon + label
- Inactive: Gray icon + label
- Items: Kasir, Riwayat, Laporan, Lainnya

### Input Fields
- Outlined, 1dp border, 12dp corners
- Focus: border turns teal
- Height: 56dp
- Labels above field

## Screens Reference (from Stitch)

1. **Splash** — Teal gradient bg, logo, "KasirKu", "Aplikasi Kasir Pintar", v1.0.0
2. **Login Kasir** — Grid avatar kasir, PIN 4 digit, numeric keypad
3. **Dashboard Kasir (HP)** — Search + kategori chips + 2-col grid + floating cart + bottom nav
4. **Dashboard Kasir (Tablet)** — 60/40 split: 4-col grid + cart panel
5. **Pembayaran** — Order summary, metode bayar (Tunai/QRIS/Transfer), kembalian
6. **Struk/Receipt** — Success checkmark, receipt card, share/save/new transaction
7. **Riwayat Transaksi** — Date filter chips + search + transaction card list
8. **Laporan** — Summary cards + bar chart + top products
9. **Settings** — Info toko, pajak, produk management, theme, export

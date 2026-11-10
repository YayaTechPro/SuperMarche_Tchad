# SuperMarché Tchad – Android Inventory Management App

## 1. Introduction
SuperMarché Tchad is a simple Android application that helps a small supermarket in Chad manage its products and stock. It was created as a university project for the Mobile Application Development course.

## 2. Objectives
- Store products permanently on the phone (add, view, edit, delete, search).
- Show the owner how the stock is doing (dashboard and alerts).
- Warn when a product is low in stock or out of stock.
- Work in English and French, with prices in the Central African CFA franc (FCFA).
- Stay simple enough to be understood and explained by a beginner.

## 3. Technologies
- Kotlin, Android Studio, XML layouts (with ViewBinding)
- SQLite through Android's built-in `SQLiteOpenHelper`
- RecyclerView, Material Design components, bottom navigation
- No external libraries other than AndroidX and Material Components

## 4. Features
- **Dashboard:** total products, total inventory value (FCFA), low-stock count, out-of-stock count, recently added products, stock alerts.
- **Products:** list, live search, add, edit, delete (with confirmation dialog).
- **Add / Edit product:** form with validation (name required, price > 0, quantity >= 0, minimum stock >= 0).
- **Alerts:** low stock (orange) and out of stock (red).
- **Settings:** English/French language switch, app name, currency info, about.
- 6 sample products inserted automatically on the very first launch.

Stock rules: **out of stock** = quantity is 0. **Low stock** = quantity > 0 and quantity <= minimum stock.

## 5. Database
Database file: `supermarket.db` – table `products`

| Column       | Type                              | Meaning                          |
|--------------|-----------------------------------|----------------------------------|
| id           | INTEGER PRIMARY KEY AUTOINCREMENT | Unique product number            |
| name         | TEXT                              | Product name                     |
| category     | TEXT                              | Category key (Food, Dairy, ...)  |
| price        | REAL                              | Price in FCFA (a number)         |
| quantity     | INTEGER                           | Units in stock                   |
| minimumStock | INTEGER                           | Low-stock threshold              |

## 6. How to open and run
1. Install Android Studio (a recent version, e.g. Koala or newer) with an Android SDK 34.
2. `File > Open` and select the folder `SuperMarche_Tchad` (the one containing `settings.gradle.kts`).
3. Wait for the Gradle sync to finish (internet is needed the first time to download dependencies).
4. Create or choose an emulator (Device Manager) or connect a phone with USB debugging.
5. Press the green **Run** button.

To test French: Settings tab > Langue/Language > Français.
To reset the sample data: uninstall the app (or "Clear storage") and run it again.

## 7. Project structure
```
app/src/main/java/com/supermarche/tchad/
  MainActivity.kt          bottom navigation + fragment container
  HomeFragment.kt          dashboard
  ProductsFragment.kt      list, search, delete
  AlertsFragment.kt        low / out of stock
  SettingsFragment.kt      language and info
  AddEditProductActivity.kt  add / edit form
  DatabaseHelper.kt        SQLite (all database code)
  ProductAdapter.kt        RecyclerView adapter
  Product.kt               data class + stock status
  Utils.kt                 price and category formatting
  SupermarcheApp.kt        sets English as default language
app/src/main/res/          layouts, drawables, colors, themes, strings (EN + FR)
```

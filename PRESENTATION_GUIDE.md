# Defense guide – SuperMarché Tchad

## 1. 30-second introduction (say this first)
"My app helps a small supermarket in Chad manage its stock. The owner can add, edit, delete and search products, see statistics on a dashboard, and get warnings when a product is low or out of stock. It works in English and French and uses FCFA. I used Kotlin, XML layouts, RecyclerView and SQLite, which are the topics of our course."

## 2. How the app is organised
- **MainActivity** = one Activity with a bottom navigation bar. Each tab is a **Fragment**.
- **AddEditProductActivity** = a second Activity opened from the Products screen (Intent). It is used for both adding and editing.
- **DatabaseHelper** = the only file that talks to SQLite. Screens call its functions.
- **Product** = a data class representing one row of the table.

## 3. Screen by screen
**Home (HomeFragment + fragment_home.xml)**
Shows 4 statistic cards and two small lists. Data comes from SQL queries (COUNT and SUM). It reloads in `onResume()`, so it updates automatically when you come back from adding/editing a product.

**Products (ProductsFragment + fragment_products.xml)**
RecyclerView with `ProductAdapter`. The search box uses `doAfterTextChanged` and calls `getAllProducts(search)` (SQL `LIKE`). Edit opens `AddEditProductActivity` with the product id. Delete shows a confirmation dialog first.

**Add / Edit (AddEditProductActivity)**
If the Intent contains `product_id`, the form is filled and "Update" is used; otherwise "Insert". Validation shows errors under the fields (TextInputLayout.error). The category dropdown stores a key ("Food") but displays a translated label.

**Alerts (AlertsFragment)**
Two lists: low stock (`quantity > 0 AND quantity <= minimumStock`) and out of stock (`quantity <= 0`).

**Settings (SettingsFragment)**
Radio buttons call `AppCompatDelegate.setApplicationLocales(...)`. Android reloads the screen with `values/strings.xml` (English) or `values-fr/strings.xml` (French).

## 4. Main Kotlin files in one sentence each
- `DatabaseHelper.kt` – creates the table, inserts sample data once, does insert / update / delete / search / statistics.
- `ProductAdapter.kt` – turns each Product into a card and colors it green / orange / red.
- `Product.kt` – the product data and the `status()` rule.
- `Utils.kt` – formats prices ("5,000 FCFA") and translates categories.
- `MainActivity.kt` – switches between the four fragments.
- `SupermarcheApp.kt` – makes English the default language.

## 5. Questions the professor may ask
**Why are sample products not inserted every time?** They are inserted inside `onCreate()` of SQLiteOpenHelper, which runs only once, when the database file is first created.
**Why are prices stored as numbers?** So we can calculate (price x quantity) in SQL and format them differently in English and French.
**How does the data stay after closing the app?** SQLite saves it in a file on the phone.
**Why Fragments?** To keep one Activity with a bottom navigation bar; each tab is a light-weight screen.
**What is ViewBinding?** It generates a class per layout so we can write `binding.tvName` instead of `findViewById`.
**How does the app know the language?** Android picks the strings file that matches the selected locale.
**What would you add next?** Sales history, categories management, export to CSV (not required for this project).

## 6. Quick demo script (3 minutes)
1. Open the app: show the dashboard and the sample alerts.
2. Products tab: search "rice", then clear.
3. Add a product with an empty name to show validation, then a valid one.
4. Back on Home: the numbers changed.
5. Edit Milk quantity from 0 to 10: it leaves the alerts.
6. Delete a product (show the confirmation dialog).
7. Settings: switch to French and show that everything is translated.

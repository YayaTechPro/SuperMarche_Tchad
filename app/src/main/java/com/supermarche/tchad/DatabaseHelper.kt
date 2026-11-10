package com.supermarche.tchad

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Handles the SQLite database: creates the table and does all the CRUD operations
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "supermarket.db"
        const val DB_VERSION = 1
        const val TABLE = "products"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_CATEGORY = "category"
        const val COL_PRICE = "price"
        const val COL_QUANTITY = "quantity"
        const val COL_MIN_STOCK = "minimumStock"

        // SQL condition shared by several queries
        private const val LOW_STOCK_CONDITION = "$COL_QUANTITY > 0 AND $COL_QUANTITY <= $COL_MIN_STOCK"
    }

    // Called only once, when the database file is created for the first time.
    // This is why the sample products are not inserted again at every launch.
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_NAME TEXT NOT NULL, " +
                "$COL_CATEGORY TEXT, " +
                "$COL_PRICE REAL NOT NULL, " +
                "$COL_QUANTITY INTEGER NOT NULL, " +
                "$COL_MIN_STOCK INTEGER NOT NULL)"
        )
        insertSampleProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    // Demonstration products (name, category, price, quantity, minimum stock)
    private fun insertSampleProducts(db: SQLiteDatabase) {
        val samples = listOf(
            Product(name = "Rice", category = "Food", price = 5000.0, quantity = 20, minimumStock = 5),
            Product(name = "Sugar", category = "Food", price = 1500.0, quantity = 5, minimumStock = 10),
            Product(name = "Milk", category = "Dairy", price = 2000.0, quantity = 0, minimumStock = 5),
            Product(name = "Cooking Oil", category = "Food", price = 3000.0, quantity = 12, minimumStock = 5),
            Product(name = "Soap", category = "Hygiene", price = 750.0, quantity = 3, minimumStock = 5),
            Product(name = "Bottled Water", category = "Drinks", price = 500.0, quantity = 30, minimumStock = 10)
        )
        for (product in samples) {
            db.insert(TABLE, null, toValues(product))
        }
    }

    // Converts a Product into the values stored in the table
    private fun toValues(product: Product): ContentValues = ContentValues().apply {
        put(COL_NAME, product.name)
        put(COL_CATEGORY, product.category)
        put(COL_PRICE, product.price)
        put(COL_QUANTITY, product.quantity)
        put(COL_MIN_STOCK, product.minimumStock)
    }

    // Converts every row of a cursor into a Product object
    private fun readProducts(cursor: Cursor): List<Product> {
        val list = mutableListOf<Product>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Product(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COL_NAME)),
                        category = it.getString(it.getColumnIndexOrThrow(COL_CATEGORY)) ?: "Other",
                        price = it.getDouble(it.getColumnIndexOrThrow(COL_PRICE)),
                        quantity = it.getInt(it.getColumnIndexOrThrow(COL_QUANTITY)),
                        minimumStock = it.getInt(it.getColumnIndexOrThrow(COL_MIN_STOCK))
                    )
                )
            }
        }
        return list
    }

    // ---------- CREATE / UPDATE / DELETE ----------

    // Returns the new id, or -1 if the insert failed
    fun insertProduct(product: Product): Long = writableDatabase.insert(TABLE, null, toValues(product))

    // Returns the number of updated rows (1 if everything went well)
    fun updateProduct(product: Product): Int =
        writableDatabase.update(TABLE, toValues(product), "$COL_ID = ?", arrayOf(product.id.toString()))

    fun deleteProduct(id: Int): Int =
        writableDatabase.delete(TABLE, "$COL_ID = ?", arrayOf(id.toString()))

    // ---------- READ ----------

    fun getProductById(id: Int): Product? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE WHERE $COL_ID = ?", arrayOf(id.toString())
        )
        return readProducts(cursor).firstOrNull()
    }

    // All products, or only those whose name/category matches the search text
    fun getAllProducts(search: String = ""): List<Product> {
        val cursor = if (search.isBlank()) {
            readableDatabase.rawQuery("SELECT * FROM $TABLE ORDER BY $COL_NAME COLLATE NOCASE", null)
        } else {
            val like = "%${search.trim()}%"
            readableDatabase.rawQuery(
                "SELECT * FROM $TABLE WHERE $COL_NAME LIKE ? OR $COL_CATEGORY LIKE ? " +
                    "ORDER BY $COL_NAME COLLATE NOCASE",
                arrayOf(like, like)
            )
        }
        return readProducts(cursor)
    }

    // The most recently added products (highest ids first)
    fun getRecentProducts(limit: Int): List<Product> = readProducts(
        readableDatabase.rawQuery("SELECT * FROM $TABLE ORDER BY $COL_ID DESC LIMIT $limit", null)
    )

    fun getLowStockProducts(): List<Product> = readProducts(
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE WHERE $LOW_STOCK_CONDITION ORDER BY $COL_NAME COLLATE NOCASE", null
        )
    )

    fun getOutOfStockProducts(): List<Product> = readProducts(
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE WHERE $COL_QUANTITY <= 0 ORDER BY $COL_NAME COLLATE NOCASE", null
        )
    )

    // ---------- DASHBOARD STATISTICS ----------

    fun countProducts(): Long = DatabaseUtils.queryNumEntries(readableDatabase, TABLE)

    fun countLowStock(): Long =
        DatabaseUtils.queryNumEntries(readableDatabase, TABLE, LOW_STOCK_CONDITION)

    fun countOutOfStock(): Long =
        DatabaseUtils.queryNumEntries(readableDatabase, TABLE, "$COL_QUANTITY <= 0")

    // Total value = sum of (price x quantity) for every product
    fun totalInventoryValue(): Double {
        val cursor = readableDatabase.rawQuery("SELECT SUM($COL_PRICE * $COL_QUANTITY) FROM $TABLE", null)
        cursor.use {
            return if (it.moveToFirst() && !it.isNull(0)) it.getDouble(0) else 0.0
        }
    }
}

package com.supermarche.tchad

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.supermarche.tchad.databinding.ActivityAddEditProductBinding

// One screen used for both "Add Product" and "Edit Product".
// If an existing product id is passed in the Intent, we are in edit mode.
class AddEditProductActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCT_ID = "product_id"
    }

    private lateinit var binding: ActivityAddEditProductBinding
    private lateinit var db: DatabaseHelper
    private lateinit var categoryKeys: Array<String>
    private lateinit var categoryLabels: Array<String>
    private var productId = -1   // -1 means "new product"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        // Category dropdown: keys are saved in the database, labels are shown to the user
        categoryKeys = resources.getStringArray(R.array.category_keys)
        categoryLabels = resources.getStringArray(R.array.category_labels)
        binding.actCategory.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryLabels)
        )
        binding.actCategory.setText(categoryLabels[0], false)

        binding.toolbar.setNavigationOnClickListener { finish() }

        productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        if (productId != -1) {
            binding.toolbar.setTitle(R.string.edit_product_title)
            binding.btnSave.setText(R.string.save_changes)
            if (savedInstanceState == null) loadProduct()
        }

        binding.btnSave.setOnClickListener { saveProduct() }
    }

    // Fill the form with the product that is being edited
    private fun loadProduct() {
        val product = db.getProductById(productId)
        if (product == null) {
            finish()
            return
        }
        binding.etName.setText(product.name)
        val categoryIndex = categoryKeys.indexOf(product.category).coerceAtLeast(0)
        binding.actCategory.setText(categoryLabels[categoryIndex], false)
        // Show whole prices without ".0"
        val priceText = if (product.price % 1.0 == 0.0) product.price.toLong().toString() else product.price.toString()
        binding.etPrice.setText(priceText)
        binding.etQuantity.setText(product.quantity.toString())
        binding.etMinStock.setText(product.minimumStock.toString())
    }

    // Validate the form, then insert or update the product
    private fun saveProduct() {
        val name = binding.etName.text.toString().trim()
        val price = binding.etPrice.text.toString().trim().replace(',', '.').toDoubleOrNull()
        val quantity = binding.etQuantity.text.toString().trim().toIntOrNull()
        val minStock = binding.etMinStock.text.toString().trim().toIntOrNull()

        // Show an error under each invalid field
        binding.tilName.error = if (name.isEmpty()) getString(R.string.error_name_empty) else null
        binding.tilPrice.error =
            if (price == null || price <= 0) getString(R.string.error_price_invalid) else null
        binding.tilQuantity.error =
            if (quantity == null || quantity < 0) getString(R.string.error_quantity_invalid) else null
        binding.tilMinStock.error =
            if (minStock == null || minStock < 0) getString(R.string.error_min_stock_invalid) else null

        // Stop here if anything is invalid
        if (name.isEmpty() || price == null || price <= 0 ||
            quantity == null || quantity < 0 || minStock == null || minStock < 0
        ) return

        // Convert the selected label back to its category key
        val selectedIndex = categoryLabels.indexOf(binding.actCategory.text.toString())
        val category = if (selectedIndex >= 0) categoryKeys[selectedIndex] else categoryKeys.last()

        val product = Product(productId.coerceAtLeast(0), name, category, price, quantity, minStock)

        try {
            if (productId == -1) {
                val newId = db.insertProduct(product)
                if (newId == -1L) throw IllegalStateException("Insert failed")
                Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show()
            } else {
                db.updateProduct(product)
                Toast.makeText(this, R.string.msg_updated, Toast.LENGTH_SHORT).show()
            }
            finish()   // go back to the previous screen
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_generic, Toast.LENGTH_LONG).show()
        }
    }
}

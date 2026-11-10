package com.supermarche.tchad

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.supermarche.tchad.databinding.FragmentProductsBinding

// Products screen: list of all products with search, edit and delete
class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        adapter = ProductAdapter(
            emptyList(),
            showActions = true,
            onEdit = { product -> openEditScreen(product) },
            onDelete = { product -> confirmDelete(product) }
        )
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter

        // Search while typing
        binding.etSearch.doAfterTextChanged { loadProducts() }

        // Open the Add Product screen
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditProductActivity::class.java))
        }
    }

    // Reload when coming back from the Add/Edit screen
    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {
        val search = binding.etSearch.text.toString()
        val products = db.getAllProducts(search)
        adapter.updateList(products)
        binding.tvEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun openEditScreen(product: Product) {
        val intent = Intent(requireContext(), AddEditProductActivity::class.java)
        intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_ID, product.id)
        startActivity(intent)
    }

    // Ask the user to confirm before deleting
    private fun confirmDelete(product: Product) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_title)
            .setMessage(getString(R.string.dialog_delete_message, product.name))
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                try {
                    db.deleteProduct(product.id)
                    Toast.makeText(requireContext(), R.string.msg_deleted, Toast.LENGTH_SHORT).show()
                    loadProducts()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_LONG).show()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

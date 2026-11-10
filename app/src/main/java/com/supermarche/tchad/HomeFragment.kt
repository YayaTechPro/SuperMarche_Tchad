package com.supermarche.tchad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.supermarche.tchad.databinding.FragmentHomeBinding

// Dashboard: statistics, recently added products and stock alerts
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper
    private val recentAdapter = ProductAdapter(emptyList(), showActions = false)
    private val alertsAdapter = ProductAdapter(emptyList(), showActions = false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())
        binding.rvRecent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecent.adapter = recentAdapter
        binding.rvAlerts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlerts.adapter = alertsAdapter
    }

    // Reload the data every time the screen becomes visible,
    // so the dashboard is always up to date after adding/editing/deleting.
    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val context = requireContext()

        // Statistic cards
        binding.tvStatProducts.text = db.countProducts().toString()
        binding.tvStatValue.text = Utils.formatPrice(context, db.totalInventoryValue())
        binding.tvStatLow.text = db.countLowStock().toString()
        binding.tvStatOut.text = db.countOutOfStock().toString()

        // Recently added products (last 3)
        val recent = db.getRecentProducts(3)
        recentAdapter.updateList(recent)
        binding.tvEmptyRecent.visibility = if (recent.isEmpty()) View.VISIBLE else View.GONE

        // Stock alerts: out-of-stock first, then low-stock (max 5 shown)
        val alerts = (db.getOutOfStockProducts() + db.getLowStockProducts()).take(5)
        alertsAdapter.updateList(alerts)
        binding.tvEmptyAlerts.visibility = if (alerts.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

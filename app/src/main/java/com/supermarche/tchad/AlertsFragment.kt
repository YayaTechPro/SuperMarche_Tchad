package com.supermarche.tchad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.supermarche.tchad.databinding.FragmentAlertsBinding

// Alerts screen: low-stock products (orange) and out-of-stock products (red)
class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper
    private val lowAdapter = ProductAdapter(emptyList(), showActions = false)
    private val outAdapter = ProductAdapter(emptyList(), showActions = false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())
        binding.rvLow.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLow.adapter = lowAdapter
        binding.rvOut.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOut.adapter = outAdapter
    }

    override fun onResume() {
        super.onResume()
        loadAlerts()
    }

    private fun loadAlerts() {
        val low = db.getLowStockProducts()
        val out = db.getOutOfStockProducts()

        lowAdapter.updateList(low)
        outAdapter.updateList(out)

        binding.tvLowTitle.text = getString(R.string.alerts_low_title, low.size)
        binding.tvOutTitle.text = getString(R.string.alerts_out_title, out.size)

        // Hide a section when it is empty; show a friendly message if there are no alerts at all
        binding.tvLowTitle.visibility = if (low.isEmpty()) View.GONE else View.VISIBLE
        binding.tvOutTitle.visibility = if (out.isEmpty()) View.GONE else View.VISIBLE
        binding.tvNoAlerts.visibility = if (low.isEmpty() && out.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

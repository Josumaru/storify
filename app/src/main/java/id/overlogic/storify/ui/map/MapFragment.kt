package id.overlogic.storify.ui.map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import id.overlogic.storify.R
import id.overlogic.storify.ui.common.factory.ViewModelFactory
import id.overlogic.storify.util.Result

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _viewModel: MapViewModel? = null
    private val viewModel get() = _viewModel!!
    private lateinit var mMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        _viewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))
        viewModel.result.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    is Result.Success -> {
                        mMap.clear()
                        for (story in it.data.listStory) {
                            if (story.lat != null && story.lon != null && story.name != null) {
                                val marker = LatLng(
                                    story.lat, story.lon
                                )
                                mMap.addMarker(
                                    MarkerOptions().position(
                                        marker
                                    ).title(story.name).snippet(story.description)
                                )
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
                            }
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Result.Loading -> {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.loading),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
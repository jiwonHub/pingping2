package com.example.pingpinge.map

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pingpinge.databinding.ActivityMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import android.annotation.SuppressLint
import android.graphics.Color
import com.example.pingpinge.DBKey
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naver.maps.map.overlay.InfoWindow

class MapActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {

    private var naverMap: NaverMap? = null
    private var blueMarker: Marker? = null
    private var redMarker: Marker? = null
    private lateinit var binding: ActivityMapBinding
    private lateinit var pingDB: DatabaseReference

    private lateinit var locationSource: FusedLocationSource

    private val mapView: MapView by lazy {
        binding.naverMap
    }

    private val pingList = mutableListOf<PingData>()
    private val markerList = mutableListOf<Marker>()
    private val infoWindow = InfoWindow()

    private val markerClickListener = object : Overlay.OnClickListener {
        override fun onClick(overlay: Overlay): Boolean {
            if (overlay is Marker) {
                if (overlay == blueMarker) {
                    showSaveDialog(overlay.position)
                    return true
                }

                val pingModel = overlay.tag as? PingData
                pingModel ?: return false

                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(applicationContext) {
                    override fun getText(p0: InfoWindow): CharSequence {
                        return pingModel.title
                    }
                }
                infoWindow.open(overlay)
                return true
            }
            return false
        }
    }

    private val listener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val pingModel= snapshot.getValue(PingData::class.java)
            pingModel ?: return

            pingList.add(pingModel)
            redMarker?.map = null

            for(pModel in pingList){
                val latLng = LatLng(pModel.lat, pModel.lng)

                val marker = Marker()
                marker.position = latLng
                marker.iconTintColor = Color.RED
                marker.map = naverMap
                marker.onClickListener = this@MapActivity
                marker.tag = pModel

                markerList.add(marker)
                redMarker = marker
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {

        }

    }

    private fun loadMarkers(){
        pingDB.addChildEventListener(listener)
    }

    private var PERMISSION = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        pingList.clear()
        pingDB = Firebase.database.reference.child(DBKey.DB_NOTICE_BOARD)
        loadMarkers()
    }

    override fun onClick(p0: Overlay): Boolean {
        return markerClickListener.onClick(p0)
    }

    @SuppressLint("ObjectAnimatorBinding")
    override fun onMapReady(map: NaverMap) {
        naverMap = map

        naverMap?.setOnMapClickListener { _, coord ->
            blueMarker?.map = null
            redMarker?.map = null

            val bMarker = Marker()
            bMarker.position = coord
            bMarker.icon = MarkerIcons.BLUE
            bMarker.map = naverMap

            blueMarker = bMarker
            loadMarkers()
            showSaveDialog(coord)
        }

        ActivityCompat.requestPermissions(
            this, PERMISSION,
            LOCATION_PERMISSION_REQUEST_CODE
        )
        //로케이션 버튼
        binding.currentLocationButton.map = naverMap
        // 현재 위치 받아오기
        naverMap?.locationSource = locationSource
        val uiSetting = naverMap?.uiSettings // 현위치 버튼
        uiSetting?.isLocationButtonEnabled = false

        naverMap?.maxZoom = 18.0 // 최대 줌
        naverMap?.minZoom = 10.0 // 최소 줌
    }

    private fun showSaveDialog(coord: LatLng) {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("장소를 저장하겠습니까?")
            .setPositiveButton("예") { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(this, SavePingActivity::class.java)
                intent.putExtra("latitude", coord.latitude)
                intent.putExtra("longitude", coord.longitude)
                startActivity(intent)
            }
            .setNegativeButton("아니오", null)
            .create()

        alertDialog.show()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        loadMarkers()
        blueMarker?.map = null
        blueMarker = null
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        loadMarkers()
        blueMarker?.map = null
        blueMarker = null
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        loadMarkers()
        blueMarker?.map = null
        blueMarker = null
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        loadMarkers()
        blueMarker?.map = null
        blueMarker = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        loadMarkers()
        blueMarker?.map = null
        blueMarker = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

}
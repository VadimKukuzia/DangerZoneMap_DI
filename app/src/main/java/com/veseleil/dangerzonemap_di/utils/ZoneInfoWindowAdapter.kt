package com.veseleil.dangerzonemap_di.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.data.model.Zone

class ZoneInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val zoneTypes = mapOf(
        "NT" to context.getString(R.string.natural),
        "TG" to context.getString(R.string.technogenic),
        "AG" to context.getString(R.string.anthropogenic),
        "EC" to context.getString(R.string.ecological)
    )

    override fun getInfoContents(p0: Marker): View? {
        val zone = p0.tag as Zone ?: return null

        val view = LayoutInflater.from(context).inflate(
            R.layout.zone_info_contents, null
        )
        view.findViewById<TextView>(R.id.zone_info_window_text_view_title)
            .text = context.resources.getString(R.string.zone_info_window_title, zone.name)

        view.findViewById<TextView>(R.id.zone_info_window_text_view_zone_type)
            .text = context.resources.getString(
            R.string.zone_info_window_zone_type,
            zoneTypes[zone.zoneType]
        )

        view.findViewById<TextView>(R.id.zone_info_window_text_view_description)
            .text = context.resources.getString(
            R.string.zone_info_window_zone_description,
            zone.description
        )

        return view
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }

}
package com.example.clothes.ndSonActivity

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.clothes.R
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.clothes.SearchCityActivity

class ndSonSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nd_son_settings)
        val settingsCity: Button = findViewById(R.id.settingsCity)
        settingsCity.setOnClickListener {
            val intent = Intent(this, SearchCityActivity::class.java)
            startActivity(intent)
        }
        val settingsReference: Button = findViewById(R.id.settingsReference)
        settingsReference.setOnClickListener {
            val prefs_feedback = this.getSharedPreferences("hot_and_cold_feedback", Context.MODE_PRIVATE)
            val editor = prefs_feedback.edit()
            editor.clear()
            editor.apply()
            Toast.makeText(this, "已恢复默认状态", Toast.LENGTH_SHORT).show()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_settings, rootKey)
        }
    }
}


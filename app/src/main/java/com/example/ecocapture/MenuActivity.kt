package com.example.ecocapture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecocapture.databinding.ActivityMenuBinding
import com.google.android.material.tabs.TabLayoutMediator

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupTabs()
    }

    private fun setupTabs() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "검색학기"
                1 -> "기록보기"
                else -> null
            }
        }.attach()
    }
}

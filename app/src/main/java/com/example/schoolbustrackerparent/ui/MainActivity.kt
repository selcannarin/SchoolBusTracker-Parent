package com.example.schoolbustrackerparent.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.example.schoolbustrackerparent.R
import com.example.schoolbustrackerparent.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.navHostFragment)

        if (navController.currentDestination?.id == R.id.signInFragment) {
            binding.bottomNavigationView.visibility = View.GONE
            hideNavigationDrawer()
        } else {
            binding.bottomNavigationView.visibility = View.VISIBLE

        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.location -> {
                    navController.navigate(R.id.busLocationFragment)
                    return@setOnItemSelectedListener true
                }

                R.id.student -> {
                    navController.navigate(R.id.studentFragment)
                    return@setOnItemSelectedListener true

                }

                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar.toolbar,
            R.string.open,
            R.string.close
        )



        binding.drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            when (id) {
                R.id.nav_busLocation -> {
                    navController.navigate(R.id.busLocationFragment)
                }

                R.id.nav_driverInfo -> {
                    navController.navigate(R.id.driverFragment)
                }

                R.id.nav_studentInfo -> {
                    navController.navigate(R.id.studentFragment)
                }

                R.id.nav_editStudent -> {
                    navController.navigate(R.id.studentFragment)
                }

                R.id.nav_signOut -> {
                    firebaseAuth.signOut()
                    navController.navigate(R.id.signInFragment)
                }
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }

    fun setBottomNavVisibilityGone() {
        binding.bottomNavigationView.isVisible = false
    }


    fun setBottomNavVisibilityVisible() {
        binding.bottomNavigationView.isVisible = true
    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun hideNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }


    fun showNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar.toolbar,
            R.string.open,
            R.string.close
        )

        toggle.isDrawerIndicatorEnabled = true
        toggle.setToolbarNavigationClickListener(null)

        binding.drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
    }

}
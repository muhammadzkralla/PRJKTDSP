package com.zkrallah.projectdsp

import android.content.Context
import android.widget.Toast
import com.zkrallah.projectdsp.domain.models.BottomNavItem

val SCREENS = listOf(
    BottomNavItem(
        "Home",
        "Home",
        selectedIcon = R.drawable.ic_home_filled,
        unSelectedIcon = R.drawable.ic_home_outlined
    ),
    BottomNavItem(
        "History",
        "History",
        selectedIcon = R.drawable.ic_calendar_filled,
        unSelectedIcon = R.drawable.ic_calendar_outlined
    ),
    BottomNavItem(
        "Options",
        "Options",
        selectedIcon = R.drawable.ic_list_filled,
        unSelectedIcon = R.drawable.ic_list_outlined
    ),
)

val ROUTES = listOf(
    "details"
)
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
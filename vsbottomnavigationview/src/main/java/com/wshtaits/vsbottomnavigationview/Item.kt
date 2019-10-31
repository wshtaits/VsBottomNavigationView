package com.wshtaits.vsbottomnavigationview

import android.view.MenuItem

internal class Item(
    val menuItem: MenuItem,
    val viewHolder: ItemViewHolder,
    var animator: ItemAnimator?
) {
    val id: Int
        get() = menuItem.itemId

    var isSelected: Boolean
        get() = menuItem.isChecked
        set(value) {
            isSelectedPrevious = isSelected
            menuItem.isChecked = value
        }

    val isSelectionChanged: Boolean
        get() = isSelected != isSelectedPrevious

    private var isSelectedPrevious = false
}
package com.wshtaits.vsbottomnavigationview

import android.view.View
import kotlinx.android.extensions.LayoutContainer

class ItemViewHolder(private val view: View) : LayoutContainer {

    override val containerView: View?
        get() = view
}
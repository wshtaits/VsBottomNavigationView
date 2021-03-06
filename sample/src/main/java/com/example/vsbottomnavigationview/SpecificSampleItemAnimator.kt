package com.example.vsbottomnavigationview

import android.animation.ValueAnimator
import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.wshtaits.vsbottomnavigationview.ItemAnimator
import com.wshtaits.vsbottomnavigationview.ItemViewHolder
import kotlinx.android.synthetic.main.item_sample.icon_iv
import kotlinx.android.synthetic.main.item_sample.title_tv
import kotlinx.android.synthetic.main.item_sample_specific.*

open class SpecificSampleItemAnimator(context: Context) : ItemAnimator() {

    private val selectedColor = context.getColor(android.R.color.black)
    private val unselectedColor = context.getColor(android.R.color.darker_gray)

    private var titleTextSize: Float = 12f

    override fun ItemViewHolder.onCreate(menuItem: MenuItem) {
        icon_iv.background = menuItem.icon

        title_tv.text = menuItem.title
        title_tv.setTextColor(selectedColor)

        if (menuItem.isChecked) {
            menuItem.icon.setTint(selectedColor)
            charge_tv.scaleX = 1f
            charge_tv.scaleY = 1f
        } else {
            menuItem.icon.setTint(unselectedColor)
            title_tv.textSize = 0f
            charge_tv.scaleX = 0f
            charge_tv.scaleY = 0f
        }
    }

    override fun ItemViewHolder.onSelect(menuItem: MenuItem) {
        menuItem.icon.setTint(selectedColor)
        animateTextSize(title_tv, 0f, titleTextSize)
        animateScale(charge_tv, 1f)
    }

    override fun ItemViewHolder.onDeselect(menuItem: MenuItem) {
        menuItem.icon.setTint(unselectedColor)
        animateTextSize(title_tv, titleTextSize, 0f)
        animateScale(charge_tv, 0f)
    }

    private fun animateTextSize(textView: TextView, fromSize: Float, toSize: Float) {
        with(ValueAnimator.ofFloat(0f, 1f)) {
            addUpdateListener { valueAnimator ->
                val percent = valueAnimator.animatedValue as Float
                textView.textSize = fromSize - (fromSize - toSize) * percent
            }
            duration = 300
            start()
        }
    }

    private fun animateScale(view: View, toScale: Float) {
        view
            .animate()
            .scaleX(toScale)
            .scaleY(toScale)
            .setDuration(300)
            .start()
    }
}
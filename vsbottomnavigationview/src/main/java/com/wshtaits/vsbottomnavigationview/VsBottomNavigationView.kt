/*
 * Copyright (c) 2019 Shtaits Valeriy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wshtaits.vsbottomnavigationview

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.core.view.children

class VsBottomNavigationView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    @MenuRes private val menuResId: Int
    @LayoutRes private val itemLayoutResId: Int

    private val items = mutableListOf<Item>()

    private var isAutoSelectable = true
    private var animator: ItemAnimator? = null

    private var onItemClickAction: ((itemId: Int) -> Unit)? = null
    private var onItemSelectAction: ((itemId: Int) -> Unit)? = null

    private var isFirstOnGlobalLayoutPerformed = false

    private var selectedItemPosition: Int
        get() {
            items.forEachIndexed { position, item ->
                if (item.isSelected) {
                    return position
                }
            }
            return -1
        }
        set(value) {
            items.forEach { item -> item.isSelected = false }
            if (value in 0..items.size) {
                items[value].isSelected = true
            }
        }

    init {
        orientation = HORIZONTAL

        val attrsTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VsBottomNavigationView)

        itemLayoutResId = getItemLayoutResId(attrsTypedArray)
        menuResId = getMenuResId(attrsTypedArray)
        isAutoSelectable = getIsAutoSelectable(attrsTypedArray)

        attrsTypedArray.recycle()
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean = p is LayoutParams

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams = LayoutParams(context, attrs)

    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)

    override fun onFinishInflate() {
        super.onFinishInflate()
        initItems()
        setNextOnGlobalLayoutAction {
            configureItems()
            isFirstOnGlobalLayoutPerformed = true
        }
    }

    override fun onRestoreInstanceState(stateParcelable: Parcelable?) {
        val state = stateParcelable as SavedState
        selectedItemPosition = state.selectedItemPosition
        super.onRestoreInstanceState(state.superState)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superStateParcelable = super.onSaveInstanceState()
        return SavedState(superStateParcelable, selectedItemPosition)
    }

    fun setAutoSelectable(isAutoSelectable: Boolean) {
        this.isAutoSelectable = isAutoSelectable
    }

    fun setItemAnimator(animator: ItemAnimator?) {
        this.animator = animator
        performIfFirstOnGlobalLayoutPerformed(::updateItemsSelection)
    }

    fun setItemAnimatorForPosition(itemPosition: Int, animator: ItemAnimator) {
        checkItemPosition(itemPosition)
        val item = items[itemPosition]
        item.animator = animator
        performIfFirstOnGlobalLayoutPerformed { updateItemSelection(item) }
    }

    fun setItemAnimatorForId(@IdRes itemId: Int, animator: ItemAnimator) {
        val itemPosition = checkItemIdAndGetPosition(itemId)
        val item = items[itemPosition]
        item.animator = animator
        performIfFirstOnGlobalLayoutPerformed { updateItemSelection(item) }
    }

    fun setOnItemClickAction(action: ((itemId: Int) -> Unit)?) {
        onItemClickAction = action
    }

    fun setOnItemSelectAction(action: ((itemId: Int) -> Unit)?) {
        onItemSelectAction = action
    }

    fun selectItemById(@IdRes itemId: Int) {
        val itemPosition = checkItemIdAndGetPosition(itemId)
        selectItemByPositionUnsafe(itemPosition)
    }

    fun selectItemByPosition(itemPosition: Int) {
        checkItemPosition(itemPosition)
        selectItemByPositionUnsafe(itemPosition)
    }

    @LayoutRes
    private fun getItemLayoutResId(attrsTypedArray: TypedArray): Int {
        val resId = attrsTypedArray.getResourceId(R.styleable.VsBottomNavigationView_itemLayout, 0)
        check(resId != 0) { "itemLayout must be specified." }
        return resId
    }

    @MenuRes
    private fun getMenuResId(attrsTypedArray: TypedArray): Int {
        val resId = attrsTypedArray.getResourceId(R.styleable.VsBottomNavigationView_menu, 0)
        check(resId != 0) { "sample must be specified." }
        return resId
    }

    private fun getIsAutoSelectable(attrsTypedArray: TypedArray): Boolean =
        attrsTypedArray.getBoolean(R.styleable.VsBottomNavigationView_isAutoSelectable, true)

    private fun checkItemPosition(itemPosition: Int) =
        require(itemPosition in 0 until items.size) { "itemPosition is out of bounds" }

    private fun checkItemIdAndGetPosition(@IdRes itemId: Int): Int {
        val itemPosition = items.indexOfFirst { item -> item.id == itemId }
        require(itemPosition != -1) { "There is no item_sample with such id" }
        return itemPosition
    }

    private fun selectItemByPositionUnsafe(itemPosition: Int) {
        selectedItemPosition = itemPosition
        performIfFirstOnGlobalLayoutPerformed(::updateItemsSelection)
        onItemSelectAction?.invoke(items[itemPosition].id)
    }

    private fun initItems() {
        val menu = inflateMenu()
        val layoutInflater = LayoutInflater.from(context)
        val inflatedItemPositions = getAndCheckInflatedItemPositions(menu.size())

        for (position in 0 until menu.size()) {
            val menuItem = menu.getItem(position)
            val item = createAndConfigureItem(menuItem, position, inflatedItemPositions, layoutInflater)
            items.add(item)
        }
    }

    private fun inflateMenu(): Menu {
        val menuInflater = MenuInflater(context)
        val menu = PopupMenu(context, null).menu
        menuInflater.inflate(menuResId, menu)
        return menu
    }

    private fun getAndCheckInflatedItemPositions(menuSize: Int): List<Int> {
        val positions = getInflatedItemPositions()
        checkInflatedItemPositions(positions, menuSize)
        return positions
    }

    private fun getInflatedItemPositions(): List<Int> =
        children
            .map { childView -> childView.layoutParams as LayoutParams }
            .map { bottomNavigationViewLayoutParams -> bottomNavigationViewLayoutParams.position }
            .filter { position -> position != -1 }
            .toList()

    private fun checkInflatedItemPositions(positions: List<Int>, menuSize: Int) {
        val set = HashSet<Int>()
        positions.forEach { position ->
            check(position != -1) { "layout_position must be specified" }
            check(position < menuSize) { "layout_position=$position is out of sample bounds" }
            check(set.add(position)) { "Duplicate of layout_position=$position" }
        }
    }

    private fun createAndConfigureItem(
        menuItem: MenuItem,
        position: Int,
        inflatedItemPositions: List<Int>,
        layoutInflater: LayoutInflater
    ): Item {
        val itemView = getOrCreateItemView(position, inflatedItemPositions, layoutInflater)
        configureItemView(itemView, menuItem)
        return createItem(itemView, menuItem)
    }

    private fun getOrCreateItemView(
        position: Int,
        inflatedItemPositions: List<Int>,
        layoutInflater: LayoutInflater
    ): View =
        if (position in inflatedItemPositions) {
            getChildAt(position)
        } else {
            val itemView = layoutInflater.inflate(itemLayoutResId, this, false)
            addView(itemView, position)
            itemView
        }

    private fun configureItemView(itemView: View, menuItem: MenuItem) {
        itemView.id = menuItem.itemId
        itemView.isClickable = true
        itemView.setOnClickListener {
            onItemClickAction?.invoke(itemView.id)
            if (isAutoSelectable) {
                selectItemById(itemView.id)
            }
        }
    }

    private fun createItem(itemView: View, menuItem: MenuItem) =
        Item(
            menuItem,
            ItemViewHolder(itemView),
            null
        )

    private fun configureItems() {
        items.forEach { item -> configureItem(item) }
    }

    private fun configureItem(item: Item) {
        val animator = getAnimatorForItem(item) ?: return
        with(animator) { item.viewHolder.onCreate(item.menuItem) }
    }

    private fun updateItemsSelection() =
        items.forEach { item ->
            if (item.isSelectionChanged) {
                updateItemSelection(item)
            }
        }

    private fun updateItemSelection(item: Item) {
        val animator = getAnimatorForItem(item) ?: return

        if (item.isSelected) {
            with(animator) { item.viewHolder.onSelect(item.menuItem) }
        } else {
            with(animator) { item.viewHolder.onDeselect(item.menuItem) }
        }
    }

    private fun getAnimatorForItem(item: Item): ItemAnimator? = item.animator ?: animator

    private fun performIfFirstOnGlobalLayoutPerformed(action: () -> Unit) {
        if (isFirstOnGlobalLayoutPerformed) {
            action()
        }
    }

    private fun setNextOnGlobalLayoutAction(action: () -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                removeOnGlobalLayoutListenerCompat(this)
                action()
            }
        })
    }

    private fun removeOnGlobalLayoutListenerCompat(victim: ViewTreeObserver.OnGlobalLayoutListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewTreeObserver.removeOnGlobalLayoutListener(victim)
        } else {
            viewTreeObserver.removeGlobalOnLayoutListener(victim)
        }
    }

    class SavedState : BaseSavedState {

        val selectedItemPosition: Int

        constructor(inParcel: Parcel) : super(inParcel) {
            selectedItemPosition = inParcel.readInt()
        }

        constructor(superState: Parcelable?, position: Int) : super(superState) {
            this.selectedItemPosition = position
        }

        override fun writeToParcel(outParcel: Parcel, flags: Int) {
            super.writeToParcel(outParcel, flags)
            outParcel.writeInt(selectedItemPosition)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {

            override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

    class LayoutParams : LinearLayout.LayoutParams {

        val position: Int

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            width = 0
            weight = 1f

            val attrsTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VsBottomNavigationView_Layout)
            position = attrsTypedArray.getInteger(R.styleable.VsBottomNavigationView_Layout_layout_menuPosition, -1)
            attrsTypedArray.recycle()
        }

        constructor(width: Int, height: Int, weight: Float) : super(width, height, weight) {
            position = -1
        }
    }
}
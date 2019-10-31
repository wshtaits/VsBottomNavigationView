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
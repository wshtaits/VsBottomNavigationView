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

open class ItemAnimator {

    open fun ItemViewHolder.onCreate(menuItem: MenuItem) { /* Stub */ }

    open fun ItemViewHolder.onSelect(menuItem: MenuItem) { /* Stub */ }

    open fun ItemViewHolder.onDeselect(menuItem: MenuItem) { /* Stub */ }
}
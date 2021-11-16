/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelabs.state.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TodoViewModel : ViewModel() {
    /**
     * State<T> transformations are regular Kotlin code.
     * Compose will observe any State<T> read by a composable,
     * even if the read happens in a regular Kotlin function called by the composable.
     * Here we're reading from currentEditPosition and todoItems to generate currentEditItem.
     * Compose will recompose a composable that reads currentEditItem whenever either changes.

     * For State<T> transformations to work, the state must be read from a State<T> object.
     * If you had defined currentEditPosition as a regular Int
     * (private var currentEditPosition = -1),
     * compose would not be able to observe changes to it.
     *
     * Compose 는 State 혹은 State 의 Transformations 에 의해 발생되는 변화만을 "observe"한다.
     * 그렇기 때문에, 단순 Primitive type이나 State가 아닌 타입을 observe 하는 행위는 recomposition 을
     * invoking 하지 않는다.
     */
    // private state
    private var currentEditPosition by mutableStateOf(-1)
    // private var currentEditPosition = -1 // It should not work at all

    var todoItems = mutableStateListOf<TodoItem>()
        private set

    // state
    val currentEditItem: TodoItem?
        get() = todoItems.getOrNull(currentEditPosition)

    fun addItem(item: TodoItem) {
        viewModelScope.launch {
            addItemInBackground(item)
        }
    }

    private suspend fun addItemInBackground(item: TodoItem) {
        withContext(Dispatchers.IO) {
            todoItems.add(item)
        }
    }

    private suspend fun removeItemInBackground(item: TodoItem) {
        withContext(Dispatchers.IO) {
            todoItems.remove(item)
        }
    }

    fun removeItem(item: TodoItem) {
        viewModelScope.launch {
            removeItemInBackground(item)
            onEditDone()
        }
    }

    // event: onEditItemSelected
    fun onEditItemSelected(item: TodoItem) {
        currentEditPosition = todoItems.indexOf(item)
    }

    // event: onEditDone
    fun onEditDone() {
        currentEditPosition = -1
    }

    // event: onEditItemChange
    fun onEditItemChange(item: TodoItem) {
        val currentItem = requireNotNull(currentEditItem)
        require(currentItem.id == item.id) {
            "You can only change an item with the same id as currentEditItem"
        }

        todoItems[currentEditPosition] = item
    }
}

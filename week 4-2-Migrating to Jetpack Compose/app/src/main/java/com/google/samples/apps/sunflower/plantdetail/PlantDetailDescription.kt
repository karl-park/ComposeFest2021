/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.plantdetail

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.ui.SunflowerTheme
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel

@Composable
fun PlantDetail(viewModel: PlantDetailViewModel) {
    val plant by viewModel.plant.observeAsState()
    plant?.let { PlantDetailDescription(it) }
}

@Composable
fun PlantDetailDescription(plant: Plant) {
    Surface {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.margin_normal))
                .scrollable(scrollState, Orientation.Vertical)
        ) {
            PlantDetailName(plant.name)
            PlantWateringHeader()
            PlantWatering(plant.wateringInterval)
            PlantDescription(plant.description)
        }

    }
}

@Composable
fun PlantDetailName(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_small))
            .wrapContentWidth(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.h5
    )
}

@Composable
fun PlantWateringHeader(
    modifier: Modifier = Modifier
) {
    val text = stringResource(R.string.watering_needs_prefix)
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.margin_small),
                end = dimensionResource(R.dimen.margin_small),
                top = dimensionResource(R.dimen.margin_normal)
            )
            .wrapContentWidth(Alignment.CenterHorizontally),
        style = TextStyle(
            color = MaterialTheme.colors.secondary,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun PlantWatering(
    wateringInterval: Int,
    modifier: Modifier = Modifier
) {
    val quantityString = LocalContext.current.resources.getQuantityString(
        R.plurals.watering_needs_suffix,
        wateringInterval,
        wateringInterval
    )
    Text(
        text = quantityString,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.margin_small)
            )
    )
}

@Composable
fun PlantDescription(
    description: String?,
    modifier: Modifier = Modifier
) {
    if (description == null) {
        Text(
            text = "",
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 555.dp)
                .padding(
                    start = dimensionResource(R.dimen.margin_small),
                    end = dimensionResource(R.dimen.margin_small),
                    top = dimensionResource(R.dimen.margin_small)
                )
                .wrapContentWidth(Alignment.CenterHorizontally),
        )
    } else {
        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    movementMethod = LinkMovementMethod.getInstance()
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 555.dp)
                .padding(
                    start = dimensionResource(R.dimen.margin_small),
                    end = dimensionResource(R.dimen.margin_small),
                    top = dimensionResource(R.dimen.margin_small)
                )
                .wrapContentWidth(Alignment.CenterHorizontally),
            update = {
                it.text = HtmlCompat.fromHtml(description, FROM_HTML_MODE_COMPACT)
            }
        )
    }
}

@Preview
@Composable
private fun PlantWateringHeaderPreview() {
    SunflowerTheme {
        val plant = Plant(
            plantId = "1",
            name = "Tomato",
            description = "A red vegetable",
            growZoneNumber = 1,
            wateringInterval = 2,
            imageUrl = ""
        )
        PlantDetailDescription(plant)
    }
}

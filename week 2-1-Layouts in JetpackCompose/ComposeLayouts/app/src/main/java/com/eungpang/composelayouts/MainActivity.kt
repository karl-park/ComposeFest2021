package com.eungpang.composelayouts

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.eungpang.composelayouts.ui.theme.ComposeLayoutsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @ExperimentalCoilApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLayoutsTheme {
                PhotographerCards()
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun PhotographerCards(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Compose Layout Codelab")
                },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(
                            Intent(context, SecondActivity::class.java)
                        )
                    }) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        PhotographerCardList()
    }
}

@ExperimentalCoilApi
@Composable
fun PhotographerCardList() {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val profiles = mutableListOf<Profile>().apply {
        addAll((0 until 30).map {
            Profile(
                name = "name-$it",
                status = "$it minutes ago",
                profileURL = "https://developer.android.com/images/brand/Android_Robot.png")
        })
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                coroutineScope.launch {
                    // 0 is the first item index
                    scrollState.animateScrollToItem(0)
                }
            }) {
                Text("Scroll to the top")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                coroutineScope.launch {
                    // listSize - 1 is the last index of the list
                    scrollState.animateScrollToItem(profiles.size - 1)
                }
            }) {
                Text("Scroll to the end")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = scrollState
        ) {
            items(profiles.size) {
                PhotographerCardBody(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    profile = profiles[it]
                )
            }
        }
    }
}

data class Profile(
    val name: String, // "Alfred Sisley"
    val status: String, // "3 minutes ago"
    val profileURL: String // "https://developer.android.com/images/brand/Android_Robot.png"
)

@ExperimentalCoilApi
@Composable
fun PhotographerCardBody(modifier: Modifier = Modifier, profile: Profile) {
    Row(modifier
        .background(Color.Green)
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colors.surface)
        .clickable(onClick = {

        })
        .padding(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {
            // Image goes here
            Image(
                painter = rememberImagePainter(
                    data = profile.profileURL
                ),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        }

        Column(
            modifier = Modifier
                .background(Color.Blue, shape = RectangleShape)
                .padding(2.dp)
                .background(Color.Red, shape = RectangleShape)
                .padding(5.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = profile.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            CompositionLocalProvider(LocalContentAlpha.provides(ContentAlpha.medium)) {
                Text(profile.status, style = MaterialTheme.typography.body2)
            }
        }

    }
}

fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = this.then(
    layout { measurable, constraints ->
        // measurable: child to be measured and placed
        // constraints: minimum and maximum for the width and height of the child
        val placeable = measurable.measure(constraints)

        // Check the composable has a first baseline
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        // Height of the composable with padding - first baseline
        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(placeable.width, height) {
            // Where the composable gets placed
            placeable.placeRelative(0, placeableY)
        }
    }
)

@Composable
fun MyColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.minHeight) {
            // Place children in the parent layout

            // Track the y co-ord we have placed children up to
            var yPosition = 20
            println("\n\nplaceables.size: ${placeables.size}\n==================\n\n")

            placeables.forEach { placeable ->
                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height

                println("yPosition: $yPosition // placeable.height = ${placeable.height}")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeLayoutsTheme {
        PhotographerCards()
    }
}
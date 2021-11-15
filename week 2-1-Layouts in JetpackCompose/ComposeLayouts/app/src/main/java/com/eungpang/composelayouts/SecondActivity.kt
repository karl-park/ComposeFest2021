package com.eungpang.composelayouts

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.eungpang.composelayouts.ui.theme.ComposeLayoutsTheme
import kotlin.random.Random

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLayoutsTheme {
                StaggeredGridLayout(
                    rows = 7
                ) {
                    ConstraintLayoutContent()

                    (0 .. 47).forEach {
                        Chip(
                            modifier = Modifier.padding(8.dp),
                            text = "Hello ${(161 * Random.nextInt())}"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StaggeredGridLayout(
    modifier: Modifier = Modifier,
    rows: Int,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    Layout(
        modifier = modifier.horizontalScroll(
            state = scrollState
        ),
        content = content
    ) { measureables, constraints ->
        val rowWidths = IntArray(rows) { 0 }
        val rowHeights = IntArray(rows) { 0 }

        val placeables = measureables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)

            val row = index.rem(rows)
            rowWidths[row] += placeable.width
            rowHeights[row] = maxOf(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        // Grid's height is the sum of the tallest element of each row coerced to the height constraints
        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth .. constraints.maxWidth) ?: constraints.minWidth
        val height = rowHeights.sum()
            .coerceIn(constraints.minHeight .. constraints.maxHeight)

        val rowY = IntArray(rows) { 0 }.apply {
            for (i in 1 until rows) {
                this[i] = this[i-1] + rowHeights[i-1]
            }
        }

        layout(width, height) {
            val rowX = IntArray(rows) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val row = index.rem(rows)
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row],
                )

                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    text: String
) {
    Card(
        modifier = modifier,
        border = BorderStroke(width = Dp.Hairline, color = Color.Black),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = 4.dp,
                horizontal = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(16.dp).background(
                    color = MaterialTheme.colors.secondary
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(text = text)
        }
    }
}

@Composable
fun ConstraintLayoutContent() {
    ConstraintLayout {
        // Creates references for the three composables
        // in the ConstraintLayout's body
        val (button1, button2, text) = createRefs()

        Button(
            onClick = { /* Do something */ },
            modifier = Modifier.constrainAs(button1) {
                top.linkTo(parent.top, margin = 16.dp)
            }
        ) {
            Text("Button 1")
        }

        val horizontalCenterGuideline = createGuidelineFromStart(fraction = 0.5f)

        Text("Text", Modifier.constrainAs(text) {
            top.linkTo(button1.bottom, margin = 16.dp)
             centerAround(horizontalCenterGuideline)
        })

        val barrier = createEndBarrier(button1, text)
        Button(
            onClick = { /* Do something */ },
            modifier = Modifier.constrainAs(button2) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(barrier)
            }
        ) {
            Text("Button 2")
        }
    }
}


@Preview
@Composable
fun ChipPreview() {
    ComposeLayoutsTheme {
        Chip(text = "Hi there")
    }
}

@Preview
@Composable
fun DefaultView() {
    ComposeLayoutsTheme {
        StaggeredGridLayout(
            rows = 3
        ) {
            ConstraintLayoutContent()

            (0 .. 10).forEach {
                Chip(text = "Hello ${(161 * Random.nextInt())}")
            }
        }
    }
}

@Preview
@Composable
fun DefaultView2() {
    ConstraintLayout {
        val text = createRef()
        val guideline = createGuidelineFromStart(fraction = 0.5f)
        Text(
            "This is a very very very very very very very long text",
            Modifier.constrainAs(text) {
                start.linkTo(guideline)
                end.linkTo(parent.end)
                width = Dimension.preferredWrapContent
                // linkTo(start = guideline, end = parent.end)
            }
        )
    }
}

@Composable
fun DecoupledConstraintLayout() {
    BoxWithConstraints {
        val constraints = if (maxWidth < maxHeight) {
            decoupledConstraints(margin = 16.dp) // Portrait constraints
        } else {
            decoupledConstraints(margin = 32.dp) // Landscape constraints
        }

        ConstraintLayout(constraints) {
            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.layoutId("button")
            ) {
                Text("Button")
            }

            Text("Text", Modifier.layoutId("text"))
        }
    }
}

private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val button = createRefFor("button")
        val text = createRefFor("text")

        constrain(button) {
            top.linkTo(parent.top, margin= margin)
        }
        constrain(text) {
            top.linkTo(button.bottom, margin)
        }
    }
}

@Composable
fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.Start),
            text = text1
        )

        Divider(color = Color.Black, modifier = Modifier.fillMaxHeight().width(1.dp))
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .wrapContentWidth(Alignment.End),
            text = text2
        )
    }
}

@Preview
@Composable
fun TwoTextsPreview() {
    ComposeLayoutsTheme {
        Surface {
            TwoTexts(text1 = "Hi\n\nasdf", text2 = "there")
        }
    }
}
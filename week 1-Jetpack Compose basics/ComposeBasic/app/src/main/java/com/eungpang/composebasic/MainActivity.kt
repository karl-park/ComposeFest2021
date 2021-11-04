package com.eungpang.composebasic

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eungpang.composebasic.ui.theme.ComposeBasicTheme
import com.eungpang.composebasic.ui.theme.Typography
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBasicTheme {
                MyApp(
                    List(100) { "${it+1}" }
                )
            }
        }
    }
}

@Composable
fun MyApp(names: List<String>) {
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
    var isExpandAll by rememberSaveable { mutableStateOf(false) }

    if (shouldShowOnboarding) {
        OnboardingScreen { shouldShowOnboarding = false }
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    isExpandAll = !isExpandAll
                }) {
                    Icon(
                        if (isExpandAll) Icons.Filled.Add else Icons.Filled.List,
                        "Expand"
                    )
                }
            }
        ) {
            Greetings(names, isExpandAll)
        }
    }
}

@Composable
private fun Greetings(names: List<String>, isExpandAll: Boolean = false) {
    val listState = rememberLazyListState()
    if (listState.isScrollInProgress.not()) {
        Log.e("Karl", "stop!")
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        items(items = names) { name ->
            Greeting(name, isExpandAll)
        }
    }
}

@Composable
fun Greeting(name: String, isExpandAll: Boolean) {
    var hasExpandedFromParent by rememberSaveable { mutableStateOf(isExpandAll) }
    var isClicked by rememberSaveable { mutableStateOf(false) }

    if (isExpandAll != hasExpandedFromParent) {
        isClicked = isExpandAll
    }

    if (hasExpandedFromParent != isExpandAll) {
        hasExpandedFromParent = isExpandAll
    }

    val extraPadding by animateDpAsState(
        targetValue = if (isClicked) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.fillMaxWidth().padding(4.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier.weight(1f).padding(bottom = extraPadding.coerceAtLeast(0.dp))
            ) {
                Text(text = "Hello")
                Text(text = name, style = Typography.h1.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold
                ))
                if (isClicked) {
                    Text(
                        text = ("Composem ipsum color sit lazy, " +
                                "padding theme elit, sed do bouncy. ").repeat(4),
                    )
                }
            }

            IconButton(onClick = { isClicked = !isClicked }) {
                Icon(
                    imageVector = if (isClicked) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = stringResource(if (isClicked) R.string.show_less else R.string.show_more),
                )
            }
        }


    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    ComposeBasicTheme {
        OnboardingScreen {}
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Composable
fun GreetingsPreviewNight() {
    ComposeBasicTheme {
        Greetings(listOf("Android", "Karl"))
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    name = "DefaultPreviewLight"
)
@Composable
fun GreetingsPreviewLight() {
    ComposeBasicTheme {
        Greetings(listOf("Android", "Karl"))
    }
}
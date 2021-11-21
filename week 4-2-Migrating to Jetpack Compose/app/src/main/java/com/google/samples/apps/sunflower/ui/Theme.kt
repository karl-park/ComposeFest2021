package com.google.samples.apps.sunflower.ui

import androidx.compose.runtime.Composable
import com.google.android.material.composethemeadapter.MdcTheme

//val LightColors = lightColors(
//    primary = ,
//    primaryVariant = ,
//    secondary = ,
//    secondaryVariant = ,
//    background = ,
//    surface = ,
//    error = ,
//    onPrimary = ,
//    onSecondary = ,
//    onBackground = ,
//    onSurface = ,
//    onError =
//)

@Composable
fun SunflowerTheme(
    content: @Composable () -> (Unit)
) {
    MdcTheme {
        content()
    }
}
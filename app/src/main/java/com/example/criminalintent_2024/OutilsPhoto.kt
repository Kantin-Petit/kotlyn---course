package com.example.criminalintent_2024

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.roundToInt

fun getBitmapRedim(chemin: String, largeurFinale: Int, hauteurFinale: Int): Bitmap {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(chemin, options)

    val largeurSource = options.outWidth.toFloat()
    val hauteurSource = options.outHeight.toFloat()

    val ratioRedim = if (hauteurSource <= hauteurFinale && largeurSource <= largeurFinale) {
        1
    } else {
        val ratioHauteur = hauteurSource/hauteurFinale
        val ratioLargeur = largeurSource/largeurFinale
        minOf(ratioHauteur, ratioLargeur).roundToInt()
    }

    return BitmapFactory.decodeFile(chemin, BitmapFactory.Options().apply {
        inSampleSize = ratioRedim
    })
}
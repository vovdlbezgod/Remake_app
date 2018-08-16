package com.k33p.remake_app.helpers

import android.graphics.*
import com.k33p.remake_app.activities.PhotoEditingActivity

// Cutting the object for inpainting
fun deletingMaskFromSource(src: Bitmap?, mask: Bitmap?): Bitmap? {
    var mask = mask
    //mask = createTransparentBitmapFromBitmap(mask, Color.BLACK);
    if (mask != null) {
        val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
        tempCanvas.drawBitmap(src!!, 0f, 0f, paint)
        mask = Bitmap.createBitmap(mask)//заменяем на маске Color.BLACK на прозрачный
        tempCanvas.drawBitmap(mask!!, 0f, 0f, null)
        paint.xfermode = null
        return result
    }
    return null
}


fun selectingObjectsOnMask(src: Bitmap?, masks: ArrayList<Bitmap>): Bitmap? {
    if (masks != null) {
        var result : Bitmap? = src

        for (mask in masks) {
            result = selectingObjectOnMask(result, mask, Color.GREEN)
        }
        return result
    }
    return null
}

// Highlighting the object
fun selectingObjectOnMask(src: Bitmap?, mask: Bitmap?, color: Int): Bitmap? {
    if (mask != null) {
        val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(result)
        val tempMask = Bitmap.createBitmap(mask)

        val colored = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val canvasColored = Canvas(colored)
        canvasColored.drawColor(color)
        val paintColored = Paint(Paint.ANTI_ALIAS_FLAG)
        paintColored.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        canvasColored.drawBitmap(colored, 0f, 0f, null)
        canvasColored.drawBitmap(tempMask!!, 0f, 0f, paintColored)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
        tempCanvas.drawBitmap(src!!, 0f, 0f, null)
        tempCanvas.drawBitmap(colored, 0f, 0f, paint)
        paint.xfermode = null
        return result
    }
    return null
}

// Get the object which was gotten with the mask
fun getObjectByMask(src: Bitmap?, mask: Bitmap?): Bitmap? {
    var mask = mask
    //mask = createTransparentBitmapFromBitmap(mask, Color.BLACK);
    if (mask != null) {
        val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        tempCanvas.drawBitmap(src!!, 0f, 0f, null)
        mask = Bitmap.createBitmap(mask)//заменяем на маске Color.BLACK на прозрачный
        tempCanvas.drawBitmap(mask!!, 0f, 0f, paint)
        paint.xfermode = null
        return result
    }
    return null
}




package com.k33p.remake_app.helpers

import android.graphics.*
import com.k33p.remake_app.activities.PhotoEditingActivity

// Cutting the object for inpainting
fun deletingMaskFromSource(src: Bitmap?, mask: Bitmap?): Bitmap? {
    if (mask != null) {
        val picw = src!!.width
        val pich = src.height
        val pixSrc = IntArray(picw * pich)
        val pix = IntArray(picw * pich)
        mask.getPixels(pix, 0, picw, 0, 0, picw, pich)
        src.getPixels(pixSrc, 0, picw, 0, 0, picw, pich)

        for (y in 0 until pich) {
            // from left to right
            for (x in 0 until picw) {
                val index = y * picw + x
                if (pix[index] == Color.BLACK) {
                    pix[index] = pixSrc[index]
                } else {
                    break
                }
            }

            // from right to left
            for (x in picw - 1 downTo 0) {
                val index = y * picw + x
                if (pix[index] == Color.BLACK) {
                    pix[index] = pixSrc[index]
                } else {
                    break
                }
            }
        }

        return Bitmap.createBitmap(pix, picw, pich,
                Bitmap.Config.RGB_565)
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


        val colored = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val canvasColored = Canvas(colored)
        canvasColored.drawColor(color)
        val paintColored = Paint(Paint.ANTI_ALIAS_FLAG)
        paintColored.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        canvasColored.drawBitmap(colored, 0f, 0f, null)
        val tempMask = PhotoEditingActivity.createTransparentBitmapFromBitmap(mask, Color.BLACK)
        canvasColored.drawBitmap(tempMask!!, 0f, 0f, paintColored)


        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)
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
        mask = PhotoEditingActivity.createTransparentBitmapFromBitmap(mask, Color.BLACK)//заменяем на маске Color.BLACK на прозрачный
        tempCanvas.drawBitmap(mask!!, 0f, 0f, paint)
        paint.xfermode = null
        return result
    }
    return null
}




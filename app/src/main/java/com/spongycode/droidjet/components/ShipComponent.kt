package com.spongycode.droidjet.components

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.res.ResourcesCompat
import com.spongycode.droidjet.interfaces.GameComponent
import com.spongycode.droidjet.R

class ShipComponent(
    context: Context,
    private val screenWidth: Int,
    private val screenHeight: Int,
    val shipWidth: Float,
    val shipHeight: Float,
) : GameComponent {

    private val drawable =
        ResourcesCompat.getDrawable(context.resources, R.drawable.ship, null)

    var shipXPosition: Float = screenWidth / 2f - shipWidth / 2f
        private set
    var shipYPosition: Float = screenHeight - (screenHeight * 0.30f) - shipHeight

    override fun update() {
    }

    override fun render(canvas: Canvas) {
        drawable?.setBounds(
            shipXPosition.toInt(),
            shipYPosition.toInt(),
            (shipXPosition + shipWidth).toInt(),
            (shipYPosition + shipHeight).toInt()
        )
        drawable?.draw(canvas)
    }

    fun moveShipBy(distanceX: Float, distanceY: Float) {
        val newShipXPosition = shipXPosition + distanceX
        val newShipYPosition = shipYPosition + distanceY

        val minX = 0f
        val minY = 0f
        val maxX = screenWidth - shipWidth
        val maxY = screenHeight - shipHeight

        shipXPosition = newShipXPosition.coerceIn(minX, maxX)
        shipYPosition = newShipYPosition.coerceIn(minY, maxY)
    }

    fun reset() {
        shipXPosition = screenWidth / 2f - shipWidth / 2f
    }

}

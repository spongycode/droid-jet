package com.spongycode.droidjet.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.content.res.ResourcesCompat
import com.spongycode.droidjet.interfaces.GameComponent
import com.spongycode.droidjet.views.GameView
import com.spongycode.droidjet.R
import kotlin.random.Random

class BarrierComponent(
    context: Context,
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val barrierHeight: Float,
) : GameComponent {

    private var barrierSpeed = GameView.getStartSpeed()
    private var currentBarrierSpeed = GameView.getStartSpeed()
    private val maxBarrierSpeed = 300f

    private var barriers: MutableList<Triple<Float, Float, Float>> = mutableListOf()
    private var totalHeightCovered: Float = 0f

    private val drawable =
        ResourcesCompat.getDrawable(context.resources, R.drawable.horizontal_barrier, null)

    override fun update() {
        val barriersToRemove: MutableList<Triple<Float, Float, Float>> = mutableListOf()
        barriers.forEachIndexed { index, (barrierX, barrierY, barrierWidth) ->
            if (barrierY > screenHeight) {
                barriersToRemove.add(barriers[index])
                GameView.updateScore(2f)
            } else {
                barriers[index] = Triple(barrierX, barrierY + currentBarrierSpeed, barrierWidth)
            }
        }
        barriers.removeAll(barriersToRemove)

        val allBarriersBelowHalfScreen = barriers.all { it.second > screenHeight / 2 }
        if (allBarriersBelowHalfScreen) {
            val minWidthPercent = 0.20f
            val maxWidthPercent = 0.40f
            val randomWidthPercent =
                Random.nextFloat() * (maxWidthPercent - minWidthPercent) + minWidthPercent
            val barrierWidth = screenWidth * randomWidthPercent
            val left = Random.nextInt((screenWidth - barrierWidth).toInt()).toFloat()
            barriers.add(Triple(left, -barrierHeight, barrierWidth))
        }

        totalHeightCovered += barrierSpeed
        for (i in barriers.indices) {
            val (barrierX, barrierY, barrierWidth) = barriers[i]
            barriers[i] = Triple(barrierX, barrierY.coerceAtLeast(-barrierHeight), barrierWidth)
        }

        val accelerationRate = 0.1f
        val increasedSpeed = barrierSpeed + (accelerationRate * GameView.getScore())
        currentBarrierSpeed = increasedSpeed.coerceAtMost(maxBarrierSpeed)
    }

    override fun render(canvas: Canvas) {
        barriers.forEach { (barrierX, barrierY, barrierWidth) ->
            drawable?.setBounds(
                barrierX.toInt(),
                barrierY.toInt(),
                (barrierX + barrierWidth).toInt(),
                (barrierY + barrierHeight).toInt()
            )
            drawable?.draw(canvas)
        }
    }

    fun collidesWith(ShapeX: Float, shapeY: Float, shapeWidth: Float, shapeHeight: Float): Boolean {
        val shapeRect = RectF(ShapeX, shapeY, ShapeX + shapeWidth, shapeY + shapeHeight)
        for ((barrierX, barrierY, barrierWidth) in barriers) {
            val barrierRect = RectF(0f, 0f, barrierWidth, barrierHeight)
            barrierRect.offsetTo(barrierX, barrierY)
            if (RectF.intersects(shapeRect, barrierRect)) {
                return true
            }
        }
        return false
    }

    fun reset() {
        barriers.clear()
        currentBarrierSpeed = GameView.getStartSpeed()
        totalHeightCovered = 0f
    }
}

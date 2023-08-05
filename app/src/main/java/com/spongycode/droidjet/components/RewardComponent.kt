package com.spongycode.droidjet.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.content.res.ResourcesCompat
import com.spongycode.droidjet.interfaces.GameComponent
import com.spongycode.droidjet.views.GameView
import com.spongycode.droidjet.R

class RewardComponent(
    context: Context,
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val rewardWidth: Float,
    private val rewardHeight: Float,
    private val barrierComponent: BarrierComponent
) : GameComponent {

    private val baseSpawnChance = 0.05f
    private val maxSpawnChance = 0.2f
    private val normalizedScore = GameView.getScore() / 1000f
    private val spawnChance1 = baseSpawnChance + (maxSpawnChance - baseSpawnChance) * normalizedScore

    private val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.reward, null)

    private var rewardSpeed = GameView.getStartSpeed()
    private var currentRewardSpeed = GameView.getStartSpeed()
    private val maxRewardSpeed = 300f

    private var rewards: MutableList<Pair<Float, Float>> = mutableListOf()

    override fun update() {
        rewards.forEachIndexed { index, (rewardX, rewardY) ->
            rewards[index] = Pair(rewardX, rewardY + currentRewardSpeed)
        }
        rewards.removeIf { (_, rewardY) -> rewardY > screenHeight }

        if (Math.random() < spawnChance1) {
            val left = (Math.random() * (screenWidth - rewardWidth)).toFloat()
            val newRewardPosition = Pair(left, -rewardHeight)
            if (!barrierComponent.collidesWith(
                    newRewardPosition.first,
                    newRewardPosition.second,
                    rewardWidth,
                    rewardHeight
                ) && !collidesWith(
                    newRewardPosition.first,
                    newRewardPosition.second,
                    rewardWidth * 2,
                    rewardHeight * 2,
                    false
                )
            ) {
                rewards.add(newRewardPosition)
            }
        }

        for (i in rewards.indices) {
            val (rewardX, rewardY) = rewards[i]
            rewards[i] = Pair(rewardX, rewardY.coerceAtLeast(-rewardHeight))
        }

        val accelerationRate = 0.1f
        val increasedSpeed = rewardSpeed + (accelerationRate * GameView.getScore())
        currentRewardSpeed = increasedSpeed.coerceAtMost(maxRewardSpeed)
    }

    override fun render(canvas: Canvas) {
        rewards.forEach { (rewardX, rewardY) ->
            drawable?.setBounds(
                rewardX.toInt(),
                rewardY.toInt(),
                (rewardX + rewardWidth).toInt(),
                (rewardY + rewardHeight).toInt()
            )
            drawable?.draw(canvas)
        }
    }

    fun collidesWith(
        ShapeX: Float,
        shapeY: Float,
        shapeWidth: Float,
        shapeHeight: Float,
        shipMode: Boolean = true
    ): Boolean {
        val shapeRect = RectF(ShapeX, shapeY, ShapeX + shapeWidth, shapeY + shapeHeight)
        for ((rewardX, rewardY) in rewards) {
            val rewardRect = RectF(0f, 0f, rewardWidth, rewardHeight)
            rewardRect.offsetTo(rewardX, rewardY)
            if (RectF.intersects(shapeRect, rewardRect)) {
                if (shipMode) {
                    rewards.remove(Pair(rewardX, rewardY))
                    GameView.updateScore(5f)
                }
                return true
            }
        }

        return false
    }

    fun reset() {
        rewards.clear()
        currentRewardSpeed = GameView.getStartSpeed()
    }
}

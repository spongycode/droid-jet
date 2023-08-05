package com.spongycode.droidjet.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.spongycode.droidjet.R
import com.spongycode.droidjet.components.BarrierComponent
import com.spongycode.droidjet.components.RewardComponent
import com.spongycode.droidjet.components.ShipComponent
import kotlin.math.sqrt

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), Runnable {

    private var gameThread: Thread? = null

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels
    private val maxTouchDistance = 300f

    private val ship = ShipComponent(
        context = context,
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        shipWidth = 150f,
        shipHeight = 150f,
    )
    private val barriers = BarrierComponent(
        context = context,
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        barrierHeight = 30f,
    )
    private val rewards = RewardComponent(
        context = context,
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        rewardWidth = 50f,
        rewardHeight = 50f,
        barrierComponent = barriers
    )

    @Volatile
    private var isRunning = false
    private var gameOver = false

    private lateinit var canvas: Canvas
    private val paint: Paint = Paint()

    private var onGameOverListener: ((Boolean) -> Unit)? = null

    init {
        paint.color = Color.WHITE
    }

    override fun run() {
        while (isRunning) {
            if (!gameOver && holder.surface.isValid) {
                canvas = holder.lockCanvas()
                canvas.drawColor(Color.WHITE)
                update()
                render()
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private var lastTouchX: Float = 0f
    private var touchStartX: Float = 0f
    private var lastTouchY: Float = 0f
    private var touchStartY: Float = 0f
    private var isMovingShip: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!gameOver) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartX = event.x
                    touchStartY = event.y
                    lastTouchX = event.x
                    lastTouchY = event.y
                    isMovingShip = false
                }

                MotionEvent.ACTION_MOVE -> {
                    if (!gameOver) {
                        val swipeDistanceX = event.x - lastTouchX
                        val swipeDistanceY = event.y - lastTouchY
                        if (!isMovingShip) {
                            val touchDistanceX = event.x - (ship.shipXPosition + ship.shipWidth / 2)
                            val touchDistanceY =
                                event.y - (ship.shipYPosition + ship.shipHeight / 2)
                            val touchDistance =
                                sqrt((touchDistanceX * touchDistanceX + touchDistanceY * touchDistanceY).toDouble()).toFloat()
                            if (touchDistance <= maxTouchDistance) {
                                isMovingShip = true
                            }
                        }
                        if (isMovingShip) {
                            ship.moveShipBy(swipeDistanceX, swipeDistanceY)
                        }
                        lastTouchX = event.x
                        lastTouchY = event.y
                    }
                }

                MotionEvent.ACTION_UP -> {
                    isMovingShip = false
                }
            }
            performClick()
            return true
        }

        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setOnGameOverListener(listener: (Boolean) -> Unit) {
        onGameOverListener = listener
    }

    fun restartGame() {
        gameOver = false
        score = 0f
        ship.reset()
        barriers.reset()
        rewards.reset()
    }

    fun setGameOver() {
        gameOver = true
    }

    fun pause() {
        isRunning = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume() {
        isRunning = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    private fun update() {
        if (!gameOver) {
            ship.update()
            barriers.update()
            rewards.update()

            rewards.collidesWith(
                ship.shipXPosition,
                ship.shipYPosition,
                ship.shipWidth,
                ship.shipHeight
            )

            if (barriers.collidesWith(
                    ship.shipXPosition,
                    ship.shipYPosition,
                    ship.shipWidth,
                    ship.shipHeight
                )
            ) {
                gameOver = true
                onGameOverListener?.invoke(true)
            }
        }
    }

    private fun render() {
        ship.render(canvas)
        barriers.render(canvas)
        rewards.render(canvas)

        var textSize = screenWidth / 8f
        val scoreText = score.toInt().toString()
        paint.textSize = textSize
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = ContextCompat.getColor(context, R.color.decent_green)
        paint.textAlign = Paint.Align.LEFT
        val scoreTextWidth = paint.measureText(scoreText)
        val scoreTextY = screenHeight / 15f
        val scoreTextX = (screenWidth - scoreTextWidth) / 2f
        canvas.drawText(scoreText, scoreTextX, scoreTextY, paint)

        if (gameOver) {
            val gameOverText = resources.getString(R.string.game_over)
            val emojiText = resources.getString(R.string.game_over_emoji)
            textSize = screenWidth / 10f
            paint.textSize = textSize
            var gameOverTextWidth = paint.measureText(gameOverText)
            while (gameOverTextWidth > screenWidth) {
                textSize -= 1
                paint.textSize = textSize
                gameOverTextWidth = paint.measureText(gameOverText)
            }
            val gameOverTextY = screenHeight / 2f
            val gameOverTextX = (screenWidth - gameOverTextWidth) / 2f
            val emojiTextWidth = paint.measureText(emojiText)
            val emojiTextY = gameOverTextY + textSize
            val emojiTextX = (screenWidth - emojiTextWidth) / 2f
            val ribbonTop = gameOverTextY - textSize * 2
            val ribbonBottom = emojiTextY + textSize
            val ribbonPaint = Paint()

            ribbonPaint.color = ContextCompat.getColor(context, R.color.decent_yellow)

            canvas.drawRect(
                0f,
                ribbonTop,
                screenWidth.toFloat(),
                ribbonBottom,
                ribbonPaint
            )
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = ContextCompat.getColor(context, R.color.decent_red)
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(gameOverText, gameOverTextX, gameOverTextY, paint)
            paint.typeface = Typeface.DEFAULT
            paint.color = Color.BLACK
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(emojiText, emojiTextX, emojiTextY, paint)
        }
    }

    companion object {
        private var score = 0f
        private const val startSpeed = 10f

        fun getStartSpeed(): Float {
            return startSpeed
        }

        fun getScore(): Float {
            return score
        }

        fun updateScore(points: Float) {
            score += points
        }
    }
}

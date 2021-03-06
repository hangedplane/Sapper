package com.sapper.android.sapperandroidversion.UI.CustomView

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.view.ViewManager
import android.widget.TextView
import com.sapper.android.sapperandroidversion.R
import com.sapper.android.sapperandroidversion.UI.Listeners.EventsSenderTopPanelListener
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.sp

/**
 * Created by Sergey on 02.02.2018.
 */

class ScoresView : TextView, EventsSenderTopPanelListener {

    private var numberOfBombs = 10
    private val HINT = resources.getString(R.string.score_hint)

    constructor(ctx: Context) : super(ctx) {
        init()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        id = R.id.tp_scores
        textSize = sp(resources.getDimension(R.dimen.topPanel_text_size)).toFloat()

        scoreChanged(0)
    }

    @SuppressLint("SetTextI18n")
    override fun scoreChanged(newScore: Int) {
        text = "$HINT $newScore/$numberOfBombs"
    }

    @SuppressLint("SetTextI18n")
    override fun startNewGame(countOfBomb: Int) {
        numberOfBombs = countOfBomb

        scoreChanged(0)
    }

    override fun gameStart() { }
    override fun clickField() { }
    override fun gameOver(isWin: Boolean) { }
    override fun makePause() { }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.scoresView(theme: Int = 0) = scoresView({}, theme)
inline fun ViewManager.scoresView(init: ScoresView.() -> Unit, theme: Int = 0) =
        ankoView(::ScoresView, theme, init)

class StopWatchView : TextView, EventsSenderTopPanelListener {

    private val mHandler = Handler()
    private var mTime = 0L
    private var mTimeBuffer = 0L
    private var isPauseMode = false

    private val START_HINT = resources.getString(R.string.stopwatch_start_hint)
    private val HINT = resources.getString(R.string.stopwatch_hint)

    private val timeSaverRunnable = object : Runnable {

        override fun run() {
            mTimeBuffer++

            mHandler.postDelayed(this, 1000)
        }
    }

    private val timeUpdaterRunnable = object : Runnable {

        @SuppressLint("SetTextI18n")
        override fun run() {
            val start = mTime
            val pauseTime = mTimeBuffer

            val millis = SystemClock.uptimeMillis() - start
            var sec = (millis / 1000 - pauseTime).toInt()

            var min = sec / 60
            sec %= 60

            val hour = min / 60
            min %= 60

            text = "$HINT $hour:" + String.format("%02d", min) + ":" + String.format("%02d", sec)

            mHandler.postDelayed(this, 1000)
        }
    }

    constructor(ctx: Context) : super(ctx) {
        init()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        id = R.id.tp_timer

        textSize = sp(resources.getDimension(R.dimen.topPanel_text_size)).toFloat()
        text = START_HINT
    }

    override fun gameStart() {
        mTime = SystemClock.uptimeMillis()
        mTimeBuffer = 0L

        mHandler.postDelayed(timeUpdaterRunnable, 100)
    }

    override fun gameOver(isWin: Boolean) {
        mHandler.removeCallbacks(timeUpdaterRunnable)
        mHandler.removeCallbacks(timeSaverRunnable)

        if (isWin) {
            val resultTime = mTime - mTimeBuffer * 1000
        }
    }

    override fun startNewGame(countOfBomb: Int) {
        text = START_HINT
        isPauseMode = false

        mHandler.removeCallbacks(timeUpdaterRunnable)
        mHandler.removeCallbacks(timeSaverRunnable)
    }

    override fun makePause() {
        if (isPauseMode) {
            mHandler.removeCallbacks(timeSaverRunnable)
            mHandler.postDelayed(timeUpdaterRunnable, 1000)
        } else {
            mHandler.removeCallbacks(timeUpdaterRunnable)
            mHandler.postDelayed(timeSaverRunnable, 1000)
        }
        isPauseMode = !isPauseMode
    }

    override fun scoreChanged(newScore: Int) { }
    override fun clickField() { }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.stopWatchView(theme: Int = 0) = stopWatchView({}, theme)
inline fun ViewManager.stopWatchView(init: StopWatchView.() -> Unit, theme: Int = 0) =
        ankoView(::StopWatchView, theme, init)

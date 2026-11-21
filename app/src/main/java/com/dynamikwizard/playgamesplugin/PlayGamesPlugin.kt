
package com.dynamikwizard.playgamesplugin

import android.app.Activity
import android.util.Log
import android.view.View
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

class PlayGamesPlugin(godot: Godot) : GodotPlugin(godot) {

    private var currentActivity: Activity? = null

    private var auth: PlayGamesAuth? = null
    private var achievements: PlayGamesAchievements? = null

    private val tag = BuildConfig.LOG_NAME

    override fun getPluginName(): String =
        BuildConfig.PLUGIN_NAME

    private val signals = PlayGamesSignals { name, args ->
        emitSignal(name, *args)
    }

    override fun getPluginSignals(): MutableSet<SignalInfo> =
        PlayGamesSignals.ALL

    override fun onMainCreate(activity: Activity?): View? {
        currentActivity = activity

        if (activity != null) {

            auth = PlayGamesAuth(
                activity = activity,
                tag = tag,

                onAuthResult = { authenticated ->

                    activity.runOnUiThread {
                        signals.authenticationCompleted(
                            authenticated
                        )
                    }
                },

                onServerAuthCodeResult = { authCode, success ->

                    activity.runOnUiThread {
                        signals.serverAuthCodeReceived(
                            authCode,
                            success
                        )
                    }
                }
            )

            achievements = PlayGamesAchievements(
                activity = activity,
                tag = tag,
                onAchievementResult = { id, success, unlocked ->

                    activity.runOnUiThread {
                        signals.achievementCompleted(
                            id,
                            success,
                            unlocked
                        )
                    }
                },
                onAchievementsLoaded = { json, success ->

                    activity.runOnUiThread {
                        signals.achievementsLoaded(
                            json,
                            success
                        )
                    }
                }
            )

            Log.d(
                tag,
                "onMainCreate -> Activity ready (Play Games Plugin)"
            )
        }

        return super.onMainCreate(activity)
    }

    @UsedByGodot
    fun checkAuthentication() {
        auth?.checkAuthentication()
    }

    @UsedByGodot
    fun signIn() {
        auth?.signIn()
    }

    @UsedByGodot
    fun showAchievements() {
        achievements?.showAchievements()
    }

    @UsedByGodot
    fun unlockAchievement(id: String) {
        achievements?.unlockAchievement(id)
    }

    @UsedByGodot
    fun setAchievementSteps(
        id: String,
        steps: Int
    ) {
        achievements?.setAchievementSteps(
            id,
            steps
        )
    }

    @UsedByGodot
    fun loadAchievements(
        forceReload: Boolean
    ) {
        achievements?.loadAchievements(
            forceReload
        )
    }

    @UsedByGodot
    fun requestServerAuthCode(
        serverClientId: String,
        forceRefreshToken: Boolean
    ) {
        auth?.requestServerAuthCode(
            serverClientId,
            forceRefreshToken
        )
    }
}

package com.dynamikwizard.playgamesplugin

import android.app.Activity
import android.util.Log
import com.google.android.gms.games.PlayGames

class PlayGamesAchievements(
    private val activity: Activity,
    private val tag: String,
    private val onAchievementResult: (
        String,
        Boolean,
        Boolean
    ) -> Unit,
    private val onAchievementsLoaded: (
        String,
        Boolean
    ) -> Unit
) {
    private val achievementsClient =
        PlayGames.getAchievementsClient(activity)

    fun showAchievements() {
        achievementsClient.achievementsIntent
            .addOnSuccessListener { intent ->
                activity.runOnUiThread {
                    activity.startActivityForResult(
                        intent,
                        ACHIEVEMENTS_REQUEST_CODE
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    tag,
                    "Failed to show achievements",
                    exception
                )
            }
    }

    fun unlockAchievement(id: String) {
        achievementsClient.unlockImmediate(id)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.e(
                        tag,
                        "Failed to unlock achievement: $id",
                        task.exception
                    )
                }

                onAchievementResult(
                    id,
                    task.isSuccessful,
                    task.isSuccessful
                )
            }
    }

    fun setAchievementSteps(
        id: String,
        steps: Int
    ) {
        if (steps <= 0) {
            onAchievementResult(
                id,
                false,
                false
            )

            return
        }

        achievementsClient.setStepsImmediate(id, steps)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.e(
                        tag,
                        "Failed to set achievement steps: $id",
                        task.exception
                    )
                }

                onAchievementResult(
                    id,
                    task.isSuccessful,
                    task.isSuccessful &&
                            task.result == true
                )
            }
    }

    fun loadAchievements(forceReload: Boolean = false) {
        achievementsClient.load(forceReload)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.e(
                        tag,
                        "Failed to load achievements",
                        task.exception
                    )

                    onAchievementsLoaded(
                        "[]",
                        false
                    )

                    return@addOnCompleteListener
                }

                val buffer = task.result.get()

                try {
                    val json = org.json.JSONArray()

                    if (buffer != null) {
                        for (achievement in buffer) {
                            val item = org.json.JSONObject()

                            val isIncremental =
                                achievement.type ==
                                        com.google.android.gms.games.achievement.Achievement.TYPE_INCREMENTAL

                            item.put(
                                "id",
                                achievement.achievementId
                            )

                            item.put(
                                "name",
                                achievement.name
                            )

                            item.put(
                                "description",
                                achievement.description
                            )

                            item.put(
                                "type",
                                achievement.type
                            )

                            item.put(
                                "state",
                                achievement.state
                            )

                            item.put(
                                "currentSteps",
                                if (isIncremental)
                                    achievement.currentSteps
                                else
                                    0
                            )

                            item.put(
                                "totalSteps",
                                if (isIncremental)
                                    achievement.totalSteps
                                else
                                    0
                            )

                            item.put(
                                "xp",
                                achievement.xpValue
                            )

                            item.put(
                                "lastUpdatedTimestamp",
                                achievement.lastUpdatedTimestamp
                            )

                            json.put(item)
                        }
                    }

                    onAchievementsLoaded(
                        json.toString(),
                        true
                    )
                } finally {
                    buffer?.release()
                }
            }
    }

    companion object {
        private const val ACHIEVEMENTS_REQUEST_CODE = 9003
    }
}
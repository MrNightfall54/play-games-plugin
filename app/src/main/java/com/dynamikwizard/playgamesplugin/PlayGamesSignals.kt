package com.dynamikwizard.playgamesplugin

import org.godotengine.godot.plugin.SignalInfo

class PlayGamesSignals(
    private val emit: (String, Array<out Any>) -> Unit
) {
    companion object {
        private const val AUTHENTICATION_COMPLETED =
            "authentication_completed"

        private const val ACHIEVEMENT_COMPLETED =
            "achievement_completed"

        private const val ACHIEVEMENTS_LOADED =
            "achievements_loaded"

        private const val SERVER_AUTH_CODE_RECEIVED =
            "server_auth_code_received"

        val ALL: MutableSet<SignalInfo>
            get() = mutableSetOf(
                SignalInfo(
                    AUTHENTICATION_COMPLETED,
                    Boolean::class.javaObjectType
                ),
                SignalInfo(
                    ACHIEVEMENT_COMPLETED,
                    String::class.java,
                    Boolean::class.javaObjectType,
                    Boolean::class.javaObjectType
                ),
                SignalInfo(
                    ACHIEVEMENTS_LOADED,
                    String::class.java,
                    Boolean::class.javaObjectType
                ),
                SignalInfo(
                    SERVER_AUTH_CODE_RECEIVED,
                    String::class.java,
                    Boolean::class.javaObjectType
                ),
            )
    }

    fun authenticationCompleted(authenticated: Boolean) {
        emit(
            AUTHENTICATION_COMPLETED,
            arrayOf(authenticated)
        )
    }

    fun achievementCompleted(
        id: String,
        success: Boolean,
        unlocked: Boolean
    ) {
        emit(
            ACHIEVEMENT_COMPLETED,
            arrayOf<Any>(
                id,
                success,
                unlocked
            )
        )
    }

    fun achievementsLoaded(
        json: String,
        success: Boolean
    ) {
        emit(
            ACHIEVEMENTS_LOADED,
            arrayOf<Any>(
                json,
                success
            )
        )
    }

    fun serverAuthCodeReceived(
        authCode: String,
        success: Boolean
    ) {
        emit(
            SERVER_AUTH_CODE_RECEIVED,
            arrayOf<Any>(
                authCode,
                success
            )
        )
    }
}
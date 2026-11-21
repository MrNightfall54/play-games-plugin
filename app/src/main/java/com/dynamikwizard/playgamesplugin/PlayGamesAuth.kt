package com.dynamikwizard.playgamesplugin

import android.app.Activity
import android.util.Log
import com.google.android.gms.games.PlayGames

class PlayGamesAuth(
    private val activity: Activity,
    private val tag: String,
    private val onAuthResult: (Boolean) -> Unit,
    private val onServerAuthCodeResult: (
        String,
        Boolean
    ) -> Unit
) {
    private val signInClient =
        PlayGames.getGamesSignInClient(activity)

    fun checkAuthentication() {
        signInClient.isAuthenticated()
            .addOnCompleteListener { task ->

                val authenticated =
                    task.isSuccessful &&
                            task.result.isAuthenticated

                if (!task.isSuccessful) {
                    Log.e(
                        tag,
                        "Authentication check failed",
                        task.exception
                    )
                }

                onAuthResult(authenticated)
            }
    }

    fun signIn() {
        signInClient.signIn()
            .addOnCompleteListener { task ->

                val authenticated =
                    task.isSuccessful &&
                            task.result.isAuthenticated

                if (!task.isSuccessful) {
                    Log.e(
                        tag,
                        "Sign in failed",
                        task.exception
                    )
                }

                onAuthResult(authenticated)
            }
    }

    fun requestServerAuthCode(
        serverClientId: String,
        forceRefreshToken: Boolean = false
    ) {
        if (serverClientId.isBlank()) {
            Log.e(
                tag,
                "Server client ID is empty"
            )

            onServerAuthCodeResult(
                "",
                false
            )

            return
        }

        signInClient
            .requestServerSideAccess(
                serverClientId,
                forceRefreshToken
            )
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.e(
                        tag,
                        "Failed to request server auth code",
                        task.exception
                    )

                    onServerAuthCodeResult(
                        "",
                        false
                    )

                    return@addOnCompleteListener
                }

                val authCode = task.result

                Log.d(
                    tag,
                    "Server auth code received"
                )

                onServerAuthCodeResult(
                    authCode,
                    true
                )
            }
    }
}
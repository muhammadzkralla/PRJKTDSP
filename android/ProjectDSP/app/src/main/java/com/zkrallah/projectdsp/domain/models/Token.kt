package com.zkrallah.projectdsp.domain.models

data class Token(
    val accessToken: String?,
    val accessTokenExpiresIn: String?,
    val refreshToken: String?,
    val refreshTokenExpiresIn: String?,
    val user: User?
)
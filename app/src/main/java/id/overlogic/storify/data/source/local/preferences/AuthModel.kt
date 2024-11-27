package id.overlogic.storify.data.source.local.preferences

data class AuthModel(
    val userId: String,
    val name: String,
    val token: String,
    val isLogin: Boolean = false
)
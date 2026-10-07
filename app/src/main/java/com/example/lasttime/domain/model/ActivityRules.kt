package com.example.lasttime.domain.model

object ActivityRules {
    const val MAX_NAME_LENGTH = 60

    /** Retorna a mensagem de erro ou null se o nome for válido. */
    fun validateName(name: String): String? = when {
        name.isBlank() -> "Informe o nome da atividade."
        name.trim().length > MAX_NAME_LENGTH -> "Use no máximo $MAX_NAME_LENGTH caracteres."
        else -> null
    }
}

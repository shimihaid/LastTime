package com.example.lasttime.domain.model

/** Erros cuja mensagem já é amigável e pode ser mostrada ao usuário. */
open class UserFacingException(message: String, cause: Throwable? = null) : Exception(message, cause)

class InvalidActivityException(message: String) : UserFacingException(message)

class ActivityNotFoundException : UserFacingException("Atividade não encontrada.")

class ExternalDataException(message: String, cause: Throwable? = null) : UserFacingException(message, cause)

fun Throwable.toUserMessage(default: String): String =
    if (this is UserFacingException) message ?: default else default

package com.example.lasttime.data.repository

import kotlin.coroutines.cancellation.CancellationException

/** Executa o bloco e devolve Result, sem engolir o cancelamento de corrotinas. */
suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

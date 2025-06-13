package com.solara.core

import com.solara.core.utils.Result


suspend fun <S, F> Result<S, F>.onSuccess(action: suspend (S) -> Unit): Result<S, F> {
    if (this is Result.Success) action(data)
    return this
}

suspend fun <S, F> Result<S, F>.onError(action: suspend (F) -> Unit): Result<S, F> {
    if (this is Result.Error) action(error)
    return this
}
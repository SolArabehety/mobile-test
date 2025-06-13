package com.solara.core.utils

/**
 * Represents a result of the execution of a use case in the domain layer of the
 * application.
 *
 * S is the domain class associated to a successful result.
 * F is the error (in the domain) associated to an un-successful result.
 */
sealed class Result<out S, out F> {
    /**
     * Represents a successful result.
     *
     * @param data is the data obtained in the result.
     */
    data class Success<S>(val data: S) : Result<S, Nothing>()

    /**
     * Represents a result with an error.
     *
     * @param error is the class/object that encapsulates domain specific errors.
     */
    data class Error<F>(val error: F) : Result<Nothing, F>()
}

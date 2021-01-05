/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.errors.handler

import dev.icerock.moko.errors.ErrorEventListener
import dev.icerock.moko.errors.HandlerResult
import dev.icerock.moko.errors.annotation.CallRequired
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import kotlin.reflect.KClass

abstract class ExceptionHandlerContext<R> {

    @CallRequired
    abstract suspend fun execute(): HandlerResult<R, Throwable>

    abstract fun <E : Throwable> catch(
        clazz: KClass<E>,
        catcher: (E) -> Boolean
    ): ExceptionHandlerContext<R>

    abstract fun finally(block: () -> Unit): ExceptionHandlerContext<R>

    inline fun <reified E : Throwable> catch(
        noinline catcher: (E) -> Boolean
    ): ExceptionHandlerContext<R> {
        return catch(E::class, catcher)
    }

    companion object {
        operator fun <T : Any, R> invoke(
            exceptionMapper: ExceptionMapper<T>,
            eventsDispatcher: EventsDispatcher<ErrorEventListener<T>>,
            onCatch: ((Throwable) -> Unit)?,
            block: suspend () -> R
        ): ExceptionHandlerContext<R> {
            return ExceptionHandlerContextImpl(
                exceptionMapper = exceptionMapper,
                eventsDispatcher = eventsDispatcher,
                onCatch = onCatch,
                block = block
            )
        }
    }
}

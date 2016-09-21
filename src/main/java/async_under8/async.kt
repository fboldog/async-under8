package async_under8

import com.rahulrav.futures.Future

fun <T> async (continuationWrapper: ContinuationWrapper? = null, coroutine c: FutureController<T>.() -> Continuation<Unit>): Future<T> {
    val controller = FutureController<T>(continuationWrapper)
    c(controller).resume(Unit)
    return controller.future
}

typealias ContinuationWrapper = (() -> Unit) -> Unit

@AllowSuspendExtensions
class FutureController<T>(
        private val continuationWrapper: ContinuationWrapper?
) {
    val future = Future<T>(Future.defaultExecutor())

    suspend fun <V> await(f: Future<V>, machine: Continuation<V>) {
        f.always { value, throwable ->
            wrapContinuationIfNeeded {
                if (throwable == null)
                    machine.resume(value!!)
                else
                    machine.resumeWithException(throwable)
            }
        }
    }

    private fun wrapContinuationIfNeeded(block: () -> Unit) {
        continuationWrapper?.invoke(block) ?: block()
    }

    operator fun handleResult(value: T, c: Continuation<Nothing>) = future.resolve(value)

    operator fun handleException(t: Throwable, c: Continuation<Nothing>) = future.reject (
        when (t) {
            is Exception -> t
            else -> Exception(t)
        }
    )
}

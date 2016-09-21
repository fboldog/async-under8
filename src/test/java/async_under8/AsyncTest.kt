package async_under8

import com.rahulrav.futures.Future
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AsyncTest {

    @Test
    fun testSimple() {
        val future = async<String> {
           await(Future.submit { "O" }) + "K"
        }

        future.onSuccess {
            assertEquals("OK", it)
        }

    }

    @Test
    fun testWaitForCompletion() {
        val toAwait = Future<String>(Future.defaultExecutor())
        val future = async<String> {
            await(toAwait) + "K"
        }

        assertFalse(future.ready)
        toAwait.resolve("O")

        future.onSuccess {
            assertEquals("OK", it)
        }
    }

    @Test
    fun testAwaitedFutureCompletedExceptionally() {
        val toAwait = Future<String>(Future.defaultExecutor())
        val future = async<String> {
            try {
                await(toAwait)
            } catch (e: RuntimeException) {
                e.message!!
            } + "K"
        }

        assertFalse(future.ready)
        toAwait.reject(RuntimeException("O"))

        future.onSuccess {
            assertEquals("OK", it)
        }
    }


    @Test
    fun testExceptionInsideCoroutine() {
        val future = async<String> {
            if (await(Future.submit { true })) {
                throw IllegalStateException("OK")
            }
            await(Future.submit { "fail" })
        }

        future.onError { error ->
            assertTrue(error is IllegalStateException)
            assertEquals("OK", error.message)
        }
    }

    @Test
    fun testContinuationWrapped() {
        val depth = AtomicInteger()

        val future = async<String>(continuationWrapper = {
            depth.andIncrement
            it()
            depth.andDecrement
        }) {
            assertEquals(0, depth.get(), "Part before first suspension should not be wrapped")

            val result =
                    await(Future.submit {
                        while (depth.get() > 0);

                        assertEquals(0, depth.get(), "Part inside suspension point should not be wrapped")
                        "OK"
                    })

            assertEquals(1, depth.get(), "Part after first suspension should be wrapped")

            await(Future.submit {
                while (depth.get() > 0);

                assertEquals(0, depth.get(), "Part inside suspension point should not be wrapped")
                "ignored"
            })

            result
        }

        future.onSuccess {
            assertEquals("OK", it)
        }
    }
}
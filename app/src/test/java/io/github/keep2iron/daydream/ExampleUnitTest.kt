package io.github.keep2iron.daydream

import android.util.Log
import io.reactivex.Observable
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        val itemCount = 200
        val groupCount = 5
        val intArray = Array(itemCount) { it }
        Observable.fromArray(*intArray)
            .buffer(itemCount / groupCount)
            .subscribe {
                println("${it.toString()}")
            }
    }
}

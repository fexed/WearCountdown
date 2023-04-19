package com.fexed.wearcountdown.presentation

import org.junit.Test;

internal class CountdownTextTests {

    @Test
    fun longCountdown() {
        var res = countdown(100000, CountdownType.LONG)
        assert(res == "1d 3h 46m 40s") { res }
        res = countdown(10000, CountdownType.LONG)
        assert(res == "2h 46m 40s") { res }
        res = countdown(1000, CountdownType.LONG)
        assert(res == "16m 40s") { res }
        res = countdown(100, CountdownType.LONG)
        assert(res == "1m 40s") { res }
        res = countdown(10, CountdownType.LONG)
        assert(res == "10s") { res }
    }
    @Test
    fun longNoSecsCountdown() {
        var res = countdown(100000, CountdownType.LONG_NO_SECONDS)
        assert(res == "1d 3h 46m") { res }
        res = countdown(10000, CountdownType.LONG_NO_SECONDS)
        assert(res == "2h 46m") { res }
        res = countdown(1000, CountdownType.LONG_NO_SECONDS)
        assert(res == "16m") { res }
        res = countdown(100, CountdownType.LONG_NO_SECONDS)
        assert(res == "1m") { res }
        res = countdown(10, CountdownType.LONG_NO_SECONDS)
        assert(res == "10s") { res }
    }
    @Test
    fun shortCountdown() {
        var res = countdown(100000, CountdownType.SHORT)
        assert(res == "1d 3h") { res }
        res = countdown(10000, CountdownType.SHORT)
        assert(res == "2h 46m") { res }
        res = countdown(1000, CountdownType.SHORT)
        assert(res == "16m") { res }
        res = countdown(100, CountdownType.SHORT)
        assert(res == "1m") { res }
        res = countdown(10, CountdownType.SHORT)
        assert(res == "0m") { res }
    }
}
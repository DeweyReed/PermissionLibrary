package com.finalweek10.permission.data

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.finalweek10.permission.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class GetResourcesTest {
    @Test
    fun resourcesShouldBeRetrieved() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        val id = appContext.resources.getIdentifier("permlab_accessCoarseLocation",
                "string", appContext.packageName)
        assertEquals(id, R.string.permlab_accessCoarseLocation)

        val s = appContext.getString(id)

        assertEquals(s, appContext.getString(R.string.permlab_accessCoarseLocation))
    }
}
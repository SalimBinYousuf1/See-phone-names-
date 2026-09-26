package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SalimDatabase
import com.example.data.local.entities.SavedNumberEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: SalimDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SalimDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun readStringFromContext_matchesAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Salim Number Identity", appName)
    }

    @Test
    fun roomDatabase_insertAndQuerySavedNumber() = runBlocking {
        val dao = database.salimDao()
        val entry = SavedNumberEntity(
            normalizedNumber = "+18002752273",
            displayNumber = "+1 (800) 275-2273",
            label = "Post Office Helpdesk",
            notes = "Open 9 to 5",
            category = "Postal Services",
            isFavorite = true
        )

        dao.insertOrUpdateSavedNumber(entry)
        val queried = dao.findSavedNumber("+18002752273")
        assertNotNull(queried)
        assertEquals("Post Office Helpdesk", queried?.label)
        assertTrue(queried?.isFavorite == true)

        val list = dao.getAllSavedNumbers().first()
        assertTrue(list.any { it.normalizedNumber == "+18002752273" })

        // Clean up
        dao.deleteSavedNumberByNumber("+18002752273")
        val afterDelete = dao.findSavedNumber("+18002752273")
        assertEquals(null, afterDelete)
    }
}

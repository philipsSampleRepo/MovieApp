package com.movies.msearch.data.network

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ApiResponseTest {
    @Test
    fun `Success - holds correct data and equals works`() {
        val success1 = ApiResponse.Success("data")
        val success2 = ApiResponse.Success("data")
        val successDifferent = ApiResponse.Success(123)

        // Verify data is stored correctly
        assertEquals("data", success1.data)

        // Equality check (data classes)
        assertEquals(success1, success2)
        assertNotEquals(success1, successDifferent)
    }

    @Test
    fun `Error - holds message and equals works`() {
        val error1 = ApiResponse.Error("Network error")
        val error2 = ApiResponse.Error("Network error")
        val errorDifferent = ApiResponse.Error("Timeout")

        // Verify message is stored correctly
        assertEquals("Network error", error1.message)

        // Equality check
        assertEquals(error1, error2)
        assertNotEquals(error1, errorDifferent)
    }

    @Test
    fun `Loading - is a singleton`() {
        val loading1 = ApiResponse.Loading
        val loading2 = ApiResponse.Loading

        // Singleton check (referential equality)
        assertSame(loading1, loading2)
    }

    @Test
    fun `ApiResponse subtypes are distinct`() {
        val success = ApiResponse.Success("test")
        val error = ApiResponse.Error("error")
        val loading = ApiResponse.Loading

        assertTrue(success is ApiResponse.Success)
        assertTrue(error is ApiResponse.Error)
        assertTrue(loading is ApiResponse.Loading)
    }

    @Test
    fun `Success toString includes data`() {
        val success = ApiResponse.Success(42)
        assertTrue(success.toString().contains("42"))
    }

    @Test
    fun `Error toString includes message`() {
        val error = ApiResponse.Error("Failed")
        assertTrue(error.toString().contains("Failed"))
    }

    @Test
    fun `Loading toString is consistent`() {
        val response: ApiResponse<Nothing> = ApiResponse.Loading
        assertTrue(response is ApiResponse.Loading)
    }

    @Test
    fun `Success with null data`() {
        val success = ApiResponse.Success(null)
        assertEquals(null, success.data)
    }

    @Test
    fun `Error with empty message`() {
        val error = ApiResponse.Error("")
        assertEquals("", error.message)
    }
}
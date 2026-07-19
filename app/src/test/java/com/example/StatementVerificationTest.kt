package com.example

import com.example.data.remote.StatementVerificationResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class StatementVerificationTest {

    @Test
    fun `test statement verification result moshi parsing`() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(StatementVerificationResult::class.java)

        val json = """
            {
              "statement": "We successfully delivered zero-balance Jan Dhan bank accounts to over 50 crore citizens.",
              "candidateName": "Narendra Modi",
              "partyName": "BJP",
              "verdict": "TRUE",
              "factCheckSource": "PIB Fact Check",
              "sourceUrl": "https://pib.gov.in",
              "explanation": "According to official Ministry of Finance figures, Jan Dhan bank accounts have indeed crossed the 50 crore milestone.",
              "confidenceScore": 0.98,
              "groundingPoints": [
                "Over 50.4 crore accounts have been opened under PMJDY as of August 2023.",
                "Total deposits in these accounts exceed 2 lakh crore."
              ]
            }
        """.trimIndent()

        val parsed = adapter.fromJson(json)
        assertNotNull(parsed)
        assertEquals("We successfully delivered zero-balance Jan Dhan bank accounts to over 50 crore citizens.", parsed?.statement)
        assertEquals("Narendra Modi", parsed?.candidateName)
        assertEquals("BJP", parsed?.partyName)
        assertEquals("TRUE", parsed?.verdict)
        assertEquals("PIB Fact Check", parsed?.factCheckSource)
        assertEquals("https://pib.gov.in", parsed?.sourceUrl)
        assertEquals(0.98, parsed?.confidenceScore ?: 0.0, 0.001)
        assertEquals(2, parsed?.groundingPoints?.size)
    }
}

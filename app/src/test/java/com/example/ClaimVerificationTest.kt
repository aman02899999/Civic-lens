package com.example

import com.example.data.remote.ClaimEvidencePoint
import com.example.data.remote.ElectionClaimVerificationResult
import com.example.data.remote.RealTimeNewsSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ClaimVerificationTest {

    @Test
    fun testElectionClaimVerificationResultMoshiParsing() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(ElectionClaimVerificationResult::class.java)

        val json = """
            {
              "claim": "Voter ID cards with QR codes are mandatory for voting in 2024 elections",
              "verdict": "FALSE",
              "verdictSummary": "ECI has confirmed standard Voter ID cards, passports, and 12 alternative photo IDs are valid; QR card is not mandatory.",
              "confidenceScore": 0.96,
              "claimOriginOrContext": "Viral WhatsApp forwarded audio and social media messages claiming old voter cards will be rejected.",
              "evidencePoints": [
                {
                  "finding": "ECI accepts EPIC along with 12 other photo IDs",
                  "sourceName": "Election Commission of India (ECI)",
                  "sourceUrl": "https://eci.gov.in",
                  "supportsClaim": false
                },
                {
                  "finding": "PIB Fact Check flagged viral rumors as false misinformation",
                  "sourceName": "PIB Fact Check",
                  "sourceUrl": "https://pib.gov.in/factcheck",
                  "supportsClaim": false
                }
              ],
              "realTimeNewsSources": [
                {
                  "title": "ECI clarifies voter ID guidelines for upcoming general elections",
                  "url": "https://eci.gov.in/voter-guidelines",
                  "publisher": "Election Commission of India",
                  "publishedDate": "2024-04-10",
                  "snippet": "Voters can use any of the 12 approved photo identification documents to cast their ballot."
                }
              ],
              "officialAgencyReference": "ECI Official Press Release & Handbook",
              "verificationTimestamp": 1713000000000
            }
        """.trimIndent()

        val parsed = adapter.fromJson(json)
        assertNotNull(parsed)
        assertEquals("Voter ID cards with QR codes are mandatory for voting in 2024 elections", parsed?.claim)
        assertEquals("FALSE", parsed?.verdict)
        assertEquals(0.96, parsed?.confidenceScore ?: 0.0, 0.001)
        assertEquals(2, parsed?.evidencePoints?.size)
        assertEquals(1, parsed?.realTimeNewsSources?.size)
        assertEquals("Election Commission of India (ECI)", parsed?.evidencePoints?.first()?.sourceName)
        assertEquals(false, parsed?.evidencePoints?.first()?.supportsClaim)
        assertEquals("ECI clarifies voter ID guidelines for upcoming general elections", parsed?.realTimeNewsSources?.first()?.title)
    }

    @Test
    fun testElectionClaimFallbackVerification() {
        val result = ElectionClaimVerificationResult(
            claim = "VVPAT slip matching is 100% mandatory for all EVMs",
            verdict = "MISLEADING",
            verdictSummary = "VVPAT paper slips are verified for 5 randomly selected polling stations per assembly constituency as mandated by the Supreme Court.",
            confidenceScore = 0.94,
            claimOriginOrContext = "Debates surrounding mandatory physical counting of all EVM paper slips.",
            evidencePoints = listOf(
                ClaimEvidencePoint(
                    finding = "Supreme Court order mandates counting of 5 EVMs per assembly segment",
                    sourceName = "Supreme Court of India / ECI",
                    sourceUrl = "https://eci.gov.in",
                    supportsClaim = false
                )
            ),
            realTimeNewsSources = listOf(
                RealTimeNewsSource(
                    title = "Supreme Court judgment on EVM-VVPAT physical verification",
                    url = "https://main.sci.gov.in",
                    publisher = "Supreme Court of India",
                    snippet = "SC reaffirms 5 polling stations random verification protocol."
                )
            ),
            officialAgencyReference = "Election Commission of India & Supreme Court of India",
            verificationTimestamp = System.currentTimeMillis()
        )

        assertNotNull(result)
        assertEquals("MISLEADING", result.verdict)
        assertTrue(result.evidencePoints.isNotEmpty())
        assertTrue(result.realTimeNewsSources.isNotEmpty())
    }
}

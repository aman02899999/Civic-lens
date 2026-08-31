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
              "claimText": "Voter ID cards with QR codes are mandatory for voting in 2024 elections",
              "verdict": "DEBUNKED FALSE",
              "verdictSummary": "ECI has confirmed standard Voter ID cards, passports, and 12 alternative photo IDs are valid; QR card is not mandatory.",
              "confidenceScore": 0.96,
              "truthScorePercent": 5,
              "claimContext": "Viral WhatsApp forwarded messages claiming old voter cards will be rejected.",
              "factCheckBreakdown": "The Election Commission of India has issued official notifications clarifying standard voter cards remain valid.",
              "keyEvidencePoints": [
                {
                  "pointTitle": "ECI Approved IDs",
                  "evidenceDetail": "ECI accepts EPIC along with 12 other photo IDs",
                  "sourceName": "Election Commission of India (ECI)"
                },
                {
                  "pointTitle": "PIB Fact Check",
                  "evidenceDetail": "PIB Fact Check flagged viral rumors as false misinformation",
                  "sourceName": "PIB Fact Check"
                }
              ],
              "realTimeNewsSources": [
                {
                  "title": "ECI clarifies voter ID guidelines for upcoming general elections",
                  "url": "https://eci.gov.in/voter-guidelines",
                  "publisher": "Election Commission of India",
                  "publishedDate": "2024-04-10",
                  "relevanceSnippet": "Voters can use any of the 12 approved photo identification documents to cast their ballot."
                }
              ],
              "officialPortalsChecked": [
                "eci.gov.in",
                "pib.gov.in"
              ],
              "recommendedClarification": "Carry any approved photo ID to the polling station.",
              "timestamp": "2024-04-10T10:00:00Z"
            }
        """.trimIndent()

        val parsed = adapter.fromJson(json)
        assertNotNull(parsed)
        assertEquals("Voter ID cards with QR codes are mandatory for voting in 2024 elections", parsed?.claimText)
        assertEquals("DEBUNKED FALSE", parsed?.verdict)
        assertEquals(0.96, parsed?.confidenceScore ?: 0.0, 0.001)
        assertEquals(5, parsed?.truthScorePercent)
        assertEquals(2, parsed?.keyEvidencePoints?.size)
        assertEquals(1, parsed?.realTimeNewsSources?.size)
        assertEquals("Election Commission of India (ECI)", parsed?.keyEvidencePoints?.first()?.sourceName)
        assertEquals("ECI clarifies voter ID guidelines for upcoming general elections", parsed?.realTimeNewsSources?.first()?.title)
    }

    @Test
    fun testElectionClaimFallbackVerification() {
        val result = ElectionClaimVerificationResult(
            claimText = "VVPAT slip matching is 100% mandatory for all EVMs",
            verdict = "MISLEADING",
            verdictSummary = "VVPAT paper slips are verified for 5 randomly selected polling stations per assembly constituency as mandated by the Supreme Court.",
            confidenceScore = 0.94,
            truthScorePercent = 35,
            claimContext = "Debates surrounding mandatory physical counting of all EVM paper slips.",
            factCheckBreakdown = "Supreme Court precedents specify 5 EVMs per assembly segment for VVPAT slip matching verification.",
            keyEvidencePoints = listOf(
                ClaimEvidencePoint(
                    pointTitle = "Supreme Court Protocol",
                    evidenceDetail = "Supreme Court order mandates counting of 5 EVMs per assembly segment",
                    sourceName = "Supreme Court of India / ECI"
                )
            ),
            realTimeNewsSources = listOf(
                RealTimeNewsSource(
                    title = "Supreme Court judgment on EVM-VVPAT physical verification",
                    url = "https://main.sci.gov.in",
                    publisher = "Supreme Court of India",
                    relevanceSnippet = "SC reaffirms 5 polling stations random verification protocol."
                )
            ),
            officialPortalsChecked = listOf("eci.gov.in", "sci.gov.in"),
            recommendedClarification = "VVPAT counting applies to random sampling per constituency.",
            timestamp = "2024-04-10T12:00:00Z"
        )

        assertNotNull(result)
        assertEquals("MISLEADING", result.verdict)
        assertTrue(result.keyEvidencePoints.isNotEmpty())
        assertTrue(result.realTimeNewsSources.isNotEmpty())
    }
}


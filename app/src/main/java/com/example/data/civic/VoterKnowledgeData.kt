package com.example.data.civic

/**
 * Curated, static reference content for the Voter Toolkit panel.
 * Educational reference only — always verify procedures against the official
 * Election Commission of India portals (eci.gov.in, voters.eci.gov.in).
 */

data class VoterTopic(
    val title: String,
    val summary: String,
    val keyPoints: List<String>,
    val category: String
)

data class CivicHelpline(
    val name: String,
    val number: String,
    val description: String,
    val category: String,
    val webUrl: String = ""
)

object VoterKnowledgeBase {

    val voterTopicCategories = listOf(
        "All", "Registration", "Voting Day", "EVM & Process", "Integrity & Complaints"
    )

    val voterTopics: List<VoterTopic> = listOf(
        VoterTopic(
            "How to Register as a Voter",
            "Any Indian citizen who is 18 years or older on the qualifying date can register as a voter in their constituency of ordinary residence — registration is free and can be completed entirely online.",
            listOf(
                "Apply online at voters.eci.gov.in (Voters' Service Portal) or through the official 'Voter Helpline' mobile app using Form 6.",
                "Four qualifying dates apply each year (1 January, 1 April, 1 July, 1 October) — you can apply in advance of the one on which you turn 18.",
                "Documents needed: proof of age (birth certificate, class 10 marksheet, passport, etc.) and proof of ordinary residence (Aadhaar, utility bill, bank passbook, rent agreement, etc.).",
                "A Booth Level Officer (BLO) may visit to verify your details; track your application status on the same portal with your reference number.",
                "Registration in more than one constituency is an offence — if you move, use Form 8 to shift your entry rather than registering afresh.",
                "There is no fee at any step; no official will ever ask for payment to register you."
            ),
            "Registration"
        ),
        VoterTopic(
            "Check and Correct Your Electoral Roll Entry",
            "Being registered once is not enough — entries can be shifted or deleted during roll revisions, so verify your entry before every election.",
            listOf(
                "Search your entry at electoralsearch.eci.gov.in by EPIC number, by details, or by mobile number.",
                "Use Form 8 (on the Voters' Service Portal) for corrections of name/photo/age, change of address, marking as a person with disability, or replacing a lost card.",
                "Download your digital e-EPIC (PDF voter card) from the portal or Voter Helpline app once your EPIC is linked to a unique mobile number.",
                "Objections to wrongful deletions can be raised with the Electoral Registration Officer (ERO) of your assembly constituency.",
                "Rolls are frozen shortly before an election — check and fix your entry as soon as elections are announced, not on polling day."
            ),
            "Registration"
        ),
        VoterTopic(
            "What Happens on Voting Day",
            "Polling is designed to take under a few minutes per voter once you reach the front of the queue — knowing the steps makes it smooth.",
            listOf(
                "Find your polling station and serial number via the Voter Helpline app, electoralsearch.eci.gov.in, or your voter information slip.",
                "Carry your EPIC (voter ID) or one of the ECI-notified alternative photo IDs (passport, driving licence, PAN, Aadhaar, MGNREGA job card, bank passbook with photo, pension document, service ID, etc.). The voter slip alone is NOT an identity document.",
                "Inside the booth: the first polling officer checks your name on the roll, the second inks your left forefinger and takes your signature/thumb impression, the third issues the ballot and directs you to the EVM.",
                "Press the blue button beside your chosen candidate; the machine beeps and the VVPAT window displays a printed slip of your choice for about 7 seconds.",
                "Voting is secret — no one, including polling staff, may see or ask whom you voted for.",
                "Queues typically prioritize senior citizens, persons with disabilities, and pregnant women; polling continues for everyone already in the queue at closing time."
            ),
            "Voting Day"
        ),
        VoterTopic(
            "EVM and VVPAT, Explained",
            "Electronic Voting Machines (EVMs) are standalone, battery-powered devices with no network connectivity, used in Indian elections since 2000 and paired with paper-trail VVPAT units since 2019.",
            listOf(
                "An EVM has two units — the Ballot Unit in the voting compartment and the Control Unit with the presiding officer; a vote registers only after the officer enables the ballot for each voter.",
                "EVMs are not connected to Wi-Fi, Bluetooth, or the internet — they are single-purpose machines with one-time programmable chips.",
                "VVPAT (Voter Verifiable Paper Audit Trail) prints a slip showing the candidate's name and symbol, visible to you for ~7 seconds before dropping into a sealed box.",
                "Before polls, machines are randomized twice, mock polls are conducted in front of party agents, and machines are sealed with agents' signatures.",
                "VVPAT slips from randomly selected polling stations in every assembly segment are counted and matched with the EVM tally after polls close.",
                "The Supreme Court has repeatedly examined and upheld the integrity of the EVM-VVPAT system, most recently declining to order full slip-matching or a ballot-paper return in 2024."
            ),
            "EVM & Process"
        ),
        VoterTopic(
            "NOTA — None of the Above",
            "Since 2013, every EVM ballot ends with a NOTA option letting you formally reject all candidates while still participating in the election.",
            listOf(
                "NOTA was introduced after the Supreme Court's judgment in PUCL v. Union of India (2013), protecting the secrecy of a 'rejection' vote.",
                "Pressing NOTA registers a counted, published vote — it is not an invalid vote.",
                "Under current law, NOTA has no electoral consequence beyond signalling: even if NOTA tops the count, the highest-polling candidate still wins.",
                "NOTA totals are published per constituency in official ECI results, making the protest visible in the record."
            ),
            "EVM & Process"
        ),
        VoterTopic(
            "Model Code of Conduct (MCC)",
            "The MCC is a set of behavioural norms for parties and candidates that comes into force the moment the ECI announces an election schedule and lasts until results are declared.",
            listOf(
                "Governments cannot announce new schemes, projects, or financial grants that could influence voters once the MCC is in force.",
                "Campaigning is barred during the 'silence period' — the 48 hours ending with the close of polling.",
                "Using places of worship, caste or communal appeals, bribing or intimidating voters, and serving liquor during elections all violate the MCC (and often the law).",
                "The MCC itself is a moral code, but many of its provisions overlap with enforceable law under the Representation of the People Act, 1951 and the BNS.",
                "Violations can be reported by any citizen through the cVIGIL app with photo/video evidence — the ECI targets a response within 100 minutes."
            ),
            "Integrity & Complaints"
        ),
        VoterTopic(
            "Reporting Election Violations",
            "The ECI runs dedicated citizen-facing channels for reporting bribery, intimidation, hate speech, MCC violations, and polling irregularities.",
            listOf(
                "cVIGIL app: capture live photo/video of a violation; the complaint is GPS-tagged and routed to field squads with a 100-minute response target. Anonymous reporting is supported.",
                "Voter Helpline 1950: national contact centre for roll, EPIC, and polling queries (prefix your STD code where asked).",
                "Bribery of voters (cash, liquor, gifts) is an offence under Section 171B/171E of the old IPC framework, now covered by BNS Chapter IX-A equivalents — both giver and taker can be prosecuted.",
                "Booth capturing, impersonation, and tampering are criminal offences under the Representation of the People Act, 1951.",
                "Complaints about your own wrongful roll deletion or EPIC issues go to the ERO/DEO; the National Grievance Services portal tracks them."
            ),
            "Integrity & Complaints"
        ),
        VoterTopic(
            "First-Time Voter Essentials",
            "A quick checklist distilled for citizens voting for the first time.",
            listOf(
                "Register as soon as you turn 18 — don't wait for an election to be announced.",
                "Save your EPIC number somewhere safe and download the e-EPIC PDF as a backup.",
                "Verify your roll entry ~2 weeks after any registration or correction, and again when elections are announced.",
                "On polling day, check the accepted-ID list before leaving home; the inked finger is your proof of having voted.",
                "Your vote is secret and yours alone — accepting money or goods for it is a crime for both sides, and no one can compel you to reveal your choice.",
                "Research candidates on the ECI's affidavit portal (affidavit.eci.gov.in) and this app's Compare Hub before deciding."
            ),
            "Voting Day"
        )
    )

    val helplineCategories = listOf(
        "All", "Emergency", "Women & Children", "Cyber & Consumer", "Civic & Legal"
    )

    val helplines: List<CivicHelpline> = listOf(
        CivicHelpline("National Emergency Number", "112", "Single emergency number for police, fire, and medical response across India (ERSS).", "Emergency"),
        CivicHelpline("Police", "100", "Direct police assistance (also reachable via 112).", "Emergency"),
        CivicHelpline("Fire Services", "101", "Fire and rescue emergency response.", "Emergency"),
        CivicHelpline("Ambulance", "108", "Emergency medical ambulance service in most states.", "Emergency"),
        CivicHelpline("Disaster Management", "1078", "National disaster helpline for floods, earthquakes, and other emergencies.", "Emergency"),
        CivicHelpline("Women's Helpline", "181", "24x7 support for women facing violence, harassment, or distress.", "Women & Children"),
        CivicHelpline("Childline (Children in Distress)", "1098", "24x7 emergency helpline for children in need of care and protection.", "Women & Children"),
        CivicHelpline("Elder Line (Senior Citizens)", "14567", "National helpline for senior citizens: abuse, pensions, care support.", "Women & Children"),
        CivicHelpline("Cyber Crime Helpline", "1930", "Report online financial fraud immediately to freeze fraudulent transactions; file complaints at the national portal.", "Cyber & Consumer", "https://cybercrime.gov.in"),
        CivicHelpline("National Consumer Helpline", "1915", "Complaints about defective goods, deficient services, and unfair trade practices; e-Daakhil for formal cases.", "Cyber & Consumer", "https://consumerhelpline.gov.in"),
        CivicHelpline("Voter Helpline (ECI)", "1950", "Electoral roll, EPIC, and polling queries; add your STD code where required.", "Civic & Legal", "https://voters.eci.gov.in"),
        CivicHelpline("Free Legal Aid (NALSA)", "15100", "Free legal aid and advice from the Legal Services Authorities for eligible citizens.", "Civic & Legal", "https://nalsa.gov.in"),
        CivicHelpline("Public Grievances (CPGRAMS)", "", "File and track grievances against any central/state government department online.", "Civic & Legal", "https://pgportal.gov.in"),
        CivicHelpline("Railway Helpline", "139", "Enquiries, security, and medical assistance on Indian Railways.", "Civic & Legal")
    )
}

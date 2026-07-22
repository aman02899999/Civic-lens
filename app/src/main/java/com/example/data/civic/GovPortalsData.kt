package com.example.data.civic

/**
 * Curated directory of official Government of India portals for scheme discovery/
 * application and government job recruitment. Links point only to official
 * gov.in / nic.in / official-body domains — no third-party job aggregators.
 */

data class PortalLink(
    val name: String,
    val description: String,
    val url: String,
    val category: String
)

object GovPortalsDirectory {

    val schemePortalCategories = listOf(
        "All", "Discovery", "Farmers", "Health", "Housing", "Workers", "Education", "Business"
    )

    val schemePortals: List<PortalLink> = listOf(
        PortalLink(
            "myScheme",
            "The government's one-stop platform to discover 3,000+ central and state schemes matched to your profile (age, income, occupation, state) — with eligibility details and direct application links.",
            "https://www.myscheme.gov.in",
            "Discovery"
        ),
        PortalLink(
            "UMANG",
            "Unified portal/app for 2,000+ government services — EPF balance, pension, gas booking, DigiLocker, scheme applications — with a single login.",
            "https://web.umang.gov.in",
            "Discovery"
        ),
        PortalLink(
            "National Portal of India — Schemes",
            "The india.gov.in master index of government schemes across all ministries and states.",
            "https://www.india.gov.in/my-government/schemes",
            "Discovery"
        ),
        PortalLink(
            "PM-KISAN",
            "₹6,000/year income support for eligible farmer families — check beneficiary status, e-KYC, and registration.",
            "https://pmkisan.gov.in",
            "Farmers"
        ),
        PortalLink(
            "Ayushman Bharat PM-JAY",
            "₹5 lakh/year cashless health cover per eligible family — check eligibility and download your Ayushman card.",
            "https://beneficiary.nha.gov.in",
            "Health"
        ),
        PortalLink(
            "Jan Aushadhi",
            "Locate Jan Aushadhi Kendras selling quality generic medicines at 50-90% lower prices.",
            "https://janaushadhi.gov.in",
            "Health"
        ),
        PortalLink(
            "PM Awas Yojana (Urban)",
            "Housing-for-all subsidies and beneficiary tracking for urban households.",
            "https://pmay-urban.gov.in",
            "Housing"
        ),
        PortalLink(
            "PM Awas Yojana (Gramin)",
            "Rural housing assistance — beneficiary lists, progress tracking, and FTO status.",
            "https://pmayg.nic.in",
            "Housing"
        ),
        PortalLink(
            "e-Shram",
            "National registry for unorganized workers — register free for an e-Shram card and linked social-security benefits.",
            "https://eshram.gov.in",
            "Workers"
        ),
        PortalLink(
            "National Scholarship Portal",
            "Central and state scholarships for students — single application window, status tracking, and institute verification.",
            "https://scholarships.gov.in",
            "Education"
        ),
        PortalLink(
            "Skill India Digital",
            "Free and subsidized skill courses, apprenticeships, and certification under Skill India programs.",
            "https://www.skillindiadigital.gov.in",
            "Education"
        ),
        PortalLink(
            "Startup India",
            "Recognition, tax benefits, and funding support for registered startups; MSME schemes and compliance help.",
            "https://www.startupindia.gov.in",
            "Business"
        ),
        PortalLink(
            "PM SVANidhi",
            "Collateral-free working-capital loans for street vendors with interest subsidy and digital-payment cashback.",
            "https://pmsvanidhi.mohua.gov.in",
            "Business"
        )
    )

    val jobPortalCategories = listOf(
        "All", "All Sectors", "Central Services", "Banking & Finance", "Railways", "Defence", "Teaching"
    )

    val jobPortals: List<PortalLink> = listOf(
        PortalLink(
            "National Career Service (NCS)",
            "The government's official job marketplace — search vacancies across sectors, register as a jobseeker free of cost, and access career counselling. Beware of anyone charging fees for NCS registration.",
            "https://www.ncs.gov.in",
            "All Sectors"
        ),
        PortalLink(
            "Employment News",
            "The government's weekly journal of central/state vacancies, results, and admit-card notices — the authoritative record of official openings.",
            "https://www.employmentnews.gov.in",
            "All Sectors"
        ),
        PortalLink(
            "UPSC",
            "Civil Services (IAS/IPS/IFS), Engineering Services, CDS, NDA, and other Group A recruitments — notifications, syllabus, and online applications.",
            "https://upsc.gov.in",
            "Central Services"
        ),
        PortalLink(
            "Staff Selection Commission (SSC)",
            "CGL, CHSL, MTS, GD Constable, and other Group B/C central government posts — one-time registration and exam calendar.",
            "https://ssc.gov.in",
            "Central Services"
        ),
        PortalLink(
            "IBPS",
            "Common recruitment for public sector banks and RRBs — PO, Clerk, Specialist Officer exams.",
            "https://www.ibps.in",
            "Banking & Finance"
        ),
        PortalLink(
            "RBI Careers",
            "Reserve Bank of India recruitment — Grade B officers, assistants, and specialist roles.",
            "https://opportunities.rbi.org.in",
            "Banking & Finance"
        ),
        PortalLink(
            "Railway Recruitment (RRB)",
            "Unified online application portal for Railway Recruitment Board vacancies — NTPC, Group D, ALP, technician posts.",
            "https://www.rrbapply.gov.in",
            "Railways"
        ),
        PortalLink(
            "Join Indian Army",
            "Officer entries (NDA, CDS, TES, TGC), Agniveer recruitment rallies, and JCO/OR vacancies.",
            "https://joinindianarmy.nic.in",
            "Defence"
        ),
        PortalLink(
            "Join Indian Navy",
            "Officer and sailor entries including Agniveer (SSR/MR) — eligibility, notifications, and applications.",
            "https://www.joinindiannavy.gov.in",
            "Defence"
        ),
        PortalLink(
            "Indian Air Force (Agnipath)",
            "Agniveer Vayu intake — eligibility, exam schedule, and online registration.",
            "https://agnipathvayu.cdac.in",
            "Defence"
        ),
        PortalLink(
            "CTET",
            "Central Teacher Eligibility Test — the qualifying exam for teaching posts in central government schools.",
            "https://ctet.nic.in",
            "Teaching"
        ),
        PortalLink(
            "KVS Recruitment",
            "Teaching and non-teaching vacancies in Kendriya Vidyalayas across India.",
            "https://kvsangathan.nic.in",
            "Teaching"
        )
    )
}

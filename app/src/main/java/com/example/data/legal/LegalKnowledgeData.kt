package com.example.data.legal

/**
 * Curated, static reference content for the "Know Your Rights" legal panel.
 * Educational reference only — not a substitute for advice from a licensed advocate.
 * Section numbers should always be cross-verified against the official Bare Act / e-Gazette text.
 */

data class ConstitutionArticle(
    val articleNumber: String,
    val title: String,
    val description: String,
    val category: String
)

data class LawSection(
    val oldIpcSection: String,
    val newBnsSection: String,
    val title: String,
    val description: String,
    val punishment: String,
    val category: String
)

data class RightsTopic(
    val title: String,
    val summary: String,
    val keyPoints: List<String>,
    val category: String
)

object LegalKnowledgeBase {

    const val constitutionCategoryAll = "All"
    val constitutionCategories = listOf("All", "Preamble", "Fundamental Rights", "Fundamental Duties", "DPSP")

    val constitutionArticles: List<ConstitutionArticle> = listOf(
        ConstitutionArticle(
            "Preamble",
            "We, the People of India",
            "Declares India a Sovereign, Socialist, Secular, Democratic Republic securing to all citizens Justice (social, economic, political), Liberty (of thought, expression, belief, faith, worship), Equality (of status and opportunity), and promoting Fraternity assuring dignity of the individual and unity and integrity of the Nation.",
            "Preamble"
        ),
        ConstitutionArticle(
            "Article 14",
            "Right to Equality",
            "The State shall not deny to any person equality before the law or the equal protection of the laws within the territory of India.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 15",
            "Prohibition of Discrimination",
            "Prohibits discrimination by the State against any citizen on grounds only of religion, race, caste, sex, or place of birth. Permits special provisions for women, children, and socially/educationally backward classes.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 16",
            "Equality of Opportunity in Public Employment",
            "Guarantees equal opportunity for all citizens in matters of employment or appointment to any office under the State, with provisions allowing reservation for backward classes.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 17",
            "Abolition of Untouchability",
            "Untouchability is abolished and its practice in any form is forbidden. Enforcement of any disability arising out of untouchability is an offence punishable by law.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 19",
            "Protection of Six Freedoms",
            "Guarantees freedom of speech and expression, assembly (peacefully, without arms), association/unions, movement throughout India, residence and settlement, and profession/trade/business — subject to reasonable restrictions.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 20",
            "Protection in Respect of Conviction for Offences",
            "No conviction except for violation of a law in force at the time of the act (no ex-post-facto laws); no double jeopardy (prosecuted/punished for the same offence more than once); no person accused of an offence can be compelled to be a witness against themselves (protection against self-incrimination).",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 21",
            "Protection of Life and Personal Liberty",
            "No person shall be deprived of their life or personal liberty except according to procedure established by law. Judicially expanded to include the right to privacy, dignity, clean environment, livelihood, health, speedy trial, and legal aid.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 21A",
            "Right to Education",
            "The State shall provide free and compulsory education to all children aged 6 to 14 years, as implemented through the Right of Children to Free and Compulsory Education (RTE) Act, 2009.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 22",
            "Protection Against Arrest and Detention",
            "Every arrested person must be informed of the grounds of arrest, has the right to consult and be defended by a legal practitioner of choice, and must be produced before the nearest magistrate within 24 hours of arrest (excluding travel time).",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 23",
            "Prohibition of Traffic in Human Beings and Forced Labour",
            "Traffic in human beings, begar (forced labour without payment), and other similar forms of forced labour are prohibited and punishable by law.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 24",
            "Prohibition of Child Labour",
            "No child below the age of 14 years shall be employed to work in any factory, mine, or engaged in any other hazardous employment.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 25",
            "Freedom of Religion",
            "All persons are equally entitled to freedom of conscience and the right to freely profess, practice, and propagate religion, subject to public order, morality, health, and other Fundamental Rights.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 29",
            "Protection of Interests of Minorities",
            "Any section of citizens with a distinct language, script, or culture has the right to conserve it. No citizen shall be denied admission into any State-maintained or aided educational institution on grounds only of religion, race, caste, or language.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 32",
            "Right to Constitutional Remedies",
            "Guarantees the right to move the Supreme Court directly for enforcement of Fundamental Rights through writs — Habeas Corpus, Mandamus, Prohibition, Certiorari, and Quo Warranto. Dr. B. R. Ambedkar called this the \"heart and soul\" of the Constitution.",
            "Fundamental Rights"
        ),
        ConstitutionArticle(
            "Article 39A",
            "Equal Justice and Free Legal Aid",
            "Directs the State to ensure the operation of the legal system promotes justice on a basis of equal opportunity and to provide free legal aid to ensure no citizen is denied justice due to economic or other disability.",
            "DPSP"
        ),
        ConstitutionArticle(
            "Article 41",
            "Right to Work, Education, and Public Assistance",
            "The State shall make effective provision for securing the right to work, education, and public assistance in cases of unemployment, old age, sickness, and disablement.",
            "DPSP"
        ),
        ConstitutionArticle(
            "Article 44",
            "Uniform Civil Code",
            "The State shall endeavour to secure a Uniform Civil Code for citizens throughout the territory of India.",
            "DPSP"
        ),
        ConstitutionArticle(
            "Article 48A",
            "Protection of Environment",
            "The State shall endeavour to protect and improve the environment and to safeguard forests and wildlife.",
            "DPSP"
        ),
        ConstitutionArticle(
            "Article 50",
            "Separation of Judiciary from Executive",
            "The State shall take steps to separate the judiciary from the executive in the public services of the State.",
            "DPSP"
        ),
        ConstitutionArticle(
            "Article 51A(a-k)",
            "Fundamental Duties of Every Citizen",
            "Added by the 42nd Amendment (1976): abide by the Constitution and respect its ideals, the National Flag and National Anthem; cherish the noble ideals of the freedom struggle; uphold sovereignty, unity and integrity of India; defend the country; promote harmony and brotherhood; value and preserve composite culture; protect the environment; develop scientific temper and humanism; safeguard public property and abjure violence; strive towards excellence; and (added by the 86th Amendment, 2002) provide opportunities for education to one's child/ward between ages 6-14.",
            "Fundamental Duties"
        )
    )

    val criminalLawCategories = listOf(
        "All", "Offences Against the Body", "Sexual & Gender Offences", "Property Offences",
        "Public Order & State", "Marriage & Family", "Cyber & Documents"
    )

    /**
     * The Bharatiya Nyaya Sanhita (BNS) 2023 replaced the Indian Penal Code (IPC) 1860,
     * effective 1 July 2024. Old IPC section numbers are retained here for reference since
     * most citizens still recognize them; the corresponding BNS section is the one currently in force.
     */
    val criminalLawSections: List<LawSection> = listOf(
        LawSection("300 / 302", "101 / 103", "Murder", "Culpable homicide amounting to murder — causing death with the intention of causing death or such bodily injury as is likely to cause death.", "Death penalty or life imprisonment, plus fine", "Offences Against the Body"),
        LawSection("304", "105", "Culpable Homicide Not Amounting to Murder", "Causing death without the specific intent or knowledge required for murder.", "Life imprisonment or up to 10 years, plus fine", "Offences Against the Body"),
        LawSection("304A", "106", "Death by Negligence", "Causing death by a rash or negligent act not amounting to culpable homicide (e.g., negligent driving).", "Up to 5 years, plus fine (up to 10 years for hit-and-run cases with fleeing)", "Offences Against the Body"),
        LawSection("307", "109", "Attempt to Murder", "Doing an act with the intention or knowledge that, if it caused death, would amount to murder.", "Up to 10 years to life imprisonment, plus fine", "Offences Against the Body"),
        LawSection("323 / 324", "115", "Voluntarily Causing Hurt", "Intentionally causing bodily pain, disease, or infirmity to another person.", "Up to 1-3 years or fine, depending on severity and weapon used", "Offences Against the Body"),
        LawSection("354", "74", "Assault on a Woman to Outrage Her Modesty", "Assault or criminal force against a woman intending to, or knowing it is likely to, outrage her modesty.", "1 to 5 years, plus fine", "Sexual & Gender Offences"),
        LawSection("354A", "75", "Sexual Harassment", "Unwelcome physical contact, demand for sexual favours, showing pornography, or sexually coloured remarks.", "Up to 3 years, plus fine", "Sexual & Gender Offences"),
        LawSection("354D", "78", "Stalking", "Following or contacting a woman despite her disinterest, or monitoring her use of the internet/email/electronic communication.", "Up to 3 years (first offence), up to 5 years for repeat offence", "Sexual & Gender Offences"),
        LawSection("375 / 376", "63 / 64", "Rape", "Non-consensual sexual intercourse as defined under the section, including specific aggravated circumstances.", "Rigorous imprisonment of 10 years to life, plus fine", "Sexual & Gender Offences"),
        LawSection("376D", "70", "Gang Rape", "Rape committed by one or more persons acting in furtherance of common intention.", "20 years to life imprisonment, plus fine to the victim", "Sexual & Gender Offences"),
        LawSection("498A", "85 / 86", "Cruelty by Husband or Relatives", "Subjecting a married woman to cruelty — wilful conduct likely to drive her to suicide/grave injury, or harassment linked to dowry demands.", "Up to 3 years, plus fine", "Marriage & Family"),
        LawSection("304B", "80", "Dowry Death", "Death of a woman within 7 years of marriage under suspicious circumstances connected to dowry harassment (read with the Dowry Prohibition Act, 1961).", "Minimum 7 years, up to life imprisonment", "Marriage & Family"),
        LawSection("363", "137", "Kidnapping", "Taking or enticing a minor (under 16 for boys, under 18 for girls) or person of unsound mind out of lawful guardianship.", "Up to 7 years, plus fine", "Offences Against the Body"),
        LawSection("379", "303", "Theft", "Dishonestly taking movable property out of another person's possession without consent.", "Up to 3 years, or fine, or both", "Property Offences"),
        LawSection("392", "309", "Robbery", "Theft or extortion committed with force, or the threat of instant death, hurt, or wrongful restraint.", "Up to 10 years rigorous imprisonment, plus fine", "Property Offences"),
        LawSection("411", "317", "Dishonestly Receiving Stolen Property", "Receiving or retaining stolen property knowing or having reason to believe it is stolen.", "Up to 3 years, or fine, or both", "Property Offences"),
        LawSection("420", "318", "Cheating and Dishonestly Inducing Delivery of Property", "Deceiving a person to fraudulently or dishonestly induce delivery of property, or to consent to retention of property.", "Up to 7 years, plus fine", "Property Offences"),
        LawSection("447", "329", "Criminal Trespass", "Entering or remaining unlawfully on another's property with intent to commit an offence, intimidate, insult, or annoy.", "Up to 3 months, or fine, or both", "Property Offences"),
        LawSection("499 / 500", "356", "Defamation", "Making or publishing an imputation concerning a person intending to harm, or with reason to believe it will harm, their reputation.", "Up to 2 years, or fine, or both (simple imprisonment)", "Cyber & Documents"),
        LawSection("463 / 465", "336 / 338", "Forgery", "Making a false document or electronic record with intent to cause damage, support a claim, or commit fraud.", "Up to 2-7 years depending on the document type, plus fine", "Cyber & Documents"),
        LawSection("141 / 144 (unlawful assembly, CrPC prohibitory order)", "189 (BNS) / 163 (BNSS)", "Unlawful Assembly & Prohibitory Orders", "Five or more persons assembled with a common unlawful object; and the procedural power of a magistrate to prohibit assembly/movement to prevent public disturbance.", "Up to 6 months to 2 years depending on the object of assembly", "Public Order & State"),
        LawSection("124A", "152", "Acts Endangering Sovereignty, Unity, and Integrity of India", "The colonial-era offence of 'Sedition' (IPC 124A) was repealed. BNS Section 152 now criminalizes acts of secession, armed rebellion, subversive activities, or exciting separatist feelings that endanger India's sovereignty or unity.", "Life imprisonment or up to 7 years, plus fine", "Public Order & State"),
        LawSection("34", "3(5)", "Common Intention", "Acts done by several persons in furtherance of a common intention make each person liable as if the act was done by them alone.", "Same liability as the principal offence committed", "Public Order & State"),
        LawSection("120B", "61", "Criminal Conspiracy", "Agreement between two or more persons to commit an illegal act, or a legal act by illegal means.", "Punishment same as abetment of the offence conspired, or up to 6 months for minor conspiracies", "Public Order & State")
    )

    val rightsTopics: List<RightsTopic> = listOf(
        RightsTopic(
            "Your Rights If Arrested",
            "Every person in India — regardless of the alleged offence — retains specific constitutional and procedural protections at the moment of arrest under Article 22, the Bharatiya Nagarik Suraksha Sanhita (BNSS, replacing the CrPC), and Supreme Court guidelines (D. K. Basu v. State of West Bengal).",
            listOf(
                "Right to know the grounds of arrest immediately, in a language you understand.",
                "Right to inform a friend, relative, or person of your choice about the arrest and where you are being held.",
                "Right to consult and be defended by a lawyer of your choice; if you cannot afford one, free legal aid must be provided (Legal Services Authorities Act, 1987).",
                "Right to be produced before a magistrate within 24 hours of arrest, excluding travel time.",
                "Women can generally only be arrested by or in the presence of a woman police officer, and not after sunset or before sunrise except in exceptional circumstances with prior written permission.",
                "Right to a medical examination at the time of arrest and periodically during detention, to record any injuries.",
                "Handcuffing is permitted only in exceptional circumstances (e.g., habitual/violent offenders, flight risk), never as a routine practice.",
                "The arrest memo must be prepared, attested by a witness, and a copy given to the arrested person."
            ),
            "Criminal Justice"
        ),
        RightsTopic(
            "Right to Information (RTI)",
            "The Right to Information Act, 2005 empowers every citizen to seek information from any public authority to promote transparency and accountability in governance.",
            listOf(
                "Any citizen can file an RTI application to a Public Information Officer (PIO) with a nominal fee (typically ₹10).",
                "The PIO must respond within 30 days; within 48 hours if the information concerns the life or liberty of a person.",
                "BPL (Below Poverty Line) applicants are exempted from the application fee.",
                "If unsatisfied or if no response is received, a first appeal can be filed with the appellate authority, followed by a second appeal to the State/Central Information Commission.",
                "Certain information is exempt — e.g., matters affecting national security, sovereignty, or personal privacy with no public interest."
            ),
            "Governance & Transparency"
        ),
        RightsTopic(
            "Consumer Rights",
            "The Consumer Protection Act, 2019 guarantees six core rights to every consumer and establishes a three-tier redressal system.",
            listOf(
                "Right to Safety — protection against goods and services hazardous to life and property.",
                "Right to Information — to be informed about quality, quantity, purity, price, and standard of goods/services.",
                "Right to Choose — access to a variety of goods/services at competitive prices.",
                "Right to be Heard — assurance that consumer interests receive due consideration.",
                "Right to Redressal — seek compensation against unfair trade practices or exploitation.",
                "Right to Consumer Education.",
                "Complaints can be filed online via the e-Daakhil portal, or with the District, State, or National Consumer Disputes Redressal Commission depending on claim value."
            ),
            "Consumer & Commerce"
        ),
        RightsTopic(
            "Women's Safety & Legal Protections",
            "Indian law provides several specific statutory protections for women covering domestic life, the workplace, and public safety.",
            listOf(
                "Protection of Women from Domestic Violence Act, 2005 (PWDVA) — covers physical, emotional, sexual, and economic abuse; allows protection orders, residence orders, and monetary relief.",
                "Sexual Harassment of Women at Workplace (Prevention, Prohibition and Redressal) Act, 2013 (POSH) — mandates an Internal Complaints Committee (ICC) in every workplace with 10+ employees.",
                "Dowry Prohibition Act, 1961 — giving or taking dowry is a punishable offence.",
                "Zero FIR — any police station must register an FIR for a cognizable offence regardless of jurisdiction and transfer it to the appropriate station.",
                "Women's Helpline: 181 (National), Police: 100/112, and the National Commission for Women (NCW) online complaint portal.",
                "Maternity Benefit Act, 1961 (amended 2017) — 26 weeks of paid maternity leave for eligible women employees."
            ),
            "Women & Family"
        ),
        RightsTopic(
            "Worker & Employee Rights",
            "India's four consolidated Labour Codes (Code on Wages, Industrial Relations Code, Code on Social Security, and Occupational Safety, Health & Working Conditions Code) govern the rights of employees and workers.",
            listOf(
                "Right to a minimum wage, notified periodically by the Central/State government.",
                "Right to timely payment of wages under the Payment of Wages provisions.",
                "Right to Provident Fund (EPF) and Gratuity after 5 years of continuous service.",
                "Right to a safe working environment and limited working hours, with overtime compensation.",
                "Protection from unfair termination — Industrial Relations Code lays down retrenchment and dispute-resolution procedures.",
                "Right to form and join trade unions.",
                "POSH protections extend to the workplace for the prevention of sexual harassment."
            ),
            "Employment & Labour"
        ),
        RightsTopic(
            "Right to Privacy & Digital Rights",
            "The Supreme Court in K. S. Puttaswamy v. Union of India (2017) unanimously declared the right to privacy a Fundamental Right protected under Article 21.",
            listOf(
                "Digital Personal Data Protection Act, 2023 (DPDP Act) — regulates how organizations collect, process, and store personal data, and grants individuals the right to access, correct, and erase their data.",
                "Right to be informed before personal data is collected and to withdraw consent at any time.",
                "Cybercrime protections under the Information Technology Act, 2000, covering identity theft, hacking, and online harassment — complaints can be filed at cybercrime.gov.in or by dialing 1930.",
                "Right against unauthorized surveillance, subject to narrow, lawful exceptions for national security.",
                "Right to seek removal of non-consensual intimate imagery and online harassment content under IT Rules, 2021."
            ),
            "Digital & Privacy"
        ),
        RightsTopic(
            "Right to Free Legal Aid",
            "Article 39A directs the State to ensure justice is not denied due to economic or other disability, implemented through the Legal Services Authorities Act, 1987.",
            listOf(
                "Free legal aid, advice, and representation is available through the National/State/District Legal Services Authorities (NALSA/SLSA/DLSA) for eligible categories.",
                "Eligible groups include women, children, SC/ST members, industrial workmen, disaster/violence victims, persons with disabilities, and those with an annual income below the prescribed threshold.",
                "Legal aid clinics and Lok Adalats provide free, speedy dispute resolution without formal court fees.",
                "Every police station and court is required to display the contact details of the local Legal Services Authority.",
                "Call the NALSA toll-free helpline 15100 for free legal aid assistance."
            ),
            "Criminal Justice"
        ),
        RightsTopic(
            "Senior Citizens' Rights",
            "The Maintenance and Welfare of Parents and Senior Citizens Act, 2007 obligates children/relatives to provide maintenance and protects elders from abandonment.",
            listOf(
                "Right to claim maintenance from children or relatives through a Maintenance Tribunal, with simplified, time-bound procedures.",
                "Right to revoke a gift/transfer of property made to a relative if they subsequently fail to provide basic amenities and care.",
                "Protection against abandonment — a punishable offence under the Act.",
                "Priority medical treatment and dedicated queues at government facilities for senior citizens.",
                "Elder helpline: 14567 (Elderline)."
            ),
            "Family & Welfare"
        ),
        RightsTopic(
            "Right to Constitutional Remedies (Writs)",
            "Articles 32 and 226 empower the Supreme Court and High Courts respectively to issue writs enforcing Fundamental Rights and legal rights.",
            listOf(
                "Habeas Corpus — 'produce the body'; used to challenge unlawful detention.",
                "Mandamus — directs a public authority to perform a duty it has failed/refused to perform.",
                "Prohibition — issued by a higher court to stop a lower court/tribunal from exceeding its jurisdiction.",
                "Certiorari — quashes an order already passed by a lower court/tribunal/authority acting beyond its powers.",
                "Quo Warranto — questions the legal authority of a person holding a public office.",
                "Public Interest Litigation (PIL) allows any public-spirited citizen to approach the Court on behalf of those unable to do so themselves."
            ),
            "Criminal Justice"
        )
    )
}

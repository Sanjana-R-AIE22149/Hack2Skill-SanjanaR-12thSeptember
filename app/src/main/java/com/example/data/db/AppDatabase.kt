package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Event
import com.example.data.model.Registration
import com.example.data.model.UserFavorite
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Event::class, Registration::class, UserFavorite::class, UserProfile::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun registrationDao(): RegistrationDao
    abstract fun userFavoriteDao(): UserFavoriteDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "blr_events_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(
                            database.eventDao(),
                            database.registrationDao(),
                            database.userProfileDao(),
                            database.userFavoriteDao()
                        )
                    }
                }
            }
        }

        suspend fun populateInitialData(
            eventDao: EventDao,
            registrationDao: RegistrationDao,
            userProfileDao: UserProfileDao,
            userFavoriteDao: UserFavoriteDao
        ) {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            val initialEvents = listOf(
                Event(
                    id = 1,
                    title = "Namma Bengaluru Midnight Marathon 2026",
                    category = "Marathon",
                    subCategory = "Running",
                    neighborhood = "Cubbon Park",
                    venueName = "Sri Kanteerava Stadium / Cubbon Park",
                    venueAddress = "Kasturba Rd, Sampangi Rama Nagara, Bengaluru 560001",
                    dateStr = "Oct 24, 2026",
                    timeStr = "11:00 PM IST",
                    eventTimestamp = now + (dayMs * 42),
                    price = 899.0,
                    totalCapacity = 1200,
                    registeredCount = 842,
                    description = "Bengaluru's iconic night-time running festival! Experience the cool Bengaluru autumn breeze on lit avenues bordering Cubbon Park and Vidhana Soudha. Timed 21K Half Marathon and 10K Challenge with live chenda melam music, hydration booths, and post-run South Indian feast.",
                    perks = "Dry-Fit Technical Jersey, Metal Finisher Medal, RFID Chip Bib, Hydration Stations, Midnight Biryani & Energy Breakfast, Medical Support",
                    rules = "Minimum age 18 for 21K, 14 for 10K. Chest bib must remain visible throughout the course. No motorized assistance.",
                    tags = "marathon, running, 21k, midnight, cubbon park, kanteerava, endurance, fitness",
                    organizerName = "Bengaluru Runners Collective",
                    organizerContact = "contact@blrrunners.org | +91 80 2234 8899",
                    isOrganizerCreated = false,
                    bannerType = "marathon",
                    latitude = 12.9698,
                    longitude = 77.5926
                ),
                Event(
                    id = 2,
                    title = "Namma Buildathon: Agentic AI & Next-Gen Systems",
                    category = "Hackathon",
                    subCategory = "Coding",
                    neighborhood = "HSR Layout",
                    venueName = "HSR Tech Innovation Park",
                    venueAddress = "27th Main Rd, Sector 1, HSR Layout, Bengaluru 560102",
                    dateStr = "Oct 17, 2026",
                    timeStr = "09:00 AM IST",
                    eventTimestamp = now + (dayMs * 35),
                    price = 0.0,
                    totalCapacity = 300,
                    registeredCount = 215,
                    description = "A high-intensity 36-hour offline hackathon in the heart of Bengaluru's startup capital. Build autonomous AI agents, multi-agent workflows, and on-device machine intelligence. ₹7,50,000 cash prize pool with direct VC angel pitch slots for the top 5 finalists.",
                    perks = "₹7.5L Prize Pool, Cloud GPU Credits, Gourmet Meals & Red Bull Fridge, Hacker Swag Kit, Mentorship from Google & Top AI Founders",
                    rules = "Teams of 1 to 4 members. Code must be written during hackathon hours. Open-source libraries permitted. Hardware & API access provided.",
                    tags = "hackathon, coding, ai, agents, hsr layout, startups, developers, machine learning",
                    organizerName = "HSR Founders Circle",
                    organizerContact = "hack@hsrinnovate.in | +91 98450 12345",
                    isOrganizerCreated = true,
                    bannerType = "hackathon",
                    latitude = 12.9121,
                    longitude = 77.6446
                ),
                Event(
                    id = 3,
                    title = "Cubbon Park Sunrise 10K & 5K Green Run",
                    category = "Marathon",
                    subCategory = "Running",
                    neighborhood = "Cubbon Park",
                    venueName = "Queen Victoria Statue, Cubbon Park",
                    venueAddress = "Kasturba Rd, Behind High Court, Bengaluru 560001",
                    dateStr = "Oct 11, 2026",
                    timeStr = "05:45 AM IST",
                    eventTimestamp = now + (dayMs * 29),
                    price = 499.0,
                    totalCapacity = 600,
                    registeredCount = 410,
                    description = "Celebrate the lung space of Bengaluru with a peaceful, shaded morning race surrounded by century-old bamboo groves and rain trees. Certified traffic-free trail route inside Cubbon Park with eco-friendly seed medals.",
                    perks = "Clay/Plantable Seed Finisher Medal, Organic Cotton T-Shirt, Hot Filter Coffee & Idli Vada Breakfast, Professional Route Photography",
                    rules = "Zero-plastic race: carry personal reusable silicon cups or water flask. Pacer groups for 45m, 50m, 60m available.",
                    tags = "running, 10k, 5k, green run, cubbon park, morning, nature, filter coffee",
                    organizerName = "Green City Striders",
                    organizerContact = "greenrun@cubbonpark.blr | +91 80 4123 9090",
                    isOrganizerCreated = false,
                    bannerType = "marathon",
                    latitude = 12.9763,
                    longitude = 77.5929
                ),
                Event(
                    id = 4,
                    title = "Koramangala Tech Founders & Builders Mixer",
                    category = "Community Activity",
                    subCategory = "Design",
                    neighborhood = "Koramangala",
                    venueName = "Third Wave Roasters Flagship & Open Terrace",
                    venueAddress = "80 Feet Rd, 4th Block, Koramangala, Bengaluru 560034",
                    dateStr = "Oct 14, 2026",
                    timeStr = "06:30 PM IST",
                    eventTimestamp = now + (dayMs * 32),
                    price = 0.0,
                    totalCapacity = 120,
                    registeredCount = 98,
                    description = "An authentic Bengaluru rooftop evening gathering for software engineers, product designers, indie hackers, and founders. Pitch your project in 60 seconds or find your technical co-founder over freshly brewed artisan coffee.",
                    perks = "Artisanal Pour-over Coffee, Startup Resource Directory, Lightning Pitch Spotlight, Dedicated Co-Founder Matching Wall",
                    rules = "Open to all passionate builders. Respectful community ethos. No aggressive recruitment pitching without consent.",
                    tags = "community, design, founders, koramangala, meetup, networking, coffee, product",
                    organizerName = "BLR Tech Collective",
                    organizerContact = "hello@blrtechcollective.dev",
                    isOrganizerCreated = true,
                    bannerType = "community",
                    latitude = 12.9344,
                    longitude = 77.6239
                ),
                Event(
                    id = 5,
                    title = "Whitefield Web3 & Open Source Hackfest",
                    category = "Hackathon",
                    subCategory = "Open Source",
                    neighborhood = "Whitefield",
                    venueName = "KTPO Convention Centre",
                    venueAddress = "Export Promotion Industrial Park, Whitefield, Bengaluru 560066",
                    dateStr = "Nov 07, 2026",
                    timeStr = "08:30 AM IST",
                    eventTimestamp = now + (dayMs * 56),
                    price = 0.0,
                    totalCapacity = 450,
                    registeredCount = 310,
                    description = "A massive collaborative hackfest uniting 400+ developers building decentralized apps, privacy tooling, and public goods. Features specialized developer workshops and sponsor bounties totaling $25,000.",
                    perks = "$25,000 Bounty Tracks, Hardware Labs, Exclusive Developer Hoodies, High-Speed Gig-Fiber LAN, Overnight Gaming Lounge",
                    rules = "Bring your own laptop and peripherals. All projects must publish source code under permissible open source license.",
                    tags = "hackathon, coding, open source, whitefield, web3, public goods, developer",
                    organizerName = "Whitefield Developers Guild",
                    organizerContact = "organize@whitefieldhack.com | +91 80 6678 1200",
                    isOrganizerCreated = false,
                    bannerType = "hackathon",
                    latitude = 12.9793,
                    longitude = 77.7289
                ),
                Event(
                    id = 6,
                    title = "Indiranagar Heritage Cycling & Morning Trail",
                    category = "Community Activity",
                    subCategory = "Cycling",
                    neighborhood = "Indiranagar",
                    venueName = "Defence Colony Club Ground",
                    venueAddress = "100 Feet Rd, Defence Colony, Indiranagar, Bengaluru 560038",
                    dateStr = "Oct 18, 2026",
                    timeStr = "06:00 AM IST",
                    eventTimestamp = now + (dayMs * 36),
                    price = 299.0,
                    totalCapacity = 80,
                    registeredCount = 64,
                    description = "Ride through leafy colonial canopies, vintage heritage houses, and tree-lined streets of Old East Bengaluru. 22-kilometer casual social ride ending with traditional Davangere benne dosa and piping hot filter kaapi.",
                    perks = "Support Van & Mechanic En-Route, Cycle Safety Reflector, Breakfast at Landmark Eatery, Heritage Guide Commentary",
                    rules = "Helmet is strictly mandatory. Rental bicycles available with prior booking at venue.",
                    tags = "cycling, community, indiranagar, breakfast, heritage, outdoor, weekend",
                    organizerName = "Bengaluru Pedal Pioneers",
                    organizerContact = "pedal@indiranagarcyclists.in",
                    isOrganizerCreated = false,
                    bannerType = "community",
                    latitude = 12.9784,
                    longitude = 77.6408
                ),
                Event(
                    id = 7,
                    title = "Electronic City 15K Corporate Run & Walkathon",
                    category = "Marathon",
                    subCategory = "Running",
                    neighborhood = "Electronic City",
                    venueName = "Velankani Tech Park Main Arena",
                    venueAddress = "Hosur Rd, Phase 1, Electronic City, Bengaluru 560100",
                    dateStr = "Nov 15, 2026",
                    timeStr = "06:00 AM IST",
                    eventTimestamp = now + (dayMs * 64),
                    price = 699.0,
                    totalCapacity = 800,
                    registeredCount = 520,
                    description = "Run on the famous Electronic City elevated expressway service loop! Designed for tech professionals and running enthusiasts aiming for personal best timings on flat, fast tarmac.",
                    perks = "Finisher Medal, Commemorative Dry-Fit Polo, Timing Tag, Corporate Team Championship Trophy, Breakfast Buffet",
                    rules = "Participants must collect race bib one day prior at the expo. Bag-deposit available on-site.",
                    tags = "marathon, 15k, corporate, electronic city, run, walkathon, fitness",
                    organizerName = "ELCITA Sports Foundation",
                    organizerContact = "events@elcita.in | +91 80 2852 0400",
                    isOrganizerCreated = false,
                    bannerType = "marathon",
                    latitude = 12.8452,
                    longitude = 77.6602
                ),
                Event(
                    id = 8,
                    title = "Bangalore Board Game & Strategy Guild Meetup",
                    category = "Community Activity",
                    subCategory = "Tabletop",
                    neighborhood = "Malleshwaram",
                    venueName = "Chitra Kala Parishath Cultural Courtyard",
                    venueAddress = "Kumara Krupa Rd, Near Shivananda Circle, Bengaluru 560001",
                    dateStr = "Oct 25, 2026",
                    timeStr = "03:00 PM IST",
                    eventTimestamp = now + (dayMs * 43),
                    price = 150.0,
                    totalCapacity = 75,
                    registeredCount = 58,
                    description = "Spend a relaxed Sunday afternoon playing modern tabletop board games, strategy eurogames, and social deduction games. Beginner-friendly tables with game gurus ready to teach rules.",
                    perks = "Library of 150+ Board Games, Tea & Mysore Pak Snacks, Board Game Raffle Entry, Dedicated Game Masters",
                    rules = "Treat all game pieces with care. Friendly and inclusive environment for all ages above 12.",
                    tags = "board games, strategy, tabletop, malleshwaram, community, weekend, games",
                    organizerName = "Namma Tabletop Guild",
                    organizerContact = "games@nammatabletop.in",
                    isOrganizerCreated = true,
                    bannerType = "community",
                    latitude = 12.9892,
                    longitude = 77.5815
                )
            )
            eventDao.insertAll(initialEvents)

            // Seed user profile
            val defaultProfile = UserProfile(
                id = 1,
                name = "Sanjana R",
                email = "sanjana.r@gmail.com",
                phone = "+91 98452 33445",
                bio = "Bengaluru marathoner & software engineer. Passionate about AI agents and weekend Cubbon Park runs.",
                preferredNeighborhood = "Cubbon Park",
                interests = "Running, Coding, Design",
                selectedTheme = "INDIAN_TRADITIONAL"
            )
            userProfileDao.insertProfile(defaultProfile)

            // Seed initial favorites
            userFavoriteDao.insertFavorite(UserFavorite(eventId = 1))
            userFavoriteDao.insertFavorite(UserFavorite(eventId = 2))

            // Seed rich demographic registrations for attendee analytics
            val sampleRegistrations = listOf(
                Registration(
                    eventId = 2,
                    eventTitle = "Namma Buildathon: Agentic AI & Next-Gen Systems",
                    eventCategory = "Hackathon",
                    eventDate = "Oct 17, 2026",
                    eventTime = "09:00 AM IST",
                    eventVenue = "HSR Tech Innovation Park",
                    attendeeName = "Sanjana R",
                    attendeeEmail = "sanjana.r@gmail.com",
                    attendeePhone = "+91 98451 22334",
                    ticketType = "Solo Builder Pass",
                    ticketPrice = 0.0,
                    bookingRef = "BLR-HK-1092",
                    registeredAt = now - dayMs * 2,
                    isCheckedIn = true,
                    attendeeDetail = "Team: NeuralBengaluru",
                    ageGroup = "25-34",
                    primaryInterest = "AI & Machine Learning",
                    experienceLevel = "Advanced"
                ),
                Registration(
                    eventId = 2,
                    eventTitle = "Namma Buildathon: Agentic AI & Next-Gen Systems",
                    eventCategory = "Hackathon",
                    eventDate = "Oct 17, 2026",
                    eventTime = "09:00 AM IST",
                    eventVenue = "HSR Tech Innovation Park",
                    attendeeName = "Sanjana R",
                    attendeeEmail = "sanjana.r@gmail.com",
                    attendeePhone = "+91 97312 90812",
                    ticketType = "Hacker Team Pass (2-4)",
                    ticketPrice = 0.0,
                    bookingRef = "BLR-HK-1093",
                    registeredAt = now - dayMs * 3,
                    isCheckedIn = false,
                    attendeeDetail = "Team: Kaveri Agents",
                    ageGroup = "18-24",
                    primaryInterest = "UI/UX Design",
                    experienceLevel = "Intermediate"
                ),
                Registration(
                    eventId = 2,
                    eventTitle = "Namma Buildathon: Agentic AI & Next-Gen Systems",
                    eventCategory = "Hackathon",
                    eventDate = "Oct 17, 2026",
                    eventTime = "09:00 AM IST",
                    eventVenue = "HSR Tech Innovation Park",
                    attendeeName = "Sanjana R",
                    attendeeEmail = "sanjana.r@gmail.com",
                    attendeePhone = "+91 99451 88776",
                    ticketType = "Solo Builder Pass",
                    ticketPrice = 0.0,
                    bookingRef = "BLR-HK-1094",
                    registeredAt = now - dayMs * 1,
                    isCheckedIn = true,
                    attendeeDetail = "Team: AgentX",
                    ageGroup = "25-34",
                    primaryInterest = "Open Source",
                    experienceLevel = "Advanced"
                ),
                Registration(
                    eventId = 4,
                    eventTitle = "Koramangala Tech Founders & Builders Mixer",
                    eventCategory = "Community Activity",
                    eventDate = "Oct 14, 2026",
                    eventTime = "06:30 PM IST",
                    eventVenue = "Third Wave Roasters Flagship, Koramangala",
                    attendeeName = "Sanjana R",
                    attendeeEmail = "sanjana.r@gmail.com",
                    attendeePhone = "+91 99001 54321",
                    ticketType = "General Entry Pass",
                    ticketPrice = 0.0,
                    bookingRef = "BLR-CM-4401",
                    registeredAt = now - dayMs * 4,
                    isCheckedIn = true,
                    attendeeDetail = "Project: Local LLM Edge",
                    ageGroup = "35-44",
                    primaryInterest = "Networking",
                    experienceLevel = "Advanced"
                ),
                Registration(
                    eventId = 1,
                    eventTitle = "Namma Bengaluru Midnight Marathon 2026",
                    eventCategory = "Marathon",
                    eventDate = "Oct 24, 2026",
                    eventTime = "11:00 PM IST",
                    eventVenue = "Sri Kanteerava Stadium / Cubbon Park",
                    attendeeName = "Sanjana R",
                    attendeeEmail = "sanjana.r@gmail.com",
                    attendeePhone = "+91 91234 56789",
                    ticketType = "21K Half Marathon (Timed)",
                    ticketPrice = 899.0,
                    bookingRef = "BLR-MR-7731",
                    registeredAt = now - dayMs * 1,
                    isCheckedIn = false,
                    attendeeDetail = "Size M",
                    ageGroup = "25-34",
                    primaryInterest = "Athletics",
                    experienceLevel = "Advanced"
                ),
                Registration(
                    eventId = 1,
                    eventTitle = "Namma Bengaluru Midnight Marathon 2026",
                    eventCategory = "Marathon",
                    eventDate = "Oct 24, 2026",
                    eventTime = "11:00 PM IST",
                    eventVenue = "Sri Kanteerava Stadium / Cubbon Park",
                    attendeeName = "Sanjana R",
                    attendeeEmail = "sanjana.r@gmail.com",
                    attendeePhone = "+91 94480 11223",
                    ticketType = "10K Challenge (Timed)",
                    ticketPrice = 899.0,
                    bookingRef = "BLR-MR-7732",
                    registeredAt = now - dayMs * 5,
                    isCheckedIn = true,
                    attendeeDetail = "Size L",
                    ageGroup = "35-44",
                    primaryInterest = "Athletics",
                    experienceLevel = "Intermediate"
                )
            )
            registrationDao.insertAll(sampleRegistrations)
        }
    }
}

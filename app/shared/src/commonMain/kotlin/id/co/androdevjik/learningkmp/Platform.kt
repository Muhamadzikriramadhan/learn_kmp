package id.co.androdevjik.learningkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
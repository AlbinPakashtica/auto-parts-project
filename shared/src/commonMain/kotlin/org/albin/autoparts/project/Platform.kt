package org.albin.autoparts.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
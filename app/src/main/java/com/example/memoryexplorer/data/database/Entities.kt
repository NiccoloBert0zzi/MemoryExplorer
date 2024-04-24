package com.example.memoryexplorer.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favourite (
    @PrimaryKey
    val memoryId: String
)

class Memory {
    constructor(
        id: String?,
        creator: String?,
        title: String?,
        description: String?,
        date: String?,
        latitude: String?,
        longitude: String?,
        image: String?,
        isPublic: Boolean
    ) {
        this.title = title
        this.description = description
        this.date = date
        this.latitude = latitude
        this.longitude = longitude
        this.image = image
        this.creator = creator
        this.id = id
        this.isPublic = isPublic
    }

    constructor()

    var id: String? = null
        private set
    var creator: String? = null
        private set
    var title: String? = null
        private set
    var description: String? = null
        private set
    var date: String? = null
        private set
    var latitude: String? = null
        private set
    var longitude: String? = null
        private set
    var image: String? = null
        private set
    var isPublic = false

    override fun toString(): String {
        return "Memory{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", image='" + image + '\'' +
                ", creator='" + creator + '\'' +
                ", id='" + id + '\'' +
                '}'
    }
}

package com.movies.models

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship


interface IActor {
    val id: Long
    val name: String
    val movie: Set<Movie>
}

@NodeEntity
class Actor(): IActor {
    @Id
    @GeneratedValue
    override var id: Long = 0
    override lateinit var name: String

    @Relationship(type = "ACTED_IN", direction = "INCOMING")
    override var movie: Set<Movie> = HashSet<Movie>()

    companion object {

    }
}


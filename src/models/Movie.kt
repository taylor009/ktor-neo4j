package com.movies.models

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.cypher.ComparisonOperator
import org.neo4j.ogm.cypher.Filter
import org.neo4j.ogm.cypher.Filters
import com.movies.neo

interface IMovie {
    fun save()

    val id: Long?
    val title: String
}


@NodeEntity(label = "Movie")
class Movie():IMovie {

    @Id
    @GeneratedValue
    override var id: Long? = null
    override lateinit var title: String

    override fun save() {
        neo.sess.save(this)
    }

    companion object{

        fun getByTitle(title: String): Movie {
            val filters = Filters().and(Filter("title", ComparisonOperator.EQUALS, title))
            val r = neo.session().loadAll(Movie::class.java, filters)
            if (r.count() > 0) {
                return r.first()
            } else {
                throw NodeNotFoundInLocalDatabase("Title", title)
            }

        }

        fun getByOrCreateByTitle(title: String): Movie {
            val filters = Filters().and(Filter("title", ComparisonOperator.EQUALS, title))

            println("neo  ${neo}")
            println("neo.session()  ${neo.session()}")
            println("neo.session().loadAll(Movie::class.java)  ${neo.session().loadAll(Movie::class.java)}")


            val r = neo.session().loadAll(Movie::class.java)
            return if (r.count() > 0) {
                r.first()
            } else {
                val result = Movie()
                result.title = title
                return result
            }
        }

    }
}

fun main() {
//    val movie = Movie.getByOrCreateByTitle("asdfasdf")

    val movie = Movie()
    movie.title = "blah"
    movie.save()
    println(movie)
}



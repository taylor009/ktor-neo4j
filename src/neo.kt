package com.movies

import com.movies.models.NodeNotFoundInLocalDatabase
import kotlinx.coroutines.delay
import org.neo4j.driver.v1.exceptions.TransientException
import org.neo4j.ogm.config.ClasspathConfigurationSource
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.exception.CypherException
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.SessionFactory
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object neo {

    private var factory: SessionFactory
    val sess: Session
        get() {
            return factory.openSession()
        }

    init {
        val configurationSource = ClasspathConfigurationSource("ogm.properties")
        val configuration = Configuration.Builder(configurationSource).autoIndex("update").build()
        this.factory = SessionFactory(configuration, "models", "com.movies.models")
    }

    fun session(): Session {
        return factory.openSession()
    }

    inline fun <T> tx(body: () -> T){
        val tx = session().beginTransaction()
        body()
        tx.commit()
    }

    suspend fun <T> txRetry(
        times: Int = 5, initialDelay: Long = 100, maxDelay: Long = 5 * 60 * 1000, factor: Double = 2.0, body: suspend () -> T
    ): T {
        var currentDelay = initialDelay

        repeat(times - 1) {
            val tx = session().beginTransaction()

            try {
                val result = body()
                tx.commit()
                return result
            } catch (e: CypherException) {
                println("retrying times:$times ${e}")
            } catch (e: TransientException) {
                println("retrying times:$times ${e}")
            } catch (e: NodeNotFoundInLocalDatabase) {
                println("retrying times:$times ${e}")
            }
            tx.rollback()

            currentDelay +=  Random.nextLong(0, 500)
            println("currentDelay with random in block $currentDelay")

            delay(currentDelay)
            println("finished delay")
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        println("LAST ATTEMPT")
        return body() // last attempt
    }
}
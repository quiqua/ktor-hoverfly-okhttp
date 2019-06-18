package eu.quiqua.example.app

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein

fun Application.routes() {
    routing {
        get("/") {
            call.respondText("Hello World!", ContentType.Text.Plain)
        }

        get("/external") {
            val client: HttpClient by kodein().instance()
            val response = client.get<HttpResponse>("https://httpbin.org/status/200")
            call.respondText(response.status.description, ContentType.Text.Plain)
        }
    }
}

fun Application.dependency_injection() {
    kodein {
        bind<HttpClient>() with singleton {
            HttpClient(OkHttp)
        }
    }
}

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 8080) {
        routes()
    }
    server.start(wait = true)
}

package eu.quiqua.example.app

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import io.specto.hoverfly.junit.core.Hoverfly
import io.specto.hoverfly.junit.core.HoverflyMode
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

internal class ServerSpec: Spek({
    describe("A ktor server") {
        val server = TestApplicationEngine(createTestEnvironment())
        val hoverfly = Hoverfly(HoverflyMode.CAPTURE)

        context("Default OkHttp Client") {
            before {
                server.start(wait = false)
                server.application.routes()
                server.application.dependency_injection()
            }

            it("should return OK") {
                with(server) {
                    handleRequest(HttpMethod.Get, "/external").let {
                        assertThat(it.response.content, equalTo("OK"))
                    }
                }
            }
        }

        context("Hoverfly enabled OkHttp Client") {
            before {
                hoverfly.start()
                server.start(wait = false)
                server.application.routes()
                //server.application.dependency_injection()
                server.application.kodein {
                    bind<HttpClient>() with singleton {
                        HttpClient(OkHttp) {
                            engine {
                                config {
                                    sslSocketFactory(
                                        hoverfly.sslConfigurer.sslContext.socketFactory,
                                        hoverfly.sslConfigurer.trustManager
                                    )
                                }
                            }
                        }
                    }
                }
            }

            it("should return OK via Hoverfly") {
                with(server) {
                    handleRequest(HttpMethod.Get, "/external").let {
                        assertThat(it.response.content, equalTo("OK"))
                    }
                }
            }

            after {
                hoverfly.exportSimulation(Paths.get("vcr-simulation"))
            }
        }
    }
})

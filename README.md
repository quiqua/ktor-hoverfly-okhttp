# Example Kotlin Server App with Hoverfly

This repository is an example how to use Hoverfly for a Ktor Kotlin web app with
OkHttp and Kodein.

The code to record does currently not work due to some SSL issues within `hoverfly-java`.

To execute the (failing) tests, please run:

` ./gradlew clean test`

This will execute two tests, one without hoverfly and one with hoverfly enabled to the
same external API `https://httpbin.org/status/200`.

In order to run the tests in IntelliJ IDEA, please install the Plugin `Spek Framework 2.0.5`.
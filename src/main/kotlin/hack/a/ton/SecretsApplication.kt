package hack.a.ton

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootIntegrationWithAwsSecretsManagerApplication

fun main(args: Array<String>) {
    runApplication<SpringBootIntegrationWithAwsSecretsManagerApplication>(*args)
}

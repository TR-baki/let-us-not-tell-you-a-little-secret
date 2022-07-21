package hack.a.ton.secrets

import org.springframework.boot.CommandLineRunner
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class BootStrapper(private val environment: Environment) : CommandLineRunner {
    override fun run(vararg args: String?) {
        println(environment.getProperty("testsecret"))
    }
}

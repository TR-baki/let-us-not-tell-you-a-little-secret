package hack.a.ton.secret

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SecretController(@Value("\${TESTSECRET}") private val secret: Secret) {

    @GetMapping("/secret")
    fun getSecret(): ResponseEntity<Secret> {
        return ResponseEntity.ok(secret)
    }
}

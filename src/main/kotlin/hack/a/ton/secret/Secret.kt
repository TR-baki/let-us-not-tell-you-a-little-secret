package hack.a.ton.secret

data class Secret(val secretValue: String) {
    override fun toString(): String {
        return "secretValue: [PROTECTED]"
    }
}

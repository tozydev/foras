package io.github.tozydev.foras.jackson

import com.fasterxml.jackson.databind.ObjectMapper

data class JacksonConfiguration(
    val objectMapper: ObjectMapper = ObjectMapper(),
)

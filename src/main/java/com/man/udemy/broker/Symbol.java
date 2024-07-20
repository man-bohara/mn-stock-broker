package com.man.udemy.broker;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Symbol(String value) {
}

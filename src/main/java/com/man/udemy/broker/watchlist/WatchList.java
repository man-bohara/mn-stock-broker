package com.man.udemy.broker.watchlist;

import com.man.udemy.broker.Symbol;
import io.micronaut.serde.annotation.Serdeable;

import java.util.ArrayList;
import java.util.List;

@Serdeable
public record WatchList(List<Symbol> symbols) {

    public WatchList() {this(new ArrayList<>()); }
}

package com.man.udemy.broker.watchlist;

import com.man.udemy.broker.data.InMemoryStoreAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.awt.*;
import java.util.UUID;

@Controller("/account/watchlist")
public record WatchListController(InMemoryStoreAccountStore store) {
    static final UUID ACCOUNT_ID = UUID.randomUUID();

    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get() {
        return store.getWatchList(ACCOUNT_ID);
    }

    @Put(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);
    }

    @Delete(consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public void delete() {
        store.deleteWatchList(ACCOUNT_ID);
    }

}

package com.man.udemy.broker.watchlist;

import com.man.udemy.broker.Symbol;
import com.man.udemy.broker.data.InMemoryStoreAccountStore;
import io.micronaut.http.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.json.tree.JsonNode;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.GET;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

    @Inject
    @Client("/account/watchlist")
    HttpClient client;

    @Inject
    InMemoryStoreAccountStore inMemoryStoreAccountStore;

    @Inject
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        inMemoryStoreAccountStore.deleteWatchList(TEST_ACCOUNT_ID);
    }

    @Test
    void returnsEmptyWatchListForTestAccount() {
        final WatchList result = client.toBlocking().retrieve(GET("/"), WatchList.class);
        assertNull(result.symbols());
        assertTrue(inMemoryStoreAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    }

    @Test
    void returnsWatchListForTestAccount() throws IOException {
        inMemoryStoreAccountStore.updateWatchList(TEST_ACCOUNT_ID, new WatchList(
                Stream.of("AAPL", "GOOGL", "MSFT")
                        .map(Symbol::new)
                        .toList()
        ));

        var response = client.toBlocking().exchange("/", JsonNode.class);

        WatchList watchList = objectMapper.readValueFromTree(response.getBody().get(), WatchList.class);
        String prettyJson = objectMapper.writeValueAsString(watchList);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("{\"symbols\":[{\"value\":\"AAPL\"},{\"value\":\"GOOGL\"},{\"value\":\"MSFT\"}]}", prettyJson);
    }

    @Test
    void canUpdateWatchListForTestAccount() {
        var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();

        var request = HttpRequest.PUT("/", new WatchList(symbols))
                .accept(MediaType.APPLICATION_JSON);
        final var added = client.toBlocking().exchange(request);

        assertEquals(symbols, inMemoryStoreAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols());
        assertEquals(HttpStatus.OK, added.getStatus());


    }

    @Test
    void canDeleteWatchListForTestAccount() {
        var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();

        var request = HttpRequest.PUT("/", new WatchList(symbols))
                .accept(MediaType.APPLICATION_JSON);
        final var added = client.toBlocking().exchange(request);

        var deleteRequest = HttpRequest.DELETE("/");

        final var delete = client.toBlocking().exchange(deleteRequest);

        assertEquals(HttpStatus.OK, added.getStatus());

        final WatchList response = client.toBlocking().retrieve(GET("/"), WatchList.class);

        assertNull(response.symbols());
        assertTrue(inMemoryStoreAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    }
}
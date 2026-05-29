package com.script.redis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RedisTest {

    private Redis redis;

    @BeforeEach
    void setUp() {
        redis = new Redis();
    }

    // ---- Key-Value (SET / GET) ----

    @Test
    void setAndGet() {
        redis.exec("SET key value1");
        assertEquals("value1", redis.exec("GET key"));
    }

    @Test
    void getNonExistentKey() {
        assertNull(redis.exec("GET nonexistent"));
    }

    @Test
    void overwriteValue() {
        redis.exec("SET key v1");
        redis.exec("SET key v2");
        assertEquals("v2", redis.exec("GET key"));
    }

    // ---- List (LPUSH / RPUSH / LRANGE) ----

    @Test
    void lpushAndLrange() {
        redis.exec("LPUSH list 3 2 1");
        List<String> result = (List<String>) redis.exec("LRANGE list 0 3");
        assertEquals(List.of("3", "2", "1"), result);
    }

    @Test
    void rpushAndLrange() {
        redis.exec("RPUSH list 1 2 3");
        List<String> result = (List<String>) redis.exec("LRANGE list 0 3");
        assertEquals(List.of("1", "2", "3"), result);
    }

    @Test
    void lrangeNonExistentKey() {
        assertNull(redis.exec("LRANGE nonexistent 0 10"));
    }

    @Test
    void lrangePartialRange() {
        redis.exec("RPUSH list 1 2 3 4 5");
        List<String> result = (List<String>) redis.exec("LRANGE list 1 3");
        assertEquals(List.of("2", "3"), result);
    }

    @Test
    void lrangeOutOfBounds() {
        redis.exec("RPUSH list a b");
        List<String> result = (List<String>) redis.exec("LRANGE list 0 100");
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    void lpushPreservesOrder() {
        redis.exec("LPUSH list 1");
        redis.exec("LPUSH list 2");
        redis.exec("LPUSH list 3");
        List<String> result = (List<String>) redis.exec("LRANGE list 0 3");
        assertEquals(List.of("3", "2", "1"), result);
    }

    // ---- Set (SADD / SREM / SMEMBERS) ----

    @Test
    void saddAndSmembers() {
        redis.exec("SADD set a b c");
        Set<String> result = (Set<String>) redis.exec("SMEMBERS set");
        assertEquals(Set.of("a", "b", "c"), result);
    }

    @Test
    void saddNoDuplicates() {
        redis.exec("SADD set a a b b c");
        Set<String> result = (Set<String>) redis.exec("SMEMBERS set");
        assertEquals(Set.of("a", "b", "c"), result);
    }

    @Test
    void sremRemovesMembers() {
        redis.exec("SADD set a b c");
        redis.exec("SREM set a b");
        Set<String> result = (Set<String>) redis.exec("SMEMBERS set");
        assertEquals(Set.of("c"), result);
    }

    @Test
    void smembersWithSremAll() {
        redis.exec("SADD set x y");
        redis.exec("SREM set x y");
        Set<String> result = (Set<String>) redis.exec("SMEMBERS set");
        assertTrue(result.isEmpty());
    }

    // ---- Hash (HSET / HGET) ----

    @Test
    void hsetAndHget() {
        redis.exec("HSET user name Alice");
        assertEquals("Alice", redis.exec("HGET user name"));
    }

    @Test
    void hgetNonExistentField() {
        redis.exec("HSET user name Alice");
        assertNull(redis.exec("HGET user age"));
    }

    @Test
    void hsetMultipleFields() {
        redis.exec("HSET user name Bob");
        redis.exec("HSET user age 30");
        assertEquals("Bob", redis.exec("HGET user name"));
        assertEquals("30", redis.exec("HGET user age"));
    }

    @Test
    void hgetNonExistentKey() {
        assertNull(redis.exec("HGET nowhere field"));
    }

    // ---- DEL ----

    @Test
    void delRemovesKey() {
        redis.exec("SET key value");
        redis.exec("DEL key");
        assertNull(redis.exec("GET key"));
    }

    @Test
    void delRemovesList() {
        redis.exec("RPUSH list 1 2");
        redis.exec("DEL list");
        assertNull(redis.exec("LRANGE list 0 10"));
    }

    @Test
    void delRemovesSet() {
        redis.exec("SADD set a b");
        redis.exec("DEL set");
        assertEquals(0, redis.exec("SMEMBERS set"));
    }

    @Test
    void delRemovesHash() {
        redis.exec("HSET h f v");
        redis.exec("DEL h");
        assertNull(redis.exec("HGET h f"));
    }

    @Test
    void delNonExistentKey() {
        redis.exec("DEL nonexistent");
    }

    // ---- exec on invalid command ----

    @Test
    void unknownCommandReturnsOne() {
        assertEquals(1, redis.exec("NOTACOMMAND"));
    }
}

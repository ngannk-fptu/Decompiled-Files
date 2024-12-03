/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.json.internal.JsonPattern;
import com.hazelcast.query.impl.getters.JsonPathCursor;
import com.hazelcast.util.collection.WeightedEvictableList;
import java.util.List;

public class JsonGetterContext {
    private static final int PATTERN_CACHE_MAX_SIZE = 20;
    private static final int PATTERN_CACHE_MAX_VOTES = 20;
    private final JsonPathCursor pathCursor;
    private final ThreadLocal<WeightedEvictableList<JsonPattern>> patternListHolder;

    public JsonGetterContext(String attributePath) {
        this.pathCursor = JsonPathCursor.createCursor(attributePath);
        this.patternListHolder = new ThreadLocal();
    }

    public List<WeightedEvictableList.WeightedItem<JsonPattern>> getPatternListSnapshot() {
        return this.getPatternList().getList();
    }

    public void voteFor(WeightedEvictableList.WeightedItem<JsonPattern> item) {
        this.getPatternList().voteFor(item);
    }

    public WeightedEvictableList.WeightedItem<JsonPattern> addOrVoteForPattern(JsonPattern pattern) {
        return this.getPatternList().addOrVote(pattern);
    }

    public JsonPathCursor newJsonPathCursor() {
        return new JsonPathCursor(this.pathCursor);
    }

    private WeightedEvictableList<JsonPattern> getPatternList() {
        WeightedEvictableList<JsonPattern> list = this.patternListHolder.get();
        if (list == null) {
            list = new WeightedEvictableList(20, 20);
            this.patternListHolder.set(list);
        }
        return list;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.com.fasterxml.jackson.core.JsonFactory;
import com.hazelcast.com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.internal.serialization.impl.NavigableJsonInputAdapter;
import com.hazelcast.internal.serialization.impl.StringNavigableJsonAdapter;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.getters.AbstractJsonGetter;
import java.io.IOException;

public final class JsonGetter
extends AbstractJsonGetter {
    public static final JsonGetter INSTANCE = new JsonGetter();
    private JsonFactory factory = new JsonFactory();

    protected JsonGetter() {
        super(null);
    }

    @Override
    protected NavigableJsonInputAdapter annotate(Object object) {
        HazelcastJsonValue hazelcastJson = (HazelcastJsonValue)object;
        return new StringNavigableJsonAdapter(hazelcastJson.toString(), 0);
    }

    @Override
    JsonParser createParser(Object obj) throws IOException {
        if (obj instanceof HazelcastJsonValue) {
            return this.factory.createParser(obj.toString());
        }
        throw new QueryException("Queried object is not of HazelcastJsonValue type. It is " + obj.getClass());
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.com.fasterxml.jackson.core.JsonFactory;
import com.hazelcast.com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DataInputNavigableJsonAdapter;
import com.hazelcast.internal.serialization.impl.NavigableJsonInputAdapter;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.getters.AbstractJsonGetter;
import java.io.IOException;

public final class JsonDataGetter
extends AbstractJsonGetter {
    private static final int UTF_CHARACTER_COUNT_FIELD_SIZE = 4;
    private JsonFactory factory = new JsonFactory();
    private InternalSerializationService ss;

    JsonDataGetter(InternalSerializationService ss) {
        super(null);
        this.ss = ss;
    }

    @Override
    protected JsonParser createParser(Object obj) throws IOException {
        Data data = (Data)obj;
        return this.factory.createParser(data.toByteArray(), 12, data.dataSize() - 4);
    }

    @Override
    protected NavigableJsonInputAdapter annotate(Object object) {
        Data data = (Data)object;
        return new DataInputNavigableJsonAdapter(this.ss.createObjectDataInput(data), 12);
    }
}


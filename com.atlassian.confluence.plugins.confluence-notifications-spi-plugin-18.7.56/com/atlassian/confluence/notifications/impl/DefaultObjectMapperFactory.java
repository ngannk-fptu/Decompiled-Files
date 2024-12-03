/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Pair
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.map.Module
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.ObjectReader
 *  org.codehaus.jackson.map.ObjectWriter
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.impl.ConfluenceJacksonSupport;
import com.atlassian.confluence.notifications.impl.ObjectMapperFactory;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Pair;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;

public class DefaultObjectMapperFactory
implements ObjectMapperFactory {
    @Override
    public ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.registerModule((Module)new ConfluenceJacksonSupport());
        return mapper;
    }

    @Override
    public Pair<ObjectReader, ObjectWriter> buildObjectMapper(Class<?> clazz) {
        ObjectMapper mapper = this.buildObjectMapper();
        ObjectWriter writer = mapper.writerWithType(clazz);
        ObjectReader reader = mapper.reader(clazz);
        return Pair.pair((Object)reader, (Object)writer);
    }

    @Override
    public <T> Either<IllegalStateException, T> verifyObjectSerializable(T payload) {
        Pair<ObjectReader, ObjectWriter> readerWriter = this.buildObjectMapper(payload.getClass());
        StringWriter buffer = new StringWriter();
        try {
            ((ObjectWriter)readerWriter.right()).writeValue((Writer)buffer, payload);
            Object output = ((ObjectReader)readerWriter.left()).readValue(buffer.toString());
            return Either.right((Object)output);
        }
        catch (IOException e) {
            IllegalStateException exception = new IllegalStateException(String.format("Given instance of [%s] is not serializable with Jackson, see cause.", payload.getClass().getName()), e);
            return Either.left((Object)exception);
        }
    }
}


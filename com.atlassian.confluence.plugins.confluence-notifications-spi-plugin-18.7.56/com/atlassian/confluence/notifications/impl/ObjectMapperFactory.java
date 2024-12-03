/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Pair
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.ObjectReader
 *  org.codehaus.jackson.map.ObjectWriter
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.fugue.Either;
import com.atlassian.fugue.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;

public interface ObjectMapperFactory {
    public ObjectMapper buildObjectMapper();

    public Pair<ObjectReader, ObjectWriter> buildObjectMapper(Class<?> var1);

    public <T> Either<IllegalStateException, T> verifyObjectSerializable(T var1);
}


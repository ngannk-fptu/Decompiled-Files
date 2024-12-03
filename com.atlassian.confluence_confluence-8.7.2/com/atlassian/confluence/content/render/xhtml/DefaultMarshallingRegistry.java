/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.fugue.Pair;
import com.google.common.collect.Maps;
import java.util.Map;

public class DefaultMarshallingRegistry
implements MarshallingRegistry {
    private final Map<Pair<Class, MarshallingType>, Marshaller> marshallerMap = Maps.newHashMap();
    private final Map<Pair<Class, MarshallingType>, Unmarshaller> unmarshallerMap = Maps.newHashMap();

    @Override
    public <T> Marshaller<T> getMarshaller(Class<T> clazz, MarshallingType type) {
        Marshaller marshaller = this.marshallerMap.get(this.makePair(clazz, type));
        if (marshaller == null) {
            throw new IllegalStateException("No marshaller found for class: " + clazz.getName() + ", type: " + type);
        }
        return marshaller;
    }

    @Override
    public <T> Unmarshaller<T> getUnmarshaller(Class<T> clazz, MarshallingType type) {
        Unmarshaller unmarshaller = this.unmarshallerMap.get(this.makePair(clazz, type));
        if (unmarshaller == null) {
            throw new IllegalStateException("No unmarshaller found for class: " + clazz.getName() + ", type: " + type);
        }
        return unmarshaller;
    }

    @Override
    public void register(Marshaller marshaller, Class clazz, MarshallingType type) {
        this.marshallerMap.put(this.makePair(clazz, type), marshaller);
    }

    @Override
    public void register(Unmarshaller unmarshaller, Class clazz, MarshallingType type) {
        this.unmarshallerMap.put(this.makePair(clazz, type), unmarshaller);
    }

    private Pair<Class, MarshallingType> makePair(Class clazz, MarshallingType type) {
        return new Pair((Object)clazz, (Object)type);
    }
}


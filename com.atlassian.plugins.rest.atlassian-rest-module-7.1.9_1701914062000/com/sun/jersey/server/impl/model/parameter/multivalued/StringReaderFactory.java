/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderProvider;
import com.sun.jersey.spi.StringReaderWorkers;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

public class StringReaderFactory
implements StringReaderWorkers {
    private Set<StringReaderProvider> readers;

    public void init(ProviderServices providerServices) {
        this.readers = providerServices.getProvidersAndServices(StringReaderProvider.class);
    }

    @Override
    public <T> StringReader<T> getStringReader(Class<T> type, Type genericType, Annotation[] annotations) {
        for (StringReaderProvider srp : this.readers) {
            StringReader sr = srp.getStringReader(type, genericType, annotations);
            if (sr == null) continue;
            return sr;
        }
        return null;
    }
}


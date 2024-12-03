/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.oxm.Marshaller
 *  org.springframework.oxm.Unmarshaller
 */
package org.springframework.http.converter.xml;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;

public class MarshallingHttpMessageConverter
extends AbstractXmlHttpMessageConverter<Object> {
    @Nullable
    private Marshaller marshaller;
    @Nullable
    private Unmarshaller unmarshaller;

    public MarshallingHttpMessageConverter() {
    }

    public MarshallingHttpMessageConverter(Marshaller marshaller) {
        Assert.notNull((Object)marshaller, "Marshaller must not be null");
        this.marshaller = marshaller;
        if (marshaller instanceof Unmarshaller) {
            this.unmarshaller = (Unmarshaller)marshaller;
        }
    }

    public MarshallingHttpMessageConverter(Marshaller marshaller, Unmarshaller unmarshaller) {
        Assert.notNull((Object)marshaller, "Marshaller must not be null");
        Assert.notNull((Object)unmarshaller, "Unmarshaller must not be null");
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return this.canRead(mediaType) && this.unmarshaller != null && this.unmarshaller.supports(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return this.canWrite(mediaType) && this.marshaller != null && this.marshaller.supports(clazz);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readFromSource(Class<?> clazz, HttpHeaders headers, Source source) throws Exception {
        Assert.notNull((Object)this.unmarshaller, "Property 'unmarshaller' is required");
        Object result = this.unmarshaller.unmarshal(source);
        if (!clazz.isInstance(result)) {
            throw new TypeMismatchException(result, clazz);
        }
        return result;
    }

    @Override
    protected void writeToResult(Object o, HttpHeaders headers, Result result) throws Exception {
        Assert.notNull((Object)this.marshaller, "Property 'marshaller' is required");
        this.marshaller.marshal(o, result);
    }
}


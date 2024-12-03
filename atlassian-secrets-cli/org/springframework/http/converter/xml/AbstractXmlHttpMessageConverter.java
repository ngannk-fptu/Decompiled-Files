/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter.xml;

import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public abstract class AbstractXmlHttpMessageConverter<T>
extends AbstractHttpMessageConverter<T> {
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    protected AbstractXmlHttpMessageConverter() {
        super(MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
    }

    @Override
    public final T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return this.readFromSource(clazz, inputMessage.getHeaders(), new StreamSource(inputMessage.getBody()));
    }

    @Override
    protected final void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.writeToResult(t, outputMessage.getHeaders(), new StreamResult(outputMessage.getBody()));
    }

    protected void transform(Source source, Result result) throws TransformerException {
        this.transformerFactory.newTransformer().transform(source, result);
    }

    protected abstract T readFromSource(Class<? extends T> var1, HttpHeaders var2, Source var3) throws IOException, HttpMessageNotReadableException;

    protected abstract void writeToResult(T var1, HttpHeaders var2, Result var3) throws IOException, HttpMessageNotWritableException;
}


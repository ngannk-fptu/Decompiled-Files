/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.bind.JAXBElement
 *  org.springframework.lang.Nullable
 *  org.springframework.oxm.Marshaller
 *  org.springframework.util.Assert
 *  org.springframework.validation.BindingResult
 */
package org.springframework.web.servlet.view.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.springframework.lang.Nullable;
import org.springframework.oxm.Marshaller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

public class MarshallingView
extends AbstractView {
    public static final String DEFAULT_CONTENT_TYPE = "application/xml";
    @Nullable
    private Marshaller marshaller;
    @Nullable
    private String modelKey;

    public MarshallingView() {
        this.setContentType(DEFAULT_CONTENT_TYPE);
        this.setExposePathVariables(false);
    }

    public MarshallingView(Marshaller marshaller) {
        this();
        Assert.notNull((Object)marshaller, (String)"Marshaller must not be null");
        this.marshaller = marshaller;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    protected void initApplicationContext() {
        Assert.notNull((Object)this.marshaller, (String)"Property 'marshaller' is required");
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object toBeMarshalled = this.locateToBeMarshalled(model);
        if (toBeMarshalled == null) {
            throw new IllegalStateException("Unable to locate object to be marshalled in model: " + model);
        }
        Assert.state((this.marshaller != null ? 1 : 0) != 0, (String)"No Marshaller set");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        this.marshaller.marshal(toBeMarshalled, (Result)new StreamResult(baos));
        this.setResponseContentType(request, response);
        response.setContentLength(baos.size());
        baos.writeTo((OutputStream)response.getOutputStream());
    }

    @Nullable
    protected Object locateToBeMarshalled(Map<String, Object> model) throws IllegalStateException {
        if (this.modelKey != null) {
            Object value = model.get(this.modelKey);
            if (value == null) {
                throw new IllegalStateException("Model contains no object with key [" + this.modelKey + "]");
            }
            if (!this.isEligibleForMarshalling(this.modelKey, value)) {
                throw new IllegalStateException("Model object [" + value + "] retrieved via key [" + this.modelKey + "] is not supported by the Marshaller");
            }
            return value;
        }
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            Object value = entry.getValue();
            if (value == null || model.size() != 1 && value instanceof BindingResult || !this.isEligibleForMarshalling(entry.getKey(), value)) continue;
            return value;
        }
        return null;
    }

    protected boolean isEligibleForMarshalling(String modelKey, Object value) {
        Assert.state((this.marshaller != null ? 1 : 0) != 0, (String)"No Marshaller set");
        Class classToCheck = value.getClass();
        if (value instanceof JAXBElement) {
            classToCheck = ((JAXBElement)value).getDeclaredType();
        }
        return this.marshaller.supports(classToCheck);
    }
}


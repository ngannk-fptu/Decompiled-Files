/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.xml.bind.JAXBException
 */
package com.atlassian.plugins.rest.common.json;

import com.atlassian.plugins.rest.common.json.JacksonJsonProviderFactory;
import com.atlassian.plugins.rest.common.json.JaxbJsonMarshaller;
import com.atlassian.plugins.rest.common.json.JsonMarshallingException;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.SerializationConfig;

public class DefaultJaxbJsonMarshaller
implements JaxbJsonMarshaller {
    private final boolean prettyPrint;
    private final JacksonJsonProvider jsonProvider;

    @Deprecated
    public DefaultJaxbJsonMarshaller() {
        this(null, false);
    }

    @Deprecated
    public DefaultJaxbJsonMarshaller(boolean prettyPrint) {
        this(null, prettyPrint);
    }

    private DefaultJaxbJsonMarshaller(Iterable<? extends Module> modules, boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        ImmutableList moduleList = modules != null ? ImmutableList.copyOf(modules) : Collections.emptyList();
        this.jsonProvider = new JacksonJsonProviderFactory().create((Iterable<? extends Module>)moduleList);
    }

    @Override
    public String marshal(Object jaxbBean) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            if (this.prettyPrint) {
                this.jsonProvider.enable(SerializationConfig.Feature.INDENT_OUTPUT, true);
            }
            this.jsonProvider.writeTo(jaxbBean, jaxbBean.getClass(), (Type)null, (Annotation[])null, MediaType.APPLICATION_JSON_TYPE, (MultivaluedMap<String, Object>)null, (OutputStream)os);
            return new String(os.toByteArray(), JsonEncoding.UTF8.getJavaName());
        }
        catch (IOException e) {
            throw new JsonMarshallingException(e);
        }
    }

    @Override
    @Deprecated
    public String marshal(Object jaxbBean, Class ... jaxbClasses) throws JAXBException {
        return this.marshal(jaxbBean);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean prettyPrint;
        private Iterable<? extends Module> modules;

        private Builder() {
        }

        public Builder prettyPrint(boolean prettyPrint) {
            this.prettyPrint = prettyPrint;
            return this;
        }

        public Builder modules(Iterable<? extends Module> modules) {
            this.modules = modules;
            return this;
        }

        public JaxbJsonMarshaller build() {
            return new DefaultJaxbJsonMarshaller(this.modules, this.prettyPrint);
        }
    }
}


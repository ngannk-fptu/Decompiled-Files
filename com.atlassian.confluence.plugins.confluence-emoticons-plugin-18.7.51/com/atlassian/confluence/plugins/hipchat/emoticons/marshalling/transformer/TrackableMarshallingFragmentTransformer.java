/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Unmarshaller
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public class TrackableMarshallingFragmentTransformer<T>
implements FragmentTransformer {
    private Set<T> unmarshallModelSet;
    private final Class<T> clazz;
    private final MarshallingType marshallerMarshallingType;
    private final MarshallingType unmarshallerMarshallingType;
    private final MarshallingRegistry marshallingRegistry;

    public Class<T> getClazz() {
        return this.clazz;
    }

    public Set<T> getUnmarshallModelSet() {
        return this.unmarshallModelSet;
    }

    private Marshaller<T> getMarshaller() {
        return this.marshallingRegistry.getMarshaller(this.clazz, this.marshallerMarshallingType);
    }

    private Unmarshaller<T> getUnmarshaller() {
        return this.marshallingRegistry.getUnmarshaller(this.clazz, this.unmarshallerMarshallingType);
    }

    public TrackableMarshallingFragmentTransformer(Class<T> clazz, MarshallingType marshallerMarshallingType, MarshallingType unmarshallerMarshallingType, MarshallingRegistry marshallingRegistry) {
        this.clazz = clazz;
        this.marshallerMarshallingType = marshallerMarshallingType;
        this.unmarshallerMarshallingType = unmarshallerMarshallingType;
        this.marshallingRegistry = marshallingRegistry;
        this.unmarshallModelSet = new HashSet<T>();
    }

    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.getUnmarshaller().handles(startElementEvent, conversionContext);
    }

    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        Object valueObject = this.getUnmarshaller().unmarshal(reader, mainFragmentTransformer, conversionContext);
        if (valueObject != null) {
            this.unmarshallModelSet.add(valueObject);
        }
        return this.getMarshaller().marshal(valueObject, conversionContext);
    }
}


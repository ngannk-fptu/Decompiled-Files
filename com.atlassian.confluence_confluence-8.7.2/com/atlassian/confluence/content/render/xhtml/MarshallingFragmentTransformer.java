/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public class MarshallingFragmentTransformer<T>
implements FragmentTransformer {
    private final Class<T> clazz;
    private final MarshallingType marshallerMarshallingType;
    private final MarshallingType unmarshallerMarshallingType;
    private final MarshallingRegistry marshallingRegistry;

    public MarshallingFragmentTransformer(Class<T> clazz, MarshallingType marshallerMarshallingType, MarshallingType unmarshallerMarshallingType, MarshallingRegistry marshallingRegistry) {
        this.clazz = clazz;
        this.marshallerMarshallingType = marshallerMarshallingType;
        this.unmarshallerMarshallingType = unmarshallerMarshallingType;
        this.marshallingRegistry = marshallingRegistry;
    }

    private Marshaller<T> getMarshaller() {
        return this.marshallingRegistry.getMarshaller(this.clazz, this.marshallerMarshallingType);
    }

    private Unmarshaller<T> getUnmarshaller() {
        return this.marshallingRegistry.getUnmarshaller(this.clazz, this.unmarshallerMarshallingType);
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.getUnmarshaller().handles(startElementEvent, conversionContext);
    }

    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        T valueObject = this.getUnmarshaller().unmarshal(reader, mainFragmentTransformer, conversionContext);
        return this.getMarshaller().marshal(valueObject, conversionContext);
    }
}


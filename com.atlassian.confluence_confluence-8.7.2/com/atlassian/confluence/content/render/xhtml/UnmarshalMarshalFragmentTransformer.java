/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public class UnmarshalMarshalFragmentTransformer<T>
implements FragmentTransformer {
    private final Unmarshaller<T> unmarshaller;
    private final Marshaller<T> marshaller;

    public UnmarshalMarshalFragmentTransformer(Unmarshaller<T> unmarshaller, Marshaller<T> marshaller) {
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.unmarshaller.handles(startElementEvent, conversionContext);
    }

    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        T valueObject = this.unmarshaller.unmarshal(reader, mainFragmentTransformer, conversionContext);
        return this.marshaller.marshal(valueObject, conversionContext);
    }
}


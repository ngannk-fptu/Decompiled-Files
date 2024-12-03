/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ElementTransformer;
import com.atlassian.confluence.content.render.xhtml.ElementTransformingXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public class ElementTransformingFragmentTransformer
implements FragmentTransformer {
    private final String conversionContextProperty;
    private final List<ElementTransformer> elementTransformers;
    private final Set<QName> handledNames;

    public ElementTransformingFragmentTransformer(List<ElementTransformer> elementTransformers, Set<QName> handledNames) {
        this.elementTransformers = elementTransformers;
        this.handledNames = handledNames;
        this.conversionContextProperty = ElementTransformingFragmentTransformer.class.getName() + "@" + Integer.toHexString(this.hashCode()) + ".applied";
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return conversionContext.getProperty(this.conversionContextProperty) == null && this.handledNames.contains(startElementEvent.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        conversionContext.setProperty(this.conversionContextProperty, "true");
        try {
            ElementTransformingXmlEventReader transformingReader = new ElementTransformingXmlEventReader(reader, this.elementTransformers);
            Streamable streamable = mainFragmentTransformer.transform(transformingReader, mainFragmentTransformer, conversionContext);
            return streamable;
        }
        finally {
            conversionContext.removeProperty(this.conversionContextProperty);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public class EditorErrorFragmentTransformer
implements FragmentTransformer {
    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return "transform-error".equals(StaxUtils.getAttributeValue(startElementEvent, "class"));
    }

    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            String urlEncodedXml = StaxUtils.getAttributeValue(reader.nextEvent().asStartElement(), "data-encoded-xml");
            return writer -> {
                try {
                    writer.write(URLDecoder.decode(urlEncodedXml, "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Unable to find UTF-8 encoding? " + e, e);
                }
            };
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }
}


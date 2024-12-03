/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkConstants;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StoragePlainTextLinkBodyUnmarshaller
implements Unmarshaller<PlainTextLinkBody> {
    @Override
    public PlainTextLinkBody unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StringBuilder body = new StringBuilder();
        try {
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (!event.isCharacters() || !event.asCharacters().isCData()) continue;
                body.append(event.asCharacters().getData());
            }
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException(ex);
        }
        finally {
            StaxUtils.closeQuietly(reader);
        }
        return new PlainTextLinkBody(body.toString());
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return startElementEvent.getName().equals(StorageLinkConstants.PLAIN_TEXT_LINK_BODY_ELEMENT_QNAME);
    }
}


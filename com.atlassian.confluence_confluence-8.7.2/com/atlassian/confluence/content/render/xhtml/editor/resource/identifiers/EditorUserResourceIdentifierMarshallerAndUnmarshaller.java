/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.sal.api.user.UserKey;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;

public class EditorUserResourceIdentifierMarshallerAndUnmarshaller
implements Unmarshaller<ResourceIdentifier>,
StaxStreamMarshaller<UserResourceIdentifier> {
    private static final String USERKEY_ATTRIBUTE_NAME = "userkey";
    private static final String LEGACY_USERNAME_ATTRIBUTE_NAME = "username";

    @Override
    public void marshal(UserResourceIdentifier userResourceIdentifier, XMLStreamWriter xmlStreamWriter, ConversionContext context) throws XMLStreamException {
        UserKey userKey = userResourceIdentifier.getUserKey();
        if (userKey != null) {
            xmlStreamWriter.writeAttribute(USERKEY_ATTRIBUTE_NAME, userKey.getStringValue());
        } else {
            xmlStreamWriter.writeAttribute(LEGACY_USERNAME_ATTRIBUTE_NAME, userResourceIdentifier.getUsername());
        }
    }

    @Override
    public ResourceIdentifier unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StartElement startElement;
        try {
            startElement = xmlEventReader.peek().asStartElement();
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        if (StaxUtils.hasAttributes(startElement, USERKEY_ATTRIBUTE_NAME)) {
            return UserResourceIdentifier.create(new UserKey(StaxUtils.getAttributeValue(startElement, USERKEY_ATTRIBUTE_NAME)));
        }
        return new UserResourceIdentifier(StaxUtils.getAttributeValue(startElement, LEGACY_USERNAME_ATTRIBUTE_NAME));
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StaxUtils.hasAttributes(startElementEvent, USERKEY_ATTRIBUTE_NAME) || StaxUtils.hasAttributes(startElementEvent, LEGACY_USERNAME_ATTRIBUTE_NAME);
    }
}


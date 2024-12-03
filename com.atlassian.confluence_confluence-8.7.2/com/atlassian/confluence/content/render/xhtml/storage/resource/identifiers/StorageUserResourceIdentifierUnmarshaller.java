/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

public class StorageUserResourceIdentifierUnmarshaller
implements Unmarshaller<UserResourceIdentifier> {
    @Override
    public UserResourceIdentifier unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            return this.innerUnmarshal(xmlEventReader);
        }
        catch (Exception e) {
            throw new XhtmlException(e);
        }
    }

    private UserResourceIdentifier innerUnmarshal(XMLEventReader xmlEventReader) throws XhtmlException, XMLStreamException {
        try {
            StartElement startElement = xmlEventReader.nextEvent().asStartElement();
            UserResourceIdentifier userResourceIdentifier = this.buildUserResourceIdentifier(startElement);
            return userResourceIdentifier;
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        finally {
            xmlEventReader.nextEvent();
        }
    }

    private UserResourceIdentifier buildUserResourceIdentifier(StartElement userResourceElement) throws XhtmlException {
        if (this.hasAttribute(userResourceElement, "userkey")) {
            UserKey userKey = new UserKey(this.getAttributeValue(userResourceElement, "userkey", true));
            return UserResourceIdentifier.create(userKey);
        }
        String username = this.getAttributeValue(userResourceElement, "username", true);
        ConfluenceUser user = FindUserHelper.getUserByUsername(username);
        if (user == null) {
            return UserResourceIdentifier.createForNonExistentUser(username);
        }
        return UserResourceIdentifier.createFromUsernameSource(user.getKey(), username);
    }

    private String getAttributeValue(StartElement startElement, String attributeName, boolean mandatory) throws XhtmlException {
        QName attributeQName = StorageUserResourceIdentifierUnmarshaller.getAttributeQName(attributeName);
        Attribute attribute = startElement.getAttributeByName(attributeQName);
        if (attribute == null) {
            if (mandatory) {
                throw new XhtmlException("Missing required attribute: " + attributeQName);
            }
            return null;
        }
        return attribute.getValue();
    }

    private static QName getAttributeQName(String attributeName) {
        return new QName("http://atlassian.com/resource/identifier", attributeName, "ri");
    }

    private boolean hasAttribute(StartElement startElement, String attributeName) throws XhtmlException {
        QName attributeQName = StorageUserResourceIdentifierUnmarshaller.getAttributeQName(attributeName);
        return startElement.getAttributeByName(attributeQName) != null;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        QName name = startElementEvent.getName();
        return "user".equals(name.getLocalPart()) && "http://atlassian.com/resource/identifier".equals(name.getNamespaceURI());
    }
}


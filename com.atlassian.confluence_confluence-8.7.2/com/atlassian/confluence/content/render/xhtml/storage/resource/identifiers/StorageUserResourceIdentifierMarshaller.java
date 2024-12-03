/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.migration.MigrationAware;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;

public class StorageUserResourceIdentifierMarshaller
implements Marshaller<UserResourceIdentifier>,
MigrationAware {
    private static final String STORAGE_FORMAT_MIGRATED_CONTEXT_PROPERTY = "user-resource-identifier-format-migrated";
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public StorageUserResourceIdentifierMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(UserResourceIdentifier userResourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        conversionContext.setProperty(STORAGE_FORMAT_MIGRATED_CONTEXT_PROPERTY, userResourceIdentifier.hasUserKey() && userResourceIdentifier.isCreatedFromUsernameSource());
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("ri", "user", "http://atlassian.com/resource/identifier");
            if (userResourceIdentifier.hasUserKey()) {
                xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "userkey", userResourceIdentifier.getUserKey().getStringValue());
            } else {
                xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "username", userResourceIdentifier.getUsername());
            }
            xmlStreamWriter.writeEndElement();
        });
    }

    @Override
    public boolean wasMigrationPerformed(ConversionContext conversionContext) {
        return conversionContext.getProperty(STORAGE_FORMAT_MIGRATED_CONTEXT_PROPERTY) == Boolean.TRUE;
    }
}


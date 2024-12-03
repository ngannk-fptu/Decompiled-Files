/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import org.apache.tika.metadata.Metadata;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class MetadataContentHandler
extends DefaultHandler {
    private final Metadata metadata;

    public MetadataContentHandler(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public void startElement(String uri, String local, String name, Attributes attributes) throws SAXException {
        if ("meta".equals(local)) {
            String aname = attributes.getValue("name");
            String content = attributes.getValue("content");
            this.metadata.add(aname, content);
        }
    }
}


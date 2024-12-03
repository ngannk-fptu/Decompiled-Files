/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml;

import com.lowagie.text.DocListener;
import com.lowagie.text.xml.SAXiTextHandler;
import com.lowagie.text.xml.XmlPeer;
import java.util.Map;
import java.util.Properties;
import org.xml.sax.Attributes;

public class SAXmyHandler
extends SAXiTextHandler<XmlPeer> {
    public SAXmyHandler(DocListener document, Map<String, XmlPeer> myTags) {
        super(document, myTags);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attrs) {
        if (this.myTags.containsKey(name)) {
            XmlPeer peer = (XmlPeer)this.myTags.get(name);
            this.handleStartingTags(peer.getTag(), peer.getAttributes(attrs));
        } else {
            Properties attributes = new Properties();
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); ++i) {
                    String attribute = attrs.getQName(i);
                    attributes.setProperty(attribute, attrs.getValue(i));
                }
            }
            this.handleStartingTags(name, attributes);
        }
    }

    @Override
    public void endElement(String uri, String lname, String name) {
        if (this.myTags.containsKey(name)) {
            XmlPeer peer = (XmlPeer)this.myTags.get(name);
            this.handleEndingTags(peer.getTag());
        } else {
            this.handleEndingTags(name);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.io.StringWriter;
import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.NotificationType;
import org.bedework.caldav.util.notifications.parse.Parser;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.webdav.servlet.shared.UrlPrefixer;
import org.bedework.webdav.servlet.shared.UrlUnprefixer;

public abstract class BaseNotificationType {
    private String name;

    public abstract QName getElementName();

    public void setName(String val) {
        this.name = val;
    }

    public String getName() {
        return this.name;
    }

    public abstract void setEncoding(String var1);

    public abstract String getEncoding();

    public abstract List<AttributeType> getElementAttributes();

    public abstract void prefixHrefs(UrlPrefixer var1) throws Throwable;

    public abstract void unprefixHrefs(UrlUnprefixer var1) throws Throwable;

    public String toXml() throws Throwable {
        StringWriter str = new StringWriter();
        XmlEmit xml = new XmlEmit();
        xml.addNs(new XmlEmit.NameSpace("DAV:", "DAV"), false);
        xml.addNs(new XmlEmit.NameSpace("urn:ietf:params:xml:ns:caldav", "C"), false);
        xml.addNs(new XmlEmit.NameSpace("http://calendarserver.org/ns/", "CSS"), false);
        xml.addNs(new XmlEmit.NameSpace("http://bedeworkcalserver.org/ns/", "BSS"), false);
        xml.addNs(new XmlEmit.NameSpace("http://bedework.org/ns/", "BSYS"), false);
        xml.startEmit(str);
        this.toXml(xml);
        return str.toString();
    }

    public void toXml(XmlEmit xml) throws Throwable {
        if (Boolean.parseBoolean(xml.getProperty("withBedeworkElements")) && this.getName() != null) {
            xml.property(BedeworkServerTags.name, this.getName());
        }
    }

    public Object clone() {
        try {
            String xml = this.toXml();
            NotificationType note = Parser.fromXml(xml);
            return note.getNotification();
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static class AttributeType {
        private final String name;
        private final String value;

        public AttributeType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }
    }
}


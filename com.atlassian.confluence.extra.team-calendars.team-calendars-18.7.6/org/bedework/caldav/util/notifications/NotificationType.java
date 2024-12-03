/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.notifications.ProcessorsType;
import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.w3c.dom.Document;

public class NotificationType {
    private ProcessorsType processors;
    private String dtstamp;
    private BaseNotificationType notification;
    private Document parsed;

    public void setProcessors(ProcessorsType processors) {
        this.processors = processors;
    }

    public ProcessorsType getProcessors() {
        return this.processors;
    }

    public void setDtstamp(String val) {
        this.dtstamp = val;
    }

    public String getDtstamp() {
        return this.dtstamp;
    }

    public void setParsed(Document val) {
        this.parsed = val;
    }

    public Document getParsed() {
        return this.parsed;
    }

    public void setNotification(BaseNotificationType val) {
        this.notification = val;
    }

    public BaseNotificationType getNotification() {
        return this.notification;
    }

    public void setName(String val) {
        if (val == null) {
            return;
        }
        BaseNotificationType bn = this.getNotification();
        String prefix = bn.getElementName().getLocalPart();
        if (val.length() < prefix.length()) {
            return;
        }
        bn.setName(val.substring(prefix.length()));
    }

    public String getName() {
        BaseNotificationType bn = this.getNotification();
        return bn.getElementName().getLocalPart() + bn.getName();
    }

    public String getContentType() {
        StringBuilder sb = new StringBuilder("notification;type=");
        QName qn = this.getNotification().getElementName();
        sb.append(qn);
        List<BaseNotificationType.AttributeType> attrs = this.getNotification().getElementAttributes();
        if (!Util.isEmpty(attrs)) {
            for (BaseNotificationType.AttributeType attr : attrs) {
                sb.append(";noteattr_");
                sb.append(attr.getName());
                sb.append("=");
                sb.append(attr.getValue());
            }
        }
        return sb.toString();
    }

    public static boolean isNotificationContentType(String val) {
        return val != null && val.startsWith("notification;type=");
    }

    public static NotificationInfo fromContentType(String val) {
        if (val == null) {
            return null;
        }
        if (!NotificationType.isNotificationContentType(val)) {
            return null;
        }
        String[] parts = val.split(";");
        if (parts.length < 2 || !parts[1].startsWith("type=")) {
            return null;
        }
        NotificationInfo ni = new NotificationInfo();
        ni.type = QName.valueOf(parts[1].substring(5));
        for (int i = 2; i < parts.length; ++i) {
            if (!parts[i].startsWith("noteattr_")) continue;
            if (ni.attrs == null) {
                ni.attrs = new ArrayList<BaseNotificationType.AttributeType>();
            }
            int pos = parts[i].indexOf("=");
            ni.attrs.add(new BaseNotificationType.AttributeType(parts[i].substring(9, pos), parts[i].substring(pos + 1)));
        }
        return ni;
    }

    public String toXml() throws Throwable {
        return this.toXml(false);
    }

    public String toXml(boolean withBedeworkElements) throws Throwable {
        StringWriter str = new StringWriter();
        XmlEmit xml = new XmlEmit();
        if (withBedeworkElements) {
            xml.setProperty("withBedeworkElements", "true");
        }
        xml.addNs(new XmlEmit.NameSpace("DAV:", "DAV"), false);
        xml.addNs(new XmlEmit.NameSpace("urn:ietf:params:xml:ns:caldav", "C"), false);
        xml.addNs(new XmlEmit.NameSpace("http://calendarserver.org/ns/", "CSS"), false);
        xml.addNs(new XmlEmit.NameSpace("http://bedeworkcalserver.org/ns/", "BW"), false);
        xml.addNs(new XmlEmit.NameSpace("http://bedework.org/ns/", "BSS"), false);
        xml.startEmit(str);
        this.toXml(xml);
        return str.toString();
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.notification);
        if (Boolean.parseBoolean(xml.getProperty("withBedeworkElements")) && this.getProcessors() != null) {
            this.getProcessors().toXml(xml);
        }
        xml.property(AppleServerTags.dtstamp, this.getDtstamp());
        this.getNotification().toXml(xml);
        xml.closeTag(AppleServerTags.notification);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("dtstamp", this.getDtstamp());
        ts.append("notification", this.getNotification().toString());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }

    public static class NotificationInfo {
        public QName type;
        public List<BaseNotificationType.AttributeType> attrs;
    }
}


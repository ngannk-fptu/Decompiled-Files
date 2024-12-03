/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.sharing.OrganizerType;
import org.bedework.caldav.util.sharing.UserType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class InviteType {
    private OrganizerType organizer;
    private List<UserType> users;

    public void setOrganizer(OrganizerType val) {
        this.organizer = val;
    }

    public OrganizerType getOrganizer() {
        return this.organizer;
    }

    public List<UserType> getUsers() {
        if (this.users == null) {
            this.users = new ArrayList<UserType>();
        }
        return this.users;
    }

    public UserType finduser(String href) {
        for (UserType u : this.getUsers()) {
            if (!u.getHref().equals(href)) continue;
            return u;
        }
        return null;
    }

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
        xml.openTag(AppleServerTags.invite);
        if (this.getOrganizer() != null) {
            this.getOrganizer().toXml(xml);
        }
        for (UserType u : this.getUsers()) {
            u.toXml(xml);
        }
        xml.closeTag(AppleServerTags.invite);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("organizer", this.getOrganizer());
        ts.append("users", this.getUsers());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}


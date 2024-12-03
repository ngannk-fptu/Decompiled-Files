/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared.serverInfo;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.serverInfo.Application;
import org.bedework.webdav.servlet.shared.serverInfo.Feature;
import org.bedework.webdav.servlet.shared.serverInfo.Features;

public class ServerInfo {
    private String token;
    private final Features features = new Features();
    private List<Application> applications;

    public void setToken(String val) {
        this.token = val;
    }

    public String getToken() {
        return this.token;
    }

    public void addFeature(Feature val) {
        this.features.addFeature(val);
    }

    public void setApplications(List<Application> val) {
        this.applications = val;
    }

    public List<Application> getApplications() {
        return this.applications;
    }

    public void addApplication(Application val) {
        if (this.applications == null) {
            this.applications = new ArrayList<Application>();
        }
        this.applications.add(val);
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
        xml.openTag(WebdavTags.serverinfo);
        if (this.getToken() != null) {
            xml.property(WebdavTags.token, this.getToken());
        }
        this.features.toXml(xml);
        if (!Util.isEmpty(this.getApplications())) {
            xml.openTag(WebdavTags.applications);
            for (Application s : this.getApplications()) {
                s.toXml(xml);
            }
            xml.closeTag(WebdavTags.applications);
        }
        xml.closeTag(WebdavTags.serverinfo);
    }
}


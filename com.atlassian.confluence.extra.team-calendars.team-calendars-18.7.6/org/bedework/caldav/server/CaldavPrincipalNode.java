/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.bedework.caldav.server.CaldavURI;
import org.bedework.caldav.server.sysinterface.CalPrincipalInfo;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.CarddavTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavPrincipalNode;

public class CaldavPrincipalNode
extends WebdavPrincipalNode {
    private CalPrincipalInfo ui;
    private final SysIntf sysi;
    private static final HashMap<QName, WebdavNsNode.PropertyTagEntry> propertyNames = new HashMap();

    public CaldavPrincipalNode(CaldavURI cdURI, SysIntf sysi, CalPrincipalInfo ui, boolean isUser) throws WebdavException {
        super(sysi, sysi.getUrlHandler(), cdURI.getPath(), cdURI.getPrincipal(), cdURI.isCollection(), cdURI.getUri());
        this.sysi = sysi;
        this.ui = ui;
        if (ui == null) {
            this.ui = sysi.getCalPrincipalInfo(cdURI.getPrincipal());
        }
    }

    @Override
    public String getDisplayname() throws WebdavException {
        String dn = this.ui.getDisplayname();
        if (dn == null) {
            return super.getDisplayname();
        }
        return dn;
    }

    @Override
    public boolean knownProperty(QName tag) {
        if (propertyNames.get(tag) != null) {
            return true;
        }
        return super.knownProperty(tag);
    }

    @Override
    public boolean generatePropertyValue(QName tag, WebdavNsIntf intf, boolean allProp) throws WebdavException {
        XmlEmit xml = intf.getXmlEmit();
        try {
            if (tag.equals(CaldavTags.calendarHomeSet)) {
                if (this.ui == null) {
                    return false;
                }
                xml.openTag(tag);
                this.generateHref(xml, this.ui.userHomePath);
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(CarddavTags.addressData)) {
                if (this.ui == null) {
                    return false;
                }
                xml.property(tag, this.ui.getCardStr());
                return true;
            }
            if (tag.equals(CaldavTags.calendarUserAddressSet)) {
                xml.openTag(tag);
                xml.property(WebdavTags.href, this.sysi.principalToCaladdr(this.getOwner()));
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(CaldavTags.scheduleInboxURL)) {
                if (this.ui == null) {
                    return false;
                }
                xml.openTag(tag);
                this.generateHref(xml, this.ui.inboxPath);
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(CaldavTags.scheduleOutboxURL)) {
                if (this.ui == null) {
                    return false;
                }
                xml.openTag(tag);
                this.generateHref(xml, this.ui.outboxPath);
                xml.closeTag(tag);
                return true;
            }
            return super.generatePropertyValue(tag, intf, allProp);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public Collection<WebdavNsNode.PropertyTagEntry> getPropertyNames() throws WebdavException {
        ArrayList<WebdavNsNode.PropertyTagEntry> res = new ArrayList<WebdavNsNode.PropertyTagEntry>();
        res.addAll(super.getPropertyNames());
        res.addAll(propertyNames.values());
        return res;
    }

    static {
        CaldavPrincipalNode.addPropEntry(propertyNames, CaldavTags.calendarHomeSet);
        CaldavPrincipalNode.addPropEntry(propertyNames, CaldavTags.calendarUserAddressSet);
        CaldavPrincipalNode.addPropEntry(propertyNames, CaldavTags.scheduleInboxURL);
        CaldavPrincipalNode.addPropEntry(propertyNames, CaldavTags.scheduleOutboxURL);
        CaldavPrincipalNode.addPropEntry(propertyNames, CarddavTags.addressData);
    }
}


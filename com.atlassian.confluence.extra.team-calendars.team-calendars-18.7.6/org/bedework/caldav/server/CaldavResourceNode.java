/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Acl;
import org.bedework.caldav.server.CalDAVResource;
import org.bedework.caldav.server.CaldavBwNode;
import org.bedework.caldav.server.CaldavURI;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.notifications.NotificationType;
import org.bedework.util.misc.Util;
import org.bedework.util.timezones.DateTimeUtil;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.w3c.dom.Element;

public class CaldavResourceNode
extends CaldavBwNode {
    private CalDAVResource resource;
    private AccessPrincipal owner;
    private String entityName;
    private Acl.CurrentAccess currentAccess;
    private static final HashMap<QName, WebdavNsNode.PropertyTagEntry> propertyNames = new HashMap();

    public CaldavResourceNode(SysIntf sysi, int status, String uri) {
        super(true, sysi, uri);
        this.setStatus(status);
    }

    public CaldavResourceNode(CaldavURI cdURI, SysIntf sysi) throws WebdavException {
        super(cdURI, sysi);
        this.resource = cdURI.getResource();
        this.col = cdURI.getCol();
        this.collection = false;
        this.allowsGet = true;
        this.entityName = cdURI.getEntityName();
        this.exists = cdURI.getExists();
    }

    public CaldavResourceNode(CalDAVResource resource, SysIntf sysi) throws WebdavException {
        super(sysi, resource.getParentPath(), true, resource.getPath());
        this.allowsGet = false;
        this.resource = resource;
        this.exists = true;
    }

    @Override
    public void init(boolean content) throws WebdavException {
        if (!content) {
            return;
        }
        try {
            if (this.resource == null && this.exists && this.entityName == null) {
                this.exists = false;
                return;
            }
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public AccessPrincipal getOwner() throws WebdavException {
        if (this.owner == null) {
            if (this.resource == null) {
                return null;
            }
            this.owner = this.resource.getOwner();
        }
        return this.owner;
    }

    @Override
    public boolean removeProperty(Element val, WebdavNsNode.SetPropertyResult spr) throws WebdavException {
        this.warn("Unimplemented - removeProperty");
        return false;
    }

    @Override
    public boolean setProperty(Element val, WebdavNsNode.SetPropertyResult spr) throws WebdavException {
        return super.setProperty(val, spr);
    }

    @Override
    public void update() throws WebdavException {
        if (this.resource != null) {
            this.getSysi().updateFile(this.resource, true);
        }
    }

    public String getEntityName() {
        return this.entityName;
    }

    @Override
    public boolean trailSlash() {
        return false;
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
            if (tag.equals(AppleServerTags.notificationtype)) {
                if (this.resource == null) {
                    return false;
                }
                NotificationType.NotificationInfo ni = this.resource.getNotificationType();
                if (ni == null) {
                    return false;
                }
                xml.openTag(tag);
                xml.startTag(ni.type);
                if (!Util.isEmpty(ni.attrs)) {
                    for (BaseNotificationType.AttributeType at : ni.attrs) {
                        xml.attribute(at.getName(), at.getValue());
                    }
                }
                xml.endEmptyTag();
                xml.closeTag(tag);
                return true;
            }
            return super.generatePropertyValue(tag, intf, allProp);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void setResource(CalDAVResource val) {
        this.resource = val;
    }

    public CalDAVResource getResource() throws WebdavException {
        this.init(true);
        return this.resource;
    }

    @Override
    public Acl.CurrentAccess getCurrentAccess() throws WebdavException {
        if (this.currentAccess != null) {
            return this.currentAccess;
        }
        if (this.resource == null) {
            return null;
        }
        try {
            this.currentAccess = this.getSysi().checkAccess(this.resource, 25, true);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
        return this.currentAccess;
    }

    @Override
    public String getEtagValue(boolean strong) throws WebdavException {
        this.init(true);
        if (this.resource == null) {
            return null;
        }
        String val = this.resource.getEtag();
        if (strong) {
            return val;
        }
        return "W/" + val;
    }

    @Override
    public String getEtokenValue() throws WebdavException {
        return this.concatEtoken(this.getEtagValue(true), "");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CaldavResourceNode{");
        sb.append("path=");
        sb.append(this.getPath());
        sb.append(", entityName=");
        sb.append(String.valueOf(this.entityName));
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String writeContent(XmlEmit xml, Writer wtr, String contentType) throws WebdavException {
        return null;
    }

    @Override
    public boolean getContentBinary() throws WebdavException {
        return true;
    }

    @Override
    public InputStream getContentStream() throws WebdavException {
        return this.resource.getBinaryContent();
    }

    @Override
    public String getContentString(String contentType) throws WebdavException {
        this.init(true);
        throw new WebdavException("binary content");
    }

    @Override
    public String getContentLang() throws WebdavException {
        return "en";
    }

    @Override
    public long getContentLen() throws WebdavException {
        this.init(true);
        if (this.resource == null) {
            return 0L;
        }
        return this.resource.getContentLen();
    }

    @Override
    public String getContentType() throws WebdavException {
        if (this.resource == null) {
            return null;
        }
        return this.resource.getContentType();
    }

    @Override
    public String getCreDate() throws WebdavException {
        this.init(false);
        if (this.resource == null) {
            return null;
        }
        return this.resource.getCreated();
    }

    @Override
    public String getDisplayname() throws WebdavException {
        return this.getEntityName();
    }

    @Override
    public String getLastmodDate() throws WebdavException {
        this.init(false);
        if (this.resource == null) {
            return null;
        }
        try {
            return DateTimeUtil.fromISODateTimeUTCtoRfc822(this.resource.getLastmod());
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public boolean allowsSyncReport() throws WebdavException {
        return false;
    }

    @Override
    public boolean getDeleted() throws WebdavException {
        if (this.resource == null) {
            return false;
        }
        return this.resource.getDeleted();
    }

    static {
        CaldavResourceNode.addPropEntry(propertyNames, AppleServerTags.notificationtype);
    }
}


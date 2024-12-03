/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;
import javax.xml.namespace.QName;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Acl;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WdCollection;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WdSysIntf;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.w3c.dom.Element;

public class WebdavPrincipalNode
extends WebdavNsNode {
    private final AccessPrincipal account;
    private static final HashMap<QName, WebdavNsNode.PropertyTagEntry> propertyNames = new HashMap();

    public WebdavPrincipalNode(WdSysIntf sysi, UrlHandler urlHandler, String path, AccessPrincipal account, boolean collection, String uri) throws WebdavException {
        super(sysi, urlHandler, path, collection, uri);
        this.account = account;
        this.userPrincipal = account.getKind() == 1;
        this.groupPrincipal = account.getKind() == 2;
    }

    @Override
    public AccessPrincipal getOwner() throws WebdavException {
        return this.account;
    }

    @Override
    public void update() throws WebdavException {
    }

    @Override
    public Acl.CurrentAccess getCurrentAccess() throws WebdavException {
        return null;
    }

    @Override
    public String getEtagValue(boolean strong) throws WebdavException {
        String val = "1234567890";
        if (strong) {
            return "\"1234567890\"";
        }
        return "W/\"1234567890\"";
    }

    @Override
    public boolean trailSlash() {
        return true;
    }

    @Override
    public Collection<? extends WdEntity> getChildren(Supplier<Object> filterGetter) throws WebdavException {
        return null;
    }

    @Override
    public WdCollection getCollection(boolean deref) throws WebdavException {
        return null;
    }

    @Override
    public WdCollection getImmediateTargetCollection() throws WebdavException {
        return null;
    }

    @Override
    public boolean allowsSyncReport() throws WebdavException {
        return false;
    }

    @Override
    public boolean getDeleted() throws WebdavException {
        return false;
    }

    @Override
    public String getSyncToken() throws WebdavException {
        return null;
    }

    @Override
    public String writeContent(XmlEmit xml, Writer wtr, String contentType) throws WebdavException {
        return null;
    }

    @Override
    public boolean getContentBinary() throws WebdavException {
        return false;
    }

    @Override
    public String getContentLang() throws WebdavException {
        return null;
    }

    @Override
    public long getContentLen() throws WebdavException {
        return 0L;
    }

    @Override
    public String getContentType() throws WebdavException {
        return null;
    }

    @Override
    public String getCreDate() throws WebdavException {
        return null;
    }

    @Override
    public String getDisplayname() throws WebdavException {
        return this.account.getAccount();
    }

    @Override
    public String getLastmodDate() throws WebdavException {
        return null;
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
    public boolean knownProperty(QName tag) {
        if (propertyNames.get(tag) != null) {
            return true;
        }
        return super.knownProperty(tag);
    }

    @Override
    public boolean generatePropertyValue(QName tag, WebdavNsIntf intf, boolean allProp) throws WebdavException {
        String ns = tag.getNamespaceURI();
        XmlEmit xml = intf.getXmlEmit();
        try {
            if (tag.equals(WebdavTags.groupMemberSet)) {
                xml.emptyTag(tag);
                return true;
            }
            if (tag.equals(WebdavTags.groupMembership)) {
                xml.emptyTag(tag);
                return true;
            }
            if (tag.equals(WebdavTags.notificationURL) || tag.equals(AppleServerTags.notificationURL)) {
                if (this.wdSysIntf.getNotificationURL() == null) {
                    return false;
                }
                xml.openTag(tag);
                this.generateHref(xml, this.wdSysIntf.getNotificationURL());
                xml.closeTag(tag);
                return true;
            }
            return super.generatePropertyValue(tag, intf, allProp);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    static {
        WebdavPrincipalNode.addPropEntry(propertyNames, WebdavTags.groupMemberSet);
        WebdavPrincipalNode.addPropEntry(propertyNames, WebdavTags.groupMembership);
        WebdavPrincipalNode.addPropEntry(propertyNames, WebdavTags.notificationURL);
        WebdavPrincipalNode.addPropEntry(propertyNames, AppleServerTags.notificationURL);
    }
}


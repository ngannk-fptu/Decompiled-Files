/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.AccessXmlUtil;
import org.bedework.access.Acl;
import org.bedework.access.PrivilegeSet;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WdCollection;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WdSysIntf;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.w3c.dom.Element;

public abstract class WebdavNsNode
implements Serializable {
    protected boolean debug;
    protected boolean exists = true;
    private transient Logger log;
    protected WdSysIntf wdSysIntf;
    protected String uri;
    protected String path;
    protected boolean collection;
    protected boolean userPrincipal;
    protected boolean groupPrincipal;
    protected boolean allowsGet;
    protected int status = 200;
    private static final HashMap<QName, PropertyTagEntry> propertyNames = new HashMap();
    private static final Collection<QName> supportedReports = new ArrayList<QName>();
    protected boolean alias;
    protected String targetUri;
    protected UrlHandler urlHandler;

    public WebdavNsNode(WdSysIntf sysi, UrlHandler urlHandler, String path, boolean collection, String uri) {
        this.wdSysIntf = sysi;
        this.urlHandler = urlHandler;
        this.path = path;
        this.collection = collection;
        this.uri = uri;
        this.debug = this.getLogger().isDebugEnabled();
    }

    public abstract Acl.CurrentAccess getCurrentAccess() throws WebdavException;

    public abstract void update() throws WebdavException;

    public abstract boolean trailSlash();

    public abstract Collection<? extends WdEntity> getChildren(Supplier<Object> var1) throws WebdavException;

    public String getPath() {
        return this.path;
    }

    public void generateHref(XmlEmit xml) throws WebdavException {
        try {
            this.generateUrl(xml, WebdavTags.href, this.uri, this.getExists());
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void generateHref(XmlEmit xml, String uri) throws WebdavException {
        this.generateUrl(xml, WebdavTags.href, uri, false);
    }

    public String getPrefixedUri() throws WebdavException {
        return this.urlHandler.prefix(this.uri);
    }

    public String getPrefixedUri(String uri) throws WebdavException {
        return this.urlHandler.prefix(uri);
    }

    public void generateUrl(XmlEmit xml, QName tag, String uri, boolean exists) throws WebdavException {
        try {
            String prefixed = this.getPrefixedUri(uri);
            if (exists) {
                if (prefixed.endsWith("/")) {
                    if (!this.trailSlash()) {
                        prefixed = prefixed.substring(0, prefixed.length() - 1);
                    }
                } else if (this.trailSlash()) {
                    prefixed = prefixed + "/";
                }
            }
            xml.property(tag, prefixed);
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public boolean removeProperty(Element val, SetPropertyResult spr) throws WebdavException {
        try {
            if (XmlUtil.nodeMatches(val, WebdavTags.getetag)) {
                spr.status = 403;
                return true;
            }
            if (XmlUtil.nodeMatches(val, WebdavTags.getlastmodified)) {
                spr.status = 403;
                return true;
            }
            return false;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public boolean setProperty(Element val, SetPropertyResult spr) throws WebdavException {
        try {
            QName tag = new QName(val.getNamespaceURI(), val.getLocalName());
            if (tag.equals(WebdavTags.getetag)) {
                spr.status = 403;
                return true;
            }
            if (tag.equals(WebdavTags.getlastmodified)) {
                spr.status = 403;
                return true;
            }
            return false;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public boolean knownProperty(QName tag) {
        return propertyNames.get(tag) != null;
    }

    public boolean generatePropertyValue(QName tag, WebdavNsIntf intf, boolean allProp) throws WebdavException {
        String ns = tag.getNamespaceURI();
        XmlEmit xml = intf.getXmlEmit();
        if (!ns.equals("DAV:")) {
            return false;
        }
        try {
            if (tag.equals(WebdavTags.acl)) {
                intf.emitAcl(this);
                return true;
            }
            if (tag.equals(WebdavTags.addMember)) {
                xml.openTag(tag);
                this.generateHref(xml, Util.buildPath(false, this.uri, "/", intf.getAddMemberSuffix()));
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(WebdavTags.creationdate)) {
                String val = this.getCreDate();
                if (val == null) {
                    return true;
                }
                xml.property(tag, val);
                return true;
            }
            if (tag.equals(WebdavTags.currentUserPrincipal)) {
                xml.openTag(tag);
                if (intf.getAccount() == null) {
                    xml.emptyTag(WebdavTags.unauthenticated);
                } else {
                    String href = intf.makeUserHref(intf.getAccount());
                    if (!href.endsWith("/")) {
                        href = href + "/";
                    }
                    xml.property(WebdavTags.href, href);
                }
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(WebdavTags.currentUserPrivilegeSet)) {
                Acl.CurrentAccess ca = this.getCurrentAccess();
                if (ca == null) {
                    xml.emptyTag(tag);
                    return true;
                }
                PrivilegeSet ps = ca.getPrivileges();
                char[] privileges = ps.getPrivileges();
                AccessXmlUtil.emitCurrentPrivSet(xml, intf.getAccessUtil().getPrivTags(), privileges);
                return true;
            }
            if (tag.equals(WebdavTags.displayname)) {
                xml.property(tag, this.getDisplayname());
                return true;
            }
            if (tag.equals(WebdavTags.getcontentlanguage)) {
                if (!this.getAllowsGet()) {
                    return true;
                }
                xml.property(tag, String.valueOf(this.getContentLang()));
                return true;
            }
            if (tag.equals(WebdavTags.getcontentlength)) {
                if (!this.getAllowsGet()) {
                    xml.property(tag, "0");
                    return true;
                }
                xml.property(tag, String.valueOf(this.getContentLen()));
                return true;
            }
            if (tag.equals(WebdavTags.getcontenttype)) {
                if (!this.getAllowsGet()) {
                    return true;
                }
                String val = this.getContentType();
                if (val == null) {
                    return true;
                }
                xml.property(tag, val);
                return true;
            }
            if (tag.equals(WebdavTags.getetag)) {
                xml.property(tag, this.getEtagValue(true));
                return true;
            }
            if (tag.equals(WebdavTags.getlastmodified)) {
                String val = this.getLastmodDate();
                if (val == null) {
                    return true;
                }
                xml.property(tag, val);
                return true;
            }
            if (tag.equals(WebdavTags.owner)) {
                xml.openTag(tag);
                String href = intf.makeUserHref(this.getOwner().getPrincipalRef());
                if (!href.endsWith("/")) {
                    href = href + "/";
                }
                xml.property(WebdavTags.href, href);
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(WebdavTags.principalURL)) {
                xml.openTag(tag);
                this.generateUrl(xml, WebdavTags.href, this.getEncodedUri(), this.getExists());
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(WebdavTags.resourcetype)) {
                if (!this.isPrincipal() && !this.isCollection()) {
                    xml.emptyTag(tag);
                    return true;
                }
                xml.openTag(tag);
                if (this.isPrincipal()) {
                    xml.emptyTag(WebdavTags.principal);
                }
                if (this.isCollection()) {
                    xml.emptyTag(WebdavTags.collection);
                }
                xml.closeTag(tag);
                return true;
            }
            if (tag.equals(WebdavTags.supportedPrivilegeSet)) {
                intf.getAccessUtil().emitSupportedPrivSet();
                return true;
            }
            if (tag.equals(WebdavTags.supportedReportSet)) {
                intf.emitSupportedReportSet(this);
                return true;
            }
            if (tag.equals(WebdavTags.syncToken)) {
                if (!this.wdSysIntf.allowsSyncReport(this.getCollection(false))) {
                    return false;
                }
                xml.property(tag, this.getSyncToken());
                return true;
            }
            return false;
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public void init(boolean content) throws WebdavException {
    }

    public boolean isPrincipal() throws WebdavException {
        return this.userPrincipal || this.groupPrincipal;
    }

    public Collection<PropertyTagEntry> getPropertyNames() throws WebdavException {
        if (!this.isPrincipal()) {
            return propertyNames.values();
        }
        ArrayList<PropertyTagEntry> res = new ArrayList<PropertyTagEntry>();
        res.addAll(propertyNames.values());
        return res;
    }

    public Collection<QName> getSupportedReports() throws WebdavException {
        ArrayList<QName> res = new ArrayList<QName>();
        res.addAll(supportedReports);
        if (this.wdSysIntf.allowsSyncReport(this.getCollection(false))) {
            res.add(WebdavTags.syncCollection);
        }
        return res;
    }

    public void setExists(boolean val) throws WebdavException {
        this.exists = val;
    }

    public boolean getExists() throws WebdavException {
        return this.exists;
    }

    public void setUri(String val) throws WebdavException {
        this.init(false);
        this.uri = val;
    }

    public String getUri() throws WebdavException {
        this.init(false);
        return this.uri;
    }

    public String getEncodedUri() throws WebdavException {
        return this.getEncodedUri(this.getUri());
    }

    public String getEncodedUri(String uri) throws WebdavException {
        try {
            return new URI(null, null, uri, null).toString();
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            throw new WebdavBadRequest();
        }
    }

    public boolean isCollection() throws WebdavException {
        return this.collection;
    }

    public void setAllowsGet(boolean val) throws WebdavException {
        this.allowsGet = val;
    }

    public boolean getAllowsGet() throws WebdavException {
        return this.allowsGet;
    }

    public void setStatus(int val) {
        this.status = val;
    }

    public int getStatus() {
        return this.status;
    }

    public void setAlias(boolean val) throws WebdavException {
        this.init(false);
        this.alias = val;
    }

    public boolean getAlias() throws WebdavException {
        this.init(false);
        return this.alias;
    }

    public void setTargetUri(String val) throws WebdavException {
        this.init(false);
        this.targetUri = val;
    }

    public String getTargetUri() throws WebdavException {
        this.init(false);
        return this.targetUri;
    }

    public WebdavNsIntf.Content getContent(String contentType) throws WebdavException {
        String cont = this.getContentString(contentType);
        if (cont == null) {
            return null;
        }
        WebdavNsIntf.Content c = new WebdavNsIntf.Content();
        c.rdr = new StringReader(cont);
        c.contentType = this.getContentType();
        c.contentLength = this.getContentLen();
        return c;
    }

    public InputStream getContentStream() throws WebdavException {
        return null;
    }

    public String getContentString(String contentType) throws WebdavException {
        return null;
    }

    public void setDefaults(QName methodTag) throws WebdavException {
    }

    public abstract String writeContent(XmlEmit var1, Writer var2, String var3) throws WebdavException;

    public abstract boolean getContentBinary() throws WebdavException;

    public abstract String getContentLang() throws WebdavException;

    public abstract long getContentLen() throws WebdavException;

    public abstract String getContentType() throws WebdavException;

    public abstract String getCreDate() throws WebdavException;

    public abstract String getDisplayname() throws WebdavException;

    public abstract String getEtagValue(boolean var1) throws WebdavException;

    public abstract String getLastmodDate() throws WebdavException;

    public abstract AccessPrincipal getOwner() throws WebdavException;

    public abstract WdCollection getCollection(boolean var1) throws WebdavException;

    public abstract WdCollection getImmediateTargetCollection() throws WebdavException;

    public abstract boolean allowsSyncReport() throws WebdavException;

    public abstract boolean getDeleted() throws WebdavException;

    public abstract String getSyncToken() throws WebdavException;

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(this.getClass());
        }
        return this.log;
    }

    protected void error(Throwable t) {
        this.getLogger().error(this, t);
    }

    protected void warn(String msg) {
        this.getLogger().warn(msg);
    }

    protected void debugMsg(String msg) {
        this.getLogger().debug(msg);
    }

    protected void logIt(String msg) {
        this.getLogger().info(msg);
    }

    protected static void addPropEntry(HashMap<QName, PropertyTagEntry> propertyNames, QName tag) {
        propertyNames.put(tag, new PropertyTagEntry(tag));
    }

    protected static void addPropEntry(HashMap<QName, PropertyTagEntry> propertyNames, QName tag, boolean inAllProp) {
        propertyNames.put(tag, new PropertyTagEntry(tag, inAllProp));
    }

    public int hashCode() {
        return this.uri.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WebdavNsNode)) {
            return false;
        }
        WebdavNsNode that = (WebdavNsNode)o;
        return this.uri.equals(that.uri);
    }

    static {
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.acl);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.addMember, false);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.creationdate, true);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.currentUserPrincipal, true);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.currentUserPrivilegeSet);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.displayname, true);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.getcontentlanguage, true);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.getcontentlength, true);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.getcontenttype, true);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.getetag, true);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.getlastmodified, true);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.owner);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.principalURL);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.resourcetype, true);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.supportedReportSet);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.supportedPrivilegeSet);
        WebdavNsNode.addPropEntry(propertyNames, WebdavTags.syncToken);
        supportedReports.add(WebdavTags.aclPrincipalPropSet);
        supportedReports.add(WebdavTags.principalMatch);
        supportedReports.add(WebdavTags.principalPropertySearch);
    }

    public static class PropVal {
        public boolean notFound;
        public String val;
    }

    public static class SetPropertyResult {
        public Element prop;
        public int status = 200;
        public String message;
        public QName rootElement;

        public SetPropertyResult(Element prop, QName rootElement) {
            this.prop = prop;
            this.rootElement = rootElement;
        }
    }

    public static class PropertyTagEntry {
        public QName tag;
        public boolean inPropAll = false;

        public PropertyTagEntry(QName tag) {
            this.tag = tag;
        }

        public PropertyTagEntry(QName tag, boolean inPropAll) {
            this.tag = tag;
            this.inPropAll = inPropAll;
        }
    }
}


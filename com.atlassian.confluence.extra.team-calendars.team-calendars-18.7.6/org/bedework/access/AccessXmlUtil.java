/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.bedework.access.AccessException;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Ace;
import org.bedework.access.AceWho;
import org.bedework.access.Acl;
import org.bedework.access.Privilege;
import org.bedework.access.PrivilegeDefs;
import org.bedework.access.PrivilegeSet;
import org.bedework.access.Privileges;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class AccessXmlUtil
implements Serializable {
    private transient Logger log;
    protected boolean debug;
    private XmlEmit xml;
    private QName[] privTags;
    public static final QName[] caldavPrivTags = new QName[]{WebdavTags.all, WebdavTags.read, WebdavTags.readAcl, WebdavTags.readCurrentUserPrivilegeSet, CaldavTags.readFreeBusy, WebdavTags.write, WebdavTags.writeAcl, WebdavTags.writeProperties, WebdavTags.writeContent, WebdavTags.bind, CaldavTags.schedule, CaldavTags.scheduleRequest, CaldavTags.scheduleReply, CaldavTags.scheduleFreeBusy, WebdavTags.unbind, WebdavTags.unlock, CaldavTags.scheduleDeliver, CaldavTags.scheduleDeliverInvite, CaldavTags.scheduleDeliverReply, CaldavTags.scheduleQueryFreebusy, CaldavTags.scheduleSend, CaldavTags.scheduleSendInvite, CaldavTags.scheduleSendReply, CaldavTags.scheduleSendFreebusy, null};
    private AccessXmlCb cb;

    public AccessXmlUtil(QName[] privTags, XmlEmit xml, AccessXmlCb cb) {
        if (privTags.length != PrivilegeDefs.privEncoding.length) {
            throw new RuntimeException("edu.rpi.cmt.access.BadParameter");
        }
        this.privTags = privTags;
        this.xml = xml;
        this.cb = cb;
        this.debug = this.getLogger().isDebugEnabled();
    }

    public static String getXmlAclString(Acl acl, boolean forWebDAV, QName[] privTags, AccessXmlCb cb) throws AccessException {
        try {
            XmlEmit xml = new XmlEmit(true);
            StringWriter su = new StringWriter();
            xml.startEmit(su);
            AccessXmlUtil au = new AccessXmlUtil(privTags, xml, cb);
            au.emitAcl(acl, forWebDAV);
            su.close();
            return su.toString();
        }
        catch (AccessException ae) {
            throw ae;
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    public void setXml(XmlEmit val) {
        this.xml = val;
    }

    public QName getErrorTag() throws AccessException {
        return this.cb.getErrorTag();
    }

    public String getErrorMsg() throws AccessException {
        return this.cb.getErrorMsg();
    }

    public Acl getAcl(String xmlStr, boolean setting) throws AccessException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return this.getAcl(doc.getDocumentElement(), setting);
        }
        catch (AccessException ae) {
            throw ae;
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    public Acl getAcl(Element root, boolean setting) throws AccessException {
        try {
            if (!XmlUtil.nodeMatches(root, WebdavTags.acl)) {
                throw this.exc("Expected ACL");
            }
            Element[] aceEls = XmlUtil.getElementsArray(root);
            ArrayList<ParsedAce> paces = new ArrayList<ParsedAce>();
            for (Element curnode : aceEls) {
                if (!XmlUtil.nodeMatches(curnode, WebdavTags.ace)) {
                    throw this.exc("Expected ACE");
                }
                ParsedAce pace = this.processAce(curnode, setting);
                if (pace == null) break;
                if (!this.debug || pace._protected) {
                    // empty if block
                }
                if (!this.debug || pace.inheritedFrom != null) {
                    // empty if block
                }
                for (ParsedAce pa : paces) {
                    if (!pa.ace.getWho().equals(pace.ace.getWho()) || pa.deny != pace.deny) continue;
                    throw this.exc("Multiple ACEs for " + pa.ace.getWho());
                }
                paces.add(pace);
            }
            ArrayList<Ace> aces = new ArrayList<Ace>();
            for (ParsedAce pa : paces) {
                if (!pa.deny) continue;
                aces.add(pa.ace);
            }
            for (ParsedAce pa : paces) {
                if (pa.deny) continue;
                aces.add(pa.ace);
            }
            return new Acl(aces);
        }
        catch (AccessException ae) {
            throw ae;
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new AccessException(t);
        }
    }

    public void emitAcl(Acl acl, boolean forWebDAV) throws AccessException {
        try {
            this.emitAces(acl.getAces(), forWebDAV);
        }
        catch (AccessException ae) {
            throw ae;
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    public void emitSupportedPrivSet() throws AccessException {
        try {
            this.xml.openTag(WebdavTags.supportedPrivilegeSet);
            this.emitSupportedPriv(Privileges.getPrivAll());
            this.xml.closeTag(WebdavTags.supportedPrivilegeSet);
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    public static void emitCurrentPrivSet(XmlEmit xml, QName[] privTags, char[] privileges) throws AccessException {
        if (privTags.length != PrivilegeDefs.privEncoding.length) {
            throw new AccessException("edu.rpi.cmt.access.BadParameter");
        }
        try {
            xml.openTag(WebdavTags.currentUserPrivilegeSet);
            for (int pi = 0; pi < privileges.length; ++pi) {
                QName pr;
                if (privileges[pi] != 'y' && privileges[pi] != 'Y' || (pr = privTags[pi]) == null) continue;
                xml.propertyTagVal(WebdavTags.privilege, pr);
            }
            xml.closeTag(WebdavTags.currentUserPrivilegeSet);
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    public static String getCurrentPrivSetString(QName[] privTags, PrivilegeSet ps) throws AccessException {
        try {
            char[] privileges = ps.getPrivileges();
            XmlEmit xml = new XmlEmit(true);
            StringWriter su = new StringWriter();
            xml.startEmit(su);
            AccessXmlUtil.emitCurrentPrivSet(xml, privTags, privileges);
            su.close();
            return su.toString();
        }
        catch (AccessException ae) {
            throw ae;
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(this.getClass());
        }
        return this.log;
    }

    protected void debugMsg(String msg) {
        this.getLogger().debug(msg);
    }

    private ParsedAce processAce(Node nd, boolean setting) throws Throwable {
        Privs privs;
        AceWho awho;
        Element[] children = XmlUtil.getElementsArray(nd);
        int pos = 0;
        if (children.length < 2) {
            throw this.exc("Bad ACE");
        }
        Element curnode = children[pos];
        boolean inverted = false;
        boolean _protected = false;
        String inheritedFrom = null;
        if (XmlUtil.nodeMatches(curnode, WebdavTags.invert)) {
            inverted = true;
            curnode = XmlUtil.getOnlyElement(curnode);
        }
        if ((awho = this.parseAcePrincipal(curnode, inverted)) == null) {
            return null;
        }
        if ((privs = this.parseGrantDeny(curnode = children[++pos])) == null) {
            if (this.debug) {
                this.debugMsg("Expected grant | deny");
            }
            this.cb.setErrorTag(WebdavTags.noAceConflict);
            return null;
        }
        if (++pos < children.length && XmlUtil.nodeMatches(curnode = children[pos], WebdavTags._protected)) {
            if (setting) {
                if (this.debug) {
                    this.debugMsg("protected element when setting acls.");
                }
                this.cb.setErrorTag(WebdavTags.noAceConflict);
                return null;
            }
            _protected = true;
            ++pos;
        }
        if (pos < children.length && XmlUtil.nodeMatches(curnode = children[pos], WebdavTags.inherited)) {
            if (setting) {
                if (this.debug) {
                    this.debugMsg("inherited element when setting acls.");
                }
                this.cb.setErrorTag(WebdavTags.noAceConflict);
                return null;
            }
            if (!XmlUtil.nodeMatches(curnode = XmlUtil.getOnlyElement(curnode), WebdavTags.href)) {
                throw this.exc("Missing inherited href");
            }
            String href = XmlUtil.getElementContent(curnode);
            if (href == null || href.length() == 0) {
                throw this.exc("Missing inherited href");
            }
            inheritedFrom = href;
            ++pos;
        }
        if (pos < children.length) {
            throw this.exc("Unexpected element " + children[pos]);
        }
        return new ParsedAce(Ace.makeAce(awho, privs.privs, inheritedFrom), privs.deny, _protected, inheritedFrom);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private AceWho parseAcePrincipal(Node nd, boolean inverted) throws Throwable {
        if (!XmlUtil.nodeMatches(nd, WebdavTags.principal)) {
            throw this.exc("Bad ACE - expect principal");
        }
        Element el = XmlUtil.getOnlyElement(nd);
        int whoType = -1;
        String who = null;
        if (XmlUtil.nodeMatches(el, WebdavTags.href)) {
            String href = XmlUtil.getElementContent(el);
            if (href == null || href.length() == 0) {
                throw this.exc("Missing href");
            }
            AccessPrincipal ap = this.cb.getPrincipal(href);
            if (ap == null) {
                this.cb.setErrorTag(WebdavTags.recognizedPrincipal);
                this.cb.setErrorMsg(href);
                return null;
            }
            whoType = ap.getKind();
            who = ap.getAclAccount();
        } else if (XmlUtil.nodeMatches(el, WebdavTags.all)) {
            whoType = 10;
        } else if (XmlUtil.nodeMatches(el, WebdavTags.authenticated)) {
            whoType = 8;
        } else if (XmlUtil.nodeMatches(el, WebdavTags.unauthenticated)) {
            whoType = 7;
        } else if (XmlUtil.nodeMatches(el, WebdavTags.property)) {
            if (!XmlUtil.nodeMatches(el = XmlUtil.getOnlyElement(el), WebdavTags.owner)) throw this.exc("Bad WHO property");
            whoType = 0;
        } else {
            if (!XmlUtil.nodeMatches(el, WebdavTags.self)) throw this.exc("Bad WHO");
            whoType = this.cb.getPrincipal().getKind();
            who = this.cb.getPrincipal().getAccount();
        }
        AceWho awho = AceWho.getAceWho(who, whoType, inverted);
        if (!this.debug) return awho;
        this.debugMsg("Parsed ace/principal =" + awho);
        return awho;
    }

    private Privs parseGrantDeny(Node nd) throws Throwable {
        boolean denial = false;
        if (XmlUtil.nodeMatches(nd, WebdavTags.deny)) {
            denial = true;
        } else if (!XmlUtil.nodeMatches(nd, WebdavTags.grant)) {
            return null;
        }
        ArrayList<Privilege> privs = new ArrayList<Privilege>();
        Element[] pchildren = XmlUtil.getElementsArray(nd);
        for (int pi = 0; pi < pchildren.length; ++pi) {
            Element pnode = pchildren[pi];
            if (!XmlUtil.nodeMatches(pnode, WebdavTags.privilege)) {
                throw this.exc("Bad ACE - expect privilege");
            }
            privs.add(this.parsePrivilege(pnode, denial));
        }
        return new Privs(privs, denial);
    }

    private Privilege parsePrivilege(Node nd, boolean denial) throws Throwable {
        int priv;
        block3: {
            Element el = XmlUtil.getOnlyElement(nd);
            for (priv = 0; priv < this.privTags.length; ++priv) {
                if (!XmlUtil.nodeMatches(el, this.privTags[priv])) {
                    continue;
                }
                break block3;
            }
            throw this.exc("Bad privilege");
        }
        if (this.debug) {
            this.debugMsg("Add priv " + priv + " denied=" + denial);
        }
        return Privileges.makePriv(priv, denial);
    }

    private void emitAces(Collection<Ace> aces, boolean forWebDAV) throws AccessException {
        try {
            this.xml.openTag(WebdavTags.acl);
            if (aces != null) {
                for (Ace ace : aces) {
                    boolean aceOpen = this.emitAce(ace, false, false);
                    if (aceOpen && forWebDAV) {
                        this.closeAce(ace);
                        aceOpen = false;
                    }
                    if (this.emitAce(ace, true, aceOpen)) {
                        aceOpen = true;
                    }
                    if (!aceOpen) continue;
                    this.closeAce(ace);
                }
            }
            this.xml.closeTag(WebdavTags.acl);
        }
        catch (AccessException ae) {
            throw ae;
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    private void closeAce(Ace ace) throws Throwable {
        if (ace.getInheritedFrom() != null) {
            QName tag = WebdavTags.inherited;
            this.xml.openTag(tag);
            this.xml.property(WebdavTags.href, ace.getInheritedFrom());
            this.xml.closeTag(tag);
        }
        this.xml.closeTag(WebdavTags.ace);
    }

    private void emitSupportedPriv(Privilege priv) throws Throwable {
        this.xml.openTag(WebdavTags.supportedPrivilege);
        this.xml.openTagNoNewline(WebdavTags.privilege);
        this.xml.emptyTagSameLine(this.privTags[priv.getIndex()]);
        this.xml.closeTagNoblanks(WebdavTags.privilege);
        if (priv.getAbstractPriv()) {
            this.xml.emptyTag(WebdavTags._abstract);
        }
        this.xml.property(WebdavTags.description, priv.getDescription());
        for (Privilege p : priv.getContainedPrivileges()) {
            this.emitSupportedPriv(p);
        }
        this.xml.closeTag(WebdavTags.supportedPrivilege);
    }

    private boolean emitAce(Ace ace, boolean denials, boolean aceOpen) throws Throwable {
        boolean tagOpen = false;
        QName tag = denials ? WebdavTags.deny : WebdavTags.grant;
        for (Privilege p : ace.getPrivs()) {
            if (denials != p.getDenial()) continue;
            if (!aceOpen) {
                this.xml.openTag(WebdavTags.ace);
                this.emitAceWho(ace.getWho());
                aceOpen = true;
            }
            if (!tagOpen) {
                this.xml.openTag(tag);
                tagOpen = true;
            }
            this.xml.propertyTagVal(WebdavTags.privilege, this.privTags[p.getIndex()]);
        }
        if (tagOpen) {
            this.xml.closeTag(tag);
        }
        return aceOpen;
    }

    private void emitAceWho(AceWho who) throws Throwable {
        boolean invert = who.getNotWho();
        if (who.getWhoType() == 9) {
            boolean bl = invert = !invert;
        }
        if (invert) {
            this.xml.openTag(WebdavTags.invert);
        }
        this.xml.openTag(WebdavTags.principal);
        int whoType = who.getWhoType();
        if (whoType == 0 || whoType == 9) {
            this.xml.openTag(WebdavTags.property);
            this.xml.emptyTag(WebdavTags.owner);
            this.xml.closeTag(WebdavTags.property);
        } else if (whoType == 7) {
            this.xml.emptyTag(WebdavTags.unauthenticated);
        } else if (whoType == 8) {
            this.xml.emptyTag(WebdavTags.authenticated);
        } else if (whoType == 10) {
            this.xml.emptyTag(WebdavTags.all);
        } else {
            String href = AccessXmlUtil.escapeChars(this.cb.makeHref(who.getWho(), whoType));
            this.xml.property(WebdavTags.href, href);
        }
        this.xml.closeTag(WebdavTags.principal);
        if (invert) {
            this.xml.closeTag(WebdavTags.invert);
        }
    }

    public static String escapeChars(String value) {
        if (value == null || value.length() == 0) {
            return value;
        }
        StringBuffer result = null;
        String filtered = null;
        for (int i = 0; i < value.length(); ++i) {
            filtered = null;
            switch (value.charAt(i)) {
                case '<': {
                    filtered = "&lt;";
                    break;
                }
                case '>': {
                    filtered = "&gt;";
                    break;
                }
                case '&': {
                    filtered = "&amp;";
                    break;
                }
                case '\"': {
                    filtered = "&quot;";
                    break;
                }
                case '\'': {
                    filtered = "&#39;";
                }
            }
            if (result == null) {
                if (filtered == null) continue;
                result = new StringBuffer(value.length() + 50);
                if (i > 0) {
                    result.append(value.substring(0, i));
                }
                result.append(filtered);
                continue;
            }
            if (filtered == null) {
                result.append(value.charAt(i));
                continue;
            }
            result.append(filtered);
        }
        if (result == null) {
            return value;
        }
        return result.toString();
    }

    private AccessException exc(String msg) {
        if (this.debug) {
            this.debugMsg(msg);
        }
        return AccessException.badXmlACL(msg);
    }

    private static class Privs {
        Collection<Privilege> privs;
        boolean deny;

        Privs(Collection<Privilege> privs, boolean deny) {
            this.privs = privs;
            this.deny = deny;
        }
    }

    private static class ParsedAce {
        Ace ace;
        boolean deny;
        boolean _protected;
        String inheritedFrom;

        ParsedAce(Ace ace, boolean deny, boolean _protected, String inheritedFrom) {
            this.ace = ace;
            this.deny = deny;
            this._protected = _protected;
            this.inheritedFrom = inheritedFrom;
        }
    }

    public static interface AccessXmlCb {
        public String makeHref(String var1, int var2) throws AccessException;

        public AccessPrincipal getPrincipal() throws AccessException;

        public AccessPrincipal getPrincipal(String var1) throws AccessException;

        public void setErrorTag(QName var1) throws AccessException;

        public QName getErrorTag() throws AccessException;

        public void setErrorMsg(String var1) throws AccessException;

        public String getErrorMsg() throws AccessException;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security;

import java.util.ArrayList;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.security.Privilege;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SupportedPrivilege
implements XmlSerializable {
    private static final String XML_SUPPORTED_PRIVILEGE = "supported-privilege";
    private static final String XML_ABSTRACT = "abstract";
    private static final String XML_DESCRIPTION = "description";
    private final Privilege privilege;
    private final boolean isAbstract;
    private final String description;
    private final String descriptionLanguage;
    private final SupportedPrivilege[] supportedPrivileges;

    public SupportedPrivilege(Privilege privilege, String description, String descriptionLanguage, boolean isAbstract, SupportedPrivilege[] supportedPrivileges) {
        if (privilege == null) {
            throw new IllegalArgumentException("DAV:supported-privilege element must contain a single privilege.");
        }
        this.privilege = privilege;
        this.description = description;
        this.descriptionLanguage = descriptionLanguage;
        this.isAbstract = isAbstract;
        this.supportedPrivileges = supportedPrivileges;
    }

    @Override
    public Element toXml(Document document) {
        Element spElem = DomUtil.createElement(document, XML_SUPPORTED_PRIVILEGE, SecurityConstants.NAMESPACE);
        spElem.appendChild(this.privilege.toXml(document));
        if (this.isAbstract) {
            DomUtil.addChildElement(spElem, XML_ABSTRACT, SecurityConstants.NAMESPACE);
        }
        if (this.description != null) {
            Element desc = DomUtil.addChildElement(spElem, XML_DESCRIPTION, SecurityConstants.NAMESPACE, this.description);
            if (this.descriptionLanguage != null) {
                DomUtil.setAttribute(desc, "lang", Namespace.XML_NAMESPACE, this.descriptionLanguage);
            }
        }
        if (this.supportedPrivileges != null) {
            for (SupportedPrivilege supportedPrivilege : this.supportedPrivileges) {
                spElem.appendChild(supportedPrivilege.toXml(document));
            }
        }
        return spElem;
    }

    public Privilege getPrivilege() {
        return this.privilege;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public SupportedPrivilege[] getSupportedPrivileges() {
        return this.supportedPrivileges;
    }

    static SupportedPrivilege getSupportedPrivilege(Element supportedPrivilege) throws DavException {
        if (!DomUtil.matches(supportedPrivilege, XML_SUPPORTED_PRIVILEGE, SecurityConstants.NAMESPACE)) {
            throw new DavException(400, "DAV:supported-privilege element expected.");
        }
        boolean isAbstract = false;
        Privilege privilege = null;
        String description = null;
        String descriptionLanguage = null;
        ArrayList<SupportedPrivilege> sp = new ArrayList<SupportedPrivilege>();
        ElementIterator children = DomUtil.getChildren(supportedPrivilege);
        while (children.hasNext()) {
            Element child = children.next();
            if (child.getLocalName().equals(XML_ABSTRACT)) {
                isAbstract = true;
                continue;
            }
            if (child.getLocalName().equals("privilege")) {
                privilege = Privilege.getPrivilege(child);
                continue;
            }
            if (child.getLocalName().equals(XML_DESCRIPTION)) {
                description = child.getLocalName();
                if (!child.hasAttribute(descriptionLanguage)) continue;
                descriptionLanguage = child.getAttribute(descriptionLanguage);
                continue;
            }
            if (!child.getLocalName().equals(XML_SUPPORTED_PRIVILEGE)) continue;
            sp.add(SupportedPrivilege.getSupportedPrivilege(child));
        }
        return new SupportedPrivilege(privilege, description, descriptionLanguage, isAbstract, sp.toArray(new SupportedPrivilege[sp.size()]));
    }
}


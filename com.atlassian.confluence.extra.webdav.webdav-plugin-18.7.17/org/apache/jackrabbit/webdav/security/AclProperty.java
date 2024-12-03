/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.security.AclResource;
import org.apache.jackrabbit.webdav.security.Principal;
import org.apache.jackrabbit.webdav.security.Privilege;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AclProperty
extends AbstractDavProperty<List<Ace>> {
    private final List<Ace> aces;

    public AclProperty(Ace[] accessControlElements) {
        this(accessControlElements == null ? new ArrayList() : Arrays.asList(accessControlElements));
    }

    private AclProperty(List<Ace> aces) {
        super(SecurityConstants.ACL, true);
        this.aces = aces;
    }

    @Override
    public List<Ace> getValue() {
        return this.aces;
    }

    public static AclProperty createFromXml(Element aclElement) throws DavException {
        if (!DomUtil.matches(aclElement, SecurityConstants.ACL.getName(), SecurityConstants.ACL.getNamespace())) {
            throw new DavException(400, "ACL request requires a DAV:acl body.");
        }
        ArrayList<Ace> aces = new ArrayList<Ace>();
        ElementIterator it = DomUtil.getChildren(aclElement, "ace", SecurityConstants.NAMESPACE);
        while (it.hasNext()) {
            Element aceElem = it.nextElement();
            aces.add(Ace.createFromXml(aceElem));
        }
        return new AclProperty(aces);
    }

    public static Ace createGrantAce(Principal principal, Privilege[] privileges, boolean invert, boolean isProtected, AclResource inheritedFrom) {
        return new Ace(principal, invert, privileges, true, isProtected, inheritedFrom);
    }

    public static Ace createDenyAce(Principal principal, Privilege[] privileges, boolean invert, boolean isProtected, AclResource inheritedFrom) {
        return new Ace(principal, invert, privileges, false, isProtected, inheritedFrom);
    }

    public static class Ace
    implements XmlSerializable,
    SecurityConstants {
        private static final String XML_ACE = "ace";
        private static final String XML_INVERT = "invert";
        private static final String XML_GRANT = "grant";
        private static final String XML_DENY = "deny";
        private static final String XML_PROTECTED = "protected";
        private static final String XML_INHERITED = "inherited";
        private final Principal principal;
        private final boolean invert;
        private final Privilege[] privileges;
        private final boolean grant;
        private final boolean isProtected;
        private final String inheritedHref;

        private Ace(Principal principal, boolean invert, Privilege[] privileges, boolean grant, boolean isProtected, AclResource inherited) {
            this(principal, invert, privileges, grant, isProtected, inherited != null ? inherited.getHref() : null);
        }

        private Ace(Principal principal, boolean invert, Privilege[] privileges, boolean grant, boolean isProtected, String inheritedHref) {
            if (principal == null) {
                throw new IllegalArgumentException("Cannot create a new ACE with 'null' principal.");
            }
            if (privileges == null || privileges.length == 0) {
                throw new IllegalArgumentException("Cannot create a new ACE: at least a single privilege must be specified.");
            }
            this.principal = principal;
            this.invert = invert;
            this.privileges = privileges;
            this.grant = grant;
            this.isProtected = isProtected;
            this.inheritedHref = inheritedHref;
        }

        public Principal getPrincipal() {
            return this.principal;
        }

        public boolean isInvert() {
            return this.invert;
        }

        public Privilege[] getPrivileges() {
            return this.privileges;
        }

        public boolean isGrant() {
            return this.grant;
        }

        public boolean isDeny() {
            return !this.grant;
        }

        public boolean isProtected() {
            return this.isProtected;
        }

        public String getInheritedHref() {
            return this.inheritedHref;
        }

        @Override
        public Element toXml(Document document) {
            Element ace = DomUtil.createElement(document, XML_ACE, SecurityConstants.NAMESPACE);
            if (this.invert) {
                Element inv = DomUtil.addChildElement(ace, XML_INVERT, SecurityConstants.NAMESPACE);
                inv.appendChild(this.principal.toXml(document));
            } else {
                ace.appendChild(this.principal.toXml(document));
            }
            Element gd = DomUtil.addChildElement(ace, this.grant ? XML_GRANT : XML_DENY, SecurityConstants.NAMESPACE);
            for (Privilege privilege : this.privileges) {
                gd.appendChild(privilege.toXml(document));
            }
            if (this.isProtected) {
                DomUtil.addChildElement(ace, XML_PROTECTED, SecurityConstants.NAMESPACE);
            }
            if (this.inheritedHref != null) {
                Element inh = DomUtil.addChildElement(ace, XML_INHERITED, SecurityConstants.NAMESPACE);
                inh.appendChild(DomUtil.hrefToXml(this.inheritedHref, document));
            }
            return ace;
        }

        private static Ace createFromXml(Element aceElement) throws DavException {
            Element pe;
            boolean invert = DomUtil.hasChildElement(aceElement, XML_INVERT, NAMESPACE);
            if (invert) {
                Element invertE = DomUtil.getChildElement(aceElement, XML_INVERT, NAMESPACE);
                pe = DomUtil.getChildElement(invertE, "principal", NAMESPACE);
            } else {
                pe = DomUtil.getChildElement(aceElement, "principal", SecurityConstants.NAMESPACE);
            }
            Principal principal = Principal.createFromXml(pe);
            boolean grant = DomUtil.hasChildElement(aceElement, XML_GRANT, SecurityConstants.NAMESPACE);
            Element gdElem = grant ? DomUtil.getChildElement(aceElement, XML_GRANT, NAMESPACE) : DomUtil.getChildElement(aceElement, XML_DENY, NAMESPACE);
            ArrayList<Privilege> privilegeList = new ArrayList<Privilege>();
            ElementIterator privIt = DomUtil.getChildren(gdElem, "privilege", NAMESPACE);
            while (privIt.hasNext()) {
                Privilege pv = Privilege.getPrivilege(privIt.nextElement());
                privilegeList.add(pv);
            }
            Privilege[] privileges = privilegeList.toArray(new Privilege[privilegeList.size()]);
            boolean isProtected = DomUtil.hasChildElement(aceElement, XML_PROTECTED, NAMESPACE);
            String inheritedHref = null;
            if (DomUtil.hasChildElement(aceElement, XML_INHERITED, NAMESPACE)) {
                Element inhE = DomUtil.getChildElement(aceElement, XML_INHERITED, NAMESPACE);
                inheritedHref = DomUtil.getChildText(inhE, "href", DavConstants.NAMESPACE);
            }
            return new Ace(principal, invert, privileges, grant, isProtected, inheritedHref);
        }
    }
}


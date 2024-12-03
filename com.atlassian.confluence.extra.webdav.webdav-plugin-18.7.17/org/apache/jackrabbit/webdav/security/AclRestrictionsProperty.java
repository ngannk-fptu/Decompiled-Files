/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security;

import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.security.Principal;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AclRestrictionsProperty
extends AbstractDavProperty {
    private static final String XML_GRANT_ONLY = "grant-only";
    private static final String XML_NO_INVERT = "no-invert";
    private static final String XML_DENY_BEFORE_GRANT = "deny-before-grant";
    private final boolean grantOnly;
    private final boolean noInvert;
    private final boolean denyBeforeGrant;
    private final Principal requiredPrincipal;

    public AclRestrictionsProperty(boolean grantOnly, boolean noInvert, boolean denyBeforeGrant, Principal requiredPrincipal) {
        super(SecurityConstants.ACL_RESTRICTIONS, true);
        this.grantOnly = grantOnly;
        this.noInvert = noInvert;
        this.denyBeforeGrant = denyBeforeGrant;
        this.requiredPrincipal = requiredPrincipal;
    }

    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("Not implemented. Use the property specific methods instead.");
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        if (this.grantOnly) {
            DomUtil.addChildElement(elem, XML_GRANT_ONLY, SecurityConstants.NAMESPACE);
        }
        if (this.noInvert) {
            DomUtil.addChildElement(elem, XML_NO_INVERT, SecurityConstants.NAMESPACE);
        }
        if (this.denyBeforeGrant) {
            DomUtil.addChildElement(elem, XML_DENY_BEFORE_GRANT, SecurityConstants.NAMESPACE);
        }
        if (this.requiredPrincipal != null) {
            elem.appendChild(this.requiredPrincipal.toXml(document));
        }
        return elem;
    }

    public boolean isGrantOnly() {
        return this.grantOnly;
    }

    public boolean isNoInvert() {
        return this.noInvert;
    }

    public boolean isDenyBeforeGrant() {
        return this.denyBeforeGrant;
    }

    public Principal getRequiredPrincipal() {
        return this.requiredPrincipal;
    }
}


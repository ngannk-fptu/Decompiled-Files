/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security;

import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Principal
implements XmlSerializable,
SecurityConstants {
    public static final String XML_PRINCIPAL = "principal";
    private static final String XML_ALL = "all";
    private static final String XML_AUTHENTICATED = "authenticated";
    private static final String XML_UNAUTHENTICATED = "unauthenticated";
    private static final String XML_SELF = "self";
    private static final String XML_PROPERTY = "property";
    private static final int TYPE_ALL = 0;
    private static final int TYPE_AUTHENTICATED = 1;
    private static final int TYPE_UNAUTHENTICATED = 2;
    private static final int TYPE_SELF = 3;
    private static final int TYPE_PROPERTY = 4;
    private static final int TYPE_HREF = 5;
    private static final Principal ALL_PRINCIPAL = new Principal(0);
    private static final Principal AUTHENTICATED_PRINCIPAL = new Principal(1);
    private static final Principal UNAUTHENTICATED_PRINCIPAL = new Principal(2);
    private static final Principal SELF_PRINCIPAL = new Principal(3);
    private static final Map<DavPropertyName, Principal> PROP_PRINCIPALS = new HashMap<DavPropertyName, Principal>();
    private final int type;
    private DavPropertyName propertyName;
    private String href;

    private Principal(int type) {
        this.type = type;
    }

    private Principal(DavPropertyName propertyName) {
        this.type = 4;
        this.propertyName = propertyName;
    }

    private Principal(String href) {
        this.type = 5;
        this.href = href;
    }

    public String getHref() {
        return this.href;
    }

    public DavPropertyName getPropertyName() {
        return this.propertyName;
    }

    @Override
    public Element toXml(Document document) {
        Element pEl = DomUtil.createElement(document, XML_PRINCIPAL, NAMESPACE);
        switch (this.type) {
            case 0: {
                DomUtil.addChildElement(pEl, XML_ALL, NAMESPACE);
                break;
            }
            case 1: {
                DomUtil.addChildElement(pEl, XML_AUTHENTICATED, NAMESPACE);
                break;
            }
            case 2: {
                DomUtil.addChildElement(pEl, XML_UNAUTHENTICATED, NAMESPACE);
                break;
            }
            case 3: {
                DomUtil.addChildElement(pEl, XML_SELF, NAMESPACE);
                break;
            }
            case 4: {
                Element prop = DomUtil.addChildElement(pEl, XML_PROPERTY, NAMESPACE);
                prop.appendChild(this.propertyName.toXml(document));
                break;
            }
            case 5: {
                Element hrefEl = DomUtil.hrefToXml(this.href, document);
                pEl.appendChild(hrefEl);
            }
        }
        return pEl;
    }

    public static Principal getAllPrincipal() {
        return ALL_PRINCIPAL;
    }

    public static Principal getAuthenticatedPrincipal() {
        return AUTHENTICATED_PRINCIPAL;
    }

    public static Principal getUnauthenticatedPrincipal() {
        return UNAUTHENTICATED_PRINCIPAL;
    }

    public static Principal getSelfPrincipal() {
        return SELF_PRINCIPAL;
    }

    public static Principal getPropertyPrincipal(DavPropertyName propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("Property-Principal must contain a valid property name.");
        }
        if (PROP_PRINCIPALS.containsKey(propertyName)) {
            return PROP_PRINCIPALS.get(propertyName);
        }
        Principal p = new Principal(propertyName);
        PROP_PRINCIPALS.put(propertyName, p);
        return p;
    }

    public static Principal getHrefPrincipal(String href) {
        if (href == null) {
            throw new IllegalArgumentException("Href-Principal must contain a valid href.");
        }
        return new Principal(href);
    }

    public static Principal createFromXml(Element principalElement) throws DavException {
        if (!DomUtil.matches(principalElement, XML_PRINCIPAL, NAMESPACE)) {
            throw new DavException(400, "DAV:principal element expected.");
        }
        if (DomUtil.hasChildElement(principalElement, XML_ALL, NAMESPACE)) {
            return ALL_PRINCIPAL;
        }
        if (DomUtil.hasChildElement(principalElement, XML_SELF, NAMESPACE)) {
            return SELF_PRINCIPAL;
        }
        if (DomUtil.hasChildElement(principalElement, XML_AUTHENTICATED, NAMESPACE)) {
            return AUTHENTICATED_PRINCIPAL;
        }
        if (DomUtil.hasChildElement(principalElement, XML_UNAUTHENTICATED, NAMESPACE)) {
            return UNAUTHENTICATED_PRINCIPAL;
        }
        if (DomUtil.hasChildElement(principalElement, "href", DavConstants.NAMESPACE)) {
            String href = DomUtil.getChildText(principalElement, "href", DavConstants.NAMESPACE);
            return Principal.getHrefPrincipal(href);
        }
        if (DomUtil.hasChildElement(principalElement, XML_PROPERTY, NAMESPACE)) {
            Element propEl = DomUtil.getChildElement(principalElement, XML_PROPERTY, NAMESPACE);
            DavPropertyName pn = DavPropertyName.createFromXml(DomUtil.getFirstChildElement(propEl));
            return Principal.getPropertyPrincipal(pn);
        }
        throw new DavException(400, "Invalid structure inside DAV:principal element.");
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel.wspolicy;

import com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public enum NamespaceVersion {
    v1_2("http://schemas.xmlsoap.org/ws/2004/09/policy", "wsp1_2", XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, XmlToken.Digest, XmlToken.DigestAlgorithm),
    v1_5("http://www.w3.org/ns/ws-policy", "wsp", XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, XmlToken.Digest, XmlToken.DigestAlgorithm);

    private final String nsUri;
    private final String defaultNsPrefix;
    private final Map<XmlToken, QName> tokenToQNameCache;

    public static NamespaceVersion resolveVersion(String uri) {
        for (NamespaceVersion namespaceVersion : NamespaceVersion.values()) {
            if (!namespaceVersion.toString().equalsIgnoreCase(uri)) continue;
            return namespaceVersion;
        }
        return null;
    }

    public static NamespaceVersion resolveVersion(QName name) {
        return NamespaceVersion.resolveVersion(name.getNamespaceURI());
    }

    public static NamespaceVersion getLatestVersion() {
        return v1_5;
    }

    public static XmlToken resolveAsToken(QName name) {
        XmlToken token;
        NamespaceVersion nsVersion = NamespaceVersion.resolveVersion(name);
        if (nsVersion != null && nsVersion.tokenToQNameCache.containsKey((Object)(token = XmlToken.resolveToken(name.getLocalPart())))) {
            return token;
        }
        return XmlToken.UNKNOWN;
    }

    private NamespaceVersion(String uri, String prefix, XmlToken ... supportedTokens) {
        this.nsUri = uri;
        this.defaultNsPrefix = prefix;
        HashMap<XmlToken, QName> temp = new HashMap<XmlToken, QName>();
        for (XmlToken token : supportedTokens) {
            temp.put(token, new QName(this.nsUri, token.toString()));
        }
        this.tokenToQNameCache = Collections.unmodifiableMap(temp);
    }

    public String getDefaultNamespacePrefix() {
        return this.defaultNsPrefix;
    }

    public QName asQName(XmlToken token) throws IllegalArgumentException {
        return this.tokenToQNameCache.get((Object)token);
    }

    public String toString() {
        return this.nsUri;
    }
}


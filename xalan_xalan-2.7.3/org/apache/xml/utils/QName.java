/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.io.Serializable;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.NameSpace;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.XML11Char;
import org.w3c.dom.Element;

public class QName
implements Serializable {
    static final long serialVersionUID = 467434581652829920L;
    protected String _localName;
    protected String _namespaceURI;
    protected String _prefix;
    public static final String S_XMLNAMESPACEURI = "http://www.w3.org/XML/1998/namespace";
    private int m_hashCode;

    public QName() {
    }

    public QName(String namespaceURI, String localName) {
        this(namespaceURI, localName, false);
    }

    public QName(String namespaceURI, String localName, boolean validate) {
        if (localName == null) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null));
        }
        if (validate && !XML11Char.isXML11ValidNCName(localName)) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this._namespaceURI = namespaceURI;
        this._localName = localName;
        this.m_hashCode = this.toString().hashCode();
    }

    public QName(String namespaceURI, String prefix, String localName) {
        this(namespaceURI, prefix, localName, false);
    }

    public QName(String namespaceURI, String prefix, String localName, boolean validate) {
        if (localName == null) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null));
        }
        if (validate) {
            if (!XML11Char.isXML11ValidNCName(localName)) {
                throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
            }
            if (null != prefix && !XML11Char.isXML11ValidNCName(prefix)) {
                throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_PREFIX_INVALID", null));
            }
        }
        this._namespaceURI = namespaceURI;
        this._prefix = prefix;
        this._localName = localName;
        this.m_hashCode = this.toString().hashCode();
    }

    public QName(String localName) {
        this(localName, false);
    }

    public QName(String localName, boolean validate) {
        if (localName == null) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null));
        }
        if (validate && !XML11Char.isXML11ValidNCName(localName)) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this._namespaceURI = null;
        this._localName = localName;
        this.m_hashCode = this.toString().hashCode();
    }

    public QName(String qname, Stack namespaces) {
        this(qname, namespaces, false);
    }

    public QName(String qname, Stack namespaces, boolean validate) {
        String namespace = null;
        String prefix = null;
        int indexOfNSSep = qname.indexOf(58);
        if (indexOfNSSep > 0) {
            prefix = qname.substring(0, indexOfNSSep);
            if (prefix.equals("xml")) {
                namespace = S_XMLNAMESPACEURI;
            } else {
                if (prefix.equals("xmlns")) {
                    return;
                }
                int depth = namespaces.size();
                block0: for (int i = depth - 1; i >= 0; --i) {
                    NameSpace ns = (NameSpace)namespaces.elementAt(i);
                    while (null != ns) {
                        if (null != ns.m_prefix && prefix.equals(ns.m_prefix)) {
                            namespace = ns.m_uri;
                            i = -1;
                            continue block0;
                        }
                        ns = ns.m_next;
                    }
                }
            }
            if (null == namespace) {
                throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[]{prefix}));
            }
        }
        String string = this._localName = indexOfNSSep < 0 ? qname : qname.substring(indexOfNSSep + 1);
        if (validate && (this._localName == null || !XML11Char.isXML11ValidNCName(this._localName))) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this._namespaceURI = namespace;
        this._prefix = prefix;
        this.m_hashCode = this.toString().hashCode();
    }

    public QName(String qname, Element namespaceContext, PrefixResolver resolver) {
        this(qname, namespaceContext, resolver, false);
    }

    public QName(String qname, Element namespaceContext, PrefixResolver resolver, boolean validate) {
        this._namespaceURI = null;
        int indexOfNSSep = qname.indexOf(58);
        if (indexOfNSSep > 0 && null != namespaceContext) {
            String prefix;
            this._prefix = prefix = qname.substring(0, indexOfNSSep);
            if (prefix.equals("xml")) {
                this._namespaceURI = S_XMLNAMESPACEURI;
            } else {
                if (prefix.equals("xmlns")) {
                    return;
                }
                this._namespaceURI = resolver.getNamespaceForPrefix(prefix, namespaceContext);
            }
            if (null == this._namespaceURI) {
                throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[]{prefix}));
            }
        }
        String string = this._localName = indexOfNSSep < 0 ? qname : qname.substring(indexOfNSSep + 1);
        if (validate && (this._localName == null || !XML11Char.isXML11ValidNCName(this._localName))) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this.m_hashCode = this.toString().hashCode();
    }

    public QName(String qname, PrefixResolver resolver) {
        this(qname, resolver, false);
    }

    public QName(String qname, PrefixResolver resolver, boolean validate) {
        String prefix = null;
        this._namespaceURI = null;
        int indexOfNSSep = qname.indexOf(58);
        if (indexOfNSSep > 0) {
            prefix = qname.substring(0, indexOfNSSep);
            this._namespaceURI = prefix.equals("xml") ? S_XMLNAMESPACEURI : resolver.getNamespaceForPrefix(prefix);
            if (null == this._namespaceURI) {
                throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[]{prefix}));
            }
            this._localName = qname.substring(indexOfNSSep + 1);
        } else {
            if (indexOfNSSep == 0) {
                throw new RuntimeException(XMLMessages.createXMLMessage("ER_NAME_CANT_START_WITH_COLON", null));
            }
            this._localName = qname;
        }
        if (validate && (this._localName == null || !XML11Char.isXML11ValidNCName(this._localName))) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this.m_hashCode = this.toString().hashCode();
        this._prefix = prefix;
    }

    public String getNamespaceURI() {
        return this._namespaceURI;
    }

    public String getPrefix() {
        return this._prefix;
    }

    public String getLocalName() {
        return this._localName;
    }

    public String toString() {
        return this._prefix != null ? this._prefix + ":" + this._localName : (this._namespaceURI != null ? "{" + this._namespaceURI + "}" + this._localName : this._localName);
    }

    public String toNamespacedString() {
        return this._namespaceURI != null ? "{" + this._namespaceURI + "}" + this._localName : this._localName;
    }

    public String getNamespace() {
        return this.getNamespaceURI();
    }

    public String getLocalPart() {
        return this.getLocalName();
    }

    public int hashCode() {
        return this.m_hashCode;
    }

    public boolean equals(String ns, String localPart) {
        String thisnamespace = this.getNamespaceURI();
        return this.getLocalName().equals(localPart) && (null != thisnamespace && null != ns ? thisnamespace.equals(ns) : null == thisnamespace && null == ns);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QName) {
            QName qname = (QName)object;
            String thisnamespace = this.getNamespaceURI();
            String thatnamespace = qname.getNamespaceURI();
            return this.getLocalName().equals(qname.getLocalName()) && (null != thisnamespace && null != thatnamespace ? thisnamespace.equals(thatnamespace) : null == thisnamespace && null == thatnamespace);
        }
        return false;
    }

    public static QName getQNameFromString(String name) {
        StringTokenizer tokenizer = new StringTokenizer(name, "{}", false);
        String s1 = tokenizer.nextToken();
        String s2 = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        QName qname = null == s2 ? new QName(null, s1) : new QName(s1, s2);
        return qname;
    }

    public static boolean isXMLNSDecl(String attRawName) {
        return attRawName.startsWith("xmlns") && (attRawName.equals("xmlns") || attRawName.startsWith("xmlns:"));
    }

    public static String getPrefixFromXMLNSDecl(String attRawName) {
        int index = attRawName.indexOf(58);
        return index >= 0 ? attRawName.substring(index + 1) : "";
    }

    public static String getLocalPart(String qname) {
        int index = qname.indexOf(58);
        return index < 0 ? qname : qname.substring(index + 1);
    }

    public static String getPrefixPart(String qname) {
        int index = qname.indexOf(58);
        return index >= 0 ? qname.substring(0, index) : "";
    }
}


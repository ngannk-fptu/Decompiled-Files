/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import java.io.Serializable;

public class QName
implements Serializable {
    private static final long serialVersionUID = -9029109610006696081L;
    private static final String EMPTY_STRING = "";
    private String namespaceURI;
    private String localPart;
    private String prefix;

    public QName(String localPart) {
        this(EMPTY_STRING, localPart, EMPTY_STRING);
    }

    public QName(String namespaceURI, String localPart) {
        this(namespaceURI, localPart, EMPTY_STRING);
    }

    public QName(String namespaceURI, String localPart, String prefix) {
        String string = this.namespaceURI = namespaceURI == null ? EMPTY_STRING : namespaceURI;
        if (localPart == null) {
            throw new IllegalArgumentException("invalid QName local part");
        }
        this.localPart = localPart;
        if (prefix == null) {
            throw new IllegalArgumentException("invalid QName prefix");
        }
        this.prefix = prefix;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String getLocalPart() {
        return this.localPart;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getQualifiedName() {
        return this.prefix.equals(EMPTY_STRING) ? this.localPart : this.prefix + ':' + this.localPart;
    }

    public String toString() {
        return this.namespaceURI.equals(EMPTY_STRING) ? this.localPart : '{' + this.namespaceURI + '}' + this.localPart;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof QName) {
            QName qName = (QName)o;
            if (!this.namespaceURI.equals(qName.namespaceURI)) {
                return false;
            }
            return this.localPart.equals(qName.localPart);
        }
        if (o instanceof String) {
            String string = (String)o;
            if (string.length() == 0) {
                return false;
            }
            int lastColonIndex = string.lastIndexOf(":");
            if (lastColonIndex < 0 || lastColonIndex == string.length() - 1) {
                return false;
            }
            String stringPrefix = string.substring(0, lastColonIndex);
            String stringLocalPart = string.substring(lastColonIndex + 1);
            if (stringPrefix.equals(this.prefix) || stringPrefix.equals(this.namespaceURI)) {
                return this.localPart.equals(stringLocalPart);
            }
            return false;
        }
        return false;
    }

    public boolean matches(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof QName) {
            QName qName = (QName)o;
            if (!(this.namespaceURI.equals(qName.namespaceURI) || this.namespaceURI.equals("*") || qName.namespaceURI.equals("*"))) {
                return false;
            }
            return this.localPart.equals(qName.localPart) || this.localPart.equals("*") || qName.localPart.equals("*");
        }
        if (o instanceof String) {
            String string = (String)o;
            if (string.length() == 0) {
                return false;
            }
            int lastColonIndex = string.lastIndexOf(":");
            if (lastColonIndex < 0 && this.prefix.length() == 0) {
                return string.equals(this.localPart);
            }
            if (lastColonIndex < 0 || lastColonIndex == string.length() - 1) {
                return false;
            }
            String stringPrefix = string.substring(0, lastColonIndex);
            String stringLocalPart = string.substring(lastColonIndex + 1);
            if (stringPrefix.equals(this.prefix) || stringPrefix.equals(this.namespaceURI) || stringPrefix.equals("*")) {
                return this.localPart.equals(stringLocalPart) || stringLocalPart.equals("*");
            }
        }
        return false;
    }

    public static QName valueOf(String s) {
        if (s == null || s.equals(EMPTY_STRING)) {
            throw new IllegalArgumentException("invalid QName literal");
        }
        if (s.charAt(0) == '{') {
            int i = s.indexOf(125);
            if (i == -1) {
                throw new IllegalArgumentException("invalid QName literal");
            }
            if (i == s.length() - 1) {
                throw new IllegalArgumentException("invalid QName literal");
            }
            return new QName(s.substring(1, i), s.substring(i + 1));
        }
        return new QName(s);
    }

    public int hashCode() {
        int result = this.namespaceURI.hashCode();
        result = 29 * result + this.localPart.hashCode();
        return result;
    }
}


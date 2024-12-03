/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.namespace;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class QName
implements Serializable {
    private static final long serialVersionUID;
    private static final long defaultSerialVersionUID = -9120448754896609940L;
    private static final long compatabilitySerialVersionUID = 4418622981026545151L;
    private final String namespaceURI;
    private final String localPart;
    private String prefix;
    private transient String qNameAsString;

    public QName(String string, String string2) {
        this(string, string2, "");
    }

    public QName(String string, String string2, String string3) {
        this.namespaceURI = string == null ? "" : string;
        if (string2 == null) {
            throw new IllegalArgumentException("local part cannot be \"null\" when creating a QName");
        }
        this.localPart = string2;
        if (string3 == null) {
            throw new IllegalArgumentException("prefix cannot be \"null\" when creating a QName");
        }
        this.prefix = string3;
    }

    public QName(String string) {
        this("", string, "");
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

    public final boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QName) {
            QName qName = (QName)object;
            return this.localPart.equals(qName.localPart) && this.namespaceURI.equals(qName.namespaceURI);
        }
        return false;
    }

    public final int hashCode() {
        return this.namespaceURI.hashCode() ^ this.localPart.hashCode();
    }

    public String toString() {
        String string = this.qNameAsString;
        if (string == null) {
            int n = this.namespaceURI.length();
            if (n == 0) {
                string = this.localPart;
            } else {
                StringBuffer stringBuffer = new StringBuffer(n + this.localPart.length() + 2);
                stringBuffer.append('{');
                stringBuffer.append(this.namespaceURI);
                stringBuffer.append('}');
                stringBuffer.append(this.localPart);
                string = stringBuffer.toString();
            }
            this.qNameAsString = string;
        }
        return string;
    }

    public static QName valueOf(String string) {
        if (string == null) {
            throw new IllegalArgumentException("cannot create QName from \"null\" or \"\" String");
        }
        if (string.length() == 0) {
            return new QName("", string, "");
        }
        if (string.charAt(0) != '{') {
            return new QName("", string, "");
        }
        if (string.startsWith("{}")) {
            throw new IllegalArgumentException("Namespace URI .equals(XMLConstants.NULL_NS_URI), .equals(\"\"), only the local part, \"" + string.substring(2 + "".length()) + "\", " + "should be provided.");
        }
        int n = string.indexOf(125);
        if (n == -1) {
            throw new IllegalArgumentException("cannot create QName from \"" + string + "\", missing closing \"}\"");
        }
        return new QName(string.substring(1, n), string.substring(n + 1), "");
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.prefix == null) {
            this.prefix = "";
        }
    }

    static {
        String string = null;
        try {
            string = (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return System.getProperty("org.apache.xml.namespace.QName.useCompatibleSerialVersionUID");
                }
            });
        }
        catch (Exception exception) {
            // empty catch block
        }
        serialVersionUID = !"1.0".equals(string) ? -9120448754896609940L : 4418622981026545151L;
    }
}


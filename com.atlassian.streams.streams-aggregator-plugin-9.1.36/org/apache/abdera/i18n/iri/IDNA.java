/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.iri;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Nameprep;
import org.apache.abdera.i18n.text.Punycode;

public final class IDNA
implements Serializable,
Cloneable {
    private static final long serialVersionUID = -617056657751424334L;
    private final String regname;

    public IDNA(InetAddress addr) {
        this(addr.getHostName());
    }

    public IDNA(String regname) {
        this.regname = IDNA.toUnicode(regname);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toASCII() {
        return IDNA.toASCII(this.regname);
    }

    public String toUnicode() {
        return IDNA.toUnicode(this.regname);
    }

    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByName(this.toASCII());
    }

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        result = 31 * result + (this.regname == null ? 0 : this.regname.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        IDNA other = (IDNA)obj;
        return !(this.regname == null ? other.regname != null : !this.regname.equals(other.regname));
    }

    public String toString() {
        return this.toUnicode();
    }

    public static boolean equals(String idn1, String idn2) {
        return IDNA.toUnicode(idn1).equals(IDNA.toUnicode(idn2));
    }

    public static String toASCII(String regname) {
        try {
            if (regname == null) {
                return null;
            }
            if (regname.length() == 0) {
                return regname;
            }
            String[] labels = regname.split("\\.");
            StringBuilder buf = new StringBuilder();
            for (String label : labels) {
                label = Nameprep.prep(label);
                char[] chars = label.toCharArray();
                CharUtils.verifyNot(chars, CharUtils.Profile.STD3ASCIIRULES);
                if (chars[0] == '-' || chars[chars.length - 1] == '-') {
                    throw new IOException("ToASCII violation");
                }
                if (!CharUtils.inRange(chars, '\u0000', '\u007f')) {
                    if (label.startsWith("xn--")) {
                        throw new IOException("ToASCII violation");
                    }
                    String pc = "xn--" + Punycode.encode(chars, null);
                    chars = pc.toCharArray();
                }
                if (chars.length > 63) {
                    throw new IOException("ToASCII violation");
                }
                if (buf.length() > 0) {
                    buf.append('.');
                }
                buf.append(chars);
            }
            return buf.toString();
        }
        catch (IOException e) {
            return regname;
        }
    }

    public static String toUnicode(String regname) {
        if (regname == null) {
            return null;
        }
        if (regname.length() == 0) {
            return regname;
        }
        String[] labels = regname.split("\\.");
        StringBuilder buf = new StringBuilder();
        for (String label : labels) {
            char[] chars = label.toCharArray();
            if (!CharUtils.inRange(chars, '\u0000', '\u007f')) {
                label = Nameprep.prep(label);
                chars = label.toCharArray();
            }
            if (label.startsWith("xn--")) {
                label = Punycode.decode(label.substring(4));
                chars = label.toCharArray();
            }
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(chars);
        }
        String check = IDNA.toASCII(buf.toString());
        if (check.equalsIgnoreCase(regname)) {
            return buf.toString();
        }
        return regname;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Filter;

public class XmlUtil {
    private static int[] RESTRICTED_SET_v1 = new int[]{0, 9, 11, 13, 14, 32, 55296, 57344, 65534, 65536};
    private static int[] RESTRICTED_SET_v11 = new int[]{11, 13, 14, 32, 127, 160, 55296, 57344, 65534, 65536};

    public static boolean restricted(XMLVersion version, char c) {
        return XmlUtil.restricted(version, (int)c);
    }

    public static boolean restricted(XMLVersion version, int c) {
        return CharUtils.invset_contains(version == XMLVersion.XML10 ? RESTRICTED_SET_v1 : RESTRICTED_SET_v11, c);
    }

    public static XMLVersion getVersion(String version) {
        return version == null ? XMLVersion.XML10 : (version.equals("1.0") ? XMLVersion.XML10 : (version.equals("1.1") ? XMLVersion.XML11 : XMLVersion.XML10));
    }

    private static class XmlFilter
    implements Filter {
        private final XMLVersion version;

        XmlFilter(XMLVersion version) {
            this.version = version;
        }

        public boolean accept(int c) {
            return !XmlUtil.restricted(this.version, c);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum XMLVersion {
        XML10,
        XML11;

        private final Filter filter = new XmlFilter(this);

        public Filter filter() {
            return this.filter;
        }
    }
}


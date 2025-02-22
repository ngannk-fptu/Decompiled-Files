/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import org.apache.xml.serializer.utils.StringToIntTable;

public final class ElemDesc {
    private int m_flags;
    private StringToIntTable m_attrs = null;
    static final int EMPTY = 2;
    private static final int FLOW = 4;
    static final int BLOCK = 8;
    static final int BLOCKFORM = 16;
    static final int BLOCKFORMFIELDSET = 32;
    private static final int CDATA = 64;
    private static final int PCDATA = 128;
    static final int RAW = 256;
    private static final int INLINE = 512;
    private static final int INLINEA = 1024;
    static final int INLINELABEL = 2048;
    static final int FONTSTYLE = 4096;
    static final int PHRASE = 8192;
    static final int FORMCTRL = 16384;
    static final int SPECIAL = 32768;
    static final int ASPECIAL = 65536;
    static final int HEADMISC = 131072;
    static final int HEAD = 262144;
    static final int LIST = 524288;
    static final int PREFORMATTED = 0x100000;
    static final int WHITESPACESENSITIVE = 0x200000;
    static final int HEADELEM = 0x400000;
    static final int HTMLELEM = 0x800000;
    public static final int ATTRURL = 2;
    public static final int ATTREMPTY = 4;

    ElemDesc(int flags) {
        this.m_flags = flags;
    }

    private boolean is(int flags) {
        return (this.m_flags & flags) != 0;
    }

    int getFlags() {
        return this.m_flags;
    }

    void setAttr(String name, int flags) {
        if (null == this.m_attrs) {
            this.m_attrs = new StringToIntTable();
        }
        this.m_attrs.put(name, flags);
    }

    public boolean isAttrFlagSet(String name, int flags) {
        return null != this.m_attrs ? (this.m_attrs.getIgnoreCase(name) & flags) != 0 : false;
    }
}


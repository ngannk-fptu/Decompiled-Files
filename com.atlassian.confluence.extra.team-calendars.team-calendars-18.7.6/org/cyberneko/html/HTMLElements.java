/*
 * Decompiled with CFR 0.152.
 */
package org.cyberneko.html;

public class HTMLElements {
    public static final short A = 0;
    public static final short ABBR = 1;
    public static final short ACRONYM = 2;
    public static final short ADDRESS = 3;
    public static final short APPLET = 4;
    public static final short AREA = 5;
    public static final short B = 6;
    public static final short BASE = 7;
    public static final short BASEFONT = 8;
    public static final short BDO = 9;
    public static final short BGSOUND = 10;
    public static final short BIG = 11;
    public static final short BLINK = 12;
    public static final short BLOCKQUOTE = 13;
    public static final short BODY = 14;
    public static final short BR = 15;
    public static final short BUTTON = 16;
    public static final short CAPTION = 17;
    public static final short CENTER = 18;
    public static final short CITE = 19;
    public static final short CODE = 20;
    public static final short COL = 21;
    public static final short COLGROUP = 22;
    public static final short COMMENT = 23;
    public static final short DEL = 24;
    public static final short DFN = 25;
    public static final short DIR = 26;
    public static final short DIV = 27;
    public static final short DD = 28;
    public static final short DL = 29;
    public static final short DT = 30;
    public static final short EM = 31;
    public static final short EMBED = 32;
    public static final short FIELDSET = 33;
    public static final short FONT = 34;
    public static final short FORM = 35;
    public static final short FRAME = 36;
    public static final short FRAMESET = 37;
    public static final short H1 = 38;
    public static final short H2 = 39;
    public static final short H3 = 40;
    public static final short H4 = 41;
    public static final short H5 = 42;
    public static final short H6 = 43;
    public static final short HEAD = 44;
    public static final short HR = 45;
    public static final short HTML = 46;
    public static final short I = 47;
    public static final short IFRAME = 48;
    public static final short ILAYER = 49;
    public static final short IMG = 50;
    public static final short INPUT = 51;
    public static final short INS = 52;
    public static final short ISINDEX = 53;
    public static final short KBD = 54;
    public static final short KEYGEN = 55;
    public static final short LABEL = 56;
    public static final short LAYER = 57;
    public static final short LEGEND = 58;
    public static final short LI = 59;
    public static final short LINK = 60;
    public static final short LISTING = 61;
    public static final short MAP = 62;
    public static final short MARQUEE = 63;
    public static final short MENU = 64;
    public static final short META = 65;
    public static final short MULTICOL = 66;
    public static final short NEXTID = 67;
    public static final short NOBR = 68;
    public static final short NOEMBED = 69;
    public static final short NOFRAMES = 70;
    public static final short NOLAYER = 71;
    public static final short NOSCRIPT = 72;
    public static final short OBJECT = 73;
    public static final short OL = 74;
    public static final short OPTION = 75;
    public static final short OPTGROUP = 76;
    public static final short P = 77;
    public static final short PARAM = 78;
    public static final short PLAINTEXT = 79;
    public static final short PRE = 80;
    public static final short Q = 81;
    public static final short RB = 82;
    public static final short RBC = 83;
    public static final short RP = 84;
    public static final short RT = 85;
    public static final short RTC = 86;
    public static final short RUBY = 87;
    public static final short S = 88;
    public static final short SAMP = 89;
    public static final short SCRIPT = 90;
    public static final short SECTION = 91;
    public static final short SELECT = 92;
    public static final short SMALL = 93;
    public static final short SOUND = 94;
    public static final short SPACER = 95;
    public static final short SPAN = 96;
    public static final short STRIKE = 97;
    public static final short STRONG = 98;
    public static final short STYLE = 99;
    public static final short SUB = 100;
    public static final short SUP = 101;
    public static final short TABLE = 102;
    public static final short TBODY = 103;
    public static final short TD = 104;
    public static final short TEXTAREA = 105;
    public static final short TFOOT = 106;
    public static final short TH = 107;
    public static final short THEAD = 108;
    public static final short TITLE = 109;
    public static final short TR = 110;
    public static final short TT = 111;
    public static final short U = 112;
    public static final short UL = 113;
    public static final short VAR = 114;
    public static final short WBR = 115;
    public static final short XML = 116;
    public static final short XMP = 117;
    public static final short UNKNOWN = 118;
    protected static final Element[][] ELEMENTS_ARRAY;
    protected static final ElementList ELEMENTS;
    public static final Element NO_SUCH_ELEMENT;

    public static final Element getElement(short code) {
        return HTMLElements.ELEMENTS.data[code];
    }

    public static final Element getElement(String ename) {
        Element element = HTMLElements.getElement(ename, NO_SUCH_ELEMENT);
        if (element == NO_SUCH_ELEMENT) {
            element = new Element(118, ename.toUpperCase(), 8, new short[]{14, 44}, null);
            element.parent = HTMLElements.NO_SUCH_ELEMENT.parent;
            element.parentCodes = HTMLElements.NO_SUCH_ELEMENT.parentCodes;
        }
        return element;
    }

    public static final Element getElement(String ename, Element element) {
        if (ename.length() > 0) {
            Element[] elements;
            int c = ename.charAt(0);
            if (c >= 97 && c <= 122) {
                c = 65 + c - 97;
            }
            if (c >= 65 && c <= 90 && (elements = ELEMENTS_ARRAY[c - 65]) != null) {
                for (int i = 0; i < elements.length; ++i) {
                    Element elem = elements[i];
                    if (!elem.name.equalsIgnoreCase(ename)) continue;
                    return elem;
                }
            }
        }
        return element;
    }

    static {
        int j;
        int i;
        ELEMENTS_ARRAY = new Element[26][];
        ELEMENTS = new ElementList();
        NO_SUCH_ELEMENT = new Element(118, "", 8, new short[]{14, 44}, null);
        HTMLElements.ELEMENTS_ARRAY[0] = new Element[]{new Element(0, "A", 8, 14, new short[]{0}), new Element(1, "ABBR", 1, 14, null), new Element(2, "ACRONYM", 1, 14, null), new Element(3, "ADDRESS", 2, 14, new short[]{77}), new Element(4, "APPLET", 8, 14, null), new Element(5, "AREA", 4, 62, null)};
        HTMLElements.ELEMENTS_ARRAY[1] = new Element[]{new Element(6, "B", 1, 14, null), new Element(7, "BASE", 4, 44, null), new Element(8, "BASEFONT", 4, 44, null), new Element(9, "BDO", 1, 14, null), new Element(10, "BGSOUND", 4, 44, null), new Element(11, "BIG", 1, 14, null), new Element(12, "BLINK", 1, 14, null), new Element(13, "BLOCKQUOTE", 2, 14, new short[]{77}), new Element(14, "BODY", 8, 46, new short[]{44}), new Element(15, "BR", 4, 14, null), new Element(16, "BUTTON", 3, 14, new short[]{16})};
        HTMLElements.ELEMENTS_ARRAY[2] = new Element[]{new Element(17, "CAPTION", 1, 102, null), new Element(18, "CENTER", 8, 14, new short[]{77}), new Element(19, "CITE", 1, 14, null), new Element(20, "CODE", 1, 14, null), new Element(21, "COL", 4, 102, null), new Element(22, "COLGROUP", 8, 102, new short[]{21, 22}), new Element(23, "COMMENT", 16, 46, null)};
        HTMLElements.ELEMENTS_ARRAY[3] = new Element[]{new Element(24, "DEL", 1, 14, null), new Element(25, "DFN", 1, 14, null), new Element(26, "DIR", 8, 14, new short[]{77}), new Element(27, "DIV", 8, 14, new short[]{77}), new Element(28, "DD", 2, 14, new short[]{30, 28, 77}), new Element(29, "DL", 2, 14, new short[]{77}), new Element(30, "DT", 2, 14, new short[]{30, 28, 77})};
        HTMLElements.ELEMENTS_ARRAY[4] = new Element[]{new Element(31, "EM", 1, 14, null), new Element(32, "EMBED", 4, 14, null)};
        HTMLElements.ELEMENTS_ARRAY[5] = new Element[]{new Element(33, "FIELDSET", 8, 14, new short[]{77}), new Element(34, "FONT", 8, 14, null), new Element(35, "FORM", 8, new short[]{14, 104, 27}, new short[]{16, 77}), new Element(36, "FRAME", 4, 37, null), new Element(37, "FRAMESET", 8, 46, null)};
        HTMLElements.ELEMENTS_ARRAY[7] = new Element[]{new Element(38, "H1", 2, new short[]{14, 0}, new short[]{38, 39, 40, 41, 42, 43, 77}), new Element(39, "H2", 2, new short[]{14, 0}, new short[]{38, 39, 40, 41, 42, 43, 77}), new Element(40, "H3", 2, new short[]{14, 0}, new short[]{38, 39, 40, 41, 42, 43, 77}), new Element(41, "H4", 2, new short[]{14, 0}, new short[]{38, 39, 40, 41, 42, 43, 77}), new Element(42, "H5", 2, new short[]{14, 0}, new short[]{38, 39, 40, 41, 42, 43, 77}), new Element(43, "H6", 2, new short[]{14, 0}, new short[]{38, 39, 40, 41, 42, 43, 77}), new Element(44, "HEAD", 0, 46, null), new Element(45, "HR", 4, 14, new short[]{77}), new Element(46, "HTML", 0, null, null)};
        HTMLElements.ELEMENTS_ARRAY[8] = new Element[]{new Element(47, "I", 1, 14, null), new Element(48, "IFRAME", 2, 14, null), new Element(49, "ILAYER", 2, 14, null), new Element(50, "IMG", 4, 14, null), new Element(51, "INPUT", 4, 14, null), new Element(52, "INS", 1, 14, null), new Element(53, "ISINDEX", 1, 44, null)};
        HTMLElements.ELEMENTS_ARRAY[10] = new Element[]{new Element(54, "KBD", 1, 14, null), new Element(55, "KEYGEN", 4, 14, null)};
        HTMLElements.ELEMENTS_ARRAY[11] = new Element[]{new Element(56, "LABEL", 1, 14, null), new Element(57, "LAYER", 2, 14, null), new Element(58, "LEGEND", 1, 33, null), new Element(59, "LI", 8, new short[]{14, 113, 74}, new short[]{59, 77}), new Element(60, "LINK", 4, 44, null), new Element(61, "LISTING", 2, 14, new short[]{77})};
        HTMLElements.ELEMENTS_ARRAY[12] = new Element[]{new Element(62, "MAP", 1, 14, null), new Element(63, "MARQUEE", 8, 14, null), new Element(64, "MENU", 8, 14, new short[]{77}), new Element(65, "META", 4, 44, new short[]{99, 109}), new Element(66, "MULTICOL", 8, 14, null)};
        HTMLElements.ELEMENTS_ARRAY[13] = new Element[]{new Element(67, "NEXTID", 4, 14, null), new Element(68, "NOBR", 1, 14, new short[]{68}), new Element(69, "NOEMBED", 8, 14, null), new Element(70, "NOFRAMES", 8, null, null), new Element(71, "NOLAYER", 8, 14, null), new Element(72, "NOSCRIPT", 8, new short[]{14}, null)};
        HTMLElements.ELEMENTS_ARRAY[14] = new Element[]{new Element(73, "OBJECT", 8, 14, null), new Element(74, "OL", 2, 14, new short[]{77}), new Element(76, "OPTGROUP", 0, 92, new short[]{75}), new Element(75, "OPTION", 0, 92, new short[]{75})};
        HTMLElements.ELEMENTS_ARRAY[15] = new Element[]{new Element(77, "P", 8, 14, new short[]{77}), new Element(78, "PARAM", 4, new short[]{73, 4}, null), new Element(79, "PLAINTEXT", 16, 14, null), new Element(80, "PRE", 2, 14, new short[]{77})};
        HTMLElements.ELEMENTS_ARRAY[16] = new Element[]{new Element(81, "Q", 1, 14, null)};
        HTMLElements.ELEMENTS_ARRAY[17] = new Element[]{new Element(82, "RB", 1, 87, new short[]{82}), new Element(83, "RBC", 0, 87, null), new Element(84, "RP", 1, 87, new short[]{82}), new Element(85, "RT", 1, 87, new short[]{82, 84}), new Element(86, "RTC", 0, 87, new short[]{83}), new Element(87, "RUBY", 8, 14, new short[]{87})};
        HTMLElements.ELEMENTS_ARRAY[18] = new Element[]{new Element(88, "S", 1, 14, null), new Element(89, "SAMP", 1, 14, null), new Element(90, "SCRIPT", 16, new short[]{44, 14}, null), new Element(91, "SECTION", 8, 14, new short[]{92}), new Element(92, "SELECT", 8, 14, new short[]{92}), new Element(93, "SMALL", 1, 14, null), new Element(94, "SOUND", 4, 44, null), new Element(95, "SPACER", 4, 14, null), new Element(96, "SPAN", 8, 14, null), new Element(97, "STRIKE", 1, 14, null), new Element(98, "STRONG", 1, 14, null), new Element(99, "STYLE", 16, new short[]{44, 14}, new short[]{99, 109, 65}), new Element(100, "SUB", 1, 14, null), new Element(101, "SUP", 1, 14, null)};
        HTMLElements.ELEMENTS_ARRAY[19] = new Element[]{new Element(102, "TABLE", 10, 14, null), new Element(103, "TBODY", 0, 102, new short[]{108, 103, 106, 104, 107, 110, 22}), new Element(104, "TD", 8, 110, 102, new short[]{104, 107}), new Element(105, "TEXTAREA", 16, 14, null), new Element(106, "TFOOT", 0, 102, new short[]{108, 103, 106, 104, 107, 110}), new Element(107, "TH", 8, 110, 102, new short[]{104, 107}), new Element(108, "THEAD", 0, 102, new short[]{108, 103, 106, 104, 107, 110, 22}), new Element(109, "TITLE", 16, new short[]{44, 14}, null), new Element(110, "TR", 2, new short[]{103, 108, 106}, 102, new short[]{104, 107, 110, 22, 27}), new Element(111, "TT", 1, 14, null)};
        HTMLElements.ELEMENTS_ARRAY[20] = new Element[]{new Element(112, "U", 1, 14, null), new Element(113, "UL", 8, 14, new short[]{77})};
        HTMLElements.ELEMENTS_ARRAY[21] = new Element[]{new Element(114, "VAR", 1, 14, null)};
        HTMLElements.ELEMENTS_ARRAY[22] = new Element[]{new Element(115, "WBR", 4, 14, null)};
        HTMLElements.ELEMENTS_ARRAY[23] = new Element[]{new Element(116, "XML", 0, 14, null), new Element(117, "XMP", 16, 14, new short[]{77})};
        for (i = 0; i < ELEMENTS_ARRAY.length; ++i) {
            Element[] elements = ELEMENTS_ARRAY[i];
            if (elements == null) continue;
            for (j = 0; j < elements.length; ++j) {
                Element element = elements[j];
                ELEMENTS.addElement(element);
            }
        }
        ELEMENTS.addElement(NO_SUCH_ELEMENT);
        for (i = 0; i < HTMLElements.ELEMENTS.size; ++i) {
            Element element = HTMLElements.ELEMENTS.data[i];
            if (element.parentCodes == null) continue;
            element.parent = new Element[element.parentCodes.length];
            for (j = 0; j < element.parentCodes.length; ++j) {
                element.parent[j] = HTMLElements.ELEMENTS.data[element.parentCodes[j]];
            }
            element.parentCodes = null;
        }
    }

    public static class ElementList {
        public int size;
        public Element[] data = new Element[120];

        public void addElement(Element element) {
            if (this.size == this.data.length) {
                Element[] newarray = new Element[this.size + 20];
                System.arraycopy(this.data, 0, newarray, 0, this.size);
                this.data = newarray;
            }
            this.data[this.size++] = element;
        }
    }

    public static class Element {
        public static final int INLINE = 1;
        public static final int BLOCK = 2;
        public static final int EMPTY = 4;
        public static final int CONTAINER = 8;
        public static final int SPECIAL = 16;
        public short code;
        public String name;
        public int flags;
        public short[] parentCodes;
        public Element[] parent;
        public short bounds;
        public short[] closes;

        public Element(short code, String name, int flags, short parent, short[] closes) {
            this(code, name, flags, new short[]{parent}, -1, closes);
        }

        public Element(short code, String name, int flags, short parent, short bounds, short[] closes) {
            this(code, name, flags, new short[]{parent}, bounds, closes);
        }

        public Element(short code, String name, int flags, short[] parents, short[] closes) {
            this(code, name, flags, parents, -1, closes);
        }

        public Element(short code, String name, int flags, short[] parents, short bounds, short[] closes) {
            this.code = code;
            this.name = name;
            this.flags = flags;
            this.parentCodes = parents;
            this.parent = null;
            this.bounds = bounds;
            this.closes = closes;
        }

        public final boolean isInline() {
            return (this.flags & 1) != 0;
        }

        public final boolean isBlock() {
            return (this.flags & 2) != 0;
        }

        public final boolean isEmpty() {
            return (this.flags & 4) != 0;
        }

        public final boolean isContainer() {
            return (this.flags & 8) != 0;
        }

        public final boolean isSpecial() {
            return (this.flags & 0x10) != 0;
        }

        public boolean closes(short tag) {
            if (this.closes != null) {
                for (int i = 0; i < this.closes.length; ++i) {
                    if (this.closes[i] != tag) continue;
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(Object o) {
            return this.name.equals(o);
        }

        public String toString() {
            return super.toString() + "(name=" + this.name + ")";
        }

        public boolean isParent(Element element) {
            if (this.parent == null) {
                return false;
            }
            for (int i = 0; i < this.parent.length; ++i) {
                if (element.code != this.parent[i].code) continue;
                return true;
            }
            return false;
        }
    }
}


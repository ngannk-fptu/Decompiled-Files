/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class HTMLElements {
    public static final short A = 0;
    public static final short ABBR = 1;
    public static final short ACRONYM = 2;
    public static final short ADDRESS = 3;
    public static final short APPLET = 4;
    public static final short AREA = 5;
    public static final short ARTICLE = 6;
    public static final short ASIDE = 7;
    public static final short AUDIO = 8;
    public static final short B = 9;
    public static final short BASE = 10;
    public static final short BASEFONT = 11;
    public static final short BDI = 12;
    public static final short BDO = 13;
    public static final short BGSOUND = 14;
    public static final short BIG = 15;
    public static final short BLINK = 16;
    public static final short BLOCKQUOTE = 17;
    public static final short BODY = 18;
    public static final short BR = 19;
    public static final short BUTTON = 20;
    public static final short CANVAS = 21;
    public static final short CAPTION = 22;
    public static final short CENTER = 23;
    public static final short CITE = 24;
    public static final short CODE = 25;
    public static final short COL = 26;
    public static final short COLGROUP = 27;
    public static final short COMMENT = 28;
    public static final short DATA = 29;
    public static final short DATALIST = 30;
    public static final short DEL = 31;
    public static final short DETAILS = 32;
    public static final short DFN = 33;
    public static final short DIALOG = 34;
    public static final short DIR = 35;
    public static final short DIV = 36;
    public static final short DD = 37;
    public static final short DL = 38;
    public static final short DT = 39;
    public static final short EM = 40;
    public static final short EMBED = 41;
    public static final short FIELDSET = 42;
    public static final short FIGCAPTION = 43;
    public static final short FIGURE = 44;
    public static final short FONT = 45;
    public static final short FOOTER = 46;
    public static final short FORM = 47;
    public static final short FRAME = 48;
    public static final short FRAMESET = 49;
    public static final short H1 = 50;
    public static final short H2 = 51;
    public static final short H3 = 52;
    public static final short H4 = 53;
    public static final short H5 = 54;
    public static final short H6 = 55;
    public static final short HEAD = 56;
    public static final short HEADER = 57;
    public static final short HR = 58;
    public static final short HTML = 59;
    public static final short I = 60;
    public static final short IFRAME = 61;
    public static final short ILAYER = 62;
    public static final short IMG = 63;
    public static final short IMAGE = 64;
    public static final short INPUT = 65;
    public static final short INS = 66;
    public static final short KBD = 67;
    public static final short KEYGEN = 68;
    public static final short LABEL = 69;
    public static final short LAYER = 70;
    public static final short LEGEND = 71;
    public static final short LI = 72;
    public static final short LINK = 73;
    public static final short LISTING = 74;
    public static final short MAIN = 75;
    public static final short MAP = 76;
    public static final short MARK = 77;
    public static final short MARQUEE = 78;
    public static final short MENU = 79;
    public static final short META = 80;
    public static final short METER = 81;
    public static final short MULTICOL = 82;
    public static final short NAV = 83;
    public static final short NEXTID = 84;
    public static final short NOBR = 85;
    public static final short NOEMBED = 86;
    public static final short NOFRAMES = 87;
    public static final short NOLAYER = 88;
    public static final short NOSCRIPT = 89;
    public static final short OBJECT = 90;
    public static final short OL = 91;
    public static final short OPTGROUP = 92;
    public static final short OPTION = 93;
    public static final short P = 94;
    public static final short PARAM = 95;
    public static final short PICTURE = 96;
    public static final short PLAINTEXT = 97;
    public static final short PRE = 98;
    public static final short PROGRESS = 99;
    public static final short Q = 100;
    public static final short RB = 101;
    public static final short RBC = 102;
    public static final short RP = 103;
    public static final short RT = 104;
    public static final short RTC = 105;
    public static final short RUBY = 106;
    public static final short S = 107;
    public static final short SAMP = 108;
    public static final short SCRIPT = 109;
    public static final short SECTION = 110;
    public static final short SELECT = 111;
    public static final short SLOT = 112;
    public static final short SMALL = 113;
    public static final short SOUND = 114;
    public static final short SOURCE = 115;
    public static final short SPACER = 116;
    public static final short SPAN = 117;
    public static final short STRIKE = 118;
    public static final short STRONG = 119;
    public static final short STYLE = 120;
    public static final short SUB = 121;
    public static final short SUMMARY = 122;
    public static final short SUP = 123;
    public static final short SVG = 124;
    public static final short TABLE = 125;
    public static final short TBODY = 126;
    public static final short TD = 127;
    public static final short TEMPLATE = 128;
    public static final short TEXTAREA = 129;
    public static final short TFOOT = 130;
    public static final short TH = 131;
    public static final short THEAD = 132;
    public static final short TIME = 133;
    public static final short TITLE = 134;
    public static final short TR = 135;
    public static final short TRACK = 136;
    public static final short TT = 137;
    public static final short OUTPUT = 138;
    public static final short U = 139;
    public static final short UL = 140;
    public static final short VAR = 141;
    public static final short VIDEO = 142;
    public static final short WBR = 143;
    public static final short XML = 144;
    public static final short XMP = 145;
    public static final short UNKNOWN = 146;
    public final Element NO_SUCH_ELEMENT = new Element(146, "", 8, new short[]{18, 56}, null);
    public final Map<Short, Element> elementsByCode = new HashMap<Short, Element>(256);
    public final Map<String, Element> elementsByName = new TreeMap<String, Element>(String.CASE_INSENSITIVE_ORDER);

    public HTMLElements() {
        Element[][] elementsArray = new Element[26][];
        elementsArray[0] = new Element[]{new Element(0, "A", 8, 18, new short[]{0}), new Element(1, "ABBR", 1, 18, null), new Element(2, "ACRONYM", 1, 18, null), new Element(3, "ADDRESS", 2, 18, new short[]{94}), new Element(4, "APPLET", 8, 18, null), new Element(5, "AREA", 4, 18, null), new Element(6, "ARTICLE", 2, 18, new short[]{94}), new Element(7, "ASIDE", 2, 18, new short[]{94}), new Element(8, "AUDIO", 8, 18, null)};
        elementsArray[1] = new Element[]{new Element(9, "B", 1, 18, new short[]{124}), new Element(10, "BASE", 4, 56, null), new Element(11, "BASEFONT", 4, 56, null), new Element(12, "BDI", 1, 18, null), new Element(13, "BDO", 1, 18, null), new Element(14, "BGSOUND", 4, 56, null), new Element(15, "BIG", 1, 18, new short[]{124}), new Element(16, "BLINK", 1, 18, null), new Element(17, "BLOCKQUOTE", 2, 18, new short[]{94, 124}), new Element(18, "BODY", 8, 59, new short[]{56, 124}), new Element(19, "BR", 4, 18, new short[]{124}), new Element(20, "BUTTON", 3, 18, new short[]{20})};
        elementsArray[2] = new Element[]{new Element(21, "CANVAS", 8, 18, null), new Element(22, "CAPTION", 1, 125, null), new Element(23, "CENTER", 8, 18, new short[]{94, 124}), new Element(24, "CITE", 1, 18, null), new Element(25, "CODE", 1, 18, new short[]{124}), new Element(26, "COL", 4, 27, null), new Element(27, "COLGROUP", 8, 125, new short[]{26, 27}), new Element(28, "COMMENT", 16, 59, null)};
        elementsArray[3] = new Element[]{new Element(29, "DATA", 8, 18, null), new Element(30, "DATALIST", 8, 18, null), new Element(31, "DEL", 1, 18, null), new Element(32, "DETAILS", 2, 18, new short[]{94}), new Element(33, "DFN", 1, 18, null), new Element(34, "DIALOG", 8, 18, new short[]{94}), new Element(35, "DIR", 8, 18, new short[]{94}), new Element(36, "DIV", 8, 18, new short[]{94, 124}), new Element(37, "DD", 2, 18, new short[]{39, 37, 94, 124}), new Element(38, "DL", 10, 18, new short[]{94, 124}), new Element(39, "DT", 2, 18, new short[]{39, 37, 94, 124})};
        elementsArray[4] = new Element[]{new Element(40, "EM", 1, 18, new short[]{124}), new Element(41, "EMBED", 4, 18, new short[]{124})};
        elementsArray[5] = new Element[]{new Element(42, "FIELDSET", 8, 18, new short[]{94}), new Element(43, "FIGCAPTION", 2, 18, new short[]{94}), new Element(44, "FIGURE", 2, 18, new short[]{94}), new Element(45, "FONT", 8, 18, null), new Element(46, "FOOTER", 2, 18, new short[]{94}), new Element(47, "FORM", 8, new short[]{18, 127, 36}, new short[]{94}), new Element(48, "FRAME", 4, 49, null), new Element(49, "FRAMESET", 8, 59, null)};
        elementsArray[7] = new Element[]{new Element(50, "H1", 2, new short[]{18, 0}, new short[]{50, 51, 52, 53, 54, 55, 94, 124}), new Element(51, "H2", 2, new short[]{18, 0}, new short[]{50, 51, 52, 53, 54, 55, 94, 124}), new Element(52, "H3", 2, new short[]{18, 0}, new short[]{50, 51, 52, 53, 54, 55, 94, 124}), new Element(53, "H4", 2, new short[]{18, 0}, new short[]{50, 51, 52, 53, 54, 55, 94, 124}), new Element(54, "H5", 2, new short[]{18, 0}, new short[]{50, 51, 52, 53, 54, 55, 94, 124}), new Element(55, "H6", 2, new short[]{18, 0}, new short[]{50, 51, 52, 53, 54, 55, 94, 124}), new Element(56, "HEAD", 0, 59, null), new Element(57, "HEADER", 2, 18, new short[]{94}), new Element(58, "HR", 4, 18, new short[]{94, 124}), new Element(59, "HTML", 0, null, null)};
        elementsArray[8] = new Element[]{new Element(60, "I", 1, 18, new short[]{124}), new Element(61, "IFRAME", 2, 18, null), new Element(62, "ILAYER", 2, 18, null), new Element(63, "IMG", 4, 18, new short[]{124}), new Element(64, "IMAGE", 4, 18, null), new Element(65, "INPUT", 4, 18, null), new Element(66, "INS", 1, 18, null)};
        elementsArray[10] = new Element[]{new Element(67, "KBD", 1, 18, null), new Element(68, "KEYGEN", 4, 18, null)};
        elementsArray[11] = new Element[]{new Element(69, "LABEL", 1, 18, null), new Element(70, "LAYER", 2, 18, null), new Element(71, "LEGEND", 1, 18, null), new Element(72, "LI", 8, new short[]{18, 140, 91, 79}, new short[]{72, 94, 124}), new Element(73, "LINK", 4, 56, null), new Element(74, "LISTING", 2, 18, new short[]{94, 124})};
        elementsArray[12] = new Element[]{new Element(75, "MAIN", 2, 18, new short[]{94}), new Element(76, "MAP", 1, 18, null), new Element(77, "MARK", 8, 18, null), new Element(78, "MARQUEE", 8, 18, null), new Element(79, "MENU", 8, 18, new short[]{94, 124}), new Element(81, "METER", 8, 18, null), new Element(80, "META", 4, 56, new short[]{120, 134, 124}), new Element(82, "MULTICOL", 8, 18, null)};
        elementsArray[13] = new Element[]{new Element(83, "NAV", 2, 18, new short[]{94}), new Element(84, "NEXTID", 1, 18, null), new Element(85, "NOBR", 1, 18, new short[]{85, 124}), new Element(86, "NOEMBED", 8, 18, null), new Element(87, "NOFRAMES", 8, null, null), new Element(88, "NOLAYER", 8, 18, null), new Element(89, "NOSCRIPT", 8, new short[]{56, 18}, null)};
        elementsArray[14] = new Element[]{new Element(90, "OBJECT", 8, 18, null), new Element(91, "OL", 2, 18, new short[]{94, 124}), new Element(92, "OPTGROUP", 1, 18, new short[]{93}), new Element(93, "OPTION", 1, 18, new short[]{93}), new Element(138, "OUTPUT", 8, 18, null)};
        elementsArray[15] = new Element[]{new Element(94, "P", 8, 18, new short[]{94, 124}), new Element(95, "PARAM", 4, 18, null), new Element(96, "PICTURE", 8, 18, null), new Element(97, "PLAINTEXT", 16, 18, new short[]{94}), new Element(98, "PRE", 2, 18, new short[]{94, 124}), new Element(99, "PROGRESS", 8, 18, null)};
        elementsArray[16] = new Element[]{new Element(100, "Q", 1, 18, null)};
        elementsArray[17] = new Element[]{new Element(101, "RB", 1, 18, null), new Element(102, "RBC", 0, 18, null), new Element(103, "RP", 1, 18, null), new Element(104, "RT", 1, 18, null), new Element(105, "RTC", 0, 18, null), new Element(106, "RUBY", 8, 18, new short[]{124})};
        elementsArray[18] = new Element[]{new Element(107, "S", 1, 18, new short[]{124}), new Element(108, "SAMP", 1, 18, null), new Element(109, "SCRIPT", 16, new short[]{56, 18}, null), new Element(110, "SECTION", 2, 18, new short[]{111, 94}), new Element(111, "SELECT", 8, 18, new short[]{111}), new Element(112, "SLOT", 8, 18, null), new Element(113, "SMALL", 1, 18, new short[]{124}), new Element(114, "SOUND", 4, 56, null), new Element(115, "SOURCE", 4, 18, null), new Element(116, "SPACER", 1, 18, null), new Element(117, "SPAN", 8, 18, new short[]{124}), new Element(118, "STRIKE", 1, 18, new short[]{124}), new Element(119, "STRONG", 1, 18, new short[]{124}), new Element(120, "STYLE", 16, new short[]{56, 18}, new short[]{120, 134, 80}), new Element(121, "SUB", 1, 18, new short[]{124}), new Element(122, "SUMMARY", 2, 18, new short[]{94}), new Element(123, "SUP", 1, 18, new short[]{124}), new Element(124, "SVG", 8, 18, null)};
        elementsArray[19] = new Element[]{new Element(125, "TABLE", 10, 18, new short[]{124}), new Element(126, "TBODY", 0, 125, new short[]{47, 132, 126, 130, 127, 131, 135, 27}), new Element(127, "TD", 8, 135, 125, new short[]{127, 131}), new Element(128, "TEMPLATE", 8, new short[]{56, 18}, null), new Element(129, "TEXTAREA", 16, 18, null), new Element(130, "TFOOT", 0, 125, new short[]{132, 126, 130, 127, 131, 135}), new Element(131, "TH", 8, 135, 125, new short[]{127, 131}), new Element(132, "THEAD", 0, 125, new short[]{132, 126, 130, 127, 131, 135, 27}), new Element(133, "TIME", 8, 18, null), new Element(134, "TITLE", 16, new short[]{56, 18}, null), new Element(135, "TR", 2, new short[]{126, 132, 130}, 125, new short[]{47, 127, 131, 135, 27, 36}), new Element(136, "TRACK", 4, 18, null), new Element(137, "TT", 1, 18, new short[]{124})};
        elementsArray[20] = new Element[]{new Element(139, "U", 1, 18, new short[]{124}), new Element(140, "UL", 8, 18, new short[]{94, 124})};
        elementsArray[21] = new Element[]{new Element(141, "VAR", 1, 18, new short[]{124}), new Element(142, "VIDEO", 8, 18, null)};
        elementsArray[22] = new Element[]{new Element(143, "WBR", 4, 18, null)};
        elementsArray[23] = new Element[]{new Element(144, "XML", 0, 18, null), new Element(145, "XMP", 16, 18, new short[]{94})};
        for (Element[] elements : elementsArray) {
            if (elements == null) continue;
            for (Element element : elements) {
                this.elementsByCode.put(element.code, element);
                this.elementsByName.put(element.name, element);
            }
        }
        this.elementsByCode.put(this.NO_SUCH_ELEMENT.code, this.NO_SUCH_ELEMENT);
        for (Element element : this.elementsByCode.values()) {
            this.defineParents(element);
        }
    }

    public void setElement(Element element) {
        this.elementsByCode.put(element.code, element);
        this.elementsByName.put(element.name, element);
        this.defineParents(element);
    }

    private void defineParents(Element element) {
        if (element.parentCodes != null) {
            element.parent = new Element[element.parentCodes.length];
            for (int j = 0; j < element.parentCodes.length; ++j) {
                element.parent[j] = this.elementsByCode.get(element.parentCodes[j]);
            }
            element.parentCodes = null;
        }
    }

    public final Element getElement(short code) {
        return this.elementsByCode.get(code);
    }

    public final Element getElement(String ename) {
        Element element = this.getElement(ename, this.NO_SUCH_ELEMENT);
        if (element == this.NO_SUCH_ELEMENT) {
            element = new Element(146, ename.toUpperCase(Locale.ROOT), this.NO_SUCH_ELEMENT.flags, this.NO_SUCH_ELEMENT.parentCodes, this.NO_SUCH_ELEMENT.closes);
            element.parent = this.NO_SUCH_ELEMENT.parent;
            element.parentCodes = this.NO_SUCH_ELEMENT.parentCodes;
        }
        return element;
    }

    public final Element getElement(String ename, Element element) {
        return this.elementsByName.getOrDefault(ename, element);
    }

    public static class Element {
        public static final int INLINE = 1;
        public static final int BLOCK = 2;
        public static final int EMPTY = 4;
        public static final int CONTAINER = 8;
        public static final int SPECIAL = 16;
        public final short code;
        public final String name;
        public final int flags;
        public short[] parentCodes;
        public Element[] parent;
        public final short bounds;
        public final short[] closes;

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
                for (short close : this.closes) {
                    if (close != tag) continue;
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(Object o) {
            if (o instanceof Element) {
                return this.name.equals(((Element)o).name);
            }
            return false;
        }

        public String toString() {
            return super.toString() + "(name=" + this.name + ")";
        }

        public boolean isParent(Element element) {
            if (this.parent == null) {
                return false;
            }
            for (Element element2 : this.parent) {
                if (element.code != element2.code) continue;
                return true;
            }
            return false;
        }
    }
}


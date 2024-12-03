/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.tidy.AttVal;
import org.w3c.tidy.AttrCheck;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.Node;
import org.w3c.tidy.TagTable;
import org.w3c.tidy.TidyUtils;

public final class AttrCheckImpl {
    public static final AttrCheck URL = new CheckUrl();
    public static final AttrCheck SCRIPT = new CheckScript();
    public static final AttrCheck NAME = new CheckName();
    public static final AttrCheck ID = new CheckId();
    public static final AttrCheck ALIGN = new CheckAlign();
    public static final AttrCheck VALIGN = new CheckValign();
    public static final AttrCheck BOOL = new CheckBool();
    public static final AttrCheck LENGTH = new CheckLength();
    public static final AttrCheck TARGET = new CheckTarget();
    public static final AttrCheck FSUBMIT = new CheckFsubmit();
    public static final AttrCheck CLEAR = new CheckClear();
    public static final AttrCheck SHAPE = new CheckShape();
    public static final AttrCheck NUMBER = new CheckNumber();
    public static final AttrCheck SCOPE = new CheckScope();
    public static final AttrCheck COLOR = new CheckColor();
    public static final AttrCheck VTYPE = new CheckVType();
    public static final AttrCheck SCROLL = new CheckScroll();
    public static final AttrCheck TEXTDIR = new CheckTextDir();
    public static final AttrCheck LANG = new CheckLang();
    public static final AttrCheck TEXT = null;
    public static final AttrCheck CHARSET = null;
    public static final AttrCheck TYPE = null;
    public static final AttrCheck CHARACTER = null;
    public static final AttrCheck URLS = null;
    public static final AttrCheck COLS = null;
    public static final AttrCheck COORDS = null;
    public static final AttrCheck DATE = null;
    public static final AttrCheck IDREF = null;
    public static final AttrCheck TFRAME = null;
    public static final AttrCheck FBORDER = null;
    public static final AttrCheck MEDIA = null;
    public static final AttrCheck LINKTYPES = null;
    public static final AttrCheck TRULES = null;

    private AttrCheckImpl() {
    }

    public static class CheckLang
    implements AttrCheck {
        public void check(Lexer lexer, Node node, AttVal attval) {
            if ("lang".equals(attval.attribute)) {
                lexer.constrainVersion(-1025);
            }
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
        }
    }

    public static class CheckTextDir
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"rtl", "ltr"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(VALID_VALUES, attval.value)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckScroll
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"no", "yes", "auto"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(VALID_VALUES, attval.value)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckVType
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"data", "object", "ref"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(VALID_VALUES, attval.value)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckColor
    implements AttrCheck {
        private static final Map COLORS = new HashMap();

        public void check(Lexer lexer, Node node, AttVal attval) {
            boolean hexUppercase = true;
            boolean invalid = false;
            boolean found = false;
            if (attval.value == null || attval.value.length() == 0) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            String given = attval.value;
            Iterator colorIter = COLORS.entrySet().iterator();
            while (colorIter.hasNext()) {
                Map.Entry color = colorIter.next();
                if (given.charAt(0) == '#') {
                    if (given.length() != 7) {
                        lexer.report.attrError(lexer, node, attval, (short)51);
                        invalid = true;
                        break;
                    }
                    if (!given.equalsIgnoreCase((String)color.getValue())) continue;
                    if (lexer.configuration.replaceColor) {
                        attval.value = (String)color.getKey();
                    }
                    found = true;
                    break;
                }
                if (TidyUtils.isLetter(given.charAt(0))) {
                    if (!given.equalsIgnoreCase((String)color.getKey())) continue;
                    if (lexer.configuration.replaceColor) {
                        attval.value = (String)color.getKey();
                    }
                    found = true;
                    break;
                }
                lexer.report.attrError(lexer, node, attval, (short)51);
                invalid = true;
                break;
            }
            if (!found && !invalid) {
                if (given.charAt(0) == '#') {
                    int i;
                    for (i = 1; i < 7; ++i) {
                        if (TidyUtils.isDigit(given.charAt(i)) || "abcdef".indexOf(Character.toLowerCase(given.charAt(i))) != -1) continue;
                        lexer.report.attrError(lexer, node, attval, (short)51);
                        invalid = true;
                        break;
                    }
                    if (!invalid && hexUppercase) {
                        for (i = 1; i < 7; ++i) {
                            attval.value = given.toUpperCase();
                        }
                    }
                } else {
                    lexer.report.attrError(lexer, node, attval, (short)51);
                    invalid = true;
                }
            }
        }

        static {
            COLORS.put("black", "#000000");
            COLORS.put("green", "#008000");
            COLORS.put("silver", "#C0C0C0");
            COLORS.put("lime", "#00FF00");
            COLORS.put("gray", "#808080");
            COLORS.put("olive", "#808000");
            COLORS.put("white", "#FFFFFF");
            COLORS.put("yellow", "#FFFF00");
            COLORS.put("maroon", "#800000");
            COLORS.put("navy", "#000080");
            COLORS.put("red", "#FF0000");
            COLORS.put("blue", "#0000FF");
            COLORS.put("purple", "#800080");
            COLORS.put("teal", "#008080");
            COLORS.put("fuchsia", "#FF00FF");
            COLORS.put("aqua", "#00FFFF");
        }
    }

    public static class CheckName
    implements AttrCheck {
        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            if (lexer.configuration.tt.isAnchorElement(node)) {
                lexer.constrainVersion(-1025);
                Node old = lexer.configuration.tt.getNodeByAnchor(attval.value);
                if (old != null && old != node) {
                    lexer.report.attrError(lexer, node, attval, (short)66);
                } else {
                    lexer.configuration.tt.anchorList = lexer.configuration.tt.addAnchor(attval.value, node);
                }
            }
        }
    }

    public static class CheckId
    implements AttrCheck {
        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null || attval.value.length() == 0) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            String p = attval.value;
            char s = p.charAt(0);
            if (p.length() == 0 || !Character.isLetter(p.charAt(0))) {
                if (lexer.isvoyager && (TidyUtils.isXMLLetter(s) || s == '_' || s == ':')) {
                    lexer.report.attrError(lexer, node, attval, (short)71);
                } else {
                    lexer.report.attrError(lexer, node, attval, (short)51);
                }
            } else {
                for (int j = 1; j < p.length(); ++j) {
                    s = p.charAt(j);
                    if (TidyUtils.isNamechar(s)) continue;
                    if (lexer.isvoyager && TidyUtils.isXMLNamechar(s)) {
                        lexer.report.attrError(lexer, node, attval, (short)71);
                        break;
                    }
                    lexer.report.attrError(lexer, node, attval, (short)51);
                    break;
                }
            }
            Node old = lexer.configuration.tt.getNodeByAnchor(attval.value);
            if (old != null && old != node) {
                lexer.report.attrError(lexer, node, attval, (short)66);
            } else {
                lexer.configuration.tt.anchorList = lexer.configuration.tt.addAnchor(attval.value, node);
            }
        }
    }

    public static class CheckNumber
    implements AttrCheck {
        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            if (("cols".equalsIgnoreCase(attval.attribute) || "rows".equalsIgnoreCase(attval.attribute)) && node.tag == lexer.configuration.tt.tagFrameset) {
                return;
            }
            String value = attval.value;
            int j = 0;
            if (node.tag == lexer.configuration.tt.tagFont && (value.startsWith("+") || value.startsWith("-"))) {
                ++j;
            }
            while (j < value.length()) {
                char p = value.charAt(j);
                if (!Character.isDigit(p)) {
                    lexer.report.attrError(lexer, node, attval, (short)51);
                    break;
                }
                ++j;
            }
        }
    }

    public static class CheckScope
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"row", "rowgroup", "col", "colgroup"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(VALID_VALUES, attval.value)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckShape
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"rect", "default", "circle", "poly"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(VALID_VALUES, attval.value)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckClear
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"none", "left", "right", "all"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                attval.value = VALID_VALUES[0];
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(VALID_VALUES, attval.value)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckFsubmit
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"get", "post"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(VALID_VALUES, attval.value)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckTarget
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"_blank", "_self", "_parent", "_top"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            lexer.constrainVersion(-5);
            if (attval.value == null || attval.value.length() == 0) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            String value = attval.value;
            if (Character.isLetter(value.charAt(0))) {
                return;
            }
            if (!TidyUtils.isInValuesIgnoreCase(VALID_VALUES, value)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckLength
    implements AttrCheck {
        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            if ("width".equalsIgnoreCase(attval.attribute) && (node.tag == lexer.configuration.tt.tagCol || node.tag == lexer.configuration.tt.tagColgroup)) {
                return;
            }
            String p = attval.value;
            if (p.length() == 0 || !Character.isDigit(p.charAt(0)) && '%' != p.charAt(0)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            } else {
                TagTable tt = lexer.configuration.tt;
                for (int j = 1; j < p.length(); ++j) {
                    if ((Character.isDigit(p.charAt(j)) || node.tag != tt.tagTd && node.tag != tt.tagTh) && (Character.isDigit(p.charAt(j)) || p.charAt(j) == '%')) continue;
                    lexer.report.attrError(lexer, node, attval, (short)51);
                    break;
                }
            }
        }
    }

    public static class CheckBool
    implements AttrCheck {
        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
        }
    }

    public static class CheckValign
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"top", "middle", "bottom", "baseline"};
        private static final String[] VALID_VALUES_IMG = new String[]{"left", "right"};
        private static final String[] VALID_VALUES_PROPRIETARY = new String[]{"texttop", "absmiddle", "absbottom", "textbottom"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
            String value = attval.value;
            if (TidyUtils.isInValuesIgnoreCase(VALID_VALUES, value)) {
                return;
            }
            if (TidyUtils.isInValuesIgnoreCase(VALID_VALUES_IMG, value)) {
                if (node.tag == null || (node.tag.model & 0x10000) == 0) {
                    lexer.report.attrError(lexer, node, attval, (short)51);
                }
            } else if (TidyUtils.isInValuesIgnoreCase(VALID_VALUES_PROPRIETARY, value)) {
                lexer.constrainVersion(448);
                lexer.report.attrError(lexer, node, attval, (short)54);
            } else {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckAlign
    implements AttrCheck {
        private static final String[] VALID_VALUES = new String[]{"left", "center", "right", "justify"};

        public void check(Lexer lexer, Node node, AttVal attval) {
            if (node.tag != null && (node.tag.model & 0x10000) != 0) {
                VALIGN.check(lexer, node, attval);
                return;
            }
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            attval.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(VALID_VALUES, attval.value)) {
                lexer.report.attrError(lexer, node, attval, (short)51);
            }
        }
    }

    public static class CheckScript
    implements AttrCheck {
        public void check(Lexer lexer, Node node, AttVal attval) {
        }
    }

    public static class CheckUrl
    implements AttrCheck {
        public void check(Lexer lexer, Node node, AttVal attval) {
            char c;
            boolean escapeFound = false;
            boolean backslashFound = false;
            int i = 0;
            if (attval.value == null) {
                lexer.report.attrError(lexer, node, attval, (short)50);
                return;
            }
            String p = attval.value;
            for (i = 0; i < p.length(); ++i) {
                c = p.charAt(i);
                if (c == '\\') {
                    backslashFound = true;
                    continue;
                }
                if (c <= '~' && c > ' ' && c != '<' && c != '>') continue;
                escapeFound = true;
            }
            if (lexer.configuration.fixBackslash && backslashFound) {
                p = attval.value = attval.value.replace('\\', '/');
            }
            if (lexer.configuration.fixUri && escapeFound) {
                StringBuffer dest = new StringBuffer();
                for (i = 0; i < p.length(); ++i) {
                    c = p.charAt(i);
                    if (c > '~' || c <= ' ' || c == '<' || c == '>') {
                        dest.append('%');
                        dest.append(Integer.toHexString(c).toUpperCase());
                        continue;
                    }
                    dest.append(c);
                }
                attval.value = dest.toString();
            }
            if (backslashFound) {
                if (lexer.configuration.fixBackslash) {
                    lexer.report.attrError(lexer, node, attval, (short)62);
                } else {
                    lexer.report.attrError(lexer, node, attval, (short)61);
                }
            }
            if (escapeFound) {
                if (lexer.configuration.fixUri) {
                    lexer.report.attrError(lexer, node, attval, (short)64);
                } else {
                    lexer.report.attrError(lexer, node, attval, (short)63);
                }
                lexer.badChars = (short)(lexer.badChars | 0x51);
            }
        }
    }
}


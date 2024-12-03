/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.tidy.AttVal;
import org.w3c.tidy.Attribute;
import org.w3c.tidy.AttributeTable;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.Node;
import org.w3c.tidy.TagCheck;

public final class TagCheckImpl {
    public static final TagCheck HTML = new CheckHTML();
    public static final TagCheck SCRIPT = new CheckSCRIPT();
    public static final TagCheck TABLE = new CheckTABLE();
    public static final TagCheck CAPTION = new CheckCaption();
    public static final TagCheck IMG = new CheckIMG();
    public static final TagCheck AREA = new CheckAREA();
    public static final TagCheck ANCHOR = new CheckAnchor();
    public static final TagCheck MAP = new CheckMap();
    public static final TagCheck STYLE = new CheckSTYLE();
    public static final TagCheck TABLECELL = new CheckTableCell();
    public static final TagCheck LINK = new CheckLINK();
    public static final TagCheck HR = new CheckHR();
    public static final TagCheck FORM = new CheckForm();
    public static final TagCheck META = new CheckMeta();

    private TagCheckImpl() {
    }

    public static class CheckLINK
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            AttVal type;
            AttVal rel = node.getAttrByName("rel");
            node.checkAttributes(lexer);
            if (rel != null && rel.value != null && rel.value.equals("stylesheet") && (type = node.getAttrByName("type")) == null) {
                AttVal missingType = new AttVal(null, null, 34, "type", "");
                lexer.report.attrError(lexer, node, missingType, (short)49);
                node.addAttribute("type", "text/css");
            }
        }
    }

    public static class CheckTableCell
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            node.checkAttributes(lexer);
            if (node.getAttrByName("width") != null || node.getAttrByName("height") != null) {
                lexer.constrainVersion(-5);
            }
        }
    }

    public static class CheckMeta
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            AttVal content = node.getAttrByName("content");
            node.checkAttributes(lexer);
            if (content == null) {
                AttVal missingAttribute = new AttVal(null, null, 34, "content", "");
                lexer.report.attrError(lexer, node, missingAttribute, (short)49);
            }
        }
    }

    public static class CheckForm
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            AttVal action = node.getAttrByName("action");
            node.checkAttributes(lexer);
            if (action == null) {
                AttVal missingAttribute = new AttVal(null, null, 34, "action", "");
                lexer.report.attrError(lexer, node, missingAttribute, (short)49);
            }
        }
    }

    public static class CheckSTYLE
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            AttVal type = node.getAttrByName("type");
            node.checkAttributes(lexer);
            if (type == null) {
                AttVal missingType = new AttVal(null, null, 34, "type", "");
                lexer.report.attrError(lexer, node, missingType, (short)49);
                node.addAttribute("type", "text/css");
            }
        }
    }

    public static class CheckMap
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            node.checkAttributes(lexer);
            lexer.fixId(node);
        }
    }

    public static class CheckAnchor
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            node.checkAttributes(lexer);
            lexer.fixId(node);
        }
    }

    public static class CheckAREA
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            boolean hasAlt = false;
            boolean hasHref = false;
            AttVal attval = node.attributes;
            while (attval != null) {
                Attribute attribute = attval.checkAttribute(lexer, node);
                if (attribute == AttributeTable.attrAlt) {
                    hasAlt = true;
                } else if (attribute == AttributeTable.attrHref) {
                    hasHref = true;
                }
                attval = attval.next;
            }
            if (!hasAlt) {
                lexer.badAccess = (short)(lexer.badAccess | 2);
                AttVal missingAlt = new AttVal(null, null, 34, "alt", "");
                lexer.report.attrError(lexer, node, missingAlt, (short)49);
            }
            if (!hasHref) {
                AttVal missingHref = new AttVal(null, null, 34, "href", "");
                lexer.report.attrError(lexer, node, missingHref, (short)49);
            }
        }
    }

    public static class CheckIMG
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            boolean hasAlt = false;
            boolean hasSrc = false;
            boolean hasUseMap = false;
            boolean hasIsMap = false;
            boolean hasDataFld = false;
            AttVal attval = node.attributes;
            while (attval != null) {
                Attribute attribute = attval.checkAttribute(lexer, node);
                if (attribute == AttributeTable.attrAlt) {
                    hasAlt = true;
                } else if (attribute == AttributeTable.attrSrc) {
                    hasSrc = true;
                } else if (attribute == AttributeTable.attrUsemap) {
                    hasUseMap = true;
                } else if (attribute == AttributeTable.attrIsmap) {
                    hasIsMap = true;
                } else if (attribute == AttributeTable.attrDatafld) {
                    hasDataFld = true;
                } else if (attribute == AttributeTable.attrWidth || attribute == AttributeTable.attrHeight) {
                    lexer.constrainVersion(-2);
                }
                attval = attval.next;
            }
            if (!hasAlt) {
                lexer.badAccess = (short)(lexer.badAccess | 1);
                AttVal missingAlt = new AttVal(null, null, 34, "alt", "");
                lexer.report.attrError(lexer, node, missingAlt, (short)49);
                if (lexer.configuration.altText != null) {
                    node.addAttribute("alt", lexer.configuration.altText);
                }
            }
            if (!hasSrc && !hasDataFld) {
                AttVal missingSrc = new AttVal(null, null, 34, "src", "");
                lexer.report.attrError(lexer, node, missingSrc, (short)49);
            }
            if (hasIsMap && !hasUseMap) {
                AttVal missingIsMap = new AttVal(null, null, 34, "ismap", "");
                lexer.report.attrError(lexer, node, missingIsMap, (short)56);
            }
        }
    }

    public static class CheckHR
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            AttVal av = node.getAttrByName("src");
            node.checkAttributes(lexer);
            if (av != null) {
                lexer.report.attrError(lexer, node, av, (short)54);
            }
        }
    }

    public static class CheckCaption
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            String value = null;
            node.checkAttributes(lexer);
            AttVal attval = node.attributes;
            while (attval != null) {
                if ("align".equalsIgnoreCase(attval.attribute)) {
                    value = attval.value;
                    break;
                }
                attval = attval.next;
            }
            if (value != null) {
                if ("left".equalsIgnoreCase(value) || "right".equalsIgnoreCase(value)) {
                    lexer.constrainVersion(8);
                } else if ("top".equalsIgnoreCase(value) || "bottom".equalsIgnoreCase(value)) {
                    lexer.constrainVersion(-4);
                } else {
                    lexer.report.attrError(lexer, node, attval, (short)51);
                }
            }
        }
    }

    public static class CheckTABLE
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            boolean hasSummary = false;
            AttVal attval = node.attributes;
            while (attval != null) {
                Attribute attribute = attval.checkAttribute(lexer, node);
                if (attribute == AttributeTable.attrSummary) {
                    hasSummary = true;
                }
                attval = attval.next;
            }
            if (!hasSummary && lexer.doctype != 1 && lexer.doctype != 2) {
                lexer.badAccess = (short)(lexer.badAccess | 4);
            }
            if (lexer.configuration.xmlOut && (attval = node.getAttrByName("border")) != null && attval.value == null) {
                attval.value = "1";
            }
            if ((attval = node.getAttrByName("height")) != null) {
                lexer.report.attrError(lexer, node, attval, (short)53);
                lexer.versions = (short)(lexer.versions & 0x1C0);
            }
        }
    }

    public static class CheckSCRIPT
    implements TagCheck {
        public void check(Lexer lexer, Node node) {
            node.checkAttributes(lexer);
            AttVal lang = node.getAttrByName("language");
            AttVal type = node.getAttrByName("type");
            if (type == null) {
                AttVal missingType = new AttVal(null, null, 34, "type", "");
                lexer.report.attrError(lexer, node, missingType, (short)49);
                if (lang != null) {
                    String str = lang.value;
                    if ("javascript".equalsIgnoreCase(str) || "jscript".equalsIgnoreCase(str)) {
                        node.addAttribute("type", "text/javascript");
                    } else if ("vbscript".equalsIgnoreCase(str)) {
                        node.addAttribute("type", "text/vbscript");
                    }
                } else {
                    node.addAttribute("type", "text/javascript");
                }
            }
        }
    }

    public static class CheckHTML
    implements TagCheck {
        private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

        public void check(Lexer lexer, Node node) {
            AttVal xmlns = node.getAttrByName("xmlns");
            if (xmlns != null && XHTML_NAMESPACE.equals(xmlns.value)) {
                lexer.isvoyager = true;
                if (!lexer.configuration.htmlOut) {
                    lexer.configuration.xHTML = true;
                }
                lexer.configuration.xmlOut = true;
                lexer.configuration.upperCaseTags = false;
                lexer.configuration.upperCaseAttrs = false;
            }
            AttVal attval = node.attributes;
            while (attval != null) {
                attval.checkAttribute(lexer, node);
                attval = attval.next;
            }
        }
    }
}


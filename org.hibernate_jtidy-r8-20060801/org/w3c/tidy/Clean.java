/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.tidy.AttVal;
import org.w3c.tidy.AttributeTable;
import org.w3c.tidy.Dict;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.Node;
import org.w3c.tidy.PPrint;
import org.w3c.tidy.ParserImpl;
import org.w3c.tidy.Style;
import org.w3c.tidy.StyleProp;
import org.w3c.tidy.TagTable;
import org.w3c.tidy.TidyUtils;

public class Clean {
    private int classNum = 1;
    private TagTable tt;

    public Clean(TagTable tagTable) {
        this.tt = tagTable;
    }

    private StyleProp insertProperty(StyleProp props, String name, String value) {
        StyleProp prev = null;
        StyleProp first = props;
        while (props != null) {
            int cmp = props.name.compareTo(name);
            if (cmp == 0) {
                return first;
            }
            if (cmp > 0) {
                StyleProp prop = new StyleProp(name, value, props);
                if (prev != null) {
                    prev.next = prop;
                } else {
                    first = prop;
                }
                return first;
            }
            prev = props;
            props = props.next;
        }
        StyleProp prop = new StyleProp(name, value, null);
        if (prev != null) {
            prev.next = prop;
        } else {
            first = prop;
        }
        return first;
    }

    private StyleProp createProps(StyleProp prop, String style) {
        int valueStart = 0;
        int nameStart = 0;
        nameStart = 0;
        while (nameStart < style.length()) {
            int valueEnd;
            int nameEnd;
            while (nameStart < style.length() && style.charAt(nameStart) == ' ') {
                ++nameStart;
            }
            for (nameEnd = nameStart; nameEnd < style.length(); ++nameEnd) {
                if (style.charAt(nameEnd) != ':') continue;
                valueStart = nameEnd + 1;
                break;
            }
            if (nameEnd >= style.length() || style.charAt(nameEnd) != ':') break;
            while (valueStart < style.length() && style.charAt(valueStart) == ' ') {
                ++valueStart;
            }
            boolean more = false;
            for (valueEnd = valueStart; valueEnd < style.length(); ++valueEnd) {
                if (style.charAt(valueEnd) != ';') continue;
                more = true;
                break;
            }
            prop = this.insertProperty(prop, style.substring(nameStart, nameEnd), style.substring(valueStart, valueEnd));
            if (!more) break;
            nameStart = valueEnd + 1;
        }
        return prop;
    }

    private String createPropString(StyleProp props) {
        String style = "";
        int len = 0;
        StyleProp prop = props;
        while (prop != null) {
            len += prop.name.length() + 2;
            len += prop.value.length() + 2;
            prop = prop.next;
        }
        prop = props;
        while (prop != null) {
            style = style.concat(prop.name);
            style = style.concat(": ");
            style = style.concat(prop.value);
            if (prop.next == null) break;
            style = style.concat("; ");
            prop = prop.next;
        }
        return style;
    }

    private String addProperty(String style, String property) {
        StyleProp prop = this.createProps(null, style);
        prop = this.createProps(prop, property);
        style = this.createPropString(prop);
        return style;
    }

    private String gensymClass(Lexer lexer, String tag) {
        String str = lexer.configuration.cssPrefix == null ? lexer.configuration.cssPrefix + this.classNum : "c" + this.classNum;
        ++this.classNum;
        return str;
    }

    private String findStyle(Lexer lexer, String tag, String properties) {
        Style style = lexer.styles;
        while (style != null) {
            if (style.tag.equals(tag) && style.properties.equals(properties)) {
                return style.tagClass;
            }
            style = style.next;
        }
        lexer.styles = style = new Style(tag, this.gensymClass(lexer, tag), properties, lexer.styles);
        return style.tagClass;
    }

    private void style2Rule(Lexer lexer, Node node) {
        AttVal styleattr = node.getAttrByName("style");
        if (styleattr != null) {
            String classname = this.findStyle(lexer, node.element, styleattr.value);
            AttVal classattr = node.getAttrByName("class");
            if (classattr != null) {
                classattr.value = classattr.value + " " + classname;
                node.removeAttribute(styleattr);
            } else {
                styleattr.attribute = "class";
                styleattr.value = classname;
            }
        }
    }

    private void addColorRule(Lexer lexer, String selector, String color) {
        if (color != null) {
            lexer.addStringLiteral(selector);
            lexer.addStringLiteral(" { color: ");
            lexer.addStringLiteral(color);
            lexer.addStringLiteral(" }\n");
        }
    }

    private void cleanBodyAttrs(Lexer lexer, Node body) {
        String bgurl = null;
        String bgcolor = null;
        String color = null;
        AttVal attr = body.getAttrByName("background");
        if (attr != null) {
            bgurl = attr.value;
            attr.value = null;
            body.removeAttribute(attr);
        }
        if ((attr = body.getAttrByName("bgcolor")) != null) {
            bgcolor = attr.value;
            attr.value = null;
            body.removeAttribute(attr);
        }
        if ((attr = body.getAttrByName("text")) != null) {
            color = attr.value;
            attr.value = null;
            body.removeAttribute(attr);
        }
        if (bgurl != null || bgcolor != null || color != null) {
            lexer.addStringLiteral(" body {\n");
            if (bgurl != null) {
                lexer.addStringLiteral("  background-image: url(");
                lexer.addStringLiteral(bgurl);
                lexer.addStringLiteral(");\n");
            }
            if (bgcolor != null) {
                lexer.addStringLiteral("  background-color: ");
                lexer.addStringLiteral(bgcolor);
                lexer.addStringLiteral(";\n");
            }
            if (color != null) {
                lexer.addStringLiteral("  color: ");
                lexer.addStringLiteral(color);
                lexer.addStringLiteral(";\n");
            }
            lexer.addStringLiteral(" }\n");
        }
        if ((attr = body.getAttrByName("link")) != null) {
            this.addColorRule(lexer, " :link", attr.value);
            body.removeAttribute(attr);
        }
        if ((attr = body.getAttrByName("vlink")) != null) {
            this.addColorRule(lexer, " :visited", attr.value);
            body.removeAttribute(attr);
        }
        if ((attr = body.getAttrByName("alink")) != null) {
            this.addColorRule(lexer, " :active", attr.value);
            body.removeAttribute(attr);
        }
    }

    private boolean niceBody(Lexer lexer, Node doc) {
        Node body = doc.findBody(lexer.configuration.tt);
        if (body != null && (body.getAttrByName("background") != null || body.getAttrByName("bgcolor") != null || body.getAttrByName("text") != null || body.getAttrByName("link") != null || body.getAttrByName("vlink") != null || body.getAttrByName("alink") != null)) {
            lexer.badLayout = (short)(lexer.badLayout | 0x10);
            return false;
        }
        return true;
    }

    private void createStyleElement(Lexer lexer, Node doc) {
        if (lexer.styles == null && this.niceBody(lexer, doc)) {
            return;
        }
        Node node = lexer.newNode((short)5, null, 0, 0, "style");
        node.implicit = true;
        AttVal av = new AttVal(null, null, 34, "type", "text/css");
        av.dict = AttributeTable.getDefaultAttributeTable().findAttribute(av);
        node.attributes = av;
        Node body = doc.findBody(lexer.configuration.tt);
        lexer.txtstart = lexer.lexsize;
        if (body != null) {
            this.cleanBodyAttrs(lexer, body);
        }
        Style style = lexer.styles;
        while (style != null) {
            lexer.addCharToLexer(32);
            lexer.addStringLiteral(style.tag);
            lexer.addCharToLexer(46);
            lexer.addStringLiteral(style.tagClass);
            lexer.addCharToLexer(32);
            lexer.addCharToLexer(123);
            lexer.addStringLiteral(style.properties);
            lexer.addCharToLexer(125);
            lexer.addCharToLexer(10);
            style = style.next;
        }
        lexer.txtend = lexer.lexsize;
        node.insertNodeAtEnd(lexer.newNode((short)4, lexer.lexbuf, lexer.txtstart, lexer.txtend));
        Node head = doc.findHEAD(lexer.configuration.tt);
        if (head != null) {
            head.insertNodeAtEnd(node);
        }
    }

    private void fixNodeLinks(Node node) {
        if (node.prev != null) {
            node.prev.next = node;
        } else {
            node.parent.content = node;
        }
        if (node.next != null) {
            node.next.prev = node;
        } else {
            node.parent.last = node;
        }
        Node child = node.content;
        while (child != null) {
            child.parent = node;
            child = child.next;
        }
    }

    private void stripOnlyChild(Node node) {
        Node child = node.content;
        node.content = child.content;
        node.last = child.last;
        child.content = null;
        child = node.content;
        while (child != null) {
            child.parent = node;
            child = child.next;
        }
    }

    private void discardContainer(Node element, Node[] pnode) {
        Node parent = element.parent;
        if (element.content != null) {
            element.last.next = element.next;
            if (element.next != null) {
                element.next.prev = element.last;
                element.last.next = element.next;
            } else {
                parent.last = element.last;
            }
            if (element.prev != null) {
                element.content.prev = element.prev;
                element.prev.next = element.content;
            } else {
                parent.content = element.content;
            }
            Node node = element.content;
            while (node != null) {
                node.parent = parent;
                node = node.next;
            }
            pnode[0] = element.content;
        } else {
            if (element.next != null) {
                element.next.prev = element.prev;
            } else {
                parent.last = element.prev;
            }
            if (element.prev != null) {
                element.prev.next = element.next;
            } else {
                parent.content = element.next;
            }
            pnode[0] = element.next;
        }
        element.next = null;
        element.content = null;
    }

    private void addStyleProperty(Node node, String property) {
        AttVal av = node.attributes;
        while (av != null && !av.attribute.equals("style")) {
            av = av.next;
        }
        if (av != null) {
            String s;
            av.value = s = this.addProperty(av.value, property);
        } else {
            av = new AttVal(node.attributes, null, 34, "style", property);
            av.dict = AttributeTable.getDefaultAttributeTable().findAttribute(av);
            node.attributes = av;
        }
    }

    private String mergeProperties(String s1, String s2) {
        StyleProp prop = this.createProps(null, s1);
        prop = this.createProps(prop, s2);
        String s = this.createPropString(prop);
        return s;
    }

    private void mergeClasses(Node node, Node child) {
        String s2 = null;
        AttVal av = child.attributes;
        while (av != null) {
            if ("class".equals(av.attribute)) {
                s2 = av.value;
                break;
            }
            av = av.next;
        }
        String s1 = null;
        av = node.attributes;
        while (av != null) {
            if ("class".equals(av.attribute)) {
                s1 = av.value;
                break;
            }
            av = av.next;
        }
        if (s1 != null) {
            if (s2 != null) {
                String names;
                av.value = names = s1 + ' ' + s2;
            }
        } else if (s2 != null) {
            av = new AttVal(node.attributes, null, 34, "class", s2);
            av.dict = AttributeTable.getDefaultAttributeTable().findAttribute(av);
            node.attributes = av;
        }
    }

    private void mergeStyles(Node node, Node child) {
        this.mergeClasses(node, child);
        String s2 = null;
        AttVal av = child.attributes;
        while (av != null) {
            if (av.attribute.equals("style")) {
                s2 = av.value;
                break;
            }
            av = av.next;
        }
        String s1 = null;
        av = node.attributes;
        while (av != null) {
            if (av.attribute.equals("style")) {
                s1 = av.value;
                break;
            }
            av = av.next;
        }
        if (s1 != null) {
            if (s2 != null) {
                String style;
                av.value = style = this.mergeProperties(s1, s2);
            }
        } else if (s2 != null) {
            av = new AttVal(node.attributes, null, 34, "style", s2);
            av.dict = AttributeTable.getDefaultAttributeTable().findAttribute(av);
            node.attributes = av;
        }
    }

    private String fontSize2Name(String size) {
        String[] sizes = new String[]{"60%", "70%", "80%", null, "120%", "150%", "200%"};
        if (size.length() > 0 && '0' <= size.charAt(0) && size.charAt(0) <= '6') {
            int n = size.charAt(0) - 48;
            return sizes[n];
        }
        if (size.length() > 0 && size.charAt(0) == '-') {
            if (size.length() > 1 && '0' <= size.charAt(1) && size.charAt(1) <= '6') {
                double x = 1.0;
                for (int n = size.charAt(1) - 48; n > 0; --n) {
                    x *= 0.8;
                }
                String buf = "" + (int)(x *= 100.0) + "%";
                return buf;
            }
            return "smaller";
        }
        if (size.length() > 1 && '0' <= size.charAt(1) && size.charAt(1) <= '6') {
            double x = 1.0;
            for (int n = size.charAt(1) - 48; n > 0; --n) {
                x *= 1.2;
            }
            String buf = "" + (int)(x *= 100.0) + "%";
            return buf;
        }
        return "larger";
    }

    private void addFontFace(Node node, String face) {
        this.addStyleProperty(node, "font-family: " + face);
    }

    private void addFontSize(Node node, String size) {
        if (size == null) {
            return;
        }
        if ("6".equals(size) && node.tag == this.tt.tagP) {
            node.element = "h1";
            this.tt.findTag(node);
            return;
        }
        if ("5".equals(size) && node.tag == this.tt.tagP) {
            node.element = "h2";
            this.tt.findTag(node);
            return;
        }
        if ("4".equals(size) && node.tag == this.tt.tagP) {
            node.element = "h3";
            this.tt.findTag(node);
            return;
        }
        String value = this.fontSize2Name(size);
        if (value != null) {
            this.addStyleProperty(node, "font-size: " + value);
        }
    }

    private void addFontColor(Node node, String color) {
        this.addStyleProperty(node, "color: " + color);
    }

    private void addAlign(Node node, String align) {
        this.addStyleProperty(node, "text-align: " + align.toLowerCase());
    }

    private void addFontStyles(Node node, AttVal av) {
        while (av != null) {
            if (av.attribute.equals("face")) {
                this.addFontFace(node, av.value);
            } else if (av.attribute.equals("size")) {
                this.addFontSize(node, av.value);
            } else if (av.attribute.equals("color")) {
                this.addFontColor(node, av.value);
            }
            av = av.next;
        }
    }

    private void textAlign(Lexer lexer, Node node) {
        AttVal prev = null;
        AttVal av = node.attributes;
        while (av != null) {
            if (av.attribute.equals("align")) {
                if (prev != null) {
                    prev.next = av.next;
                } else {
                    node.attributes = av.next;
                }
                if (av.value == null) break;
                this.addAlign(node, av.value);
                break;
            }
            prev = av;
            av = av.next;
        }
    }

    private boolean dir2Div(Lexer lexer, Node node) {
        if (node.tag == this.tt.tagDir || node.tag == this.tt.tagUl || node.tag == this.tt.tagOl) {
            Node child = node.content;
            if (child == null) {
                return false;
            }
            if (child.next != null) {
                return false;
            }
            if (child.tag != this.tt.tagLi) {
                return false;
            }
            if (!child.implicit) {
                return false;
            }
            node.tag = this.tt.tagDiv;
            node.element = "div";
            this.addStyleProperty(node, "margin-left: 2em");
            this.stripOnlyChild(node);
            return true;
        }
        return false;
    }

    private boolean center2Div(Lexer lexer, Node node, Node[] pnode) {
        if (node.tag == this.tt.tagCenter) {
            if (lexer.configuration.dropFontTags) {
                if (node.content != null) {
                    Node last = node.last;
                    Node parent = node.parent;
                    this.discardContainer(node, pnode);
                    node = lexer.inferredTag("br");
                    if (last.next != null) {
                        last.next.prev = node;
                    }
                    node.next = last.next;
                    last.next = node;
                    node.prev = last;
                    if (parent.last == last) {
                        parent.last = node;
                    }
                    node.parent = parent;
                } else {
                    Node prev = node.prev;
                    Node next = node.next;
                    Node parent = node.parent;
                    this.discardContainer(node, pnode);
                    node = lexer.inferredTag("br");
                    node.next = next;
                    node.prev = prev;
                    node.parent = parent;
                    if (next != null) {
                        next.prev = node;
                    } else {
                        parent.last = node;
                    }
                    if (prev != null) {
                        prev.next = node;
                    } else {
                        parent.content = node;
                    }
                }
                return true;
            }
            node.tag = this.tt.tagDiv;
            node.element = "div";
            this.addStyleProperty(node, "text-align: center");
            return true;
        }
        return false;
    }

    private boolean mergeDivs(Lexer lexer, Node node) {
        if (node.tag != this.tt.tagDiv) {
            return false;
        }
        Node child = node.content;
        if (child == null) {
            return false;
        }
        if (child.tag != this.tt.tagDiv) {
            return false;
        }
        if (child.next != null) {
            return false;
        }
        this.mergeStyles(node, child);
        this.stripOnlyChild(node);
        return true;
    }

    private boolean nestedList(Lexer lexer, Node node, Node[] pnode) {
        if (node.tag == this.tt.tagUl || node.tag == this.tt.tagOl) {
            Node child = node.content;
            if (child == null) {
                return false;
            }
            if (child.next != null) {
                return false;
            }
            Node list = child.content;
            if (list == null) {
                return false;
            }
            if (list.tag != node.tag) {
                return false;
            }
            pnode[0] = list;
            list.prev = node.prev;
            list.next = node.next;
            list.parent = node.parent;
            this.fixNodeLinks(list);
            child.content = null;
            node.content = null;
            node.next = null;
            node = null;
            if (list.prev != null && (list.prev.tag == this.tt.tagUl || list.prev.tag == this.tt.tagOl)) {
                node = list;
                list = node.prev;
                list.next = node.next;
                if (list.next != null) {
                    list.next.prev = list;
                }
                node.parent = child = list.last;
                node.next = null;
                node.prev = child.last;
                this.fixNodeLinks(node);
                this.cleanNode(lexer, node);
            }
            return true;
        }
        return false;
    }

    private boolean blockStyle(Lexer lexer, Node node) {
        if ((node.tag.model & 0xE8) != 0 && node.tag != this.tt.tagTable && node.tag != this.tt.tagTr && node.tag != this.tt.tagLi) {
            Node child;
            if (node.tag != this.tt.tagCaption) {
                this.textAlign(lexer, node);
            }
            if ((child = node.content) == null) {
                return false;
            }
            if (child.next != null) {
                return false;
            }
            if (child.tag == this.tt.tagB) {
                this.mergeStyles(node, child);
                this.addStyleProperty(node, "font-weight: bold");
                this.stripOnlyChild(node);
                return true;
            }
            if (child.tag == this.tt.tagI) {
                this.mergeStyles(node, child);
                this.addStyleProperty(node, "font-style: italic");
                this.stripOnlyChild(node);
                return true;
            }
            if (child.tag == this.tt.tagFont) {
                this.mergeStyles(node, child);
                this.addFontStyles(node, child.attributes);
                this.stripOnlyChild(node);
                return true;
            }
        }
        return false;
    }

    private boolean inlineStyle(Lexer lexer, Node node, Node[] pnode) {
        if (node.tag != this.tt.tagFont && (node.tag.model & 0x210) != 0) {
            Node child = node.content;
            if (child == null) {
                return false;
            }
            if (child.next != null) {
                return false;
            }
            if (child.tag == this.tt.tagB && lexer.configuration.logicalEmphasis) {
                this.mergeStyles(node, child);
                this.addStyleProperty(node, "font-weight: bold");
                this.stripOnlyChild(node);
                return true;
            }
            if (child.tag == this.tt.tagI && lexer.configuration.logicalEmphasis) {
                this.mergeStyles(node, child);
                this.addStyleProperty(node, "font-style: italic");
                this.stripOnlyChild(node);
                return true;
            }
            if (child.tag == this.tt.tagFont) {
                this.mergeStyles(node, child);
                this.addFontStyles(node, child.attributes);
                this.stripOnlyChild(node);
                return true;
            }
        }
        return false;
    }

    private boolean font2Span(Lexer lexer, Node node, Node[] pnode) {
        if (node.tag == this.tt.tagFont) {
            if (lexer.configuration.dropFontTags) {
                this.discardContainer(node, pnode);
                return false;
            }
            if (node.parent.content == node && node.next == null) {
                return false;
            }
            this.addFontStyles(node, node.attributes);
            AttVal av = node.attributes;
            AttVal style = null;
            while (av != null) {
                AttVal next = av.next;
                if (av.attribute.equals("style")) {
                    av.next = null;
                    style = av;
                }
                av = next;
            }
            node.attributes = style;
            node.tag = this.tt.tagSpan;
            node.element = "span";
            return true;
        }
        return false;
    }

    private Node cleanNode(Lexer lexer, Node node) {
        Node next = null;
        Node[] o = new Node[1];
        boolean b = false;
        next = node;
        while (node != null && node.isElement()) {
            o[0] = next;
            b = this.dir2Div(lexer, node);
            next = o[0];
            if (!b) {
                b = this.nestedList(lexer, node, o);
                next = o[0];
                if (b) {
                    return next;
                }
                b = this.center2Div(lexer, node, o);
                next = o[0];
                if (!b) {
                    b = this.mergeDivs(lexer, node);
                    next = o[0];
                    if (!b) {
                        b = this.blockStyle(lexer, node);
                        next = o[0];
                        if (!b) {
                            b = this.inlineStyle(lexer, node, o);
                            next = o[0];
                            if (!b) {
                                b = this.font2Span(lexer, node, o);
                                next = o[0];
                                if (!b) break;
                            }
                        }
                    }
                }
            }
            node = next;
        }
        return next;
    }

    private Node createStyleProperties(Lexer lexer, Node node, Node[] prepl) {
        Node child = node.content;
        if (child != null) {
            Node[] repl = new Node[]{node};
            while (child != null) {
                child = this.createStyleProperties(lexer, child, repl);
                if (repl[0] != node) {
                    return repl[0];
                }
                if (child == null) continue;
                child = child.next;
            }
        }
        return this.cleanNode(lexer, node);
    }

    private void defineStyleRules(Lexer lexer, Node node) {
        if (node.content != null) {
            Node child = node.content;
            while (child != null) {
                this.defineStyleRules(lexer, child);
                child = child.next;
            }
        }
        this.style2Rule(lexer, node);
    }

    public void cleanTree(Lexer lexer, Node doc) {
        Node[] repl = new Node[]{doc};
        doc = this.createStyleProperties(lexer, doc, repl);
        if (!lexer.configuration.makeClean) {
            this.defineStyleRules(lexer, doc);
            this.createStyleElement(lexer, doc);
        }
    }

    public void nestedEmphasis(Node node) {
        Node[] o = new Node[1];
        while (node != null) {
            Node next = node.next;
            if ((node.tag == this.tt.tagB || node.tag == this.tt.tagI) && node.parent != null && node.parent.tag == node.tag) {
                o[0] = next;
                this.discardContainer(node, o);
                node = next = o[0];
                continue;
            }
            if (node.content != null) {
                this.nestedEmphasis(node.content);
            }
            node = next;
        }
    }

    public void emFromI(Node node) {
        while (node != null) {
            if (node.tag == this.tt.tagI) {
                node.element = this.tt.tagEm.name;
                node.tag = this.tt.tagEm;
            } else if (node.tag == this.tt.tagB) {
                node.element = this.tt.tagStrong.name;
                node.tag = this.tt.tagStrong;
            }
            if (node.content != null) {
                this.emFromI(node.content);
            }
            node = node.next;
        }
    }

    public void list2BQ(Node node) {
        while (node != null) {
            if (node.content != null) {
                this.list2BQ(node.content);
            }
            if (node.tag != null && node.tag.getParser() == ParserImpl.LIST && node.hasOneChild() && node.content.implicit) {
                this.stripOnlyChild(node);
                node.element = this.tt.tagBlockquote.name;
                node.tag = this.tt.tagBlockquote;
                node.implicit = true;
            }
            node = node.next;
        }
    }

    public void bQ2Div(Node node) {
        while (node != null) {
            if (node.tag == this.tt.tagBlockquote && node.implicit) {
                int indent = 1;
                while (node.hasOneChild() && node.content.tag == this.tt.tagBlockquote && node.implicit) {
                    ++indent;
                    this.stripOnlyChild(node);
                }
                if (node.content != null) {
                    this.bQ2Div(node.content);
                }
                String indentBuf = "margin-left: " + new Integer(2 * indent).toString() + "em";
                node.element = this.tt.tagDiv.name;
                node.tag = this.tt.tagDiv;
                AttVal attval = node.getAttrByName("style");
                if (attval != null && attval.value != null) {
                    attval.value = indentBuf + "; " + attval.value;
                } else {
                    node.addAttribute("style", indentBuf);
                }
            } else if (node.content != null) {
                this.bQ2Div(node.content);
            }
            node = node.next;
        }
    }

    Node findEnclosingCell(Node node) {
        Node check = node;
        while (check != null) {
            if (check.tag == this.tt.tagTd) {
                return check;
            }
            check = check.parent;
        }
        return null;
    }

    public Node pruneSection(Lexer lexer, Node node) {
        while (true) {
            if ((node = Node.discardElement(node)) == null) {
                return null;
            }
            if (node.type != 9) continue;
            if (TidyUtils.getString(node.textarray, node.start, 2).equals("if")) {
                node = this.pruneSection(lexer, node);
                continue;
            }
            if (TidyUtils.getString(node.textarray, node.start, 5).equals("endif")) break;
        }
        node = Node.discardElement(node);
        return node;
    }

    public void dropSections(Lexer lexer, Node node) {
        while (node != null) {
            if (node.type == 9) {
                if (TidyUtils.getString(node.textarray, node.start, 2).equals("if") && !TidyUtils.getString(node.textarray, node.start, 7).equals("if !vml")) {
                    node = this.pruneSection(lexer, node);
                    continue;
                }
                node = Node.discardElement(node);
                continue;
            }
            if (node.content != null) {
                this.dropSections(lexer, node.content);
            }
            node = node.next;
        }
    }

    public void purgeWord2000Attributes(Node node) {
        AttVal attr = null;
        AttVal next = null;
        AttVal prev = null;
        attr = node.attributes;
        while (attr != null) {
            next = attr.next;
            if (attr.attribute != null && attr.value != null && attr.attribute.equals("class") && (attr.value.equals("Code") || !attr.value.startsWith("Mso"))) {
                prev = attr;
            } else if (attr.attribute != null && (attr.attribute.equals("class") || attr.attribute.equals("style") || attr.attribute.equals("lang") || attr.attribute.startsWith("x:") || (attr.attribute.equals("height") || attr.attribute.equals("width")) && (node.tag == this.tt.tagTd || node.tag == this.tt.tagTr || node.tag == this.tt.tagTh))) {
                if (prev != null) {
                    prev.next = next;
                } else {
                    node.attributes = next;
                }
            } else {
                prev = attr;
            }
            attr = next;
        }
    }

    public Node stripSpan(Lexer lexer, Node span) {
        Node node;
        Node prev = null;
        this.cleanWord2000(lexer, span.content);
        Node content = span.content;
        if (span.prev != null) {
            prev = span.prev;
        } else if (content != null) {
            node = content;
            content = content.next;
            node.removeNode();
            Node.insertNodeBeforeElement(span, node);
            prev = node;
        }
        while (content != null) {
            node = content;
            content = content.next;
            node.removeNode();
            prev.insertNodeAfterElement(node);
            prev = node;
        }
        if (span.next == null) {
            span.parent.last = prev;
        }
        node = span.next;
        span.content = null;
        Node.discardElement(span);
        return node;
    }

    private void normalizeSpaces(Lexer lexer, Node node) {
        while (node != null) {
            if (node.content != null) {
                this.normalizeSpaces(lexer, node.content);
            }
            if (node.type == 4) {
                int[] c = new int[1];
                int p = node.start;
                for (int i = node.start; i < node.end; ++i) {
                    c[0] = node.textarray[i];
                    if (c[0] > 127) {
                        i += PPrint.getUTF8(node.textarray, i, c);
                    }
                    if (c[0] == 160) {
                        c[0] = 32;
                    }
                    p = PPrint.putUTF8(node.textarray, p, c[0]);
                }
            }
            node = node.next;
        }
    }

    boolean noMargins(Node node) {
        AttVal attval = node.getAttrByName("style");
        if (attval == null || attval.value == null) {
            return false;
        }
        if (attval.value.indexOf("margin-top: 0") == -1) {
            return false;
        }
        return attval.value.indexOf("margin-bottom: 0") != -1;
    }

    boolean singleSpace(Lexer lexer, Node node) {
        if (node.content != null) {
            node = node.content;
            if (node.next != null) {
                return false;
            }
            if (node.type != 4) {
                return false;
            }
            if (node.end - node.start == 1 && lexer.lexbuf[node.start] == 32) {
                return true;
            }
            if (node.end - node.start == 2) {
                int[] c = new int[1];
                PPrint.getUTF8(lexer.lexbuf, node.start, c);
                if (c[0] == 160) {
                    return true;
                }
            }
        }
        return false;
    }

    public void cleanWord2000(Lexer lexer, Node node) {
        Node list = null;
        while (node != null) {
            AttVal attr;
            if (node.tag == this.tt.tagHtml) {
                if (node.getAttrByName("xmlns:o") == null) {
                    return;
                }
                lexer.configuration.tt.freeAttrs(node);
            }
            if (node.tag == this.tt.tagP && this.noMargins(node)) {
                Node.coerceNode(lexer, node, this.tt.tagPre);
                this.purgeWord2000Attributes(node);
                if (node.content != null) {
                    this.cleanWord2000(lexer, node.content);
                }
                Node pre = node;
                node = node.next;
                while (node.tag == this.tt.tagP && this.noMargins(node)) {
                    Node next = node.next;
                    node.removeNode();
                    pre.insertNodeAtEnd(lexer.newLineNode());
                    pre.insertNodeAtEnd(node);
                    this.stripSpan(lexer, node);
                    node = next;
                }
                if (node == null) break;
            }
            if (node.tag != null && TidyUtils.toBoolean(node.tag.model & 8) && this.singleSpace(lexer, node)) {
                node = this.stripSpan(lexer, node);
                continue;
            }
            if (node.tag == this.tt.tagStyle || node.tag == this.tt.tagMeta || node.type == 2) {
                node = Node.discardElement(node);
                continue;
            }
            if (node.tag == this.tt.tagSpan || node.tag == this.tt.tagFont) {
                node = this.stripSpan(lexer, node);
                continue;
            }
            if (node.tag == this.tt.tagLink && (attr = node.getAttrByName("rel")) != null && attr.value != null && attr.value.equals("File-List")) {
                node = Node.discardElement(node);
                continue;
            }
            if (node.content == null && node.tag == this.tt.tagP) {
                node = Node.discardElement(node);
                continue;
            }
            if (node.tag == this.tt.tagP) {
                attr = node.getAttrByName("class");
                AttVal atrStyle = node.getAttrByName("style");
                if (attr != null && attr.value != null && (attr.value.equals("MsoListBullet") || attr.value.equals("MsoListNumber") || atrStyle != null && atrStyle.value.indexOf("mso-list:") != -1)) {
                    Dict listType = this.tt.tagUl;
                    if (attr.value.equals("MsoListNumber")) {
                        listType = this.tt.tagOl;
                    }
                    Node.coerceNode(lexer, node, this.tt.tagLi);
                    if (list == null || list.tag != listType) {
                        list = lexer.inferredTag(listType.name);
                        Node.insertNodeBeforeElement(node, list);
                    }
                    this.purgeWord2000Attributes(node);
                    if (node.content != null) {
                        this.cleanWord2000(lexer, node.content);
                    }
                    node.removeNode();
                    list.insertNodeAtEnd(node);
                    node = list;
                } else if (attr != null && attr.value != null && attr.value.equals("Code")) {
                    Node br = lexer.newLineNode();
                    this.normalizeSpaces(lexer, node);
                    if (list == null || list.tag != this.tt.tagPre) {
                        list = lexer.inferredTag("pre");
                        Node.insertNodeBeforeElement(node, list);
                    }
                    node.removeNode();
                    list.insertNodeAtEnd(node);
                    this.stripSpan(lexer, node);
                    list.insertNodeAtEnd(br);
                    node = list.next;
                } else {
                    list = null;
                }
            } else {
                list = null;
            }
            if (node.type == 5 || node.type == 7) {
                this.purgeWord2000Attributes(node);
            }
            if (node.content != null) {
                this.cleanWord2000(lexer, node.content);
            }
            node = node.next;
        }
    }

    public boolean isWord2000(Node root) {
        Node html = root.findHTML(this.tt);
        if (html != null && html.getAttrByName("xmlns:o") != null) {
            return true;
        }
        Node head = root.findHEAD(this.tt);
        if (head != null) {
            Node node = head.content;
            while (node != null) {
                AttVal attval;
                if (node.tag == this.tt.tagMeta && (attval = node.getAttrByName("name")) != null && attval.value != null && "generator".equals(attval.value) && (attval = node.getAttrByName("content")) != null && attval.value != null && attval.value.indexOf("Microsoft") != -1) {
                    return true;
                }
                node = node.next;
            }
        }
        return false;
    }

    static void bumpObject(Lexer lexer, Node html) {
        if (html == null) {
            return;
        }
        Node head = null;
        Node body = null;
        TagTable tt = lexer.configuration.tt;
        Node node = html.content;
        while (node != null) {
            if (node.tag == tt.tagHead) {
                head = node;
            }
            if (node.tag == tt.tagBody) {
                body = node;
            }
            node = node.next;
        }
        if (head != null && body != null) {
            node = head.content;
            while (node != null) {
                Node next = node.next;
                if (node.tag == tt.tagObject) {
                    boolean bump = false;
                    Node child = node.content;
                    while (child != null) {
                        if (child.type == 4 && !node.isBlank(lexer) || child.tag != tt.tagParam) {
                            bump = true;
                            break;
                        }
                        child = child.next;
                    }
                    if (bump) {
                        node.removeNode();
                        body.insertNodeAtStart(node);
                    }
                }
                node = next;
            }
        }
    }
}


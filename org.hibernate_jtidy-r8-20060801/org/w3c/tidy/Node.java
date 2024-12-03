/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.tidy.AttVal;
import org.w3c.tidy.AttributeTable;
import org.w3c.tidy.DOMCDATASectionImpl;
import org.w3c.tidy.DOMCommentImpl;
import org.w3c.tidy.DOMDocumentImpl;
import org.w3c.tidy.DOMDocumentTypeImpl;
import org.w3c.tidy.DOMElementImpl;
import org.w3c.tidy.DOMNodeImpl;
import org.w3c.tidy.DOMProcessingInstructionImpl;
import org.w3c.tidy.DOMTextImpl;
import org.w3c.tidy.Dict;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.TagTable;
import org.w3c.tidy.TidyUtils;

public class Node
implements Cloneable {
    public static final short ROOT_NODE = 0;
    public static final short DOCTYPE_TAG = 1;
    public static final short COMMENT_TAG = 2;
    public static final short PROC_INS_TAG = 3;
    public static final short TEXT_NODE = 4;
    public static final short START_TAG = 5;
    public static final short END_TAG = 6;
    public static final short START_END_TAG = 7;
    public static final short CDATA_TAG = 8;
    public static final short SECTION_TAG = 9;
    public static final short ASP_TAG = 10;
    public static final short JSTE_TAG = 11;
    public static final short PHP_TAG = 12;
    public static final short XML_DECL = 13;
    private static final String[] NODETYPE_STRING = new String[]{"RootNode", "DocTypeTag", "CommentTag", "ProcInsTag", "TextNode", "StartTag", "EndTag", "StartEndTag", "SectionTag", "AspTag", "PhpTag", "XmlDecl"};
    protected Node parent = null;
    protected Node prev = null;
    protected Node next = null;
    protected Node last = null;
    protected int start;
    protected int end;
    protected byte[] textarray;
    protected short type;
    protected boolean closed;
    protected boolean implicit;
    protected boolean linebreak;
    protected Dict was;
    protected Dict tag;
    protected String element;
    protected AttVal attributes;
    protected Node content;
    protected org.w3c.dom.Node adapter;

    public Node() {
        this(4, null, 0, 0);
    }

    public Node(short type, byte[] textarray, int start, int end) {
        this.start = start;
        this.end = end;
        this.textarray = textarray;
        this.type = type;
        this.closed = false;
        this.implicit = false;
        this.linebreak = false;
        this.was = null;
        this.tag = null;
        this.element = null;
        this.attributes = null;
        this.content = null;
    }

    public Node(short type, byte[] textarray, int start, int end, String element, TagTable tt) {
        this.start = start;
        this.end = end;
        this.textarray = textarray;
        this.type = type;
        this.closed = false;
        this.implicit = false;
        this.linebreak = false;
        this.was = null;
        this.tag = null;
        this.element = element;
        this.attributes = null;
        this.content = null;
        if (type == 5 || type == 7 || type == 6) {
            tt.findTag(this);
        }
    }

    protected Object clone() {
        Node node;
        try {
            node = (Node)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("CloneNotSupportedException " + e.getMessage());
        }
        if (this.textarray != null) {
            node.textarray = new byte[this.end - this.start];
            node.start = 0;
            node.end = this.end - this.start;
            if (node.end > 0) {
                System.arraycopy(this.textarray, this.start, node.textarray, node.start, node.end);
            }
        }
        if (this.attributes != null) {
            node.attributes = (AttVal)this.attributes.clone();
        }
        return node;
    }

    public AttVal getAttrByName(String name) {
        AttVal attr = this.attributes;
        while (!(attr == null || name != null && attr.attribute != null && attr.attribute.equals(name))) {
            attr = attr.next;
        }
        return attr;
    }

    public void checkAttributes(Lexer lexer) {
        AttVal attval = this.attributes;
        while (attval != null) {
            attval.checkAttribute(lexer, this);
            attval = attval.next;
        }
    }

    public void repairDuplicateAttributes(Lexer lexer) {
        AttVal attval = this.attributes;
        while (attval != null) {
            if (attval.asp == null && attval.php == null) {
                AttVal current = attval.next;
                while (current != null) {
                    if (current.asp == null && current.php == null && attval.attribute != null && attval.attribute.equalsIgnoreCase(current.attribute)) {
                        AttVal temp;
                        if ("class".equalsIgnoreCase(current.attribute) && lexer.configuration.joinClasses) {
                            current.value = current.value + " " + attval.value;
                            temp = attval.next;
                            current = temp.next == null ? null : current.next;
                            lexer.report.attrError(lexer, this, attval, (short)68);
                            this.removeAttribute(attval);
                            attval = temp;
                            continue;
                        }
                        if ("style".equalsIgnoreCase(current.attribute) && lexer.configuration.joinStyles) {
                            int end = current.value.length() - 1;
                            current.value = current.value.charAt(end) == ';' ? current.value + " " + attval.value : (current.value.charAt(end) == '}' ? current.value + " { " + attval.value + " }" : current.value + "; " + attval.value);
                            temp = attval.next;
                            current = temp.next == null ? null : current.next;
                            lexer.report.attrError(lexer, this, attval, (short)68);
                            this.removeAttribute(attval);
                            attval = temp;
                            continue;
                        }
                        if (lexer.configuration.duplicateAttrs == 0) {
                            temp = current.next;
                            lexer.report.attrError(lexer, this, current, (short)55);
                            this.removeAttribute(current);
                            current = temp;
                            continue;
                        }
                        temp = attval.next;
                        current = attval.next == null ? null : current.next;
                        lexer.report.attrError(lexer, this, attval, (short)55);
                        this.removeAttribute(attval);
                        attval = temp;
                        continue;
                    }
                    current = current.next;
                }
                attval = attval.next;
                continue;
            }
            attval = attval.next;
        }
    }

    public void addAttribute(String name, String value) {
        AttVal av = new AttVal(null, null, null, null, 34, name, value);
        av.dict = AttributeTable.getDefaultAttributeTable().findAttribute(av);
        if (this.attributes == null) {
            this.attributes = av;
        } else {
            AttVal here = this.attributes;
            while (here.next != null) {
                here = here.next;
            }
            here.next = av;
        }
    }

    public void removeAttribute(AttVal attr) {
        AttVal prev = null;
        AttVal av = this.attributes;
        while (av != null) {
            AttVal next = av.next;
            if (av == attr) {
                if (prev != null) {
                    prev.next = next;
                } else {
                    this.attributes = next;
                }
            } else {
                prev = av;
            }
            av = next;
        }
    }

    public Node findDocType() {
        Node node = this.content;
        while (node != null && node.type != 1) {
            node = node.next;
        }
        return node;
    }

    public void discardDocType() {
        Node node = this.findDocType();
        if (node != null) {
            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                node.parent.content = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
            node.next = null;
        }
    }

    public static Node discardElement(Node element) {
        Node next = null;
        if (element != null) {
            next = element.next;
            element.removeNode();
        }
        return next;
    }

    public void insertNodeAtStart(Node node) {
        node.parent = this;
        if (this.content == null) {
            this.last = node;
        } else {
            this.content.prev = node;
        }
        node.next = this.content;
        node.prev = null;
        this.content = node;
    }

    public void insertNodeAtEnd(Node node) {
        node.parent = this;
        node.prev = this.last;
        if (this.last != null) {
            this.last.next = node;
        } else {
            this.content = node;
        }
        this.last = node;
    }

    public static void insertNodeAsParent(Node element, Node node) {
        node.content = element;
        node.last = element;
        node.parent = element.parent;
        element.parent = node;
        if (node.parent.content == element) {
            node.parent.content = node;
        }
        if (node.parent.last == element) {
            node.parent.last = node;
        }
        node.prev = element.prev;
        element.prev = null;
        if (node.prev != null) {
            node.prev.next = node;
        }
        node.next = element.next;
        element.next = null;
        if (node.next != null) {
            node.next.prev = node;
        }
    }

    public static void insertNodeBeforeElement(Node element, Node node) {
        Node parent;
        node.parent = parent = element.parent;
        node.next = element;
        node.prev = element.prev;
        element.prev = node;
        if (node.prev != null) {
            node.prev.next = node;
        }
        if (parent != null && parent.content == element) {
            parent.content = node;
        }
    }

    public void insertNodeAfterElement(Node node) {
        Node parent;
        node.parent = parent = this.parent;
        if (parent != null && parent.last == this) {
            parent.last = node;
        } else {
            node.next = this.next;
            if (node.next != null) {
                node.next.prev = node;
            }
        }
        this.next = node;
        node.prev = this;
    }

    public static void trimEmptyElement(Lexer lexer, Node element) {
        if (lexer.configuration.trimEmpty) {
            TagTable tt = lexer.configuration.tt;
            if (lexer.canPrune(element)) {
                if (element.type != 4) {
                    lexer.report.warning(lexer, element, null, (short)23);
                }
                Node.discardElement(element);
            } else if (element.tag == tt.tagP && element.content == null) {
                Node node = lexer.inferredTag("br");
                Node.coerceNode(lexer, element, tt.tagBr);
                element.insertNodeAfterElement(node);
            }
        }
    }

    public static void trimTrailingSpace(Lexer lexer, Node element, Node last) {
        TagTable tt = lexer.configuration.tt;
        if (last != null && last.type == 4) {
            byte c;
            if (last.end > last.start && ((c = lexer.lexbuf[last.end - 1]) == 160 || c == 32)) {
                if (c == 160 && (element.tag == tt.tagTd || element.tag == tt.tagTh)) {
                    if (last.end > last.start + 1) {
                        --last.end;
                    }
                } else {
                    --last.end;
                    if (TidyUtils.toBoolean(element.tag.model & 0x10) && !TidyUtils.toBoolean(element.tag.model & 0x400)) {
                        lexer.insertspace = true;
                    }
                }
            }
            if (last.start == last.end) {
                Node.trimEmptyElement(lexer, last);
            }
        }
    }

    protected static Node escapeTag(Lexer lexer, Node element) {
        Node node = lexer.newNode();
        node.start = lexer.lexsize;
        node.textarray = element.textarray;
        lexer.addByte(60);
        if (element.type == 6) {
            lexer.addByte(47);
        }
        if (element.element != null) {
            lexer.addStringLiteral(element.element);
        } else if (element.type == 1) {
            lexer.addByte(33);
            lexer.addByte(68);
            lexer.addByte(79);
            lexer.addByte(67);
            lexer.addByte(84);
            lexer.addByte(89);
            lexer.addByte(80);
            lexer.addByte(69);
            lexer.addByte(32);
            for (int i = element.start; i < element.end; ++i) {
                lexer.addByte(lexer.lexbuf[i]);
            }
        }
        if (element.type == 7) {
            lexer.addByte(47);
        }
        lexer.addByte(62);
        node.end = lexer.lexsize;
        return node;
    }

    public boolean isBlank(Lexer lexer) {
        if (this.type == 4) {
            if (this.end == this.start) {
                return true;
            }
            if (this.end == this.start + 1 && lexer.lexbuf[this.end - 1] == 32) {
                return true;
            }
        }
        return false;
    }

    public static void trimInitialSpace(Lexer lexer, Node element, Node text) {
        if (text.type == 4 && text.textarray[text.start] == 32 && text.start < text.end) {
            if (TidyUtils.toBoolean(element.tag.model & 0x10) && !TidyUtils.toBoolean(element.tag.model & 0x400) && element.parent.content != element) {
                Node prev = element.prev;
                if (prev != null && prev.type == 4) {
                    if (prev.textarray[prev.end - 1] != 32) {
                        prev.textarray[prev.end++] = 32;
                    }
                    ++element.start;
                } else {
                    Node node = lexer.newNode();
                    if (element.start >= element.end) {
                        node.start = 0;
                        node.end = 1;
                        node.textarray = new byte[1];
                    } else {
                        node.start = element.start++;
                        node.end = element.start;
                        node.textarray = element.textarray;
                    }
                    node.textarray[node.start] = 32;
                    node.prev = prev;
                    if (prev != null) {
                        prev.next = node;
                    }
                    node.next = element;
                    element.prev = node;
                    node.parent = element.parent;
                }
            }
            ++text.start;
        }
    }

    public static void trimSpaces(Lexer lexer, Node element) {
        Node text = element.content;
        TagTable tt = lexer.configuration.tt;
        if (text != null && text.type == 4 && element.tag != tt.tagPre) {
            Node.trimInitialSpace(lexer, element, text);
        }
        if ((text = element.last) != null && text.type == 4) {
            Node.trimTrailingSpace(lexer, element, text);
        }
    }

    public boolean isDescendantOf(Dict tag) {
        Node parent = this.parent;
        while (parent != null) {
            if (parent.tag == tag) {
                return true;
            }
            parent = parent.parent;
        }
        return false;
    }

    public static void insertDocType(Lexer lexer, Node element, Node doctype) {
        TagTable tt = lexer.configuration.tt;
        lexer.report.warning(lexer, element, doctype, (short)34);
        while (element.tag != tt.tagHtml) {
            element = element.parent;
        }
        Node.insertNodeBeforeElement(element, doctype);
    }

    public Node findBody(TagTable tt) {
        Node node = this.content;
        while (node != null && node.tag != tt.tagHtml) {
            node = node.next;
        }
        if (node == null) {
            return null;
        }
        node = node.content;
        while (node != null && node.tag != tt.tagBody && node.tag != tt.tagFrameset) {
            node = node.next;
        }
        if (node.tag == tt.tagFrameset) {
            node = node.content;
            while (node != null && node.tag != tt.tagNoframes) {
                node = node.next;
            }
            if (node != null) {
                node = node.content;
                while (node != null && node.tag != tt.tagBody) {
                    node = node.next;
                }
            }
        }
        return node;
    }

    public boolean isElement() {
        return this.type == 5 || this.type == 7;
    }

    public static void moveBeforeTable(Node row, Node node, TagTable tt) {
        Node table = row.parent;
        while (table != null) {
            if (table.tag == tt.tagTable) {
                if (table.parent.content == table) {
                    table.parent.content = node;
                }
                node.prev = table.prev;
                node.next = table;
                table.prev = node;
                node.parent = table.parent;
                if (node.prev == null) break;
                node.prev.next = node;
                break;
            }
            table = table.parent;
        }
    }

    public static void fixEmptyRow(Lexer lexer, Node row) {
        if (row.content == null) {
            Node cell = lexer.inferredTag("td");
            row.insertNodeAtEnd(cell);
            lexer.report.warning(lexer, row, cell, (short)12);
        }
    }

    public static void coerceNode(Lexer lexer, Node node, Dict tag) {
        Node tmp = lexer.inferredTag(tag.name);
        lexer.report.warning(lexer, node, tmp, (short)20);
        node.was = node.tag;
        node.tag = tag;
        node.type = (short)5;
        node.implicit = true;
        node.element = tag.name;
    }

    public void removeNode() {
        if (this.prev != null) {
            this.prev.next = this.next;
        }
        if (this.next != null) {
            this.next.prev = this.prev;
        }
        if (this.parent != null) {
            if (this.parent.content == this) {
                this.parent.content = this.next;
            }
            if (this.parent.last == this) {
                this.parent.last = this.prev;
            }
        }
        this.parent = null;
        this.prev = null;
        this.next = null;
    }

    public static boolean insertMisc(Node element, Node node) {
        if (node.type == 2 || node.type == 3 || node.type == 8 || node.type == 9 || node.type == 10 || node.type == 11 || node.type == 12 || node.type == 13) {
            element.insertNodeAtEnd(node);
            return true;
        }
        return false;
    }

    public boolean isNewNode() {
        if (this.tag != null) {
            return TidyUtils.toBoolean(this.tag.model & 0x100000);
        }
        return true;
    }

    public boolean hasOneChild() {
        return this.content != null && this.content.next == null;
    }

    public Node findHTML(TagTable tt) {
        Node node = this.content;
        while (node != null && node.tag != tt.tagHtml) {
            node = node.next;
        }
        return node;
    }

    public Node findHEAD(TagTable tt) {
        Node node = this.findHTML(tt);
        if (node != null) {
            node = node.content;
            while (node != null && node.tag != tt.tagHead) {
                node = node.next;
            }
        }
        return node;
    }

    public boolean checkNodeIntegrity() {
        Node child;
        boolean found = false;
        if (this.prev != null && this.prev.next != this) {
            return false;
        }
        if (this.next != null && this.next.prev != this) {
            return false;
        }
        if (this.parent != null) {
            if (this.prev == null && this.parent.content != this) {
                return false;
            }
            if (this.next == null && this.parent.last != this) {
                return false;
            }
            child = this.parent.content;
            while (child != null) {
                if (child == this) {
                    found = true;
                    break;
                }
                child = child.next;
            }
            if (!found) {
                return false;
            }
        }
        child = this.content;
        while (child != null) {
            if (!child.checkNodeIntegrity()) {
                return false;
            }
            child = child.next;
        }
        return true;
    }

    public void addClass(String classname) {
        AttVal classattr = this.getAttrByName("class");
        if (classattr != null) {
            classattr.value = classattr.value + " " + classname;
        } else {
            this.addAttribute("class", classname);
        }
    }

    public String toString() {
        String s = "";
        Node n = this;
        while (n != null) {
            s = s + "[Node type=";
            s = s + NODETYPE_STRING[n.type];
            s = s + ",element=";
            s = n.element != null ? s + n.element : s + "null";
            if (n.type == 4 || n.type == 2 || n.type == 3) {
                s = s + ",text=";
                if (n.textarray != null && n.start <= n.end) {
                    s = s + "\"";
                    s = s + TidyUtils.getString(n.textarray, n.start, n.end - n.start);
                    s = s + "\"";
                } else {
                    s = s + "null";
                }
            }
            s = s + ",content=";
            s = n.content != null ? s + n.content.toString() : s + "null";
            s = s + "]";
            if (n.next != null) {
                s = s + ",";
            }
            n = n.next;
        }
        return s;
    }

    protected org.w3c.dom.Node getAdapter() {
        if (this.adapter == null) {
            switch (this.type) {
                case 0: {
                    this.adapter = new DOMDocumentImpl(this);
                    break;
                }
                case 5: 
                case 7: {
                    this.adapter = new DOMElementImpl(this);
                    break;
                }
                case 1: {
                    this.adapter = new DOMDocumentTypeImpl(this);
                    break;
                }
                case 2: {
                    this.adapter = new DOMCommentImpl(this);
                    break;
                }
                case 4: {
                    this.adapter = new DOMTextImpl(this);
                    break;
                }
                case 8: {
                    this.adapter = new DOMCDATASectionImpl(this);
                    break;
                }
                case 3: {
                    this.adapter = new DOMProcessingInstructionImpl(this);
                    break;
                }
                default: {
                    this.adapter = new DOMNodeImpl(this);
                }
            }
        }
        return this.adapter;
    }

    protected Node cloneNode(boolean deep) {
        Node node = (Node)this.clone();
        if (deep) {
            Node child = this.content;
            while (child != null) {
                Node newChild = child.cloneNode(deep);
                node.insertNodeAtEnd(newChild);
                child = child.next;
            }
        }
        return node;
    }

    protected void setType(short newType) {
        this.type = newType;
    }

    public boolean isJavaScript() {
        boolean result = false;
        if (this.attributes == null) {
            return true;
        }
        AttVal attr = this.attributes;
        while (attr != null) {
            if (("language".equalsIgnoreCase(attr.attribute) || "type".equalsIgnoreCase(attr.attribute)) && "javascript".equalsIgnoreCase(attr.value)) {
                result = true;
            }
            attr = attr.next;
        }
        return result;
    }

    public boolean expectsContent() {
        if (this.type != 5) {
            return false;
        }
        if (this.tag == null) {
            return true;
        }
        return !TidyUtils.toBoolean(this.tag.model & 1);
    }
}


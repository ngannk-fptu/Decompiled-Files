/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.tidy.AttVal;
import org.w3c.tidy.Clean;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.Node;
import org.w3c.tidy.Parser;
import org.w3c.tidy.TagTable;
import org.w3c.tidy.TidyUtils;

public final class ParserImpl {
    public static final Parser HTML = new ParseHTML();
    public static final Parser HEAD = new ParseHead();
    public static final Parser TITLE = new ParseTitle();
    public static final Parser SCRIPT = new ParseScript();
    public static final Parser BODY = new ParseBody();
    public static final Parser FRAMESET = new ParseFrameSet();
    public static final Parser INLINE = new ParseInline();
    public static final Parser LIST = new ParseList();
    public static final Parser DEFLIST = new ParseDefList();
    public static final Parser PRE = new ParsePre();
    public static final Parser BLOCK = new ParseBlock();
    public static final Parser TABLETAG = new ParseTableTag();
    public static final Parser COLGROUP = new ParseColGroup();
    public static final Parser ROWGROUP = new ParseRowGroup();
    public static final Parser ROW = new ParseRow();
    public static final Parser NOFRAMES = new ParseNoFrames();
    public static final Parser SELECT = new ParseSelect();
    public static final Parser TEXT = new ParseText();
    public static final Parser EMPTY = new ParseEmpty();
    public static final Parser OPTGROUP = new ParseOptGroup();

    private ParserImpl() {
    }

    protected static void parseTag(Lexer lexer, Node node, short mode) {
        if ((node.tag.model & 1) != 0) {
            lexer.waswhite = false;
        } else if ((node.tag.model & 0x10) == 0) {
            lexer.insertspace = false;
        }
        if (node.tag.getParser() == null) {
            return;
        }
        if (node.type == 7) {
            Node.trimEmptyElement(lexer, node);
            return;
        }
        node.tag.getParser().parse(lexer, node, mode);
    }

    protected static void moveToHead(Lexer lexer, Node element, Node node) {
        node.removeNode();
        TagTable tt = lexer.configuration.tt;
        if (node.type == 5 || node.type == 7) {
            lexer.report.warning(lexer, element, node, (short)11);
            while (element.tag != tt.tagHtml) {
                element = element.parent;
            }
            Node head = element.content;
            while (head != null) {
                if (head.tag == tt.tagHead) {
                    head.insertNodeAtEnd(node);
                    break;
                }
                head = head.next;
            }
            if (node.tag.getParser() != null) {
                ParserImpl.parseTag(lexer, node, (short)0);
            }
        } else {
            lexer.report.warning(lexer, element, node, (short)8);
        }
    }

    static void moveNodeToBody(Lexer lexer, Node node) {
        node.removeNode();
        Node body = lexer.root.findBody(lexer.configuration.tt);
        body.insertNodeAtEnd(node);
    }

    public static Node parseDocument(Lexer lexer) {
        Node node;
        Node doctype = null;
        TagTable tt = lexer.configuration.tt;
        Node document = lexer.newNode();
        document.type = 0;
        lexer.root = document;
        while ((node = lexer.getToken((short)0)) != null) {
            Node html;
            if (Node.insertMisc(document, node)) continue;
            if (node.type == 1) {
                if (doctype == null) {
                    document.insertNodeAtEnd(node);
                    doctype = node;
                    continue;
                }
                lexer.report.warning(lexer, document, node, (short)8);
                continue;
            }
            if (node.type == 6) {
                lexer.report.warning(lexer, document, node, (short)8);
                continue;
            }
            if (node.type != 5 || node.tag != tt.tagHtml) {
                lexer.ungetToken();
                html = lexer.inferredTag("html");
            } else {
                html = node;
            }
            if (document.findDocType() == null && !lexer.configuration.bodyOnly) {
                lexer.report.warning(lexer, null, null, (short)44);
            }
            document.insertNodeAtEnd(html);
            HTML.parse(lexer, html, (short)0);
            break;
        }
        return document;
    }

    public static boolean XMLPreserveWhiteSpace(Node element, TagTable tt) {
        AttVal attribute = element.attributes;
        while (attribute != null) {
            if (attribute.attribute.equals("xml:space")) {
                return attribute.value.equals("preserve");
            }
            attribute = attribute.next;
        }
        if (element.element == null) {
            return false;
        }
        if ("pre".equalsIgnoreCase(element.element) || "script".equalsIgnoreCase(element.element) || "style".equalsIgnoreCase(element.element)) {
            return true;
        }
        if (tt != null && tt.findParser(element) == PRE) {
            return true;
        }
        return "xsl:text".equalsIgnoreCase(element.element);
    }

    public static void parseXMLElement(Lexer lexer, Node element, short mode) {
        Node node;
        if (ParserImpl.XMLPreserveWhiteSpace(element, lexer.configuration.tt)) {
            mode = (short)2;
        }
        while ((node = lexer.getToken(mode)) != null) {
            if (node.type == 6 && node.element.equals(element.element)) {
                element.closed = true;
                break;
            }
            if (node.type == 6) {
                lexer.report.error(lexer, element, node, (short)13);
                continue;
            }
            if (node.type == 5) {
                ParserImpl.parseXMLElement(lexer, node, mode);
            }
            element.insertNodeAtEnd(node);
        }
        if ((node = element.content) != null && node.type == 4 && mode != 2 && node.textarray[node.start] == 32) {
            ++node.start;
            if (node.start >= node.end) {
                Node.discardElement(node);
            }
        }
        if ((node = element.last) != null && node.type == 4 && mode != 2 && node.textarray[node.end - 1] == 32) {
            --node.end;
            if (node.start >= node.end) {
                Node.discardElement(node);
            }
        }
    }

    public static Node parseXMLDocument(Lexer lexer) {
        Node node;
        Node document = lexer.newNode();
        document.type = 0;
        Node doctype = null;
        lexer.configuration.xmlTags = true;
        while ((node = lexer.getToken((short)0)) != null) {
            if (node.type == 6) {
                lexer.report.warning(lexer, null, node, (short)13);
                continue;
            }
            if (Node.insertMisc(document, node)) continue;
            if (node.type == 1) {
                if (doctype == null) {
                    document.insertNodeAtEnd(node);
                    doctype = node;
                    continue;
                }
                lexer.report.warning(lexer, document, node, (short)8);
                continue;
            }
            if (node.type == 7) {
                document.insertNodeAtEnd(node);
                continue;
            }
            if (node.type != 5) continue;
            document.insertNodeAtEnd(node);
            ParserImpl.parseXMLElement(lexer, node, (short)0);
        }
        if (doctype != null && !lexer.checkDocTypeKeyWords(doctype)) {
            lexer.report.warning(lexer, doctype, null, (short)37);
        }
        if (lexer.configuration.xmlPi) {
            lexer.fixXmlDecl(document);
        }
        return document;
    }

    static void badForm(Lexer lexer) {
        lexer.badForm = 1;
        lexer.errors = (short)(lexer.errors + 1);
    }

    public static class ParseOptGroup
    implements Parser {
        public void parse(Lexer lexer, Node field, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            lexer.insert = -1;
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == field.tag && node.type == 6) {
                    field.closed = true;
                    Node.trimSpaces(lexer, field);
                    return;
                }
                if (Node.insertMisc(field, node)) continue;
                if (node.type == 5 && (node.tag == tt.tagOption || node.tag == tt.tagOptgroup)) {
                    if (node.tag == tt.tagOptgroup) {
                        lexer.report.warning(lexer, field, node, (short)19);
                    }
                    field.insertNodeAtEnd(node);
                    ParserImpl.parseTag(lexer, node, (short)1);
                    continue;
                }
                lexer.report.warning(lexer, field, node, (short)8);
            }
        }
    }

    public static class ParseText
    implements Parser {
        public void parse(Lexer lexer, Node field, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            lexer.insert = -1;
            mode = field.tag == tt.tagTextarea ? (short)2 : (short)1;
            while ((node = lexer.getToken(mode)) != null) {
                if (node.tag == field.tag && node.type == 6) {
                    field.closed = true;
                    Node.trimSpaces(lexer, field);
                    return;
                }
                if (Node.insertMisc(field, node)) continue;
                if (node.type == 4) {
                    if (field.content == null && (mode & 2) == 0) {
                        Node.trimSpaces(lexer, field);
                    }
                    if (node.start >= node.end) continue;
                    field.insertNodeAtEnd(node);
                    continue;
                }
                if (node.tag != null && (node.tag.model & 0x10) != 0 && (node.tag.model & 0x400) == 0) {
                    lexer.report.warning(lexer, field, node, (short)8);
                    continue;
                }
                if ((field.tag.model & 0x8000) == 0) {
                    lexer.report.warning(lexer, field, node, (short)7);
                }
                lexer.ungetToken();
                Node.trimSpaces(lexer, field);
                return;
            }
            if ((field.tag.model & 0x8000) == 0) {
                lexer.report.warning(lexer, field, node, (short)6);
            }
        }
    }

    public static class ParseSelect
    implements Parser {
        public void parse(Lexer lexer, Node field, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            lexer.insert = -1;
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == field.tag && node.type == 6) {
                    field.closed = true;
                    Node.trimSpaces(lexer, field);
                    return;
                }
                if (Node.insertMisc(field, node)) continue;
                if (node.type == 5 && (node.tag == tt.tagOption || node.tag == tt.tagOptgroup || node.tag == tt.tagScript)) {
                    field.insertNodeAtEnd(node);
                    ParserImpl.parseTag(lexer, node, (short)0);
                    continue;
                }
                lexer.report.warning(lexer, field, node, (short)8);
            }
            lexer.report.warning(lexer, field, node, (short)6);
        }
    }

    public static class ParseNoFrames
    implements Parser {
        public void parse(Lexer lexer, Node noframes, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            lexer.badAccess = (short)(lexer.badAccess | 0x20);
            mode = 0;
            while ((node = lexer.getToken(mode)) != null) {
                if (node.tag == noframes.tag && node.type == 6) {
                    noframes.closed = true;
                    Node.trimSpaces(lexer, noframes);
                    return;
                }
                if (node.tag == tt.tagFrame || node.tag == tt.tagFrameset) {
                    Node.trimSpaces(lexer, noframes);
                    if (node.type == 6) {
                        lexer.report.warning(lexer, noframes, node, (short)8);
                    } else {
                        lexer.report.warning(lexer, noframes, node, (short)7);
                        lexer.ungetToken();
                    }
                    return;
                }
                if (node.tag == tt.tagHtml) {
                    if (node.type != 5 && node.type != 7) continue;
                    lexer.report.warning(lexer, noframes, node, (short)8);
                    continue;
                }
                if (Node.insertMisc(noframes, node)) continue;
                if (node.tag == tt.tagBody && node.type == 5) {
                    boolean seenbody = lexer.seenEndBody;
                    noframes.insertNodeAtEnd(node);
                    ParserImpl.parseTag(lexer, node, (short)0);
                    if (!seenbody) continue;
                    Node.coerceNode(lexer, node, tt.tagDiv);
                    ParserImpl.moveNodeToBody(lexer, node);
                    continue;
                }
                if (node.type == 4 || node.tag != null && node.type != 6) {
                    if (lexer.seenEndBody) {
                        Node body = lexer.root.findBody(tt);
                        if (node.type == 4) {
                            lexer.ungetToken();
                            node = lexer.inferredTag("p");
                            lexer.report.warning(lexer, noframes, node, (short)27);
                        }
                        body.insertNodeAtEnd(node);
                    } else {
                        lexer.ungetToken();
                        node = lexer.inferredTag("body");
                        if (lexer.configuration.xmlOut) {
                            lexer.report.warning(lexer, noframes, node, (short)15);
                        }
                        noframes.insertNodeAtEnd(node);
                    }
                    ParserImpl.parseTag(lexer, node, (short)0);
                    continue;
                }
                lexer.report.warning(lexer, noframes, node, (short)8);
            }
            lexer.report.warning(lexer, noframes, node, (short)6);
        }
    }

    public static class ParseRow
    implements Parser {
        public void parse(Lexer lexer, Node row, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            if ((row.tag.model & 1) != 0) {
                return;
            }
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == row.tag) {
                    if (node.type == 6) {
                        row.closed = true;
                        Node.fixEmptyRow(lexer, row);
                        return;
                    }
                    lexer.ungetToken();
                    Node.fixEmptyRow(lexer, row);
                    return;
                }
                if (node.type == 6) {
                    if (node.tag == tt.tagForm || node.tag != null && (node.tag.model & 0x18) != 0) {
                        if (node.tag == tt.tagForm) {
                            ParserImpl.badForm(lexer);
                        }
                        lexer.report.warning(lexer, row, node, (short)8);
                        continue;
                    }
                    if (node.tag == tt.tagTd || node.tag == tt.tagTh) {
                        lexer.report.warning(lexer, row, node, (short)8);
                        continue;
                    }
                    Node parent = row.parent;
                    while (parent != null) {
                        if (node.tag == parent.tag) {
                            lexer.ungetToken();
                            Node.trimEmptyElement(lexer, row);
                            return;
                        }
                        parent = parent.parent;
                    }
                }
                if (Node.insertMisc(row, node)) continue;
                if (node.tag == null && node.type != 4) {
                    lexer.report.warning(lexer, row, node, (short)8);
                    continue;
                }
                if (node.tag == tt.tagTable) {
                    lexer.report.warning(lexer, row, node, (short)8);
                    continue;
                }
                if (node.tag != null && (node.tag.model & 0x100) != 0) {
                    lexer.ungetToken();
                    Node.trimEmptyElement(lexer, row);
                    return;
                }
                if (node.type == 6) {
                    lexer.report.warning(lexer, row, node, (short)8);
                    continue;
                }
                if (node.type != 6) {
                    if (node.tag == tt.tagForm) {
                        lexer.ungetToken();
                        node = lexer.inferredTag("td");
                        lexer.report.warning(lexer, row, node, (short)12);
                    } else {
                        if (node.type == 4 || (node.tag.model & 0x18) != 0) {
                            Node.moveBeforeTable(row, node, tt);
                            lexer.report.warning(lexer, row, node, (short)11);
                            lexer.exiled = true;
                            if (node.type != 4) {
                                ParserImpl.parseTag(lexer, node, (short)0);
                            }
                            lexer.exiled = false;
                            continue;
                        }
                        if ((node.tag.model & 4) != 0) {
                            lexer.report.warning(lexer, row, node, (short)11);
                            ParserImpl.moveToHead(lexer, row, node);
                            continue;
                        }
                    }
                }
                if (node.tag != tt.tagTd && node.tag != tt.tagTh) {
                    lexer.report.warning(lexer, row, node, (short)11);
                    continue;
                }
                row.insertNodeAtEnd(node);
                boolean excludeState = lexer.excludeBlocks;
                lexer.excludeBlocks = false;
                ParserImpl.parseTag(lexer, node, (short)0);
                lexer.excludeBlocks = excludeState;
                while (lexer.istack.size() > lexer.istackbase) {
                    lexer.popInline(null);
                }
            }
            Node.trimEmptyElement(lexer, row);
        }
    }

    public static class ParseRowGroup
    implements Parser {
        public void parse(Lexer lexer, Node rowgroup, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            if ((rowgroup.tag.model & 1) != 0) {
                return;
            }
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == rowgroup.tag) {
                    if (node.type == 6) {
                        rowgroup.closed = true;
                        Node.trimEmptyElement(lexer, rowgroup);
                        return;
                    }
                    lexer.ungetToken();
                    return;
                }
                if (node.tag == tt.tagTable && node.type == 6) {
                    lexer.ungetToken();
                    Node.trimEmptyElement(lexer, rowgroup);
                    return;
                }
                if (Node.insertMisc(rowgroup, node)) continue;
                if (node.tag == null && node.type != 4) {
                    lexer.report.warning(lexer, rowgroup, node, (short)8);
                    continue;
                }
                if (node.type != 6) {
                    if (node.tag == tt.tagTd || node.tag == tt.tagTh) {
                        lexer.ungetToken();
                        node = lexer.inferredTag("tr");
                        lexer.report.warning(lexer, rowgroup, node, (short)12);
                    } else {
                        if (node.type == 4 || (node.tag.model & 0x18) != 0) {
                            Node.moveBeforeTable(rowgroup, node, tt);
                            lexer.report.warning(lexer, rowgroup, node, (short)11);
                            lexer.exiled = true;
                            if (node.type != 4) {
                                ParserImpl.parseTag(lexer, node, (short)0);
                            }
                            lexer.exiled = false;
                            continue;
                        }
                        if ((node.tag.model & 4) != 0) {
                            lexer.report.warning(lexer, rowgroup, node, (short)11);
                            ParserImpl.moveToHead(lexer, rowgroup, node);
                            continue;
                        }
                    }
                }
                if (node.type == 6) {
                    if (node.tag == tt.tagForm || node.tag != null && (node.tag.model & 0x18) != 0) {
                        if (node.tag == tt.tagForm) {
                            ParserImpl.badForm(lexer);
                        }
                        lexer.report.warning(lexer, rowgroup, node, (short)8);
                        continue;
                    }
                    if (node.tag == tt.tagTr || node.tag == tt.tagTd || node.tag == tt.tagTh) {
                        lexer.report.warning(lexer, rowgroup, node, (short)8);
                        continue;
                    }
                    Node parent = rowgroup.parent;
                    while (parent != null) {
                        if (node.tag == parent.tag) {
                            lexer.ungetToken();
                            Node.trimEmptyElement(lexer, rowgroup);
                            return;
                        }
                        parent = parent.parent;
                    }
                }
                if ((node.tag.model & 0x100) != 0) {
                    if (node.type != 6) {
                        lexer.ungetToken();
                    }
                    Node.trimEmptyElement(lexer, rowgroup);
                    return;
                }
                if (node.type == 6) {
                    lexer.report.warning(lexer, rowgroup, node, (short)8);
                    continue;
                }
                if (node.tag != tt.tagTr) {
                    node = lexer.inferredTag("tr");
                    lexer.report.warning(lexer, rowgroup, node, (short)12);
                    lexer.ungetToken();
                }
                rowgroup.insertNodeAtEnd(node);
                ParserImpl.parseTag(lexer, node, (short)0);
            }
            Node.trimEmptyElement(lexer, rowgroup);
        }
    }

    public static class ParseColGroup
    implements Parser {
        public void parse(Lexer lexer, Node colgroup, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            if ((colgroup.tag.model & 1) != 0) {
                return;
            }
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == colgroup.tag && node.type == 6) {
                    colgroup.closed = true;
                    return;
                }
                if (node.type == 6) {
                    if (node.tag == tt.tagForm) {
                        ParserImpl.badForm(lexer);
                        lexer.report.warning(lexer, colgroup, node, (short)8);
                        continue;
                    }
                    Node parent = colgroup.parent;
                    while (parent != null) {
                        if (node.tag == parent.tag) {
                            lexer.ungetToken();
                            return;
                        }
                        parent = parent.parent;
                    }
                }
                if (node.type == 4) {
                    lexer.ungetToken();
                    return;
                }
                if (Node.insertMisc(colgroup, node)) continue;
                if (node.tag == null) {
                    lexer.report.warning(lexer, colgroup, node, (short)8);
                    continue;
                }
                if (node.tag != tt.tagCol) {
                    lexer.ungetToken();
                    return;
                }
                if (node.type == 6) {
                    lexer.report.warning(lexer, colgroup, node, (short)8);
                    continue;
                }
                colgroup.insertNodeAtEnd(node);
                ParserImpl.parseTag(lexer, node, (short)0);
            }
        }
    }

    public static class ParseTableTag
    implements Parser {
        public void parse(Lexer lexer, Node table, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            lexer.deferDup();
            int istackbase = lexer.istackbase;
            lexer.istackbase = lexer.istack.size();
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == table.tag && node.type == 6) {
                    lexer.istackbase = istackbase;
                    table.closed = true;
                    Node.trimEmptyElement(lexer, table);
                    return;
                }
                if (Node.insertMisc(table, node)) continue;
                if (node.tag == null && node.type != 4) {
                    lexer.report.warning(lexer, table, node, (short)8);
                    continue;
                }
                if (node.type != 6) {
                    if (node.tag == tt.tagTd || node.tag == tt.tagTh || node.tag == tt.tagTable) {
                        lexer.ungetToken();
                        node = lexer.inferredTag("tr");
                        lexer.report.warning(lexer, table, node, (short)12);
                    } else {
                        if (node.type == 4 || (node.tag.model & 0x18) != 0) {
                            Node.insertNodeBeforeElement(table, node);
                            lexer.report.warning(lexer, table, node, (short)11);
                            lexer.exiled = true;
                            if (node.type != 4) {
                                ParserImpl.parseTag(lexer, node, (short)0);
                            }
                            lexer.exiled = false;
                            continue;
                        }
                        if ((node.tag.model & 4) != 0) {
                            ParserImpl.moveToHead(lexer, table, node);
                            continue;
                        }
                    }
                }
                if (node.type == 6) {
                    if (node.tag == tt.tagForm || node.tag != null && (node.tag.model & 0x18) != 0) {
                        ParserImpl.badForm(lexer);
                        lexer.report.warning(lexer, table, node, (short)8);
                        continue;
                    }
                    if (node.tag != null && (node.tag.model & 0x280) != 0 || node.tag != null && (node.tag.model & 0x18) != 0) {
                        lexer.report.warning(lexer, table, node, (short)8);
                        continue;
                    }
                    Node parent = table.parent;
                    while (parent != null) {
                        if (node.tag == parent.tag) {
                            lexer.report.warning(lexer, table, node, (short)7);
                            lexer.ungetToken();
                            lexer.istackbase = istackbase;
                            Node.trimEmptyElement(lexer, table);
                            return;
                        }
                        parent = parent.parent;
                    }
                }
                if ((node.tag.model & 0x80) == 0) {
                    lexer.ungetToken();
                    lexer.report.warning(lexer, table, node, (short)11);
                    lexer.istackbase = istackbase;
                    Node.trimEmptyElement(lexer, table);
                    return;
                }
                if (node.type == 5 || node.type == 7) {
                    table.insertNodeAtEnd(node);
                    ParserImpl.parseTag(lexer, node, (short)0);
                    continue;
                }
                lexer.report.warning(lexer, table, node, (short)8);
            }
            lexer.report.warning(lexer, table, node, (short)6);
            Node.trimEmptyElement(lexer, table);
            lexer.istackbase = istackbase;
        }
    }

    public static class ParseBlock
    implements Parser {
        public void parse(Lexer lexer, Node element, short mode) {
            Node node;
            int istackbase = 0;
            TagTable tt = lexer.configuration.tt;
            boolean checkstack = true;
            if ((element.tag.model & 1) != 0) {
                return;
            }
            if (element.tag == tt.tagForm && element.isDescendantOf(tt.tagForm)) {
                lexer.report.warning(lexer, element, null, (short)25);
            }
            if ((element.tag.model & 0x800) != 0) {
                istackbase = lexer.istackbase;
                lexer.istackbase = lexer.istack.size();
            }
            if ((element.tag.model & 0x20000) == 0) {
                lexer.inlineDup(null);
            }
            mode = 0;
            while ((node = lexer.getToken(mode)) != null) {
                if (node.type == 6 && node.tag != null && (node.tag == element.tag || element.was == node.tag)) {
                    if ((element.tag.model & 0x800) != 0) {
                        while (lexer.istack.size() > lexer.istackbase) {
                            lexer.popInline(null);
                        }
                        lexer.istackbase = istackbase;
                    }
                    element.closed = true;
                    Node.trimSpaces(lexer, element);
                    Node.trimEmptyElement(lexer, element);
                    return;
                }
                if (node.tag == tt.tagHtml || node.tag == tt.tagHead || node.tag == tt.tagBody) {
                    if (node.type != 5 && node.type != 7) continue;
                    lexer.report.warning(lexer, element, node, (short)8);
                    continue;
                }
                if (node.type == 6) {
                    if (node.tag == null) {
                        lexer.report.warning(lexer, element, node, (short)8);
                        continue;
                    }
                    if (node.tag == tt.tagBr) {
                        node.type = (short)5;
                    } else if (node.tag == tt.tagP) {
                        Node.coerceNode(lexer, node, tt.tagBr);
                        element.insertNodeAtEnd(node);
                        node = lexer.inferredTag("br");
                    } else {
                        Node parent = element.parent;
                        while (parent != null) {
                            if (node.tag == parent.tag) {
                                if ((element.tag.model & 0x8000) == 0) {
                                    lexer.report.warning(lexer, element, node, (short)7);
                                }
                                lexer.ungetToken();
                                if ((element.tag.model & 0x800) != 0) {
                                    while (lexer.istack.size() > lexer.istackbase) {
                                        lexer.popInline(null);
                                    }
                                    lexer.istackbase = istackbase;
                                }
                                Node.trimSpaces(lexer, element);
                                Node.trimEmptyElement(lexer, element);
                                return;
                            }
                            parent = parent.parent;
                        }
                        if (lexer.exiled && node.tag.model != 0 && (node.tag.model & 0x80) != 0) {
                            lexer.ungetToken();
                            Node.trimSpaces(lexer, element);
                            Node.trimEmptyElement(lexer, element);
                            return;
                        }
                    }
                }
                if (node.type == 4) {
                    boolean iswhitenode = false;
                    if (node.type == 4 && node.end <= node.start + 1 && lexer.lexbuf[node.start] == 32) {
                        iswhitenode = true;
                    }
                    if (lexer.configuration.encloseBlockText && !iswhitenode) {
                        lexer.ungetToken();
                        node = lexer.inferredTag("p");
                        element.insertNodeAtEnd(node);
                        ParserImpl.parseTag(lexer, node, (short)1);
                        continue;
                    }
                    if (checkstack) {
                        checkstack = false;
                        if ((element.tag.model & 0x20000) == 0 && lexer.inlineDup(node) > 0) continue;
                    }
                    element.insertNodeAtEnd(node);
                    mode = 1;
                    if (element.tag != tt.tagBody && element.tag != tt.tagMap && element.tag != tt.tagBlockquote && element.tag != tt.tagForm && element.tag != tt.tagNoscript) continue;
                    lexer.constrainVersion(-5);
                    continue;
                }
                if (Node.insertMisc(element, node)) continue;
                if (node.tag == tt.tagParam) {
                    if ((element.tag.model & 0x1000) != 0 && (node.type == 5 || node.type == 7)) {
                        element.insertNodeAtEnd(node);
                        continue;
                    }
                    lexer.report.warning(lexer, element, node, (short)8);
                    continue;
                }
                if (node.tag == tt.tagArea) {
                    if (element.tag == tt.tagMap && (node.type == 5 || node.type == 7)) {
                        element.insertNodeAtEnd(node);
                        continue;
                    }
                    lexer.report.warning(lexer, element, node, (short)8);
                    continue;
                }
                if (node.tag == null) {
                    lexer.report.warning(lexer, element, node, (short)8);
                    continue;
                }
                if ((node.tag.model & 0x10) == 0) {
                    if (node.type != 5 && node.type != 7) {
                        if (node.tag == tt.tagForm) {
                            ParserImpl.badForm(lexer);
                        }
                        lexer.report.warning(lexer, element, node, (short)8);
                        continue;
                    }
                    if (element.tag == tt.tagLi && (node.tag == tt.tagFrame || node.tag == tt.tagFrameset || node.tag == tt.tagOptgroup || node.tag == tt.tagOption)) {
                        lexer.report.warning(lexer, element, node, (short)8);
                        continue;
                    }
                    if (element.tag == tt.tagTd || element.tag == tt.tagTh) {
                        if ((node.tag.model & 4) != 0) {
                            ParserImpl.moveToHead(lexer, element, node);
                            continue;
                        }
                        if ((node.tag.model & 0x20) != 0) {
                            lexer.ungetToken();
                            node = lexer.inferredTag("ul");
                            node.addClass("noindent");
                            lexer.excludeBlocks = true;
                        } else if ((node.tag.model & 0x40) != 0) {
                            lexer.ungetToken();
                            node = lexer.inferredTag("dl");
                            lexer.excludeBlocks = true;
                        }
                        if ((node.tag.model & 8) == 0) {
                            lexer.ungetToken();
                            Node.trimSpaces(lexer, element);
                            Node.trimEmptyElement(lexer, element);
                            return;
                        }
                    } else if ((node.tag.model & 8) != 0) {
                        if (lexer.excludeBlocks) {
                            if ((element.tag.model & 0x8000) == 0) {
                                lexer.report.warning(lexer, element, node, (short)7);
                            }
                            lexer.ungetToken();
                            if ((element.tag.model & 0x800) != 0) {
                                lexer.istackbase = istackbase;
                            }
                            Node.trimSpaces(lexer, element);
                            Node.trimEmptyElement(lexer, element);
                            return;
                        }
                    } else {
                        if ((node.tag.model & 4) != 0) {
                            ParserImpl.moveToHead(lexer, element, node);
                            continue;
                        }
                        if (element.tag == tt.tagForm && element.parent.tag == tt.tagTd && element.parent.implicit) {
                            if (node.tag == tt.tagTd) {
                                lexer.report.warning(lexer, element, node, (short)8);
                                continue;
                            }
                            if (node.tag == tt.tagTh) {
                                lexer.report.warning(lexer, element, node, (short)8);
                                node = element.parent;
                                node.element = "th";
                                node.tag = tt.tagTh;
                                continue;
                            }
                        }
                        if ((element.tag.model & 0x8000) == 0 && !element.implicit) {
                            lexer.report.warning(lexer, element, node, (short)7);
                        }
                        lexer.ungetToken();
                        if ((node.tag.model & 0x20) != 0) {
                            if (element.parent != null && element.parent.tag != null && element.parent.tag.getParser() == LIST) {
                                Node.trimSpaces(lexer, element);
                                Node.trimEmptyElement(lexer, element);
                                return;
                            }
                            node = lexer.inferredTag("ul");
                            node.addClass("noindent");
                        } else if ((node.tag.model & 0x40) != 0) {
                            if (element.parent.tag == tt.tagDl) {
                                Node.trimSpaces(lexer, element);
                                Node.trimEmptyElement(lexer, element);
                                return;
                            }
                            node = lexer.inferredTag("dl");
                        } else if ((node.tag.model & 0x80) != 0 || (node.tag.model & 0x200) != 0) {
                            node = lexer.inferredTag("table");
                        } else {
                            if ((element.tag.model & 0x800) != 0) {
                                while (lexer.istack.size() > lexer.istackbase) {
                                    lexer.popInline(null);
                                }
                                lexer.istackbase = istackbase;
                                Node.trimSpaces(lexer, element);
                                Node.trimEmptyElement(lexer, element);
                                return;
                            }
                            Node.trimSpaces(lexer, element);
                            Node.trimEmptyElement(lexer, element);
                            return;
                        }
                    }
                }
                if (node.type == 5 || node.type == 7) {
                    if (TidyUtils.toBoolean(node.tag.model & 0x10)) {
                        if (checkstack && !node.implicit) {
                            checkstack = false;
                            if (!TidyUtils.toBoolean(element.tag.model & 0x20000) && lexer.inlineDup(node) > 0) continue;
                        }
                        mode = 1;
                    } else {
                        checkstack = true;
                        mode = 0;
                    }
                    if (node.tag == tt.tagBr) {
                        Node.trimSpaces(lexer, element);
                    }
                    element.insertNodeAtEnd(node);
                    if (node.implicit) {
                        lexer.report.warning(lexer, element, node, (short)15);
                    }
                    ParserImpl.parseTag(lexer, node, (short)0);
                    continue;
                }
                if (node.type == 6) {
                    lexer.popInline(node);
                }
                lexer.report.warning(lexer, element, node, (short)8);
            }
            if ((element.tag.model & 0x8000) == 0) {
                lexer.report.warning(lexer, element, node, (short)6);
            }
            if ((element.tag.model & 0x800) != 0) {
                while (lexer.istack.size() > lexer.istackbase) {
                    lexer.popInline(null);
                }
                lexer.istackbase = istackbase;
            }
            Node.trimSpaces(lexer, element);
            Node.trimEmptyElement(lexer, element);
        }
    }

    public static class ParsePre
    implements Parser {
        public void parse(Lexer lexer, Node pre, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            if ((pre.tag.model & 1) != 0) {
                return;
            }
            if ((pre.tag.model & 0x80000) != 0) {
                Node.coerceNode(lexer, pre, tt.tagPre);
            }
            lexer.inlineDup(null);
            while ((node = lexer.getToken((short)2)) != null) {
                if (node.tag == pre.tag && node.type == 6) {
                    Node.trimSpaces(lexer, pre);
                    pre.closed = true;
                    Node.trimEmptyElement(lexer, pre);
                    return;
                }
                if (node.tag == tt.tagHtml) {
                    if (node.type != 5 && node.type != 7) continue;
                    lexer.report.warning(lexer, pre, node, (short)8);
                    continue;
                }
                if (node.type == 4) {
                    if (pre.content == null) {
                        if (node.textarray[node.start] == 10) {
                            ++node.start;
                        }
                        if (node.start >= node.end) continue;
                    }
                    pre.insertNodeAtEnd(node);
                    continue;
                }
                if (Node.insertMisc(pre, node)) continue;
                if (!lexer.preContent(node)) {
                    lexer.report.warning(lexer, pre, node, (short)39);
                    Node newnode = Node.escapeTag(lexer, node);
                    pre.insertNodeAtEnd(newnode);
                    continue;
                }
                if (node.tag == tt.tagP) {
                    if (node.type == 5) {
                        lexer.report.warning(lexer, pre, node, (short)14);
                        Node.trimSpaces(lexer, pre);
                        Node.coerceNode(lexer, node, tt.tagBr);
                        pre.insertNodeAtEnd(node);
                        continue;
                    }
                    lexer.report.warning(lexer, pre, node, (short)8);
                    continue;
                }
                if (node.type == 5 || node.type == 7) {
                    if (node.tag == tt.tagBr) {
                        Node.trimSpaces(lexer, pre);
                    }
                    pre.insertNodeAtEnd(node);
                    ParserImpl.parseTag(lexer, node, (short)2);
                    continue;
                }
                lexer.report.warning(lexer, pre, node, (short)8);
            }
            lexer.report.warning(lexer, pre, node, (short)6);
            Node.trimEmptyElement(lexer, pre);
        }
    }

    public static class ParseDefList
    implements Parser {
        public void parse(Lexer lexer, Node list, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            if ((list.tag.model & 1) != 0) {
                return;
            }
            lexer.insert = -1;
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == list.tag && node.type == 6) {
                    list.closed = true;
                    Node.trimEmptyElement(lexer, list);
                    return;
                }
                if (Node.insertMisc(list, node)) continue;
                if (node.type == 4) {
                    lexer.ungetToken();
                    node = lexer.inferredTag("dt");
                    lexer.report.warning(lexer, list, node, (short)12);
                }
                if (node.tag == null) {
                    lexer.report.warning(lexer, list, node, (short)8);
                    continue;
                }
                if (node.type == 6) {
                    if (node.tag == tt.tagForm) {
                        ParserImpl.badForm(lexer);
                        lexer.report.warning(lexer, list, node, (short)8);
                        continue;
                    }
                    Node parent = list.parent;
                    while (parent != null) {
                        if (node.tag == parent.tag) {
                            lexer.report.warning(lexer, list, node, (short)7);
                            lexer.ungetToken();
                            Node.trimEmptyElement(lexer, list);
                            return;
                        }
                        parent = parent.parent;
                    }
                }
                if (node.tag == tt.tagCenter) {
                    if (list.content != null) {
                        list.insertNodeAfterElement(node);
                    } else {
                        Node.insertNodeBeforeElement(list, node);
                        Node.discardElement(list);
                    }
                    ParserImpl.parseTag(lexer, node, mode);
                    list = lexer.inferredTag("dl");
                    node.insertNodeAfterElement(list);
                    continue;
                }
                if (node.tag != tt.tagDt && node.tag != tt.tagDd) {
                    lexer.ungetToken();
                    if ((node.tag.model & 0x18) == 0) {
                        lexer.report.warning(lexer, list, node, (short)11);
                        Node.trimEmptyElement(lexer, list);
                        return;
                    }
                    if ((node.tag.model & 0x10) == 0 && lexer.excludeBlocks) {
                        Node.trimEmptyElement(lexer, list);
                        return;
                    }
                    node = lexer.inferredTag("dd");
                    lexer.report.warning(lexer, list, node, (short)12);
                }
                if (node.type == 6) {
                    lexer.report.warning(lexer, list, node, (short)8);
                    continue;
                }
                list.insertNodeAtEnd(node);
                ParserImpl.parseTag(lexer, node, (short)0);
            }
            lexer.report.warning(lexer, list, node, (short)6);
            Node.trimEmptyElement(lexer, list);
        }
    }

    public static class ParseEmpty
    implements Parser {
        public void parse(Lexer lexer, Node element, short mode) {
            Node node;
            if (lexer.isvoyager && (node = lexer.getToken(mode)) != null && (node.type != 6 || node.tag != element.tag)) {
                lexer.report.warning(lexer, element, node, (short)41);
                lexer.ungetToken();
            }
        }
    }

    public static class ParseList
    implements Parser {
        public void parse(Lexer lexer, Node list, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            if ((list.tag.model & 1) != 0) {
                return;
            }
            lexer.insert = -1;
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == list.tag && node.type == 6) {
                    if ((list.tag.model & 0x80000) != 0) {
                        Node.coerceNode(lexer, list, tt.tagUl);
                    }
                    list.closed = true;
                    Node.trimEmptyElement(lexer, list);
                    return;
                }
                if (Node.insertMisc(list, node)) continue;
                if (node.type != 4 && node.tag == null) {
                    lexer.report.warning(lexer, list, node, (short)8);
                    continue;
                }
                if (node.type == 6) {
                    if (node.tag == tt.tagForm) {
                        ParserImpl.badForm(lexer);
                        lexer.report.warning(lexer, list, node, (short)8);
                        continue;
                    }
                    if (node.tag != null && (node.tag.model & 0x10) != 0) {
                        lexer.report.warning(lexer, list, node, (short)8);
                        lexer.popInline(node);
                        continue;
                    }
                    Node parent = list.parent;
                    while (parent != null) {
                        if (node.tag == parent.tag) {
                            lexer.report.warning(lexer, list, node, (short)7);
                            lexer.ungetToken();
                            if ((list.tag.model & 0x80000) != 0) {
                                Node.coerceNode(lexer, list, tt.tagUl);
                            }
                            Node.trimEmptyElement(lexer, list);
                            return;
                        }
                        parent = parent.parent;
                    }
                    lexer.report.warning(lexer, list, node, (short)8);
                    continue;
                }
                if (node.tag != tt.tagLi) {
                    lexer.ungetToken();
                    if (node.tag != null && (node.tag.model & 8) != 0 && lexer.excludeBlocks) {
                        lexer.report.warning(lexer, list, node, (short)7);
                        Node.trimEmptyElement(lexer, list);
                        return;
                    }
                    node = lexer.inferredTag("li");
                    node.addAttribute("style", "list-style: none");
                    lexer.report.warning(lexer, list, node, (short)12);
                }
                list.insertNodeAtEnd(node);
                ParserImpl.parseTag(lexer, node, (short)0);
            }
            if ((list.tag.model & 0x80000) != 0) {
                Node.coerceNode(lexer, list, tt.tagUl);
            }
            lexer.report.warning(lexer, list, node, (short)6);
            Node.trimEmptyElement(lexer, list);
        }
    }

    public static class ParseInline
    implements Parser {
        public void parse(Lexer lexer, Node element, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            if (TidyUtils.toBoolean(element.tag.model & 1)) {
                return;
            }
            if (TidyUtils.toBoolean(element.tag.model & 8) || element.tag == tt.tagDt) {
                lexer.inlineDup(null);
            } else if (TidyUtils.toBoolean(element.tag.model & 0x10)) {
                lexer.pushInline(element);
            }
            if (element.tag == tt.tagNobr) {
                lexer.badLayout = (short)(lexer.badLayout | 4);
            } else if (element.tag == tt.tagFont) {
                lexer.badLayout = (short)(lexer.badLayout | 8);
            }
            if (mode != 2) {
                mode = 1;
            }
            while ((node = lexer.getToken(mode)) != null) {
                if (node.tag == element.tag && node.type == 6) {
                    if (TidyUtils.toBoolean(element.tag.model & 0x10)) {
                        lexer.popInline(node);
                    }
                    if (!TidyUtils.toBoolean(mode & 2)) {
                        Node.trimSpaces(lexer, element);
                    }
                    if (element.tag == tt.tagFont && element.content != null && element.content == element.last) {
                        Node child = element.content;
                        if (child.tag == tt.tagA) {
                            child.parent = element.parent;
                            child.next = element.next;
                            child.prev = element.prev;
                            if (child.prev != null) {
                                child.prev.next = child;
                            } else {
                                child.parent.content = child;
                            }
                            if (child.next != null) {
                                child.next.prev = child;
                            } else {
                                child.parent.last = child;
                            }
                            element.next = null;
                            element.prev = null;
                            element.parent = child;
                            element.content = child.content;
                            element.last = child.last;
                            child.content = element;
                            child.last = element;
                            child = element.content;
                            while (child != null) {
                                child.parent = element;
                                child = child.next;
                            }
                        }
                    }
                    element.closed = true;
                    Node.trimSpaces(lexer, element);
                    Node.trimEmptyElement(lexer, element);
                    return;
                }
                if (node.type == 5 && node.tag == element.tag && lexer.isPushed(node) && !node.implicit && !element.implicit && node.tag != null && (node.tag.model & 0x10) != 0 && node.tag != tt.tagA && node.tag != tt.tagFont && node.tag != tt.tagBig && node.tag != tt.tagSmall && node.tag != tt.tagQ) {
                    if (element.content != null && node.attributes == null) {
                        lexer.report.warning(lexer, element, node, (short)24);
                        node.type = (short)6;
                        lexer.ungetToken();
                        continue;
                    }
                    lexer.report.warning(lexer, element, node, (short)9);
                } else if (lexer.isPushed(node) && node.type == 5 && node.tag == tt.tagQ) {
                    lexer.report.warning(lexer, element, node, (short)40);
                }
                if (node.type == 4) {
                    if (element.content == null && !TidyUtils.toBoolean(mode & 2)) {
                        Node.trimSpaces(lexer, element);
                    }
                    if (node.start >= node.end) continue;
                    element.insertNodeAtEnd(node);
                    continue;
                }
                if (Node.insertMisc(element, node)) continue;
                if (node.tag == tt.tagHtml) {
                    if (node.type == 5 || node.type == 7) {
                        lexer.report.warning(lexer, element, node, (short)8);
                        continue;
                    }
                    lexer.ungetToken();
                    if ((mode & 2) == 0) {
                        Node.trimSpaces(lexer, element);
                    }
                    Node.trimEmptyElement(lexer, element);
                    return;
                }
                if (node.tag == tt.tagP && node.type == 5 && ((mode & 2) != 0 || element.tag == tt.tagDt || element.isDescendantOf(tt.tagDt))) {
                    node.tag = tt.tagBr;
                    node.element = "br";
                    Node.trimSpaces(lexer, element);
                    element.insertNodeAtEnd(node);
                    continue;
                }
                if (node.tag == null || node.tag == tt.tagParam) {
                    lexer.report.warning(lexer, element, node, (short)8);
                    continue;
                }
                if (node.tag == tt.tagBr && node.type == 6) {
                    node.type = (short)5;
                }
                if (node.type == 6) {
                    if (node.tag == tt.tagBr) {
                        node.type = (short)5;
                    } else if (node.tag == tt.tagP) {
                        if (!element.isDescendantOf(tt.tagP)) {
                            Node.coerceNode(lexer, node, tt.tagBr);
                            Node.trimSpaces(lexer, element);
                            element.insertNodeAtEnd(node);
                            node = lexer.inferredTag("br");
                            continue;
                        }
                    } else {
                        if ((node.tag.model & 0x10) != 0 && node.tag != tt.tagA && (node.tag.model & 0x800) == 0 && (element.tag.model & 0x10) != 0) {
                            lexer.popInline(element);
                            if (element.tag != tt.tagA) {
                                if (node.tag == tt.tagA && node.tag != element.tag) {
                                    lexer.report.warning(lexer, element, node, (short)7);
                                    lexer.ungetToken();
                                } else {
                                    lexer.report.warning(lexer, element, node, (short)10);
                                }
                                if ((mode & 2) == 0) {
                                    Node.trimSpaces(lexer, element);
                                }
                                Node.trimEmptyElement(lexer, element);
                                return;
                            }
                            lexer.report.warning(lexer, element, node, (short)8);
                            continue;
                        }
                        if (lexer.exiled && node.tag.model != 0 && (node.tag.model & 0x80) != 0) {
                            lexer.ungetToken();
                            Node.trimSpaces(lexer, element);
                            Node.trimEmptyElement(lexer, element);
                            return;
                        }
                    }
                }
                if ((node.tag.model & 0x4000) != 0 && (element.tag.model & 0x4000) != 0) {
                    if (node.tag == element.tag) {
                        lexer.report.warning(lexer, element, node, (short)10);
                    } else {
                        lexer.report.warning(lexer, element, node, (short)7);
                        lexer.ungetToken();
                    }
                    if ((mode & 2) == 0) {
                        Node.trimSpaces(lexer, element);
                    }
                    Node.trimEmptyElement(lexer, element);
                    return;
                }
                if (node.tag == tt.tagA && !node.implicit && (element.tag == tt.tagA || element.isDescendantOf(tt.tagA))) {
                    if (node.type != 6 && node.attributes == null) {
                        node.type = (short)6;
                        lexer.report.warning(lexer, element, node, (short)24);
                        lexer.ungetToken();
                        continue;
                    }
                    lexer.ungetToken();
                    lexer.report.warning(lexer, element, node, (short)7);
                    if ((mode & 2) == 0) {
                        Node.trimSpaces(lexer, element);
                    }
                    Node.trimEmptyElement(lexer, element);
                    return;
                }
                if ((element.tag.model & 0x4000) != 0) {
                    if (node.tag == tt.tagCenter || node.tag == tt.tagDiv) {
                        if (node.type != 5 && node.type != 7) {
                            lexer.report.warning(lexer, element, node, (short)8);
                            continue;
                        }
                        lexer.report.warning(lexer, element, node, (short)11);
                        if (element.content == null) {
                            Node.insertNodeAsParent(element, node);
                            continue;
                        }
                        element.insertNodeAfterElement(node);
                        if ((mode & 2) == 0) {
                            Node.trimSpaces(lexer, element);
                        }
                        element = lexer.cloneNode(element);
                        element.start = lexer.lexsize;
                        element.end = lexer.lexsize;
                        node.insertNodeAtEnd(element);
                        continue;
                    }
                    if (node.tag == tt.tagHr) {
                        if (node.type != 5 && node.type != 7) {
                            lexer.report.warning(lexer, element, node, (short)8);
                            continue;
                        }
                        lexer.report.warning(lexer, element, node, (short)11);
                        if (element.content == null) {
                            Node.insertNodeBeforeElement(element, node);
                            continue;
                        }
                        element.insertNodeAfterElement(node);
                        if ((mode & 2) == 0) {
                            Node.trimSpaces(lexer, element);
                        }
                        element = lexer.cloneNode(element);
                        element.start = lexer.lexsize;
                        element.end = lexer.lexsize;
                        node.insertNodeAfterElement(element);
                        continue;
                    }
                }
                if (element.tag == tt.tagDt && node.tag == tt.tagHr) {
                    if (node.type != 5 && node.type != 7) {
                        lexer.report.warning(lexer, element, node, (short)8);
                        continue;
                    }
                    lexer.report.warning(lexer, element, node, (short)11);
                    Node dd = lexer.inferredTag("dd");
                    if (element.content == null) {
                        Node.insertNodeBeforeElement(element, dd);
                        dd.insertNodeAtEnd(node);
                        continue;
                    }
                    element.insertNodeAfterElement(dd);
                    dd.insertNodeAtEnd(node);
                    if ((mode & 2) == 0) {
                        Node.trimSpaces(lexer, element);
                    }
                    element = lexer.cloneNode(element);
                    element.start = lexer.lexsize;
                    element.end = lexer.lexsize;
                    dd.insertNodeAfterElement(element);
                    continue;
                }
                if (node.type == 6) {
                    Node parent = element.parent;
                    while (parent != null) {
                        if (node.tag == parent.tag) {
                            if ((element.tag.model & 0x8000) == 0 && !element.implicit) {
                                lexer.report.warning(lexer, element, node, (short)7);
                            }
                            if (element.tag == tt.tagA) {
                                lexer.popInline(element);
                            }
                            lexer.ungetToken();
                            if ((mode & 2) == 0) {
                                Node.trimSpaces(lexer, element);
                            }
                            Node.trimEmptyElement(lexer, element);
                            return;
                        }
                        parent = parent.parent;
                    }
                }
                if ((node.tag.model & 0x10) == 0) {
                    if (node.type != 5) {
                        lexer.report.warning(lexer, element, node, (short)8);
                        continue;
                    }
                    if ((element.tag.model & 0x8000) == 0) {
                        lexer.report.warning(lexer, element, node, (short)7);
                    }
                    if ((node.tag.model & 4) != 0 && (node.tag.model & 8) == 0) {
                        ParserImpl.moveToHead(lexer, element, node);
                        continue;
                    }
                    if (element.tag == tt.tagA) {
                        if (node.tag != null && (node.tag.model & 0x4000) == 0) {
                            lexer.popInline(element);
                        } else if (element.content == null) {
                            Node.discardElement(element);
                            lexer.ungetToken();
                            return;
                        }
                    }
                    lexer.ungetToken();
                    if ((mode & 2) == 0) {
                        Node.trimSpaces(lexer, element);
                    }
                    Node.trimEmptyElement(lexer, element);
                    return;
                }
                if (node.type == 5 || node.type == 7) {
                    if (node.implicit) {
                        lexer.report.warning(lexer, element, node, (short)15);
                    }
                    if (node.tag == tt.tagBr) {
                        Node.trimSpaces(lexer, element);
                    }
                    element.insertNodeAtEnd(node);
                    ParserImpl.parseTag(lexer, node, mode);
                    continue;
                }
                lexer.report.warning(lexer, element, node, (short)8);
            }
            if ((element.tag.model & 0x8000) == 0) {
                lexer.report.warning(lexer, element, node, (short)6);
            }
            Node.trimEmptyElement(lexer, element);
        }
    }

    public static class ParseFrameSet
    implements Parser {
        public void parse(Lexer lexer, Node frameset, short mode) {
            Node node;
            TagTable tt = lexer.configuration.tt;
            lexer.badAccess = (short)(lexer.badAccess | 0x10);
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == frameset.tag && node.type == 6) {
                    frameset.closed = true;
                    Node.trimSpaces(lexer, frameset);
                    return;
                }
                if (Node.insertMisc(frameset, node)) continue;
                if (node.tag == null) {
                    lexer.report.warning(lexer, frameset, node, (short)8);
                    continue;
                }
                if ((node.type == 5 || node.type == 7) && node.tag != null && (node.tag.model & 4) != 0) {
                    ParserImpl.moveToHead(lexer, frameset, node);
                    continue;
                }
                if (node.tag == tt.tagBody) {
                    lexer.ungetToken();
                    node = lexer.inferredTag("noframes");
                    lexer.report.warning(lexer, frameset, node, (short)15);
                }
                if (node.type == 5 && (node.tag.model & 0x2000) != 0) {
                    frameset.insertNodeAtEnd(node);
                    lexer.excludeBlocks = false;
                    ParserImpl.parseTag(lexer, node, (short)1);
                    continue;
                }
                if (node.type == 7 && (node.tag.model & 0x2000) != 0) {
                    frameset.insertNodeAtEnd(node);
                    continue;
                }
                lexer.report.warning(lexer, frameset, node, (short)8);
            }
            lexer.report.warning(lexer, frameset, node, (short)6);
        }
    }

    public static class ParseBody
    implements Parser {
        public void parse(Lexer lexer, Node body, short mode) {
            Node node;
            mode = 0;
            boolean checkstack = true;
            TagTable tt = lexer.configuration.tt;
            Clean.bumpObject(lexer, body.parent);
            while ((node = lexer.getToken(mode)) != null) {
                if (node.tag == tt.tagHtml) {
                    if (node.type == 5 || node.type == 7 || lexer.seenEndHtml) {
                        lexer.report.warning(lexer, body, node, (short)8);
                        continue;
                    }
                    lexer.seenEndHtml = true;
                    continue;
                }
                if (lexer.seenEndBody && (node.type == 5 || node.type == 6 || node.type == 7)) {
                    lexer.report.warning(lexer, body, node, (short)27);
                }
                if (node.tag == body.tag && node.type == 6) {
                    body.closed = true;
                    Node.trimSpaces(lexer, body);
                    lexer.seenEndBody = true;
                    mode = 0;
                    if (body.parent.tag != tt.tagNoframes) continue;
                    break;
                }
                if (node.tag == tt.tagNoframes) {
                    if (node.type == 5) {
                        body.insertNodeAtEnd(node);
                        BLOCK.parse(lexer, node, mode);
                        continue;
                    }
                    if (node.type == 6 && body.parent.tag == tt.tagNoframes) {
                        Node.trimSpaces(lexer, body);
                        lexer.ungetToken();
                        break;
                    }
                }
                if ((node.tag == tt.tagFrame || node.tag == tt.tagFrameset) && body.parent.tag == tt.tagNoframes) {
                    Node.trimSpaces(lexer, body);
                    lexer.ungetToken();
                    break;
                }
                boolean iswhitenode = false;
                if (node.type == 4 && node.end <= node.start + 1 && node.textarray[node.start] == 32) {
                    iswhitenode = true;
                }
                if (Node.insertMisc(body, node)) continue;
                if (node.type == 4) {
                    if (iswhitenode && mode == 0) continue;
                    if (lexer.configuration.encloseBodyText && !iswhitenode) {
                        lexer.ungetToken();
                        Node para = lexer.inferredTag("p");
                        body.insertNodeAtEnd(para);
                        ParserImpl.parseTag(lexer, para, mode);
                        mode = 1;
                        continue;
                    }
                    lexer.constrainVersion(-6);
                    if (checkstack) {
                        checkstack = false;
                        if (lexer.inlineDup(node) > 0) continue;
                    }
                    body.insertNodeAtEnd(node);
                    mode = 1;
                    continue;
                }
                if (node.type == 1) {
                    Node.insertDocType(lexer, body, node);
                    continue;
                }
                if (node.tag == null || node.tag == tt.tagParam) {
                    lexer.report.warning(lexer, body, node, (short)8);
                    continue;
                }
                lexer.excludeBlocks = false;
                if ((node.tag.model & 8) == 0 && (node.tag.model & 0x10) == 0 || node.tag == tt.tagInput) {
                    if ((node.tag.model & 4) == 0) {
                        lexer.report.warning(lexer, body, node, (short)11);
                    }
                    if ((node.tag.model & 2) != 0) {
                        if (node.tag != tt.tagBody || !body.implicit || body.attributes != null) continue;
                        body.attributes = node.attributes;
                        node.attributes = null;
                        continue;
                    }
                    if ((node.tag.model & 4) != 0) {
                        ParserImpl.moveToHead(lexer, body, node);
                        continue;
                    }
                    if ((node.tag.model & 0x20) != 0) {
                        lexer.ungetToken();
                        node = lexer.inferredTag("ul");
                        node.addClass("noindent");
                        lexer.excludeBlocks = true;
                    } else if ((node.tag.model & 0x40) != 0) {
                        lexer.ungetToken();
                        node = lexer.inferredTag("dl");
                        lexer.excludeBlocks = true;
                    } else if ((node.tag.model & 0x380) != 0) {
                        lexer.ungetToken();
                        node = lexer.inferredTag("table");
                        lexer.excludeBlocks = true;
                    } else if (node.tag == tt.tagInput) {
                        lexer.ungetToken();
                        node = lexer.inferredTag("form");
                        lexer.excludeBlocks = true;
                    } else {
                        if ((node.tag.model & 0x600) != 0) continue;
                        lexer.ungetToken();
                        return;
                    }
                }
                if (node.type == 6) {
                    if (node.tag == tt.tagBr) {
                        node.type = (short)5;
                    } else if (node.tag == tt.tagP) {
                        Node.coerceNode(lexer, node, tt.tagBr);
                        body.insertNodeAtEnd(node);
                        node = lexer.inferredTag("br");
                    } else if ((node.tag.model & 0x10) != 0) {
                        lexer.popInline(node);
                    }
                }
                if (node.type == 5 || node.type == 7) {
                    if ((node.tag.model & 0x10) != 0 && (node.tag.model & 0x20000) == 0) {
                        if (node.tag == tt.tagImg) {
                            lexer.constrainVersion(-5);
                        } else {
                            lexer.constrainVersion(-6);
                        }
                        if (checkstack && !node.implicit) {
                            checkstack = false;
                            if (lexer.inlineDup(node) > 0) continue;
                        }
                        mode = 1;
                    } else {
                        checkstack = true;
                        mode = 0;
                    }
                    if (node.implicit) {
                        lexer.report.warning(lexer, body, node, (short)15);
                    }
                    body.insertNodeAtEnd(node);
                    ParserImpl.parseTag(lexer, node, mode);
                    continue;
                }
                lexer.report.warning(lexer, body, node, (short)8);
            }
        }
    }

    public static class ParseScript
    implements Parser {
        public void parse(Lexer lexer, Node script, short mode) {
            Node node = lexer.getCDATA(script);
            if (node != null) {
                script.insertNodeAtEnd(node);
            }
        }
    }

    public static class ParseTitle
    implements Parser {
        public void parse(Lexer lexer, Node title, short mode) {
            Node node;
            while ((node = lexer.getToken((short)1)) != null) {
                if (node.tag == title.tag && node.type == 5) {
                    lexer.report.warning(lexer, title, node, (short)24);
                    node.type = (short)6;
                    continue;
                }
                if (node.tag == title.tag && node.type == 6) {
                    title.closed = true;
                    Node.trimSpaces(lexer, title);
                    return;
                }
                if (node.type == 4) {
                    if (title.content == null) {
                        Node.trimInitialSpace(lexer, title, node);
                    }
                    if (node.start >= node.end) continue;
                    title.insertNodeAtEnd(node);
                    continue;
                }
                if (Node.insertMisc(title, node)) continue;
                if (node.tag == null) {
                    lexer.report.warning(lexer, title, node, (short)8);
                    continue;
                }
                lexer.report.warning(lexer, title, node, (short)7);
                lexer.ungetToken();
                Node.trimSpaces(lexer, title);
                return;
            }
            lexer.report.warning(lexer, title, node, (short)6);
        }
    }

    public static class ParseHead
    implements Parser {
        public void parse(Lexer lexer, Node head, short mode) {
            Node node;
            int hasTitle = 0;
            int hasBase = 0;
            TagTable tt = lexer.configuration.tt;
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == head.tag && node.type == 6) {
                    head.closed = true;
                    break;
                }
                if (node.type == 4) {
                    lexer.report.warning(lexer, head, node, (short)11);
                    lexer.ungetToken();
                    break;
                }
                if (Node.insertMisc(head, node)) continue;
                if (node.type == 1) {
                    Node.insertDocType(lexer, head, node);
                    continue;
                }
                if (node.tag == null) {
                    lexer.report.warning(lexer, head, node, (short)8);
                    continue;
                }
                if (!TidyUtils.toBoolean(node.tag.model & 4)) {
                    if (lexer.isvoyager) {
                        lexer.report.warning(lexer, head, node, (short)11);
                    }
                    lexer.ungetToken();
                    break;
                }
                if (node.type == 5 || node.type == 7) {
                    if (node.tag == tt.tagTitle) {
                        if (++hasTitle > 1) {
                            lexer.report.warning(lexer, head, node, (short)38);
                        }
                    } else if (node.tag == tt.tagBase) {
                        if (++hasBase > 1) {
                            lexer.report.warning(lexer, head, node, (short)38);
                        }
                    } else if (node.tag == tt.tagNoscript) {
                        lexer.report.warning(lexer, head, node, (short)11);
                    }
                    head.insertNodeAtEnd(node);
                    ParserImpl.parseTag(lexer, node, (short)0);
                    continue;
                }
                lexer.report.warning(lexer, head, node, (short)8);
            }
            if (hasTitle == 0) {
                if (!lexer.configuration.bodyOnly) {
                    lexer.report.warning(lexer, head, null, (short)17);
                }
                head.insertNodeAtEnd(lexer.inferredTag("title"));
            }
        }
    }

    public static class ParseHTML
    implements Parser {
        public void parse(Lexer lexer, Node html, short mode) {
            Node node;
            block28: {
                TagTable tt;
                Node noframes;
                Node frameset;
                block27: {
                    frameset = null;
                    noframes = null;
                    lexer.configuration.xmlTags = false;
                    lexer.seenEndBody = false;
                    tt = lexer.configuration.tt;
                    while (true) {
                        if ((node = lexer.getToken((short)0)) == null) {
                            node = lexer.inferredTag("head");
                            break block27;
                        }
                        if (node.tag == tt.tagHead) break block27;
                        if (node.tag == html.tag && node.type == 6) {
                            lexer.report.warning(lexer, html, node, (short)8);
                            continue;
                        }
                        if (!Node.insertMisc(html, node)) break;
                    }
                    lexer.ungetToken();
                    node = lexer.inferredTag("head");
                }
                Node head = node;
                html.insertNodeAtEnd(head);
                HEAD.parse(lexer, head, mode);
                block1: while (true) {
                    if ((node = lexer.getToken((short)0)) == null) {
                        if (frameset == null) {
                            node = lexer.inferredTag("body");
                            html.insertNodeAtEnd(node);
                            BODY.parse(lexer, node, mode);
                        }
                        return;
                    }
                    if (node.tag == html.tag) {
                        if (node.type != 5 && frameset == null) {
                            lexer.report.warning(lexer, html, node, (short)8);
                            continue;
                        }
                        if (node.type != 6) continue;
                        lexer.seenEndHtml = true;
                        continue;
                    }
                    if (Node.insertMisc(html, node)) continue;
                    if (node.tag == tt.tagBody) {
                        if (node.type != 5) {
                            lexer.report.warning(lexer, html, node, (short)8);
                            continue;
                        }
                        if (frameset != null) {
                            lexer.ungetToken();
                            if (noframes == null) {
                                noframes = lexer.inferredTag("noframes");
                                frameset.insertNodeAtEnd(noframes);
                                lexer.report.warning(lexer, html, noframes, (short)15);
                            }
                            ParserImpl.parseTag(lexer, noframes, mode);
                            continue;
                        }
                        lexer.constrainVersion(-17);
                        break block28;
                    }
                    if (node.tag == tt.tagFrameset) {
                        if (node.type != 5) {
                            lexer.report.warning(lexer, html, node, (short)8);
                            continue;
                        }
                        if (frameset != null) {
                            lexer.report.error(lexer, html, node, (short)18);
                        } else {
                            frameset = node;
                        }
                        html.insertNodeAtEnd(node);
                        ParserImpl.parseTag(lexer, node, mode);
                        node = frameset.content;
                        while (true) {
                            if (node == null) continue block1;
                            if (node.tag == tt.tagNoframes) {
                                noframes = node;
                            }
                            node = node.next;
                        }
                    }
                    if (node.tag == tt.tagNoframes) {
                        if (node.type != 5) {
                            lexer.report.warning(lexer, html, node, (short)8);
                            continue;
                        }
                        if (frameset == null) {
                            lexer.report.warning(lexer, html, node, (short)8);
                            node = lexer.inferredTag("body");
                            break block28;
                        }
                        if (noframes == null) {
                            noframes = node;
                            frameset.insertNodeAtEnd(noframes);
                        }
                        ParserImpl.parseTag(lexer, noframes, mode);
                        continue;
                    }
                    if (node.type == 5 || node.type == 7) {
                        if (node.tag != null && (node.tag.model & 4) != 0) {
                            ParserImpl.moveToHead(lexer, html, node);
                            continue;
                        }
                        if (frameset != null && node.tag == tt.tagFrame) {
                            lexer.report.warning(lexer, html, node, (short)8);
                            continue;
                        }
                    }
                    lexer.ungetToken();
                    if (frameset == null) break;
                    if (noframes == null) {
                        noframes = lexer.inferredTag("noframes");
                        frameset.insertNodeAtEnd(noframes);
                    } else {
                        lexer.report.warning(lexer, html, node, (short)26);
                    }
                    lexer.constrainVersion(16);
                    ParserImpl.parseTag(lexer, noframes, mode);
                }
                node = lexer.inferredTag("body");
                lexer.constrainVersion(-17);
            }
            html.insertNodeAtEnd(node);
            ParserImpl.parseTag(lexer, node, mode);
            lexer.seenEndHtml = true;
        }
    }
}


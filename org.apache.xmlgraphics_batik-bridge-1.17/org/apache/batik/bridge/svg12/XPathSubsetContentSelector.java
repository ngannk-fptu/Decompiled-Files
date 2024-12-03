/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.XBLOMContentElement
 *  org.apache.batik.parser.AbstractScanner
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.xml.XMLUtilities
 */
package org.apache.batik.bridge.svg12;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.batik.anim.dom.XBLOMContentElement;
import org.apache.batik.bridge.svg12.AbstractContentSelector;
import org.apache.batik.bridge.svg12.ContentManager;
import org.apache.batik.parser.AbstractScanner;
import org.apache.batik.parser.ParseException;
import org.apache.batik.xml.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathSubsetContentSelector
extends AbstractContentSelector {
    protected static final int SELECTOR_INVALID = -1;
    protected static final int SELECTOR_ANY = 0;
    protected static final int SELECTOR_QNAME = 1;
    protected static final int SELECTOR_ID = 2;
    protected int selectorType;
    protected String prefix;
    protected String localName;
    protected int index;
    protected SelectedNodes selectedContent;

    public XPathSubsetContentSelector(ContentManager cm, XBLOMContentElement content, Element bound, String selector) {
        super(cm, content, bound);
        this.parseSelector(selector);
    }

    protected void parseSelector(String selector) {
        this.selectorType = -1;
        Scanner scanner = new Scanner(selector);
        int token = scanner.next();
        if (token == 1) {
            String name1 = scanner.getStringValue();
            token = scanner.next();
            if (token == 0) {
                this.selectorType = 1;
                this.prefix = null;
                this.localName = name1;
                this.index = 0;
                return;
            }
            if (token == 2) {
                token = scanner.next();
                if (token == 1) {
                    String name2 = scanner.getStringValue();
                    token = scanner.next();
                    if (token == 0) {
                        this.selectorType = 1;
                        this.prefix = name1;
                        this.localName = name2;
                        this.index = 0;
                        return;
                    }
                    if (token == 3 && (token = scanner.next()) == 8) {
                        int number = Integer.parseInt(scanner.getStringValue());
                        token = scanner.next();
                        if (token == 4 && (token = scanner.next()) == 0) {
                            this.selectorType = 1;
                            this.prefix = name1;
                            this.localName = name2;
                            this.index = number;
                            return;
                        }
                    }
                } else if (token == 3) {
                    token = scanner.next();
                    if (token == 8) {
                        int number = Integer.parseInt(scanner.getStringValue());
                        token = scanner.next();
                        if (token == 4 && (token = scanner.next()) == 0) {
                            this.selectorType = 1;
                            this.prefix = null;
                            this.localName = name1;
                            this.index = number;
                            return;
                        }
                    }
                } else if (token == 5 && name1.equals("id") && (token = scanner.next()) == 7) {
                    String id = scanner.getStringValue();
                    token = scanner.next();
                    if (token == 6 && (token = scanner.next()) == 0) {
                        this.selectorType = 2;
                        this.localName = id;
                        return;
                    }
                }
            }
        } else if (token == 9) {
            token = scanner.next();
            if (token == 0) {
                this.selectorType = 0;
                return;
            }
            if (token == 3 && (token = scanner.next()) == 8) {
                int number = Integer.parseInt(scanner.getStringValue());
                token = scanner.next();
                if (token == 4 && (token = scanner.next()) == 0) {
                    this.selectorType = 0;
                    this.index = number;
                    return;
                }
            }
        }
    }

    @Override
    public NodeList getSelectedContent() {
        if (this.selectedContent == null) {
            this.selectedContent = new SelectedNodes();
        }
        return this.selectedContent;
    }

    @Override
    boolean update() {
        if (this.selectedContent == null) {
            this.selectedContent = new SelectedNodes();
            return true;
        }
        return this.selectedContent.update();
    }

    protected static class Scanner
    extends AbstractScanner {
        public static final int EOF = 0;
        public static final int NAME = 1;
        public static final int COLON = 2;
        public static final int LEFT_SQUARE_BRACKET = 3;
        public static final int RIGHT_SQUARE_BRACKET = 4;
        public static final int LEFT_PARENTHESIS = 5;
        public static final int RIGHT_PARENTHESIS = 6;
        public static final int STRING = 7;
        public static final int NUMBER = 8;
        public static final int ASTERISK = 9;

        public Scanner(String s) {
            super(s);
        }

        protected int endGap() {
            return this.current == -1 ? 0 : 1;
        }

        protected void nextToken() throws ParseException {
            try {
                switch (this.current) {
                    case -1: {
                        this.type = 0;
                        return;
                    }
                    case 58: {
                        this.nextChar();
                        this.type = 2;
                        return;
                    }
                    case 91: {
                        this.nextChar();
                        this.type = 3;
                        return;
                    }
                    case 93: {
                        this.nextChar();
                        this.type = 4;
                        return;
                    }
                    case 40: {
                        this.nextChar();
                        this.type = 5;
                        return;
                    }
                    case 41: {
                        this.nextChar();
                        this.type = 6;
                        return;
                    }
                    case 42: {
                        this.nextChar();
                        this.type = 9;
                        return;
                    }
                    case 9: 
                    case 10: 
                    case 12: 
                    case 13: 
                    case 32: {
                        do {
                            this.nextChar();
                        } while (XMLUtilities.isXMLSpace((char)((char)this.current)));
                        this.nextToken();
                        return;
                    }
                    case 39: {
                        this.type = this.string1();
                        return;
                    }
                    case 34: {
                        this.type = this.string2();
                        return;
                    }
                    case 48: 
                    case 49: 
                    case 50: 
                    case 51: 
                    case 52: 
                    case 53: 
                    case 54: 
                    case 55: 
                    case 56: 
                    case 57: {
                        this.type = this.number();
                        return;
                    }
                }
                if (XMLUtilities.isXMLNameFirstCharacter((char)((char)this.current))) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && this.current != 58 && XMLUtilities.isXMLNameCharacter((char)((char)this.current)));
                    this.type = 1;
                    return;
                }
                this.nextChar();
                throw new ParseException("identifier.character", this.reader.getLine(), this.reader.getColumn());
            }
            catch (IOException e) {
                throw new ParseException((Exception)e);
            }
        }

        protected int string1() throws IOException {
            this.start = this.position;
            block4: while (true) {
                switch (this.nextChar()) {
                    case -1: {
                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                    }
                    case 39: {
                        break block4;
                    }
                    default: {
                        continue block4;
                    }
                }
                break;
            }
            this.nextChar();
            return 7;
        }

        protected int string2() throws IOException {
            this.start = this.position;
            block4: while (true) {
                switch (this.nextChar()) {
                    case -1: {
                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                    }
                    case 34: {
                        break block4;
                    }
                    default: {
                        continue block4;
                    }
                }
                break;
            }
            this.nextChar();
            return 7;
        }

        protected int number() throws IOException {
            block7: while (true) {
                switch (this.nextChar()) {
                    case 46: {
                        switch (this.nextChar()) {
                            case 48: 
                            case 49: 
                            case 50: 
                            case 51: 
                            case 52: 
                            case 53: 
                            case 54: 
                            case 55: 
                            case 56: 
                            case 57: {
                                return this.dotNumber();
                            }
                        }
                        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                    }
                    default: {
                        break block7;
                    }
                    case 48: 
                    case 49: 
                    case 50: 
                    case 51: 
                    case 52: 
                    case 53: 
                    case 54: 
                    case 55: 
                    case 56: 
                    case 57: {
                        continue block7;
                    }
                }
                break;
            }
            return 8;
        }

        protected int dotNumber() throws IOException {
            block3: while (true) {
                switch (this.nextChar()) {
                    default: {
                        break block3;
                    }
                    case 48: 
                    case 49: 
                    case 50: 
                    case 51: 
                    case 52: 
                    case 53: 
                    case 54: 
                    case 55: 
                    case 56: 
                    case 57: {
                        continue block3;
                    }
                }
                break;
            }
            return 8;
        }
    }

    protected class SelectedNodes
    implements NodeList {
        protected ArrayList nodes = new ArrayList(10);

        public SelectedNodes() {
            this.update();
        }

        protected boolean update() {
            ArrayList oldNodes = (ArrayList)this.nodes.clone();
            this.nodes.clear();
            int nth = 0;
            for (Node n = XPathSubsetContentSelector.this.boundElement.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() != 1) continue;
                Element e = (Element)n;
                boolean matched = XPathSubsetContentSelector.this.selectorType == 0;
                switch (XPathSubsetContentSelector.this.selectorType) {
                    case 2: {
                        matched = e.getAttributeNS(null, "id").equals(XPathSubsetContentSelector.this.localName);
                        break;
                    }
                    case 1: {
                        if (XPathSubsetContentSelector.this.prefix == null) {
                            matched = e.getNamespaceURI() == null;
                        } else {
                            String ns = XPathSubsetContentSelector.this.contentElement.lookupNamespaceURI(XPathSubsetContentSelector.this.prefix);
                            if (ns != null) {
                                matched = e.getNamespaceURI().equals(ns);
                            }
                        }
                        boolean bl = matched = matched && XPathSubsetContentSelector.this.localName.equals(e.getLocalName());
                    }
                }
                if (XPathSubsetContentSelector.this.selectorType == 0 || XPathSubsetContentSelector.this.selectorType == 1) {
                    boolean bl = matched = matched && (XPathSubsetContentSelector.this.index == 0 || ++nth == XPathSubsetContentSelector.this.index);
                }
                if (!matched || XPathSubsetContentSelector.this.isSelected(n)) continue;
                this.nodes.add(e);
            }
            int nodesSize = this.nodes.size();
            if (oldNodes.size() != nodesSize) {
                return true;
            }
            for (int i = 0; i < nodesSize; ++i) {
                if (oldNodes.get(i) == this.nodes.get(i)) continue;
                return true;
            }
            return false;
        }

        @Override
        public Node item(int index) {
            if (index < 0 || index >= this.nodes.size()) {
                return null;
            }
            return (Node)this.nodes.get(index);
        }

        @Override
        public int getLength() {
            return this.nodes.size();
        }
    }
}


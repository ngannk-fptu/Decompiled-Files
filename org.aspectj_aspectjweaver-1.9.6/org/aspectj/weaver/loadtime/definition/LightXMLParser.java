/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime.definition;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LightXMLParser {
    private static final char NULL_CHAR = '\u0000';
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private ArrayList children = new ArrayList();
    private String name = null;
    private char pushedBackChar;
    private Reader reader;
    private static Map<String, char[]> entities = new HashMap<String, char[]>();

    public ArrayList getChildrens() {
        return this.children;
    }

    public String getName() {
        return this.name;
    }

    public void parseFromReader(Reader reader) throws Exception {
        char c;
        this.pushedBackChar = '\u0000';
        this.attributes = new HashMap<String, Object>();
        this.name = null;
        this.children = new ArrayList();
        this.reader = reader;
        while (true) {
            if ((c = this.skipBlanks()) != '<') {
                throw new Exception("LightParser Exception: Expected < but got: " + c);
            }
            c = this.getNextChar();
            if (c != '!' && c != '?') break;
            this.skipCommentOrXmlTag(0);
        }
        this.pushBackChar(c);
        this.parseNode(this);
    }

    private char skipBlanks() throws Exception {
        char c;
        block3: while (true) {
            c = this.getNextChar();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    continue block3;
                }
            }
            break;
        }
        return c;
    }

    private char getWhitespaces(StringBuffer result) throws Exception {
        char c;
        block4: while (true) {
            c = this.getNextChar();
            switch (c) {
                case '\t': 
                case '\n': 
                case ' ': {
                    result.append(c);
                }
                case '\r': {
                    continue block4;
                }
            }
            break;
        }
        return c;
    }

    private void getNodeName(StringBuffer result) throws Exception {
        while (true) {
            char c;
            if (!((c = this.getNextChar()) >= 'a' && c <= 'z' || c <= 'Z' && c >= 'A' || c <= '9' && c >= '0' || c == '_' || c == '-' || c == '.' || c == ':')) {
                this.pushBackChar(c);
                return;
            }
            result.append(c);
        }
    }

    private void getString(StringBuffer string) throws Exception {
        char delimiter = this.getNextChar();
        if (delimiter != '\'' && delimiter != '\"') {
            throw new Exception("Parsing error. Expected ' or \"  but got: " + delimiter);
        }
        char c;
        while ((c = this.getNextChar()) != delimiter) {
            if (c == '&') {
                this.mapEntity(string);
                continue;
            }
            string.append(c);
        }
        return;
    }

    private void getPCData(StringBuffer data) throws Exception {
        while (true) {
            char c;
            if ((c = this.getNextChar()) == '<') {
                c = this.getNextChar();
                if (c == '!') {
                    this.checkCDATA(data);
                    continue;
                }
                this.pushBackChar(c);
                return;
            }
            data.append(c);
        }
    }

    private boolean checkCDATA(StringBuffer buf) throws Exception {
        char c = this.getNextChar();
        if (c != '[') {
            this.pushBackChar(c);
            this.skipCommentOrXmlTag(0);
            return false;
        }
        if (!this.checkLiteral("CDATA[")) {
            this.skipCommentOrXmlTag(1);
            return false;
        }
        int delimiterCharsSkipped = 0;
        block4: while (delimiterCharsSkipped < 3) {
            int i;
            c = this.getNextChar();
            switch (c) {
                case ']': {
                    if (delimiterCharsSkipped < 2) {
                        ++delimiterCharsSkipped;
                        continue block4;
                    }
                    buf.append(']');
                    buf.append(']');
                    delimiterCharsSkipped = 0;
                    continue block4;
                }
                case '>': {
                    if (delimiterCharsSkipped < 2) {
                        for (i = 0; i < delimiterCharsSkipped; ++i) {
                            buf.append(']');
                        }
                        delimiterCharsSkipped = 0;
                        buf.append('>');
                        continue block4;
                    }
                    delimiterCharsSkipped = 3;
                    continue block4;
                }
            }
            for (i = 0; i < delimiterCharsSkipped; ++i) {
                buf.append(']');
            }
            buf.append(c);
            delimiterCharsSkipped = 0;
        }
        return true;
    }

    private void skipCommentOrXmlTag(int bracketLevel) throws Exception {
        char c;
        char delim = '\u0000';
        int level = 1;
        if (bracketLevel == 0) {
            c = this.getNextChar();
            if (c == '-') {
                c = this.getNextChar();
                if (c == ']') {
                    --bracketLevel;
                } else if (c == '[') {
                    ++bracketLevel;
                } else if (c == '-') {
                    this.skipComment();
                    return;
                }
            } else if (c == '[') {
                ++bracketLevel;
            }
        }
        while (level > 0) {
            c = this.getNextChar();
            if (delim == '\u0000') {
                if (c == '\"' || c == '\'') {
                    delim = c;
                } else if (bracketLevel <= 0) {
                    if (c == '<') {
                        ++level;
                    } else if (c == '>') {
                        --level;
                    }
                }
                if (c == '[') {
                    ++bracketLevel;
                    continue;
                }
                if (c != ']') continue;
                --bracketLevel;
                continue;
            }
            if (c != delim) continue;
            delim = '\u0000';
        }
    }

    private void parseNode(LightXMLParser elt) throws Exception {
        char c;
        String name;
        StringBuffer buf;
        block20: {
            buf = new StringBuffer();
            this.getNodeName(buf);
            name = buf.toString();
            elt.setName(name);
            c = this.skipBlanks();
            while (c != '>' && c != '/') {
                this.emptyBuf(buf);
                this.pushBackChar(c);
                this.getNodeName(buf);
                String key = buf.toString();
                c = this.skipBlanks();
                if (c != '=') {
                    throw new Exception("Parsing error. Expected = but got: " + c);
                }
                this.pushBackChar(this.skipBlanks());
                this.emptyBuf(buf);
                this.getString(buf);
                elt.setAttribute(key, buf);
                c = this.skipBlanks();
            }
            if (c == '/') {
                c = this.getNextChar();
                if (c != '>') {
                    throw new Exception("Parsing error. Expected > but got: " + c);
                }
                return;
            }
            this.emptyBuf(buf);
            c = this.getWhitespaces(buf);
            if (c != '<') {
                this.pushBackChar(c);
                this.getPCData(buf);
            } else {
                while ((c = this.getNextChar()) == '!') {
                    if (this.checkCDATA(buf)) {
                        this.getPCData(buf);
                    } else {
                        c = this.getWhitespaces(buf);
                        if (c == '<') continue;
                        this.pushBackChar(c);
                        this.getPCData(buf);
                    }
                    break block20;
                }
                if (c != '/') {
                    this.emptyBuf(buf);
                }
                if (c == '/') {
                    this.pushBackChar(c);
                }
            }
        }
        if (buf.length() == 0) {
            while (c != '/') {
                if (c == '!') {
                    for (int i = 0; i < 2; ++i) {
                        c = this.getNextChar();
                        if (c == '-') continue;
                        throw new Exception("Parsing error. Expected element or comment");
                    }
                    this.skipComment();
                } else {
                    this.pushBackChar(c);
                    LightXMLParser child = this.createAnotherElement();
                    this.parseNode(child);
                    elt.addChild(child);
                }
                c = this.skipBlanks();
                if (c != '<') {
                    throw new Exception("Parsing error. Expected <, but got: " + c);
                }
                c = this.getNextChar();
            }
            this.pushBackChar(c);
        }
        if ((c = this.getNextChar()) != '/') {
            throw new Exception("Parsing error. Expected /, but got: " + c);
        }
        this.pushBackChar(this.skipBlanks());
        if (!this.checkLiteral(name)) {
            throw new Exception("Parsing error. Expected " + name);
        }
        if (this.skipBlanks() != '>') {
            throw new Exception("Parsing error. Expected >, but got: " + c);
        }
    }

    private void skipComment() throws Exception {
        int dashes = 2;
        while (dashes > 0) {
            char ch = this.getNextChar();
            if (ch == '-') {
                --dashes;
                continue;
            }
            dashes = 2;
        }
        char nextChar = this.getNextChar();
        if (nextChar != '>') {
            throw new Exception("Parsing error. Expected > but got: " + nextChar);
        }
    }

    private boolean checkLiteral(String literal) throws Exception {
        int length = literal.length();
        for (int i = 0; i < length; ++i) {
            if (this.getNextChar() == literal.charAt(i)) continue;
            return false;
        }
        return true;
    }

    private char getNextChar() throws Exception {
        if (this.pushedBackChar != '\u0000') {
            char c = this.pushedBackChar;
            this.pushedBackChar = '\u0000';
            return c;
        }
        int i = this.reader.read();
        if (i < 0) {
            throw new Exception("Parsing error. Unexpected end of data");
        }
        return (char)i;
    }

    private void mapEntity(StringBuffer buf) throws Exception {
        char c = '\u0000';
        StringBuffer keyBuf = new StringBuffer();
        while ((c = this.getNextChar()) != ';') {
            keyBuf.append(c);
        }
        String key = keyBuf.toString();
        if (key.charAt(0) == '#') {
            try {
                c = key.charAt(1) == 'x' ? (char)Integer.parseInt(key.substring(2), 16) : (char)Integer.parseInt(key.substring(1), 10);
            }
            catch (NumberFormatException e) {
                throw new Exception("Unknown entity: " + key);
            }
            buf.append(c);
        } else {
            char[] value = entities.get(key);
            if (value == null) {
                throw new Exception("Unknown entity: " + key);
            }
            buf.append(value);
        }
    }

    private void pushBackChar(char c) {
        this.pushedBackChar = c;
    }

    private void addChild(LightXMLParser child) {
        this.children.add(child);
    }

    private void setAttribute(String name, Object value) {
        this.attributes.put(name, value.toString());
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    private LightXMLParser createAnotherElement() {
        return new LightXMLParser();
    }

    private void setName(String name) {
        this.name = name;
    }

    private void emptyBuf(StringBuffer buf) {
        buf.setLength(0);
    }

    static {
        entities.put("amp", new char[]{'&'});
        entities.put("quot", new char[]{'\"'});
        entities.put("apos", new char[]{'\''});
        entities.put("lt", new char[]{'<'});
        entities.put("gt", new char[]{'>'});
    }
}


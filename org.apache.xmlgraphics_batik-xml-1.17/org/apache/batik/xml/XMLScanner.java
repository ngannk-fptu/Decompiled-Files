/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.i18n.Localizable
 *  org.apache.batik.i18n.LocalizableSupport
 *  org.apache.batik.util.io.NormalizingReader
 *  org.apache.batik.util.io.StreamNormalizingReader
 *  org.apache.batik.util.io.StringNormalizingReader
 */
package org.apache.batik.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.util.io.NormalizingReader;
import org.apache.batik.util.io.StreamNormalizingReader;
import org.apache.batik.util.io.StringNormalizingReader;
import org.apache.batik.xml.XMLException;
import org.apache.batik.xml.XMLUtilities;

public class XMLScanner
implements Localizable {
    public static final int DOCUMENT_START_CONTEXT = 0;
    public static final int TOP_LEVEL_CONTEXT = 1;
    public static final int PI_CONTEXT = 2;
    public static final int XML_DECL_CONTEXT = 3;
    public static final int DOCTYPE_CONTEXT = 4;
    public static final int START_TAG_CONTEXT = 5;
    public static final int CONTENT_CONTEXT = 6;
    public static final int DTD_DECLARATIONS_CONTEXT = 7;
    public static final int CDATA_SECTION_CONTEXT = 8;
    public static final int END_TAG_CONTEXT = 9;
    public static final int ATTRIBUTE_VALUE_CONTEXT = 10;
    public static final int ATTLIST_CONTEXT = 11;
    public static final int ELEMENT_DECLARATION_CONTEXT = 12;
    public static final int ENTITY_CONTEXT = 13;
    public static final int NOTATION_CONTEXT = 14;
    public static final int NOTATION_TYPE_CONTEXT = 15;
    public static final int ENUMERATION_CONTEXT = 16;
    public static final int ENTITY_VALUE_CONTEXT = 17;
    protected static final String BUNDLE_CLASSNAME = "org.apache.batik.xml.resources.Messages";
    protected LocalizableSupport localizableSupport = new LocalizableSupport("org.apache.batik.xml.resources.Messages", XMLScanner.class.getClassLoader());
    protected NormalizingReader reader;
    protected int current;
    protected int type;
    protected char[] buffer = new char[1024];
    protected int position;
    protected int start;
    protected int end;
    protected int context = 0;
    protected int depth;
    protected boolean piEndRead;
    protected boolean inDTD;
    protected char attrDelimiter;
    protected boolean cdataEndRead;

    public XMLScanner(Reader r) throws XMLException {
        try {
            this.reader = new StreamNormalizingReader(r);
            this.current = this.nextChar();
        }
        catch (IOException e) {
            throw new XMLException(e);
        }
    }

    public XMLScanner(InputStream is, String enc) throws XMLException {
        try {
            this.reader = new StreamNormalizingReader(is, enc);
            this.current = this.nextChar();
        }
        catch (IOException e) {
            throw new XMLException(e);
        }
    }

    public XMLScanner(String s) throws XMLException {
        try {
            this.reader = new StringNormalizingReader(s);
            this.current = this.nextChar();
        }
        catch (IOException e) {
            throw new XMLException(e);
        }
    }

    public void setLocale(Locale l) {
        this.localizableSupport.setLocale(l);
    }

    public Locale getLocale() {
        return this.localizableSupport.getLocale();
    }

    public String formatMessage(String key, Object[] args) throws MissingResourceException {
        return this.localizableSupport.formatMessage(key, args);
    }

    public void setDepth(int i) {
        this.depth = i;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setContext(int c) {
        this.context = c;
    }

    public int getContext() {
        return this.context;
    }

    public int getType() {
        return this.type;
    }

    public int getLine() {
        return this.reader.getLine();
    }

    public int getColumn() {
        return this.reader.getColumn();
    }

    public char[] getBuffer() {
        return this.buffer;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public char getStringDelimiter() {
        return this.attrDelimiter;
    }

    public int getStartOffset() {
        switch (this.type) {
            case 21: {
                return -3;
            }
            case 7: {
                return -2;
            }
            case 9: 
            case 13: 
            case 16: 
            case 25: 
            case 34: {
                return 1;
            }
            case 5: 
            case 10: 
            case 12: {
                return 2;
            }
            case 4: {
                return 4;
            }
        }
        return 0;
    }

    public int getEndOffset() {
        switch (this.type) {
            case 12: 
            case 13: 
            case 18: 
            case 25: 
            case 34: {
                return -1;
            }
            case 6: {
                return -2;
            }
            case 4: {
                return -3;
            }
            case 8: {
                if (this.cdataEndRead) {
                    return -3;
                }
                return 0;
            }
        }
        return 0;
    }

    public void clearBuffer() {
        if (this.position <= 0) {
            this.position = 0;
        } else {
            this.buffer[0] = this.buffer[this.position - 1];
            this.position = 1;
        }
    }

    public int next() throws XMLException {
        return this.next(this.context);
    }

    public int next(int ctx) throws XMLException {
        this.start = this.position - 1;
        try {
            switch (ctx) {
                case 0: {
                    this.type = this.nextInDocumentStart();
                    break;
                }
                case 1: {
                    this.type = this.nextInTopLevel();
                    break;
                }
                case 2: {
                    this.type = this.nextInPI();
                    break;
                }
                case 5: {
                    this.type = this.nextInStartTag();
                    break;
                }
                case 10: {
                    this.type = this.nextInAttributeValue();
                    break;
                }
                case 6: {
                    this.type = this.nextInContent();
                    break;
                }
                case 9: {
                    this.type = this.nextInEndTag();
                    break;
                }
                case 8: {
                    this.type = this.nextInCDATASection();
                    break;
                }
                case 3: {
                    this.type = this.nextInXMLDecl();
                    break;
                }
                case 4: {
                    this.type = this.nextInDoctype();
                    break;
                }
                case 7: {
                    this.type = this.nextInDTDDeclarations();
                    break;
                }
                case 12: {
                    this.type = this.nextInElementDeclaration();
                    break;
                }
                case 11: {
                    this.type = this.nextInAttList();
                    break;
                }
                case 14: {
                    this.type = this.nextInNotation();
                    break;
                }
                case 13: {
                    this.type = this.nextInEntity();
                    break;
                }
                case 17: {
                    return this.nextInEntityValue();
                }
                case 15: {
                    return this.nextInNotationType();
                }
                case 16: {
                    return this.nextInEnumeration();
                }
                default: {
                    throw new IllegalArgumentException("unexpected ctx:" + ctx);
                }
            }
        }
        catch (IOException e) {
            throw new XMLException(e);
        }
        this.end = this.position - (this.current == -1 ? 0 : 1);
        return this.type;
    }

    protected int nextInDocumentStart() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                this.context = this.depth == 0 ? 1 : 6;
                return 1;
            }
            case 60: {
                switch (this.nextChar()) {
                    case 63: {
                        int c1 = this.nextChar();
                        if (c1 == -1 || !XMLUtilities.isXMLNameFirstCharacter((char)c1)) {
                            throw this.createXMLException("invalid.pi.target");
                        }
                        this.context = 2;
                        int c2 = this.nextChar();
                        if (c2 == -1 || !XMLUtilities.isXMLNameCharacter((char)c2)) {
                            return 5;
                        }
                        int c3 = this.nextChar();
                        if (c3 == -1 || !XMLUtilities.isXMLNameCharacter((char)c3)) {
                            return 5;
                        }
                        int c4 = this.nextChar();
                        if (c4 != -1 && XMLUtilities.isXMLNameCharacter((char)c4)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                            return 5;
                        }
                        if (c1 == 120 && c2 == 109 && c3 == 108) {
                            this.context = 3;
                            return 2;
                        }
                        if (!(c1 != 120 && c1 != 88 || c2 != 109 && c2 != 77 || c3 != 108 && c3 != 76)) {
                            throw this.createXMLException("xml.reserved");
                        }
                        return 5;
                    }
                    case 33: {
                        switch (this.nextChar()) {
                            case 45: {
                                return this.readComment();
                            }
                            case 68: {
                                this.context = 4;
                                return this.readIdentifier("OCTYPE", 3, -1);
                            }
                        }
                        throw this.createXMLException("invalid.doctype");
                    }
                }
                this.context = 5;
                ++this.depth;
                return this.readName(9);
            }
            case -1: {
                return 0;
            }
        }
        if (this.depth == 0) {
            throw this.createXMLException("invalid.character");
        }
        return this.nextInContent();
    }

    protected int nextInTopLevel() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 60: {
                switch (this.nextChar()) {
                    case 63: {
                        this.context = 2;
                        return this.readPIStart();
                    }
                    case 33: {
                        switch (this.nextChar()) {
                            case 45: {
                                return this.readComment();
                            }
                            case 68: {
                                this.context = 4;
                                return this.readIdentifier("OCTYPE", 3, -1);
                            }
                        }
                        throw this.createXMLException("invalid.character");
                    }
                }
                this.context = 5;
                ++this.depth;
                return this.readName(9);
            }
            case -1: {
                return 0;
            }
        }
        throw this.createXMLException("invalid.character");
    }

    protected int nextInPI() throws IOException, XMLException {
        if (this.piEndRead) {
            this.piEndRead = false;
            this.context = this.depth == 0 ? 1 : 6;
            return 7;
        }
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 63: {
                if (this.nextChar() != 62) {
                    throw this.createXMLException("pi.end.expected");
                }
                this.nextChar();
                this.context = this.inDTD ? 7 : (this.depth == 0 ? 1 : 6);
                return 7;
            }
        }
        while (true) {
            this.nextChar();
            if (this.current != -1 && this.current != 63) continue;
            this.nextChar();
            if (this.current == -1 || this.current == 62) break;
        }
        this.nextChar();
        this.piEndRead = true;
        return 6;
    }

    protected int nextInStartTag() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 47: {
                if (this.nextChar() != 62) {
                    throw this.createXMLException("malformed.tag.end");
                }
                this.nextChar();
                this.context = --this.depth == 0 ? 1 : 6;
                return 19;
            }
            case 62: {
                this.nextChar();
                this.context = 6;
                return 20;
            }
            case 61: {
                this.nextChar();
                return 15;
            }
            case 34: {
                this.attrDelimiter = (char)34;
                this.nextChar();
                while (true) {
                    switch (this.current) {
                        case 34: {
                            this.nextChar();
                            return 25;
                        }
                        case 38: {
                            this.context = 10;
                            return 16;
                        }
                        case 60: {
                            throw this.createXMLException("invalid.character");
                        }
                        case -1: {
                            throw this.createXMLException("unexpected.eof");
                        }
                    }
                    this.nextChar();
                }
            }
            case 39: {
                this.attrDelimiter = (char)39;
                this.nextChar();
                while (true) {
                    switch (this.current) {
                        case 39: {
                            this.nextChar();
                            return 25;
                        }
                        case 38: {
                            this.context = 10;
                            return 16;
                        }
                        case 60: {
                            throw this.createXMLException("invalid.character");
                        }
                        case -1: {
                            throw this.createXMLException("unexpected.eof");
                        }
                    }
                    this.nextChar();
                }
            }
        }
        return this.readName(14);
    }

    protected int nextInAttributeValue() throws IOException, XMLException {
        if (this.current == -1) {
            return 0;
        }
        if (this.current == 38) {
            return this.readReference();
        }
        block10: while (true) {
            switch (this.current) {
                case -1: 
                case 38: 
                case 60: {
                    break block10;
                }
                case 34: 
                case 39: {
                    if (this.current == this.attrDelimiter) break block10;
                }
                default: {
                    this.nextChar();
                    continue block10;
                }
            }
            break;
        }
        switch (this.current) {
            case -1: {
                break;
            }
            case 60: {
                throw this.createXMLException("invalid.character");
            }
            case 38: {
                return 17;
            }
            case 34: 
            case 39: {
                this.nextChar();
                this.context = this.inDTD ? 11 : 5;
            }
        }
        return 18;
    }

    protected int nextInContent() throws IOException, XMLException {
        switch (this.current) {
            case -1: {
                return 0;
            }
            case 38: {
                return this.readReference();
            }
            case 60: {
                switch (this.nextChar()) {
                    case 63: {
                        this.context = 2;
                        return this.readPIStart();
                    }
                    case 33: {
                        switch (this.nextChar()) {
                            case 45: {
                                return this.readComment();
                            }
                            case 91: {
                                this.context = 8;
                                return this.readIdentifier("CDATA[", 11, -1);
                            }
                        }
                        throw this.createXMLException("invalid.character");
                    }
                    case 47: {
                        this.nextChar();
                        this.context = 9;
                        return this.readName(10);
                    }
                }
                ++this.depth;
                this.context = 5;
                return this.readName(9);
            }
        }
        block17: while (true) {
            switch (this.current) {
                default: {
                    this.nextChar();
                    continue block17;
                }
                case -1: 
                case 38: 
                case 60: 
            }
            break;
        }
        return 8;
    }

    protected int nextInEndTag() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 62: {
                if (--this.depth < 0) {
                    throw this.createXMLException("unexpected.end.tag");
                }
                this.context = this.depth == 0 ? 1 : 6;
                this.nextChar();
                return 20;
            }
        }
        throw this.createXMLException("invalid.character");
    }

    protected int nextInCDATASection() throws IOException, XMLException {
        if (this.cdataEndRead) {
            this.cdataEndRead = false;
            this.context = 6;
            return 21;
        }
        while (this.current != -1) {
            while (this.current != 93 && this.current != -1) {
                this.nextChar();
            }
            if (this.current == -1) continue;
            this.nextChar();
            if (this.current != 93) continue;
            this.nextChar();
            if (this.current != 62) continue;
        }
        if (this.current == -1) {
            throw this.createXMLException("unexpected.eof");
        }
        this.nextChar();
        this.cdataEndRead = true;
        return 8;
    }

    protected int nextInXMLDecl() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 118: {
                return this.readIdentifier("ersion", 22, -1);
            }
            case 101: {
                return this.readIdentifier("ncoding", 23, -1);
            }
            case 115: {
                return this.readIdentifier("tandalone", 24, -1);
            }
            case 61: {
                this.nextChar();
                return 15;
            }
            case 63: {
                this.nextChar();
                if (this.current != 62) {
                    throw this.createXMLException("pi.end.expected");
                }
                this.nextChar();
                this.context = 1;
                return 7;
            }
            case 34: {
                this.attrDelimiter = (char)34;
                return this.readString();
            }
            case 39: {
                this.attrDelimiter = (char)39;
                return this.readString();
            }
        }
        throw this.createXMLException("invalid.character");
    }

    protected int nextInDoctype() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 62: {
                this.nextChar();
                this.context = 1;
                return 20;
            }
            case 83: {
                return this.readIdentifier("YSTEM", 26, 14);
            }
            case 80: {
                return this.readIdentifier("UBLIC", 27, 14);
            }
            case 34: {
                this.attrDelimiter = (char)34;
                return this.readString();
            }
            case 39: {
                this.attrDelimiter = (char)39;
                return this.readString();
            }
            case 91: {
                this.nextChar();
                this.context = 7;
                this.inDTD = true;
                return 28;
            }
        }
        return this.readName(14);
    }

    protected int nextInDTDDeclarations() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 93: {
                this.nextChar();
                this.context = 4;
                this.inDTD = false;
                return 29;
            }
            case 37: {
                return this.readPEReference();
            }
            case 60: {
                switch (this.nextChar()) {
                    case 63: {
                        this.context = 2;
                        return this.readPIStart();
                    }
                    case 33: {
                        switch (this.nextChar()) {
                            case 45: {
                                return this.readComment();
                            }
                            case 69: {
                                switch (this.nextChar()) {
                                    case 76: {
                                        this.context = 12;
                                        return this.readIdentifier("EMENT", 30, -1);
                                    }
                                    case 78: {
                                        this.context = 13;
                                        return this.readIdentifier("TITY", 32, -1);
                                    }
                                }
                                throw this.createXMLException("invalid.character");
                            }
                            case 65: {
                                this.context = 11;
                                return this.readIdentifier("TTLIST", 31, -1);
                            }
                            case 78: {
                                this.context = 14;
                                return this.readIdentifier("OTATION", 33, -1);
                            }
                        }
                        throw this.createXMLException("invalid.character");
                    }
                }
                throw this.createXMLException("invalid.character");
            }
        }
        throw this.createXMLException("invalid.character");
    }

    protected int readString() throws IOException, XMLException {
        do {
            this.nextChar();
        } while (this.current != -1 && this.current != this.attrDelimiter);
        if (this.current == -1) {
            throw this.createXMLException("unexpected.eof");
        }
        this.nextChar();
        return 25;
    }

    protected int readComment() throws IOException, XMLException {
        if (this.nextChar() != 45) {
            throw this.createXMLException("malformed.comment");
        }
        int c = this.nextChar();
        while (c != -1) {
            while (c != -1 && c != 45) {
                c = this.nextChar();
            }
            c = this.nextChar();
            if (c != 45) continue;
        }
        if (c == -1) {
            throw this.createXMLException("unexpected.eof");
        }
        c = this.nextChar();
        if (c != 62) {
            throw this.createXMLException("malformed.comment");
        }
        this.nextChar();
        return 4;
    }

    protected int readIdentifier(String s, int type, int ntype) throws IOException, XMLException {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            this.nextChar();
            if (this.current == s.charAt(i)) continue;
            if (ntype == -1) {
                throw this.createXMLException("invalid.character");
            }
            while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current)) {
                this.nextChar();
            }
            return ntype;
        }
        this.nextChar();
        return type;
    }

    protected int readName(int type) throws IOException, XMLException {
        if (this.current == -1) {
            throw this.createXMLException("unexpected.eof");
        }
        if (!XMLUtilities.isXMLNameFirstCharacter((char)this.current)) {
            throw this.createXMLException("invalid.name");
        }
        do {
            this.nextChar();
        } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
        return type;
    }

    protected int readPIStart() throws IOException, XMLException {
        int c1 = this.nextChar();
        if (c1 == -1) {
            throw this.createXMLException("unexpected.eof");
        }
        if (!XMLUtilities.isXMLNameFirstCharacter((char)this.current)) {
            throw this.createXMLException("malformed.pi.target");
        }
        int c2 = this.nextChar();
        if (c2 == -1 || !XMLUtilities.isXMLNameCharacter((char)c2)) {
            return 5;
        }
        int c3 = this.nextChar();
        if (c3 == -1 || !XMLUtilities.isXMLNameCharacter((char)c3)) {
            return 5;
        }
        int c4 = this.nextChar();
        if (c4 != -1 && XMLUtilities.isXMLNameCharacter((char)c4)) {
            do {
                this.nextChar();
            } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
            return 5;
        }
        if (!(c1 != 120 && c1 != 88 || c2 != 109 && c2 != 77 || c3 != 108 && c3 != 76)) {
            throw this.createXMLException("xml.reserved");
        }
        return 5;
    }

    protected int nextInElementDeclaration() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 62: {
                this.nextChar();
                this.context = 7;
                return 20;
            }
            case 37: {
                this.nextChar();
                int t = this.readName(34);
                if (this.current != 59) {
                    throw this.createXMLException("malformed.parameter.entity");
                }
                this.nextChar();
                return t;
            }
            case 69: {
                return this.readIdentifier("MPTY", 35, 14);
            }
            case 65: {
                return this.readIdentifier("NY", 36, 14);
            }
            case 63: {
                this.nextChar();
                return 37;
            }
            case 43: {
                this.nextChar();
                return 38;
            }
            case 42: {
                this.nextChar();
                return 39;
            }
            case 40: {
                this.nextChar();
                return 40;
            }
            case 41: {
                this.nextChar();
                return 41;
            }
            case 124: {
                this.nextChar();
                return 42;
            }
            case 44: {
                this.nextChar();
                return 43;
            }
            case 35: {
                return this.readIdentifier("PCDATA", 44, -1);
            }
        }
        return this.readName(14);
    }

    protected int nextInAttList() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 62: {
                this.nextChar();
                this.context = 7;
                this.type = 20;
                return 20;
            }
            case 37: {
                int t = this.readName(34);
                if (this.current != 59) {
                    throw this.createXMLException("malformed.parameter.entity");
                }
                this.nextChar();
                return t;
            }
            case 67: {
                return this.readIdentifier("DATA", 45, 14);
            }
            case 73: {
                this.nextChar();
                if (this.current != 68) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 46;
                }
                if (this.current != 82) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 69) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 70) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 47;
                }
                if (this.current != 83) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 48;
                }
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                this.type = 14;
                return 14;
            }
            case 78: {
                switch (this.nextChar()) {
                    default: {
                        do {
                            this.nextChar();
                        } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                        return 14;
                    }
                    case 79: {
                        this.context = 15;
                        return this.readIdentifier("TATION", 57, 14);
                    }
                    case 77: 
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 84) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 79) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 75) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 69) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 78) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 49;
                }
                if (this.current != 83) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 50;
                }
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                return 14;
            }
            case 69: {
                this.nextChar();
                if (this.current != 78) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 84) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 73) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                if (this.current != 84) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                    this.type = 14;
                    return 14;
                }
                this.nextChar();
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                switch (this.current) {
                    case 89: {
                        this.nextChar();
                        if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                            return 51;
                        }
                        do {
                            this.nextChar();
                        } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                        return 14;
                    }
                    case 73: {
                        this.nextChar();
                        if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                            return 14;
                        }
                        if (this.current != 69) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                            return 14;
                        }
                        this.nextChar();
                        if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                            return 14;
                        }
                        if (this.current != 83) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                            return 14;
                        }
                        return 52;
                    }
                }
                if (this.current == -1 || !XMLUtilities.isXMLNameCharacter((char)this.current)) {
                    return 14;
                }
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
                return 14;
            }
            case 34: {
                this.attrDelimiter = (char)34;
                this.nextChar();
                if (this.current == -1) {
                    throw this.createXMLException("unexpected.eof");
                }
                if (this.current != 34 && this.current != 38) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && this.current != 34 && this.current != 38);
                }
                switch (this.current) {
                    case 38: {
                        this.context = 10;
                        return 16;
                    }
                    case 34: {
                        this.nextChar();
                        return 25;
                    }
                }
                throw this.createXMLException("invalid.character");
            }
            case 39: {
                this.attrDelimiter = (char)39;
                this.nextChar();
                if (this.current == -1) {
                    throw this.createXMLException("unexpected.eof");
                }
                if (this.current != 39 && this.current != 38) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && this.current != 39 && this.current != 38);
                }
                switch (this.current) {
                    case 38: {
                        this.context = 10;
                        return 16;
                    }
                    case 39: {
                        this.nextChar();
                        return 25;
                    }
                }
                throw this.createXMLException("invalid.character");
            }
            case 35: {
                switch (this.nextChar()) {
                    case 82: {
                        return this.readIdentifier("EQUIRED", 53, -1);
                    }
                    case 73: {
                        return this.readIdentifier("MPLIED", 54, -1);
                    }
                    case 70: {
                        return this.readIdentifier("IXED", 55, -1);
                    }
                }
                throw this.createXMLException("invalid.character");
            }
            case 40: {
                this.nextChar();
                this.context = 16;
                return 40;
            }
        }
        return this.readName(14);
    }

    protected int nextInNotation() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 62: {
                this.nextChar();
                this.context = 7;
                return 20;
            }
            case 37: {
                int t = this.readName(34);
                if (this.current != 59) {
                    throw this.createXMLException("malformed.parameter.entity");
                }
                this.nextChar();
                return t;
            }
            case 83: {
                return this.readIdentifier("YSTEM", 26, 14);
            }
            case 80: {
                return this.readIdentifier("UBLIC", 27, 14);
            }
            case 34: {
                this.attrDelimiter = (char)34;
                return this.readString();
            }
            case 39: {
                this.attrDelimiter = (char)39;
                return this.readString();
            }
        }
        return this.readName(14);
    }

    protected int nextInEntity() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 62: {
                this.nextChar();
                this.context = 7;
                return 20;
            }
            case 37: {
                this.nextChar();
                return 58;
            }
            case 83: {
                return this.readIdentifier("YSTEM", 26, 14);
            }
            case 80: {
                return this.readIdentifier("UBLIC", 27, 14);
            }
            case 78: {
                return this.readIdentifier("DATA", 59, 14);
            }
            case 34: {
                this.attrDelimiter = (char)34;
                this.nextChar();
                if (this.current == -1) {
                    throw this.createXMLException("unexpected.eof");
                }
                if (this.current != 34 && this.current != 38 && this.current != 37) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && this.current != 34 && this.current != 38 && this.current != 37);
                }
                switch (this.current) {
                    default: {
                        throw this.createXMLException("invalid.character");
                    }
                    case 37: 
                    case 38: {
                        this.context = 17;
                        break;
                    }
                    case 34: {
                        this.nextChar();
                        return 25;
                    }
                }
                return 16;
            }
            case 39: {
                this.attrDelimiter = (char)39;
                this.nextChar();
                if (this.current == -1) {
                    throw this.createXMLException("unexpected.eof");
                }
                if (this.current != 39 && this.current != 38 && this.current != 37) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && this.current != 39 && this.current != 38 && this.current != 37);
                }
                switch (this.current) {
                    default: {
                        throw this.createXMLException("invalid.character");
                    }
                    case 37: 
                    case 38: {
                        this.context = 17;
                        break;
                    }
                    case 39: {
                        this.nextChar();
                        return 25;
                    }
                }
                return 16;
            }
        }
        return this.readName(14);
    }

    protected int nextInEntityValue() throws IOException, XMLException {
        switch (this.current) {
            case 38: {
                return this.readReference();
            }
            case 37: {
                int t = this.nextChar();
                this.readName(34);
                if (this.current != 59) {
                    throw this.createXMLException("invalid.parameter.entity");
                }
                this.nextChar();
                return t;
            }
        }
        while (this.current != -1 && this.current != this.attrDelimiter && this.current != 38 && this.current != 37) {
            this.nextChar();
        }
        switch (this.current) {
            case -1: {
                throw this.createXMLException("unexpected.eof");
            }
            case 34: 
            case 39: {
                this.nextChar();
                this.context = 13;
                return 25;
            }
        }
        return 16;
    }

    protected int nextInNotationType() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 124: {
                this.nextChar();
                return 42;
            }
            case 40: {
                this.nextChar();
                return 40;
            }
            case 41: {
                this.nextChar();
                this.context = 11;
                return 41;
            }
        }
        return this.readName(14);
    }

    protected int nextInEnumeration() throws IOException, XMLException {
        switch (this.current) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                do {
                    this.nextChar();
                } while (this.current != -1 && XMLUtilities.isXMLSpace((char)this.current));
                return 1;
            }
            case 124: {
                this.nextChar();
                return 42;
            }
            case 41: {
                this.nextChar();
                this.context = 11;
                return 41;
            }
        }
        return this.readNmtoken();
    }

    protected int readReference() throws IOException, XMLException {
        this.nextChar();
        if (this.current == 35) {
            this.nextChar();
            int i = 0;
            switch (this.current) {
                case 120: {
                    do {
                        ++i;
                        this.nextChar();
                    } while (this.current >= 48 && this.current <= 57 || this.current >= 97 && this.current <= 102 || this.current >= 65 && this.current <= 70);
                    break;
                }
                default: {
                    do {
                        ++i;
                        this.nextChar();
                    } while (this.current >= 48 && this.current <= 57);
                    break;
                }
                case -1: {
                    throw this.createXMLException("unexpected.eof");
                }
            }
            if (i == 1 || this.current != 59) {
                throw this.createXMLException("character.reference");
            }
            this.nextChar();
            return 12;
        }
        int t = this.readName(13);
        if (this.current != 59) {
            throw this.createXMLException("character.reference");
        }
        this.nextChar();
        return t;
    }

    protected int readPEReference() throws IOException, XMLException {
        this.nextChar();
        if (this.current == -1) {
            throw this.createXMLException("unexpected.eof");
        }
        if (!XMLUtilities.isXMLNameFirstCharacter((char)this.current)) {
            throw this.createXMLException("invalid.parameter.entity");
        }
        do {
            this.nextChar();
        } while (this.current != -1 && XMLUtilities.isXMLNameCharacter((char)this.current));
        if (this.current != 59) {
            throw this.createXMLException("invalid.parameter.entity");
        }
        this.nextChar();
        return 34;
    }

    protected int readNmtoken() throws IOException, XMLException {
        if (this.current == -1) {
            throw this.createXMLException("unexpected.eof");
        }
        while (XMLUtilities.isXMLNameCharacter((char)this.current)) {
            this.nextChar();
        }
        return 56;
    }

    protected int nextChar() throws IOException {
        this.current = this.reader.read();
        if (this.current == -1) {
            return this.current;
        }
        if (this.position == this.buffer.length) {
            char[] t = new char[1 + this.position + this.position / 2];
            System.arraycopy(this.buffer, 0, t, 0, this.position);
            this.buffer = t;
        }
        char c = (char)this.current;
        this.buffer[this.position++] = c;
        return c;
    }

    protected XMLException createXMLException(String message) {
        String m;
        try {
            m = this.formatMessage(message, new Object[]{this.reader.getLine(), this.reader.getColumn()});
        }
        catch (MissingResourceException e) {
            m = message;
        }
        return new XMLException(m);
    }
}


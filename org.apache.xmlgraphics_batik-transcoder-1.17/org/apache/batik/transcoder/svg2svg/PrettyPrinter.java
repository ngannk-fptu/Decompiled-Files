/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.xml.XMLException
 *  org.apache.batik.xml.XMLScanner
 */
package org.apache.batik.transcoder.svg2svg;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.svg2svg.OutputManager;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.batik.xml.XMLException;
import org.apache.batik.xml.XMLScanner;

public class PrettyPrinter {
    public static final int DOCTYPE_CHANGE = 0;
    public static final int DOCTYPE_REMOVE = 1;
    public static final int DOCTYPE_KEEP_UNCHANGED = 2;
    protected XMLScanner scanner;
    protected OutputManager output;
    protected Writer writer;
    protected ErrorHandler errorHandler = SVGTranscoder.DEFAULT_ERROR_HANDLER;
    protected String newline = "\n";
    protected boolean format = true;
    protected int tabulationWidth = 4;
    protected int documentWidth = 80;
    protected int doctypeOption = 2;
    protected String publicId;
    protected String systemId;
    protected String xmlDeclaration;
    protected int type;

    public void setXMLDeclaration(String s) {
        this.xmlDeclaration = s;
    }

    public void setDoctypeOption(int i) {
        this.doctypeOption = i;
    }

    public void setPublicId(String s) {
        this.publicId = s;
    }

    public void setSystemId(String s) {
        this.systemId = s;
    }

    public void setNewline(String s) {
        this.newline = s;
    }

    public String getNewline() {
        return this.newline;
    }

    public void setFormat(boolean b) {
        this.format = b;
    }

    public boolean getFormat() {
        return this.format;
    }

    public void setTabulationWidth(int i) {
        this.tabulationWidth = Math.max(i, 0);
    }

    public int getTabulationWidth() {
        return this.tabulationWidth;
    }

    public void setDocumentWidth(int i) {
        this.documentWidth = Math.max(i, 0);
    }

    public int getDocumentWidth() {
        return this.documentWidth;
    }

    public void print(Reader r, Writer w) throws TranscoderException, IOException {
        try {
            this.scanner = new XMLScanner(r);
            this.output = new OutputManager(this, w);
            this.writer = w;
            this.type = this.scanner.next();
            this.printXMLDecl();
            block17: while (true) {
                switch (this.type) {
                    case 1: {
                        this.output.printTopSpaces(this.getCurrentValue());
                        this.scanner.clearBuffer();
                        this.type = this.scanner.next();
                        continue block17;
                    }
                    case 4: {
                        this.output.printComment(this.getCurrentValue());
                        this.scanner.clearBuffer();
                        this.type = this.scanner.next();
                        continue block17;
                    }
                    case 5: {
                        this.printPI();
                        continue block17;
                    }
                }
                break;
            }
            this.printDoctype();
            block18: while (true) {
                this.scanner.clearBuffer();
                switch (this.type) {
                    case 1: {
                        this.output.printTopSpaces(this.getCurrentValue());
                        this.scanner.clearBuffer();
                        this.type = this.scanner.next();
                        continue block18;
                    }
                    case 4: {
                        this.output.printComment(this.getCurrentValue());
                        this.scanner.clearBuffer();
                        this.type = this.scanner.next();
                        continue block18;
                    }
                    case 5: {
                        this.printPI();
                        continue block18;
                    }
                }
                break;
            }
            if (this.type != 9) {
                throw this.fatalError("element", null);
            }
            this.printElement();
            block19: while (true) {
                switch (this.type) {
                    case 1: {
                        this.output.printTopSpaces(this.getCurrentValue());
                        this.scanner.clearBuffer();
                        this.type = this.scanner.next();
                        continue block19;
                    }
                    case 4: {
                        this.output.printComment(this.getCurrentValue());
                        this.scanner.clearBuffer();
                        this.type = this.scanner.next();
                        continue block19;
                    }
                    case 5: {
                        this.printPI();
                        continue block19;
                    }
                }
                break;
            }
        }
        catch (XMLException e) {
            this.errorHandler.fatalError(new TranscoderException(e.getMessage()));
        }
    }

    protected void printXMLDecl() throws TranscoderException, XMLException, IOException {
        if (this.xmlDeclaration == null) {
            if (this.type == 2) {
                if (this.scanner.next() != 1) {
                    throw this.fatalError("space", null);
                }
                char[] space1 = this.getCurrentValue();
                if (this.scanner.next() != 22) {
                    throw this.fatalError("token", new Object[]{"version"});
                }
                this.type = this.scanner.next();
                char[] space2 = null;
                if (this.type == 1) {
                    space2 = this.getCurrentValue();
                    this.type = this.scanner.next();
                }
                if (this.type != 15) {
                    throw this.fatalError("token", new Object[]{"="});
                }
                this.type = this.scanner.next();
                char[] space3 = null;
                if (this.type == 1) {
                    space3 = this.getCurrentValue();
                    this.type = this.scanner.next();
                }
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                char[] version = this.getCurrentValue();
                char versionDelim = this.scanner.getStringDelimiter();
                char[] space4 = null;
                char[] space5 = null;
                char[] space6 = null;
                char[] encoding = null;
                char encodingDelim = '\u0000';
                char[] space7 = null;
                char[] space8 = null;
                char[] space9 = null;
                char[] standalone = null;
                char standaloneDelim = '\u0000';
                char[] space10 = null;
                this.type = this.scanner.next();
                if (this.type == 1) {
                    space4 = this.getCurrentValue();
                    this.type = this.scanner.next();
                    if (this.type == 23) {
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space5 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 15) {
                            throw this.fatalError("token", new Object[]{"="});
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space6 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 25) {
                            throw this.fatalError("string", null);
                        }
                        encoding = this.getCurrentValue();
                        encodingDelim = this.scanner.getStringDelimiter();
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space7 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                    }
                    if (this.type == 24) {
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space8 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 15) {
                            throw this.fatalError("token", new Object[]{"="});
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space9 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                        if (this.type != 25) {
                            throw this.fatalError("string", null);
                        }
                        standalone = this.getCurrentValue();
                        standaloneDelim = this.scanner.getStringDelimiter();
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            space10 = this.getCurrentValue();
                            this.type = this.scanner.next();
                        }
                    }
                }
                if (this.type != 7) {
                    throw this.fatalError("pi.end", null);
                }
                this.output.printXMLDecl(space1, space2, space3, version, versionDelim, space4, space5, space6, encoding, encodingDelim, space7, space8, space9, standalone, standaloneDelim, space10);
                this.type = this.scanner.next();
            }
        } else {
            this.output.printString(this.xmlDeclaration);
            this.output.printNewline();
            if (this.type == 2) {
                if (this.scanner.next() != 1) {
                    throw this.fatalError("space", null);
                }
                if (this.scanner.next() != 22) {
                    throw this.fatalError("token", new Object[]{"version"});
                }
                this.type = this.scanner.next();
                if (this.type == 1) {
                    this.type = this.scanner.next();
                }
                if (this.type != 15) {
                    throw this.fatalError("token", new Object[]{"="});
                }
                this.type = this.scanner.next();
                if (this.type == 1) {
                    this.type = this.scanner.next();
                }
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                this.type = this.scanner.next();
                if (this.type == 1) {
                    this.type = this.scanner.next();
                    if (this.type == 23) {
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                        if (this.type != 15) {
                            throw this.fatalError("token", new Object[]{"="});
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                        if (this.type != 25) {
                            throw this.fatalError("string", null);
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                    }
                    if (this.type == 24) {
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                        if (this.type != 15) {
                            throw this.fatalError("token", new Object[]{"="});
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                        if (this.type != 25) {
                            throw this.fatalError("string", null);
                        }
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.type = this.scanner.next();
                        }
                    }
                }
                if (this.type != 7) {
                    throw this.fatalError("pi.end", null);
                }
                this.type = this.scanner.next();
            }
        }
    }

    protected void printPI() throws TranscoderException, XMLException, IOException {
        char[] target = this.getCurrentValue();
        this.type = this.scanner.next();
        char[] space = new char[]{};
        if (this.type == 1) {
            space = this.getCurrentValue();
            this.type = this.scanner.next();
        }
        if (this.type != 6) {
            throw this.fatalError("pi.data", null);
        }
        char[] data = this.getCurrentValue();
        this.type = this.scanner.next();
        if (this.type != 7) {
            throw this.fatalError("pi.end", null);
        }
        this.output.printPI(target, space, data);
        this.type = this.scanner.next();
    }

    protected void printDoctype() throws TranscoderException, XMLException, IOException {
        switch (this.doctypeOption) {
            default: {
                if (this.type == 3) {
                    this.type = this.scanner.next();
                    if (this.type != 1) {
                        throw this.fatalError("space", null);
                    }
                    char[] space1 = this.getCurrentValue();
                    this.type = this.scanner.next();
                    if (this.type != 14) {
                        throw this.fatalError("name", null);
                    }
                    char[] root = this.getCurrentValue();
                    char[] space2 = null;
                    String externalId = null;
                    char[] space3 = null;
                    char[] string1 = null;
                    char string1Delim = '\u0000';
                    char[] space4 = null;
                    char[] string2 = null;
                    int string2Delim = 0;
                    char[] space5 = null;
                    this.type = this.scanner.next();
                    if (this.type == 1) {
                        space2 = this.getCurrentValue();
                        this.type = this.scanner.next();
                        switch (this.type) {
                            case 27: {
                                externalId = "PUBLIC";
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                space3 = this.getCurrentValue();
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                string1 = this.getCurrentValue();
                                string1Delim = this.scanner.getStringDelimiter();
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                space4 = this.getCurrentValue();
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                string2 = this.getCurrentValue();
                                string2Delim = this.scanner.getStringDelimiter();
                                this.type = this.scanner.next();
                                if (this.type != 1) break;
                                space5 = this.getCurrentValue();
                                this.type = this.scanner.next();
                                break;
                            }
                            case 26: {
                                externalId = "SYSTEM";
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                space3 = this.getCurrentValue();
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                string1 = this.getCurrentValue();
                                string1Delim = this.scanner.getStringDelimiter();
                                this.type = this.scanner.next();
                                if (this.type != 1) break;
                                space4 = this.getCurrentValue();
                                this.type = this.scanner.next();
                            }
                        }
                    }
                    if (this.doctypeOption == 0) {
                        if (this.publicId != null) {
                            externalId = "PUBLIC";
                            string1 = this.publicId.toCharArray();
                            string1Delim = '\"';
                            if (this.systemId != null) {
                                string2 = this.systemId.toCharArray();
                                string2Delim = 34;
                            }
                        } else if (this.systemId != null) {
                            externalId = "SYSTEM";
                            string1 = this.systemId.toCharArray();
                            string1Delim = '\"';
                            string2 = null;
                        }
                    }
                    this.output.printDoctypeStart(space1, root, space2, externalId, space3, string1, string1Delim, space4, string2, (char)string2Delim, space5);
                    if (this.type == 28) {
                        this.output.printCharacter('[');
                        this.type = this.scanner.next();
                        block22: while (true) {
                            switch (this.type) {
                                case 1: {
                                    this.output.printSpaces(this.getCurrentValue(), true);
                                    this.scanner.clearBuffer();
                                    this.type = this.scanner.next();
                                    continue block22;
                                }
                                case 4: {
                                    this.output.printComment(this.getCurrentValue());
                                    this.scanner.clearBuffer();
                                    this.type = this.scanner.next();
                                    continue block22;
                                }
                                case 5: {
                                    this.printPI();
                                    continue block22;
                                }
                                case 34: {
                                    this.output.printParameterEntityReference(this.getCurrentValue());
                                    this.scanner.clearBuffer();
                                    this.type = this.scanner.next();
                                    continue block22;
                                }
                                case 30: {
                                    this.scanner.clearBuffer();
                                    this.printElementDeclaration();
                                    continue block22;
                                }
                                case 31: {
                                    this.scanner.clearBuffer();
                                    this.printAttlist();
                                    continue block22;
                                }
                                case 33: {
                                    this.scanner.clearBuffer();
                                    this.printNotation();
                                    continue block22;
                                }
                                case 32: {
                                    this.scanner.clearBuffer();
                                    this.printEntityDeclaration();
                                    continue block22;
                                }
                                case 29: {
                                    this.output.printCharacter(']');
                                    this.scanner.clearBuffer();
                                    this.type = this.scanner.next();
                                    break block22;
                                }
                                default: {
                                    throw this.fatalError("xml", null);
                                }
                            }
                            break;
                        }
                    }
                    char[] endSpace = null;
                    if (this.type == 1) {
                        endSpace = this.getCurrentValue();
                        this.type = this.scanner.next();
                    }
                    if (this.type != 20) {
                        throw this.fatalError("end", null);
                    }
                    this.type = this.scanner.next();
                    this.output.printDoctypeEnd(endSpace);
                    break;
                }
                if (this.doctypeOption != 0) break;
                String externalId = "PUBLIC";
                char[] string1 = "-//W3C//DTD SVG 1.0//EN".toCharArray();
                char[] string2 = "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd".toCharArray();
                if (this.publicId != null) {
                    string1 = this.publicId.toCharArray();
                    if (this.systemId != null) {
                        string2 = this.systemId.toCharArray();
                    }
                } else if (this.systemId != null) {
                    externalId = "SYSTEM";
                    string1 = this.systemId.toCharArray();
                    string2 = null;
                }
                this.output.printDoctypeStart(new char[]{' '}, new char[]{'s', 'v', 'g'}, new char[]{' '}, externalId, new char[]{' '}, string1, '\"', new char[]{' '}, string2, '\"', null);
                this.output.printDoctypeEnd(null);
                break;
            }
            case 1: {
                if (this.type == 3) {
                    this.type = this.scanner.next();
                    if (this.type != 1) {
                        throw this.fatalError("space", null);
                    }
                    this.type = this.scanner.next();
                    if (this.type != 14) {
                        throw this.fatalError("name", null);
                    }
                    this.type = this.scanner.next();
                    if (this.type == 1) {
                        this.type = this.scanner.next();
                        switch (this.type) {
                            case 27: {
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 1) break;
                                this.type = this.scanner.next();
                                break;
                            }
                            case 26: {
                                this.type = this.scanner.next();
                                if (this.type != 1) {
                                    throw this.fatalError("space", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 25) {
                                    throw this.fatalError("string", null);
                                }
                                this.type = this.scanner.next();
                                if (this.type != 1) break;
                                this.type = this.scanner.next();
                            }
                        }
                    }
                    if (this.type == 28) {
                        do {
                            this.type = this.scanner.next();
                        } while (this.type != 29);
                    }
                    if (this.type == 1) {
                        this.type = this.scanner.next();
                    }
                    if (this.type != 20) {
                        throw this.fatalError("end", null);
                    }
                }
                this.type = this.scanner.next();
            }
        }
    }

    protected String printElement() throws TranscoderException, XMLException, IOException {
        char[] name = this.getCurrentValue();
        String nameStr = new String(name);
        LinkedList<OutputManager.AttributeInfo> attributes = new LinkedList<OutputManager.AttributeInfo>();
        char[] space = null;
        this.type = this.scanner.next();
        while (this.type == 1) {
            space = this.getCurrentValue();
            this.type = this.scanner.next();
            if (this.type != 14) continue;
            char[] attName = this.getCurrentValue();
            char[] space1 = null;
            this.type = this.scanner.next();
            if (this.type == 1) {
                space1 = this.getCurrentValue();
                this.type = this.scanner.next();
            }
            if (this.type != 15) {
                throw this.fatalError("token", new Object[]{"="});
            }
            this.type = this.scanner.next();
            char[] space2 = null;
            if (this.type == 1) {
                space2 = this.getCurrentValue();
                this.type = this.scanner.next();
            }
            if (this.type != 25 && this.type != 16) {
                throw this.fatalError("string", null);
            }
            char valueDelim = this.scanner.getStringDelimiter();
            boolean hasEntityRef = false;
            StringBuffer sb = new StringBuffer();
            sb.append(this.getCurrentValue());
            block10: while (true) {
                this.scanner.clearBuffer();
                this.type = this.scanner.next();
                switch (this.type) {
                    case 16: 
                    case 17: 
                    case 18: 
                    case 25: {
                        sb.append(this.getCurrentValue());
                        continue block10;
                    }
                    case 12: {
                        hasEntityRef = true;
                        sb.append("&#");
                        sb.append(this.getCurrentValue());
                        sb.append(";");
                        continue block10;
                    }
                    case 13: {
                        hasEntityRef = true;
                        sb.append("&");
                        sb.append(this.getCurrentValue());
                        sb.append(";");
                        continue block10;
                    }
                }
                break;
            }
            attributes.add(new OutputManager.AttributeInfo(space, attName, space1, space2, new String(sb), valueDelim, hasEntityRef));
            space = null;
        }
        this.output.printElementStart(name, attributes, space);
        switch (this.type) {
            default: {
                throw this.fatalError("xml", null);
            }
            case 19: {
                this.output.printElementEnd(null, null);
                break;
            }
            case 20: {
                this.output.printCharacter('>');
                this.type = this.scanner.next();
                this.printContent(this.allowSpaceAtStart(nameStr));
                if (this.type != 10) {
                    throw this.fatalError("end.tag", null);
                }
                name = this.getCurrentValue();
                this.type = this.scanner.next();
                space = null;
                if (this.type == 1) {
                    space = this.getCurrentValue();
                    this.type = this.scanner.next();
                }
                this.output.printElementEnd(name, space);
                if (this.type == 20) break;
                throw this.fatalError("end", null);
            }
        }
        this.type = this.scanner.next();
        return nameStr;
    }

    boolean allowSpaceAtStart(String tagName) {
        return true;
    }

    protected void printContent(boolean spaceAtStart) throws TranscoderException, XMLException, IOException {
        boolean preceedingSpace = false;
        block9: while (true) {
            switch (this.type) {
                case 4: {
                    this.output.printComment(this.getCurrentValue());
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    preceedingSpace = false;
                    continue block9;
                }
                case 5: {
                    this.printPI();
                    preceedingSpace = false;
                    continue block9;
                }
                case 8: {
                    preceedingSpace = this.output.printCharacterData(this.getCurrentValue(), spaceAtStart, preceedingSpace);
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    spaceAtStart = false;
                    continue block9;
                }
                case 11: {
                    this.type = this.scanner.next();
                    if (this.type != 8) {
                        throw this.fatalError("character.data", null);
                    }
                    this.output.printCDATASection(this.getCurrentValue());
                    if (this.scanner.next() != 21) {
                        throw this.fatalError("section.end", null);
                    }
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    preceedingSpace = false;
                    spaceAtStart = false;
                    continue block9;
                }
                case 9: {
                    String name = this.printElement();
                    spaceAtStart = this.allowSpaceAtStart(name);
                    continue block9;
                }
                case 12: {
                    this.output.printCharacterEntityReference(this.getCurrentValue(), spaceAtStart, preceedingSpace);
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    spaceAtStart = false;
                    preceedingSpace = false;
                    continue block9;
                }
                case 13: {
                    this.output.printEntityReference(this.getCurrentValue(), spaceAtStart);
                    this.scanner.clearBuffer();
                    this.type = this.scanner.next();
                    spaceAtStart = false;
                    preceedingSpace = false;
                    continue block9;
                }
            }
            break;
        }
    }

    protected void printNotation() throws TranscoderException, XMLException, IOException {
        int t = this.scanner.next();
        if (t != 1) {
            throw this.fatalError("space", null);
        }
        char[] space1 = this.getCurrentValue();
        t = this.scanner.next();
        if (t != 14) {
            throw this.fatalError("name", null);
        }
        char[] name = this.getCurrentValue();
        t = this.scanner.next();
        if (t != 1) {
            throw this.fatalError("space", null);
        }
        char[] space2 = this.getCurrentValue();
        t = this.scanner.next();
        String externalId = null;
        char[] space3 = null;
        char[] string1 = null;
        char string1Delim = '\u0000';
        char[] space4 = null;
        char[] string2 = null;
        char string2Delim = '\u0000';
        switch (t) {
            default: {
                throw this.fatalError("notation.definition", null);
            }
            case 27: {
                externalId = "PUBLIC";
                t = this.scanner.next();
                if (t != 1) {
                    throw this.fatalError("space", null);
                }
                space3 = this.getCurrentValue();
                t = this.scanner.next();
                if (t != 25) {
                    throw this.fatalError("string", null);
                }
                string1 = this.getCurrentValue();
                string1Delim = this.scanner.getStringDelimiter();
                t = this.scanner.next();
                if (t != 1) break;
                space4 = this.getCurrentValue();
                t = this.scanner.next();
                if (t != 25) break;
                string2 = this.getCurrentValue();
                string2Delim = this.scanner.getStringDelimiter();
                t = this.scanner.next();
                break;
            }
            case 26: {
                externalId = "SYSTEM";
                t = this.scanner.next();
                if (t != 1) {
                    throw this.fatalError("space", null);
                }
                space3 = this.getCurrentValue();
                t = this.scanner.next();
                if (t != 25) {
                    throw this.fatalError("string", null);
                }
                string1 = this.getCurrentValue();
                string1Delim = this.scanner.getStringDelimiter();
                t = this.scanner.next();
            }
        }
        char[] space5 = null;
        if (t == 1) {
            space5 = this.getCurrentValue();
            t = this.scanner.next();
        }
        if (t != 20) {
            throw this.fatalError("end", null);
        }
        this.output.printNotation(space1, name, space2, externalId, space3, string1, string1Delim, space4, string2, string2Delim, space5);
        this.scanner.next();
    }

    protected void printAttlist() throws TranscoderException, XMLException, IOException {
        this.type = this.scanner.next();
        if (this.type != 1) {
            throw this.fatalError("space", null);
        }
        char[] space = this.getCurrentValue();
        this.type = this.scanner.next();
        if (this.type != 14) {
            throw this.fatalError("name", null);
        }
        char[] name = this.getCurrentValue();
        this.type = this.scanner.next();
        this.output.printAttlistStart(space, name);
        while (this.type == 1) {
            space = this.getCurrentValue();
            this.type = this.scanner.next();
            if (this.type != 14) break;
            name = this.getCurrentValue();
            this.type = this.scanner.next();
            if (this.type != 1) {
                throw this.fatalError("space", null);
            }
            char[] space2 = this.getCurrentValue();
            this.type = this.scanner.next();
            this.output.printAttName(space, name, space2);
            switch (this.type) {
                case 45: 
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: {
                    this.output.printCharacters(this.getCurrentValue());
                    this.type = this.scanner.next();
                    break;
                }
                case 57: {
                    this.output.printCharacters(this.getCurrentValue());
                    this.type = this.scanner.next();
                    if (this.type != 1) {
                        throw this.fatalError("space", null);
                    }
                    this.output.printSpaces(this.getCurrentValue(), false);
                    this.type = this.scanner.next();
                    if (this.type != 40) {
                        throw this.fatalError("left.brace", null);
                    }
                    this.type = this.scanner.next();
                    LinkedList<OutputManager.NameInfo> names = new LinkedList();
                    space = null;
                    if (this.type == 1) {
                        space = this.getCurrentValue();
                        this.type = this.scanner.next();
                    }
                    if (this.type != 14) {
                        throw this.fatalError("name", null);
                    }
                    name = this.getCurrentValue();
                    this.type = this.scanner.next();
                    space2 = null;
                    if (this.type == 1) {
                        space2 = this.getCurrentValue();
                        this.type = this.scanner.next();
                    }
                    names.add(new OutputManager.NameInfo(space, name, space2));
                    block22: while (true) {
                        switch (this.type) {
                            default: {
                                break block22;
                            }
                            case 42: {
                                this.type = this.scanner.next();
                                space = null;
                                if (this.type == 1) {
                                    space = this.getCurrentValue();
                                    this.type = this.scanner.next();
                                }
                                if (this.type != 14) {
                                    throw this.fatalError("name", null);
                                }
                                name = this.getCurrentValue();
                                this.type = this.scanner.next();
                                space2 = null;
                                if (this.type == 1) {
                                    space2 = this.getCurrentValue();
                                    this.type = this.scanner.next();
                                }
                                names.add(new OutputManager.NameInfo(space, name, space2));
                                continue block22;
                            }
                        }
                        break;
                    }
                    if (this.type != 41) {
                        throw this.fatalError("right.brace", null);
                    }
                    this.output.printEnumeration(names);
                    this.type = this.scanner.next();
                    break;
                }
                case 40: {
                    this.type = this.scanner.next();
                    LinkedList<OutputManager.NameInfo> names = new LinkedList<OutputManager.NameInfo>();
                    space = null;
                    if (this.type == 1) {
                        space = this.getCurrentValue();
                        this.type = this.scanner.next();
                    }
                    if (this.type != 56) {
                        throw this.fatalError("nmtoken", null);
                    }
                    name = this.getCurrentValue();
                    this.type = this.scanner.next();
                    space2 = null;
                    if (this.type == 1) {
                        space2 = this.getCurrentValue();
                        this.type = this.scanner.next();
                    }
                    names.add(new OutputManager.NameInfo(space, name, space2));
                    block23: while (true) {
                        switch (this.type) {
                            default: {
                                break block23;
                            }
                            case 42: {
                                this.type = this.scanner.next();
                                space = null;
                                if (this.type == 1) {
                                    space = this.getCurrentValue();
                                    this.type = this.scanner.next();
                                }
                                if (this.type != 56) {
                                    throw this.fatalError("nmtoken", null);
                                }
                                name = this.getCurrentValue();
                                this.type = this.scanner.next();
                                space2 = null;
                                if (this.type == 1) {
                                    space2 = this.getCurrentValue();
                                    this.type = this.scanner.next();
                                }
                                names.add(new OutputManager.NameInfo(space, name, space2));
                                continue block23;
                            }
                        }
                        break;
                    }
                    if (this.type != 41) {
                        throw this.fatalError("right.brace", null);
                    }
                    this.output.printEnumeration(names);
                    this.type = this.scanner.next();
                }
            }
            if (this.type == 1) {
                this.output.printSpaces(this.getCurrentValue(), true);
                this.type = this.scanner.next();
            }
            switch (this.type) {
                default: {
                    throw this.fatalError("default.decl", null);
                }
                case 53: 
                case 54: {
                    this.output.printCharacters(this.getCurrentValue());
                    this.type = this.scanner.next();
                    break;
                }
                case 55: {
                    this.output.printCharacters(this.getCurrentValue());
                    this.type = this.scanner.next();
                    if (this.type != 1) {
                        throw this.fatalError("space", null);
                    }
                    this.output.printSpaces(this.getCurrentValue(), false);
                    this.type = this.scanner.next();
                    if (this.type != 25 && this.type != 16) {
                        throw this.fatalError("space", null);
                    }
                }
                case 16: 
                case 25: {
                    this.output.printCharacter(this.scanner.getStringDelimiter());
                    this.output.printCharacters(this.getCurrentValue());
                    block24: while (true) {
                        this.type = this.scanner.next();
                        switch (this.type) {
                            case 16: 
                            case 17: 
                            case 18: 
                            case 25: {
                                this.output.printCharacters(this.getCurrentValue());
                                continue block24;
                            }
                            case 12: {
                                this.output.printString("&#");
                                this.output.printCharacters(this.getCurrentValue());
                                this.output.printCharacter(';');
                                continue block24;
                            }
                            case 13: {
                                this.output.printCharacter('&');
                                this.output.printCharacters(this.getCurrentValue());
                                this.output.printCharacter(';');
                                continue block24;
                            }
                        }
                        break;
                    }
                    this.output.printCharacter(this.scanner.getStringDelimiter());
                }
            }
            space = null;
        }
        if (this.type != 20) {
            throw this.fatalError("end", null);
        }
        this.output.printAttlistEnd(space);
        this.type = this.scanner.next();
    }

    protected void printEntityDeclaration() throws TranscoderException, XMLException, IOException {
        this.writer.write("<!ENTITY");
        this.type = this.scanner.next();
        if (this.type != 1) {
            throw this.fatalError("space", null);
        }
        this.writer.write(this.getCurrentValue());
        this.type = this.scanner.next();
        boolean pe = false;
        switch (this.type) {
            default: {
                throw this.fatalError("xml", null);
            }
            case 14: {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
                break;
            }
            case 58: {
                pe = true;
                this.writer.write(37);
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
                if (this.type != 14) {
                    throw this.fatalError("name", null);
                }
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
        }
        if (this.type != 1) {
            throw this.fatalError("space", null);
        }
        this.writer.write(this.getCurrentValue());
        this.type = this.scanner.next();
        switch (this.type) {
            case 16: 
            case 25: {
                char sd = this.scanner.getStringDelimiter();
                this.writer.write(sd);
                block14: while (true) {
                    switch (this.type) {
                        case 16: 
                        case 17: 
                        case 18: 
                        case 25: {
                            this.writer.write(this.getCurrentValue());
                            break;
                        }
                        case 13: {
                            this.writer.write(38);
                            this.writer.write(this.getCurrentValue());
                            this.writer.write(59);
                            break;
                        }
                        case 34: {
                            this.writer.write(38);
                            this.writer.write(this.getCurrentValue());
                            this.writer.write(59);
                            break;
                        }
                        default: {
                            break block14;
                        }
                    }
                    this.type = this.scanner.next();
                }
                this.writer.write(sd);
                if (this.type == 1) {
                    this.writer.write(this.getCurrentValue());
                    this.type = this.scanner.next();
                }
                if (this.type != 20) {
                    throw this.fatalError("end", null);
                }
                this.writer.write(">");
                this.type = this.scanner.next();
                return;
            }
            case 27: {
                this.writer.write("PUBLIC");
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.type = this.scanner.next();
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                this.writer.write(" \"");
                this.writer.write(this.getCurrentValue());
                this.writer.write("\" \"");
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.type = this.scanner.next();
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                this.writer.write(this.getCurrentValue());
                this.writer.write(34);
                break;
            }
            case 26: {
                this.writer.write("SYSTEM");
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.type = this.scanner.next();
                if (this.type != 25) {
                    throw this.fatalError("string", null);
                }
                this.writer.write(" \"");
                this.writer.write(this.getCurrentValue());
                this.writer.write(34);
            }
        }
        this.type = this.scanner.next();
        if (this.type == 1) {
            this.writer.write(this.getCurrentValue());
            this.type = this.scanner.next();
            if (!pe && this.type == 59) {
                this.writer.write("NDATA");
                this.type = this.scanner.next();
                if (this.type != 1) {
                    throw this.fatalError("space", null);
                }
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
                if (this.type != 14) {
                    throw this.fatalError("name", null);
                }
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
            if (this.type == 1) {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
        }
        if (this.type != 20) {
            throw this.fatalError("end", null);
        }
        this.writer.write(62);
        this.type = this.scanner.next();
    }

    protected void printElementDeclaration() throws TranscoderException, XMLException, IOException {
        this.writer.write("<!ELEMENT");
        this.type = this.scanner.next();
        if (this.type != 1) {
            throw this.fatalError("space", null);
        }
        this.writer.write(this.getCurrentValue());
        this.type = this.scanner.next();
        switch (this.type) {
            default: {
                throw this.fatalError("name", null);
            }
            case 14: 
        }
        this.writer.write(this.getCurrentValue());
        this.type = this.scanner.next();
        if (this.type != 1) {
            throw this.fatalError("space", null);
        }
        this.writer.write(this.getCurrentValue());
        this.type = this.scanner.next();
        block3 : switch (this.type) {
            case 35: {
                this.writer.write("EMPTY");
                this.type = this.scanner.next();
                break;
            }
            case 36: {
                this.writer.write("ANY");
                this.type = this.scanner.next();
                break;
            }
            case 40: {
                this.writer.write(40);
                this.type = this.scanner.next();
                if (this.type == 1) {
                    this.writer.write(this.getCurrentValue());
                    this.type = this.scanner.next();
                }
                switch (this.type) {
                    case 44: {
                        this.writer.write("#PCDATA");
                        this.type = this.scanner.next();
                        while (true) {
                            switch (this.type) {
                                case 1: {
                                    this.writer.write(this.getCurrentValue());
                                    this.type = this.scanner.next();
                                    break;
                                }
                                case 42: {
                                    this.writer.write(124);
                                    this.type = this.scanner.next();
                                    if (this.type == 1) {
                                        this.writer.write(this.getCurrentValue());
                                        this.type = this.scanner.next();
                                    }
                                    if (this.type != 14) {
                                        throw this.fatalError("name", null);
                                    }
                                    this.writer.write(this.getCurrentValue());
                                    this.type = this.scanner.next();
                                    break;
                                }
                                case 41: {
                                    this.writer.write(41);
                                    this.type = this.scanner.next();
                                    break block3;
                                }
                            }
                        }
                    }
                    case 14: 
                    case 40: {
                        this.printChildren();
                        if (this.type != 41) {
                            throw this.fatalError("right.brace", null);
                        }
                        this.writer.write(41);
                        this.type = this.scanner.next();
                        if (this.type == 1) {
                            this.writer.write(this.getCurrentValue());
                            this.type = this.scanner.next();
                        }
                        switch (this.type) {
                            case 37: {
                                this.writer.write(63);
                                this.type = this.scanner.next();
                                break block3;
                            }
                            case 39: {
                                this.writer.write(42);
                                this.type = this.scanner.next();
                                break block3;
                            }
                            case 38: {
                                this.writer.write(43);
                                this.type = this.scanner.next();
                            }
                        }
                    }
                }
            }
        }
        if (this.type == 1) {
            this.writer.write(this.getCurrentValue());
            this.type = this.scanner.next();
        }
        if (this.type != 20) {
            throw this.fatalError("end", null);
        }
        this.writer.write(62);
        this.scanner.next();
    }

    /*
     * Enabled aggressive block sorting
     */
    protected void printChildren() throws TranscoderException, XMLException, IOException {
        int op = 0;
        while (true) {
            switch (this.type) {
                default: {
                    throw new RuntimeException("Invalid XML");
                }
                case 14: {
                    this.writer.write(this.getCurrentValue());
                    this.type = this.scanner.next();
                    break;
                }
                case 40: {
                    this.writer.write(40);
                    this.type = this.scanner.next();
                    if (this.type == 1) {
                        this.writer.write(this.getCurrentValue());
                        this.type = this.scanner.next();
                    }
                    this.printChildren();
                    if (this.type != 41) {
                        throw this.fatalError("right.brace", null);
                    }
                    this.writer.write(41);
                    this.type = this.scanner.next();
                }
            }
            if (this.type == 1) {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
            switch (this.type) {
                case 41: {
                    return;
                }
                case 39: {
                    this.writer.write(42);
                    this.type = this.scanner.next();
                    break;
                }
                case 37: {
                    this.writer.write(63);
                    this.type = this.scanner.next();
                    break;
                }
                case 38: {
                    this.writer.write(43);
                    this.type = this.scanner.next();
                }
            }
            if (this.type == 1) {
                this.writer.write(this.getCurrentValue());
                this.type = this.scanner.next();
            }
            switch (this.type) {
                case 42: {
                    if (op != 0 && op != this.type) {
                        throw new RuntimeException("Invalid XML");
                    }
                    this.writer.write(124);
                    op = this.type;
                    this.type = this.scanner.next();
                    break;
                }
                case 43: {
                    if (op != 0 && op != this.type) {
                        throw new RuntimeException("Invalid XML");
                    }
                    this.writer.write(44);
                    op = this.type;
                    this.type = this.scanner.next();
                    break;
                }
            }
            if (this.type != 1) continue;
            this.writer.write(this.getCurrentValue());
            this.type = this.scanner.next();
        }
    }

    protected char[] getCurrentValue() {
        int off = this.scanner.getStart() + this.scanner.getStartOffset();
        int len = this.scanner.getEnd() + this.scanner.getEndOffset() - off;
        char[] result = new char[len];
        char[] buffer = this.scanner.getBuffer();
        System.arraycopy(buffer, off, result, 0, len);
        return result;
    }

    protected TranscoderException fatalError(String key, Object[] params) throws TranscoderException {
        TranscoderException result = new TranscoderException(key);
        this.errorHandler.fatalError(result);
        return result;
    }
}


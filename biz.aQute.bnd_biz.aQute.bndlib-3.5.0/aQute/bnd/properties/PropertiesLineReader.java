/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.properties;

import aQute.bnd.properties.BadLocationException;
import aQute.bnd.properties.Document;
import aQute.bnd.properties.IDocument;
import aQute.bnd.properties.IRegion;
import aQute.bnd.properties.LineType;
import aQute.bnd.properties.Region;

public class PropertiesLineReader {
    private final IDocument document;
    private final int lineCount;
    private int lineNum = 0;
    private IRegion lastRegion = null;
    private String lastKey = null;
    private String lastValue = null;

    public PropertiesLineReader(IDocument document) {
        this.document = document;
        this.lineCount = document.getNumberOfLines();
    }

    public PropertiesLineReader(String data) {
        this(new Document(data));
    }

    public LineType next() throws Exception {
        int index = 0;
        char[] chars = null;
        StringBuilder keyData = new StringBuilder();
        StringBuilder valueData = new StringBuilder();
        StringBuilder currentBuffer = keyData;
        boolean started = false;
        while (true) {
            if (chars == null) {
                chars = this.grabLine(false);
            }
            if (chars == null) {
                return LineType.eof;
            }
            if (index >= chars.length) break;
            char c = chars[index];
            if (c == '\\') {
                if (++index == chars.length) {
                    chars = this.grabLine(true);
                    index = 0;
                    if (chars == null || chars.length == 0) break;
                }
                currentBuffer.append(chars[index]);
                ++index;
                continue;
            }
            if (c == '=' || c == ':') {
                currentBuffer = valueData;
            }
            if (!(started || c != '#' && c != '!')) {
                return LineType.comment;
            }
            if (Character.isWhitespace(c)) {
                if (started) {
                    currentBuffer = valueData;
                }
            } else {
                started = true;
                currentBuffer.append(c);
            }
            ++index;
        }
        if (!started) {
            return LineType.blank;
        }
        this.lastKey = keyData.toString();
        return LineType.entry;
    }

    private char[] grabLine(boolean continued) throws BadLocationException {
        if (this.lineNum >= this.lineCount) {
            this.lastRegion = null;
            return null;
        }
        IRegion lineInfo = this.document.getLineInformation(this.lineNum);
        char[] chars = this.document.get(lineInfo.getOffset(), lineInfo.getLength()).toCharArray();
        if (continued) {
            int length = this.lastRegion.getLength();
            length += this.document.getLineDelimiter(this.lineNum - 1).length();
            this.lastRegion = new Region(this.lastRegion.getOffset(), length += lineInfo.getLength());
        } else {
            this.lastRegion = lineInfo;
        }
        ++this.lineNum;
        return chars;
    }

    public IRegion region() {
        if (this.lastRegion == null) {
            throw new IllegalStateException("Last region not available: either before start or after end of document.");
        }
        return this.lastRegion;
    }

    public String key() {
        if (this.lastKey == null) {
            throw new IllegalStateException("Last key not available: either before state or after end of document, or last line type was not 'entry'.");
        }
        return this.lastKey;
    }

    public String value() {
        if (this.lastValue == null) {
            throw new IllegalStateException("Last value not available: either before state or after end of document, or last line type was not 'entry'.");
        }
        return this.lastValue;
    }
}


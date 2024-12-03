/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdfparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.BaseParser;
import org.apache.pdfbox.pdfparser.InputStreamSource;
import org.apache.pdfbox.pdfparser.RandomAccessSource;
import org.apache.pdfbox.pdfparser.SequentialSource;
import org.apache.pdfbox.pdmodel.common.PDStream;

public class PDFStreamParser
extends BaseParser {
    private static final Log LOG = LogFactory.getLog(PDFStreamParser.class);
    private final List<Object> streamObjects = new ArrayList<Object>(100);
    private static final int MAX_BIN_CHAR_TEST_LENGTH = 10;
    private final byte[] binCharTestArr = new byte[10];

    @Deprecated
    public PDFStreamParser(PDStream stream) throws IOException {
        super(new InputStreamSource(stream.createInputStream()));
    }

    @Deprecated
    public PDFStreamParser(COSStream stream) throws IOException {
        super(new InputStreamSource(stream.createInputStream()));
    }

    public PDFStreamParser(PDContentStream contentStream) throws IOException {
        super(new InputStreamSource(contentStream.getContents()));
    }

    public PDFStreamParser(byte[] bytes) {
        super(new RandomAccessSource(new RandomAccessBuffer(bytes)));
    }

    public void parse() throws IOException {
        Object token;
        while ((token = this.parseNextToken()) != null) {
            this.streamObjects.add(token);
        }
    }

    public List<Object> getTokens() {
        return this.streamObjects;
    }

    public Object parseNextToken() throws IOException {
        if (this.seqSource.isClosed()) {
            return null;
        }
        this.skipSpaces();
        if (this.seqSource.isEOF()) {
            this.close();
            return null;
        }
        char c = (char)this.seqSource.peek();
        switch (c) {
            case '<': {
                int leftBracket = this.seqSource.read();
                c = (char)this.seqSource.peek();
                this.seqSource.unread(leftBracket);
                if (c == '<') {
                    try {
                        return this.parseCOSDictionary();
                    }
                    catch (IOException exception) {
                        LOG.warn((Object)("Stop reading invalid dictionary from content stream at offset " + this.seqSource.getPosition()));
                        this.close();
                        return null;
                    }
                }
                return this.parseCOSString();
            }
            case '[': {
                try {
                    return this.parseCOSArray();
                }
                catch (IOException exception) {
                    LOG.warn((Object)("Stop reading invalid array from content stream at offset " + this.seqSource.getPosition()));
                    this.close();
                    return null;
                }
            }
            case '(': {
                return this.parseCOSString();
            }
            case '/': {
                return this.parseCOSName();
            }
            case 'n': {
                String nullString = this.readString();
                if (nullString.equals("null")) {
                    return COSNull.NULL;
                }
                return Operator.getOperator(nullString);
            }
            case 'f': 
            case 't': {
                String next = this.readString();
                if (next.equals("true")) {
                    return COSBoolean.TRUE;
                }
                if (next.equals("false")) {
                    return COSBoolean.FALSE;
                }
                return Operator.getOperator(next);
            }
            case '+': 
            case '-': 
            case '.': 
            case '0': 
            case '1': 
            case '2': 
            case '3': 
            case '4': 
            case '5': 
            case '6': 
            case '7': 
            case '8': 
            case '9': {
                boolean dotNotRead;
                StringBuilder buf = new StringBuilder();
                buf.append(c);
                this.seqSource.read();
                if (c == '-' && this.seqSource.peek() == c) {
                    this.seqSource.read();
                }
                boolean bl = dotNotRead = c != '.';
                while (Character.isDigit(c = (char)this.seqSource.peek()) || dotNotRead && c == '.' || c == '-') {
                    if (c != '-') {
                        buf.append(c);
                    }
                    this.seqSource.read();
                    if (!dotNotRead || c != '.') continue;
                    dotNotRead = false;
                }
                return COSNumber.get(buf.toString());
            }
            case 'B': {
                String nextOperator = this.readString();
                Operator beginImageOP = Operator.getOperator(nextOperator);
                if (nextOperator.equals("BI")) {
                    COSDictionary imageParams = new COSDictionary();
                    beginImageOP.setImageParameters(imageParams);
                    Object nextToken = null;
                    while ((nextToken = this.parseNextToken()) instanceof COSName) {
                        Object value = this.parseNextToken();
                        if (!(value instanceof COSBase)) {
                            LOG.warn((Object)("Unexpected token in inline image dictionary at offset " + (this.seqSource.isClosed() ? "EOF" : Long.valueOf(this.seqSource.getPosition()))));
                            break;
                        }
                        imageParams.setItem((COSName)nextToken, (COSBase)value);
                    }
                    if (nextToken instanceof Operator) {
                        Operator imageData = (Operator)nextToken;
                        if (imageData.getImageData() == null || imageData.getImageData().length == 0) {
                            LOG.warn((Object)("empty inline image at stream offset " + this.seqSource.getPosition()));
                        }
                        beginImageOP.setImageData(imageData.getImageData());
                    }
                }
                return beginImageOP;
            }
            case 'I': {
                String id = Character.toString((char)this.seqSource.read()) + (char)this.seqSource.read();
                if (!id.equals("ID")) {
                    long currentPosition = this.seqSource.getPosition();
                    this.close();
                    throw new IOException("Error: Expected operator 'ID' actual='" + id + "' at stream offset " + currentPosition);
                }
                ByteArrayOutputStream imageData = new ByteArrayOutputStream();
                if (this.isWhitespace()) {
                    this.seqSource.read();
                }
                int lastByte = this.seqSource.read();
                int currentByte = this.seqSource.read();
                while (!(lastByte == 69 && currentByte == 73 && this.hasNextSpaceOrReturn() && this.hasNoFollowingBinData(this.seqSource) || this.seqSource.isEOF())) {
                    imageData.write(lastByte);
                    lastByte = currentByte;
                    currentByte = this.seqSource.read();
                }
                Operator beginImageDataOP = Operator.getOperator("ID");
                beginImageDataOP.setImageData(imageData.toByteArray());
                return beginImageDataOP;
            }
            case ']': {
                this.seqSource.read();
                return COSNull.NULL;
            }
        }
        String operator = this.readOperator().trim();
        if (operator.length() > 0) {
            return Operator.getOperator(operator);
        }
        return null;
    }

    private boolean hasNoFollowingBinData(SequentialSource pdfSource) throws IOException {
        int readBytes = pdfSource.read(this.binCharTestArr, 0, 10);
        boolean noBinData = true;
        int startOpIdx = -1;
        int endOpIdx = -1;
        if (readBytes > 0) {
            String s;
            for (int bIdx = 0; bIdx < readBytes; ++bIdx) {
                byte b = this.binCharTestArr[bIdx];
                if (b != 0 && b < 9 || b > 10 && b < 32 && b != 13) {
                    noBinData = false;
                    break;
                }
                if (startOpIdx == -1 && b != 0 && b != 9 && b != 32 && b != 10 && b != 13) {
                    startOpIdx = bIdx;
                    continue;
                }
                if (startOpIdx == -1 || endOpIdx != -1 || b != 0 && b != 9 && b != 32 && b != 10 && b != 13) continue;
                endOpIdx = bIdx;
            }
            if (!(endOpIdx == -1 || startOpIdx == -1 || "Q".equals(s = new String(this.binCharTestArr, startOpIdx, endOpIdx - startOpIdx)) || "EMC".equals(s) || "S".equals(s))) {
                noBinData = false;
            }
            if (readBytes == 10) {
                if (startOpIdx != -1 && endOpIdx == -1) {
                    endOpIdx = 10;
                }
                if (endOpIdx != -1 && startOpIdx != -1 && endOpIdx - startOpIdx > 3) {
                    noBinData = false;
                }
            }
            pdfSource.unread(this.binCharTestArr, 0, readBytes);
        }
        if (!noBinData) {
            LOG.warn((Object)("ignoring 'EI' assumed to be in the middle of inline image at stream offset " + pdfSource.getPosition()));
        }
        return noBinData;
    }

    protected String readOperator() throws IOException {
        this.skipSpaces();
        StringBuilder buffer = new StringBuilder(4);
        int nextChar = this.seqSource.peek();
        while (!(nextChar == -1 || this.isWhitespace(nextChar) || this.isClosing(nextChar) || nextChar == 91 || nextChar == 60 || nextChar == 40 || nextChar == 47 || nextChar == 37 || nextChar >= 48 && nextChar <= 57)) {
            char currentChar = (char)this.seqSource.read();
            nextChar = this.seqSource.peek();
            buffer.append(currentChar);
            if (currentChar != 'd' || nextChar != 48 && nextChar != 49) continue;
            buffer.append((char)this.seqSource.read());
            nextChar = this.seqSource.peek();
        }
        return buffer.toString();
    }

    private boolean isSpaceOrReturn(int c) {
        return c == 10 || c == 13 || c == 32;
    }

    private boolean hasNextSpaceOrReturn() throws IOException {
        return this.isSpaceOrReturn(this.seqSource.peek());
    }

    public void close() throws IOException {
        if (this.seqSource != null) {
            this.seqSource.close();
        }
    }
}


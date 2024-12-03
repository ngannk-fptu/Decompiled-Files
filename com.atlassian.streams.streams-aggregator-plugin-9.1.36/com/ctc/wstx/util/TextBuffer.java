/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.dtd.DTDEventListener;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.StringUtil;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.CharArrayBase64Decoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.XMLValidator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public final class TextBuffer {
    static final int DEF_INITIAL_BUFFER_SIZE = 500;
    static final int MAX_SEGMENT_LENGTH = 262144;
    static final int INT_SPACE = 32;
    private final ReaderConfig mConfig;
    private char[] mInputBuffer;
    private int mInputStart;
    private int mInputLen;
    private boolean mHasSegments = false;
    private ArrayList mSegments;
    private int mSegmentSize;
    private char[] mCurrentSegment;
    private int mCurrentSize;
    private String mResultString;
    private char[] mResultArray;
    public static final int MAX_INDENT_SPACES = 32;
    public static final int MAX_INDENT_TABS = 8;
    private static final String sIndSpaces = "\n                                 ";
    private static final char[] sIndSpacesArray = "\n                                 ".toCharArray();
    private static final String[] sIndSpacesStrings = new String[sIndSpacesArray.length];
    private static final String sIndTabs = "\n\t\t\t\t\t\t\t\t\t";
    private static final char[] sIndTabsArray = "\n\t\t\t\t\t\t\t\t\t".toCharArray();
    private static final String[] sIndTabsStrings = new String[sIndTabsArray.length];

    private TextBuffer(ReaderConfig cfg) {
        this.mConfig = cfg;
    }

    public static TextBuffer createRecyclableBuffer(ReaderConfig cfg) {
        return new TextBuffer(cfg);
    }

    public static TextBuffer createTemporaryBuffer() {
        return new TextBuffer(null);
    }

    public void recycle(boolean force) {
        if (this.mConfig != null && this.mCurrentSegment != null) {
            if (force) {
                this.resetWithEmpty();
            } else {
                if (this.mInputStart < 0 && this.mSegmentSize + this.mCurrentSize > 0) {
                    return;
                }
                if (this.mSegments != null && this.mSegments.size() > 0) {
                    this.mSegments.clear();
                    this.mSegmentSize = 0;
                }
            }
            char[] buf = this.mCurrentSegment;
            this.mCurrentSegment = null;
            this.mConfig.freeMediumCBuffer(buf);
        }
    }

    public void resetWithEmpty() {
        this.mInputBuffer = null;
        this.mInputStart = -1;
        this.mInputLen = 0;
        this.mResultString = null;
        this.mResultArray = null;
        if (this.mHasSegments) {
            this.clearSegments();
        }
        this.mCurrentSize = 0;
    }

    public void resetWithEmptyString() {
        this.mInputBuffer = null;
        this.mInputStart = -1;
        this.mInputLen = 0;
        this.mResultString = "";
        this.mResultArray = null;
        if (this.mHasSegments) {
            this.clearSegments();
        }
        this.mCurrentSize = 0;
    }

    public void resetWithShared(char[] buf, int start, int len) {
        this.mInputBuffer = buf;
        this.mInputStart = start;
        this.mInputLen = len;
        this.mResultString = null;
        this.mResultArray = null;
        if (this.mHasSegments) {
            this.clearSegments();
        }
    }

    public void resetWithCopy(char[] buf, int start, int len) {
        this.mInputBuffer = null;
        this.mInputStart = -1;
        this.mInputLen = 0;
        this.mResultString = null;
        this.mResultArray = null;
        if (this.mHasSegments) {
            this.clearSegments();
        } else {
            if (this.mCurrentSegment == null) {
                this.mCurrentSegment = this.allocBuffer(len);
            }
            this.mSegmentSize = 0;
            this.mCurrentSize = 0;
        }
        this.append(buf, start, len);
    }

    public void resetInitialized() {
        this.resetWithEmpty();
        if (this.mCurrentSegment == null) {
            this.mCurrentSegment = this.allocBuffer(0);
        }
    }

    private final char[] allocBuffer(int needed) {
        int size = Math.max(needed, 500);
        char[] buf = null;
        if (this.mConfig != null && (buf = this.mConfig.allocMediumCBuffer(size)) != null) {
            return buf;
        }
        return new char[size];
    }

    private final void clearSegments() {
        this.mHasSegments = false;
        this.mSegments.clear();
        this.mSegmentSize = 0;
        this.mCurrentSize = 0;
    }

    public void resetWithIndentation(int indCharCount, char indChar) {
        String text;
        this.mInputStart = 0;
        this.mInputLen = indCharCount + 1;
        if (indChar == '\t') {
            this.mInputBuffer = sIndTabsArray;
            text = sIndTabsStrings[indCharCount];
            if (text == null) {
                TextBuffer.sIndTabsStrings[indCharCount] = text = sIndTabs.substring(0, this.mInputLen);
            }
        } else {
            this.mInputBuffer = sIndSpacesArray;
            text = sIndSpacesStrings[indCharCount];
            if (text == null) {
                TextBuffer.sIndSpacesStrings[indCharCount] = text = sIndSpaces.substring(0, this.mInputLen);
            }
        }
        this.mResultString = text;
        this.mResultArray = null;
        if (this.mSegments != null && this.mSegments.size() > 0) {
            this.mSegments.clear();
            this.mSegmentSize = 0;
            this.mCurrentSize = 0;
        }
    }

    public int size() {
        if (this.mInputStart >= 0) {
            return this.mInputLen;
        }
        return this.mSegmentSize + this.mCurrentSize;
    }

    public int getTextStart() {
        return this.mInputStart >= 0 ? this.mInputStart : 0;
    }

    public char[] getTextBuffer() {
        if (this.mInputStart >= 0) {
            return this.mInputBuffer;
        }
        if (this.mSegments == null || this.mSegments.size() == 0) {
            return this.mCurrentSegment;
        }
        return this.contentsAsArray();
    }

    public void decode(TypedValueDecoder tvd) throws IllegalArgumentException {
        int end;
        int start;
        char[] buf;
        if (this.mInputStart >= 0) {
            buf = this.mInputBuffer;
            start = this.mInputStart;
            end = start + this.mInputLen;
        } else {
            buf = this.getTextBuffer();
            start = 0;
            end = this.mSegmentSize + this.mCurrentSize;
        }
        while (true) {
            if (start >= end) {
                tvd.handleEmptyValue();
                return;
            }
            if (!StringUtil.isSpace(buf[start])) break;
            ++start;
        }
        while (--end > start && StringUtil.isSpace(buf[end])) {
        }
        tvd.decode(buf, start, end + 1);
    }

    public int decodeElements(TypedArrayDecoder tad, InputProblemReporter rep) throws TypedXMLStreamException {
        int count;
        block11: {
            count = 0;
            if (this.mInputStart < 0) {
                if (this.mHasSegments) {
                    this.mInputBuffer = this.buildResultArray();
                    this.mInputLen = this.mInputBuffer.length;
                    this.clearSegments();
                } else {
                    this.mInputBuffer = this.mCurrentSegment;
                    this.mInputLen = this.mCurrentSize;
                }
                this.mInputStart = 0;
            }
            int ptr = this.mInputStart;
            int end = ptr + this.mInputLen;
            char[] buf = this.mInputBuffer;
            int start = ptr;
            try {
                while (ptr < end) {
                    int tokenEnd;
                    while (buf[ptr] <= ' ') {
                        if (++ptr < end) continue;
                        break block11;
                    }
                    start = ptr++;
                    while (ptr < end && buf[ptr] > ' ') {
                        ++ptr;
                    }
                    ++count;
                    if (!tad.decodeValue(buf, start, tokenEnd = ptr++)) continue;
                    break;
                }
            }
            catch (IllegalArgumentException iae) {
                Location loc = rep.getLocation();
                String lexical = new String(buf, start, ptr - start - 1);
                throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
            }
            finally {
                this.mInputStart = ptr;
                this.mInputLen = end - ptr;
            }
        }
        return count;
    }

    public void initBinaryChunks(Base64Variant v, CharArrayBase64Decoder dec, boolean firstChunk) {
        if (this.mInputStart < 0) {
            dec.init(v, firstChunk, this.mCurrentSegment, 0, this.mCurrentSize, this.mSegments);
        } else {
            dec.init(v, firstChunk, this.mInputBuffer, this.mInputStart, this.mInputLen, null);
        }
    }

    public String contentsAsString() {
        if (this.mResultString == null) {
            if (this.mResultArray != null) {
                this.mResultString = new String(this.mResultArray);
            } else if (this.mInputStart >= 0) {
                if (this.mInputLen < 1) {
                    this.mResultString = "";
                    return "";
                }
                this.mResultString = new String(this.mInputBuffer, this.mInputStart, this.mInputLen);
            } else {
                int segLen = this.mSegmentSize;
                int currLen = this.mCurrentSize;
                if (segLen == 0) {
                    this.mResultString = currLen == 0 ? "" : new String(this.mCurrentSegment, 0, currLen);
                } else {
                    StringBuffer sb = new StringBuffer(segLen + currLen);
                    if (this.mSegments != null) {
                        int len = this.mSegments.size();
                        for (int i = 0; i < len; ++i) {
                            char[] curr = (char[])this.mSegments.get(i);
                            sb.append(curr, 0, curr.length);
                        }
                    }
                    sb.append(this.mCurrentSegment, 0, this.mCurrentSize);
                    this.mResultString = sb.toString();
                }
            }
        }
        return this.mResultString;
    }

    public StringBuffer contentsAsStringBuffer(int extraSpace) {
        if (this.mResultString != null) {
            return new StringBuffer(this.mResultString);
        }
        if (this.mResultArray != null) {
            StringBuffer sb = new StringBuffer(this.mResultArray.length + extraSpace);
            sb.append(this.mResultArray, 0, this.mResultArray.length);
            return sb;
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen < 1) {
                return new StringBuffer();
            }
            StringBuffer sb = new StringBuffer(this.mInputLen + extraSpace);
            sb.append(this.mInputBuffer, this.mInputStart, this.mInputLen);
            return sb;
        }
        int segLen = this.mSegmentSize;
        int currLen = this.mCurrentSize;
        StringBuffer sb = new StringBuffer(segLen + currLen + extraSpace);
        if (this.mSegments != null) {
            int len = this.mSegments.size();
            for (int i = 0; i < len; ++i) {
                char[] curr = (char[])this.mSegments.get(i);
                sb.append(curr, 0, curr.length);
            }
        }
        sb.append(this.mCurrentSegment, 0, currLen);
        return sb;
    }

    public void contentsToStringBuffer(StringBuffer sb) {
        if (this.mResultString != null) {
            sb.append(this.mResultString);
        } else if (this.mResultArray != null) {
            sb.append(this.mResultArray);
        } else if (this.mInputStart >= 0) {
            if (this.mInputLen > 0) {
                sb.append(this.mInputBuffer, this.mInputStart, this.mInputLen);
            }
        } else {
            if (this.mSegments != null) {
                int len = this.mSegments.size();
                for (int i = 0; i < len; ++i) {
                    char[] curr = (char[])this.mSegments.get(i);
                    sb.append(curr, 0, curr.length);
                }
            }
            sb.append(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }

    public char[] contentsAsArray() {
        char[] result = this.mResultArray;
        if (result == null) {
            this.mResultArray = result = this.buildResultArray();
        }
        return result;
    }

    public int contentsToArray(int srcStart, char[] dst, int dstStart, int len) {
        if (this.mInputStart >= 0) {
            int amount = this.mInputLen - srcStart;
            if (amount > len) {
                amount = len;
            } else if (amount < 0) {
                amount = 0;
            }
            if (amount > 0) {
                System.arraycopy(this.mInputBuffer, this.mInputStart + srcStart, dst, dstStart, amount);
            }
            return amount;
        }
        int totalAmount = 0;
        if (this.mSegments != null) {
            int segc = this.mSegments.size();
            for (int i = 0; i < segc; ++i) {
                char[] segment = (char[])this.mSegments.get(i);
                int segLen = segment.length;
                int amount = segLen - srcStart;
                if (amount < 1) {
                    srcStart -= segLen;
                    continue;
                }
                if (amount >= len) {
                    System.arraycopy(segment, srcStart, dst, dstStart, len);
                    return totalAmount + len;
                }
                System.arraycopy(segment, srcStart, dst, dstStart, amount);
                totalAmount += amount;
                dstStart += amount;
                len -= amount;
                srcStart = 0;
            }
        }
        if (len > 0) {
            int maxAmount = this.mCurrentSize - srcStart;
            if (len > maxAmount) {
                len = maxAmount;
            }
            if (len > 0) {
                System.arraycopy(this.mCurrentSegment, srcStart, dst, dstStart, len);
                totalAmount += len;
            }
        }
        return totalAmount;
    }

    public int rawContentsTo(Writer w) throws IOException {
        if (this.mResultArray != null) {
            w.write(this.mResultArray);
            return this.mResultArray.length;
        }
        if (this.mResultString != null) {
            w.write(this.mResultString);
            return this.mResultString.length();
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen > 0) {
                w.write(this.mInputBuffer, this.mInputStart, this.mInputLen);
            }
            return this.mInputLen;
        }
        int rlen = 0;
        if (this.mSegments != null) {
            int len = this.mSegments.size();
            for (int i = 0; i < len; ++i) {
                char[] ch = (char[])this.mSegments.get(i);
                w.write(ch);
                rlen += ch.length;
            }
        }
        if (this.mCurrentSize > 0) {
            w.write(this.mCurrentSegment, 0, this.mCurrentSize);
            rlen += this.mCurrentSize;
        }
        return rlen;
    }

    public Reader rawContentsViaReader() throws IOException {
        if (this.mResultArray != null) {
            return new CharArrayReader(this.mResultArray);
        }
        if (this.mResultString != null) {
            return new StringReader(this.mResultString);
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen > 0) {
                return new CharArrayReader(this.mInputBuffer, this.mInputStart, this.mInputLen);
            }
            return new StringReader("");
        }
        if (this.mSegments == null || this.mSegments.size() == 0) {
            return new CharArrayReader(this.mCurrentSegment, 0, this.mCurrentSize);
        }
        return new BufferReader(this.mSegments, this.mCurrentSegment, this.mCurrentSize);
    }

    public boolean isAllWhitespace() {
        if (this.mInputStart >= 0) {
            int i;
            char[] buf = this.mInputBuffer;
            int last = i + this.mInputLen;
            for (i = this.mInputStart; i < last; ++i) {
                if (buf[i] <= ' ') continue;
                return false;
            }
            return true;
        }
        if (this.mSegments != null) {
            int len = this.mSegments.size();
            for (int i = 0; i < len; ++i) {
                char[] buf = (char[])this.mSegments.get(i);
                int len2 = buf.length;
                for (int j = 0; j < len2; ++j) {
                    if (buf[j] <= ' ') continue;
                    return false;
                }
            }
        }
        char[] buf = this.mCurrentSegment;
        int len = this.mCurrentSize;
        for (int i = 0; i < len; ++i) {
            if (buf[i] <= ' ') continue;
            return false;
        }
        return true;
    }

    public boolean endsWith(String str) {
        if (this.mInputStart >= 0) {
            this.unshare(16);
        }
        int segIndex = this.mSegments == null ? 0 : this.mSegments.size();
        int inIndex = str.length() - 1;
        char[] buf = this.mCurrentSegment;
        int bufIndex = this.mCurrentSize - 1;
        while (inIndex >= 0) {
            if (str.charAt(inIndex) != buf[bufIndex]) {
                return false;
            }
            if (--inIndex == 0) break;
            if (--bufIndex >= 0) continue;
            if (--segIndex < 0) {
                return false;
            }
            buf = (char[])this.mSegments.get(segIndex);
            bufIndex = buf.length - 1;
        }
        return true;
    }

    public boolean equalsString(String str) {
        int expLen = str.length();
        if (this.mInputStart >= 0) {
            if (this.mInputLen != expLen) {
                return false;
            }
            for (int i = 0; i < expLen; ++i) {
                if (str.charAt(i) == this.mInputBuffer[this.mInputStart + i]) continue;
                return false;
            }
            return true;
        }
        if (expLen != this.size()) {
            return false;
        }
        char[] seg = this.mSegments == null || this.mSegments.size() == 0 ? this.mCurrentSegment : this.contentsAsArray();
        for (int i = 0; i < expLen; ++i) {
            if (seg[i] == str.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public void fireSaxCharacterEvents(ContentHandler h) throws SAXException {
        if (this.mResultArray != null) {
            h.characters(this.mResultArray, 0, this.mResultArray.length);
        } else if (this.mInputStart >= 0) {
            h.characters(this.mInputBuffer, this.mInputStart, this.mInputLen);
        } else {
            if (this.mSegments != null) {
                int len = this.mSegments.size();
                for (int i = 0; i < len; ++i) {
                    char[] ch = (char[])this.mSegments.get(i);
                    h.characters(ch, 0, ch.length);
                }
            }
            if (this.mCurrentSize > 0) {
                h.characters(this.mCurrentSegment, 0, this.mCurrentSize);
            }
        }
    }

    public void fireSaxSpaceEvents(ContentHandler h) throws SAXException {
        if (this.mResultArray != null) {
            h.ignorableWhitespace(this.mResultArray, 0, this.mResultArray.length);
        } else if (this.mInputStart >= 0) {
            h.ignorableWhitespace(this.mInputBuffer, this.mInputStart, this.mInputLen);
        } else {
            if (this.mSegments != null) {
                int len = this.mSegments.size();
                for (int i = 0; i < len; ++i) {
                    char[] ch = (char[])this.mSegments.get(i);
                    h.ignorableWhitespace(ch, 0, ch.length);
                }
            }
            if (this.mCurrentSize > 0) {
                h.ignorableWhitespace(this.mCurrentSegment, 0, this.mCurrentSize);
            }
        }
    }

    public void fireSaxCommentEvent(LexicalHandler h) throws SAXException {
        if (this.mResultArray != null) {
            h.comment(this.mResultArray, 0, this.mResultArray.length);
        } else if (this.mInputStart >= 0) {
            h.comment(this.mInputBuffer, this.mInputStart, this.mInputLen);
        } else if (this.mSegments != null && this.mSegments.size() > 0) {
            char[] ch = this.contentsAsArray();
            h.comment(ch, 0, ch.length);
        } else {
            h.comment(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }

    public void fireDtdCommentEvent(DTDEventListener l) {
        if (this.mResultArray != null) {
            l.dtdComment(this.mResultArray, 0, this.mResultArray.length);
        } else if (this.mInputStart >= 0) {
            l.dtdComment(this.mInputBuffer, this.mInputStart, this.mInputLen);
        } else if (this.mSegments != null && this.mSegments.size() > 0) {
            char[] ch = this.contentsAsArray();
            l.dtdComment(ch, 0, ch.length);
        } else {
            l.dtdComment(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }

    public void validateText(XMLValidator vld, boolean lastSegment) throws XMLStreamException {
        if (this.mInputStart >= 0) {
            vld.validateText(this.mInputBuffer, this.mInputStart, this.mInputStart + this.mInputLen, lastSegment);
        } else {
            vld.validateText(this.contentsAsString(), lastSegment);
        }
    }

    public void ensureNotShared() {
        if (this.mInputStart >= 0) {
            this.unshare(16);
        }
    }

    public void append(char c) {
        if (this.mInputStart >= 0) {
            this.unshare(16);
        }
        this.mResultString = null;
        this.mResultArray = null;
        char[] curr = this.mCurrentSegment;
        if (this.mCurrentSize >= curr.length) {
            this.expand(1);
            curr = this.mCurrentSegment;
        }
        curr[this.mCurrentSize++] = c;
    }

    public void append(char[] c, int start, int len) {
        if (this.mInputStart >= 0) {
            this.unshare(len);
        }
        this.mResultString = null;
        this.mResultArray = null;
        char[] curr = this.mCurrentSegment;
        int max = curr.length - this.mCurrentSize;
        if (max >= len) {
            System.arraycopy(c, start, curr, this.mCurrentSize, len);
            this.mCurrentSize += len;
        } else {
            if (max > 0) {
                System.arraycopy(c, start, curr, this.mCurrentSize, max);
                start += max;
                len -= max;
            }
            this.expand(len);
            System.arraycopy(c, start, this.mCurrentSegment, 0, len);
            this.mCurrentSize = len;
        }
    }

    public void append(String str) {
        int len = str.length();
        if (this.mInputStart >= 0) {
            this.unshare(len);
        }
        this.mResultString = null;
        this.mResultArray = null;
        char[] curr = this.mCurrentSegment;
        int max = curr.length - this.mCurrentSize;
        if (max >= len) {
            str.getChars(0, len, curr, this.mCurrentSize);
            this.mCurrentSize += len;
        } else {
            if (max > 0) {
                str.getChars(0, max, curr, this.mCurrentSize);
                len -= max;
            }
            this.expand(len);
            str.getChars(max, max + len, this.mCurrentSegment, 0);
            this.mCurrentSize = len;
        }
    }

    public char[] getCurrentSegment() {
        if (this.mInputStart >= 0) {
            this.unshare(1);
        } else {
            char[] curr = this.mCurrentSegment;
            if (curr == null) {
                this.mCurrentSegment = this.allocBuffer(0);
            } else if (this.mCurrentSize >= curr.length) {
                this.expand(1);
            }
        }
        return this.mCurrentSegment;
    }

    public int getCurrentSegmentSize() {
        return this.mCurrentSize;
    }

    public void setCurrentLength(int len) {
        this.mCurrentSize = len;
    }

    public char[] finishCurrentSegment() {
        if (this.mSegments == null) {
            this.mSegments = new ArrayList();
        }
        this.mHasSegments = true;
        this.mSegments.add(this.mCurrentSegment);
        int oldLen = this.mCurrentSegment.length;
        this.mSegmentSize += oldLen;
        char[] curr = new char[this.calcNewSize(oldLen)];
        this.mCurrentSize = 0;
        this.mCurrentSegment = curr;
        return curr;
    }

    private int calcNewSize(int latestSize) {
        int incr = latestSize < 8000 ? latestSize : latestSize >> 1;
        int size = latestSize + incr;
        return Math.min(size, 262144);
    }

    public String toString() {
        return this.contentsAsString();
    }

    public void unshare(int needExtra) {
        int len = this.mInputLen;
        this.mInputLen = 0;
        char[] inputBuf = this.mInputBuffer;
        this.mInputBuffer = null;
        int start = this.mInputStart;
        this.mInputStart = -1;
        int needed = len + needExtra;
        if (this.mCurrentSegment == null || needed > this.mCurrentSegment.length) {
            this.mCurrentSegment = this.allocBuffer(needed);
        }
        if (len > 0) {
            System.arraycopy(inputBuf, start, this.mCurrentSegment, 0, len);
        }
        this.mSegmentSize = 0;
        this.mCurrentSize = len;
    }

    private void expand(int roomNeeded) {
        if (this.mSegments == null) {
            this.mSegments = new ArrayList();
        }
        char[] curr = this.mCurrentSegment;
        this.mHasSegments = true;
        this.mSegments.add(curr);
        int oldLen = curr.length;
        this.mSegmentSize += oldLen;
        int newSize = Math.max(roomNeeded, this.calcNewSize(oldLen));
        curr = new char[newSize];
        this.mCurrentSize = 0;
        this.mCurrentSegment = curr;
    }

    private char[] buildResultArray() {
        char[] result;
        if (this.mResultString != null) {
            return this.mResultString.toCharArray();
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen < 1) {
                return DataUtil.getEmptyCharArray();
            }
            result = new char[this.mInputLen];
            System.arraycopy(this.mInputBuffer, this.mInputStart, result, 0, this.mInputLen);
        } else {
            int size = this.size();
            if (size < 1) {
                return DataUtil.getEmptyCharArray();
            }
            int offset = 0;
            result = new char[size];
            if (this.mSegments != null) {
                int len = this.mSegments.size();
                for (int i = 0; i < len; ++i) {
                    char[] curr = (char[])this.mSegments.get(i);
                    int currLen = curr.length;
                    System.arraycopy(curr, 0, result, offset, currLen);
                    offset += currLen;
                }
            }
            System.arraycopy(this.mCurrentSegment, 0, result, offset, this.mCurrentSize);
        }
        return result;
    }

    private static final class BufferReader
    extends Reader {
        ArrayList _Segments;
        char[] _CurrentSegment;
        final int _CurrentLength;
        int _SegmentIndex;
        int _SegmentOffset;
        int _CurrentOffset;

        public BufferReader(ArrayList segs, char[] currSeg, int currSegLen) {
            this._Segments = segs;
            this._CurrentSegment = currSeg;
            this._CurrentLength = currSegLen;
            this._SegmentIndex = 0;
            this._CurrentOffset = 0;
            this._SegmentOffset = 0;
        }

        public void close() {
            this._Segments = null;
            this._CurrentSegment = null;
        }

        public void mark(int x) throws IOException {
            throw new IOException("mark() not supported");
        }

        public boolean markSupported() {
            return false;
        }

        public int read(char[] cbuf, int offset, int len) {
            if (len < 1) {
                return 0;
            }
            int origOffset = offset;
            while (this._Segments != null) {
                char[] curr = (char[])this._Segments.get(this._SegmentIndex);
                int max = curr.length - this._SegmentOffset;
                if (len <= max) {
                    System.arraycopy(curr, this._SegmentOffset, cbuf, offset, len);
                    this._SegmentOffset += len;
                    return (offset += len) - origOffset;
                }
                if (max > 0) {
                    System.arraycopy(curr, this._SegmentOffset, cbuf, offset, max);
                    offset += max;
                }
                if (++this._SegmentIndex >= this._Segments.size()) {
                    this._Segments = null;
                    continue;
                }
                this._SegmentOffset = 0;
            }
            if (len > 0 && this._CurrentSegment != null) {
                int max = this._CurrentLength - this._CurrentOffset;
                if (len >= max) {
                    len = max;
                    System.arraycopy(this._CurrentSegment, this._CurrentOffset, cbuf, offset, len);
                    this._CurrentSegment = null;
                } else {
                    System.arraycopy(this._CurrentSegment, this._CurrentOffset, cbuf, offset, len);
                    this._CurrentOffset += len;
                }
                offset += len;
            }
            return origOffset == offset ? -1 : offset - origOffset;
        }

        public boolean ready() {
            return true;
        }

        public void reset() throws IOException {
            throw new IOException("reset() not supported");
        }

        public long skip(long amount) {
            if (amount < 0L) {
                return 0L;
            }
            long origAmount = amount;
            while (this._Segments != null) {
                char[] curr = (char[])this._Segments.get(this._SegmentIndex);
                int max = curr.length - this._SegmentOffset;
                if ((long)max >= amount) {
                    this._SegmentOffset += (int)amount;
                    return origAmount;
                }
                amount -= (long)max;
                if (++this._SegmentIndex >= this._Segments.size()) {
                    this._Segments = null;
                    continue;
                }
                this._SegmentOffset = 0;
            }
            if (amount > 0L && this._CurrentSegment != null) {
                int max = this._CurrentLength - this._CurrentOffset;
                if (amount >= (long)max) {
                    amount -= (long)max;
                    this._CurrentSegment = null;
                } else {
                    amount = 0L;
                    this._CurrentOffset += (int)amount;
                }
            }
            return amount == origAmount ? -1L : origAmount - amount;
        }
    }
}


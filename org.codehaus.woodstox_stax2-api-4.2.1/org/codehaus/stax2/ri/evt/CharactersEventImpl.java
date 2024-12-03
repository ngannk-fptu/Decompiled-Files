/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class CharactersEventImpl
extends BaseEventImpl
implements Characters {
    final String mContent;
    final boolean mIsCData;
    final boolean mIgnorableWS;
    boolean mWhitespaceChecked = false;
    boolean mIsWhitespace = false;

    public CharactersEventImpl(Location loc, String content, boolean cdata) {
        super(loc);
        this.mContent = content;
        this.mIsCData = cdata;
        this.mIgnorableWS = false;
    }

    private CharactersEventImpl(Location loc, String content, boolean cdata, boolean allWS, boolean ignorableWS) {
        super(loc);
        this.mContent = content;
        this.mIsCData = cdata;
        this.mIsWhitespace = allWS;
        if (allWS) {
            this.mWhitespaceChecked = true;
            this.mIgnorableWS = ignorableWS;
        } else {
            this.mWhitespaceChecked = false;
            this.mIgnorableWS = false;
        }
    }

    public static final CharactersEventImpl createIgnorableWS(Location loc, String content) {
        return new CharactersEventImpl(loc, content, false, true, true);
    }

    public static final CharactersEventImpl createNonIgnorableWS(Location loc, String content) {
        return new CharactersEventImpl(loc, content, false, true, false);
    }

    @Override
    public Characters asCharacters() {
        return this;
    }

    @Override
    public int getEventType() {
        return this.mIsCData ? 12 : 4;
    }

    @Override
    public boolean isCharacters() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            if (this.mIsCData) {
                w.write("<![CDATA[");
                w.write(this.mContent);
                w.write("]]>");
            } else {
                CharactersEventImpl.writeEscapedXMLText(w, this.mContent);
            }
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        if (this.mIsCData) {
            w.writeCData(this.mContent);
        } else {
            w.writeCharacters(this.mContent);
        }
    }

    @Override
    public String getData() {
        return this.mContent;
    }

    @Override
    public boolean isCData() {
        return this.mIsCData;
    }

    @Override
    public boolean isIgnorableWhiteSpace() {
        return this.mIgnorableWS;
    }

    @Override
    public boolean isWhiteSpace() {
        if (!this.mWhitespaceChecked) {
            int i;
            this.mWhitespaceChecked = true;
            String str = this.mContent;
            int len = str.length();
            for (i = 0; i < len && str.charAt(i) <= ' '; ++i) {
            }
            this.mIsWhitespace = i == len;
        }
        return this.mIsWhitespace;
    }

    public void setWhitespaceStatus(boolean status) {
        this.mWhitespaceChecked = true;
        this.mIsWhitespace = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Characters)) {
            return false;
        }
        Characters other = (Characters)o;
        if (this.mContent.equals(other.getData())) {
            return this.isCData() == other.isCData();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.mContent.hashCode();
    }

    protected static void writeEscapedXMLText(Writer w, String text) throws IOException {
        int len = text.length();
        for (int i = 0; i < len; ++i) {
            int start = i;
            char c = '\u0000';
            while (i < len && (c = text.charAt(i)) != '<' && c != '&' && (c != '>' || i < 2 || text.charAt(i - 1) != ']' || text.charAt(i - 2) != ']')) {
                ++i;
            }
            int outLen = i - start;
            if (outLen > 0) {
                w.write(text, start, outLen);
            }
            if (i >= len) continue;
            if (c == '<') {
                w.write("&lt;");
                continue;
            }
            if (c == '&') {
                w.write("&amp;");
                continue;
            }
            if (c != '>') continue;
            w.write("&gt;");
        }
    }
}


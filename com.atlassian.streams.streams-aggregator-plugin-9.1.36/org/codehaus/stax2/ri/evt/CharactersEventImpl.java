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

    public CharactersEventImpl(Location location, String string, boolean bl) {
        super(location);
        this.mContent = string;
        this.mIsCData = bl;
        this.mIgnorableWS = false;
    }

    private CharactersEventImpl(Location location, String string, boolean bl, boolean bl2, boolean bl3) {
        super(location);
        this.mContent = string;
        this.mIsCData = bl;
        this.mIsWhitespace = bl2;
        if (bl2) {
            this.mWhitespaceChecked = true;
            this.mIgnorableWS = bl3;
        } else {
            this.mWhitespaceChecked = false;
            this.mIgnorableWS = false;
        }
    }

    public static final CharactersEventImpl createIgnorableWS(Location location, String string) {
        return new CharactersEventImpl(location, string, false, true, true);
    }

    public static final CharactersEventImpl createNonIgnorableWS(Location location, String string) {
        return new CharactersEventImpl(location, string, false, true, false);
    }

    public Characters asCharacters() {
        return this;
    }

    public int getEventType() {
        return this.mIsCData ? 12 : 4;
    }

    public boolean isCharacters() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            if (this.mIsCData) {
                writer.write("<![CDATA[");
                writer.write(this.mContent);
                writer.write("]]>");
            } else {
                CharactersEventImpl.writeEscapedXMLText(writer, this.mContent);
            }
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        if (this.mIsCData) {
            xMLStreamWriter2.writeCData(this.mContent);
        } else {
            xMLStreamWriter2.writeCharacters(this.mContent);
        }
    }

    public String getData() {
        return this.mContent;
    }

    public boolean isCData() {
        return this.mIsCData;
    }

    public boolean isIgnorableWhiteSpace() {
        return this.mIgnorableWS;
    }

    public boolean isWhiteSpace() {
        if (!this.mWhitespaceChecked) {
            int n;
            this.mWhitespaceChecked = true;
            String string = this.mContent;
            int n2 = string.length();
            for (n = 0; n < n2 && string.charAt(n) <= ' '; ++n) {
            }
            this.mIsWhitespace = n == n2;
        }
        return this.mIsWhitespace;
    }

    public void setWhitespaceStatus(boolean bl) {
        this.mWhitespaceChecked = true;
        this.mIsWhitespace = bl;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof Characters)) {
            return false;
        }
        Characters characters = (Characters)object;
        if (this.mContent.equals(characters.getData())) {
            return this.isCData() == characters.isCData();
        }
        return false;
    }

    public int hashCode() {
        return this.mContent.hashCode();
    }

    protected static void writeEscapedXMLText(Writer writer, String string) throws IOException {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            int n2 = i;
            char c = '\u0000';
            while (i < n && (c = string.charAt(i)) != '<' && c != '&' && (c != '>' || i < 2 || string.charAt(i - 1) != ']' || string.charAt(i - 2) != ']')) {
                ++i;
            }
            int n3 = i - n2;
            if (n3 > 0) {
                writer.write(string, n2, n3);
            }
            if (i >= n) continue;
            if (c == '<') {
                writer.write("&lt;");
                continue;
            }
            if (c == '&') {
                writer.write("&amp;");
                continue;
            }
            if (c != '>') continue;
            writer.write("&gt;");
        }
    }
}


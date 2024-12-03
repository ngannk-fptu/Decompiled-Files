/*
 * Decompiled with CFR 0.152.
 */
package org.kxml2.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class KXmlParser
implements XmlPullParser {
    private Object location;
    private static final String UNEXPECTED_EOF = "Unexpected EOF";
    private static final String ILLEGAL_TYPE = "Wrong event type";
    private static final int LEGACY = 999;
    private static final int XML_DECL = 998;
    private String version;
    private Boolean standalone;
    private boolean processNsp;
    private boolean relaxed;
    private Hashtable entityMap;
    private int depth;
    private String[] elementStack = new String[16];
    private String[] nspStack = new String[8];
    private int[] nspCounts = new int[4];
    private Reader reader;
    private String encoding;
    private char[] srcBuf;
    private int srcPos;
    private int srcCount;
    private int line;
    private int column;
    private char[] txtBuf = new char[128];
    private int txtPos;
    private int type;
    private boolean isWhitespace;
    private String namespace;
    private String prefix;
    private String name;
    private boolean degenerated;
    private int attributeCount;
    private String[] attributes = new String[16];
    private int stackMismatch = 0;
    private String error;
    private int[] peek = new int[2];
    private int peekCount;
    private boolean wasCR;
    private boolean unresolved;
    private boolean token;

    public KXmlParser() {
        this.srcBuf = new char[Runtime.getRuntime().freeMemory() >= 0x100000L ? 8192 : 128];
    }

    private final boolean isProp(String string, boolean bl, String string2) {
        if (!string.startsWith("http://xmlpull.org/v1/doc/")) {
            return false;
        }
        if (bl) {
            return string.substring(42).equals(string2);
        }
        return string.substring(40).equals(string2);
    }

    private final boolean adjustNsp() throws XmlPullParserException {
        String string;
        int n;
        String string2;
        int n2;
        boolean bl = false;
        for (n2 = 0; n2 < this.attributeCount << 2; n2 += 4) {
            string2 = this.attributes[n2 + 2];
            n = string2.indexOf(58);
            if (n != -1) {
                string = string2.substring(0, n);
                string2 = string2.substring(n + 1);
            } else {
                if (!string2.equals("xmlns")) continue;
                string = string2;
                string2 = null;
            }
            if (!string.equals("xmlns")) {
                bl = true;
                continue;
            }
            int n3 = this.depth;
            int n4 = this.nspCounts[n3];
            this.nspCounts[n3] = n4 + 1;
            int n5 = n4 << 1;
            this.nspStack = this.ensureCapacity(this.nspStack, n5 + 2);
            this.nspStack[n5] = string2;
            this.nspStack[n5 + 1] = this.attributes[n2 + 3];
            if (string2 != null && this.attributes[n2 + 3].equals("")) {
                this.error("illegal empty namespace");
            }
            System.arraycopy(this.attributes, n2 + 4, this.attributes, n2, (--this.attributeCount << 2) - n2);
            n2 -= 4;
        }
        if (bl) {
            for (n2 = (this.attributeCount << 2) - 4; n2 >= 0; n2 -= 4) {
                string2 = this.attributes[n2 + 2];
                n = string2.indexOf(58);
                if (n == 0 && !this.relaxed) {
                    throw new RuntimeException("illegal attribute name: " + string2 + " at " + this);
                }
                if (n == -1) continue;
                string = string2.substring(0, n);
                string2 = string2.substring(n + 1);
                String string3 = this.getNamespace(string);
                if (string3 == null && !this.relaxed) {
                    throw new RuntimeException("Undefined Prefix: " + string + " in " + this);
                }
                this.attributes[n2] = string3;
                this.attributes[n2 + 1] = string;
                this.attributes[n2 + 2] = string2;
            }
        }
        if ((n2 = this.name.indexOf(58)) == 0) {
            this.error("illegal tag name: " + this.name);
        }
        if (n2 != -1) {
            this.prefix = this.name.substring(0, n2);
            this.name = this.name.substring(n2 + 1);
        }
        this.namespace = this.getNamespace(this.prefix);
        if (this.namespace == null) {
            if (this.prefix != null) {
                this.error("undefined prefix: " + this.prefix);
            }
            this.namespace = "";
        }
        return bl;
    }

    private final String[] ensureCapacity(String[] stringArray, int n) {
        if (stringArray.length >= n) {
            return stringArray;
        }
        String[] stringArray2 = new String[n + 16];
        System.arraycopy(stringArray, 0, stringArray2, 0, stringArray.length);
        return stringArray2;
    }

    private final void error(String string) throws XmlPullParserException {
        if (this.relaxed) {
            if (this.error == null) {
                this.error = "ERR: " + string;
            }
        } else {
            this.exception(string);
        }
    }

    private final void exception(String string) throws XmlPullParserException {
        throw new XmlPullParserException(string.length() < 100 ? string : string.substring(0, 100) + "\n", this, null);
    }

    private final void nextImpl() throws IOException, XmlPullParserException {
        if (this.reader == null) {
            this.exception("No Input specified");
        }
        if (this.type == 3) {
            --this.depth;
        }
        do {
            this.attributeCount = -1;
            if (this.degenerated) {
                this.degenerated = false;
                this.type = 3;
                return;
            }
            if (this.error != null) {
                for (int i = 0; i < this.error.length(); ++i) {
                    this.push(this.error.charAt(i));
                }
                this.error = null;
                this.type = 9;
                return;
            }
            if (this.relaxed && (this.stackMismatch > 0 || this.peek(0) == -1 && this.depth > 0)) {
                int n = this.depth - 1 << 2;
                this.type = 3;
                this.namespace = this.elementStack[n];
                this.prefix = this.elementStack[n + 1];
                this.name = this.elementStack[n + 2];
                if (this.stackMismatch != 1) {
                    this.error = "missing end tag /" + this.name + " inserted";
                }
                if (this.stackMismatch > 0) {
                    --this.stackMismatch;
                }
                return;
            }
            this.prefix = null;
            this.name = null;
            this.namespace = null;
            this.type = this.peekType();
            switch (this.type) {
                case 6: {
                    this.pushEntity();
                    return;
                }
                case 2: {
                    this.parseStartTag(false);
                    return;
                }
                case 3: {
                    this.parseEndTag();
                    return;
                }
                case 1: {
                    return;
                }
                case 4: {
                    this.pushText(60, !this.token);
                    if (this.depth == 0 && this.isWhitespace) {
                        this.type = 7;
                    }
                    return;
                }
            }
            this.type = this.parseLegacy(this.token);
        } while (this.type == 998);
    }

    private final int parseLegacy(boolean bl) throws IOException, XmlPullParserException {
        int n;
        int n2;
        String string = "";
        int n3 = 0;
        this.read();
        int n4 = this.read();
        if (n4 == 63) {
            if (!(this.peek(0) != 120 && this.peek(0) != 88 || this.peek(1) != 109 && this.peek(1) != 77)) {
                if (bl) {
                    this.push(this.peek(0));
                    this.push(this.peek(1));
                }
                this.read();
                this.read();
                if ((this.peek(0) == 108 || this.peek(0) == 76) && this.peek(1) <= 32) {
                    if (this.line != 1 || this.column > 4) {
                        this.error("PI must not start with xml");
                    }
                    this.parseStartTag(true);
                    if (this.attributeCount < 1 || !"version".equals(this.attributes[2])) {
                        this.error("version expected");
                    }
                    this.version = this.attributes[3];
                    int n5 = 1;
                    if (n5 < this.attributeCount && "encoding".equals(this.attributes[6])) {
                        this.encoding = this.attributes[7];
                        ++n5;
                    }
                    if (n5 < this.attributeCount && "standalone".equals(this.attributes[4 * n5 + 2])) {
                        String string2 = this.attributes[3 + 4 * n5];
                        if ("yes".equals(string2)) {
                            this.standalone = new Boolean(true);
                        } else if ("no".equals(string2)) {
                            this.standalone = new Boolean(false);
                        } else {
                            this.error("illegal standalone value: " + string2);
                        }
                        ++n5;
                    }
                    if (n5 != this.attributeCount) {
                        this.error("illegal xmldecl");
                    }
                    this.isWhitespace = true;
                    this.txtPos = 0;
                    return 998;
                }
            }
            n2 = 63;
            n = 8;
        } else if (n4 == 33) {
            if (this.peek(0) == 45) {
                n = 9;
                string = "--";
                n2 = 45;
            } else if (this.peek(0) == 91) {
                n = 5;
                string = "[CDATA[";
                n2 = 93;
                bl = true;
            } else {
                n = 10;
                string = "DOCTYPE";
                n2 = -1;
            }
        } else {
            this.error("illegal: <" + n4);
            return 9;
        }
        for (int i = 0; i < string.length(); ++i) {
            this.read(string.charAt(i));
        }
        if (n == 10) {
            this.parseDoctype(bl);
        } else {
            while (true) {
                if ((n4 = this.read()) == -1) {
                    this.error(UNEXPECTED_EOF);
                    return 9;
                }
                if (bl) {
                    this.push(n4);
                }
                if ((n2 == 63 || n4 == n2) && this.peek(0) == n2 && this.peek(1) == 62) break;
                n3 = n4;
            }
            if (n2 == 45 && n3 == 45) {
                this.error("illegal comment delimiter: --->");
            }
            this.read();
            this.read();
            if (bl && n2 != 63) {
                --this.txtPos;
            }
        }
        return n;
    }

    private final void parseDoctype(boolean bl) throws IOException, XmlPullParserException {
        int n = 1;
        boolean bl2 = false;
        while (true) {
            int n2 = this.read();
            switch (n2) {
                case -1: {
                    this.error(UNEXPECTED_EOF);
                    return;
                }
                case 39: {
                    bl2 = !bl2;
                    break;
                }
                case 60: {
                    if (bl2) break;
                    ++n;
                    break;
                }
                case 62: {
                    if (bl2 || --n != 0) break;
                    return;
                }
            }
            if (!bl) continue;
            this.push(n2);
        }
    }

    private final void parseEndTag() throws IOException, XmlPullParserException {
        this.read();
        this.read();
        this.name = this.readName();
        this.skip();
        this.read('>');
        int n = this.depth - 1 << 2;
        if (this.depth == 0) {
            this.error("element stack empty");
            this.type = 9;
            return;
        }
        if (!this.name.equals(this.elementStack[n + 3])) {
            int n2;
            this.error("expected: /" + this.elementStack[n + 3] + " read: " + this.name);
            for (n2 = n; n2 >= 0 && !this.name.toLowerCase().equals(this.elementStack[n2 + 3].toLowerCase()); n2 -= 4) {
                ++this.stackMismatch;
            }
            if (n2 < 0) {
                this.stackMismatch = 0;
                this.type = 9;
                return;
            }
        }
        this.namespace = this.elementStack[n];
        this.prefix = this.elementStack[n + 1];
        this.name = this.elementStack[n + 2];
    }

    private final int peekType() throws IOException {
        switch (this.peek(0)) {
            case -1: {
                return 1;
            }
            case 38: {
                return 6;
            }
            case 60: {
                switch (this.peek(1)) {
                    case 47: {
                        return 3;
                    }
                    case 33: 
                    case 63: {
                        return 999;
                    }
                }
                return 2;
            }
        }
        return 4;
    }

    private final String get(int n) {
        return new String(this.txtBuf, n, this.txtPos - n);
    }

    private final void push(int n) {
        this.isWhitespace &= n <= 32;
        if (this.txtPos == this.txtBuf.length) {
            char[] cArray = new char[this.txtPos * 4 / 3 + 4];
            System.arraycopy(this.txtBuf, 0, cArray, 0, this.txtPos);
            this.txtBuf = cArray;
        }
        this.txtBuf[this.txtPos++] = (char)n;
    }

    private final void parseStartTag(boolean bl) throws IOException, XmlPullParserException {
        Object object;
        int n;
        if (!bl) {
            this.read();
        }
        this.name = this.readName();
        this.attributeCount = 0;
        while (true) {
            this.skip();
            n = this.peek(0);
            if (bl) {
                if (n == 63) {
                    this.read();
                    this.read('>');
                    return;
                }
            } else {
                if (n == 47) {
                    this.degenerated = true;
                    this.read();
                    this.skip();
                    this.read('>');
                    break;
                }
                if (n == 62 && !bl) {
                    this.read();
                    break;
                }
            }
            if (n == -1) {
                this.error(UNEXPECTED_EOF);
                return;
            }
            object = this.readName();
            if (((String)object).length() == 0) {
                this.error("attr name expected");
                break;
            }
            int n2 = this.attributeCount++ << 2;
            this.attributes = this.ensureCapacity(this.attributes, n2 + 4);
            this.attributes[n2++] = "";
            this.attributes[n2++] = null;
            this.attributes[n2++] = object;
            this.skip();
            if (this.peek(0) != 61) {
                this.error("Attr.value missing f. " + (String)object);
                this.attributes[n2] = "1";
                continue;
            }
            this.read('=');
            this.skip();
            int n3 = this.peek(0);
            if (n3 != 39 && n3 != 34) {
                this.error("attr value delimiter missing!");
                n3 = 32;
            } else {
                this.read();
            }
            int n4 = this.txtPos;
            this.pushText(n3, true);
            this.attributes[n2] = this.get(n4);
            this.txtPos = n4;
            if (n3 == 32) continue;
            this.read();
        }
        n = this.depth++ << 2;
        this.elementStack = this.ensureCapacity(this.elementStack, n + 4);
        this.elementStack[n + 3] = this.name;
        if (this.depth >= this.nspCounts.length) {
            object = new int[this.depth + 4];
            System.arraycopy(this.nspCounts, 0, object, 0, this.nspCounts.length);
            this.nspCounts = (int[])object;
        }
        this.nspCounts[this.depth] = this.nspCounts[this.depth - 1];
        if (this.processNsp) {
            this.adjustNsp();
        } else {
            this.namespace = "";
        }
        this.elementStack[n] = this.namespace;
        this.elementStack[n + 1] = this.prefix;
        this.elementStack[n + 2] = this.name;
    }

    private final void pushEntity() throws IOException, XmlPullParserException {
        int n;
        this.push(this.read());
        int n2 = this.txtPos;
        while ((n = this.read()) != 59) {
            if (!(n >= 128 || n >= 48 && n <= 57 || n >= 97 && n <= 122 || n >= 65 && n <= 90 || n == 95 || n == 45 || n == 35)) {
                if (!this.relaxed) {
                    this.error("unterminated entity ref");
                }
                if (n != -1) {
                    this.push(n);
                }
                return;
            }
            this.push(n);
        }
        String string = this.get(n2);
        this.txtPos = n2 - 1;
        if (this.token && this.type == 6) {
            this.name = string;
        }
        if (string.charAt(0) == '#') {
            int n3 = string.charAt(1) == 'x' ? Integer.parseInt(string.substring(2), 16) : Integer.parseInt(string.substring(1));
            this.push(n3);
            return;
        }
        String string2 = (String)this.entityMap.get(string);
        boolean bl = this.unresolved = string2 == null;
        if (this.unresolved) {
            if (!this.token) {
                this.error("unresolved: &" + string + ";");
            }
        } else {
            for (int i = 0; i < string2.length(); ++i) {
                this.push(string2.charAt(i));
            }
        }
    }

    private final void pushText(int n, boolean bl) throws IOException, XmlPullParserException {
        int n2 = this.peek(0);
        int n3 = 0;
        while (n2 != -1 && n2 != n && (n != 32 || n2 > 32 && n2 != 62)) {
            if (n2 == 38) {
                if (!bl) break;
                this.pushEntity();
            } else if (n2 == 10 && this.type == 2) {
                this.read();
                this.push(32);
            } else {
                this.push(this.read());
            }
            if (n2 == 62 && n3 >= 2 && n != 93) {
                this.error("Illegal: ]]>");
            }
            n3 = n2 == 93 ? ++n3 : 0;
            n2 = this.peek(0);
        }
    }

    private final void read(char c) throws IOException, XmlPullParserException {
        int n = this.read();
        if (n != c) {
            this.error("expected: '" + c + "' actual: '" + (char)n + "'");
        }
    }

    private final int read() throws IOException {
        int n;
        if (this.peekCount == 0) {
            n = this.peek(0);
        } else {
            n = this.peek[0];
            this.peek[0] = this.peek[1];
        }
        --this.peekCount;
        ++this.column;
        if (n == 10) {
            ++this.line;
            this.column = 1;
        }
        return n;
    }

    private final int peek(int n) throws IOException {
        while (n >= this.peekCount) {
            int n2;
            if (this.srcBuf.length <= 1) {
                n2 = this.reader.read();
            } else if (this.srcPos < this.srcCount) {
                n2 = this.srcBuf[this.srcPos++];
            } else {
                this.srcCount = this.reader.read(this.srcBuf, 0, this.srcBuf.length);
                n2 = this.srcCount <= 0 ? -1 : this.srcBuf[0];
                this.srcPos = 1;
            }
            if (n2 == 13) {
                this.wasCR = true;
                this.peek[this.peekCount++] = 10;
                continue;
            }
            if (n2 == 10) {
                if (!this.wasCR) {
                    this.peek[this.peekCount++] = 10;
                }
            } else {
                this.peek[this.peekCount++] = n2;
            }
            this.wasCR = false;
        }
        return this.peek[n];
    }

    private final String readName() throws IOException, XmlPullParserException {
        int n = this.txtPos;
        int n2 = this.peek(0);
        if (!(n2 >= 97 && n2 <= 122 || n2 >= 65 && n2 <= 90 || n2 == 95 || n2 == 58 || n2 >= 192 || this.relaxed)) {
            this.error("name expected");
        }
        do {
            this.push(this.read());
        } while ((n2 = this.peek(0)) >= 97 && n2 <= 122 || n2 >= 65 && n2 <= 90 || n2 >= 48 && n2 <= 57 || n2 == 95 || n2 == 45 || n2 == 58 || n2 == 46 || n2 >= 183);
        String string = this.get(n);
        this.txtPos = n;
        return string;
    }

    private final void skip() throws IOException {
        int n;
        while ((n = this.peek(0)) <= 32 && n != -1) {
            this.read();
        }
    }

    public void setInput(Reader reader) throws XmlPullParserException {
        this.reader = reader;
        this.line = 1;
        this.column = 0;
        this.type = 0;
        this.name = null;
        this.namespace = null;
        this.degenerated = false;
        this.attributeCount = -1;
        this.encoding = null;
        this.version = null;
        this.standalone = null;
        if (reader == null) {
            return;
        }
        this.srcPos = 0;
        this.srcCount = 0;
        this.peekCount = 0;
        this.depth = 0;
        this.entityMap = new Hashtable();
        this.entityMap.put("amp", "&");
        this.entityMap.put("apos", "'");
        this.entityMap.put("gt", ">");
        this.entityMap.put("lt", "<");
        this.entityMap.put("quot", "\"");
    }

    public void setInput(InputStream inputStream, String string) throws XmlPullParserException {
        this.srcPos = 0;
        this.srcCount = 0;
        String string2 = string;
        if (inputStream == null) {
            throw new IllegalArgumentException();
        }
        try {
            int n;
            if (string2 == null) {
                int n2;
                n = 0;
                while (this.srcCount < 4 && (n2 = inputStream.read()) != -1) {
                    n = n << 8 | n2;
                    this.srcBuf[this.srcCount++] = (char)n2;
                }
                if (this.srcCount == 4) {
                    switch (n) {
                        case 65279: {
                            string2 = "UTF-32BE";
                            this.srcCount = 0;
                            break;
                        }
                        case -131072: {
                            string2 = "UTF-32LE";
                            this.srcCount = 0;
                            break;
                        }
                        case 60: {
                            string2 = "UTF-32BE";
                            this.srcBuf[0] = 60;
                            this.srcCount = 1;
                            break;
                        }
                        case 0x3C000000: {
                            string2 = "UTF-32LE";
                            this.srcBuf[0] = 60;
                            this.srcCount = 1;
                            break;
                        }
                        case 3932223: {
                            string2 = "UTF-16BE";
                            this.srcBuf[0] = 60;
                            this.srcBuf[1] = 63;
                            this.srcCount = 2;
                            break;
                        }
                        case 1006649088: {
                            string2 = "UTF-16LE";
                            this.srcBuf[0] = 60;
                            this.srcBuf[1] = 63;
                            this.srcCount = 2;
                            break;
                        }
                        case 1010792557: {
                            while ((n2 = inputStream.read()) != -1) {
                                this.srcBuf[this.srcCount++] = (char)n2;
                                if (n2 != 62) continue;
                                String string3 = new String(this.srcBuf, 0, this.srcCount);
                                int n3 = string3.indexOf("encoding");
                                if (n3 == -1) break;
                                while (string3.charAt(n3) != '\"' && string3.charAt(n3) != '\'') {
                                    ++n3;
                                }
                                char c = string3.charAt(n3++);
                                int n4 = string3.indexOf(c, n3);
                                string2 = string3.substring(n3, n4);
                                break;
                            }
                        }
                        default: {
                            if ((n & 0xFFFF0000) == -16842752) {
                                string2 = "UTF-16BE";
                                this.srcBuf[0] = (char)(this.srcBuf[2] << 8 | this.srcBuf[3]);
                                this.srcCount = 1;
                                break;
                            }
                            if ((n & 0xFFFF0000) == -131072) {
                                string2 = "UTF-16LE";
                                this.srcBuf[0] = (char)(this.srcBuf[3] << 8 | this.srcBuf[2]);
                                this.srcCount = 1;
                                break;
                            }
                            if ((n & 0xFFFFFF00) != -272908544) break;
                            string2 = "UTF-8";
                            this.srcBuf[0] = this.srcBuf[3];
                            this.srcCount = 1;
                        }
                    }
                }
            }
            if (string2 == null) {
                string2 = "UTF-8";
            }
            n = this.srcCount;
            this.setInput(new InputStreamReader(inputStream, string2));
            this.encoding = string;
            this.srcCount = n;
        }
        catch (Exception exception) {
            throw new XmlPullParserException("Invalid stream or encoding: " + exception.toString(), this, exception);
        }
    }

    public boolean getFeature(String string) {
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(string)) {
            return this.processNsp;
        }
        if (this.isProp(string, false, "relaxed")) {
            return this.relaxed;
        }
        return false;
    }

    public String getInputEncoding() {
        return this.encoding;
    }

    public void defineEntityReplacementText(String string, String string2) throws XmlPullParserException {
        if (this.entityMap == null) {
            throw new RuntimeException("entity replacement text must be defined after setInput!");
        }
        this.entityMap.put(string, string2);
    }

    public Object getProperty(String string) {
        if (this.isProp(string, true, "xmldecl-version")) {
            return this.version;
        }
        if (this.isProp(string, true, "xmldecl-standalone")) {
            return this.standalone;
        }
        if (this.isProp(string, true, "location")) {
            return this.location != null ? this.location : this.reader.toString();
        }
        return null;
    }

    public int getNamespaceCount(int n) {
        if (n > this.depth) {
            throw new IndexOutOfBoundsException();
        }
        return this.nspCounts[n];
    }

    public String getNamespacePrefix(int n) {
        return this.nspStack[n << 1];
    }

    public String getNamespaceUri(int n) {
        return this.nspStack[(n << 1) + 1];
    }

    public String getNamespace(String string) {
        if ("xml".equals(string)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if ("xmlns".equals(string)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        for (int i = (this.getNamespaceCount(this.depth) << 1) - 2; i >= 0; i -= 2) {
            if (!(string == null ? this.nspStack[i] == null : string.equals(this.nspStack[i]))) continue;
            return this.nspStack[i + 1];
        }
        return null;
    }

    public int getDepth() {
        return this.depth;
    }

    public String getPositionDescription() {
        StringBuffer stringBuffer = new StringBuffer(this.type < XmlPullParser.TYPES.length ? XmlPullParser.TYPES[this.type] : "unknown");
        stringBuffer.append(' ');
        if (this.type == 2 || this.type == 3) {
            if (this.degenerated) {
                stringBuffer.append("(empty) ");
            }
            stringBuffer.append('<');
            if (this.type == 3) {
                stringBuffer.append('/');
            }
            if (this.prefix != null) {
                stringBuffer.append("{" + this.namespace + "}" + this.prefix + ":");
            }
            stringBuffer.append(this.name);
            int n = this.attributeCount << 2;
            for (int i = 0; i < n; i += 4) {
                stringBuffer.append(' ');
                if (this.attributes[i + 1] != null) {
                    stringBuffer.append("{" + this.attributes[i] + "}" + this.attributes[i + 1] + ":");
                }
                stringBuffer.append(this.attributes[i + 2] + "='" + this.attributes[i + 3] + "'");
            }
            stringBuffer.append('>');
        } else if (this.type != 7) {
            if (this.type != 4) {
                stringBuffer.append(this.getText());
            } else if (this.isWhitespace) {
                stringBuffer.append("(whitespace)");
            } else {
                String string = this.getText();
                if (string.length() > 16) {
                    string = string.substring(0, 16) + "...";
                }
                stringBuffer.append(string);
            }
        }
        stringBuffer.append("@" + this.line + ":" + this.column);
        if (this.location != null) {
            stringBuffer.append(" in ");
            stringBuffer.append(this.location);
        } else if (this.reader != null) {
            stringBuffer.append(" in ");
            stringBuffer.append(this.reader.toString());
        }
        return stringBuffer.toString();
    }

    public int getLineNumber() {
        return this.line;
    }

    public int getColumnNumber() {
        return this.column;
    }

    public boolean isWhitespace() throws XmlPullParserException {
        if (this.type != 4 && this.type != 7 && this.type != 5) {
            this.exception(ILLEGAL_TYPE);
        }
        return this.isWhitespace;
    }

    public String getText() {
        return this.type < 4 || this.type == 6 && this.unresolved ? null : this.get(0);
    }

    public char[] getTextCharacters(int[] nArray) {
        if (this.type >= 4) {
            if (this.type == 6) {
                nArray[0] = 0;
                nArray[1] = this.name.length();
                return this.name.toCharArray();
            }
            nArray[0] = 0;
            nArray[1] = this.txtPos;
            return this.txtBuf;
        }
        nArray[0] = -1;
        nArray[1] = -1;
        return null;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isEmptyElementTag() throws XmlPullParserException {
        if (this.type != 2) {
            this.exception(ILLEGAL_TYPE);
        }
        return this.degenerated;
    }

    public int getAttributeCount() {
        return this.attributeCount;
    }

    public String getAttributeType(int n) {
        return "CDATA";
    }

    public boolean isAttributeDefault(int n) {
        return false;
    }

    public String getAttributeNamespace(int n) {
        if (n >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[n << 2];
    }

    public String getAttributeName(int n) {
        if (n >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[(n << 2) + 2];
    }

    public String getAttributePrefix(int n) {
        if (n >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[(n << 2) + 1];
    }

    public String getAttributeValue(int n) {
        if (n >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[(n << 2) + 3];
    }

    public String getAttributeValue(String string, String string2) {
        for (int i = (this.attributeCount << 2) - 4; i >= 0; i -= 4) {
            if (!this.attributes[i + 2].equals(string2) || string != null && !this.attributes[i].equals(string)) continue;
            return this.attributes[i + 3];
        }
        return null;
    }

    public int getEventType() throws XmlPullParserException {
        return this.type;
    }

    public int next() throws XmlPullParserException, IOException {
        this.txtPos = 0;
        this.isWhitespace = true;
        int n = 9999;
        this.token = false;
        do {
            this.nextImpl();
            if (this.type >= n) continue;
            n = this.type;
        } while (n > 6 || n >= 4 && this.peekType() >= 4);
        this.type = n;
        if (this.type > 4) {
            this.type = 4;
        }
        return this.type;
    }

    public int nextToken() throws XmlPullParserException, IOException {
        this.isWhitespace = true;
        this.txtPos = 0;
        this.token = true;
        this.nextImpl();
        return this.type;
    }

    public int nextTag() throws XmlPullParserException, IOException {
        this.next();
        if (this.type == 4 && this.isWhitespace) {
            this.next();
        }
        if (this.type != 3 && this.type != 2) {
            this.exception("unexpected type");
        }
        return this.type;
    }

    public void require(int n, String string, String string2) throws XmlPullParserException, IOException {
        if (n != this.type || string != null && !string.equals(this.getNamespace()) || string2 != null && !string2.equals(this.getName())) {
            this.exception("expected: " + XmlPullParser.TYPES[n] + " {" + string + "}" + string2);
        }
    }

    public String nextText() throws XmlPullParserException, IOException {
        String string;
        if (this.type != 2) {
            this.exception("precondition: START_TAG");
        }
        this.next();
        if (this.type == 4) {
            string = this.getText();
            this.next();
        } else {
            string = "";
        }
        if (this.type != 3) {
            this.exception("END_TAG expected");
        }
        return string;
    }

    public void setFeature(String string, boolean bl) throws XmlPullParserException {
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(string)) {
            this.processNsp = bl;
        } else if (this.isProp(string, false, "relaxed")) {
            this.relaxed = bl;
        } else {
            this.exception("unsupported feature: " + string);
        }
    }

    public void setProperty(String string, Object object) throws XmlPullParserException {
        if (!this.isProp(string, true, "location")) {
            throw new XmlPullParserException("unsupported property: " + string);
        }
        this.location = object;
    }

    public void skipSubTree() throws XmlPullParserException, IOException {
        this.require(2, null, null);
        int n = 1;
        while (n > 0) {
            int n2 = this.next();
            if (n2 == 3) {
                --n;
                continue;
            }
            if (n2 != 2) continue;
            ++n;
        }
    }
}


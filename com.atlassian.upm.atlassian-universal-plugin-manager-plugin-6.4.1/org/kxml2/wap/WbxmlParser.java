/*
 * Decompiled with CFR 0.152.
 */
package org.kxml2.wap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class WbxmlParser
implements XmlPullParser {
    public static final int WAP_EXTENSION = 64;
    private static final String UNEXPECTED_EOF = "Unexpected EOF";
    private static final String ILLEGAL_TYPE = "Wrong event type";
    private InputStream in;
    private int TAG_TABLE = 0;
    private int ATTR_START_TABLE = 1;
    private int ATTR_VALUE_TABLE = 2;
    private String[] attrStartTable;
    private String[] attrValueTable;
    private String[] tagTable;
    private byte[] stringTable;
    private Hashtable cacheStringTable = null;
    private boolean processNsp;
    private int depth;
    private String[] elementStack = new String[16];
    private String[] nspStack = new String[8];
    private int[] nspCounts = new int[4];
    private int attributeCount;
    private String[] attributes = new String[16];
    private int nextId = -2;
    private Vector tables = new Vector();
    int version;
    int publicIdentifierId;
    int charSet;
    private String prefix;
    private String namespace;
    private String name;
    private String text;
    private Object wapExtensionData;
    private int wapExtensionCode;
    private int type;
    private int codePage;
    private boolean degenerated;
    private boolean isWhitespace;
    private String encoding = null;

    public boolean getFeature(String string) {
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(string)) {
            return this.processNsp;
        }
        return false;
    }

    public String getInputEncoding() {
        return this.encoding;
    }

    public void defineEntityReplacementText(String string, String string2) throws XmlPullParserException {
    }

    public Object getProperty(String string) {
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
        return stringBuffer.toString();
    }

    public int getLineNumber() {
        return -1;
    }

    public int getColumnNumber() {
        return -1;
    }

    public boolean isWhitespace() throws XmlPullParserException {
        if (this.type != 4 && this.type != 7 && this.type != 5) {
            this.exception(ILLEGAL_TYPE);
        }
        return this.isWhitespace;
    }

    public String getText() {
        return this.text;
    }

    public char[] getTextCharacters(int[] nArray) {
        if (this.type >= 4) {
            nArray[0] = 0;
            nArray[1] = this.text.length();
            char[] cArray = new char[this.text.length()];
            this.text.getChars(0, this.text.length(), cArray, 0);
            return cArray;
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
        this.isWhitespace = true;
        int n = 9999;
        block3: while (true) {
            String string = this.text;
            this.nextImpl();
            if (this.type < n) {
                n = this.type;
            }
            if (n > 5) continue;
            if (n < 4) break;
            if (string != null) {
                this.text = this.text == null ? string : string + this.text;
            }
            switch (this.peekId()) {
                case 2: 
                case 3: 
                case 4: 
                case 68: 
                case 132: 
                case 196: {
                    continue block3;
                }
            }
            break;
        }
        this.type = n;
        if (this.type > 4) {
            this.type = 4;
        }
        return this.type;
    }

    public int nextToken() throws XmlPullParserException, IOException {
        this.isWhitespace = true;
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

    public void require(int n, String string, String string2) throws XmlPullParserException, IOException {
        if (n != this.type || string != null && !string.equals(this.getNamespace()) || string2 != null && !string2.equals(this.getName())) {
            this.exception("expected: " + XmlPullParser.TYPES[n] + " {" + string + "}" + string2);
        }
    }

    public void setInput(Reader reader) throws XmlPullParserException {
        this.exception("InputStream required");
    }

    public void setInput(InputStream inputStream, String string) throws XmlPullParserException {
        this.in = inputStream;
        try {
            int n;
            block9: {
                block8: {
                    this.version = this.readByte();
                    this.publicIdentifierId = this.readInt();
                    if (this.publicIdentifierId == 0) {
                        this.readInt();
                    }
                    int n2 = this.readInt();
                    if (null != string) break block8;
                    switch (n2) {
                        case 4: {
                            this.encoding = "ISO-8859-1";
                            break block9;
                        }
                        case 106: {
                            this.encoding = "UTF-8";
                            break block9;
                        }
                        default: {
                            throw new UnsupportedEncodingException("" + n2);
                        }
                    }
                }
                this.encoding = string;
            }
            int n3 = this.readInt();
            this.stringTable = new byte[n3];
            for (int i = 0; i < n3 && (n = inputStream.read(this.stringTable, i, n3 - i)) > 0; i += n) {
            }
            this.selectPage(0, true);
            this.selectPage(0, false);
        }
        catch (IOException iOException) {
            this.exception("Illegal input format");
        }
    }

    public void setFeature(String string, boolean bl) throws XmlPullParserException {
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(string)) {
            this.processNsp = bl;
        } else {
            this.exception("unsupported feature: " + string);
        }
    }

    public void setProperty(String string, Object object) throws XmlPullParserException {
        throw new XmlPullParserException("unsupported property: " + string);
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
                this.exception("illegal empty namespace");
            }
            System.arraycopy(this.attributes, n2 + 4, this.attributes, n2, (--this.attributeCount << 2) - n2);
            n2 -= 4;
        }
        if (bl) {
            for (n2 = (this.attributeCount << 2) - 4; n2 >= 0; n2 -= 4) {
                string2 = this.attributes[n2 + 2];
                n = string2.indexOf(58);
                if (n == 0) {
                    throw new RuntimeException("illegal attribute name: " + string2 + " at " + this);
                }
                if (n == -1) continue;
                string = string2.substring(0, n);
                string2 = string2.substring(n + 1);
                String string3 = this.getNamespace(string);
                if (string3 == null) {
                    throw new RuntimeException("Undefined Prefix: " + string + " in " + this);
                }
                this.attributes[n2] = string3;
                this.attributes[n2 + 1] = string;
                this.attributes[n2 + 2] = string2;
                for (int i = (this.attributeCount << 2) - 4; i > n2; i -= 4) {
                    if (!string2.equals(this.attributes[i + 2]) || !string3.equals(this.attributes[i])) continue;
                    this.exception("Duplicate Attribute: {" + string3 + "}" + string2);
                }
            }
        }
        if ((n2 = this.name.indexOf(58)) == 0) {
            this.exception("illegal tag name: " + this.name);
        } else if (n2 != -1) {
            this.prefix = this.name.substring(0, n2);
            this.name = this.name.substring(n2 + 1);
        }
        this.namespace = this.getNamespace(this.prefix);
        if (this.namespace == null) {
            if (this.prefix != null) {
                this.exception("undefined prefix: " + this.prefix);
            }
            this.namespace = "";
        }
        return bl;
    }

    private final void setTable(int n, int n2, String[] stringArray) {
        if (this.stringTable != null) {
            throw new RuntimeException("setXxxTable must be called before setInput!");
        }
        while (this.tables.size() < 3 * n + 3) {
            this.tables.addElement(null);
        }
        this.tables.setElementAt(stringArray, n * 3 + n2);
    }

    private final void exception(String string) throws XmlPullParserException {
        throw new XmlPullParserException(string, this, null);
    }

    private void selectPage(int n, boolean bl) throws XmlPullParserException {
        if (this.tables.size() == 0 && n == 0) {
            return;
        }
        if (n * 3 > this.tables.size()) {
            this.exception("Code Page " + n + " undefined!");
        }
        if (bl) {
            this.tagTable = (String[])this.tables.elementAt(n * 3 + this.TAG_TABLE);
        } else {
            this.attrStartTable = (String[])this.tables.elementAt(n * 3 + this.ATTR_START_TABLE);
            this.attrValueTable = (String[])this.tables.elementAt(n * 3 + this.ATTR_VALUE_TABLE);
        }
    }

    private final void nextImpl() throws IOException, XmlPullParserException {
        if (this.type == 3) {
            --this.depth;
        }
        if (this.degenerated) {
            this.type = 3;
            this.degenerated = false;
            return;
        }
        this.text = null;
        this.prefix = null;
        this.name = null;
        int n = this.peekId();
        while (n == 0) {
            this.nextId = -2;
            this.selectPage(this.readByte(), true);
            n = this.peekId();
        }
        this.nextId = -2;
        switch (n) {
            case -1: {
                this.type = 1;
                break;
            }
            case 1: {
                int n2 = this.depth - 1 << 2;
                this.type = 3;
                this.namespace = this.elementStack[n2];
                this.prefix = this.elementStack[n2 + 1];
                this.name = this.elementStack[n2 + 2];
                break;
            }
            case 2: {
                this.type = 6;
                char c = (char)this.readInt();
                this.text = "" + c;
                this.name = "#" + c;
                break;
            }
            case 3: {
                this.type = 4;
                this.text = this.readStrI();
                break;
            }
            case 64: 
            case 65: 
            case 66: 
            case 128: 
            case 129: 
            case 130: 
            case 192: 
            case 193: 
            case 194: 
            case 195: {
                this.parseWapExtension(n);
                break;
            }
            case 67: {
                throw new RuntimeException("PI curr. not supp.");
            }
            case 131: {
                this.type = 4;
                this.text = this.readStrT();
                break;
            }
            default: {
                this.parseElement(n);
            }
        }
    }

    public void parseWapExtension(int n) throws IOException, XmlPullParserException {
        this.type = 64;
        this.wapExtensionCode = n;
        switch (n) {
            case 64: 
            case 65: 
            case 66: {
                this.wapExtensionData = this.readStrI();
                break;
            }
            case 128: 
            case 129: 
            case 130: {
                this.wapExtensionData = new Integer(this.readInt());
                break;
            }
            case 192: 
            case 193: 
            case 194: {
                break;
            }
            case 195: {
                int n2 = this.readInt();
                byte[] byArray = new byte[n2];
                for (int i = 0; i < n2; ++i) {
                    byArray[i] = (byte)this.readByte();
                }
                this.wapExtensionData = byArray;
                break;
            }
            default: {
                this.exception("illegal id: " + n);
            }
        }
    }

    public void readAttr() throws IOException, XmlPullParserException {
        int n = this.readByte();
        int n2 = 0;
        while (n != 1) {
            StringBuffer stringBuffer;
            while (n == 0) {
                this.selectPage(this.readByte(), false);
                n = this.readByte();
            }
            String string = this.resolveId(this.attrStartTable, n);
            int n3 = string.indexOf(61);
            if (n3 == -1) {
                stringBuffer = new StringBuffer();
            } else {
                stringBuffer = new StringBuffer(string.substring(n3 + 1));
                string = string.substring(0, n3);
            }
            n = this.readByte();
            while (n > 128 || n == 0 || n == 2 || n == 3 || n == 131 || n >= 64 && n <= 66 || n >= 128 && n <= 130) {
                switch (n) {
                    case 0: {
                        this.selectPage(this.readByte(), false);
                        break;
                    }
                    case 2: {
                        stringBuffer.append((char)this.readInt());
                        break;
                    }
                    case 3: {
                        stringBuffer.append(this.readStrI());
                        break;
                    }
                    case 64: 
                    case 65: 
                    case 66: 
                    case 128: 
                    case 129: 
                    case 130: 
                    case 192: 
                    case 193: 
                    case 194: 
                    case 195: {
                        throw new RuntimeException("wap extension in attr not supported yet");
                    }
                    case 131: {
                        stringBuffer.append(this.readStrT());
                        break;
                    }
                    default: {
                        stringBuffer.append(this.resolveId(this.attrValueTable, n));
                    }
                }
                n = this.readByte();
            }
            this.attributes = this.ensureCapacity(this.attributes, n2 + 4);
            this.attributes[n2++] = "";
            this.attributes[n2++] = null;
            this.attributes[n2++] = string;
            this.attributes[n2++] = stringBuffer.toString();
            ++this.attributeCount;
        }
    }

    private int peekId() throws IOException {
        if (this.nextId == -2) {
            this.nextId = this.in.read();
        }
        return this.nextId;
    }

    String resolveId(String[] stringArray, int n) throws IOException {
        int n2 = (n & 0x7F) - 5;
        if (n2 == -1) {
            return this.readStrT();
        }
        if (n2 < 0 || stringArray == null || n2 >= stringArray.length || stringArray[n2] == null) {
            throw new IOException("id " + n + " undef.");
        }
        return stringArray[n2];
    }

    void parseElement(int n) throws IOException, XmlPullParserException {
        this.type = 2;
        this.name = this.resolveId(this.tagTable, n & 0x3F);
        this.attributeCount = 0;
        if ((n & 0x80) != 0) {
            this.readAttr();
        }
        this.degenerated = (n & 0x40) == 0;
        int n2 = this.depth++ << 2;
        this.elementStack = this.ensureCapacity(this.elementStack, n2 + 4);
        this.elementStack[n2 + 3] = this.name;
        if (this.depth >= this.nspCounts.length) {
            int[] nArray = new int[this.depth + 4];
            System.arraycopy(this.nspCounts, 0, nArray, 0, this.nspCounts.length);
            this.nspCounts = nArray;
        }
        this.nspCounts[this.depth] = this.nspCounts[this.depth - 1];
        for (int i = this.attributeCount - 1; i > 0; --i) {
            for (int j = 0; j < i; ++j) {
                if (!this.getAttributeName(i).equals(this.getAttributeName(j))) continue;
                this.exception("Duplicate Attribute: " + this.getAttributeName(i));
            }
        }
        if (this.processNsp) {
            this.adjustNsp();
        } else {
            this.namespace = "";
        }
        this.elementStack[n2] = this.namespace;
        this.elementStack[n2 + 1] = this.prefix;
        this.elementStack[n2 + 2] = this.name;
    }

    private final String[] ensureCapacity(String[] stringArray, int n) {
        if (stringArray.length >= n) {
            return stringArray;
        }
        String[] stringArray2 = new String[n + 16];
        System.arraycopy(stringArray, 0, stringArray2, 0, stringArray.length);
        return stringArray2;
    }

    int readByte() throws IOException {
        int n = this.in.read();
        if (n == -1) {
            throw new IOException(UNEXPECTED_EOF);
        }
        return n;
    }

    int readInt() throws IOException {
        int n;
        int n2 = 0;
        do {
            n = this.readByte();
            n2 = n2 << 7 | n & 0x7F;
        } while ((n & 0x80) != 0);
        return n2;
    }

    String readStrI() throws IOException {
        int n;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean bl = true;
        while ((n = this.in.read()) != 0) {
            if (n == -1) {
                throw new IOException(UNEXPECTED_EOF);
            }
            if (n > 32) {
                bl = false;
            }
            byteArrayOutputStream.write(n);
        }
        this.isWhitespace = bl;
        String string = new String(byteArrayOutputStream.toByteArray(), this.encoding);
        byteArrayOutputStream.close();
        return string;
    }

    String readStrT() throws IOException {
        String string;
        int n = this.readInt();
        if (this.cacheStringTable == null) {
            this.cacheStringTable = new Hashtable();
        }
        if ((string = (String)this.cacheStringTable.get(new Integer(n))) == null) {
            int n2;
            for (n2 = n; n2 < this.stringTable.length && this.stringTable[n2] != 0; ++n2) {
            }
            string = new String(this.stringTable, n, n2 - n, this.encoding);
            this.cacheStringTable.put(new Integer(n), string);
        }
        return string;
    }

    public void setTagTable(int n, String[] stringArray) {
        this.setTable(n, this.TAG_TABLE, stringArray);
    }

    public void setAttrStartTable(int n, String[] stringArray) {
        this.setTable(n, this.ATTR_START_TABLE, stringArray);
    }

    public void setAttrValueTable(int n, String[] stringArray) {
        this.setTable(n, this.ATTR_VALUE_TABLE, stringArray);
    }
}


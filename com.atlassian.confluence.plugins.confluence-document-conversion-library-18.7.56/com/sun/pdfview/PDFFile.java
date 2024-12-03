/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.Cache;
import com.sun.pdfview.OutlineNode;
import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.PDFParser;
import com.sun.pdfview.PDFXref;
import com.sun.pdfview.action.GoToAction;
import com.sun.pdfview.action.PDFAction;
import com.sun.pdfview.annotation.PDFAnnotation;
import com.sun.pdfview.decrypt.EncryptionUnsupportedByPlatformException;
import com.sun.pdfview.decrypt.EncryptionUnsupportedByProductException;
import com.sun.pdfview.decrypt.IdentityDecrypter;
import com.sun.pdfview.decrypt.PDFAuthenticationFailureException;
import com.sun.pdfview.decrypt.PDFDecrypter;
import com.sun.pdfview.decrypt.PDFDecrypterFactory;
import com.sun.pdfview.decrypt.PDFPassword;
import com.sun.pdfview.decrypt.UnsupportedEncryptionException;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class PDFFile {
    public static final int NUL_CHAR = 0;
    public static final int FF_CHAR = 12;
    private String versionString = "1.1";
    private int majorVersion = 1;
    private int minorVersion = 1;
    private static final String VERSION_COMMENT = "%PDF-";
    ByteBuffer buf;
    PDFXref[] objIdx;
    PDFObject root = null;
    PDFObject encrypt = null;
    PDFObject info = null;
    Cache cache;
    private boolean printable = true;
    private boolean saveable = true;
    private PDFDecrypter defaultDecrypter = IdentityDecrypter.getInstance();

    public PDFFile(ByteBuffer buf) throws IOException {
        this(buf, null);
    }

    public PDFFile(ByteBuffer buf, PDFPassword password) throws IOException {
        this.buf = buf;
        this.cache = new Cache();
        this.parseFile(password);
    }

    public boolean isPrintable() {
        return this.printable;
    }

    public boolean isSaveable() {
        return this.saveable;
    }

    public PDFObject getRoot() {
        return this.root;
    }

    public int getNumPages() {
        try {
            return this.root.getDictRef("Pages").getDictRef("Count").getIntValue();
        }
        catch (Exception ioe) {
            return 0;
        }
    }

    public String getStringMetadata(String name) throws IOException {
        if (this.info != null) {
            PDFObject meta = this.info.getDictRef(name);
            return meta != null ? meta.getTextStringValue() : null;
        }
        return null;
    }

    public Iterator<String> getMetadataKeys() throws IOException {
        if (this.info != null) {
            return this.info.getDictKeys();
        }
        return Collections.emptyList().iterator();
    }

    public synchronized PDFObject dereference(PDFXref ref, PDFDecrypter decrypter) throws IOException {
        int id = ref.getID();
        if (id >= this.objIdx.length || this.objIdx[id] == null) {
            return PDFObject.nullObj;
        }
        PDFObject obj = this.objIdx[id].getObject();
        if (obj != null) {
            return obj;
        }
        int startPos = this.buf.position();
        boolean compressed = this.objIdx[id].getCompressed();
        if (!compressed) {
            int loc = this.objIdx[id].getFilePos();
            if (loc < 0) {
                return PDFObject.nullObj;
            }
            this.buf.position(loc);
            obj = this.readObject(ref.getID(), ref.getGeneration(), decrypter);
        } else {
            int compId = this.objIdx[id].getID();
            int idx = this.objIdx[id].getGeneration();
            if (idx < 0) {
                return PDFObject.nullObj;
            }
            PDFXref compRef = new PDFXref(compId, 0);
            PDFObject compObj = this.dereference(compRef, decrypter);
            int first = compObj.getDictionary().get("First").getIntValue();
            int n = compObj.getDictionary().get("N").getIntValue();
            if (idx >= n) {
                return PDFObject.nullObj;
            }
            ByteBuffer strm = compObj.getStreamBuffer();
            ByteBuffer oldBuf = this.buf;
            this.buf = strm;
            for (int i = 0; i < idx; ++i) {
                this.readObject(-1, -1, true, IdentityDecrypter.getInstance());
                this.readObject(-1, -1, true, IdentityDecrypter.getInstance());
            }
            PDFObject objNumPO = this.readObject(-1, -1, true, IdentityDecrypter.getInstance());
            PDFObject offsetPO = this.readObject(-1, -1, true, IdentityDecrypter.getInstance());
            int objNum = objNumPO.getIntValue();
            int offset = offsetPO.getIntValue();
            if (objNum != id) {
                return PDFObject.nullObj;
            }
            this.buf.position(first + offset);
            obj = this.readObject(objNum, 0, IdentityDecrypter.getInstance());
            this.buf = oldBuf;
        }
        if (obj == null) {
            obj = PDFObject.nullObj;
        }
        this.objIdx[id].setObject(obj);
        this.buf.position(startPos);
        return obj;
    }

    public static boolean isWhiteSpace(int c) {
        switch (c) {
            case 0: 
            case 9: 
            case 10: 
            case 12: 
            case 13: 
            case 32: {
                return true;
            }
        }
        return false;
    }

    public static boolean isDelimiter(int c) {
        switch (c) {
            case 37: 
            case 40: 
            case 41: 
            case 47: 
            case 60: 
            case 62: 
            case 91: 
            case 93: 
            case 123: 
            case 125: {
                return true;
            }
        }
        return false;
    }

    public static boolean isRegularCharacter(int c) {
        return !PDFFile.isWhiteSpace(c) && !PDFFile.isDelimiter(c);
    }

    private PDFObject readObject(int objNum, int objGen, PDFDecrypter decrypter) throws IOException {
        return this.readObject(objNum, objGen, false, decrypter);
    }

    private PDFObject readObject(int objNum, int objGen, boolean numscan, PDFDecrypter decrypter) throws IOException {
        PDFObject obj = null;
        while (obj == null) {
            byte c;
            while (PDFFile.isWhiteSpace(c = this.buf.get())) {
            }
            if (c == 60) {
                c = this.buf.get();
                if (c == 60) {
                    obj = this.readDictionary(objNum, objGen, decrypter);
                    continue;
                }
                this.buf.position(this.buf.position() - 1);
                obj = this.readHexString(objNum, objGen, decrypter);
                continue;
            }
            if (c == 40) {
                obj = this.readLiteralString(objNum, objGen, decrypter);
                continue;
            }
            if (c == 91) {
                obj = this.readArray(objNum, objGen, decrypter);
                continue;
            }
            if (c == 47) {
                obj = this.readName();
                continue;
            }
            if (c == 37) {
                this.readLine();
                continue;
            }
            if (c >= 48 && c <= 57 || c == 45 || c == 43 || c == 46) {
                obj = this.readNumber((char)c);
                if (numscan) continue;
                int startPos = this.buf.position();
                PDFObject testnum = this.readObject(-1, -1, true, decrypter);
                if (testnum != null && testnum.getType() == 2) {
                    PDFObject testR = this.readObject(-1, -1, true, decrypter);
                    if (testR != null && testR.getType() == 9 && testR.getStringValue().equals("R")) {
                        PDFXref xref = new PDFXref(obj.getIntValue(), testnum.getIntValue());
                        obj = new PDFObject(this, xref);
                        continue;
                    }
                    if (testR != null && testR.getType() == 9 && testR.getStringValue().equals("obj")) {
                        obj = this.readObjectDescription(obj.getIntValue(), testnum.getIntValue(), decrypter);
                        continue;
                    }
                    this.buf.position(startPos);
                    continue;
                }
                this.buf.position(startPos);
                continue;
            }
            if (c >= 97 && c <= 122 || c >= 65 && c <= 90) {
                obj = this.readKeyword((char)c);
                continue;
            }
            this.buf.position(this.buf.position() - 1);
            break;
        }
        return obj;
    }

    private boolean nextItemIs(String match) throws IOException {
        byte c;
        while (PDFFile.isWhiteSpace(c = this.buf.get())) {
        }
        for (int i = 0; i < match.length(); ++i) {
            if (i > 0) {
                c = this.buf.get();
            }
            if (c == match.charAt(i)) continue;
            return false;
        }
        return true;
    }

    private void processVersion(String versionString) {
        try {
            StringTokenizer tokens = new StringTokenizer(versionString, ".");
            this.majorVersion = Integer.parseInt(tokens.nextToken());
            this.minorVersion = Integer.parseInt(tokens.nextToken());
            this.versionString = versionString;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public String getVersionString() {
        return this.versionString;
    }

    private PDFObject readDictionary(int objNum, int objGen, PDFDecrypter decrypter) throws IOException {
        PDFObject name;
        HashMap<String, PDFObject> hm = new HashMap<String, PDFObject>();
        while ((name = this.readObject(objNum, objGen, decrypter)) != null) {
            if (name.getType() != 4) {
                throw new PDFParseException("First item in dictionary must be a /Name.  (Was " + name + ")");
            }
            PDFObject value = this.readObject(objNum, objGen, decrypter);
            if (value == null) continue;
            hm.put(name.getStringValue(), value);
        }
        if (!this.nextItemIs(">>")) {
            throw new PDFParseException("End of dictionary wasn't '>>'");
        }
        return new PDFObject(this, 6, hm);
    }

    private int readHexDigit() throws IOException {
        int a;
        while (PDFFile.isWhiteSpace(a = this.buf.get())) {
        }
        switch (a) {
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                a -= 48;
                break;
            }
            case 97: 
            case 98: 
            case 99: 
            case 100: 
            case 101: 
            case 102: {
                a -= 87;
                break;
            }
            case 65: 
            case 66: 
            case 67: 
            case 68: 
            case 69: 
            case 70: {
                a -= 55;
                break;
            }
            default: {
                a = -1;
            }
        }
        return a;
    }

    private int readHexPair() throws IOException {
        int first = this.readHexDigit();
        if (first < 0) {
            this.buf.position(this.buf.position() - 1);
            return -1;
        }
        int second = this.readHexDigit();
        if (second < 0) {
            this.buf.position(this.buf.position() - 1);
            return first << 4;
        }
        return (first << 4) + second;
    }

    private PDFObject readHexString(int objNum, int objGen, PDFDecrypter decrypter) throws IOException {
        int val;
        StringBuffer sb = new StringBuffer();
        while ((val = this.readHexPair()) >= 0) {
            sb.append((char)val);
        }
        if (this.buf.get() != 62) {
            throw new PDFParseException("Bad character in Hex String");
        }
        return new PDFObject(this, 3, decrypter.decryptString(objNum, objGen, sb.toString()));
    }

    private PDFObject readLiteralString(int objNum, int objGen, PDFDecrypter decrypter) throws IOException {
        int parencount = 1;
        StringBuffer sb = new StringBuffer();
        while (parencount > 0) {
            int c = this.buf.get() & 0xFF;
            if (c == 40) {
                ++parencount;
            } else if (c == 41) {
                if (--parencount == 0) {
                    c = -1;
                    break;
                }
            } else if (c == 92) {
                c = this.buf.get() & 0xFF;
                if (c >= 48 && c < 56) {
                    int val = 0;
                    for (int count = 0; c >= 48 && c < 56 && count < 3; ++count) {
                        val = val * 8 + c - 48;
                        c = this.buf.get() & 0xFF;
                    }
                    this.buf.position(this.buf.position() - 1);
                    c = val;
                } else if (c == 110) {
                    c = 10;
                } else if (c == 114) {
                    c = 13;
                } else if (c == 116) {
                    c = 9;
                } else if (c == 98) {
                    c = 8;
                } else if (c == 102) {
                    c = 12;
                } else if (c == 13) {
                    c = this.buf.get() & 0xFF;
                    if (c != 10) {
                        this.buf.position(this.buf.position() - 1);
                    }
                    c = -1;
                } else if (c == 10) {
                    c = -1;
                }
            }
            if (c < 0) continue;
            sb.append((char)c);
        }
        return new PDFObject(this, 3, decrypter.decryptString(objNum, objGen, sb.toString()));
    }

    private String readLine() {
        StringBuffer sb = new StringBuffer();
        while (this.buf.remaining() > 0) {
            char c = (char)this.buf.get();
            if (c == '\r') {
                char n;
                if (this.buf.remaining() <= 0 || (n = (char)this.buf.get(this.buf.position())) != '\n') break;
                this.buf.get();
                break;
            }
            if (c == '\n') break;
            sb.append(c);
        }
        return sb.toString();
    }

    private PDFObject readArray(int objNum, int objGen, PDFDecrypter decrypter) throws IOException {
        PDFObject obj;
        ArrayList<PDFObject> ary = new ArrayList<PDFObject>();
        while ((obj = this.readObject(objNum, objGen, decrypter)) != null) {
            ary.add(obj);
        }
        if (this.buf.get() != 93) {
            throw new PDFParseException("Array should end with ']'");
        }
        PDFObject[] objlist = new PDFObject[ary.size()];
        for (int i = 0; i < objlist.length; ++i) {
            objlist[i] = (PDFObject)ary.get(i);
        }
        return new PDFObject(this, 5, objlist);
    }

    private PDFObject readName() throws IOException {
        int c;
        StringBuffer sb = new StringBuffer();
        while (PDFFile.isRegularCharacter(c = this.buf.get()) && (c >= 33 || c <= 126)) {
            if (c == 35 && this.majorVersion != 1 && this.minorVersion != 1) {
                int hex = this.readHexPair();
                if (hex >= 0) {
                    c = hex;
                } else {
                    throw new PDFParseException("Bad #hex in /Name");
                }
            }
            sb.append((char)c);
        }
        this.buf.position(this.buf.position() - 1);
        return new PDFObject(this, 4, sb.toString());
    }

    private PDFObject readNumber(char start) throws IOException {
        double value;
        boolean neg = start == '-';
        boolean sawdot = start == '.';
        double dotmult = sawdot ? 0.1 : 1.0;
        double d = value = start >= '0' && start <= '9' ? (double)(start - 48) : 0.0;
        while (true) {
            byte c;
            if ((c = this.buf.get()) == 46) {
                if (sawdot) {
                    throw new PDFParseException("Can't have two '.' in a number");
                }
                sawdot = true;
                dotmult = 0.1;
                continue;
            }
            if (c < 48 || c > 57) break;
            int val = c - 48;
            if (sawdot) {
                value += (double)val * dotmult;
                dotmult *= 0.1;
                continue;
            }
            value = value * 10.0 + (double)val;
        }
        this.buf.position(this.buf.position() - 1);
        if (neg) {
            value = -value;
        }
        return new PDFObject(this, 2, new Double(value));
    }

    private PDFObject readKeyword(char start) throws IOException {
        byte c;
        StringBuffer sb = new StringBuffer(String.valueOf(start));
        while (PDFFile.isRegularCharacter(c = this.buf.get())) {
            sb.append((char)c);
        }
        this.buf.position(this.buf.position() - 1);
        return new PDFObject(this, 9, sb.toString());
    }

    private PDFObject readObjectDescription(int objNum, int objGen, PDFDecrypter decrypter) throws IOException {
        String endcheck;
        long debugpos = this.buf.position();
        PDFObject obj = this.readObject(objNum, objGen, decrypter);
        PDFObject endkey = this.readObject(objNum, objGen, decrypter);
        if (endkey.getType() != 9) {
            throw new PDFParseException("Expected 'stream' or 'endobj'");
        }
        if (obj.getType() == 6 && endkey.getStringValue().equals("stream")) {
            this.readLine();
            ByteBuffer data = this.readStream(obj);
            if (data == null) {
                data = ByteBuffer.allocate(0);
            }
            obj.setStream(data);
            endkey = this.readObject(objNum, objGen, decrypter);
        }
        if ((endcheck = endkey.getStringValue()) == null || !endcheck.equals("endobj")) {
            System.out.println("WARNING: object at " + debugpos + " didn't end with 'endobj'");
        }
        obj.setObjectId(objNum, objGen);
        return obj;
    }

    private ByteBuffer readStream(PDFObject dict) throws IOException {
        PDFObject lengthObj = dict.getDictRef("Length");
        int length = -1;
        if (lengthObj != null) {
            length = lengthObj.getIntValue();
        }
        if (length < 0) {
            throw new PDFParseException("Unknown length for stream");
        }
        int start = this.buf.position();
        ByteBuffer streamBuf = this.buf.slice();
        streamBuf.limit(length);
        this.buf.position(this.buf.position() + length);
        int ending = this.buf.position();
        if (!this.nextItemIs("endstream")) {
            System.out.println("read " + length + " chars from " + start + " to " + ending);
            throw new PDFParseException("Stream ended inappropriately");
        }
        return streamBuf;
    }

    private void readTrailer(PDFPassword password) throws IOException, PDFAuthenticationFailureException, EncryptionUnsupportedByProductException, EncryptionUnsupportedByPlatformException {
        this.objIdx = new PDFXref[50];
        int pos = this.buf.position();
        PDFDecrypter newDefaultDecrypter = null;
        while (true) {
            PDFObject prevloc;
            PDFObject xrefstmPos;
            PDFObject obj;
            if (!this.nextItemIs("xref")) {
                this.buf.position(pos);
                this.readTrailer15(password);
                return;
            }
            while ((obj = this.readObject(-1, -1, IdentityDecrypter.getInstance())).getType() != 9 || !obj.getStringValue().equals("trailer")) {
                if (obj.getType() != 2) {
                    throw new PDFParseException("Expected number for first xref entry");
                }
                int refstart = obj.getIntValue();
                obj = this.readObject(-1, -1, IdentityDecrypter.getInstance());
                if (obj.getType() != 2) {
                    throw new PDFParseException("Expected number for length of xref table");
                }
                int reflen = obj.getIntValue();
                this.readLine();
                if (refstart + reflen >= this.objIdx.length) {
                    PDFXref[] nobjIdx = new PDFXref[refstart + reflen];
                    System.arraycopy(this.objIdx, 0, nobjIdx, 0, this.objIdx.length);
                    this.objIdx = nobjIdx;
                }
                for (int refID = refstart; refID < refstart + reflen; ++refID) {
                    byte[] refline = new byte[20];
                    this.buf.get(refline);
                    if (this.objIdx[refID] != null) continue;
                    this.objIdx[refID] = refline[17] == 110 ? new PDFXref(refline) : new PDFXref(null);
                }
            }
            PDFObject trailerdict = this.readObject(-1, -1, IdentityDecrypter.getInstance());
            if (trailerdict.getType() != 6) {
                throw new IOException("Expected dictionary after \"trailer\"");
            }
            if (this.root == null) {
                this.root = trailerdict.getDictRef("Root");
                if (this.root != null) {
                    this.root.setObjectId(-1, -1);
                }
            }
            if (this.encrypt == null) {
                this.encrypt = trailerdict.getDictRef("Encrypt");
                if (this.encrypt != null) {
                    this.encrypt.setObjectId(-1, -1);
                }
                newDefaultDecrypter = PDFDecrypterFactory.createDecryptor(this.encrypt, trailerdict.getDictRef("ID"), password);
            }
            if (this.info == null) {
                this.info = trailerdict.getDictRef("Info");
                if (this.info != null) {
                    if (!this.info.isIndirect()) {
                        throw new PDFParseException("Info in trailer must be an indirect reference");
                    }
                    this.info.setObjectId(-1, -1);
                }
            }
            if ((xrefstmPos = trailerdict.getDictRef("XRefStm")) != null) {
                int pos14 = this.buf.position();
                this.buf.position(xrefstmPos.getIntValue());
                this.readTrailer15(password);
                this.buf.position(pos14);
            }
            if ((prevloc = trailerdict.getDictRef("Prev")) == null) break;
            this.buf.position(prevloc.getIntValue());
        }
        if (this.root == null) {
            throw new PDFParseException("No /Root key found in trailer dictionary");
        }
        if (this.encrypt != null) {
            PDFObject permissions = this.encrypt.getDictRef("P");
            if (permissions != null && !newDefaultDecrypter.isOwnerAuthorised()) {
                int perms;
                int n = perms = permissions != null ? permissions.getIntValue() : 0;
                if (permissions != null) {
                    this.printable = (perms & 4) != 0;
                    this.saveable = (perms & 0x10) != 0;
                }
            }
            this.defaultDecrypter = newDefaultDecrypter;
        }
        if (this.root.getDictRef("Version") != null) {
            this.processVersion(this.root.getDictRef("Version").getStringValue());
        }
        this.root.dereference();
    }

    private void readTrailer15(PDFPassword password) throws IOException, EncryptionUnsupportedByProductException, EncryptionUnsupportedByPlatformException {
        PDFObject xrefObj;
        PDFDecrypter newDefaultDecrypter = null;
        while ((xrefObj = this.readObject(-1, -1, IdentityDecrypter.getInstance())).getDictionary() != null && !xrefObj.getDictionary().isEmpty()) {
            PDFObject prevloc;
            int[] idxArray;
            PDFObject[] wNums = xrefObj.getDictionary().get("W").getArray();
            int l1 = wNums[0].getIntValue();
            int l2 = wNums[1].getIntValue();
            int l3 = wNums[2].getIntValue();
            int size = xrefObj.getDictionary().get("Size").getIntValue();
            byte[] strmbuf = xrefObj.getStream();
            int strmPos = 0;
            PDFObject idxNums = xrefObj.getDictionary().get("Index");
            if (idxNums == null) {
                idxArray = new int[]{0, size};
            } else {
                PDFObject[] idxNumArr = idxNums.getArray();
                idxArray = new int[idxNumArr.length];
                for (int i = 0; i < idxNumArr.length; ++i) {
                    idxArray[i] = idxNumArr[i].getIntValue();
                }
            }
            int idxLen = idxArray.length;
            int idxPos = 0;
            while (idxPos < idxLen) {
                int reflen;
                int refstart;
                if ((refstart = idxArray[idxPos++]) + (reflen = idxArray[idxPos++]) >= this.objIdx.length) {
                    PDFXref[] nobjIdx = new PDFXref[refstart + reflen];
                    System.arraycopy(this.objIdx, 0, nobjIdx, 0, this.objIdx.length);
                    this.objIdx = nobjIdx;
                }
                for (int refID = refstart; refID < refstart + reflen; ++refID) {
                    int type = this.readNum(strmbuf, strmPos, l1);
                    int id = this.readNum(strmbuf, strmPos += l1, l2);
                    int gen = this.readNum(strmbuf, strmPos += l2, l3);
                    strmPos += l3;
                    if (this.objIdx[refID] != null) continue;
                    this.objIdx[refID] = type == 0 ? new PDFXref(null) : (type == 1 ? new PDFXref(id, gen) : new PDFXref(id, gen, true));
                }
            }
            HashMap<String, PDFObject> trailerdict = xrefObj.getDictionary();
            if (this.root == null) {
                this.root = trailerdict.get("Root");
                if (this.root != null) {
                    this.root.setObjectId(-1, -1);
                }
            }
            if (this.encrypt == null) {
                this.encrypt = trailerdict.get("Encrypt");
                if (this.encrypt != null) {
                    this.encrypt.setObjectId(-1, -1);
                }
                newDefaultDecrypter = PDFDecrypterFactory.createDecryptor(this.encrypt, trailerdict.get("ID"), password);
            }
            if (this.info == null) {
                this.info = trailerdict.get("Info");
                if (this.info != null) {
                    if (!this.info.isIndirect()) {
                        throw new PDFParseException("Info in trailer must be an indirect reference");
                    }
                    this.info.setObjectId(-1, -1);
                }
            }
            if ((prevloc = trailerdict.get("Prev")) == null) break;
            this.buf.position(prevloc.getIntValue());
            if (this.root.getDictRef("Version") == null) continue;
            this.processVersion(this.root.getDictRef("Version").getStringValue());
        }
        if (this.root == null) {
            throw new PDFParseException("No /Root key found in trailer dictionary");
        }
        if (this.encrypt != null) {
            PDFObject permissions = this.encrypt.getDictRef("P");
            if (permissions != null) {
                int perms;
                int n = perms = permissions != null ? permissions.getIntValue() : 0;
                if (permissions != null) {
                    this.printable = (perms & 4) != 0;
                    boolean bl = this.saveable = (perms & 0x10) != 0;
                }
            }
            if (newDefaultDecrypter != null && !newDefaultDecrypter.isOwnerAuthorised()) {
                this.defaultDecrypter = newDefaultDecrypter;
            }
        }
        this.root.dereference();
    }

    private int readNum(byte[] sbuf, int pos, int numBytes) {
        int result = 0;
        for (int i = 0; i < numBytes; ++i) {
            result = (result << 8) + (sbuf[pos + i] & 0xFF);
        }
        return result;
    }

    private void parseFile(PDFPassword password) throws IOException {
        String scans;
        int scanPos;
        this.buf.rewind();
        String versionLine = this.readLine();
        if (versionLine.startsWith(VERSION_COMMENT)) {
            this.processVersion(versionLine.substring(VERSION_COMMENT.length()));
        }
        this.buf.rewind();
        byte[] scan = new byte[32];
        int loc = 0;
        for (scanPos = this.buf.remaining() - scan.length; scanPos >= 0; scanPos -= scan.length - 10) {
            this.buf.position(scanPos);
            this.buf.get(scan);
            scans = new String(scan);
            loc = scans.indexOf("startxref");
            if (loc <= 0) continue;
            if (scanPos + loc + scan.length > this.buf.limit()) break;
            scanPos += loc;
            loc = 0;
            break;
        }
        if (scanPos < 0) {
            throw new IOException("This may not be a PDF File");
        }
        this.buf.position(scanPos);
        this.buf.get(scan);
        scans = new String(scan);
        if (scans.charAt(loc += 10) < ' ') {
            ++loc;
        }
        while (scans.charAt(loc) == ' ') {
            ++loc;
        }
        int numstart = loc;
        while (loc < scans.length() && scans.charAt(loc) >= '0' && scans.charAt(loc) <= '9') {
            ++loc;
        }
        int xrefpos = Integer.parseInt(scans.substring(numstart, loc));
        this.buf.position(xrefpos);
        try {
            this.readTrailer(password);
        }
        catch (UnsupportedEncryptionException e) {
            throw new PDFParseException(e.getMessage(), e);
        }
    }

    public OutlineNode getOutline() throws IOException {
        PDFObject oroot = this.root.getDictRef("Outlines");
        OutlineNode work = null;
        OutlineNode outline = null;
        if (oroot != null) {
            PDFObject scan = oroot.getDictRef("First");
            outline = work = new OutlineNode("<top>");
            while (scan != null) {
                PDFObject kid;
                String title = scan.getDictRef("Title").getTextStringValue();
                OutlineNode build = new OutlineNode(title);
                work.add(build);
                PDFAction action = null;
                PDFObject actionObj = scan.getDictRef("A");
                if (actionObj != null) {
                    action = PDFAction.getAction(actionObj, this.getRoot());
                } else {
                    PDFObject destObj = scan.getDictRef("Dest");
                    if (destObj != null) {
                        try {
                            PDFDestination dest = PDFDestination.getDestination(destObj, this.getRoot());
                            action = new GoToAction(dest);
                        }
                        catch (IOException dest) {
                            // empty catch block
                        }
                    }
                }
                if (action != null) {
                    build.setAction(action);
                }
                if ((kid = scan.getDictRef("First")) != null) {
                    work = build;
                    scan = kid;
                    continue;
                }
                PDFObject next = scan.getDictRef("Next");
                while (next == null) {
                    scan = scan.getDictRef("Parent");
                    next = scan.getDictRef("Next");
                    if ((work = (OutlineNode)work.getParent()) != null) continue;
                }
                scan = next;
            }
        }
        return outline;
    }

    public int getPageNumber(PDFObject page) throws IOException {
        PDFObject parent;
        PDFObject typeObj;
        if (page.getType() == 5) {
            page = page.getAt(0);
        }
        if ((typeObj = page.getDictRef("Type")) == null || !typeObj.getStringValue().equals("Page")) {
            return 0;
        }
        int count = 0;
        while ((parent = page.getDictRef("Parent")) != null) {
            PDFObject[] kids = parent.getDictRef("Kids").getArray();
            for (int i = 0; i < kids.length && !kids[i].equals(page); ++i) {
                PDFObject kcount = kids[i].getDictRef("Count");
                if (kcount != null) {
                    count += kcount.getIntValue();
                    continue;
                }
                ++count;
            }
            page = parent;
        }
        return count;
    }

    public PDFPage getPage(int pagenum) {
        return this.getPage(pagenum, false);
    }

    public PDFPage getPage(int pagenum, boolean wait) {
        Integer key = new Integer(pagenum);
        HashMap<String, PDFObject> resources = null;
        PDFObject pageObj = null;
        boolean needread = false;
        PDFPage page = this.cache.getPage(key);
        PDFParser parser = this.cache.getPageParser(key);
        if (page == null) {
            try {
                resources = new HashMap<String, PDFObject>();
                PDFObject topPagesObj = this.root.getDictRef("Pages");
                pageObj = this.findPage(topPagesObj, 0, pagenum, resources);
                if (pageObj == null) {
                    return null;
                }
                page = this.createPage(pagenum, pageObj);
                byte[] stream = this.getContents(pageObj);
                parser = new PDFParser(page, stream, resources);
                this.cache.addPage(key, page, parser);
            }
            catch (IOException ioe) {
                System.out.println("GetPage inner loop:");
                ioe.printStackTrace();
                return null;
            }
        }
        if (parser != null && !parser.isFinished()) {
            parser.go(wait);
        }
        return page;
    }

    public void flushPage(int pageNum) {
        this.cache.removePage(new Integer(pageNum));
    }

    public void stop(int pageNum) {
        PDFParser parser = this.cache.getPageParser(new Integer(pageNum));
        if (parser != null) {
            parser.stop();
        }
    }

    private byte[] getContents(PDFObject pageObj) throws IOException {
        PDFObject contentsObj = pageObj.getDictRef("Contents");
        if (contentsObj == null) {
            throw new IOException("No page contents!");
        }
        PDFObject[] contents = contentsObj.getArray();
        if (contents.length == 1) {
            return contents[0].getStream();
        }
        int len = 0;
        for (int i = 0; i < contents.length; ++i) {
            byte[] data = contents[i].getStream();
            if (data == null) {
                throw new PDFParseException("No stream on content " + i + ": " + contents[i]);
            }
            len += data.length;
        }
        byte[] stream = new byte[len];
        len = 0;
        for (int i = 0; i < contents.length; ++i) {
            byte[] data = contents[i].getStream();
            System.arraycopy(data, 0, stream, len, data.length);
            len += data.length;
        }
        return stream;
    }

    private PDFPage createPage(int pagenum, PDFObject pageObj) throws IOException {
        PDFObject rotateObj;
        PDFObject cropboxObj;
        int rotation = 0;
        Rectangle2D.Float mediabox = null;
        Rectangle2D.Float cropbox = null;
        PDFObject mediaboxObj = this.getInheritedValue(pageObj, "MediaBox");
        if (mediaboxObj != null) {
            mediabox = this.parseRect(mediaboxObj);
        }
        if ((cropboxObj = this.getInheritedValue(pageObj, "CropBox")) != null) {
            cropbox = this.parseRect(cropboxObj);
        }
        if ((rotateObj = this.getInheritedValue(pageObj, "Rotate")) != null) {
            rotation = rotateObj.getIntValue();
        }
        PDFObject annots = this.getInheritedValue(pageObj, "Annots");
        ArrayList<PDFAnnotation> annotationList = new ArrayList<PDFAnnotation>();
        if (annots != null) {
            PDFObject[] array;
            if (annots.getType() != 5) {
                throw new PDFParseException("Can't parse annotations: " + annots.toString());
            }
            for (PDFObject object : array = annots.getArray()) {
                try {
                    PDFAnnotation pdfAnnot = PDFAnnotation.createAnnotation(object);
                    annotationList.add(pdfAnnot);
                }
                catch (PDFParseException e) {
                    e.printStackTrace();
                }
            }
        }
        Rectangle2D.Float bbox = cropbox == null ? mediabox : cropbox;
        PDFPage page = new PDFPage(pagenum, bbox, rotation, this.cache);
        page.setAnnots(annotationList);
        return page;
    }

    private PDFObject findPage(PDFObject pagedict, int start, int getPage, Map<String, PDFObject> resources) throws IOException {
        PDFObject typeObj;
        PDFObject rsrcObj = pagedict.getDictRef("Resources");
        if (rsrcObj != null) {
            resources.putAll(rsrcObj.getDictionary());
        }
        if ((typeObj = pagedict.getDictRef("Type")) != null && typeObj.getStringValue().equals("Page")) {
            return pagedict;
        }
        PDFObject kidsObj = pagedict.getDictRef("Kids");
        if (kidsObj != null) {
            PDFObject[] kids = kidsObj.getArray();
            for (int i = 0; i < kids.length; ++i) {
                int count = 1;
                PDFObject countItem = kids[i].getDictRef("Count");
                if (countItem != null) {
                    count = countItem.getIntValue();
                }
                if (start + count >= getPage) {
                    return this.findPage(kids[i], start, getPage, resources);
                }
                start += count;
            }
        }
        return null;
    }

    private PDFObject getInheritedValue(PDFObject pageObj, String propName) throws IOException {
        PDFObject propObj = pageObj.getDictRef(propName);
        if (propObj != null) {
            return propObj;
        }
        PDFObject parentObj = pageObj.getDictRef("Parent");
        if (parentObj != null) {
            return this.getInheritedValue(parentObj, propName);
        }
        return null;
    }

    public Rectangle2D.Float parseRect(PDFObject obj) throws IOException {
        if (obj.getType() == 5) {
            PDFObject[] bounds = obj.getArray();
            if (bounds.length == 4) {
                return new Rectangle2D.Float(bounds[0].getFloatValue(), bounds[1].getFloatValue(), bounds[2].getFloatValue() - bounds[0].getFloatValue(), bounds[3].getFloatValue() - bounds[1].getFloatValue());
            }
            throw new PDFParseException("Rectangle definition didn't have 4 elements");
        }
        throw new PDFParseException("Rectangle definition not an array");
    }

    public PDFDecrypter getDefaultDecrypter() {
        return this.defaultDecrypter;
    }
}


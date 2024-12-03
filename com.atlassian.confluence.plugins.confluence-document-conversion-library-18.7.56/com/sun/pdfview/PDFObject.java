/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.PDFParser;
import com.sun.pdfview.PDFStringUtil;
import com.sun.pdfview.PDFXref;
import com.sun.pdfview.decode.PDFDecoder;
import com.sun.pdfview.decrypt.IdentityDecrypter;
import com.sun.pdfview.decrypt.PDFDecrypter;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PDFObject {
    public static final int INDIRECT = 0;
    public static final int BOOLEAN = 1;
    public static final int NUMBER = 2;
    public static final int STRING = 3;
    public static final int NAME = 4;
    public static final int ARRAY = 5;
    public static final int DICTIONARY = 6;
    public static final int STREAM = 7;
    public static final int NULL = 8;
    public static final int KEYWORD = 9;
    public static final int OBJ_NUM_EMBEDDED = -2;
    public static final int OBJ_NUM_TRAILER = -1;
    public static final PDFObject nullObj = new PDFObject(null, 8, null);
    private int type;
    private Object value;
    private ByteBuffer stream;
    private SoftReference decodedStream;
    private PDFFile owner;
    private SoftReference cache;
    private int objNum = -2;
    private int objGen = -2;

    public PDFObject(PDFFile owner, int type, Object value) {
        this.type = type;
        if (type == 4) {
            value = ((String)value).intern();
        } else if (type == 9 && value.equals("true")) {
            this.type = 1;
            value = Boolean.TRUE;
        } else if (type == 9 && value.equals("false")) {
            this.type = 1;
            value = Boolean.FALSE;
        }
        this.value = value;
        this.owner = owner;
    }

    public PDFObject(Object obj) throws PDFParseException {
        this.owner = null;
        this.value = obj;
        if (obj instanceof Double || obj instanceof Integer) {
            this.type = 2;
        } else if (obj instanceof String) {
            this.type = 4;
        } else if (obj instanceof PDFObject[]) {
            this.type = 5;
        } else if (obj instanceof Object[]) {
            Object[] srcary = (Object[])obj;
            PDFObject[] dstary = new PDFObject[srcary.length];
            for (int i = 0; i < srcary.length; ++i) {
                dstary[i] = new PDFObject(srcary[i]);
            }
            this.value = dstary;
            this.type = 5;
        } else if (obj instanceof HashMap) {
            this.type = 6;
        } else if (obj instanceof Boolean) {
            this.type = 1;
        } else if (obj instanceof PDFParser.Tok) {
            PDFParser.Tok tok = (PDFParser.Tok)obj;
            if (tok.name.equals("true")) {
                this.value = Boolean.TRUE;
                this.type = 1;
            } else if (tok.name.equals("false")) {
                this.value = Boolean.FALSE;
                this.type = 1;
            } else {
                this.value = tok.name;
                this.type = 4;
            }
        } else {
            throw new PDFParseException("Bad type for raw PDFObject: " + obj);
        }
    }

    public PDFObject(PDFFile owner, PDFXref xref) {
        this.type = 0;
        this.value = xref;
        this.owner = owner;
    }

    public int getType() throws IOException {
        if (this.type == 0) {
            return this.dereference().getType();
        }
        return this.type;
    }

    public void setStream(ByteBuffer data) {
        this.type = 7;
        this.stream = data;
    }

    public Object getCache() throws IOException {
        if (this.type == 0) {
            return this.dereference().getCache();
        }
        if (this.cache != null) {
            return this.cache.get();
        }
        return null;
    }

    public void setCache(Object obj) throws IOException {
        if (this.type == 0) {
            this.dereference().setCache(obj);
            return;
        }
        this.cache = new SoftReference<Object>(obj);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getStream() throws IOException {
        if (this.type == 0) {
            return this.dereference().getStream();
        }
        if (this.type == 7 && this.stream != null) {
            byte[] data = null;
            ByteBuffer byteBuffer = this.stream;
            synchronized (byteBuffer) {
                byte[] ary;
                ByteBuffer streamBuf = this.decodeStream();
                if (streamBuf.hasArray() && streamBuf.arrayOffset() == 0 && (ary = streamBuf.array()).length == streamBuf.remaining()) {
                    return ary;
                }
                data = new byte[streamBuf.remaining()];
                streamBuf.get(data);
                streamBuf.flip();
            }
            return data;
        }
        if (this.type == 3) {
            return PDFStringUtil.asBytes(this.getStringValue());
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ByteBuffer getStreamBuffer() throws IOException {
        if (this.type == 0) {
            return this.dereference().getStreamBuffer();
        }
        if (this.type == 7 && this.stream != null) {
            ByteBuffer byteBuffer = this.stream;
            synchronized (byteBuffer) {
                ByteBuffer streamBuf = this.decodeStream();
                return streamBuf.duplicate();
            }
        }
        if (this.type == 3) {
            String src = this.getStringValue();
            return ByteBuffer.wrap(src.getBytes());
        }
        return null;
    }

    private ByteBuffer decodeStream() throws IOException {
        ByteBuffer outStream = null;
        if (this.decodedStream != null) {
            outStream = (ByteBuffer)this.decodedStream.get();
        }
        if (outStream == null) {
            this.stream.rewind();
            outStream = PDFDecoder.decodeStream(this, this.stream);
            this.decodedStream = new SoftReference<ByteBuffer>(outStream);
        }
        return outStream;
    }

    public int getIntValue() throws IOException {
        if (this.type == 0) {
            return this.dereference().getIntValue();
        }
        if (this.type == 2) {
            return ((Double)this.value).intValue();
        }
        return 0;
    }

    public float getFloatValue() throws IOException {
        if (this.type == 0) {
            return this.dereference().getFloatValue();
        }
        if (this.type == 2) {
            return ((Double)this.value).floatValue();
        }
        return 0.0f;
    }

    public double getDoubleValue() throws IOException {
        if (this.type == 0) {
            return this.dereference().getDoubleValue();
        }
        if (this.type == 2) {
            return (Double)this.value;
        }
        return 0.0;
    }

    public PDFObject getRoot() {
        return this.owner.getRoot();
    }

    public String getStringValue() throws IOException {
        if (this.type == 0) {
            return this.dereference().getStringValue();
        }
        if (this.type == 3 || this.type == 4 || this.type == 9) {
            return (String)this.value;
        }
        return null;
    }

    public String getTextStringValue() throws IOException {
        return PDFStringUtil.asTextString(this.getStringValue());
    }

    public PDFObject[] getArray() throws IOException {
        if (this.type == 0) {
            return this.dereference().getArray();
        }
        if (this.type == 5) {
            PDFObject[] ary = (PDFObject[])this.value;
            return ary;
        }
        PDFObject[] ary = new PDFObject[]{this};
        return ary;
    }

    public boolean getBooleanValue() throws IOException {
        if (this.type == 0) {
            return this.dereference().getBooleanValue();
        }
        if (this.type == 1) {
            return this.value == Boolean.TRUE;
        }
        return false;
    }

    public PDFObject getAt(int idx) throws IOException {
        if (this.type == 0) {
            return this.dereference().getAt(idx);
        }
        if (this.type == 5) {
            PDFObject[] ary = (PDFObject[])this.value;
            return ary[idx];
        }
        return null;
    }

    public Iterator getDictKeys() throws IOException {
        if (this.type == 0) {
            return this.dereference().getDictKeys();
        }
        if (this.type == 6 || this.type == 7) {
            return ((HashMap)this.value).keySet().iterator();
        }
        return new ArrayList().iterator();
    }

    public HashMap<String, PDFObject> getDictionary() throws IOException {
        if (this.type == 0) {
            return this.dereference().getDictionary();
        }
        if (this.type == 6 || this.type == 7) {
            return (HashMap)this.value;
        }
        return new HashMap<String, PDFObject>();
    }

    public PDFObject getDictRef(String key) throws IOException {
        if (this.type == 0) {
            return this.dereference().getDictRef(key);
        }
        if (this.type == 6 || this.type == 7) {
            key = key.intern();
            HashMap h = (HashMap)this.value;
            PDFObject obj = (PDFObject)h.get(key.intern());
            return obj;
        }
        return null;
    }

    public boolean isDictType(String match) throws IOException {
        if (this.type == 0) {
            return this.dereference().isDictType(match);
        }
        if (this.type != 6 && this.type != 7) {
            return false;
        }
        PDFObject obj = this.getDictRef("Type");
        return obj != null && obj.getStringValue().equals(match);
    }

    public PDFDecrypter getDecrypter() {
        return this.owner != null ? this.owner.getDefaultDecrypter() : IdentityDecrypter.getInstance();
    }

    public void setObjectId(int objNum, int objGen) {
        assert (objNum >= -1);
        assert (objGen >= -1);
        this.objNum = objNum;
        this.objGen = objGen;
    }

    public int getObjNum() {
        return this.objNum;
    }

    public int getObjGen() {
        return this.objGen;
    }

    public String toString() {
        try {
            if (this.type == 0) {
                StringBuffer str = new StringBuffer();
                str.append("Indirect to #" + ((PDFXref)this.value).getID() + (((PDFXref)this.value).getCompressed() ? " comp" : ""));
                try {
                    PDFObject obj = this.cachedDereference();
                    str.append("\n" + (obj == null ? "<ref>" : obj.toString()));
                }
                catch (Throwable t) {
                    str.append(t.toString());
                }
                return str.toString();
            }
            if (this.type == 1) {
                return "Boolean: " + (this.getBooleanValue() ? "true" : "false");
            }
            if (this.type == 2) {
                return "Number: " + this.getDoubleValue();
            }
            if (this.type == 3) {
                return "String: " + this.getStringValue();
            }
            if (this.type == 4) {
                return "Name: /" + this.getStringValue();
            }
            if (this.type == 5) {
                return "Array, length=" + ((PDFObject[])this.value).length;
            }
            if (this.type == 6) {
                StringBuffer sb = new StringBuffer();
                PDFObject obj = this.getDictRef("Type");
                if (obj != null) {
                    sb.append(obj.getStringValue());
                    obj = this.getDictRef("Subtype");
                    if (obj == null) {
                        obj = this.getDictRef("S");
                    }
                    if (obj != null) {
                        sb.append("/" + obj.getStringValue());
                    }
                } else {
                    sb.append("Untyped");
                }
                sb.append(" dictionary. Keys:");
                HashMap hm = (HashMap)this.value;
                for (Map.Entry entry : hm.entrySet()) {
                    sb.append("\n   " + entry.getKey() + "  " + entry.getValue());
                }
                return sb.toString();
            }
            if (this.type == 7) {
                byte[] st = this.getStream();
                if (st == null) {
                    return "Broken stream";
                }
                return "Stream: [[" + new String(st, 0, st.length > 30 ? 30 : st.length) + "]]";
            }
            if (this.type == 8) {
                return "Null";
            }
            if (this.type == 9) {
                return "Keyword: " + this.getStringValue();
            }
            return "Whoops!  big error!  Unknown type";
        }
        catch (IOException ioe) {
            return "Caught an error: " + ioe;
        }
    }

    public PDFObject dereference() throws IOException {
        if (this.type == 0) {
            PDFObject obj = null;
            if (this.cache != null) {
                obj = (PDFObject)this.cache.get();
            }
            if (obj == null || obj.value == null) {
                if (this.owner == null) {
                    System.out.println("Bad seed (owner==null)!  Object=" + this);
                }
                obj = this.owner.dereference((PDFXref)this.value, this.getDecrypter());
                this.cache = new SoftReference<PDFObject>(obj);
            }
            return obj;
        }
        return this;
    }

    public PDFObject cachedDereference() throws IOException {
        if (this.type == 0) {
            PDFObject obj = null;
            if (this.cache != null) {
                obj = (PDFObject)this.cache.get();
            }
            if ((obj == null || obj.value == null) && this.owner == null) {
                System.out.println("Bad seed (owner==null)!  Object=" + this);
            }
            return obj;
        }
        return this;
    }

    public boolean isIndirect() {
        return this.type == 0;
    }

    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        }
        if (this.type == 0 && o instanceof PDFObject) {
            PDFObject obj = (PDFObject)o;
            if (obj.type == 0) {
                PDFXref lXref = (PDFXref)this.value;
                PDFXref rXref = (PDFXref)obj.value;
                return lXref.getID() == rXref.getID() && lXref.getGeneration() == rXref.getGeneration() && lXref.getCompressed() == rXref.getCompressed();
            }
        }
        return false;
    }
}


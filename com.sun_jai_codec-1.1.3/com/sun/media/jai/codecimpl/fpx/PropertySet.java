/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.fpx;

import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.fpx.JaiI18N;
import com.sun.media.jai.codecimpl.fpx.Property;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

class PropertySet {
    private static final int TYPE_VT_EMPTY = -1;
    private static final int TYPE_VT_NULL = -1;
    private static final int TYPE_VT_I2 = 2;
    private static final int TYPE_VT_I4 = 3;
    private static final int TYPE_VT_R4 = -1;
    private static final int TYPE_VT_R8 = -1;
    private static final int TYPE_VT_CY = -1;
    private static final int TYPE_VT_DATE = -1;
    private static final int TYPE_VT_BSTR = -1;
    private static final int TYPE_VT_ERROR = -1;
    private static final int TYPE_VT_BOOL = -1;
    private static final int TYPE_VT_VARIANT = -1;
    private static final int TYPE_VT_UI1 = -1;
    private static final int TYPE_VT_UI2 = -1;
    private static final int TYPE_VT_UI4 = 19;
    private static final int TYPE_VT_I8 = -1;
    private static final int TYPE_VT_UI8 = -1;
    private static final int TYPE_VT_LPSTR = 30;
    private static final int TYPE_VT_LPWSTR = 31;
    private static final int TYPE_VT_FILETIME = 64;
    private static final int TYPE_VT_BLOB = 65;
    private static final int TYPE_VT_STREAM = -1;
    private static final int TYPE_VT_STORAGE = -1;
    private static final int TYPE_VT_STREAMED_OBJECT = -1;
    private static final int TYPE_VT_STORED_OBJECT = -1;
    private static final int TYPE_VT_BLOB_OBJECT = -1;
    private static final int TYPE_VT_CF = 71;
    private static final int TYPE_VT_CLSID = 72;
    private static final int TYPE_VT_VECTOR = 4096;
    SeekableStream stream;
    Hashtable properties = new Hashtable();

    public PropertySet(SeekableStream stream) throws IOException {
        this.stream = stream;
        stream.seek(44L);
        int sectionOffset = stream.readIntLE();
        stream.seek(sectionOffset);
        int sectionSize = stream.readIntLE();
        int sectionCount = stream.readIntLE();
        for (int i = 0; i < sectionCount; ++i) {
            stream.seek(sectionOffset + 8 * i + 8);
            int pid = stream.readIntLE();
            int offset = stream.readIntLE();
            stream.seek(sectionOffset + offset);
            int type = stream.readIntLE();
            Property p = new Property(type, sectionOffset + offset + 4);
            this.properties.put(new Integer(pid), p);
        }
    }

    public boolean hasProperty(int id) {
        Property p = (Property)this.properties.get(new Integer(id));
        return p != null;
    }

    public int getI4(int id) {
        Property p = (Property)this.properties.get(new Integer(id));
        try {
            int offset = p.getOffset();
            this.stream.seek(offset);
            return this.stream.readIntLE();
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PropertySet1"), e, this, false);
            return -1;
        }
    }

    public int getUI1(int id) {
        Property p = (Property)this.properties.get(new Integer(id));
        try {
            int offset = p.getOffset();
            this.stream.seek(offset);
            return this.stream.readUnsignedByte();
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PropertySet1"), e, this, false);
            return -1;
        }
    }

    public int getUI2(int id) {
        Property p = (Property)this.properties.get(new Integer(id));
        try {
            int offset = p.getOffset();
            this.stream.seek(offset);
            return this.stream.readUnsignedShortLE();
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PropertySet2"), e, this, false);
            return -1;
        }
    }

    public long getUI4(int id) {
        Property p = (Property)this.properties.get(new Integer(id));
        try {
            int offset = p.getOffset();
            this.stream.seek(offset);
            return this.stream.readUnsignedIntLE();
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PropertySet4"), e, this, false);
            return -1L;
        }
    }

    public long getUI4(int id, long defaultValue) {
        Property p = (Property)this.properties.get(new Integer(id));
        if (p == null) {
            return defaultValue;
        }
        try {
            int offset = p.getOffset();
            this.stream.seek(offset);
            return this.stream.readUnsignedIntLE();
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PropertySet4"), e, this, false);
            return -1L;
        }
    }

    public String getLPSTR(int id) {
        Property p = (Property)this.properties.get(new Integer(id));
        if (p == null) {
            return null;
        }
        try {
            int offset = p.getOffset();
            this.stream.seek(offset);
            int length = this.stream.readIntLE();
            StringBuffer sb = new StringBuffer(length);
            for (int i = 0; i < length; ++i) {
                sb.append((char)this.stream.read());
            }
            return sb.toString();
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PropertySet5"), e, this, false);
            return null;
        }
    }

    public String getLPWSTR(int id) {
        Property p = (Property)this.properties.get(new Integer(id));
        try {
            int offset = p.getOffset();
            this.stream.seek(offset);
            int length = this.stream.readIntLE();
            StringBuffer sb = new StringBuffer(length);
            for (int i = 0; i < length; ++i) {
                sb.append(this.stream.readCharLE());
            }
            return sb.toString();
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PropertySet5"), e, this, false);
            return null;
        }
    }

    public float getR4(int id) {
        Property p = (Property)this.properties.get(new Integer(id));
        try {
            int offset = p.getOffset();
            this.stream.seek(offset);
            return this.stream.readFloatLE();
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PropertySet6"), e, this, false);
            return -1.0f;
        }
    }

    public Date getDate(int id) {
        throw new RuntimeException(JaiI18N.getString("PropertySet0"));
    }

    public Date getFiletime(int id) {
        throw new RuntimeException(JaiI18N.getString("PropertySet0"));
    }

    public byte[] getBlob(int id) {
        Property p = (Property)this.properties.get(new Integer(id));
        try {
            int offset = p.getOffset();
            this.stream.seek(offset);
            int length = this.stream.readIntLE();
            byte[] buf = new byte[length];
            this.stream.seek(offset + 4);
            this.stream.readFully(buf);
            return buf;
        }
        catch (IOException e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PropertySet7"), e, this, false);
            return null;
        }
    }

    public int[] getUI1Vector(int id) {
        throw new RuntimeException(JaiI18N.getString("PropertySet0"));
    }

    public int[] getUI2Vector(int id) {
        throw new RuntimeException(JaiI18N.getString("PropertySet0"));
    }

    public long[] getUI4Vector(int id) {
        throw new RuntimeException(JaiI18N.getString("PropertySet0"));
    }

    public float[] getR4Vector(int id) {
        throw new RuntimeException(JaiI18N.getString("PropertySet0"));
    }

    public String[] getLPWSTRVector(int id) {
        throw new RuntimeException(JaiI18N.getString("PropertySet0"));
    }
}


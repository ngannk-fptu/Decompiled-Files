/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.hmef.attribute;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.hmef.CompressedRTF;
import org.apache.poi.hmef.attribute.MAPIAttribute;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.StringUtil;

public final class MAPIRtfAttribute
extends MAPIAttribute {
    private static final int MAX_RECORD_LENGTH = 50000000;
    private final byte[] decompressed;
    private final String data;

    public MAPIRtfAttribute(MAPIProperty property, int type, byte[] data) throws IOException {
        super(property, type, data);
        byte[] tmp;
        CompressedRTF rtf = new CompressedRTF();
        try (UnsynchronizedByteArrayInputStream is = new UnsynchronizedByteArrayInputStream(data);){
            tmp = rtf.decompress((InputStream)is);
        }
        this.decompressed = tmp.length > rtf.getDeCompressedSize() ? IOUtils.safelyClone(tmp, 0, rtf.getDeCompressedSize(), 50000000) : tmp;
        this.data = StringUtil.getFromCompressedUnicode(this.decompressed, 0, this.decompressed.length);
    }

    public byte[] getRawData() {
        return super.getData();
    }

    @Override
    public byte[] getData() {
        return this.decompressed;
    }

    public String getDataString() {
        return this.data;
    }

    @Override
    public String toString() {
        return this.getProperty() + " " + this.data;
    }
}


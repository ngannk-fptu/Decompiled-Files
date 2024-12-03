/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.ByteArrayOutputStream
 */
package org.apache.poi.hslf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hslf.record.ExOleObjStg;
import org.apache.poi.sl.usermodel.ObjectData;

public class HSLFObjectData
implements ObjectData,
GenericRecord {
    private final ExOleObjStg storage;

    public HSLFObjectData(ExOleObjStg storage) {
        this.storage = storage;
    }

    @Override
    public InputStream getInputStream() {
        return this.storage.getData();
    }

    @Override
    public OutputStream getOutputStream() {
        return new ByteArrayOutputStream(){

            public void close() throws IOException {
                HSLFObjectData.this.setData(HSLFObjectData.this.getBytes());
            }
        };
    }

    public void setData(byte[] data) throws IOException {
        this.storage.setData(data);
    }

    public ExOleObjStg getExOleObjStg() {
        return this.storage;
    }

    @Override
    public String getOLE2ClassName() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }

    @Override
    public List<? extends GenericRecord> getGenericChildren() {
        return Collections.singletonList(this.getExOleObjStg());
    }
}


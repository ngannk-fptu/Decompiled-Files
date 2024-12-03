/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.cci.Record
 *  javax.resource.cci.Streamable
 *  org.springframework.util.FileCopyUtils
 */
package org.springframework.jca.cci.core.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.resource.cci.Record;
import javax.resource.cci.Streamable;
import org.springframework.util.FileCopyUtils;

@Deprecated
public class CommAreaRecord
implements Record,
Streamable {
    private byte[] bytes = new byte[0];
    private String recordName = "";
    private String recordShortDescription = "";

    public CommAreaRecord() {
    }

    public CommAreaRecord(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getRecordName() {
        return this.recordName;
    }

    public void setRecordShortDescription(String recordShortDescription) {
        this.recordShortDescription = recordShortDescription;
    }

    public String getRecordShortDescription() {
        return this.recordShortDescription;
    }

    public void read(InputStream in) throws IOException {
        this.bytes = FileCopyUtils.copyToByteArray((InputStream)in);
    }

    public void write(OutputStream out) throws IOException {
        out.write(this.bytes);
        out.flush();
    }

    public byte[] toByteArray() {
        return this.bytes;
    }

    public Object clone() {
        return new CommAreaRecord(this.bytes);
    }
}


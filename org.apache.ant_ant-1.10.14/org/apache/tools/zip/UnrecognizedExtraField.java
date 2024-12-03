/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import org.apache.tools.zip.CentralDirectoryParsingZipExtraField;
import org.apache.tools.zip.ZipShort;
import org.apache.tools.zip.ZipUtil;

public class UnrecognizedExtraField
implements CentralDirectoryParsingZipExtraField {
    private ZipShort headerId;
    private byte[] localData;
    private byte[] centralData;

    public void setHeaderId(ZipShort headerId) {
        this.headerId = headerId;
    }

    @Override
    public ZipShort getHeaderId() {
        return this.headerId;
    }

    public void setLocalFileDataData(byte[] data) {
        this.localData = ZipUtil.copy(data);
    }

    @Override
    public ZipShort getLocalFileDataLength() {
        return new ZipShort(this.localData.length);
    }

    @Override
    public byte[] getLocalFileDataData() {
        return ZipUtil.copy(this.localData);
    }

    public void setCentralDirectoryData(byte[] data) {
        this.centralData = ZipUtil.copy(data);
    }

    @Override
    public ZipShort getCentralDirectoryLength() {
        if (this.centralData != null) {
            return new ZipShort(this.centralData.length);
        }
        return this.getLocalFileDataLength();
    }

    @Override
    public byte[] getCentralDirectoryData() {
        if (this.centralData != null) {
            return ZipUtil.copy(this.centralData);
        }
        return this.getLocalFileDataData();
    }

    @Override
    public void parseFromLocalFileData(byte[] data, int offset, int length) {
        byte[] tmp = new byte[length];
        System.arraycopy(data, offset, tmp, 0, length);
        this.setLocalFileDataData(tmp);
    }

    @Override
    public void parseFromCentralDirectoryData(byte[] data, int offset, int length) {
        byte[] tmp = new byte[length];
        System.arraycopy(data, offset, tmp, 0, length);
        this.setCentralDirectoryData(tmp);
        if (this.localData == null) {
            this.setLocalFileDataData(tmp);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import org.apache.tools.zip.CentralDirectoryParsingZipExtraField;
import org.apache.tools.zip.ZipShort;
import org.apache.tools.zip.ZipUtil;

public final class UnparseableExtraFieldData
implements CentralDirectoryParsingZipExtraField {
    private static final ZipShort HEADER_ID = new ZipShort(44225);
    private byte[] localFileData;
    private byte[] centralDirectoryData;

    @Override
    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    @Override
    public ZipShort getLocalFileDataLength() {
        return new ZipShort(this.localFileData == null ? 0 : this.localFileData.length);
    }

    @Override
    public ZipShort getCentralDirectoryLength() {
        return this.centralDirectoryData == null ? this.getLocalFileDataLength() : new ZipShort(this.centralDirectoryData.length);
    }

    @Override
    public byte[] getLocalFileDataData() {
        return ZipUtil.copy(this.localFileData);
    }

    @Override
    public byte[] getCentralDirectoryData() {
        return this.centralDirectoryData == null ? this.getLocalFileDataData() : ZipUtil.copy(this.centralDirectoryData);
    }

    @Override
    public void parseFromLocalFileData(byte[] buffer, int offset, int length) {
        this.localFileData = new byte[length];
        System.arraycopy(buffer, offset, this.localFileData, 0, length);
    }

    @Override
    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) {
        this.centralDirectoryData = new byte[length];
        System.arraycopy(buffer, offset, this.centralDirectoryData, 0, length);
        if (this.localFileData == null) {
            this.parseFromLocalFileData(buffer, offset, length);
        }
    }
}


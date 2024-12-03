/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import java.util.zip.ZipException;
import org.apache.tools.zip.ZipShort;

public interface ZipExtraField {
    public ZipShort getHeaderId();

    public ZipShort getLocalFileDataLength();

    public ZipShort getCentralDirectoryLength();

    public byte[] getLocalFileDataData();

    public byte[] getCentralDirectoryData();

    public void parseFromLocalFileData(byte[] var1, int var2, int var3) throws ZipException;
}


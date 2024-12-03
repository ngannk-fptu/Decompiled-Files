/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import java.util.zip.ZipException;
import org.apache.tools.zip.ZipExtraField;

public interface CentralDirectoryParsingZipExtraField
extends ZipExtraField {
    public void parseFromCentralDirectoryData(byte[] var1, int var2, int var3) throws ZipException;
}


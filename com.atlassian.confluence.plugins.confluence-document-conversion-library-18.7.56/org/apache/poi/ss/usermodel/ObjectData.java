/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import java.io.IOException;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.SimpleShape;

public interface ObjectData
extends SimpleShape {
    public byte[] getObjectData() throws IOException;

    public boolean hasDirectoryEntry();

    public DirectoryEntry getDirectory() throws IOException;

    public String getOLE2ClassName();

    public String getFileName();

    public PictureData getPictureData();

    default public String getContentType() {
        return "binary/octet-stream";
    }
}


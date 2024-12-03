/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.eventfilesystem;

import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;

public class POIFSReaderEvent {
    private final DocumentInputStream stream;
    private final POIFSDocumentPath path;
    private final String documentName;
    private final ClassID storageClassId;

    POIFSReaderEvent(DocumentInputStream stream, POIFSDocumentPath path, String documentName, ClassID storageClassId) {
        this.stream = stream;
        this.path = path;
        this.documentName = documentName;
        this.storageClassId = storageClassId;
    }

    public DocumentInputStream getStream() {
        return this.stream;
    }

    public POIFSDocumentPath getPath() {
        return this.path;
    }

    public String getName() {
        return this.documentName;
    }

    public ClassID getStorageClassId() {
        return this.storageClassId;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.POIReadOnlyDocument;
import org.apache.poi.hpbf.model.EscherDelayStm;
import org.apache.poi.hpbf.model.EscherStm;
import org.apache.poi.hpbf.model.MainContents;
import org.apache.poi.hpbf.model.QuillContents;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public final class HPBFDocument
extends POIReadOnlyDocument {
    private MainContents mainContents;
    private QuillContents quillContents;
    private EscherStm escherStm;
    private EscherDelayStm escherDelayStm;

    public HPBFDocument(POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }

    public HPBFDocument(InputStream inp) throws IOException {
        this(new POIFSFileSystem(inp));
    }

    public HPBFDocument(DirectoryNode dir) throws IOException {
        super(dir);
        this.mainContents = new MainContents(dir);
        this.quillContents = new QuillContents(dir);
        this.escherStm = new EscherStm(dir);
        this.escherDelayStm = new EscherDelayStm(dir);
    }

    public MainContents getMainContents() {
        return this.mainContents;
    }

    public QuillContents getQuillContents() {
        return this.quillContents;
    }

    public EscherStm getEscherStm() {
        return this.escherStm;
    }

    public EscherDelayStm getEscherDelayStm() {
        return this.escherDelayStm;
    }
}


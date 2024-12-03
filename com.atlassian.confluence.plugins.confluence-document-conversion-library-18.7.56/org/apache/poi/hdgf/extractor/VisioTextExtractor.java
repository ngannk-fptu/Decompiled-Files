/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.hdgf.HDGFDiagram;
import org.apache.poi.hdgf.chunks.Chunk;
import org.apache.poi.hdgf.streams.ChunkStream;
import org.apache.poi.hdgf.streams.PointerContainingStream;
import org.apache.poi.hdgf.streams.Stream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public final class VisioTextExtractor
implements POIOLE2TextExtractor {
    private HDGFDiagram hdgf;
    private boolean doCloseFilesystem = true;

    public VisioTextExtractor(HDGFDiagram hdgf) {
        this.hdgf = hdgf;
    }

    public VisioTextExtractor(POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }

    public VisioTextExtractor(DirectoryNode dir) throws IOException {
        this(new HDGFDiagram(dir));
    }

    public VisioTextExtractor(InputStream inp) throws IOException {
        this(new POIFSFileSystem(inp));
    }

    public String[] getAllText() {
        ArrayList<String> text = new ArrayList<String>();
        for (Stream stream : this.hdgf.getTopLevelStreams()) {
            this.findText(stream, text);
        }
        return text.toArray(new String[0]);
    }

    private void findText(Stream stream, List<String> text) {
        if (stream instanceof PointerContainingStream) {
            PointerContainingStream ps = (PointerContainingStream)stream;
            for (Stream substream : ps.getPointedToStreams()) {
                this.findText(substream, text);
            }
        }
        if (stream instanceof ChunkStream) {
            ChunkStream cs = (ChunkStream)stream;
            for (Chunk chunk : cs.getChunks()) {
                String str;
                Chunk.Command cmd;
                if (chunk == null || chunk.getName() == null || !"Text".equals(chunk.getName()) || chunk.getCommands().length <= 0 || (cmd = chunk.getCommands()[0]) == null || cmd.getValue() == null || (str = cmd.getValue().toString()).isEmpty() || "\n".equals(str)) continue;
                text.add(str);
            }
        }
    }

    @Override
    public String getText() {
        StringBuilder text = new StringBuilder();
        for (String t : this.getAllText()) {
            text.append(t);
            if (t.endsWith("\r") || t.endsWith("\n")) continue;
            text.append('\n');
        }
        return text.toString();
    }

    @Override
    public HDGFDiagram getDocument() {
        return this.hdgf;
    }

    @Override
    public void setCloseFilesystem(boolean doCloseFilesystem) {
        this.doCloseFilesystem = doCloseFilesystem;
    }

    @Override
    public boolean isCloseFilesystem() {
        return this.doCloseFilesystem;
    }

    @Override
    public HDGFDiagram getFilesystem() {
        return this.hdgf;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.dev;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import org.apache.poi.hdgf.HDGFDiagram;
import org.apache.poi.hdgf.chunks.Chunk;
import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.hdgf.streams.ChunkStream;
import org.apache.poi.hdgf.streams.PointerContainingStream;
import org.apache.poi.hdgf.streams.Stream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public final class VSDDumper {
    static final String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
    private final PrintStream ps;
    private final HDGFDiagram hdgf;

    VSDDumper(PrintStream ps, HDGFDiagram hdgf) {
        this.ps = ps;
        this.hdgf = hdgf;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Use:");
            System.err.println("  VSDDumper <filename>");
            System.exit(1);
        }
        try (POIFSFileSystem poifs = new POIFSFileSystem(new File(args[0]));
             HDGFDiagram hdgf = new HDGFDiagram(poifs);){
            PrintStream ps = System.out;
            ps.println("Opened " + args[0]);
            VSDDumper vd = new VSDDumper(ps, hdgf);
            vd.dumpFile();
        }
    }

    public void dumpFile() {
        this.dumpVal("Claimed document size", this.hdgf.getDocumentSize(), 0);
        this.ps.println();
        this.dumpStream(this.hdgf.getTrailerStream(), 0);
    }

    private void dumpStream(Stream stream, int indent) {
        Pointer ptr = stream.getPointer();
        this.dumpVal("Stream at", ptr.getOffset(), indent);
        this.dumpVal("Type is", ptr.getType(), indent + 1);
        this.dumpVal("Format is", ptr.getFormat(), indent + 1);
        this.dumpVal("Length is", ptr.getLength(), indent + 1);
        if (ptr.destinationCompressed()) {
            this.dumpVal("DC.Length is", stream._getContentsLength(), indent + 1);
        }
        this.dumpVal("Compressed is", ptr.destinationCompressed(), indent + 1);
        this.dumpVal("Stream is", stream.getClass().getName(), indent + 1);
        byte[] db = stream._getStore()._getContents();
        String ds = db.length >= 8 ? Arrays.toString(db) : "[]";
        this.dumpVal("First few bytes are", ds, indent + 1);
        if (stream instanceof PointerContainingStream) {
            Stream[] streams = ((PointerContainingStream)stream).getPointedToStreams();
            this.dumpVal("Nbr of children", streams.length, indent + 1);
            for (Stream s : streams) {
                this.dumpStream(s, indent + 1);
            }
        }
        if (stream instanceof ChunkStream) {
            Chunk[] chunks = ((ChunkStream)stream).getChunks();
            this.dumpVal("Nbr of chunks", chunks.length, indent + 1);
            for (Chunk chunk : chunks) {
                this.dumpChunk(chunk, indent + 1);
            }
        }
    }

    private void dumpChunk(Chunk chunk, int indent) {
        this.dumpVal(chunk.getName(), "", indent);
        this.dumpVal("Length is", chunk._getContents().length, indent);
        this.dumpVal("OD Size is", chunk.getOnDiskSize(), indent);
        this.dumpVal("T / S is", chunk.getTrailer() + " / " + chunk.getSeparator(), indent);
        Chunk.Command[] commands = chunk.getCommands();
        this.dumpVal("Nbr of commands", commands.length, indent);
        for (Chunk.Command command : commands) {
            this.dumpVal(command.getDefinition().getName(), "" + command.getValue(), indent + 1);
        }
    }

    private void dumpVal(String label, long value, int indent) {
        this.ps.print(tabs.substring(0, indent));
        this.ps.print(label);
        this.ps.print('\t');
        this.ps.print(value);
        this.ps.print(" (0x");
        this.ps.print(Long.toHexString(value));
        this.ps.println(")");
    }

    private void dumpVal(String label, boolean value, int indent) {
        this.dumpVal(label, Boolean.toString(value), indent);
    }

    private void dumpVal(String label, String value, int indent) {
        this.ps.print(tabs.substring(0, indent));
        this.ps.print(label);
        this.ps.print('\t');
        this.ps.println(value);
    }
}


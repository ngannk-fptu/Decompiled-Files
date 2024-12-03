/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf;

import java.io.IOException;
import org.apache.poi.POIReadOnlyDocument;
import org.apache.poi.hdgf.chunks.ChunkFactory;
import org.apache.poi.hdgf.pointers.Pointer;
import org.apache.poi.hdgf.pointers.PointerFactory;
import org.apache.poi.hdgf.streams.PointerContainingStream;
import org.apache.poi.hdgf.streams.Stream;
import org.apache.poi.hdgf.streams.StringsStream;
import org.apache.poi.hdgf.streams.TrailerStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

public final class HDGFDiagram
extends POIReadOnlyDocument {
    private static final String VISIO_HEADER = "Visio (TM) Drawing\r\n";
    private long docSize;
    private Pointer trailerPointer;
    private TrailerStream trailer;

    public HDGFDiagram(POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }

    public HDGFDiagram(DirectoryNode dir) throws IOException {
        super(dir);
        byte[] _docstream;
        try (DocumentInputStream is = dir.createDocumentInputStream("VisioDocument");){
            _docstream = IOUtils.toByteArray(is);
        }
        String typeString = new String(_docstream, 0, 20, LocaleUtil.CHARSET_1252);
        if (!typeString.equals(VISIO_HEADER)) {
            throw new IllegalArgumentException("Wasn't a valid visio document, started with " + typeString);
        }
        short version = LittleEndian.getShort(_docstream, 26);
        this.docSize = LittleEndian.getUInt(_docstream, 28);
        PointerFactory ptrFactory = new PointerFactory(version);
        ChunkFactory chunkFactory = new ChunkFactory(version);
        this.trailerPointer = ptrFactory.createPointer(_docstream, 36);
        this.trailer = (TrailerStream)Stream.createStream(this.trailerPointer, _docstream, chunkFactory, ptrFactory);
        this.trailer.findChildren(_docstream);
    }

    public TrailerStream getTrailerStream() {
        return this.trailer;
    }

    public Stream[] getTopLevelStreams() {
        return this.trailer.getPointedToStreams();
    }

    public long getDocumentSize() {
        return this.docSize;
    }

    public void debug() {
        System.err.println("Trailer is at " + this.trailerPointer.getOffset());
        System.err.println("Trailer has type " + this.trailerPointer.getType());
        System.err.println("Trailer has length " + this.trailerPointer.getLength());
        System.err.println("Trailer has format " + this.trailerPointer.getFormat());
        for (int i = 0; i < this.trailer.getPointedToStreams().length; ++i) {
            PointerContainingStream pcs;
            Stream stream = this.trailer.getPointedToStreams()[i];
            Pointer ptr = stream.getPointer();
            System.err.println("Looking at pointer " + i);
            System.err.println("\tType is " + ptr.getType() + "\t\t" + Integer.toHexString(ptr.getType()));
            System.err.println("\tOffset is " + ptr.getOffset() + "\t\t" + Long.toHexString(ptr.getOffset()));
            System.err.println("\tAddress is " + ptr.getAddress() + "\t" + Long.toHexString(ptr.getAddress()));
            System.err.println("\tLength is " + ptr.getLength() + "\t\t" + Long.toHexString(ptr.getLength()));
            System.err.println("\tFormat is " + ptr.getFormat() + "\t\t" + Long.toHexString(ptr.getFormat()));
            System.err.println("\tCompressed is " + ptr.destinationCompressed());
            System.err.println("\tStream is " + stream.getClass());
            if (stream instanceof PointerContainingStream && (pcs = (PointerContainingStream)stream).getPointedToStreams() != null && pcs.getPointedToStreams().length > 0) {
                System.err.println("\tContains " + pcs.getPointedToStreams().length + " other pointers/streams");
                for (int j = 0; j < pcs.getPointedToStreams().length; ++j) {
                    Stream ss = pcs.getPointedToStreams()[j];
                    Pointer sptr = ss.getPointer();
                    System.err.println("\t\t" + j + " - Type is " + sptr.getType() + "\t\t" + Integer.toHexString(sptr.getType()));
                    System.err.println("\t\t" + j + " - Length is " + sptr.getLength() + "\t\t" + Long.toHexString(sptr.getLength()));
                }
            }
            if (!(stream instanceof StringsStream)) continue;
            System.err.println("\t\t**strings**");
            StringsStream ss = (StringsStream)stream;
            System.err.println("\t\t" + ss._getContentsLength());
        }
    }
}


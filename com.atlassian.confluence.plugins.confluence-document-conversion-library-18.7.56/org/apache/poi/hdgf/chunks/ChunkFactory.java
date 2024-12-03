/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.chunks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hdgf.chunks.Chunk;
import org.apache.poi.hdgf.chunks.ChunkHeader;
import org.apache.poi.hdgf.chunks.ChunkSeparator;
import org.apache.poi.hdgf.chunks.ChunkTrailer;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LocaleUtil;

public final class ChunkFactory {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;
    private int version;
    private final Map<Integer, CommandDefinition[]> chunkCommandDefinitions = new HashMap<Integer, CommandDefinition[]>();
    private static final String chunkTableName = "/org/apache/poi/hdgf/chunks_parse_cmds.tbl";
    private static final Logger LOG = LogManager.getLogger(ChunkFactory.class);

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public ChunkFactory(int version) throws IOException {
        this.version = version;
        this.processChunkParseCommands();
    }

    private void processChunkParseCommands() throws IOException {
        try (InputStream cpd = ChunkFactory.class.getResourceAsStream(chunkTableName);){
            if (cpd == null) {
                throw new IllegalStateException("Unable to find HDGF chunk definition on the classpath - /org/apache/poi/hdgf/chunks_parse_cmds.tbl");
            }
            try (BufferedReader inp = new BufferedReader(new InputStreamReader(cpd, LocaleUtil.CHARSET_1252));){
                String line;
                while ((line = inp.readLine()) != null) {
                    if (line.isEmpty() || "# \t".contains(line.substring(0, 1))) continue;
                    if (!line.matches("^start [0-9]+$")) {
                        throw new IllegalStateException("Expecting start xxx, found " + line);
                    }
                    int chunkType = Integer.parseInt(line.substring(6));
                    ArrayList<CommandDefinition> defsL = new ArrayList<CommandDefinition>();
                    while ((line = inp.readLine()) != null && !line.startsWith("end")) {
                        StringTokenizer st = new StringTokenizer(line, " ");
                        int defType = Integer.parseInt(st.nextToken());
                        int offset = Integer.parseInt(st.nextToken());
                        String name = st.nextToken("\uffff").substring(1);
                        CommandDefinition def = new CommandDefinition(defType, offset, name);
                        defsL.add(def);
                    }
                    CommandDefinition[] defs = defsL.toArray(new CommandDefinition[0]);
                    this.chunkCommandDefinitions.put(chunkType, defs);
                }
            }
        }
    }

    public int getVersion() {
        return this.version;
    }

    public Chunk createChunk(byte[] data, int offset) {
        ChunkHeader header = ChunkHeader.createChunkHeader(this.version, data, offset);
        if (header.getLength() < 0) {
            throw new IllegalArgumentException("Found a chunk with a negative length, which isn't allowed");
        }
        int endOfDataPos = offset + header.getLength() + header.getSizeInBytes();
        if (endOfDataPos > data.length) {
            LOG.atWarn().log("Header called for {} bytes, but that would take us past the end of the data!", (Object)Unbox.box(header.getLength()));
            endOfDataPos = data.length;
            header.setLength(data.length - offset - header.getSizeInBytes());
            if (header.hasTrailer()) {
                header.setLength(header.getLength() - 8);
                endOfDataPos -= 8;
            }
            if (header.hasSeparator()) {
                header.setLength(header.getLength() - 4);
                endOfDataPos -= 4;
            }
        }
        ChunkTrailer trailer = null;
        ChunkSeparator separator = null;
        if (header.hasTrailer()) {
            if (endOfDataPos <= data.length - 8) {
                trailer = new ChunkTrailer(data, endOfDataPos);
                endOfDataPos += 8;
            } else {
                LOG.atError().log("Header claims a length to {} there's then no space for the trailer in the data ({})", (Object)Unbox.box(endOfDataPos), (Object)Unbox.box(data.length));
            }
        }
        if (header.hasSeparator()) {
            if (endOfDataPos <= data.length - 4) {
                separator = new ChunkSeparator(data, endOfDataPos);
            } else {
                LOG.atError().log("Header claims a length to {} there's then no space for the separator in the data ({})", (Object)Unbox.box(endOfDataPos), (Object)Unbox.box(data.length));
            }
        }
        byte[] contents = IOUtils.safelyClone(data, offset + header.getSizeInBytes(), header.getLength(), MAX_RECORD_LENGTH);
        Chunk chunk = new Chunk(header, trailer, separator, contents);
        CommandDefinition[] defs = this.chunkCommandDefinitions.get(header.getType());
        if (defs == null) {
            defs = new CommandDefinition[]{};
        }
        chunk.setCommandDefinitions(defs);
        chunk.processCommands();
        return chunk;
    }

    public static class CommandDefinition {
        private int type;
        private int offset;
        private String name;

        public CommandDefinition(int type, int offset, String name) {
            this.type = type;
            this.offset = offset;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public int getOffset() {
            return this.offset;
        }

        public int getType() {
            return this.type;
        }
    }
}


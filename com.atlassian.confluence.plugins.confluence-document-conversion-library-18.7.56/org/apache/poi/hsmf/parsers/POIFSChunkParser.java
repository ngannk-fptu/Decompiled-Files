/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.datatypes.ByteChunk;
import org.apache.poi.hsmf.datatypes.ByteChunkDeferred;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.ChunkGroup;
import org.apache.poi.hsmf.datatypes.Chunks;
import org.apache.poi.hsmf.datatypes.DirectoryChunk;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.MessagePropertiesChunk;
import org.apache.poi.hsmf.datatypes.MessageSubmissionChunk;
import org.apache.poi.hsmf.datatypes.NameIdChunks;
import org.apache.poi.hsmf.datatypes.RecipientChunks;
import org.apache.poi.hsmf.datatypes.StoragePropertiesChunk;
import org.apache.poi.hsmf.datatypes.StringChunk;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public final class POIFSChunkParser {
    private static final Logger LOG = LogManager.getLogger(POIFSChunkParser.class);

    private POIFSChunkParser() {
    }

    public static ChunkGroup[] parse(POIFSFileSystem fs) {
        return POIFSChunkParser.parse(fs.getRoot());
    }

    public static ChunkGroup[] parse(DirectoryNode node) {
        Chunks mainChunks = new Chunks();
        ArrayList<ChunkGroup> groups = new ArrayList<ChunkGroup>();
        groups.add(mainChunks);
        for (Entry entry : node) {
            if (!(entry instanceof DirectoryNode)) continue;
            DirectoryNode dir = (DirectoryNode)entry;
            ChunkGroup group = null;
            if (dir.getName().startsWith("__attach_version1.0_#")) {
                group = new AttachmentChunks(dir.getName());
            }
            if (dir.getName().startsWith("__nameid_version1.0")) {
                group = new NameIdChunks();
            }
            if (dir.getName().startsWith("__recip_version1.0_#")) {
                group = new RecipientChunks(dir.getName());
            }
            if (group == null) continue;
            POIFSChunkParser.processChunks(dir, group);
            groups.add(group);
        }
        POIFSChunkParser.processChunks(node, mainChunks);
        for (ChunkGroup group : groups) {
            group.chunksComplete();
        }
        return groups.toArray(new ChunkGroup[0]);
    }

    private static void processChunks(DirectoryNode node, ChunkGroup grouping) {
        TreeMap<Integer, MultiChunk> multiChunks = new TreeMap<Integer, MultiChunk>();
        for (Entry entry : node) {
            if (!(entry instanceof DocumentNode) && (!(entry instanceof DirectoryNode) || !entry.getName().endsWith(Types.DIRECTORY.asFileEnding()))) continue;
            POIFSChunkParser.process(entry, grouping, multiChunks);
        }
        multiChunks.entrySet().stream().flatMap(me -> ((MultiChunk)me.getValue()).getChunks().values().stream()).filter(Objects::nonNull).forEach(grouping::record);
    }

    private static void process(Entry entry, ChunkGroup grouping, Map<Integer, MultiChunk> multiChunks) {
        Chunk chunk;
        String entryName = entry.getName();
        boolean[] isMultiValued = new boolean[]{false};
        Chunk chunk2 = chunk = "__properties_version1.0".equals(entryName) ? POIFSChunkParser.readPropertiesChunk(grouping, entry) : POIFSChunkParser.readPrimitiveChunk(entry, isMultiValued, multiChunks);
        if (chunk == null) {
            return;
        }
        if (entry instanceof DocumentNode) {
            try (DocumentInputStream inp = new DocumentInputStream((DocumentNode)entry);){
                chunk.readValue(inp);
            }
            catch (IOException e) {
                LOG.atError().withThrowable(e).log("Error reading from part {}", (Object)entry.getName());
            }
        }
        if (!isMultiValued[0]) {
            grouping.record(chunk);
        }
    }

    private static Chunk readPropertiesChunk(ChunkGroup grouping, Entry entry) {
        if (grouping instanceof Chunks) {
            boolean isEmbedded = entry.getParent() != null && entry.getParent().getParent() != null;
            return new MessagePropertiesChunk(grouping, isEmbedded);
        }
        return new StoragePropertiesChunk(grouping);
    }

    private static Chunk readPrimitiveChunk(Entry entry, boolean[] isMultiValue, Map<Integer, MultiChunk> multiChunks) {
        int typeId;
        int chunkId;
        String entryName = entry.getName();
        int splitAt = entryName.lastIndexOf(95);
        if (entryName.length() < 9 || splitAt == -1) {
            return null;
        }
        String namePrefix = entryName.substring(0, splitAt + 1);
        String ids = entryName.substring(splitAt + 1);
        if (namePrefix.equals("Olk10SideProps") || namePrefix.equals("Olk10SideProps_")) {
            return null;
        }
        if (splitAt > entryName.length() - 8) {
            throw new IllegalArgumentException("Invalid chunk name " + entryName);
        }
        try {
            chunkId = Integer.parseInt(ids.substring(0, 4), 16);
            int tid = Integer.parseInt(ids.substring(4, 8), 16);
            isMultiValue[0] = (tid & 0x1000) != 0;
            typeId = tid & 0xFFFFEFFF;
        }
        catch (NumberFormatException e) {
            return null;
        }
        Types.MAPIType type = Types.getById(typeId);
        if (type == null) {
            type = Types.createCustom(typeId);
        }
        if (chunkId == MAPIProperty.MESSAGE_SUBMISSION_ID.id) {
            return new MessageSubmissionChunk(namePrefix, chunkId, type);
        }
        if (type == Types.BINARY && chunkId == MAPIProperty.ATTACH_DATA.id) {
            ByteChunkDeferred bcd = new ByteChunkDeferred(namePrefix, chunkId, type);
            if (entry instanceof DocumentNode) {
                bcd.readValue((DocumentNode)entry);
            }
            return bcd;
        }
        if (isMultiValue[0]) {
            return POIFSChunkParser.readMultiValue(namePrefix, ids, chunkId, entry, type, multiChunks);
        }
        if (type == Types.DIRECTORY && entry instanceof DirectoryNode) {
            return new DirectoryChunk((DirectoryNode)entry, namePrefix, chunkId, type);
        }
        if (type == Types.BINARY) {
            return new ByteChunk(namePrefix, chunkId, type);
        }
        if (type == Types.ASCII_STRING || type == Types.UNICODE_STRING) {
            return new StringChunk(namePrefix, chunkId, type);
        }
        LOG.atWarn().log("UNSUPPORTED PROP TYPE {}", (Object)entryName);
        return null;
    }

    private static Chunk readMultiValue(String namePrefix, String ids, int chunkId, Entry entry, Types.MAPIType type, Map<Integer, MultiChunk> multiChunks) {
        Chunk chunk;
        long multiValueIdx = -1L;
        if (ids.contains("-")) {
            String mvidxstr = ids.substring(ids.lastIndexOf(45) + 1);
            try {
                multiValueIdx = Long.parseLong(mvidxstr) & 0xFFFFFFFFL;
            }
            catch (NumberFormatException ignore) {
                LOG.atWarn().log("Can't read multi value idx from entry {}", (Object)entry.getName());
            }
        }
        final MultiChunk mc = multiChunks.computeIfAbsent(chunkId, k -> new MultiChunk());
        if (multiValueIdx == -1L) {
            return new ByteChunk(chunkId, Types.BINARY){

                @Override
                public void readValue(InputStream value) throws IOException {
                    super.readValue(value);
                    mc.setLength(this.getValue().length / 4);
                }
            };
        }
        if (type == Types.BINARY) {
            chunk = new ByteChunk(namePrefix, chunkId, type);
        } else if (type == Types.ASCII_STRING || type == Types.UNICODE_STRING) {
            chunk = new StringChunk(namePrefix, chunkId, type);
        } else {
            LOG.atWarn().log("Unsupported multivalued prop type for entry {}", (Object)entry.getName());
            return null;
        }
        mc.addChunk((int)multiValueIdx, chunk);
        return chunk;
    }

    private static class MultiChunk {
        private int length = -1;
        private final Map<Integer, Chunk> chunks = new TreeMap<Integer, Chunk>();

        private MultiChunk() {
        }

        int getLength() {
            return this.length;
        }

        void setLength(int length) {
            this.length = length;
        }

        void addChunk(int multiValueIdx, Chunk value) {
            this.chunks.put(multiValueIdx, value);
        }

        Map<Integer, Chunk> getChunks() {
            return this.chunks;
        }
    }
}


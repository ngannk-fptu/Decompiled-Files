/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.chunks;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hdgf.chunks.ChunkFactory;
import org.apache.poi.hdgf.chunks.ChunkHeader;
import org.apache.poi.hdgf.chunks.ChunkSeparator;
import org.apache.poi.hdgf.chunks.ChunkTrailer;
import org.apache.poi.util.LittleEndian;

public final class Chunk {
    private static final Logger LOG = LogManager.getLogger(Chunk.class);
    private final byte[] contents;
    private final ChunkHeader header;
    private final ChunkTrailer trailer;
    private final ChunkSeparator separator;
    private ChunkFactory.CommandDefinition[] commandDefinitions;
    private Command[] commands;
    private String name;

    public Chunk(ChunkHeader header, ChunkTrailer trailer, ChunkSeparator separator, byte[] contents) {
        this.header = header;
        this.trailer = trailer;
        this.separator = separator;
        this.contents = (byte[])contents.clone();
    }

    public byte[] _getContents() {
        return this.contents;
    }

    public ChunkHeader getHeader() {
        return this.header;
    }

    public ChunkSeparator getSeparator() {
        return this.separator;
    }

    public ChunkTrailer getTrailer() {
        return this.trailer;
    }

    public ChunkFactory.CommandDefinition[] getCommandDefinitions() {
        return this.commandDefinitions;
    }

    void setCommandDefinitions(ChunkFactory.CommandDefinition[] commandDefinitions) {
        this.commandDefinitions = commandDefinitions;
    }

    public Command[] getCommands() {
        return this.commands;
    }

    public String getName() {
        return this.name;
    }

    public int getOnDiskSize() {
        int size = this.header.getSizeInBytes() + this.contents.length;
        if (this.trailer != null) {
            size += this.trailer.getTrailerData().length;
        }
        if (this.separator != null) {
            size += this.separator.separatorData.length;
        }
        return size;
    }

    void processCommands() {
        if (this.commandDefinitions == null) {
            throw new IllegalStateException("You must supply the command definitions before calling processCommands!");
        }
        ArrayList<BlockOffsetCommand> commandList = new ArrayList<BlockOffsetCommand>();
        for (ChunkFactory.CommandDefinition cdef : this.commandDefinitions) {
            int type = cdef.getType();
            int offset = cdef.getOffset();
            if (type == 10) {
                this.name = cdef.getName();
                continue;
            }
            if (type == 18) continue;
            Command command = type == 11 || type == 21 ? new BlockOffsetCommand(cdef) : new Command(cdef);
            switch (type) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 11: 
                case 12: 
                case 16: 
                case 17: 
                case 21: 
                case 28: 
                case 29: {
                    break;
                }
                default: {
                    if (offset < 19) break;
                    offset -= 19;
                }
            }
            if (offset >= this.contents.length) {
                LOG.atWarn().log("Command offset {} past end of data at {}", (Object)Unbox.box(offset), (Object)Unbox.box(this.contents.length));
                continue;
            }
            try {
                switch (type) {
                    case 0: 
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: {
                        command.value = (this.contents[offset] >>> type & 1) == 1;
                        break;
                    }
                    case 8: {
                        command.value = this.contents[offset];
                        break;
                    }
                    case 9: {
                        command.value = LittleEndian.getDouble(this.contents, offset);
                        break;
                    }
                    case 12: {
                        int startsAt;
                        if (this.contents.length < 8) {
                            command.value = "";
                            break;
                        }
                        int endsAt = startsAt = 8;
                        for (int j = startsAt; j < this.contents.length - 1 && endsAt == startsAt; ++j) {
                            if (this.contents[j] != 0 || this.contents[j + 1] != 0) continue;
                            endsAt = j;
                        }
                        if (endsAt == startsAt) {
                            endsAt = this.contents.length;
                        }
                        int strLen = endsAt - startsAt;
                        command.value = new String(this.contents, startsAt, strLen, this.header.getChunkCharset());
                        break;
                    }
                    case 25: {
                        command.value = LittleEndian.getShort(this.contents, offset);
                        break;
                    }
                    case 26: {
                        command.value = LittleEndian.getInt(this.contents, offset);
                        break;
                    }
                    case 11: 
                    case 21: {
                        if (offset < this.contents.length - 3) {
                            int bOffset = (int)LittleEndian.getUInt(this.contents, offset);
                            Command bcmd = command;
                            ((BlockOffsetCommand)bcmd).setOffset(bOffset);
                        }
                        break;
                    }
                    default: {
                        LOG.atInfo().log("Command of type {} not processed!", (Object)Unbox.box(type));
                        break;
                    }
                }
            }
            catch (Exception e) {
                LOG.atError().withThrowable(e).log("Unexpected error processing command, ignoring and continuing. Command: {}", (Object)command);
            }
            commandList.add((BlockOffsetCommand)command);
        }
        this.commands = commandList.toArray(new Command[0]);
    }

    private static final class BlockOffsetCommand
    extends Command {
        private BlockOffsetCommand(ChunkFactory.CommandDefinition definition) {
            super(definition, null);
        }

        private void setOffset(int offset) {
            this.value = offset;
        }
    }

    public static class Command {
        protected Object value;
        private ChunkFactory.CommandDefinition definition;

        private Command(ChunkFactory.CommandDefinition definition, Object value) {
            this.definition = definition;
            this.value = value;
        }

        private Command(ChunkFactory.CommandDefinition definition) {
            this(definition, (Object)null);
        }

        public ChunkFactory.CommandDefinition getDefinition() {
            return this.definition;
        }

        public Object getValue() {
            return this.value;
        }
    }
}


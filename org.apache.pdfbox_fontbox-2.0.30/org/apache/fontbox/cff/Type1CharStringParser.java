/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.cff;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.cff.CharStringCommand;
import org.apache.fontbox.cff.DataInput;

public class Type1CharStringParser {
    private static final Log LOG = LogFactory.getLog(Type1CharStringParser.class);
    static final int RETURN = 11;
    static final int CALLSUBR = 10;
    static final int TWO_BYTE = 12;
    static final int CALLOTHERSUBR = 16;
    static final int POP = 17;
    private final String fontName;
    private final String glyphName;

    public Type1CharStringParser(String fontName, String glyphName) {
        this.fontName = fontName;
        this.glyphName = glyphName;
    }

    public List<Object> parse(byte[] bytes, List<byte[]> subrs) throws IOException {
        return this.parse(bytes, subrs, new ArrayList<Object>());
    }

    private List<Object> parse(byte[] bytes, List<byte[]> subrs, List<Object> sequence) throws IOException {
        DataInput input = new DataInput(bytes);
        while (input.hasRemaining()) {
            int b0 = input.readUnsignedByte();
            if (b0 == 10) {
                Object obj = sequence.remove(sequence.size() - 1);
                if (!(obj instanceof Integer)) {
                    LOG.warn((Object)("Parameter " + obj + " for CALLSUBR is ignored, integer expected in glyph '" + this.glyphName + "' of font " + this.fontName));
                    continue;
                }
                Integer operand = (Integer)obj;
                if (operand >= 0 && operand < subrs.size()) {
                    byte[] subrBytes = subrs.get(operand);
                    this.parse(subrBytes, subrs, sequence);
                    Object lastItem = sequence.get(sequence.size() - 1);
                    if (!(lastItem instanceof CharStringCommand) || ((CharStringCommand)lastItem).getKey().getValue()[0] != 11) continue;
                    sequence.remove(sequence.size() - 1);
                    continue;
                }
                LOG.warn((Object)("CALLSUBR is ignored, operand: " + operand + ", subrs.size(): " + subrs.size() + " in glyph '" + this.glyphName + "' of font " + this.fontName));
                while (sequence.get(sequence.size() - 1) instanceof Integer) {
                    sequence.remove(sequence.size() - 1);
                }
                continue;
            }
            if (b0 == 12 && input.peekUnsignedByte(0) == 16) {
                input.readByte();
                Integer othersubrNum = (Integer)sequence.remove(sequence.size() - 1);
                Integer numArgs = (Integer)sequence.remove(sequence.size() - 1);
                ArrayDeque<Integer> results = new ArrayDeque<Integer>();
                switch (othersubrNum) {
                    case 0: {
                        results.push(Type1CharStringParser.removeInteger(sequence));
                        results.push(Type1CharStringParser.removeInteger(sequence));
                        sequence.remove(sequence.size() - 1);
                        sequence.add(0);
                        sequence.add(new CharStringCommand(12, 16));
                        break;
                    }
                    case 1: {
                        sequence.add(1);
                        sequence.add(new CharStringCommand(12, 16));
                        break;
                    }
                    case 3: {
                        results.push(Type1CharStringParser.removeInteger(sequence));
                        break;
                    }
                    default: {
                        for (int i = 0; i < numArgs; ++i) {
                            results.push(Type1CharStringParser.removeInteger(sequence));
                        }
                    }
                }
                while (input.peekUnsignedByte(0) == 12 && input.peekUnsignedByte(1) == 17) {
                    input.readByte();
                    input.readByte();
                    sequence.add(results.pop());
                }
                if (results.size() <= 0) continue;
                LOG.warn((Object)("Value left on the PostScript stack in glyph " + this.glyphName + " of font " + this.fontName));
                continue;
            }
            if (b0 >= 0 && b0 <= 31) {
                sequence.add(this.readCommand(input, b0));
                continue;
            }
            if (b0 >= 32 && b0 <= 255) {
                sequence.add(this.readNumber(input, b0));
                continue;
            }
            throw new IllegalArgumentException();
        }
        return sequence;
    }

    private static Integer removeInteger(List<Object> sequence) throws IOException {
        Object item = sequence.remove(sequence.size() - 1);
        if (item instanceof Integer) {
            return (Integer)item;
        }
        CharStringCommand command = (CharStringCommand)item;
        if (command.getKey().getValue()[0] == 12 && command.getKey().getValue()[1] == 12) {
            int a = (Integer)sequence.remove(sequence.size() - 1);
            int b = (Integer)sequence.remove(sequence.size() - 1);
            return b / a;
        }
        throw new IOException("Unexpected char string command: " + command.getKey());
    }

    private CharStringCommand readCommand(DataInput input, int b0) throws IOException {
        if (b0 == 12) {
            int b1 = input.readUnsignedByte();
            return new CharStringCommand(b0, b1);
        }
        return new CharStringCommand(b0);
    }

    private Integer readNumber(DataInput input, int b0) throws IOException {
        if (b0 >= 32 && b0 <= 246) {
            return b0 - 139;
        }
        if (b0 >= 247 && b0 <= 250) {
            int b1 = input.readUnsignedByte();
            return (b0 - 247) * 256 + b1 + 108;
        }
        if (b0 >= 251 && b0 <= 254) {
            int b1 = input.readUnsignedByte();
            return -(b0 - 251) * 256 - b1 - 108;
        }
        if (b0 == 255) {
            return input.readInt();
        }
        throw new IllegalArgumentException();
    }
}


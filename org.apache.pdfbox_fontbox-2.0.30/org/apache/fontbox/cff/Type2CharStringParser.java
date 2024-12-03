/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.fontbox.cff.CharStringCommand;
import org.apache.fontbox.cff.DataInput;

public class Type2CharStringParser {
    private int hstemCount = 0;
    private int vstemCount = 0;
    private List<Object> sequence = null;
    private final String fontName;
    private final String glyphName;

    public Type2CharStringParser(String fontName, String glyphName) {
        this.fontName = fontName;
        this.glyphName = glyphName;
    }

    public Type2CharStringParser(String fontName, int cid) {
        this.fontName = fontName;
        this.glyphName = String.format(Locale.US, "%04x", cid);
    }

    public List<Object> parse(byte[] bytes, byte[][] globalSubrIndex, byte[][] localSubrIndex) throws IOException {
        return this.parse(bytes, globalSubrIndex, localSubrIndex, true);
    }

    private List<Object> parse(byte[] bytes, byte[][] globalSubrIndex, byte[][] localSubrIndex, boolean init) throws IOException {
        boolean globalSubroutineIndexProvided;
        if (init) {
            this.hstemCount = 0;
            this.vstemCount = 0;
            this.sequence = new ArrayList<Object>();
        }
        DataInput input = new DataInput(bytes);
        boolean localSubroutineIndexProvided = localSubrIndex != null && localSubrIndex.length > 0;
        boolean bl = globalSubroutineIndexProvided = globalSubrIndex != null && globalSubrIndex.length > 0;
        while (input.hasRemaining()) {
            Object lastItem;
            byte[] subrBytes;
            int subrNumber;
            int nSubrs;
            int bias;
            Integer operand;
            int b0 = input.readUnsignedByte();
            if (b0 == 10 && localSubroutineIndexProvided) {
                operand = (Integer)this.sequence.remove(this.sequence.size() - 1);
                bias = 0;
                nSubrs = localSubrIndex.length;
                bias = nSubrs < 1240 ? 107 : (nSubrs < 33900 ? 1131 : 32768);
                subrNumber = bias + operand;
                if (subrNumber >= localSubrIndex.length) continue;
                subrBytes = localSubrIndex[subrNumber];
                this.parse(subrBytes, globalSubrIndex, localSubrIndex, false);
                lastItem = this.sequence.get(this.sequence.size() - 1);
                if (!(lastItem instanceof CharStringCommand) || ((CharStringCommand)lastItem).getKey().getValue()[0] != 11) continue;
                this.sequence.remove(this.sequence.size() - 1);
                continue;
            }
            if (b0 == 29 && globalSubroutineIndexProvided) {
                nSubrs = globalSubrIndex.length;
                bias = nSubrs < 1240 ? 107 : (nSubrs < 33900 ? 1131 : 32768);
                subrNumber = bias + (operand = (Integer)this.sequence.remove(this.sequence.size() - 1));
                if (subrNumber >= globalSubrIndex.length) continue;
                subrBytes = globalSubrIndex[subrNumber];
                this.parse(subrBytes, globalSubrIndex, localSubrIndex, false);
                lastItem = this.sequence.get(this.sequence.size() - 1);
                if (!(lastItem instanceof CharStringCommand) || ((CharStringCommand)lastItem).getKey().getValue()[0] != 11) continue;
                this.sequence.remove(this.sequence.size() - 1);
                continue;
            }
            if (b0 >= 0 && b0 <= 27) {
                this.sequence.add(this.readCommand(b0, input));
                continue;
            }
            if (b0 == 28) {
                this.sequence.add(this.readNumber(b0, input));
                continue;
            }
            if (b0 >= 29 && b0 <= 31) {
                this.sequence.add(this.readCommand(b0, input));
                continue;
            }
            if (b0 >= 32 && b0 <= 255) {
                this.sequence.add(this.readNumber(b0, input));
                continue;
            }
            throw new IllegalArgumentException();
        }
        return this.sequence;
    }

    private CharStringCommand readCommand(int b0, DataInput input) throws IOException {
        if (b0 == 1 || b0 == 18) {
            this.hstemCount += this.peekNumbers().size() / 2;
        } else if (b0 == 3 || b0 == 19 || b0 == 20 || b0 == 23) {
            this.vstemCount += this.peekNumbers().size() / 2;
        }
        if (b0 == 12) {
            int b1 = input.readUnsignedByte();
            return new CharStringCommand(b0, b1);
        }
        if (b0 == 19 || b0 == 20) {
            int[] value = new int[1 + this.getMaskLength()];
            value[0] = b0;
            for (int i = 1; i < value.length; ++i) {
                value[i] = input.readUnsignedByte();
            }
            return new CharStringCommand(value);
        }
        return new CharStringCommand(b0);
    }

    private Number readNumber(int b0, DataInput input) throws IOException {
        if (b0 == 28) {
            return (int)input.readShort();
        }
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
            short value = input.readShort();
            double fraction = (double)input.readUnsignedShort() / 65535.0;
            return (double)value + fraction;
        }
        throw new IllegalArgumentException();
    }

    private int getMaskLength() {
        int hintCount = this.hstemCount + this.vstemCount;
        int length = hintCount / 8;
        if (hintCount % 8 > 0) {
            ++length;
        }
        return length;
    }

    private List<Number> peekNumbers() {
        ArrayList<Number> numbers = new ArrayList<Number>();
        for (int i = this.sequence.size() - 1; i > -1; --i) {
            Object object = this.sequence.get(i);
            if (!(object instanceof Number)) {
                return numbers;
            }
            numbers.add(0, (Number)object);
        }
        return numbers;
    }
}


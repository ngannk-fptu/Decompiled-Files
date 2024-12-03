/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SkeinEngine
implements Memoable {
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private static final int PARAM_TYPE_KEY = 0;
    private static final int PARAM_TYPE_CONFIG = 4;
    private static final int PARAM_TYPE_MESSAGE = 48;
    private static final int PARAM_TYPE_OUTPUT = 63;
    private static final Hashtable INITIAL_STATES = new Hashtable();
    final ThreefishEngine threefish;
    private final int outputSizeBytes;
    long[] chain;
    private long[] initialState;
    private byte[] key;
    private Parameter[] preMessageParameters;
    private Parameter[] postMessageParameters;
    private final UBI ubi;
    private final byte[] singleByte = new byte[1];

    private static void initialState(int blockSize, int outputSize, long[] state) {
        INITIAL_STATES.put(SkeinEngine.variantIdentifier(blockSize / 8, outputSize / 8), state);
    }

    private static Integer variantIdentifier(int blockSizeBytes, int outputSizeBytes) {
        return Integers.valueOf(outputSizeBytes << 16 | blockSizeBytes);
    }

    public SkeinEngine(int blockSizeBits, int outputSizeBits) {
        if (outputSizeBits % 8 != 0) {
            throw new IllegalArgumentException("Output size must be a multiple of 8 bits. :" + outputSizeBits);
        }
        this.outputSizeBytes = outputSizeBits / 8;
        this.threefish = new ThreefishEngine(blockSizeBits);
        this.ubi = new UBI(this.threefish.getBlockSize());
    }

    public SkeinEngine(SkeinEngine engine) {
        this(engine.getBlockSize() * 8, engine.getOutputSize() * 8);
        this.copyIn(engine);
    }

    private void copyIn(SkeinEngine engine) {
        this.ubi.reset(engine.ubi);
        this.chain = Arrays.clone(engine.chain, this.chain);
        this.initialState = Arrays.clone(engine.initialState, this.initialState);
        this.key = Arrays.clone(engine.key, this.key);
        this.preMessageParameters = SkeinEngine.clone(engine.preMessageParameters, this.preMessageParameters);
        this.postMessageParameters = SkeinEngine.clone(engine.postMessageParameters, this.postMessageParameters);
    }

    private static Parameter[] clone(Parameter[] data, Parameter[] existing) {
        if (data == null) {
            return null;
        }
        if (existing == null || existing.length != data.length) {
            existing = new Parameter[data.length];
        }
        System.arraycopy(data, 0, existing, 0, existing.length);
        return existing;
    }

    @Override
    public Memoable copy() {
        return new SkeinEngine(this);
    }

    @Override
    public void reset(Memoable other) {
        SkeinEngine s = (SkeinEngine)other;
        if (this.getBlockSize() != s.getBlockSize() || this.outputSizeBytes != s.outputSizeBytes) {
            throw new IllegalArgumentException("Incompatible parameters in provided SkeinEngine.");
        }
        this.copyIn(s);
    }

    public int getOutputSize() {
        return this.outputSizeBytes;
    }

    public int getBlockSize() {
        return this.threefish.getBlockSize();
    }

    public void init(SkeinParameters params) {
        this.chain = null;
        this.key = null;
        this.preMessageParameters = null;
        this.postMessageParameters = null;
        if (params != null) {
            byte[] key = params.getKey();
            if (key.length < 16) {
                throw new IllegalArgumentException("Skein key must be at least 128 bits.");
            }
            this.initParams(params.getParameters());
        }
        this.createInitialState();
        this.ubiInit(48);
    }

    private void initParams(Hashtable parameters) {
        Enumeration keys = parameters.keys();
        Vector<Parameter> pre = new Vector<Parameter>();
        Vector<Parameter> post = new Vector<Parameter>();
        while (keys.hasMoreElements()) {
            Integer type = (Integer)keys.nextElement();
            byte[] value = (byte[])parameters.get(type);
            if (type == 0) {
                this.key = value;
                continue;
            }
            if (type < 48) {
                pre.addElement(new Parameter(type, value));
                continue;
            }
            post.addElement(new Parameter(type, value));
        }
        this.preMessageParameters = new Parameter[pre.size()];
        pre.copyInto(this.preMessageParameters);
        SkeinEngine.sort(this.preMessageParameters);
        this.postMessageParameters = new Parameter[post.size()];
        post.copyInto(this.postMessageParameters);
        SkeinEngine.sort(this.postMessageParameters);
    }

    private static void sort(Parameter[] params) {
        if (params == null) {
            return;
        }
        for (int i = 1; i < params.length; ++i) {
            Parameter param = params[i];
            for (int hole = i; hole > 0 && param.getType() < params[hole - 1].getType(); --hole) {
                params[hole] = params[hole - 1];
            }
            params[hole] = param;
        }
    }

    private void createInitialState() {
        long[] precalc = (long[])INITIAL_STATES.get(SkeinEngine.variantIdentifier(this.getBlockSize(), this.getOutputSize()));
        if (this.key == null && precalc != null) {
            this.chain = Arrays.clone(precalc);
        } else {
            this.chain = new long[this.getBlockSize() / 8];
            if (this.key != null) {
                this.ubiComplete(0, this.key);
            }
            this.ubiComplete(4, new Configuration(this.outputSizeBytes * 8).getBytes());
        }
        if (this.preMessageParameters != null) {
            for (int i = 0; i < this.preMessageParameters.length; ++i) {
                Parameter param = this.preMessageParameters[i];
                this.ubiComplete(param.getType(), param.getValue());
            }
        }
        this.initialState = Arrays.clone(this.chain);
    }

    public void reset() {
        System.arraycopy(this.initialState, 0, this.chain, 0, this.chain.length);
        this.ubiInit(48);
    }

    private void ubiComplete(int type, byte[] value) {
        this.ubiInit(type);
        this.ubi.update(value, 0, value.length, this.chain);
        this.ubiFinal();
    }

    private void ubiInit(int type) {
        this.ubi.reset(type);
    }

    private void ubiFinal() {
        this.ubi.doFinal(this.chain);
    }

    private void checkInitialised() {
        if (this.ubi == null) {
            throw new IllegalArgumentException("Skein engine is not initialised.");
        }
    }

    public void update(byte in) {
        this.singleByte[0] = in;
        this.update(this.singleByte, 0, 1);
    }

    public void update(byte[] in, int inOff, int len) {
        this.checkInitialised();
        this.ubi.update(in, inOff, len, this.chain);
    }

    public int doFinal(byte[] out, int outOff) {
        this.checkInitialised();
        if (out.length < outOff + this.outputSizeBytes) {
            throw new OutputLengthException("Output buffer is too short to hold output");
        }
        this.ubiFinal();
        if (this.postMessageParameters != null) {
            for (int i = 0; i < this.postMessageParameters.length; ++i) {
                Parameter param = this.postMessageParameters[i];
                this.ubiComplete(param.getType(), param.getValue());
            }
        }
        int blockSize = this.getBlockSize();
        int blocksRequired = (this.outputSizeBytes + blockSize - 1) / blockSize;
        for (int i = 0; i < blocksRequired; ++i) {
            int toWrite = Math.min(blockSize, this.outputSizeBytes - i * blockSize);
            this.output(i, out, outOff + i * blockSize, toWrite);
        }
        this.reset();
        return this.outputSizeBytes;
    }

    private void output(long outputSequence, byte[] out, int outOff, int outputBytes) {
        byte[] currentBytes = new byte[8];
        Pack.longToLittleEndian(outputSequence, currentBytes, 0);
        long[] outputWords = new long[this.chain.length];
        this.ubiInit(63);
        this.ubi.update(currentBytes, 0, currentBytes.length, outputWords);
        this.ubi.doFinal(outputWords);
        int wordsRequired = (outputBytes + 8 - 1) / 8;
        for (int i = 0; i < wordsRequired; ++i) {
            int toWrite = Math.min(8, outputBytes - i * 8);
            if (toWrite == 8) {
                Pack.longToLittleEndian(outputWords[i], out, outOff + i * 8);
                continue;
            }
            Pack.longToLittleEndian(outputWords[i], currentBytes, 0);
            System.arraycopy(currentBytes, 0, out, outOff + i * 8, toWrite);
        }
    }

    static {
        SkeinEngine.initialState(256, 128, new long[]{-2228972824489528736L, -8629553674646093540L, 1155188648486244218L, -3677226592081559102L});
        SkeinEngine.initialState(256, 160, new long[]{1450197650740764312L, 3081844928540042640L, -3136097061834271170L, 3301952811952417661L});
        SkeinEngine.initialState(256, 224, new long[]{-4176654842910610933L, -8688192972455077604L, -7364642305011795836L, 4056579644589979102L});
        SkeinEngine.initialState(256, 256, new long[]{-243853671043386295L, 3443677322885453875L, -5531612722399640561L, 7662005193972177513L});
        SkeinEngine.initialState(512, 128, new long[]{-6288014694233956526L, 2204638249859346602L, 3502419045458743507L, -4829063503441264548L, 983504137758028059L, 1880512238245786339L, -6715892782214108542L, 7602827311880509485L});
        SkeinEngine.initialState(512, 160, new long[]{2934123928682216849L, -4399710721982728305L, 1684584802963255058L, 5744138295201861711L, 2444857010922934358L, -2807833639722848072L, -5121587834665610502L, 118355523173251694L});
        SkeinEngine.initialState(512, 224, new long[]{-3688341020067007964L, -3772225436291745297L, -8300862168937575580L, 4146387520469897396L, 1106145742801415120L, 7455425944880474941L, -7351063101234211863L, -7048981346965512457L});
        SkeinEngine.initialState(512, 384, new long[]{-6631894876634615969L, -5692838220127733084L, -7099962856338682626L, -2911352911530754598L, 2000907093792408677L, 9140007292425499655L, 6093301768906360022L, 2769176472213098488L});
        SkeinEngine.initialState(512, 512, new long[]{5261240102383538638L, 978932832955457283L, -8083517948103779378L, -7339365279355032399L, 6752626034097301424L, -1531723821829733388L, -7417126464950782685L, -5901786942805128141L});
    }

    private static class Configuration {
        private byte[] bytes = new byte[32];

        public Configuration(long outputSizeBits) {
            this.bytes[0] = 83;
            this.bytes[1] = 72;
            this.bytes[2] = 65;
            this.bytes[3] = 51;
            this.bytes[4] = 1;
            this.bytes[5] = 0;
            Pack.longToLittleEndian(outputSizeBits, this.bytes, 8);
        }

        public byte[] getBytes() {
            return this.bytes;
        }
    }

    public static class Parameter {
        private int type;
        private byte[] value;

        public Parameter(int type, byte[] value) {
            this.type = type;
            this.value = value;
        }

        public int getType() {
            return this.type;
        }

        public byte[] getValue() {
            return this.value;
        }
    }

    private class UBI {
        private final UbiTweak tweak = new UbiTweak();
        private byte[] currentBlock;
        private int currentOffset;
        private long[] message;

        public UBI(int blockSize) {
            this.currentBlock = new byte[blockSize];
            this.message = new long[this.currentBlock.length / 8];
        }

        public void reset(UBI ubi) {
            this.currentBlock = Arrays.clone(ubi.currentBlock, this.currentBlock);
            this.currentOffset = ubi.currentOffset;
            this.message = Arrays.clone(ubi.message, this.message);
            this.tweak.reset(ubi.tweak);
        }

        public void reset(int type) {
            this.tweak.reset();
            this.tweak.setType(type);
            this.currentOffset = 0;
        }

        public void update(byte[] value, int offset, int len, long[] output) {
            int copied = 0;
            while (len > copied) {
                if (this.currentOffset == this.currentBlock.length) {
                    this.processBlock(output);
                    this.tweak.setFirst(false);
                    this.currentOffset = 0;
                }
                int toCopy = Math.min(len - copied, this.currentBlock.length - this.currentOffset);
                System.arraycopy(value, offset + copied, this.currentBlock, this.currentOffset, toCopy);
                copied += toCopy;
                this.currentOffset += toCopy;
                this.tweak.advancePosition(toCopy);
            }
        }

        private void processBlock(long[] output) {
            SkeinEngine.this.threefish.init(true, SkeinEngine.this.chain, this.tweak.getWords());
            Pack.littleEndianToLong(this.currentBlock, 0, this.message);
            SkeinEngine.this.threefish.processBlock(this.message, output);
            for (int i = 0; i < output.length; ++i) {
                int n = i;
                output[n] = output[n] ^ this.message[i];
            }
        }

        public void doFinal(long[] output) {
            for (int i = this.currentOffset; i < this.currentBlock.length; ++i) {
                this.currentBlock[i] = 0;
            }
            this.tweak.setFinal(true);
            this.processBlock(output);
        }
    }

    private static class UbiTweak {
        private static final long LOW_RANGE = 9223372034707292160L;
        private static final long T1_FINAL = Long.MIN_VALUE;
        private static final long T1_FIRST = 0x4000000000000000L;
        private long[] tweak = new long[2];
        private boolean extendedPosition;

        public UbiTweak() {
            this.reset();
        }

        public void reset(UbiTweak tweak) {
            this.tweak = Arrays.clone(tweak.tweak, this.tweak);
            this.extendedPosition = tweak.extendedPosition;
        }

        public void reset() {
            this.tweak[0] = 0L;
            this.tweak[1] = 0L;
            this.extendedPosition = false;
            this.setFirst(true);
        }

        public void setType(int type) {
            this.tweak[1] = this.tweak[1] & 0xFFFFFFC000000000L | ((long)type & 0x3FL) << 56;
        }

        public int getType() {
            return (int)(this.tweak[1] >>> 56 & 0x3FL);
        }

        public void setFirst(boolean first) {
            this.tweak[1] = first ? this.tweak[1] | 0x4000000000000000L : this.tweak[1] & 0xBFFFFFFFFFFFFFFFL;
        }

        public boolean isFirst() {
            return (this.tweak[1] & 0x4000000000000000L) != 0L;
        }

        public void setFinal(boolean last) {
            this.tweak[1] = last ? this.tweak[1] | Long.MIN_VALUE : this.tweak[1] & Long.MAX_VALUE;
        }

        public boolean isFinal() {
            return (this.tweak[1] & Long.MIN_VALUE) != 0L;
        }

        public void advancePosition(int advance) {
            if (this.extendedPosition) {
                long[] parts = new long[]{this.tweak[0] & 0xFFFFFFFFL, this.tweak[0] >>> 32 & 0xFFFFFFFFL, this.tweak[1] & 0xFFFFFFFFL};
                long carry = advance;
                for (int i = 0; i < parts.length; ++i) {
                    parts[i] = carry += parts[i];
                    carry >>>= 32;
                }
                this.tweak[0] = (parts[1] & 0xFFFFFFFFL) << 32 | parts[0] & 0xFFFFFFFFL;
                this.tweak[1] = this.tweak[1] & 0xFFFFFFFF00000000L | parts[2] & 0xFFFFFFFFL;
            } else {
                long position = this.tweak[0];
                this.tweak[0] = position += (long)advance;
                if (position > 9223372034707292160L) {
                    this.extendedPosition = true;
                }
            }
        }

        public long[] getWords() {
            return this.tweak;
        }

        public String toString() {
            return this.getType() + " first: " + this.isFirst() + ", final: " + this.isFinal();
        }
    }
}


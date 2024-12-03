/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.codecs.CodecUtil
 *  org.apache.lucene.store.DataInput
 *  org.apache.lucene.store.InputStreamDataInput
 *  org.apache.lucene.util.IOUtils
 *  org.apache.lucene.util.IntsRef
 */
package org.apache.lucene.analysis.ja.dict;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.apache.lucene.analysis.ja.dict.Dictionary;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.InputStreamDataInput;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.IntsRef;

public abstract class BinaryDictionary
implements Dictionary {
    public static final String DICT_FILENAME_SUFFIX = "$buffer.dat";
    public static final String TARGETMAP_FILENAME_SUFFIX = "$targetMap.dat";
    public static final String POSDICT_FILENAME_SUFFIX = "$posDict.dat";
    public static final String DICT_HEADER = "kuromoji_dict";
    public static final String TARGETMAP_HEADER = "kuromoji_dict_map";
    public static final String POSDICT_HEADER = "kuromoji_dict_pos";
    public static final int VERSION = 1;
    private final ByteBuffer buffer;
    private final int[] targetMapOffsets;
    private final int[] targetMap;
    private final String[] posDict;
    private final String[] inflTypeDict;
    private final String[] inflFormDict;
    public static final int HAS_BASEFORM = 1;
    public static final int HAS_READING = 2;
    public static final int HAS_PRONUNCIATION = 4;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected BinaryDictionary() throws IOException {
        InputStream mapIS = null;
        InputStream dictIS = null;
        InputStream posIS = null;
        IOException priorE = null;
        int[] targetMapOffsets = null;
        int[] targetMap = null;
        String[] posDict = null;
        String[] inflFormDict = null;
        String[] inflTypeDict = null;
        ByteBuffer buffer = null;
        try {
            mapIS = this.getResource(TARGETMAP_FILENAME_SUFFIX);
            mapIS = new BufferedInputStream(mapIS);
            InputStreamDataInput in = new InputStreamDataInput(mapIS);
            CodecUtil.checkHeader((DataInput)in, (String)TARGETMAP_HEADER, (int)1, (int)1);
            targetMap = new int[in.readVInt()];
            targetMapOffsets = new int[in.readVInt()];
            int accum = 0;
            int sourceId = 0;
            for (int ofs = 0; ofs < targetMap.length; ++ofs) {
                int val = in.readVInt();
                if ((val & 1) != 0) {
                    targetMapOffsets[sourceId] = ofs;
                    ++sourceId;
                }
                targetMap[ofs] = accum += val >>> 1;
            }
            if (sourceId + 1 != targetMapOffsets.length) {
                throw new IOException("targetMap file format broken");
            }
            targetMapOffsets[sourceId] = targetMap.length;
            mapIS.close();
            mapIS = null;
            posIS = this.getResource(POSDICT_FILENAME_SUFFIX);
            posIS = new BufferedInputStream(posIS);
            in = new InputStreamDataInput(posIS);
            CodecUtil.checkHeader((DataInput)in, (String)POSDICT_HEADER, (int)1, (int)1);
            int posSize = in.readVInt();
            posDict = new String[posSize];
            inflTypeDict = new String[posSize];
            inflFormDict = new String[posSize];
            for (int j = 0; j < posSize; ++j) {
                posDict[j] = in.readString();
                inflTypeDict[j] = in.readString();
                inflFormDict[j] = in.readString();
                if (inflTypeDict[j].length() == 0) {
                    inflTypeDict[j] = null;
                }
                if (inflFormDict[j].length() != 0) continue;
                inflFormDict[j] = null;
            }
            posIS.close();
            posIS = null;
            dictIS = this.getResource(DICT_FILENAME_SUFFIX);
            in = new InputStreamDataInput(dictIS);
            CodecUtil.checkHeader((DataInput)in, (String)DICT_HEADER, (int)1, (int)1);
            int size = in.readVInt();
            ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(size);
            ReadableByteChannel channel = Channels.newChannel(dictIS);
            int read = channel.read(tmpBuffer);
            if (read != size) {
                throw new EOFException("Cannot read whole dictionary");
            }
            dictIS.close();
            dictIS = null;
            buffer = tmpBuffer.asReadOnlyBuffer();
        }
        catch (IOException ioe) {
            try {
                priorE = ioe;
            }
            catch (Throwable throwable) {
                IOUtils.closeWhileHandlingException((Exception)priorE, (Closeable[])new Closeable[]{mapIS, posIS, dictIS});
                throw throwable;
            }
            IOUtils.closeWhileHandlingException((Exception)priorE, (Closeable[])new Closeable[]{mapIS, posIS, dictIS});
        }
        IOUtils.closeWhileHandlingException((Exception)priorE, (Closeable[])new Closeable[]{mapIS, posIS, dictIS});
        this.targetMap = targetMap;
        this.targetMapOffsets = targetMapOffsets;
        this.posDict = posDict;
        this.inflTypeDict = inflTypeDict;
        this.inflFormDict = inflFormDict;
        this.buffer = buffer;
    }

    protected final InputStream getResource(String suffix) throws IOException {
        return BinaryDictionary.getClassResource(this.getClass(), suffix);
    }

    public static final InputStream getClassResource(Class<?> clazz, String suffix) throws IOException {
        InputStream is = clazz.getResourceAsStream(clazz.getSimpleName() + suffix);
        if (is == null) {
            throw new FileNotFoundException("Not in classpath: " + clazz.getName().replace('.', '/') + suffix);
        }
        return is;
    }

    public void lookupWordIds(int sourceId, IntsRef ref) {
        ref.ints = this.targetMap;
        ref.offset = this.targetMapOffsets[sourceId];
        ref.length = this.targetMapOffsets[sourceId + 1] - ref.offset;
    }

    @Override
    public int getLeftId(int wordId) {
        return this.buffer.getShort(wordId) >>> 3;
    }

    @Override
    public int getRightId(int wordId) {
        return this.buffer.getShort(wordId) >>> 3;
    }

    @Override
    public int getWordCost(int wordId) {
        return this.buffer.getShort(wordId + 2);
    }

    @Override
    public String getBaseForm(int wordId, char[] surfaceForm, int off, int len) {
        if (this.hasBaseFormData(wordId)) {
            int offset = BinaryDictionary.baseFormOffset(wordId);
            int data = this.buffer.get(offset++) & 0xFF;
            int prefix = data >>> 4;
            int suffix = data & 0xF;
            char[] text = new char[prefix + suffix];
            System.arraycopy(surfaceForm, off, text, 0, prefix);
            for (int i = 0; i < suffix; ++i) {
                text[prefix + i] = this.buffer.getChar(offset + (i << 1));
            }
            return new String(text);
        }
        return null;
    }

    @Override
    public String getReading(int wordId, char[] surface, int off, int len) {
        if (this.hasReadingData(wordId)) {
            int readingData;
            int offset = this.readingOffset(wordId);
            return this.readString(offset, readingData >>> 1, ((readingData = this.buffer.get(offset++) & 0xFF) & 1) == 1);
        }
        char[] text = new char[len];
        for (int i = 0; i < len; ++i) {
            char ch = surface[off + i];
            text[i] = ch > '\u3040' && ch < '\u3097' ? (char)(ch + 96) : ch;
        }
        return new String(text);
    }

    @Override
    public String getPartOfSpeech(int wordId) {
        return this.posDict[this.getLeftId(wordId)];
    }

    @Override
    public String getPronunciation(int wordId, char[] surface, int off, int len) {
        if (this.hasPronunciationData(wordId)) {
            int pronunciationData;
            int offset = this.pronunciationOffset(wordId);
            return this.readString(offset, pronunciationData >>> 1, ((pronunciationData = this.buffer.get(offset++) & 0xFF) & 1) == 1);
        }
        return this.getReading(wordId, surface, off, len);
    }

    @Override
    public String getInflectionType(int wordId) {
        return this.inflTypeDict[this.getLeftId(wordId)];
    }

    @Override
    public String getInflectionForm(int wordId) {
        return this.inflFormDict[this.getLeftId(wordId)];
    }

    private static int baseFormOffset(int wordId) {
        return wordId + 4;
    }

    private int readingOffset(int wordId) {
        int offset = BinaryDictionary.baseFormOffset(wordId);
        if (this.hasBaseFormData(wordId)) {
            int baseFormLength = this.buffer.get(offset++) & 0xF;
            return offset + (baseFormLength << 1);
        }
        return offset;
    }

    private int pronunciationOffset(int wordId) {
        if (this.hasReadingData(wordId)) {
            int readingData;
            int offset = this.readingOffset(wordId);
            int readingLength = ((readingData = this.buffer.get(offset++) & 0xFF) & 1) == 0 ? readingData & 0xFE : readingData >>> 1;
            return offset + readingLength;
        }
        return this.readingOffset(wordId);
    }

    private boolean hasBaseFormData(int wordId) {
        return (this.buffer.getShort(wordId) & 1) != 0;
    }

    private boolean hasReadingData(int wordId) {
        return (this.buffer.getShort(wordId) & 2) != 0;
    }

    private boolean hasPronunciationData(int wordId) {
        return (this.buffer.getShort(wordId) & 4) != 0;
    }

    private String readString(int offset, int length, boolean kana) {
        char[] text = new char[length];
        if (kana) {
            for (int i = 0; i < length; ++i) {
                text[i] = (char)(12448 + (this.buffer.get(offset + i) & 0xFF));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                text[i] = this.buffer.getChar(offset + (i << 1));
            }
        }
        return new String(text);
    }
}


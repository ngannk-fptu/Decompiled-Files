/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.codecs.CodecUtil
 *  org.apache.lucene.store.DataInput
 *  org.apache.lucene.store.InputStreamDataInput
 *  org.apache.lucene.util.IOUtils
 */
package org.apache.lucene.analysis.ja.dict;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import org.apache.lucene.analysis.ja.dict.BinaryDictionary;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.InputStreamDataInput;
import org.apache.lucene.util.IOUtils;

public final class CharacterDefinition {
    public static final String FILENAME_SUFFIX = ".dat";
    public static final String HEADER = "kuromoji_cd";
    public static final int VERSION = 1;
    public static final int CLASS_COUNT = CharacterClass.values().length;
    private final byte[] characterCategoryMap = new byte[65536];
    private final boolean[] invokeMap = new boolean[CLASS_COUNT];
    private final boolean[] groupMap = new boolean[CLASS_COUNT];
    public static final byte NGRAM = (byte)CharacterClass.NGRAM.ordinal();
    public static final byte DEFAULT = (byte)CharacterClass.DEFAULT.ordinal();
    public static final byte SPACE = (byte)CharacterClass.SPACE.ordinal();
    public static final byte SYMBOL = (byte)CharacterClass.SYMBOL.ordinal();
    public static final byte NUMERIC = (byte)CharacterClass.NUMERIC.ordinal();
    public static final byte ALPHA = (byte)CharacterClass.ALPHA.ordinal();
    public static final byte CYRILLIC = (byte)CharacterClass.CYRILLIC.ordinal();
    public static final byte GREEK = (byte)CharacterClass.GREEK.ordinal();
    public static final byte HIRAGANA = (byte)CharacterClass.HIRAGANA.ordinal();
    public static final byte KATAKANA = (byte)CharacterClass.KATAKANA.ordinal();
    public static final byte KANJI = (byte)CharacterClass.KANJI.ordinal();
    public static final byte KANJINUMERIC = (byte)CharacterClass.KANJINUMERIC.ordinal();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CharacterDefinition() throws IOException {
        IOException priorE = null;
        InputStream is = null;
        try {
            is = BinaryDictionary.getClassResource(this.getClass(), FILENAME_SUFFIX);
            is = new BufferedInputStream(is);
            InputStreamDataInput in = new InputStreamDataInput(is);
            CodecUtil.checkHeader((DataInput)in, (String)HEADER, (int)1, (int)1);
            in.readBytes(this.characterCategoryMap, 0, this.characterCategoryMap.length);
            for (int i = 0; i < CLASS_COUNT; ++i) {
                byte b = in.readByte();
                this.invokeMap[i] = (b & 1) != 0;
                this.groupMap[i] = (b & 2) != 0;
            }
        }
        catch (IOException ioe) {
            try {
                priorE = ioe;
            }
            catch (Throwable throwable) {
                IOUtils.closeWhileHandlingException((Exception)priorE, (Closeable[])new Closeable[]{is});
                throw throwable;
            }
            IOUtils.closeWhileHandlingException((Exception)priorE, (Closeable[])new Closeable[]{is});
        }
        IOUtils.closeWhileHandlingException((Exception)priorE, (Closeable[])new Closeable[]{is});
    }

    public byte getCharacterClass(char c) {
        return this.characterCategoryMap[c];
    }

    public boolean isInvoke(char c) {
        return this.invokeMap[this.characterCategoryMap[c]];
    }

    public boolean isGroup(char c) {
        return this.groupMap[this.characterCategoryMap[c]];
    }

    public boolean isKanji(char c) {
        byte characterClass = this.characterCategoryMap[c];
        return characterClass == KANJI || characterClass == KANJINUMERIC;
    }

    public static byte lookupCharacterClass(String characterClassName) {
        return (byte)CharacterClass.valueOf(characterClassName).ordinal();
    }

    public static CharacterDefinition getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final CharacterDefinition INSTANCE;

        private SingletonHolder() {
        }

        static {
            try {
                INSTANCE = new CharacterDefinition();
            }
            catch (IOException ioe) {
                throw new RuntimeException("Cannot load CharacterDefinition.", ioe);
            }
        }
    }

    private static enum CharacterClass {
        NGRAM,
        DEFAULT,
        SPACE,
        SYMBOL,
        NUMERIC,
        ALPHA,
        CYRILLIC,
        GREEK,
        HIRAGANA,
        KATAKANA,
        KANJI,
        KANJINUMERIC;

    }
}


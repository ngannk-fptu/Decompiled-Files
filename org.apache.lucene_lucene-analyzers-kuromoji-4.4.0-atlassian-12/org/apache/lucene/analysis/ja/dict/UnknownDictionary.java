/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.ja.dict;

import java.io.IOException;
import org.apache.lucene.analysis.ja.dict.BinaryDictionary;
import org.apache.lucene.analysis.ja.dict.CharacterDefinition;

public final class UnknownDictionary
extends BinaryDictionary {
    private final CharacterDefinition characterDefinition = CharacterDefinition.getInstance();

    private UnknownDictionary() throws IOException {
    }

    public int lookup(char[] text, int offset, int len) {
        if (!this.characterDefinition.isGroup(text[offset])) {
            return 1;
        }
        byte characterIdOfFirstCharacter = this.characterDefinition.getCharacterClass(text[offset]);
        int length = 1;
        for (int i = 1; i < len && characterIdOfFirstCharacter == this.characterDefinition.getCharacterClass(text[offset + i]); ++i) {
            ++length;
        }
        return length;
    }

    public CharacterDefinition getCharacterDefinition() {
        return this.characterDefinition;
    }

    @Override
    public String getReading(int wordId, char[] surface, int off, int len) {
        return null;
    }

    @Override
    public String getInflectionType(int wordId) {
        return null;
    }

    @Override
    public String getInflectionForm(int wordId) {
        return null;
    }

    public static UnknownDictionary getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final UnknownDictionary INSTANCE;

        private SingletonHolder() {
        }

        static {
            try {
                INSTANCE = new UnknownDictionary();
            }
            catch (IOException ioe) {
                throw new RuntimeException("Cannot load UnknownDictionary.", ioe);
            }
        }
    }
}


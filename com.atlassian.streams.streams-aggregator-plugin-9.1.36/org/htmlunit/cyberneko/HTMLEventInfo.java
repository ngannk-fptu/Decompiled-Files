/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko;

import org.htmlunit.cyberneko.xerces.xni.Augmentations;

public interface HTMLEventInfo
extends Augmentations {
    public int getBeginLineNumber();

    public int getBeginColumnNumber();

    public int getBeginCharacterOffset();

    public int getEndLineNumber();

    public int getEndColumnNumber();

    public int getEndCharacterOffset();

    public boolean isSynthesized();

    public static class SynthesizedItem
    implements HTMLEventInfo {
        @Override
        public int getBeginLineNumber() {
            return -1;
        }

        @Override
        public int getBeginColumnNumber() {
            return -1;
        }

        @Override
        public int getBeginCharacterOffset() {
            return -1;
        }

        @Override
        public int getEndLineNumber() {
            return -1;
        }

        @Override
        public int getEndColumnNumber() {
            return -1;
        }

        @Override
        public int getEndCharacterOffset() {
            return -1;
        }

        @Override
        public boolean isSynthesized() {
            return true;
        }

        public String toString() {
            return "synthesized";
        }
    }
}


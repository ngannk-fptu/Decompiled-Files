/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

public class RecoveryScannerData {
    public int insertedTokensPtr = -1;
    public int[][] insertedTokens;
    public int[] insertedTokensPosition;
    public boolean[] insertedTokenUsed;
    public int replacedTokensPtr = -1;
    public int[][] replacedTokens;
    public int[] replacedTokensStart;
    public int[] replacedTokensEnd;
    public boolean[] replacedTokenUsed;
    public int removedTokensPtr = -1;
    public int[] removedTokensStart;
    public int[] removedTokensEnd;
    public boolean[] removedTokenUsed;

    public RecoveryScannerData removeUnused() {
        int i;
        if (this.insertedTokens != null) {
            int newInsertedTokensPtr = -1;
            i = 0;
            while (i <= this.insertedTokensPtr) {
                if (this.insertedTokenUsed[i]) {
                    this.insertedTokens[++newInsertedTokensPtr] = this.insertedTokens[i];
                    this.insertedTokensPosition[newInsertedTokensPtr] = this.insertedTokensPosition[i];
                    this.insertedTokenUsed[newInsertedTokensPtr] = this.insertedTokenUsed[i];
                }
                ++i;
            }
            this.insertedTokensPtr = newInsertedTokensPtr;
        }
        if (this.replacedTokens != null) {
            int newReplacedTokensPtr = -1;
            i = 0;
            while (i <= this.replacedTokensPtr) {
                if (this.replacedTokenUsed[i]) {
                    this.replacedTokens[++newReplacedTokensPtr] = this.replacedTokens[i];
                    this.replacedTokensStart[newReplacedTokensPtr] = this.replacedTokensStart[i];
                    this.replacedTokensEnd[newReplacedTokensPtr] = this.replacedTokensEnd[i];
                    this.replacedTokenUsed[newReplacedTokensPtr] = this.replacedTokenUsed[i];
                }
                ++i;
            }
            this.replacedTokensPtr = newReplacedTokensPtr;
        }
        if (this.removedTokensStart != null) {
            int newRemovedTokensPtr = -1;
            i = 0;
            while (i <= this.removedTokensPtr) {
                if (this.removedTokenUsed[i]) {
                    this.removedTokensStart[++newRemovedTokensPtr] = this.removedTokensStart[i];
                    this.removedTokensEnd[newRemovedTokensPtr] = this.removedTokensEnd[i];
                    this.removedTokenUsed[newRemovedTokensPtr] = this.removedTokenUsed[i];
                }
                ++i;
            }
            this.removedTokensPtr = newRemovedTokensPtr;
        }
        return this;
    }
}


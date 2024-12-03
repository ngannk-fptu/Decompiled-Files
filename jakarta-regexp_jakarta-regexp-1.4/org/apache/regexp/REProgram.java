/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.io.Serializable;

public class REProgram
implements Serializable {
    static final int OPT_HASBACKREFS = 1;
    static final int OPT_HASBOL = 2;
    char[] instruction;
    int lenInstruction;
    char[] prefix;
    int flags;
    int maxParens = -1;

    public REProgram(char[] cArray) {
        this(cArray, cArray.length);
    }

    public REProgram(int n, char[] cArray) {
        this(cArray, cArray.length);
        this.maxParens = n;
    }

    public REProgram(char[] cArray, int n) {
        this.setInstructions(cArray, n);
    }

    public char[] getInstructions() {
        if (this.lenInstruction != 0) {
            char[] cArray = new char[this.lenInstruction];
            System.arraycopy(this.instruction, 0, cArray, 0, this.lenInstruction);
            return cArray;
        }
        return null;
    }

    /*
     * Enabled aggressive block sorting
     */
    public void setInstructions(char[] cArray, int n) {
        int n2;
        this.instruction = cArray;
        this.lenInstruction = n;
        this.flags = 0;
        this.prefix = null;
        if (cArray == null) return;
        if (n == 0) return;
        if (n >= 3 && cArray[0] == '|' && cArray[(n2 = cArray[2]) + 0] == 'E' && n >= 6) {
            char c = cArray[3];
            if (c == 'A') {
                char c2 = cArray[4];
                this.prefix = new char[c2];
                System.arraycopy(cArray, 6, this.prefix, 0, c2);
            } else if (c == '^') {
                this.flags |= 2;
            }
        }
        n2 = 0;
        while (n2 < n) {
            switch (cArray[n2 + 0]) {
                case '[': {
                    n2 += cArray[n2 + 1] * 2;
                    break;
                }
                case 'A': {
                    n2 += cArray[n2 + 1];
                    break;
                }
                case '#': {
                    this.flags |= 1;
                    return;
                }
            }
            n2 += 3;
        }
    }
}


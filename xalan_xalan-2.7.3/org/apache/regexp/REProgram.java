/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

public class REProgram {
    static final int OPT_HASBACKREFS = 1;
    char[] instruction;
    int lenInstruction;
    char[] prefix;
    int flags;

    public REProgram(char[] cArray) {
        this(cArray, cArray.length);
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
        if (n >= 3 && cArray[0] == '|' && cArray[n2 = cArray[2]] == 'E' && n >= 6 && cArray[3] == 'A') {
            char c = cArray[4];
            this.prefix = new char[c];
            System.arraycopy(cArray, 6, this.prefix, 0, c);
        }
        n2 = 0;
        while (n2 < n) {
            switch (cArray[n2]) {
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


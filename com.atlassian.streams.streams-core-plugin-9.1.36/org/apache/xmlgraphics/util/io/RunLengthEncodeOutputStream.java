/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlgraphics.util.io.Finalizable;

public class RunLengthEncodeOutputStream
extends FilterOutputStream
implements Finalizable {
    private static final int MAX_SEQUENCE_COUNT = 127;
    private static final int END_OF_DATA = 128;
    private static final int BYTE_MAX = 256;
    private static final int NOT_IDENTIFY_SEQUENCE = 0;
    private static final int START_SEQUENCE = 1;
    private static final int IN_SEQUENCE = 2;
    private static final int NOT_IN_SEQUENCE = 3;
    private int runCount;
    private int isSequence = 0;
    private byte[] runBuffer = new byte[128];

    public RunLengthEncodeOutputStream(OutputStream out) {
        super(out);
    }

    public void write(byte b) throws IOException {
        this.runBuffer[this.runCount] = b;
        block0 : switch (this.runCount) {
            case 0: {
                this.runCount = 0;
                this.isSequence = 0;
                ++this.runCount;
                break;
            }
            case 1: {
                if (this.runBuffer[this.runCount] != this.runBuffer[this.runCount - 1]) {
                    this.isSequence = 3;
                }
                ++this.runCount;
                break;
            }
            case 2: {
                this.isSequence = this.runBuffer[this.runCount] != this.runBuffer[this.runCount - 1] ? 3 : (this.isSequence == 3 ? 1 : 2);
                ++this.runCount;
                break;
            }
            case 127: {
                if (this.isSequence == 2) {
                    this.out.write(130);
                    this.out.write(this.runBuffer[this.runCount - 1]);
                    this.runBuffer[0] = this.runBuffer[this.runCount];
                    this.runCount = 1;
                } else {
                    this.out.write(127);
                    this.out.write(this.runBuffer, 0, this.runCount + 1);
                    this.runCount = 0;
                }
                this.isSequence = 0;
                break;
            }
            default: {
                switch (this.isSequence) {
                    case 3: {
                        if (this.runBuffer[this.runCount] == this.runBuffer[this.runCount - 1]) {
                            this.isSequence = 1;
                        }
                        ++this.runCount;
                        break block0;
                    }
                    case 1: {
                        if (this.runBuffer[this.runCount] == this.runBuffer[this.runCount - 1]) {
                            this.out.write(this.runCount - 3);
                            this.out.write(this.runBuffer, 0, this.runCount - 2);
                            this.runBuffer[0] = this.runBuffer[this.runCount];
                            this.runBuffer[1] = this.runBuffer[this.runCount];
                            this.runBuffer[2] = this.runBuffer[this.runCount];
                            this.runCount = 3;
                            this.isSequence = 2;
                            break block0;
                        }
                        this.isSequence = 3;
                        ++this.runCount;
                        break block0;
                    }
                }
                if (this.runBuffer[this.runCount] != this.runBuffer[this.runCount - 1]) {
                    this.out.write(256 - (this.runCount - 1));
                    this.out.write(this.runBuffer[this.runCount - 1]);
                    this.runBuffer[0] = this.runBuffer[this.runCount];
                    this.runCount = 1;
                    this.isSequence = 0;
                    break;
                }
                ++this.runCount;
            }
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (byte aB : b) {
            this.write(aB);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(b[off + i]);
        }
    }

    @Override
    public void finalizeStream() throws IOException {
        switch (this.isSequence) {
            case 2: {
                this.out.write(256 - (this.runCount - 1));
                this.out.write(this.runBuffer[this.runCount - 1]);
                break;
            }
            default: {
                this.out.write(this.runCount - 1);
                this.out.write(this.runBuffer, 0, this.runCount);
            }
        }
        this.out.write(128);
        this.flush();
        if (this.out instanceof Finalizable) {
            ((Finalizable)((Object)this.out)).finalizeStream();
        }
    }

    @Override
    public void close() throws IOException {
        this.finalizeStream();
        super.close();
    }
}


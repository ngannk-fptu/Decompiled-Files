/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class UnconditionalFlowInfo
extends FlowInfo {
    public static final boolean COVERAGE_TEST_FLAG = false;
    public static int CoverageTestId;
    public long definiteInits;
    public long potentialInits;
    public long nullBit1;
    public long nullBit2;
    public long nullBit3;
    public long nullBit4;
    public long iNBit;
    public long iNNBit;
    public static final int extraLength = 8;
    public long[][] extra;
    public int maxFieldCount;
    public static final int BitCacheSize = 64;
    public static final int IN = 6;
    public static final int INN = 7;

    public static UnconditionalFlowInfo fakeInitializedFlowInfo(int localsCount, int maxFieldCount) {
        UnconditionalFlowInfo flowInfo = new UnconditionalFlowInfo();
        flowInfo.maxFieldCount = maxFieldCount;
        int i = 0;
        while (i < localsCount) {
            flowInfo.markAsDefinitelyAssigned(i + maxFieldCount);
            ++i;
        }
        return flowInfo;
    }

    @Override
    public FlowInfo addInitializationsFrom(FlowInfo inits) {
        return this.addInfoFrom(inits, true);
    }

    @Override
    public FlowInfo addNullInfoFrom(FlowInfo inits) {
        return this.addInfoFrom(inits, false);
    }

    private FlowInfo addInfoFrom(FlowInfo inits, boolean handleInits) {
        long na1;
        long nb1;
        long nb3;
        long b3;
        long na2;
        long na3;
        long na4;
        long nb4;
        long b4;
        long nb2;
        long b2;
        long b1;
        long a4;
        long a3;
        long a2;
        long a1;
        boolean otherHasNulls;
        if (this == DEAD_END) {
            return this;
        }
        if (inits == DEAD_END) {
            return this;
        }
        UnconditionalFlowInfo otherInits = inits.unconditionalInits();
        if (handleInits) {
            this.definiteInits |= otherInits.definiteInits;
            this.potentialInits |= otherInits.potentialInits;
        }
        boolean thisHadNulls = (this.tagBits & 4) != 0;
        boolean bl = otherHasNulls = (otherInits.tagBits & 4) != 0;
        if (otherHasNulls) {
            if (!thisHadNulls) {
                this.nullBit1 = otherInits.nullBit1;
                this.nullBit2 = otherInits.nullBit2;
                this.nullBit3 = otherInits.nullBit3;
                this.nullBit4 = otherInits.nullBit4;
                this.iNBit = otherInits.iNBit;
                this.iNNBit = otherInits.iNNBit;
            } else {
                a1 = this.nullBit1;
                a2 = this.nullBit2;
                a3 = this.nullBit3;
                a4 = this.nullBit4;
                long protNN1111 = a1 & a2 & a3 & a4;
                long acceptNonNull = otherInits.iNNBit;
                long acceptNull = otherInits.iNBit | protNN1111;
                long dontResetToStart = protNN1111 ^ 0xFFFFFFFFFFFFFFFFL | acceptNonNull;
                a1 &= dontResetToStart;
                a2 = dontResetToStart & acceptNull & a2;
                a3 = dontResetToStart & acceptNonNull & a3;
                a1 &= a2 | a3 | (a4 &= dontResetToStart);
                b1 = otherInits.nullBit1;
                b2 = otherInits.nullBit2;
                nb2 = b2 ^ 0xFFFFFFFFFFFFFFFFL;
                b4 = otherInits.nullBit4;
                nb4 = b4 ^ 0xFFFFFFFFFFFFFFFFL;
                na4 = a4 ^ 0xFFFFFFFFFFFFFFFFL;
                na3 = a3 ^ 0xFFFFFFFFFFFFFFFFL;
                na2 = a2 ^ 0xFFFFFFFFFFFFFFFFL;
                b3 = otherInits.nullBit3;
                nb3 = b3 ^ 0xFFFFFFFFFFFFFFFFL;
                this.nullBit1 = b1 | a1 & (a3 & a4 & nb2 & nb4 | (na4 | na3) & (na2 & nb2 | a2 & nb3 & nb4));
                nb1 = b1 ^ 0xFFFFFFFFFFFFFFFFL;
                na1 = a1 ^ 0xFFFFFFFFFFFFFFFFL;
                this.nullBit2 = b2 & (nb4 | nb3) | na3 & na4 & b2 | a2 & (nb3 & nb4 | nb1 & (na3 | na1) | a1 & b2);
                this.nullBit3 = b3 & (nb1 & (b2 | a2 | na1) | b1 & (b4 | nb2 | a1 & a3) | na1 & na2 & na4) | a3 & nb2 & nb4 | nb1 & ((na2 & a4 | na1) & a3 | a1 & na2 & na4 & b2);
                this.nullBit4 = nb1 & (a4 & (na3 & nb3 | (a3 | na2) & nb2) | a1 & (a3 & nb2 & b4 | a2 & b2 & (b4 | a3 & na4 & nb3))) | b1 & (a3 & a4 & b4 | na2 & na4 & nb3 & b4 | a2 & ((b3 | a4) & b4 | na3 & a4 & b2 & b3) | na1 & (b4 | (a4 | a2) & b2 & b3)) | (na1 & (na3 & nb3 | na2 & nb2) | a1 & (nb2 & nb3 | a2 & a3)) & b4;
                this.iNBit &= otherInits.iNBit;
                this.iNNBit &= otherInits.iNNBit;
            }
            this.tagBits |= 4;
        }
        if (this.extra != null || otherInits.extra != null) {
            int mergeLimit = 0;
            int copyLimit = 0;
            if (this.extra != null) {
                if (otherInits.extra != null) {
                    int length = this.extra[0].length;
                    int otherLength = otherInits.extra[0].length;
                    if (length < otherLength) {
                        this.growSpace(otherLength, 0, length);
                        mergeLimit = length;
                        copyLimit = otherLength;
                    } else {
                        mergeLimit = otherLength;
                    }
                }
            } else if (otherInits.extra != null) {
                int j;
                this.extra = new long[8][];
                int otherLength = otherInits.extra[0].length;
                this.extra[0] = new long[otherLength];
                this.extra[1] = new long[otherLength];
                if (handleInits) {
                    System.arraycopy(otherInits.extra[0], 0, this.extra[0], 0, otherLength);
                    System.arraycopy(otherInits.extra[1], 0, this.extra[1], 0, otherLength);
                }
                if (otherHasNulls) {
                    j = 2;
                    while (j < 8) {
                        this.extra[j] = new long[otherLength];
                        System.arraycopy(otherInits.extra[j], 0, this.extra[j], 0, otherLength);
                        ++j;
                    }
                    if ((this.tagBits & 0x40) != 0) {
                        Arrays.fill(this.extra[6], 0, otherLength, -1L);
                        Arrays.fill(this.extra[7], 0, otherLength, -1L);
                    }
                } else {
                    j = 2;
                    while (j < 8) {
                        this.extra[j] = new long[otherLength];
                        ++j;
                    }
                    System.arraycopy(otherInits.extra[6], 0, this.extra[6], 0, otherLength);
                    System.arraycopy(otherInits.extra[7], 0, this.extra[7], 0, otherLength);
                }
            }
            if (handleInits) {
                int i = 0;
                while (i < mergeLimit) {
                    long[] lArray = this.extra[0];
                    int n = i;
                    lArray[n] = lArray[n] | otherInits.extra[0][i];
                    long[] lArray2 = this.extra[1];
                    int n2 = i;
                    lArray2[n2] = lArray2[n2] | otherInits.extra[1][i];
                    ++i;
                }
                while (i < copyLimit) {
                    this.extra[0][i] = otherInits.extra[0][i];
                    this.extra[1][i] = otherInits.extra[1][i];
                    ++i;
                }
            }
            if (!thisHadNulls) {
                if (copyLimit < mergeLimit) {
                    copyLimit = mergeLimit;
                }
                mergeLimit = 0;
            }
            if (!otherHasNulls) {
                copyLimit = 0;
                mergeLimit = 0;
            }
            int i = 0;
            while (i < mergeLimit) {
                a1 = this.extra[2][i];
                a2 = this.extra[3][i];
                a3 = this.extra[4][i];
                a4 = this.extra[5][i];
                long protNN1111 = a1 & a2 & a3 & a4;
                long acceptNonNull = otherInits.extra[7][i];
                long acceptNull = otherInits.extra[6][i] | protNN1111;
                long dontResetToStart = protNN1111 ^ 0xFFFFFFFFFFFFFFFFL | acceptNonNull;
                a1 &= dontResetToStart;
                a2 = dontResetToStart & acceptNull & a2;
                a3 = dontResetToStart & acceptNonNull & a3;
                a1 &= a2 | a3 | (a4 &= dontResetToStart);
                b1 = otherInits.extra[2][i];
                b2 = otherInits.extra[3][i];
                nb2 = b2 ^ 0xFFFFFFFFFFFFFFFFL;
                b4 = otherInits.extra[5][i];
                nb4 = b4 ^ 0xFFFFFFFFFFFFFFFFL;
                na4 = a4 ^ 0xFFFFFFFFFFFFFFFFL;
                na3 = a3 ^ 0xFFFFFFFFFFFFFFFFL;
                na2 = a2 ^ 0xFFFFFFFFFFFFFFFFL;
                b3 = otherInits.extra[4][i];
                nb3 = b3 ^ 0xFFFFFFFFFFFFFFFFL;
                this.extra[2][i] = b1 | a1 & (a3 & a4 & nb2 & nb4 | (na4 | na3) & (na2 & nb2 | a2 & nb3 & nb4));
                nb1 = b1 ^ 0xFFFFFFFFFFFFFFFFL;
                na1 = a1 ^ 0xFFFFFFFFFFFFFFFFL;
                this.extra[3][i] = b2 & (nb4 | nb3) | na3 & na4 & b2 | a2 & (nb3 & nb4 | nb1 & (na3 | na1) | a1 & b2);
                this.extra[4][i] = b3 & (nb1 & (b2 | a2 | na1) | b1 & (b4 | nb2 | a1 & a3) | na1 & na2 & na4) | a3 & nb2 & nb4 | nb1 & ((na2 & a4 | na1) & a3 | a1 & na2 & na4 & b2);
                this.extra[5][i] = nb1 & (a4 & (na3 & nb3 | (a3 | na2) & nb2) | a1 & (a3 & nb2 & b4 | a2 & b2 & (b4 | a3 & na4 & nb3))) | b1 & (a3 & a4 & b4 | na2 & na4 & nb3 & b4 | a2 & ((b3 | a4) & b4 | na3 & a4 & b2 & b3) | na1 & (b4 | (a4 | a2) & b2 & b3)) | (na1 & (na3 & nb3 | na2 & nb2) | a1 & (nb2 & nb3 | a2 & a3)) & b4;
                long[] lArray = this.extra[6];
                int n = i;
                lArray[n] = lArray[n] & otherInits.extra[6][i];
                long[] lArray3 = this.extra[7];
                int n3 = i;
                lArray3[n3] = lArray3[n3] & otherInits.extra[7][i];
                ++i;
            }
            while (i < copyLimit) {
                int j = 2;
                while (j < 8) {
                    this.extra[j][i] = otherInits.extra[j][i];
                    ++j;
                }
                ++i;
            }
        }
        return this;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public FlowInfo addPotentialInitializationsFrom(FlowInfo inits) {
        block7: {
            block6: {
                if (this == UnconditionalFlowInfo.DEAD_END) {
                    return this;
                }
                if (inits == UnconditionalFlowInfo.DEAD_END) {
                    return this;
                }
                otherInits = inits.unconditionalInits();
                this.potentialInits |= otherInits.potentialInits;
                if (this.extra == null) break block6;
                if (otherInits.extra == null) break block7;
                i = 0;
                length = this.extra[0].length;
                otherLength = otherInits.extra[0].length;
                if (length >= otherLength) ** GOTO lbl29
                this.growSpace(otherLength, 0, length);
                while (i < length) {
                    v0 = this.extra[1];
                    v1 = i;
                    v0[v1] = v0[v1] | otherInits.extra[1][i];
                    ++i;
                }
                while (i < otherLength) {
                    this.extra[1][i] = otherInits.extra[1][i];
                    ++i;
                }
                break block7;
lbl-1000:
                // 1 sources

                {
                    v2 = this.extra[1];
                    v3 = i;
                    v2[v3] = v2[v3] | otherInits.extra[1][i];
                    ++i;
lbl29:
                    // 2 sources

                    ** while (i < otherLength)
                }
lbl30:
                // 1 sources

                break block7;
            }
            if (otherInits.extra != null) {
                otherLength = otherInits.extra[0].length;
                this.createExtraSpace(otherLength);
                System.arraycopy(otherInits.extra[1], 0, this.extra[1], 0, otherLength);
            }
        }
        this.addPotentialNullInfoFrom(otherInits);
        return this;
    }

    public UnconditionalFlowInfo addPotentialNullInfoFrom(UnconditionalFlowInfo otherInits) {
        long na1;
        long nb1;
        long nb3;
        long na3;
        long na4;
        long na2;
        long a2;
        long b3;
        long b1;
        long nb4;
        long b4;
        long nb2;
        long b2;
        long a4;
        long a3;
        long a1;
        if ((this.tagBits & 3) != 0 || (otherInits.tagBits & 3) != 0 || (otherInits.tagBits & 4) == 0) {
            return this;
        }
        boolean thisHadNulls = (this.tagBits & 4) != 0;
        boolean thisHasNulls = false;
        if (thisHadNulls) {
            a1 = this.nullBit1;
            a3 = this.nullBit3;
            a4 = this.nullBit4;
            b2 = otherInits.nullBit2;
            nb2 = b2 ^ 0xFFFFFFFFFFFFFFFFL;
            b4 = otherInits.nullBit4;
            nb4 = b4 ^ 0xFFFFFFFFFFFFFFFFL;
            b1 = otherInits.nullBit1;
            b3 = otherInits.nullBit3;
            a2 = this.nullBit2;
            na2 = a2 ^ 0xFFFFFFFFFFFFFFFFL;
            na4 = a4 ^ 0xFFFFFFFFFFFFFFFFL;
            na3 = a3 ^ 0xFFFFFFFFFFFFFFFFL;
            nb3 = b3 ^ 0xFFFFFFFFFFFFFFFFL;
            this.nullBit1 = a1 & (a3 & a4 & (nb2 & nb4 | b1 & b3) | na2 & (b1 & b3 | (na4 | na3) & nb2) | a2 & ((na4 | na3) & (nb3 & nb4 | b1 & b2)));
            nb1 = b1 ^ 0xFFFFFFFFFFFFFFFFL;
            na1 = a1 ^ 0xFFFFFFFFFFFFFFFFL;
            this.nullBit2 = b2 & (nb3 | nb1) | a2 & (nb3 & nb4 | b2 | na3 | na1);
            this.nullBit3 = b3 & (nb1 & b2 | a2 & (nb2 | a3) | na1 & nb2 | a1 & na2 & na4 & b1) | a3 & (nb2 & nb4 | na2 & a4 | na1) | a1 & na2 & na4 & b2;
            this.nullBit4 = na3 & (nb1 & nb3 & b4 | a4 & (nb3 | b1 & b2)) | nb2 & (na3 & b1 & nb3 | na2 & (nb1 & b4 | b1 & nb3 | a4)) | a3 & (a4 & (nb2 | b1 & b3) | a1 & a2 & (nb1 & b4 | na4 & (b2 | b1) & nb3));
            if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0L) {
                thisHasNulls = true;
            }
        } else {
            this.nullBit1 = 0L;
            b2 = otherInits.nullBit2;
            b3 = otherInits.nullBit3;
            nb3 = b3 ^ 0xFFFFFFFFFFFFFFFFL;
            b1 = otherInits.nullBit1;
            nb1 = b1 ^ 0xFFFFFFFFFFFFFFFFL;
            this.nullBit2 = b2 & (nb3 | nb1);
            nb2 = b2 ^ 0xFFFFFFFFFFFFFFFFL;
            this.nullBit3 = b3 & (nb1 | nb2);
            b4 = otherInits.nullBit4;
            this.nullBit4 = (b1 ^ 0xFFFFFFFFFFFFFFFFL) & (b3 ^ 0xFFFFFFFFFFFFFFFFL) & b4 | (b2 ^ 0xFFFFFFFFFFFFFFFFL) & (b1 & (b3 ^ 0xFFFFFFFFFFFFFFFFL) | (b1 ^ 0xFFFFFFFFFFFFFFFFL) & b4);
            if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0L) {
                thisHasNulls = true;
            }
        }
        if (otherInits.extra != null) {
            int mergeLimit = 0;
            int copyLimit = otherInits.extra[0].length;
            if (this.extra == null) {
                this.createExtraSpace(copyLimit);
            } else {
                mergeLimit = copyLimit;
                if (mergeLimit > this.extra[0].length) {
                    mergeLimit = this.extra[0].length;
                    this.growSpace(copyLimit, 0, mergeLimit);
                    if (!thisHadNulls) {
                        mergeLimit = 0;
                    }
                }
            }
            int i = 0;
            while (i < mergeLimit) {
                a1 = this.extra[2][i];
                a3 = this.extra[4][i];
                a4 = this.extra[5][i];
                b2 = otherInits.extra[3][i];
                nb2 = b2 ^ 0xFFFFFFFFFFFFFFFFL;
                b4 = otherInits.extra[5][i];
                nb4 = b4 ^ 0xFFFFFFFFFFFFFFFFL;
                b1 = otherInits.extra[2][i];
                b3 = otherInits.extra[4][i];
                a2 = this.extra[3][i];
                na2 = a2 ^ 0xFFFFFFFFFFFFFFFFL;
                na4 = a4 ^ 0xFFFFFFFFFFFFFFFFL;
                na3 = a3 ^ 0xFFFFFFFFFFFFFFFFL;
                nb3 = b3 ^ 0xFFFFFFFFFFFFFFFFL;
                this.extra[2][i] = a1 & (a3 & a4 & (nb2 & nb4 | b1 & b3) | na2 & (b1 & b3 | (na4 | na3) & nb2) | a2 & ((na4 | na3) & (nb3 & nb4 | b1 & b2)));
                nb1 = b1 ^ 0xFFFFFFFFFFFFFFFFL;
                na1 = a1 ^ 0xFFFFFFFFFFFFFFFFL;
                this.extra[3][i] = b2 & (nb3 | nb1) | a2 & (nb3 & nb4 | b2 | na3 | na1);
                this.extra[4][i] = b3 & (nb1 & b2 | a2 & (nb2 | a3) | na1 & nb2 | a1 & na2 & na4 & b1) | a3 & (nb2 & nb4 | na2 & a4 | na1) | a1 & na2 & na4 & b2;
                this.extra[5][i] = na3 & (nb1 & nb3 & b4 | a4 & (nb3 | b1 & b2)) | nb2 & (na3 & b1 & nb3 | na2 & (nb1 & b4 | b1 & nb3 | a4)) | a3 & (a4 & (nb2 | b1 & b3) | a1 & a2 & (nb1 & b4 | na4 & (b2 | b1) & nb3));
                if ((this.extra[3][i] | this.extra[4][i] | this.extra[5][i]) != 0L) {
                    thisHasNulls = true;
                }
                ++i;
            }
            while (i < copyLimit) {
                this.extra[2][i] = 0L;
                b2 = otherInits.extra[3][i];
                b3 = otherInits.extra[4][i];
                nb3 = b3 ^ 0xFFFFFFFFFFFFFFFFL;
                b1 = otherInits.extra[2][i];
                nb1 = b1 ^ 0xFFFFFFFFFFFFFFFFL;
                this.extra[3][i] = b2 & (nb3 | nb1);
                nb2 = b2 ^ 0xFFFFFFFFFFFFFFFFL;
                this.extra[4][i] = b3 & (nb1 | nb2);
                b4 = otherInits.extra[5][i];
                this.extra[5][i] = (b1 ^ 0xFFFFFFFFFFFFFFFFL) & (b3 ^ 0xFFFFFFFFFFFFFFFFL) & b4 | (b2 ^ 0xFFFFFFFFFFFFFFFFL) & (b1 & (b3 ^ 0xFFFFFFFFFFFFFFFFL) | (b1 ^ 0xFFFFFFFFFFFFFFFFL) & b4);
                if ((this.extra[3][i] | this.extra[4][i] | this.extra[5][i]) != 0L) {
                    thisHasNulls = true;
                }
                ++i;
            }
        }
        this.tagBits = thisHasNulls ? (this.tagBits |= 4) : (this.tagBits &= 4);
        return this;
    }

    @Override
    public final boolean cannotBeDefinitelyNullOrNonNull(LocalVariableBinding local) {
        if ((this.tagBits & 4) == 0 || (local.type.tagBits & 2L) != 0L) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (((this.nullBit1 ^ 0xFFFFFFFFFFFFFFFFL) & (this.nullBit2 & this.nullBit3 | this.nullBit4) | (this.nullBit2 ^ 0xFFFFFFFFFFFFFFFFL) & (this.nullBit3 ^ 0xFFFFFFFFFFFFFFFFL) & this.nullBit4) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[0].length) {
            return false;
        }
        long a2 = this.extra[3][vectorIndex];
        long a3 = this.extra[4][vectorIndex];
        long a4 = this.extra[5][vectorIndex];
        return (((this.extra[2][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL) & (a2 & a3 | a4) | (a2 ^ 0xFFFFFFFFFFFFFFFFL) & (a3 ^ 0xFFFFFFFFFFFFFFFFL) & a4) & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean cannotBeNull(LocalVariableBinding local) {
        if ((this.tagBits & 4) == 0 || (local.type.tagBits & 2L) != 0L) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit3 & (this.nullBit2 & this.nullBit4 | this.nullBit2 ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[0].length) {
            return false;
        }
        return (this.extra[2][vectorIndex] & this.extra[4][vectorIndex] & (this.extra[3][vectorIndex] & this.extra[5][vectorIndex] | this.extra[3][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean canOnlyBeNull(LocalVariableBinding local) {
        if ((this.tagBits & 4) == 0 || (local.type.tagBits & 2L) != 0L) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit2 & (this.nullBit3 ^ 0xFFFFFFFFFFFFFFFFL | this.nullBit4 ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[0].length) {
            return false;
        }
        return (this.extra[2][vectorIndex] & this.extra[3][vectorIndex] & (this.extra[4][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL | this.extra[5][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position % 64) != 0L;
    }

    @Override
    public FlowInfo copy() {
        boolean hasNullInfo;
        if (this == DEAD_END) {
            return this;
        }
        UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
        copy.definiteInits = this.definiteInits;
        copy.potentialInits = this.potentialInits;
        boolean bl = hasNullInfo = (this.tagBits & 4) != 0;
        if (hasNullInfo) {
            copy.nullBit1 = this.nullBit1;
            copy.nullBit2 = this.nullBit2;
            copy.nullBit3 = this.nullBit3;
            copy.nullBit4 = this.nullBit4;
        }
        copy.iNBit = this.iNBit;
        copy.iNNBit = this.iNNBit;
        copy.tagBits = this.tagBits;
        copy.maxFieldCount = this.maxFieldCount;
        if (this.extra != null) {
            copy.extra = new long[8][];
            int length = this.extra[0].length;
            copy.extra[0] = new long[length];
            System.arraycopy(this.extra[0], 0, copy.extra[0], 0, length);
            copy.extra[1] = new long[length];
            System.arraycopy(this.extra[1], 0, copy.extra[1], 0, length);
            if (hasNullInfo) {
                int j = 2;
                while (j < 6) {
                    copy.extra[j] = new long[length];
                    System.arraycopy(this.extra[j], 0, copy.extra[j], 0, length);
                    ++j;
                }
            } else {
                int j = 2;
                while (j < 6) {
                    copy.extra[j] = new long[length];
                    ++j;
                }
            }
            copy.extra[6] = new long[length];
            System.arraycopy(this.extra[6], 0, copy.extra[6], 0, length);
            copy.extra[7] = new long[length];
            System.arraycopy(this.extra[7], 0, copy.extra[7], 0, length);
        }
        return copy;
    }

    public UnconditionalFlowInfo discardInitializationInfo() {
        if (this == DEAD_END) {
            return this;
        }
        this.potentialInits = 0L;
        this.definiteInits = 0L;
        if (this.extra != null) {
            int i = 0;
            int length = this.extra[0].length;
            while (i < length) {
                this.extra[1][i] = 0L;
                this.extra[0][i] = 0L;
                ++i;
            }
        }
        return this;
    }

    public UnconditionalFlowInfo discardNonFieldInitializations() {
        int limit = this.maxFieldCount;
        if (limit < 64) {
            long mask = (1L << limit) - 1L;
            this.definiteInits &= mask;
            this.potentialInits &= mask;
            this.nullBit1 &= mask;
            this.nullBit2 &= mask;
            this.nullBit3 &= mask;
            this.nullBit4 &= mask;
            this.iNBit &= mask;
            this.iNNBit &= mask;
        }
        if (this.extra == null) {
            return this;
        }
        int vectorIndex = limit / 64 - 1;
        int length = this.extra[0].length;
        if (vectorIndex >= length) {
            return this;
        }
        if (vectorIndex >= 0) {
            long mask = (1L << limit % 64) - 1L;
            int j = 0;
            while (j < 8) {
                long[] lArray = this.extra[j];
                int n = vectorIndex;
                lArray[n] = lArray[n] & mask;
                ++j;
            }
        }
        int i = vectorIndex + 1;
        while (i < length) {
            int j = 0;
            while (j < 8) {
                this.extra[j][i] = 0L;
                ++j;
            }
            ++i;
        }
        return this;
    }

    @Override
    public FlowInfo initsWhenFalse() {
        return this;
    }

    @Override
    public FlowInfo initsWhenTrue() {
        return this;
    }

    private final boolean isDefinitelyAssigned(int position) {
        if (position < 64) {
            return (this.definiteInits & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[0].length) {
            return false;
        }
        return (this.extra[0][vectorIndex] & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean isDefinitelyAssigned(FieldBinding field) {
        if ((this.tagBits & 1) != 0) {
            return true;
        }
        return this.isDefinitelyAssigned(field.id);
    }

    @Override
    public final boolean isDefinitelyAssigned(LocalVariableBinding local) {
        if ((this.tagBits & 1) != 0 && (local.declaration.bits & 0x40000000) != 0) {
            return true;
        }
        return this.isDefinitelyAssigned(local.id + this.maxFieldCount);
    }

    @Override
    public final boolean isDefinitelyNonNull(LocalVariableBinding local) {
        if ((this.tagBits & 3) != 0 || (this.tagBits & 4) == 0) {
            return false;
        }
        if ((local.type.tagBits & 2L) != 0L || local.constant() != Constant.NotAConstant) {
            return true;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit3 & (this.nullBit2 ^ 0xFFFFFFFFFFFFFFFFL | this.nullBit4) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[2].length) {
            return false;
        }
        return (this.extra[2][vectorIndex] & this.extra[4][vectorIndex] & (this.extra[3][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL | this.extra[5][vectorIndex]) & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean isDefinitelyNull(LocalVariableBinding local) {
        if ((this.tagBits & 3) != 0 || (this.tagBits & 4) == 0 || (local.type.tagBits & 2L) != 0L) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit2 & (this.nullBit3 ^ 0xFFFFFFFFFFFFFFFFL | this.nullBit4 ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[2].length) {
            return false;
        }
        return (this.extra[2][vectorIndex] & this.extra[3][vectorIndex] & (this.extra[4][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL | this.extra[5][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean isDefinitelyUnknown(LocalVariableBinding local) {
        if ((this.tagBits & 3) != 0 || (this.tagBits & 4) == 0) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit4 & (this.nullBit2 ^ 0xFFFFFFFFFFFFFFFFL) & (this.nullBit3 ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[2].length) {
            return false;
        }
        return (this.extra[2][vectorIndex] & this.extra[5][vectorIndex] & (this.extra[3][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL) & (this.extra[4][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean hasNullInfoFor(LocalVariableBinding local) {
        if ((this.tagBits & 3) != 0 || (this.tagBits & 4) == 0) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return ((this.nullBit1 | this.nullBit2 | this.nullBit3 | this.nullBit4) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[2].length) {
            return false;
        }
        return ((this.extra[2][vectorIndex] | this.extra[3][vectorIndex] | this.extra[4][vectorIndex] | this.extra[5][vectorIndex]) & 1L << position % 64) != 0L;
    }

    private final boolean isPotentiallyAssigned(int position) {
        if (position < 64) {
            return (this.potentialInits & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[0].length) {
            return false;
        }
        return (this.extra[1][vectorIndex] & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean isPotentiallyAssigned(FieldBinding field) {
        return this.isPotentiallyAssigned(field.id);
    }

    @Override
    public final boolean isPotentiallyAssigned(LocalVariableBinding local) {
        if (local.constant() != Constant.NotAConstant) {
            return true;
        }
        return this.isPotentiallyAssigned(local.id + this.maxFieldCount);
    }

    @Override
    public final boolean isPotentiallyNonNull(LocalVariableBinding local) {
        if ((this.tagBits & 4) == 0 || (local.type.tagBits & 2L) != 0L) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit3 & (this.nullBit1 ^ 0xFFFFFFFFFFFFFFFFL | this.nullBit2 ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[2].length) {
            return false;
        }
        return (this.extra[4][vectorIndex] & (this.extra[2][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL | this.extra[3][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean isPotentiallyNull(LocalVariableBinding local) {
        if ((this.tagBits & 4) == 0 || (local.type.tagBits & 2L) != 0L) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit2 & (this.nullBit1 ^ 0xFFFFFFFFFFFFFFFFL | this.nullBit3 ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[2].length) {
            return false;
        }
        return (this.extra[3][vectorIndex] & (this.extra[2][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL | this.extra[4][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL) & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean isPotentiallyUnknown(LocalVariableBinding local) {
        if ((this.tagBits & 3) != 0 || (this.tagBits & 4) == 0) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit4 & (this.nullBit1 ^ 0xFFFFFFFFFFFFFFFFL | (this.nullBit2 ^ 0xFFFFFFFFFFFFFFFFL) & (this.nullBit3 ^ 0xFFFFFFFFFFFFFFFFL)) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[2].length) {
            return false;
        }
        return (this.extra[5][vectorIndex] & (this.extra[2][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL | (this.extra[3][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL) & (this.extra[4][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL)) & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean isProtectedNonNull(LocalVariableBinding local) {
        if ((this.tagBits & 4) == 0 || (local.type.tagBits & 2L) != 0L) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit3 & this.nullBit4 & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[0].length) {
            return false;
        }
        return (this.extra[2][vectorIndex] & this.extra[4][vectorIndex] & this.extra[5][vectorIndex] & 1L << position % 64) != 0L;
    }

    @Override
    public final boolean isProtectedNull(LocalVariableBinding local) {
        if ((this.tagBits & 4) == 0 || (local.type.tagBits & 2L) != 0L) {
            return false;
        }
        int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit2 & (this.nullBit3 ^ this.nullBit4) & 1L << position) != 0L;
        }
        if (this.extra == null) {
            return false;
        }
        int vectorIndex = position / 64 - 1;
        if (vectorIndex >= this.extra[0].length) {
            return false;
        }
        return (this.extra[2][vectorIndex] & this.extra[3][vectorIndex] & (this.extra[4][vectorIndex] ^ this.extra[5][vectorIndex]) & 1L << position % 64) != 0L;
    }

    protected static boolean isTrue(boolean expression, String message) {
        if (!expression) {
            throw new AssertionFailedException("assertion failed: " + message);
        }
        return expression;
    }

    @Override
    public void markAsComparedEqualToNonNull(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.tagBits |= 4;
            int position = local.id + this.maxFieldCount;
            if (position < 64) {
                long mask = 1L << position;
                long a1 = this.nullBit1;
                long a2 = this.nullBit2;
                long na2 = a2 ^ 0xFFFFFFFFFFFFFFFFL;
                long a3 = this.nullBit3;
                long a4 = this.nullBit4;
                if ((mask & a1 & na2 & (a3 ^ 0xFFFFFFFFFFFFFFFFL) & a4) != 0L) {
                    this.nullBit4 &= mask ^ 0xFFFFFFFFFFFFFFFFL;
                } else if ((mask & a1 & na2 & a3) == 0L) {
                    this.nullBit4 |= mask;
                    if ((mask & a1) == 0L) {
                        if ((mask & a2 & (a3 ^ a4)) != 0L) {
                            this.nullBit2 &= mask ^ 0xFFFFFFFFFFFFFFFFL;
                        } else if ((mask & (a2 | a3 | a4)) == 0L) {
                            this.nullBit2 |= mask;
                        }
                    }
                }
                this.nullBit1 |= mask;
                this.nullBit3 |= mask;
                this.iNBit &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                } else {
                    int oldLength = this.extra[0].length;
                    if (vectorIndex >= oldLength) {
                        this.growSpace(vectorIndex + 1, 0, oldLength);
                    }
                }
                long mask = 1L << position % 64;
                long a1 = this.extra[2][vectorIndex];
                long a2 = this.extra[3][vectorIndex];
                long na2 = a2 ^ 0xFFFFFFFFFFFFFFFFL;
                long a3 = this.extra[4][vectorIndex];
                long a4 = this.extra[5][vectorIndex];
                if ((mask & a1 & na2 & (a3 ^ 0xFFFFFFFFFFFFFFFFL) & a4) != 0L) {
                    long[] lArray = this.extra[5];
                    int n = vectorIndex;
                    lArray[n] = lArray[n] & (mask ^ 0xFFFFFFFFFFFFFFFFL);
                } else if ((mask & a1 & na2 & a3) == 0L) {
                    long[] lArray = this.extra[5];
                    int n = vectorIndex;
                    lArray[n] = lArray[n] | mask;
                    if ((mask & a1) == 0L) {
                        if ((mask & a2 & (a3 ^ a4)) != 0L) {
                            long[] lArray2 = this.extra[3];
                            int n2 = vectorIndex;
                            lArray2[n2] = lArray2[n2] & (mask ^ 0xFFFFFFFFFFFFFFFFL);
                        } else if ((mask & (a2 | a3 | a4)) == 0L) {
                            long[] lArray3 = this.extra[3];
                            int n3 = vectorIndex;
                            lArray3[n3] = lArray3[n3] | mask;
                        }
                    }
                }
                long[] lArray = this.extra[2];
                int n = vectorIndex;
                lArray[n] = lArray[n] | mask;
                long[] lArray4 = this.extra[4];
                int n4 = vectorIndex;
                lArray4[n4] = lArray4[n4] | mask;
                long[] lArray5 = this.extra[6];
                int n5 = vectorIndex;
                lArray5[n5] = lArray5[n5] & (mask ^ 0xFFFFFFFFFFFFFFFFL);
            }
        }
    }

    @Override
    public void markAsComparedEqualToNull(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.tagBits |= 4;
            int position = local.id + this.maxFieldCount;
            if (position < 64) {
                long mask = 1L << position;
                if ((mask & this.nullBit1) != 0L) {
                    if ((mask & (this.nullBit2 ^ 0xFFFFFFFFFFFFFFFFL | this.nullBit3 | this.nullBit4 ^ 0xFFFFFFFFFFFFFFFFL)) != 0L) {
                        this.nullBit4 &= mask ^ 0xFFFFFFFFFFFFFFFFL;
                    }
                } else if ((mask & this.nullBit4) != 0L) {
                    this.nullBit3 &= mask ^ 0xFFFFFFFFFFFFFFFFL;
                } else if ((mask & this.nullBit2) != 0L) {
                    this.nullBit3 &= mask ^ 0xFFFFFFFFFFFFFFFFL;
                    this.nullBit4 |= mask;
                } else {
                    this.nullBit3 |= mask;
                }
                this.nullBit1 |= mask;
                this.nullBit2 |= mask;
                this.iNNBit &= mask ^ 0xFFFFFFFFFFFFFFFFL;
            } else {
                int vectorIndex = position / 64 - 1;
                long mask = 1L << position % 64;
                if (this.extra == null) {
                    int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                } else {
                    int oldLength = this.extra[0].length;
                    if (vectorIndex >= oldLength) {
                        this.growSpace(vectorIndex + 1, 0, oldLength);
                    }
                }
                if ((mask & this.extra[2][vectorIndex]) != 0L) {
                    if ((mask & (this.extra[3][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL | this.extra[4][vectorIndex] | this.extra[5][vectorIndex] ^ 0xFFFFFFFFFFFFFFFFL)) != 0L) {
                        long[] lArray = this.extra[5];
                        int n = vectorIndex;
                        lArray[n] = lArray[n] & (mask ^ 0xFFFFFFFFFFFFFFFFL);
                    }
                } else if ((mask & this.extra[5][vectorIndex]) != 0L) {
                    long[] lArray = this.extra[4];
                    int n = vectorIndex;
                    lArray[n] = lArray[n] & (mask ^ 0xFFFFFFFFFFFFFFFFL);
                } else if ((mask & this.extra[3][vectorIndex]) != 0L) {
                    long[] lArray = this.extra[4];
                    int n = vectorIndex;
                    lArray[n] = lArray[n] & (mask ^ 0xFFFFFFFFFFFFFFFFL);
                    long[] lArray2 = this.extra[5];
                    int n2 = vectorIndex;
                    lArray2[n2] = lArray2[n2] | mask;
                } else {
                    long[] lArray = this.extra[4];
                    int n = vectorIndex;
                    lArray[n] = lArray[n] | mask;
                }
                long[] lArray = this.extra[2];
                int n = vectorIndex;
                lArray[n] = lArray[n] | mask;
                long[] lArray3 = this.extra[3];
                int n3 = vectorIndex;
                lArray3[n3] = lArray3[n3] | mask;
                long[] lArray4 = this.extra[7];
                int n4 = vectorIndex;
                lArray4[n4] = lArray4[n4] & (mask ^ 0xFFFFFFFFFFFFFFFFL);
            }
        }
    }

    private final void markAsDefinitelyAssigned(int position) {
        if (this != DEAD_END) {
            if (position < 64) {
                long mask = 1L << position;
                this.definiteInits |= mask;
                this.potentialInits |= mask;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                } else {
                    int oldLength = this.extra[0].length;
                    if (vectorIndex >= oldLength) {
                        this.growSpace(vectorIndex + 1, 0, oldLength);
                    }
                }
                long[] lArray = this.extra[0];
                int n = vectorIndex;
                long mask = 1L << position % 64;
                lArray[n] = lArray[n] | mask;
                long[] lArray2 = this.extra[1];
                int n2 = vectorIndex;
                lArray2[n2] = lArray2[n2] | mask;
            }
        }
    }

    @Override
    public void markAsDefinitelyAssigned(FieldBinding field) {
        if (this != DEAD_END) {
            this.markAsDefinitelyAssigned(field.id);
        }
    }

    @Override
    public void markAsDefinitelyAssigned(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.markAsDefinitelyAssigned(local.id + this.maxFieldCount);
        }
    }

    @Override
    public void markAsDefinitelyNonNull(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.tagBits |= 4;
            int position = local.id + this.maxFieldCount;
            if (position < 64) {
                long mask = 1L << position;
                this.nullBit1 |= mask;
                this.nullBit3 |= mask;
                this.nullBit2 &= (mask ^= 0xFFFFFFFFFFFFFFFFL);
                this.nullBit4 &= mask;
                this.iNBit &= mask;
                this.iNNBit &= mask;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                } else {
                    int oldLength = this.extra[0].length;
                    if (vectorIndex >= oldLength) {
                        this.growSpace(vectorIndex + 1, 0, oldLength);
                    }
                }
                long[] lArray = this.extra[2];
                int n = vectorIndex;
                long mask = 1L << position % 64;
                lArray[n] = lArray[n] | mask;
                long[] lArray2 = this.extra[4];
                int n2 = vectorIndex;
                lArray2[n2] = lArray2[n2] | mask;
                long[] lArray3 = this.extra[3];
                int n3 = vectorIndex;
                lArray3[n3] = lArray3[n3] & (mask ^= 0xFFFFFFFFFFFFFFFFL);
                long[] lArray4 = this.extra[5];
                int n4 = vectorIndex;
                lArray4[n4] = lArray4[n4] & mask;
                long[] lArray5 = this.extra[6];
                int n5 = vectorIndex;
                lArray5[n5] = lArray5[n5] & mask;
                long[] lArray6 = this.extra[7];
                int n6 = vectorIndex;
                lArray6[n6] = lArray6[n6] & mask;
            }
        }
    }

    @Override
    public void markAsDefinitelyNull(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.tagBits |= 4;
            int position = local.id + this.maxFieldCount;
            if (position < 64) {
                long mask = 1L << position;
                this.nullBit1 |= mask;
                this.nullBit2 |= mask;
                this.nullBit3 &= (mask ^= 0xFFFFFFFFFFFFFFFFL);
                this.nullBit4 &= mask;
                this.iNBit &= mask;
                this.iNNBit &= mask;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                } else {
                    int oldLength = this.extra[0].length;
                    if (vectorIndex >= oldLength) {
                        this.growSpace(vectorIndex + 1, 0, oldLength);
                    }
                }
                long[] lArray = this.extra[2];
                int n = vectorIndex;
                long mask = 1L << position % 64;
                lArray[n] = lArray[n] | mask;
                long[] lArray2 = this.extra[3];
                int n2 = vectorIndex;
                lArray2[n2] = lArray2[n2] | mask;
                long[] lArray3 = this.extra[4];
                int n3 = vectorIndex;
                lArray3[n3] = lArray3[n3] & (mask ^= 0xFFFFFFFFFFFFFFFFL);
                long[] lArray4 = this.extra[5];
                int n4 = vectorIndex;
                lArray4[n4] = lArray4[n4] & mask;
                long[] lArray5 = this.extra[6];
                int n5 = vectorIndex;
                lArray5[n5] = lArray5[n5] & mask;
                long[] lArray6 = this.extra[7];
                int n6 = vectorIndex;
                lArray6[n6] = lArray6[n6] & mask;
            }
        }
    }

    @Override
    public void markAsDefinitelyUnknown(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.tagBits |= 4;
            int position = local.id + this.maxFieldCount;
            if (position < 64) {
                long mask = 1L << position;
                this.nullBit1 |= mask;
                this.nullBit4 |= mask;
                this.nullBit2 &= (mask ^= 0xFFFFFFFFFFFFFFFFL);
                this.nullBit3 &= mask;
                this.iNBit &= mask;
                this.iNNBit &= mask;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                } else {
                    int oldLength = this.extra[0].length;
                    if (vectorIndex >= oldLength) {
                        this.growSpace(vectorIndex + 1, 0, oldLength);
                    }
                }
                long[] lArray = this.extra[2];
                int n = vectorIndex;
                long mask = 1L << position % 64;
                lArray[n] = lArray[n] | mask;
                long[] lArray2 = this.extra[5];
                int n2 = vectorIndex;
                lArray2[n2] = lArray2[n2] | mask;
                long[] lArray3 = this.extra[3];
                int n3 = vectorIndex;
                lArray3[n3] = lArray3[n3] & (mask ^= 0xFFFFFFFFFFFFFFFFL);
                long[] lArray4 = this.extra[4];
                int n4 = vectorIndex;
                lArray4[n4] = lArray4[n4] & mask;
                long[] lArray5 = this.extra[6];
                int n5 = vectorIndex;
                lArray5[n5] = lArray5[n5] & mask;
                long[] lArray6 = this.extra[7];
                int n6 = vectorIndex;
                lArray6[n6] = lArray6[n6] & mask;
            }
        }
    }

    @Override
    public void resetNullInfo(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.tagBits |= 4;
            int position = local.id + this.maxFieldCount;
            if (position < 64) {
                long mask = 1L << position ^ 0xFFFFFFFFFFFFFFFFL;
                this.nullBit1 &= mask;
                this.nullBit2 &= mask;
                this.nullBit3 &= mask;
                this.nullBit4 &= mask;
                this.iNBit &= mask;
                this.iNNBit &= mask;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null || vectorIndex >= this.extra[2].length) {
                    return;
                }
                long[] lArray = this.extra[2];
                int n = vectorIndex;
                long mask = 1L << position % 64 ^ 0xFFFFFFFFFFFFFFFFL;
                lArray[n] = lArray[n] & mask;
                long[] lArray2 = this.extra[3];
                int n2 = vectorIndex;
                lArray2[n2] = lArray2[n2] & mask;
                long[] lArray3 = this.extra[4];
                int n3 = vectorIndex;
                lArray3[n3] = lArray3[n3] & mask;
                long[] lArray4 = this.extra[5];
                int n4 = vectorIndex;
                lArray4[n4] = lArray4[n4] & mask;
                long[] lArray5 = this.extra[6];
                int n5 = vectorIndex;
                lArray5[n5] = lArray5[n5] & mask;
                long[] lArray6 = this.extra[7];
                int n6 = vectorIndex;
                lArray6[n6] = lArray6[n6] & mask;
            }
        }
    }

    @Override
    public void markPotentiallyUnknownBit(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.tagBits |= 4;
            int position = local.id + this.maxFieldCount;
            if (position < 64) {
                long mask = 1L << position;
                UnconditionalFlowInfo.isTrue((this.nullBit1 & mask) == 0L, "Adding 'unknown' mark in unexpected state");
                this.nullBit4 |= mask;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                } else {
                    int oldLength = this.extra[0].length;
                    if (vectorIndex >= oldLength) {
                        this.growSpace(vectorIndex + 1, 0, oldLength);
                    }
                }
                long mask = 1L << position % 64;
                UnconditionalFlowInfo.isTrue((this.extra[2][vectorIndex] & mask) == 0L, "Adding 'unknown' mark in unexpected state");
                long[] lArray = this.extra[5];
                int n = vectorIndex;
                lArray[n] = lArray[n] | mask;
            }
        }
    }

    @Override
    public void markPotentiallyNullBit(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.tagBits |= 4;
            int position = local.id + this.maxFieldCount;
            if (position < 64) {
                long mask = 1L << position;
                UnconditionalFlowInfo.isTrue((this.nullBit1 & mask) == 0L, "Adding 'potentially null' mark in unexpected state");
                this.nullBit2 |= mask;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                } else {
                    int oldLength = this.extra[0].length;
                    if (vectorIndex >= oldLength) {
                        this.growSpace(vectorIndex + 1, 0, oldLength);
                    }
                }
                long mask = 1L << position % 64;
                long[] lArray = this.extra[3];
                int n = vectorIndex;
                lArray[n] = lArray[n] | mask;
                UnconditionalFlowInfo.isTrue((this.extra[2][vectorIndex] & mask) == 0L, "Adding 'potentially null' mark in unexpected state");
            }
        }
    }

    @Override
    public void markPotentiallyNonNullBit(LocalVariableBinding local) {
        if (this != DEAD_END) {
            this.tagBits |= 4;
            int position = local.id + this.maxFieldCount;
            if (position < 64) {
                long mask = 1L << position;
                UnconditionalFlowInfo.isTrue((this.nullBit1 & mask) == 0L, "Adding 'potentially non-null' mark in unexpected state");
                this.nullBit3 |= mask;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                } else {
                    int oldLength = this.extra[0].length;
                    if (vectorIndex >= oldLength) {
                        this.growSpace(vectorIndex + 1, 0, oldLength);
                    }
                }
                long mask = 1L << position % 64;
                UnconditionalFlowInfo.isTrue((this.extra[2][vectorIndex] & mask) == 0L, "Adding 'potentially non-null' mark in unexpected state");
                long[] lArray = this.extra[4];
                int n = vectorIndex;
                lArray[n] = lArray[n] | mask;
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public UnconditionalFlowInfo mergedWith(UnconditionalFlowInfo otherInits) {
        block30: {
            if ((otherInits.tagBits & 1) != 0 && this != UnconditionalFlowInfo.DEAD_END) {
                return this;
            }
            if ((this.tagBits & 1) != 0) {
                return (UnconditionalFlowInfo)otherInits.copy();
            }
            this.definiteInits &= otherInits.definiteInits;
            this.potentialInits |= otherInits.potentialInits;
            thisHasNulls = (this.tagBits & 4) != 0;
            otherHasNulls = (otherInits.tagBits & 4) != 0;
            thisWasUnreachable = false;
            otherIsUnreachable = false;
            if ((otherInits.tagBits & 2) != 0) {
                otherIsUnreachable = true;
            } else if ((this.tagBits & 2) != 0) {
                this.nullBit1 = otherInits.nullBit1;
                this.nullBit2 = otherInits.nullBit2;
                this.nullBit3 = otherInits.nullBit3;
                this.nullBit4 = otherInits.nullBit4;
                this.iNBit = otherInits.iNBit;
                this.iNNBit = otherInits.iNNBit;
                thisWasUnreachable = true;
                thisHasNulls = otherHasNulls;
                this.tagBits = otherInits.tagBits;
            } else if (thisHasNulls) {
                if (otherHasNulls) {
                    a1 = this.nullBit1;
                    b1 = otherInits.nullBit1;
                    a2 = this.nullBit2;
                    b2 = otherInits.nullBit2;
                    a3 = this.nullBit3;
                    a4 = this.nullBit4;
                    b3 = otherInits.nullBit3;
                    b4 = otherInits.nullBit4;
                    nb2 = b2 ^ -1L;
                    na2 = a2 ^ -1L;
                    na3 = a3 ^ -1L;
                    this.nullBit1 = a1 & b1 & (a2 & (b2 & (a3 & a4 ^ b3 & b4 ^ -1L) | a3 & a4 & nb2) | na2 & (b2 & b3 & b4 | nb2 & (na3 ^ b3)));
                    nb3 = b3 ^ -1L;
                    nb1 = b1 ^ -1L;
                    na1 = a1 ^ -1L;
                    nb4 = b4 ^ -1L;
                    na4 = a4 ^ -1L;
                    this.nullBit2 = b2 & (nb3 | nb1 | a3 & (a4 | na1) & nb4) | a2 & (b2 | na4 & b3 & (b4 | nb1) | na3 | na1);
                    this.nullBit3 = a3 & (na1 | a1 & na2 | b3 & (na4 ^ b4)) | b3 & (nb1 | b1 & nb2);
                    this.nullBit4 = na3 & (nb1 & nb3 & b4 | b1 & (nb2 & nb3 | a4 & b2 & nb4) | na1 & a4 & (nb3 | b1 & b2)) | a3 & a4 & (b3 & b4 | b1 & nb2 | na1 & a2) | na2 & (nb1 & b4 | b1 & nb3 | na1 & a4) & nb2 | a1 & (na3 & (nb3 & b4 | b1 & b2 & b3 & nb4 | na2 & (nb3 | nb2)) | na2 & b3 & b4 | a2 & (nb1 & b4 | a3 & na4 & b1) & nb3) | nb1 & b2 & b3 & b4;
                } else {
                    a1 = this.nullBit1;
                    this.nullBit1 = 0L;
                    a2 = this.nullBit2;
                    a3 = this.nullBit3;
                    na1 = a1 ^ -1L;
                    na3 = a3 ^ -1L | na1;
                    this.nullBit2 = a2 & na3;
                    na2 = a2 ^ -1L;
                    a4 = this.nullBit4;
                    this.nullBit3 = a3 & (na2 & a4 | na1) | a1 & na2 & (a4 ^ -1L);
                    this.nullBit4 = (na3 | na2) & na1 & a4 | a1 & na3 & na2;
                }
                this.iNBit |= otherInits.iNBit;
                this.iNNBit |= otherInits.iNNBit;
            } else if (otherHasNulls) {
                this.nullBit1 = 0L;
                b2 = otherInits.nullBit2;
                b3 = otherInits.nullBit3;
                b1 = otherInits.nullBit1;
                nb1 = b1 ^ -1L;
                nb3 = b3 ^ -1L | nb1;
                this.nullBit2 = b2 & nb3;
                nb2 = b2 ^ -1L;
                b4 = otherInits.nullBit4;
                this.nullBit3 = b3 & (nb2 & b4 | nb1) | b1 & nb2 & (b4 ^ -1L);
                this.nullBit4 = (nb3 | nb2) & nb1 & b4 | b1 & nb3 & nb2;
                this.iNBit |= otherInits.iNBit;
                this.iNNBit |= otherInits.iNNBit;
                v0 = thisHasNulls = this.nullBit2 != 0L || this.nullBit3 != 0L || this.nullBit4 != 0L;
            }
            if (this.extra == null && otherInits.extra == null) break block30;
            mergeLimit = 0;
            copyLimit = 0;
            resetLimit = 0;
            if (this.extra != null) {
                if (otherInits.extra != null) {
                    length = this.extra[0].length;
                    otherLength = otherInits.extra[0].length;
                    if (length < otherLength) {
                        this.growSpace(otherLength, 0, length);
                        mergeLimit = length;
                        copyLimit = otherLength;
                    } else {
                        mergeLimit = otherLength;
                        resetLimit = length;
                    }
                } else {
                    resetLimit = this.extra[0].length;
                }
            } else if (otherInits.extra != null) {
                otherLength = otherInits.extra[0].length;
                this.extra = new long[8][];
                j = 0;
                while (j < 8) {
                    this.extra[j] = new long[otherLength];
                    ++j;
                }
                System.arraycopy(otherInits.extra[1], 0, this.extra[1], 0, otherLength);
                System.arraycopy(otherInits.extra[6], 0, this.extra[6], 0, otherLength);
                System.arraycopy(otherInits.extra[7], 0, this.extra[7], 0, otherLength);
                copyLimit = otherLength;
            }
            i = 0;
            while (i < mergeLimit) {
                v1 = this.extra[0];
                v2 = i;
                v1[v2] = v1[v2] & otherInits.extra[0][i];
                v3 = this.extra[1];
                v4 = i;
                v3[v4] = v3[v4] | otherInits.extra[1][i];
                ++i;
            }
            while (i < copyLimit) {
                this.extra[1][i] = otherInits.extra[1][i];
                ++i;
            }
            while (i < resetLimit) {
                this.extra[0][i] = 0L;
                ++i;
            }
            if (!otherHasNulls || otherIsUnreachable) {
                resetLimit = otherIsUnreachable != false ? 0 : Math.max(resetLimit, mergeLimit);
                copyLimit = 0;
                mergeLimit = 0;
            }
            i = 0;
            if (!thisWasUnreachable) ** GOTO lbl175
            if (otherInits.extra != null) {
                while (i < mergeLimit) {
                    this.extra[2][i] = otherInits.extra[2][i];
                    this.extra[3][i] = otherInits.extra[3][i];
                    this.extra[4][i] = otherInits.extra[4][i];
                    this.extra[5][i] = otherInits.extra[5][i];
                    ++i;
                }
            }
            while (i < resetLimit) {
                this.extra[2][i] = 0L;
                this.extra[3][i] = 0L;
                this.extra[4][i] = 0L;
                this.extra[5][i] = 0L;
                ++i;
            }
            break block30;
lbl-1000:
            // 1 sources

            {
                a1 = this.extra[2][i];
                b1 = otherInits.extra[2][i];
                a2 = this.extra[3][i];
                b2 = otherInits.extra[3][i];
                a3 = this.extra[4][i];
                a4 = this.extra[5][i];
                b3 = otherInits.extra[4][i];
                b4 = otherInits.extra[5][i];
                nb2 = b2 ^ -1L;
                na2 = a2 ^ -1L;
                na3 = a3 ^ -1L;
                this.extra[2][i] = a1 & b1 & (a2 & (b2 & (a3 & a4 ^ b3 & b4 ^ -1L) | a3 & a4 & nb2) | na2 & (b2 & b3 & b4 | nb2 & (na3 ^ b3)));
                nb3 = b3 ^ -1L;
                nb1 = b1 ^ -1L;
                na1 = a1 ^ -1L;
                nb4 = b4 ^ -1L;
                na4 = a4 ^ -1L;
                this.extra[3][i] = b2 & (nb3 | nb1 | a3 & (a4 | na1) & nb4) | a2 & (b2 | na4 & b3 & (b4 | nb1) | na3 | na1);
                this.extra[4][i] = a3 & (na1 | a1 & na2 | b3 & (na4 ^ b4)) | b3 & (nb1 | b1 & nb2);
                this.extra[5][i] = na3 & (nb1 & nb3 & b4 | b1 & (nb2 & nb3 | a4 & b2 & nb4) | na1 & a4 & (nb3 | b1 & b2)) | a3 & a4 & (b3 & b4 | b1 & nb2 | na1 & a2) | na2 & (nb1 & b4 | b1 & nb3 | na1 & a4) & nb2 | a1 & (na3 & (nb3 & b4 | b1 & b2 & b3 & nb4 | na2 & (nb3 | nb2)) | na2 & b3 & b4 | a2 & (nb1 & b4 | a3 & na4 & b1) & nb3) | nb1 & b2 & b3 & b4;
                v5 = this.extra[6];
                v6 = i;
                v5[v6] = v5[v6] | otherInits.extra[6][i];
                v7 = this.extra[7];
                v8 = i;
                v7[v8] = v7[v8] | otherInits.extra[7][i];
                thisHasNulls = thisHasNulls != false || this.extra[3][i] != 0L || this.extra[4][i] != 0L || this.extra[5][i] != 0L;
                ++i;
lbl175:
                // 2 sources

                ** while (i < mergeLimit)
            }
lbl176:
            // 2 sources

            while (i < copyLimit) {
                this.extra[2][i] = 0L;
                b2 = otherInits.extra[3][i];
                b3 = otherInits.extra[4][i];
                b1 = otherInits.extra[2][i];
                nb1 = b1 ^ -1L;
                nb3 = b3 ^ -1L | nb1;
                this.extra[3][i] = b2 & nb3;
                nb2 = b2 ^ -1L;
                b4 = otherInits.extra[5][i];
                this.extra[4][i] = b3 & (nb2 & b4 | nb1) | b1 & nb2 & (b4 ^ -1L);
                this.extra[5][i] = (nb3 | nb2) & nb1 & b4 | b1 & nb3 & nb2;
                v9 = this.extra[6];
                v10 = i;
                v9[v10] = v9[v10] | otherInits.extra[6][i];
                v11 = this.extra[7];
                v12 = i;
                v11[v12] = v11[v12] | otherInits.extra[7][i];
                thisHasNulls = thisHasNulls != false || this.extra[3][i] != 0L || this.extra[4][i] != 0L || this.extra[5][i] != 0L;
                ++i;
            }
            while (i < resetLimit) {
                a1 = this.extra[2][i];
                this.extra[2][i] = 0L;
                a2 = this.extra[3][i];
                a3 = this.extra[4][i];
                na1 = a1 ^ -1L;
                na3 = a3 ^ -1L | na1;
                this.extra[3][i] = a2 & na3;
                na2 = a2 ^ -1L;
                a4 = this.extra[5][i];
                this.extra[4][i] = a3 & (na2 & a4 | na1) | a1 & na2 & (a4 ^ -1L);
                this.extra[5][i] = (na3 | na2) & na1 & a4 | a1 & na3 & na2;
                if (otherInits.extra != null && otherInits.extra[0].length > i) {
                    v13 = this.extra[6];
                    v14 = i;
                    v13[v14] = v13[v14] | otherInits.extra[6][i];
                    v15 = this.extra[7];
                    v16 = i;
                    v15[v16] = v15[v16] | otherInits.extra[7][i];
                }
                thisHasNulls = thisHasNulls != false || this.extra[3][i] != 0L || this.extra[4][i] != 0L || this.extra[5][i] != 0L;
                ++i;
            }
        }
        this.tagBits = thisHasNulls ? (this.tagBits |= 4) : (this.tagBits &= -5);
        return this;
    }

    static int numberOfEnclosingFields(ReferenceBinding type) {
        int count = 0;
        type = type.enclosingType();
        while (type != null) {
            count += type.fieldCount();
            type = type.enclosingType();
        }
        return count;
    }

    @Override
    public UnconditionalFlowInfo nullInfoLessUnconditionalCopy() {
        if (this == DEAD_END) {
            return this;
        }
        UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
        copy.definiteInits = this.definiteInits;
        copy.potentialInits = this.potentialInits;
        copy.iNBit = -1L;
        copy.iNNBit = -1L;
        copy.tagBits = this.tagBits & 0xFFFFFFFB;
        copy.tagBits |= 0x40;
        copy.maxFieldCount = this.maxFieldCount;
        if (this.extra != null) {
            copy.extra = new long[8][];
            int length = this.extra[0].length;
            copy.extra[0] = new long[length];
            System.arraycopy(this.extra[0], 0, copy.extra[0], 0, length);
            copy.extra[1] = new long[length];
            System.arraycopy(this.extra[1], 0, copy.extra[1], 0, length);
            int j = 2;
            while (j < 8) {
                copy.extra[j] = new long[length];
                ++j;
            }
            Arrays.fill(copy.extra[6], -1L);
            Arrays.fill(copy.extra[7], -1L);
        }
        return copy;
    }

    @Override
    public FlowInfo safeInitsWhenTrue() {
        return this.copy();
    }

    @Override
    public FlowInfo setReachMode(int reachMode) {
        if (this == DEAD_END) {
            return this;
        }
        if (reachMode == 0) {
            this.tagBits &= 0xFFFFFFFC;
        } else if (reachMode == 2) {
            this.tagBits |= 2;
        } else {
            if ((this.tagBits & 3) == 0) {
                this.potentialInits = 0L;
                if (this.extra != null) {
                    int i = 0;
                    int length = this.extra[0].length;
                    while (i < length) {
                        this.extra[1][i] = 0L;
                        ++i;
                    }
                }
            }
            this.tagBits |= reachMode;
        }
        return this;
    }

    @Override
    public String toString() {
        if (this == DEAD_END) {
            return "FlowInfo.DEAD_END";
        }
        if ((this.tagBits & 4) != 0) {
            if (this.extra == null) {
                return "FlowInfo<def: " + Long.toHexString(this.definiteInits) + ", pot: " + Long.toHexString(this.potentialInits) + ", reachable:" + ((this.tagBits & 3) == 0) + ", null: " + Long.toHexString(this.nullBit1) + '.' + Long.toHexString(this.nullBit2) + '.' + Long.toHexString(this.nullBit3) + '.' + Long.toHexString(this.nullBit4) + ", incoming: " + Long.toHexString(this.iNBit) + '.' + Long.toHexString(this.iNNBit) + ">";
            }
            String def = "FlowInfo<def:[" + Long.toHexString(this.definiteInits);
            String pot = "], pot:[" + Long.toHexString(this.potentialInits);
            String nullS = ", null:[" + Long.toHexString(this.nullBit1) + '.' + Long.toHexString(this.nullBit2) + '.' + Long.toHexString(this.nullBit3) + '.' + Long.toHexString(this.nullBit4) + ", incoming: " + Long.toHexString(this.iNBit) + '.' + Long.toHexString(this.iNNBit);
            int i = 0;
            int ceil = this.extra[0].length > 3 ? 3 : this.extra[0].length;
            while (i < ceil) {
                def = String.valueOf(def) + "," + Long.toHexString(this.extra[0][i]);
                pot = String.valueOf(pot) + "," + Long.toHexString(this.extra[1][i]);
                nullS = String.valueOf(nullS) + "\n\t" + Long.toHexString(this.extra[2][i]) + '.' + Long.toHexString(this.extra[3][i]) + '.' + Long.toHexString(this.extra[4][i]) + '.' + Long.toHexString(this.extra[5][i]) + ", incoming: " + Long.toHexString(this.extra[6][i]) + '.' + Long.toHexString(this.extra[7][i]);
                ++i;
            }
            if (ceil < this.extra[0].length) {
                def = String.valueOf(def) + ",...";
                pot = String.valueOf(pot) + ",...";
                nullS = String.valueOf(nullS) + ",...";
            }
            return String.valueOf(def) + pot + "], reachable:" + ((this.tagBits & 3) == 0) + nullS + "]>";
        }
        if (this.extra == null) {
            return "FlowInfo<def: " + this.definiteInits + ", pot: " + this.potentialInits + ", reachable:" + ((this.tagBits & 3) == 0) + ", no null info>";
        }
        String def = "FlowInfo<def:[" + this.definiteInits;
        String pot = "], pot:[" + this.potentialInits;
        int i = 0;
        int ceil = this.extra[0].length > 3 ? 3 : this.extra[0].length;
        while (i < ceil) {
            def = String.valueOf(def) + "," + this.extra[0][i];
            pot = String.valueOf(pot) + "," + this.extra[1][i];
            ++i;
        }
        if (ceil < this.extra[0].length) {
            def = String.valueOf(def) + ",...";
            pot = String.valueOf(pot) + ",...";
        }
        return String.valueOf(def) + pot + "], reachable:" + ((this.tagBits & 3) == 0) + ", no null info>";
    }

    @Override
    public UnconditionalFlowInfo unconditionalCopy() {
        return (UnconditionalFlowInfo)this.copy();
    }

    @Override
    public UnconditionalFlowInfo unconditionalFieldLessCopy() {
        UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
        copy.tagBits = this.tagBits;
        copy.maxFieldCount = this.maxFieldCount;
        int limit = this.maxFieldCount;
        if (limit < 64) {
            long mask = (1L << limit) - 1L ^ 0xFFFFFFFFFFFFFFFFL;
            copy.definiteInits = this.definiteInits & mask;
            copy.potentialInits = this.potentialInits & mask;
            copy.nullBit1 = this.nullBit1 & mask;
            copy.nullBit2 = this.nullBit2 & mask;
            copy.nullBit3 = this.nullBit3 & mask;
            copy.nullBit4 = this.nullBit4 & mask;
            copy.iNBit = this.iNBit & mask;
            copy.iNNBit = this.iNNBit & mask;
        }
        if (this.extra == null) {
            return copy;
        }
        int vectorIndex = limit / 64 - 1;
        int length = this.extra[0].length;
        if (vectorIndex >= length) {
            return copy;
        }
        copy.extra = new long[8][];
        int copyStart = vectorIndex + 1;
        if (copyStart < length) {
            int copyLength = length - copyStart;
            int j = 0;
            while (j < 8) {
                copy.extra[j] = new long[length];
                System.arraycopy(this.extra[j], copyStart, copy.extra[j], copyStart, copyLength);
                ++j;
            }
        } else if (vectorIndex >= 0) {
            copy.createExtraSpace(length);
        }
        if (vectorIndex >= 0) {
            long mask = (1L << limit % 64) - 1L ^ 0xFFFFFFFFFFFFFFFFL;
            int j = 0;
            while (j < 8) {
                copy.extra[j][vectorIndex] = this.extra[j][vectorIndex] & mask;
                ++j;
            }
        }
        return copy;
    }

    @Override
    public UnconditionalFlowInfo unconditionalInits() {
        return this;
    }

    @Override
    public UnconditionalFlowInfo unconditionalInitsWithoutSideEffect() {
        return this;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public UnconditionalFlowInfo mergeDefiniteInitsWith(UnconditionalFlowInfo otherInits) {
        block10: {
            block8: {
                block9: {
                    if ((otherInits.tagBits & 1) != 0 && this != UnconditionalFlowInfo.DEAD_END) {
                        return this;
                    }
                    if ((this.tagBits & 1) != 0) {
                        return (UnconditionalFlowInfo)otherInits.copy();
                    }
                    this.definiteInits &= otherInits.definiteInits;
                    if (this.extra == null) break block8;
                    if (otherInits.extra == null) break block9;
                    i = 0;
                    length = this.extra[0].length;
                    otherLength = otherInits.extra[0].length;
                    if (length >= otherLength) ** GOTO lbl28
                    this.growSpace(otherLength, 0, length);
                    while (i < length) {
                        v0 = this.extra[0];
                        v1 = i;
                        v0[v1] = v0[v1] & otherInits.extra[0][i];
                        ++i;
                    }
                    while (i < otherLength) {
                        this.extra[0][i] = otherInits.extra[0][i];
                        ++i;
                    }
                    break block10;
lbl-1000:
                    // 1 sources

                    {
                        v2 = this.extra[0];
                        v3 = i;
                        v2[v3] = v2[v3] & otherInits.extra[0][i];
                        ++i;
lbl28:
                        // 2 sources

                        ** while (i < otherLength)
                    }
lbl29:
                    // 2 sources

                    while (i < length) {
                        this.extra[0][i] = 0L;
                        ++i;
                    }
                    break block10;
                }
                i = 0;
                while (i < this.extra[0].length) {
                    this.extra[0][i] = 0L;
                    ++i;
                }
                break block10;
            }
            if (otherInits.extra != null) {
                otherLength = otherInits.extra[0].length;
                this.createExtraSpace(otherLength);
                System.arraycopy(otherInits.extra[0], 0, this.extra[0], 0, otherLength);
            }
        }
        return this;
    }

    @Override
    public void resetAssignmentInfo(LocalVariableBinding local) {
        this.resetAssignmentInfo(local.id + this.maxFieldCount);
    }

    public void resetAssignmentInfo(int position) {
        if (this != DEAD_END) {
            if (position < 64) {
                long mask = 1L << position ^ 0xFFFFFFFFFFFFFFFFL;
                this.definiteInits &= mask;
                this.potentialInits &= mask;
            } else {
                int vectorIndex = position / 64 - 1;
                if (this.extra == null || vectorIndex >= this.extra[0].length) {
                    return;
                }
                long[] lArray = this.extra[0];
                int n = vectorIndex;
                long mask = 1L << position % 64 ^ 0xFFFFFFFFFFFFFFFFL;
                lArray[n] = lArray[n] & mask;
                long[] lArray2 = this.extra[1];
                int n2 = vectorIndex;
                lArray2[n2] = lArray2[n2] & mask;
            }
        }
    }

    private void createExtraSpace(int length) {
        this.extra = new long[8][];
        int j = 0;
        while (j < 8) {
            this.extra[j] = new long[length];
            ++j;
        }
        if ((this.tagBits & 0x40) != 0) {
            Arrays.fill(this.extra[6], -1L);
            Arrays.fill(this.extra[7], -1L);
        }
    }

    public void growSpace(int newLength, int copyStart, int copyLength) {
        int j = 0;
        while (j < 8) {
            this.extra[j] = new long[newLength];
            System.arraycopy(this.extra[j], copyStart, this.extra[j], copyStart, copyLength);
            ++j;
        }
        if ((this.tagBits & 0x40) != 0) {
            Arrays.fill(this.extra[6], copyStart + copyLength, newLength, -1L);
            Arrays.fill(this.extra[7], copyStart + copyLength, newLength, -1L);
        }
    }

    public void acceptAllIncomingNullness() {
        this.iNBit = -1L;
        this.iNNBit = -1L;
        if (this.extra != null) {
            Arrays.fill(this.extra[6], -1L);
            Arrays.fill(this.extra[7], -1L);
        }
    }

    public static class AssertionFailedException
    extends RuntimeException {
        private static final long serialVersionUID = 1827352841030089703L;

        public AssertionFailedException(String message) {
            super(message);
        }
    }
}


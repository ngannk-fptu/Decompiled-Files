/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserConstants;
import org.apache.jackrabbit.spi.commons.query.sql.SimpleCharStream;
import org.apache.jackrabbit.spi.commons.query.sql.Token;
import org.apache.jackrabbit.spi.commons.query.sql.TokenMgrError;

public class JCRSQLParserTokenManager
implements JCRSQLParserConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final long[] jjbitVec3 = new long[]{0L, -16384L, -17590038560769L, 0x7FFFFFL};
    static final long[] jjbitVec4 = new long[]{0L, 0L, 0L, -36028797027352577L};
    static final long[] jjbitVec5 = new long[]{0x7FF3FFFFFFFFFFFFL, 9223372036854775294L, -1L, -274156627316187121L};
    static final long[] jjbitVec6 = new long[]{0xFFFFFFL, -65536L, -576458553280167937L, 3L};
    static final long[] jjbitVec7 = new long[]{0L, 0L, -17179879616L, 4503588160110591L};
    static final long[] jjbitVec8 = new long[]{-8194L, -536936449L, -65533L, 234134404065073567L};
    static final long[] jjbitVec9 = new long[]{-562949953421312L, -8547991553L, 127L, 0x707FFFFFF0000L};
    static final long[] jjbitVec10 = new long[]{576460743713488896L, -562949953419266L, 0x7CFFFFFFFFFFFFFFL, 412319973375L};
    static final long[] jjbitVec11 = new long[]{2594073385365405664L, 0x3FF000000L, 271902628478820320L, 0x30003B0000000L};
    static final long[] jjbitVec12 = new long[]{247132830528276448L, 7881300924956672L, 2589004636761075680L, 0x100000000L};
    static final long[] jjbitVec13 = new long[]{2579997437506199520L, 0x3B0000000L, 270153412153034720L, 0L};
    static final long[] jjbitVec14 = new long[]{283724577500946400L, 0x300000000L, 283724577500946400L, 0x340000000L};
    static final long[] jjbitVec15 = new long[]{288228177128316896L, 0x300000000L, 0L, 0L};
    static final long[] jjbitVec16 = new long[]{3799912185593854L, 63L, 2309621682768192918L, 31L};
    static final long[] jjbitVec17 = new long[]{0L, 0x3FFFFFFFEFFL, 0L, 0L};
    static final long[] jjbitVec18 = new long[]{0L, 0L, -4294967296L, 36028797018898495L};
    static final long[] jjbitVec19 = new long[]{5764607523034749677L, 12493387738468353L, -756383734487318528L, 144405459145588743L};
    static final long[] jjbitVec20 = new long[]{-1L, -1L, -4026531841L, 0x3FFFFFFFFFFFFFFL};
    static final long[] jjbitVec21 = new long[]{-3233808385L, 0x3FFFFFFFAAFF3F3FL, 0x5FDFFFFFFFFFFFFFL, 2295745090394464220L};
    static final long[] jjbitVec22 = new long[]{0x4C4000000000L, 0L, 7L, 0L};
    static final long[] jjbitVec23 = new long[]{4389456576640L, -2L, -8587837441L, 0x7FFFFFFFFFFFFFFL};
    static final long[] jjbitVec24 = new long[]{35184372088800L, 0L, 0L, 0L};
    static final long[] jjbitVec25 = new long[]{-1L, -1L, 0x3FFFFFFFFFL, 0L};
    static final long[] jjbitVec26 = new long[]{-1L, -1L, 0xFFFFFFFFFL, 0L};
    static final int[] jjnextStates = new int[]{15, 16, 18, 19, 21, 1, 2, 3, 5, 4, 18, 19, 21, 30, 31, 41, 42, 53, 54, 65, 66, 68, 69, 37, 67, 68, 69, 37, 81, 82, 93, 94, 96, 98, 97, 95, 96, 98, 97, 79, 108, 39, 50, 2, 3, 7, 8, 12, 13, 22, 23};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "\"", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", ":", ";", "<", "=", ">", "?", "_", "|", "[", "]", null, null, null, null, null, null, null, null, null, "<>", ">=", "<=", "||", "..", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT"};
    static final long[] jjtoToken = new long[]{2305842736483270145L, 17180792801L};
    static final long[] jjtoSkip = new long[]{62L, 0L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[109];
    private final int[] jjstateSet = new int[218];
    protected char curChar;
    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '\t': {
                this.jjmatchedKind = 4;
                return this.jjMoveNfa_0(0, 0);
            }
            case '\n': {
                this.jjmatchedKind = 3;
                return this.jjMoveNfa_0(0, 0);
            }
            case '\r': {
                this.jjmatchedKind = 2;
                return this.jjMoveNfa_0(0, 0);
            }
            case ' ': {
                this.jjmatchedKind = 1;
                return this.jjMoveNfa_0(0, 0);
            }
            case '\"': {
                this.jjmatchedKind = 38;
                return this.jjMoveNfa_0(0, 0);
            }
            case '%': {
                this.jjmatchedKind = 39;
                return this.jjMoveNfa_0(0, 0);
            }
            case '&': {
                this.jjmatchedKind = 40;
                return this.jjMoveNfa_0(0, 0);
            }
            case '\'': {
                this.jjmatchedKind = 41;
                return this.jjMoveNfa_0(0, 0);
            }
            case '(': {
                this.jjmatchedKind = 42;
                return this.jjMoveNfa_0(0, 0);
            }
            case ')': {
                this.jjmatchedKind = 43;
                return this.jjMoveNfa_0(0, 0);
            }
            case '*': {
                this.jjmatchedKind = 44;
                return this.jjMoveNfa_0(0, 0);
            }
            case '+': {
                this.jjmatchedKind = 45;
                return this.jjMoveNfa_0(0, 0);
            }
            case ',': {
                this.jjmatchedKind = 46;
                return this.jjMoveNfa_0(0, 0);
            }
            case '-': {
                this.jjmatchedKind = 47;
                return this.jjMoveNfa_0(0, 0);
            }
            case '.': {
                this.jjmatchedKind = 48;
                return this.jjMoveStringLiteralDfa1_0(0L, 512L);
            }
            case '/': {
                this.jjmatchedKind = 49;
                return this.jjMoveNfa_0(0, 0);
            }
            case ':': {
                this.jjmatchedKind = 50;
                return this.jjMoveNfa_0(0, 0);
            }
            case ';': {
                this.jjmatchedKind = 51;
                return this.jjMoveNfa_0(0, 0);
            }
            case '<': {
                this.jjmatchedKind = 52;
                return this.jjMoveStringLiteralDfa1_0(0L, 160L);
            }
            case '=': {
                this.jjmatchedKind = 53;
                return this.jjMoveNfa_0(0, 0);
            }
            case '>': {
                this.jjmatchedKind = 54;
                return this.jjMoveStringLiteralDfa1_0(0L, 64L);
            }
            case '?': {
                this.jjmatchedKind = 55;
                return this.jjMoveNfa_0(0, 0);
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa1_0(24576L, 0L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa1_0(0x4000200L, 0L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa1_0(0x20000000L, 0L);
            }
            case 'D': {
                return this.jjMoveStringLiteralDfa1_0(65536L, 0L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa1_0(0x9000000L, 0L);
            }
            case 'F': {
                return this.jjMoveStringLiteralDfa1_0(524288L, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa1_0(5120L, 0L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa1_0(0x120000L, 0L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa1_0(294912L, 0L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa1_0(0x200800L, 0L);
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa1_0(0x52000000L, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa1_0(0x400000L, 0L);
            }
            case 'W': {
                return this.jjMoveStringLiteralDfa1_0(0x800000L, 0L);
            }
            case '[': {
                this.jjmatchedKind = 58;
                return this.jjMoveNfa_0(0, 0);
            }
            case ']': {
                this.jjmatchedKind = 59;
                return this.jjMoveNfa_0(0, 0);
            }
            case '_': {
                this.jjmatchedKind = 56;
                return this.jjMoveNfa_0(0, 0);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(24576L, 0L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(0x4000200L, 0L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa1_0(0x20000000L, 0L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_0(65536L, 0L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_0(0x9000000L, 0L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(524288L, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(5120L, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(0x120000L, 0L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(294912L, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_0(0x200800L, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_0(0x52000000L, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa1_0(0x400000L, 0L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa1_0(0x800000L, 0L);
            }
            case '|': {
                this.jjmatchedKind = 57;
                return this.jjMoveStringLiteralDfa1_0(0L, 256L);
            }
        }
        return this.jjMoveNfa_0(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_0(long active0, long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 0);
        }
        switch (this.curChar) {
            case '.': {
                if ((active1 & 0x200L) == 0L) break;
                this.jjmatchedKind = 73;
                this.jjmatchedPos = 1;
                break;
            }
            case '=': {
                if ((active1 & 0x40L) != 0L) {
                    this.jjmatchedKind = 70;
                    this.jjmatchedPos = 1;
                    break;
                }
                if ((active1 & 0x80L) == 0L) break;
                this.jjmatchedKind = 71;
                this.jjmatchedPos = 1;
                break;
            }
            case '>': {
                if ((active1 & 0x20L) == 0L) break;
                this.jjmatchedKind = 69;
                this.jjmatchedPos = 1;
                break;
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x6010000L, active1, 0L);
            }
            case 'H': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x800000L, active1, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x10020000L, active1, 0L);
            }
            case 'N': {
                if ((active0 & 0x400L) != 0L) {
                    this.jjmatchedKind = 10;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 8192L, active1, 0L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa2_0(active0, 537952256L, active1, 0L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x40400000L, active1, 0L);
            }
            case 'R': {
                if ((active0 & 0x800L) != 0L) {
                    this.jjmatchedKind = 11;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x280000L, active1, 0L);
            }
            case 'S': {
                if ((active0 & 0x1000L) != 0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x1004000L, active1, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa2_0(active0, 262144L, active1, 0L);
            }
            case 'X': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x8000000L, active1, 0L);
            }
            case 'Y': {
                if ((active0 & 0x200L) == 0L) break;
                this.jjmatchedKind = 9;
                this.jjmatchedPos = 1;
                break;
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x6010000L, active1, 0L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x800000L, active1, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x10020000L, active1, 0L);
            }
            case 'n': {
                if ((active0 & 0x400L) != 0L) {
                    this.jjmatchedKind = 10;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 8192L, active1, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_0(active0, 537952256L, active1, 0L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x40400000L, active1, 0L);
            }
            case 'r': {
                if ((active0 & 0x800L) != 0L) {
                    this.jjmatchedKind = 11;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x280000L, active1, 0L);
            }
            case 's': {
                if ((active0 & 0x1000L) != 0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x1004000L, active1, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(active0, 262144L, active1, 0L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x8000000L, active1, 0L);
            }
            case 'y': {
                if ((active0 & 0x200L) == 0L) break;
                this.jjmatchedKind = 9;
                this.jjmatchedPos = 1;
                break;
            }
            case '|': {
                if ((active1 & 0x100L) == 0L) break;
                this.jjmatchedKind = 72;
                this.jjmatchedPos = 1;
                break;
            }
        }
        return this.jjMoveNfa_0(0, 1);
    }

    private final int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0L) {
            return this.jjMoveNfa_0(0, 1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 1);
        }
        switch (this.curChar) {
            case 'C': {
                if ((active0 & 0x4000L) != 0L) {
                    this.jjmatchedKind = 14;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x9000000L);
            }
            case 'D': {
                if ((active0 & 0x2000L) != 0L) {
                    this.jjmatchedKind = 13;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x200000L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x40800000L);
            }
            case 'K': {
                return this.jjMoveStringLiteralDfa3_0(active0, 131072L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x2040000L);
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x10000000L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x20000000L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa3_0(active0, 524288L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x400000L);
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa3_0(active0, 65536L);
            }
            case 'T': {
                if ((active0 & 0x8000L) != 0L) {
                    this.jjmatchedKind = 15;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x4000000L);
            }
            case 'W': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x100000L);
            }
            case 'c': {
                if ((active0 & 0x4000L) != 0L) {
                    this.jjmatchedKind = 14;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x9000000L);
            }
            case 'd': {
                if ((active0 & 0x2000L) != 0L) {
                    this.jjmatchedKind = 13;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x200000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x40800000L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa3_0(active0, 131072L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x2040000L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x10000000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x20000000L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_0(active0, 524288L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x400000L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(active0, 65536L);
            }
            case 't': {
                if ((active0 & 0x8000L) != 0L) {
                    this.jjmatchedKind = 15;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x4000000L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x100000L);
            }
        }
        return this.jjMoveNfa_0(0, 2);
    }

    private final int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 2);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 2);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x1000000L);
            }
            case 'C': {
                if ((active0 & 0x10000L) == 0L) break;
                this.jjmatchedKind = 16;
                this.jjmatchedPos = 3;
                break;
            }
            case 'E': {
                if ((active0 & 0x20000L) != 0L) {
                    this.jjmatchedKind = 17;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0xA700000L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x10000000L);
            }
            case 'L': {
                if ((active0 & 0x40000L) != 0L) {
                    this.jjmatchedKind = 18;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0x40000000L);
            }
            case 'M': {
                if ((active0 & 0x80000L) == 0L) break;
                this.jjmatchedKind = 19;
                this.jjmatchedPos = 3;
                break;
            }
            case 'R': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x800000L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x20000000L);
            }
            case 'W': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x4000000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x1000000L);
            }
            case 'c': {
                if ((active0 & 0x10000L) == 0L) break;
                this.jjmatchedKind = 16;
                this.jjmatchedPos = 3;
                break;
            }
            case 'e': {
                if ((active0 & 0x20000L) != 0L) {
                    this.jjmatchedKind = 17;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0xA700000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x10000000L);
            }
            case 'l': {
                if ((active0 & 0x40000L) != 0L) {
                    this.jjmatchedKind = 18;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0x40000000L);
            }
            case 'm': {
                if ((active0 & 0x80000L) == 0L) break;
                this.jjmatchedKind = 19;
                this.jjmatchedPos = 3;
                break;
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x800000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x20000000L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x4000000L);
            }
        }
        return this.jjMoveNfa_0(0, 3);
    }

    private final int jjMoveStringLiteralDfa4_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 3);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x20000000L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x2000000L);
            }
            case 'E': {
                if ((active0 & 0x800000L) != 0L) {
                    this.jjmatchedKind = 23;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0x4000000L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x50000000L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x1000000L);
            }
            case 'R': {
                if ((active0 & 0x100000L) != 0L) {
                    this.jjmatchedKind = 20;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x200000L) != 0L) {
                    this.jjmatchedKind = 21;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x400000L) != 0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0x8000000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x20000000L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x2000000L);
            }
            case 'e': {
                if ((active0 & 0x800000L) != 0L) {
                    this.jjmatchedKind = 23;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0x4000000L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x50000000L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x1000000L);
            }
            case 'r': {
                if ((active0 & 0x100000L) != 0L) {
                    this.jjmatchedKind = 20;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x200000L) != 0L) {
                    this.jjmatchedKind = 21;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x400000L) != 0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0x8000000L);
            }
        }
        return this.jjMoveNfa_0(0, 4);
    }

    private final int jjMoveStringLiteralDfa5_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 4);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 4);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x10000000L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x40000000L);
            }
            case 'E': {
                if ((active0 & 0x1000000L) != 0L) {
                    this.jjmatchedKind = 24;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0x4000000L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x20000000L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x8000000L);
            }
            case 'T': {
                if ((active0 & 0x2000000L) == 0L) break;
                this.jjmatchedKind = 25;
                this.jjmatchedPos = 5;
                break;
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x10000000L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x40000000L);
            }
            case 'e': {
                if ((active0 & 0x1000000L) != 0L) {
                    this.jjmatchedKind = 24;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0x4000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x20000000L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x8000000L);
            }
            case 't': {
                if ((active0 & 0x2000000L) == 0L) break;
                this.jjmatchedKind = 25;
                this.jjmatchedPos = 5;
                break;
            }
        }
        return this.jjMoveNfa_0(0, 5);
    }

    private final int jjMoveStringLiteralDfa6_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 5);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 5);
        }
        switch (this.curChar) {
            case 'H': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x40000000L);
            }
            case 'N': {
                if ((active0 & 0x4000000L) != 0L) {
                    this.jjmatchedKind = 26;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0x20000000L);
            }
            case 'R': {
                if ((active0 & 0x10000000L) == 0L) break;
                this.jjmatchedKind = 28;
                this.jjmatchedPos = 6;
                break;
            }
            case 'T': {
                if ((active0 & 0x8000000L) == 0L) break;
                this.jjmatchedKind = 27;
                this.jjmatchedPos = 6;
                break;
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x40000000L);
            }
            case 'n': {
                if ((active0 & 0x4000000L) != 0L) {
                    this.jjmatchedKind = 26;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0x20000000L);
            }
            case 'r': {
                if ((active0 & 0x10000000L) == 0L) break;
                this.jjmatchedKind = 28;
                this.jjmatchedPos = 6;
                break;
            }
            case 't': {
                if ((active0 & 0x8000000L) == 0L) break;
                this.jjmatchedKind = 27;
                this.jjmatchedPos = 6;
                break;
            }
        }
        return this.jjMoveNfa_0(0, 6);
    }

    private final int jjMoveStringLiteralDfa7_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 6);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 6);
        }
        switch (this.curChar) {
            case 'E': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x40000000L);
            }
            case 'S': {
                if ((active0 & 0x20000000L) == 0L) break;
                this.jjmatchedKind = 29;
                this.jjmatchedPos = 7;
                break;
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x40000000L);
            }
            case 's': {
                if ((active0 & 0x20000000L) == 0L) break;
                this.jjmatchedKind = 29;
                this.jjmatchedPos = 7;
                break;
            }
        }
        return this.jjMoveNfa_0(0, 7);
    }

    private final int jjMoveStringLiteralDfa8_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 7);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 7);
        }
        switch (this.curChar) {
            case 'C': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0x40000000L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0x40000000L);
            }
        }
        return this.jjMoveNfa_0(0, 8);
    }

    private final int jjMoveStringLiteralDfa9_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 8);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 8);
        }
        switch (this.curChar) {
            case 'K': {
                if ((active0 & 0x40000000L) == 0L) break;
                this.jjmatchedKind = 30;
                this.jjmatchedPos = 9;
                break;
            }
            case 'k': {
                if ((active0 & 0x40000000L) == 0L) break;
                this.jjmatchedKind = 30;
                this.jjmatchedPos = 9;
                break;
            }
        }
        return this.jjMoveNfa_0(0, 9);
    }

    private final void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }

    private final void jjAddStates(int start, int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
        } while (start++ != end);
    }

    private final void jjCheckNAddTwoStates(int state1, int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }

    private final void jjCheckNAddStates(int start, int end) {
        do {
            this.jjCheckNAdd(jjnextStates[start]);
        } while (start++ != end);
    }

    private final void jjCheckNAddStates(int start) {
        this.jjCheckNAdd(jjnextStates[start]);
        this.jjCheckNAdd(jjnextStates[start + 1]);
    }

    private final int jjMoveNfa_0(int startState, int curPos) {
        int strKind = this.jjmatchedKind;
        int strPos = this.jjmatchedPos;
        int seenUpto = curPos + 1;
        this.input_stream.backup(seenUpto);
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            throw new Error("Internal Error");
        }
        curPos = 0;
        int startsAt = 0;
        this.jjnewStateCnt = 109;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block133: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                this.jjCheckNAddStates(0, 4);
                            } else if ((0x280000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(15, 18);
                            } else if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(26, 27);
                            } else if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(12, 13);
                            } else if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(7, 10);
                            }
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAddStates(5, 7);
                            break;
                        }
                        case 2: {
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\n' || kind <= 5) continue block133;
                            kind = 5;
                            break;
                        }
                        case 5: {
                            if ((0x7FF000000000000L & l) == 0L) continue block133;
                            if (kind > 60) {
                                kind = 60;
                            }
                            this.jjAddStates(8, 9);
                            break;
                        }
                        case 6: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(7, 10);
                            break;
                        }
                        case 7: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(7, 8);
                            break;
                        }
                        case 8: {
                            if (this.curChar != '\"') continue block133;
                            if (kind > 64) {
                                kind = 64;
                            }
                            this.jjCheckNAdd(9);
                            break;
                        }
                        case 9: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(7, 8);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAdd(9);
                            break;
                        }
                        case 11: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 12: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '\'') continue block133;
                            if (kind > 98) {
                                kind = 98;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 14: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(15, 18);
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) continue block133;
                            if (kind > 74) {
                                kind = 74;
                            }
                            this.jjCheckNAddTwoStates(15, 16);
                            break;
                        }
                        case 16: {
                            if (this.curChar != '.') continue block133;
                            if (kind > 74) {
                                kind = 74;
                            }
                            this.jjCheckNAdd(17);
                            break;
                        }
                        case 17: {
                            if ((0x3FF000000000000L & l) == 0L) continue block133;
                            if (kind > 74) {
                                kind = 74;
                            }
                            this.jjCheckNAdd(17);
                            break;
                        }
                        case 18: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(10, 12);
                            break;
                        }
                        case 19: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddTwoStates(20, 21);
                            break;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(20, 21);
                            break;
                        }
                        case 22: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(23);
                            break;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) == 0L) continue block133;
                            if (kind > 76) {
                                kind = 76;
                            }
                            this.jjCheckNAdd(23);
                            break;
                        }
                        case 24: {
                            if ((0x3FF000000000000L & l) == 0L) continue block133;
                            if (kind > 74) {
                                kind = 74;
                            }
                            this.jjCheckNAddStates(0, 4);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddTwoStates(26, 27);
                            break;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) == 0L) continue block133;
                            if (kind > 74) {
                                kind = 74;
                            }
                            this.jjCheckNAdd(26);
                            break;
                        }
                        case 27: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(27, 21);
                            break;
                        }
                        case 30: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(13, 14);
                            break;
                        }
                        case 31: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAdd(32);
                            break;
                        }
                        case 32: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(32, 33);
                            break;
                        }
                        case 33: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(34);
                            break;
                        }
                        case 34: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(34, 35);
                            break;
                        }
                        case 35: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(36);
                            break;
                        }
                        case 36: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(36, 37);
                            break;
                        }
                        case 37: {
                            if (this.curChar != '\'' || kind <= 81) continue block133;
                            kind = 81;
                            break;
                        }
                        case 41: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(15, 16);
                            break;
                        }
                        case 42: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAdd(43);
                            break;
                        }
                        case 43: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(43, 44);
                            break;
                        }
                        case 44: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(45);
                            break;
                        }
                        case 45: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(45, 46);
                            break;
                        }
                        case 46: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(47);
                            break;
                        }
                        case 47: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(47, 48);
                            break;
                        }
                        case 48: {
                            if (this.curChar != '\'' || kind <= 82) continue block133;
                            kind = 82;
                            break;
                        }
                        case 53: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(17, 18);
                            break;
                        }
                        case 54: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAdd(55);
                            break;
                        }
                        case 55: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(55, 56);
                            break;
                        }
                        case 56: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(57);
                            break;
                        }
                        case 57: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(57, 58);
                            break;
                        }
                        case 58: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(59);
                            break;
                        }
                        case 59: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(59, 60);
                            break;
                        }
                        case 60: {
                            if (this.curChar != ' ') break;
                            this.jjCheckNAdd(61);
                            break;
                        }
                        case 61: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(61, 62);
                            break;
                        }
                        case 62: {
                            if (this.curChar != ':') break;
                            this.jjCheckNAdd(63);
                            break;
                        }
                        case 63: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(63, 64);
                            break;
                        }
                        case 64: {
                            if (this.curChar != ':') break;
                            this.jjCheckNAdd(65);
                            break;
                        }
                        case 65: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(19, 23);
                            break;
                        }
                        case 66: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(24, 27);
                            break;
                        }
                        case 67: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(24, 27);
                            break;
                        }
                        case 69: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(70);
                            break;
                        }
                        case 70: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(70, 71);
                            break;
                        }
                        case 71: {
                            if (this.curChar != ':') break;
                            this.jjCheckNAdd(72);
                            break;
                        }
                        case 72: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(72, 37);
                            break;
                        }
                        case 81: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(28, 29);
                            break;
                        }
                        case 82: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAdd(83);
                            break;
                        }
                        case 83: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(83, 84);
                            break;
                        }
                        case 84: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(85);
                            break;
                        }
                        case 85: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(85, 86);
                            break;
                        }
                        case 86: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(87);
                            break;
                        }
                        case 87: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(87, 88);
                            break;
                        }
                        case 88: {
                            if (this.curChar != ' ') break;
                            this.jjCheckNAdd(89);
                            break;
                        }
                        case 89: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(89, 90);
                            break;
                        }
                        case 90: {
                            if (this.curChar != ':') break;
                            this.jjCheckNAdd(91);
                            break;
                        }
                        case 91: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(91, 92);
                            break;
                        }
                        case 92: {
                            if (this.curChar != ':') break;
                            this.jjCheckNAdd(93);
                            break;
                        }
                        case 93: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(30, 34);
                            break;
                        }
                        case 94: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(35, 38);
                            break;
                        }
                        case 95: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(35, 38);
                            break;
                        }
                        case 97: {
                            if (this.curChar != '\'' || kind <= 83) continue block133;
                            kind = 83;
                            break;
                        }
                        case 98: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(99);
                            break;
                        }
                        case 99: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(99, 100);
                            break;
                        }
                        case 100: {
                            if (this.curChar != ':') break;
                            this.jjCheckNAdd(101);
                            break;
                        }
                        case 101: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(101, 97);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block134: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 60) {
                                    kind = 60;
                                }
                                this.jjCheckNAddTwoStates(5, 4);
                            }
                            if (this.curChar == 'T') {
                                this.jjAddStates(39, 40);
                                break;
                            }
                            if (this.curChar != 'D') break;
                            this.jjAddStates(41, 42);
                            break;
                        }
                        case 2: {
                            this.jjAddStates(43, 44);
                            break;
                        }
                        case 4: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L) continue block134;
                            if (kind > 60) {
                                kind = 60;
                            }
                            this.jjCheckNAddTwoStates(5, 4);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '_') continue block134;
                            if (kind > 60) {
                                kind = 60;
                            }
                            this.jjCheckNAddTwoStates(5, 4);
                            break;
                        }
                        case 7: {
                            this.jjAddStates(45, 46);
                            break;
                        }
                        case 12: {
                            this.jjAddStates(47, 48);
                            break;
                        }
                        case 21: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(49, 50);
                            break;
                        }
                        case 28: {
                            if (this.curChar != 'D') break;
                            this.jjAddStates(41, 42);
                            break;
                        }
                        case 29: {
                            if (this.curChar != 'E') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                        case 38: {
                            if (this.curChar != 'T') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 39: {
                            if (this.curChar != 'A') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 40: {
                            if (this.curChar != 'E') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 49: {
                            if (this.curChar != 'T') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 50: {
                            if (this.curChar != 'A') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 49;
                            break;
                        }
                        case 51: {
                            if (this.curChar != 'T') break;
                            this.jjAddStates(39, 40);
                            break;
                        }
                        case 52: {
                            if (this.curChar != 'P') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 53;
                            break;
                        }
                        case 60: {
                            if (this.curChar != 'T') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 61;
                            break;
                        }
                        case 68: {
                            if (this.curChar != 'Z') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 73: {
                            if (this.curChar != 'M') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 52;
                            break;
                        }
                        case 74: {
                            if (this.curChar != 'A') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 73;
                            break;
                        }
                        case 75: {
                            if (this.curChar != 'T') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 74;
                            break;
                        }
                        case 76: {
                            if (this.curChar != 'S') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 75;
                            break;
                        }
                        case 77: {
                            if (this.curChar != 'E') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 76;
                            break;
                        }
                        case 78: {
                            if (this.curChar != 'M') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 77;
                            break;
                        }
                        case 79: {
                            if (this.curChar != 'I') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 78;
                            break;
                        }
                        case 80: {
                            if (this.curChar != 'P') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 81;
                            break;
                        }
                        case 88: {
                            if (this.curChar != 'T') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 89;
                            break;
                        }
                        case 96: {
                            if (this.curChar != 'Z') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 97;
                            break;
                        }
                        case 102: {
                            if (this.curChar != 'M') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 80;
                            break;
                        }
                        case 103: {
                            if (this.curChar != 'A') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 102;
                            break;
                        }
                        case 104: {
                            if (this.curChar != 'T') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 103;
                            break;
                        }
                        case 105: {
                            if (this.curChar != 'S') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 104;
                            break;
                        }
                        case 106: {
                            if (this.curChar != 'E') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 105;
                            break;
                        }
                        case 107: {
                            if (this.curChar != 'M') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 106;
                            break;
                        }
                        case 108: {
                            if (this.curChar != 'I') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 107;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 0x3F);
                int i2 = (this.curChar & 0xFF) >> 6;
                long l2 = 1L << (this.curChar & 0x3F);
                block135: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 4: {
                            if (!JCRSQLParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block135;
                            if (kind > 60) {
                                kind = 60;
                            }
                            this.jjCheckNAddTwoStates(5, 4);
                            break;
                        }
                        case 2: {
                            if (!JCRSQLParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(43, 44);
                            break;
                        }
                        case 7: {
                            if (!JCRSQLParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(45, 46);
                            break;
                        }
                        case 12: {
                            if (!JCRSQLParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(47, 48);
                            break;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            i = this.jjnewStateCnt;
            this.jjnewStateCnt = startsAt;
            if (i == (startsAt = 109 - this.jjnewStateCnt)) break;
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                // empty catch block
                break;
            }
        }
        if (this.jjmatchedPos > strPos) {
            return curPos;
        }
        int toRet = Math.max(curPos, seenUpto);
        if (curPos < toRet) {
            i = toRet - Math.min(curPos, seenUpto);
            while (i-- > 0) {
                try {
                    this.curChar = this.input_stream.readChar();
                }
                catch (IOException e) {
                    throw new Error("Internal Error : Please send a bug report.");
                }
            }
        }
        if (this.jjmatchedPos < strPos) {
            this.jjmatchedKind = strKind;
            this.jjmatchedPos = strPos;
        } else if (this.jjmatchedPos == strPos && this.jjmatchedKind > strKind) {
            this.jjmatchedKind = strKind;
        }
        return toRet;
    }

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec2[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec4[i2] & l2) != 0L;
            }
            case 1: {
                return (jjbitVec5[i2] & l2) != 0L;
            }
            case 2: {
                return (jjbitVec6[i2] & l2) != 0L;
            }
            case 3: {
                return (jjbitVec7[i2] & l2) != 0L;
            }
            case 4: {
                return (jjbitVec8[i2] & l2) != 0L;
            }
            case 5: {
                return (jjbitVec9[i2] & l2) != 0L;
            }
            case 6: {
                return (jjbitVec10[i2] & l2) != 0L;
            }
            case 9: {
                return (jjbitVec11[i2] & l2) != 0L;
            }
            case 10: {
                return (jjbitVec12[i2] & l2) != 0L;
            }
            case 11: {
                return (jjbitVec13[i2] & l2) != 0L;
            }
            case 12: {
                return (jjbitVec14[i2] & l2) != 0L;
            }
            case 13: {
                return (jjbitVec15[i2] & l2) != 0L;
            }
            case 14: {
                return (jjbitVec16[i2] & l2) != 0L;
            }
            case 15: {
                return (jjbitVec17[i2] & l2) != 0L;
            }
            case 16: {
                return (jjbitVec18[i2] & l2) != 0L;
            }
            case 17: {
                return (jjbitVec19[i2] & l2) != 0L;
            }
            case 30: {
                return (jjbitVec20[i2] & l2) != 0L;
            }
            case 31: {
                return (jjbitVec21[i2] & l2) != 0L;
            }
            case 33: {
                return (jjbitVec22[i2] & l2) != 0L;
            }
            case 48: {
                return (jjbitVec23[i2] & l2) != 0L;
            }
            case 49: {
                return (jjbitVec24[i2] & l2) != 0L;
            }
            case 159: {
                return (jjbitVec25[i2] & l2) != 0L;
            }
            case 215: {
                return (jjbitVec26[i2] & l2) != 0L;
            }
        }
        return (jjbitVec3[i1] & l1) != 0L;
    }

    public JCRSQLParserTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public JCRSQLParserTokenManager(SimpleCharStream stream, int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }

    public void ReInit(SimpleCharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }

    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 109;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }

    protected Token jjFillToken() {
        Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        String im = jjstrLiteralImages[this.jjmatchedKind];
        t.image = im == null ? this.input_stream.GetImage() : im;
        t.beginLine = this.input_stream.getBeginLine();
        t.beginColumn = this.input_stream.getBeginColumn();
        t.endLine = this.input_stream.getEndLine();
        t.endColumn = this.input_stream.getEndColumn();
        return t;
    }

    public Token getNextToken() {
        int curPos;
        block7: {
            Object specialToken = null;
            curPos = 0;
            do {
                try {
                    this.curChar = this.input_stream.BeginToken();
                }
                catch (IOException e) {
                    this.jjmatchedKind = 0;
                    Token matchedToken = this.jjFillToken();
                    return matchedToken;
                }
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_0();
                if (this.jjmatchedKind == Integer.MAX_VALUE) break block7;
                if (this.jjmatchedPos + 1 >= curPos) continue;
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            } while ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0L);
            Token matchedToken = this.jjFillToken();
            return matchedToken;
        }
        int error_line = this.input_stream.getEndLine();
        int error_column = this.input_stream.getEndColumn();
        String error_after = null;
        boolean EOFSeen = false;
        try {
            this.input_stream.readChar();
            this.input_stream.backup(1);
        }
        catch (IOException e1) {
            EOFSeen = true;
            String string = error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
            if (this.curChar == '\n' || this.curChar == '\r') {
                ++error_line;
                error_column = 0;
            }
            ++error_column;
        }
        if (!EOFSeen) {
            this.input_stream.backup(1);
            error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
        }
        throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
    }
}


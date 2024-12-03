/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import java.io.IOException;
import java.io.PrintStream;
import net.sf.ehcache.search.parser.EhcacheSearchParserConstants;
import net.sf.ehcache.search.parser.SimpleCharStream;
import net.sf.ehcache.search.parser.Token;
import net.sf.ehcache.search.parser.TokenMgrError;

public class EhcacheSearchParserTokenManager
implements EhcacheSearchParserConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{3, 4, 6, 22, 24};
    public static final String[] jjstrLiteralImages = new String[]{"", "*", null, ",", "(", ")", "[", "]", "=", "!=", null, null, ">=", ">", "<=", "<", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT"};
    static final long[] jjtoToken = new long[]{0x3FFFFFFFFFFFFFFL};
    static final long[] jjtoSkip = new long[]{0x3800000000000000L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[25];
    private final int[] jjstateSet = new int[50];
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

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '\t': {
                this.jjmatchedKind = 60;
                return this.jjMoveNfa_0(0, 0);
            }
            case '\n': {
                this.jjmatchedKind = 61;
                return this.jjMoveNfa_0(0, 0);
            }
            case ' ': {
                this.jjmatchedKind = 59;
                return this.jjMoveNfa_0(0, 0);
            }
            case '!': {
                return this.jjMoveStringLiteralDfa1_0(512L);
            }
            case '(': {
                this.jjmatchedKind = 4;
                return this.jjMoveStringLiteralDfa1_0(70334384439296L);
            }
            case ')': {
                this.jjmatchedKind = 5;
                return this.jjMoveNfa_0(0, 0);
            }
            case '*': {
                this.jjmatchedKind = 1;
                return this.jjMoveNfa_0(0, 0);
            }
            case ',': {
                this.jjmatchedKind = 3;
                return this.jjMoveNfa_0(0, 0);
            }
            case '<': {
                this.jjmatchedKind = 15;
                return this.jjMoveStringLiteralDfa1_0(16384L);
            }
            case '=': {
                this.jjmatchedKind = 8;
                return this.jjMoveNfa_0(0, 0);
            }
            case '>': {
                this.jjmatchedKind = 13;
                return this.jjMoveStringLiteralDfa1_0(4096L);
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa1_0(703687475331076L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa1_0(0x200000L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa1_0(0x80000000L);
            }
            case 'D': {
                return this.jjMoveStringLiteralDfa1_0(0x1400000000000L);
            }
            case 'F': {
                return this.jjMoveStringLiteralDfa1_0(0x200000000L);
            }
            case 'G': {
                return this.jjMoveStringLiteralDfa1_0(0x20000000000000L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa1_0(22023168L);
            }
            case 'K': {
                return this.jjMoveStringLiteralDfa1_0(65536L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa1_0(0x400800000L);
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa1_0(0x30000000L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa1_0(524288L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa1_0(0x4000004000000L);
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa1_0(0x108000000L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa1_0(0x18000000000000L);
            }
            case 'V': {
                return this.jjMoveStringLiteralDfa1_0(131072L);
            }
            case 'W': {
                return this.jjMoveStringLiteralDfa1_0(262144L);
            }
            case '[': {
                this.jjmatchedKind = 6;
                return this.jjMoveNfa_0(0, 0);
            }
            case ']': {
                this.jjmatchedKind = 7;
                return this.jjMoveNfa_0(0, 0);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(703687475331076L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(0x200000L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa1_0(0x80000000L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_0(0x1400000000000L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(0x200000000L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa1_0(0x20000000000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(22023168L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa1_0(65536L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(0x400800000L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_0(0x30000000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(524288L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_0(0x4000004000000L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_0(0x108000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa1_0(0x18000000000000L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa1_0(131072L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa1_0(262144L);
            }
        }
        return this.jjMoveNfa_0(0, 0);
    }

    private int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 0);
        }
        switch (this.curChar) {
            case '=': {
                if ((active0 & 0x200L) != 0L) {
                    this.jjmatchedKind = 9;
                    this.jjmatchedPos = 1;
                    break;
                }
                if ((active0 & 0x1000L) != 0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 1;
                    break;
                }
                if ((active0 & 0x4000L) == 0L) break;
                this.jjmatchedKind = 14;
                this.jjmatchedPos = 1;
                break;
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x10020000L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x1800000000L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x200000000000L);
            }
            case 'D': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x60000000000L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa2_0(active0, 351848018018304L);
            }
            case 'F': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x10000000000L);
            }
            case 'H': {
                return this.jjMoveStringLiteralDfa2_0(active0, 262144L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa2_0(active0, 155164082176L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x8000400004L);
            }
            case 'N': {
                if ((active0 & 0x1000000L) != 0L) {
                    this.jjmatchedKind = 24;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x2000000L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x80080000L);
            }
            case 'R': {
                if ((active0 & 0x4000000L) != 0L) {
                    this.jjmatchedKind = 26;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x24000200000000L);
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa2_0(active0, 7485750040857600L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x8000000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x10020000L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x1800000000L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x200000000000L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x60000000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 351848018018304L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x10000000000L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa2_0(active0, 262144L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_0(active0, 155164082176L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x8000400004L);
            }
            case 'n': {
                if ((active0 & 0x1000000L) != 0L) {
                    this.jjmatchedKind = 24;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x2000000L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x80080000L);
            }
            case 'r': {
                if ((active0 & 0x4000000L) != 0L) {
                    this.jjmatchedKind = 26;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x24000200000000L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa2_0(active0, 7485750040857600L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x8000000L);
            }
        }
        return this.jjMoveNfa_0(0, 1);
    }

    private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 1);
        }
        switch (this.curChar) {
            case ' ': {
                return this.jjMoveStringLiteralDfa3_0(active0, 3072L);
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x40000000000L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x100000L);
            }
            case 'C': {
                if ((active0 & 0x800000000000L) != 0L) {
                    this.jjmatchedKind = 47;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x2000000000000L);
            }
            case 'D': {
                if ((active0 & 0x2000000L) != 0L) {
                    this.jjmatchedKind = 25;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x4000000000000L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa3_0(active0, 6755399441317888L);
            }
            case 'H': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x204000000000L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x400000L);
            }
            case 'K': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x800000L);
            }
            case 'L': {
                if ((active0 & 4L) != 0L) {
                    this.jjmatchedKind = 2;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x10100020000L);
            }
            case 'M': {
                if ((active0 & 0x8000000L) != 0L) {
                    this.jjmatchedKind = 27;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x400000000L);
            }
            case 'N': {
                if ((active0 & 0x20000000L) != 0L) {
                    this.jjmatchedKind = 29;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x2000000000L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa3_0(active0, 9009990983483392L);
            }
            case 'Q': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x100000000000L);
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x1400000000000L);
            }
            case 'T': {
                if ((active0 & 0x80000L) != 0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x80000200000L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x80000000L);
            }
            case 'X': {
                if ((active0 & 0x10000000L) == 0L) break;
                this.jjmatchedKind = 28;
                this.jjmatchedPos = 2;
                break;
            }
            case 'Y': {
                if ((active0 & 0x10000L) != 0L) {
                    this.jjmatchedKind = 16;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x1000000000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x40000000000L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x100000L);
            }
            case 'c': {
                if ((active0 & 0x800000000000L) != 0L) {
                    this.jjmatchedKind = 47;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x2000000000000L);
            }
            case 'd': {
                if ((active0 & 0x2000000L) != 0L) {
                    this.jjmatchedKind = 25;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x4000000000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_0(active0, 6755399441317888L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x204000000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x400000L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x800000L);
            }
            case 'l': {
                if ((active0 & 4L) != 0L) {
                    this.jjmatchedKind = 2;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x10100020000L);
            }
            case 'm': {
                if ((active0 & 0x8000000L) != 0L) {
                    this.jjmatchedKind = 27;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x400000000L);
            }
            case 'n': {
                if ((active0 & 0x20000000L) != 0L) {
                    this.jjmatchedKind = 29;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x2000000000L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_0(active0, 9009990983483392L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x100000000000L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x1400000000000L);
            }
            case 't': {
                if ((active0 & 0x80000L) != 0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x80000200000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x80000000L);
            }
            case 'x': {
                if ((active0 & 0x10000000L) == 0L) break;
                this.jjmatchedKind = 28;
                this.jjmatchedPos = 2;
                break;
            }
            case 'y': {
                if ((active0 & 0x10000L) != 0L) {
                    this.jjmatchedKind = 16;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x1000000000L);
            }
        }
        return this.jjMoveNfa_0(0, 2);
    }

    private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
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
            case ' ': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x18000000000000L);
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x200000000000L);
            }
            case 'C': {
                if ((active0 & 0x400000000000L) != 0L) {
                    this.jjmatchedKind = 46;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0x1000000000000L);
            }
            case 'E': {
                if ((active0 & 0x800000L) != 0L) {
                    this.jjmatchedKind = 23;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0x6000100100000L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x400000000L);
            }
            case 'K': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x400000L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x100000000000L);
            }
            case 'M': {
                if ((active0 & 0x200000000L) == 0L) break;
                this.jjmatchedKind = 33;
                this.jjmatchedPos = 3;
                break;
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x8080000C00L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa4_0(active0, 1408749273088L);
            }
            case 'R': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x80000040000L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x43000000000L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x20020000020000L);
            }
            case 'W': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x200000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x200000000000L);
            }
            case 'c': {
                if ((active0 & 0x400000000000L) != 0L) {
                    this.jjmatchedKind = 46;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0x1000000000000L);
            }
            case 'e': {
                if ((active0 & 0x800000L) != 0L) {
                    this.jjmatchedKind = 23;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0x6000100100000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x400000000L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x400000L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x100000000000L);
            }
            case 'm': {
                if ((active0 & 0x200000000L) == 0L) break;
                this.jjmatchedKind = 33;
                this.jjmatchedPos = 3;
                break;
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x8080000C00L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa4_0(active0, 1408749273088L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x80000040000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x43000000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x20020000020000L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x200000L);
            }
        }
        return this.jjMoveNfa_0(0, 3);
    }

    private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
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
            case ')': {
                if ((active0 & 0x2000000000L) == 0L) break;
                this.jjmatchedKind = 37;
                this.jjmatchedPos = 4;
                break;
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x10000000000L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x20000000000L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x18000100000000L);
            }
            case 'D': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x100000000000L);
            }
            case 'E': {
                if ((active0 & 0x20000L) != 0L) {
                    this.jjmatchedKind = 17;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x40000L) != 0L) {
                    this.jjmatchedKind = 18;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x400000L) != 0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 285941744795648L);
            }
            case 'G': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x8000000000L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x80000000000L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x800000000L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x2000000000000L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa5_0(active0, 2048L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x20000000000000L);
            }
            case 'R': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x4204000000000L);
            }
            case 'T': {
                if ((active0 & 0x80000000L) != 0L) {
                    this.jjmatchedKind = 31;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x400000000L) != 0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0x100000L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa5_0(active0, 1024L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x10000000000L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x20000000000L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x18000100000000L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x100000000000L);
            }
            case 'e': {
                if ((active0 & 0x20000L) != 0L) {
                    this.jjmatchedKind = 17;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x40000L) != 0L) {
                    this.jjmatchedKind = 18;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x400000L) != 0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 285941744795648L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x8000000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x80000000000L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x800000000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x2000000000000L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa5_0(active0, 2048L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x20000000000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x4204000000000L);
            }
            case 't': {
                if ((active0 & 0x80000000L) != 0L) {
                    this.jjmatchedKind = 31;
                    this.jjmatchedPos = 4;
                } else if ((active0 & 0x400000000L) != 0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0x100000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa5_0(active0, 1024L);
            }
        }
        return this.jjMoveNfa_0(0, 4);
    }

    private int jjMoveStringLiteralDfa5_0(long old0, long active0) {
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
            case ' ': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x24000000000000L);
            }
            case ')': {
                if ((active0 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 35;
                    this.jjmatchedPos = 5;
                    break;
                }
                if ((active0 & 0x1000000000L) != 0L) {
                    this.jjmatchedKind = 36;
                    this.jjmatchedPos = 5;
                    break;
                }
                if ((active0 & 0x8000000000L) != 0L) {
                    this.jjmatchedKind = 39;
                    this.jjmatchedPos = 5;
                    break;
                }
                if ((active0 & 0x40000000000L) != 0L) {
                    this.jjmatchedKind = 42;
                    this.jjmatchedPos = 5;
                    break;
                }
                if ((active0 & 0x200000000000L) == 0L) break;
                this.jjmatchedKind = 45;
                this.jjmatchedPos = 5;
                break;
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x18100000000000L);
            }
            case 'D': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x2000000000000L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x200000L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x20000000400L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x1080000000000L);
            }
            case 'T': {
                if ((active0 & 0x100000000L) != 0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 1374389536768L);
            }
            case 'W': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x100000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x18100000000000L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x2000000000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x200000L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x20000000400L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x1080000000000L);
            }
            case 't': {
                if ((active0 & 0x100000000L) != 0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 1374389536768L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x100000L);
            }
        }
        return this.jjMoveNfa_0(0, 5);
    }

    private int jjMoveStringLiteralDfa6_0(long old0, long active0) {
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
            case ' ': {
                return this.jjMoveStringLiteralDfa7_0(active0, 2048L);
            }
            case ')': {
                if ((active0 & 0x4000000000L) != 0L) {
                    this.jjmatchedKind = 38;
                    this.jjmatchedPos = 6;
                    break;
                }
                if ((active0 & 0x10000000000L) == 0L) break;
                this.jjmatchedKind = 40;
                this.jjmatchedPos = 6;
                break;
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x24000000000000L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x18000000000000L);
            }
            case 'D': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x1000000000000L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x20000100000L);
            }
            case 'G': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x80000000000L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x2000000000000L);
            }
            case 'L': {
                if ((active0 & 0x400L) == 0L) break;
                this.jjmatchedKind = 10;
                this.jjmatchedPos = 6;
                break;
            }
            case 'N': {
                if ((active0 & 0x200000L) == 0L) break;
                this.jjmatchedKind = 21;
                this.jjmatchedPos = 6;
                break;
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x100000000000L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x24000000000000L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x18000000000000L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x1000000000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x20000100000L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x80000000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x2000000000000L);
            }
            case 'l': {
                if ((active0 & 0x400L) == 0L) break;
                this.jjmatchedKind = 10;
                this.jjmatchedPos = 6;
                break;
            }
            case 'n': {
                if ((active0 & 0x200000L) == 0L) break;
                this.jjmatchedKind = 21;
                this.jjmatchedPos = 6;
                break;
            }
            case 't': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x100000000000L);
            }
        }
        return this.jjMoveNfa_0(0, 6);
    }

    private int jjMoveStringLiteralDfa7_0(long old0, long active0) {
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
            case ')': {
                if ((active0 & 0x20000000000L) != 0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 7;
                    break;
                }
                if ((active0 & 0x80000000000L) == 0L) break;
                this.jjmatchedKind = 43;
                this.jjmatchedPos = 7;
                break;
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x100000100000L);
            }
            case 'H': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x18000000000000L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x1000000000000L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x2000000000800L);
            }
            case 'Y': {
                if ((active0 & 0x4000000000000L) != 0L) {
                    this.jjmatchedKind = 50;
                    this.jjmatchedPos = 7;
                    break;
                }
                if ((active0 & 0x20000000000000L) == 0L) break;
                this.jjmatchedKind = 53;
                this.jjmatchedPos = 7;
                break;
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x100000100000L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x18000000000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x1000000000000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x2000000000800L);
            }
            case 'y': {
                if ((active0 & 0x4000000000000L) != 0L) {
                    this.jjmatchedKind = 50;
                    this.jjmatchedPos = 7;
                    break;
                }
                if ((active0 & 0x20000000000000L) == 0L) break;
                this.jjmatchedKind = 53;
                this.jjmatchedPos = 7;
                break;
            }
        }
        return this.jjMoveNfa_0(0, 7);
    }

    private int jjMoveStringLiteralDfa8_0(long old0, long active0) {
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
            case ')': {
                if ((active0 & 0x100000000000L) == 0L) break;
                this.jjmatchedKind = 44;
                this.jjmatchedPos = 8;
                break;
            }
            case 'E': {
                if ((active0 & 0x8000000000000L) != 0L) {
                    this.jjmatchedKind = 51;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0x10000000000000L);
            }
            case 'G': {
                if ((active0 & 0x2000000000000L) == 0L) break;
                this.jjmatchedKind = 49;
                this.jjmatchedPos = 8;
                break;
            }
            case 'N': {
                if ((active0 & 0x100000L) != 0L) {
                    this.jjmatchedKind = 20;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0x1000000000000L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa9_0(active0, 2048L);
            }
            case 'e': {
                if ((active0 & 0x8000000000000L) != 0L) {
                    this.jjmatchedKind = 51;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0x10000000000000L);
            }
            case 'g': {
                if ((active0 & 0x2000000000000L) == 0L) break;
                this.jjmatchedKind = 49;
                this.jjmatchedPos = 8;
                break;
            }
            case 'n': {
                if ((active0 & 0x100000L) != 0L) {
                    this.jjmatchedKind = 20;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0x1000000000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa9_0(active0, 2048L);
            }
        }
        return this.jjMoveNfa_0(0, 8);
    }

    private int jjMoveStringLiteralDfa9_0(long old0, long active0) {
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
            case ' ': {
                return this.jjMoveStringLiteralDfa10_0(active0, 0x10000000000000L);
            }
            case 'G': {
                if ((active0 & 0x1000000000000L) == 0L) break;
                this.jjmatchedKind = 48;
                this.jjmatchedPos = 9;
                break;
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa10_0(active0, 2048L);
            }
            case 'g': {
                if ((active0 & 0x1000000000000L) == 0L) break;
                this.jjmatchedKind = 48;
                this.jjmatchedPos = 9;
                break;
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa10_0(active0, 2048L);
            }
        }
        return this.jjMoveNfa_0(0, 9);
    }

    private int jjMoveStringLiteralDfa10_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 9);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 9);
        }
        switch (this.curChar) {
            case 'L': {
                if ((active0 & 0x800L) == 0L) break;
                this.jjmatchedKind = 11;
                this.jjmatchedPos = 10;
                break;
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0x10000000000000L);
            }
            case 'l': {
                if ((active0 & 0x800L) == 0L) break;
                this.jjmatchedKind = 11;
                this.jjmatchedPos = 10;
                break;
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0x10000000000000L);
            }
        }
        return this.jjMoveNfa_0(0, 10);
    }

    private int jjMoveStringLiteralDfa11_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 10);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 10);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0x10000000000000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0x10000000000000L);
            }
        }
        return this.jjMoveNfa_0(0, 11);
    }

    private int jjMoveStringLiteralDfa12_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 11);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 11);
        }
        switch (this.curChar) {
            case 'N': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0x10000000000000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0x10000000000000L);
            }
        }
        return this.jjMoveNfa_0(0, 12);
    }

    private int jjMoveStringLiteralDfa13_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 12);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 12);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa14_0(active0, 0x10000000000000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa14_0(active0, 0x10000000000000L);
            }
        }
        return this.jjMoveNfa_0(0, 13);
    }

    private int jjMoveStringLiteralDfa14_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 13);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 13);
        }
        switch (this.curChar) {
            case 'G': {
                return this.jjMoveStringLiteralDfa15_0(active0, 0x10000000000000L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa15_0(active0, 0x10000000000000L);
            }
        }
        return this.jjMoveNfa_0(0, 14);
    }

    private int jjMoveStringLiteralDfa15_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 14);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 14);
        }
        switch (this.curChar) {
            case 'E': {
                return this.jjMoveStringLiteralDfa16_0(active0, 0x10000000000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa16_0(active0, 0x10000000000000L);
            }
        }
        return this.jjMoveNfa_0(0, 15);
    }

    private int jjMoveStringLiteralDfa16_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjMoveNfa_0(0, 15);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(0, 15);
        }
        switch (this.curChar) {
            case 'R': {
                if ((active0 & 0x10000000000000L) == 0L) break;
                this.jjmatchedKind = 52;
                this.jjmatchedPos = 16;
                break;
            }
            case 'r': {
                if ((active0 & 0x10000000000000L) == 0L) break;
                this.jjmatchedKind = 52;
                this.jjmatchedPos = 16;
                break;
            }
        }
        return this.jjMoveNfa_0(0, 16);
    }

    private int jjMoveNfa_0(int startState, int curPos) {
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
        this.jjnewStateCnt = 25;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block47: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FFE00000000000L & l) != 0L) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAdd(7);
                            } else if (this.curChar == '\'') {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAdd(1);
                                break;
                            }
                            if (this.curChar != '-') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & l) == 0L) continue block47;
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 3: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 5: {
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 6: {
                            if (this.curChar != '\'' || kind <= 55) continue block47;
                            kind = 55;
                            break;
                        }
                        case 7: {
                            if ((0x3FFE00000000000L & l) == 0L) continue block47;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(7);
                            break;
                        }
                        case 9: {
                            if (this.curChar != ' ') break;
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                        case 10: {
                            if ((0xFFFFAC7EFFFFFFFFL & l) == 0L) continue block47;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(10, 11);
                            break;
                        }
                        case 11: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 12: {
                            if ((0xFFFFAC7EFFFFFFFFL & l) == 0L) continue block47;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(11, 12);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block48: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7FFFFFE97FFFFFEL & l) != 0L) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAdd(7);
                            }
                            if ((0x200000002L & l) != 0L) {
                                this.jjAddStates(3, 4);
                                break;
                            }
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 3: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 5: {
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 7: {
                            if ((0x7FFFFFE97FFFFFEL & l) == 0L) continue block48;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(7);
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 10: {
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(10, 11);
                            break;
                        }
                        case 12: {
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(11, 12);
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 14: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 15: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 16: {
                            if ((0x200000002L & l) == 0L) break;
                            this.jjAddStates(3, 4);
                            break;
                        }
                        case 17: {
                            if ((0x2000000020L & l) == 0L || kind <= 30) continue block48;
                            kind = 30;
                            break;
                        }
                        case 18: {
                            if ((0x8000000080L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 19: {
                            if ((0x200000002L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 20: {
                            if ((0x4000000040000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 21: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 22: {
                            if ((0x40000000400000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 23: {
                            if ((0x8000000080L & l) == 0L || kind <= 30) continue block48;
                            kind = 30;
                            break;
                        }
                        case 24: {
                            if ((0x40000000400000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
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
                block49: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: 
                        case 5: {
                            if (!EhcacheSearchParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 10: {
                            if (!EhcacheSearchParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block49;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(10, 11);
                            break;
                        }
                        case 12: {
                            if (!EhcacheSearchParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block49;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(11, 12);
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
            if (i == (startsAt = 25 - this.jjnewStateCnt)) break;
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

    public EhcacheSearchParserTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public EhcacheSearchParserTokenManager(SimpleCharStream stream, int lexState) {
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

    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 25;
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
        String im = jjstrLiteralImages[this.jjmatchedKind];
        String curTokenImage = im == null ? this.input_stream.GetImage() : im;
        int beginLine = this.input_stream.getBeginLine();
        int beginColumn = this.input_stream.getBeginColumn();
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    public Token getNextToken() {
        int curPos;
        block7: {
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

    private void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }

    private void jjAddStates(int start, int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
        } while (start++ != end);
    }

    private void jjCheckNAddTwoStates(int state1, int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }

    private void jjCheckNAddStates(int start, int end) {
        do {
            this.jjCheckNAdd(jjnextStates[start]);
        } while (start++ != end);
    }
}


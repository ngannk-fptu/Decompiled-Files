/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.json;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.tomcat.util.json.JSONParserConstants;
import org.apache.tomcat.util.json.JavaCharStream;
import org.apache.tomcat.util.json.Token;
import org.apache.tomcat.util.json.TokenMgrError;

public class JSONParserTokenManager
implements JSONParserConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, ",", "{", "}", ":", "[", "]", null, null, null, null, null, null, null, null, null, null, "''", "\"\"", null, null, null, null, null};
    static final int[] jjnextStates = new int[]{25, 26, 28, 34, 17, 20, 27, 35, 29, 25, 28, 29, 6, 7, 9, 11, 12, 14, 1, 2, 18, 19, 21, 23, 32, 33};
    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    public static final String[] lexStateNames = new String[]{"DEFAULT"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = new long[]{483364801L};
    static final long[] jjtoSkip = new long[]{62L};
    static final long[] jjtoSpecial = new long[]{0L};
    static final long[] jjtoMore = new long[]{0L};
    protected JavaCharStream input_stream;
    private final int[] jjrounds = new int[38];
    private final int[] jjstateSet = new int[76];
    private final StringBuilder jjimage;
    private StringBuilder image = this.jjimage = new StringBuilder();
    private int jjimageLen;
    private int lengthOfMatch;
    protected int curChar;

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0xE0000L) != 0L) {
                    this.jjmatchedKind = 28;
                    return 15;
                }
                if ((active0 & 0x400000L) != 0L) {
                    return 38;
                }
                if ((active0 & 0x800000L) != 0L) {
                    return 39;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0xE0000L) != 0L) {
                    this.jjmatchedKind = 28;
                    this.jjmatchedPos = 1;
                    return 15;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0xE0000L) != 0L) {
                    this.jjmatchedKind = 28;
                    this.jjmatchedPos = 2;
                    return 15;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0xA0000L) != 0L) {
                    return 15;
                }
                if ((active0 & 0x40000L) != 0L) {
                    this.jjmatchedKind = 28;
                    this.jjmatchedPos = 3;
                    return 15;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case 34: {
                return this.jjMoveStringLiteralDfa1_0(0x800000L);
            }
            case 39: {
                return this.jjMoveStringLiteralDfa1_0(0x400000L);
            }
            case 44: {
                return this.jjStopAtPos(0, 6);
            }
            case 58: {
                return this.jjStopAtPos(0, 9);
            }
            case 91: {
                return this.jjStopAtPos(0, 10);
            }
            case 93: {
                return this.jjStopAtPos(0, 11);
            }
            case 70: 
            case 102: {
                return this.jjMoveStringLiteralDfa1_0(262144L);
            }
            case 78: 
            case 110: {
                return this.jjMoveStringLiteralDfa1_0(524288L);
            }
            case 84: 
            case 116: {
                return this.jjMoveStringLiteralDfa1_0(131072L);
            }
            case 123: {
                return this.jjStopAtPos(0, 7);
            }
            case 125: {
                return this.jjStopAtPos(0, 8);
            }
        }
        return this.jjMoveNfa_0(0, 0);
    }

    private int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case 34: {
                if ((active0 & 0x800000L) == 0L) break;
                return this.jjStopAtPos(1, 23);
            }
            case 39: {
                if ((active0 & 0x400000L) == 0L) break;
                return this.jjStopAtPos(1, 22);
            }
            case 65: 
            case 97: {
                return this.jjMoveStringLiteralDfa2_0(active0, 262144L);
            }
            case 82: 
            case 114: {
                return this.jjMoveStringLiteralDfa2_0(active0, 131072L);
            }
            case 85: 
            case 117: {
                return this.jjMoveStringLiteralDfa2_0(active0, 524288L);
            }
        }
        return this.jjStartNfa_0(0, active0);
    }

    private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 76: 
            case 108: {
                return this.jjMoveStringLiteralDfa3_0(active0, 786432L);
            }
            case 85: 
            case 117: {
                return this.jjMoveStringLiteralDfa3_0(active0, 131072L);
            }
        }
        return this.jjStartNfa_0(1, active0);
    }

    private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 69: 
            case 101: {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 17, 15);
            }
            case 76: 
            case 108: {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 19, 15);
            }
            case 83: 
            case 115: {
                return this.jjMoveStringLiteralDfa4_0(active0, 262144L);
            }
        }
        return this.jjStartNfa_0(2, active0);
    }

    private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 69: 
            case 101: {
                if ((active0 & 0x40000L) == 0L) break;
                return this.jjStartNfaWithStates_0(4, 18, 15);
            }
        }
        return this.jjStartNfa_0(3, active0);
    }

    private int jjStartNfaWithStates_0(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_0(state, pos + 1);
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 38;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < 64) {
                long l = 1L << this.curChar;
                block62: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 28) {
                                    kind = 28;
                                }
                                this.jjCheckNAdd(15);
                            } else if ((0x3400L & l) != 0L) {
                                if (kind > 5) {
                                    kind = 5;
                                }
                            } else if ((0x100000200L & l) != 0L) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                            } else if (this.curChar == 45) {
                                this.jjCheckNAddStates(0, 3);
                            } else if (this.curChar == 47) {
                                this.jjAddStates(4, 5);
                            } else if (this.curChar == 34) {
                                this.jjCheckNAddTwoStates(11, 12);
                            } else if (this.curChar == 39) {
                                this.jjCheckNAddTwoStates(6, 7);
                            } else if (this.curChar == 35) {
                                this.jjCheckNAddTwoStates(1, 2);
                            }
                            if ((0x3FE000000000000L & l) != 0L) {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddStates(6, 8);
                                break;
                            }
                            if (this.curChar != 48) break;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddStates(9, 11);
                            break;
                        }
                        case 6: 
                        case 38: {
                            if ((0xFFFFFF7FFFFFC9FFL & l) == 0L) break;
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 11: 
                        case 39: {
                            if ((0xFFFFFFFBFFFFC9FFL & l) == 0L) break;
                            this.jjCheckNAddStates(15, 17);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFFFFFFCBFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if ((0x3400L & l) == 0L || kind <= 3) continue block62;
                            kind = 3;
                            break;
                        }
                        case 3: {
                            if ((0x100000200L & l) == 0L || kind <= 4) continue block62;
                            kind = 4;
                            break;
                        }
                        case 4: {
                            if ((0x3400L & l) == 0L || kind <= 5) continue block62;
                            kind = 5;
                            break;
                        }
                        case 5: {
                            if (this.curChar != 39) break;
                            this.jjCheckNAddTwoStates(6, 7);
                            break;
                        }
                        case 8: {
                            if ((0x808000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 9: {
                            if (this.curChar != 39 || kind <= 26) continue block62;
                            kind = 26;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 34) break;
                            this.jjCheckNAddTwoStates(11, 12);
                            break;
                        }
                        case 13: {
                            if ((0x800400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(15, 17);
                            break;
                        }
                        case 14: {
                            if (this.curChar != 34 || kind <= 27) continue block62;
                            kind = 27;
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) continue block62;
                            if (kind > 28) {
                                kind = 28;
                            }
                            this.jjCheckNAdd(15);
                            break;
                        }
                        case 16: {
                            if (this.curChar != 47) break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 17: {
                            if (this.curChar != 47) break;
                            this.jjCheckNAddTwoStates(18, 19);
                            break;
                        }
                        case 18: {
                            if ((0xFFFFFFFFFFFFCBFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(18, 19);
                            break;
                        }
                        case 19: {
                            if ((0x3400L & l) == 0L || kind <= 1) continue block62;
                            kind = 1;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 42) break;
                            this.jjCheckNAddTwoStates(21, 23);
                            break;
                        }
                        case 21: {
                            this.jjCheckNAddTwoStates(21, 23);
                            break;
                        }
                        case 22: {
                            if (this.curChar != 47 || kind <= 2) continue block62;
                            kind = 2;
                            break;
                        }
                        case 23: {
                            if (this.curChar != 42) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 24: {
                            if (this.curChar != 45) break;
                            this.jjCheckNAddStates(0, 3);
                            break;
                        }
                        case 25: {
                            if (this.curChar != 48) continue block62;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAdd(25);
                            break;
                        }
                        case 26: {
                            if ((0x3FE000000000000L & l) == 0L) continue block62;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAdd(27);
                            break;
                        }
                        case 27: {
                            if ((0x3FF000000000000L & l) == 0L) continue block62;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAdd(27);
                            break;
                        }
                        case 28: {
                            if (this.curChar != 48) break;
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 29: {
                            if (this.curChar != 46) break;
                            this.jjCheckNAdd(30);
                            break;
                        }
                        case 30: {
                            if ((0x3FF000000000000L & l) == 0L) continue block62;
                            if (kind > 16) {
                                kind = 16;
                            }
                            this.jjCheckNAddTwoStates(30, 31);
                            break;
                        }
                        case 32: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(33);
                            break;
                        }
                        case 33: {
                            if ((0x3FF000000000000L & l) == 0L) continue block62;
                            if (kind > 16) {
                                kind = 16;
                            }
                            this.jjCheckNAdd(33);
                            break;
                        }
                        case 34: {
                            if ((0x3FE000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(35, 29);
                            break;
                        }
                        case 35: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(35, 29);
                            break;
                        }
                        case 36: {
                            if (this.curChar != 48) continue block62;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddStates(9, 11);
                            break;
                        }
                        case 37: {
                            if ((0x3FE000000000000L & l) == 0L) continue block62;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 0x3F);
                block63: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 15: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L) continue block63;
                            if (kind > 28) {
                                kind = 28;
                            }
                            this.jjCheckNAdd(15);
                            break;
                        }
                        case 38: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddStates(12, 14);
                                break;
                            }
                            if (this.curChar != 92) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 39: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddStates(15, 17);
                                break;
                            }
                            if (this.curChar != 92) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 1: {
                            this.jjAddStates(18, 19);
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 7: {
                            if (this.curChar != 92) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 8: {
                            if ((0x14404410144044L & l) == 0L) break;
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 11: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(15, 17);
                            break;
                        }
                        case 12: {
                            if (this.curChar != 92) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 13: {
                            if ((0x14404410144044L & l) == 0L) break;
                            this.jjCheckNAddStates(15, 17);
                            break;
                        }
                        case 18: {
                            this.jjAddStates(20, 21);
                            break;
                        }
                        case 21: {
                            this.jjAddStates(22, 23);
                            break;
                        }
                        case 31: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(24, 25);
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
                block64: do {
                    switch (this.jjstateSet[--i]) {
                        case 6: 
                        case 38: {
                            if (!JSONParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block64;
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 11: 
                        case 39: {
                            if (!JSONParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block64;
                            this.jjCheckNAddStates(15, 17);
                            break;
                        }
                        case 1: {
                            if (!JSONParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block64;
                            this.jjAddStates(18, 19);
                            break;
                        }
                        case 18: {
                            if (!JSONParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block64;
                            this.jjAddStates(20, 21);
                            break;
                        }
                        case 21: {
                            if (!JSONParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block64;
                            this.jjAddStates(22, 23);
                            break;
                        }
                        default: {
                            if (i1 != 0 && l1 != 0L && i2 != 0 && l2 != 0L) continue block64;
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
            if (i == (startsAt = 38 - this.jjnewStateCnt)) {
                return curPos;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
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

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec2[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    public Token getNextToken() {
        int curPos;
        block7: {
            curPos = 0;
            do {
                try {
                    this.curChar = this.input_stream.BeginToken();
                }
                catch (Exception e) {
                    this.jjmatchedKind = 0;
                    this.jjmatchedPos = -1;
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
            if (this.curChar == 10 || this.curChar == 13) {
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

    void SkipLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            default: 
        }
    }

    void MoreLexicalActions() {
        this.lengthOfMatch = this.jjmatchedPos + 1;
        this.jjimageLen += this.lengthOfMatch;
        switch (this.jjmatchedKind) {
            default: 
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            default: 
        }
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

    public JSONParserTokenManager(JavaCharStream stream) {
        this.input_stream = stream;
    }

    public JSONParserTokenManager(JavaCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void ReInit(JavaCharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 38;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(JavaCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
}


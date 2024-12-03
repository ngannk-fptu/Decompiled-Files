/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.text.StrBuilder
 */
package org.apache.velocity.runtime.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.velocity.runtime.parser.CharStream;
import org.apache.velocity.runtime.parser.ParserConstants;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.TokenMgrError;

public class ParserTokenManager
implements ParserConstants {
    private int fileDepth = 0;
    private int lparen = 0;
    private int rparen = 0;
    List stateStack = new ArrayList(50);
    public boolean debugPrint = false;
    private boolean inReference;
    public boolean inDirective;
    private boolean inComment;
    public boolean inSet;
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{91, 93, 94, 95, 100, 101, 91, 94, 61, 100, 29, 31, 32, 35, 11, 13, 14, 15, 1, 2, 4, 11, 18, 13, 14, 15, 26, 27, 33, 34, 70, 71, 73, 74, 75, 76, 87, 89, 84, 85, 81, 82, 16, 17, 19, 21, 26, 27, 64, 65, 77, 78, 98, 99, 102, 103, 69, 71, 72, 73, 78, 79, 69, 72, 6, 78, 15, 16, 27, 28, 30, 38, 39, 41, 46, 28, 47, 62, 39, 63, 50, 53, 60, 67, 18, 19, 20, 21, 31, 36, 43, 9, 10, 22, 23, 76, 77, 80, 81, 5, 6, 7, 8, 6, 11, 39, 14, 15, 17, 18, 22, 24, 3, 4, 20, 21, 29, 30, 31, 32, 45, 47, 48, 49, 54, 55, 45, 48, 31, 54, 24, 26, 27, 30, 6, 8, 9, 10, 6, 13, 8, 9, 10, 21, 22, 28, 29, 37, 38, 39, 40, 11, 12, 14, 16, 21, 22, 34, 35, 41, 42, 52, 53, 56, 57, 8, 9, 10, 11, 12, 13, 6, 11, 33, 17, 18, 20, 21, 23, 24, 25, 26, 27, 28, 54, 56, 57, 58, 68, 69, 54, 57, 63, 68, 6, 11, 52, 32, 34, 35, 38, 14, 16, 17, 18, 14, 21, 16, 17, 18, 29, 30, 36, 37, 42, 43, 44, 45, 19, 20, 22, 24, 29, 30, 46, 47, 61, 62, 66, 67, 6, 11, 27, 17, 18, 19, 20};
    public static final String[] jjstrLiteralImages = new String[]{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"REFERENCE", "REFMODIFIER", "REFINDEX", "DIRECTIVE", "REFMOD2", "DEFAULT", "REFMOD", "IN_TEXTBLOCK", "IN_MULTI_LINE_COMMENT", "IN_FORMAL_COMMENT", "IN_SINGLE_LINE_COMMENT", "PRE_DIRECTIVE"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = new long[]{7169730605161152511L, 316L};
    static final long[] jjtoSkip = new long[]{0x20000000L, 192L};
    static final long[] jjtoSpecial = new long[]{0L, 192L};
    static final long[] jjtoMore = new long[]{1075806208L, 0L};
    protected CharStream input_stream;
    private final int[] jjrounds = new int[105];
    private final int[] jjstateSet = new int[210];
    private final StrBuilder jjimage;
    private StrBuilder image = this.jjimage = new StrBuilder();
    private int jjimageLen;
    private int lengthOfMatch;
    protected char curChar;
    int curLexState = 5;
    int defaultLexState = 5;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public boolean stateStackPop() {
        ParserState s;
        try {
            s = (ParserState)this.stateStack.remove(this.stateStack.size() - 1);
        }
        catch (IndexOutOfBoundsException e) {
            this.lparen = 0;
            this.SwitchTo(5);
            return false;
        }
        if (this.debugPrint) {
            System.out.println(" stack pop (" + this.stateStack.size() + ") : lparen=" + s.lparen + " newstate=" + s.lexstate);
        }
        this.lparen = s.lparen;
        this.rparen = s.rparen;
        this.SwitchTo(s.lexstate);
        return true;
    }

    public boolean stateStackPush() {
        if (this.debugPrint) {
            System.out.println(" (" + this.stateStack.size() + ") pushing cur state : " + this.curLexState);
        }
        ParserState s = new ParserState();
        s.lparen = this.lparen;
        s.rparen = this.rparen;
        s.lexstate = this.curLexState;
        this.lparen = 0;
        this.stateStack.add(s);
        return true;
    }

    public void clearStateVars() {
        this.stateStack.clear();
        this.lparen = 0;
        this.rparen = 0;
        this.inReference = false;
        this.inDirective = false;
        this.inComment = false;
        this.inSet = false;
    }

    private void RPARENHandler() {
        boolean closed = false;
        if (this.inComment) {
            closed = true;
        }
        while (!closed) {
            if (this.lparen > 0) {
                if (this.lparen == this.rparen + 1) {
                    this.stateStackPop();
                } else {
                    ++this.rparen;
                }
                closed = true;
                continue;
            }
            if (this.stateStackPop()) continue;
            break;
        }
    }

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_3(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1000000000L) != 0L) {
                    return 105;
                }
                if ((active0 & 0x100L) != 0L) {
                    return 69;
                }
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 61;
                    return 67;
                }
                if ((active0 & 0x40L) != 0L) {
                    return 62;
                }
                if ((active0 & 0x4000000000000L) != 0L) {
                    return 54;
                }
                if ((active0 & 0x3A0000L) != 0L) {
                    return 7;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 61;
                    this.jjmatchedPos = 1;
                    return 67;
                }
                if ((active0 & 0x80000L) != 0L) {
                    return 5;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 61;
                    this.jjmatchedPos = 2;
                    return 67;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x200000000L) != 0L) {
                    return 67;
                }
                if ((active0 & 0x400000000L) != 0L) {
                    this.jjmatchedKind = 61;
                    this.jjmatchedPos = 3;
                    return 67;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_3(int pos, long active0) {
        return this.jjMoveNfa_3(this.jjStopStringLiteralDfa_3(pos, active0), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_3() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_3(0x2A0000L);
            }
            case '%': {
                return this.jjStopAtPos(0, 40);
            }
            case '(': {
                return this.jjStopAtPos(0, 10);
            }
            case '*': {
                return this.jjStopAtPos(0, 38);
            }
            case '+': {
                return this.jjStopAtPos(0, 37);
            }
            case ',': {
                return this.jjStopAtPos(0, 5);
            }
            case '-': {
                return this.jjStartNfaWithStates_3(0, 36, 105);
            }
            case '.': {
                return this.jjMoveStringLiteralDfa1_3(64L);
            }
            case '/': {
                return this.jjStopAtPos(0, 39);
            }
            case ':': {
                return this.jjStopAtPos(0, 7);
            }
            case '=': {
                return this.jjStartNfaWithStates_3(0, 50, 54);
            }
            case '[': {
                return this.jjStopAtPos(0, 3);
            }
            case ']': {
                return this.jjStopAtPos(0, 4);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_3(0x400000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_3(0x200000000L);
            }
            case '{': {
                return this.jjStartNfaWithStates_3(0, 8, 69);
            }
            case '}': {
                return this.jjStopAtPos(0, 9);
            }
        }
        return this.jjMoveNfa_3(0, 0);
    }

    private int jjMoveStringLiteralDfa1_3(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x200000L) == 0L) break;
                return this.jjStopAtPos(1, 21);
            }
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_3(1, 19, 5);
            }
            case '.': {
                if ((active0 & 0x40L) == 0L) break;
                return this.jjStopAtPos(1, 6);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_3(active0, 131072L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_3(active0, 0x400000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_3(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_3(0, active0);
    }

    private int jjMoveStringLiteralDfa2_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_3(active0, 0x400000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_3(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_3(1, active0);
    }

    private int jjMoveStringLiteralDfa3_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x200000000L) == 0L) break;
                return this.jjStartNfaWithStates_3(3, 33, 67);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_3(active0, 0x400000000L);
            }
        }
        return this.jjStartNfa_3(2, active0);
    }

    private int jjMoveStringLiteralDfa4_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x400000000L) == 0L) break;
                return this.jjStartNfaWithStates_3(4, 34, 67);
            }
        }
        return this.jjStartNfa_3(3, active0);
    }

    private int jjStartNfaWithStates_3(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_3(state, pos + 1);
    }

    private int jjMoveNfa_3(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 105;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block125: do {
                    switch (this.jjstateSet[--i]) {
                        case 105: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(100, 101);
                            } else if (this.curChar == '.') {
                                this.jjCheckNAdd(62);
                            }
                            if ((0x3FF000000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(94, 95);
                            }
                            if ((0x3FF000000000000L & l) == 0L) break;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(91, 93);
                            break;
                        }
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAddStates(0, 5);
                            } else if ((0x100002600L & l) != 0L) {
                                if (kind > 31) {
                                    kind = 31;
                                }
                                this.jjCheckNAdd(9);
                            } else if (this.curChar == '-') {
                                this.jjCheckNAddStates(6, 9);
                            } else if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(77, 78);
                            } else if (this.curChar == '.') {
                                this.jjCheckNAdd(62);
                            } else if (this.curChar == '!') {
                                if (kind > 49) {
                                    kind = 49;
                                }
                            } else if (this.curChar == '=') {
                                this.jjstateSet[this.jjnewStateCnt++] = 54;
                            } else if (this.curChar == '>') {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
                            } else if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 49;
                            } else if (this.curChar == '&') {
                                this.jjstateSet[this.jjnewStateCnt++] = 39;
                            } else if (this.curChar == '\'') {
                                this.jjCheckNAddStates(10, 13);
                            } else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(14, 17);
                            } else if (this.curChar == '#') {
                                this.jjstateSet[this.jjnewStateCnt++] = 7;
                            } else if (this.curChar == ')') {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                this.jjCheckNAddStates(18, 20);
                            }
                            if ((0x2400L & l) != 0L) {
                                if (kind > 35) {
                                    kind = 35;
                                }
                            } else if (this.curChar == '!') {
                                this.jjstateSet[this.jjnewStateCnt++] = 58;
                            } else if (this.curChar == '>') {
                                if (kind > 45) {
                                    kind = 45;
                                }
                            } else if (this.curChar == '<' && kind > 43) {
                                kind = 43;
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 1: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(18, 20);
                            break;
                        }
                        case 2: {
                            if ((0x2400L & l) == 0L || kind <= 11) continue block125;
                            kind = 11;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\n' || kind <= 11) continue block125;
                            kind = 11;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block125;
                            kind = 18;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '#') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if ((0x100002600L & l) == 0L) continue block125;
                            if (kind > 31) {
                                kind = 31;
                            }
                            this.jjCheckNAdd(9);
                            break;
                        }
                        case 10: 
                        case 12: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 11: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '\"') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '\"' || kind <= 32) continue block125;
                            kind = 32;
                            break;
                        }
                        case 17: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(21, 25);
                            break;
                        }
                        case 18: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 19: {
                            if ((0xF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 20: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAdd(18);
                            break;
                        }
                        case 22: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
                            break;
                        }
                        case 24: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 25: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 26: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(26, 27);
                            break;
                        }
                        case 27: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 28: 
                        case 30: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddStates(10, 13);
                            break;
                        }
                        case 29: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(10, 13);
                            break;
                        }
                        case 31: {
                            if (this.curChar != '\'') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                        case 33: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(28, 29);
                            break;
                        }
                        case 34: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(10, 13);
                            break;
                        }
                        case 35: {
                            if (this.curChar != '\'' || kind <= 32) continue block125;
                            kind = 32;
                            break;
                        }
                        case 36: {
                            if ((0x2400L & l) == 0L || kind <= 35) continue block125;
                            kind = 35;
                            break;
                        }
                        case 37: {
                            if (this.curChar != '\n' || kind <= 35) continue block125;
                            kind = 35;
                            break;
                        }
                        case 38: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 39: {
                            if (this.curChar != '&' || kind <= 41) continue block125;
                            kind = 41;
                            break;
                        }
                        case 40: {
                            if (this.curChar != '&') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 39;
                            break;
                        }
                        case 48: {
                            if (this.curChar != '<' || kind <= 43) continue block125;
                            kind = 43;
                            break;
                        }
                        case 49: {
                            if (this.curChar != '=' || kind <= 44) continue block125;
                            kind = 44;
                            break;
                        }
                        case 50: {
                            if (this.curChar != '<') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 49;
                            break;
                        }
                        case 51: {
                            if (this.curChar != '>' || kind <= 45) continue block125;
                            kind = 45;
                            break;
                        }
                        case 52: {
                            if (this.curChar != '=' || kind <= 46) continue block125;
                            kind = 46;
                            break;
                        }
                        case 53: {
                            if (this.curChar != '>') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 52;
                            break;
                        }
                        case 54: {
                            if (this.curChar != '=' || kind <= 47) continue block125;
                            kind = 47;
                            break;
                        }
                        case 55: {
                            if (this.curChar != '=') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 54;
                            break;
                        }
                        case 58: {
                            if (this.curChar != '=' || kind <= 48) continue block125;
                            kind = 48;
                            break;
                        }
                        case 59: {
                            if (this.curChar != '!') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 58;
                            break;
                        }
                        case 60: {
                            if (this.curChar != '!' || kind <= 49) continue block125;
                            kind = 49;
                            break;
                        }
                        case 61: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(62);
                            break;
                        }
                        case 62: {
                            if ((0x3FF000000000000L & l) == 0L) continue block125;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(62, 63);
                            break;
                        }
                        case 64: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(65);
                            break;
                        }
                        case 65: {
                            if ((0x3FF000000000000L & l) == 0L) continue block125;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(65);
                            break;
                        }
                        case 67: {
                            if ((0x3FF000000000000L & l) == 0L) continue block125;
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 67;
                            break;
                        }
                        case 70: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjAddStates(30, 31);
                            break;
                        }
                        case 74: {
                            if (this.curChar != '$' || kind <= 15) continue block125;
                            kind = 15;
                            break;
                        }
                        case 76: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(77, 78);
                            break;
                        }
                        case 78: {
                            if (this.curChar != '!' || kind <= 16) continue block125;
                            kind = 16;
                            break;
                        }
                        case 79: {
                            if (this.curChar != '$') continue block125;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(77, 78);
                            break;
                        }
                        case 90: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAddStates(6, 9);
                            break;
                        }
                        case 91: {
                            if ((0x3FF000000000000L & l) == 0L) continue block125;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(91, 93);
                            break;
                        }
                        case 92: {
                            if (this.curChar != '.' || kind <= 56) continue block125;
                            kind = 56;
                            break;
                        }
                        case 93: {
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 92;
                            break;
                        }
                        case 94: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(94, 95);
                            break;
                        }
                        case 95: {
                            if (this.curChar != '.') continue block125;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(96, 97);
                            break;
                        }
                        case 96: {
                            if ((0x3FF000000000000L & l) == 0L) continue block125;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(96, 97);
                            break;
                        }
                        case 98: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(99);
                            break;
                        }
                        case 99: {
                            if ((0x3FF000000000000L & l) == 0L) continue block125;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(99);
                            break;
                        }
                        case 100: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(100, 101);
                            break;
                        }
                        case 102: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(103);
                            break;
                        }
                        case 103: {
                            if ((0x3FF000000000000L & l) == 0L) continue block125;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(103);
                            break;
                        }
                        case 104: {
                            if ((0x3FF000000000000L & l) == 0L) continue block125;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddStates(0, 5);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block126: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
                                if (kind > 61) {
                                    kind = 61;
                                }
                                this.jjCheckNAdd(67);
                            } else if (this.curChar == '\\') {
                                this.jjCheckNAddStates(32, 35);
                            } else if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 69;
                            } else if (this.curChar == '|') {
                                this.jjstateSet[this.jjnewStateCnt++] = 44;
                            }
                            if (this.curChar == 'n') {
                                this.jjAddStates(36, 37);
                                break;
                            }
                            if (this.curChar == 'g') {
                                this.jjAddStates(38, 39);
                                break;
                            }
                            if (this.curChar == 'l') {
                                this.jjAddStates(40, 41);
                                break;
                            }
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 56;
                                break;
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 46;
                                break;
                            }
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 6: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 11: {
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 15: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(42, 47);
                            break;
                        }
                        case 16: {
                            if ((0x14404400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 21: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 22: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 23: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
                            break;
                        }
                        case 24: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 25: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 29: {
                            this.jjAddStates(10, 13);
                            break;
                        }
                        case 32: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(28, 29);
                            break;
                        }
                        case 41: {
                            if (this.curChar != 'd' || kind <= 41) continue block126;
                            kind = 41;
                            break;
                        }
                        case 42: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 43: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 44: {
                            if (this.curChar != '|' || kind <= 42) continue block126;
                            kind = 42;
                            break;
                        }
                        case 45: {
                            if (this.curChar != '|') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 44;
                            break;
                        }
                        case 46: {
                            if (this.curChar != 'r' || kind <= 42) continue block126;
                            kind = 42;
                            break;
                        }
                        case 47: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 46;
                            break;
                        }
                        case 56: {
                            if (this.curChar != 'q' || kind <= 47) continue block126;
                            kind = 47;
                            break;
                        }
                        case 57: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 56;
                            break;
                        }
                        case 63: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(48, 49);
                            break;
                        }
                        case 66: 
                        case 67: {
                            if ((0x7FFFFFE87FFFFFFL & l) == 0L) continue block126;
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAdd(67);
                            break;
                        }
                        case 68: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 69;
                            break;
                        }
                        case 69: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(70, 71);
                            break;
                        }
                        case 70: {
                            if ((0x7FFFFFE87FFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(70, 71);
                            break;
                        }
                        case 71: {
                            if (this.curChar != '}' || kind <= 62) continue block126;
                            kind = 62;
                            break;
                        }
                        case 72: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(32, 35);
                            break;
                        }
                        case 73: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(73, 74);
                            break;
                        }
                        case 75: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(75, 76);
                            break;
                        }
                        case 77: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(50, 51);
                            break;
                        }
                        case 80: {
                            if (this.curChar != 'l') break;
                            this.jjAddStates(40, 41);
                            break;
                        }
                        case 81: {
                            if (this.curChar != 't' || kind <= 43) continue block126;
                            kind = 43;
                            break;
                        }
                        case 82: {
                            if (this.curChar != 'e' || kind <= 44) continue block126;
                            kind = 44;
                            break;
                        }
                        case 83: {
                            if (this.curChar != 'g') break;
                            this.jjAddStates(38, 39);
                            break;
                        }
                        case 84: {
                            if (this.curChar != 't' || kind <= 45) continue block126;
                            kind = 45;
                            break;
                        }
                        case 85: {
                            if (this.curChar != 'e' || kind <= 46) continue block126;
                            kind = 46;
                            break;
                        }
                        case 86: {
                            if (this.curChar != 'n') break;
                            this.jjAddStates(36, 37);
                            break;
                        }
                        case 87: {
                            if (this.curChar != 'e' || kind <= 48) continue block126;
                            kind = 48;
                            break;
                        }
                        case 88: {
                            if (this.curChar != 't' || kind <= 49) continue block126;
                            kind = 49;
                            break;
                        }
                        case 89: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 88;
                            break;
                        }
                        case 97: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(52, 53);
                            break;
                        }
                        case 101: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(54, 55);
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
                block127: do {
                    switch (this.jjstateSet[--i]) {
                        case 6: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block127;
                            kind = 18;
                            break;
                        }
                        case 11: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(14, 17);
                            break;
                        }
                        case 29: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(10, 13);
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
            if (i == (startsAt = 105 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_11(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x3A0000L) != 0L) {
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x80000L) != 0L) {
                    return 0;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_11(int pos, long active0) {
        return this.jjMoveNfa_11(this.jjStopStringLiteralDfa_11(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_11() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_11(0x2A0000L);
            }
        }
        return this.jjMoveNfa_11(3, 0);
    }

    private int jjMoveStringLiteralDfa1_11(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_11(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x200000L) == 0L) break;
                return this.jjStopAtPos(1, 21);
            }
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_11(1, 19, 0);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_11(active0, 131072L);
            }
        }
        return this.jjStartNfa_11(0, active0);
    }

    private int jjMoveStringLiteralDfa2_11(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_11(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_11(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
        }
        return this.jjStartNfa_11(1, active0);
    }

    private int jjStartNfaWithStates_11(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_11(state, pos + 1);
    }

    private int jjMoveNfa_11(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 83;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block94: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAddStates(56, 61);
                                break;
                            }
                            if (this.curChar == '-') {
                                this.jjCheckNAddStates(62, 65);
                                break;
                            }
                            if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(22, 23);
                                break;
                            }
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(7);
                                break;
                            }
                            if (this.curChar != '#') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 0: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block94;
                            kind = 18;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(7);
                            break;
                        }
                        case 7: {
                            if ((0x3FF000000000000L & l) == 0L) continue block94;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(7, 8);
                            break;
                        }
                        case 9: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(10);
                            break;
                        }
                        case 10: {
                            if ((0x3FF000000000000L & l) == 0L) continue block94;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(10);
                            break;
                        }
                        case 12: {
                            if ((0x3FF000000000000L & l) == 0L) continue block94;
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjAddStates(66, 67);
                            break;
                        }
                        case 19: {
                            if (this.curChar != '$' || kind <= 15) continue block94;
                            kind = 15;
                            break;
                        }
                        case 21: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(22, 23);
                            break;
                        }
                        case 23: {
                            if (this.curChar != '!' || kind <= 16) continue block94;
                            kind = 16;
                            break;
                        }
                        case 24: {
                            if (this.curChar != '$') continue block94;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(22, 23);
                            break;
                        }
                        case 27: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(68, 70);
                            break;
                        }
                        case 28: {
                            if ((0x2400L & l) == 0L || kind <= 51) continue block94;
                            kind = 51;
                            break;
                        }
                        case 29: {
                            if (this.curChar != '\n' || kind <= 51) continue block94;
                            kind = 51;
                            break;
                        }
                        case 30: 
                        case 47: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAdd(29);
                            break;
                        }
                        case 38: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(71, 73);
                            break;
                        }
                        case 39: {
                            if ((0x2400L & l) == 0L || kind <= 54) continue block94;
                            kind = 54;
                            break;
                        }
                        case 40: {
                            if (this.curChar != '\n' || kind <= 54) continue block94;
                            kind = 54;
                            break;
                        }
                        case 41: 
                        case 63: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAdd(40);
                            break;
                        }
                        case 46: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(74, 76);
                            break;
                        }
                        case 62: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(77, 79);
                            break;
                        }
                        case 68: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAddStates(62, 65);
                            break;
                        }
                        case 69: {
                            if ((0x3FF000000000000L & l) == 0L) continue block94;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(69, 71);
                            break;
                        }
                        case 70: {
                            if (this.curChar != '.' || kind <= 56) continue block94;
                            kind = 56;
                            break;
                        }
                        case 71: {
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 70;
                            break;
                        }
                        case 72: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(72, 73);
                            break;
                        }
                        case 73: {
                            if (this.curChar != '.') continue block94;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(74, 75);
                            break;
                        }
                        case 74: {
                            if ((0x3FF000000000000L & l) == 0L) continue block94;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(74, 75);
                            break;
                        }
                        case 76: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(77);
                            break;
                        }
                        case 77: {
                            if ((0x3FF000000000000L & l) == 0L) continue block94;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(77);
                            break;
                        }
                        case 78: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(78, 79);
                            break;
                        }
                        case 80: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(81);
                            break;
                        }
                        case 81: {
                            if ((0x3FF000000000000L & l) == 0L) continue block94;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(81);
                            break;
                        }
                        case 82: {
                            if ((0x3FF000000000000L & l) == 0L) continue block94;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddStates(56, 61);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block95: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
                                if (kind > 61) {
                                    kind = 61;
                                }
                                this.jjCheckNAdd(12);
                            } else if (this.curChar == '{') {
                                this.jjAddStates(80, 83);
                            } else if (this.curChar == '\\') {
                                this.jjCheckNAddStates(84, 87);
                            }
                            if (this.curChar == 'e') {
                                this.jjAddStates(88, 90);
                                break;
                            }
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                                break;
                            }
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 1: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 4: {
                            if (this.curChar != 'f' || kind <= 52) continue block95;
                            kind = 52;
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 8: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(91, 92);
                            break;
                        }
                        case 11: 
                        case 12: {
                            if ((0x7FFFFFE87FFFFFFL & l) == 0L) continue block95;
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 14: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(15, 16);
                            break;
                        }
                        case 15: {
                            if ((0x7FFFFFE87FFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(15, 16);
                            break;
                        }
                        case 16: {
                            if (this.curChar != '}' || kind <= 62) continue block95;
                            kind = 62;
                            break;
                        }
                        case 17: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(84, 87);
                            break;
                        }
                        case 18: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(18, 19);
                            break;
                        }
                        case 20: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(20, 21);
                            break;
                        }
                        case 22: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(93, 94);
                            break;
                        }
                        case 25: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(88, 90);
                            break;
                        }
                        case 26: {
                            if (this.curChar != 'd') continue block95;
                            if (kind > 51) {
                                kind = 51;
                            }
                            this.jjCheckNAddStates(68, 70);
                            break;
                        }
                        case 31: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 32: {
                            if (this.curChar != 'f' || kind <= 53) continue block95;
                            kind = 53;
                            break;
                        }
                        case 33: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 32;
                            break;
                        }
                        case 34: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 33;
                            break;
                        }
                        case 35: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 34;
                            break;
                        }
                        case 36: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 35;
                            break;
                        }
                        case 37: {
                            if (this.curChar != 'e') continue block95;
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAddStates(71, 73);
                            break;
                        }
                        case 42: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 43: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 44: {
                            if (this.curChar != '{') break;
                            this.jjAddStates(80, 83);
                            break;
                        }
                        case 45: {
                            if (this.curChar != '}') continue block95;
                            if (kind > 51) {
                                kind = 51;
                            }
                            this.jjCheckNAddStates(74, 76);
                            break;
                        }
                        case 48: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 45;
                            break;
                        }
                        case 49: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 48;
                            break;
                        }
                        case 50: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 49;
                            break;
                        }
                        case 51: {
                            if (this.curChar != '}' || kind <= 52) continue block95;
                            kind = 52;
                            break;
                        }
                        case 52: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            break;
                        }
                        case 53: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 52;
                            break;
                        }
                        case 54: {
                            if (this.curChar != '}' || kind <= 53) continue block95;
                            kind = 53;
                            break;
                        }
                        case 55: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 54;
                            break;
                        }
                        case 56: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 55;
                            break;
                        }
                        case 57: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 56;
                            break;
                        }
                        case 58: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 57;
                            break;
                        }
                        case 59: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 58;
                            break;
                        }
                        case 60: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 59;
                            break;
                        }
                        case 61: {
                            if (this.curChar != '}') continue block95;
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAddStates(77, 79);
                            break;
                        }
                        case 64: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 61;
                            break;
                        }
                        case 65: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 64;
                            break;
                        }
                        case 66: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 65;
                            break;
                        }
                        case 67: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 66;
                            break;
                        }
                        case 75: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(95, 96);
                            break;
                        }
                        case 79: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(97, 98);
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
                block96: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block96;
                            kind = 18;
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
            if (i == (startsAt = 83 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_8(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1A0000L) != 0L) {
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x80000L) != 0L) {
                    return 0;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_8(int pos, long active0) {
        return this.jjMoveNfa_8(this.jjStopStringLiteralDfa_8(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_8() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_8(655360L);
            }
            case '*': {
                return this.jjMoveStringLiteralDfa1_8(0x8000000L);
            }
        }
        return this.jjMoveNfa_8(3, 0);
    }

    private int jjMoveStringLiteralDfa1_8(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x8000000L) == 0L) break;
                return this.jjStopAtPos(1, 27);
            }
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_8(1, 19, 0);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_8(active0, 131072L);
            }
        }
        return this.jjStartNfa_8(0, active0);
    }

    private int jjMoveStringLiteralDfa2_8(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_8(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
        }
        return this.jjStartNfa_8(1, active0);
    }

    private int jjStartNfaWithStates_8(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_8(state, pos + 1);
    }

    private int jjMoveNfa_8(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 12;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block23: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(9, 10);
                                break;
                            }
                            if (this.curChar != '#') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 0: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block23;
                            kind = 18;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '$' || kind <= 15) continue block23;
                            kind = 15;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '!' || kind <= 16) continue block23;
                            kind = 16;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '$') continue block23;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(99, 102);
                            break;
                        }
                        case 1: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(5, 6);
                            break;
                        }
                        case 7: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(7, 8);
                            break;
                        }
                        case 9: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(91, 92);
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
                block25: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block25;
                            kind = 18;
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
            if (i == (startsAt = 12 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_6(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1A0000L) != 0L) {
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x80000L) != 0L) {
                    return 0;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_6(int pos, long active0) {
        return this.jjMoveNfa_6(this.jjStopStringLiteralDfa_6(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_6() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_6(655360L);
            }
        }
        return this.jjMoveNfa_6(3, 0);
    }

    private int jjMoveStringLiteralDfa1_6(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_6(1, 19, 0);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_6(active0, 131072L);
            }
        }
        return this.jjStartNfa_6(0, active0);
    }

    private int jjMoveStringLiteralDfa2_6(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_6(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
        }
        return this.jjStartNfa_6(1, active0);
    }

    private int jjStartNfaWithStates_6(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_6(state, pos + 1);
    }

    private int jjMoveNfa_6(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 12;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block23: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(9, 10);
                                break;
                            }
                            if (this.curChar != '#') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 0: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block23;
                            kind = 18;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '$' || kind <= 15) continue block23;
                            kind = 15;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '!' || kind <= 16) continue block23;
                            kind = 16;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '$') continue block23;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(99, 102);
                            break;
                        }
                        case 1: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(5, 6);
                            break;
                        }
                        case 7: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(7, 8);
                            break;
                        }
                        case 9: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(91, 92);
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
                block25: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block25;
                            kind = 18;
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
            if (i == (startsAt = 12 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_5(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0xC00000L) != 0L) {
                    return 20;
                }
                if ((active0 & 0x3A0000L) != 0L) {
                    return 39;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x400000L) != 0L) {
                    return 40;
                }
                if ((active0 & 0x80000L) != 0L) {
                    return 37;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_5(int pos, long active0) {
        return this.jjMoveNfa_5(this.jjStopStringLiteralDfa_5(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_5() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_5(0x2A0000L);
            }
            case '\\': {
                this.jjmatchedKind = 23;
                return this.jjMoveStringLiteralDfa1_5(0x400000L);
            }
        }
        return this.jjMoveNfa_5(13, 0);
    }

    private int jjMoveStringLiteralDfa1_5(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_5(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x200000L) == 0L) break;
                return this.jjStopAtPos(1, 21);
            }
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_5(1, 19, 37);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_5(active0, 131072L);
            }
            case '\\': {
                if ((active0 & 0x400000L) == 0L) break;
                return this.jjStartNfaWithStates_5(1, 22, 40);
            }
        }
        return this.jjStartNfa_5(0, active0);
    }

    private int jjMoveStringLiteralDfa2_5(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_5(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_5(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
        }
        return this.jjStartNfa_5(1, active0);
    }

    private int jjStartNfaWithStates_5(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_5(state, pos + 1);
    }

    private int jjMoveNfa_5(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 40;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block58: do {
                    switch (this.jjstateSet[--i]) {
                        case 39: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 40: {
                            if (this.curChar == '$') {
                                this.jjCheckNAddTwoStates(33, 34);
                            }
                            if (this.curChar != '$' || kind <= 15) continue block58;
                            kind = 15;
                            break;
                        }
                        case 13: {
                            if ((0xFFFFFFE7FFFFFFFFL & l) != 0L) {
                                if (kind > 24) {
                                    kind = 24;
                                }
                                this.jjCheckNAdd(12);
                            } else if (this.curChar == '#') {
                                this.jjCheckNAddStates(103, 105);
                            } else if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(33, 34);
                            }
                            if ((0x100000200L & l) != 0L) {
                                this.jjCheckNAddTwoStates(0, 1);
                                break;
                            }
                            if (this.curChar != '$') break;
                            this.jjCheckNAddStates(106, 109);
                            break;
                        }
                        case 20: {
                            if (this.curChar == '$') {
                                this.jjCheckNAddTwoStates(33, 34);
                            } else if (this.curChar == '#') {
                                this.jjAddStates(110, 111);
                            }
                            if (this.curChar != '$' || kind <= 15) continue block58;
                            kind = 15;
                            break;
                        }
                        case 0: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 1: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddTwoStates(6, 11);
                            break;
                        }
                        case 3: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(112, 113);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '(' || kind <= 14) continue block58;
                            kind = 14;
                            break;
                        }
                        case 12: {
                            if ((0xFFFFFFE7FFFFFFFFL & l) == 0L) continue block58;
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 15: 
                        case 16: {
                            if (this.curChar != '!') break;
                            this.jjCheckNAdd(14);
                            break;
                        }
                        case 18: {
                            if (this.curChar != '.' || kind <= 72) continue block58;
                            kind = 72;
                            break;
                        }
                        case 21: {
                            if (this.curChar != '#') break;
                            this.jjAddStates(110, 111);
                            break;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) == 0L) continue block58;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjAddStates(26, 27);
                            break;
                        }
                        case 30: {
                            if (this.curChar != '$' || kind <= 15) continue block58;
                            kind = 15;
                            break;
                        }
                        case 32: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 34: {
                            if (this.curChar != '!' || kind <= 16) continue block58;
                            kind = 16;
                            break;
                        }
                        case 35: {
                            if (this.curChar != '$') continue block58;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 36: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddStates(103, 105);
                            break;
                        }
                        case 37: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 38: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block58;
                            kind = 18;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block59: do {
                    switch (this.jjstateSet[--i]) {
                        case 39: {
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
                                break;
                            }
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 40: {
                            if (this.curChar == '\\') {
                                this.jjAddStates(114, 115);
                            }
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(31, 32);
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(29, 30);
                            break;
                        }
                        case 13: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L) {
                                if (kind > 24) {
                                    kind = 24;
                                }
                                this.jjCheckNAdd(12);
                            } else if (this.curChar == '\\') {
                                this.jjCheckNAddStates(116, 119);
                            }
                            if (this.curChar != '\\') break;
                            this.jjAddStates(114, 115);
                            break;
                        }
                        case 20: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(31, 32);
                            }
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(29, 30);
                            }
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 2: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 8: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 12: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) continue block59;
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 14: {
                            if (this.curChar != '[' || kind <= 72) continue block59;
                            kind = 72;
                            break;
                        }
                        case 17: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 19: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(114, 115);
                            break;
                        }
                        case 22: 
                        case 23: {
                            if ((0x7FFFFFE87FFFFFFL & l) == 0L) continue block59;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAdd(23);
                            break;
                        }
                        case 24: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 25: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(26, 27);
                            break;
                        }
                        case 26: {
                            if ((0x7FFFFFE87FFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(26, 27);
                            break;
                        }
                        case 27: {
                            if (this.curChar != '}' || kind <= 13) continue block59;
                            kind = 13;
                            break;
                        }
                        case 28: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(116, 119);
                            break;
                        }
                        case 29: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(29, 30);
                            break;
                        }
                        case 31: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(31, 32);
                            break;
                        }
                        case 33: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(28, 29);
                            break;
                        }
                        case 38: {
                            if (kind <= 18) break;
                            kind = 18;
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
                block60: do {
                    switch (this.jjstateSet[--i]) {
                        case 12: 
                        case 13: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block60;
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 38: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block60;
                            kind = 18;
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
            if (i == (startsAt = 40 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_9(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1A0000L) != 0L) {
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x80000L) != 0L) {
                    return 0;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_9(int pos, long active0) {
        return this.jjMoveNfa_9(this.jjStopStringLiteralDfa_9(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_9() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_9(655360L);
            }
            case '*': {
                return this.jjMoveStringLiteralDfa1_9(0x4000000L);
            }
        }
        return this.jjMoveNfa_9(3, 0);
    }

    private int jjMoveStringLiteralDfa1_9(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_9(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x4000000L) == 0L) break;
                return this.jjStopAtPos(1, 26);
            }
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_9(1, 19, 0);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_9(active0, 131072L);
            }
        }
        return this.jjStartNfa_9(0, active0);
    }

    private int jjMoveStringLiteralDfa2_9(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_9(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_9(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
        }
        return this.jjStartNfa_9(1, active0);
    }

    private int jjStartNfaWithStates_9(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_9(state, pos + 1);
    }

    private int jjMoveNfa_9(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 12;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block23: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(9, 10);
                                break;
                            }
                            if (this.curChar != '#') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 0: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block23;
                            kind = 18;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '$' || kind <= 15) continue block23;
                            kind = 15;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '!' || kind <= 16) continue block23;
                            kind = 16;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '$') continue block23;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(99, 102);
                            break;
                        }
                        case 1: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(5, 6);
                            break;
                        }
                        case 7: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(7, 8);
                            break;
                        }
                        case 9: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(91, 92);
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
                block25: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block25;
                            kind = 18;
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
            if (i == (startsAt = 12 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_2(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1A0000L) != 0L) {
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x80000L) != 0L) {
                    return 0;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_2(int pos, long active0) {
        return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_2(655360L);
            }
            case ']': {
                return this.jjStopAtPos(0, 2);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_2(0x400000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_2(0x200000000L);
            }
        }
        return this.jjMoveNfa_2(3, 0);
    }

    private int jjMoveStringLiteralDfa1_2(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_2(1, 19, 0);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_2(active0, 131072L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_2(active0, 0x400000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_2(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_2(0, active0);
    }

    private int jjMoveStringLiteralDfa2_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_2(active0, 0x400000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_2(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_2(1, active0);
    }

    private int jjMoveStringLiteralDfa3_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x200000000L) == 0L) break;
                return this.jjStopAtPos(3, 33);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_2(active0, 0x400000000L);
            }
        }
        return this.jjStartNfa_2(2, active0);
    }

    private int jjMoveStringLiteralDfa4_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x400000000L) == 0L) break;
                return this.jjStopAtPos(4, 34);
            }
        }
        return this.jjStartNfa_2(3, active0);
    }

    private int jjStartNfaWithStates_2(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_2(state, pos + 1);
    }

    private int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 59;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block76: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAddStates(120, 125);
                                break;
                            }
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 31) {
                                    kind = 31;
                                }
                                this.jjCheckNAdd(4);
                                break;
                            }
                            if (this.curChar == '-') {
                                this.jjCheckNAddStates(126, 129);
                                break;
                            }
                            if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(41, 42);
                                break;
                            }
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(32);
                                break;
                            }
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(130, 133);
                                break;
                            }
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(134, 137);
                                break;
                            }
                            if (this.curChar != '#') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 0: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block76;
                            kind = 18;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 4: {
                            if ((0x100002600L & l) == 0L) continue block76;
                            if (kind > 31) {
                                kind = 31;
                            }
                            this.jjCheckNAdd(4);
                            break;
                        }
                        case 5: 
                        case 7: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddStates(134, 137);
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(134, 137);
                            break;
                        }
                        case 8: {
                            if (this.curChar != '\"') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != '\"' || kind <= 32) continue block76;
                            kind = 32;
                            break;
                        }
                        case 12: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(138, 142);
                            break;
                        }
                        case 13: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(134, 137);
                            break;
                        }
                        case 14: {
                            if ((0xF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 15: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAdd(13);
                            break;
                        }
                        case 17: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 18: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 19: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(134, 137);
                            break;
                        }
                        case 21: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(143, 144);
                            break;
                        }
                        case 22: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(134, 137);
                            break;
                        }
                        case 23: 
                        case 25: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddStates(130, 133);
                            break;
                        }
                        case 24: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(130, 133);
                            break;
                        }
                        case 26: {
                            if (this.curChar != '\'') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 28: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(145, 146);
                            break;
                        }
                        case 29: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(130, 133);
                            break;
                        }
                        case 30: {
                            if (this.curChar != '\'' || kind <= 32) continue block76;
                            kind = 32;
                            break;
                        }
                        case 31: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(32);
                            break;
                        }
                        case 32: {
                            if ((0x3FF000000000000L & l) == 0L) continue block76;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(32, 33);
                            break;
                        }
                        case 34: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(35);
                            break;
                        }
                        case 35: {
                            if ((0x3FF000000000000L & l) == 0L) continue block76;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(35);
                            break;
                        }
                        case 38: {
                            if (this.curChar != '$' || kind <= 15) continue block76;
                            kind = 15;
                            break;
                        }
                        case 40: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(41, 42);
                            break;
                        }
                        case 42: {
                            if (this.curChar != '!' || kind <= 16) continue block76;
                            kind = 16;
                            break;
                        }
                        case 43: {
                            if (this.curChar != '$') continue block76;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(41, 42);
                            break;
                        }
                        case 44: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAddStates(126, 129);
                            break;
                        }
                        case 45: {
                            if ((0x3FF000000000000L & l) == 0L) continue block76;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(45, 47);
                            break;
                        }
                        case 46: {
                            if (this.curChar != '.' || kind <= 56) continue block76;
                            kind = 56;
                            break;
                        }
                        case 47: {
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 46;
                            break;
                        }
                        case 48: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(48, 49);
                            break;
                        }
                        case 49: {
                            if (this.curChar != '.') continue block76;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(50, 51);
                            break;
                        }
                        case 50: {
                            if ((0x3FF000000000000L & l) == 0L) continue block76;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(50, 51);
                            break;
                        }
                        case 52: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(53);
                            break;
                        }
                        case 53: {
                            if ((0x3FF000000000000L & l) == 0L) continue block76;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(53);
                            break;
                        }
                        case 54: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(54, 55);
                            break;
                        }
                        case 56: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(57);
                            break;
                        }
                        case 57: {
                            if ((0x3FF000000000000L & l) == 0L) continue block76;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(57);
                            break;
                        }
                        case 58: {
                            if ((0x3FF000000000000L & l) == 0L) continue block76;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddStates(120, 125);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(147, 150);
                            break;
                        }
                        case 1: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 6: {
                            this.jjCheckNAddStates(134, 137);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(151, 156);
                            break;
                        }
                        case 11: {
                            if ((0x14404400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(134, 137);
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 17: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 18: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 19: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 20: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddStates(134, 137);
                            break;
                        }
                        case 24: {
                            this.jjAddStates(130, 133);
                            break;
                        }
                        case 27: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(145, 146);
                            break;
                        }
                        case 33: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(157, 158);
                            break;
                        }
                        case 37: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(37, 38);
                            break;
                        }
                        case 39: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(39, 40);
                            break;
                        }
                        case 41: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(159, 160);
                            break;
                        }
                        case 51: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(161, 162);
                            break;
                        }
                        case 55: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(163, 164);
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
                block78: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block78;
                            kind = 18;
                            break;
                        }
                        case 6: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(134, 137);
                            break;
                        }
                        case 24: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(130, 133);
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
            if (i == (startsAt = 59 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_10(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1A0000L) != 0L) {
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x80000L) != 0L) {
                    return 0;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_10(int pos, long active0) {
        return this.jjMoveNfa_10(this.jjStopStringLiteralDfa_10(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_10() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_10(655360L);
            }
        }
        return this.jjMoveNfa_10(3, 0);
    }

    private int jjMoveStringLiteralDfa1_10(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_10(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_10(1, 19, 0);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_10(active0, 131072L);
            }
        }
        return this.jjStartNfa_10(0, active0);
    }

    private int jjMoveStringLiteralDfa2_10(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_10(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_10(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
        }
        return this.jjStartNfa_10(1, active0);
    }

    private int jjStartNfaWithStates_10(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_10(state, pos + 1);
    }

    private int jjMoveNfa_10(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 15;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block26: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0x2400L & l) != 0L) {
                                if (kind > 25) {
                                    kind = 25;
                                }
                            } else if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(12, 13);
                            } else if (this.curChar == '#') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 0: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block26;
                            kind = 18;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 4: {
                            if ((0x2400L & l) == 0L || kind <= 25) continue block26;
                            kind = 25;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\n' || kind <= 25) continue block26;
                            kind = 25;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 9: {
                            if (this.curChar != '$' || kind <= 15) continue block26;
                            kind = 15;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '!' || kind <= 16) continue block26;
                            kind = 16;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '$') continue block26;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(165, 168);
                            break;
                        }
                        case 1: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(8, 9);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(10, 11);
                            break;
                        }
                        case 12: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(169, 170);
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
                block28: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block28;
                            kind = 18;
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
            if (i == (startsAt = 15 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x3A0000L) != 0L) {
                    return 33;
                }
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    return 13;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 1;
                    return 13;
                }
                if ((active0 & 0x80000L) != 0L) {
                    return 31;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 2;
                    return 13;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x200000000L) != 0L) {
                    return 13;
                }
                if ((active0 & 0x400000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 3;
                    return 13;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_0(int pos, long active0, long active1) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_0(0x2A0000L);
            }
            case '[': {
                return this.jjStopAtPos(0, 1);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(0x400000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_0(0x200000000L);
            }
            case '{': {
                return this.jjStopAtPos(0, 68);
            }
            case '}': {
                return this.jjStopAtPos(0, 69);
            }
        }
        return this.jjMoveNfa_0(12, 0);
    }

    private int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0, 0L);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x200000L) == 0L) break;
                return this.jjStopAtPos(1, 21);
            }
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_0(1, 19, 31);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_0(active0, 131072L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x400000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_0(0, active0, 0L);
    }

    private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(0, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0, 0L);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x400000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_0(1, active0, 0L);
    }

    private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(1, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x200000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 33, 13);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x400000000L);
            }
        }
        return this.jjStartNfa_0(2, active0, 0L);
    }

    private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(2, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x400000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(4, 34, 13);
            }
        }
        return this.jjStartNfa_0(3, active0, 0L);
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
        this.jjnewStateCnt = 34;
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
                        case 12: {
                            if ((0x100000200L & l) != 0L) {
                                this.jjCheckNAddTwoStates(0, 1);
                            } else if (this.curChar == '#') {
                                this.jjCheckNAddStates(171, 173);
                            } else if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(27, 28);
                            } else if (this.curChar == '.') {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                            }
                            if (this.curChar != '$') break;
                            this.jjCheckNAddStates(174, 177);
                            break;
                        }
                        case 33: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 31;
                            break;
                        }
                        case 0: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 1: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddTwoStates(6, 11);
                            break;
                        }
                        case 3: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(112, 113);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '(' || kind <= 14) continue block47;
                            kind = 14;
                            break;
                        }
                        case 13: {
                            if ((0x3FF200000000000L & l) == 0L) continue block47;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 16: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddStates(174, 177);
                            break;
                        }
                        case 18: 
                        case 19: {
                            if (this.curChar != '!') break;
                            this.jjCheckNAdd(17);
                            break;
                        }
                        case 21: {
                            if (this.curChar != '.' || kind <= 72) continue block47;
                            kind = 72;
                            break;
                        }
                        case 24: {
                            if (this.curChar != '$' || kind <= 15) continue block47;
                            kind = 15;
                            break;
                        }
                        case 26: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(27, 28);
                            break;
                        }
                        case 28: {
                            if (this.curChar != '!' || kind <= 16) continue block47;
                            kind = 16;
                            break;
                        }
                        case 29: {
                            if (this.curChar != '$') continue block47;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(27, 28);
                            break;
                        }
                        case 30: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddStates(171, 173);
                            break;
                        }
                        case 31: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 32;
                            break;
                        }
                        case 32: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block47;
                            kind = 18;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block48: do {
                    switch (this.jjstateSet[--i]) {
                        case 12: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 66) {
                                    kind = 66;
                                }
                                this.jjCheckNAdd(13);
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(178, 181);
                            break;
                        }
                        case 33: {
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
                                break;
                            }
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 2: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 8: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 13: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block48;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjCheckNAdd(13);
                            break;
                        }
                        case 15: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 67) continue block48;
                            kind = 67;
                            break;
                        }
                        case 17: {
                            if (this.curChar != '[' || kind <= 72) continue block48;
                            kind = 72;
                            break;
                        }
                        case 20: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 22: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(178, 181);
                            break;
                        }
                        case 23: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(23, 24);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(25, 26);
                            break;
                        }
                        case 27: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(182, 183);
                            break;
                        }
                        case 32: {
                            if (kind <= 18) break;
                            kind = 18;
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
                        case 32: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block49;
                            kind = 18;
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
            if (i == (startsAt = 34 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_4(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1A0000L) != 0L) {
                    return 52;
                }
                if ((active0 & 0x40L) != 0L) {
                    return 74;
                }
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    return 40;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 1;
                    return 40;
                }
                if ((active0 & 0x80000L) != 0L) {
                    return 50;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 2;
                    return 40;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x200000000L) != 0L) {
                    return 40;
                }
                if ((active0 & 0x400000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 3;
                    return 40;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_4(int pos, long active0) {
        return this.jjMoveNfa_4(this.jjStopStringLiteralDfa_4(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_4() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_4(655360L);
            }
            case ')': {
                return this.jjStopAtPos(0, 12);
            }
            case ',': {
                return this.jjStopAtPos(0, 5);
            }
            case '.': {
                return this.jjMoveStringLiteralDfa1_4(64L);
            }
            case ':': {
                return this.jjStopAtPos(0, 7);
            }
            case '[': {
                return this.jjStopAtPos(0, 3);
            }
            case ']': {
                return this.jjStopAtPos(0, 4);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_4(0x400000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_4(0x200000000L);
            }
            case '{': {
                return this.jjStopAtPos(0, 8);
            }
            case '}': {
                return this.jjStopAtPos(0, 9);
            }
        }
        return this.jjMoveNfa_4(13, 0);
    }

    private int jjMoveStringLiteralDfa1_4(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_4(1, 19, 50);
            }
            case '.': {
                if ((active0 & 0x40L) == 0L) break;
                return this.jjStopAtPos(1, 6);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_4(active0, 131072L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x400000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_4(0, active0);
    }

    private int jjMoveStringLiteralDfa2_4(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_4(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_4(active0, 0x400000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_4(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_4(1, active0);
    }

    private int jjMoveStringLiteralDfa3_4(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_4(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x200000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(3, 33, 40);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_4(active0, 0x400000000L);
            }
        }
        return this.jjStartNfa_4(2, active0);
    }

    private int jjMoveStringLiteralDfa4_4(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_4(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x400000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(4, 34, 40);
            }
        }
        return this.jjStartNfa_4(3, active0);
    }

    private int jjStartNfaWithStates_4(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_4(state, pos + 1);
    }

    private int jjMoveNfa_4(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 75;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block95: do {
                    switch (this.jjstateSet[--i]) {
                        case 52: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 50;
                            break;
                        }
                        case 64: 
                        case 74: {
                            if ((0x3FF000000000000L & l) == 0L) continue block95;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(64, 65);
                            break;
                        }
                        case 13: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAddStates(184, 189);
                            } else if ((0x100002600L & l) != 0L) {
                                if (kind > 31) {
                                    kind = 31;
                                }
                                this.jjCheckNAdd(12);
                            } else if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(64, 74);
                            } else if (this.curChar == '-') {
                                this.jjCheckNAddStates(190, 193);
                            } else if (this.curChar == '#') {
                                this.jjCheckNAddStates(194, 196);
                            } else if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(46, 47);
                            } else if (this.curChar == '\'') {
                                this.jjCheckNAddStates(197, 200);
                            } else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(201, 204);
                            }
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 0: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 1: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddTwoStates(6, 11);
                            break;
                        }
                        case 3: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(112, 113);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '(' || kind <= 14) continue block95;
                            kind = 14;
                            break;
                        }
                        case 12: {
                            if ((0x100002600L & l) == 0L) continue block95;
                            if (kind > 31) {
                                kind = 31;
                            }
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 14: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(201, 204);
                            break;
                        }
                        case 15: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddStates(201, 204);
                            break;
                        }
                        case 16: {
                            if (this.curChar != '\"') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 17: {
                            if (this.curChar != '\"' || kind <= 32) continue block95;
                            kind = 32;
                            break;
                        }
                        case 20: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(205, 209);
                            break;
                        }
                        case 21: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(201, 204);
                            break;
                        }
                        case 22: {
                            if ((0xF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 23: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAdd(21);
                            break;
                        }
                        case 25: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 27: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 28: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(201, 204);
                            break;
                        }
                        case 29: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(210, 211);
                            break;
                        }
                        case 30: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(201, 204);
                            break;
                        }
                        case 31: 
                        case 33: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddStates(197, 200);
                            break;
                        }
                        case 32: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(197, 200);
                            break;
                        }
                        case 34: {
                            if (this.curChar != '\'') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 33;
                            break;
                        }
                        case 36: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(212, 213);
                            break;
                        }
                        case 37: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(197, 200);
                            break;
                        }
                        case 38: {
                            if (this.curChar != '\'' || kind <= 32) continue block95;
                            kind = 32;
                            break;
                        }
                        case 40: {
                            if ((0x3FF200000000000L & l) == 0L) continue block95;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 43: {
                            if (this.curChar != '$' || kind <= 15) continue block95;
                            kind = 15;
                            break;
                        }
                        case 45: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(46, 47);
                            break;
                        }
                        case 47: {
                            if (this.curChar != '!' || kind <= 16) continue block95;
                            kind = 16;
                            break;
                        }
                        case 48: {
                            if (this.curChar != '$') continue block95;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(46, 47);
                            break;
                        }
                        case 49: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddStates(194, 196);
                            break;
                        }
                        case 50: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            break;
                        }
                        case 51: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block95;
                            kind = 18;
                            break;
                        }
                        case 53: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAddStates(190, 193);
                            break;
                        }
                        case 54: {
                            if ((0x3FF000000000000L & l) == 0L) continue block95;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(54, 56);
                            break;
                        }
                        case 55: {
                            if (this.curChar != '.' || kind <= 56) continue block95;
                            kind = 56;
                            break;
                        }
                        case 56: {
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 55;
                            break;
                        }
                        case 57: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(57, 58);
                            break;
                        }
                        case 58: {
                            if (this.curChar != '.') continue block95;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(59, 60);
                            break;
                        }
                        case 59: {
                            if ((0x3FF000000000000L & l) == 0L) continue block95;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAddTwoStates(59, 60);
                            break;
                        }
                        case 61: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(62);
                            break;
                        }
                        case 62: {
                            if ((0x3FF000000000000L & l) == 0L) continue block95;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(62);
                            break;
                        }
                        case 63: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(64);
                            break;
                        }
                        case 66: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(67);
                            break;
                        }
                        case 67: {
                            if ((0x3FF000000000000L & l) == 0L) continue block95;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(67);
                            break;
                        }
                        case 68: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(68, 69);
                            break;
                        }
                        case 70: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(71);
                            break;
                        }
                        case 71: {
                            if ((0x3FF000000000000L & l) == 0L) continue block95;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(71);
                            break;
                        }
                        case 72: {
                            if ((0x3FF000000000000L & l) == 0L) continue block95;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddStates(184, 189);
                            break;
                        }
                        case 73: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddTwoStates(64, 74);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block96: do {
                    switch (this.jjstateSet[--i]) {
                        case 52: {
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
                                break;
                            }
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 74: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 67) continue block96;
                            kind = 67;
                            break;
                        }
                        case 13: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 66) {
                                    kind = 66;
                                }
                                this.jjCheckNAdd(40);
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(214, 217);
                            break;
                        }
                        case 2: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 8: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 14: {
                            this.jjCheckNAddStates(201, 204);
                            break;
                        }
                        case 18: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(218, 223);
                            break;
                        }
                        case 19: {
                            if ((0x14404400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(201, 204);
                            break;
                        }
                        case 24: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 25: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 26: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 27: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 28: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddStates(201, 204);
                            break;
                        }
                        case 32: {
                            this.jjAddStates(197, 200);
                            break;
                        }
                        case 35: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(212, 213);
                            break;
                        }
                        case 39: 
                        case 40: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block96;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjCheckNAdd(40);
                            break;
                        }
                        case 41: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(214, 217);
                            break;
                        }
                        case 42: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(42, 43);
                            break;
                        }
                        case 44: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(44, 45);
                            break;
                        }
                        case 46: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(224, 225);
                            break;
                        }
                        case 51: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 60: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(226, 227);
                            break;
                        }
                        case 65: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(228, 229);
                            break;
                        }
                        case 69: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(30, 31);
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
                block97: do {
                    switch (this.jjstateSet[--i]) {
                        case 14: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(201, 204);
                            break;
                        }
                        case 32: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(197, 200);
                            break;
                        }
                        case 51: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block97;
                            kind = 18;
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
            if (i == (startsAt = 75 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_1(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    return 13;
                }
                if ((active0 & 0x1A0000L) != 0L) {
                    return 27;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 1;
                    return 13;
                }
                if ((active0 & 0x80000L) != 0L) {
                    return 25;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x600000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 2;
                    return 13;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x200000000L) != 0L) {
                    return 13;
                }
                if ((active0 & 0x400000000L) != 0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 3;
                    return 13;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_1(int pos, long active0, long active1) {
        return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_1(655360L);
            }
            case '(': {
                return this.jjStopAtPos(0, 10);
            }
            case '[': {
                return this.jjStopAtPos(0, 1);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_1(0x400000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_1(0x200000000L);
            }
            case '{': {
                return this.jjStopAtPos(0, 68);
            }
            case '}': {
                return this.jjStopAtPos(0, 69);
            }
        }
        return this.jjMoveNfa_1(12, 0);
    }

    private int jjMoveStringLiteralDfa1_1(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(0, active0, 0L);
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_1(1, 19, 25);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_1(active0, 131072L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x400000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_1(0, active0, 0L);
    }

    private int jjMoveStringLiteralDfa2_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(0, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(1, active0, 0L);
            return 2;
        }
        switch (this.curChar) {
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x400000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x200000000L);
            }
        }
        return this.jjStartNfa_1(1, active0, 0L);
    }

    private int jjMoveStringLiteralDfa3_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(1, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(2, active0, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x200000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(3, 33, 13);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_1(active0, 0x400000000L);
            }
        }
        return this.jjStartNfa_1(2, active0, 0L);
    }

    private int jjMoveStringLiteralDfa4_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(2, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(3, active0, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x400000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(4, 34, 13);
            }
        }
        return this.jjStartNfa_1(3, active0, 0L);
    }

    private int jjStartNfaWithStates_1(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_1(state, pos + 1);
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 28;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block42: do {
                    switch (this.jjstateSet[--i]) {
                        case 12: {
                            if ((0x100000200L & l) != 0L) {
                                this.jjCheckNAddTwoStates(0, 1);
                                break;
                            }
                            if (this.curChar == '#') {
                                this.jjCheckNAddStates(230, 232);
                                break;
                            }
                            if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(21, 22);
                                break;
                            }
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 27: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 0: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 1: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddTwoStates(6, 11);
                            break;
                        }
                        case 3: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(112, 113);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '(' || kind <= 14) continue block42;
                            kind = 14;
                            break;
                        }
                        case 13: {
                            if ((0x3FF200000000000L & l) == 0L) continue block42;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 18: {
                            if (this.curChar != '$' || kind <= 15) continue block42;
                            kind = 15;
                            break;
                        }
                        case 20: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(21, 22);
                            break;
                        }
                        case 22: {
                            if (this.curChar != '!' || kind <= 16) continue block42;
                            kind = 16;
                            break;
                        }
                        case 23: {
                            if (this.curChar != '$') continue block42;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(21, 22);
                            break;
                        }
                        case 24: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddStates(230, 232);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 26: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block42;
                            kind = 18;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block43: do {
                    switch (this.jjstateSet[--i]) {
                        case 12: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 66) {
                                    kind = 66;
                                }
                                this.jjCheckNAdd(13);
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(233, 236);
                            break;
                        }
                        case 27: {
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
                                break;
                            }
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 2: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 8: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 13: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block43;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjCheckNAdd(13);
                            break;
                        }
                        case 15: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 67) continue block43;
                            kind = 67;
                            break;
                        }
                        case 16: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(233, 236);
                            break;
                        }
                        case 17: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(17, 18);
                            break;
                        }
                        case 19: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(19, 20);
                            break;
                        }
                        case 21: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(143, 144);
                            break;
                        }
                        case 26: {
                            if (kind <= 18) break;
                            kind = 18;
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
                block44: do {
                    switch (this.jjstateSet[--i]) {
                        case 26: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block44;
                            kind = 18;
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
            if (i == (startsAt = 28 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_7(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1A0000L) != 0L) {
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x80000L) != 0L) {
                    return 0;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_7(int pos, long active0) {
        return this.jjMoveNfa_7(this.jjStopStringLiteralDfa_7(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_7() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_7(655360L);
            }
            case ']': {
                return this.jjMoveStringLiteralDfa1_7(0x10000000L);
            }
        }
        return this.jjMoveNfa_7(3, 0);
    }

    private int jjMoveStringLiteralDfa1_7(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_7(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_7(1, 19, 0);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa2_7(active0, 131072L);
            }
            case ']': {
                return this.jjMoveStringLiteralDfa2_7(active0, 0x10000000L);
            }
        }
        return this.jjStartNfa_7(0, active0);
    }

    private int jjMoveStringLiteralDfa2_7(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_7(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_7(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStopAtPos(2, 28);
            }
            case '[': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(2, 17);
            }
        }
        return this.jjStartNfa_7(1, active0);
    }

    private int jjStartNfaWithStates_7(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_7(state, pos + 1);
    }

    private int jjMoveNfa_7(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 12;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block23: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar == '$') {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                this.jjCheckNAddTwoStates(9, 10);
                                break;
                            }
                            if (this.curChar != '#') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 0: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 18) continue block23;
                            kind = 18;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '$' || kind <= 15) continue block23;
                            kind = 15;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '!' || kind <= 16) continue block23;
                            kind = 16;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '$') continue block23;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(99, 102);
                            break;
                        }
                        case 1: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(5, 6);
                            break;
                        }
                        case 7: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(7, 8);
                            break;
                        }
                        case 9: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(91, 92);
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
                block25: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block25;
                            kind = 18;
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
            if (i == (startsAt = 12 - this.jjnewStateCnt)) {
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

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec2[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    public ParserTokenManager(CharStream stream) {
        this.input_stream = stream;
    }

    public ParserTokenManager(CharStream stream, int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }

    public void ReInit(CharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 105;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(CharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 12 || lexState < 0) {
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
        Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        t.image = curTokenImage;
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    public Token getNextToken() {
        Token specialToken = null;
        int curPos = 0;
        block20: while (true) {
            Token matchedToken;
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException e) {
                this.jjmatchedKind = 0;
                matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                return matchedToken;
            }
            this.image = this.jjimage;
            this.image.setLength(0);
            this.jjimageLen = 0;
            while (true) {
                switch (this.curLexState) {
                    case 0: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_0();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 70) break;
                        this.jjmatchedKind = 70;
                        break;
                    }
                    case 1: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 70) break;
                        this.jjmatchedKind = 70;
                        break;
                    }
                    case 2: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_2();
                        break;
                    }
                    case 3: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_3();
                        break;
                    }
                    case 4: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_4();
                        break;
                    }
                    case 5: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_5();
                        break;
                    }
                    case 6: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_6();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 70) break;
                        this.jjmatchedKind = 70;
                        break;
                    }
                    case 7: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_7();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 30) break;
                        this.jjmatchedKind = 30;
                        break;
                    }
                    case 8: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_8();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 29) break;
                        this.jjmatchedKind = 29;
                        break;
                    }
                    case 9: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_9();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 29) break;
                        this.jjmatchedKind = 29;
                        break;
                    }
                    case 10: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_10();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 29) break;
                        this.jjmatchedKind = 29;
                        break;
                    }
                    case 11: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_11();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 71) break;
                        this.jjmatchedKind = 71;
                    }
                }
                if (this.jjmatchedKind == Integer.MAX_VALUE) break block20;
                if (this.jjmatchedPos + 1 < curPos) {
                    this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                }
                if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                    matchedToken = this.jjFillToken();
                    matchedToken.specialToken = specialToken;
                    this.TokenLexicalActions(matchedToken);
                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }
                    return matchedToken;
                }
                if ((jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                    if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                        matchedToken = this.jjFillToken();
                        if (specialToken == null) {
                            specialToken = matchedToken;
                        } else {
                            matchedToken.specialToken = specialToken;
                            specialToken = specialToken.next = matchedToken;
                        }
                        this.SkipLexicalActions(matchedToken);
                    } else {
                        this.SkipLexicalActions(null);
                    }
                    if (jjnewLexState[this.jjmatchedKind] == -1) continue block20;
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                    continue block20;
                }
                this.MoreLexicalActions();
                if (jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                }
                curPos = 0;
                this.jjmatchedKind = Integer.MAX_VALUE;
                try {
                    this.curChar = this.input_stream.readChar();
                }
                catch (IOException e1) {
                    // empty catch block
                    break block20;
                }
            }
            break;
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

    void SkipLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 70: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.input_stream.backup(1);
                this.inReference = false;
                if (this.debugPrint) {
                    System.out.print("REF_TERM :");
                }
                this.stateStackPop();
                break;
            }
            case 71: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (this.debugPrint) {
                    System.out.print("DIRECTIVE_TERM :");
                }
                this.input_stream.backup(1);
                this.inDirective = false;
                this.stateStackPop();
                break;
            }
        }
    }

    void MoreLexicalActions() {
        this.lengthOfMatch = this.jjmatchedPos + 1;
        this.jjimageLen += this.lengthOfMatch;
        switch (this.jjmatchedKind) {
            case 15: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                if (this.inComment) break;
                if (this.curLexState == 0) {
                    this.inReference = false;
                    this.stateStackPop();
                }
                this.inReference = true;
                if (this.debugPrint) {
                    System.out.print("$  : going to 0");
                }
                this.stateStackPush();
                this.SwitchTo(0);
                break;
            }
            case 16: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                if (this.inComment) break;
                if (this.curLexState == 0) {
                    this.inReference = false;
                    this.stateStackPop();
                }
                this.inReference = true;
                if (this.debugPrint) {
                    System.out.print("$!  : going to 0");
                }
                this.stateStackPush();
                this.SwitchTo(0);
                break;
            }
            case 17: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                if (this.inComment) break;
                this.inComment = true;
                this.stateStackPush();
                this.SwitchTo(7);
                break;
            }
            case 18: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                if (this.inComment) break;
                this.input_stream.backup(1);
                this.inComment = true;
                this.stateStackPush();
                this.SwitchTo(9);
                break;
            }
            case 19: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                if (this.inComment) break;
                this.inComment = true;
                this.stateStackPush();
                this.SwitchTo(8);
                break;
            }
            case 20: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                if (this.inComment) break;
                if (this.curLexState == 0 || this.curLexState == 1) {
                    this.inReference = false;
                    this.stateStackPop();
                }
                this.inDirective = true;
                if (this.debugPrint) {
                    System.out.print("# :  going to 3");
                }
                this.stateStackPush();
                this.SwitchTo(11);
                break;
            }
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 1: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.stateStackPush();
                this.SwitchTo(2);
                break;
            }
            case 2: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.stateStackPop();
                break;
            }
            case 10: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (!this.inComment) {
                    ++this.lparen;
                }
                if (this.curLexState != 1) break;
                this.SwitchTo(4);
                break;
            }
            case 11: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.RPARENHandler();
                break;
            }
            case 12: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.SwitchTo(0);
                break;
            }
            case 14: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (!this.inComment) {
                    this.inDirective = true;
                    if (this.debugPrint) {
                        System.out.print("#set :  going to 3");
                    }
                    this.stateStackPush();
                    this.inSet = true;
                    this.SwitchTo(3);
                }
                if (this.inComment) break;
                ++this.lparen;
                if (this.curLexState != 1) break;
                this.SwitchTo(4);
                break;
            }
            case 21: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (this.inComment) break;
                if (this.curLexState == 0) {
                    this.inReference = false;
                    this.stateStackPop();
                }
                this.inComment = true;
                this.stateStackPush();
                this.SwitchTo(10);
                break;
            }
            case 25: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.inComment = false;
                this.stateStackPop();
                break;
            }
            case 26: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.inComment = false;
                this.stateStackPop();
                break;
            }
            case 27: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.inComment = false;
                this.stateStackPop();
                break;
            }
            case 28: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.inComment = false;
                this.stateStackPop();
                break;
            }
            case 32: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (this.curLexState != 3 || this.inSet || this.lparen != 0) break;
                this.stateStackPop();
                break;
            }
            case 35: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (this.debugPrint) {
                    System.out.println(" NEWLINE :");
                }
                this.stateStackPop();
                if (this.inSet) {
                    this.inSet = false;
                }
                if (!this.inDirective) break;
                this.inDirective = false;
                break;
            }
            case 51: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.inDirective = false;
                this.stateStackPop();
                break;
            }
            case 52: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.SwitchTo(3);
                break;
            }
            case 53: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.SwitchTo(3);
                break;
            }
            case 54: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.inDirective = false;
                this.stateStackPop();
                break;
            }
            case 56: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (matchedToken.image.endsWith("..")) {
                    this.input_stream.backup(2);
                    matchedToken.image = matchedToken.image.substring(0, matchedToken.image.length() - 2);
                }
                if (this.lparen != 0 || this.inSet || this.curLexState == 4 || this.curLexState == 2) break;
                this.stateStackPop();
                break;
            }
            case 57: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (this.lparen != 0 || this.inSet || this.curLexState == 4) break;
                this.stateStackPop();
                break;
            }
            case 67: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.input_stream.backup(1);
                matchedToken.image = ".";
                if (this.debugPrint) {
                    System.out.print("DOT : switching to 1");
                }
                this.SwitchTo(1);
                break;
            }
            case 69: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.stateStackPop();
                break;
            }
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

    private static class ParserState {
        int lparen;
        int rparen;
        int lexstate;

        private ParserState() {
        }
    }
}


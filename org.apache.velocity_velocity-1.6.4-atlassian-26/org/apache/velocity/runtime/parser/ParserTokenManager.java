/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.util.EmptyStackException;
import java.util.Hashtable;
import java.util.Stack;
import org.apache.velocity.runtime.parser.CharStream;
import org.apache.velocity.runtime.parser.ParserConstants;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.TokenMgrError;

public class ParserTokenManager
implements ParserConstants {
    private int fileDepth = 0;
    private int lparen = 0;
    private int rparen = 0;
    Stack stateStack = new Stack();
    public boolean debugPrint = false;
    private boolean inReference;
    public boolean inDirective;
    private boolean inComment;
    public boolean inSet;
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{87, 89, 90, 91, 96, 97, 87, 90, 57, 96, 27, 28, 31, 11, 12, 13, 1, 2, 4, 11, 16, 12, 13, 24, 25, 29, 30, 66, 67, 69, 70, 71, 72, 83, 85, 80, 81, 77, 78, 14, 15, 17, 19, 24, 25, 60, 61, 73, 74, 94, 95, 98, 99, 5, 6, 7, 8, 9, 10, 78, 80, 81, 82, 87, 88, 78, 81, 10, 87, 19, 20, 31, 32, 34, 42, 43, 45, 50, 32, 51, 66, 43, 67, 54, 57, 64, 71, 76, 22, 23, 24, 25, 35, 40, 47, 13, 14, 26, 27, 85, 86, 89, 90, 6, 11, 33, 16, 18, 3, 4, 20, 21, 23, 24, 25, 26, 14, 15, 27, 28, 8, 9, 10, 11, 12, 13, 6, 11, 27, 17, 18, 19, 20, 21, 22, 50, 52, 53, 54, 64, 65, 50, 53, 59, 64, 6, 11, 48, 30, 31, 34, 14, 15, 16, 14, 19, 15, 16, 32, 33, 38, 39, 40, 41, 17, 18, 20, 22, 27, 28, 42, 43, 57, 58, 62, 63};
    public static final String[] jjstrLiteralImages = new String[]{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DIRECTIVE", "REFMOD2", "REFMODIFIER", "DEFAULT", "REFERENCE", "PRE_DIRECTIVE", "IN_MULTI_LINE_COMMENT", "IN_FORMAL_COMMENT", "IN_SINGLE_LINE_COMMENT"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = new long[]{-4163577855537831937L, 3L};
    static final long[] jjtoSkip = new long[]{0x2000000L, 12L};
    static final long[] jjtoSpecial = new long[]{0L, 12L};
    static final long[] jjtoMore = new long[]{253952L, 0L};
    protected CharStream input_stream;
    private final int[] jjrounds = new int[101];
    private final int[] jjstateSet = new int[202];
    StringBuffer image;
    int jjimageLen;
    int lengthOfMatch;
    protected char curChar;
    int curLexState = 3;
    int defaultLexState = 3;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public boolean stateStackPop() {
        Hashtable h;
        try {
            h = (Hashtable)this.stateStack.pop();
        }
        catch (EmptyStackException e) {
            this.lparen = 0;
            this.SwitchTo(3);
            return false;
        }
        if (this.debugPrint) {
            System.out.println(" stack pop (" + this.stateStack.size() + ") : lparen=" + (Integer)h.get("lparen") + " newstate=" + (Integer)h.get("lexstate"));
        }
        this.lparen = (Integer)h.get("lparen");
        this.rparen = (Integer)h.get("rparen");
        this.SwitchTo((Integer)h.get("lexstate"));
        return true;
    }

    public boolean stateStackPush() {
        if (this.debugPrint) {
            System.out.println(" (" + this.stateStack.size() + ") pushing cur state : " + this.curLexState);
        }
        Hashtable<String, Integer> h = new Hashtable<String, Integer>();
        h.put("lexstate", new Integer(this.curLexState));
        h.put("lparen", new Integer(this.lparen));
        h.put("rparen", new Integer(this.rparen));
        this.lparen = 0;
        this.stateStack.push(h);
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

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x10L) != 0L) {
                    return 58;
                }
                if ((active0 & 0x80000000L) != 0L) {
                    return 101;
                }
                if ((active0 & 0x40L) != 0L) {
                    return 65;
                }
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 57;
                    return 63;
                }
                if ((active0 & 0x200000000000L) != 0L) {
                    return 50;
                }
                if ((active0 & 0x70000L) != 0L) {
                    return 7;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x10000L) != 0L) {
                    return 5;
                }
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 57;
                    this.jjmatchedPos = 1;
                    return 63;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 57;
                    this.jjmatchedPos = 2;
                    return 63;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x10000000L) != 0L) {
                    return 63;
                }
                if ((active0 & 0x20000000L) != 0L) {
                    this.jjmatchedKind = 57;
                    this.jjmatchedPos = 3;
                    return 63;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }

    private final int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private final int jjStartNfaWithStates_0(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_0(327680L);
            }
            case '%': {
                return this.jjStopAtPos(0, 35);
            }
            case '(': {
                return this.jjStopAtPos(0, 8);
            }
            case '*': {
                return this.jjStopAtPos(0, 33);
            }
            case '+': {
                return this.jjStopAtPos(0, 32);
            }
            case ',': {
                return this.jjStopAtPos(0, 3);
            }
            case '-': {
                return this.jjStartNfaWithStates_0(0, 31, 101);
            }
            case '.': {
                return this.jjMoveStringLiteralDfa1_0(16L);
            }
            case '/': {
                return this.jjStopAtPos(0, 34);
            }
            case ':': {
                return this.jjStopAtPos(0, 5);
            }
            case '=': {
                return this.jjStartNfaWithStates_0(0, 45, 50);
            }
            case '[': {
                return this.jjStopAtPos(0, 1);
            }
            case ']': {
                return this.jjStopAtPos(0, 2);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(0x20000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_0(0x10000000L);
            }
            case '{': {
                return this.jjStartNfaWithStates_0(0, 6, 65);
            }
            case '}': {
                return this.jjStopAtPos(0, 7);
            }
        }
        return this.jjMoveNfa_0(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x40000L) == 0L) break;
                return this.jjStopAtPos(1, 18);
            }
            case '*': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_0(1, 16, 5);
            }
            case '.': {
                if ((active0 & 0x10L) == 0L) break;
                return this.jjStopAtPos(1, 4);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x20000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x10000000L);
            }
        }
        return this.jjStartNfa_0(0, active0);
    }

    private final int jjMoveStringLiteralDfa2_0(long old0, long active0) {
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
            case 'l': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x20000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x10000000L);
            }
        }
        return this.jjStartNfa_0(1, active0);
    }

    private final int jjMoveStringLiteralDfa3_0(long old0, long active0) {
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
            case 'e': {
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 28, 63);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x20000000L);
            }
        }
        return this.jjStartNfa_0(2, active0);
    }

    private final int jjMoveStringLiteralDfa4_0(long old0, long active0) {
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
            case 'e': {
                if ((active0 & 0x20000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(4, 29, 63);
            }
        }
        return this.jjStartNfa_0(3, active0);
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
        int startsAt = 0;
        this.jjnewStateCnt = 101;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block123: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 52) {
                                    kind = 52;
                                }
                                this.jjCheckNAddStates(0, 5);
                            } else if ((0x100002600L & l) != 0L) {
                                if (kind > 26) {
                                    kind = 26;
                                }
                                this.jjCheckNAdd(9);
                            } else if (this.curChar == '-') {
                                this.jjCheckNAddStates(6, 9);
                            } else if (this.curChar == '$') {
                                if (kind > 13) {
                                    kind = 13;
                                }
                                this.jjCheckNAddTwoStates(73, 74);
                            } else if (this.curChar == '.') {
                                this.jjCheckNAdd(58);
                            } else if (this.curChar == '!') {
                                if (kind > 44) {
                                    kind = 44;
                                }
                            } else if (this.curChar == '=') {
                                this.jjstateSet[this.jjnewStateCnt++] = 50;
                            } else if (this.curChar == '>') {
                                this.jjstateSet[this.jjnewStateCnt++] = 48;
                            } else if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 45;
                            } else if (this.curChar == '&') {
                                this.jjstateSet[this.jjnewStateCnt++] = 35;
                            } else if (this.curChar == '\'') {
                                this.jjCheckNAddStates(10, 12);
                            } else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(13, 15);
                            } else if (this.curChar == '#') {
                                this.jjstateSet[this.jjnewStateCnt++] = 7;
                            } else if (this.curChar == ')') {
                                if (kind > 9) {
                                    kind = 9;
                                }
                                this.jjCheckNAddStates(16, 18);
                            }
                            if ((0x2400L & l) != 0L) {
                                if (kind > 30) {
                                    kind = 30;
                                }
                            } else if (this.curChar == '!') {
                                this.jjstateSet[this.jjnewStateCnt++] = 54;
                            } else if (this.curChar == '>') {
                                if (kind > 40) {
                                    kind = 40;
                                }
                            } else if (this.curChar == '<' && kind > 38) {
                                kind = 38;
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 33;
                            break;
                        }
                        case 101: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(96, 97);
                            } else if (this.curChar == '.') {
                                this.jjCheckNAdd(58);
                            }
                            if ((0x3FF000000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(90, 91);
                            }
                            if ((0x3FF000000000000L & l) == 0L) break;
                            if (kind > 52) {
                                kind = 52;
                            }
                            this.jjCheckNAddTwoStates(87, 89);
                            break;
                        }
                        case 1: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(16, 18);
                            break;
                        }
                        case 2: {
                            if ((0x2400L & l) == 0L || kind <= 9) continue block123;
                            kind = 9;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\n' || kind <= 9) continue block123;
                            kind = 9;
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
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 15) continue block123;
                            kind = 15;
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
                            if ((0x100002600L & l) == 0L) continue block123;
                            if (kind > 26) {
                                kind = 26;
                            }
                            this.jjCheckNAdd(9);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 11: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 12: {
                            if (this.curChar != '\"' || kind <= 27) continue block123;
                            kind = 27;
                            break;
                        }
                        case 14: {
                            if ((0x8400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 15: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(19, 22);
                            break;
                        }
                        case 16: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 17: {
                            if ((0xF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 18: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAdd(16);
                            break;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 21: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 22: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 24: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(23, 24);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 26: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddStates(10, 12);
                            break;
                        }
                        case 27: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(10, 12);
                            break;
                        }
                        case 29: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(25, 26);
                            break;
                        }
                        case 30: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(10, 12);
                            break;
                        }
                        case 31: {
                            if (this.curChar != '\'' || kind <= 27) continue block123;
                            kind = 27;
                            break;
                        }
                        case 32: {
                            if ((0x2400L & l) == 0L || kind <= 30) continue block123;
                            kind = 30;
                            break;
                        }
                        case 33: {
                            if (this.curChar != '\n' || kind <= 30) continue block123;
                            kind = 30;
                            break;
                        }
                        case 34: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 33;
                            break;
                        }
                        case 35: {
                            if (this.curChar != '&' || kind <= 36) continue block123;
                            kind = 36;
                            break;
                        }
                        case 36: {
                            if (this.curChar != '&') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 35;
                            break;
                        }
                        case 44: {
                            if (this.curChar != '<' || kind <= 38) continue block123;
                            kind = 38;
                            break;
                        }
                        case 45: {
                            if (this.curChar != '=' || kind <= 39) continue block123;
                            kind = 39;
                            break;
                        }
                        case 46: {
                            if (this.curChar != '<') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 45;
                            break;
                        }
                        case 47: {
                            if (this.curChar != '>' || kind <= 40) continue block123;
                            kind = 40;
                            break;
                        }
                        case 48: {
                            if (this.curChar != '=' || kind <= 41) continue block123;
                            kind = 41;
                            break;
                        }
                        case 49: {
                            if (this.curChar != '>') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 48;
                            break;
                        }
                        case 50: {
                            if (this.curChar != '=' || kind <= 42) continue block123;
                            kind = 42;
                            break;
                        }
                        case 51: {
                            if (this.curChar != '=') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 50;
                            break;
                        }
                        case 54: {
                            if (this.curChar != '=' || kind <= 43) continue block123;
                            kind = 43;
                            break;
                        }
                        case 55: {
                            if (this.curChar != '!') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 54;
                            break;
                        }
                        case 56: {
                            if (this.curChar != '!' || kind <= 44) continue block123;
                            kind = 44;
                            break;
                        }
                        case 57: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(58);
                            break;
                        }
                        case 58: {
                            if ((0x3FF000000000000L & l) == 0L) continue block123;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(58, 59);
                            break;
                        }
                        case 60: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(61);
                            break;
                        }
                        case 61: {
                            if ((0x3FF000000000000L & l) == 0L) continue block123;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(61);
                            break;
                        }
                        case 63: {
                            if ((0x3FF000000000000L & l) == 0L) continue block123;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 63;
                            break;
                        }
                        case 66: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjAddStates(27, 28);
                            break;
                        }
                        case 70: {
                            if (this.curChar != '$' || kind <= 13) continue block123;
                            kind = 13;
                            break;
                        }
                        case 72: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(73, 74);
                            break;
                        }
                        case 74: {
                            if (this.curChar != '!' || kind <= 14) continue block123;
                            kind = 14;
                            break;
                        }
                        case 75: {
                            if (this.curChar != '$') continue block123;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAddTwoStates(73, 74);
                            break;
                        }
                        case 86: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAddStates(6, 9);
                            break;
                        }
                        case 87: {
                            if ((0x3FF000000000000L & l) == 0L) continue block123;
                            if (kind > 52) {
                                kind = 52;
                            }
                            this.jjCheckNAddTwoStates(87, 89);
                            break;
                        }
                        case 88: {
                            if (this.curChar != '.' || kind <= 52) continue block123;
                            kind = 52;
                            break;
                        }
                        case 89: {
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 88;
                            break;
                        }
                        case 90: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(90, 91);
                            break;
                        }
                        case 91: {
                            if (this.curChar != '.') continue block123;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(92, 93);
                            break;
                        }
                        case 92: {
                            if ((0x3FF000000000000L & l) == 0L) continue block123;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(92, 93);
                            break;
                        }
                        case 94: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(95);
                            break;
                        }
                        case 95: {
                            if ((0x3FF000000000000L & l) == 0L) continue block123;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(95);
                            break;
                        }
                        case 96: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(96, 97);
                            break;
                        }
                        case 98: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(99);
                            break;
                        }
                        case 99: {
                            if ((0x3FF000000000000L & l) == 0L) continue block123;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(99);
                            break;
                        }
                        case 100: {
                            if ((0x3FF000000000000L & l) == 0L) continue block123;
                            if (kind > 52) {
                                kind = 52;
                            }
                            this.jjCheckNAddStates(0, 5);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block124: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                this.jjCheckNAdd(63);
                            } else if (this.curChar == '\\') {
                                this.jjCheckNAddStates(29, 32);
                            } else if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 65;
                            } else if (this.curChar == '|') {
                                this.jjstateSet[this.jjnewStateCnt++] = 40;
                            }
                            if (this.curChar == 'n') {
                                this.jjAddStates(33, 34);
                                break;
                            }
                            if (this.curChar == 'g') {
                                this.jjAddStates(35, 36);
                                break;
                            }
                            if (this.curChar == 'l') {
                                this.jjAddStates(37, 38);
                                break;
                            }
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
                                break;
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 42;
                                break;
                            }
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 6: {
                            if (kind <= 15) break;
                            kind = 15;
                            break;
                        }
                        case 11: {
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(39, 44);
                            break;
                        }
                        case 14: {
                            if ((0x14404410000000L & l) == 0L) break;
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 19: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 20: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 21: {
                            if ((0x7E0000007EL & l) == 0L) break;
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
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 27: {
                            this.jjAddStates(10, 12);
                            break;
                        }
                        case 28: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(25, 26);
                            break;
                        }
                        case 37: {
                            if (this.curChar != 'd' || kind <= 36) continue block124;
                            kind = 36;
                            break;
                        }
                        case 38: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 39: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 40: {
                            if (this.curChar != '|' || kind <= 37) continue block124;
                            kind = 37;
                            break;
                        }
                        case 41: {
                            if (this.curChar != '|') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 42: {
                            if (this.curChar != 'r' || kind <= 37) continue block124;
                            kind = 37;
                            break;
                        }
                        case 43: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 52: {
                            if (this.curChar != 'q' || kind <= 42) continue block124;
                            kind = 42;
                            break;
                        }
                        case 53: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 52;
                            break;
                        }
                        case 59: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(45, 46);
                            break;
                        }
                        case 62: 
                        case 63: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block124;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(63);
                            break;
                        }
                        case 64: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 65;
                            break;
                        }
                        case 65: 
                        case 66: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(66, 67);
                            break;
                        }
                        case 67: {
                            if (this.curChar != '}' || kind <= 58) continue block124;
                            kind = 58;
                            break;
                        }
                        case 68: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(29, 32);
                            break;
                        }
                        case 69: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(69, 70);
                            break;
                        }
                        case 71: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(71, 72);
                            break;
                        }
                        case 73: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(47, 48);
                            break;
                        }
                        case 76: {
                            if (this.curChar != 'l') break;
                            this.jjAddStates(37, 38);
                            break;
                        }
                        case 77: {
                            if (this.curChar != 't' || kind <= 38) continue block124;
                            kind = 38;
                            break;
                        }
                        case 78: {
                            if (this.curChar != 'e' || kind <= 39) continue block124;
                            kind = 39;
                            break;
                        }
                        case 79: {
                            if (this.curChar != 'g') break;
                            this.jjAddStates(35, 36);
                            break;
                        }
                        case 80: {
                            if (this.curChar != 't' || kind <= 40) continue block124;
                            kind = 40;
                            break;
                        }
                        case 81: {
                            if (this.curChar != 'e' || kind <= 41) continue block124;
                            kind = 41;
                            break;
                        }
                        case 82: {
                            if (this.curChar != 'n') break;
                            this.jjAddStates(33, 34);
                            break;
                        }
                        case 83: {
                            if (this.curChar != 'e' || kind <= 43) continue block124;
                            kind = 43;
                            break;
                        }
                        case 84: {
                            if (this.curChar != 't' || kind <= 44) continue block124;
                            kind = 44;
                            break;
                        }
                        case 85: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 84;
                            break;
                        }
                        case 93: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(49, 50);
                            break;
                        }
                        case 97: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(51, 52);
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
                block125: do {
                    switch (this.jjstateSet[--i]) {
                        case 6: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block125;
                            kind = 15;
                            break;
                        }
                        case 11: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(13, 15);
                            break;
                        }
                        case 27: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(10, 12);
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
            if (i == (startsAt = 101 - this.jjnewStateCnt)) {
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
                if ((active0 & 0x70000L) != 0L) {
                    return 2;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_6(int pos, long active0) {
        return this.jjMoveNfa_6(this.jjStopStringLiteralDfa_6(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_6(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_6() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_6(327680L);
            }
            case '*': {
                return this.jjMoveStringLiteralDfa1_6(0x1000000L);
            }
        }
        return this.jjMoveNfa_6(3, 0);
    }

    private final int jjMoveStringLiteralDfa1_6(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x40000L) != 0L) {
                    return this.jjStopAtPos(1, 18);
                }
                if ((active0 & 0x1000000L) == 0L) break;
                return this.jjStopAtPos(1, 24);
            }
            case '*': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_6(1, 16, 0);
            }
        }
        return this.jjStartNfa_6(0, active0);
    }

    private final int jjMoveNfa_6(int startState, int curPos) {
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
                                if (kind > 13) {
                                    kind = 13;
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
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 15) continue block23;
                            kind = 15;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '$' || kind <= 13) continue block23;
                            kind = 13;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '!' || kind <= 14) continue block23;
                            kind = 14;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '$') continue block23;
                            if (kind > 13) {
                                kind = 13;
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
                            this.jjCheckNAddStates(53, 56);
                            break;
                        }
                        case 1: {
                            if (kind <= 15) break;
                            kind = 15;
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
                            this.jjAddStates(57, 58);
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
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block25;
                            kind = 15;
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
                if ((active0 & 0x70000L) != 0L) {
                    return 2;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_5(int pos, long active0) {
        return this.jjMoveNfa_5(this.jjStopStringLiteralDfa_5(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_5(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_5() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_5(327680L);
            }
        }
        return this.jjMoveNfa_5(3, 0);
    }

    private final int jjMoveStringLiteralDfa1_5(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_5(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x40000L) == 0L) break;
                return this.jjStopAtPos(1, 18);
            }
            case '*': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_5(1, 16, 0);
            }
        }
        return this.jjStartNfa_5(0, active0);
    }

    private final int jjMoveNfa_5(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 92;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block102: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 52) {
                                    kind = 52;
                                }
                                this.jjCheckNAddStates(59, 64);
                                break;
                            }
                            if (this.curChar == '-') {
                                this.jjCheckNAddStates(65, 68);
                                break;
                            }
                            if (this.curChar == '$') {
                                if (kind > 13) {
                                    kind = 13;
                                }
                                this.jjCheckNAddTwoStates(26, 27);
                                break;
                            }
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(11);
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
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 15) continue block102;
                            kind = 15;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 10: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(11);
                            break;
                        }
                        case 11: {
                            if ((0x3FF000000000000L & l) == 0L) continue block102;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(11, 12);
                            break;
                        }
                        case 13: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(14);
                            break;
                        }
                        case 14: {
                            if ((0x3FF000000000000L & l) == 0L) continue block102;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(14);
                            break;
                        }
                        case 16: {
                            if ((0x3FF000000000000L & l) == 0L) continue block102;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 19: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjAddStates(69, 70);
                            break;
                        }
                        case 23: {
                            if (this.curChar != '$' || kind <= 13) continue block102;
                            kind = 13;
                            break;
                        }
                        case 25: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(26, 27);
                            break;
                        }
                        case 27: {
                            if (this.curChar != '!' || kind <= 14) continue block102;
                            kind = 14;
                            break;
                        }
                        case 28: {
                            if (this.curChar != '$') continue block102;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAddTwoStates(26, 27);
                            break;
                        }
                        case 31: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(71, 73);
                            break;
                        }
                        case 32: {
                            if ((0x2400L & l) == 0L || kind <= 46) continue block102;
                            kind = 46;
                            break;
                        }
                        case 33: {
                            if (this.curChar != '\n' || kind <= 46) continue block102;
                            kind = 46;
                            break;
                        }
                        case 34: 
                        case 51: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAdd(33);
                            break;
                        }
                        case 42: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(74, 76);
                            break;
                        }
                        case 43: {
                            if ((0x2400L & l) == 0L || kind <= 49) continue block102;
                            kind = 49;
                            break;
                        }
                        case 44: {
                            if (this.curChar != '\n' || kind <= 49) continue block102;
                            kind = 49;
                            break;
                        }
                        case 45: 
                        case 67: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAdd(44);
                            break;
                        }
                        case 50: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(77, 79);
                            break;
                        }
                        case 66: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(80, 82);
                            break;
                        }
                        case 77: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAddStates(65, 68);
                            break;
                        }
                        case 78: {
                            if ((0x3FF000000000000L & l) == 0L) continue block102;
                            if (kind > 52) {
                                kind = 52;
                            }
                            this.jjCheckNAddTwoStates(78, 80);
                            break;
                        }
                        case 79: {
                            if (this.curChar != '.' || kind <= 52) continue block102;
                            kind = 52;
                            break;
                        }
                        case 80: {
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 79;
                            break;
                        }
                        case 81: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(81, 82);
                            break;
                        }
                        case 82: {
                            if (this.curChar != '.') continue block102;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(83, 84);
                            break;
                        }
                        case 83: {
                            if ((0x3FF000000000000L & l) == 0L) continue block102;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(83, 84);
                            break;
                        }
                        case 85: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(86);
                            break;
                        }
                        case 86: {
                            if ((0x3FF000000000000L & l) == 0L) continue block102;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(86);
                            break;
                        }
                        case 87: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(87, 88);
                            break;
                        }
                        case 89: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(90);
                            break;
                        }
                        case 90: {
                            if ((0x3FF000000000000L & l) == 0L) continue block102;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(90);
                            break;
                        }
                        case 91: {
                            if ((0x3FF000000000000L & l) == 0L) continue block102;
                            if (kind > 52) {
                                kind = 52;
                            }
                            this.jjCheckNAddStates(59, 64);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block103: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                this.jjCheckNAdd(16);
                            } else if (this.curChar == '{') {
                                this.jjAddStates(83, 87);
                            } else if (this.curChar == '\\') {
                                this.jjCheckNAddStates(88, 91);
                            }
                            if (this.curChar == 'e') {
                                this.jjAddStates(92, 94);
                                break;
                            }
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 18;
                                break;
                            }
                            if (this.curChar == 's') {
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                                break;
                            }
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 1: {
                            if (kind <= 15) break;
                            kind = 15;
                            break;
                        }
                        case 4: {
                            if (this.curChar != 'f' || kind <= 47) continue block103;
                            kind = 47;
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'p' || kind <= 50) continue block103;
                            kind = 50;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 12: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(95, 96);
                            break;
                        }
                        case 15: 
                        case 16: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block103;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(16);
                            break;
                        }
                        case 17: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 18: 
                        case 19: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(19, 20);
                            break;
                        }
                        case 20: {
                            if (this.curChar != '}' || kind <= 58) continue block103;
                            kind = 58;
                            break;
                        }
                        case 21: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(88, 91);
                            break;
                        }
                        case 22: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(22, 23);
                            break;
                        }
                        case 24: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(24, 25);
                            break;
                        }
                        case 26: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(97, 98);
                            break;
                        }
                        case 29: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(92, 94);
                            break;
                        }
                        case 30: {
                            if (this.curChar != 'd') continue block103;
                            if (kind > 46) {
                                kind = 46;
                            }
                            this.jjCheckNAddStates(71, 73);
                            break;
                        }
                        case 35: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                        case 36: {
                            if (this.curChar != 'f' || kind <= 48) continue block103;
                            kind = 48;
                            break;
                        }
                        case 37: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 38: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 39: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 40: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 39;
                            break;
                        }
                        case 41: {
                            if (this.curChar != 'e') continue block103;
                            if (kind > 49) {
                                kind = 49;
                            }
                            this.jjCheckNAddStates(74, 76);
                            break;
                        }
                        case 46: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 47: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 46;
                            break;
                        }
                        case 48: {
                            if (this.curChar != '{') break;
                            this.jjAddStates(83, 87);
                            break;
                        }
                        case 49: {
                            if (this.curChar != '}') continue block103;
                            if (kind > 46) {
                                kind = 46;
                            }
                            this.jjCheckNAddStates(77, 79);
                            break;
                        }
                        case 52: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 49;
                            break;
                        }
                        case 53: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 52;
                            break;
                        }
                        case 54: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 53;
                            break;
                        }
                        case 55: {
                            if (this.curChar != '}' || kind <= 47) continue block103;
                            kind = 47;
                            break;
                        }
                        case 56: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 55;
                            break;
                        }
                        case 57: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 56;
                            break;
                        }
                        case 58: {
                            if (this.curChar != '}' || kind <= 48) continue block103;
                            kind = 48;
                            break;
                        }
                        case 59: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 58;
                            break;
                        }
                        case 60: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 59;
                            break;
                        }
                        case 61: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 60;
                            break;
                        }
                        case 62: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 61;
                            break;
                        }
                        case 63: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 62;
                            break;
                        }
                        case 64: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 63;
                            break;
                        }
                        case 65: {
                            if (this.curChar != '}') continue block103;
                            if (kind > 49) {
                                kind = 49;
                            }
                            this.jjCheckNAddStates(80, 82);
                            break;
                        }
                        case 68: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 65;
                            break;
                        }
                        case 69: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 68;
                            break;
                        }
                        case 70: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 69;
                            break;
                        }
                        case 71: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 70;
                            break;
                        }
                        case 72: {
                            if (this.curChar != '}' || kind <= 50) continue block103;
                            kind = 50;
                            break;
                        }
                        case 73: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 72;
                            break;
                        }
                        case 74: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 73;
                            break;
                        }
                        case 75: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 74;
                            break;
                        }
                        case 76: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 75;
                            break;
                        }
                        case 84: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(99, 100);
                            break;
                        }
                        case 88: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(101, 102);
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
                block104: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block104;
                            kind = 15;
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
            if (i == (startsAt = 92 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_3(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x180000L) != 0L) {
                    return 14;
                }
                if ((active0 & 0x70000L) != 0L) {
                    return 33;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_3(int pos, long active0) {
        return this.jjMoveNfa_3(this.jjStopStringLiteralDfa_3(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_3(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_3() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_3(327680L);
            }
            case '\\': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_3(524288L);
            }
        }
        return this.jjMoveNfa_3(22, 0);
    }

    private final int jjMoveStringLiteralDfa1_3(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x40000L) == 0L) break;
                return this.jjStopAtPos(1, 18);
            }
            case '*': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_3(1, 16, 31);
            }
            case '\\': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStartNfaWithStates_3(1, 19, 34);
            }
        }
        return this.jjStartNfa_3(0, active0);
    }

    private final int jjMoveNfa_3(int startState, int curPos) {
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
                block52: do {
                    switch (this.jjstateSet[--i]) {
                        case 22: {
                            if ((0xFFFFFFE7FFFFFFFFL & l) != 0L) {
                                if (kind > 21) {
                                    kind = 21;
                                }
                                this.jjCheckNAdd(12);
                            } else if (this.curChar == '#') {
                                this.jjCheckNAddStates(103, 105);
                            } else if (this.curChar == '$') {
                                if (kind > 13) {
                                    kind = 13;
                                }
                                this.jjCheckNAddTwoStates(27, 28);
                            }
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 14: {
                            if (this.curChar == '$') {
                                this.jjCheckNAddTwoStates(27, 28);
                            } else if (this.curChar == '#') {
                                this.jjAddStates(106, 107);
                            }
                            if (this.curChar != '$' || kind <= 13) continue block52;
                            kind = 13;
                            break;
                        }
                        case 34: {
                            if (this.curChar == '$') {
                                this.jjCheckNAddTwoStates(27, 28);
                            }
                            if (this.curChar != '$' || kind <= 13) continue block52;
                            kind = 13;
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
                            this.jjAddStates(108, 109);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '(' || kind <= 12) continue block52;
                            kind = 12;
                            break;
                        }
                        case 12: {
                            if ((0xFFFFFFE7FFFFFFFFL & l) == 0L) continue block52;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 15: {
                            if (this.curChar != '#') break;
                            this.jjAddStates(106, 107);
                            break;
                        }
                        case 17: {
                            if ((0x3FF000000000000L & l) == 0L) continue block52;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjAddStates(110, 111);
                            break;
                        }
                        case 24: {
                            if (this.curChar != '$' || kind <= 13) continue block52;
                            kind = 13;
                            break;
                        }
                        case 26: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(27, 28);
                            break;
                        }
                        case 28: {
                            if (this.curChar != '!' || kind <= 14) continue block52;
                            kind = 14;
                            break;
                        }
                        case 29: {
                            if (this.curChar != '$') continue block52;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAddTwoStates(27, 28);
                            break;
                        }
                        case 30: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddStates(103, 105);
                            break;
                        }
                        case 31: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 32;
                            break;
                        }
                        case 32: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 15) continue block52;
                            kind = 15;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block53: do {
                    switch (this.jjstateSet[--i]) {
                        case 22: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L) {
                                if (kind > 21) {
                                    kind = 21;
                                }
                                this.jjCheckNAdd(12);
                            } else if (this.curChar == '\\') {
                                this.jjCheckNAddStates(112, 115);
                            }
                            if (this.curChar != '\\') break;
                            this.jjAddStates(116, 117);
                            break;
                        }
                        case 14: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(25, 26);
                            }
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(23, 24);
                            }
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 34: {
                            if (this.curChar == '\\') {
                                this.jjAddStates(116, 117);
                            }
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(25, 26);
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(23, 24);
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
                        case 12: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) continue block53;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(116, 117);
                            break;
                        }
                        case 16: 
                        case 17: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block53;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAdd(17);
                            break;
                        }
                        case 18: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 19: 
                        case 20: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(20, 21);
                            break;
                        }
                        case 21: {
                            if (this.curChar != '}' || kind <= 11) continue block53;
                            kind = 11;
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
                            this.jjAddStates(118, 119);
                            break;
                        }
                        case 32: {
                            if (kind <= 15) break;
                            kind = 15;
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
                block54: do {
                    switch (this.jjstateSet[--i]) {
                        case 12: 
                        case 22: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block54;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 32: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block54;
                            kind = 15;
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

    private final int jjStopStringLiteralDfa_7(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x70000L) != 0L) {
                    return 2;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_7(int pos, long active0) {
        return this.jjMoveNfa_7(this.jjStopStringLiteralDfa_7(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_7(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_7() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_7(327680L);
            }
            case '*': {
                return this.jjMoveStringLiteralDfa1_7(0x800000L);
            }
        }
        return this.jjMoveNfa_7(3, 0);
    }

    private final int jjMoveStringLiteralDfa1_7(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_7(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x40000L) != 0L) {
                    return this.jjStopAtPos(1, 18);
                }
                if ((active0 & 0x800000L) == 0L) break;
                return this.jjStopAtPos(1, 23);
            }
            case '*': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_7(1, 16, 0);
            }
        }
        return this.jjStartNfa_7(0, active0);
    }

    private final int jjMoveNfa_7(int startState, int curPos) {
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
                                if (kind > 13) {
                                    kind = 13;
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
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 15) continue block23;
                            kind = 15;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '$' || kind <= 13) continue block23;
                            kind = 13;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(9, 10);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '!' || kind <= 14) continue block23;
                            kind = 14;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '$') continue block23;
                            if (kind > 13) {
                                kind = 13;
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
                            this.jjCheckNAddStates(53, 56);
                            break;
                        }
                        case 1: {
                            if (kind <= 15) break;
                            kind = 15;
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
                            this.jjAddStates(57, 58);
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
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block25;
                            kind = 15;
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

    private final int jjStopStringLiteralDfa_8(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x70000L) != 0L) {
                    return 2;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_8(int pos, long active0) {
        return this.jjMoveNfa_8(this.jjStopStringLiteralDfa_8(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_8(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_8() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_8(327680L);
            }
        }
        return this.jjMoveNfa_8(3, 0);
    }

    private final int jjMoveStringLiteralDfa1_8(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x40000L) == 0L) break;
                return this.jjStopAtPos(1, 18);
            }
            case '*': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_8(1, 16, 0);
            }
        }
        return this.jjStartNfa_8(0, active0);
    }

    private final int jjMoveNfa_8(int startState, int curPos) {
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
                                if (kind > 22) {
                                    kind = 22;
                                }
                            } else if (this.curChar == '$') {
                                if (kind > 13) {
                                    kind = 13;
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
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 15) continue block26;
                            kind = 15;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 4: {
                            if ((0x2400L & l) == 0L || kind <= 22) continue block26;
                            kind = 22;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\n' || kind <= 22) continue block26;
                            kind = 22;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 9: {
                            if (this.curChar != '$' || kind <= 13) continue block26;
                            kind = 13;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '!' || kind <= 14) continue block26;
                            kind = 14;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '$') continue block26;
                            if (kind > 13) {
                                kind = 13;
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
                            this.jjCheckNAddStates(120, 123);
                            break;
                        }
                        case 1: {
                            if (kind <= 15) break;
                            kind = 15;
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
                            this.jjAddStates(124, 125);
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
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block28;
                            kind = 15;
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

    private final int jjStopStringLiteralDfa_4(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x70000L) != 0L) {
                    return 27;
                }
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    return 13;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 1;
                    return 13;
                }
                if ((active0 & 0x10000L) != 0L) {
                    return 25;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 2;
                    return 13;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x10000000L) != 0L) {
                    return 13;
                }
                if ((active0 & 0x20000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 3;
                    return 13;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_4(int pos, long active0, long active1) {
        return this.jjMoveNfa_4(this.jjStopStringLiteralDfa_4(pos, active0, active1), pos + 1);
    }

    private final int jjStartNfaWithStates_4(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_4() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_4(327680L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_4(0x20000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_4(0x10000000L);
            }
            case '{': {
                return this.jjStopAtPos(0, 64);
            }
            case '}': {
                return this.jjStopAtPos(0, 65);
            }
        }
        return this.jjMoveNfa_4(12, 0);
    }

    private final int jjMoveStringLiteralDfa1_4(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(0, active0, 0L);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x40000L) == 0L) break;
                return this.jjStopAtPos(1, 18);
            }
            case '*': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_4(1, 16, 25);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x20000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x10000000L);
            }
        }
        return this.jjStartNfa_4(0, active0, 0L);
    }

    private final int jjMoveStringLiteralDfa2_4(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_4(0, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(1, active0, 0L);
            return 2;
        }
        switch (this.curChar) {
            case 'l': {
                return this.jjMoveStringLiteralDfa3_4(active0, 0x20000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_4(active0, 0x10000000L);
            }
        }
        return this.jjStartNfa_4(1, active0, 0L);
    }

    private final int jjMoveStringLiteralDfa3_4(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_4(1, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(2, active0, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(3, 28, 13);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_4(active0, 0x20000000L);
            }
        }
        return this.jjStartNfa_4(2, active0, 0L);
    }

    private final int jjMoveStringLiteralDfa4_4(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_4(2, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(3, active0, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x20000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(4, 29, 13);
            }
        }
        return this.jjStartNfa_4(3, active0, 0L);
    }

    private final int jjMoveNfa_4(int startState, int curPos) {
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
                                this.jjCheckNAddStates(126, 128);
                                break;
                            }
                            if (this.curChar == '$') {
                                if (kind > 13) {
                                    kind = 13;
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
                            this.jjAddStates(108, 109);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '(' || kind <= 12) continue block42;
                            kind = 12;
                            break;
                        }
                        case 13: {
                            if ((0x3FF200000000000L & l) == 0L) continue block42;
                            if (kind > 62) {
                                kind = 62;
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
                            if (this.curChar != '$' || kind <= 13) continue block42;
                            kind = 13;
                            break;
                        }
                        case 20: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(21, 22);
                            break;
                        }
                        case 22: {
                            if (this.curChar != '!' || kind <= 14) continue block42;
                            kind = 14;
                            break;
                        }
                        case 23: {
                            if (this.curChar != '$') continue block42;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAddTwoStates(21, 22);
                            break;
                        }
                        case 24: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddStates(126, 128);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 26: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 15) continue block42;
                            kind = 15;
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
                                if (kind > 62) {
                                    kind = 62;
                                }
                                this.jjCheckNAdd(13);
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(129, 132);
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
                            if (kind > 62) {
                                kind = 62;
                            }
                            this.jjCheckNAdd(13);
                            break;
                        }
                        case 15: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 63) continue block43;
                            kind = 63;
                            break;
                        }
                        case 16: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(129, 132);
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
                            this.jjAddStates(133, 134);
                            break;
                        }
                        case 26: {
                            if (kind <= 15) break;
                            kind = 15;
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
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block44;
                            kind = 15;
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

    private final int jjStopStringLiteralDfa_1(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x70000L) != 0L) {
                    return 48;
                }
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    return 36;
                }
                if ((active0 & 0x10L) != 0L) {
                    return 70;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x10000L) != 0L) {
                    return 46;
                }
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 1;
                    return 36;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 2;
                    return 36;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x10000000L) != 0L) {
                    return 36;
                }
                if ((active0 & 0x20000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 3;
                    return 36;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_1(int pos, long active0) {
        return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_1(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_1(327680L);
            }
            case ')': {
                return this.jjStopAtPos(0, 10);
            }
            case ',': {
                return this.jjStopAtPos(0, 3);
            }
            case '.': {
                return this.jjMoveStringLiteralDfa1_1(16L);
            }
            case ':': {
                return this.jjStopAtPos(0, 5);
            }
            case '[': {
                return this.jjStopAtPos(0, 1);
            }
            case ']': {
                return this.jjStopAtPos(0, 2);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_1(0x20000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_1(0x10000000L);
            }
            case '{': {
                return this.jjStopAtPos(0, 6);
            }
            case '}': {
                return this.jjStopAtPos(0, 7);
            }
        }
        return this.jjMoveNfa_1(13, 0);
    }

    private final int jjMoveStringLiteralDfa1_1(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x40000L) == 0L) break;
                return this.jjStopAtPos(1, 18);
            }
            case '*': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_1(1, 16, 46);
            }
            case '.': {
                if ((active0 & 0x10L) == 0L) break;
                return this.jjStopAtPos(1, 4);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x20000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x10000000L);
            }
        }
        return this.jjStartNfa_1(0, active0);
    }

    private final int jjMoveStringLiteralDfa2_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 'l': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x20000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x10000000L);
            }
        }
        return this.jjStartNfa_1(1, active0);
    }

    private final int jjMoveStringLiteralDfa3_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(3, 28, 36);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_1(active0, 0x20000000L);
            }
        }
        return this.jjStartNfa_1(2, active0);
    }

    private final int jjMoveStringLiteralDfa4_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x20000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(4, 29, 36);
            }
        }
        return this.jjStartNfa_1(3, active0);
    }

    private final int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 71;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block93: do {
                    switch (this.jjstateSet[--i]) {
                        case 13: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 52) {
                                    kind = 52;
                                }
                                this.jjCheckNAddStates(135, 140);
                            } else if ((0x100002600L & l) != 0L) {
                                if (kind > 26) {
                                    kind = 26;
                                }
                                this.jjCheckNAdd(12);
                            } else if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(60, 70);
                            } else if (this.curChar == '-') {
                                this.jjCheckNAddStates(141, 144);
                            } else if (this.curChar == '#') {
                                this.jjCheckNAddStates(145, 147);
                            } else if (this.curChar == '$') {
                                if (kind > 13) {
                                    kind = 13;
                                }
                                this.jjCheckNAddTwoStates(42, 43);
                            } else if (this.curChar == '\'') {
                                this.jjCheckNAddStates(148, 150);
                            } else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(151, 153);
                            }
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 60: 
                        case 70: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(60, 61);
                            break;
                        }
                        case 48: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 46;
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
                            this.jjAddStates(108, 109);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '(' || kind <= 12) continue block93;
                            kind = 12;
                            break;
                        }
                        case 12: {
                            if ((0x100002600L & l) == 0L) continue block93;
                            if (kind > 26) {
                                kind = 26;
                            }
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 14: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(151, 153);
                            break;
                        }
                        case 15: {
                            if (this.curChar != '\"' || kind <= 27) continue block93;
                            kind = 27;
                            break;
                        }
                        case 17: {
                            if ((0x8400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(151, 153);
                            break;
                        }
                        case 18: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(154, 157);
                            break;
                        }
                        case 19: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(151, 153);
                            break;
                        }
                        case 20: {
                            if ((0xF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 21: {
                            if ((0xFF000000000000L & l) == 0L) break;
                            this.jjCheckNAdd(19);
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
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(151, 153);
                            break;
                        }
                        case 27: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(118, 119);
                            break;
                        }
                        case 28: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(151, 153);
                            break;
                        }
                        case 29: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddStates(148, 150);
                            break;
                        }
                        case 30: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(148, 150);
                            break;
                        }
                        case 32: {
                            if (this.curChar != ' ') break;
                            this.jjAddStates(158, 159);
                            break;
                        }
                        case 33: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(148, 150);
                            break;
                        }
                        case 34: {
                            if (this.curChar != '\'' || kind <= 27) continue block93;
                            kind = 27;
                            break;
                        }
                        case 36: {
                            if ((0x3FF200000000000L & l) == 0L) continue block93;
                            if (kind > 62) {
                                kind = 62;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 39: {
                            if (this.curChar != '$' || kind <= 13) continue block93;
                            kind = 13;
                            break;
                        }
                        case 41: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(42, 43);
                            break;
                        }
                        case 43: {
                            if (this.curChar != '!' || kind <= 14) continue block93;
                            kind = 14;
                            break;
                        }
                        case 44: {
                            if (this.curChar != '$') continue block93;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            break;
                        }
                        case 45: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddStates(145, 147);
                            break;
                        }
                        case 46: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 47;
                            break;
                        }
                        case 47: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 15) continue block93;
                            kind = 15;
                            break;
                        }
                        case 49: {
                            if (this.curChar != '-') break;
                            this.jjCheckNAddStates(141, 144);
                            break;
                        }
                        case 50: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 52) {
                                kind = 52;
                            }
                            this.jjCheckNAddTwoStates(50, 52);
                            break;
                        }
                        case 51: {
                            if (this.curChar != '.' || kind <= 52) continue block93;
                            kind = 52;
                            break;
                        }
                        case 52: {
                            if (this.curChar != '.') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            break;
                        }
                        case 53: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(53, 54);
                            break;
                        }
                        case 54: {
                            if (this.curChar != '.') continue block93;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(55, 56);
                            break;
                        }
                        case 55: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(55, 56);
                            break;
                        }
                        case 57: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(58);
                            break;
                        }
                        case 58: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(58);
                            break;
                        }
                        case 59: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(60);
                            break;
                        }
                        case 62: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(63);
                            break;
                        }
                        case 63: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(63);
                            break;
                        }
                        case 64: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(64, 65);
                            break;
                        }
                        case 66: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(67);
                            break;
                        }
                        case 67: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(67);
                            break;
                        }
                        case 68: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 52) {
                                kind = 52;
                            }
                            this.jjCheckNAddStates(135, 140);
                            break;
                        }
                        case 69: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddTwoStates(60, 70);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block94: do {
                    switch (this.jjstateSet[--i]) {
                        case 13: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 62) {
                                    kind = 62;
                                }
                                this.jjCheckNAdd(36);
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(160, 163);
                            break;
                        }
                        case 70: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 63) continue block94;
                            kind = 63;
                            break;
                        }
                        case 48: {
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
                        case 14: {
                            this.jjCheckNAddStates(151, 153);
                            break;
                        }
                        case 16: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(164, 169);
                            break;
                        }
                        case 17: {
                            if ((0x14404410000000L & l) == 0L) break;
                            this.jjCheckNAddStates(151, 153);
                            break;
                        }
                        case 22: {
                            if (this.curChar != 'u') break;
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
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 26: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddStates(151, 153);
                            break;
                        }
                        case 30: {
                            this.jjAddStates(148, 150);
                            break;
                        }
                        case 31: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(158, 159);
                            break;
                        }
                        case 35: 
                        case 36: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block94;
                            if (kind > 62) {
                                kind = 62;
                            }
                            this.jjCheckNAdd(36);
                            break;
                        }
                        case 37: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(160, 163);
                            break;
                        }
                        case 38: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(38, 39);
                            break;
                        }
                        case 40: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(40, 41);
                            break;
                        }
                        case 42: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(170, 171);
                            break;
                        }
                        case 47: {
                            if (kind <= 15) break;
                            kind = 15;
                            break;
                        }
                        case 56: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(172, 173);
                            break;
                        }
                        case 61: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(174, 175);
                            break;
                        }
                        case 65: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(27, 28);
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
                block95: do {
                    switch (this.jjstateSet[--i]) {
                        case 14: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(151, 153);
                            break;
                        }
                        case 30: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(148, 150);
                            break;
                        }
                        case 47: {
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block95;
                            kind = 15;
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
            if (i == (startsAt = 71 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x70000L) != 0L) {
                    return 27;
                }
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    return 13;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 1;
                    return 13;
                }
                if ((active0 & 0x10000L) != 0L) {
                    return 25;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x30000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 2;
                    return 13;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x10000000L) != 0L) {
                    return 13;
                }
                if ((active0 & 0x20000000L) != 0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 3;
                    return 13;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_2(int pos, long active0, long active1) {
        return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0, active1), pos + 1);
    }

    private final int jjStartNfaWithStates_2(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '#': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_2(327680L);
            }
            case '(': {
                return this.jjStopAtPos(0, 8);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_2(0x20000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_2(0x10000000L);
            }
            case '{': {
                return this.jjStopAtPos(0, 64);
            }
            case '}': {
                return this.jjStopAtPos(0, 65);
            }
        }
        return this.jjMoveNfa_2(12, 0);
    }

    private final int jjMoveStringLiteralDfa1_2(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(0, active0, 0L);
            return 1;
        }
        switch (this.curChar) {
            case '#': {
                if ((active0 & 0x40000L) == 0L) break;
                return this.jjStopAtPos(1, 18);
            }
            case '*': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_2(1, 16, 25);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_2(active0, 0x20000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_2(active0, 0x10000000L);
            }
        }
        return this.jjStartNfa_2(0, active0, 0L);
    }

    private final int jjMoveStringLiteralDfa2_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(0, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(1, active0, 0L);
            return 2;
        }
        switch (this.curChar) {
            case 'l': {
                return this.jjMoveStringLiteralDfa3_2(active0, 0x20000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_2(active0, 0x10000000L);
            }
        }
        return this.jjStartNfa_2(1, active0, 0L);
    }

    private final int jjMoveStringLiteralDfa3_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(1, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(2, active0, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(3, 28, 13);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_2(active0, 0x20000000L);
            }
        }
        return this.jjStartNfa_2(2, active0, 0L);
    }

    private final int jjMoveStringLiteralDfa4_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(2, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(3, active0, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x20000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(4, 29, 13);
            }
        }
        return this.jjStartNfa_2(3, active0, 0L);
    }

    private final int jjMoveNfa_2(int startState, int curPos) {
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
                                this.jjCheckNAddStates(126, 128);
                                break;
                            }
                            if (this.curChar == '$') {
                                if (kind > 13) {
                                    kind = 13;
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
                            this.jjAddStates(108, 109);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '(' || kind <= 12) continue block42;
                            kind = 12;
                            break;
                        }
                        case 13: {
                            if ((0x3FF200000000000L & l) == 0L) continue block42;
                            if (kind > 62) {
                                kind = 62;
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
                            if (this.curChar != '$' || kind <= 13) continue block42;
                            kind = 13;
                            break;
                        }
                        case 20: {
                            if (this.curChar != '$') break;
                            this.jjCheckNAddTwoStates(21, 22);
                            break;
                        }
                        case 22: {
                            if (this.curChar != '!' || kind <= 14) continue block42;
                            kind = 14;
                            break;
                        }
                        case 23: {
                            if (this.curChar != '$') continue block42;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAddTwoStates(21, 22);
                            break;
                        }
                        case 24: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddStates(126, 128);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 26: {
                            if ((0xFFFFFFF7FFFFFFFFL & l) == 0L || kind <= 15) continue block42;
                            kind = 15;
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
                                if (kind > 62) {
                                    kind = 62;
                                }
                                this.jjCheckNAdd(13);
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(129, 132);
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
                            if (kind > 62) {
                                kind = 62;
                            }
                            this.jjCheckNAdd(13);
                            break;
                        }
                        case 15: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 63) continue block43;
                            kind = 63;
                            break;
                        }
                        case 16: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(129, 132);
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
                            this.jjAddStates(133, 134);
                            break;
                        }
                        case 26: {
                            if (kind <= 15) break;
                            kind = 15;
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
                            if (!ParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block44;
                            kind = 15;
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

    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 101;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(CharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 9 || lexState < 0) {
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
        Token specialToken = null;
        int curPos = 0;
        block17: while (true) {
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
            this.image = null;
            this.jjimageLen = 0;
            while (true) {
                switch (this.curLexState) {
                    case 0: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_0();
                        break;
                    }
                    case 1: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 66) break;
                        this.jjmatchedKind = 66;
                        break;
                    }
                    case 2: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_2();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 66) break;
                        this.jjmatchedKind = 66;
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
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 66) break;
                        this.jjmatchedKind = 66;
                        break;
                    }
                    case 5: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_5();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 67) break;
                        this.jjmatchedKind = 67;
                        break;
                    }
                    case 6: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_6();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 25) break;
                        this.jjmatchedKind = 25;
                        break;
                    }
                    case 7: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_7();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 25) break;
                        this.jjmatchedKind = 25;
                        break;
                    }
                    case 8: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_8();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 25) break;
                        this.jjmatchedKind = 25;
                    }
                }
                if (this.jjmatchedKind == Integer.MAX_VALUE) break block17;
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
                    if (jjnewLexState[this.jjmatchedKind] == -1) continue block17;
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                    continue block17;
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
                catch (IOException e) {
                    // empty catch block
                    break block17;
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
            case 66: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.input_stream.backup(1);
                this.inReference = false;
                if (this.debugPrint) {
                    System.out.print("REF_TERM :");
                }
                this.stateStackPop();
                break;
            }
            case 67: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
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
            case 13: {
                if (this.image == null) {
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen)));
                } else {
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                }
                this.jjimageLen = 0;
                if (this.inComment) break;
                if (this.curLexState == 4) {
                    this.inReference = false;
                    this.stateStackPop();
                }
                this.inReference = true;
                if (this.debugPrint) {
                    System.out.print("$  : going to 4");
                }
                this.stateStackPush();
                this.SwitchTo(4);
                break;
            }
            case 14: {
                if (this.image == null) {
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen)));
                } else {
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                }
                this.jjimageLen = 0;
                if (this.inComment) break;
                if (this.curLexState == 4) {
                    this.inReference = false;
                    this.stateStackPop();
                }
                this.inReference = true;
                if (this.debugPrint) {
                    System.out.print("$!  : going to 4");
                }
                this.stateStackPush();
                this.SwitchTo(4);
                break;
            }
            case 15: {
                if (this.image == null) {
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen)));
                } else {
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                }
                this.jjimageLen = 0;
                if (this.inComment) break;
                this.input_stream.backup(1);
                this.inComment = true;
                this.stateStackPush();
                this.SwitchTo(7);
                break;
            }
            case 16: {
                if (this.image == null) {
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen)));
                } else {
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                }
                this.jjimageLen = 0;
                if (this.inComment) break;
                this.inComment = true;
                this.stateStackPush();
                this.SwitchTo(6);
                break;
            }
            case 17: {
                if (this.image == null) {
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen)));
                } else {
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                }
                this.jjimageLen = 0;
                if (this.inComment) break;
                if (this.curLexState == 4 || this.curLexState == 2) {
                    this.inReference = false;
                    this.stateStackPop();
                }
                this.inDirective = true;
                if (this.debugPrint) {
                    System.out.print("# :  going to 0");
                }
                this.stateStackPush();
                this.SwitchTo(5);
                break;
            }
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 8: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                if (!this.inComment) {
                    ++this.lparen;
                }
                if (this.curLexState != 2) break;
                this.SwitchTo(1);
                break;
            }
            case 9: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.RPARENHandler();
                break;
            }
            case 10: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.SwitchTo(4);
                break;
            }
            case 12: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                if (!this.inComment) {
                    this.inDirective = true;
                    if (this.debugPrint) {
                        System.out.print("#set :  going to 0");
                    }
                    this.stateStackPush();
                    this.inSet = true;
                    this.SwitchTo(0);
                }
                if (this.inComment) break;
                ++this.lparen;
                if (this.curLexState != 2) break;
                this.SwitchTo(1);
                break;
            }
            case 18: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                if (this.inComment) break;
                if (this.curLexState == 4) {
                    this.inReference = false;
                    this.stateStackPop();
                }
                this.inComment = true;
                this.stateStackPush();
                this.SwitchTo(8);
                break;
            }
            case 22: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.inComment = false;
                this.stateStackPop();
                break;
            }
            case 23: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.inComment = false;
                this.stateStackPop();
                break;
            }
            case 24: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.inComment = false;
                this.stateStackPop();
                break;
            }
            case 27: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                if (this.curLexState != 0 || this.inSet || this.lparen != 0) break;
                this.stateStackPop();
                break;
            }
            case 30: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
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
            case 46: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.inDirective = false;
                this.stateStackPop();
                break;
            }
            case 47: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.SwitchTo(0);
                break;
            }
            case 48: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.SwitchTo(0);
                break;
            }
            case 49: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.inDirective = false;
                this.stateStackPop();
                break;
            }
            case 50: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.inDirective = false;
                this.stateStackPop();
                break;
            }
            case 52: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                if (matchedToken.image.endsWith("..")) {
                    this.input_stream.backup(2);
                    matchedToken.image = matchedToken.image.substring(0, matchedToken.image.length() - 2);
                }
                if (this.lparen != 0 || this.inSet || this.curLexState == 1) break;
                this.stateStackPop();
                break;
            }
            case 53: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                if (this.lparen != 0 || this.inSet || this.curLexState == 1) break;
                this.stateStackPop();
                break;
            }
            case 63: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.input_stream.backup(1);
                matchedToken.image = ".";
                if (this.debugPrint) {
                    System.out.print("DOT : switching to 2");
                }
                this.SwitchTo(2);
                break;
            }
            case 65: {
                if (this.image == null) {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image = new StringBuffer(new String(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch)));
                } else {
                    this.lengthOfMatch = this.jjmatchedPos + 1;
                    this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                }
                this.stateStackPop();
                break;
            }
        }
    }
}


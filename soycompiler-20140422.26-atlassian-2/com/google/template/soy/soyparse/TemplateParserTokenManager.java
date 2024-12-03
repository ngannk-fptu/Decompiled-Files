/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soyparse;

import com.google.template.soy.soyparse.SimpleCharStream;
import com.google.template.soy.soyparse.TemplateParserConstants;
import com.google.template.soy.soyparse.Token;
import com.google.template.soy.soyparse.TokenMgrError;
import java.io.IOException;
import java.io.PrintStream;

public class TemplateParserTokenManager
implements TemplateParserConstants {
    private SoyTagDelimiter currSoyTagDelim = SoyTagDelimiter.SINGLE_BRACES;
    private String currCmdName = null;
    private boolean isInLiteralBlock = false;
    private boolean isInMsgBlock = false;
    private boolean isInMsgHtmlTag = false;
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{33, 43, 44, 45, 4, 6, 15, 17, 19, 25, 21, 31, 13, 14, 23, 24, 20, 30, 12, 13, 22, 23};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, "{", "{{", "}", "}}", "@param", null, "sp", "nil", "\\n", "\\r", "\\t", "lb", "rb", "literal", "/literal", "msg", "fallbackmsg", "/msg", "plural", "/plural", "select", "/select", "print", "xid", "css", "let", "/let", "if", "elseif", "else", "/if", "switch", "/switch", "foreach", "ifempty", "/foreach", "for", "/for", null, null, "param", "/param", "log", "/log", "debugger", "case", "default", "namespace", "template", "/template", null, null, "}", "/}", "}}", "/}}", null, null, null, null, null, null, null, "}", "/}", "}}", "/}}", "{", null, "{{", null, "<", ">", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT", "DEFAULT_IN_MSG_BLOCK_AT_SOL", "DEFAULT_NOT_SOL", "DEFAULT_IN_MSG_BLOCK_NOT_SOL", "IN_BLOCK_DOC_COMMENT", "IN_BLOCK_NONDOC_COMMENT", "AFTER_SOY_TAG_OPEN", "AFTER_CMD_NAME_1", "AFTER_CMD_NAME_2", "IN_CMD_TEXT_1", "IN_CMD_TEXT_2", "IN_LITERAL_BLOCK"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, 4, 5, -1, -1, -1, 6, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = new long[]{-288230376151711807L, 8606580735L};
    static final long[] jjtoSkip = new long[]{288230376151711774L, 0L};
    static final long[] jjtoMore = new long[]{32L, 131072L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[46];
    private final int[] jjstateSet = new int[92];
    private final StringBuilder jjimage;
    private StringBuilder image = this.jjimage = new StringBuilder();
    private int jjimageLen;
    private int lengthOfMatch;
    protected char curChar;
    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    int[] jjemptyLineNo = new int[12];
    int[] jjemptyColNo = new int[12];
    boolean[] jjbeenHere = new boolean[12];

    private void handleSpecialCaseCmdsWithoutCmdText(Token matchedToken) {
        if (this.currCmdName == null) {
            return;
        }
        if (this.currCmdName.equals("literal")) {
            this.isInLiteralBlock = true;
        } else if (this.currCmdName.equals("/literal")) {
            TemplateParserTokenManager.throwTokenMgrError("Found '/literal' tag outside of any 'literal' block", matchedToken);
        } else if (this.currCmdName.equals("msg")) {
            TemplateParserTokenManager.throwTokenMgrError("Tag 'msg' must have command text", matchedToken);
        } else if (this.currCmdName.equals("/msg")) {
            if (!this.isInMsgBlock) {
                TemplateParserTokenManager.throwTokenMgrError("Found unmatched '/msg' tag", matchedToken);
            }
            if (this.isInMsgHtmlTag) {
                TemplateParserTokenManager.throwTokenMgrError("Found '/msg' tag while within an HTML tag in a 'msg' block. Please close the HTML tag before ending the 'msg' block", matchedToken);
            }
            this.isInMsgBlock = false;
        } else {
            throw new AssertionError();
        }
    }

    private void handleSpecialCaseCmdsWithCmdText(Token matchedToken) {
        if (this.currCmdName == null) {
            return;
        }
        if (this.currCmdName.equals("literal")) {
            TemplateParserTokenManager.throwTokenMgrError("Tag 'literal' must not have command text", matchedToken);
        } else if (this.currCmdName.equals("/literal")) {
            TemplateParserTokenManager.throwTokenMgrError("Found '/literal' tag outside of any 'literal' block", matchedToken);
        } else if (this.currCmdName.equals("msg")) {
            if (this.isInMsgBlock) {
                TemplateParserTokenManager.throwTokenMgrError("Nested 'msg' tags not allowed", matchedToken);
            }
            this.isInMsgBlock = true;
            this.isInMsgHtmlTag = false;
        } else if (this.currCmdName.equals("/msg")) {
            TemplateParserTokenManager.throwTokenMgrError("Tag '/msg' must not have command text", matchedToken);
        } else {
            throw new AssertionError();
        }
    }

    private void switchToStateDefaultAtSol() {
        if (this.isInMsgBlock) {
            this.SwitchTo(1);
        } else {
            this.SwitchTo(0);
        }
    }

    private void switchToStateDefaultNotSol() {
        if (this.isInMsgBlock) {
            this.SwitchTo(3);
        } else {
            this.SwitchTo(2);
        }
    }

    private void switchToStateDefaultNotSolOrLiteral() {
        if (this.isInLiteralBlock) {
            this.SwitchTo(11);
        } else if (this.isInMsgBlock) {
            this.SwitchTo(3);
        } else {
            this.SwitchTo(2);
        }
    }

    private void switchToStateAfterCmdName() {
        if (this.currSoyTagDelim == SoyTagDelimiter.SINGLE_BRACES) {
            this.SwitchTo(7);
        } else {
            this.SwitchTo(8);
        }
    }

    private void switchToStateInCmdText() {
        if (this.currSoyTagDelim == SoyTagDelimiter.SINGLE_BRACES) {
            this.SwitchTo(9);
        } else {
            this.SwitchTo(10);
        }
    }

    private static void throwTokenMgrError(String msg, Token matchedToken) throws TokenMgrError {
        throw new TokenMgrError(msg + " [line " + matchedToken.beginLine + ", column " + matchedToken.beginColumn + "].", 0);
    }

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_4(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x40L) != 0L) {
                    this.jjmatchedKind = 5;
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_4(int pos, long active0) {
        return this.jjMoveNfa_4(this.jjStopStringLiteralDfa_4(pos, active0), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_4() {
        switch (this.curChar) {
            case '*': {
                return this.jjMoveStringLiteralDfa1_4(64L);
            }
        }
        return this.jjMoveNfa_4(0, 0);
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
            case '/': {
                if ((active0 & 0x40L) == 0L) break;
                return this.jjStopAtPos(1, 6);
            }
        }
        return this.jjStartNfa_4(0, active0);
    }

    private int jjMoveNfa_4(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 2;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block13: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind > 5) {
                                kind = 5;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block13;
                            kind = 97;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '?' || kind <= 97) continue block13;
                            kind = 97;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            kind = 5;
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
                block15: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 5) continue block15;
                            kind = 5;
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
            if (i == (startsAt = 2 - this.jjnewStateCnt)) {
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

    private int jjMoveStringLiteralDfa0_11() {
        return this.jjMoveNfa_11(0, 0);
    }

    private int jjMoveNfa_11(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 46;
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
                        case 0: {
                            if (kind > 81) {
                                kind = 81;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block58;
                            kind = 97;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '?' || kind <= 97) continue block58;
                            kind = 97;
                            break;
                        }
                        case 4: {
                            if ((0x100002600L & l) == 0L || kind <= 83) continue block58;
                            kind = 83;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\n' || kind <= 83) continue block58;
                            kind = 83;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 13: {
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 15: {
                            if ((0x100002600L & l) == 0L || kind <= 84) continue block58;
                            kind = 84;
                            break;
                        }
                        case 16: {
                            if (this.curChar != '\n' || kind <= 84) continue block58;
                            kind = 84;
                            break;
                        }
                        case 17: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 33: {
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 32;
                            break;
                        }
                        case 42: {
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block59: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind > 81) {
                                kind = 81;
                            }
                            if (this.curChar == '{') {
                                this.jjAddStates(0, 3);
                            }
                            if (this.curChar != '{') break;
                            this.jjCheckNAddTwoStates(13, 23);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAddTwoStates(13, 23);
                            break;
                        }
                        case 3: {
                            if (this.curChar != 'l') break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 14: {
                            if (this.curChar != 'l') break;
                            this.jjAddStates(6, 7);
                            break;
                        }
                        case 15: {
                            if (this.curChar != '}' || kind <= 84) continue block59;
                            kind = 84;
                            break;
                        }
                        case 18: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 19: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 21: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 22: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 23: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 24: {
                            if (this.curChar != '{') break;
                            this.jjAddStates(0, 3);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '}' || kind <= 82) continue block59;
                            kind = 82;
                            break;
                        }
                        case 26: {
                            if (this.curChar != 'l') break;
                            this.jjCheckNAdd(25);
                            break;
                        }
                        case 27: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 28: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 29: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 30: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 31: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                        case 32: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 31;
                            break;
                        }
                        case 34: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAdd(25);
                            break;
                        }
                        case 35: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 34;
                            break;
                        }
                        case 36: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 35;
                            break;
                        }
                        case 37: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 38: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 39: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 40: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 39;
                            break;
                        }
                        case 41: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 43: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 44: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAdd(13);
                            break;
                        }
                        case 45: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAdd(23);
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
                        case 0: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 81) continue block60;
                            kind = 81;
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
            if (i == (startsAt = 46 - this.jjnewStateCnt)) {
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
                if ((active0 & 0x1000L) != 0L) {
                    this.jjmatchedKind = 13;
                    this.jjmatchedPos = 0;
                    return 1;
                }
                if ((active0 & 0x50000000000000L) != 0L) {
                    return 11;
                }
                if ((active0 & 0x20000100000000L) != 0L) {
                    return 5;
                }
                if ((active0 & 0x20A29442A400000L) != 0L) {
                    return 19;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x50000000000000L) != 0L) {
                    return 10;
                }
                if ((active0 & 0x20000000000000L) != 0L) {
                    return 4;
                }
                if ((active0 & 0x1000L) != 0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 13;
                        this.jjmatchedPos = 1;
                    }
                    return 26;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x1000L) != 0L) {
                    if (this.jjmatchedPos != 2) {
                        this.jjmatchedKind = 13;
                        this.jjmatchedPos = 2;
                    }
                    return 26;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x1000L) != 0L) {
                    if (this.jjmatchedPos != 3) {
                        this.jjmatchedKind = 13;
                        this.jjmatchedPos = 3;
                    }
                    return 26;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x1000L) != 0L) {
                    this.jjmatchedKind = 13;
                    this.jjmatchedPos = 4;
                    return 26;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x1000L) != 0L) {
                    return 26;
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
            case '/': {
                return this.jjMoveStringLiteralDfa1_6(146975310586314752L);
            }
            case '@': {
                return this.jjMoveStringLiteralDfa1_6(4096L);
            }
            case '\\': {
                return this.jjMoveStringLiteralDfa1_6(458752L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa1_6(0x20000100000000L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_6(0x50000000000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_6(0x3000000000L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_6(0x120001000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_6(0x40800000000L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_6(1125908499398656L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_6(0x800000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_6(0x80000000008000L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa1_6(0x1000044000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa1_6(0x100000L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_6(550024265728L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_6(0x100000000000000L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa1_6(0x80000000L);
            }
        }
        return this.jjMoveNfa_6(0, 0);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa2_6(active0, 0xA1000001000000L);
            }
            case 'b': {
                if ((active0 & 0x80000L) != 0L) {
                    return this.jjStopAtPos(1, 19);
                }
                if ((active0 & 0x100000L) == 0L) break;
                return this.jjStopAtPos(1, 20);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_6(active0, 94575601033150464L);
            }
            case 'f': {
                if ((active0 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 35;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_6(active0, 0x2C0000000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_6(active0, 277027520512L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa2_6(active0, 2252023223287808L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa2_6(active0, 0x2000000L);
            }
            case 'n': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStopAtPos(1, 16);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_6(active0, 1145691116142592L);
            }
            case 'p': {
                if ((active0 & 0x4000L) != 0L) {
                    return this.jjStopAtPos(1, 14);
                }
                return this.jjMoveStringLiteralDfa2_6(active0, 562950087643136L);
            }
            case 'r': {
                if ((active0 & 0x20000L) != 0L) {
                    return this.jjStopAtPos(1, 17);
                }
                return this.jjMoveStringLiteralDfa2_6(active0, 0x40000000L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa2_6(active0, 1104351854592L);
            }
            case 't': {
                if ((active0 & 0x40000L) != 0L) {
                    return this.jjStopAtPos(1, 18);
                }
                return this.jjMoveStringLiteralDfa2_6(active0, 0x200000000000000L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa2_6(active0, 0x8000000000L);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa3_6(active0, 0x2000000001000L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa3_6(active0, 0x10000000000000L);
            }
            case 'd': {
                if ((active0 & 0x80000000L) == 0L) break;
                return this.jjStopAtPos(2, 31);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_6(active0, 0x200040420000000L);
            }
            case 'f': {
                if ((active0 & 0x4000000000L) != 0L) {
                    return this.jjStopAtPos(2, 38);
                }
                return this.jjMoveStringLiteralDfa3_6(active0, 0x40000000000000L);
            }
            case 'g': {
                if ((active0 & 0x800000L) != 0L) {
                    return this.jjStopAtPos(2, 23);
                }
                if ((active0 & 0x4000000000000L) == 0L) break;
                return this.jjStopAtPos(2, 50);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa3_6(active0, 0x8040400000L);
            }
            case 'l': {
                if ((active0 & 0x8000L) != 0L) {
                    return this.jjStopAtPos(2, 15);
                }
                return this.jjMoveStringLiteralDfa3_6(active0, 0x19000000L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa3_6(active0, 0x180000000000000L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_6(active0, 0x8280000000000L);
            }
            case 'r': {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 44;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_6(active0, 0x1020000000000L);
            }
            case 's': {
                if ((active0 & 0x100000000L) != 0L) {
                    return this.jjStopAtPos(2, 32);
                }
                return this.jjMoveStringLiteralDfa3_6(active0, 0x20003002000000L);
            }
            case 't': {
                if ((active0 & 0x200000000L) != 0L) {
                    return this.jjStopAtPos(2, 33);
                }
                return this.jjMoveStringLiteralDfa3_6(active0, 0x200000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_6(active0, 0x4000000L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa3_6(active0, 0x10000000000L);
            }
        }
        return this.jjStartNfa_6(1, active0);
    }

    private int jjMoveStringLiteralDfa3_6(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_6(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa4_6(active0, 0x41000000000000L);
            }
            case 'e': {
                if ((active0 & 0x2000000000L) != 0L) {
                    this.jjmatchedKind = 37;
                    this.jjmatchedPos = 3;
                } else if ((active0 & 0x20000000000000L) != 0L) {
                    return this.jjStopAtPos(3, 53);
                }
                return this.jjMoveStringLiteralDfa4_6(active0, 36031065032228864L);
            }
            case 'g': {
                if ((active0 & 0x2000000L) != 0L) {
                    return this.jjStopAtPos(3, 25);
                }
                if ((active0 & 0x8000000000000L) == 0L) break;
                return this.jjStopAtPos(3, 51);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_6(active0, 0x10000000000L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa4_6(active0, 0x21000000L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa4_6(active0, 0x200040000000000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa4_6(active0, 0x40000000L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa4_6(active0, 0x100000000000000L);
            }
            case 'r': {
                if ((active0 & 0x200000000000L) != 0L) {
                    this.jjmatchedKind = 45;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_6(active0, 571746113556480L);
            }
            case 't': {
                if ((active0 & 0x400000000L) != 0L) {
                    return this.jjStopAtPos(3, 34);
                }
                return this.jjMoveStringLiteralDfa4_6(active0, 0x8000400000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa4_6(active0, 0x10000008000000L);
            }
        }
        return this.jjStartNfa_6(2, active0);
    }

    private int jjMoveStringLiteralDfa4_6(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_6(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa5_6(active0, 565149043789824L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa5_6(active0, 0x1000000L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa5_6(active0, 0x8010000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa5_6(active0, 8796634087424L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa5_6(active0, 0x10000000000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa5_6(active0, 0x1000000000L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa5_6(active0, 0x100000000000000L);
            }
            case 'm': {
                if ((active0 & 0x1000000000000L) == 0L) break;
                return this.jjStopAtPos(4, 48);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_6(active0, 0x200040000000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa5_6(active0, 0x8200000L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa5_6(active0, 0x80000000000000L);
            }
            case 't': {
                if ((active0 & 0x40000000L) != 0L) {
                    return this.jjStopAtPos(4, 30);
                }
                return this.jjMoveStringLiteralDfa5_6(active0, 0x10000000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa5_6(active0, 0x40000000000000L);
            }
        }
        return this.jjStartNfa_6(3, active0);
    }

    private int jjMoveStringLiteralDfa5_6(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_6(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(4, active0);
            return 5;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa6_6(active0, 72066390284042240L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa6_6(active0, 0x30020000000L);
            }
            case 'f': {
                if ((active0 & 0x1000000000L) == 0L) break;
                return this.jjStopAtPos(5, 36);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa6_6(active0, 0x10000000000000L);
            }
            case 'h': {
                if ((active0 & 0x8000000000L) == 0L) break;
                return this.jjStopAtPos(5, 39);
            }
            case 'l': {
                if ((active0 & 0x4000000L) != 0L) {
                    return this.jjStopAtPos(5, 26);
                }
                return this.jjMoveStringLiteralDfa6_6(active0, 0x240000000000000L);
            }
            case 'm': {
                if ((active0 & 0x1000L) != 0L) {
                    return this.jjStartNfaWithStates_6(5, 12, 26);
                }
                if ((active0 & 0x2000000000000L) == 0L) break;
                return this.jjStopAtPos(5, 49);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa6_6(active0, 0x80000000000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa6_6(active0, 0x400000L);
            }
            case 't': {
                if ((active0 & 0x10000000L) != 0L) {
                    return this.jjStopAtPos(5, 28);
                }
                return this.jjMoveStringLiteralDfa6_6(active0, 0x40000000000L);
            }
        }
        return this.jjStartNfa_6(4, active0);
    }

    private int jjMoveStringLiteralDfa6_6(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_6(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(5, active0);
            return 6;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa7_6(active0, 180143985099014144L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa7_6(active0, 0x80001000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa7_6(active0, 0x10000000000000L);
            }
            case 'h': {
                if ((active0 & 0x10000000000L) != 0L) {
                    return this.jjStopAtPos(6, 40);
                }
                if ((active0 & 0x20000000000L) == 0L) break;
                return this.jjStopAtPos(6, 41);
            }
            case 'l': {
                if ((active0 & 0x200000L) != 0L) {
                    return this.jjStopAtPos(6, 21);
                }
                if ((active0 & 0x8000000L) == 0L) break;
                return this.jjStopAtPos(6, 27);
            }
            case 't': {
                if ((active0 & 0x20000000L) != 0L) {
                    return this.jjStopAtPos(6, 29);
                }
                if ((active0 & 0x40000000000000L) != 0L) {
                    return this.jjStopAtPos(6, 54);
                }
                return this.jjMoveStringLiteralDfa7_6(active0, 0x100000000000000L);
            }
            case 'y': {
                if ((active0 & 0x40000000000L) == 0L) break;
                return this.jjStopAtPos(6, 42);
            }
        }
        return this.jjStartNfa_6(5, active0);
    }

    private int jjMoveStringLiteralDfa7_6(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_6(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(6, active0);
            return 7;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa8_6(active0, 0x80000000000000L);
            }
            case 'e': {
                if ((active0 & 0x100000000000000L) == 0L) break;
                return this.jjStopAtPos(7, 56);
            }
            case 'h': {
                if ((active0 & 0x80000000000L) == 0L) break;
                return this.jjStopAtPos(7, 43);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa8_6(active0, 0x1000000L);
            }
            case 'l': {
                if ((active0 & 0x400000L) == 0L) break;
                return this.jjStopAtPos(7, 22);
            }
            case 'r': {
                if ((active0 & 0x10000000000000L) == 0L) break;
                return this.jjStopAtPos(7, 52);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa8_6(active0, 0x200000000000000L);
            }
        }
        return this.jjStartNfa_6(6, active0);
    }

    private int jjMoveStringLiteralDfa8_6(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_6(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(7, active0);
            return 8;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x80000000000000L) != 0L) {
                    return this.jjStopAtPos(8, 55);
                }
                if ((active0 & 0x200000000000000L) == 0L) break;
                return this.jjStopAtPos(8, 57);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa9_6(active0, 0x1000000L);
            }
        }
        return this.jjStartNfa_6(7, active0);
    }

    private int jjMoveStringLiteralDfa9_6(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_6(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(8, active0);
            return 9;
        }
        switch (this.curChar) {
            case 's': {
                return this.jjMoveStringLiteralDfa10_6(active0, 0x1000000L);
            }
        }
        return this.jjStartNfa_6(8, active0);
    }

    private int jjMoveStringLiteralDfa10_6(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_6(8, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(9, active0);
            return 10;
        }
        switch (this.curChar) {
            case 'g': {
                if ((active0 & 0x1000000L) == 0L) break;
                return this.jjStopAtPos(10, 24);
            }
        }
        return this.jjStartNfa_6(9, active0);
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
        this.jjnewStateCnt = 26;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block36: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (this.curChar == '/') {
                                this.jjAddStates(8, 9);
                                break;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block36;
                            kind = 97;
                            break;
                        }
                        case 2: 
                        case 26: {
                            if ((0x3FF000000000000L & l) == 0L) continue block36;
                            kind = 13;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 14: {
                            if (this.curChar != '?') break;
                            kind = 97;
                            break;
                        }
                        case 15: {
                            if (this.curChar != '/') break;
                            this.jjAddStates(8, 9);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block37: do {
                    switch (this.jjstateSet[--i]) {
                        case 19: {
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 24;
                                break;
                            }
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 0: {
                            if ((0x2800000000000000L & l) != 0L) {
                                if (kind <= 59) break;
                                kind = 59;
                                break;
                            }
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                break;
                            }
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 5;
                                break;
                            }
                            if (this.curChar != '@') break;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 26: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 13) {
                                    kind = 13;
                                }
                                this.jjCheckNAddTwoStates(1, 2);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block37;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block37;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 3: {
                            if (this.curChar != 'l' || kind <= 46) continue block37;
                            kind = 46;
                            break;
                        }
                        case 4: 
                        case 7: {
                            if (this.curChar != 'l') break;
                            this.jjCheckNAdd(3);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 13: {
                            if ((0x2800000000000000L & l) == 0L) break;
                            kind = 59;
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'l' || kind <= 47) continue block37;
                            kind = 47;
                            break;
                        }
                        case 17: 
                        case 20: {
                            if (this.curChar != 'l') break;
                            this.jjCheckNAdd(16);
                            break;
                        }
                        case 18: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 21: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 22: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 23: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 24: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 25: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
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
                do {
                    switch (this.jjstateSet[--i]) {
                        default: 
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
            if (i == (startsAt = 26 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_3(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x18L) != 0L) {
                    this.jjmatchedKind = 87;
                    return -1;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x18L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 87;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_3(int pos, long active0, long active1) {
        return this.jjMoveNfa_3(this.jjStopStringLiteralDfa_3(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_3() {
        switch (this.curChar) {
            case '/': {
                return this.jjMoveStringLiteralDfa1_3(24L);
            }
            case '<': {
                return this.jjStopAtPos(0, 79);
            }
            case '>': {
                return this.jjStopAtPos(0, 80);
            }
            case '{': {
                this.jjmatchedKind = 8;
                return this.jjMoveStringLiteralDfa1_3(512L);
            }
            case '}': {
                this.jjmatchedKind = 10;
                return this.jjMoveStringLiteralDfa1_3(2048L);
            }
        }
        return this.jjMoveNfa_3(0, 0);
    }

    private int jjMoveStringLiteralDfa1_3(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(0, active0, 0L);
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x10L) != 0L) {
                    this.jjmatchedKind = 4;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_3(active0, 8L);
            }
            case '{': {
                if ((active0 & 0x200L) == 0L) break;
                return this.jjStopAtPos(1, 9);
            }
            case '}': {
                if ((active0 & 0x800L) == 0L) break;
                return this.jjStopAtPos(1, 11);
            }
        }
        return this.jjStartNfa_3(0, active0, 0L);
    }

    private int jjMoveStringLiteralDfa2_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(0, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(1, active0, 0L);
            return 2;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 8L) == 0L) break;
                return this.jjStopAtPos(2, 3);
            }
        }
        return this.jjStartNfa_3(1, active0, 0L);
    }

    private int jjMoveNfa_3(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 9;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) != 0L) {
                                if (kind > 87) {
                                    kind = 87;
                                }
                            } else if ((0x100000200L & l) != 0L) {
                                if (kind > 86) {
                                    kind = 86;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                            } else if ((0x2400L & l) != 0L && kind > 85) {
                                kind = 85;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                break;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block22;
                            kind = 97;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '\n' || kind <= 85) continue block22;
                            kind = 85;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 87) continue block22;
                            kind = 87;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '?' || kind <= 97) continue block22;
                            kind = 97;
                            break;
                        }
                        case 5: {
                            if ((0x100000200L & l) == 0L) continue block22;
                            if (kind > 86) {
                                kind = 86;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '/') continue block22;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(7);
                            break;
                        }
                        case 7: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) continue block22;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(7);
                            break;
                        }
                        case 8: {
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind <= 87) break;
                            kind = 87;
                            break;
                        }
                        case 7: {
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
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
                block24: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 87) continue block24;
                            kind = 87;
                            break;
                        }
                        case 7: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block24;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
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
            if (i == (startsAt = 9 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_10(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active1 & 0x400L) != 0L) {
                    this.jjmatchedKind = 70;
                    return -1;
                }
                if ((active1 & 0x2000L) != 0L) {
                    this.jjmatchedKind = 70;
                    return 5;
                }
                if ((active1 & 0x200L) != 0L) {
                    this.jjmatchedKind = 70;
                    return 7;
                }
                return -1;
            }
            case 1: {
                if ((active1 & 0x400L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 70;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                if ((active1 & 0x200L) != 0L) {
                    return 4;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_10(int pos, long active0, long active1) {
        return this.jjMoveNfa_10(this.jjStopStringLiteralDfa_10(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_10() {
        switch (this.curChar) {
            case '/': {
                return this.jjMoveStringLiteralDfa1_10(1024L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_10(8192L);
            }
            case '}': {
                return this.jjMoveStringLiteralDfa1_10(512L);
            }
        }
        return this.jjMoveNfa_10(0, 0);
    }

    private int jjMoveStringLiteralDfa1_10(long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_10(0, 0L, active1);
            return 1;
        }
        switch (this.curChar) {
            case '{': {
                if ((active1 & 0x2000L) == 0L) break;
                return this.jjStopAtPos(1, 77);
            }
            case '}': {
                if ((active1 & 0x200L) != 0L) {
                    return this.jjStartNfaWithStates_10(1, 73, 4);
                }
                return this.jjMoveStringLiteralDfa2_10(active1, 1024L);
            }
        }
        return this.jjStartNfa_10(0, 0L, active1);
    }

    private int jjMoveStringLiteralDfa2_10(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_10(0, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_10(1, 0L, active1);
            return 2;
        }
        switch (this.curChar) {
            case '}': {
                if ((active1 & 0x400L) == 0L) break;
                return this.jjStopAtPos(2, 74);
            }
        }
        return this.jjStartNfa_10(1, 0L, active1);
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
        this.jjnewStateCnt = 32;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block45: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind > 70) {
                                kind = 70;
                            }
                            if ((0x100002600L & l) != 0L) {
                                this.jjAddStates(10, 11);
                                break;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block45;
                            kind = 97;
                            break;
                        }
                        case 2: {
                            if ((0x3FF000000000000L & l) == 0L) continue block45;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 3: {
                            if (kind <= 70) break;
                            kind = 70;
                            break;
                        }
                        case 9: {
                            if (this.curChar != '?' || kind <= 97) continue block45;
                            kind = 97;
                            break;
                        }
                        case 10: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(10, 11);
                            break;
                        }
                        case 11: {
                            if (this.curChar != '\"') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 13: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjAddStates(12, 13);
                            break;
                        }
                        case 14: {
                            if (this.curChar != '\"' || kind <= 67) continue block45;
                            kind = 67;
                            break;
                        }
                        case 15: {
                            if (this.curChar != '=') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 22: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(23, 24);
                            break;
                        }
                        case 23: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(23, 24);
                            break;
                        }
                        case 24: {
                            if (this.curChar != '\"' || kind <= 68) continue block45;
                            kind = 68;
                            break;
                        }
                        case 25: {
                            if (this.curChar != '=') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block46: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind > 70) {
                                kind = 70;
                            }
                            if (this.curChar == '}') {
                                this.jjstateSet[this.jjnewStateCnt++] = 7;
                                break;
                            }
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 5;
                                break;
                            }
                            if (this.curChar != '|') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: 
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block46;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 3: {
                            if (kind <= 70) break;
                            kind = 70;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '}' || kind <= 78) continue block46;
                            kind = 78;
                            break;
                        }
                        case 5: 
                        case 7: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAdd(4);
                            break;
                        }
                        case 6: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '}') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 12: 
                        case 13: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(13, 14);
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 18: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 19: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 21: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 23: {
                            this.jjAddStates(14, 15);
                            break;
                        }
                        case 26: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 27: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 28: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 29: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 30: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 31: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
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
                block47: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 70) continue block47;
                            kind = 70;
                            break;
                        }
                        case 23: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(14, 15);
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
            if (i == (startsAt = 32 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_8(int pos, long active0, long active1) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_8(int pos, long active0, long active1) {
        return this.jjMoveNfa_8(this.jjStopStringLiteralDfa_8(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_8() {
        switch (this.curChar) {
            case '/': {
                return this.jjMoveStringLiteralDfa1_8(Long.MIN_VALUE);
            }
            case '}': {
                return this.jjMoveStringLiteralDfa1_8(0x4000000000000000L);
            }
        }
        return this.jjMoveNfa_8(0, 0);
    }

    private int jjMoveStringLiteralDfa1_8(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(0, active0, 0L);
            return 1;
        }
        switch (this.curChar) {
            case '}': {
                if ((active0 & 0x4000000000000000L) != 0L) {
                    return this.jjStopAtPos(1, 62);
                }
                return this.jjMoveStringLiteralDfa2_8(active0, Long.MIN_VALUE);
            }
        }
        return this.jjStartNfa_8(0, active0, 0L);
    }

    private int jjMoveStringLiteralDfa2_8(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_8(0, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(1, active0, 0L);
            return 2;
        }
        switch (this.curChar) {
            case '}': {
                if ((active0 & Long.MIN_VALUE) == 0L) break;
                return this.jjStopAtPos(2, 63);
            }
        }
        return this.jjStartNfa_8(1, active0, 0L);
    }

    private int jjMoveNfa_8(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block13: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 64) {
                                    kind = 64;
                                }
                            } else if (this.curChar == '?' && kind > 97) {
                                kind = 97;
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '\n' || kind <= 64) continue block13;
                            kind = 64;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '?') break;
                            kind = 97;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        default: 
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 0x3F);
                int i2 = (this.curChar & 0xFF) >> 6;
                long l2 = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        default: 
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
            if (i == (startsAt = 4 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_9(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active1 & 0x80L) != 0L) {
                    return 4;
                }
                if ((active1 & 0x100L) != 0L) {
                    this.jjmatchedKind = 69;
                    return 6;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_9(int pos, long active0, long active1) {
        return this.jjMoveNfa_9(this.jjStopStringLiteralDfa_9(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_9() {
        switch (this.curChar) {
            case '/': {
                return this.jjMoveStringLiteralDfa1_9(256L);
            }
            case '{': {
                return this.jjStopAtPos(0, 75);
            }
            case '}': {
                return this.jjStartNfaWithStates_9(0, 71, 4);
            }
        }
        return this.jjMoveNfa_9(0, 0);
    }

    private int jjMoveStringLiteralDfa1_9(long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_9(0, 0L, active1);
            return 1;
        }
        switch (this.curChar) {
            case '}': {
                if ((active1 & 0x100L) == 0L) break;
                return this.jjStartNfaWithStates_9(1, 72, 4);
            }
        }
        return this.jjStartNfa_9(0, 0L, active1);
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
        this.jjnewStateCnt = 31;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block44: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind > 69) {
                                kind = 69;
                            }
                            if ((0x100002600L & l) != 0L) {
                                this.jjAddStates(16, 17);
                                break;
                            }
                            if (this.curChar == '/') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                                break;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block44;
                            kind = 97;
                            break;
                        }
                        case 2: {
                            if ((0x3FF000000000000L & l) == 0L) continue block44;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 3: {
                            if (kind <= 69) break;
                            kind = 69;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '?' || kind <= 97) continue block44;
                            kind = 97;
                            break;
                        }
                        case 9: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(16, 17);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '\"') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 12: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjAddStates(18, 19);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '\"' || kind <= 67) continue block44;
                            kind = 67;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '=') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 21: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(22, 23);
                            break;
                        }
                        case 22: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(22, 23);
                            break;
                        }
                        case 23: {
                            if (this.curChar != '\"' || kind <= 68) continue block44;
                            kind = 68;
                            break;
                        }
                        case 24: {
                            if (this.curChar != '=') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block45: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0L) {
                                if (kind > 69) {
                                    kind = 69;
                                }
                            } else if (this.curChar == '}') {
                                this.jjCheckNAdd(4);
                            }
                            if (this.curChar != '|') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: 
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block45;
                            if (kind > 66) {
                                kind = 66;
                            }
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 3: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L || kind <= 69) continue block45;
                            kind = 69;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '}' || kind <= 76) continue block45;
                            kind = 76;
                            break;
                        }
                        case 5: 
                        case 6: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAdd(4);
                            break;
                        }
                        case 11: 
                        case 12: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 15: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 18: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 19: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 22: {
                            this.jjAddStates(20, 21);
                            break;
                        }
                        case 25: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
                            break;
                        }
                        case 26: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 27: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 28: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 29: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 30: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
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
                block46: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 69) continue block46;
                            kind = 69;
                            break;
                        }
                        case 22: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(20, 21);
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
            if (i == (startsAt = 31 - this.jjnewStateCnt)) {
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
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_7(int pos, long active0) {
        return this.jjMoveNfa_7(this.jjStopStringLiteralDfa_7(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_7() {
        switch (this.curChar) {
            case '/': {
                return this.jjMoveStringLiteralDfa1_7(0x2000000000000000L);
            }
            case '}': {
                return this.jjStopAtPos(0, 60);
            }
        }
        return this.jjMoveNfa_7(0, 0);
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
            case '}': {
                if ((active0 & 0x2000000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 61);
            }
        }
        return this.jjStartNfa_7(0, active0);
    }

    private int jjMoveNfa_7(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block13: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 64) {
                                    kind = 64;
                                }
                            } else if (this.curChar == '?' && kind > 97) {
                                kind = 97;
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '\n' || kind <= 64) continue block13;
                            kind = 64;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '?') break;
                            kind = 97;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        default: 
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 0x3F);
                int i2 = (this.curChar & 0xFF) >> 6;
                long l2 = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        default: 
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
            if (i == (startsAt = 4 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x18L) != 0L) {
                    this.jjmatchedKind = 87;
                    return 0;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '/': {
                return this.jjMoveStringLiteralDfa1_0(24L);
            }
            case '{': {
                this.jjmatchedKind = 8;
                return this.jjMoveStringLiteralDfa1_0(512L);
            }
            case '}': {
                this.jjmatchedKind = 10;
                return this.jjMoveStringLiteralDfa1_0(2048L);
            }
        }
        return this.jjMoveNfa_0(3, 0);
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
            case '*': {
                if ((active0 & 0x10L) != 0L) {
                    this.jjmatchedKind = 4;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 8L);
            }
            case '{': {
                if ((active0 & 0x200L) == 0L) break;
                return this.jjStopAtPos(1, 9);
            }
            case '}': {
                if ((active0 & 0x800L) == 0L) break;
                return this.jjStopAtPos(1, 11);
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
            case '*': {
                if ((active0 & 8L) == 0L) break;
                return this.jjStopAtPos(2, 3);
            }
        }
        return this.jjStartNfa_0(1, active0);
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 9;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) != 0L) {
                                if (kind > 87) {
                                    kind = 87;
                                }
                            } else if ((0x100000200L & l) != 0L) {
                                if (kind > 86) {
                                    kind = 86;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                            } else if ((0x2400L & l) != 0L && kind > 85) {
                                kind = 85;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                                break;
                            }
                            if (this.curChar == '/') {
                                this.jjstateSet[this.jjnewStateCnt++] = 0;
                                break;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block22;
                            kind = 97;
                            break;
                        }
                        case 0: {
                            if (this.curChar != '/') continue block22;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) continue block22;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '\n' || kind <= 85) continue block22;
                            kind = 85;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 87) continue block22;
                            kind = 87;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '?' || kind <= 97) continue block22;
                            kind = 97;
                            break;
                        }
                        case 8: {
                            if ((0x100000200L & l) == 0L) continue block22;
                            if (kind > 86) {
                                kind = 86;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (kind <= 87) break;
                            kind = 87;
                            break;
                        }
                        case 1: {
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
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
                block24: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 87) continue block24;
                            kind = 87;
                            break;
                        }
                        case 1: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block24;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
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
            if (i == (startsAt = 9 - this.jjnewStateCnt)) {
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
                if ((active0 & 0x18L) != 0L) {
                    this.jjmatchedKind = 87;
                    return -1;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x18L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 87;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
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
            case '/': {
                return this.jjMoveStringLiteralDfa1_2(24L);
            }
            case '{': {
                this.jjmatchedKind = 8;
                return this.jjMoveStringLiteralDfa1_2(512L);
            }
            case '}': {
                this.jjmatchedKind = 10;
                return this.jjMoveStringLiteralDfa1_2(2048L);
            }
        }
        return this.jjMoveNfa_2(0, 0);
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
                if ((active0 & 0x10L) != 0L) {
                    this.jjmatchedKind = 4;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_2(active0, 8L);
            }
            case '{': {
                if ((active0 & 0x200L) == 0L) break;
                return this.jjStopAtPos(1, 9);
            }
            case '}': {
                if ((active0 & 0x800L) == 0L) break;
                return this.jjStopAtPos(1, 11);
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
            case '*': {
                if ((active0 & 8L) == 0L) break;
                return this.jjStopAtPos(2, 3);
            }
        }
        return this.jjStartNfa_2(1, active0);
    }

    private int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 9;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) != 0L) {
                                if (kind > 87) {
                                    kind = 87;
                                }
                            } else if ((0x100000200L & l) != 0L) {
                                if (kind > 86) {
                                    kind = 86;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                            } else if ((0x2400L & l) != 0L && kind > 85) {
                                kind = 85;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                break;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block22;
                            kind = 97;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '\n' || kind <= 85) continue block22;
                            kind = 85;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 87) continue block22;
                            kind = 87;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '?' || kind <= 97) continue block22;
                            kind = 97;
                            break;
                        }
                        case 5: {
                            if ((0x100000200L & l) == 0L) continue block22;
                            if (kind > 86) {
                                kind = 86;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '/') continue block22;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(7);
                            break;
                        }
                        case 7: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) continue block22;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(7);
                            break;
                        }
                        case 8: {
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind <= 87) break;
                            kind = 87;
                            break;
                        }
                        case 7: {
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
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
                block24: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 87) continue block24;
                            kind = 87;
                            break;
                        }
                        case 7: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block24;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
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
            if (i == (startsAt = 9 - this.jjnewStateCnt)) {
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
                if ((active0 & 0x18L) != 0L) {
                    this.jjmatchedKind = 87;
                    return 0;
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
            case '/': {
                return this.jjMoveStringLiteralDfa1_1(24L);
            }
            case '<': {
                return this.jjStopAtPos(0, 79);
            }
            case '>': {
                return this.jjStopAtPos(0, 80);
            }
            case '{': {
                this.jjmatchedKind = 8;
                return this.jjMoveStringLiteralDfa1_1(512L);
            }
            case '}': {
                this.jjmatchedKind = 10;
                return this.jjMoveStringLiteralDfa1_1(2048L);
            }
        }
        return this.jjMoveNfa_1(3, 0);
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
                if ((active0 & 0x10L) != 0L) {
                    this.jjmatchedKind = 4;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 8L);
            }
            case '{': {
                if ((active0 & 0x200L) == 0L) break;
                return this.jjStopAtPos(1, 9);
            }
            case '}': {
                if ((active0 & 0x800L) == 0L) break;
                return this.jjStopAtPos(1, 11);
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
            case '*': {
                if ((active0 & 8L) == 0L) break;
                return this.jjStopAtPos(2, 3);
            }
        }
        return this.jjStartNfa_1(1, active0, 0L);
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 9;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) != 0L) {
                                if (kind > 87) {
                                    kind = 87;
                                }
                            } else if ((0x100000200L & l) != 0L) {
                                if (kind > 86) {
                                    kind = 86;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                            } else if ((0x2400L & l) != 0L && kind > 85) {
                                kind = 85;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                                break;
                            }
                            if (this.curChar == '/') {
                                this.jjstateSet[this.jjnewStateCnt++] = 0;
                                break;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block22;
                            kind = 97;
                            break;
                        }
                        case 0: {
                            if (this.curChar != '/') continue block22;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) continue block22;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '\n' || kind <= 85) continue block22;
                            kind = 85;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 87) continue block22;
                            kind = 87;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '?' || kind <= 97) continue block22;
                            kind = 97;
                            break;
                        }
                        case 8: {
                            if ((0x100000200L & l) == 0L) continue block22;
                            if (kind > 86) {
                                kind = 86;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (kind <= 87) break;
                            kind = 87;
                            break;
                        }
                        case 1: {
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
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
                block24: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 87) continue block24;
                            kind = 87;
                            break;
                        }
                        case 1: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block24;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
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
            if (i == (startsAt = 9 - this.jjnewStateCnt)) {
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
                if ((active0 & 0x80L) != 0L) {
                    this.jjmatchedKind = 5;
                    return -1;
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
            case '*': {
                return this.jjMoveStringLiteralDfa1_5(128L);
            }
        }
        return this.jjMoveNfa_5(0, 0);
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
            case '/': {
                if ((active0 & 0x80L) == 0L) break;
                return this.jjStopAtPos(1, 7);
            }
        }
        return this.jjStartNfa_5(0, active0);
    }

    private int jjMoveNfa_5(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 2;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block13: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind > 5) {
                                kind = 5;
                            }
                            if (this.curChar != '?' || kind <= 97) continue block13;
                            kind = 97;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '?' || kind <= 97) continue block13;
                            kind = 97;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            kind = 5;
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
                block15: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!TemplateParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 5) continue block15;
                            kind = 5;
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
            if (i == (startsAt = 2 - this.jjnewStateCnt)) {
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

    public TemplateParserTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public TemplateParserTokenManager(SimpleCharStream stream, int lexState) {
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
        int i = 46;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
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
        int beginColumn;
        int endColumn;
        int beginLine;
        int endLine;
        String curTokenImage;
        if (this.jjmatchedPos < 0) {
            curTokenImage = this.image == null ? "" : this.image.toString();
            beginLine = endLine = this.input_stream.getBeginLine();
            beginColumn = endColumn = this.input_stream.getBeginColumn();
        } else {
            String im = jjstrLiteralImages[this.jjmatchedKind];
            curTokenImage = im == null ? this.input_stream.GetImage() : im;
            beginLine = this.input_stream.getBeginLine();
            beginColumn = this.input_stream.getBeginColumn();
            endLine = this.input_stream.getEndLine();
            endColumn = this.input_stream.getEndColumn();
        }
        Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    public Token getNextToken() {
        int curPos = 0;
        block20: while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException e) {
                this.jjmatchedKind = 0;
                Token matchedToken = this.jjFillToken();
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
                        break;
                    }
                    case 1: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_1();
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
                        this.jjmatchedKind = 58;
                        this.jjmatchedPos = -1;
                        curPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_6();
                        break;
                    }
                    case 7: {
                        this.jjmatchedKind = 65;
                        this.jjmatchedPos = -1;
                        curPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_7();
                        break;
                    }
                    case 8: {
                        this.jjmatchedKind = 65;
                        this.jjmatchedPos = -1;
                        curPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_8();
                        break;
                    }
                    case 9: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_9();
                        break;
                    }
                    case 10: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_10();
                        break;
                    }
                    case 11: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_11();
                    }
                }
                if (this.jjmatchedKind == Integer.MAX_VALUE) break block20;
                if (this.jjmatchedPos + 1 < curPos) {
                    this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                }
                if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                    Token matchedToken = this.jjFillToken();
                    this.TokenLexicalActions(matchedToken);
                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }
                    return matchedToken;
                }
                if ((jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                    this.SkipLexicalActions(null);
                    if (jjnewLexState[this.jjmatchedKind] == -1) continue block20;
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                    continue block20;
                }
                this.jjimageLen += this.jjmatchedPos + 1;
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
            case 1: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.switchToStateDefaultNotSol();
                break;
            }
            case 58: {
                if (this.jjmatchedPos == -1) {
                    if (this.jjbeenHere[6] && this.jjemptyLineNo[6] == this.input_stream.getBeginLine() && this.jjemptyColNo[6] == this.input_stream.getBeginColumn()) {
                        throw new TokenMgrError("Error: Bailing out of infinite loop caused by repeated empty string matches at line " + this.input_stream.getBeginLine() + ", column " + this.input_stream.getBeginColumn() + ".", 3);
                    }
                    this.jjemptyLineNo[6] = this.input_stream.getBeginLine();
                    this.jjemptyColNo[6] = this.input_stream.getBeginColumn();
                    this.jjbeenHere[6] = true;
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.switchToStateInCmdText();
                break;
            }
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 6: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                matchedToken.image = this.image.substring(0, this.image.length() - 2).trim();
                this.switchToStateDefaultNotSol();
                break;
            }
            case 7: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                matchedToken.image = this.image.substring(0, this.image.length() - 2).trim();
                this.switchToStateDefaultNotSol();
                break;
            }
            case 8: {
                this.image.append(jjstrLiteralImages[8]);
                this.lengthOfMatch = jjstrLiteralImages[8].length();
                this.currSoyTagDelim = SoyTagDelimiter.SINGLE_BRACES;
                break;
            }
            case 9: {
                this.image.append(jjstrLiteralImages[9]);
                this.lengthOfMatch = jjstrLiteralImages[9].length();
                this.currSoyTagDelim = SoyTagDelimiter.DOUBLE_BRACES;
                break;
            }
            case 10: {
                this.image.append(jjstrLiteralImages[10]);
                this.lengthOfMatch = jjstrLiteralImages[10].length();
                TemplateParserTokenManager.throwTokenMgrError("Unmatched right brace '}'", matchedToken);
                break;
            }
            case 11: {
                this.image.append(jjstrLiteralImages[11]);
                this.lengthOfMatch = jjstrLiteralImages[11].length();
                TemplateParserTokenManager.throwTokenMgrError("Unmatched double right brace '}}'", matchedToken);
                break;
            }
            case 12: {
                this.image.append(jjstrLiteralImages[12]);
                this.lengthOfMatch = jjstrLiteralImages[12].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 13: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                TemplateParserTokenManager.throwTokenMgrError("Invalid declaration '" + matchedToken.image + "'", matchedToken);
                break;
            }
            case 14: {
                this.image.append(jjstrLiteralImages[14]);
                this.lengthOfMatch = jjstrLiteralImages[14].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 15: {
                this.image.append(jjstrLiteralImages[15]);
                this.lengthOfMatch = jjstrLiteralImages[15].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 16: {
                this.image.append(jjstrLiteralImages[16]);
                this.lengthOfMatch = jjstrLiteralImages[16].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 17: {
                this.image.append(jjstrLiteralImages[17]);
                this.lengthOfMatch = jjstrLiteralImages[17].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 18: {
                this.image.append(jjstrLiteralImages[18]);
                this.lengthOfMatch = jjstrLiteralImages[18].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 19: {
                this.image.append(jjstrLiteralImages[19]);
                this.lengthOfMatch = jjstrLiteralImages[19].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 20: {
                this.image.append(jjstrLiteralImages[20]);
                this.lengthOfMatch = jjstrLiteralImages[20].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 21: {
                this.image.append(jjstrLiteralImages[21]);
                this.lengthOfMatch = jjstrLiteralImages[21].length();
                this.currCmdName = "literal";
                this.switchToStateAfterCmdName();
                break;
            }
            case 22: {
                this.image.append(jjstrLiteralImages[22]);
                this.lengthOfMatch = jjstrLiteralImages[22].length();
                this.currCmdName = "/literal";
                this.switchToStateAfterCmdName();
                break;
            }
            case 23: {
                this.image.append(jjstrLiteralImages[23]);
                this.lengthOfMatch = jjstrLiteralImages[23].length();
                this.currCmdName = "msg";
                this.switchToStateAfterCmdName();
                break;
            }
            case 24: {
                this.image.append(jjstrLiteralImages[24]);
                this.lengthOfMatch = jjstrLiteralImages[24].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 25: {
                this.image.append(jjstrLiteralImages[25]);
                this.lengthOfMatch = jjstrLiteralImages[25].length();
                this.currCmdName = "/msg";
                this.switchToStateAfterCmdName();
                break;
            }
            case 26: {
                this.image.append(jjstrLiteralImages[26]);
                this.lengthOfMatch = jjstrLiteralImages[26].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 27: {
                this.image.append(jjstrLiteralImages[27]);
                this.lengthOfMatch = jjstrLiteralImages[27].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 28: {
                this.image.append(jjstrLiteralImages[28]);
                this.lengthOfMatch = jjstrLiteralImages[28].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 29: {
                this.image.append(jjstrLiteralImages[29]);
                this.lengthOfMatch = jjstrLiteralImages[29].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 30: {
                this.image.append(jjstrLiteralImages[30]);
                this.lengthOfMatch = jjstrLiteralImages[30].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 31: {
                this.image.append(jjstrLiteralImages[31]);
                this.lengthOfMatch = jjstrLiteralImages[31].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 32: {
                this.image.append(jjstrLiteralImages[32]);
                this.lengthOfMatch = jjstrLiteralImages[32].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 33: {
                this.image.append(jjstrLiteralImages[33]);
                this.lengthOfMatch = jjstrLiteralImages[33].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 34: {
                this.image.append(jjstrLiteralImages[34]);
                this.lengthOfMatch = jjstrLiteralImages[34].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 35: {
                this.image.append(jjstrLiteralImages[35]);
                this.lengthOfMatch = jjstrLiteralImages[35].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 36: {
                this.image.append(jjstrLiteralImages[36]);
                this.lengthOfMatch = jjstrLiteralImages[36].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 37: {
                this.image.append(jjstrLiteralImages[37]);
                this.lengthOfMatch = jjstrLiteralImages[37].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 38: {
                this.image.append(jjstrLiteralImages[38]);
                this.lengthOfMatch = jjstrLiteralImages[38].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 39: {
                this.image.append(jjstrLiteralImages[39]);
                this.lengthOfMatch = jjstrLiteralImages[39].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 40: {
                this.image.append(jjstrLiteralImages[40]);
                this.lengthOfMatch = jjstrLiteralImages[40].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 41: {
                this.image.append(jjstrLiteralImages[41]);
                this.lengthOfMatch = jjstrLiteralImages[41].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 42: {
                this.image.append(jjstrLiteralImages[42]);
                this.lengthOfMatch = jjstrLiteralImages[42].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 43: {
                this.image.append(jjstrLiteralImages[43]);
                this.lengthOfMatch = jjstrLiteralImages[43].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 44: {
                this.image.append(jjstrLiteralImages[44]);
                this.lengthOfMatch = jjstrLiteralImages[44].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 45: {
                this.image.append(jjstrLiteralImages[45]);
                this.lengthOfMatch = jjstrLiteralImages[45].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 46: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.switchToStateAfterCmdName();
                break;
            }
            case 47: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.switchToStateAfterCmdName();
                break;
            }
            case 48: {
                this.image.append(jjstrLiteralImages[48]);
                this.lengthOfMatch = jjstrLiteralImages[48].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 49: {
                this.image.append(jjstrLiteralImages[49]);
                this.lengthOfMatch = jjstrLiteralImages[49].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 50: {
                this.image.append(jjstrLiteralImages[50]);
                this.lengthOfMatch = jjstrLiteralImages[50].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 51: {
                this.image.append(jjstrLiteralImages[51]);
                this.lengthOfMatch = jjstrLiteralImages[51].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 52: {
                this.image.append(jjstrLiteralImages[52]);
                this.lengthOfMatch = jjstrLiteralImages[52].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 53: {
                this.image.append(jjstrLiteralImages[53]);
                this.lengthOfMatch = jjstrLiteralImages[53].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 54: {
                this.image.append(jjstrLiteralImages[54]);
                this.lengthOfMatch = jjstrLiteralImages[54].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 55: {
                this.image.append(jjstrLiteralImages[55]);
                this.lengthOfMatch = jjstrLiteralImages[55].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 56: {
                this.image.append(jjstrLiteralImages[56]);
                this.lengthOfMatch = jjstrLiteralImages[56].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 57: {
                this.image.append(jjstrLiteralImages[57]);
                this.lengthOfMatch = jjstrLiteralImages[57].length();
                this.switchToStateAfterCmdName();
                break;
            }
            case 59: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                TemplateParserTokenManager.throwTokenMgrError("First character in a Soy tag must not be a brace character (consider inserting a space before the brace character)", matchedToken);
                break;
            }
            case 60: {
                this.image.append(jjstrLiteralImages[60]);
                this.lengthOfMatch = jjstrLiteralImages[60].length();
                this.handleSpecialCaseCmdsWithoutCmdText(matchedToken);
                this.currCmdName = null;
                this.switchToStateDefaultNotSolOrLiteral();
                break;
            }
            case 61: {
                this.image.append(jjstrLiteralImages[61]);
                this.lengthOfMatch = jjstrLiteralImages[61].length();
                this.handleSpecialCaseCmdsWithoutCmdText(matchedToken);
                this.currCmdName = null;
                this.switchToStateDefaultNotSolOrLiteral();
                break;
            }
            case 62: {
                this.image.append(jjstrLiteralImages[62]);
                this.lengthOfMatch = jjstrLiteralImages[62].length();
                this.handleSpecialCaseCmdsWithoutCmdText(matchedToken);
                this.currCmdName = null;
                this.switchToStateDefaultNotSolOrLiteral();
                break;
            }
            case 63: {
                this.image.append(jjstrLiteralImages[63]);
                this.lengthOfMatch = jjstrLiteralImages[63].length();
                this.handleSpecialCaseCmdsWithoutCmdText(matchedToken);
                this.currCmdName = null;
                this.switchToStateDefaultNotSolOrLiteral();
                break;
            }
            case 64: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.handleSpecialCaseCmdsWithCmdText(matchedToken);
                this.switchToStateInCmdText();
                break;
            }
            case 65: {
                if (this.jjmatchedPos == -1) {
                    if (this.jjbeenHere[7] && this.jjemptyLineNo[7] == this.input_stream.getBeginLine() && this.jjemptyColNo[7] == this.input_stream.getBeginColumn()) {
                        throw new TokenMgrError("Error: Bailing out of infinite loop caused by repeated empty string matches at line " + this.input_stream.getBeginLine() + ", column " + this.input_stream.getBeginColumn() + ".", 3);
                    }
                    this.jjemptyLineNo[7] = this.input_stream.getBeginLine();
                    this.jjemptyColNo[7] = this.input_stream.getBeginColumn();
                    this.jjbeenHere[7] = true;
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.switchToStateInCmdText();
                break;
            }
            case 68: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                TemplateParserTokenManager.throwTokenMgrError("Found 'phname' attribute that is not a valid identifier (" + matchedToken.image + ")", matchedToken);
                break;
            }
            case 71: {
                this.image.append(jjstrLiteralImages[71]);
                this.lengthOfMatch = jjstrLiteralImages[71].length();
                this.currCmdName = null;
                this.switchToStateDefaultNotSol();
                break;
            }
            case 72: {
                this.image.append(jjstrLiteralImages[72]);
                this.lengthOfMatch = jjstrLiteralImages[72].length();
                this.currCmdName = null;
                this.switchToStateDefaultNotSol();
                break;
            }
            case 73: {
                this.image.append(jjstrLiteralImages[73]);
                this.lengthOfMatch = jjstrLiteralImages[73].length();
                this.currCmdName = null;
                this.switchToStateDefaultNotSol();
                break;
            }
            case 74: {
                this.image.append(jjstrLiteralImages[74]);
                this.lengthOfMatch = jjstrLiteralImages[74].length();
                this.currCmdName = null;
                this.switchToStateDefaultNotSol();
                break;
            }
            case 75: {
                this.image.append(jjstrLiteralImages[75]);
                this.lengthOfMatch = jjstrLiteralImages[75].length();
                TemplateParserTokenManager.throwTokenMgrError("Left brace '{' not allowed within a Soy tag delimited by single braces (consider using double braces to delimit the Soy tag)", matchedToken);
                break;
            }
            case 76: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                TemplateParserTokenManager.throwTokenMgrError("Found Soy tag opened by '{' but closed by '}}' (please use consistent delimiters)", matchedToken);
                break;
            }
            case 77: {
                this.image.append(jjstrLiteralImages[77]);
                this.lengthOfMatch = jjstrLiteralImages[77].length();
                TemplateParserTokenManager.throwTokenMgrError("Double left brace '{{' not allowed within a Soy tag delimited by double braces (consider inserting a space: '{ {')", matchedToken);
                break;
            }
            case 78: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                TemplateParserTokenManager.throwTokenMgrError("Last character in a Soy tag must not be a brace character (consider inserting a space after the brace character)", matchedToken);
                break;
            }
            case 79: {
                this.image.append(jjstrLiteralImages[79]);
                this.lengthOfMatch = jjstrLiteralImages[79].length();
                if (this.isInMsgHtmlTag) {
                    TemplateParserTokenManager.throwTokenMgrError("In a 'msg' block, found '<' within HTML tag", matchedToken);
                }
                this.isInMsgHtmlTag = true;
                break;
            }
            case 80: {
                this.image.append(jjstrLiteralImages[80]);
                this.lengthOfMatch = jjstrLiteralImages[80].length();
                if (!this.isInMsgHtmlTag) {
                    TemplateParserTokenManager.throwTokenMgrError("In a 'msg' block, found '>' while not within HTML tag", matchedToken);
                }
                this.isInMsgHtmlTag = false;
                break;
            }
            case 82: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.isInLiteralBlock = false;
                matchedToken.image = this.image.substring(0, this.image.length() - this.lengthOfMatch);
                this.switchToStateDefaultNotSol();
                break;
            }
            case 83: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                TemplateParserTokenManager.throwTokenMgrError("Tag '/literal' must not have command text", matchedToken);
                break;
            }
            case 84: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                TemplateParserTokenManager.throwTokenMgrError("Nested 'literal' tags not allowed", matchedToken);
                break;
            }
            case 85: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.switchToStateDefaultAtSol();
                break;
            }
            case 86: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.switchToStateDefaultNotSol();
                break;
            }
            case 87: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.switchToStateDefaultNotSol();
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

    private static enum SoyTagDelimiter {
        SINGLE_BRACES,
        DOUBLE_BRACES;

    }
}


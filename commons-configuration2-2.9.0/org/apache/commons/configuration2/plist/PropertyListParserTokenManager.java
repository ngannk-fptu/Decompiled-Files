/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.plist;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.configuration2.plist.PropertyListParserConstants;
import org.apache.commons.configuration2.plist.SimpleCharStream;
import org.apache.commons.configuration2.plist.Token;
import org.apache.commons.configuration2.plist.TokenMgrError;

public class PropertyListParserTokenManager
implements PropertyListParserConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{10, 12, 13};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, "(", ")", ",", "{", "}", ";", "=", "<", ">", "<*D", "\"", null, null, null, null, null, null, null, "\\\""};
    public static final String[] lexStateNames = new String[]{"DEFAULT", "IN_COMMENT", "IN_SINGLE_LINE_COMMENT"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, 1, -1, 0, 2, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = new long[]{1044379649L};
    static final long[] jjtoSkip = new long[]{670L};
    static final long[] jjtoSpecial = new long[]{512L};
    static final long[] jjtoMore = new long[]{1376L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[14];
    private final int[] jjstateSet = new int[28];
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

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x20000120L) != 0L) {
                    this.jjmatchedKind = 27;
                    return 8;
                }
                if ((active0 & 0x80000L) != 0L) {
                    return 8;
                }
                if ((active0 & 0x200000L) != 0L) {
                    return 14;
                }
                if ((active0 & 0x140000L) != 0L) {
                    return 6;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x100000L) != 0L) {
                    this.jjmatchedKind = 27;
                    this.jjmatchedPos = 1;
                    return 3;
                }
                if ((active0 & 0x120L) != 0L) {
                    return 8;
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
            case '\"': {
                return this.jjStartNfaWithStates_0(0, 21, 14);
            }
            case '(': {
                return this.jjStopAtPos(0, 11);
            }
            case ')': {
                return this.jjStopAtPos(0, 12);
            }
            case ',': {
                return this.jjStopAtPos(0, 13);
            }
            case '/': {
                return this.jjMoveStringLiteralDfa1_0(288L);
            }
            case ';': {
                return this.jjStopAtPos(0, 16);
            }
            case '<': {
                this.jjmatchedKind = 18;
                return this.jjMoveStringLiteralDfa1_0(0x100000L);
            }
            case '=': {
                return this.jjStopAtPos(0, 17);
            }
            case '>': {
                return this.jjStartNfaWithStates_0(0, 19, 8);
            }
            case '\\': {
                return this.jjMoveStringLiteralDfa1_0(0x20000000L);
            }
            case '{': {
                return this.jjStopAtPos(0, 14);
            }
            case '}': {
                return this.jjStopAtPos(0, 15);
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
            case '\"': {
                if ((active0 & 0x20000000L) == 0L) break;
                return this.jjStopAtPos(1, 29);
            }
            case '*': {
                if ((active0 & 0x20L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 5, 8);
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x100000L);
            }
            case '/': {
                if ((active0 & 0x100L) == 0L) break;
                return this.jjStartNfaWithStates_0(1, 8, 8);
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
            case 'D': {
                if ((active0 & 0x100000L) == 0L) break;
                return this.jjStartNfaWithStates_0(2, 20, 15);
            }
        }
        return this.jjStartNfa_0(1, active0);
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
        this.jjnewStateCnt = 14;
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
                        case 15: {
                            if ((0xD7FFECFAFFFFD9FFL & l) != 0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if ((0x7FF280100000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(4, 5);
                                break;
                            }
                            if (this.curChar != '>' || kind <= 26) continue block36;
                            kind = 26;
                            break;
                        }
                        case 6: {
                            if ((0xD7FFECFAFFFFD9FFL & l) != 0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if ((0x3FF000100002600L & l) != 0L) {
                                this.jjCheckNAddTwoStates(1, 2);
                                break;
                            }
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                                break;
                            }
                            if (this.curChar != '>' || kind <= 25) continue block36;
                            kind = 25;
                            break;
                        }
                        case 14: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            if (this.curChar != '\"' || kind <= 28) continue block36;
                            kind = 28;
                            break;
                        }
                        case 3: 
                        case 8: {
                            if ((0xD7FFECFAFFFFD9FFL & l) == 0L) continue block36;
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            break;
                        }
                        case 0: {
                            if ((0xD7FFECFAFFFFD9FFL & l) != 0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            } else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                            }
                            if (this.curChar != '<') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 1: {
                            if ((0x3FF000100002600L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '>' || kind <= 25) continue block36;
                            kind = 25;
                            break;
                        }
                        case 4: {
                            if ((0x7FF280100000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '>' || kind <= 26) continue block36;
                            kind = 26;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '<') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 9: 
                        case 11: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 10: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '\"' || kind <= 28) continue block36;
                            kind = 28;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block37: do {
                    switch (this.jjstateSet[--i]) {
                        case 15: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if (this.curChar != 'Z') break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 6: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 14: {
                            this.jjCheckNAddStates(0, 2);
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 3: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if (this.curChar != 'D') break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 0: 
                        case 8: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) continue block37;
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            break;
                        }
                        case 1: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 4: {
                            if (this.curChar != 'Z') break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 10: {
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 12: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else {
                int i2 = (this.curChar & 0xFF) >> 6;
                long l2 = 1L << (this.curChar & 0x3F);
                block38: do {
                    switch (this.jjstateSet[--i]) {
                        case 8: 
                        case 15: {
                            if ((jjbitVec0[i2] & l2) == 0L) continue block38;
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            break;
                        }
                        case 6: {
                            if ((jjbitVec0[i2] & l2) == 0L) continue block38;
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            break;
                        }
                        case 10: 
                        case 14: {
                            if ((jjbitVec0[i2] & l2) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 3: {
                            if ((jjbitVec0[i2] & l2) == 0L) continue block38;
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            break;
                        }
                        case 0: {
                            if ((jjbitVec0[i2] & l2) == 0L) continue block38;
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
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
            if (i == (startsAt = 14 - this.jjnewStateCnt)) {
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

    private int jjMoveStringLiteralDfa0_2() {
        return this.jjMoveNfa_2(0, 0);
    }

    private int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 3;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block12: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x2400L & l) != 0L && kind > 9) {
                                kind = 9;
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '\n' || kind <= 9) continue block12;
                            kind = 9;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
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
            if (i == (startsAt = 3 - this.jjnewStateCnt)) {
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

    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '*': {
                return this.jjMoveStringLiteralDfa1_1(128L);
            }
        }
        return 1;
    }

    private int jjMoveStringLiteralDfa1_1(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return 1;
        }
        switch (this.curChar) {
            case '/': {
                if ((active0 & 0x80L) == 0L) break;
                return this.jjStopAtPos(1, 7);
            }
            default: {
                return 2;
            }
        }
        return 2;
    }

    public PropertyListParserTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public PropertyListParserTokenManager(SimpleCharStream stream, int lexState) {
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
        int i = 14;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 3 || lexState < 0) {
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
        Token specialToken = null;
        int curPos = 0;
        block13: while (true) {
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
                        try {
                            this.input_stream.backup(0);
                            while (this.curChar <= ' ' && (0x100002600L & 1L << this.curChar) != 0L) {
                                this.curChar = this.input_stream.BeginToken();
                            }
                        }
                        catch (IOException e1) {
                            continue block13;
                        }
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_0();
                        break;
                    }
                    case 1: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 6) break;
                        this.jjmatchedKind = 6;
                        break;
                    }
                    case 2: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_2();
                        if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 10) break;
                        this.jjmatchedKind = 10;
                    }
                }
                if (this.jjmatchedKind == Integer.MAX_VALUE) break block13;
                if (this.jjmatchedPos + 1 < curPos) {
                    this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                }
                if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                    matchedToken = this.jjFillToken();
                    matchedToken.specialToken = specialToken;
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
                    if (jjnewLexState[this.jjmatchedKind] == -1) continue block13;
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                    continue block13;
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
                catch (IOException e1) {
                    // empty catch block
                    break block13;
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
}


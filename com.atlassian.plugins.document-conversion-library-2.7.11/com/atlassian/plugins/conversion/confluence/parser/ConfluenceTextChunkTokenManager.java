/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.parser.ConfluenceTextChunkConstants;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import com.atlassian.plugins.conversion.confluence.parser.TokenMgrError;
import java.io.IOException;
import java.io.PrintStream;

public class ConfluenceTextChunkTokenManager
implements ConfluenceTextChunkConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final long[] jjbitVec3 = new long[]{0L, -16384L, -17590038560769L, 0x7FFFFFL};
    static final long[] jjbitVec4 = new long[]{0L, 0L, 0x400040000000000L, -36028797027352577L};
    static final long[] jjbitVec5 = new long[]{-1L, -1L, -1L, -270215977642229761L};
    static final long[] jjbitVec6 = new long[]{0xFFFFFFL, -65536L, 0x1FFFFFFFFFFL, 0L};
    static final long[] jjbitVec7 = new long[]{0L, 0L, -17179879616L, 4503588160110591L};
    static final long[] jjbitVec8 = new long[]{-8194L, -536936449L, -65533L, 234134404065073567L};
    static final long[] jjbitVec9 = new long[]{-562949953421312L, -8581545985L, -4900197869555810049L, 1979120929931270L};
    static final long[] jjbitVec10 = new long[]{576460743713488896L, -281474976186369L, 0x7CFFFFFFFFFFFFFFL, 68032818806783L};
    static final long[] jjbitVec11 = new long[]{-4323455642275676178L, 68703174655L, -4339783389948567570L, 844492307446175L};
    static final long[] jjbitVec12 = new long[]{-4364553187899111452L, 4503601204443527L, -2022681381666312210L, 4295048127L};
    static final long[] jjbitVec13 = new long[]{-4337531590134882322L, 15837706639L, -4341532606274353172L, 15815L};
    static final long[] jjbitVec14 = new long[]{0xFFDFFFFFDDFEEL, 0L, -4327961440926441492L, 13958659551L};
    static final long[] jjbitVec15 = new long[]{-4323457841299070996L, 12884917711L, 0L, 0L};
    static final long[] jjbitVec16 = new long[]{0x7FFFFFFFFFFFFFEL, 0xFFFFFFFL, 4323293666156225942L, 805322591L};
    static final long[] jjbitVec17 = new long[]{-4422534834027495423L, -558551906910465L, 215680200883507167L, 0L};
    static final long[] jjbitVec18 = new long[]{0L, 0L, -4294967296L, 36028797018898495L};
    static final long[] jjbitVec19 = new long[]{-1L, -1L, -4026531841L, 0x3FFFFFFFFFFFFFFL};
    static final long[] jjbitVec20 = new long[]{-3233808385L, 0x3FFFFFFFAAFF3F3FL, 0x1FDFFFFFFFFFFFFFL, 2295745090394464220L};
    static final long[] jjbitVec21 = new long[]{0L, Long.MIN_VALUE, 0L, 0L};
    static final long[] jjbitVec22 = new long[]{0L, -2L, -8186232833L, 1765411053929234431L};
    static final long[] jjbitVec23 = new long[]{35184372088800L, 0L, 0L, 0L};
    static final long[] jjbitVec24 = new long[]{-1L, -1L, 0x3FFFFFFFFFL, 0L};
    static final long[] jjbitVec25 = new long[]{-1L, -1L, 0xFFFFFFFFFL, 0L};
    static final long[] jjbitVec26 = new long[]{-844432446455424L, 16383L, 0xFFF00000000L, -16777216L};
    static final long[] jjbitVec27 = new long[]{0L, 0L, -288234774198222849L, 0x80000000800000L};
    static final long[] jjbitVec28 = new long[]{0L, 0L, 0L, 0x3C0000000000000L};
    static final long[] jjbitVec29 = new long[]{-16777216L, 65535L, -2199023255552L, -1L};
    static final long[] jjbitVec30 = new long[]{-1L, -1L, 17179879615L, -4503588160110592L};
    static final long[] jjbitVec31 = new long[]{8193L, 0x20010000L, 65532L, -234134404065073568L};
    static final long[] jjbitVec32 = new long[]{0x1FFFFFFFFFFFFL, 8581545984L, 0x4400FFFFFFFFFF00L, -1979120929931271L};
    static final long[] jjbitVec33 = new long[]{-576460743713488897L, 0xFFFFFFF80000L, -9007199254740992000L, -68032818806784L};
    static final long[] jjbitVec34 = new long[]{4323455642275676177L, -68703174656L, 4339783389948567569L, -844492307446176L};
    static final long[] jjbitVec35 = new long[]{4364553187899111451L, -4503601204443528L, 2022681381666312209L, -4295048128L};
    static final long[] jjbitVec36 = new long[]{4337531590134882321L, -15837706640L, 4341532606274353171L, -15816L};
    static final long[] jjbitVec37 = new long[]{-4501400603975663L, -1L, 4327961440926441491L, -13958659552L};
    static final long[] jjbitVec38 = new long[]{4323457841299070995L, -12884917712L, -1L, -1L};
    static final long[] jjbitVec39 = new long[]{-576460752303423487L, -268435456L, -4323293666156225943L, -805322592L};
    static final long[] jjbitVec40 = new long[]{4422534834027495422L, 558551906910464L, -215680200883507168L, -1L};
    static final long[] jjbitVec41 = new long[]{-1L, -1L, 0xFFFFFFFFL, -36028797018898496L};
    static final long[] jjbitVec42 = new long[]{0L, 0L, 0xF0000000L, -288230376151711744L};
    static final long[] jjbitVec43 = new long[]{0xC0C00000L, -4611686017001275200L, -2296835809958952960L, -2295745090394464221L};
    static final long[] jjbitVec44 = new long[]{-1L, Long.MAX_VALUE, -1L, -1L};
    static final long[] jjbitVec45 = new long[]{-1L, 1L, 8186232832L, -1765411053929234432L};
    static final long[] jjbitVec46 = new long[]{-35184372088801L, -1L, -1L, -1L};
    static final long[] jjbitVec47 = new long[]{0L, 0L, -274877906944L, -1L};
    static final long[] jjbitVec48 = new long[]{0L, 0L, -68719476736L, -1L};
    static final int[] jjnextStates = new int[]{2, 5, 1, 2, 29, 31, 32, 33, 37, 39, 43, 44, 46, 48, 50, 54, 8, 9, 34, 35, 29, 31, 33};
    public static final String[] jjstrLiteralImages = new String[]{"", null, "{*}", "{_}", "{??}", "{-}", "{+}", "{^}", "{~}", "{{{}", "{}}}", null, null, null, null, null, null, null, null, " -- ", " --- ", null, null, null, null, null, null, null, null, null, null, null, "\\\\", null, null, "\\*", "*", null, "{*}", null, null, null, null, "_", "{_}", null, "\\_", null, null, null, "??", "{??}", null, "\\?", null, null, null, "-", "{-}", null, "\\-", null, null, null, "+", "{+}", null, "\\+", null, null, null, "^", "{^}", null, "\\^", null, null, null, "~", "{~}", null, "\\~", null, null, null, "}}", "{}}}", null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT", "INBOLD", "INEMPHASIS", "INCITATION", "INSTRIKE", "INUNDERLINE", "INSUPER", "INSUB", "INMONO", "INSIDEMACRO"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[55];
    private final int[] jjstateSet = new int[110];
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

    private final int jjStopStringLiteralDfa_8(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active1 & 0x600000L) != 0L) {
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active1 & 0x600000L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 2: {
                if ((active1 & 0x400000L) != 0L) {
                    this.jjmatchedKind = 84;
                    this.jjmatchedPos = 2;
                    return 4;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_8(int pos, long active0, long active1) {
        return this.jjMoveNfa_8(this.jjStopStringLiteralDfa_8(pos, active0, active1), pos + 1);
    }

    private final int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
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
            case '{': {
                return this.jjMoveStringLiteralDfa1_8(0x400000L);
            }
            case '}': {
                return this.jjMoveStringLiteralDfa1_8(0x200000L);
            }
        }
        return this.jjMoveNfa_8(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_8(long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(0, 0L, active1);
            return 1;
        }
        switch (this.curChar) {
            case '}': {
                if ((active1 & 0x200000L) != 0L) {
                    return this.jjStartNfaWithStates_8(1, 85, 1);
                }
                return this.jjMoveStringLiteralDfa2_8(active1, 0x400000L);
            }
        }
        return this.jjStartNfa_8(0, 0L, active1);
    }

    private final int jjMoveStringLiteralDfa2_8(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_8(0, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(1, 0L, active1);
            return 2;
        }
        switch (this.curChar) {
            case '}': {
                return this.jjMoveStringLiteralDfa3_8(active1, 0x400000L);
            }
        }
        return this.jjStartNfa_8(1, 0L, active1);
    }

    private final int jjMoveStringLiteralDfa3_8(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_8(1, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(2, 0L, active1);
            return 3;
        }
        switch (this.curChar) {
            case '}': {
                if ((active1 & 0x400000L) == 0L) break;
                return this.jjStopAtPos(3, 86);
            }
        }
        return this.jjStartNfa_8(2, 0L, active1);
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

    private final int jjMoveNfa_8(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 6;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & l) == 0L || kind <= 87) continue block19;
                            kind = 87;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (this.curChar == '}') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                            }
                            if (this.curChar != '}' || kind <= 84) continue block20;
                            kind = 84;
                            break;
                        }
                        case 2: {
                            if (this.curChar == '}') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            if (this.curChar != '}') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 0: {
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 3: {
                            if (this.curChar != '}') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 4: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 87) continue block20;
                            kind = 87;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '}') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
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
                block21: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 87) continue block21;
                            kind = 87;
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
            if (i == (startsAt = 6 - this.jjnewStateCnt)) {
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
                if ((active0 & 0x5000000000L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x4000000000L) != 0L) {
                    this.jjmatchedKind = 37;
                    this.jjmatchedPos = 1;
                    return 3;
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
            case '*': {
                return this.jjStartNfaWithStates_1(0, 36, 1);
            }
            case '\\': {
                return this.jjMoveStringLiteralDfa1_1(0x800000000L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_1(0x4000000000L);
            }
        }
        return this.jjMoveNfa_1(0, 0);
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
            case '*': {
                if ((active0 & 0x800000000L) != 0L) {
                    return this.jjStopAtPos(1, 35);
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 0x4000000000L);
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
            case '}': {
                if ((active0 & 0x4000000000L) == 0L) break;
                return this.jjStopAtPos(2, 38);
            }
        }
        return this.jjStartNfa_1(1, active0);
    }

    private final int jjMoveNfa_1(int startState, int curPos) {
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
                block17: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            if (this.curChar != '*' || kind <= 37) continue block17;
                            kind = 37;
                            break;
                        }
                        case 0: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0x3FF000000000000L & l) == 0L || kind <= 39) continue block17;
                            kind = 39;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 39) continue block18;
                            kind = 39;
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
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 39) continue block19;
                            kind = 39;
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

    private final int jjStopStringLiteralDfa_2(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x180000000000L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 42;
                    this.jjmatchedPos = 1;
                    return 3;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_2(int pos, long active0) {
        return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0), pos + 1);
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
            case '\\': {
                return this.jjMoveStringLiteralDfa1_2(0x400000000000L);
            }
            case '_': {
                return this.jjStartNfaWithStates_2(0, 43, 1);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_2(0x100000000000L);
            }
        }
        return this.jjMoveNfa_2(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_2(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '_': {
                if ((active0 & 0x400000000000L) != 0L) {
                    return this.jjStopAtPos(1, 46);
                }
                return this.jjMoveStringLiteralDfa2_2(active0, 0x100000000000L);
            }
        }
        return this.jjStartNfa_2(0, active0);
    }

    private final int jjMoveStringLiteralDfa2_2(long old0, long active0) {
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
            case '}': {
                if ((active0 & 0x100000000000L) == 0L) break;
                return this.jjStopAtPos(2, 44);
            }
        }
        return this.jjStartNfa_2(1, active0);
    }

    private final int jjMoveNfa_2(int startState, int curPos) {
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
                block17: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if ((0x3FF000000000000L & l) == 0L || kind <= 45) continue block17;
                            kind = 45;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (this.curChar == '_') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            if (this.curChar != '_' || kind <= 42) continue block18;
                            kind = 42;
                            break;
                        }
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '_') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 45) continue block18;
                            kind = 45;
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
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 45) continue block19;
                            kind = 45;
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

    private final int jjStopStringLiteralDfa_5(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active1 & 3L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 1: {
                if ((active1 & 2L) != 0L) {
                    this.jjmatchedKind = 63;
                    this.jjmatchedPos = 1;
                    return 3;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_5(int pos, long active0, long active1) {
        return this.jjMoveNfa_5(this.jjStopStringLiteralDfa_5(pos, active0, active1), pos + 1);
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
            case '+': {
                return this.jjStartNfaWithStates_5(0, 64, 1);
            }
            case '\\': {
                return this.jjMoveStringLiteralDfa1_5(8L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_5(2L);
            }
        }
        return this.jjMoveNfa_5(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_5(long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_5(0, 0L, active1);
            return 1;
        }
        switch (this.curChar) {
            case '+': {
                if ((active1 & 8L) != 0L) {
                    return this.jjStopAtPos(1, 67);
                }
                return this.jjMoveStringLiteralDfa2_5(active1, 2L);
            }
        }
        return this.jjStartNfa_5(0, 0L, active1);
    }

    private final int jjMoveStringLiteralDfa2_5(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_5(0, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_5(1, 0L, active1);
            return 2;
        }
        switch (this.curChar) {
            case '}': {
                if ((active1 & 2L) == 0L) break;
                return this.jjStopAtPos(2, 65);
            }
        }
        return this.jjStartNfa_5(1, 0L, active1);
    }

    private final int jjMoveNfa_5(int startState, int curPos) {
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
                block17: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (this.curChar == '+') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            if (this.curChar != '+' || kind <= 63) continue block17;
                            kind = 63;
                            break;
                        }
                        case 0: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '+') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0x3FF000000000000L & l) == 0L || kind <= 66) continue block17;
                            kind = 66;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 66) continue block18;
                            kind = 66;
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
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 66) continue block19;
                            kind = 66;
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
                if ((active0 & 0x100000000L) != 0L) {
                    return 1;
                }
                if ((active0 & 0x180000L) != 0L) {
                    return 55;
                }
                if ((active0 & 0x7FCL) != 0L) {
                    return 25;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x100000000L) != 0L) {
                    return 2;
                }
                if ((active0 & 0x200L) != 0L) {
                    return 26;
                }
                if ((active0 & 0x1FCL) != 0L) {
                    return 56;
                }
                if ((active0 & 0x180000L) != 0L) {
                    return 45;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x10L) != 0L) {
                    return 56;
                }
                if ((active0 & 0x180000L) != 0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 2;
                    return -1;
                }
                if ((active0 & 0x200L) != 0L) {
                    this.jjmatchedKind = 29;
                    this.jjmatchedPos = 2;
                    return -1;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x180000L) != 0L) {
                    if (this.jjmatchedPos < 2) {
                        this.jjmatchedKind = 22;
                        this.jjmatchedPos = 2;
                    }
                    return -1;
                }
                if ((active0 & 0x200L) != 0L) {
                    if (this.jjmatchedPos < 2) {
                        this.jjmatchedKind = 29;
                        this.jjmatchedPos = 2;
                    }
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
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
            case ' ': {
                return this.jjMoveStringLiteralDfa1_0(0x180000L);
            }
            case '\\': {
                return this.jjMoveStringLiteralDfa1_0(0x100000000L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_0(2044L);
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
            case '*': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4L);
            }
            case '+': {
                return this.jjMoveStringLiteralDfa2_0(active0, 64L);
            }
            case '-': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1572896L);
            }
            case '?': {
                return this.jjMoveStringLiteralDfa2_0(active0, 16L);
            }
            case '\\': {
                if ((active0 & 0x100000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(1, 32, 2);
            }
            case '^': {
                return this.jjMoveStringLiteralDfa2_0(active0, 128L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa2_0(active0, 8L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa2_0(active0, 512L);
            }
            case '}': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1024L);
            }
            case '~': {
                return this.jjMoveStringLiteralDfa2_0(active0, 256L);
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
            case '-': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x180000L);
            }
            case '?': {
                return this.jjMoveStringLiteralDfa3_0(active0, 16L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa3_0(active0, 512L);
            }
            case '}': {
                if ((active0 & 4L) != 0L) {
                    return this.jjStopAtPos(2, 2);
                }
                if ((active0 & 8L) != 0L) {
                    return this.jjStopAtPos(2, 3);
                }
                if ((active0 & 0x20L) != 0L) {
                    return this.jjStopAtPos(2, 5);
                }
                if ((active0 & 0x40L) != 0L) {
                    return this.jjStopAtPos(2, 6);
                }
                if ((active0 & 0x80L) != 0L) {
                    return this.jjStopAtPos(2, 7);
                }
                if ((active0 & 0x100L) != 0L) {
                    return this.jjStopAtPos(2, 8);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 1024L);
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
            case ' ': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStopAtPos(3, 19);
            }
            case '-': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x100000L);
            }
            case '}': {
                if ((active0 & 0x10L) != 0L) {
                    return this.jjStopAtPos(3, 4);
                }
                if ((active0 & 0x200L) != 0L) {
                    return this.jjStopAtPos(3, 9);
                }
                if ((active0 & 0x400L) == 0L) break;
                return this.jjStopAtPos(3, 10);
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
            case ' ': {
                if ((active0 & 0x100000L) == 0L) break;
                return this.jjStopAtPos(4, 20);
            }
        }
        return this.jjStartNfa_0(3, active0);
    }

    private final int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 55;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block114: do {
                    switch (this.jjstateSet[--i]) {
                        case 25: 
                        case 31: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) break;
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 55: {
                            if (this.curChar == '+') {
                                this.jjstateSet[this.jjnewStateCnt++] = 47;
                                break;
                            }
                            if (this.curChar == '-') {
                                this.jjstateSet[this.jjnewStateCnt++] = 45;
                                break;
                            }
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 38;
                                break;
                            }
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 56: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) break;
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 1: {
                            if ((0x80002C0200000000L & l) != 0L && kind > 1) {
                                kind = 1;
                            }
                            if (this.curChar == '+') {
                                this.jjstateSet[this.jjnewStateCnt++] = 47;
                                break;
                            }
                            if (this.curChar == '-') {
                                this.jjstateSet[this.jjnewStateCnt++] = 45;
                                break;
                            }
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 38;
                                break;
                            }
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 0: {
                            if ((0x7C00D3FDFFFFFFFFL & l) != 0L) {
                                this.jjAddStates(8, 15);
                                break;
                            }
                            if (this.curChar == '+') {
                                this.jjstateSet[this.jjnewStateCnt++] = 20;
                                break;
                            }
                            if (this.curChar == '-') {
                                this.jjstateSet[this.jjnewStateCnt++] = 18;
                                break;
                            }
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                break;
                            }
                            if (this.curChar == '!') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                                break;
                            }
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 2: {
                            if ((0x80002C0200000000L & l) == 0L || kind <= 1) continue block114;
                            kind = 1;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '!') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 4: {
                            if ((0xFFFFFFFCFFFFDBFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(5, 6);
                            break;
                        }
                        case 5: {
                            if ((0xFFFFFFFDFFFFDBFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(5, 6);
                            break;
                        }
                        case 6: {
                            if (this.curChar != '!' || kind <= 11) continue block114;
                            kind = 11;
                            break;
                        }
                        case 8: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) break;
                            this.jjAddStates(16, 17);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 11: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 13) continue block114;
                            kind = 13;
                            break;
                        }
                        case 13: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 15) continue block114;
                            kind = 15;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 15: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 17) continue block114;
                            kind = 17;
                            break;
                        }
                        case 16: {
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 17: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 18: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 21) continue block114;
                            kind = 21;
                            break;
                        }
                        case 19: {
                            if (this.curChar != '+') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 20: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 23) continue block114;
                            kind = 23;
                            break;
                        }
                        case 22: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 25) continue block114;
                            kind = 25;
                            break;
                        }
                        case 24: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 27) continue block114;
                            kind = 27;
                            break;
                        }
                        case 26: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 29) continue block114;
                            kind = 29;
                            break;
                        }
                        case 30: {
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 36: {
                            if ((0x7C00D3FDFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(8, 15);
                            break;
                        }
                        case 37: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 38: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 14) continue block114;
                            kind = 14;
                            break;
                        }
                        case 40: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 16) continue block114;
                            kind = 16;
                            break;
                        }
                        case 41: {
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 42: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 18) continue block114;
                            kind = 18;
                            break;
                        }
                        case 43: {
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 44: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 45;
                            break;
                        }
                        case 45: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 22) continue block114;
                            kind = 22;
                            break;
                        }
                        case 46: {
                            if (this.curChar != '+') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 47;
                            break;
                        }
                        case 47: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 24) continue block114;
                            kind = 24;
                            break;
                        }
                        case 49: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 26) continue block114;
                            kind = 26;
                            break;
                        }
                        case 51: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 28) continue block114;
                            kind = 28;
                            break;
                        }
                        case 53: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L || kind <= 30) continue block114;
                            kind = 30;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block115: do {
                    switch (this.jjstateSet[--i]) {
                        case 25: {
                            if ((0xD7FFFFFFEFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddStates(4, 7);
                            } else if (this.curChar == '\\') {
                                this.jjAddStates(18, 19);
                            } else if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 26;
                            }
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                        case 55: {
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
                                break;
                            }
                            if (this.curChar == '~') {
                                this.jjstateSet[this.jjnewStateCnt++] = 51;
                                break;
                            }
                            if (this.curChar == '^') {
                                this.jjstateSet[this.jjnewStateCnt++] = 49;
                                break;
                            }
                            if (this.curChar != '_') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 56: {
                            if ((0xD7FFFFFFEFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddStates(4, 7);
                            } else if (this.curChar == '\\') {
                                this.jjAddStates(18, 19);
                            } else if (this.curChar == '}' && kind > 31) {
                                kind = 31;
                            }
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                        case 1: {
                            if ((0x78000000E8000000L & l) != 0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                            } else if (this.curChar == '\\') {
                                this.jjCheckNAdd(2);
                            }
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
                                break;
                            }
                            if (this.curChar == '~') {
                                this.jjstateSet[this.jjnewStateCnt++] = 51;
                                break;
                            }
                            if (this.curChar == '^') {
                                this.jjstateSet[this.jjnewStateCnt++] = 49;
                                break;
                            }
                            if (this.curChar != '_') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 0: {
                            if ((0x8000000110000001L & l) != 0L) {
                                this.jjAddStates(8, 15);
                            } else if (this.curChar == '{') {
                                this.jjCheckNAddStates(20, 22);
                            } else if (this.curChar == '~') {
                                this.jjstateSet[this.jjnewStateCnt++] = 24;
                            } else if (this.curChar == '^') {
                                this.jjstateSet[this.jjnewStateCnt++] = 22;
                            } else if (this.curChar == '_') {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
                            } else if (this.curChar == '[') {
                                this.jjCheckNAdd(8);
                            }
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 25;
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if ((0x78000000E8000000L & l) == 0L || kind <= 1) continue block115;
                            kind = 1;
                            break;
                        }
                        case 4: {
                            if ((0xEFFFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(5, 6);
                            break;
                        }
                        case 5: {
                            this.jjCheckNAddTwoStates(5, 6);
                            break;
                        }
                        case 7: {
                            if (this.curChar != '[') break;
                            this.jjCheckNAdd(8);
                            break;
                        }
                        case 8: {
                            if ((0xFFFFFFFFD7FFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(8, 9);
                            break;
                        }
                        case 9: {
                            if (this.curChar != ']' || kind <= 12) continue block115;
                            kind = 12;
                            break;
                        }
                        case 11: {
                            if (kind <= 13) break;
                            kind = 13;
                            break;
                        }
                        case 12: {
                            if (this.curChar != '_') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 13: {
                            if (kind <= 15) break;
                            kind = 15;
                            break;
                        }
                        case 15: {
                            if (kind <= 17) break;
                            kind = 17;
                            break;
                        }
                        case 18: {
                            if (kind <= 21) break;
                            kind = 21;
                            break;
                        }
                        case 20: {
                            if (kind <= 23) break;
                            kind = 23;
                            break;
                        }
                        case 21: {
                            if (this.curChar != '^') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 22: {
                            if (kind <= 25) break;
                            kind = 25;
                            break;
                        }
                        case 23: {
                            if (this.curChar != '~') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
                            break;
                        }
                        case 24: {
                            if (kind <= 27) break;
                            kind = 27;
                            break;
                        }
                        case 26: {
                            if (kind <= 29) break;
                            kind = 29;
                            break;
                        }
                        case 27: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 28: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAddStates(20, 22);
                            break;
                        }
                        case 29: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                        case 30: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 31: {
                            if ((0xD7FFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 32: {
                            if (this.curChar != '}' || kind <= 31) continue block115;
                            kind = 31;
                            break;
                        }
                        case 33: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(18, 19);
                            break;
                        }
                        case 34: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 35: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 36: {
                            if ((0x8000000110000001L & l) == 0L) break;
                            this.jjAddStates(8, 15);
                            break;
                        }
                        case 38: {
                            if (kind <= 14) break;
                            kind = 14;
                            break;
                        }
                        case 39: {
                            if (this.curChar != '_') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 40: {
                            if (kind <= 16) break;
                            kind = 16;
                            break;
                        }
                        case 42: {
                            if (kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 45: {
                            if (kind <= 22) break;
                            kind = 22;
                            break;
                        }
                        case 47: {
                            if (kind <= 24) break;
                            kind = 24;
                            break;
                        }
                        case 48: {
                            if (this.curChar != '^') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 49;
                            break;
                        }
                        case 49: {
                            if (kind <= 26) break;
                            kind = 26;
                            break;
                        }
                        case 50: {
                            if (this.curChar != '~') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            break;
                        }
                        case 51: {
                            if (kind <= 28) break;
                            kind = 28;
                            break;
                        }
                        case 52: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 53;
                            break;
                        }
                        case 53: {
                            if (kind <= 30) break;
                            kind = 30;
                            break;
                        }
                        case 54: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 52;
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
                block116: do {
                    switch (this.jjstateSet[--i]) {
                        case 25: 
                        case 30: 
                        case 31: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 56: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 0: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(8, 15);
                            break;
                        }
                        case 4: 
                        case 5: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(5, 6);
                            break;
                        }
                        case 8: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(16, 17);
                            break;
                        }
                        case 11: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 13) continue block116;
                            kind = 13;
                            break;
                        }
                        case 13: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block116;
                            kind = 15;
                            break;
                        }
                        case 15: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 17) continue block116;
                            kind = 17;
                            break;
                        }
                        case 18: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 21) continue block116;
                            kind = 21;
                            break;
                        }
                        case 20: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 23) continue block116;
                            kind = 23;
                            break;
                        }
                        case 22: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 25) continue block116;
                            kind = 25;
                            break;
                        }
                        case 24: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 27) continue block116;
                            kind = 27;
                            break;
                        }
                        case 26: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 29) continue block116;
                            kind = 29;
                            break;
                        }
                        case 38: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 14) continue block116;
                            kind = 14;
                            break;
                        }
                        case 40: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 16) continue block116;
                            kind = 16;
                            break;
                        }
                        case 42: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block116;
                            kind = 18;
                            break;
                        }
                        case 45: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 22) continue block116;
                            kind = 22;
                            break;
                        }
                        case 47: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 24) continue block116;
                            kind = 24;
                            break;
                        }
                        case 49: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 26) continue block116;
                            kind = 26;
                            break;
                        }
                        case 51: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 28) continue block116;
                            kind = 28;
                            break;
                        }
                        case 53: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 30) continue block116;
                            kind = 30;
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
            if (i == (startsAt = 55 - this.jjnewStateCnt)) {
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
                if ((active0 & 0xC000000000000L) != 0L) {
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0xC000000000000L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x8000000000000L) != 0L) {
                    this.jjmatchedKind = 49;
                    this.jjmatchedPos = 2;
                    return 4;
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
            case '?': {
                return this.jjMoveStringLiteralDfa1_3(0x4000000000000L);
            }
            case '\\': {
                return this.jjMoveStringLiteralDfa1_3(0x20000000000000L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_3(0x8000000000000L);
            }
        }
        return this.jjMoveNfa_3(0, 0);
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
            case '?': {
                if ((active0 & 0x4000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_3(1, 50, 1);
                }
                if ((active0 & 0x20000000000000L) != 0L) {
                    return this.jjStopAtPos(1, 53);
                }
                return this.jjMoveStringLiteralDfa2_3(active0, 0x8000000000000L);
            }
        }
        return this.jjStartNfa_3(0, active0);
    }

    private final int jjMoveStringLiteralDfa2_3(long old0, long active0) {
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
            case '?': {
                return this.jjMoveStringLiteralDfa3_3(active0, 0x8000000000000L);
            }
        }
        return this.jjStartNfa_3(1, active0);
    }

    private final int jjMoveStringLiteralDfa3_3(long old0, long active0) {
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
            case '}': {
                if ((active0 & 0x8000000000000L) == 0L) break;
                return this.jjStopAtPos(3, 51);
            }
        }
        return this.jjStartNfa_3(2, active0);
    }

    private final int jjMoveNfa_3(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 6;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (this.curChar == '?') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                            }
                            if (this.curChar != '?' || kind <= 49) continue block19;
                            kind = 49;
                            break;
                        }
                        case 2: {
                            if (this.curChar == '?') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 0: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 3: {
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & l) == 0L || kind <= 52) continue block19;
                            kind = 52;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 52) continue block20;
                            kind = 52;
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
                block21: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 52) continue block21;
                            kind = 52;
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
            if (i == (startsAt = 6 - this.jjnewStateCnt)) {
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
                if ((active0 & 0x600000000000000L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x400000000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 1;
                    return 3;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_4(int pos, long active0) {
        return this.jjMoveNfa_4(this.jjStopStringLiteralDfa_4(pos, active0), pos + 1);
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
            case '-': {
                return this.jjStartNfaWithStates_4(0, 57, 1);
            }
            case '\\': {
                return this.jjMoveStringLiteralDfa1_4(0x1000000000000000L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_4(0x400000000000000L);
            }
        }
        return this.jjMoveNfa_4(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_4(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '-': {
                if ((active0 & 0x1000000000000000L) != 0L) {
                    return this.jjStopAtPos(1, 60);
                }
                return this.jjMoveStringLiteralDfa2_4(active0, 0x400000000000000L);
            }
        }
        return this.jjStartNfa_4(0, active0);
    }

    private final int jjMoveStringLiteralDfa2_4(long old0, long active0) {
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
            case '}': {
                if ((active0 & 0x400000000000000L) == 0L) break;
                return this.jjStopAtPos(2, 58);
            }
        }
        return this.jjStartNfa_4(1, active0);
    }

    private final int jjMoveNfa_4(int startState, int curPos) {
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
                block17: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (this.curChar == '-') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            if (this.curChar != '-' || kind <= 56) continue block17;
                            kind = 56;
                            break;
                        }
                        case 0: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0x3FF000000000000L & l) == 0L || kind <= 59) continue block17;
                            kind = 59;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 59) continue block18;
                            kind = 59;
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
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 59) continue block19;
                            kind = 59;
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

    private final int jjMoveStringLiteralDfa0_9() {
        return this.jjMoveNfa_9(0, 0);
    }

    private final int jjMoveNfa_9(int startState, int curPos) {
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
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            this.jjAddStates(2, 3);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '}') break;
                            kind = 90;
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
                        case 1: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 3);
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

    private final int jjStopStringLiteralDfa_7(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active1 & 0xC000L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 1: {
                if ((active1 & 0x8000L) != 0L) {
                    this.jjmatchedKind = 77;
                    this.jjmatchedPos = 1;
                    return 3;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_7(int pos, long active0, long active1) {
        return this.jjMoveNfa_7(this.jjStopStringLiteralDfa_7(pos, active0, active1), pos + 1);
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
            case '\\': {
                return this.jjMoveStringLiteralDfa1_7(131072L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_7(32768L);
            }
            case '~': {
                return this.jjStartNfaWithStates_7(0, 78, 1);
            }
        }
        return this.jjMoveNfa_7(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_7(long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_7(0, 0L, active1);
            return 1;
        }
        switch (this.curChar) {
            case '~': {
                if ((active1 & 0x20000L) != 0L) {
                    return this.jjStopAtPos(1, 81);
                }
                return this.jjMoveStringLiteralDfa2_7(active1, 32768L);
            }
        }
        return this.jjStartNfa_7(0, 0L, active1);
    }

    private final int jjMoveStringLiteralDfa2_7(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_7(0, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_7(1, 0L, active1);
            return 2;
        }
        switch (this.curChar) {
            case '}': {
                if ((active1 & 0x8000L) == 0L) break;
                return this.jjStopAtPos(2, 79);
            }
        }
        return this.jjStartNfa_7(1, 0L, active1);
    }

    private final int jjMoveNfa_7(int startState, int curPos) {
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
                block17: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if ((0x3FF000000000000L & l) == 0L || kind <= 80) continue block17;
                            kind = 80;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (this.curChar == '~') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            if (this.curChar != '~' || kind <= 77) continue block18;
                            kind = 77;
                            break;
                        }
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '~') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 80) continue block18;
                            kind = 80;
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
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 80) continue block19;
                            kind = 80;
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

    private final int jjStopStringLiteralDfa_6(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active1 & 0x180L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 1: {
                if ((active1 & 0x100L) != 0L) {
                    this.jjmatchedKind = 70;
                    this.jjmatchedPos = 1;
                    return 3;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_6(int pos, long active0, long active1) {
        return this.jjMoveNfa_6(this.jjStopStringLiteralDfa_6(pos, active0, active1), pos + 1);
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
            case '\\': {
                return this.jjMoveStringLiteralDfa1_6(1024L);
            }
            case '^': {
                return this.jjStartNfaWithStates_6(0, 71, 1);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_6(256L);
            }
        }
        return this.jjMoveNfa_6(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_6(long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(0, 0L, active1);
            return 1;
        }
        switch (this.curChar) {
            case '^': {
                if ((active1 & 0x400L) != 0L) {
                    return this.jjStopAtPos(1, 74);
                }
                return this.jjMoveStringLiteralDfa2_6(active1, 256L);
            }
        }
        return this.jjStartNfa_6(0, 0L, active1);
    }

    private final int jjMoveStringLiteralDfa2_6(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_6(0, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(1, 0L, active1);
            return 2;
        }
        switch (this.curChar) {
            case '}': {
                if ((active1 & 0x100L) == 0L) break;
                return this.jjStopAtPos(2, 72);
            }
        }
        return this.jjStartNfa_6(1, 0L, active1);
    }

    private final int jjMoveNfa_6(int startState, int curPos) {
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
                block17: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if ((0x3FF000000000000L & l) == 0L || kind <= 73) continue block17;
                            kind = 73;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (this.curChar == '^') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            if (this.curChar != '^' || kind <= 70) continue block18;
                            kind = 70;
                            break;
                        }
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '^') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L || kind <= 73) continue block18;
                            kind = 73;
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
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 3: {
                            if (!ConfluenceTextChunkTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 73) continue block19;
                            kind = 73;
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
            case 30: {
                return (jjbitVec19[i2] & l2) != 0L;
            }
            case 31: {
                return (jjbitVec20[i2] & l2) != 0L;
            }
            case 32: {
                return (jjbitVec21[i2] & l2) != 0L;
            }
            case 48: {
                return (jjbitVec22[i2] & l2) != 0L;
            }
            case 49: {
                return (jjbitVec23[i2] & l2) != 0L;
            }
            case 159: {
                return (jjbitVec24[i2] & l2) != 0L;
            }
            case 215: {
                return (jjbitVec25[i2] & l2) != 0L;
            }
        }
        return (jjbitVec3[i1] & l1) != 0L;
    }

    private static final boolean jjCanMove_2(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec27[i2] & l2) != 0L;
            }
            case 1: {
                return (jjbitVec28[i2] & l2) != 0L;
            }
            case 2: {
                return (jjbitVec29[i2] & l2) != 0L;
            }
            case 3: {
                return (jjbitVec30[i2] & l2) != 0L;
            }
            case 4: {
                return (jjbitVec31[i2] & l2) != 0L;
            }
            case 5: {
                return (jjbitVec32[i2] & l2) != 0L;
            }
            case 6: {
                return (jjbitVec33[i2] & l2) != 0L;
            }
            case 9: {
                return (jjbitVec34[i2] & l2) != 0L;
            }
            case 10: {
                return (jjbitVec35[i2] & l2) != 0L;
            }
            case 11: {
                return (jjbitVec36[i2] & l2) != 0L;
            }
            case 12: {
                return (jjbitVec37[i2] & l2) != 0L;
            }
            case 13: {
                return (jjbitVec38[i2] & l2) != 0L;
            }
            case 14: {
                return (jjbitVec39[i2] & l2) != 0L;
            }
            case 15: {
                return (jjbitVec40[i2] & l2) != 0L;
            }
            case 16: {
                return (jjbitVec41[i2] & l2) != 0L;
            }
            case 30: {
                return (jjbitVec42[i2] & l2) != 0L;
            }
            case 31: {
                return (jjbitVec43[i2] & l2) != 0L;
            }
            case 32: {
                return (jjbitVec44[i2] & l2) != 0L;
            }
            case 48: {
                return (jjbitVec45[i2] & l2) != 0L;
            }
            case 49: {
                return (jjbitVec46[i2] & l2) != 0L;
            }
            case 159: {
                return (jjbitVec47[i2] & l2) != 0L;
            }
            case 215: {
                return (jjbitVec48[i2] & l2) != 0L;
            }
        }
        return (jjbitVec26[i1] & l1) != 0L;
    }

    public ConfluenceTextChunkTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public ConfluenceTextChunkTokenManager(SimpleCharStream stream, int lexState) {
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
        int i = 55;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 10 || lexState < 0) {
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
        Object specialToken = null;
        int curPos = 0;
        try {
            this.curChar = this.input_stream.BeginToken();
        }
        catch (IOException e) {
            this.jjmatchedKind = 0;
            Token matchedToken = this.jjFillToken();
            return matchedToken;
        }
        switch (this.curLexState) {
            case 0: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_0();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 34) break;
                this.jjmatchedKind = 34;
                break;
            }
            case 1: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_1();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 41) break;
                this.jjmatchedKind = 41;
                break;
            }
            case 2: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_2();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 48) break;
                this.jjmatchedKind = 48;
                break;
            }
            case 3: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_3();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 55) break;
                this.jjmatchedKind = 55;
                break;
            }
            case 4: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_4();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 62) break;
                this.jjmatchedKind = 62;
                break;
            }
            case 5: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_5();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 69) break;
                this.jjmatchedKind = 69;
                break;
            }
            case 6: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_6();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 76) break;
                this.jjmatchedKind = 76;
                break;
            }
            case 7: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_7();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 83) break;
                this.jjmatchedKind = 83;
                break;
            }
            case 8: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_8();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 89) break;
                this.jjmatchedKind = 89;
                break;
            }
            case 9: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_9();
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 91) break;
                this.jjmatchedKind = 91;
            }
        }
        if (this.jjmatchedKind != Integer.MAX_VALUE) {
            if (this.jjmatchedPos + 1 < curPos) {
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }
            Token matchedToken = this.jjFillToken();
            if (jjnewLexState[this.jjmatchedKind] != -1) {
                this.curLexState = jjnewLexState[this.jjmatchedKind];
            }
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


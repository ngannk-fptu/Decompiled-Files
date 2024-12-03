/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.xpath;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Stack;
import org.apache.jackrabbit.spi.commons.query.xpath.SimpleCharStream;
import org.apache.jackrabbit.spi.commons.query.xpath.Token;
import org.apache.jackrabbit.spi.commons.query.xpath.TokenMgrError;
import org.apache.jackrabbit.spi.commons.query.xpath.XPathConstants;

public class XPathTokenManager
implements XPathConstants {
    private Stack stateStack = new Stack();
    static final int PARENMARKER = 2000;
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{0L, -16384L, -17590038560769L, 0x7FFFFFL};
    static final long[] jjbitVec2 = new long[]{0L, 0L, 0L, -36028797027352577L};
    static final long[] jjbitVec3 = new long[]{0x7FF3FFFFFFFFFFFFL, 9223372036854775294L, -1L, -274156627316187121L};
    static final long[] jjbitVec4 = new long[]{0xFFFFFFL, -65536L, -576458553280167937L, 3L};
    static final long[] jjbitVec5 = new long[]{0L, 0L, -17179879616L, 4503588160110591L};
    static final long[] jjbitVec6 = new long[]{-8194L, -536936449L, -65533L, 234134404065073567L};
    static final long[] jjbitVec7 = new long[]{-562949953421312L, -8547991553L, 127L, 0x707FFFFFF0000L};
    static final long[] jjbitVec8 = new long[]{576460743713488896L, -562949953419266L, 0x7CFFFFFFFFFFFFFFL, 412319973375L};
    static final long[] jjbitVec9 = new long[]{2594073385365405664L, 0x3FF000000L, 271902628478820320L, 0x30003B0000000L};
    static final long[] jjbitVec10 = new long[]{247132830528276448L, 7881300924956672L, 2589004636761075680L, 0x100000000L};
    static final long[] jjbitVec11 = new long[]{2579997437506199520L, 0x3B0000000L, 270153412153034720L, 0L};
    static final long[] jjbitVec12 = new long[]{283724577500946400L, 0x300000000L, 283724577500946400L, 0x340000000L};
    static final long[] jjbitVec13 = new long[]{288228177128316896L, 0x300000000L, 0L, 0L};
    static final long[] jjbitVec14 = new long[]{3799912185593854L, 63L, 2309621682768192918L, 31L};
    static final long[] jjbitVec15 = new long[]{0L, 0x3FFFFFFFEFFL, 0L, 0L};
    static final long[] jjbitVec16 = new long[]{0L, 0L, -4294967296L, 36028797018898495L};
    static final long[] jjbitVec17 = new long[]{5764607523034749677L, 12493387738468353L, -756383734487318528L, 144405459145588743L};
    static final long[] jjbitVec18 = new long[]{-1L, -1L, -4026531841L, 0x3FFFFFFFFFFFFFFL};
    static final long[] jjbitVec19 = new long[]{-3233808385L, 0x3FFFFFFFAAFF3F3FL, 0x5FDFFFFFFFFFFFFFL, 2295745090394464220L};
    static final long[] jjbitVec20 = new long[]{0x4C4000000000L, 0L, 7L, 0L};
    static final long[] jjbitVec21 = new long[]{4389456576640L, -2L, -8587837441L, 0x7FFFFFFFFFFFFFFL};
    static final long[] jjbitVec22 = new long[]{35184372088800L, 0L, 0L, 0L};
    static final long[] jjbitVec23 = new long[]{-1L, -1L, 0x3FFFFFFFFFL, 0L};
    static final long[] jjbitVec24 = new long[]{-1L, -1L, 0xFFFFFFFFFL, 0L};
    static final long[] jjbitVec25 = new long[]{0L, 0L, 0x80000000000000L, -36028797027352577L};
    static final long[] jjbitVec26 = new long[]{0xFFFFFFL, -65536L, -576458553280167937L, 196611L};
    static final long[] jjbitVec27 = new long[]{-1L, 0x30000003FL, -17179879488L, 4503588160110591L};
    static final long[] jjbitVec28 = new long[]{-8194L, -536936449L, -65413L, 234134404065073567L};
    static final long[] jjbitVec29 = new long[]{-562949953421312L, -8547991553L, -4899916411759099777L, 1979120929931286L};
    static final long[] jjbitVec30 = new long[]{576460743713488896L, -277081224642561L, 0x7CFFFFFFFFFFFFFFL, 288017070894841855L};
    static final long[] jjbitVec31 = new long[]{-864691128455135250L, 281268803485695L, -3186861885341720594L, 1125692414638495L};
    static final long[] jjbitVec32 = new long[]{-3211631683292264476L, 9006925953907079L, -869759877059465234L, 281204393786303L};
    static final long[] jjbitVec33 = new long[]{-878767076314341394L, 281215949093263L, -4341532606274353172L, 280925229301191L};
    static final long[] jjbitVec34 = new long[]{-4327961440926441490L, 281212990012895L, -4327961440926441492L, 281214063754719L};
    static final long[] jjbitVec35 = new long[]{-4323457841299070996L, 281212992110031L, 0L, 0L};
    static final long[] jjbitVec36 = new long[]{0x7FF7FFFFFFFFFFEL, 0x3FF7FFFL, 4323293666156225942L, 0x3FF3F5FL};
    static final long[] jjbitVec37 = new long[]{-4422530440275951616L, -558551906910465L, 215680200883507167L, 0L};
    static final long[] jjbitVec38 = new long[]{0L, 0L, 0L, 9126739968L};
    static final long[] jjbitVec39 = new long[]{17732914942836896L, -2L, -6876561409L, 0x77FFFFFFFFFFFFFFL};
    static final long[] jjbitVec40 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec41 = new long[]{0L, 0L, -1L, -1L};
    static final long[] jjbitVec42 = new long[]{-2L, -1L, -1L, Long.MAX_VALUE};
    static final long[] jjbitVec43 = new long[]{-1L, -1L, -1L, 0x3FFFFFFFFFFFFFFFL};
    static final int[] jjnextStates = new int[]{1, 2, 4, 5, 2, 3, 2, 3, 5, 11, 12, 27, 28, 27, 28, 30, 56, 57, 60, 17, 18, 41, 42, 47, 49, 64, 65, 75, 77, 85, 86, 93, 95, 103, 105, 109, 110, 118, 119, 126, 127, 135, 137, 142, 143, 148, 149, 163, 164, 180, 188, 182, 184, 194, 195, 197, 198, 199, 201, 161, 178, 192, 140, 145, 124, 132, 101, 107, 115, 83, 90, 55, 72, 58, 59, 61, 62, 3, 4, 7, 8, 10, 13, 16, 18, 32, 33, 37, 39, 59, 67, 61, 63, 74, 76, 84, 86, 91, 99, 104, 109, 102, 112, 82, 88, 57, 71, 8, 15, 769, 770, 771, 73, 74, 75, 77, 78, 80, 83, 84, 86, 89, 92, 93, 94, 94, 86, 89, 7, 14, 21, 30, 38, 39, 47, 48, 62, 63, 67, 68, 77, 78, 80, 83, 84, 86, 89, 85, 86, 89, 97, 98, 101, 105, 107, 116, 118, 126, 128, 144, 145, 154, 155, 156, 157, 159, 160, 161, 170, 171, 181, 183, 188, 189, 196, 197, 205, 207, 217, 219, 237, 245, 252, 260, 267, 280, 269, 274, 287, 305, 312, 324, 331, 339, 346, 354, 361, 370, 377, 394, 379, 388, 401, 416, 403, 410, 423, 439, 425, 433, 446, 447, 460, 461, 469, 479, 471, 472, 487, 489, 495, 497, 514, 516, 525, 526, 527, 528, 529, 551, 552, 574, 575, 598, 600, 604, 605, 619, 620, 636, 637, 642, 644, 661, 663, 672, 673, 677, 678, 685, 686, 687, 688, 690, 691, 692, 699, 700, 707, 708, 714, 715, 719, 720, 724, 725, 736, 742, 748, 754, 760, 761, 778, 779, 787, 790, 796, 763, 764, 766, 767, 769, 770, 771, 772, 773, 775, 785, 802, 746, 758, 759, 717, 722, 733, 683, 697, 705, 711, 659, 670, 674, 602, 617, 634, 639, 493, 512, 523, 549, 572, 595, 215, 235, 250, 265, 285, 310, 329, 344, 359, 375, 399, 421, 444, 458, 467, 484, 186, 194, 202, 96, 114, 124, 142, 152, 168, 178, 81, 82, 87, 88, 99, 100, 102, 103, 156, 157, 159, 160, 161, 687, 688, 690, 691, 692};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, "encoding", null, null, null, null, null, null, "<?", "<?", "?>", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "ordered", "unordered", null, null, "yes", "no", "external", "or", "and", "div", "idiv", "mod", "*", "in", null, null, null, "$", null, null, null, "?", null, "satisfies", "return", "then", "else", "default", null, null, "preserve", "strip", "namespace", null, "to", "where", "collation", "intersect", "union", "except", "as", "at", "case", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "*", "*", null, null, "/", "//", "/", "//", "=", "=", "is", "!=", "<=", "<<", ">=", ">>", "eq", "ne", "gt", "ge", "lt", "le", ":=", "<", ">", "-", "+", "-", "+", "?", "*", "+", "|", "(", "@", "[", "]", ")", ")", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ",", ",", ";", "%%%", "\"", "\"", ".", "..", null, null, "ascending", "descending", null, null, null, null, null, null, null, null, null, null, "<![CDATA[", "<![CDATA[", null, null, null, null, "<", "<", ">", "/>", "</", ">", "=", null, "{", "{", "{{", "}}", "\"\"", "''", null, null, null, null, null, null, null, "'", "'", null, null, null, null, null, null, null, null, null, null, null, null, null, "<!--", "<!--", "-->", null, null, null, null, null, null, "}", null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT", "OPERATOR", "KINDTESTFORPI", "XQUERYVERSION", "ITEMTYPE", "NAMESPACEDECL", "NAMESPACEKEYWORD", "KINDTEST", "XMLSPACE_DECL", "SINGLETYPE", "VARNAME", "OCCURRENCEINDICATOR", "CLOSEKINDTEST", "ELEMENT_CONTENT", "PROCESSING_INSTRUCTION", "PROCESSING_INSTRUCTION_CONTENT", "START_TAG", "QUOT_ATTRIBUTE_CONTENT", "EXT_NAME", "CDATA_SECTION", "APOS_ATTRIBUTE_CONTENT", "END_TAG", "XML_COMMENT", "EXPR_COMMENT", "EXT_CONTENT", "EXT_KEY"};
    public static final int[] jjnewLexState = new int[]{-1, 1, 1, 1, 1, -1, 3, -1, -1, 0, 0, 5, -1, -1, 1, 14, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 10, 1, -1, 0, -1, 1, 0, 0, 0, 0, -1, 8, 5, 0, 0, 5, 5, 0, 0, -1, 0, 0, 0, 4, 0, 4, 4, 9, 11, 7, 7, 7, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 5, -1, 6, 6, 1, 6, 6, -1, -1, 1, 12, 1, 1, -1, -1, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 1, 1, 1, 0, 0, -1, 0, -1, 1, -1, 10, 10, 10, 10, 9, 4, -1, -1, -1, 7, 7, -1, 7, 7, 7, 2, 7, -1, 7, 7, -1, 7, 2, 7, 7, 7, -1, -1, 0, 7, 0, 0, 17, 16, 1, 1, 0, 0, -1, -1, -1, -1, 10, 11, 1, 12, 24, -1, -1, -1, 19, 19, -1, -1, -1, -1, 16, 16, 13, -1, 21, -1, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, 16, -1, -1, -1, 25, -1, -1, -1, -1, 23, -1, -1, 18, 18, 22, 22, -1, 1, -1, -1, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1};
    static final long[] jjtoToken = new long[]{-2674012278779905L, -103079215105L, -288230376152760321L, 684995878322167L};
    static final long[] jjtoSkip = new long[]{20480L, 0L, 0x400000000000000L, 432487374384005120L};
    static final long[] jjtoSpecial = new long[]{0L, 0L, 0x400000000000000L, 141810156437504L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[803];
    private final int[] jjstateSet = new int[1606];
    StringBuffer image;
    int jjimageLen;
    int lengthOfMatch;
    protected char curChar;
    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    private void pushState() {
        this.stateStack.addElement(new Integer(this.curLexState));
    }

    private void pushState(int state) {
        this.stateStack.push(new Integer(state));
    }

    private void popState() {
        int nextState;
        if (this.stateStack.size() == 0) {
            this.printLinePos();
        }
        if ((nextState = ((Integer)this.stateStack.pop()).intValue()) == 2000) {
            this.printLinePos();
        }
        this.SwitchTo(nextState);
    }

    private boolean isState(int state) {
        for (int i = 0; i < this.stateStack.size(); ++i) {
            if ((Integer)this.stateStack.elementAt(i) != state) continue;
            return true;
        }
        return false;
    }

    private void pushParenState(int commaState, int rparState) {
        this.stateStack.push(new Integer(rparState));
        this.stateStack.push(new Integer(commaState));
        this.stateStack.push(new Integer(2000));
        this.SwitchTo(commaState);
    }

    public void printLinePos() {
        System.err.println("Line: " + this.input_stream.getEndLine());
    }

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_14(int pos, long active0) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_14(int pos, long active0) {
        return this.jjMoveNfa_14(this.jjStopStringLiteralDfa_14(pos, active0), pos + 1);
    }

    private final int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private final int jjStartNfaWithStates_14(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_14(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_14() {
        switch (this.curChar) {
            case '?': {
                return this.jjMoveStringLiteralDfa1_14(131072L);
            }
        }
        return this.jjMoveNfa_14(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_14(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_14(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '>': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(1, 17);
            }
        }
        return this.jjStartNfa_14(0, active0);
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

    private final int jjMoveNfa_14(int startState, int curPos) {
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
                block14: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 2: {
                            if ((0x100002600L & l) == 0L) continue block14;
                            kind = 238;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 1: {
                            if ((0x3FF600000000000L & l) == 0L) continue block14;
                            kind = 46;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block15: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block15;
                            if (kind > 46) {
                                kind = 46;
                            }
                            this.jjCheckNAdd(1);
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
                block16: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block16;
                            if (kind > 46) {
                                kind = 46;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block16;
                            if (kind > 46) {
                                kind = 46;
                            }
                            this.jjCheckNAdd(1);
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

    private final int jjStopStringLiteralDfa_3(int pos, long active0, long active1, long active2) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_3(int pos, long active0, long active1, long active2) {
        return this.jjMoveNfa_3(this.jjStopStringLiteralDfa_3(pos, active0, active1, active2), pos + 1);
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
            case ';': {
                return this.jjStopAtPos(0, 170);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_3(256L);
            }
        }
        return this.jjMoveNfa_3(7, 0);
    }

    private final int jjMoveStringLiteralDfa1_3(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(0, active0, 0L, 0L);
            return 1;
        }
        switch (this.curChar) {
            case 'n': {
                return this.jjMoveStringLiteralDfa2_3(active0, 256L);
            }
        }
        return this.jjStartNfa_3(0, active0, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa2_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(0, old0, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(1, active0, 0L, 0L);
            return 2;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa3_3(active0, 256L);
            }
        }
        return this.jjStartNfa_3(1, active0, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa3_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(1, old0, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(2, active0, 0L, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'o': {
                return this.jjMoveStringLiteralDfa4_3(active0, 256L);
            }
        }
        return this.jjStartNfa_3(2, active0, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa4_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(2, old0, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(3, active0, 0L, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'd': {
                return this.jjMoveStringLiteralDfa5_3(active0, 256L);
            }
        }
        return this.jjStartNfa_3(3, active0, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa5_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(3, old0, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(4, active0, 0L, 0L);
            return 5;
        }
        switch (this.curChar) {
            case 'i': {
                return this.jjMoveStringLiteralDfa6_3(active0, 256L);
            }
        }
        return this.jjStartNfa_3(4, active0, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa6_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(4, old0, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(5, active0, 0L, 0L);
            return 6;
        }
        switch (this.curChar) {
            case 'n': {
                return this.jjMoveStringLiteralDfa7_3(active0, 256L);
            }
        }
        return this.jjStartNfa_3(5, active0, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa7_3(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_3(5, old0, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_3(6, active0, 0L, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 'g': {
                if ((active0 & 0x100L) == 0L) break;
                return this.jjStopAtPos(7, 8);
            }
        }
        return this.jjStartNfa_3(6, active0, 0L, 0L);
    }

    private final int jjMoveNfa_3(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 7;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block21: do {
                    switch (this.jjstateSet[--i]) {
                        case 7: {
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(6);
                                break;
                            }
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(4, 5);
                                break;
                            }
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 0: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\"') continue block21;
                            if (kind > 7) {
                                kind = 7;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 4: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\'') continue block21;
                            if (kind > 7) {
                                kind = 7;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 6: {
                            if ((0x100002600L & l) == 0L) continue block21;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(6);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            this.jjAddStates(2, 3);
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
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
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
            if (i == (startsAt = 7 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_15(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x20000L) != 0L) {
                    this.jjmatchedKind = 215;
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_15(int pos, long active0) {
        return this.jjMoveNfa_15(this.jjStopStringLiteralDfa_15(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_15(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_15(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_15() {
        switch (this.curChar) {
            case '?': {
                return this.jjMoveStringLiteralDfa1_15(131072L);
            }
        }
        return this.jjMoveNfa_15(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_15(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_15(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '>': {
                if ((active0 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(1, 17);
            }
        }
        return this.jjStartNfa_15(0, active0);
    }

    private final int jjMoveNfa_15(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 1;
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
                        case 0: {
                            if ((0xFFFFFFFF00002600L & l) == 0L) break;
                            kind = 215;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            kind = 215;
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
                block14: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!XPathTokenManager.jjCanMove_3(hiByte, i1, i2, l1, l2) || kind <= 215) continue block14;
                            kind = 215;
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
            if (i == (startsAt = 1 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_21(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_21(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_21(this.jjStopStringLiteralDfa_21(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_21(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_21(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_21() {
        switch (this.curChar) {
            case '>': {
                return this.jjStopAtPos(0, 201);
            }
        }
        return this.jjMoveNfa_21(1, 0);
    }

    private final int jjMoveNfa_21(int startState, int curPos) {
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
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 1: {
                            if ((0x100002600L & l) == 0L) continue block20;
                            kind = 237;
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 2: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 3: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 5: {
                            if ((0x3FF600000000000L & l) == 0L) continue block20;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block21: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: 
                        case 5: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAdd(5);
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
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAdd(5);
                            break;
                        }
                        case 5: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAdd(5);
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

    private final int jjStopStringLiteralDfa_22(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active3 & 0x40000000000L) != 0L) {
                    this.jjmatchedKind = 213;
                    return 2;
                }
                return -1;
            }
            case 1: {
                if ((active3 & 0x40000000000L) != 0L) {
                    this.jjmatchedKind = 214;
                    this.jjmatchedPos = 1;
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_22(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_22(this.jjStopStringLiteralDfa_22(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_22(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_22(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_22() {
        switch (this.curChar) {
            case '-': {
                return this.jjMoveStringLiteralDfa1_22(0x40000000000L);
            }
        }
        return this.jjMoveNfa_22(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_22(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_22(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case '-': {
                return this.jjMoveStringLiteralDfa2_22(active3, 0x40000000000L);
            }
        }
        return this.jjStartNfa_22(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa2_22(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_22(0, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_22(1, 0L, 0L, 0L, active3);
            return 2;
        }
        switch (this.curChar) {
            case '>': {
                if ((active3 & 0x40000000000L) == 0L) break;
                return this.jjStopAtPos(2, 234);
            }
        }
        return this.jjStartNfa_22(1, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_22(int startState, int curPos) {
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
                block16: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFF00002600L & l) != 0L && kind > 213) {
                                kind = 213;
                            }
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 2: {
                            if ((0xFFFFFFFF00002600L & l) == 0L || kind <= 214) continue block16;
                            kind = 214;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind <= 213) break;
                            kind = 213;
                            break;
                        }
                        case 2: {
                            if (kind <= 214) break;
                            kind = 214;
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
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!XPathTokenManager.jjCanMove_3(hiByte, i1, i2, l1, l2) || kind <= 213) continue block18;
                            kind = 213;
                            break;
                        }
                        case 2: {
                            if (!XPathTokenManager.jjCanMove_3(hiByte, i1, i2, l1, l2) || kind <= 214) continue block18;
                            kind = 214;
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

    private final int jjStopStringLiteralDfa_24(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active3 & 0x100000000L) != 0L) {
                    this.jjmatchedKind = 223;
                    return -1;
                }
                return -1;
            }
            case 1: {
                if ((active3 & 0x100000000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 223;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_24(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_24(this.jjStopStringLiteralDfa_24(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_24(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_24(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_24() {
        switch (this.curChar) {
            case ':': {
                return this.jjMoveStringLiteralDfa1_24(0x100000000L);
            }
        }
        return this.jjMoveNfa_24(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_24(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_24(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                return this.jjMoveStringLiteralDfa2_24(active3, 0x100000000L);
            }
        }
        return this.jjStartNfa_24(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa2_24(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_24(0, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_24(1, 0L, 0L, 0L, active3);
            return 2;
        }
        switch (this.curChar) {
            case ')': {
                if ((active3 & 0x100000000L) == 0L) break;
                return this.jjStopAtPos(2, 224);
            }
        }
        return this.jjStartNfa_24(1, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_24(int startState, int curPos) {
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
                            if ((0xFFFFFFFF00002600L & l) != 0L && kind > 223) {
                                kind = 223;
                            }
                            if ((0x100002600L & l) == 0L) break;
                            if (kind > 239) {
                                kind = 239;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if ((0x100002600L & l) == 0L) continue block13;
                            if (kind > 239) {
                                kind = 239;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            kind = 223;
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
                            if (!XPathTokenManager.jjCanMove_3(hiByte, i1, i2, l1, l2) || kind <= 223) continue block15;
                            kind = 223;
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

    private final int jjStopStringLiteralDfa_7(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_7(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_7(this.jjStopStringLiteralDfa_7(pos, active0, active1, active2, active3), pos + 1);
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
            case '(': {
                return this.jjMoveStringLiteralDfa1_7(0x800000000L);
            }
            case ')': {
                return this.jjStopAtPos(0, 139);
            }
            case '*': {
                return this.jjStopAtPos(0, 102);
            }
            case '{': {
                return this.jjStopAtPos(0, 205);
            }
        }
        return this.jjMoveNfa_7(9, 0);
    }

    private final int jjMoveStringLiteralDfa1_7(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_7(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) == 0L) break;
                return this.jjStopAtPos(1, 227);
            }
        }
        return this.jjStartNfa_7(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_7(int startState, int curPos) {
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
                block45: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 9: {
                            if ((0x100002600L & l) == 0L) continue block45;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 2: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 3: {
                            if (this.curChar != '(' || kind <= 157) continue block45;
                            kind = 157;
                            break;
                        }
                        case 11: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(9, 10);
                            break;
                        }
                        case 12: {
                            if (this.curChar != '(' || kind <= 160) continue block45;
                            kind = 160;
                            break;
                        }
                        case 19: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 27: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjAddStates(11, 12);
                            break;
                        }
                        case 28: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 30: {
                            if ((0x3FF600000000000L & l) == 0L) continue block45;
                            if (kind > 185) {
                                kind = 185;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block46: do {
                    switch (this.jjstateSet[--i]) {
                        case 9: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 185) {
                                    kind = 185;
                                }
                                this.jjCheckNAddStates(13, 15);
                            }
                            if (this.curChar == 's') {
                                this.jjstateSet[this.jjnewStateCnt++] = 24;
                                break;
                            }
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 1: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 4: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(9, 10);
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 14: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 15: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 18: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 21: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 22: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 23: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 24: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 25: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
                            break;
                        }
                        case 26: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block46;
                            if (kind > 185) {
                                kind = 185;
                            }
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 27: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(27, 28);
                            break;
                        }
                        case 29: 
                        case 30: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block46;
                            if (kind > 185) {
                                kind = 185;
                            }
                            this.jjCheckNAdd(30);
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
                        case 9: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block47;
                            if (kind > 185) {
                                kind = 185;
                            }
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 27: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(27, 28);
                            break;
                        }
                        case 29: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block47;
                            if (kind > 185) {
                                kind = 185;
                            }
                            this.jjCheckNAdd(30);
                            break;
                        }
                        case 30: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block47;
                            if (kind > 185) {
                                kind = 185;
                            }
                            this.jjCheckNAdd(30);
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

    private final int jjStopStringLiteralDfa_25(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_25(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_25(this.jjStopStringLiteralDfa_25(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_25(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_25(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_25() {
        switch (this.curChar) {
            case 'e': {
                return this.jjMoveStringLiteralDfa1_25(0x8000000000L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa1_25(0x4000000000L);
            }
        }
        return this.jjMoveNfa_25(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_25(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_25(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case 'r': {
                return this.jjMoveStringLiteralDfa2_25(active3, 0x4000000000L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa2_25(active3, 0x8000000000L);
            }
        }
        return this.jjStartNfa_25(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa2_25(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_25(0, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_25(1, 0L, 0L, 0L, active3);
            return 2;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa3_25(active3, 0x4000000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa3_25(active3, 0x8000000000L);
            }
        }
        return this.jjStartNfa_25(1, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa3_25(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_25(1, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_25(2, 0L, 0L, 0L, active3);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                return this.jjMoveStringLiteralDfa4_25(active3, 0x8000000000L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa4_25(active3, 0x4000000000L);
            }
        }
        return this.jjStartNfa_25(2, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa4_25(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_25(2, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_25(3, 0L, 0L, 0L, active3);
            return 4;
        }
        switch (this.curChar) {
            case 'm': {
                return this.jjMoveStringLiteralDfa5_25(active3, 0x4000000000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa5_25(active3, 0x8000000000L);
            }
        }
        return this.jjStartNfa_25(3, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa5_25(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_25(3, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_25(4, 0L, 0L, 0L, active3);
            return 5;
        }
        switch (this.curChar) {
            case 'a': {
                if ((active3 & 0x4000000000L) == 0L) break;
                return this.jjStopAtPos(5, 230);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa6_25(active3, 0x8000000000L);
            }
        }
        return this.jjStartNfa_25(4, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa6_25(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_25(4, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_25(5, 0L, 0L, 0L, active3);
            return 6;
        }
        switch (this.curChar) {
            case 'i': {
                return this.jjMoveStringLiteralDfa7_25(active3, 0x8000000000L);
            }
        }
        return this.jjStartNfa_25(5, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa7_25(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_25(5, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_25(6, 0L, 0L, 0L, active3);
            return 7;
        }
        switch (this.curChar) {
            case 'o': {
                return this.jjMoveStringLiteralDfa8_25(active3, 0x8000000000L);
            }
        }
        return this.jjStartNfa_25(6, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa8_25(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_25(6, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_25(7, 0L, 0L, 0L, active3);
            return 8;
        }
        switch (this.curChar) {
            case 'n': {
                if ((active3 & 0x8000000000L) == 0L) break;
                return this.jjStopAtPos(8, 231);
            }
        }
        return this.jjStartNfa_25(7, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_25(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 1;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block10: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x100002600L & l) == 0L) continue block10;
                            kind = 239;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
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
            if (i == (startsAt = 1 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_23(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active3 & 0x2840000000L) != 0L) {
                    this.jjmatchedKind = 228;
                    return -1;
                }
                return -1;
            }
            case 1: {
                if ((active3 & 0x2840000000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 228;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_23(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_23(this.jjStopStringLiteralDfa_23(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_23(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_23(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_23() {
        switch (this.curChar) {
            case '(': {
                return this.jjMoveStringLiteralDfa1_23(0x840000000L);
            }
            case ':': {
                return this.jjMoveStringLiteralDfa1_23(0x2000000000L);
            }
        }
        return this.jjMoveNfa_23(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_23(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_23(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ')': {
                if ((active3 & 0x2000000000L) == 0L) break;
                return this.jjStopAtPos(1, 229);
            }
            case ':': {
                if ((active3 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_23(active3, 0x40000000L);
            }
        }
        return this.jjStartNfa_23(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa2_23(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_23(0, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_23(1, 0L, 0L, 0L, active3);
            return 2;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x40000000L) == 0L) break;
                return this.jjStopAtPos(2, 222);
            }
        }
        return this.jjStartNfa_23(1, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_23(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 1;
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
                        case 0: {
                            if ((0xFFFFFFFF00002600L & l) == 0L) break;
                            kind = 228;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            kind = 228;
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
                block14: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!XPathTokenManager.jjCanMove_3(hiByte, i1, i2, l1, l2) || kind <= 228) continue block14;
                            kind = 228;
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
            if (i == (startsAt = 1 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_4(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x100080000000000L) != 0L || (active1 & 0x780000000000048L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 202;
                }
                if ((active0 & 0x200000000000000L) != 0L || (active1 & 4L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 140;
                }
                if ((active1 & 0x40000000000000L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 44;
                }
                if ((active1 & 0x400L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 101;
                }
                if ((active0 & 0x240000000000L) != 0L || (active1 & 0x800000000020L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 83;
                }
                if ((active0 & 0x20000000000L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 14;
                }
                if ((active0 & 0x10000000000L) != 0L || (active1 & 0x300L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 55;
                }
                if ((active0 & 0x80000000000000L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 161;
                }
                if ((active0 & 0x400004000000000L) != 0L || (active1 & 0x20000000000080L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 124;
                }
                if ((active0 & 0x8000000000L) != 0L) {
                    this.jjmatchedKind = 183;
                    return 52;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x8000000000L) != 0L) {
                    return 51;
                }
                if ((active0 & 0x400000000000000L) != 0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 183;
                        this.jjmatchedPos = 1;
                    }
                    return 131;
                }
                if ((active0 & 0x200000000000L) != 0L || (active1 & 0x20L) != 0L) {
                    return 82;
                }
                if ((active1 & 0x7E0800000000104L) != 0L) {
                    return 202;
                }
                if ((active1 & 0x400L) != 0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 183;
                        this.jjmatchedPos = 1;
                    }
                    return 100;
                }
                if ((active1 & 0x200L) != 0L) {
                    return 71;
                }
                if ((active0 & 0x3800F4000000000L) != 0L || (active1 & 0xC8L) != 0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 183;
                        this.jjmatchedPos = 1;
                    }
                    return 202;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x780044000000000L) != 0L || (active1 & 0xE8L) != 0L) {
                    this.jjmatchedKind = 183;
                    this.jjmatchedPos = 2;
                    return 202;
                }
                if ((active1 & 0x400L) != 0L) {
                    this.jjmatchedKind = 183;
                    this.jjmatchedPos = 2;
                    return 99;
                }
                if ((active0 & 0xB0000000000L) != 0L) {
                    return 202;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x600040000000000L) != 0L || (active1 & 0x400L) != 0L) {
                    return 202;
                }
                if ((active0 & 0x180004000000000L) != 0L || (active1 & 0xE8L) != 0L) {
                    this.jjmatchedKind = 183;
                    this.jjmatchedPos = 3;
                    return 202;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x180004000000000L) != 0L || (active1 & 0xA0L) != 0L) {
                    this.jjmatchedKind = 183;
                    this.jjmatchedPos = 4;
                    return 202;
                }
                if ((active1 & 0x48L) != 0L) {
                    return 202;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x80004000000000L) != 0L || (active1 & 0x20L) != 0L) {
                    this.jjmatchedKind = 183;
                    this.jjmatchedPos = 5;
                    return 202;
                }
                if ((active0 & 0x100000000000000L) != 0L || (active1 & 0x80L) != 0L) {
                    return 202;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x80004000000000L) != 0L || (active1 & 0x20L) != 0L) {
                    this.jjmatchedKind = 183;
                    this.jjmatchedPos = 6;
                    return 202;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x80000000000000L) != 0L || (active1 & 0x20L) != 0L) {
                    this.jjmatchedKind = 183;
                    this.jjmatchedPos = 7;
                    return 202;
                }
                if ((active0 & 0x4000000000L) != 0L) {
                    return 202;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_4(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_4(this.jjStopStringLiteralDfa_4(pos, active0, active1, active2, active3), pos + 1);
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
            case '!': {
                return this.jjMoveStringLiteralDfa1_4(0L, 0x1000000000000L, 0L);
            }
            case '$': {
                return this.jjStopAtPos(0, 49);
            }
            case '(': {
                this.jjmatchedKind = 134;
                return this.jjMoveStringLiteralDfa1_4(0L, 0L, 0x840000000L);
            }
            case ',': {
                return this.jjStopAtPos(0, 168);
            }
            case '-': {
                return this.jjStopAtPos(0, 126);
            }
            case ':': {
                return this.jjMoveStringLiteralDfa1_4(0L, 0x800000000000000L, 0L);
            }
            case ';': {
                return this.jjStopAtPos(0, 170);
            }
            case '<': {
                this.jjmatchedKind = 124;
                return this.jjMoveStringLiteralDfa1_4(0L, 0x6000000000000L, 0L);
            }
            case '=': {
                return this.jjStopAtPos(0, 109);
            }
            case '>': {
                this.jjmatchedKind = 125;
                return this.jjMoveStringLiteralDfa1_4(0L, 0x18000000000000L, 0L);
            }
            case '[': {
                return this.jjStopAtPos(0, 136);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_4(0x10000000000L, 768L, 0L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa1_4(0L, 1024L, 0L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_4(0x20000000000L, 0L, 0L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_4(0x400004000000000L, 0x20000000000080L, 0L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa1_4(0L, 0x180000000000000L, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_4(0x240000000000L, 0x800000000020L, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_4(0L, 0x600000000000000L, 0L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_4(0x80000000000L, 0L, 0L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_4(0L, 0x40000000000000L, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_4(0x8000000000L, 0L, 0L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa1_4(0x100000000000000L, 0L, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_4(0x80000000000000L, 0L, 0L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_4(0x200000000000000L, 4L, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa1_4(0L, 64L, 0L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa1_4(0L, 8L, 0L);
            }
            case '|': {
                return this.jjStopAtPos(0, 133);
            }
        }
        return this.jjMoveNfa_4(15, 0);
    }

    private final int jjMoveStringLiteralDfa1_4(long active0, long active1, long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(0, active0, active1, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_4(active0, 0L, active1, 0L, active3, 0x40000000L);
            }
            case '<': {
                if ((active1 & 0x4000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 114);
            }
            case '=': {
                if ((active1 & 0x1000000000000L) != 0L) {
                    return this.jjStopAtPos(1, 112);
                }
                if ((active1 & 0x2000000000000L) != 0L) {
                    return this.jjStopAtPos(1, 113);
                }
                if ((active1 & 0x8000000000000L) != 0L) {
                    return this.jjStopAtPos(1, 115);
                }
                if ((active1 & 0x800000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 123);
            }
            case '>': {
                if ((active1 & 0x10000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 116);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x80000000000000L, active1, 1024L, active3, 0L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x40000000000L, active1, 0L, active3, 0L);
            }
            case 'e': {
                if ((active1 & 0x40000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_4(1, 118, 202);
                }
                if ((active1 & 0x100000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_4(1, 120, 202);
                }
                if ((active1 & 0x400000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_4(1, 122, 202);
                }
                return this.jjMoveStringLiteralDfa2_4(active0, 0x100000000000000L, active1, 0L, active3, 0L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x200000000000000L, active1, 8L, active3, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x20000000000L, active1, 0L, active3, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x400000000000000L, active1, 0L, active3, 0L);
            }
            case 'n': {
                if ((active0 & 0x200000000000L) != 0L) {
                    this.jjmatchedKind = 45;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_4(active0, 0x10000000000L, active1, 96L, active3, 0L);
            }
            case 'o': {
                if ((active1 & 4L) != 0L) {
                    return this.jjStartNfaWithStates_4(1, 66, 202);
                }
                return this.jjMoveStringLiteralDfa2_4(active0, 0x80000000000L, active1, 0L, active3, 0L);
            }
            case 'q': {
                if ((active1 & 0x20000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(1, 117, 202);
            }
            case 'r': {
                if ((active0 & 0x8000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(1, 39, 51);
            }
            case 's': {
                if ((active1 & 0x100L) != 0L) {
                    return this.jjStartNfaWithStates_4(1, 72, 202);
                }
                if ((active1 & 0x800000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(1, 111, 202);
            }
            case 't': {
                if ((active1 & 0x200L) != 0L) {
                    return this.jjStartNfaWithStates_4(1, 73, 71);
                }
                if ((active1 & 0x80000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_4(1, 119, 202);
                }
                if ((active1 & 0x200000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(1, 121, 202);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa2_4(active0, 0x4000000000L, active1, 128L, active3, 0L);
            }
        }
        return this.jjStartNfa_4(0, active0, active1, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa2_4(long old0, long active0, long old1, long active1, long old3, long active3) {
        if (((active0 &= old0) | (active1 &= old1) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_4(0, old0, old1, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(1, active0, active1, 0L, active3);
            return 2;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x40000000L) == 0L) break;
                return this.jjStopAtPos(2, 222);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa3_4(active0, 0L, active1, 128L, active3, 0L);
            }
            case 'd': {
                if ((active0 & 0x10000000000L) != 0L) {
                    return this.jjStartNfaWithStates_4(2, 40, 202);
                }
                if ((active0 & 0x80000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(2, 43, 202);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_4(active0, 0x200000000000000L, active1, 8L, active3, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa3_4(active0, 0x40000000000L, active1, 64L, active3, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_4(active0, 0x400000000000000L, active1, 1024L, active3, 0L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa3_4(active0, 108086665934798848L, active1, 32L, active3, 0L);
            }
            case 'v': {
                if ((active0 & 0x20000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(2, 41, 202);
            }
        }
        return this.jjStartNfa_4(1, active0, active1, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa3_4(long old0, long active0, long old1, long active1, long old3, long active3) {
        if (((active0 &= old0) | (active1 &= old1) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_4(1, old0, old1, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(2, active0, active1, 0L, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x400000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_4(3, 58, 202);
                }
                if ((active1 & 0x400L) != 0L) {
                    return this.jjStartNfaWithStates_4(3, 74, 202);
                }
                return this.jjMoveStringLiteralDfa4_4(active0, 0x4000000000L, active1, 160L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_4(active0, 0x80000000000000L, active1, 0L);
            }
            case 'n': {
                if ((active0 & 0x200000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(3, 57, 202);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa4_4(active0, 0L, active1, 64L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa4_4(active0, 0L, active1, 8L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa4_4(active0, 0x100000000000000L, active1, 0L);
            }
            case 'v': {
                if ((active0 & 0x40000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(3, 42, 202);
            }
        }
        return this.jjStartNfa_4(2, active0, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa4_4(long old0, long active0, long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0L) {
            return this.jjStartNfa_4(2, old0, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(3, active0, active1, 0L, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active1 & 8L) == 0L) break;
                return this.jjStartNfaWithStates_4(4, 67, 202);
            }
            case 'n': {
                if ((active1 & 0x40L) == 0L) break;
                return this.jjStartNfaWithStates_4(4, 70, 202);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_4(active0, 0L, active1, 128L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa5_4(active0, 0x100004000000000L, active1, 32L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa5_4(active0, 0x80000000000000L, active1, 0L);
            }
        }
        return this.jjStartNfa_4(3, active0, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa5_4(long old0, long active0, long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0L) {
            return this.jjStartNfa_4(3, old0, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(4, active0, active1, 0L, 0L);
            return 5;
        }
        switch (this.curChar) {
            case 'f': {
                return this.jjMoveStringLiteralDfa6_4(active0, 0x80000000000000L, active1, 0L);
            }
            case 'n': {
                if ((active0 & 0x100000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_4(5, 56, 202);
                }
                return this.jjMoveStringLiteralDfa6_4(active0, 0x4000000000L, active1, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa6_4(active0, 0L, active1, 32L);
            }
            case 't': {
                if ((active1 & 0x80L) == 0L) break;
                return this.jjStartNfaWithStates_4(5, 71, 202);
            }
        }
        return this.jjStartNfa_4(4, active0, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa6_4(long old0, long active0, long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0L) {
            return this.jjStartNfa_4(4, old0, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(5, active0, active1, 0L, 0L);
            return 6;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa7_4(active0, 0x4000000000L, active1, 0L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa7_4(active0, 0L, active1, 32L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa7_4(active0, 0x80000000000000L, active1, 0L);
            }
        }
        return this.jjStartNfa_4(5, active0, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa7_4(long old0, long active0, long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0L) {
            return this.jjStartNfa_4(5, old0, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(6, active0, active1, 0L, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa8_4(active0, 0L, active1, 32L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa8_4(active0, 0x80000000000000L, active1, 0L);
            }
            case 'l': {
                if ((active0 & 0x4000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(7, 38, 202);
            }
        }
        return this.jjStartNfa_4(6, active0, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa8_4(long old0, long active0, long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0L) {
            return this.jjStartNfa_4(6, old0, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_4(7, active0, active1, 0L, 0L);
            return 8;
        }
        switch (this.curChar) {
            case 's': {
                if ((active0 & 0x80000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_4(8, 55, 202);
            }
            case 't': {
                if ((active1 & 0x20L) == 0L) break;
                return this.jjStartNfaWithStates_4(8, 69, 202);
            }
        }
        return this.jjStartNfa_4(7, active0, active1, 0L, 0L);
    }

    private final int jjMoveNfa_4(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 202;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block259: do {
                    switch (this.jjstateSet[--i]) {
                        case 51: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 101: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 161: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 44: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 14: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 202: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 71: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if ((0x100002600L & l) != 0L) {
                                this.jjCheckNAddStates(16, 18);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            } else if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(61, 62);
                            } else if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(58, 59);
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 99: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 124: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 83: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 131: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 55: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 82: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 52: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 140: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 0: 
                        case 15: {
                            if ((0x100002600L & l) == 0L) continue block259;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 100: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 2: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 3: {
                            if (this.curChar != '(' || kind <= 150) continue block259;
                            kind = 150;
                            break;
                        }
                        case 7: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 17: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(19, 20);
                            break;
                        }
                        case 18: {
                            if (this.curChar != '(' || kind <= 162) continue block259;
                            kind = 162;
                            break;
                        }
                        case 29: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 41: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(21, 22);
                            break;
                        }
                        case 42: {
                            if (this.curChar != '(' || kind <= 165) continue block259;
                            kind = 165;
                            break;
                        }
                        case 47: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(23, 24);
                            break;
                        }
                        case 56: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(16, 18);
                            break;
                        }
                        case 57: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(58, 59);
                            break;
                        }
                        case 58: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(58, 59);
                            break;
                        }
                        case 59: {
                            if (this.curChar != '\"') continue block259;
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAdd(57);
                            break;
                        }
                        case 60: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddTwoStates(61, 62);
                            break;
                        }
                        case 61: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(61, 62);
                            break;
                        }
                        case 62: {
                            if (this.curChar != '\'') continue block259;
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAdd(60);
                            break;
                        }
                        case 64: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(25, 26);
                            break;
                        }
                        case 65: {
                            if (this.curChar != '(' || kind <= 158) continue block259;
                            kind = 158;
                            break;
                        }
                        case 75: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(27, 28);
                            break;
                        }
                        case 85: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(29, 30);
                            break;
                        }
                        case 86: {
                            if (this.curChar != '(') break;
                            this.jjCheckNAddTwoStates(87, 88);
                            break;
                        }
                        case 87: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(87, 88);
                            break;
                        }
                        case 88: {
                            if (this.curChar != ')' || kind <= 77) continue block259;
                            kind = 77;
                            break;
                        }
                        case 93: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(31, 32);
                            break;
                        }
                        case 103: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(33, 34);
                            break;
                        }
                        case 109: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(35, 36);
                            break;
                        }
                        case 110: {
                            if (this.curChar != '(' || kind <= 164) continue block259;
                            kind = 164;
                            break;
                        }
                        case 118: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(37, 38);
                            break;
                        }
                        case 119: {
                            if (this.curChar != '(') break;
                            this.jjCheckNAddTwoStates(120, 121);
                            break;
                        }
                        case 120: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(120, 121);
                            break;
                        }
                        case 121: {
                            if (this.curChar != ')' || kind <= 96) continue block259;
                            kind = 96;
                            break;
                        }
                        case 126: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(39, 40);
                            break;
                        }
                        case 127: {
                            if (this.curChar != '(' || kind <= 156) continue block259;
                            kind = 156;
                            break;
                        }
                        case 135: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(41, 42);
                            break;
                        }
                        case 142: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(43, 44);
                            break;
                        }
                        case 143: {
                            if (this.curChar != '(' || kind <= 163) continue block259;
                            kind = 163;
                            break;
                        }
                        case 148: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(45, 46);
                            break;
                        }
                        case 149: {
                            if (this.curChar != '(' || kind <= 159) continue block259;
                            kind = 159;
                            break;
                        }
                        case 156: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 155;
                            break;
                        }
                        case 163: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(47, 48);
                            break;
                        }
                        case 164: {
                            if (this.curChar != '(' || kind <= 161) continue block259;
                            kind = 161;
                            break;
                        }
                        case 173: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 172;
                            break;
                        }
                        case 180: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(49, 50);
                            break;
                        }
                        case 182: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(51, 52);
                            break;
                        }
                        case 194: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 195: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 196;
                            break;
                        }
                        case 197: {
                            if ((0x3FF600000000000L & l) == 0L) continue block259;
                            if (kind > 183) {
                                kind = 183;
                            }
                            this.jjCheckNAdd(197);
                            break;
                        }
                        case 198: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(198, 199);
                            break;
                        }
                        case 199: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 200;
                            break;
                        }
                        case 201: {
                            if ((0x3FF600000000000L & l) == 0L) continue block259;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block260: do {
                    switch (this.jjstateSet[--i]) {
                        case 51: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 50;
                            break;
                        }
                        case 101: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 114;
                            } else if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 106;
                            }
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 100;
                            break;
                        }
                        case 161: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 191;
                            } else if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 177;
                            }
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 160;
                            break;
                        }
                        case 44: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 43;
                            break;
                        }
                        case 14: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 202: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 71: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 70;
                            break;
                        }
                        case 99: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 103;
                            }
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 98;
                            break;
                        }
                        case 124: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 131;
                                break;
                            }
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 123;
                            break;
                        }
                        case 83: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 89;
                                break;
                            }
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 82;
                            break;
                        }
                        case 131: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 130;
                            break;
                        }
                        case 55: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 71;
                            }
                            if (this.curChar != 't') break;
                            this.jjAddStates(16, 18);
                            break;
                        }
                        case 82: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 81;
                            break;
                        }
                        case 52: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            break;
                        }
                        case 140: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 144;
                                break;
                            }
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 139;
                            break;
                        }
                        case 15: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAddStates(53, 58);
                            }
                            if (this.curChar == 's') {
                                this.jjAddStates(59, 61);
                                break;
                            }
                            if (this.curChar == 't') {
                                this.jjAddStates(62, 63);
                                break;
                            }
                            if (this.curChar == 'e') {
                                this.jjAddStates(64, 65);
                                break;
                            }
                            if (this.curChar == 'c') {
                                this.jjAddStates(66, 68);
                                break;
                            }
                            if (this.curChar == 'i') {
                                this.jjAddStates(69, 70);
                                break;
                            }
                            if (this.curChar == 'a') {
                                this.jjAddStates(71, 72);
                                break;
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
                                break;
                            }
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 44;
                                break;
                            }
                            if (this.curChar == 'p') {
                                this.jjstateSet[this.jjnewStateCnt++] = 38;
                                break;
                            }
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 100: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 184) {
                                    kind = 184;
                                }
                                this.jjCheckNAdd(201);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (this.curChar == 's') {
                                this.jjstateSet[this.jjnewStateCnt++] = 102;
                            }
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 99;
                            break;
                        }
                        case 1: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 4: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'n') break;
                            this.jjAddStates(19, 20);
                            break;
                        }
                        case 19: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 21: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 22: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 23: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 24: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 25: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
                            break;
                        }
                        case 26: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 27: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 28: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 30: {
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 31: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                        case 32: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 31;
                            break;
                        }
                        case 33: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 32;
                            break;
                        }
                        case 34: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 33;
                            break;
                        }
                        case 35: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 34;
                            break;
                        }
                        case 36: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 35;
                            break;
                        }
                        case 37: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 38: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 39: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 40: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(21, 22);
                            break;
                        }
                        case 43: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 45: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 44;
                            break;
                        }
                        case 46: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 47;
                            break;
                        }
                        case 48: {
                            if (this.curChar != 'y' || kind <= 176) continue block260;
                            kind = 176;
                            break;
                        }
                        case 49: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 48;
                            break;
                        }
                        case 50: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 46;
                            break;
                        }
                        case 53: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 52;
                            break;
                        }
                        case 54: {
                            if (this.curChar != 'a') break;
                            this.jjAddStates(71, 72);
                            break;
                        }
                        case 58: {
                            this.jjAddStates(73, 74);
                            break;
                        }
                        case 61: {
                            this.jjAddStates(75, 76);
                            break;
                        }
                        case 63: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(25, 26);
                            break;
                        }
                        case 66: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 63;
                            break;
                        }
                        case 67: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 66;
                            break;
                        }
                        case 68: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 67;
                            break;
                        }
                        case 69: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 68;
                            break;
                        }
                        case 70: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 69;
                            break;
                        }
                        case 72: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 71;
                            break;
                        }
                        case 73: {
                            if (this.curChar != 'i') break;
                            this.jjAddStates(69, 70);
                            break;
                        }
                        case 74: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 75;
                            break;
                        }
                        case 76: {
                            if (this.curChar != 'f' || kind <= 75) continue block260;
                            kind = 75;
                            break;
                        }
                        case 77: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 76;
                            break;
                        }
                        case 78: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 74;
                            break;
                        }
                        case 79: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 78;
                            break;
                        }
                        case 80: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 79;
                            break;
                        }
                        case 81: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 80;
                            break;
                        }
                        case 84: {
                            if (this.curChar != 'm') break;
                            this.jjAddStates(29, 30);
                            break;
                        }
                        case 89: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 84;
                            break;
                        }
                        case 90: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 89;
                            break;
                        }
                        case 91: {
                            if (this.curChar != 'c') break;
                            this.jjAddStates(66, 68);
                            break;
                        }
                        case 92: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 93;
                            break;
                        }
                        case 94: {
                            if (this.curChar != 's' || kind <= 76) continue block260;
                            kind = 76;
                            break;
                        }
                        case 95: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 94;
                            break;
                        }
                        case 96: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 92;
                            break;
                        }
                        case 97: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 96;
                            break;
                        }
                        case 98: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 97;
                            break;
                        }
                        case 102: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 103;
                            break;
                        }
                        case 104: {
                            if (this.curChar != 's' || kind <= 144) continue block260;
                            kind = 144;
                            break;
                        }
                        case 105: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 104;
                            break;
                        }
                        case 106: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 102;
                            break;
                        }
                        case 107: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 106;
                            break;
                        }
                        case 108: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(35, 36);
                            break;
                        }
                        case 111: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 108;
                            break;
                        }
                        case 112: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 111;
                            break;
                        }
                        case 113: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 112;
                            break;
                        }
                        case 114: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 113;
                            break;
                        }
                        case 115: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 114;
                            break;
                        }
                        case 116: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(64, 65);
                            break;
                        }
                        case 117: {
                            if (this.curChar != 'y') break;
                            this.jjAddStates(37, 38);
                            break;
                        }
                        case 122: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 117;
                            break;
                        }
                        case 123: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 122;
                            break;
                        }
                        case 125: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(39, 40);
                            break;
                        }
                        case 128: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 125;
                            break;
                        }
                        case 129: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 128;
                            break;
                        }
                        case 130: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 129;
                            break;
                        }
                        case 132: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 131;
                            break;
                        }
                        case 133: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(62, 63);
                            break;
                        }
                        case 134: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 135;
                            break;
                        }
                        case 136: {
                            if (this.curChar != 's' || kind <= 145) continue block260;
                            kind = 145;
                            break;
                        }
                        case 137: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 136;
                            break;
                        }
                        case 138: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 134;
                            break;
                        }
                        case 139: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 138;
                            break;
                        }
                        case 141: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(43, 44);
                            break;
                        }
                        case 144: {
                            if (this.curChar != 'x') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 141;
                            break;
                        }
                        case 145: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 144;
                            break;
                        }
                        case 146: {
                            if (this.curChar != 's') break;
                            this.jjAddStates(59, 61);
                            break;
                        }
                        case 147: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(45, 46);
                            break;
                        }
                        case 150: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 147;
                            break;
                        }
                        case 151: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 150;
                            break;
                        }
                        case 152: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 151;
                            break;
                        }
                        case 153: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 152;
                            break;
                        }
                        case 154: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 153;
                            break;
                        }
                        case 155: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 154;
                            break;
                        }
                        case 157: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 156;
                            break;
                        }
                        case 158: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 157;
                            break;
                        }
                        case 159: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 158;
                            break;
                        }
                        case 160: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 159;
                            break;
                        }
                        case 162: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(47, 48);
                            break;
                        }
                        case 165: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 162;
                            break;
                        }
                        case 166: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 165;
                            break;
                        }
                        case 167: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 166;
                            break;
                        }
                        case 168: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 167;
                            break;
                        }
                        case 169: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 168;
                            break;
                        }
                        case 170: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 169;
                            break;
                        }
                        case 171: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 170;
                            break;
                        }
                        case 172: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 171;
                            break;
                        }
                        case 174: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 173;
                            break;
                        }
                        case 175: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 174;
                            break;
                        }
                        case 176: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 175;
                            break;
                        }
                        case 177: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 176;
                            break;
                        }
                        case 178: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 177;
                            break;
                        }
                        case 179: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 180;
                            break;
                        }
                        case 181: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 182;
                            break;
                        }
                        case 183: {
                            if (this.curChar != 'y' || kind <= 177) continue block260;
                            kind = 177;
                            break;
                        }
                        case 184: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 183;
                            break;
                        }
                        case 185: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 181;
                            break;
                        }
                        case 186: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 185;
                            break;
                        }
                        case 187: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 186;
                            break;
                        }
                        case 188: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 187;
                            break;
                        }
                        case 189: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 179;
                            break;
                        }
                        case 190: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 189;
                            break;
                        }
                        case 191: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 190;
                            break;
                        }
                        case 192: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 191;
                            break;
                        }
                        case 193: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block260;
                            if (kind > 183) {
                                kind = 183;
                            }
                            this.jjCheckNAddStates(53, 58);
                            break;
                        }
                        case 194: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 196: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block260;
                            if (kind > 183) {
                                kind = 183;
                            }
                            this.jjCheckNAdd(197);
                            break;
                        }
                        case 197: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block260;
                            if (kind > 183) {
                                kind = 183;
                            }
                            this.jjCheckNAdd(197);
                            break;
                        }
                        case 198: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(198, 199);
                            break;
                        }
                        case 200: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block260;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 201: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block260;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
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
                block261: do {
                    switch (this.jjstateSet[--i]) {
                        case 51: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 101: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 161: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 44: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 14: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 202: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 71: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 99: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 124: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 83: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 131: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 55: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 82: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 52: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 140: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 15: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block261;
                            if (kind > 183) {
                                kind = 183;
                            }
                            this.jjCheckNAddStates(53, 58);
                            break;
                        }
                        case 100: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(194, 195);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 183) {
                                    kind = 183;
                                }
                                this.jjCheckNAdd(197);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(198, 199);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 58: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(73, 74);
                            break;
                        }
                        case 61: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(75, 76);
                            break;
                        }
                        case 194: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 196: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block261;
                            if (kind > 183) {
                                kind = 183;
                            }
                            this.jjCheckNAdd(197);
                            break;
                        }
                        case 197: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block261;
                            if (kind > 183) {
                                kind = 183;
                            }
                            this.jjCheckNAdd(197);
                            break;
                        }
                        case 198: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(198, 199);
                            break;
                        }
                        case 200: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block261;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 201: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block261;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(201);
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
            if (i == (startsAt = 202 - this.jjnewStateCnt)) {
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

    private final int jjMoveStringLiteralDfa0_18() {
        return this.jjMoveNfa_18(1, 0);
    }

    private final int jjMoveNfa_18(int startState, int curPos) {
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
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 1: {
                            if ((0x100002600L & l) == 0L) continue block20;
                            kind = 239;
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 2: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 3: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 5: {
                            if ((0x3FF600000000000L & l) == 0L) continue block20;
                            if (kind > 186) {
                                kind = 186;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block21: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 186) {
                                kind = 186;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: 
                        case 5: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 186) {
                                kind = 186;
                            }
                            this.jjCheckNAdd(5);
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
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 186) {
                                kind = 186;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 186) {
                                kind = 186;
                            }
                            this.jjCheckNAdd(5);
                            break;
                        }
                        case 5: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 186) {
                                kind = 186;
                            }
                            this.jjCheckNAdd(5);
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

    private final int jjStopStringLiteralDfa_13(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active3 & 0x8000L) != 0L) {
                    this.jjmatchedKind = 210;
                    return -1;
                }
                return -1;
            }
            case 1: {
                if ((active3 & 0x8000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 210;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_13(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_13(this.jjStopStringLiteralDfa_13(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_13(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_13(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_13() {
        switch (this.curChar) {
            case '<': {
                this.jjmatchedKind = 196;
                return this.jjMoveStringLiteralDfa1_13(65536L, Long.MIN_VALUE, 0x20000000100L);
            }
            case '{': {
                this.jjmatchedKind = 204;
                return this.jjMoveStringLiteralDfa1_13(0L, 0L, 16384L);
            }
            case '}': {
                return this.jjMoveStringLiteralDfa1_13(0L, 0L, 32768L);
            }
        }
        return this.jjMoveNfa_13(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_13(long active0, long active2, long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_13(0, active0, 0L, active2, active3);
            return 1;
        }
        switch (this.curChar) {
            case '!': {
                return this.jjMoveStringLiteralDfa2_13(active0, 0L, active2, Long.MIN_VALUE, active3, 0x20000000000L);
            }
            case '/': {
                if ((active3 & 0x100L) == 0L) break;
                return this.jjStopAtPos(1, 200);
            }
            case '?': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStopAtPos(1, 16);
            }
            case '{': {
                if ((active3 & 0x4000L) == 0L) break;
                return this.jjStopAtPos(1, 206);
            }
            case '}': {
                if ((active3 & 0x8000L) == 0L) break;
                return this.jjStopAtPos(1, 207);
            }
        }
        return this.jjStartNfa_13(0, active0, 0L, active2, active3);
    }

    private final int jjMoveStringLiteralDfa2_13(long old0, long active0, long old2, long active2, long old3, long active3) {
        if (((active0 &= old0) | (active2 &= old2) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_13(0, old0, 0L, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_13(1, 0L, 0L, active2, active3);
            return 2;
        }
        switch (this.curChar) {
            case '-': {
                return this.jjMoveStringLiteralDfa3_13(active2, 0L, active3, 0x20000000000L);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa3_13(active2, Long.MIN_VALUE, active3, 0L);
            }
        }
        return this.jjStartNfa_13(1, 0L, 0L, active2, active3);
    }

    private final int jjMoveStringLiteralDfa3_13(long old2, long active2, long old3, long active3) {
        if (((active2 &= old2) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_13(1, 0L, 0L, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_13(2, 0L, 0L, active2, active3);
            return 3;
        }
        switch (this.curChar) {
            case '-': {
                if ((active3 & 0x20000000000L) == 0L) break;
                return this.jjStopAtPos(3, 233);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa4_13(active2, Long.MIN_VALUE, active3, 0L);
            }
        }
        return this.jjStartNfa_13(2, 0L, 0L, active2, active3);
    }

    private final int jjMoveStringLiteralDfa4_13(long old2, long active2, long old3, long active3) {
        if (((active2 &= old2) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_13(2, 0L, 0L, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_13(3, 0L, 0L, active2, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'D': {
                return this.jjMoveStringLiteralDfa5_13(active2, Long.MIN_VALUE);
            }
        }
        return this.jjStartNfa_13(3, 0L, 0L, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa5_13(long old2, long active2) {
        if ((active2 &= old2) == 0L) {
            return this.jjStartNfa_13(3, 0L, 0L, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_13(4, 0L, 0L, active2, 0L);
            return 5;
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa6_13(active2, Long.MIN_VALUE);
            }
        }
        return this.jjStartNfa_13(4, 0L, 0L, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa6_13(long old2, long active2) {
        if ((active2 &= old2) == 0L) {
            return this.jjStartNfa_13(4, 0L, 0L, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_13(5, 0L, 0L, active2, 0L);
            return 6;
        }
        switch (this.curChar) {
            case 'T': {
                return this.jjMoveStringLiteralDfa7_13(active2, Long.MIN_VALUE);
            }
        }
        return this.jjStartNfa_13(5, 0L, 0L, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa7_13(long old2, long active2) {
        if ((active2 &= old2) == 0L) {
            return this.jjStartNfa_13(5, 0L, 0L, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_13(6, 0L, 0L, active2, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa8_13(active2, Long.MIN_VALUE);
            }
        }
        return this.jjStartNfa_13(6, 0L, 0L, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa8_13(long old2, long active2) {
        if ((active2 &= old2) == 0L) {
            return this.jjStartNfa_13(6, 0L, 0L, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_13(7, 0L, 0L, active2, 0L);
            return 8;
        }
        switch (this.curChar) {
            case '[': {
                if ((active2 & Long.MIN_VALUE) == 0L) break;
                return this.jjStopAtPos(8, 191);
            }
        }
        return this.jjStartNfa_13(7, 0L, 0L, active2, 0L);
    }

    private final int jjMoveNfa_13(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 21;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block33: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFF00002600L & l) != 0L && kind > 210) {
                                kind = 210;
                            }
                            if (this.curChar == '&') {
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                            }
                            if (this.curChar != '&') break;
                            this.jjAddStates(77, 80);
                            break;
                        }
                        case 2: {
                            if (this.curChar != ';' || kind <= 193) continue block33;
                            kind = 193;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddTwoStates(15, 17);
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(15, 16);
                            break;
                        }
                        case 16: {
                            if (this.curChar != ';' || kind <= 194) continue block33;
                            kind = 194;
                            break;
                        }
                        case 18: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(18, 16);
                            break;
                        }
                        case 19: {
                            if (this.curChar != '&') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 20: {
                            if ((0xFFFFFFFF00002600L & l) == 0L || kind <= 210) continue block33;
                            kind = 210;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind <= 210) break;
                            kind = 210;
                            break;
                        }
                        case 1: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 3: {
                            if (this.curChar != 'l') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 4: {
                            if (this.curChar != 'g') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'o') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'q') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'a') break;
                            this.jjAddStates(81, 82);
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'p') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 's') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'x') break;
                            this.jjCheckNAdd(18);
                            break;
                        }
                        case 18: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(18, 16);
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
                block35: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!XPathTokenManager.jjCanMove_3(hiByte, i1, i2, l1, l2) || kind <= 210) continue block35;
                            kind = 210;
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
            if (i == (startsAt = 21 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_1(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x80000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    return 57;
                }
                if ((active0 & 0x240000000000L) != 0L || (active1 & 0x800000000020L) != 0L) {
                    this.jjmatchedKind = 249;
                    return 24;
                }
                if ((active1 & 0x600000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    return 34;
                }
                if ((active1 & 0x410L) != 0L) {
                    this.jjmatchedKind = 249;
                    return 82;
                }
                if ((active0 & 0x400004000000000L) != 0L || (active1 & 0x20000000000080L) != 0L) {
                    this.jjmatchedKind = 249;
                    return 102;
                }
                if ((active0 & 0x9000B3200000000L) != 0L || (active1 & 0x1C0000000000348L) != 0L || (active2 & 0xC000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    return 52;
                }
                if ((active0 & 0x200000000000000L) != 0L || (active1 & 4L) != 0L) {
                    this.jjmatchedKind = 249;
                    return 42;
                }
                if ((active0 & 0x8100000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    return 50;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x8100000000L) != 0L) {
                    return 49;
                }
                if ((active0 & 0x200000000000L) != 0L || (active1 & 0x20L) != 0L) {
                    return 23;
                }
                if ((active1 & 0x400000000000000L) != 0L) {
                    return 31;
                }
                if ((active0 & 0x2000000000L) != 0L || (active1 & 0x3E0800000000304L) != 0L || (active2 & 0x4000000000000L) != 0L) {
                    return 52;
                }
                if ((active1 & 0x400L) != 0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 249;
                        this.jjmatchedPos = 1;
                    }
                    return 81;
                }
                if ((active0 & 0xF800F5200000000L) != 0L || (active1 & 0xD8L) != 0L || (active2 & 0x8000000000000L) != 0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 249;
                        this.jjmatchedPos = 1;
                    }
                    return 52;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0xF80044200000000L) != 0L || (active1 & 0xF8L) != 0L || (active2 & 0xC000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 2;
                    return 52;
                }
                if ((active1 & 0x400L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 2;
                    return 80;
                }
                if ((active0 & 0xB1000000000L) != 0L) {
                    return 52;
                }
                if ((active0 & 0x100000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 2;
                    return 48;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x980004200000000L) != 0L || (active1 & 0xF8L) != 0L || (active2 & 0xC000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 3;
                    return 52;
                }
                if ((active0 & 0x100000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 3;
                    return 44;
                }
                if ((active0 & 0x600040000000000L) != 0L || (active1 & 0x400L) != 0L) {
                    return 52;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x100000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 4;
                    return 113;
                }
                if ((active1 & 0x48L) != 0L) {
                    return 52;
                }
                if ((active0 & 0x980004200000000L) != 0L || (active1 & 0xB0L) != 0L || (active2 & 0xC000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 4;
                    return 52;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x880004300000000L) != 0L || (active1 & 0x30L) != 0L || (active2 & 0xC000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 5;
                    return 52;
                }
                if ((active0 & 0x100000000000000L) != 0L || (active1 & 0x80L) != 0L) {
                    return 52;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x80004200000000L) != 0L || (active1 & 0x30L) != 0L || (active2 & 0xC000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 6;
                    return 52;
                }
                if ((active0 & 0x800000100000000L) != 0L) {
                    return 52;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x4000000000L) != 0L) {
                    return 52;
                }
                if ((active0 & 0x80000200000000L) != 0L || (active1 & 0x30L) != 0L || (active2 & 0xC000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 7;
                    return 52;
                }
                return -1;
            }
            case 8: {
                if ((active2 & 0x8000000000000L) != 0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 8;
                    return 52;
                }
                if ((active0 & 0x80000200000000L) != 0L || (active1 & 0x30L) != 0L || (active2 & 0x4000000000000L) != 0L) {
                    return 52;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_1(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0, active1, active2, active3), pos + 1);
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
            case '!': {
                return this.jjMoveStringLiteralDfa1_1(0L, 0x1000000000000L, 0L, 0L);
            }
            case '$': {
                return this.jjStopAtPos(0, 49);
            }
            case '%': {
                return this.jjMoveStringLiteralDfa1_1(0L, 0L, 0x80000000000L, 0L);
            }
            case '(': {
                return this.jjMoveStringLiteralDfa1_1(0L, 0L, 0L, 0x840000000L);
            }
            case ')': {
                return this.jjStopAtPos(0, 138);
            }
            case '*': {
                return this.jjStopAtPos(0, 44);
            }
            case '+': {
                return this.jjStopAtPos(0, 127);
            }
            case ',': {
                return this.jjStopAtPos(0, 168);
            }
            case '-': {
                return this.jjStopAtPos(0, 126);
            }
            case '/': {
                this.jjmatchedKind = 107;
                return this.jjMoveStringLiteralDfa1_1(0L, 0x100000000000L, 0L, 0L);
            }
            case ':': {
                return this.jjMoveStringLiteralDfa1_1(0L, 0x800000000000000L, 0L, 0L);
            }
            case ';': {
                return this.jjStopAtPos(0, 170);
            }
            case '<': {
                this.jjmatchedKind = 124;
                return this.jjMoveStringLiteralDfa1_1(0L, 0x6000000000000L, 0L, 0L);
            }
            case '=': {
                return this.jjStopAtPos(0, 109);
            }
            case '>': {
                this.jjmatchedKind = 125;
                return this.jjMoveStringLiteralDfa1_1(0L, 0x18000000000000L, 0L, 0L);
            }
            case '?': {
                return this.jjStopAtPos(0, 130);
            }
            case '[': {
                return this.jjStopAtPos(0, 136);
            }
            case ']': {
                return this.jjStopAtPos(0, 137);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_1(0x10000000000L, 768L, 0x4000000000000L, 0L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa1_1(0L, 1040L, 0L, 0L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_1(0x800020000000000L, 0L, 0x8000000000000L, 0L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_1(0x400004000000000L, 0x20000000000080L, 0L, 0L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa1_1(0L, 0x180000000000000L, 0L, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_1(0x240000000000L, 0x800000000020L, 0L, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_1(0L, 0x600000000000000L, 0L, 0L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_1(0x80000000000L, 0L, 0L, 0L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_1(0x2000000000L, 0x40000000000000L, 0L, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_1(0x8100000000L, 0L, 0L, 0L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa1_1(0x100000000000000L, 0L, 0L, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_1(0x80000000000000L, 0L, 0L, 0L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_1(0x200000000000000L, 4L, 0L, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa1_1(0x200000000L, 64L, 0L, 0L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa1_1(0L, 8L, 0L, 0L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa1_1(0x1000000000L, 0L, 0L, 0L);
            }
            case '{': {
                return this.jjStopAtPos(0, 205);
            }
            case '|': {
                return this.jjStopAtPos(0, 133);
            }
            case '}': {
                return this.jjStopAtPos(0, 241);
            }
        }
        return this.jjMoveNfa_1(14, 0);
    }

    private final int jjMoveStringLiteralDfa1_1(long active0, long active1, long active2, long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(0, active0, active1, active2, active3);
            return 1;
        }
        switch (this.curChar) {
            case '%': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0L, active1, 0L, active2, 0x80000000000L, active3, 0L);
            }
            case '/': {
                if ((active1 & 0x100000000000L) == 0L) break;
                return this.jjStopAtPos(1, 108);
            }
            case ':': {
                if ((active3 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 0L, active1, 0L, active2, 0L, active3, 0x40000000L);
            }
            case '<': {
                if ((active1 & 0x4000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 114);
            }
            case '=': {
                if ((active1 & 0x1000000000000L) != 0L) {
                    return this.jjStopAtPos(1, 112);
                }
                if ((active1 & 0x2000000000000L) != 0L) {
                    return this.jjStopAtPos(1, 113);
                }
                if ((active1 & 0x8000000000000L) != 0L) {
                    return this.jjStopAtPos(1, 115);
                }
                if ((active1 & 0x800000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 123);
            }
            case '>': {
                if ((active1 & 0x10000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 116);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x80000000000000L, active1, 1024L, active2, 0L, active3, 0L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x40000000000L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 'e': {
                if ((active1 & 0x40000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 118, 52);
                }
                if ((active1 & 0x100000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 120, 52);
                }
                if ((active1 & 0x400000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 122, 31);
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 0x900001000000000L, active1, 0L, active2, 0x8000000000000L, active3, 0L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x200000000000000L, active1, 8L, active2, 0L, active3, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x20000000000L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x400000000000000L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 'n': {
                if ((active0 & 0x200000000000L) != 0L) {
                    this.jjmatchedKind = 45;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 0x10200000000L, active1, 96L, active2, 0L, active3, 0L);
            }
            case 'o': {
                if ((active0 & 0x2000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 37, 52);
                }
                if ((active1 & 4L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 66, 52);
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 0x80000000000L, active1, 16L, active2, 0L, active3, 0L);
            }
            case 'q': {
                if ((active1 & 0x20000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(1, 117, 52);
            }
            case 'r': {
                if ((active0 & 0x8000000000L) != 0L) {
                    this.jjmatchedKind = 39;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 0x100000000L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 's': {
                if ((active1 & 0x100L) != 0L) {
                    this.jjmatchedKind = 72;
                    this.jjmatchedPos = 1;
                } else if ((active1 & 0x800000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 111, 52);
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 0L, active1, 0L, active2, 0x4000000000000L, active3, 0L);
            }
            case 't': {
                if ((active1 & 0x200L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 73, 52);
                }
                if ((active1 & 0x80000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 119, 52);
                }
                if ((active1 & 0x200000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(1, 121, 52);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x4000000000L, active1, 128L, active2, 0L, active3, 0L);
            }
        }
        return this.jjStartNfa_1(0, active0, active1, active2, active3);
    }

    private final int jjMoveStringLiteralDfa2_1(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_1(0, old0, old1, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(1, active0, active1, active2, active3);
            return 2;
        }
        switch (this.curChar) {
            case '%': {
                if ((active2 & 0x80000000000L) == 0L) break;
                return this.jjStopAtPos(2, 171);
            }
            case ':': {
                if ((active3 & 0x40000000L) == 0L) break;
                return this.jjStopAtPos(2, 222);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0L, active1, 128L, active2, 0x4000000000000L, active3, 0L);
            }
            case 'd': {
                if ((active0 & 0x10000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(2, 40, 52);
                }
                if ((active0 & 0x80000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(2, 43, 52);
                }
                return this.jjMoveStringLiteralDfa3_1(active0, 0x100000000L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x200000000000000L, active1, 8L, active2, 0L, active3, 0L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x800000000000000L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x40000000000L, active1, 64L, active2, 0L, active3, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0L, active1, 16L, active2, 0L, active3, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x200000000L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 's': {
                if ((active0 & 0x1000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(2, 36, 52);
                }
                return this.jjMoveStringLiteralDfa3_1(active0, 0x400000000000000L, active1, 1024L, active2, 0x8000000000000L, active3, 0L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa3_1(active0, 108086665934798848L, active1, 32L, active2, 0L, active3, 0L);
            }
            case 'v': {
                if ((active0 & 0x20000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(2, 41, 52);
            }
        }
        return this.jjStartNfa_1(1, active0, active1, active2, active3);
    }

    private final int jjMoveStringLiteralDfa3_1(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_1(1, old0, old1, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(2, active0, active1, active2, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa4_1(active0, 0x800000000000000L, active1, 0L, active2, 0L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa4_1(active0, 0L, active1, 0L, active2, 0x8000000000000L);
            }
            case 'e': {
                if ((active0 & 0x400000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(3, 58, 52);
                }
                if ((active1 & 0x400L) != 0L) {
                    return this.jjStartNfaWithStates_1(3, 74, 52);
                }
                return this.jjMoveStringLiteralDfa4_1(active0, 0x4100000000L, active1, 160L, active2, 0x4000000000000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_1(active0, 0x80000000000000L, active1, 0L, active2, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa4_1(active0, 0L, active1, 16L, active2, 0L);
            }
            case 'n': {
                if ((active0 & 0x200000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(3, 57, 52);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa4_1(active0, 0L, active1, 64L, active2, 0L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa4_1(active0, 0x200000000L, active1, 8L, active2, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa4_1(active0, 0x100000000000000L, active1, 0L, active2, 0L);
            }
            case 'v': {
                if ((active0 & 0x40000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(3, 42, 52);
            }
        }
        return this.jjStartNfa_1(2, active0, active1, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa4_1(long old0, long active0, long old1, long active1, long old2, long active2) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L) {
            return this.jjStartNfa_1(2, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(3, active0, active1, active2, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa5_1(active0, 0L, active1, 16L, active2, 0L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa5_1(active0, 0x200000000L, active1, 0L, active2, 0L);
            }
            case 'e': {
                if ((active1 & 8L) != 0L) {
                    return this.jjStartNfaWithStates_1(4, 67, 52);
                }
                return this.jjMoveStringLiteralDfa5_1(active0, 0L, active1, 0L, active2, 0x8000000000000L);
            }
            case 'n': {
                if ((active1 & 0x40L) != 0L) {
                    return this.jjStartNfaWithStates_1(4, 70, 52);
                }
                return this.jjMoveStringLiteralDfa5_1(active0, 0L, active1, 0L, active2, 0x4000000000000L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_1(active0, 0L, active1, 128L, active2, 0L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa5_1(active0, 0x100004100000000L, active1, 32L, active2, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa5_1(active0, 0x80000000000000L, active1, 0L, active2, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa5_1(active0, 0x800000000000000L, active1, 0L, active2, 0L);
            }
        }
        return this.jjStartNfa_1(3, active0, active1, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa5_1(long old0, long active0, long old1, long active1, long old2, long active2) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L) {
            return this.jjStartNfa_1(3, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(4, active0, active1, active2, 0L);
            return 5;
        }
        switch (this.curChar) {
            case 'd': {
                return this.jjMoveStringLiteralDfa6_1(active0, 0L, active1, 0L, active2, 0x4000000000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa6_1(active0, 0x300000000L, active1, 0L, active2, 0L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa6_1(active0, 0x80000000000000L, active1, 0L, active2, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa6_1(active0, 0x800000000000000L, active1, 0L, active2, 0L);
            }
            case 'n': {
                if ((active0 & 0x100000000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(5, 56, 52);
                }
                return this.jjMoveStringLiteralDfa6_1(active0, 0x4000000000L, active1, 0L, active2, 0x8000000000000L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa6_1(active0, 0L, active1, 32L, active2, 0L);
            }
            case 't': {
                if ((active1 & 0x80L) != 0L) {
                    return this.jjStartNfaWithStates_1(5, 71, 52);
                }
                return this.jjMoveStringLiteralDfa6_1(active0, 0L, active1, 16L, active2, 0L);
            }
        }
        return this.jjStartNfa_1(4, active0, active1, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa6_1(long old0, long active0, long old1, long active1, long old2, long active2) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L) {
            return this.jjStartNfa_1(4, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(5, active0, active1, active2, 0L);
            return 6;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa7_1(active0, 0x4000000000L, active1, 0L, active2, 0L);
            }
            case 'd': {
                if ((active0 & 0x100000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(6, 32, 52);
                }
                return this.jjMoveStringLiteralDfa7_1(active0, 0L, active1, 0L, active2, 0x8000000000000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa7_1(active0, 0L, active1, 32L, active2, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa7_1(active0, 0x80000000000000L, active1, 16L, active2, 0x4000000000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa7_1(active0, 0x200000000L, active1, 0L, active2, 0L);
            }
            case 't': {
                if ((active0 & 0x800000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(6, 59, 52);
            }
        }
        return this.jjStartNfa_1(5, active0, active1, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa7_1(long old0, long active0, long old1, long active1, long old2, long active2) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L) {
            return this.jjStartNfa_1(5, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(6, active0, active1, active2, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa8_1(active0, 0L, active1, 32L, active2, 0L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa8_1(active0, 0x80000200000000L, active1, 0L, active2, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa8_1(active0, 0L, active1, 0L, active2, 0x8000000000000L);
            }
            case 'l': {
                if ((active0 & 0x4000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(7, 38, 52);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa8_1(active0, 0L, active1, 0L, active2, 0x4000000000000L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa8_1(active0, 0L, active1, 16L, active2, 0L);
            }
        }
        return this.jjStartNfa_1(6, active0, active1, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa8_1(long old0, long active0, long old1, long active1, long old2, long active2) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L) {
            return this.jjStartNfa_1(6, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(7, active0, active1, active2, 0L);
            return 8;
        }
        switch (this.curChar) {
            case 'd': {
                if ((active0 & 0x200000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(8, 33, 52);
            }
            case 'g': {
                if ((active2 & 0x4000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(8, 178, 52);
            }
            case 'n': {
                if ((active1 & 0x10L) != 0L) {
                    return this.jjStartNfaWithStates_1(8, 68, 52);
                }
                return this.jjMoveStringLiteralDfa9_1(active0, 0L, active1, 0L, active2, 0x8000000000000L);
            }
            case 's': {
                if ((active0 & 0x80000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(8, 55, 52);
            }
            case 't': {
                if ((active1 & 0x20L) == 0L) break;
                return this.jjStartNfaWithStates_1(8, 69, 52);
            }
        }
        return this.jjStartNfa_1(7, active0, active1, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa9_1(long old0, long active0, long old1, long active1, long old2, long active2) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0L) {
            return this.jjStartNfa_1(7, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(8, 0L, 0L, active2, 0L);
            return 9;
        }
        switch (this.curChar) {
            case 'g': {
                if ((active2 & 0x8000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(9, 179, 52);
            }
        }
        return this.jjStartNfa_1(8, 0L, 0L, active2, 0L);
    }

    private final int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 113;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block142: do {
                    switch (this.jjstateSet[--i]) {
                        case 44: 
                        case 52: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 81: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 24: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 49: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 42: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 113: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                                break;
                            }
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(45, 47);
                            break;
                        }
                        case 31: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 57: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 34: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 80: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 14: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                                break;
                            }
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(6);
                                break;
                            }
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(4, 5);
                                break;
                            }
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 48: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 50: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 82: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 102: {
                            if ((0x3FF000000000000L & l) == 0L) continue block142;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 0: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\"') continue block142;
                            if (kind > 4) {
                                kind = 4;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 4: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\'') continue block142;
                            if (kind > 4) {
                                kind = 4;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 6: {
                            if ((0x100002600L & l) == 0L) continue block142;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(6);
                            break;
                        }
                        case 16: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(83, 84);
                            break;
                        }
                        case 27: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(11, 12);
                            break;
                        }
                        case 28: {
                            if (this.curChar != '$' || kind <= 142) continue block142;
                            kind = 142;
                            break;
                        }
                        case 32: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(85, 86);
                            break;
                        }
                        case 33: {
                            if (this.curChar != '$' || kind <= 143) continue block142;
                            kind = 143;
                            break;
                        }
                        case 37: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(87, 88);
                            break;
                        }
                        case 45: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(45, 47);
                            break;
                        }
                        case 59: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(89, 90);
                            break;
                        }
                        case 61: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(91, 92);
                            break;
                        }
                        case 74: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(93, 94);
                            break;
                        }
                        case 84: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(95, 96);
                            break;
                        }
                        case 91: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(97, 98);
                            break;
                        }
                        case 104: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(99, 100);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block143: do {
                    switch (this.jjstateSet[--i]) {
                        case 44: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 45;
                            break;
                        }
                        case 81: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar == 's') {
                                this.jjstateSet[this.jjnewStateCnt++] = 83;
                            }
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 80;
                            break;
                        }
                        case 24: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 49: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 48;
                            break;
                        }
                        case 42: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 52: 
                        case 113: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L) continue block143;
                            if (kind > 249) {
                                kind = 249;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 31: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar != 't') break;
                            this.jjAddStates(85, 86);
                            break;
                        }
                        case 57: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 70;
                            }
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 56;
                            break;
                        }
                        case 34: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 31;
                            break;
                        }
                        case 80: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 84;
                            }
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 79;
                            break;
                        }
                        case 23: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 14: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar == 'e') {
                                this.jjAddStates(101, 102);
                                break;
                            }
                            if (this.curChar == 'c') {
                                this.jjAddStates(103, 104);
                                break;
                            }
                            if (this.curChar == 's') {
                                this.jjAddStates(105, 106);
                                break;
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 50;
                                break;
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 42;
                                break;
                            }
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 34;
                                break;
                            }
                            if (this.curChar == 'f') {
                                this.jjstateSet[this.jjnewStateCnt++] = 29;
                                break;
                            }
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 24;
                                break;
                            }
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 48: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 44;
                            break;
                        }
                        case 50: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 49;
                            break;
                        }
                        case 82: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 87;
                            }
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 81;
                            break;
                        }
                        case 102: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0L) {
                                if (kind > 249) {
                                    kind = 249;
                                }
                                this.jjCheckNAdd(52);
                            }
                            if (this.curChar == 'm') {
                                this.jjstateSet[this.jjnewStateCnt++] = 111;
                            }
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 101;
                            break;
                        }
                        case 1: {
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'e' || kind <= 52) continue block143;
                            kind = 52;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'v') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 15: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'f' || kind <= 75) continue block143;
                            kind = 75;
                            break;
                        }
                        case 18: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 19: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 21: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 22: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 25: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
                            break;
                        }
                        case 26: {
                            if (this.curChar != 'r') break;
                            this.jjAddStates(11, 12);
                            break;
                        }
                        case 29: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 30: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 35: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 34;
                            break;
                        }
                        case 36: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 38: {
                            if (this.curChar != 's' || kind <= 145) continue block143;
                            kind = 145;
                            break;
                        }
                        case 39: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 40: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 41: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 43: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 46: {
                            if (this.curChar != 'y' || kind <= 176) continue block143;
                            kind = 176;
                            break;
                        }
                        case 47: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 46;
                            break;
                        }
                        case 51: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 50;
                            break;
                        }
                        case 53: {
                            if (this.curChar != 's') break;
                            this.jjAddStates(105, 106);
                            break;
                        }
                        case 54: {
                            if (this.curChar != 'p' || kind <= 52) continue block143;
                            kind = 52;
                            break;
                        }
                        case 55: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 54;
                            break;
                        }
                        case 56: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 55;
                            break;
                        }
                        case 58: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 59;
                            break;
                        }
                        case 60: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 61;
                            break;
                        }
                        case 62: {
                            if (this.curChar != 'y' || kind <= 177) continue block143;
                            kind = 177;
                            break;
                        }
                        case 63: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 62;
                            break;
                        }
                        case 64: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 60;
                            break;
                        }
                        case 65: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 64;
                            break;
                        }
                        case 66: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 65;
                            break;
                        }
                        case 67: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 66;
                            break;
                        }
                        case 68: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 58;
                            break;
                        }
                        case 69: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 68;
                            break;
                        }
                        case 70: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 69;
                            break;
                        }
                        case 71: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 70;
                            break;
                        }
                        case 72: {
                            if (this.curChar != 'c') break;
                            this.jjAddStates(103, 104);
                            break;
                        }
                        case 73: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 74;
                            break;
                        }
                        case 75: {
                            if (this.curChar != 's' || kind <= 76) continue block143;
                            kind = 76;
                            break;
                        }
                        case 76: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 75;
                            break;
                        }
                        case 77: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 73;
                            break;
                        }
                        case 78: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 77;
                            break;
                        }
                        case 79: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 78;
                            break;
                        }
                        case 83: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 84;
                            break;
                        }
                        case 85: {
                            if (this.curChar != 's' || kind <= 144) continue block143;
                            kind = 144;
                            break;
                        }
                        case 86: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 85;
                            break;
                        }
                        case 87: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 83;
                            break;
                        }
                        case 88: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 87;
                            break;
                        }
                        case 89: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(101, 102);
                            break;
                        }
                        case 90: {
                            if (this.curChar != 'y') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 91;
                            break;
                        }
                        case 92: {
                            if (this.curChar != 't' || kind <= 180) continue block143;
                            kind = 180;
                            break;
                        }
                        case 93: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 92;
                            break;
                        }
                        case 94: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 93;
                            break;
                        }
                        case 95: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 94;
                            break;
                        }
                        case 96: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 95;
                            break;
                        }
                        case 97: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 96;
                            break;
                        }
                        case 98: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 97;
                            break;
                        }
                        case 99: {
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 98;
                            break;
                        }
                        case 100: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 90;
                            break;
                        }
                        case 101: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 100;
                            break;
                        }
                        case 103: {
                            if (this.curChar != 'y') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 104;
                            break;
                        }
                        case 105: {
                            if (this.curChar != 't' || kind <= 181) continue block143;
                            kind = 181;
                            break;
                        }
                        case 106: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 105;
                            break;
                        }
                        case 107: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 106;
                            break;
                        }
                        case 108: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 107;
                            break;
                        }
                        case 109: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 108;
                            break;
                        }
                        case 110: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 103;
                            break;
                        }
                        case 111: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 110;
                            break;
                        }
                        case 112: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 111;
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
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
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
            if (i == (startsAt = 113 - this.jjnewStateCnt)) {
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

    private final int jjMoveStringLiteralDfa0_19() {
        return this.jjMoveNfa_19(0, 0);
    }

    private final int jjMoveNfa_19(int startState, int curPos) {
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
                block15: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFF00002600L & l) == 0L || kind <= 216) continue block15;
                            kind = 216;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '>' || kind <= 192) continue block15;
                            kind = 192;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind > 216) {
                                kind = 216;
                            }
                            if (this.curChar != ']') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if (this.curChar != ']') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 3: {
                            if (kind <= 216) break;
                            kind = 216;
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
                block17: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!XPathTokenManager.jjCanMove_3(hiByte, i1, i2, l1, l2) || kind <= 216) continue block17;
                            kind = 216;
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

    private final int jjStopStringLiteralDfa_6(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_6(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_6(this.jjStopStringLiteralDfa_6(pos, active0, active1, active2, active3), pos + 1);
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
            case '(': {
                return this.jjMoveStringLiteralDfa1_6(0L, 0x840000000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_6(1L, 0L);
            }
        }
        return this.jjMoveNfa_6(21, 0);
    }

    private final int jjMoveStringLiteralDfa1_6(long active1, long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(0, 0L, active1, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_6(active1, 0L, active3, 0x40000000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_6(active1, 1L, active3, 0L);
            }
        }
        return this.jjStartNfa_6(0, 0L, active1, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa2_6(long old1, long active1, long old3, long active3) {
        if (((active1 &= old1) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_6(0, 0L, old1, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(1, 0L, active1, 0L, active3);
            return 2;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x40000000L) == 0L) break;
                return this.jjStopAtPos(2, 222);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa3_6(active1, 1L, active3, 0L);
            }
        }
        return this.jjStartNfa_6(1, 0L, active1, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa3_6(long old1, long active1, long old3, long active3) {
        if (((active1 &= old1) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_6(1, 0L, old1, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(2, 0L, active1, 0L, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                return this.jjMoveStringLiteralDfa4_6(active1, 1L);
            }
        }
        return this.jjStartNfa_6(2, 0L, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa4_6(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_6(2, 0L, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(3, 0L, active1, 0L, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 's': {
                return this.jjMoveStringLiteralDfa5_6(active1, 1L);
            }
        }
        return this.jjStartNfa_6(3, 0L, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa5_6(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_6(3, 0L, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(4, 0L, active1, 0L, 0L);
            return 5;
        }
        switch (this.curChar) {
            case 'p': {
                return this.jjMoveStringLiteralDfa6_6(active1, 1L);
            }
        }
        return this.jjStartNfa_6(4, 0L, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa6_6(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_6(4, 0L, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(5, 0L, active1, 0L, 0L);
            return 6;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa7_6(active1, 1L);
            }
        }
        return this.jjStartNfa_6(5, 0L, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa7_6(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_6(5, 0L, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(6, 0L, active1, 0L, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa8_6(active1, 1L);
            }
        }
        return this.jjStartNfa_6(6, 0L, active1, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa8_6(long old1, long active1) {
        if ((active1 &= old1) == 0L) {
            return this.jjStartNfa_6(6, 0L, old1, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_6(7, 0L, active1, 0L, 0L);
            return 8;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active1 & 1L) == 0L) break;
                return this.jjStopAtPos(8, 64);
            }
        }
        return this.jjStartNfa_6(7, 0L, active1, 0L, 0L);
    }

    private final int jjMoveNfa_6(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 22;
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
                        case 21: {
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(6);
                                break;
                            }
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(4, 5);
                                break;
                            }
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 0: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\"') continue block36;
                            if (kind > 10) {
                                kind = 10;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 4: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\'') continue block36;
                            if (kind > 10) {
                                kind = 10;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 6: {
                            if ((0x100002600L & l) == 0L) continue block36;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(6);
                            break;
                        }
                        case 8: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(107, 108);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block37: do {
                    switch (this.jjstateSet[--i]) {
                        case 21: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 1: {
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 7: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 't' || kind <= 93) continue block37;
                            kind = 93;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 14: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 15: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 18: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 19: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
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
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
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
            if (i == (startsAt = 22 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_10(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_10(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_10(this.jjStopStringLiteralDfa_10(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_10(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_10() {
        switch (this.curChar) {
            case '(': {
                return this.jjMoveStringLiteralDfa1_10(0x840000000L);
            }
        }
        return this.jjMoveNfa_10(1, 0);
    }

    private final int jjMoveStringLiteralDfa1_10(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_10(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_10(active3, 0x40000000L);
            }
        }
        return this.jjStartNfa_10(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa2_10(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_10(0, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_10(1, 0L, 0L, 0L, active3);
            return 2;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x40000000L) == 0L) break;
                return this.jjStopAtPos(2, 222);
            }
        }
        return this.jjStartNfa_10(1, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_10(int startState, int curPos) {
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
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 1: {
                            if ((0x100002600L & l) == 0L) continue block20;
                            kind = 12;
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 2: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 3: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 5: {
                            if ((0x3FF600000000000L & l) == 0L) continue block20;
                            if (kind > 50) {
                                kind = 50;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block21: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 50) {
                                kind = 50;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: 
                        case 5: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 50) {
                                kind = 50;
                            }
                            this.jjCheckNAdd(5);
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
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 50) {
                                kind = 50;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 50) {
                                kind = 50;
                            }
                            this.jjCheckNAdd(5);
                            break;
                        }
                        case 5: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 50) {
                                kind = 50;
                            }
                            this.jjCheckNAdd(5);
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

    private final int jjStopStringLiteralDfa_16(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_16(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_16(this.jjStopStringLiteralDfa_16(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_16(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_16(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_16() {
        switch (this.curChar) {
            case '\"': {
                return this.jjStopAtPos(0, 172);
            }
            case '\'': {
                return this.jjStopAtPos(0, 217);
            }
            case '/': {
                return this.jjMoveStringLiteralDfa1_16(128L);
            }
            case '=': {
                return this.jjStopAtPos(0, 202);
            }
            case '>': {
                return this.jjStopAtPos(0, 198);
            }
        }
        return this.jjMoveNfa_16(1, 0);
    }

    private final int jjMoveStringLiteralDfa1_16(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_16(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case '>': {
                if ((active3 & 0x80L) == 0L) break;
                return this.jjStopAtPos(1, 199);
            }
        }
        return this.jjStartNfa_16(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_16(int startState, int curPos) {
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
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 1: {
                            if ((0x100002600L & l) == 0L) continue block20;
                            kind = 237;
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 2: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 3: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 5: {
                            if ((0x3FF600000000000L & l) == 0L) continue block20;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block21: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: 
                        case 5: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAdd(5);
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
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAdd(5);
                            break;
                        }
                        case 5: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 203) {
                                kind = 203;
                            }
                            this.jjCheckNAdd(5);
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

    private final int jjStopStringLiteralDfa_9(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_9(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_9(this.jjStopStringLiteralDfa_9(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_9(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_9() {
        switch (this.curChar) {
            case '(': {
                return this.jjMoveStringLiteralDfa1_9(0x800000000L);
            }
        }
        return this.jjMoveNfa_9(1, 0);
    }

    private final int jjMoveStringLiteralDfa1_9(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_9(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) == 0L) break;
                return this.jjStopAtPos(1, 227);
            }
        }
        return this.jjStartNfa_9(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_9(int startState, int curPos) {
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
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 1: {
                            if ((0x100002600L & l) == 0L) continue block20;
                            kind = 12;
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 2: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjAddStates(4, 5);
                            break;
                        }
                        case 3: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 5: {
                            if ((0x3FF600000000000L & l) == 0L) continue block20;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block21: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: 
                        case 5: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block21;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(5);
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
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 2: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(5);
                            break;
                        }
                        case 5: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block22;
                            if (kind > 184) {
                                kind = 184;
                            }
                            this.jjCheckNAdd(5);
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

    private final int jjStopStringLiteralDfa_5(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_5(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_5(this.jjStopStringLiteralDfa_5(pos, active0, active1, active2, active3), pos + 1);
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
            case '(': {
                return this.jjMoveStringLiteralDfa1_5(0x840000000L);
            }
            case '=': {
                return this.jjStopAtPos(0, 110);
            }
        }
        return this.jjMoveNfa_5(7, 0);
    }

    private final int jjMoveStringLiteralDfa1_5(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_5(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_5(active3, 0x40000000L);
            }
        }
        return this.jjStartNfa_5(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa2_5(long old3, long active3) {
        if ((active3 &= old3) == 0L) {
            return this.jjStartNfa_5(0, 0L, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_5(1, 0L, 0L, 0L, active3);
            return 2;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x40000000L) == 0L) break;
                return this.jjStopAtPos(2, 222);
            }
        }
        return this.jjStartNfa_5(1, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_5(int startState, int curPos) {
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
                block25: do {
                    switch (this.jjstateSet[--i]) {
                        case 7: {
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(6);
                                break;
                            }
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(4, 5);
                                break;
                            }
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 0: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\"') continue block25;
                            if (kind > 10) {
                                kind = 10;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 4: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\'') continue block25;
                            if (kind > 10) {
                                kind = 10;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 6: {
                            if ((0x100002600L & l) == 0L) continue block25;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(6);
                            break;
                        }
                        case 8: {
                            if ((0x3FF600000000000L & l) == 0L) continue block25;
                            if (kind > 188) {
                                kind = 188;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block26: do {
                    switch (this.jjstateSet[--i]) {
                        case 7: 
                        case 8: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block26;
                            if (kind > 188) {
                                kind = 188;
                            }
                            this.jjCheckNAdd(8);
                            break;
                        }
                        case 1: {
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            this.jjAddStates(2, 3);
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
                block27: do {
                    switch (this.jjstateSet[--i]) {
                        case 7: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block27;
                            if (kind > 188) {
                                kind = 188;
                            }
                            this.jjCheckNAdd(8);
                            break;
                        }
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 8: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block27;
                            if (kind > 188) {
                                kind = 188;
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

    private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_2(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0, active1, active2, active3), pos + 1);
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
            case '(': {
                return this.jjMoveStringLiteralDfa1_2(0x800000000L);
            }
            case ')': {
                return this.jjStopAtPos(0, 139);
            }
        }
        return this.jjMoveNfa_2(6, 0);
    }

    private final int jjMoveStringLiteralDfa1_2(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) == 0L) break;
                return this.jjStopAtPos(1, 227);
            }
        }
        return this.jjStartNfa_2(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 8;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block24: do {
                    switch (this.jjstateSet[--i]) {
                        case 6: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(4, 5);
                                break;
                            }
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 0: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\"') continue block24;
                            if (kind > 5) {
                                kind = 5;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 4: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\'') continue block24;
                            if (kind > 5) {
                                kind = 5;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 7: {
                            if ((0x3FF600000000000L & l) == 0L) continue block24;
                            if (kind > 189) {
                                kind = 189;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block25: do {
                    switch (this.jjstateSet[--i]) {
                        case 6: 
                        case 7: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block25;
                            if (kind > 189) {
                                kind = 189;
                            }
                            this.jjCheckNAdd(7);
                            break;
                        }
                        case 1: {
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            this.jjAddStates(2, 3);
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
                block26: do {
                    switch (this.jjstateSet[--i]) {
                        case 6: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block26;
                            if (kind > 189) {
                                kind = 189;
                            }
                            this.jjCheckNAdd(7);
                            break;
                        }
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 7: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block26;
                            if (kind > 189) {
                                kind = 189;
                            }
                            this.jjCheckNAdd(7);
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
            if (i == (startsAt = 8 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_8(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_8(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_8(this.jjStopStringLiteralDfa_8(pos, active0, active1, active2, active3), pos + 1);
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
            case '(': {
                return this.jjMoveStringLiteralDfa1_8(0L, 0x840000000L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa1_8(0x4000000000000000L, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_8(Long.MIN_VALUE, 0L);
            }
        }
        return this.jjMoveNfa_8(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_8(long active0, long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(0, active0, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_8(active0, 0L, active3, 0x40000000L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_8(active0, 0x4000000000000000L, active3, 0L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa2_8(active0, Long.MIN_VALUE, active3, 0L);
            }
        }
        return this.jjStartNfa_8(0, active0, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa2_8(long old0, long active0, long old3, long active3) {
        if (((active0 &= old0) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_8(0, old0, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(1, active0, 0L, 0L, active3);
            return 2;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x40000000L) == 0L) break;
                return this.jjStopAtPos(2, 222);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_8(active0, 0x4000000000000000L, active3, 0L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa3_8(active0, Long.MIN_VALUE, active3, 0L);
            }
        }
        return this.jjStartNfa_8(1, active0, 0L, 0L, active3);
    }

    private final int jjMoveStringLiteralDfa3_8(long old0, long active0, long old3, long active3) {
        if (((active0 &= old0) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_8(1, old0, 0L, 0L, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(2, active0, 0L, 0L, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'i': {
                return this.jjMoveStringLiteralDfa4_8(active0, Long.MIN_VALUE);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_8(active0, 0x4000000000000000L);
            }
        }
        return this.jjStartNfa_8(2, active0, 0L, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa4_8(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_8(2, old0, 0L, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(3, active0, 0L, 0L, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                return this.jjMoveStringLiteralDfa5_8(active0, 0x4000000000000000L);
            }
            case 'p': {
                if ((active0 & Long.MIN_VALUE) == 0L) break;
                return this.jjStopAtPos(4, 63);
            }
        }
        return this.jjStartNfa_8(3, active0, 0L, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa5_8(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_8(3, old0, 0L, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(4, active0, 0L, 0L, 0L);
            return 5;
        }
        switch (this.curChar) {
            case 'r': {
                return this.jjMoveStringLiteralDfa6_8(active0, 0x4000000000000000L);
            }
        }
        return this.jjStartNfa_8(4, active0, 0L, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa6_8(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_8(4, old0, 0L, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(5, active0, 0L, 0L, 0L);
            return 6;
        }
        switch (this.curChar) {
            case 'v': {
                return this.jjMoveStringLiteralDfa7_8(active0, 0x4000000000000000L);
            }
        }
        return this.jjStartNfa_8(5, active0, 0L, 0L, 0L);
    }

    private final int jjMoveStringLiteralDfa7_8(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_8(5, old0, 0L, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_8(6, active0, 0L, 0L, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x4000000000000000L) == 0L) break;
                return this.jjStopAtPos(7, 62);
            }
        }
        return this.jjStartNfa_8(6, active0, 0L, 0L, 0L);
    }

    private final int jjMoveNfa_8(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 1;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block10: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x100002600L & l) == 0L) continue block10;
                            kind = 12;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
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
            if (i == (startsAt = 1 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active1 & 0x2000000000L) != 0L) {
                    return 58;
                }
                if ((active1 & 0x100L) != 0L) {
                    this.jjmatchedKind = 235;
                    return 96;
                }
                if ((active2 & 0xC00000000000L) != 0L) {
                    return 803;
                }
                return -1;
            }
            case 1: {
                if ((active1 & 0x100L) != 0L) {
                    return 804;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_0(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1, active2, active3), pos + 1);
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
            case '$': {
                return this.jjStopAtPos(0, 49);
            }
            case '%': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0x80000000000L, 0L);
            }
            case '(': {
                this.jjmatchedKind = 134;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0x840000000L);
            }
            case ')': {
                return this.jjStopAtPos(0, 138);
            }
            case '*': {
                return this.jjStartNfaWithStates_0(0, 101, 58);
            }
            case '+': {
                return this.jjStopAtPos(0, 129);
            }
            case ',': {
                return this.jjStopAtPos(0, 168);
            }
            case '-': {
                return this.jjStopAtPos(0, 128);
            }
            case '.': {
                this.jjmatchedKind = 174;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0x800000000000L, 0L);
            }
            case '/': {
                this.jjmatchedKind = 105;
                return this.jjMoveStringLiteralDfa1_0(0L, 0x40000000000L, 0L, 0L);
            }
            case ';': {
                return this.jjStopAtPos(0, 170);
            }
            case '<': {
                this.jjmatchedKind = 197;
                return this.jjMoveStringLiteralDfa1_0(32768L, 0L, 0x4000000000000000L, 0x10000000000L);
            }
            case '@': {
                return this.jjStopAtPos(0, 135);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(0L, 256L, 0L, 0L);
            }
            case '{': {
                return this.jjStopAtPos(0, 205);
            }
            case '}': {
                return this.jjStopAtPos(0, 241);
            }
        }
        return this.jjMoveNfa_0(19, 0);
    }

    private final int jjMoveStringLiteralDfa1_0(long active0, long active1, long active2, long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0, active1, active2, active3);
            return 1;
        }
        switch (this.curChar) {
            case '!': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 0x4000000000000000L, active3, 0x10000000000L);
            }
            case '%': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 0x80000000000L, active3, 0L);
            }
            case '.': {
                if ((active2 & 0x800000000000L) == 0L) break;
                return this.jjStopAtPos(1, 175);
            }
            case '/': {
                if ((active1 & 0x40000000000L) == 0L) break;
                return this.jjStopAtPos(1, 106);
            }
            case ':': {
                if ((active3 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 0L, active3, 0x40000000L);
            }
            case '?': {
                if ((active0 & 0x8000L) == 0L) break;
                return this.jjStopAtPos(1, 15);
            }
            case 's': {
                if ((active1 & 0x100L) == 0L) break;
                return this.jjStartNfaWithStates_0(1, 72, 804);
            }
        }
        return this.jjStartNfa_0(0, active0, active1, active2, active3);
    }

    private final int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1, long old2, long active2, long old3, long active3) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_0(0, old0, old1, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(1, 0L, 0L, active2, active3);
            return 2;
        }
        switch (this.curChar) {
            case '%': {
                if ((active2 & 0x80000000000L) == 0L) break;
                return this.jjStopAtPos(2, 171);
            }
            case '-': {
                return this.jjMoveStringLiteralDfa3_0(active2, 0L, active3, 0x10000000000L);
            }
            case ':': {
                if ((active3 & 0x40000000L) == 0L) break;
                return this.jjStopAtPos(2, 222);
            }
            case '[': {
                return this.jjMoveStringLiteralDfa3_0(active2, 0x4000000000000000L, active3, 0L);
            }
        }
        return this.jjStartNfa_0(1, 0L, 0L, active2, active3);
    }

    private final int jjMoveStringLiteralDfa3_0(long old2, long active2, long old3, long active3) {
        if (((active2 &= old2) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_0(1, 0L, 0L, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(2, 0L, 0L, active2, active3);
            return 3;
        }
        switch (this.curChar) {
            case '-': {
                if ((active3 & 0x10000000000L) == 0L) break;
                return this.jjStopAtPos(3, 232);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa4_0(active2, 0x4000000000000000L, active3, 0L);
            }
        }
        return this.jjStartNfa_0(2, 0L, 0L, active2, active3);
    }

    private final int jjMoveStringLiteralDfa4_0(long old2, long active2, long old3, long active3) {
        if (((active2 &= old2) | (active3 &= old3)) == 0L) {
            return this.jjStartNfa_0(2, 0L, 0L, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(3, 0L, 0L, active2, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'D': {
                return this.jjMoveStringLiteralDfa5_0(active2, 0x4000000000000000L);
            }
        }
        return this.jjStartNfa_0(3, 0L, 0L, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa5_0(long old2, long active2) {
        if ((active2 &= old2) == 0L) {
            return this.jjStartNfa_0(3, 0L, 0L, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(4, 0L, 0L, active2, 0L);
            return 5;
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa6_0(active2, 0x4000000000000000L);
            }
        }
        return this.jjStartNfa_0(4, 0L, 0L, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa6_0(long old2, long active2) {
        if ((active2 &= old2) == 0L) {
            return this.jjStartNfa_0(4, 0L, 0L, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(5, 0L, 0L, active2, 0L);
            return 6;
        }
        switch (this.curChar) {
            case 'T': {
                return this.jjMoveStringLiteralDfa7_0(active2, 0x4000000000000000L);
            }
        }
        return this.jjStartNfa_0(5, 0L, 0L, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa7_0(long old2, long active2) {
        if ((active2 &= old2) == 0L) {
            return this.jjStartNfa_0(5, 0L, 0L, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(6, 0L, 0L, active2, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa8_0(active2, 0x4000000000000000L);
            }
        }
        return this.jjStartNfa_0(6, 0L, 0L, active2, 0L);
    }

    private final int jjMoveStringLiteralDfa8_0(long old2, long active2) {
        if ((active2 &= old2) == 0L) {
            return this.jjStartNfa_0(6, 0L, 0L, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(7, 0L, 0L, active2, 0L);
            return 8;
        }
        switch (this.curChar) {
            case '[': {
                if ((active2 & 0x4000000000000000L) == 0L) break;
                return this.jjStopAtPos(8, 190);
            }
        }
        return this.jjStartNfa_0(7, 0L, 0L, active2, 0L);
    }

    /*
     * Opcode count of 14130 triggered aggressive code reduction.  Override with --aggressivesizethreshold.
     */
    private final int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 803;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                while (true) {
                    switch (this.jjstateSet[--i]) {
                        case 804: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 235) {
                                    kind = 235;
                                }
                                this.jjCheckNAdd(775);
                            } else if ((0x100002600L & l) != 0L) {
                                this.jjCheckNAddTwoStates(770, 771);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 774;
                            } else if (this.curChar == '(' && kind > 187) {
                                kind = 187;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(772, 773);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 768;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddStates(109, 111);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 765;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(766, 767);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(763, 764);
                            break;
                        }
                        case 96: {
                            if ((0x3FF600000000000L & l) != 0L) {
                                if (kind > 235) {
                                    kind = 235;
                                }
                                this.jjCheckNAdd(775);
                            } else if ((0x100002600L & l) != 0L) {
                                this.jjCheckNAddTwoStates(770, 771);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 774;
                            } else if (this.curChar == '(' && kind > 187) {
                                kind = 187;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(772, 773);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 768;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddStates(109, 111);
                            } else if (this.curChar == ':') {
                                this.jjstateSet[this.jjnewStateCnt++] = 765;
                            }
                            if ((0x3FF600000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(766, 767);
                            }
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(763, 764);
                            break;
                        }
                        case 19: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAddStates(112, 121);
                                break;
                            }
                            if ((0x100002600L & l) != 0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                                this.jjCheckNAdd(36);
                                break;
                            }
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(122, 124);
                                break;
                            }
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 58;
                                break;
                            }
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(4, 5);
                                break;
                            }
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 803: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                this.jjCheckNAddStates(125, 127);
                            }
                            if ((0x3FF000000000000L & l) != 0L) {
                                this.jjCheckNAddTwoStates(93, 80);
                            }
                            if ((0x3FF000000000000L & l) == 0L) break;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(92);
                            break;
                        }
                        case 0: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\"') break;
                            if (kind > 4) {
                                kind = 4;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 4: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(4, 5);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\'') break;
                            if (kind > 4) {
                                kind = 4;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 7: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(128, 129);
                            break;
                        }
                        case 21: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(130, 131);
                            break;
                        }
                        case 36: {
                            if ((0x100002600L & l) == 0L) break;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(36);
                            break;
                        }
                        case 38: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(132, 133);
                            break;
                        }
                        case 47: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(134, 135);
                            break;
                        }
                        case 57: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 58;
                            break;
                        }
                        case 58: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 59;
                            break;
                        }
                        case 60: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            if (kind > 104) {
                                kind = 104;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 60;
                            break;
                        }
                        case 62: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(136, 137);
                            break;
                        }
                        case 63: {
                            if (this.curChar != '$' || kind <= 143) break;
                            kind = 143;
                            break;
                        }
                        case 67: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(138, 139);
                            break;
                        }
                        case 68: {
                            if (this.curChar != '(' || kind <= 152) break;
                            kind = 152;
                            break;
                        }
                        case 72: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(112, 121);
                            break;
                        }
                        case 73: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(73);
                            break;
                        }
                        case 74: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(74, 75);
                            break;
                        }
                        case 75: {
                            if (this.curChar != '.') break;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(76);
                            break;
                        }
                        case 76: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(76);
                            break;
                        }
                        case 77: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(140, 142);
                            break;
                        }
                        case 78: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddTwoStates(79, 80);
                            break;
                        }
                        case 79: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(79, 80);
                            break;
                        }
                        case 81: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(82);
                            break;
                        }
                        case 82: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAdd(82);
                            break;
                        }
                        case 83: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(143, 146);
                            break;
                        }
                        case 84: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(147, 149);
                            break;
                        }
                        case 85: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(147, 149);
                            break;
                        }
                        case 87: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(88);
                            break;
                        }
                        case 88: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(88, 89);
                            break;
                        }
                        case 90: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            if (kind > 250) {
                                kind = 250;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 90;
                            break;
                        }
                        case 91: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(122, 124);
                            break;
                        }
                        case 92: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(92);
                            break;
                        }
                        case 93: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(93, 80);
                            break;
                        }
                        case 94: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(125, 127);
                            break;
                        }
                        case 97: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(150, 152);
                            break;
                        }
                        case 98: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(99, 100);
                            break;
                        }
                        case 99: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(99, 100);
                            break;
                        }
                        case 100: {
                            if (this.curChar != '\"') break;
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAdd(98);
                            break;
                        }
                        case 101: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddTwoStates(102, 103);
                            break;
                        }
                        case 102: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(102, 103);
                            break;
                        }
                        case 103: {
                            if (this.curChar != '\'') break;
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAdd(101);
                            break;
                        }
                        case 105: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(153, 154);
                            break;
                        }
                        case 106: {
                            if (this.curChar != ':' || kind <= 21) break;
                            kind = 21;
                            break;
                        }
                        case 107: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 106;
                            break;
                        }
                        case 116: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(155, 156);
                            break;
                        }
                        case 117: {
                            if (this.curChar != ':' || kind <= 24) break;
                            kind = 24;
                            break;
                        }
                        case 118: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 117;
                            break;
                        }
                        case 126: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(157, 158);
                            break;
                        }
                        case 127: {
                            if (this.curChar != ':' || kind <= 29) break;
                            kind = 29;
                            break;
                        }
                        case 128: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 127;
                            break;
                        }
                        case 132: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 131;
                            break;
                        }
                        case 135: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 134;
                            break;
                        }
                        case 144: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(159, 160);
                            break;
                        }
                        case 145: {
                            if (this.curChar != '(' || kind <= 79) break;
                            kind = 79;
                            break;
                        }
                        case 154: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(161, 162);
                            break;
                        }
                        case 156: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjAddStates(163, 164);
                            break;
                        }
                        case 157: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 158;
                            break;
                        }
                        case 159: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(165, 167);
                            break;
                        }
                        case 160: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(160, 161);
                            break;
                        }
                        case 170: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(168, 169);
                            break;
                        }
                        case 181: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(170, 171);
                            break;
                        }
                        case 182: {
                            if (this.curChar != ':' || kind <= 18) break;
                            kind = 18;
                            break;
                        }
                        case 183: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 182;
                            break;
                        }
                        case 188: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(172, 173);
                            break;
                        }
                        case 196: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(174, 175);
                            break;
                        }
                        case 197: {
                            if (this.curChar != '(' || kind <= 153) break;
                            kind = 153;
                            break;
                        }
                        case 205: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(176, 177);
                            break;
                        }
                        case 206: {
                            if (this.curChar != ':' || kind <= 19) break;
                            kind = 19;
                            break;
                        }
                        case 207: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 206;
                            break;
                        }
                        case 217: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(178, 179);
                            break;
                        }
                        case 218: {
                            if (this.curChar != ':' || kind <= 23) break;
                            kind = 23;
                            break;
                        }
                        case 219: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 218;
                            break;
                        }
                        case 223: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 222;
                            break;
                        }
                        case 226: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 225;
                            break;
                        }
                        case 237: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(180, 181);
                            break;
                        }
                        case 252: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(182, 183);
                            break;
                        }
                        case 267: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(184, 185);
                            break;
                        }
                        case 269: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(186, 187);
                            break;
                        }
                        case 287: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(188, 189);
                            break;
                        }
                        case 298: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 297;
                            break;
                        }
                        case 312: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(190, 191);
                            break;
                        }
                        case 331: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(192, 193);
                            break;
                        }
                        case 346: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(194, 195);
                            break;
                        }
                        case 350: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 349;
                            break;
                        }
                        case 361: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(196, 197);
                            break;
                        }
                        case 377: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(198, 199);
                            break;
                        }
                        case 379: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(200, 201);
                            break;
                        }
                        case 401: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(202, 203);
                            break;
                        }
                        case 403: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(204, 205);
                            break;
                        }
                        case 423: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(206, 207);
                            break;
                        }
                        case 425: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(208, 209);
                            break;
                        }
                        case 446: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(210, 211);
                            break;
                        }
                        case 447: {
                            if (this.curChar != '(' || kind <= 149) break;
                            kind = 149;
                            break;
                        }
                        case 451: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 450;
                            break;
                        }
                        case 460: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(212, 213);
                            break;
                        }
                        case 469: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(214, 215);
                            break;
                        }
                        case 471: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(216, 217);
                            break;
                        }
                        case 472: {
                            if (this.curChar != '$' || kind <= 182) break;
                            kind = 182;
                            break;
                        }
                        case 487: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(218, 219);
                            break;
                        }
                        case 488: {
                            if (this.curChar != ':' || kind <= 20) break;
                            kind = 20;
                            break;
                        }
                        case 489: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 488;
                            break;
                        }
                        case 495: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(220, 221);
                            break;
                        }
                        case 496: {
                            if (this.curChar != ':' || kind <= 26) break;
                            kind = 26;
                            break;
                        }
                        case 497: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 496;
                            break;
                        }
                        case 504: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 503;
                            break;
                        }
                        case 514: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(222, 223);
                            break;
                        }
                        case 515: {
                            if (this.curChar != ':' || kind <= 28) break;
                            kind = 28;
                            break;
                        }
                        case 516: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 515;
                            break;
                        }
                        case 525: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(224, 225);
                            break;
                        }
                        case 527: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(226, 228);
                            break;
                        }
                        case 528: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(528, 529);
                            break;
                        }
                        case 540: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 539;
                            break;
                        }
                        case 551: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(229, 230);
                            break;
                        }
                        case 563: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 562;
                            break;
                        }
                        case 574: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(231, 232);
                            break;
                        }
                        case 575: {
                            if (this.curChar != '(' || kind <= 155) break;
                            kind = 155;
                            break;
                        }
                        case 586: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 585;
                            break;
                        }
                        case 598: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(233, 234);
                            break;
                        }
                        case 599: {
                            if (this.curChar != ':' || kind <= 22) break;
                            kind = 22;
                            break;
                        }
                        case 600: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 599;
                            break;
                        }
                        case 604: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(235, 236);
                            break;
                        }
                        case 605: {
                            if (this.curChar != '(' || kind <= 80) break;
                            kind = 80;
                            break;
                        }
                        case 612: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 611;
                            break;
                        }
                        case 619: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(237, 238);
                            break;
                        }
                        case 620: {
                            if (this.curChar != '(' || kind <= 81) break;
                            kind = 81;
                            break;
                        }
                        case 629: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 628;
                            break;
                        }
                        case 636: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(239, 240);
                            break;
                        }
                        case 637: {
                            if (this.curChar != '$' || kind <= 140) break;
                            kind = 140;
                            break;
                        }
                        case 642: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(241, 242);
                            break;
                        }
                        case 643: {
                            if (this.curChar != ':' || kind <= 25) break;
                            kind = 25;
                            break;
                        }
                        case 644: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 643;
                            break;
                        }
                        case 651: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 650;
                            break;
                        }
                        case 661: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(243, 244);
                            break;
                        }
                        case 662: {
                            if (this.curChar != ':' || kind <= 27) break;
                            kind = 27;
                            break;
                        }
                        case 663: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 662;
                            break;
                        }
                        case 672: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(245, 246);
                            break;
                        }
                        case 673: {
                            if (this.curChar != '$' || kind <= 142) break;
                            kind = 142;
                            break;
                        }
                        case 677: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(247, 248);
                            break;
                        }
                        case 678: {
                            if (this.curChar != '(' || kind <= 78) break;
                            kind = 78;
                            break;
                        }
                        case 685: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(249, 250);
                            break;
                        }
                        case 687: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjAddStates(251, 252);
                            break;
                        }
                        case 688: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 689;
                            break;
                        }
                        case 690: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(253, 255);
                            break;
                        }
                        case 691: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(691, 692);
                            break;
                        }
                        case 699: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(256, 257);
                            break;
                        }
                        case 707: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(258, 259);
                            break;
                        }
                        case 708: {
                            if (this.curChar != '$' || kind <= 141) break;
                            kind = 141;
                            break;
                        }
                        case 714: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(260, 261);
                            break;
                        }
                        case 719: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(262, 263);
                            break;
                        }
                        case 720: {
                            if (this.curChar != '(' || kind <= 154) break;
                            kind = 154;
                            break;
                        }
                        case 724: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(264, 265);
                            break;
                        }
                        case 725: {
                            if (this.curChar != '(' || kind <= 167) break;
                            kind = 167;
                            break;
                        }
                        case 736: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(266, 267);
                            break;
                        }
                        case 748: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(268, 269);
                            break;
                        }
                        case 760: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(270, 271);
                            break;
                        }
                        case 761: {
                            if (this.curChar != '(' || kind <= 166) break;
                            kind = 166;
                            break;
                        }
                        case 763: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(763, 764);
                            break;
                        }
                        case 764: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 765;
                            break;
                        }
                        case 765: {
                            if (this.curChar != '*' || kind <= 103) break;
                            kind = 103;
                            break;
                        }
                        case 766: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(766, 767);
                            break;
                        }
                        case 767: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 768;
                            break;
                        }
                        case 769: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(109, 111);
                            break;
                        }
                        case 770: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(770, 771);
                            break;
                        }
                        case 771: {
                            if (this.curChar != '(' || kind <= 187) break;
                            kind = 187;
                            break;
                        }
                        case 772: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(772, 773);
                            break;
                        }
                        case 773: {
                            if (this.curChar != ':') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 774;
                            break;
                        }
                        case 775: {
                            if ((0x3FF600000000000L & l) == 0L) break;
                            if (kind > 235) {
                                kind = 235;
                            }
                            this.jjCheckNAdd(775);
                            break;
                        }
                        case 778: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(272, 273);
                            break;
                        }
                        case 787: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjAddStates(274, 276);
                            break;
                        }
                    }
                    if (i != startsAt) {
                        continue;
                    }
                    break;
                }
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                while (true) {
                    switch (this.jjstateSet[--i]) {
                        case 804: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 235) {
                                    kind = 235;
                                }
                                this.jjCheckNAdd(775);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(772, 773);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddStates(109, 111);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(766, 767);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(763, 764);
                            break;
                        }
                        case 96: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 235) {
                                    kind = 235;
                                }
                                this.jjCheckNAdd(775);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(772, 773);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddStates(109, 111);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(766, 767);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                this.jjCheckNAddTwoStates(763, 764);
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 177;
                            } else if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 141;
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 167;
                            } else if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 123;
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 151;
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 113;
                            }
                            if (this.curChar != 't') break;
                            this.jjAddStates(150, 152);
                            break;
                        }
                        case 19: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 235) {
                                    kind = 235;
                                }
                                this.jjCheckNAddStates(277, 286);
                            }
                            if (this.curChar == 'v') {
                                this.jjAddStates(287, 288);
                                break;
                            }
                            if (this.curChar == 'i') {
                                this.jjAddStates(289, 291);
                                break;
                            }
                            if (this.curChar == 't') {
                                this.jjAddStates(292, 294);
                                break;
                            }
                            if (this.curChar == 'e') {
                                this.jjAddStates(295, 298);
                                break;
                            }
                            if (this.curChar == 'f') {
                                this.jjAddStates(299, 301);
                                break;
                            }
                            if (this.curChar == 's') {
                                this.jjAddStates(302, 305);
                                break;
                            }
                            if (this.curChar == 'p') {
                                this.jjAddStates(306, 311);
                                break;
                            }
                            if (this.curChar == 'd') {
                                this.jjAddStates(312, 327);
                                break;
                            }
                            if (this.curChar == 'c') {
                                this.jjAddStates(328, 330);
                                break;
                            }
                            if (this.curChar == 'a') {
                                this.jjAddStates(331, 337);
                                break;
                            }
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 70;
                                break;
                            }
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 64;
                                break;
                            }
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 55;
                                break;
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 44;
                                break;
                            }
                            if (this.curChar == 'm') {
                                this.jjstateSet[this.jjnewStateCnt++] = 34;
                                break;
                            }
                            if (this.curChar != 'x') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 1: {
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'y') break;
                            this.jjAddStates(128, 129);
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'n' || kind <= 6) break;
                            kind = 6;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 14: {
                            if (this.curChar != 'v') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 15: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 18: {
                            if (this.curChar != 'q') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(130, 131);
                            break;
                        }
                        case 22: {
                            if (this.curChar != 'e' || kind <= 11) break;
                            kind = 11;
                            break;
                        }
                        case 23: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 24: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 23;
                            break;
                        }
                        case 25: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
                            break;
                        }
                        case 26: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 27: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 28: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 29: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 30: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 31: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 32: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 31;
                            break;
                        }
                        case 33: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 32;
                            break;
                        }
                        case 34: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 33;
                            break;
                        }
                        case 35: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 34;
                            break;
                        }
                        case 37: {
                            if (this.curChar != 'd') break;
                            this.jjAddStates(132, 133);
                            break;
                        }
                        case 39: {
                            if (this.curChar != '{' || kind <= 82) break;
                            kind = 82;
                            break;
                        }
                        case 40: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 41: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 42: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 43: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 44: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 43;
                            break;
                        }
                        case 45: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 44;
                            break;
                        }
                        case 46: {
                            if (this.curChar != 'd') break;
                            this.jjAddStates(134, 135);
                            break;
                        }
                        case 48: {
                            if (this.curChar != '{' || kind <= 83) break;
                            kind = 83;
                            break;
                        }
                        case 49: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 46;
                            break;
                        }
                        case 50: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 49;
                            break;
                        }
                        case 51: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 50;
                            break;
                        }
                        case 52: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            break;
                        }
                        case 53: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 52;
                            break;
                        }
                        case 54: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 53;
                            break;
                        }
                        case 55: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 54;
                            break;
                        }
                        case 56: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 55;
                            break;
                        }
                        case 59: 
                        case 60: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            if (kind > 104) {
                                kind = 104;
                            }
                            this.jjCheckNAdd(60);
                            break;
                        }
                        case 61: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(136, 137);
                            break;
                        }
                        case 64: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 61;
                            break;
                        }
                        case 65: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 64;
                            break;
                        }
                        case 66: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(138, 139);
                            break;
                        }
                        case 69: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 66;
                            break;
                        }
                        case 70: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 69;
                            break;
                        }
                        case 71: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 70;
                            break;
                        }
                        case 80: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(338, 339);
                            break;
                        }
                        case 86: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(340, 341);
                            break;
                        }
                        case 89: 
                        case 90: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L) break;
                            if (kind > 250) {
                                kind = 250;
                            }
                            this.jjCheckNAdd(90);
                            break;
                        }
                        case 95: {
                            if (this.curChar != 'a') break;
                            this.jjAddStates(331, 337);
                            break;
                        }
                        case 99: {
                            this.jjAddStates(342, 343);
                            break;
                        }
                        case 102: {
                            this.jjAddStates(344, 345);
                            break;
                        }
                        case 104: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(153, 154);
                            break;
                        }
                        case 108: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 104;
                            break;
                        }
                        case 109: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 108;
                            break;
                        }
                        case 110: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 109;
                            break;
                        }
                        case 111: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 110;
                            break;
                        }
                        case 112: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 111;
                            break;
                        }
                        case 113: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 112;
                            break;
                        }
                        case 114: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 113;
                            break;
                        }
                        case 115: {
                            if (this.curChar != 'r') break;
                            this.jjAddStates(155, 156);
                            break;
                        }
                        case 119: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 115;
                            break;
                        }
                        case 120: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 119;
                            break;
                        }
                        case 121: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 120;
                            break;
                        }
                        case 122: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 121;
                            break;
                        }
                        case 123: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 122;
                            break;
                        }
                        case 124: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 123;
                            break;
                        }
                        case 125: {
                            if (this.curChar != 'f') break;
                            this.jjAddStates(157, 158);
                            break;
                        }
                        case 129: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 125;
                            break;
                        }
                        case 130: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 129;
                            break;
                        }
                        case 131: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 130;
                            break;
                        }
                        case 133: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 132;
                            break;
                        }
                        case 134: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 133;
                            break;
                        }
                        case 136: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 135;
                            break;
                        }
                        case 137: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 136;
                            break;
                        }
                        case 138: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 137;
                            break;
                        }
                        case 139: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 138;
                            break;
                        }
                        case 140: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 139;
                            break;
                        }
                        case 141: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 140;
                            break;
                        }
                        case 142: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 141;
                            break;
                        }
                        case 143: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(159, 160);
                            break;
                        }
                        case 146: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 143;
                            break;
                        }
                        case 147: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 146;
                            break;
                        }
                        case 148: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 147;
                            break;
                        }
                        case 149: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 148;
                            break;
                        }
                        case 150: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 149;
                            break;
                        }
                        case 151: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 150;
                            break;
                        }
                        case 152: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 151;
                            break;
                        }
                        case 153: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 154;
                            break;
                        }
                        case 155: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(346, 350);
                            break;
                        }
                        case 156: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(156, 157);
                            break;
                        }
                        case 158: 
                        case 159: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(165, 167);
                            break;
                        }
                        case 161: {
                            if (this.curChar != '{' || kind <= 85) break;
                            kind = 85;
                            break;
                        }
                        case 162: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 153;
                            break;
                        }
                        case 163: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 162;
                            break;
                        }
                        case 164: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 163;
                            break;
                        }
                        case 165: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 164;
                            break;
                        }
                        case 166: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 165;
                            break;
                        }
                        case 167: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 166;
                            break;
                        }
                        case 168: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 167;
                            break;
                        }
                        case 169: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(168, 169);
                            break;
                        }
                        case 171: {
                            if (this.curChar != '{' || kind <= 90) break;
                            kind = 90;
                            break;
                        }
                        case 172: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 169;
                            break;
                        }
                        case 173: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 172;
                            break;
                        }
                        case 174: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 173;
                            break;
                        }
                        case 175: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 174;
                            break;
                        }
                        case 176: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 175;
                            break;
                        }
                        case 177: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 176;
                            break;
                        }
                        case 178: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 177;
                            break;
                        }
                        case 179: {
                            if (this.curChar != 'c') break;
                            this.jjAddStates(328, 330);
                            break;
                        }
                        case 180: {
                            if (this.curChar != 'd') break;
                            this.jjAddStates(170, 171);
                            break;
                        }
                        case 184: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 180;
                            break;
                        }
                        case 185: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 184;
                            break;
                        }
                        case 186: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 185;
                            break;
                        }
                        case 187: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(172, 173);
                            break;
                        }
                        case 189: {
                            if (this.curChar != '{' || kind <= 88) break;
                            kind = 88;
                            break;
                        }
                        case 190: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 187;
                            break;
                        }
                        case 191: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 190;
                            break;
                        }
                        case 192: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 191;
                            break;
                        }
                        case 193: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 192;
                            break;
                        }
                        case 194: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 193;
                            break;
                        }
                        case 195: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(174, 175);
                            break;
                        }
                        case 198: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 195;
                            break;
                        }
                        case 199: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 198;
                            break;
                        }
                        case 200: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 199;
                            break;
                        }
                        case 201: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 200;
                            break;
                        }
                        case 202: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 201;
                            break;
                        }
                        case 203: {
                            if (this.curChar != 'd') break;
                            this.jjAddStates(312, 327);
                            break;
                        }
                        case 204: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(176, 177);
                            break;
                        }
                        case 208: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 204;
                            break;
                        }
                        case 209: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 208;
                            break;
                        }
                        case 210: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 209;
                            break;
                        }
                        case 211: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 210;
                            break;
                        }
                        case 212: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 211;
                            break;
                        }
                        case 213: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 212;
                            break;
                        }
                        case 214: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 213;
                            break;
                        }
                        case 215: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 214;
                            break;
                        }
                        case 216: {
                            if (this.curChar != 'f') break;
                            this.jjAddStates(178, 179);
                            break;
                        }
                        case 220: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 216;
                            break;
                        }
                        case 221: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 220;
                            break;
                        }
                        case 222: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 221;
                            break;
                        }
                        case 224: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 223;
                            break;
                        }
                        case 225: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 224;
                            break;
                        }
                        case 227: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 226;
                            break;
                        }
                        case 228: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 227;
                            break;
                        }
                        case 229: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 228;
                            break;
                        }
                        case 230: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 229;
                            break;
                        }
                        case 231: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 230;
                            break;
                        }
                        case 232: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 231;
                            break;
                        }
                        case 233: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 232;
                            break;
                        }
                        case 234: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 233;
                            break;
                        }
                        case 235: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 234;
                            break;
                        }
                        case 236: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 237;
                            break;
                        }
                        case 238: {
                            if (this.curChar != 'n' || kind <= 30) break;
                            kind = 30;
                            break;
                        }
                        case 239: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 238;
                            break;
                        }
                        case 240: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 239;
                            break;
                        }
                        case 241: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 240;
                            break;
                        }
                        case 242: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 241;
                            break;
                        }
                        case 243: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 242;
                            break;
                        }
                        case 244: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 243;
                            break;
                        }
                        case 245: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 244;
                            break;
                        }
                        case 246: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 236;
                            break;
                        }
                        case 247: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 246;
                            break;
                        }
                        case 248: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 247;
                            break;
                        }
                        case 249: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 248;
                            break;
                        }
                        case 250: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 249;
                            break;
                        }
                        case 251: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 252;
                            break;
                        }
                        case 253: {
                            if (this.curChar != 'g' || kind <= 31) break;
                            kind = 31;
                            break;
                        }
                        case 254: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 253;
                            break;
                        }
                        case 255: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 254;
                            break;
                        }
                        case 256: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 255;
                            break;
                        }
                        case 257: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 256;
                            break;
                        }
                        case 258: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 257;
                            break;
                        }
                        case 259: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 258;
                            break;
                        }
                        case 260: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 259;
                            break;
                        }
                        case 261: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 251;
                            break;
                        }
                        case 262: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 261;
                            break;
                        }
                        case 263: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 262;
                            break;
                        }
                        case 264: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 263;
                            break;
                        }
                        case 265: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 264;
                            break;
                        }
                        case 266: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 267;
                            break;
                        }
                        case 268: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 269;
                            break;
                        }
                        case 270: {
                            if (this.curChar != 'r' || kind <= 34) break;
                            kind = 34;
                            break;
                        }
                        case 271: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 270;
                            break;
                        }
                        case 272: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 271;
                            break;
                        }
                        case 273: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 272;
                            break;
                        }
                        case 274: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 273;
                            break;
                        }
                        case 275: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 268;
                            break;
                        }
                        case 276: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 275;
                            break;
                        }
                        case 277: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 276;
                            break;
                        }
                        case 278: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 277;
                            break;
                        }
                        case 279: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 278;
                            break;
                        }
                        case 280: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 279;
                            break;
                        }
                        case 281: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 266;
                            break;
                        }
                        case 282: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 281;
                            break;
                        }
                        case 283: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 282;
                            break;
                        }
                        case 284: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 283;
                            break;
                        }
                        case 285: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 284;
                            break;
                        }
                        case 286: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 287;
                            break;
                        }
                        case 288: {
                            if (this.curChar != 's' || kind <= 35) break;
                            kind = 35;
                            break;
                        }
                        case 289: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 288;
                            break;
                        }
                        case 290: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 289;
                            break;
                        }
                        case 291: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 290;
                            break;
                        }
                        case 292: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 291;
                            break;
                        }
                        case 293: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 292;
                            break;
                        }
                        case 294: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 293;
                            break;
                        }
                        case 295: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 294;
                            break;
                        }
                        case 296: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 295;
                            break;
                        }
                        case 297: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 296;
                            break;
                        }
                        case 299: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 298;
                            break;
                        }
                        case 300: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 299;
                            break;
                        }
                        case 301: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 300;
                            break;
                        }
                        case 302: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 301;
                            break;
                        }
                        case 303: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 302;
                            break;
                        }
                        case 304: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 303;
                            break;
                        }
                        case 305: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 304;
                            break;
                        }
                        case 306: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 286;
                            break;
                        }
                        case 307: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 306;
                            break;
                        }
                        case 308: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 307;
                            break;
                        }
                        case 309: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 308;
                            break;
                        }
                        case 310: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 309;
                            break;
                        }
                        case 311: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 312;
                            break;
                        }
                        case 313: {
                            if (this.curChar != 'n' || kind <= 54) break;
                            kind = 54;
                            break;
                        }
                        case 314: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 313;
                            break;
                        }
                        case 315: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 314;
                            break;
                        }
                        case 316: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 315;
                            break;
                        }
                        case 317: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 316;
                            break;
                        }
                        case 318: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 317;
                            break;
                        }
                        case 319: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 318;
                            break;
                        }
                        case 320: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 319;
                            break;
                        }
                        case 321: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 320;
                            break;
                        }
                        case 322: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 321;
                            break;
                        }
                        case 323: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 322;
                            break;
                        }
                        case 324: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 323;
                            break;
                        }
                        case 325: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 311;
                            break;
                        }
                        case 326: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 325;
                            break;
                        }
                        case 327: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 326;
                            break;
                        }
                        case 328: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 327;
                            break;
                        }
                        case 329: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 328;
                            break;
                        }
                        case 330: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 331;
                            break;
                        }
                        case 332: {
                            if (this.curChar != 'e' || kind <= 60) break;
                            kind = 60;
                            break;
                        }
                        case 333: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 332;
                            break;
                        }
                        case 334: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 333;
                            break;
                        }
                        case 335: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 334;
                            break;
                        }
                        case 336: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 335;
                            break;
                        }
                        case 337: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 336;
                            break;
                        }
                        case 338: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 337;
                            break;
                        }
                        case 339: {
                            if (this.curChar != 'x') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 338;
                            break;
                        }
                        case 340: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 330;
                            break;
                        }
                        case 341: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 340;
                            break;
                        }
                        case 342: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 341;
                            break;
                        }
                        case 343: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 342;
                            break;
                        }
                        case 344: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 343;
                            break;
                        }
                        case 345: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 346;
                            break;
                        }
                        case 347: {
                            if (this.curChar != 'i' || kind <= 61) break;
                            kind = 61;
                            break;
                        }
                        case 348: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 347;
                            break;
                        }
                        case 349: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 348;
                            break;
                        }
                        case 351: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 350;
                            break;
                        }
                        case 352: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 351;
                            break;
                        }
                        case 353: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 352;
                            break;
                        }
                        case 354: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 353;
                            break;
                        }
                        case 355: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 345;
                            break;
                        }
                        case 356: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 355;
                            break;
                        }
                        case 357: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 356;
                            break;
                        }
                        case 358: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 357;
                            break;
                        }
                        case 359: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 358;
                            break;
                        }
                        case 360: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 361;
                            break;
                        }
                        case 362: {
                            if (this.curChar != 'e' || kind <= 65) break;
                            kind = 65;
                            break;
                        }
                        case 363: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 362;
                            break;
                        }
                        case 364: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 363;
                            break;
                        }
                        case 365: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 364;
                            break;
                        }
                        case 366: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 365;
                            break;
                        }
                        case 367: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 366;
                            break;
                        }
                        case 368: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 367;
                            break;
                        }
                        case 369: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 368;
                            break;
                        }
                        case 370: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 369;
                            break;
                        }
                        case 371: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 360;
                            break;
                        }
                        case 372: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 371;
                            break;
                        }
                        case 373: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 372;
                            break;
                        }
                        case 374: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 373;
                            break;
                        }
                        case 375: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 374;
                            break;
                        }
                        case 376: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 377;
                            break;
                        }
                        case 378: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 379;
                            break;
                        }
                        case 380: {
                            if (this.curChar != 'n' || kind <= 92) break;
                            kind = 92;
                            break;
                        }
                        case 381: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 380;
                            break;
                        }
                        case 382: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 381;
                            break;
                        }
                        case 383: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 382;
                            break;
                        }
                        case 384: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 383;
                            break;
                        }
                        case 385: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 384;
                            break;
                        }
                        case 386: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 385;
                            break;
                        }
                        case 387: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 386;
                            break;
                        }
                        case 388: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 387;
                            break;
                        }
                        case 389: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 378;
                            break;
                        }
                        case 390: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 389;
                            break;
                        }
                        case 391: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 390;
                            break;
                        }
                        case 392: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 391;
                            break;
                        }
                        case 393: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 392;
                            break;
                        }
                        case 394: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 393;
                            break;
                        }
                        case 395: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 376;
                            break;
                        }
                        case 396: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 395;
                            break;
                        }
                        case 397: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 396;
                            break;
                        }
                        case 398: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 397;
                            break;
                        }
                        case 399: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 398;
                            break;
                        }
                        case 400: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 401;
                            break;
                        }
                        case 402: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 403;
                            break;
                        }
                        case 404: {
                            if (this.curChar != 't' || kind <= 94) break;
                            kind = 94;
                            break;
                        }
                        case 405: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 404;
                            break;
                        }
                        case 406: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 405;
                            break;
                        }
                        case 407: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 406;
                            break;
                        }
                        case 408: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 407;
                            break;
                        }
                        case 409: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 408;
                            break;
                        }
                        case 410: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 409;
                            break;
                        }
                        case 411: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 402;
                            break;
                        }
                        case 412: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 411;
                            break;
                        }
                        case 413: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 412;
                            break;
                        }
                        case 414: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 413;
                            break;
                        }
                        case 415: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 414;
                            break;
                        }
                        case 416: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 415;
                            break;
                        }
                        case 417: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 400;
                            break;
                        }
                        case 418: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 417;
                            break;
                        }
                        case 419: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 418;
                            break;
                        }
                        case 420: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 419;
                            break;
                        }
                        case 421: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 420;
                            break;
                        }
                        case 422: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 423;
                            break;
                        }
                        case 424: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 425;
                            break;
                        }
                        case 426: {
                            if (this.curChar != 'n' || kind <= 95) break;
                            kind = 95;
                            break;
                        }
                        case 427: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 426;
                            break;
                        }
                        case 428: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 427;
                            break;
                        }
                        case 429: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 428;
                            break;
                        }
                        case 430: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 429;
                            break;
                        }
                        case 431: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 430;
                            break;
                        }
                        case 432: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 431;
                            break;
                        }
                        case 433: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 432;
                            break;
                        }
                        case 434: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 424;
                            break;
                        }
                        case 435: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 434;
                            break;
                        }
                        case 436: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 435;
                            break;
                        }
                        case 437: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 436;
                            break;
                        }
                        case 438: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 437;
                            break;
                        }
                        case 439: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 438;
                            break;
                        }
                        case 440: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 422;
                            break;
                        }
                        case 441: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 440;
                            break;
                        }
                        case 442: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 441;
                            break;
                        }
                        case 443: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 442;
                            break;
                        }
                        case 444: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 443;
                            break;
                        }
                        case 445: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(210, 211);
                            break;
                        }
                        case 448: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 445;
                            break;
                        }
                        case 449: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 448;
                            break;
                        }
                        case 450: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 449;
                            break;
                        }
                        case 452: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 451;
                            break;
                        }
                        case 453: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 452;
                            break;
                        }
                        case 454: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 453;
                            break;
                        }
                        case 455: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 454;
                            break;
                        }
                        case 456: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 455;
                            break;
                        }
                        case 457: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 456;
                            break;
                        }
                        case 458: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 457;
                            break;
                        }
                        case 459: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(212, 213);
                            break;
                        }
                        case 461: {
                            if (this.curChar != '{' || kind <= 151) break;
                            kind = 151;
                            break;
                        }
                        case 462: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 459;
                            break;
                        }
                        case 463: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 462;
                            break;
                        }
                        case 464: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 463;
                            break;
                        }
                        case 465: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 464;
                            break;
                        }
                        case 466: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 465;
                            break;
                        }
                        case 467: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 466;
                            break;
                        }
                        case 468: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 469;
                            break;
                        }
                        case 470: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(216, 217);
                            break;
                        }
                        case 473: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 470;
                            break;
                        }
                        case 474: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 473;
                            break;
                        }
                        case 475: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 474;
                            break;
                        }
                        case 476: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 475;
                            break;
                        }
                        case 477: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 476;
                            break;
                        }
                        case 478: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 477;
                            break;
                        }
                        case 479: {
                            if (this.curChar != 'v') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 478;
                            break;
                        }
                        case 480: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 468;
                            break;
                        }
                        case 481: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 480;
                            break;
                        }
                        case 482: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 481;
                            break;
                        }
                        case 483: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 482;
                            break;
                        }
                        case 484: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 483;
                            break;
                        }
                        case 485: {
                            if (this.curChar != 'p') break;
                            this.jjAddStates(306, 311);
                            break;
                        }
                        case 486: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(218, 219);
                            break;
                        }
                        case 490: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 486;
                            break;
                        }
                        case 491: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 490;
                            break;
                        }
                        case 492: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 491;
                            break;
                        }
                        case 493: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 492;
                            break;
                        }
                        case 494: {
                            if (this.curChar != 'g') break;
                            this.jjAddStates(220, 221);
                            break;
                        }
                        case 498: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 494;
                            break;
                        }
                        case 499: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 498;
                            break;
                        }
                        case 500: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 499;
                            break;
                        }
                        case 501: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 500;
                            break;
                        }
                        case 502: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 501;
                            break;
                        }
                        case 503: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 502;
                            break;
                        }
                        case 505: {
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 504;
                            break;
                        }
                        case 506: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 505;
                            break;
                        }
                        case 507: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 506;
                            break;
                        }
                        case 508: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 507;
                            break;
                        }
                        case 509: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 508;
                            break;
                        }
                        case 510: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 509;
                            break;
                        }
                        case 511: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 510;
                            break;
                        }
                        case 512: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 511;
                            break;
                        }
                        case 513: {
                            if (this.curChar != 'g') break;
                            this.jjAddStates(222, 223);
                            break;
                        }
                        case 517: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 513;
                            break;
                        }
                        case 518: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 517;
                            break;
                        }
                        case 519: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 518;
                            break;
                        }
                        case 520: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 519;
                            break;
                        }
                        case 521: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 520;
                            break;
                        }
                        case 522: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 521;
                            break;
                        }
                        case 523: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 522;
                            break;
                        }
                        case 524: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 525;
                            break;
                        }
                        case 526: 
                        case 527: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(226, 228);
                            break;
                        }
                        case 529: {
                            if (this.curChar != '{' || kind <= 86) break;
                            kind = 86;
                            break;
                        }
                        case 530: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 524;
                            break;
                        }
                        case 531: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 530;
                            break;
                        }
                        case 532: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 531;
                            break;
                        }
                        case 533: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 532;
                            break;
                        }
                        case 534: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 533;
                            break;
                        }
                        case 535: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 534;
                            break;
                        }
                        case 536: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 535;
                            break;
                        }
                        case 537: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 536;
                            break;
                        }
                        case 538: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 537;
                            break;
                        }
                        case 539: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 538;
                            break;
                        }
                        case 541: {
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 540;
                            break;
                        }
                        case 542: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 541;
                            break;
                        }
                        case 543: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 542;
                            break;
                        }
                        case 544: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 543;
                            break;
                        }
                        case 545: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 544;
                            break;
                        }
                        case 546: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 545;
                            break;
                        }
                        case 547: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 546;
                            break;
                        }
                        case 548: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 547;
                            break;
                        }
                        case 549: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 548;
                            break;
                        }
                        case 550: {
                            if (this.curChar != 'n') break;
                            this.jjAddStates(229, 230);
                            break;
                        }
                        case 552: {
                            if (this.curChar != '{' || kind <= 87) break;
                            kind = 87;
                            break;
                        }
                        case 553: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 550;
                            break;
                        }
                        case 554: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 553;
                            break;
                        }
                        case 555: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 554;
                            break;
                        }
                        case 556: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 555;
                            break;
                        }
                        case 557: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 556;
                            break;
                        }
                        case 558: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 557;
                            break;
                        }
                        case 559: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 558;
                            break;
                        }
                        case 560: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 559;
                            break;
                        }
                        case 561: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 560;
                            break;
                        }
                        case 562: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 561;
                            break;
                        }
                        case 564: {
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 563;
                            break;
                        }
                        case 565: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 564;
                            break;
                        }
                        case 566: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 565;
                            break;
                        }
                        case 567: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 566;
                            break;
                        }
                        case 568: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 567;
                            break;
                        }
                        case 569: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 568;
                            break;
                        }
                        case 570: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 569;
                            break;
                        }
                        case 571: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 570;
                            break;
                        }
                        case 572: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 571;
                            break;
                        }
                        case 573: {
                            if (this.curChar != 'n') break;
                            this.jjAddStates(231, 232);
                            break;
                        }
                        case 576: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 573;
                            break;
                        }
                        case 577: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 576;
                            break;
                        }
                        case 578: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 577;
                            break;
                        }
                        case 579: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 578;
                            break;
                        }
                        case 580: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 579;
                            break;
                        }
                        case 581: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 580;
                            break;
                        }
                        case 582: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 581;
                            break;
                        }
                        case 583: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 582;
                            break;
                        }
                        case 584: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 583;
                            break;
                        }
                        case 585: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 584;
                            break;
                        }
                        case 587: {
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 586;
                            break;
                        }
                        case 588: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 587;
                            break;
                        }
                        case 589: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 588;
                            break;
                        }
                        case 590: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 589;
                            break;
                        }
                        case 591: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 590;
                            break;
                        }
                        case 592: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 591;
                            break;
                        }
                        case 593: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 592;
                            break;
                        }
                        case 594: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 593;
                            break;
                        }
                        case 595: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 594;
                            break;
                        }
                        case 596: {
                            if (this.curChar != 's') break;
                            this.jjAddStates(302, 305);
                            break;
                        }
                        case 597: {
                            if (this.curChar != 'f') break;
                            this.jjAddStates(233, 234);
                            break;
                        }
                        case 601: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 597;
                            break;
                        }
                        case 602: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 601;
                            break;
                        }
                        case 603: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(235, 236);
                            break;
                        }
                        case 606: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 603;
                            break;
                        }
                        case 607: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 606;
                            break;
                        }
                        case 608: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 607;
                            break;
                        }
                        case 609: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 608;
                            break;
                        }
                        case 610: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 609;
                            break;
                        }
                        case 611: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 610;
                            break;
                        }
                        case 613: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 612;
                            break;
                        }
                        case 614: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 613;
                            break;
                        }
                        case 615: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 614;
                            break;
                        }
                        case 616: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 615;
                            break;
                        }
                        case 617: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 616;
                            break;
                        }
                        case 618: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(237, 238);
                            break;
                        }
                        case 621: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 618;
                            break;
                        }
                        case 622: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 621;
                            break;
                        }
                        case 623: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 622;
                            break;
                        }
                        case 624: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 623;
                            break;
                        }
                        case 625: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 624;
                            break;
                        }
                        case 626: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 625;
                            break;
                        }
                        case 627: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 626;
                            break;
                        }
                        case 628: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 627;
                            break;
                        }
                        case 630: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 629;
                            break;
                        }
                        case 631: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 630;
                            break;
                        }
                        case 632: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 631;
                            break;
                        }
                        case 633: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 632;
                            break;
                        }
                        case 634: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 633;
                            break;
                        }
                        case 635: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(239, 240);
                            break;
                        }
                        case 638: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 635;
                            break;
                        }
                        case 639: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 638;
                            break;
                        }
                        case 640: {
                            if (this.curChar != 'f') break;
                            this.jjAddStates(299, 301);
                            break;
                        }
                        case 641: {
                            if (this.curChar != 'g') break;
                            this.jjAddStates(241, 242);
                            break;
                        }
                        case 645: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 641;
                            break;
                        }
                        case 646: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 645;
                            break;
                        }
                        case 647: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 646;
                            break;
                        }
                        case 648: {
                            if (this.curChar != 'b') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 647;
                            break;
                        }
                        case 649: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 648;
                            break;
                        }
                        case 650: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 649;
                            break;
                        }
                        case 652: {
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 651;
                            break;
                        }
                        case 653: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 652;
                            break;
                        }
                        case 654: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 653;
                            break;
                        }
                        case 655: {
                            if (this.curChar != 'w') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 654;
                            break;
                        }
                        case 656: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 655;
                            break;
                        }
                        case 657: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 656;
                            break;
                        }
                        case 658: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 657;
                            break;
                        }
                        case 659: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 658;
                            break;
                        }
                        case 660: {
                            if (this.curChar != 'g') break;
                            this.jjAddStates(243, 244);
                            break;
                        }
                        case 664: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 660;
                            break;
                        }
                        case 665: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 664;
                            break;
                        }
                        case 666: {
                            if (this.curChar != 'w') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 665;
                            break;
                        }
                        case 667: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 666;
                            break;
                        }
                        case 668: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 667;
                            break;
                        }
                        case 669: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 668;
                            break;
                        }
                        case 670: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 669;
                            break;
                        }
                        case 671: {
                            if (this.curChar != 'r') break;
                            this.jjAddStates(245, 246);
                            break;
                        }
                        case 674: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 671;
                            break;
                        }
                        case 675: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(295, 298);
                            break;
                        }
                        case 676: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(247, 248);
                            break;
                        }
                        case 679: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 676;
                            break;
                        }
                        case 680: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 679;
                            break;
                        }
                        case 681: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 680;
                            break;
                        }
                        case 682: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 681;
                            break;
                        }
                        case 683: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 682;
                            break;
                        }
                        case 684: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 685;
                            break;
                        }
                        case 686: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(351, 355);
                            break;
                        }
                        case 687: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(687, 688);
                            break;
                        }
                        case 689: 
                        case 690: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(253, 255);
                            break;
                        }
                        case 692: {
                            if (this.curChar != '{' || kind <= 84) break;
                            kind = 84;
                            break;
                        }
                        case 693: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 684;
                            break;
                        }
                        case 694: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 693;
                            break;
                        }
                        case 695: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 694;
                            break;
                        }
                        case 696: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 695;
                            break;
                        }
                        case 697: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 696;
                            break;
                        }
                        case 698: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(256, 257);
                            break;
                        }
                        case 700: {
                            if (this.curChar != '{' || kind <= 89) break;
                            kind = 89;
                            break;
                        }
                        case 701: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 698;
                            break;
                        }
                        case 702: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 701;
                            break;
                        }
                        case 703: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 702;
                            break;
                        }
                        case 704: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 703;
                            break;
                        }
                        case 705: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 704;
                            break;
                        }
                        case 706: {
                            if (this.curChar != 'y') break;
                            this.jjAddStates(258, 259);
                            break;
                        }
                        case 709: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 706;
                            break;
                        }
                        case 710: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 709;
                            break;
                        }
                        case 711: {
                            if (this.curChar != 'v') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 710;
                            break;
                        }
                        case 712: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(292, 294);
                            break;
                        }
                        case 713: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(260, 261);
                            break;
                        }
                        case 715: {
                            if (this.curChar != '{' || kind <= 91) break;
                            kind = 91;
                            break;
                        }
                        case 716: {
                            if (this.curChar != 'x') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 713;
                            break;
                        }
                        case 717: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 716;
                            break;
                        }
                        case 718: {
                            if (this.curChar != 't') break;
                            this.jjAddStates(262, 263);
                            break;
                        }
                        case 721: {
                            if (this.curChar != 'x') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 718;
                            break;
                        }
                        case 722: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 721;
                            break;
                        }
                        case 723: {
                            if (this.curChar != 'h') break;
                            this.jjAddStates(264, 265);
                            break;
                        }
                        case 726: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 723;
                            break;
                        }
                        case 727: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 726;
                            break;
                        }
                        case 728: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 727;
                            break;
                        }
                        case 729: {
                            if (this.curChar != 'w') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 728;
                            break;
                        }
                        case 730: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 729;
                            break;
                        }
                        case 731: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 730;
                            break;
                        }
                        case 732: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 731;
                            break;
                        }
                        case 733: {
                            if (this.curChar != 'y') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 732;
                            break;
                        }
                        case 734: {
                            if (this.curChar != 'i') break;
                            this.jjAddStates(289, 291);
                            break;
                        }
                        case 735: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 736;
                            break;
                        }
                        case 737: {
                            if (this.curChar != 'a' || kind <= 97) break;
                            kind = 97;
                            break;
                        }
                        case 738: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 737;
                            break;
                        }
                        case 739: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 738;
                            break;
                        }
                        case 740: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 739;
                            break;
                        }
                        case 741: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 740;
                            break;
                        }
                        case 742: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 741;
                            break;
                        }
                        case 743: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 735;
                            break;
                        }
                        case 744: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 743;
                            break;
                        }
                        case 745: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 744;
                            break;
                        }
                        case 746: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 745;
                            break;
                        }
                        case 747: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 748;
                            break;
                        }
                        case 749: {
                            if (this.curChar != 'e' || kind <= 98) break;
                            kind = 98;
                            break;
                        }
                        case 750: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 749;
                            break;
                        }
                        case 751: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 750;
                            break;
                        }
                        case 752: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 751;
                            break;
                        }
                        case 753: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 752;
                            break;
                        }
                        case 754: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 753;
                            break;
                        }
                        case 755: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 747;
                            break;
                        }
                        case 756: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 755;
                            break;
                        }
                        case 757: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 756;
                            break;
                        }
                        case 758: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 757;
                            break;
                        }
                        case 759: {
                            if (this.curChar != 'f') break;
                            this.jjAddStates(270, 271);
                            break;
                        }
                        case 762: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            if (kind > 235) {
                                kind = 235;
                            }
                            this.jjCheckNAddStates(277, 286);
                            break;
                        }
                        case 763: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(763, 764);
                            break;
                        }
                        case 766: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(766, 767);
                            break;
                        }
                        case 768: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(109, 111);
                            break;
                        }
                        case 769: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(109, 111);
                            break;
                        }
                        case 772: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(772, 773);
                            break;
                        }
                        case 774: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            if (kind > 235) {
                                kind = 235;
                            }
                            this.jjCheckNAdd(775);
                            break;
                        }
                        case 775: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            if (kind > 235) {
                                kind = 235;
                            }
                            this.jjCheckNAdd(775);
                            break;
                        }
                        case 776: {
                            if (this.curChar != 'v') break;
                            this.jjAddStates(287, 288);
                            break;
                        }
                        case 777: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(272, 273);
                            break;
                        }
                        case 779: {
                            if (this.curChar != '{' || kind <= 146) break;
                            kind = 146;
                            break;
                        }
                        case 780: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 777;
                            break;
                        }
                        case 781: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 780;
                            break;
                        }
                        case 782: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 781;
                            break;
                        }
                        case 783: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 782;
                            break;
                        }
                        case 784: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 783;
                            break;
                        }
                        case 785: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 784;
                            break;
                        }
                        case 786: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 787;
                            break;
                        }
                        case 788: {
                            if (this.curChar != 'x' || kind <= 147) break;
                            kind = 147;
                            break;
                        }
                        case 789: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 788;
                            break;
                        }
                        case 790: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 789;
                            break;
                        }
                        case 791: {
                            if (this.curChar != 't' || kind <= 147) break;
                            kind = 147;
                            break;
                        }
                        case 792: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 791;
                            break;
                        }
                        case 793: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 792;
                            break;
                        }
                        case 794: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 793;
                            break;
                        }
                        case 795: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 794;
                            break;
                        }
                        case 796: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 795;
                            break;
                        }
                        case 797: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 786;
                            break;
                        }
                        case 798: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 797;
                            break;
                        }
                        case 799: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 798;
                            break;
                        }
                        case 800: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 799;
                            break;
                        }
                        case 801: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 800;
                            break;
                        }
                        case 802: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 801;
                            break;
                        }
                    }
                    if (i != startsAt) {
                        continue;
                    }
                    break;
                }
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 0x3F);
                int i2 = (this.curChar & 0xFF) >> 6;
                long l2 = 1L << (this.curChar & 0x3F);
                block856: do {
                    switch (this.jjstateSet[--i]) {
                        case 804: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(763, 764);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(766, 767);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddStates(109, 111);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(772, 773);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            if (kind > 235) {
                                kind = 235;
                            }
                            this.jjCheckNAdd(775);
                            break;
                        }
                        case 96: {
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(763, 764);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(766, 767);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddStates(109, 111);
                            }
                            if (XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(772, 773);
                            }
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            if (kind > 235) {
                                kind = 235;
                            }
                            this.jjCheckNAdd(775);
                            break;
                        }
                        case 19: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block856;
                            if (kind > 235) {
                                kind = 235;
                            }
                            this.jjCheckNAddStates(277, 286);
                            break;
                        }
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 4: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjAddStates(2, 3);
                            break;
                        }
                        case 59: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block856;
                            if (kind > 104) {
                                kind = 104;
                            }
                            this.jjCheckNAdd(60);
                            break;
                        }
                        case 60: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            if (kind > 104) {
                                kind = 104;
                            }
                            this.jjCheckNAdd(60);
                            break;
                        }
                        case 99: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjAddStates(342, 343);
                            break;
                        }
                        case 102: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjAddStates(344, 345);
                            break;
                        }
                        case 155: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(346, 350);
                            break;
                        }
                        case 156: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddTwoStates(156, 157);
                            break;
                        }
                        case 158: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(165, 167);
                            break;
                        }
                        case 159: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(165, 167);
                            break;
                        }
                        case 526: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(226, 228);
                            break;
                        }
                        case 527: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(226, 228);
                            break;
                        }
                        case 686: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(351, 355);
                            break;
                        }
                        case 687: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddTwoStates(687, 688);
                            break;
                        }
                        case 689: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(253, 255);
                            break;
                        }
                        case 690: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(253, 255);
                            break;
                        }
                        case 763: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddTwoStates(763, 764);
                            break;
                        }
                        case 766: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddTwoStates(766, 767);
                            break;
                        }
                        case 768: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(109, 111);
                            break;
                        }
                        case 769: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddStates(109, 111);
                            break;
                        }
                        case 772: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            this.jjCheckNAddTwoStates(772, 773);
                            break;
                        }
                        case 774: {
                            if (!XPathTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block856;
                            if (kind > 235) {
                                kind = 235;
                            }
                            this.jjCheckNAdd(775);
                            break;
                        }
                        case 775: {
                            if (!XPathTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block856;
                            if (kind > 235) {
                                kind = 235;
                            }
                            this.jjCheckNAdd(775);
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
            startsAt = 803 - this.jjnewStateCnt;
            if (i == startsAt) {
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

    private final int jjStopStringLiteralDfa_20(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active3 & 0x4028000L) != 0L) {
                    this.jjmatchedKind = 212;
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_20(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_20(this.jjStopStringLiteralDfa_20(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_20(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_20(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_20() {
        switch (this.curChar) {
            case '\'': {
                this.jjmatchedKind = 212;
                return this.jjMoveStringLiteralDfa1_20(131072L);
            }
            case '{': {
                this.jjmatchedKind = 204;
                return this.jjMoveStringLiteralDfa1_20(16384L);
            }
            case '}': {
                return this.jjMoveStringLiteralDfa1_20(32768L);
            }
        }
        return this.jjMoveNfa_20(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_20(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_20(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case '\'': {
                if ((active3 & 0x20000L) == 0L) break;
                return this.jjStopAtPos(1, 209);
            }
            case '{': {
                if ((active3 & 0x4000L) == 0L) break;
                return this.jjStopAtPos(1, 206);
            }
            case '}': {
                if ((active3 & 0x8000L) == 0L) break;
                return this.jjStopAtPos(1, 207);
            }
        }
        return this.jjStartNfa_20(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_20(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 21;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block33: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFF00002600L & l) != 0L && kind > 212) {
                                kind = 212;
                            }
                            if (this.curChar == '&') {
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                            }
                            if (this.curChar != '&') break;
                            this.jjAddStates(77, 80);
                            break;
                        }
                        case 2: {
                            if (this.curChar != ';' || kind <= 193) continue block33;
                            kind = 193;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddTwoStates(15, 17);
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(15, 16);
                            break;
                        }
                        case 16: {
                            if (this.curChar != ';' || kind <= 194) continue block33;
                            kind = 194;
                            break;
                        }
                        case 18: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(18, 16);
                            break;
                        }
                        case 19: {
                            if (this.curChar != '&') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 20: {
                            if ((0xFFFFFFFF00002600L & l) == 0L || kind <= 212) continue block33;
                            kind = 212;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind <= 212) break;
                            kind = 212;
                            break;
                        }
                        case 1: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 3: {
                            if (this.curChar != 'l') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 4: {
                            if (this.curChar != 'g') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'o') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'q') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'a') break;
                            this.jjAddStates(81, 82);
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'p') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 's') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'x') break;
                            this.jjCheckNAdd(18);
                            break;
                        }
                        case 18: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(18, 16);
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
                block35: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!XPathTokenManager.jjCanMove_3(hiByte, i1, i2, l1, l2) || kind <= 212) continue block35;
                            kind = 212;
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
            if (i == (startsAt = 21 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_11(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active3 & 0x800000000L) != 0L) {
                    this.jjmatchedKind = 14;
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_11(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_11(this.jjStopStringLiteralDfa_11(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_11(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_11() {
        switch (this.curChar) {
            case '(': {
                return this.jjMoveStringLiteralDfa1_11(0x800000000L);
            }
            case '*': {
                return this.jjStopAtPos(0, 131);
            }
            case '+': {
                return this.jjStopAtPos(0, 132);
            }
            case '?': {
                return this.jjStopAtPos(0, 130);
            }
        }
        return this.jjMoveNfa_11(1, 0);
    }

    private final int jjMoveStringLiteralDfa1_11(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_11(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) == 0L) break;
                return this.jjStopAtPos(1, 227);
            }
        }
        return this.jjStartNfa_11(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_11(int startState, int curPos) {
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
                        case 1: {
                            if ((0x7FFFF3FFFFFFFFFFL & l) != 0L && kind > 14) {
                                kind = 14;
                            }
                            if ((0x100002600L & l) == 0L) break;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 0: {
                            if ((0x100002600L & l) == 0L) continue block13;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(0);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            kind = 14;
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
                        case 1: {
                            if (!XPathTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2) || kind <= 14) continue block15;
                            kind = 14;
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

    private final int jjStopStringLiteralDfa_17(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            case 0: {
                if ((active3 & 0x8000L) != 0L) {
                    this.jjmatchedKind = 211;
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_17(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_17(this.jjStopStringLiteralDfa_17(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_17(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_17(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_17() {
        switch (this.curChar) {
            case '\"': {
                this.jjmatchedKind = 173;
                return this.jjMoveStringLiteralDfa1_17(65536L);
            }
            case '{': {
                this.jjmatchedKind = 204;
                return this.jjMoveStringLiteralDfa1_17(16384L);
            }
            case '}': {
                return this.jjMoveStringLiteralDfa1_17(32768L);
            }
        }
        return this.jjMoveNfa_17(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_17(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_17(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case '\"': {
                if ((active3 & 0x10000L) == 0L) break;
                return this.jjStopAtPos(1, 208);
            }
            case '{': {
                if ((active3 & 0x4000L) == 0L) break;
                return this.jjStopAtPos(1, 206);
            }
            case '}': {
                if ((active3 & 0x8000L) == 0L) break;
                return this.jjStopAtPos(1, 207);
            }
        }
        return this.jjStartNfa_17(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_17(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 21;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block33: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFF00002600L & l) != 0L && kind > 211) {
                                kind = 211;
                            }
                            if (this.curChar == '&') {
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                            }
                            if (this.curChar != '&') break;
                            this.jjAddStates(77, 80);
                            break;
                        }
                        case 2: {
                            if (this.curChar != ';' || kind <= 193) continue block33;
                            kind = 193;
                            break;
                        }
                        case 14: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAddTwoStates(15, 17);
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(15, 16);
                            break;
                        }
                        case 16: {
                            if (this.curChar != ';' || kind <= 194) continue block33;
                            kind = 194;
                            break;
                        }
                        case 18: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(18, 16);
                            break;
                        }
                        case 19: {
                            if (this.curChar != '&') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 20: {
                            if ((0xFFFFFFFF00002600L & l) == 0L || kind <= 211) continue block33;
                            kind = 211;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind <= 211) break;
                            kind = 211;
                            break;
                        }
                        case 1: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 3: {
                            if (this.curChar != 'l') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 4: {
                            if (this.curChar != 'g') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'o') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'q') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'a') break;
                            this.jjAddStates(81, 82);
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'p') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 's') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'x') break;
                            this.jjCheckNAdd(18);
                            break;
                        }
                        case 18: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(18, 16);
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
                block35: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!XPathTokenManager.jjCanMove_3(hiByte, i1, i2, l1, l2) || kind <= 211) continue block35;
                            kind = 211;
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
            if (i == (startsAt = 21 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_12(int pos, long active0, long active1, long active2, long active3) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_12(int pos, long active0, long active1, long active2, long active3) {
        return this.jjMoveNfa_12(this.jjStopStringLiteralDfa_12(pos, active0, active1, active2, active3), pos + 1);
    }

    private final int jjStartNfaWithStates_12(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_12(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_12() {
        switch (this.curChar) {
            case '(': {
                return this.jjMoveStringLiteralDfa1_12(0x800000000L);
            }
            case ')': {
                return this.jjStopAtPos(0, 139);
            }
            case ',': {
                return this.jjStopAtPos(0, 169);
            }
            case '?': {
                return this.jjStopAtPos(0, 53);
            }
            case '{': {
                return this.jjStopAtPos(0, 205);
            }
        }
        return this.jjMoveNfa_12(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_12(long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_12(0, 0L, 0L, 0L, active3);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active3 & 0x800000000L) == 0L) break;
                return this.jjStopAtPos(1, 227);
            }
        }
        return this.jjStartNfa_12(0, 0L, 0L, 0L, active3);
    }

    private final int jjMoveNfa_12(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 1;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block10: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x100002600L & l) == 0L) continue block10;
                            kind = 12;
                            this.jjstateSet[this.jjnewStateCnt++] = 0;
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
            if (i == (startsAt = 1 - this.jjnewStateCnt)) {
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
            case 1: {
                return (jjbitVec3[i2] & l2) != 0L;
            }
            case 2: {
                return (jjbitVec4[i2] & l2) != 0L;
            }
            case 3: {
                return (jjbitVec5[i2] & l2) != 0L;
            }
            case 4: {
                return (jjbitVec6[i2] & l2) != 0L;
            }
            case 5: {
                return (jjbitVec7[i2] & l2) != 0L;
            }
            case 6: {
                return (jjbitVec8[i2] & l2) != 0L;
            }
            case 9: {
                return (jjbitVec9[i2] & l2) != 0L;
            }
            case 10: {
                return (jjbitVec10[i2] & l2) != 0L;
            }
            case 11: {
                return (jjbitVec11[i2] & l2) != 0L;
            }
            case 12: {
                return (jjbitVec12[i2] & l2) != 0L;
            }
            case 13: {
                return (jjbitVec13[i2] & l2) != 0L;
            }
            case 14: {
                return (jjbitVec14[i2] & l2) != 0L;
            }
            case 15: {
                return (jjbitVec15[i2] & l2) != 0L;
            }
            case 16: {
                return (jjbitVec16[i2] & l2) != 0L;
            }
            case 17: {
                return (jjbitVec17[i2] & l2) != 0L;
            }
            case 30: {
                return (jjbitVec18[i2] & l2) != 0L;
            }
            case 31: {
                return (jjbitVec19[i2] & l2) != 0L;
            }
            case 33: {
                return (jjbitVec20[i2] & l2) != 0L;
            }
            case 48: {
                return (jjbitVec21[i2] & l2) != 0L;
            }
            case 49: {
                return (jjbitVec22[i2] & l2) != 0L;
            }
            case 159: {
                return (jjbitVec23[i2] & l2) != 0L;
            }
            case 215: {
                return (jjbitVec24[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec25[i2] & l2) != 0L;
            }
            case 1: {
                return (jjbitVec3[i2] & l2) != 0L;
            }
            case 2: {
                return (jjbitVec26[i2] & l2) != 0L;
            }
            case 3: {
                return (jjbitVec27[i2] & l2) != 0L;
            }
            case 4: {
                return (jjbitVec28[i2] & l2) != 0L;
            }
            case 5: {
                return (jjbitVec29[i2] & l2) != 0L;
            }
            case 6: {
                return (jjbitVec30[i2] & l2) != 0L;
            }
            case 9: {
                return (jjbitVec31[i2] & l2) != 0L;
            }
            case 10: {
                return (jjbitVec32[i2] & l2) != 0L;
            }
            case 11: {
                return (jjbitVec33[i2] & l2) != 0L;
            }
            case 12: {
                return (jjbitVec34[i2] & l2) != 0L;
            }
            case 13: {
                return (jjbitVec35[i2] & l2) != 0L;
            }
            case 14: {
                return (jjbitVec36[i2] & l2) != 0L;
            }
            case 15: {
                return (jjbitVec37[i2] & l2) != 0L;
            }
            case 16: {
                return (jjbitVec16[i2] & l2) != 0L;
            }
            case 17: {
                return (jjbitVec17[i2] & l2) != 0L;
            }
            case 30: {
                return (jjbitVec18[i2] & l2) != 0L;
            }
            case 31: {
                return (jjbitVec19[i2] & l2) != 0L;
            }
            case 32: {
                return (jjbitVec38[i2] & l2) != 0L;
            }
            case 33: {
                return (jjbitVec20[i2] & l2) != 0L;
            }
            case 48: {
                return (jjbitVec39[i2] & l2) != 0L;
            }
            case 49: {
                return (jjbitVec22[i2] & l2) != 0L;
            }
            case 159: {
                return (jjbitVec23[i2] & l2) != 0L;
            }
            case 215: {
                return (jjbitVec24[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    private static final boolean jjCanMove_2(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec41[i2] & l2) != 0L;
            }
        }
        return (jjbitVec40[i1] & l1) != 0L;
    }

    private static final boolean jjCanMove_3(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec41[i2] & l2) != 0L;
            }
            case 255: {
                return (jjbitVec43[i2] & l2) != 0L;
            }
        }
        return (jjbitVec42[i1] & l1) != 0L;
    }

    public XPathTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public XPathTokenManager(SimpleCharStream stream, int lexState) {
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
        int i = 803;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 26 || lexState < 0) {
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
        while (true) {
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
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_6();
                    break;
                }
                case 7: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_7();
                    break;
                }
                case 8: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
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
                    break;
                }
                case 12: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_12();
                    break;
                }
                case 13: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_13();
                    break;
                }
                case 14: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_14();
                    break;
                }
                case 15: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_15();
                    break;
                }
                case 16: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_16();
                    break;
                }
                case 17: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_17();
                    break;
                }
                case 18: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_18();
                    break;
                }
                case 19: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_19();
                    break;
                }
                case 20: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_20();
                    break;
                }
                case 21: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_21();
                    break;
                }
                case 22: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_22();
                    break;
                }
                case 23: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_23();
                    break;
                }
                case 24: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_24();
                    break;
                }
                case 25: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_25();
                }
            }
            if (this.jjmatchedKind == Integer.MAX_VALUE) break;
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
            if (jjnewLexState[this.jjmatchedKind] == -1) continue;
            this.curLexState = jjnewLexState[this.jjmatchedKind];
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
            case 14: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.input_stream.backup(1);
                break;
            }
            case 222: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState();
                break;
            }
            case 224: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.popState();
                break;
            }
            case 227: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState();
                break;
            }
            case 229: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.popState();
                break;
            }
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 15: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[15]);
                this.pushState(1);
                break;
            }
            case 16: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[16]);
                this.pushState();
                break;
            }
            case 17: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[17]);
                this.popState();
                break;
            }
            case 78: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 79: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 80: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 81: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 82: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 83: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 84: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 85: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 86: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 87: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 88: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 89: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 90: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 91: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 139: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[139]);
                this.popState();
                break;
            }
            case 146: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 147: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 149: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 150: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(11);
                break;
            }
            case 151: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 152: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 153: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 154: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 155: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(1);
                break;
            }
            case 156: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(11);
                break;
            }
            case 157: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(7);
                break;
            }
            case 158: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(11);
                break;
            }
            case 159: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(11);
                break;
            }
            case 160: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(7);
                break;
            }
            case 161: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(11);
                break;
            }
            case 162: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(11);
                break;
            }
            case 163: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(11);
                break;
            }
            case 164: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(11);
                break;
            }
            case 165: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.pushState(11);
                break;
            }
            case 190: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[190]);
                this.pushState(1);
                break;
            }
            case 191: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[191]);
                this.pushState();
                break;
            }
            case 192: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.popState();
                break;
            }
            case 196: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[196]);
                this.pushState();
                break;
            }
            case 197: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[197]);
                this.pushState(1);
                break;
            }
            case 199: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[199]);
                this.popState();
                break;
            }
            case 201: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[201]);
                this.popState();
                break;
            }
            case 204: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[204]);
                this.pushState();
                break;
            }
            case 205: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[205]);
                this.pushState(1);
                break;
            }
            case 232: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[232]);
                this.pushState(1);
                break;
            }
            case 233: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[233]);
                this.pushState();
                break;
            }
            case 234: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[234]);
                this.popState();
                break;
            }
            case 241: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(jjstrLiteralImages[241]);
                this.popState();
                break;
            }
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.regex;

import java.io.Serializable;
import java.text.CharacterIterator;
import java.util.Locale;
import java.util.Stack;
import org.apache.xmlbeans.impl.regex.BMPattern;
import org.apache.xmlbeans.impl.regex.IntStack;
import org.apache.xmlbeans.impl.regex.Match;
import org.apache.xmlbeans.impl.regex.Op;
import org.apache.xmlbeans.impl.regex.ParseException;
import org.apache.xmlbeans.impl.regex.ParserForXMLSchema;
import org.apache.xmlbeans.impl.regex.REUtil;
import org.apache.xmlbeans.impl.regex.RangeToken;
import org.apache.xmlbeans.impl.regex.RegexParser;
import org.apache.xmlbeans.impl.regex.Token;

public class RegularExpression
implements Serializable {
    private static final long serialVersionUID = 6242499334195006401L;
    static final boolean DEBUG = false;
    String regex;
    int options;
    int nofparen;
    Token tokentree;
    boolean hasBackReferences = false;
    transient int minlength;
    transient Op operations = null;
    transient int numberOfClosures;
    transient Context context = null;
    transient RangeToken firstChar = null;
    transient String fixedString = null;
    transient int fixedStringOptions;
    transient BMPattern fixedStringTable = null;
    transient boolean fixedStringOnly = false;
    static final int IGNORE_CASE = 2;
    static final int SINGLE_LINE = 4;
    static final int MULTIPLE_LINES = 8;
    static final int EXTENDED_COMMENT = 16;
    static final int USE_UNICODE_CATEGORY = 32;
    static final int UNICODE_WORD_BOUNDARY = 64;
    static final int PROHIBIT_HEAD_CHARACTER_OPTIMIZATION = 128;
    static final int PROHIBIT_FIXED_STRING_OPTIMIZATION = 256;
    static final int XMLSCHEMA_MODE = 512;
    static final int SPECIAL_COMMA = 1024;
    private static final int WT_IGNORE = 0;
    private static final int WT_LETTER = 1;
    private static final int WT_OTHER = 2;
    static final int LINE_FEED = 10;
    static final int CARRIAGE_RETURN = 13;
    static final int LINE_SEPARATOR = 8232;
    static final int PARAGRAPH_SEPARATOR = 8233;

    private synchronized void compile(Token tok) {
        if (this.operations != null) {
            return;
        }
        this.numberOfClosures = 0;
        this.operations = this.compile(tok, null, false);
    }

    private Op compile(Token tok, Op next, boolean reverse) {
        Op ret;
        switch (tok.type) {
            case 11: {
                ret = Op.createDot();
                ret.next = next;
                break;
            }
            case 0: {
                ret = Op.createChar(tok.getChar());
                ret.next = next;
                break;
            }
            case 8: {
                ret = Op.createAnchor(tok.getChar());
                ret.next = next;
                break;
            }
            case 4: 
            case 5: {
                ret = Op.createRange(tok);
                ret.next = next;
                break;
            }
            case 1: {
                ret = next;
                if (!reverse) {
                    for (int i = tok.size() - 1; i >= 0; --i) {
                        ret = this.compile(tok.getChild(i), ret, false);
                    }
                } else {
                    for (int i = 0; i < tok.size(); ++i) {
                        ret = this.compile(tok.getChild(i), ret, true);
                    }
                }
                break;
            }
            case 2: {
                Op.UnionOp uni = Op.createUnion(tok.size());
                for (int i = 0; i < tok.size(); ++i) {
                    uni.addElement(this.compile(tok.getChild(i), next, reverse));
                }
                ret = uni;
                break;
            }
            case 3: 
            case 9: {
                Token child = tok.getChild(0);
                int min = tok.getMin();
                int max = tok.getMax();
                if (min >= 0 && min == max) {
                    ret = next;
                    for (int i = 0; i < min; ++i) {
                        ret = this.compile(child, ret, reverse);
                    }
                } else {
                    if (min > 0 && max > 0) {
                        max -= min;
                    }
                    if (max > 0) {
                        ret = next;
                        for (int i = 0; i < max; ++i) {
                            Op.ChildOp q = Op.createQuestion(tok.type == 9);
                            q.next = next;
                            q.setChild(this.compile(child, ret, reverse));
                            ret = q;
                        }
                    } else {
                        Op.ChildOp op = tok.type == 9 ? Op.createNonGreedyClosure() : Op.createClosure(this.numberOfClosures++);
                        op.next = next;
                        op.setChild(this.compile(child, op, reverse));
                        ret = op;
                    }
                    if (min <= 0) break;
                    for (int i = 0; i < min; ++i) {
                        ret = this.compile(child, ret, reverse);
                    }
                }
                break;
            }
            case 7: {
                ret = next;
                break;
            }
            case 10: {
                ret = Op.createString(tok.getString());
                ret.next = next;
                break;
            }
            case 12: {
                ret = Op.createBackReference(tok.getReferenceNumber());
                ret.next = next;
                break;
            }
            case 6: {
                if (tok.getParenNumber() == 0) {
                    ret = this.compile(tok.getChild(0), next, reverse);
                    break;
                }
                if (reverse) {
                    next = Op.createCapture(tok.getParenNumber(), next);
                    next = this.compile(tok.getChild(0), next, reverse);
                    ret = Op.createCapture(-tok.getParenNumber(), next);
                    break;
                }
                next = Op.createCapture(-tok.getParenNumber(), next);
                next = this.compile(tok.getChild(0), next, reverse);
                ret = Op.createCapture(tok.getParenNumber(), next);
                break;
            }
            case 20: {
                ret = Op.createLook(20, next, this.compile(tok.getChild(0), null, false));
                break;
            }
            case 21: {
                ret = Op.createLook(21, next, this.compile(tok.getChild(0), null, false));
                break;
            }
            case 22: {
                ret = Op.createLook(22, next, this.compile(tok.getChild(0), null, true));
                break;
            }
            case 23: {
                ret = Op.createLook(23, next, this.compile(tok.getChild(0), null, true));
                break;
            }
            case 24: {
                ret = Op.createIndependent(next, this.compile(tok.getChild(0), null, reverse));
                break;
            }
            case 25: {
                ret = Op.createModifier(next, this.compile(tok.getChild(0), null, reverse), ((Token.ModifierToken)tok).getOptions(), ((Token.ModifierToken)tok).getOptionsMask());
                break;
            }
            case 26: {
                Token.ConditionToken ctok = (Token.ConditionToken)tok;
                int ref = ctok.refNumber;
                Op condition = ctok.condition == null ? null : this.compile(ctok.condition, null, reverse);
                Op yes = this.compile(ctok.yes, next, reverse);
                Op no = ctok.no == null ? null : this.compile(ctok.no, next, reverse);
                ret = Op.createCondition(next, ref, condition, yes, no);
                break;
            }
            default: {
                throw new RuntimeException("Unknown token type: " + tok.type);
            }
        }
        return ret;
    }

    public boolean matches(char[] target) {
        return this.matches(target, 0, target.length, (Match)null);
    }

    public boolean matches(char[] target, int start, int end) {
        return this.matches(target, start, end, (Match)null);
    }

    public boolean matches(char[] target, Match match) {
        return this.matches(target, 0, target.length, match);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean matches(char[] target, int start, int end, Match match) {
        int matchStart;
        int o;
        RegularExpression regularExpression = this;
        synchronized (regularExpression) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        Context con = null;
        Context context = this.context;
        synchronized (context) {
            con = this.context.inuse ? new Context() : this.context;
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        con.match = match;
        if (RegularExpression.isSet(this.options, 512)) {
            int matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.setInUse(false);
                return true;
            }
            return false;
        }
        if (this.fixedStringOnly) {
            int o2 = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o2 >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o2);
                    con.match.setEnd(0, o2 + this.fixedString.length());
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }
        if (this.fixedString != null && (o = this.fixedStringTable.matches(target, con.start, con.limit)) < 0) {
            con.setInUse(false);
            return false;
        }
        int limit = con.limit - this.minlength;
        int matchEnd = -1;
        if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (RegularExpression.isSet(this.options, 4)) {
                matchStart = con.start;
                matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            } else {
                boolean previousIsEOL = true;
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    char ch = target[matchStart];
                    if (RegularExpression.isEOLChar(ch)) {
                        previousIsEOL = true;
                        continue;
                    }
                    if (!previousIsEOL || 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                        previousIsEOL = false;
                        continue;
                    }
                    break;
                }
            }
        } else if (this.firstChar != null) {
            RangeToken range = this.firstChar;
            for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                int ch = target[matchStart];
                if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                    ch = REUtil.composeFromSurrogates(ch, target[matchStart + 1]);
                }
                if (!range.match(ch) || 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                    continue;
                }
                break;
            }
        } else {
            for (matchStart = con.start; matchStart <= limit && 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options)); ++matchStart) {
            }
        }
        if (matchEnd >= 0) {
            if (con.match != null) {
                con.match.setBeginning(0, matchStart);
                con.match.setEnd(0, matchEnd);
            }
            con.setInUse(false);
            return true;
        }
        con.setInUse(false);
        return false;
    }

    public boolean matches(String target) {
        return this.matches(target, 0, target.length(), (Match)null);
    }

    public boolean matches(String target, int start, int end) {
        return this.matches(target, start, end, (Match)null);
    }

    public boolean matches(String target, Match match) {
        return this.matches(target, 0, target.length(), match);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean matches(String target, int start, int end, Match match) {
        int matchStart;
        int o;
        RegularExpression regularExpression = this;
        synchronized (regularExpression) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        Context con = null;
        Context context = this.context;
        synchronized (context) {
            con = this.context.inuse ? new Context() : this.context;
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        con.match = match;
        if (RegularExpression.isSet(this.options, 512)) {
            int matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.setInUse(false);
                return true;
            }
            return false;
        }
        if (this.fixedStringOnly) {
            int o2 = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o2 >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o2);
                    con.match.setEnd(0, o2 + this.fixedString.length());
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }
        if (this.fixedString != null && (o = this.fixedStringTable.matches(target, con.start, con.limit)) < 0) {
            con.setInUse(false);
            return false;
        }
        int limit = con.limit - this.minlength;
        int matchEnd = -1;
        if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (RegularExpression.isSet(this.options, 4)) {
                matchStart = con.start;
                matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            } else {
                boolean previousIsEOL = true;
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    char ch = target.charAt(matchStart);
                    if (RegularExpression.isEOLChar(ch)) {
                        previousIsEOL = true;
                        continue;
                    }
                    if (!previousIsEOL || 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                        previousIsEOL = false;
                        continue;
                    }
                    break;
                }
            }
        } else if (this.firstChar != null) {
            RangeToken range = this.firstChar;
            for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                int ch = target.charAt(matchStart);
                if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                    ch = REUtil.composeFromSurrogates(ch, target.charAt(matchStart + 1));
                }
                if (!range.match(ch) || 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                    continue;
                }
                break;
            }
        } else {
            for (matchStart = con.start; matchStart <= limit && 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options)); ++matchStart) {
            }
        }
        if (matchEnd >= 0) {
            if (con.match != null) {
                con.match.setBeginning(0, matchStart);
                con.match.setEnd(0, matchEnd);
            }
            con.setInUse(false);
            return true;
        }
        con.setInUse(false);
        return false;
    }

    private int match(Context con, Op op, int offset, int dx, int opts) {
        ExpressionTarget target = con.target;
        Stack<Op> opStack = new Stack<Op>();
        IntStack dataStack = new IntStack();
        boolean isSetIgnoreCase = RegularExpression.isSet(opts, 2);
        int retValue = -1;
        boolean returned = false;
        block28: while (true) {
            if (op == null || offset > con.limit || offset < con.start) {
                retValue = op == null ? (RegularExpression.isSet(opts, 512) && offset != con.limit ? -1 : offset) : -1;
                returned = true;
            } else {
                retValue = -1;
                switch (op.type) {
                    case 1: {
                        int o1;
                        int n = o1 = dx > 0 ? offset : offset - 1;
                        if (o1 >= con.limit || o1 < 0 || !this.matchChar(op.getData(), target.charAt(o1), isSetIgnoreCase)) {
                            returned = true;
                            break;
                        }
                        offset += dx;
                        op = op.next;
                        break;
                    }
                    case 0: {
                        int ch;
                        int o1;
                        int n = o1 = dx > 0 ? offset : offset - 1;
                        if (o1 >= con.limit || o1 < 0) {
                            returned = true;
                            break;
                        }
                        if (RegularExpression.isSet(opts, 4)) {
                            if (REUtil.isHighSurrogate(target.charAt(o1)) && o1 + dx >= 0 && o1 + dx < con.limit) {
                                o1 += dx;
                            }
                        } else {
                            ch = target.charAt(o1);
                            if (REUtil.isHighSurrogate(ch) && o1 + dx >= 0 && o1 + dx < con.limit) {
                                ch = REUtil.composeFromSurrogates(ch, target.charAt(o1 += dx));
                            }
                            if (RegularExpression.isEOLChar(ch)) {
                                returned = true;
                                break;
                            }
                        }
                        offset = dx > 0 ? o1 + 1 : o1;
                        op = op.next;
                        break;
                    }
                    case 3: 
                    case 4: {
                        RangeToken tok;
                        int o1;
                        int n = o1 = dx > 0 ? offset : offset - 1;
                        if (o1 >= con.limit || o1 < 0) {
                            returned = true;
                            break;
                        }
                        int ch = target.charAt(offset);
                        if (REUtil.isHighSurrogate(ch) && o1 + dx < con.limit && o1 + dx >= 0) {
                            ch = REUtil.composeFromSurrogates(ch, target.charAt(o1 += dx));
                        }
                        if (!(tok = op.getToken()).match(ch)) {
                            returned = true;
                            break;
                        }
                        offset = dx > 0 ? o1 + 1 : o1;
                        op = op.next;
                        break;
                    }
                    case 5: {
                        if (!this.matchAnchor(target, op, con, offset, opts)) {
                            returned = true;
                            break;
                        }
                        op = op.next;
                        break;
                    }
                    case 16: {
                        int refno = op.getData();
                        if (refno <= 0 || refno >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + refno);
                        }
                        if (con.match == null) break;
                        if (con.match.getBeginning(refno) < 0 || con.match.getEnd(refno) < 0) {
                            returned = true;
                            break;
                        }
                        int o2 = con.match.getBeginning(refno);
                        int literallen = con.match.getEnd(refno) - o2;
                        if (dx > 0) {
                            if (!target.regionMatches(isSetIgnoreCase, offset, con.limit, o2, literallen)) {
                                returned = true;
                                break;
                            }
                            offset += literallen;
                        } else {
                            if (!target.regionMatches(isSetIgnoreCase, offset - literallen, con.limit, o2, literallen)) {
                                returned = true;
                                break;
                            }
                            offset -= literallen;
                        }
                        op = op.next;
                        break;
                    }
                    case 6: {
                        String literal = op.getString();
                        int literallen = literal.length();
                        if (dx > 0) {
                            if (!target.regionMatches(isSetIgnoreCase, offset, con.limit, literal, literallen)) {
                                returned = true;
                                break;
                            }
                            offset += literallen;
                        } else {
                            if (!target.regionMatches(isSetIgnoreCase, offset - literallen, con.limit, literal, literallen)) {
                                returned = true;
                                break;
                            }
                            offset -= literallen;
                        }
                        op = op.next;
                        break;
                    }
                    case 7: {
                        int id = op.getData();
                        if (con.closureContexts[id].contains(offset)) {
                            returned = true;
                            break;
                        }
                        con.closureContexts[id].addOffset(offset);
                    }
                    case 9: {
                        opStack.push(op);
                        dataStack.push(offset);
                        op = op.getChild();
                        break;
                    }
                    case 8: 
                    case 10: {
                        opStack.push(op);
                        dataStack.push(offset);
                        op = op.next;
                        break;
                    }
                    case 11: {
                        if (op.size() == 0) {
                            returned = true;
                            break;
                        }
                        opStack.push(op);
                        dataStack.push(0);
                        dataStack.push(offset);
                        op = op.elementAt(0);
                        break;
                    }
                    case 15: {
                        int refno = op.getData();
                        if (con.match != null) {
                            if (refno > 0) {
                                dataStack.push(con.match.getBeginning(refno));
                                con.match.setBeginning(refno, offset);
                            } else {
                                int index = -refno;
                                dataStack.push(con.match.getEnd(index));
                                con.match.setEnd(index, offset);
                            }
                            opStack.push(op);
                            dataStack.push(offset);
                        }
                        op = op.next;
                        break;
                    }
                    case 20: 
                    case 21: 
                    case 22: 
                    case 23: {
                        opStack.push(op);
                        dataStack.push(dx);
                        dataStack.push(offset);
                        dx = op.type == 20 || op.type == 21 ? 1 : -1;
                        op = op.getChild();
                        break;
                    }
                    case 24: {
                        opStack.push(op);
                        dataStack.push(offset);
                        op = op.getChild();
                        break;
                    }
                    case 25: {
                        int localopts = opts;
                        localopts |= op.getData();
                        opStack.push(op);
                        dataStack.push(opts);
                        dataStack.push(offset);
                        opts = localopts &= ~op.getData2();
                        op = op.getChild();
                        break;
                    }
                    case 26: {
                        Op.ConditionOp cop = (Op.ConditionOp)op;
                        if (cop.refNumber > 0) {
                            if (cop.refNumber >= this.nofparen) {
                                throw new RuntimeException("Internal Error: Reference number must be more than zero: " + cop.refNumber);
                            }
                            if (con.match != null && con.match.getBeginning(cop.refNumber) >= 0 && con.match.getEnd(cop.refNumber) >= 0) {
                                op = cop.yes;
                                break;
                            }
                            if (cop.no != null) {
                                op = cop.no;
                                break;
                            }
                            op = cop.next;
                            break;
                        }
                        opStack.push(op);
                        dataStack.push(offset);
                        op = cop.condition;
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unknown operation type: " + op.type);
                    }
                }
            }
            block29: while (true) {
                if (!returned) continue block28;
                if (opStack.isEmpty()) {
                    return retValue;
                }
                op = (Op)opStack.pop();
                offset = dataStack.pop();
                switch (op.type) {
                    case 7: 
                    case 9: {
                        if (retValue >= 0) continue block29;
                        op = op.next;
                        returned = false;
                        continue block29;
                    }
                    case 8: 
                    case 10: {
                        if (retValue >= 0) continue block29;
                        op = op.getChild();
                        returned = false;
                        continue block29;
                    }
                    case 11: {
                        int unionIndex = dataStack.pop();
                        if (retValue >= 0) continue block29;
                        if (++unionIndex < op.size()) {
                            opStack.push(op);
                            dataStack.push(unionIndex);
                            dataStack.push(offset);
                            op = op.elementAt(unionIndex);
                            returned = false;
                            continue block29;
                        }
                        retValue = -1;
                        continue block29;
                    }
                    case 15: {
                        int refno = op.getData();
                        int saved = dataStack.pop();
                        if (con.match == null || retValue >= 0) continue block29;
                        if (refno > 0) {
                            con.match.setBeginning(refno, saved);
                            continue block29;
                        }
                        con.match.setEnd(-refno, saved);
                        continue block29;
                    }
                    case 20: 
                    case 22: {
                        dx = dataStack.pop();
                        if (0 <= retValue) {
                            op = op.next;
                            returned = false;
                        }
                        retValue = -1;
                        continue block29;
                    }
                    case 21: 
                    case 23: {
                        dx = dataStack.pop();
                        if (0 > retValue) {
                            op = op.next;
                            returned = false;
                        }
                        retValue = -1;
                        continue block29;
                    }
                    case 25: {
                        opts = dataStack.pop();
                    }
                    case 24: {
                        if (retValue < 0) continue block29;
                        offset = retValue;
                        op = op.next;
                        returned = false;
                        continue block29;
                    }
                    case 26: {
                        Op.ConditionOp cop = (Op.ConditionOp)op;
                        op = 0 <= retValue ? cop.yes : (cop.no != null ? cop.no : cop.next);
                        returned = false;
                        continue block29;
                    }
                }
            }
            break;
        }
    }

    private boolean matchChar(int ch, int other, boolean ignoreCase) {
        return ignoreCase ? RegularExpression.matchIgnoreCase(ch, other) : ch == other;
    }

    boolean matchAnchor(ExpressionTarget target, Op op, Context con, int offset, int opts) {
        boolean go = false;
        switch (op.getData()) {
            case 94: {
                if (!(RegularExpression.isSet(opts, 8) ? offset != con.start && (offset <= con.start || offset >= con.limit || !RegularExpression.isEOLChar(target.charAt(offset - 1))) : offset != con.start)) break;
                return false;
            }
            case 64: {
                if (offset == con.start || offset > con.start && RegularExpression.isEOLChar(target.charAt(offset - 1))) break;
                return false;
            }
            case 36: {
                if (!(RegularExpression.isSet(opts, 8) ? offset != con.limit && (offset >= con.limit || !RegularExpression.isEOLChar(target.charAt(offset))) : !(offset == con.limit || offset + 1 == con.limit && RegularExpression.isEOLChar(target.charAt(offset)) || offset + 2 == con.limit && target.charAt(offset) == '\r' && target.charAt(offset + 1) == '\n'))) break;
                return false;
            }
            case 65: {
                if (offset == con.start) break;
                return false;
            }
            case 90: {
                if (offset == con.limit || offset + 1 == con.limit && RegularExpression.isEOLChar(target.charAt(offset)) || offset + 2 == con.limit && target.charAt(offset) == '\r' && target.charAt(offset + 1) == '\n') break;
                return false;
            }
            case 122: {
                if (offset == con.limit) break;
                return false;
            }
            case 98: {
                if (con.length == 0) {
                    return false;
                }
                int after = RegularExpression.getWordType(target, con.start, con.limit, offset, opts);
                if (after == 0) {
                    return false;
                }
                int before = RegularExpression.getPreviousWordType(target, con.start, con.limit, offset, opts);
                if (after != before) break;
                return false;
            }
            case 66: {
                if (con.length == 0) {
                    go = true;
                } else {
                    int after = RegularExpression.getWordType(target, con.start, con.limit, offset, opts);
                    boolean bl = go = after == 0 || after == RegularExpression.getPreviousWordType(target, con.start, con.limit, offset, opts);
                }
                if (go) break;
                return false;
            }
            case 60: {
                if (con.length == 0 || offset == con.limit) {
                    return false;
                }
                if (RegularExpression.getWordType(target, con.start, con.limit, offset, opts) == 1 && RegularExpression.getPreviousWordType(target, con.start, con.limit, offset, opts) == 2) break;
                return false;
            }
            case 62: {
                if (con.length == 0 || offset == con.start) {
                    return false;
                }
                if (RegularExpression.getWordType(target, con.start, con.limit, offset, opts) == 2 && RegularExpression.getPreviousWordType(target, con.start, con.limit, offset, opts) == 1) break;
                return false;
            }
        }
        return true;
    }

    private static final int getPreviousWordType(ExpressionTarget target, int begin, int end, int offset, int opts) {
        int ret = RegularExpression.getWordType(target, begin, end, --offset, opts);
        while (ret == 0) {
            ret = RegularExpression.getWordType(target, begin, end, --offset, opts);
        }
        return ret;
    }

    private static final int getWordType(ExpressionTarget target, int begin, int end, int offset, int opts) {
        if (offset < begin || offset >= end) {
            return 2;
        }
        return RegularExpression.getWordType0(target.charAt(offset), opts);
    }

    public boolean matches(CharacterIterator target) {
        return this.matches(target, (Match)null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean matches(CharacterIterator target, Match match) {
        int matchStart;
        int o;
        int start = target.getBeginIndex();
        int end = target.getEndIndex();
        RegularExpression regularExpression = this;
        synchronized (regularExpression) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        Context con = null;
        Context context = this.context;
        synchronized (context) {
            con = this.context.inuse ? new Context() : this.context;
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        con.match = match;
        if (RegularExpression.isSet(this.options, 512)) {
            int matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.setInUse(false);
                return true;
            }
            return false;
        }
        if (this.fixedStringOnly) {
            int o2 = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o2 >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o2);
                    con.match.setEnd(0, o2 + this.fixedString.length());
                }
                con.setInUse(false);
                return true;
            }
            con.setInUse(false);
            return false;
        }
        if (this.fixedString != null && (o = this.fixedStringTable.matches(target, con.start, con.limit)) < 0) {
            con.setInUse(false);
            return false;
        }
        int limit = con.limit - this.minlength;
        int matchEnd = -1;
        if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (RegularExpression.isSet(this.options, 4)) {
                matchStart = con.start;
                matchEnd = this.match(con, this.operations, con.start, 1, this.options);
            } else {
                boolean previousIsEOL = true;
                for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                    char ch = target.setIndex(matchStart);
                    if (RegularExpression.isEOLChar(ch)) {
                        previousIsEOL = true;
                        continue;
                    }
                    if (!previousIsEOL || 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                        previousIsEOL = false;
                        continue;
                    }
                    break;
                }
            }
        } else if (this.firstChar != null) {
            RangeToken range = this.firstChar;
            for (matchStart = con.start; matchStart <= limit; ++matchStart) {
                int ch = target.setIndex(matchStart);
                if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                    ch = REUtil.composeFromSurrogates(ch, target.setIndex(matchStart + 1));
                }
                if (!range.match(ch) || 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options))) {
                    continue;
                }
                break;
            }
        } else {
            for (matchStart = con.start; matchStart <= limit && 0 > (matchEnd = this.match(con, this.operations, matchStart, 1, this.options)); ++matchStart) {
            }
        }
        if (matchEnd >= 0) {
            if (con.match != null) {
                con.match.setBeginning(0, matchStart);
                con.match.setEnd(0, matchEnd);
            }
            con.setInUse(false);
            return true;
        }
        con.setInUse(false);
        return false;
    }

    void prepare() {
        RangeToken firstChar;
        int fresult;
        this.compile(this.tokentree);
        this.minlength = this.tokentree.getMinLength();
        this.firstChar = null;
        if (!RegularExpression.isSet(this.options, 128) && !RegularExpression.isSet(this.options, 512) && (fresult = this.tokentree.analyzeFirstCharacter(firstChar = Token.createRange(), this.options)) == 1) {
            firstChar.compactRanges();
            this.firstChar = firstChar;
        }
        if (this.operations != null && (this.operations.type == 6 || this.operations.type == 1) && this.operations.next == null) {
            this.fixedStringOnly = true;
            if (this.operations.type == 6) {
                this.fixedString = this.operations.getString();
            } else if (this.operations.getData() >= 65536) {
                this.fixedString = REUtil.decomposeToSurrogates(this.operations.getData());
            } else {
                char[] ac = new char[]{(char)this.operations.getData()};
                this.fixedString = new String(ac);
            }
            this.fixedStringOptions = this.options;
            this.fixedStringTable = new BMPattern(this.fixedString, 256, RegularExpression.isSet(this.fixedStringOptions, 2));
        } else if (!RegularExpression.isSet(this.options, 256) && !RegularExpression.isSet(this.options, 512)) {
            Token.FixedStringContainer container = new Token.FixedStringContainer();
            this.tokentree.findFixedString(container, this.options);
            this.fixedString = container.token == null ? null : container.token.getString();
            this.fixedStringOptions = container.options;
            if (this.fixedString != null && this.fixedString.length() < 2) {
                this.fixedString = null;
            }
            if (this.fixedString != null) {
                this.fixedStringTable = new BMPattern(this.fixedString, 256, RegularExpression.isSet(this.fixedStringOptions, 2));
            }
        }
    }

    private static boolean isSet(int options, int flag) {
        return (options & flag) == flag;
    }

    public RegularExpression(String regex) throws ParseException {
        this(regex, null);
    }

    public RegularExpression(String regex, String options) throws ParseException {
        this.setPattern(regex, options);
    }

    public RegularExpression(String regex, String options, Locale locale) throws ParseException {
        this.setPattern(regex, options, locale);
    }

    RegularExpression(String regex, Token tok, int parens, boolean hasBackReferences, int options) {
        this.regex = regex;
        this.tokentree = tok;
        this.nofparen = parens;
        this.options = options;
        this.hasBackReferences = hasBackReferences;
    }

    public void setPattern(String newPattern) throws ParseException {
        this.setPattern(newPattern, Locale.getDefault());
    }

    public void setPattern(String newPattern, Locale locale) throws ParseException {
        this.setPattern(newPattern, this.options, locale);
    }

    private void setPattern(String newPattern, int options, Locale locale) throws ParseException {
        this.regex = newPattern;
        this.options = options;
        RegexParser rp = RegularExpression.isSet(this.options, 512) ? new ParserForXMLSchema(locale) : new RegexParser(locale);
        this.tokentree = rp.parse(this.regex, this.options);
        this.nofparen = rp.parennumber;
        this.hasBackReferences = rp.hasBackReferences;
        this.operations = null;
        this.context = null;
    }

    public void setPattern(String newPattern, String options) throws ParseException {
        this.setPattern(newPattern, options, Locale.getDefault());
    }

    public void setPattern(String newPattern, String options, Locale locale) throws ParseException {
        this.setPattern(newPattern, REUtil.parseOptions(options), locale);
    }

    public String getPattern() {
        return this.regex;
    }

    public String toString() {
        return this.tokentree.toString(this.options);
    }

    public String getOptions() {
        return REUtil.createOptionString(this.options);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RegularExpression)) {
            return false;
        }
        RegularExpression r = (RegularExpression)obj;
        return this.regex.equals(r.regex) && this.options == r.options;
    }

    boolean equals(String pattern, int options) {
        return this.regex.equals(pattern) && this.options == options;
    }

    public int hashCode() {
        return (this.regex + "/" + this.getOptions()).hashCode();
    }

    public int getNumberOfGroups() {
        return this.nofparen;
    }

    private static int getWordType0(char ch, int opts) {
        if (!RegularExpression.isSet(opts, 64)) {
            if (RegularExpression.isSet(opts, 32)) {
                return Token.getRange("IsWord", true).match(ch) ? 1 : 2;
            }
            return RegularExpression.isWordChar(ch) ? 1 : 2;
        }
        switch (Character.getType(ch)) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                return 1;
            }
            case 6: 
            case 7: 
            case 16: {
                return 0;
            }
            case 15: {
                switch (ch) {
                    case '\t': 
                    case '\n': 
                    case '\u000b': 
                    case '\f': 
                    case '\r': {
                        return 2;
                    }
                }
                return 0;
            }
        }
        return 2;
    }

    private static boolean isEOLChar(int ch) {
        return ch == 10 || ch == 13 || ch == 8232 || ch == 8233;
    }

    private static boolean isWordChar(int ch) {
        if (ch == 95) {
            return true;
        }
        if (ch < 48) {
            return false;
        }
        if (ch > 122) {
            return false;
        }
        if (ch <= 57) {
            return true;
        }
        if (ch < 65) {
            return false;
        }
        if (ch <= 90) {
            return true;
        }
        return ch >= 97;
    }

    private static boolean matchIgnoreCase(int chardata, int ch) {
        char uch2;
        if (chardata == ch) {
            return true;
        }
        if (chardata > 65535 || ch > 65535) {
            return false;
        }
        char uch1 = Character.toUpperCase((char)chardata);
        if (uch1 == (uch2 = Character.toUpperCase((char)ch))) {
            return true;
        }
        return Character.toLowerCase(uch1) == Character.toLowerCase(uch2);
    }

    static final class Context {
        int start;
        int limit;
        int length;
        Match match;
        boolean inuse = false;
        ClosureContext[] closureContexts;
        private StringTarget stringTarget;
        private CharArrayTarget charArrayTarget;
        private CharacterIteratorTarget characterIteratorTarget;
        ExpressionTarget target;

        Context() {
        }

        private void resetCommon(int nofclosures) {
            this.length = this.limit - this.start;
            this.setInUse(true);
            this.match = null;
            if (this.closureContexts == null || this.closureContexts.length != nofclosures) {
                this.closureContexts = new ClosureContext[nofclosures];
            }
            for (int i = 0; i < nofclosures; ++i) {
                if (this.closureContexts[i] == null) {
                    this.closureContexts[i] = new ClosureContext();
                    continue;
                }
                this.closureContexts[i].reset();
            }
        }

        void reset(CharacterIterator target, int start, int limit, int nofclosures) {
            if (this.characterIteratorTarget == null) {
                this.characterIteratorTarget = new CharacterIteratorTarget(target);
            } else {
                this.characterIteratorTarget.resetTarget(target);
            }
            this.target = this.characterIteratorTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }

        void reset(String target, int start, int limit, int nofclosures) {
            if (this.stringTarget == null) {
                this.stringTarget = new StringTarget(target);
            } else {
                this.stringTarget.resetTarget(target);
            }
            this.target = this.stringTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }

        void reset(char[] target, int start, int limit, int nofclosures) {
            if (this.charArrayTarget == null) {
                this.charArrayTarget = new CharArrayTarget(target);
            } else {
                this.charArrayTarget.resetTarget(target);
            }
            this.target = this.charArrayTarget;
            this.start = start;
            this.limit = limit;
            this.resetCommon(nofclosures);
        }

        synchronized void setInUse(boolean inUse) {
            this.inuse = inUse;
        }
    }

    static final class ClosureContext {
        int[] offsets = new int[4];
        int currentIndex = 0;

        ClosureContext() {
        }

        boolean contains(int offset) {
            for (int i = 0; i < this.currentIndex; ++i) {
                if (this.offsets[i] != offset) continue;
                return true;
            }
            return false;
        }

        void reset() {
            this.currentIndex = 0;
        }

        void addOffset(int offset) {
            if (this.currentIndex == this.offsets.length) {
                this.offsets = this.expandOffsets();
            }
            this.offsets[this.currentIndex++] = offset;
        }

        private int[] expandOffsets() {
            int len = this.offsets.length;
            int newLen = len << 1;
            int[] newOffsets = new int[newLen];
            System.arraycopy(this.offsets, 0, newOffsets, 0, this.currentIndex);
            return newOffsets;
        }
    }

    static final class CharacterIteratorTarget
    extends ExpressionTarget {
        CharacterIterator target;

        CharacterIteratorTarget(CharacterIterator target) {
            this.target = target;
        }

        void resetTarget(CharacterIterator target) {
            this.target = target;
        }

        @Override
        char charAt(int index) {
            return this.target.setIndex(index);
        }

        @Override
        boolean regionMatches(boolean ignoreCase, int offset, int limit, String part, int partlen) {
            if (offset < 0 || limit - offset < partlen) {
                return false;
            }
            return ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, part, partlen) : this.regionMatches(offset, limit, part, partlen);
        }

        private boolean regionMatches(int offset, int limit, String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                if (this.target.setIndex(offset++) == part.charAt(i++)) continue;
                return false;
            }
            return true;
        }

        private boolean regionMatchesIgnoreCase(int offset, int limit, String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                char uch2;
                char uch1;
                char ch2;
                char ch1;
                if ((ch1 = this.target.setIndex(offset++)) == (ch2 = part.charAt(i++)) || (uch1 = Character.toUpperCase(ch1)) == (uch2 = Character.toUpperCase(ch2)) || Character.toLowerCase(uch1) == Character.toLowerCase(uch2)) continue;
                return false;
            }
            return true;
        }

        @Override
        boolean regionMatches(boolean ignoreCase, int offset, int limit, int offset2, int partlen) {
            if (offset < 0 || limit - offset < partlen) {
                return false;
            }
            return ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, offset2, partlen) : this.regionMatches(offset, limit, offset2, partlen);
        }

        private boolean regionMatches(int offset, int limit, int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                if (this.target.setIndex(offset++) == this.target.setIndex(i++)) continue;
                return false;
            }
            return true;
        }

        private boolean regionMatchesIgnoreCase(int offset, int limit, int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                char uch2;
                char uch1;
                char ch2;
                char ch1;
                if ((ch1 = this.target.setIndex(offset++)) == (ch2 = this.target.setIndex(i++)) || (uch1 = Character.toUpperCase(ch1)) == (uch2 = Character.toUpperCase(ch2)) || Character.toLowerCase(uch1) == Character.toLowerCase(uch2)) continue;
                return false;
            }
            return true;
        }
    }

    static final class CharArrayTarget
    extends ExpressionTarget {
        char[] target;

        CharArrayTarget(char[] target) {
            this.target = target;
        }

        void resetTarget(char[] target) {
            this.target = target;
        }

        @Override
        char charAt(int index) {
            return this.target[index];
        }

        @Override
        boolean regionMatches(boolean ignoreCase, int offset, int limit, String part, int partlen) {
            if (offset < 0 || limit - offset < partlen) {
                return false;
            }
            return ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, part, partlen) : this.regionMatches(offset, limit, part, partlen);
        }

        private boolean regionMatches(int offset, int limit, String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                if (this.target[offset++] == part.charAt(i++)) continue;
                return false;
            }
            return true;
        }

        private boolean regionMatchesIgnoreCase(int offset, int limit, String part, int partlen) {
            int i = 0;
            while (partlen-- > 0) {
                char uch2;
                char uch1;
                char ch2;
                char ch1;
                if ((ch1 = this.target[offset++]) == (ch2 = part.charAt(i++)) || (uch1 = Character.toUpperCase(ch1)) == (uch2 = Character.toUpperCase(ch2)) || Character.toLowerCase(uch1) == Character.toLowerCase(uch2)) continue;
                return false;
            }
            return true;
        }

        @Override
        boolean regionMatches(boolean ignoreCase, int offset, int limit, int offset2, int partlen) {
            if (offset < 0 || limit - offset < partlen) {
                return false;
            }
            return ignoreCase ? this.regionMatchesIgnoreCase(offset, limit, offset2, partlen) : this.regionMatches(offset, limit, offset2, partlen);
        }

        private boolean regionMatches(int offset, int limit, int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                if (this.target[offset++] == this.target[i++]) continue;
                return false;
            }
            return true;
        }

        private boolean regionMatchesIgnoreCase(int offset, int limit, int offset2, int partlen) {
            int i = offset2;
            while (partlen-- > 0) {
                char uch2;
                char uch1;
                char ch2;
                char ch1;
                if ((ch1 = this.target[offset++]) == (ch2 = this.target[i++]) || (uch1 = Character.toUpperCase(ch1)) == (uch2 = Character.toUpperCase(ch2)) || Character.toLowerCase(uch1) == Character.toLowerCase(uch2)) continue;
                return false;
            }
            return true;
        }
    }

    static final class StringTarget
    extends ExpressionTarget {
        private String target;

        StringTarget(String target) {
            this.target = target;
        }

        void resetTarget(String target) {
            this.target = target;
        }

        @Override
        char charAt(int index) {
            return this.target.charAt(index);
        }

        @Override
        boolean regionMatches(boolean ignoreCase, int offset, int limit, String part, int partlen) {
            if (limit - offset < partlen) {
                return false;
            }
            return ignoreCase ? this.target.regionMatches(true, offset, part, 0, partlen) : this.target.regionMatches(offset, part, 0, partlen);
        }

        @Override
        boolean regionMatches(boolean ignoreCase, int offset, int limit, int offset2, int partlen) {
            if (limit - offset < partlen) {
                return false;
            }
            return ignoreCase ? this.target.regionMatches(true, offset, this.target, offset2, partlen) : this.target.regionMatches(offset, this.target, offset2, partlen);
        }
    }

    static abstract class ExpressionTarget {
        ExpressionTarget() {
        }

        abstract char charAt(int var1);

        abstract boolean regionMatches(boolean var1, int var2, int var3, String var4, int var5);

        abstract boolean regionMatches(boolean var1, int var2, int var3, int var4, int var5);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xpath.regex;

import java.io.Serializable;
import java.text.CharacterIterator;
import java.util.Locale;
import java.util.Stack;
import org.apache.xerces.impl.xpath.regex.BMPattern;
import org.apache.xerces.impl.xpath.regex.Match;
import org.apache.xerces.impl.xpath.regex.Op;
import org.apache.xerces.impl.xpath.regex.ParseException;
import org.apache.xerces.impl.xpath.regex.ParserForXMLSchema;
import org.apache.xerces.impl.xpath.regex.REUtil;
import org.apache.xerces.impl.xpath.regex.RangeToken;
import org.apache.xerces.impl.xpath.regex.RegexParser;
import org.apache.xerces.impl.xpath.regex.Token;
import org.apache.xerces.util.IntStack;

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

    private synchronized void compile(Token token) {
        if (this.operations != null) {
            return;
        }
        this.numberOfClosures = 0;
        this.operations = this.compile(token, null, false);
    }

    private Op compile(Token token, Op op, boolean bl) {
        Op op2;
        switch (token.type) {
            case 11: {
                op2 = Op.createDot();
                op2.next = op;
                break;
            }
            case 0: {
                op2 = Op.createChar(token.getChar());
                op2.next = op;
                break;
            }
            case 8: {
                op2 = Op.createAnchor(token.getChar());
                op2.next = op;
                break;
            }
            case 4: 
            case 5: {
                op2 = Op.createRange(token);
                op2.next = op;
                break;
            }
            case 1: {
                op2 = op;
                if (!bl) {
                    for (int i = token.size() - 1; i >= 0; --i) {
                        op2 = this.compile(token.getChild(i), op2, false);
                    }
                } else {
                    for (int i = 0; i < token.size(); ++i) {
                        op2 = this.compile(token.getChild(i), op2, true);
                    }
                }
                break;
            }
            case 2: {
                Op.UnionOp unionOp = Op.createUnion(token.size());
                for (int i = 0; i < token.size(); ++i) {
                    unionOp.addElement(this.compile(token.getChild(i), op, bl));
                }
                op2 = unionOp;
                break;
            }
            case 3: 
            case 9: {
                Token token2 = token.getChild(0);
                int n = token.getMin();
                int n2 = token.getMax();
                if (n >= 0 && n == n2) {
                    op2 = op;
                    for (int i = 0; i < n; ++i) {
                        op2 = this.compile(token2, op2, bl);
                    }
                } else {
                    if (n > 0 && n2 > 0) {
                        n2 -= n;
                    }
                    if (n2 > 0) {
                        op2 = op;
                        for (int i = 0; i < n2; ++i) {
                            Op.ChildOp childOp = Op.createQuestion(token.type == 9);
                            childOp.next = op;
                            childOp.setChild(this.compile(token2, op2, bl));
                            op2 = childOp;
                        }
                    } else {
                        Op.ChildOp childOp = token.type == 9 ? Op.createNonGreedyClosure() : Op.createClosure(this.numberOfClosures++);
                        childOp.next = op;
                        childOp.setChild(this.compile(token2, childOp, bl));
                        op2 = childOp;
                    }
                    if (n <= 0) break;
                    for (int i = 0; i < n; ++i) {
                        op2 = this.compile(token2, op2, bl);
                    }
                }
                break;
            }
            case 7: {
                op2 = op;
                break;
            }
            case 10: {
                op2 = Op.createString(token.getString());
                op2.next = op;
                break;
            }
            case 12: {
                op2 = Op.createBackReference(token.getReferenceNumber());
                op2.next = op;
                break;
            }
            case 6: {
                if (token.getParenNumber() == 0) {
                    op2 = this.compile(token.getChild(0), op, bl);
                    break;
                }
                if (bl) {
                    op = Op.createCapture(token.getParenNumber(), op);
                    op = this.compile(token.getChild(0), op, bl);
                    op2 = Op.createCapture(-token.getParenNumber(), op);
                    break;
                }
                op = Op.createCapture(-token.getParenNumber(), op);
                op = this.compile(token.getChild(0), op, bl);
                op2 = Op.createCapture(token.getParenNumber(), op);
                break;
            }
            case 20: {
                op2 = Op.createLook(20, op, this.compile(token.getChild(0), null, false));
                break;
            }
            case 21: {
                op2 = Op.createLook(21, op, this.compile(token.getChild(0), null, false));
                break;
            }
            case 22: {
                op2 = Op.createLook(22, op, this.compile(token.getChild(0), null, true));
                break;
            }
            case 23: {
                op2 = Op.createLook(23, op, this.compile(token.getChild(0), null, true));
                break;
            }
            case 24: {
                op2 = Op.createIndependent(op, this.compile(token.getChild(0), null, bl));
                break;
            }
            case 25: {
                op2 = Op.createModifier(op, this.compile(token.getChild(0), null, bl), ((Token.ModifierToken)token).getOptions(), ((Token.ModifierToken)token).getOptionsMask());
                break;
            }
            case 26: {
                Token.ConditionToken conditionToken = (Token.ConditionToken)token;
                int n = conditionToken.refNumber;
                Op op3 = conditionToken.condition == null ? null : this.compile(conditionToken.condition, null, bl);
                Op op4 = this.compile(conditionToken.yes, op, bl);
                Op op5 = conditionToken.no == null ? null : this.compile(conditionToken.no, op, bl);
                op2 = Op.createCondition(op, n, op3, op4, op5);
                break;
            }
            default: {
                throw new RuntimeException("Unknown token type: " + token.type);
            }
        }
        return op2;
    }

    public boolean matches(char[] cArray) {
        return this.matches(cArray, 0, cArray.length, (Match)null);
    }

    public boolean matches(char[] cArray, int n, int n2) {
        return this.matches(cArray, n, n2, (Match)null);
    }

    public boolean matches(char[] cArray, Match match) {
        return this.matches(cArray, 0, cArray.length, match);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean matches(char[] cArray, int n, int n2, Match match) {
        int n3;
        int n4;
        Object object = this;
        synchronized (object) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        object = null;
        Context context = this.context;
        synchronized (context) {
            object = this.context.inuse ? new Context() : this.context;
            ((Context)object).reset(cArray, n, n2, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(cArray);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        ((Context)object).match = match;
        if (RegularExpression.isSet(this.options, 512)) {
            int n5 = this.match((Context)object, this.operations, ((Context)object).start, 1, this.options);
            if (n5 == ((Context)object).limit) {
                if (((Context)object).match != null) {
                    ((Context)object).match.setBeginning(0, ((Context)object).start);
                    ((Context)object).match.setEnd(0, n5);
                }
                ((Context)object).setInUse(false);
                return true;
            }
            return false;
        }
        if (this.fixedStringOnly) {
            int n6 = this.fixedStringTable.matches(cArray, ((Context)object).start, ((Context)object).limit);
            if (n6 >= 0) {
                if (((Context)object).match != null) {
                    ((Context)object).match.setBeginning(0, n6);
                    ((Context)object).match.setEnd(0, n6 + this.fixedString.length());
                }
                ((Context)object).setInUse(false);
                return true;
            }
            ((Context)object).setInUse(false);
            return false;
        }
        if (this.fixedString != null && (n4 = this.fixedStringTable.matches(cArray, ((Context)object).start, ((Context)object).limit)) < 0) {
            ((Context)object).setInUse(false);
            return false;
        }
        int n7 = ((Context)object).limit - this.minlength;
        int n8 = -1;
        if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (RegularExpression.isSet(this.options, 4)) {
                n3 = ((Context)object).start;
                n8 = this.match((Context)object, this.operations, ((Context)object).start, 1, this.options);
            } else {
                boolean bl = true;
                for (n3 = ((Context)object).start; n3 <= n7; ++n3) {
                    char c = cArray[n3];
                    if (RegularExpression.isEOLChar(c)) {
                        bl = true;
                        continue;
                    }
                    if (!bl || 0 > (n8 = this.match((Context)object, this.operations, n3, 1, this.options))) {
                        bl = false;
                        continue;
                    }
                    break;
                }
            }
        } else if (this.firstChar != null) {
            RangeToken rangeToken = this.firstChar;
            for (n3 = ((Context)object).start; n3 <= n7; ++n3) {
                int n9 = cArray[n3];
                if (REUtil.isHighSurrogate(n9) && n3 + 1 < ((Context)object).limit) {
                    n9 = REUtil.composeFromSurrogates(n9, cArray[n3 + 1]);
                }
                if (!rangeToken.match(n9) || 0 > (n8 = this.match((Context)object, this.operations, n3, 1, this.options))) {
                    continue;
                }
                break;
            }
        } else {
            for (n3 = ((Context)object).start; n3 <= n7 && 0 > (n8 = this.match((Context)object, this.operations, n3, 1, this.options)); ++n3) {
            }
        }
        if (n8 >= 0) {
            if (((Context)object).match != null) {
                ((Context)object).match.setBeginning(0, n3);
                ((Context)object).match.setEnd(0, n8);
            }
            ((Context)object).setInUse(false);
            return true;
        }
        ((Context)object).setInUse(false);
        return false;
    }

    public boolean matches(String string) {
        return this.matches(string, 0, string.length(), (Match)null);
    }

    public boolean matches(String string, int n, int n2) {
        return this.matches(string, n, n2, (Match)null);
    }

    public boolean matches(String string, Match match) {
        return this.matches(string, 0, string.length(), match);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean matches(String string, int n, int n2, Match match) {
        int n3;
        int n4;
        Object object = this;
        synchronized (object) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        object = null;
        Context context = this.context;
        synchronized (context) {
            object = this.context.inuse ? new Context() : this.context;
            ((Context)object).reset(string, n, n2, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(string);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        ((Context)object).match = match;
        if (RegularExpression.isSet(this.options, 512)) {
            int n5 = this.match((Context)object, this.operations, ((Context)object).start, 1, this.options);
            if (n5 == ((Context)object).limit) {
                if (((Context)object).match != null) {
                    ((Context)object).match.setBeginning(0, ((Context)object).start);
                    ((Context)object).match.setEnd(0, n5);
                }
                ((Context)object).setInUse(false);
                return true;
            }
            return false;
        }
        if (this.fixedStringOnly) {
            int n6 = this.fixedStringTable.matches(string, ((Context)object).start, ((Context)object).limit);
            if (n6 >= 0) {
                if (((Context)object).match != null) {
                    ((Context)object).match.setBeginning(0, n6);
                    ((Context)object).match.setEnd(0, n6 + this.fixedString.length());
                }
                ((Context)object).setInUse(false);
                return true;
            }
            ((Context)object).setInUse(false);
            return false;
        }
        if (this.fixedString != null && (n4 = this.fixedStringTable.matches(string, ((Context)object).start, ((Context)object).limit)) < 0) {
            ((Context)object).setInUse(false);
            return false;
        }
        int n7 = ((Context)object).limit - this.minlength;
        int n8 = -1;
        if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (RegularExpression.isSet(this.options, 4)) {
                n3 = ((Context)object).start;
                n8 = this.match((Context)object, this.operations, ((Context)object).start, 1, this.options);
            } else {
                boolean bl = true;
                for (n3 = ((Context)object).start; n3 <= n7; ++n3) {
                    char c = string.charAt(n3);
                    if (RegularExpression.isEOLChar(c)) {
                        bl = true;
                        continue;
                    }
                    if (!bl || 0 > (n8 = this.match((Context)object, this.operations, n3, 1, this.options))) {
                        bl = false;
                        continue;
                    }
                    break;
                }
            }
        } else if (this.firstChar != null) {
            RangeToken rangeToken = this.firstChar;
            for (n3 = ((Context)object).start; n3 <= n7; ++n3) {
                int n9 = string.charAt(n3);
                if (REUtil.isHighSurrogate(n9) && n3 + 1 < ((Context)object).limit) {
                    n9 = REUtil.composeFromSurrogates(n9, string.charAt(n3 + 1));
                }
                if (!rangeToken.match(n9) || 0 > (n8 = this.match((Context)object, this.operations, n3, 1, this.options))) {
                    continue;
                }
                break;
            }
        } else {
            for (n3 = ((Context)object).start; n3 <= n7 && 0 > (n8 = this.match((Context)object, this.operations, n3, 1, this.options)); ++n3) {
            }
        }
        if (n8 >= 0) {
            if (((Context)object).match != null) {
                ((Context)object).match.setBeginning(0, n3);
                ((Context)object).match.setEnd(0, n8);
            }
            ((Context)object).setInUse(false);
            return true;
        }
        ((Context)object).setInUse(false);
        return false;
    }

    private int match(Context context, Op op, int n, int n2, int n3) {
        ExpressionTarget expressionTarget = context.target;
        Stack<Op> stack = new Stack<Op>();
        IntStack intStack = new IntStack();
        boolean bl = RegularExpression.isSet(n3, 2);
        int n4 = -1;
        boolean bl2 = false;
        block28: while (true) {
            Object object;
            int n5;
            if (op == null || n > context.limit || n < context.start) {
                n4 = op == null ? (RegularExpression.isSet(n3, 512) && n != context.limit ? -1 : n) : -1;
                bl2 = true;
            } else {
                n4 = -1;
                switch (op.type) {
                    case 1: {
                        int n6;
                        int n7 = n6 = n2 > 0 ? n : n - 1;
                        if (n6 >= context.limit || n6 < 0 || !this.matchChar(op.getData(), expressionTarget.charAt(n6), bl)) {
                            bl2 = true;
                            break;
                        }
                        n += n2;
                        op = op.next;
                        break;
                    }
                    case 0: {
                        int n8;
                        int n9 = n8 = n2 > 0 ? n : n - 1;
                        if (n8 >= context.limit || n8 < 0) {
                            bl2 = true;
                            break;
                        }
                        if (RegularExpression.isSet(n3, 4)) {
                            if (REUtil.isHighSurrogate(expressionTarget.charAt(n8)) && n8 + n2 >= 0 && n8 + n2 < context.limit) {
                                n8 += n2;
                            }
                        } else {
                            n5 = expressionTarget.charAt(n8);
                            if (REUtil.isHighSurrogate(n5) && n8 + n2 >= 0 && n8 + n2 < context.limit) {
                                n5 = REUtil.composeFromSurrogates(n5, expressionTarget.charAt(n8 += n2));
                            }
                            if (RegularExpression.isEOLChar(n5)) {
                                bl2 = true;
                                break;
                            }
                        }
                        n = n2 > 0 ? n8 + 1 : n8;
                        op = op.next;
                        break;
                    }
                    case 3: 
                    case 4: {
                        int n10;
                        int n11 = n10 = n2 > 0 ? n : n - 1;
                        if (n10 >= context.limit || n10 < 0) {
                            bl2 = true;
                            break;
                        }
                        n5 = expressionTarget.charAt(n);
                        if (REUtil.isHighSurrogate(n5) && n10 + n2 < context.limit && n10 + n2 >= 0) {
                            n5 = REUtil.composeFromSurrogates(n5, expressionTarget.charAt(n10 += n2));
                        }
                        if (!((RangeToken)(object = op.getToken())).match(n5)) {
                            bl2 = true;
                            break;
                        }
                        n = n2 > 0 ? n10 + 1 : n10;
                        op = op.next;
                        break;
                    }
                    case 5: {
                        if (!this.matchAnchor(expressionTarget, op, context, n, n3)) {
                            bl2 = true;
                            break;
                        }
                        op = op.next;
                        break;
                    }
                    case 16: {
                        int n12 = op.getData();
                        if (n12 <= 0 || n12 >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + n12);
                        }
                        if (context.match.getBeginning(n12) < 0 || context.match.getEnd(n12) < 0) {
                            bl2 = true;
                            break;
                        }
                        n5 = context.match.getBeginning(n12);
                        int n13 = context.match.getEnd(n12) - n5;
                        if (n2 > 0) {
                            if (!expressionTarget.regionMatches(bl, n, context.limit, n5, n13)) {
                                bl2 = true;
                                break;
                            }
                            n += n13;
                        } else {
                            if (!expressionTarget.regionMatches(bl, n - n13, context.limit, n5, n13)) {
                                bl2 = true;
                                break;
                            }
                            n -= n13;
                        }
                        op = op.next;
                        break;
                    }
                    case 6: {
                        String string = op.getString();
                        n5 = string.length();
                        if (n2 > 0) {
                            if (!expressionTarget.regionMatches(bl, n, context.limit, string, n5)) {
                                bl2 = true;
                                break;
                            }
                            n += n5;
                        } else {
                            if (!expressionTarget.regionMatches(bl, n - n5, context.limit, string, n5)) {
                                bl2 = true;
                                break;
                            }
                            n -= n5;
                        }
                        op = op.next;
                        break;
                    }
                    case 7: {
                        int n14 = op.getData();
                        if (context.closureContexts[n14].contains(n)) {
                            bl2 = true;
                            break;
                        }
                        context.closureContexts[n14].addOffset(n);
                    }
                    case 9: {
                        stack.push(op);
                        intStack.push(n);
                        op = op.getChild();
                        break;
                    }
                    case 8: 
                    case 10: {
                        stack.push(op);
                        intStack.push(n);
                        op = op.next;
                        break;
                    }
                    case 11: {
                        if (op.size() == 0) {
                            bl2 = true;
                            break;
                        }
                        stack.push(op);
                        intStack.push(0);
                        intStack.push(n);
                        op = op.elementAt(0);
                        break;
                    }
                    case 15: {
                        int n15 = op.getData();
                        if (context.match != null) {
                            if (n15 > 0) {
                                intStack.push(context.match.getBeginning(n15));
                                context.match.setBeginning(n15, n);
                            } else {
                                n5 = -n15;
                                intStack.push(context.match.getEnd(n5));
                                context.match.setEnd(n5, n);
                            }
                            stack.push(op);
                            intStack.push(n);
                        }
                        op = op.next;
                        break;
                    }
                    case 20: 
                    case 21: 
                    case 22: 
                    case 23: {
                        stack.push(op);
                        intStack.push(n2);
                        intStack.push(n);
                        n2 = op.type == 20 || op.type == 21 ? 1 : -1;
                        op = op.getChild();
                        break;
                    }
                    case 24: {
                        stack.push(op);
                        intStack.push(n);
                        op = op.getChild();
                        break;
                    }
                    case 25: {
                        int n16 = n3;
                        n16 |= op.getData();
                        stack.push(op);
                        intStack.push(n3);
                        intStack.push(n);
                        n3 = n16 &= ~op.getData2();
                        op = op.getChild();
                        break;
                    }
                    case 26: {
                        Op.ConditionOp conditionOp = (Op.ConditionOp)op;
                        if (conditionOp.refNumber > 0) {
                            if (conditionOp.refNumber >= this.nofparen) {
                                throw new RuntimeException("Internal Error: Reference number must be more than zero: " + conditionOp.refNumber);
                            }
                            if (context.match.getBeginning(conditionOp.refNumber) >= 0 && context.match.getEnd(conditionOp.refNumber) >= 0) {
                                op = conditionOp.yes;
                                break;
                            }
                            if (conditionOp.no != null) {
                                op = conditionOp.no;
                                break;
                            }
                            op = conditionOp.next;
                            break;
                        }
                        stack.push(op);
                        intStack.push(n);
                        op = conditionOp.condition;
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unknown operation type: " + op.type);
                    }
                }
            }
            block29: while (true) {
                if (!bl2) continue block28;
                if (stack.isEmpty()) {
                    return n4;
                }
                op = (Op)stack.pop();
                n = intStack.pop();
                switch (op.type) {
                    case 7: 
                    case 9: {
                        if (n4 >= 0) break;
                        op = op.next;
                        bl2 = false;
                        break;
                    }
                    case 8: 
                    case 10: {
                        if (n4 >= 0) break;
                        op = op.getChild();
                        bl2 = false;
                        break;
                    }
                    case 11: {
                        int n17 = intStack.pop();
                        if (n4 >= 0) continue block29;
                        if (++n17 < op.size()) {
                            stack.push(op);
                            intStack.push(n17);
                            intStack.push(n);
                            op = op.elementAt(n17);
                            bl2 = false;
                            break;
                        }
                        n4 = -1;
                        break;
                    }
                    case 15: {
                        int n18 = op.getData();
                        n5 = intStack.pop();
                        if (n4 >= 0) break;
                        if (n18 > 0) {
                            context.match.setBeginning(n18, n5);
                            break;
                        }
                        context.match.setEnd(-n18, n5);
                        break;
                    }
                    case 20: 
                    case 22: {
                        n2 = intStack.pop();
                        if (0 <= n4) {
                            op = op.next;
                            bl2 = false;
                        }
                        n4 = -1;
                        break;
                    }
                    case 21: 
                    case 23: {
                        n2 = intStack.pop();
                        if (0 > n4) {
                            op = op.next;
                            bl2 = false;
                        }
                        n4 = -1;
                        break;
                    }
                    case 25: {
                        n3 = intStack.pop();
                    }
                    case 24: {
                        if (n4 < 0) break;
                        n = n4;
                        op = op.next;
                        bl2 = false;
                        break;
                    }
                    case 26: {
                        object = (Op.ConditionOp)op;
                        op = 0 <= n4 ? ((Op.ConditionOp)object).yes : (((Op.ConditionOp)object).no != null ? ((Op.ConditionOp)object).no : ((Op.ConditionOp)object).next);
                        bl2 = false;
                        break;
                    }
                }
            }
            break;
        }
    }

    private boolean matchChar(int n, int n2, boolean bl) {
        return bl ? RegularExpression.matchIgnoreCase(n, n2) : n == n2;
    }

    boolean matchAnchor(ExpressionTarget expressionTarget, Op op, Context context, int n, int n2) {
        boolean bl = false;
        switch (op.getData()) {
            case 94: {
                if (!(RegularExpression.isSet(n2, 8) ? n != context.start && (n <= context.start || n >= context.limit || !RegularExpression.isEOLChar(expressionTarget.charAt(n - 1))) : n != context.start)) break;
                return false;
            }
            case 64: {
                if (n == context.start || n > context.start && RegularExpression.isEOLChar(expressionTarget.charAt(n - 1))) break;
                return false;
            }
            case 36: {
                if (!(RegularExpression.isSet(n2, 8) ? n != context.limit && (n >= context.limit || !RegularExpression.isEOLChar(expressionTarget.charAt(n))) : !(n == context.limit || n + 1 == context.limit && RegularExpression.isEOLChar(expressionTarget.charAt(n)) || n + 2 == context.limit && expressionTarget.charAt(n) == '\r' && expressionTarget.charAt(n + 1) == '\n'))) break;
                return false;
            }
            case 65: {
                if (n == context.start) break;
                return false;
            }
            case 90: {
                if (n == context.limit || n + 1 == context.limit && RegularExpression.isEOLChar(expressionTarget.charAt(n)) || n + 2 == context.limit && expressionTarget.charAt(n) == '\r' && expressionTarget.charAt(n + 1) == '\n') break;
                return false;
            }
            case 122: {
                if (n == context.limit) break;
                return false;
            }
            case 98: {
                if (context.length == 0) {
                    return false;
                }
                int n3 = RegularExpression.getWordType(expressionTarget, context.start, context.limit, n, n2);
                if (n3 == 0) {
                    return false;
                }
                int n4 = RegularExpression.getPreviousWordType(expressionTarget, context.start, context.limit, n, n2);
                if (n3 != n4) break;
                return false;
            }
            case 66: {
                if (context.length == 0) {
                    bl = true;
                } else {
                    int n5 = RegularExpression.getWordType(expressionTarget, context.start, context.limit, n, n2);
                    boolean bl2 = bl = n5 == 0 || n5 == RegularExpression.getPreviousWordType(expressionTarget, context.start, context.limit, n, n2);
                }
                if (bl) break;
                return false;
            }
            case 60: {
                if (context.length == 0 || n == context.limit) {
                    return false;
                }
                if (RegularExpression.getWordType(expressionTarget, context.start, context.limit, n, n2) == 1 && RegularExpression.getPreviousWordType(expressionTarget, context.start, context.limit, n, n2) == 2) break;
                return false;
            }
            case 62: {
                if (context.length == 0 || n == context.start) {
                    return false;
                }
                if (RegularExpression.getWordType(expressionTarget, context.start, context.limit, n, n2) == 2 && RegularExpression.getPreviousWordType(expressionTarget, context.start, context.limit, n, n2) == 1) break;
                return false;
            }
        }
        return true;
    }

    private static final int getPreviousWordType(ExpressionTarget expressionTarget, int n, int n2, int n3, int n4) {
        int n5 = RegularExpression.getWordType(expressionTarget, n, n2, --n3, n4);
        while (n5 == 0) {
            n5 = RegularExpression.getWordType(expressionTarget, n, n2, --n3, n4);
        }
        return n5;
    }

    private static final int getWordType(ExpressionTarget expressionTarget, int n, int n2, int n3, int n4) {
        if (n3 < n || n3 >= n2) {
            return 2;
        }
        return RegularExpression.getWordType0(expressionTarget.charAt(n3), n4);
    }

    public boolean matches(CharacterIterator characterIterator) {
        return this.matches(characterIterator, (Match)null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean matches(CharacterIterator characterIterator, Match match) {
        int n;
        int n2;
        int n3 = characterIterator.getBeginIndex();
        int n4 = characterIterator.getEndIndex();
        Object object = this;
        synchronized (object) {
            if (this.operations == null) {
                this.prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        object = null;
        Context context = this.context;
        synchronized (context) {
            object = this.context.inuse ? new Context() : this.context;
            ((Context)object).reset(characterIterator, n3, n4, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(characterIterator);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        ((Context)object).match = match;
        if (RegularExpression.isSet(this.options, 512)) {
            int n5 = this.match((Context)object, this.operations, ((Context)object).start, 1, this.options);
            if (n5 == ((Context)object).limit) {
                if (((Context)object).match != null) {
                    ((Context)object).match.setBeginning(0, ((Context)object).start);
                    ((Context)object).match.setEnd(0, n5);
                }
                ((Context)object).setInUse(false);
                return true;
            }
            return false;
        }
        if (this.fixedStringOnly) {
            int n6 = this.fixedStringTable.matches(characterIterator, ((Context)object).start, ((Context)object).limit);
            if (n6 >= 0) {
                if (((Context)object).match != null) {
                    ((Context)object).match.setBeginning(0, n6);
                    ((Context)object).match.setEnd(0, n6 + this.fixedString.length());
                }
                ((Context)object).setInUse(false);
                return true;
            }
            ((Context)object).setInUse(false);
            return false;
        }
        if (this.fixedString != null && (n2 = this.fixedStringTable.matches(characterIterator, ((Context)object).start, ((Context)object).limit)) < 0) {
            ((Context)object).setInUse(false);
            return false;
        }
        int n7 = ((Context)object).limit - this.minlength;
        int n8 = -1;
        if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (RegularExpression.isSet(this.options, 4)) {
                n = ((Context)object).start;
                n8 = this.match((Context)object, this.operations, ((Context)object).start, 1, this.options);
            } else {
                boolean bl = true;
                for (n = ((Context)object).start; n <= n7; ++n) {
                    char c = characterIterator.setIndex(n);
                    if (RegularExpression.isEOLChar(c)) {
                        bl = true;
                        continue;
                    }
                    if (!bl || 0 > (n8 = this.match((Context)object, this.operations, n, 1, this.options))) {
                        bl = false;
                        continue;
                    }
                    break;
                }
            }
        } else if (this.firstChar != null) {
            RangeToken rangeToken = this.firstChar;
            for (n = ((Context)object).start; n <= n7; ++n) {
                int n9 = characterIterator.setIndex(n);
                if (REUtil.isHighSurrogate(n9) && n + 1 < ((Context)object).limit) {
                    n9 = REUtil.composeFromSurrogates(n9, characterIterator.setIndex(n + 1));
                }
                if (!rangeToken.match(n9) || 0 > (n8 = this.match((Context)object, this.operations, n, 1, this.options))) {
                    continue;
                }
                break;
            }
        } else {
            for (n = ((Context)object).start; n <= n7 && 0 > (n8 = this.match((Context)object, this.operations, n, 1, this.options)); ++n) {
            }
        }
        if (n8 >= 0) {
            if (((Context)object).match != null) {
                ((Context)object).match.setBeginning(0, n);
                ((Context)object).match.setEnd(0, n8);
            }
            ((Context)object).setInUse(false);
            return true;
        }
        ((Context)object).setInUse(false);
        return false;
    }

    void prepare() {
        Object object;
        int n;
        this.compile(this.tokentree);
        this.minlength = this.tokentree.getMinLength();
        this.firstChar = null;
        if (!RegularExpression.isSet(this.options, 128) && !RegularExpression.isSet(this.options, 512) && (n = this.tokentree.analyzeFirstCharacter((RangeToken)(object = Token.createRange()), this.options)) == 1) {
            ((RangeToken)object).compactRanges();
            this.firstChar = object;
        }
        if (this.operations != null && (this.operations.type == 6 || this.operations.type == 1) && this.operations.next == null) {
            this.fixedStringOnly = true;
            if (this.operations.type == 6) {
                this.fixedString = this.operations.getString();
            } else if (this.operations.getData() >= 65536) {
                this.fixedString = REUtil.decomposeToSurrogates(this.operations.getData());
            } else {
                object = new char[1];
                object[0] = (char)this.operations.getData();
                this.fixedString = new String((char[])object);
            }
            this.fixedStringOptions = this.options;
            this.fixedStringTable = new BMPattern(this.fixedString, 256, RegularExpression.isSet(this.fixedStringOptions, 2));
        } else if (!RegularExpression.isSet(this.options, 256) && !RegularExpression.isSet(this.options, 512)) {
            object = new Token.FixedStringContainer();
            this.tokentree.findFixedString((Token.FixedStringContainer)object, this.options);
            this.fixedString = ((Token.FixedStringContainer)object).token == null ? null : ((Token.FixedStringContainer)object).token.getString();
            this.fixedStringOptions = ((Token.FixedStringContainer)object).options;
            if (this.fixedString != null && this.fixedString.length() < 2) {
                this.fixedString = null;
            }
            if (this.fixedString != null) {
                this.fixedStringTable = new BMPattern(this.fixedString, 256, RegularExpression.isSet(this.fixedStringOptions, 2));
            }
        }
    }

    private static final boolean isSet(int n, int n2) {
        return (n & n2) == n2;
    }

    public RegularExpression(String string) throws ParseException {
        this(string, null);
    }

    public RegularExpression(String string, String string2) throws ParseException {
        this.setPattern(string, string2);
    }

    public RegularExpression(String string, String string2, Locale locale) throws ParseException {
        this.setPattern(string, string2, locale);
    }

    RegularExpression(String string, Token token, int n, boolean bl, int n2) {
        this.regex = string;
        this.tokentree = token;
        this.nofparen = n;
        this.options = n2;
        this.hasBackReferences = bl;
    }

    public void setPattern(String string) throws ParseException {
        this.setPattern(string, Locale.getDefault());
    }

    public void setPattern(String string, Locale locale) throws ParseException {
        this.setPattern(string, this.options, locale);
    }

    private void setPattern(String string, int n, Locale locale) throws ParseException {
        this.regex = string;
        this.options = n;
        RegexParser regexParser = RegularExpression.isSet(this.options, 512) ? new ParserForXMLSchema(locale) : new RegexParser(locale);
        this.tokentree = regexParser.parse(this.regex, this.options);
        this.nofparen = regexParser.parennumber;
        this.hasBackReferences = regexParser.hasBackReferences;
        this.operations = null;
        this.context = null;
    }

    public void setPattern(String string, String string2) throws ParseException {
        this.setPattern(string, string2, Locale.getDefault());
    }

    public void setPattern(String string, String string2, Locale locale) throws ParseException {
        this.setPattern(string, REUtil.parseOptions(string2), locale);
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

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof RegularExpression)) {
            return false;
        }
        RegularExpression regularExpression = (RegularExpression)object;
        return this.regex.equals(regularExpression.regex) && this.options == regularExpression.options;
    }

    boolean equals(String string, int n) {
        return this.regex.equals(string) && this.options == n;
    }

    public int hashCode() {
        return (this.regex + "/" + this.getOptions()).hashCode();
    }

    public int getNumberOfGroups() {
        return this.nofparen;
    }

    private static final int getWordType0(char c, int n) {
        if (!RegularExpression.isSet(n, 64)) {
            if (RegularExpression.isSet(n, 32)) {
                return Token.getRange("IsWord", true).match(c) ? 1 : 2;
            }
            return RegularExpression.isWordChar(c) ? 1 : 2;
        }
        switch (Character.getType(c)) {
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
                switch (c) {
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

    private static final boolean isEOLChar(int n) {
        return n == 10 || n == 13 || n == 8232 || n == 8233;
    }

    private static final boolean isWordChar(int n) {
        if (n == 95) {
            return true;
        }
        if (n < 48) {
            return false;
        }
        if (n > 122) {
            return false;
        }
        if (n <= 57) {
            return true;
        }
        if (n < 65) {
            return false;
        }
        if (n <= 90) {
            return true;
        }
        return n >= 97;
    }

    private static final boolean matchIgnoreCase(int n, int n2) {
        char c;
        if (n == n2) {
            return true;
        }
        if (n > 65535 || n2 > 65535) {
            return false;
        }
        char c2 = Character.toUpperCase((char)n);
        if (c2 == (c = Character.toUpperCase((char)n2))) {
            return true;
        }
        return Character.toLowerCase(c2) == Character.toLowerCase(c);
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

        private void resetCommon(int n) {
            this.length = this.limit - this.start;
            this.setInUse(true);
            this.match = null;
            if (this.closureContexts == null || this.closureContexts.length != n) {
                this.closureContexts = new ClosureContext[n];
            }
            for (int i = 0; i < n; ++i) {
                if (this.closureContexts[i] == null) {
                    this.closureContexts[i] = new ClosureContext();
                    continue;
                }
                this.closureContexts[i].reset();
            }
        }

        void reset(CharacterIterator characterIterator, int n, int n2, int n3) {
            if (this.characterIteratorTarget == null) {
                this.characterIteratorTarget = new CharacterIteratorTarget(characterIterator);
            } else {
                this.characterIteratorTarget.resetTarget(characterIterator);
            }
            this.target = this.characterIteratorTarget;
            this.start = n;
            this.limit = n2;
            this.resetCommon(n3);
        }

        void reset(String string, int n, int n2, int n3) {
            if (this.stringTarget == null) {
                this.stringTarget = new StringTarget(string);
            } else {
                this.stringTarget.resetTarget(string);
            }
            this.target = this.stringTarget;
            this.start = n;
            this.limit = n2;
            this.resetCommon(n3);
        }

        void reset(char[] cArray, int n, int n2, int n3) {
            if (this.charArrayTarget == null) {
                this.charArrayTarget = new CharArrayTarget(cArray);
            } else {
                this.charArrayTarget.resetTarget(cArray);
            }
            this.target = this.charArrayTarget;
            this.start = n;
            this.limit = n2;
            this.resetCommon(n3);
        }

        synchronized void setInUse(boolean bl) {
            this.inuse = bl;
        }
    }

    static final class ClosureContext {
        int[] offsets = new int[4];
        int currentIndex = 0;

        ClosureContext() {
        }

        boolean contains(int n) {
            for (int i = 0; i < this.currentIndex; ++i) {
                if (this.offsets[i] != n) continue;
                return true;
            }
            return false;
        }

        void reset() {
            this.currentIndex = 0;
        }

        void addOffset(int n) {
            if (this.currentIndex == this.offsets.length) {
                this.offsets = this.expandOffsets();
            }
            this.offsets[this.currentIndex++] = n;
        }

        private int[] expandOffsets() {
            int n = this.offsets.length;
            int n2 = n << 1;
            int[] nArray = new int[n2];
            System.arraycopy(this.offsets, 0, nArray, 0, this.currentIndex);
            return nArray;
        }
    }

    static final class CharacterIteratorTarget
    extends ExpressionTarget {
        CharacterIterator target;

        CharacterIteratorTarget(CharacterIterator characterIterator) {
            this.target = characterIterator;
        }

        final void resetTarget(CharacterIterator characterIterator) {
            this.target = characterIterator;
        }

        @Override
        final char charAt(int n) {
            return this.target.setIndex(n);
        }

        @Override
        final boolean regionMatches(boolean bl, int n, int n2, String string, int n3) {
            if (n < 0 || n2 - n < n3) {
                return false;
            }
            return bl ? this.regionMatchesIgnoreCase(n, n2, string, n3) : this.regionMatches(n, n2, string, n3);
        }

        private final boolean regionMatches(int n, int n2, String string, int n3) {
            int n4 = 0;
            while (n3-- > 0) {
                if (this.target.setIndex(n++) == string.charAt(n4++)) continue;
                return false;
            }
            return true;
        }

        private final boolean regionMatchesIgnoreCase(int n, int n2, String string, int n3) {
            int n4 = 0;
            while (n3-- > 0) {
                char c;
                char c2;
                char c3;
                char c4;
                if ((c4 = this.target.setIndex(n++)) == (c3 = string.charAt(n4++)) || (c2 = Character.toUpperCase(c4)) == (c = Character.toUpperCase(c3)) || Character.toLowerCase(c2) == Character.toLowerCase(c)) continue;
                return false;
            }
            return true;
        }

        @Override
        final boolean regionMatches(boolean bl, int n, int n2, int n3, int n4) {
            if (n < 0 || n2 - n < n4) {
                return false;
            }
            return bl ? this.regionMatchesIgnoreCase(n, n2, n3, n4) : this.regionMatches(n, n2, n3, n4);
        }

        private final boolean regionMatches(int n, int n2, int n3, int n4) {
            int n5 = n3;
            while (n4-- > 0) {
                if (this.target.setIndex(n++) == this.target.setIndex(n5++)) continue;
                return false;
            }
            return true;
        }

        private final boolean regionMatchesIgnoreCase(int n, int n2, int n3, int n4) {
            int n5 = n3;
            while (n4-- > 0) {
                char c;
                char c2;
                char c3;
                char c4;
                if ((c4 = this.target.setIndex(n++)) == (c3 = this.target.setIndex(n5++)) || (c2 = Character.toUpperCase(c4)) == (c = Character.toUpperCase(c3)) || Character.toLowerCase(c2) == Character.toLowerCase(c)) continue;
                return false;
            }
            return true;
        }
    }

    static final class CharArrayTarget
    extends ExpressionTarget {
        char[] target;

        CharArrayTarget(char[] cArray) {
            this.target = cArray;
        }

        final void resetTarget(char[] cArray) {
            this.target = cArray;
        }

        @Override
        char charAt(int n) {
            return this.target[n];
        }

        @Override
        final boolean regionMatches(boolean bl, int n, int n2, String string, int n3) {
            if (n < 0 || n2 - n < n3) {
                return false;
            }
            return bl ? this.regionMatchesIgnoreCase(n, n2, string, n3) : this.regionMatches(n, n2, string, n3);
        }

        private final boolean regionMatches(int n, int n2, String string, int n3) {
            int n4 = 0;
            while (n3-- > 0) {
                if (this.target[n++] == string.charAt(n4++)) continue;
                return false;
            }
            return true;
        }

        private final boolean regionMatchesIgnoreCase(int n, int n2, String string, int n3) {
            int n4 = 0;
            while (n3-- > 0) {
                char c;
                char c2;
                char c3;
                char c4;
                if ((c4 = this.target[n++]) == (c3 = string.charAt(n4++)) || (c2 = Character.toUpperCase(c4)) == (c = Character.toUpperCase(c3)) || Character.toLowerCase(c2) == Character.toLowerCase(c)) continue;
                return false;
            }
            return true;
        }

        @Override
        final boolean regionMatches(boolean bl, int n, int n2, int n3, int n4) {
            if (n < 0 || n2 - n < n4) {
                return false;
            }
            return bl ? this.regionMatchesIgnoreCase(n, n2, n3, n4) : this.regionMatches(n, n2, n3, n4);
        }

        private final boolean regionMatches(int n, int n2, int n3, int n4) {
            int n5 = n3;
            while (n4-- > 0) {
                if (this.target[n++] == this.target[n5++]) continue;
                return false;
            }
            return true;
        }

        private final boolean regionMatchesIgnoreCase(int n, int n2, int n3, int n4) {
            int n5 = n3;
            while (n4-- > 0) {
                char c;
                char c2;
                char c3;
                char c4;
                if ((c4 = this.target[n++]) == (c3 = this.target[n5++]) || (c2 = Character.toUpperCase(c4)) == (c = Character.toUpperCase(c3)) || Character.toLowerCase(c2) == Character.toLowerCase(c)) continue;
                return false;
            }
            return true;
        }
    }

    static final class StringTarget
    extends ExpressionTarget {
        private String target;

        StringTarget(String string) {
            this.target = string;
        }

        final void resetTarget(String string) {
            this.target = string;
        }

        @Override
        final char charAt(int n) {
            return this.target.charAt(n);
        }

        @Override
        final boolean regionMatches(boolean bl, int n, int n2, String string, int n3) {
            if (n2 - n < n3) {
                return false;
            }
            return bl ? this.target.regionMatches(true, n, string, 0, n3) : this.target.regionMatches(n, string, 0, n3);
        }

        @Override
        final boolean regionMatches(boolean bl, int n, int n2, int n3, int n4) {
            if (n2 - n < n4) {
                return false;
            }
            return bl ? this.target.regionMatches(true, n, this.target, n3, n4) : this.target.regionMatches(n, this.target, n3, n4);
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


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.compiler;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xpath.Expression;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.UnionPathIterator;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.OpMap;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.functions.FuncExtFunctionAvailable;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.And;
import org.apache.xpath.operations.Bool;
import org.apache.xpath.operations.Div;
import org.apache.xpath.operations.Equals;
import org.apache.xpath.operations.Gt;
import org.apache.xpath.operations.Gte;
import org.apache.xpath.operations.Lt;
import org.apache.xpath.operations.Lte;
import org.apache.xpath.operations.Minus;
import org.apache.xpath.operations.Mod;
import org.apache.xpath.operations.Mult;
import org.apache.xpath.operations.Neg;
import org.apache.xpath.operations.NotEquals;
import org.apache.xpath.operations.Number;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Or;
import org.apache.xpath.operations.Plus;
import org.apache.xpath.operations.UnaryOperation;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.FunctionPattern;
import org.apache.xpath.patterns.StepPattern;
import org.apache.xpath.patterns.UnionPattern;

public class Compiler
extends OpMap {
    private int locPathDepth = -1;
    private static final boolean DEBUG = false;
    private static long s_nextMethodId = 0L;
    private PrefixResolver m_currentPrefixResolver = null;
    ErrorListener m_errorHandler;
    SourceLocator m_locator;
    private FunctionTable m_functionTable;

    public Compiler(ErrorListener errorHandler, SourceLocator locator, FunctionTable fTable) {
        this.m_errorHandler = errorHandler;
        this.m_locator = locator;
        this.m_functionTable = fTable;
    }

    public Compiler() {
        this.m_errorHandler = null;
        this.m_locator = null;
    }

    public Expression compile(int opPos) throws TransformerException {
        int op = this.getOp(opPos);
        Expression expr = null;
        switch (op) {
            case 1: {
                expr = this.compile(opPos + 2);
                break;
            }
            case 2: {
                expr = this.or(opPos);
                break;
            }
            case 3: {
                expr = this.and(opPos);
                break;
            }
            case 4: {
                expr = this.notequals(opPos);
                break;
            }
            case 5: {
                expr = this.equals(opPos);
                break;
            }
            case 6: {
                expr = this.lte(opPos);
                break;
            }
            case 7: {
                expr = this.lt(opPos);
                break;
            }
            case 8: {
                expr = this.gte(opPos);
                break;
            }
            case 9: {
                expr = this.gt(opPos);
                break;
            }
            case 10: {
                expr = this.plus(opPos);
                break;
            }
            case 11: {
                expr = this.minus(opPos);
                break;
            }
            case 12: {
                expr = this.mult(opPos);
                break;
            }
            case 13: {
                expr = this.div(opPos);
                break;
            }
            case 14: {
                expr = this.mod(opPos);
                break;
            }
            case 16: {
                expr = this.neg(opPos);
                break;
            }
            case 17: {
                expr = this.string(opPos);
                break;
            }
            case 18: {
                expr = this.bool(opPos);
                break;
            }
            case 19: {
                expr = this.number(opPos);
                break;
            }
            case 20: {
                expr = this.union(opPos);
                break;
            }
            case 21: {
                expr = this.literal(opPos);
                break;
            }
            case 22: {
                expr = this.variable(opPos);
                break;
            }
            case 23: {
                expr = this.group(opPos);
                break;
            }
            case 27: {
                expr = this.numberlit(opPos);
                break;
            }
            case 26: {
                expr = this.arg(opPos);
                break;
            }
            case 24: {
                expr = this.compileExtension(opPos);
                break;
            }
            case 25: {
                expr = this.compileFunction(opPos);
                break;
            }
            case 28: {
                expr = this.locationPath(opPos);
                break;
            }
            case 29: {
                expr = null;
                break;
            }
            case 30: {
                expr = this.matchPattern(opPos + 2);
                break;
            }
            case 31: {
                expr = this.locationPathPattern(opPos);
                break;
            }
            case 15: {
                this.error("ER_UNKNOWN_OPCODE", new Object[]{"quo"});
                break;
            }
            default: {
                this.error("ER_UNKNOWN_OPCODE", new Object[]{Integer.toString(this.getOp(opPos))});
            }
        }
        return expr;
    }

    private Expression compileOperation(Operation operation, int opPos) throws TransformerException {
        int leftPos = Compiler.getFirstChildPos(opPos);
        int rightPos = this.getNextOpPos(leftPos);
        operation.setLeftRight(this.compile(leftPos), this.compile(rightPos));
        return operation;
    }

    private Expression compileUnary(UnaryOperation unary, int opPos) throws TransformerException {
        int rightPos = Compiler.getFirstChildPos(opPos);
        unary.setRight(this.compile(rightPos));
        return unary;
    }

    protected Expression or(int opPos) throws TransformerException {
        return this.compileOperation(new Or(), opPos);
    }

    protected Expression and(int opPos) throws TransformerException {
        return this.compileOperation(new And(), opPos);
    }

    protected Expression notequals(int opPos) throws TransformerException {
        return this.compileOperation(new NotEquals(), opPos);
    }

    protected Expression equals(int opPos) throws TransformerException {
        return this.compileOperation(new Equals(), opPos);
    }

    protected Expression lte(int opPos) throws TransformerException {
        return this.compileOperation(new Lte(), opPos);
    }

    protected Expression lt(int opPos) throws TransformerException {
        return this.compileOperation(new Lt(), opPos);
    }

    protected Expression gte(int opPos) throws TransformerException {
        return this.compileOperation(new Gte(), opPos);
    }

    protected Expression gt(int opPos) throws TransformerException {
        return this.compileOperation(new Gt(), opPos);
    }

    protected Expression plus(int opPos) throws TransformerException {
        return this.compileOperation(new Plus(), opPos);
    }

    protected Expression minus(int opPos) throws TransformerException {
        return this.compileOperation(new Minus(), opPos);
    }

    protected Expression mult(int opPos) throws TransformerException {
        return this.compileOperation(new Mult(), opPos);
    }

    protected Expression div(int opPos) throws TransformerException {
        return this.compileOperation(new Div(), opPos);
    }

    protected Expression mod(int opPos) throws TransformerException {
        return this.compileOperation(new Mod(), opPos);
    }

    protected Expression neg(int opPos) throws TransformerException {
        return this.compileUnary(new Neg(), opPos);
    }

    protected Expression string(int opPos) throws TransformerException {
        return this.compileUnary(new org.apache.xpath.operations.String(), opPos);
    }

    protected Expression bool(int opPos) throws TransformerException {
        return this.compileUnary(new Bool(), opPos);
    }

    protected Expression number(int opPos) throws TransformerException {
        return this.compileUnary(new Number(), opPos);
    }

    protected Expression literal(int opPos) {
        opPos = Compiler.getFirstChildPos(opPos);
        return (XString)this.getTokenQueue().elementAt(this.getOp(opPos));
    }

    protected Expression numberlit(int opPos) {
        opPos = Compiler.getFirstChildPos(opPos);
        return (XNumber)this.getTokenQueue().elementAt(this.getOp(opPos));
    }

    protected Expression variable(int opPos) throws TransformerException {
        Variable var = new Variable();
        int nsPos = this.getOp(opPos = Compiler.getFirstChildPos(opPos));
        String namespace = -2 == nsPos ? null : (String)this.getTokenQueue().elementAt(nsPos);
        String localname = (String)this.getTokenQueue().elementAt(this.getOp(opPos + 1));
        QName qname = new QName(namespace, localname);
        var.setQName(qname);
        return var;
    }

    protected Expression group(int opPos) throws TransformerException {
        return this.compile(opPos + 2);
    }

    protected Expression arg(int opPos) throws TransformerException {
        return this.compile(opPos + 2);
    }

    protected Expression union(int opPos) throws TransformerException {
        ++this.locPathDepth;
        try {
            LocPathIterator locPathIterator = UnionPathIterator.createUnionIterator(this, opPos);
            return locPathIterator;
        }
        finally {
            --this.locPathDepth;
        }
    }

    public int getLocationPathDepth() {
        return this.locPathDepth;
    }

    FunctionTable getFunctionTable() {
        return this.m_functionTable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Expression locationPath(int opPos) throws TransformerException {
        ++this.locPathDepth;
        try {
            DTMIterator iter = WalkerFactory.newDTMIterator(this, opPos, this.locPathDepth == 0);
            Expression expression = (Expression)((Object)iter);
            return expression;
        }
        finally {
            --this.locPathDepth;
        }
    }

    public Expression predicate(int opPos) throws TransformerException {
        return this.compile(opPos + 2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Expression matchPattern(int opPos) throws TransformerException {
        ++this.locPathDepth;
        try {
            int nextOpPos = opPos;
            int i = 0;
            while (this.getOp(nextOpPos) == 31) {
                nextOpPos = this.getNextOpPos(nextOpPos);
                ++i;
            }
            if (i == 1) {
                Expression expression = this.compile(opPos);
                return expression;
            }
            UnionPattern up = new UnionPattern();
            StepPattern[] patterns = new StepPattern[i];
            i = 0;
            while (this.getOp(opPos) == 31) {
                nextOpPos = this.getNextOpPos(opPos);
                patterns[i] = (StepPattern)this.compile(opPos);
                opPos = nextOpPos;
                ++i;
            }
            up.setPatterns(patterns);
            UnionPattern unionPattern = up;
            return unionPattern;
        }
        finally {
            --this.locPathDepth;
        }
    }

    public Expression locationPathPattern(int opPos) throws TransformerException {
        opPos = Compiler.getFirstChildPos(opPos);
        return this.stepPattern(opPos, 0, null);
    }

    public int getWhatToShow(int opPos) {
        int axesType = this.getOp(opPos);
        int testType = this.getOp(opPos + 3);
        switch (testType) {
            case 1030: {
                return 128;
            }
            case 1031: {
                return 12;
            }
            case 1032: {
                return 64;
            }
            case 1033: {
                switch (axesType) {
                    case 49: {
                        return 4096;
                    }
                    case 39: 
                    case 51: {
                        return 2;
                    }
                    case 38: 
                    case 42: 
                    case 48: {
                        return -1;
                    }
                }
                if (this.getOp(0) == 30) {
                    return -1283;
                }
                return -3;
            }
            case 35: {
                return 1280;
            }
            case 1034: {
                return 65536;
            }
            case 34: {
                switch (axesType) {
                    case 49: {
                        return 4096;
                    }
                    case 39: 
                    case 51: {
                        return 2;
                    }
                    case 52: 
                    case 53: {
                        return 1;
                    }
                }
                return 1;
            }
        }
        return -1;
    }

    protected StepPattern stepPattern(int opPos, int stepCount, StepPattern ancestorPattern) throws TransformerException {
        StepPattern pattern;
        int argLen;
        int startOpPos = opPos;
        int stepType = this.getOp(opPos);
        if (-1 == stepType) {
            return null;
        }
        boolean addMagicSelf = true;
        int endStep = this.getNextOpPos(opPos);
        switch (stepType) {
            case 25: {
                addMagicSelf = false;
                argLen = this.getOp(opPos + 1);
                pattern = new FunctionPattern(this.compileFunction(opPos), 10, 3);
                break;
            }
            case 50: {
                addMagicSelf = false;
                argLen = this.getArgLengthOfStep(opPos);
                opPos = Compiler.getFirstChildPosOfStep(opPos);
                pattern = new StepPattern(1280, 10, 3);
                break;
            }
            case 51: {
                argLen = this.getArgLengthOfStep(opPos);
                opPos = Compiler.getFirstChildPosOfStep(opPos);
                pattern = new StepPattern(2, this.getStepNS(startOpPos), this.getStepLocalName(startOpPos), 10, 2);
                break;
            }
            case 52: {
                argLen = this.getArgLengthOfStep(opPos);
                opPos = Compiler.getFirstChildPosOfStep(opPos);
                int what = this.getWhatToShow(startOpPos);
                if (1280 == what) {
                    addMagicSelf = false;
                }
                pattern = new StepPattern(this.getWhatToShow(startOpPos), this.getStepNS(startOpPos), this.getStepLocalName(startOpPos), 0, 3);
                break;
            }
            case 53: {
                argLen = this.getArgLengthOfStep(opPos);
                opPos = Compiler.getFirstChildPosOfStep(opPos);
                pattern = new StepPattern(this.getWhatToShow(startOpPos), this.getStepNS(startOpPos), this.getStepLocalName(startOpPos), 10, 3);
                break;
            }
            default: {
                this.error("ER_UNKNOWN_MATCH_OPERATION", null);
                return null;
            }
        }
        pattern.setPredicates(this.getCompiledPredicates(opPos + argLen));
        if (null != ancestorPattern) {
            pattern.setRelativePathPattern(ancestorPattern);
        }
        StepPattern relativePathPattern = this.stepPattern(endStep, stepCount + 1, pattern);
        return null != relativePathPattern ? relativePathPattern : pattern;
    }

    public Expression[] getCompiledPredicates(int opPos) throws TransformerException {
        int count = this.countPredicates(opPos);
        if (count > 0) {
            Expression[] predicates = new Expression[count];
            this.compilePredicates(opPos, predicates);
            return predicates;
        }
        return null;
    }

    public int countPredicates(int opPos) throws TransformerException {
        int count = 0;
        while (29 == this.getOp(opPos)) {
            ++count;
            opPos = this.getNextOpPos(opPos);
        }
        return count;
    }

    private void compilePredicates(int opPos, Expression[] predicates) throws TransformerException {
        int i = 0;
        while (29 == this.getOp(opPos)) {
            predicates[i] = this.predicate(opPos);
            opPos = this.getNextOpPos(opPos);
            ++i;
        }
    }

    Expression compileFunction(int opPos) throws TransformerException {
        int endFunc = opPos + this.getOp(opPos + 1) - 1;
        opPos = Compiler.getFirstChildPos(opPos);
        int funcID = this.getOp(opPos);
        ++opPos;
        if (-1 != funcID) {
            Function func = this.m_functionTable.getFunction(funcID);
            if (func instanceof FuncExtFunctionAvailable) {
                ((FuncExtFunctionAvailable)func).setFunctionTable(this.m_functionTable);
            }
            func.postCompileStep(this);
            try {
                int i = 0;
                int p = opPos;
                while (p < endFunc) {
                    func.setArg(this.compile(p), i);
                    p = this.getNextOpPos(p);
                    ++i;
                }
                func.checkNumberArgs(i);
            }
            catch (WrongNumberArgsException wnae) {
                String name = this.m_functionTable.getFunctionName(funcID);
                this.m_errorHandler.fatalError(new TransformerException(XSLMessages.createXPATHMessage("ER_ONLY_ALLOWS", new Object[]{name, wnae.getMessage()}), this.m_locator));
            }
            return func;
        }
        this.error("ER_FUNCTION_TOKEN_NOT_FOUND", null);
        return null;
    }

    private synchronized long getNextMethodId() {
        if (s_nextMethodId == Long.MAX_VALUE) {
            s_nextMethodId = 0L;
        }
        return s_nextMethodId++;
    }

    private Expression compileExtension(int opPos) throws TransformerException {
        int endExtFunc = opPos + this.getOp(opPos + 1) - 1;
        opPos = Compiler.getFirstChildPos(opPos);
        String ns = (String)this.getTokenQueue().elementAt(this.getOp(opPos));
        String funcName = (String)this.getTokenQueue().elementAt(this.getOp(++opPos));
        ++opPos;
        FuncExtFunction extension = new FuncExtFunction(ns, funcName, String.valueOf(this.getNextMethodId()));
        try {
            int i = 0;
            while (opPos < endExtFunc) {
                int nextOpPos = this.getNextOpPos(opPos);
                ((Function)extension).setArg(this.compile(opPos), i);
                opPos = nextOpPos;
                ++i;
            }
        }
        catch (WrongNumberArgsException wrongNumberArgsException) {
            // empty catch block
        }
        return extension;
    }

    public void warn(String msg, Object[] args) throws TransformerException {
        String fmsg = XSLMessages.createXPATHWarning(msg, args);
        if (null != this.m_errorHandler) {
            this.m_errorHandler.warning(new TransformerException(fmsg, this.m_locator));
        } else {
            System.out.println(fmsg + "; file " + this.m_locator.getSystemId() + "; line " + this.m_locator.getLineNumber() + "; column " + this.m_locator.getColumnNumber());
        }
    }

    public void assertion(boolean b, String msg) {
        if (!b) {
            String fMsg = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[]{msg});
            throw new RuntimeException(fMsg);
        }
    }

    @Override
    public void error(String msg, Object[] args) throws TransformerException {
        String fmsg = XSLMessages.createXPATHMessage(msg, args);
        if (null == this.m_errorHandler) {
            throw new TransformerException(fmsg, (SAXSourceLocator)this.m_locator);
        }
        this.m_errorHandler.fatalError(new TransformerException(fmsg, this.m_locator));
    }

    public PrefixResolver getNamespaceContext() {
        return this.m_currentPrefixResolver;
    }

    public void setNamespaceContext(PrefixResolver pr) {
        this.m_currentPrefixResolver = pr;
    }
}


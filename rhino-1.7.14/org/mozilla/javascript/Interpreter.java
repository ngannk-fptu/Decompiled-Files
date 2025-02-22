/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.mozilla.javascript.ArrowFunction;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.CodeGenerator;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.ConstProperties;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.ES6Generator;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EqualObjectGraphs;
import org.mozilla.javascript.Evaluator;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Icode;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.InterpretedFunction;
import org.mozilla.javascript.InterpreterData;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.NativeContinuation;
import org.mozilla.javascript.NativeGenerator;
import org.mozilla.javascript.NativeIterator;
import org.mozilla.javascript.NativeWith;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.SecurityUtilities;
import org.mozilla.javascript.UintMap;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.debug.DebugFrame;

public final class Interpreter
extends Icode
implements Evaluator {
    InterpreterData itsData;
    static final int EXCEPTION_TRY_START_SLOT = 0;
    static final int EXCEPTION_TRY_END_SLOT = 1;
    static final int EXCEPTION_HANDLER_SLOT = 2;
    static final int EXCEPTION_TYPE_SLOT = 3;
    static final int EXCEPTION_LOCAL_SLOT = 4;
    static final int EXCEPTION_SCOPE_SLOT = 5;
    static final int EXCEPTION_SLOT_SIZE = 6;

    private static boolean compareIdata(InterpreterData i1, InterpreterData i2) {
        return i1 == i2 || Objects.equals(Interpreter.getEncodedSource(i1), Interpreter.getEncodedSource(i2));
    }

    private static CallFrame captureFrameForGenerator(CallFrame frame) {
        frame.frozen = true;
        CallFrame result = frame.cloneFrozen();
        frame.frozen = false;
        result.parentFrame = null;
        result.frameIndex = 0;
        return result;
    }

    @Override
    public Object compile(CompilerEnvirons compilerEnv, ScriptNode tree, String encodedSource, boolean returnFunction) {
        CodeGenerator cgen = new CodeGenerator();
        this.itsData = cgen.compile(compilerEnv, tree, encodedSource, returnFunction);
        return this.itsData;
    }

    @Override
    public Script createScriptObject(Object bytecode, Object staticSecurityDomain) {
        if (bytecode != this.itsData) {
            Kit.codeBug();
        }
        return InterpretedFunction.createScript(this.itsData, staticSecurityDomain);
    }

    @Override
    public void setEvalScriptFlag(Script script) {
        ((InterpretedFunction)script).idata.evalScriptFlag = true;
    }

    @Override
    public Function createFunctionObject(Context cx, Scriptable scope, Object bytecode, Object staticSecurityDomain) {
        if (bytecode != this.itsData) {
            Kit.codeBug();
        }
        return InterpretedFunction.createFunction(cx, scope, this.itsData, staticSecurityDomain);
    }

    private static int getShort(byte[] iCode, int pc) {
        return iCode[pc] << 8 | iCode[pc + 1] & 0xFF;
    }

    private static int getIndex(byte[] iCode, int pc) {
        return (iCode[pc] & 0xFF) << 8 | iCode[pc + 1] & 0xFF;
    }

    private static int getInt(byte[] iCode, int pc) {
        return iCode[pc] << 24 | (iCode[pc + 1] & 0xFF) << 16 | (iCode[pc + 2] & 0xFF) << 8 | iCode[pc + 3] & 0xFF;
    }

    private static int getExceptionHandler(CallFrame frame, boolean onlyFinally) {
        int[] exceptionTable = frame.idata.itsExceptionTable;
        if (exceptionTable == null) {
            return -1;
        }
        int pc = frame.pc - 1;
        int best = -1;
        int bestStart = 0;
        int bestEnd = 0;
        for (int i = 0; i != exceptionTable.length; i += 6) {
            int start = exceptionTable[i + 0];
            int end = exceptionTable[i + 1];
            if (start > pc || pc >= end || onlyFinally && exceptionTable[i + 3] != 1) continue;
            if (best >= 0) {
                if (bestEnd < end) continue;
                if (bestStart > start) {
                    Kit.codeBug();
                }
                if (bestEnd == end) {
                    Kit.codeBug();
                }
            }
            best = i;
            bestStart = start;
            bestEnd = end;
        }
        return best;
    }

    static void dumpICode(InterpreterData idata) {
    }

    private static int bytecodeSpan(int bytecode) {
        switch (bytecode) {
            case -66: 
            case -65: 
            case -63: 
            case -62: 
            case 50: 
            case 73: {
                return 3;
            }
            case -54: 
            case -23: 
            case -6: 
            case 5: 
            case 6: 
            case 7: {
                return 3;
            }
            case -21: {
                return 5;
            }
            case 57: {
                return 2;
            }
            case -11: 
            case -10: 
            case -9: 
            case -8: 
            case -7: {
                return 2;
            }
            case -27: {
                return 3;
            }
            case -28: {
                return 5;
            }
            case -38: {
                return 2;
            }
            case -39: {
                return 3;
            }
            case -40: {
                return 5;
            }
            case -45: {
                return 2;
            }
            case -46: {
                return 3;
            }
            case -47: {
                return 5;
            }
            case -61: 
            case -49: 
            case -48: {
                return 2;
            }
            case -26: {
                return 3;
            }
        }
        if (!Interpreter.validBytecode(bytecode)) {
            throw Kit.codeBug();
        }
        return 1;
    }

    static int[] getLineNumbers(InterpreterData data) {
        int span;
        UintMap presentLines = new UintMap();
        byte[] iCode = data.itsICode;
        int iCodeLength = iCode.length;
        for (int pc = 0; pc != iCodeLength; pc += span) {
            byte bytecode = iCode[pc];
            span = Interpreter.bytecodeSpan(bytecode);
            if (bytecode != -26) continue;
            if (span != 3) {
                Kit.codeBug();
            }
            int line = Interpreter.getIndex(iCode, pc + 1);
            presentLines.put(line, 0);
        }
        return presentLines.getKeys();
    }

    @Override
    public void captureStackInfo(RhinoException ex) {
        Object[] array;
        Context cx = Context.getCurrentContext();
        if (cx == null || cx.lastInterpreterFrame == null) {
            ex.interpreterStackInfo = null;
            ex.interpreterLineData = null;
            return;
        }
        if (cx.previousInterpreterInvocations == null || cx.previousInterpreterInvocations.size() == 0) {
            array = new CallFrame[1];
        } else {
            int previousCount = cx.previousInterpreterInvocations.size();
            if (cx.previousInterpreterInvocations.peek() == cx.lastInterpreterFrame) {
                --previousCount;
            }
            array = new CallFrame[previousCount + 1];
            cx.previousInterpreterInvocations.toArray(array);
        }
        array[array.length - 1] = (CallFrame)cx.lastInterpreterFrame;
        int interpreterFrameCount = 0;
        for (int i = 0; i != array.length; ++i) {
            interpreterFrameCount += 1 + ((CallFrame)array[i]).frameIndex;
        }
        int[] linePC = new int[interpreterFrameCount];
        int linePCIndex = interpreterFrameCount;
        int i = array.length;
        while (i != 0) {
            Object frame = array[--i];
            while (frame != null) {
                linePC[--linePCIndex] = ((CallFrame)frame).pcSourceLineStart;
                frame = ((CallFrame)frame).parentFrame;
            }
        }
        if (linePCIndex != 0) {
            Kit.codeBug();
        }
        ex.interpreterStackInfo = array;
        ex.interpreterLineData = linePC;
    }

    @Override
    public String getSourcePositionFromStack(Context cx, int[] linep) {
        CallFrame frame = (CallFrame)cx.lastInterpreterFrame;
        InterpreterData idata = frame.idata;
        linep[0] = frame.pcSourceLineStart >= 0 ? Interpreter.getIndex(idata.itsICode, frame.pcSourceLineStart) : 0;
        return idata.itsSourceFile;
    }

    @Override
    public String getPatchedStack(RhinoException ex, String nativeStackTrace) {
        String tag = "org.mozilla.javascript.Interpreter.interpretLoop";
        StringBuilder sb = new StringBuilder(nativeStackTrace.length() + 1000);
        String lineSeparator = SecurityUtilities.getSystemProperty("line.separator");
        CallFrame[] array = (CallFrame[])ex.interpreterStackInfo;
        int[] linePC = ex.interpreterLineData;
        int arrayIndex = array.length;
        int linePCIndex = linePC.length;
        int offset = 0;
        while (arrayIndex != 0) {
            char c;
            --arrayIndex;
            int pos = nativeStackTrace.indexOf(tag, offset);
            if (pos < 0) break;
            pos += tag.length();
            while (pos != nativeStackTrace.length() && (c = nativeStackTrace.charAt(pos)) != '\n' && c != '\r') {
                ++pos;
            }
            sb.append(nativeStackTrace.substring(offset, pos));
            offset = pos;
            CallFrame frame = array[arrayIndex];
            while (frame != null) {
                if (linePCIndex == 0) {
                    Kit.codeBug();
                }
                --linePCIndex;
                InterpreterData idata = frame.idata;
                sb.append(lineSeparator);
                sb.append("\tat script");
                if (idata.itsName != null && idata.itsName.length() != 0) {
                    sb.append('.');
                    sb.append(idata.itsName);
                }
                sb.append('(');
                sb.append(idata.itsSourceFile);
                int pc = linePC[linePCIndex];
                if (pc >= 0) {
                    sb.append(':');
                    sb.append(Interpreter.getIndex(idata.itsICode, pc));
                }
                sb.append(')');
                frame = frame.parentFrame;
            }
        }
        sb.append(nativeStackTrace.substring(offset));
        return sb.toString();
    }

    @Override
    public List<String> getScriptStack(RhinoException ex) {
        ScriptStackElement[][] stack = this.getScriptStackElements(ex);
        ArrayList<String> list = new ArrayList<String>(stack.length);
        String lineSeparator = SecurityUtilities.getSystemProperty("line.separator");
        for (ScriptStackElement[] group : stack) {
            StringBuilder sb = new StringBuilder();
            for (ScriptStackElement elem : group) {
                elem.renderJavaStyle(sb);
                sb.append(lineSeparator);
            }
            list.add(sb.toString());
        }
        return list;
    }

    public ScriptStackElement[][] getScriptStackElements(RhinoException ex) {
        if (ex.interpreterStackInfo == null) {
            return null;
        }
        ArrayList<ScriptStackElement[]> list = new ArrayList<ScriptStackElement[]>();
        CallFrame[] array = (CallFrame[])ex.interpreterStackInfo;
        int[] linePC = ex.interpreterLineData;
        int arrayIndex = array.length;
        int linePCIndex = linePC.length;
        while (arrayIndex != 0) {
            CallFrame frame = array[--arrayIndex];
            ArrayList<ScriptStackElement> group = new ArrayList<ScriptStackElement>();
            while (frame != null) {
                int pc;
                if (linePCIndex == 0) {
                    Kit.codeBug();
                }
                InterpreterData idata = frame.idata;
                String fileName = idata.itsSourceFile;
                String functionName = null;
                int lineNumber = -1;
                if ((pc = linePC[--linePCIndex]) >= 0) {
                    lineNumber = Interpreter.getIndex(idata.itsICode, pc);
                }
                if (idata.itsName != null && idata.itsName.length() != 0) {
                    functionName = idata.itsName;
                }
                frame = frame.parentFrame;
                group.add(new ScriptStackElement(fileName, functionName, lineNumber));
            }
            list.add(group.toArray(new ScriptStackElement[group.size()]));
        }
        return (ScriptStackElement[][])list.toArray((T[])new ScriptStackElement[list.size()][]);
    }

    static String getEncodedSource(InterpreterData idata) {
        if (idata.encodedSource == null) {
            return null;
        }
        return idata.encodedSource.substring(idata.encodedSourceStart, idata.encodedSourceEnd);
    }

    private static void initFunction(Context cx, Scriptable scope, InterpretedFunction parent, int index) {
        InterpretedFunction fn = InterpretedFunction.createFunction(cx, scope, parent, index);
        ScriptRuntime.initFunction(cx, scope, fn, fn.idata.itsFunctionType, parent.idata.evalScriptFlag);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Object interpret(InterpretedFunction ifun, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!ScriptRuntime.hasTopCall(cx)) {
            Kit.codeBug();
        }
        if (cx.interpreterSecurityDomain != ifun.securityDomain) {
            Object savedDomain = cx.interpreterSecurityDomain;
            cx.interpreterSecurityDomain = ifun.securityDomain;
            try {
                Object object = ifun.securityController.callWithDomain(ifun.securityDomain, cx, ifun, scope, thisObj, args);
                return object;
            }
            finally {
                cx.interpreterSecurityDomain = savedDomain;
            }
        }
        CallFrame frame = Interpreter.initFrame(cx, scope, thisObj, args, null, 0, args.length, ifun, null);
        frame.isContinuationsTopFrame = cx.isContinuationsTopCall;
        cx.isContinuationsTopCall = false;
        return Interpreter.interpretLoop(cx, frame, null);
    }

    public static Object resumeGenerator(Context cx, Scriptable scope, int operation, Object savedState, Object value) {
        CallFrame frame = (CallFrame)savedState;
        GeneratorState generatorState = new GeneratorState(operation, value);
        if (operation == 2) {
            try {
                return Interpreter.interpretLoop(cx, frame, generatorState);
            }
            catch (RuntimeException e) {
                if (e != value) {
                    throw e;
                }
                return Undefined.instance;
            }
        }
        Object result = Interpreter.interpretLoop(cx, frame, generatorState);
        if (generatorState.returnedException != null) {
            throw generatorState.returnedException;
        }
        return result;
    }

    public static Object restartContinuation(NativeContinuation c, Context cx, Scriptable scope, Object[] args) {
        if (!ScriptRuntime.hasTopCall(cx)) {
            return ScriptRuntime.doTopCall(c, cx, scope, null, args, cx.isTopLevelStrict);
        }
        Object arg = args.length == 0 ? Undefined.instance : args[0];
        CallFrame capturedFrame = (CallFrame)c.getImplementation();
        if (capturedFrame == null) {
            return arg;
        }
        ContinuationJump cjump = new ContinuationJump(c, null);
        cjump.result = arg;
        return Interpreter.interpretLoop(cx, null, cjump);
    }

    private static Object interpretLoop(Context cx, CallFrame frame, Object throwable) {
        double interpreterResultDbl;
        Object interpreterResult;
        UniqueTag DBL_MRK;
        block242: {
            ContinuationJump cjump;
            DBL_MRK = UniqueTag.DOUBLE_MARK;
            Object undefined = Undefined.instance;
            boolean instructionCounting = cx.instructionThreshold != 0;
            int INVOCATION_COST = 100;
            int EXCEPTION_COST = 100;
            String stringReg = null;
            BigInteger bigIntReg = null;
            int indexReg = -1;
            if (cx.lastInterpreterFrame != null) {
                if (cx.previousInterpreterInvocations == null) {
                    cx.previousInterpreterInvocations = new ObjArray();
                }
                cx.previousInterpreterInvocations.push(cx.lastInterpreterFrame);
            }
            GeneratorState generatorState = null;
            if (throwable != null) {
                if (throwable instanceof GeneratorState) {
                    generatorState = (GeneratorState)throwable;
                    Interpreter.enterFrame(cx, frame, ScriptRuntime.emptyArgs, true);
                    throwable = null;
                } else if (!(throwable instanceof ContinuationJump)) {
                    Kit.codeBug();
                }
            }
            interpreterResult = null;
            interpreterResultDbl = 0.0;
            block141: while (true) {
                block243: {
                    int exState;
                    block241: {
                        try {
                            block142: while (true) {
                                if (throwable != null) {
                                    frame = Interpreter.processThrowable(cx, throwable, frame, indexReg, instructionCounting);
                                    throwable = frame.throwable;
                                    frame.throwable = null;
                                } else if (generatorState == null && frame.frozen) {
                                    Kit.codeBug();
                                }
                                Object[] stack = frame.stack;
                                double[] sDbl = frame.sDbl;
                                Object[] vars = frame.varSource.stack;
                                double[] varDbls = frame.varSource.sDbl;
                                int[] varAttributes = frame.varSource.stackAttributes;
                                byte[] iCode = frame.idata.itsICode;
                                String[] strings = frame.idata.itsStringTable;
                                BigInteger[] bigInts = frame.idata.itsBigIntTable;
                                int stackTop = frame.savedStackTop;
                                cx.lastInterpreterFrame = frame;
                                block143: while (true) {
                                    int offset;
                                    int op = iCode[frame.pc++];
                                    switch (op) {
                                        case -62: {
                                            if (!frame.frozen) {
                                                --frame.pc;
                                                CallFrame generatorFrame = Interpreter.captureFrameForGenerator(frame);
                                                generatorFrame.frozen = true;
                                                if (cx.getLanguageVersion() >= 200) {
                                                    frame.result = new ES6Generator(frame.scope, generatorFrame.fnOrScript, generatorFrame);
                                                    break block143;
                                                }
                                                frame.result = new NativeGenerator(frame.scope, generatorFrame.fnOrScript, generatorFrame);
                                                break block143;
                                            }
                                        }
                                        case -66: 
                                        case 73: {
                                            if (!frame.frozen) {
                                                return Interpreter.freezeGenerator(cx, frame, stackTop, generatorState, op == -66);
                                            }
                                            Object obj = Interpreter.thawGenerator(frame, stackTop, generatorState, op);
                                            if (obj == Scriptable.NOT_FOUND) continue block143;
                                            throwable = obj;
                                            break block241;
                                        }
                                        case -63: {
                                            frame.frozen = true;
                                            int sourceLine = Interpreter.getIndex(iCode, frame.pc);
                                            generatorState.returnedException = new JavaScriptException(NativeIterator.getStopIterationObject(frame.scope), frame.idata.itsSourceFile, sourceLine);
                                            break block143;
                                        }
                                        case -65: {
                                            frame.frozen = true;
                                            frame.result = stack[stackTop];
                                            frame.resultDbl = sDbl[stackTop];
                                            --stackTop;
                                            NativeIterator.StopIteration si = new NativeIterator.StopIteration(frame.result == UniqueTag.DOUBLE_MARK ? Double.valueOf(frame.resultDbl) : frame.result);
                                            int sourceLine = Interpreter.getIndex(iCode, frame.pc);
                                            generatorState.returnedException = new JavaScriptException(si, frame.idata.itsSourceFile, sourceLine);
                                            break block143;
                                        }
                                        case 50: {
                                            Object value = stack[stackTop];
                                            if (value == DBL_MRK) {
                                                value = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            --stackTop;
                                            int sourceLine = Interpreter.getIndex(iCode, frame.pc);
                                            throwable = new JavaScriptException(value, frame.idata.itsSourceFile, sourceLine);
                                            break block241;
                                        }
                                        case 51: {
                                            throwable = stack[indexReg += frame.localShift];
                                            break block241;
                                        }
                                        case 14: 
                                        case 15: 
                                        case 16: 
                                        case 17: {
                                            stackTop = Interpreter.doCompare(frame, op, stack, sDbl, stackTop);
                                            continue block143;
                                        }
                                        case 52: 
                                        case 53: {
                                            stackTop = Interpreter.doInOrInstanceof(cx, op, stack, sDbl, stackTop);
                                            continue block143;
                                        }
                                        case 12: 
                                        case 13: {
                                            boolean valBln = Interpreter.doEquals(stack, sDbl, --stackTop);
                                            stack[stackTop] = ScriptRuntime.wrapBoolean(valBln ^= op == 13);
                                            continue block143;
                                        }
                                        case 46: 
                                        case 47: {
                                            boolean valBln = Interpreter.doShallowEquals(stack, sDbl, --stackTop);
                                            stack[stackTop] = ScriptRuntime.wrapBoolean(valBln ^= op == 47);
                                            continue block143;
                                        }
                                        case 7: {
                                            if (!Interpreter.stack_boolean(frame, stackTop--)) break;
                                            frame.pc += 2;
                                            continue block143;
                                        }
                                        case 6: {
                                            if (Interpreter.stack_boolean(frame, stackTop--)) break;
                                            frame.pc += 2;
                                            continue block143;
                                        }
                                        case -6: {
                                            if (!Interpreter.stack_boolean(frame, stackTop--)) {
                                                frame.pc += 2;
                                                continue block143;
                                            }
                                            stack[stackTop--] = null;
                                            break;
                                        }
                                        case 5: {
                                            break;
                                        }
                                        case -23: {
                                            stack[++stackTop] = DBL_MRK;
                                            sDbl[stackTop] = frame.pc + 2;
                                            break;
                                        }
                                        case -24: {
                                            if (stackTop == frame.emptyStackTop + 1) {
                                                stack[indexReg += frame.localShift] = stack[stackTop];
                                                sDbl[indexReg] = sDbl[stackTop];
                                                --stackTop;
                                                continue block143;
                                            }
                                            if (stackTop == frame.emptyStackTop) continue block143;
                                            Kit.codeBug();
                                            continue block143;
                                        }
                                        case -25: {
                                            Object value;
                                            if (instructionCounting) {
                                                Interpreter.addInstructionCount(cx, frame, 0);
                                            }
                                            if ((value = stack[indexReg += frame.localShift]) != DBL_MRK) {
                                                throwable = value;
                                                break block241;
                                            }
                                            frame.pc = (int)sDbl[indexReg];
                                            if (!instructionCounting) continue block143;
                                            frame.pcPrevBranch = frame.pc;
                                            continue block143;
                                        }
                                        case -4: {
                                            stack[stackTop] = null;
                                            --stackTop;
                                            continue block143;
                                        }
                                        case -5: {
                                            frame.result = stack[stackTop];
                                            frame.resultDbl = sDbl[stackTop];
                                            stack[stackTop] = null;
                                            --stackTop;
                                            continue block143;
                                        }
                                        case -1: {
                                            stack[stackTop + 1] = stack[stackTop];
                                            sDbl[stackTop + 1] = sDbl[stackTop];
                                            ++stackTop;
                                            continue block143;
                                        }
                                        case -2: {
                                            stack[stackTop + 1] = stack[stackTop - 1];
                                            sDbl[stackTop + 1] = sDbl[stackTop - 1];
                                            stack[stackTop + 2] = stack[stackTop];
                                            sDbl[stackTop + 2] = sDbl[stackTop];
                                            stackTop += 2;
                                            continue block143;
                                        }
                                        case -3: {
                                            Object o = stack[stackTop];
                                            stack[stackTop] = stack[stackTop - 1];
                                            stack[stackTop - 1] = o;
                                            double d = sDbl[stackTop];
                                            sDbl[stackTop] = sDbl[stackTop - 1];
                                            sDbl[stackTop - 1] = d;
                                            continue block143;
                                        }
                                        case 4: {
                                            frame.result = stack[stackTop];
                                            frame.resultDbl = sDbl[stackTop];
                                            --stackTop;
                                            break block143;
                                        }
                                        case 65: {
                                            break block143;
                                        }
                                        case -22: {
                                            frame.result = undefined;
                                            break block143;
                                        }
                                        case 27: {
                                            stackTop = Interpreter.doBitNOT(frame, stack, sDbl, stackTop);
                                            continue block143;
                                        }
                                        case 9: 
                                        case 10: 
                                        case 11: 
                                        case 18: 
                                        case 19: {
                                            stackTop = Interpreter.doBitOp(frame, op, stack, sDbl, stackTop);
                                            continue block143;
                                        }
                                        case 20: {
                                            double lDbl = Interpreter.stack_double(frame, stackTop - 1);
                                            int rIntValue = Interpreter.stack_int32(frame, stackTop) & 0x1F;
                                            stack[--stackTop] = DBL_MRK;
                                            sDbl[stackTop] = ScriptRuntime.toUint32(lDbl) >>> rIntValue;
                                            continue block143;
                                        }
                                        case 28: {
                                            double rDbl = Interpreter.stack_double(frame, stackTop);
                                            stack[stackTop] = DBL_MRK;
                                            sDbl[stackTop] = rDbl;
                                            continue block143;
                                        }
                                        case 29: {
                                            Number rNum = Interpreter.stack_numeric(frame, stackTop);
                                            Number rNegNum = ScriptRuntime.negate(rNum);
                                            if (rNegNum instanceof BigInteger) {
                                                stack[stackTop] = rNegNum;
                                                continue block143;
                                            }
                                            stack[stackTop] = DBL_MRK;
                                            sDbl[stackTop] = rNegNum.doubleValue();
                                            continue block143;
                                        }
                                        case 21: {
                                            Interpreter.doAdd(stack, sDbl, --stackTop, cx);
                                            continue block143;
                                        }
                                        case 22: 
                                        case 23: 
                                        case 24: 
                                        case 25: 
                                        case 75: {
                                            stackTop = Interpreter.doArithmetic(frame, op, stack, sDbl, stackTop);
                                            continue block143;
                                        }
                                        case 26: {
                                            stack[stackTop] = ScriptRuntime.wrapBoolean(!Interpreter.stack_boolean(frame, stackTop));
                                            continue block143;
                                        }
                                        case 49: {
                                            stack[++stackTop] = ScriptRuntime.bind(cx, frame.scope, stringReg);
                                            continue block143;
                                        }
                                        case 8: 
                                        case 74: {
                                            Object rhs = stack[stackTop];
                                            if (rhs == DBL_MRK) {
                                                rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            Scriptable lhs = (Scriptable)stack[--stackTop];
                                            stack[stackTop] = op == 8 ? ScriptRuntime.setName(lhs, rhs, cx, frame.scope, stringReg) : ScriptRuntime.strictSetName(lhs, rhs, cx, frame.scope, stringReg);
                                            continue block143;
                                        }
                                        case -59: {
                                            Object rhs = stack[stackTop];
                                            if (rhs == DBL_MRK) {
                                                rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            Scriptable lhs = (Scriptable)stack[--stackTop];
                                            stack[stackTop] = ScriptRuntime.setConst(lhs, rhs, cx, stringReg);
                                            continue block143;
                                        }
                                        case 0: 
                                        case 31: {
                                            stackTop = Interpreter.doDelName(cx, frame, op, stack, sDbl, stackTop);
                                            continue block143;
                                        }
                                        case 34: {
                                            Object lhs = stack[stackTop];
                                            if (lhs == DBL_MRK) {
                                                lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.getObjectPropNoWarn(lhs, stringReg, cx, frame.scope);
                                            continue block143;
                                        }
                                        case 33: {
                                            Object lhs = stack[stackTop];
                                            if (lhs == DBL_MRK) {
                                                lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.getObjectProp(lhs, stringReg, cx, frame.scope);
                                            continue block143;
                                        }
                                        case 35: {
                                            Object lhs;
                                            Object rhs = stack[stackTop];
                                            if (rhs == DBL_MRK) {
                                                rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            if ((lhs = stack[--stackTop]) == DBL_MRK) {
                                                lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.setObjectProp(lhs, stringReg, rhs, cx, frame.scope);
                                            continue block143;
                                        }
                                        case -9: {
                                            Object lhs = stack[stackTop];
                                            if (lhs == DBL_MRK) {
                                                lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.propIncrDecr(lhs, stringReg, cx, frame.scope, iCode[frame.pc]);
                                            ++frame.pc;
                                            continue block143;
                                        }
                                        case 36: {
                                            stackTop = Interpreter.doGetElem(cx, frame, stack, sDbl, stackTop);
                                            continue block143;
                                        }
                                        case 37: {
                                            stackTop = Interpreter.doSetElem(cx, frame, stack, sDbl, stackTop);
                                            continue block143;
                                        }
                                        case -10: {
                                            stackTop = Interpreter.doElemIncDec(cx, frame, iCode, stack, sDbl, stackTop);
                                            continue block143;
                                        }
                                        case 68: {
                                            Ref ref = (Ref)stack[stackTop];
                                            stack[stackTop] = ScriptRuntime.refGet(ref, cx);
                                            continue block143;
                                        }
                                        case 69: {
                                            Object value = stack[stackTop];
                                            if (value == DBL_MRK) {
                                                value = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            Ref ref = (Ref)stack[--stackTop];
                                            stack[stackTop] = ScriptRuntime.refSet(ref, value, cx, frame.scope);
                                            continue block143;
                                        }
                                        case 70: {
                                            Ref ref = (Ref)stack[stackTop];
                                            stack[stackTop] = ScriptRuntime.refDel(ref, cx);
                                            continue block143;
                                        }
                                        case -11: {
                                            Ref ref = (Ref)stack[stackTop];
                                            stack[stackTop] = ScriptRuntime.refIncrDecr(ref, cx, frame.scope, iCode[frame.pc]);
                                            ++frame.pc;
                                            continue block143;
                                        }
                                        case 54: {
                                            stack[++stackTop] = stack[indexReg += frame.localShift];
                                            sDbl[stackTop] = sDbl[indexReg];
                                            continue block143;
                                        }
                                        case -56: {
                                            stack[indexReg += frame.localShift] = null;
                                            continue block143;
                                        }
                                        case -15: {
                                            stack[++stackTop] = ScriptRuntime.getNameFunctionAndThis(stringReg, cx, frame.scope);
                                            stack[++stackTop] = ScriptRuntime.lastStoredScriptable(cx);
                                            continue block143;
                                        }
                                        case -16: {
                                            Object obj = stack[stackTop];
                                            if (obj == DBL_MRK) {
                                                obj = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.getPropFunctionAndThis(obj, stringReg, cx, frame.scope);
                                            stack[++stackTop] = ScriptRuntime.lastStoredScriptable(cx);
                                            continue block143;
                                        }
                                        case -17: {
                                            Object id;
                                            Object obj = stack[stackTop - 1];
                                            if (obj == DBL_MRK) {
                                                obj = ScriptRuntime.wrapNumber(sDbl[stackTop - 1]);
                                            }
                                            if ((id = stack[stackTop]) == DBL_MRK) {
                                                id = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop - 1] = ScriptRuntime.getElemFunctionAndThis(obj, id, cx, frame.scope);
                                            stack[stackTop] = ScriptRuntime.lastStoredScriptable(cx);
                                            continue block143;
                                        }
                                        case -18: {
                                            Object value = stack[stackTop];
                                            if (value == DBL_MRK) {
                                                value = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.getValueFunctionAndThis(value, cx);
                                            stack[++stackTop] = ScriptRuntime.lastStoredScriptable(cx);
                                            continue block143;
                                        }
                                        case -21: {
                                            if (instructionCounting) {
                                                cx.instructionCount += 100;
                                            }
                                            stackTop = Interpreter.doCallSpecial(cx, frame, stack, sDbl, stackTop, iCode, indexReg);
                                            continue block143;
                                        }
                                        case -55: 
                                        case 38: 
                                        case 71: {
                                            BaseFunction ifun;
                                            if (instructionCounting) {
                                                cx.instructionCount += 100;
                                            }
                                            Callable fun = (Callable)stack[stackTop -= 1 + indexReg];
                                            Scriptable funThisObj = (Scriptable)stack[stackTop + 1];
                                            if (op == 71) {
                                                Object[] outArgs = Interpreter.getArgsArray(stack, sDbl, stackTop + 2, indexReg);
                                                stack[stackTop] = ScriptRuntime.callRef(fun, funThisObj, outArgs, cx);
                                                continue block143;
                                            }
                                            Scriptable calleeScope = frame.scope;
                                            if (frame.useActivation) {
                                                calleeScope = ScriptableObject.getTopLevelScope(frame.scope);
                                            }
                                            if (fun instanceof InterpretedFunction) {
                                                ifun = (InterpretedFunction)fun;
                                                if (frame.fnOrScript.securityDomain == ifun.securityDomain) {
                                                    CallFrame callParentFrame = frame;
                                                    if (op == -55) {
                                                        callParentFrame = frame.parentFrame;
                                                        Interpreter.exitFrame(cx, frame, null);
                                                    }
                                                    CallFrame calleeFrame = Interpreter.initFrame(cx, calleeScope, funThisObj, stack, sDbl, stackTop + 2, indexReg, ifun, callParentFrame);
                                                    if (op != -55) {
                                                        frame.savedStackTop = stackTop;
                                                        frame.savedCallOp = op;
                                                    }
                                                    frame = calleeFrame;
                                                    continue block142;
                                                }
                                            }
                                            if (fun instanceof NativeContinuation) {
                                                ContinuationJump cjump2 = new ContinuationJump((NativeContinuation)fun, frame);
                                                if (indexReg == 0) {
                                                    cjump2.result = undefined;
                                                } else {
                                                    cjump2.result = stack[stackTop + 2];
                                                    cjump2.resultDbl = sDbl[stackTop + 2];
                                                }
                                                throwable = cjump2;
                                                break block241;
                                            }
                                            if (fun instanceof IdFunctionObject) {
                                                Callable applyCallable;
                                                ifun = (IdFunctionObject)fun;
                                                if (NativeContinuation.isContinuationConstructor((IdFunctionObject)ifun)) {
                                                    frame.stack[stackTop] = Interpreter.captureContinuation(cx, frame.parentFrame, false);
                                                    continue block143;
                                                }
                                                if (BaseFunction.isApplyOrCall((IdFunctionObject)ifun) && (applyCallable = ScriptRuntime.getCallable(funThisObj)) instanceof InterpretedFunction) {
                                                    InterpretedFunction iApplyCallable = (InterpretedFunction)applyCallable;
                                                    if (frame.fnOrScript.securityDomain == iApplyCallable.securityDomain) {
                                                        frame = Interpreter.initFrameForApplyOrCall(cx, frame, indexReg, stack, sDbl, stackTop, op, calleeScope, (IdFunctionObject)ifun, iApplyCallable);
                                                        continue block142;
                                                    }
                                                }
                                            }
                                            if (fun instanceof ScriptRuntime.NoSuchMethodShim) {
                                                ScriptRuntime.NoSuchMethodShim noSuchMethodShim = (ScriptRuntime.NoSuchMethodShim)fun;
                                                Callable noSuchMethodMethod = noSuchMethodShim.noSuchMethodMethod;
                                                if (noSuchMethodMethod instanceof InterpretedFunction) {
                                                    InterpretedFunction ifun2 = (InterpretedFunction)noSuchMethodMethod;
                                                    if (frame.fnOrScript.securityDomain == ifun2.securityDomain) {
                                                        frame = Interpreter.initFrameForNoSuchMethod(cx, frame, indexReg, stack, sDbl, stackTop, op, funThisObj, calleeScope, noSuchMethodShim, ifun2);
                                                        continue block142;
                                                    }
                                                }
                                            }
                                            cx.lastInterpreterFrame = frame;
                                            frame.savedCallOp = op;
                                            frame.savedStackTop = stackTop;
                                            stack[stackTop] = fun.call(cx, calleeScope, funThisObj, Interpreter.getArgsArray(stack, sDbl, stackTop + 2, indexReg));
                                            continue block143;
                                        }
                                        case 30: {
                                            IdFunctionObject ifun;
                                            Object lhs;
                                            if (instructionCounting) {
                                                cx.instructionCount += 100;
                                            }
                                            if ((lhs = stack[stackTop -= indexReg]) instanceof InterpretedFunction) {
                                                InterpretedFunction f = (InterpretedFunction)lhs;
                                                if (frame.fnOrScript.securityDomain == f.securityDomain) {
                                                    Scriptable newInstance = f.createObject(cx, frame.scope);
                                                    CallFrame calleeFrame = Interpreter.initFrame(cx, frame.scope, newInstance, stack, sDbl, stackTop + 1, indexReg, f, frame);
                                                    stack[stackTop] = newInstance;
                                                    frame.savedStackTop = stackTop;
                                                    frame.savedCallOp = op;
                                                    frame = calleeFrame;
                                                    continue block142;
                                                }
                                            }
                                            if (!(lhs instanceof Function)) {
                                                if (lhs == DBL_MRK) {
                                                    lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                                }
                                                throw ScriptRuntime.notFunctionError(lhs);
                                            }
                                            Function fun = (Function)lhs;
                                            if (fun instanceof IdFunctionObject && NativeContinuation.isContinuationConstructor(ifun = (IdFunctionObject)fun)) {
                                                frame.stack[stackTop] = Interpreter.captureContinuation(cx, frame.parentFrame, false);
                                                continue block143;
                                            }
                                            Object[] outArgs = Interpreter.getArgsArray(stack, sDbl, stackTop + 1, indexReg);
                                            stack[stackTop] = fun.construct(cx, frame.scope, outArgs);
                                            continue block143;
                                        }
                                        case 32: {
                                            Object lhs = stack[stackTop];
                                            if (lhs == DBL_MRK) {
                                                lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.typeof(lhs);
                                            continue block143;
                                        }
                                        case -14: {
                                            stack[++stackTop] = ScriptRuntime.typeofName(frame.scope, stringReg);
                                            continue block143;
                                        }
                                        case 41: {
                                            stack[++stackTop] = stringReg;
                                            continue block143;
                                        }
                                        case -27: {
                                            stack[++stackTop] = DBL_MRK;
                                            sDbl[stackTop] = Interpreter.getShort(iCode, frame.pc);
                                            frame.pc += 2;
                                            continue block143;
                                        }
                                        case -28: {
                                            stack[++stackTop] = DBL_MRK;
                                            sDbl[stackTop] = Interpreter.getInt(iCode, frame.pc);
                                            frame.pc += 4;
                                            continue block143;
                                        }
                                        case 40: {
                                            stack[++stackTop] = DBL_MRK;
                                            sDbl[stackTop] = frame.idata.itsDoubleTable[indexReg];
                                            continue block143;
                                        }
                                        case 83: {
                                            stack[++stackTop] = bigIntReg;
                                            continue block143;
                                        }
                                        case 39: {
                                            stack[++stackTop] = ScriptRuntime.name(cx, frame.scope, stringReg);
                                            continue block143;
                                        }
                                        case -8: {
                                            stack[++stackTop] = ScriptRuntime.nameIncrDecr(frame.scope, stringReg, cx, iCode[frame.pc]);
                                            ++frame.pc;
                                            continue block143;
                                        }
                                        case -61: {
                                            indexReg = iCode[frame.pc++];
                                        }
                                        case 160: {
                                            stackTop = Interpreter.doSetConstVar(frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg);
                                            continue block143;
                                        }
                                        case -49: {
                                            indexReg = iCode[frame.pc++];
                                        }
                                        case 56: {
                                            stackTop = Interpreter.doSetVar(frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg);
                                            continue block143;
                                        }
                                        case -48: {
                                            indexReg = iCode[frame.pc++];
                                        }
                                        case 55: {
                                            stackTop = Interpreter.doGetVar(frame, stack, sDbl, stackTop, vars, varDbls, indexReg);
                                            continue block143;
                                        }
                                        case -7: {
                                            stackTop = Interpreter.doVarIncDec(cx, frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg);
                                            continue block143;
                                        }
                                        case -51: {
                                            stack[++stackTop] = DBL_MRK;
                                            sDbl[stackTop] = 0.0;
                                            continue block143;
                                        }
                                        case -52: {
                                            stack[++stackTop] = DBL_MRK;
                                            sDbl[stackTop] = 1.0;
                                            continue block143;
                                        }
                                        case 42: {
                                            stack[++stackTop] = null;
                                            continue block143;
                                        }
                                        case 43: {
                                            stack[++stackTop] = frame.thisObj;
                                            continue block143;
                                        }
                                        case 64: {
                                            stack[++stackTop] = frame.fnOrScript;
                                            continue block143;
                                        }
                                        case 44: {
                                            stack[++stackTop] = Boolean.FALSE;
                                            continue block143;
                                        }
                                        case 45: {
                                            stack[++stackTop] = Boolean.TRUE;
                                            continue block143;
                                        }
                                        case -50: {
                                            stack[++stackTop] = undefined;
                                            continue block143;
                                        }
                                        case 2: {
                                            Object lhs = stack[stackTop];
                                            if (lhs == DBL_MRK) {
                                                lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            --stackTop;
                                            frame.scope = ScriptRuntime.enterWith(lhs, cx, frame.scope);
                                            continue block143;
                                        }
                                        case 3: {
                                            frame.scope = ScriptRuntime.leaveWith(frame.scope);
                                            continue block143;
                                        }
                                        case 57: {
                                            boolean afterFirstScope = frame.idata.itsICode[frame.pc] != 0;
                                            Throwable caughtException = (Throwable)stack[--stackTop + 1];
                                            Scriptable lastCatchScope = !afterFirstScope ? null : (Scriptable)stack[indexReg += frame.localShift];
                                            stack[indexReg] = ScriptRuntime.newCatchScope(caughtException, lastCatchScope, stringReg, cx, frame.scope);
                                            ++frame.pc;
                                            continue block143;
                                        }
                                        case 58: 
                                        case 59: 
                                        case 60: 
                                        case 61: {
                                            Object lhs = stack[stackTop];
                                            if (lhs == DBL_MRK) {
                                                lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            --stackTop;
                                            int enumType = op == 58 ? 0 : (op == 59 ? 1 : (op == 61 ? 6 : 2));
                                            stack[indexReg += frame.localShift] = ScriptRuntime.enumInit(lhs, cx, frame.scope, enumType);
                                            continue block143;
                                        }
                                        case 62: 
                                        case 63: {
                                            Object val = stack[indexReg += frame.localShift];
                                            stack[++stackTop] = op == 62 ? ScriptRuntime.enumNext(val) : ScriptRuntime.enumId(val, cx);
                                            continue block143;
                                        }
                                        case 72: {
                                            Object obj = stack[stackTop];
                                            if (obj == DBL_MRK) {
                                                obj = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.specialRef(obj, stringReg, cx, frame.scope);
                                            continue block143;
                                        }
                                        case 79: {
                                            stackTop = Interpreter.doRefMember(cx, stack, sDbl, stackTop, indexReg);
                                            continue block143;
                                        }
                                        case 80: {
                                            stackTop = Interpreter.doRefNsMember(cx, stack, sDbl, stackTop, indexReg);
                                            continue block143;
                                        }
                                        case 81: {
                                            Object name = stack[stackTop];
                                            if (name == DBL_MRK) {
                                                name = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.nameRef(name, cx, frame.scope, indexReg);
                                            continue block143;
                                        }
                                        case 82: {
                                            stackTop = Interpreter.doRefNsName(cx, frame, stack, sDbl, stackTop, indexReg);
                                            continue block143;
                                        }
                                        case -12: {
                                            frame.scope = (Scriptable)stack[indexReg += frame.localShift];
                                            continue block143;
                                        }
                                        case -13: {
                                            stack[indexReg += frame.localShift] = frame.scope;
                                            continue block143;
                                        }
                                        case -19: {
                                            InterpretedFunction fn = InterpretedFunction.createFunction(cx, frame.scope, frame.fnOrScript, indexReg);
                                            if (fn.idata.itsFunctionType == 4) {
                                                stack[++stackTop] = new ArrowFunction(cx, frame.scope, fn, frame.thisObj);
                                                continue block143;
                                            }
                                            stack[++stackTop] = fn;
                                            continue block143;
                                        }
                                        case -20: {
                                            Interpreter.initFunction(cx, frame.scope, frame.fnOrScript, indexReg);
                                            continue block143;
                                        }
                                        case 48: {
                                            Object re = frame.idata.itsRegExpLiterals[indexReg];
                                            stack[++stackTop] = ScriptRuntime.wrapRegExp(cx, frame.scope, re);
                                            continue block143;
                                        }
                                        case -74: {
                                            Object[] templateLiterals = frame.idata.itsTemplateLiterals;
                                            stack[++stackTop] = ScriptRuntime.getTemplateLiteralCallSite(cx, frame.scope, templateLiterals, indexReg);
                                            continue block143;
                                        }
                                        case -29: {
                                            stack[++stackTop] = new int[indexReg];
                                            stack[++stackTop] = new Object[indexReg];
                                            sDbl[stackTop] = 0.0;
                                            continue block143;
                                        }
                                        case -30: {
                                            Object value = stack[stackTop];
                                            if (value == DBL_MRK) {
                                                value = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            int i = (int)sDbl[--stackTop];
                                            ((Object[])stack[stackTop])[i] = value;
                                            sDbl[stackTop] = i + 1;
                                            continue block143;
                                        }
                                        case -57: {
                                            Object value = stack[stackTop];
                                            int i = (int)sDbl[--stackTop];
                                            ((Object[])stack[stackTop])[i] = value;
                                            ((int[])stack[stackTop - 1])[i] = -1;
                                            sDbl[stackTop] = i + 1;
                                            continue block143;
                                        }
                                        case -58: {
                                            Object value = stack[stackTop];
                                            int i = (int)sDbl[--stackTop];
                                            ((Object[])stack[stackTop])[i] = value;
                                            ((int[])stack[stackTop - 1])[i] = 1;
                                            sDbl[stackTop] = i + 1;
                                            continue block143;
                                        }
                                        case -31: 
                                        case 66: 
                                        case 67: {
                                            Scriptable val;
                                            Object[] data = (Object[])stack[stackTop];
                                            int[] getterSetters = (int[])stack[--stackTop];
                                            if (op == 67) {
                                                Object[] ids = (Object[])frame.idata.literalIds[indexReg];
                                                val = ScriptRuntime.newObjectLiteral(ids, data, getterSetters, cx, frame.scope);
                                            } else {
                                                int[] skipIndexces = null;
                                                if (op == -31) {
                                                    skipIndexces = (int[])frame.idata.literalIds[indexReg];
                                                }
                                                val = ScriptRuntime.newArrayLiteral(data, skipIndexces, cx, frame.scope);
                                            }
                                            stack[stackTop] = val;
                                            continue block143;
                                        }
                                        case -53: {
                                            Object lhs = stack[stackTop];
                                            if (lhs == DBL_MRK) {
                                                lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            --stackTop;
                                            frame.scope = ScriptRuntime.enterDotQuery(lhs, frame.scope);
                                            continue block143;
                                        }
                                        case -54: {
                                            boolean valBln = Interpreter.stack_boolean(frame, stackTop);
                                            Object x = ScriptRuntime.updateDotQuery(valBln, frame.scope);
                                            if (x != null) {
                                                stack[stackTop] = x;
                                                frame.scope = ScriptRuntime.leaveDotQuery(frame.scope);
                                                frame.pc += 2;
                                                continue block143;
                                            }
                                            --stackTop;
                                            break;
                                        }
                                        case 76: {
                                            Object value = stack[stackTop];
                                            if (value == DBL_MRK) {
                                                value = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                                            }
                                            stack[stackTop] = ScriptRuntime.setDefaultNamespace(value, cx);
                                            continue block143;
                                        }
                                        case 77: {
                                            Object value = stack[stackTop];
                                            if (value == DBL_MRK) continue block143;
                                            stack[stackTop] = ScriptRuntime.escapeAttributeValue(value, cx);
                                            continue block143;
                                        }
                                        case 78: {
                                            Object value = stack[stackTop];
                                            if (value == DBL_MRK) continue block143;
                                            stack[stackTop] = ScriptRuntime.escapeTextValue(value, cx);
                                            continue block143;
                                        }
                                        case -64: {
                                            if (frame.debuggerFrame == null) continue block143;
                                            frame.debuggerFrame.onDebuggerStatement(cx);
                                            continue block143;
                                        }
                                        case -26: {
                                            frame.pcSourceLineStart = frame.pc;
                                            if (frame.debuggerFrame != null) {
                                                int line = Interpreter.getIndex(iCode, frame.pc);
                                                frame.debuggerFrame.onLineChange(cx, line);
                                            }
                                            frame.pc += 2;
                                            continue block143;
                                        }
                                        case -32: {
                                            indexReg = 0;
                                            continue block143;
                                        }
                                        case -33: {
                                            indexReg = 1;
                                            continue block143;
                                        }
                                        case -34: {
                                            indexReg = 2;
                                            continue block143;
                                        }
                                        case -35: {
                                            indexReg = 3;
                                            continue block143;
                                        }
                                        case -36: {
                                            indexReg = 4;
                                            continue block143;
                                        }
                                        case -37: {
                                            indexReg = 5;
                                            continue block143;
                                        }
                                        case -38: {
                                            indexReg = 0xFF & iCode[frame.pc];
                                            ++frame.pc;
                                            continue block143;
                                        }
                                        case -39: {
                                            indexReg = Interpreter.getIndex(iCode, frame.pc);
                                            frame.pc += 2;
                                            continue block143;
                                        }
                                        case -40: {
                                            indexReg = Interpreter.getInt(iCode, frame.pc);
                                            frame.pc += 4;
                                            continue block143;
                                        }
                                        case -41: {
                                            stringReg = strings[0];
                                            continue block143;
                                        }
                                        case -42: {
                                            stringReg = strings[1];
                                            continue block143;
                                        }
                                        case -43: {
                                            stringReg = strings[2];
                                            continue block143;
                                        }
                                        case -44: {
                                            stringReg = strings[3];
                                            continue block143;
                                        }
                                        case -45: {
                                            stringReg = strings[0xFF & iCode[frame.pc]];
                                            ++frame.pc;
                                            continue block143;
                                        }
                                        case -46: {
                                            stringReg = strings[Interpreter.getIndex(iCode, frame.pc)];
                                            frame.pc += 2;
                                            continue block143;
                                        }
                                        case -47: {
                                            stringReg = strings[Interpreter.getInt(iCode, frame.pc)];
                                            frame.pc += 4;
                                            continue block143;
                                        }
                                        case -67: {
                                            bigIntReg = bigInts[0];
                                            continue block143;
                                        }
                                        case -68: {
                                            bigIntReg = bigInts[1];
                                            continue block143;
                                        }
                                        case -69: {
                                            bigIntReg = bigInts[2];
                                            continue block143;
                                        }
                                        case -70: {
                                            bigIntReg = bigInts[3];
                                            continue block143;
                                        }
                                        case -71: {
                                            bigIntReg = bigInts[0xFF & iCode[frame.pc]];
                                            ++frame.pc;
                                            continue block143;
                                        }
                                        case -72: {
                                            bigIntReg = bigInts[Interpreter.getIndex(iCode, frame.pc)];
                                            frame.pc += 2;
                                            continue block143;
                                        }
                                        case -73: {
                                            bigIntReg = bigInts[Interpreter.getInt(iCode, frame.pc)];
                                            frame.pc += 4;
                                            continue block143;
                                        }
                                        default: {
                                            Interpreter.dumpICode(frame.idata);
                                            throw new RuntimeException("Unknown icode : " + op + " @ pc : " + (frame.pc - 1));
                                        }
                                    }
                                    if (instructionCounting) {
                                        Interpreter.addInstructionCount(cx, frame, 2);
                                    }
                                    frame.pc = (offset = Interpreter.getShort(iCode, frame.pc)) != 0 ? (frame.pc += offset - 1) : frame.idata.longJumps.getExistingInt(frame.pc);
                                    if (!instructionCounting) continue;
                                    frame.pcPrevBranch = frame.pc;
                                }
                                Interpreter.exitFrame(cx, frame, null);
                                interpreterResult = frame.result;
                                interpreterResultDbl = frame.resultDbl;
                                if (frame.parentFrame != null) {
                                    frame = frame.parentFrame;
                                    if (frame.frozen) {
                                        frame = frame.cloneFrozen();
                                    }
                                    Interpreter.setCallResult(frame, interpreterResult, interpreterResultDbl);
                                    interpreterResult = null;
                                    continue;
                                }
                                break block242;
                                break;
                            }
                        }
                        catch (Throwable ex) {
                            if (throwable != null) {
                                ex.printStackTrace(System.err);
                                throw new IllegalStateException();
                            }
                            throwable = ex;
                        }
                    }
                    if (throwable == null) {
                        Kit.codeBug();
                    }
                    int EX_CATCH_STATE = 2;
                    boolean EX_FINALLY_STATE = true;
                    boolean EX_NO_JS_STATE = false;
                    cjump = null;
                    if (generatorState != null && generatorState.operation == 2 && throwable == generatorState.value) {
                        exState = 1;
                    } else if (throwable instanceof JavaScriptException) {
                        exState = 2;
                    } else if (throwable instanceof EcmaError) {
                        exState = 2;
                    } else if (throwable instanceof EvaluatorException) {
                        exState = 2;
                    } else if (throwable instanceof ContinuationPending) {
                        exState = 0;
                    } else if (throwable instanceof RuntimeException) {
                        exState = cx.hasFeature(13) ? 2 : 1;
                    } else if (throwable instanceof Error) {
                        exState = cx.hasFeature(13) ? 2 : 0;
                    } else if (throwable instanceof ContinuationJump) {
                        exState = 1;
                        cjump = (ContinuationJump)throwable;
                    } else {
                        int n = exState = cx.hasFeature(13) ? 2 : 1;
                    }
                    if (instructionCounting) {
                        try {
                            Interpreter.addInstructionCount(cx, frame, 100);
                        }
                        catch (RuntimeException ex) {
                            throwable = ex;
                            exState = 1;
                        }
                        catch (Error ex) {
                            throwable = ex;
                            cjump = null;
                            exState = 0;
                        }
                    }
                    if (frame.debuggerFrame != null && throwable instanceof RuntimeException) {
                        RuntimeException rex = (RuntimeException)throwable;
                        try {
                            frame.debuggerFrame.onExceptionThrown(cx, rex);
                        }
                        catch (Throwable ex) {
                            throwable = ex;
                            cjump = null;
                            exState = 0;
                        }
                    }
                    do {
                        boolean onlyFinally;
                        if (exState != 0 && (indexReg = Interpreter.getExceptionHandler(frame, onlyFinally = exState != 2)) >= 0) continue block141;
                        Interpreter.exitFrame(cx, frame, throwable);
                        frame = frame.parentFrame;
                        if (frame == null) break block243;
                    } while (cjump == null || cjump.branchFrame != frame);
                    indexReg = -1;
                    continue;
                }
                if (cjump == null) break block242;
                if (cjump.branchFrame != null) {
                    Kit.codeBug();
                }
                if (cjump.capturedFrame == null) break;
                indexReg = -1;
            }
            interpreterResult = cjump.result;
            interpreterResultDbl = cjump.resultDbl;
            throwable = null;
        }
        if (cx.previousInterpreterInvocations != null && cx.previousInterpreterInvocations.size() != 0) {
            cx.lastInterpreterFrame = cx.previousInterpreterInvocations.pop();
        } else {
            cx.lastInterpreterFrame = null;
            cx.previousInterpreterInvocations = null;
        }
        if (throwable != null) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException)throwable;
            }
            throw (Error)throwable;
        }
        return interpreterResult != DBL_MRK ? interpreterResult : ScriptRuntime.wrapNumber(interpreterResultDbl);
    }

    private static int doInOrInstanceof(Context cx, int op, Object[] stack, double[] sDbl, int stackTop) {
        Object lhs;
        Object rhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        if ((lhs = stack[--stackTop]) == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        boolean valBln = op == 52 ? ScriptRuntime.in(lhs, rhs, cx) : ScriptRuntime.instanceOf(lhs, rhs, cx);
        stack[stackTop] = ScriptRuntime.wrapBoolean(valBln);
        return stackTop;
    }

    /*
     * Unable to fully structure code
     */
    private static int doCompare(CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
        block2: {
            rhs = stack[--stackTop + 1];
            lhs = stack[stackTop];
            if (rhs != UniqueTag.DOUBLE_MARK) break block2;
            rNum = sDbl[stackTop + 1];
            lNum = Interpreter.stack_numeric(frame, stackTop);
            ** GOTO lbl11
        }
        if (lhs == UniqueTag.DOUBLE_MARK) {
            rNum = ScriptRuntime.toNumeric(rhs);
            lNum = sDbl[stackTop];
lbl11:
            // 2 sources

            valBln = ScriptRuntime.compare(lNum, rNum, op);
        } else {
            valBln = ScriptRuntime.compare(lhs, rhs, op);
        }
        stack[stackTop] = ScriptRuntime.wrapBoolean(valBln);
        return stackTop;
    }

    private static int doBitOp(CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
        Number lValue = Interpreter.stack_numeric(frame, stackTop - 1);
        Number rValue = Interpreter.stack_numeric(frame, stackTop);
        --stackTop;
        Number result = null;
        switch (op) {
            case 11: {
                result = ScriptRuntime.bitwiseAND(lValue, rValue);
                break;
            }
            case 9: {
                result = ScriptRuntime.bitwiseOR(lValue, rValue);
                break;
            }
            case 10: {
                result = ScriptRuntime.bitwiseXOR(lValue, rValue);
                break;
            }
            case 18: {
                result = ScriptRuntime.leftShift(lValue, rValue);
                break;
            }
            case 19: {
                result = ScriptRuntime.signedRightShift(lValue, rValue);
            }
        }
        if (result instanceof BigInteger) {
            stack[stackTop] = result;
        } else {
            stack[stackTop] = UniqueTag.DOUBLE_MARK;
            sDbl[stackTop] = result.doubleValue();
        }
        return stackTop;
    }

    private static int doBitNOT(CallFrame frame, Object[] stack, double[] sDbl, int stackTop) {
        Number value = Interpreter.stack_numeric(frame, stackTop);
        Number result = ScriptRuntime.bitwiseNOT(value);
        if (result instanceof BigInteger) {
            stack[stackTop] = result;
        } else {
            stack[stackTop] = UniqueTag.DOUBLE_MARK;
            sDbl[stackTop] = result.doubleValue();
        }
        return stackTop;
    }

    private static int doDelName(Context cx, CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
        Object lhs;
        Object rhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        if ((lhs = stack[--stackTop]) == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        stack[stackTop] = ScriptRuntime.delete(lhs, rhs, cx, frame.scope, op == 0);
        return stackTop;
    }

    private static int doGetElem(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop) {
        Object value;
        Object id;
        Object lhs;
        if ((lhs = stack[--stackTop]) == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        if ((id = stack[stackTop + 1]) != UniqueTag.DOUBLE_MARK) {
            value = ScriptRuntime.getObjectElem(lhs, id, cx, frame.scope);
        } else {
            double d = sDbl[stackTop + 1];
            value = ScriptRuntime.getObjectIndex(lhs, d, cx, frame.scope);
        }
        stack[stackTop] = value;
        return stackTop;
    }

    private static int doSetElem(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop) {
        Object value;
        Object id;
        Object lhs;
        Object rhs = stack[(stackTop -= 2) + 2];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            rhs = ScriptRuntime.wrapNumber(sDbl[stackTop + 2]);
        }
        if ((lhs = stack[stackTop]) == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        if ((id = stack[stackTop + 1]) != UniqueTag.DOUBLE_MARK) {
            value = ScriptRuntime.setObjectElem(lhs, id, rhs, cx, frame.scope);
        } else {
            double d = sDbl[stackTop + 1];
            value = ScriptRuntime.setObjectIndex(lhs, d, rhs, cx, frame.scope);
        }
        stack[stackTop] = value;
        return stackTop;
    }

    private static int doElemIncDec(Context cx, CallFrame frame, byte[] iCode, Object[] stack, double[] sDbl, int stackTop) {
        Object lhs;
        Object rhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        if ((lhs = stack[--stackTop]) == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        stack[stackTop] = ScriptRuntime.elemIncrDecr(lhs, rhs, cx, frame.scope, iCode[frame.pc]);
        ++frame.pc;
        return stackTop;
    }

    private static int doCallSpecial(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop, byte[] iCode, int indexReg) {
        int callType = iCode[frame.pc] & 0xFF;
        boolean isNew = iCode[frame.pc + 1] != 0;
        int sourceLine = Interpreter.getIndex(iCode, frame.pc + 2);
        if (isNew) {
            Object function = stack[stackTop -= indexReg];
            if (function == UniqueTag.DOUBLE_MARK) {
                function = ScriptRuntime.wrapNumber(sDbl[stackTop]);
            }
            Object[] outArgs = Interpreter.getArgsArray(stack, sDbl, stackTop + 1, indexReg);
            stack[stackTop] = ScriptRuntime.newSpecial(cx, function, outArgs, frame.scope, callType);
        } else {
            Scriptable functionThis = (Scriptable)stack[(stackTop -= 1 + indexReg) + 1];
            Callable function = (Callable)stack[stackTop];
            Object[] outArgs = Interpreter.getArgsArray(stack, sDbl, stackTop + 2, indexReg);
            stack[stackTop] = ScriptRuntime.callSpecial(cx, function, functionThis, outArgs, frame.scope, frame.thisObj, callType, frame.idata.itsSourceFile, sourceLine);
        }
        frame.pc += 4;
        return stackTop;
    }

    private static int doSetConstVar(CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int[] varAttributes, int indexReg) {
        if (!frame.useActivation) {
            if ((varAttributes[indexReg] & 1) == 0) {
                throw Context.reportRuntimeErrorById("msg.var.redecl", frame.idata.argNames[indexReg]);
            }
            if ((varAttributes[indexReg] & 8) != 0) {
                vars[indexReg] = stack[stackTop];
                int n = indexReg;
                varAttributes[n] = varAttributes[n] & 0xFFFFFFF7;
                varDbls[indexReg] = sDbl[stackTop];
            }
        } else {
            Object val = stack[stackTop];
            if (val == UniqueTag.DOUBLE_MARK) {
                val = ScriptRuntime.wrapNumber(sDbl[stackTop]);
            }
            String stringReg = frame.idata.argNames[indexReg];
            if (frame.scope instanceof ConstProperties) {
                ConstProperties cp = (ConstProperties)((Object)frame.scope);
                cp.putConst(stringReg, frame.scope, val);
            } else {
                throw Kit.codeBug();
            }
        }
        return stackTop;
    }

    private static int doSetVar(CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int[] varAttributes, int indexReg) {
        if (!frame.useActivation) {
            if ((varAttributes[indexReg] & 1) == 0) {
                vars[indexReg] = stack[stackTop];
                varDbls[indexReg] = sDbl[stackTop];
            }
        } else {
            Object val = stack[stackTop];
            if (val == UniqueTag.DOUBLE_MARK) {
                val = ScriptRuntime.wrapNumber(sDbl[stackTop]);
            }
            String stringReg = frame.idata.argNames[indexReg];
            frame.scope.put(stringReg, frame.scope, val);
        }
        return stackTop;
    }

    private static int doGetVar(CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int indexReg) {
        ++stackTop;
        if (!frame.useActivation) {
            stack[stackTop] = vars[indexReg];
            sDbl[stackTop] = varDbls[indexReg];
        } else {
            String stringReg = frame.idata.argNames[indexReg];
            stack[stackTop] = frame.scope.get(stringReg, frame.scope);
        }
        return stackTop;
    }

    private static int doVarIncDec(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int[] varAttributes, int indexReg) {
        ++stackTop;
        byte incrDecrMask = frame.idata.itsICode[frame.pc];
        if (!frame.useActivation) {
            Object varValue = vars[indexReg];
            double d = 0.0;
            BigInteger bi = null;
            if (varValue == UniqueTag.DOUBLE_MARK) {
                d = varDbls[indexReg];
            } else {
                Number num = ScriptRuntime.toNumeric(varValue);
                if (num instanceof BigInteger) {
                    bi = (BigInteger)num;
                } else {
                    d = num.doubleValue();
                }
            }
            if (bi == null) {
                boolean post;
                double d2 = (incrDecrMask & 1) == 0 ? d + 1.0 : d - 1.0;
                boolean bl = post = (incrDecrMask & 2) != 0;
                if ((varAttributes[indexReg] & 1) == 0) {
                    if (varValue != UniqueTag.DOUBLE_MARK) {
                        vars[indexReg] = UniqueTag.DOUBLE_MARK;
                    }
                    varDbls[indexReg] = d2;
                    stack[stackTop] = UniqueTag.DOUBLE_MARK;
                    sDbl[stackTop] = post ? d : d2;
                } else if (post && varValue != UniqueTag.DOUBLE_MARK) {
                    stack[stackTop] = varValue;
                } else {
                    stack[stackTop] = UniqueTag.DOUBLE_MARK;
                    sDbl[stackTop] = post ? d : d2;
                }
            } else {
                boolean post;
                BigInteger result = (incrDecrMask & 1) == 0 ? bi.add(BigInteger.ONE) : bi.subtract(BigInteger.ONE);
                boolean bl = post = (incrDecrMask & 2) != 0;
                if ((varAttributes[indexReg] & 1) == 0) {
                    vars[indexReg] = result;
                    stack[stackTop] = post ? bi : result;
                } else {
                    stack[stackTop] = post && varValue != UniqueTag.DOUBLE_MARK ? varValue : (post ? bi : result);
                }
            }
        } else {
            String varName = frame.idata.argNames[indexReg];
            stack[stackTop] = ScriptRuntime.nameIncrDecr(frame.scope, varName, cx, incrDecrMask);
        }
        ++frame.pc;
        return stackTop;
    }

    private static int doRefMember(Context cx, Object[] stack, double[] sDbl, int stackTop, int flags) {
        Object obj;
        Object elem = stack[stackTop];
        if (elem == UniqueTag.DOUBLE_MARK) {
            elem = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        if ((obj = stack[--stackTop]) == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        stack[stackTop] = ScriptRuntime.memberRef(obj, elem, cx, flags);
        return stackTop;
    }

    private static int doRefNsMember(Context cx, Object[] stack, double[] sDbl, int stackTop, int flags) {
        Object obj;
        Object ns;
        Object elem = stack[stackTop];
        if (elem == UniqueTag.DOUBLE_MARK) {
            elem = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        if ((ns = stack[--stackTop]) == UniqueTag.DOUBLE_MARK) {
            ns = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        if ((obj = stack[--stackTop]) == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        stack[stackTop] = ScriptRuntime.memberRef(obj, ns, elem, cx, flags);
        return stackTop;
    }

    private static int doRefNsName(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop, int flags) {
        Object ns;
        Object name = stack[stackTop];
        if (name == UniqueTag.DOUBLE_MARK) {
            name = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        if ((ns = stack[--stackTop]) == UniqueTag.DOUBLE_MARK) {
            ns = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        stack[stackTop] = ScriptRuntime.nameRef(ns, name, cx, frame.scope, flags);
        return stackTop;
    }

    private static CallFrame initFrameForNoSuchMethod(Context cx, CallFrame frame, int indexReg, Object[] stack, double[] sDbl, int stackTop, int op, Scriptable funThisObj, Scriptable calleeScope, ScriptRuntime.NoSuchMethodShim noSuchMethodShim, InterpretedFunction ifun) {
        Object[] argsArray = null;
        int shift = stackTop + 2;
        Object[] elements = new Object[indexReg];
        int i = 0;
        while (i < indexReg) {
            Object val = stack[shift];
            if (val == UniqueTag.DOUBLE_MARK) {
                val = ScriptRuntime.wrapNumber(sDbl[shift]);
            }
            elements[i] = val;
            ++i;
            ++shift;
        }
        argsArray = new Object[]{noSuchMethodShim.methodName, cx.newArray(calleeScope, elements)};
        CallFrame callParentFrame = frame;
        if (op == -55) {
            callParentFrame = frame.parentFrame;
            Interpreter.exitFrame(cx, frame, null);
        }
        CallFrame calleeFrame = Interpreter.initFrame(cx, calleeScope, funThisObj, argsArray, null, 0, 2, ifun, callParentFrame);
        if (op != -55) {
            frame.savedStackTop = stackTop;
            frame.savedCallOp = op;
        }
        return calleeFrame;
    }

    private static boolean doEquals(Object[] stack, double[] sDbl, int stackTop) {
        Object rhs = stack[stackTop + 1];
        Object lhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            if (lhs == UniqueTag.DOUBLE_MARK) {
                return sDbl[stackTop] == sDbl[stackTop + 1];
            }
            return ScriptRuntime.eqNumber(sDbl[stackTop + 1], lhs);
        }
        if (lhs == UniqueTag.DOUBLE_MARK) {
            return ScriptRuntime.eqNumber(sDbl[stackTop], rhs);
        }
        return ScriptRuntime.eq(lhs, rhs);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean doShallowEquals(Object[] stack, double[] sDbl, int stackTop) {
        double ldbl;
        double rdbl;
        Object rhs = stack[stackTop + 1];
        Object lhs = stack[stackTop];
        UniqueTag DBL_MRK = UniqueTag.DOUBLE_MARK;
        if (rhs == DBL_MRK) {
            rdbl = sDbl[stackTop + 1];
            if (lhs == DBL_MRK) {
                ldbl = sDbl[stackTop];
            } else {
                if (!(lhs instanceof Number)) return false;
                if (lhs instanceof BigInteger) return false;
                ldbl = ((Number)lhs).doubleValue();
            }
        } else {
            if (lhs != DBL_MRK) return ScriptRuntime.shallowEq(lhs, rhs);
            ldbl = sDbl[stackTop];
            if (!(rhs instanceof Number)) return false;
            if (rhs instanceof BigInteger) return false;
            rdbl = ((Number)rhs).doubleValue();
        }
        if (ldbl != rdbl) return false;
        return true;
    }

    private static CallFrame processThrowable(Context cx, Object throwable, CallFrame frame, int indexReg, boolean instructionCounting) {
        if (indexReg >= 0) {
            if (frame.frozen) {
                frame = frame.cloneFrozen();
            }
            int[] table = frame.idata.itsExceptionTable;
            frame.pc = table[indexReg + 2];
            if (instructionCounting) {
                frame.pcPrevBranch = frame.pc;
            }
            frame.savedStackTop = frame.emptyStackTop;
            int scopeLocal = frame.localShift + table[indexReg + 5];
            int exLocal = frame.localShift + table[indexReg + 4];
            frame.scope = (Scriptable)frame.stack[scopeLocal];
            frame.stack[exLocal] = throwable;
            throwable = null;
        } else {
            ContinuationJump cjump = (ContinuationJump)throwable;
            throwable = null;
            if (cjump.branchFrame != frame) {
                Kit.codeBug();
            }
            if (cjump.capturedFrame == null) {
                Kit.codeBug();
            }
            int rewindCount = cjump.capturedFrame.frameIndex + 1;
            if (cjump.branchFrame != null) {
                rewindCount -= cjump.branchFrame.frameIndex;
            }
            int enterCount = 0;
            CallFrame[] enterFrames = null;
            CallFrame x = cjump.capturedFrame;
            for (int i = 0; i != rewindCount; ++i) {
                if (!x.frozen) {
                    Kit.codeBug();
                }
                if (x.useActivation) {
                    if (enterFrames == null) {
                        enterFrames = new CallFrame[rewindCount - i];
                    }
                    enterFrames[enterCount] = x;
                    ++enterCount;
                }
                x = x.parentFrame;
            }
            while (enterCount != 0) {
                x = enterFrames[--enterCount];
                Interpreter.enterFrame(cx, x, ScriptRuntime.emptyArgs, true);
            }
            frame = cjump.capturedFrame.cloneFrozen();
            Interpreter.setCallResult(frame, cjump.result, cjump.resultDbl);
        }
        frame.throwable = throwable;
        return frame;
    }

    private static Object freezeGenerator(Context cx, CallFrame frame, int stackTop, GeneratorState generatorState, boolean yieldStar) {
        Object result;
        if (generatorState.operation == 2) {
            throw ScriptRuntime.typeErrorById("msg.yield.closing", new Object[0]);
        }
        frame.frozen = true;
        frame.result = frame.stack[stackTop];
        frame.resultDbl = frame.sDbl[stackTop];
        frame.savedStackTop = stackTop;
        --frame.pc;
        ScriptRuntime.exitActivationFunction(cx);
        Object object = result = frame.result != UniqueTag.DOUBLE_MARK ? frame.result : ScriptRuntime.wrapNumber(frame.resultDbl);
        if (yieldStar) {
            return new ES6Generator.YieldStarResult(result);
        }
        return result;
    }

    private static Object thawGenerator(CallFrame frame, int stackTop, GeneratorState generatorState, int op) {
        frame.frozen = false;
        int sourceLine = Interpreter.getIndex(frame.idata.itsICode, frame.pc);
        frame.pc += 2;
        if (generatorState.operation == 1) {
            return new JavaScriptException(generatorState.value, frame.idata.itsSourceFile, sourceLine);
        }
        if (generatorState.operation == 2) {
            return generatorState.value;
        }
        if (generatorState.operation != 0) {
            throw Kit.codeBug();
        }
        if (op == 73 || op == -66) {
            frame.stack[stackTop] = generatorState.value;
        }
        return Scriptable.NOT_FOUND;
    }

    private static CallFrame initFrameForApplyOrCall(Context cx, CallFrame frame, int indexReg, Object[] stack, double[] sDbl, int stackTop, int op, Scriptable calleeScope, IdFunctionObject ifun, InterpretedFunction iApplyCallable) {
        CallFrame calleeFrame;
        Scriptable applyThis;
        if (indexReg != 0) {
            Object obj = stack[stackTop + 2];
            if (obj == UniqueTag.DOUBLE_MARK) {
                obj = ScriptRuntime.wrapNumber(sDbl[stackTop + 2]);
            }
            applyThis = ScriptRuntime.toObjectOrNull(cx, obj, frame.scope);
        } else {
            applyThis = null;
        }
        if (applyThis == null) {
            applyThis = ScriptRuntime.getTopCallScope(cx);
        }
        if (op == -55) {
            Interpreter.exitFrame(cx, frame, null);
            frame = frame.parentFrame;
        } else {
            frame.savedStackTop = stackTop;
            frame.savedCallOp = op;
        }
        if (BaseFunction.isApply(ifun)) {
            Object[] callArgs = indexReg < 2 ? ScriptRuntime.emptyArgs : ScriptRuntime.getApplyArguments(cx, stack[stackTop + 3]);
            calleeFrame = Interpreter.initFrame(cx, calleeScope, applyThis, callArgs, null, 0, callArgs.length, iApplyCallable, frame);
        } else {
            for (int i = 1; i < indexReg; ++i) {
                stack[stackTop + 1 + i] = stack[stackTop + 2 + i];
                sDbl[stackTop + 1 + i] = sDbl[stackTop + 2 + i];
            }
            int argCount = indexReg < 2 ? 0 : indexReg - 1;
            calleeFrame = Interpreter.initFrame(cx, calleeScope, applyThis, stack, sDbl, stackTop + 2, argCount, iApplyCallable, frame);
        }
        return calleeFrame;
    }

    private static CallFrame initFrame(Context cx, Scriptable callerScope, Scriptable thisObj, Object[] args, double[] argsDbl, int argShift, int argCount, InterpretedFunction fnOrScript, CallFrame parentFrame) {
        CallFrame frame = new CallFrame(cx, thisObj, fnOrScript, parentFrame);
        frame.initializeArgs(cx, callerScope, args, argsDbl, argShift, argCount);
        Interpreter.enterFrame(cx, frame, args, false);
        return frame;
    }

    private static void enterFrame(Context cx, CallFrame frame, Object[] args, boolean continuationRestart) {
        boolean isDebugged;
        boolean usesActivation = frame.idata.itsNeedsActivation;
        boolean bl = isDebugged = frame.debuggerFrame != null;
        if (usesActivation || isDebugged) {
            Scriptable scope = frame.scope;
            if (scope == null) {
                Kit.codeBug();
            } else if (continuationRestart) {
                while (scope instanceof NativeWith) {
                    if ((scope = scope.getParentScope()) != null && (frame.parentFrame == null || frame.parentFrame.scope != scope)) continue;
                    Kit.codeBug();
                    break;
                }
            }
            if (isDebugged) {
                frame.debuggerFrame.onEnter(cx, scope, frame.thisObj, args);
            }
            if (usesActivation) {
                ScriptRuntime.enterActivationFunction(cx, scope);
            }
        }
    }

    private static void exitFrame(Context cx, CallFrame frame, Object throwable) {
        if (frame.idata.itsNeedsActivation) {
            ScriptRuntime.exitActivationFunction(cx);
        }
        if (frame.debuggerFrame != null) {
            try {
                if (throwable instanceof Throwable) {
                    frame.debuggerFrame.onExit(cx, true, throwable);
                } else {
                    ContinuationJump cjump = (ContinuationJump)throwable;
                    Object result = cjump == null ? frame.result : cjump.result;
                    if (result == UniqueTag.DOUBLE_MARK) {
                        double resultDbl = cjump == null ? frame.resultDbl : cjump.resultDbl;
                        result = ScriptRuntime.wrapNumber(resultDbl);
                    }
                    frame.debuggerFrame.onExit(cx, false, result);
                }
            }
            catch (Throwable ex) {
                System.err.println("RHINO USAGE WARNING: onExit terminated with exception");
                ex.printStackTrace(System.err);
            }
        }
    }

    private static void setCallResult(CallFrame frame, Object callResult, double callResultDbl) {
        if (frame.savedCallOp == 38) {
            frame.stack[frame.savedStackTop] = callResult;
            frame.sDbl[frame.savedStackTop] = callResultDbl;
        } else if (frame.savedCallOp == 30) {
            if (callResult instanceof Scriptable) {
                frame.stack[frame.savedStackTop] = callResult;
            }
        } else {
            Kit.codeBug();
        }
        frame.savedCallOp = 0;
    }

    public static NativeContinuation captureContinuation(Context cx) {
        if (cx.lastInterpreterFrame == null || !(cx.lastInterpreterFrame instanceof CallFrame)) {
            throw new IllegalStateException("Interpreter frames not found");
        }
        return Interpreter.captureContinuation(cx, (CallFrame)cx.lastInterpreterFrame, true);
    }

    private static NativeContinuation captureContinuation(Context cx, CallFrame frame, boolean requireContinuationsTopFrame) {
        NativeContinuation c = new NativeContinuation();
        ScriptRuntime.setObjectProtoAndParent(c, ScriptRuntime.getTopCallScope(cx));
        CallFrame x = frame;
        CallFrame outermost = frame;
        while (x != null && !x.frozen) {
            x.frozen = true;
            for (int i = x.savedStackTop + 1; i != x.stack.length; ++i) {
                x.stack[i] = null;
                x.stackAttributes[i] = 0;
            }
            if (x.savedCallOp == 38) {
                x.stack[x.savedStackTop] = null;
            } else if (x.savedCallOp != 30) {
                Kit.codeBug();
            }
            outermost = x;
            x = x.parentFrame;
        }
        if (requireContinuationsTopFrame) {
            while (outermost.parentFrame != null) {
                outermost = outermost.parentFrame;
            }
            if (!outermost.isContinuationsTopFrame) {
                throw new IllegalStateException("Cannot capture continuation from JavaScript code not called directly by executeScriptWithContinuations or callFunctionWithContinuations");
            }
        }
        c.initImplementation(frame);
        return c;
    }

    private static int stack_int32(CallFrame frame, int i) {
        Object x = frame.stack[i];
        if (x == UniqueTag.DOUBLE_MARK) {
            return ScriptRuntime.toInt32(frame.sDbl[i]);
        }
        return ScriptRuntime.toInt32(x);
    }

    private static double stack_double(CallFrame frame, int i) {
        Object x = frame.stack[i];
        if (x != UniqueTag.DOUBLE_MARK) {
            return ScriptRuntime.toNumber(x);
        }
        return frame.sDbl[i];
    }

    private static Number stack_numeric(CallFrame frame, int i) {
        Object x = frame.stack[i];
        if (x != UniqueTag.DOUBLE_MARK) {
            return ScriptRuntime.toNumeric(x);
        }
        return frame.sDbl[i];
    }

    private static boolean stack_boolean(CallFrame frame, int i) {
        Object x = frame.stack[i];
        if (Boolean.TRUE.equals(x)) {
            return true;
        }
        if (Boolean.FALSE.equals(x)) {
            return false;
        }
        if (x == UniqueTag.DOUBLE_MARK) {
            double d = frame.sDbl[i];
            return !Double.isNaN(d) && d != 0.0;
        }
        if (x == null || x == Undefined.instance) {
            return false;
        }
        if (x instanceof BigInteger) {
            return !((BigInteger)x).equals(BigInteger.ZERO);
        }
        if (x instanceof Number) {
            double d = ((Number)x).doubleValue();
            return !Double.isNaN(d) && d != 0.0;
        }
        return ScriptRuntime.toBoolean(x);
    }

    private static void doAdd(Object[] stack, double[] sDbl, int stackTop, Context cx) {
        boolean leftRightOrder;
        double d;
        Object rhs = stack[stackTop + 1];
        Object lhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            d = sDbl[stackTop + 1];
            if (lhs == UniqueTag.DOUBLE_MARK) {
                int n = stackTop;
                sDbl[n] = sDbl[n] + d;
                return;
            }
            leftRightOrder = true;
        } else if (lhs == UniqueTag.DOUBLE_MARK) {
            d = sDbl[stackTop];
            lhs = rhs;
            leftRightOrder = false;
        } else {
            if (lhs instanceof Scriptable || rhs instanceof Scriptable) {
                stack[stackTop] = ScriptRuntime.add(lhs, rhs, cx);
            } else if (lhs instanceof CharSequence) {
                stack[stackTop] = rhs instanceof CharSequence ? new ConsString((CharSequence)lhs, (CharSequence)rhs) : new ConsString((CharSequence)lhs, ScriptRuntime.toCharSequence(rhs));
            } else if (rhs instanceof CharSequence) {
                stack[stackTop] = new ConsString(ScriptRuntime.toCharSequence(lhs), (CharSequence)rhs);
            } else {
                Number rNum;
                Number lNum = lhs instanceof Number ? (Number)((Number)lhs) : (Number)ScriptRuntime.toNumeric(lhs);
                Number number = rNum = rhs instanceof Number ? (Number)((Number)rhs) : (Number)ScriptRuntime.toNumeric(rhs);
                if (lNum instanceof BigInteger && rNum instanceof BigInteger) {
                    stack[stackTop] = ((BigInteger)lNum).add((BigInteger)rNum);
                } else {
                    if (lNum instanceof BigInteger || rNum instanceof BigInteger) {
                        throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
                    }
                    stack[stackTop] = UniqueTag.DOUBLE_MARK;
                    sDbl[stackTop] = lNum.doubleValue() + rNum.doubleValue();
                }
            }
            return;
        }
        if (lhs instanceof Scriptable) {
            rhs = ScriptRuntime.wrapNumber(d);
            if (!leftRightOrder) {
                Object tmp = lhs;
                lhs = rhs;
                rhs = tmp;
            }
            stack[stackTop] = ScriptRuntime.add(lhs, rhs, cx);
        } else if (lhs instanceof CharSequence) {
            String rstr = ScriptRuntime.numberToString(d, 10);
            stack[stackTop] = leftRightOrder ? new ConsString((CharSequence)lhs, rstr) : new ConsString(rstr, (CharSequence)lhs);
        } else {
            Number lNum;
            Number number = lNum = lhs instanceof Number ? (Number)((Number)lhs) : (Number)ScriptRuntime.toNumeric(lhs);
            if (lNum instanceof BigInteger) {
                throw ScriptRuntime.typeErrorById("msg.cant.convert.to.number", "BigInt");
            }
            stack[stackTop] = UniqueTag.DOUBLE_MARK;
            sDbl[stackTop] = lNum.doubleValue() + d;
        }
    }

    private static int doArithmetic(CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
        Number lNum = Interpreter.stack_numeric(frame, stackTop - 1);
        Number rNum = Interpreter.stack_numeric(frame, stackTop);
        --stackTop;
        Number result = null;
        switch (op) {
            case 22: {
                result = ScriptRuntime.subtract(lNum, rNum);
                break;
            }
            case 23: {
                result = ScriptRuntime.multiply(lNum, rNum);
                break;
            }
            case 24: {
                result = ScriptRuntime.divide(lNum, rNum);
                break;
            }
            case 25: {
                result = ScriptRuntime.remainder(lNum, rNum);
                break;
            }
            case 75: {
                result = ScriptRuntime.exponentiate(lNum, rNum);
            }
        }
        if (result instanceof BigInteger) {
            stack[stackTop] = result;
        } else {
            stack[stackTop] = UniqueTag.DOUBLE_MARK;
            sDbl[stackTop] = result.doubleValue();
        }
        return stackTop;
    }

    private static Object[] getArgsArray(Object[] stack, double[] sDbl, int shift, int count) {
        if (count == 0) {
            return ScriptRuntime.emptyArgs;
        }
        Object[] args = new Object[count];
        int i = 0;
        while (i != count) {
            Object val = stack[shift];
            if (val == UniqueTag.DOUBLE_MARK) {
                val = ScriptRuntime.wrapNumber(sDbl[shift]);
            }
            args[i] = val;
            ++i;
            ++shift;
        }
        return args;
    }

    private static void addInstructionCount(Context cx, CallFrame frame, int extra) {
        cx.instructionCount += frame.pc - frame.pcPrevBranch + extra;
        if (cx.instructionCount > cx.instructionThreshold) {
            cx.observeInstructionCount(cx.instructionCount);
            cx.instructionCount = 0;
        }
    }

    static class GeneratorState {
        int operation;
        Object value;
        RuntimeException returnedException;

        GeneratorState(int operation, Object value) {
            this.operation = operation;
            this.value = value;
        }
    }

    private static final class ContinuationJump
    implements Serializable {
        private static final long serialVersionUID = 7687739156004308247L;
        CallFrame capturedFrame;
        CallFrame branchFrame;
        Object result;
        double resultDbl;

        ContinuationJump(NativeContinuation c, CallFrame current) {
            this.capturedFrame = (CallFrame)c.getImplementation();
            if (this.capturedFrame == null || current == null) {
                this.branchFrame = null;
            } else {
                CallFrame chain1 = this.capturedFrame;
                CallFrame chain2 = current;
                int diff = chain1.frameIndex - chain2.frameIndex;
                if (diff != 0) {
                    if (diff < 0) {
                        chain1 = current;
                        chain2 = this.capturedFrame;
                        diff = -diff;
                    }
                    do {
                        chain1 = chain1.parentFrame;
                    } while (--diff != 0);
                    if (chain1.frameIndex != chain2.frameIndex) {
                        Kit.codeBug();
                    }
                }
                while (chain1 != chain2 && chain1 != null) {
                    chain1 = chain1.parentFrame;
                    chain2 = chain2.parentFrame;
                }
                this.branchFrame = chain1;
                if (this.branchFrame != null && !this.branchFrame.frozen) {
                    Kit.codeBug();
                }
            }
        }
    }

    private static class CallFrame
    implements Cloneable,
    Serializable {
        private static final long serialVersionUID = -2843792508994958978L;
        CallFrame parentFrame;
        int frameIndex;
        boolean frozen;
        final InterpretedFunction fnOrScript;
        final InterpreterData idata;
        Object[] stack;
        int[] stackAttributes;
        double[] sDbl;
        final CallFrame varSource;
        final int localShift;
        final int emptyStackTop;
        final DebugFrame debuggerFrame;
        final boolean useActivation;
        boolean isContinuationsTopFrame;
        final Scriptable thisObj;
        Object result;
        double resultDbl;
        int pc;
        int pcPrevBranch;
        int pcSourceLineStart;
        Scriptable scope;
        int savedStackTop;
        int savedCallOp;
        Object throwable;

        CallFrame(Context cx, Scriptable thisObj, InterpretedFunction fnOrScript, CallFrame parentFrame) {
            this.idata = fnOrScript.idata;
            this.debuggerFrame = cx.debugger != null ? cx.debugger.getFrame(cx, this.idata) : null;
            this.useActivation = this.debuggerFrame != null || this.idata.itsNeedsActivation;
            this.emptyStackTop = this.idata.itsMaxVars + this.idata.itsMaxLocals - 1;
            this.fnOrScript = fnOrScript;
            this.varSource = this;
            this.localShift = this.idata.itsMaxVars;
            this.thisObj = thisObj;
            this.parentFrame = parentFrame;
            int n = this.frameIndex = parentFrame == null ? 0 : parentFrame.frameIndex + 1;
            if (this.frameIndex > cx.getMaximumInterpreterStackDepth()) {
                throw Context.reportRuntimeError("Exceeded maximum stack depth");
            }
            this.result = Undefined.instance;
            this.pcSourceLineStart = this.idata.firstLinePC;
            this.savedStackTop = this.emptyStackTop;
        }

        void initializeArgs(Context cx, Scriptable callerScope, Object[] args, double[] argsDbl, int argShift, int argCount) {
            int maxFrameArray;
            if (this.useActivation) {
                if (argsDbl != null) {
                    args = Interpreter.getArgsArray(args, argsDbl, argShift, argCount);
                }
                argShift = 0;
                argsDbl = null;
            }
            if (this.idata.itsFunctionType != 0) {
                this.scope = this.fnOrScript.getParentScope();
                if (this.useActivation) {
                    this.scope = this.idata.itsFunctionType == 4 ? ScriptRuntime.createArrowFunctionActivation(this.fnOrScript, this.scope, args, this.idata.isStrict) : ScriptRuntime.createFunctionActivation(this.fnOrScript, this.scope, args, this.idata.isStrict);
                }
            } else {
                this.scope = callerScope;
                ScriptRuntime.initScript(this.fnOrScript, this.thisObj, cx, this.scope, this.fnOrScript.idata.evalScriptFlag);
            }
            if (this.idata.itsNestedFunctions != null) {
                if (this.idata.itsFunctionType != 0 && !this.idata.itsNeedsActivation) {
                    Kit.codeBug();
                }
                for (int i = 0; i < this.idata.itsNestedFunctions.length; ++i) {
                    InterpreterData fdata = this.idata.itsNestedFunctions[i];
                    if (fdata.itsFunctionType != 1) continue;
                    Interpreter.initFunction(cx, this.scope, this.fnOrScript, i);
                }
            }
            if ((maxFrameArray = this.idata.itsMaxFrameArray) != this.emptyStackTop + this.idata.itsMaxStack + 1) {
                Kit.codeBug();
            }
            this.stack = new Object[maxFrameArray];
            this.stackAttributes = new int[maxFrameArray];
            this.sDbl = new double[maxFrameArray];
            int varCount = this.idata.getParamAndVarCount();
            for (int i = 0; i < varCount; ++i) {
                if (!this.idata.getParamOrVarConst(i)) continue;
                this.stackAttributes[i] = 13;
            }
            int definedArgs = this.idata.argCount;
            if (definedArgs > argCount) {
                definedArgs = argCount;
            }
            System.arraycopy(args, argShift, this.stack, 0, definedArgs);
            if (argsDbl != null) {
                System.arraycopy(argsDbl, argShift, this.sDbl, 0, definedArgs);
            }
            for (int i = definedArgs; i != this.idata.itsMaxVars; ++i) {
                this.stack[i] = Undefined.instance;
            }
        }

        CallFrame cloneFrozen() {
            CallFrame copy;
            if (!this.frozen) {
                Kit.codeBug();
            }
            try {
                copy = (CallFrame)this.clone();
            }
            catch (CloneNotSupportedException ex) {
                throw new IllegalStateException();
            }
            copy.stack = (Object[])this.stack.clone();
            copy.stackAttributes = (int[])this.stackAttributes.clone();
            copy.sDbl = (double[])this.sDbl.clone();
            copy.frozen = false;
            return copy;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean equals(Object other) {
            if (other instanceof CallFrame) {
                Context cx = Context.enter();
                try {
                    if (ScriptRuntime.hasTopCall(cx)) {
                        boolean bl = this.equalsInTopScope(other);
                        return bl;
                    }
                    Scriptable top = ScriptableObject.getTopLevelScope(this.scope);
                    boolean bl = (Boolean)ScriptRuntime.doTopCall((c, scope, thisObj, args) -> this.equalsInTopScope(other), cx, top, top, ScriptRuntime.emptyArgs, this.isStrictTopFrame());
                    return bl;
                }
                finally {
                    Context.exit();
                }
            }
            return false;
        }

        public int hashCode() {
            int depth = 0;
            CallFrame f = this;
            int h = 0;
            do {
                h = 31 * (31 * h + f.pc) + f.idata.icodeHashCode();
            } while ((f = f.parentFrame) != null && depth++ < 8);
            return h;
        }

        private Boolean equalsInTopScope(Object other) {
            return EqualObjectGraphs.withThreadLocal(eq -> CallFrame.equals(this, (CallFrame)other, eq));
        }

        private boolean isStrictTopFrame() {
            CallFrame f = this;
            CallFrame p;
            while ((p = f.parentFrame) != null) {
                f = p;
            }
            return f.idata.isStrict;
        }

        private static Boolean equals(CallFrame f1, CallFrame f2, EqualObjectGraphs equal) {
            while (f1 != f2) {
                if (f1 == null || f2 == null) {
                    return Boolean.FALSE;
                }
                if (!f1.fieldsEqual(f2, equal)) {
                    return Boolean.FALSE;
                }
                f1 = f1.parentFrame;
                f2 = f2.parentFrame;
            }
            return Boolean.TRUE;
        }

        private boolean fieldsEqual(CallFrame other, EqualObjectGraphs equal) {
            return this.frameIndex == other.frameIndex && this.pc == other.pc && Interpreter.compareIdata(this.idata, other.idata) && equal.equalGraphs(this.varSource.stack, other.varSource.stack) && Arrays.equals(this.varSource.sDbl, other.varSource.sDbl) && equal.equalGraphs(this.thisObj, other.thisObj) && equal.equalGraphs(this.fnOrScript, other.fnOrScript) && equal.equalGraphs(this.scope, other.scope);
        }
    }
}


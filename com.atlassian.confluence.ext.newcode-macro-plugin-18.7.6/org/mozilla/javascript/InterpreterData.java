/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import org.mozilla.javascript.Interpreter;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.UintMap;
import org.mozilla.javascript.debug.DebuggableScript;

final class InterpreterData
implements Serializable,
DebuggableScript {
    private static final long serialVersionUID = 5067677351589230234L;
    static final int INITIAL_MAX_ICODE_LENGTH = 1024;
    static final int INITIAL_STRINGTABLE_SIZE = 64;
    static final int INITIAL_NUMBERTABLE_SIZE = 64;
    static final int INITIAL_BIGINTTABLE_SIZE = 64;
    String itsName;
    String itsSourceFile;
    boolean itsNeedsActivation;
    int itsFunctionType;
    String[] itsStringTable;
    double[] itsDoubleTable;
    BigInteger[] itsBigIntTable;
    InterpreterData[] itsNestedFunctions;
    Object[] itsRegExpLiterals;
    Object[] itsTemplateLiterals;
    byte[] itsICode;
    int[] itsExceptionTable;
    int itsMaxVars;
    int itsMaxLocals;
    int itsMaxStack;
    int itsMaxFrameArray;
    String[] argNames;
    boolean[] argIsConst;
    int argCount;
    int itsMaxCalleeArgs;
    String encodedSource;
    int encodedSourceStart;
    int encodedSourceEnd;
    int languageVersion;
    boolean isStrict;
    boolean topLevel;
    boolean isES6Generator;
    Object[] literalIds;
    UintMap longJumps;
    int firstLinePC = -1;
    InterpreterData parentData;
    boolean evalScriptFlag;
    private int icodeHashCode = 0;
    boolean declaredAsVar;
    boolean declaredAsFunctionExpression;

    InterpreterData(int languageVersion, String sourceFile, String encodedSource, boolean isStrict) {
        this.languageVersion = languageVersion;
        this.itsSourceFile = sourceFile;
        this.encodedSource = encodedSource;
        this.isStrict = isStrict;
        this.init();
    }

    InterpreterData(InterpreterData parent) {
        this.parentData = parent;
        this.languageVersion = parent.languageVersion;
        this.itsSourceFile = parent.itsSourceFile;
        this.encodedSource = parent.encodedSource;
        this.isStrict = parent.isStrict;
        this.init();
    }

    private void init() {
        this.itsICode = new byte[1024];
        this.itsStringTable = new String[64];
        this.itsBigIntTable = new BigInteger[64];
    }

    @Override
    public boolean isTopLevel() {
        return this.topLevel;
    }

    @Override
    public boolean isFunction() {
        return this.itsFunctionType != 0;
    }

    @Override
    public String getFunctionName() {
        return this.itsName;
    }

    @Override
    public int getParamCount() {
        return this.argCount;
    }

    @Override
    public int getParamAndVarCount() {
        return this.argNames.length;
    }

    @Override
    public String getParamOrVarName(int index) {
        return this.argNames[index];
    }

    public boolean getParamOrVarConst(int index) {
        return this.argIsConst[index];
    }

    @Override
    public String getSourceName() {
        return this.itsSourceFile;
    }

    @Override
    public boolean isGeneratedScript() {
        return ScriptRuntime.isGeneratedScript(this.itsSourceFile);
    }

    @Override
    public int[] getLineNumbers() {
        return Interpreter.getLineNumbers(this);
    }

    @Override
    public int getFunctionCount() {
        return this.itsNestedFunctions == null ? 0 : this.itsNestedFunctions.length;
    }

    @Override
    public DebuggableScript getFunction(int index) {
        return this.itsNestedFunctions[index];
    }

    @Override
    public DebuggableScript getParent() {
        return this.parentData;
    }

    public int icodeHashCode() {
        int h = this.icodeHashCode;
        if (h == 0) {
            this.icodeHashCode = h = Arrays.hashCode(this.itsICode);
        }
        return h;
    }
}


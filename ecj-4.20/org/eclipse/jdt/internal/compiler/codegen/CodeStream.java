/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CaseLabel;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.util.Util;

public class CodeStream {
    public static FieldBinding[] ImplicitThis = new FieldBinding[0];
    public static final int LABELS_INCREMENT = 5;
    public static final int LOCALS_INCREMENT = 10;
    public static final CompilationResult RESTART_IN_WIDE_MODE = new CompilationResult(null, 0, 0, 0);
    public static final CompilationResult RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE = new CompilationResult(null, 0, 0, 0);
    public int allLocalsCounter;
    public byte[] bCodeStream;
    public ClassFile classFile;
    public int classFileOffset;
    public ConstantPool constantPool;
    public int countLabels;
    public ExceptionLabel[] exceptionLabels = new ExceptionLabel[5];
    public int exceptionLabelsCounter;
    public int generateAttributes;
    static final int L_UNKNOWN = 0;
    static final int L_OPTIMIZABLE = 2;
    static final int L_CANNOT_OPTIMIZE = 4;
    public BranchLabel[] labels = new BranchLabel[5];
    public int lastEntryPC;
    public int lastAbruptCompletion;
    public int[] lineSeparatorPositions;
    public int lineNumberStart;
    public int lineNumberEnd;
    public LocalVariableBinding[] locals = new LocalVariableBinding[10];
    public int maxFieldCount;
    public int maxLocals;
    public AbstractMethodDeclaration methodDeclaration;
    public LambdaExpression lambdaExpression;
    public int[] pcToSourceMap = new int[24];
    public int pcToSourceMapSize;
    public int position;
    public boolean preserveUnusedLocals;
    public int stackDepth;
    public int stackMax;
    public int startingClassFileOffset;
    protected long targetLevel;
    public LocalVariableBinding[] visibleLocals = new LocalVariableBinding[10];
    int visibleLocalsCount;
    public boolean wideMode = false;
    public Stack<TypeBinding> switchSaveTypeBindings = new Stack();
    public int lastSwitchCumulativeSyntheticVars = 0;

    public CodeStream(ClassFile givenClassFile) {
        this.targetLevel = givenClassFile.targetJDK;
        this.generateAttributes = givenClassFile.produceAttributes;
        if ((givenClassFile.produceAttributes & 2) != 0) {
            this.lineSeparatorPositions = givenClassFile.referenceBinding.scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions();
        }
    }

    public static int insertionIndex(int[] pcToSourceMap, int length, int pc) {
        int g = 0;
        int d = length - 2;
        int m = 0;
        while (g <= d) {
            int currentPC;
            m = (g + d) / 2;
            if ((m & 1) != 0) {
                --m;
            }
            if (pc < (currentPC = pcToSourceMap[m])) {
                d = m - 2;
                continue;
            }
            if (pc > currentPC) {
                g = m + 2;
                continue;
            }
            return -1;
        }
        if (pc < pcToSourceMap[m]) {
            return m;
        }
        return m + 2;
    }

    /*
     * Unable to fully structure code
     */
    public static final void sort(int[] tab, int lo0, int hi0, int[] result) {
        block5: {
            lo = lo0;
            hi = hi0;
            if (hi0 <= lo0) break block5;
            mid = tab[lo0 + (hi0 - lo0) / 2];
            ** GOTO lbl16
            {
                ++lo;
                do {
                    if (lo < hi0 && tab[lo] < mid) continue block0;
                    while (hi > lo0 && tab[hi] > mid) {
                        --hi;
                    }
                    if (lo > hi) continue;
                    CodeStream.swap(tab, lo, hi, result);
                    ++lo;
                    --hi;
lbl16:
                    // 3 sources

                } while (lo <= hi);
            }
            if (lo0 < hi) {
                CodeStream.sort(tab, lo0, hi, result);
            }
            if (lo < hi0) {
                CodeStream.sort(tab, lo, hi0, result);
            }
        }
    }

    private static final void swap(int[] a, int i, int j, int[] result) {
        int T = a[i];
        a[i] = a[j];
        a[j] = T;
        T = result[j];
        result[j] = result[i];
        result[i] = T;
    }

    public void aaload() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 50;
        this.pushTypeBindingArray();
    }

    public void aastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 83;
        this.popTypeBinding(3);
    }

    public void aconst_null() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 1;
        this.pushTypeBinding(TypeBinding.NULL);
    }

    public void addDefinitelyAssignedVariables(Scope scope, int initStateIndex) {
        if ((this.generateAttributes & 0x1C) == 0) {
            return;
        }
        int i = 0;
        while (i < this.visibleLocalsCount) {
            LocalVariableBinding localBinding = this.visibleLocals[i];
            if (localBinding != null && this.isDefinitelyAssigned(scope, initStateIndex, localBinding) && (localBinding.initializationCount == 0 || localBinding.initializationPCs[(localBinding.initializationCount - 1 << 1) + 1] != -1)) {
                localBinding.recordInitializationStartPC(this.position);
            }
            ++i;
        }
    }

    public void addLabel(BranchLabel aLabel) {
        if (this.countLabels == this.labels.length) {
            this.labels = new BranchLabel[this.countLabels + 5];
            System.arraycopy(this.labels, 0, this.labels, 0, this.countLabels);
        }
        this.labels[this.countLabels++] = aLabel;
    }

    public void addVariable(LocalVariableBinding localBinding) {
    }

    public void addVisibleLocalVariable(LocalVariableBinding localBinding) {
        if ((this.generateAttributes & 0x1C) == 0) {
            return;
        }
        if (this.visibleLocalsCount >= this.visibleLocals.length) {
            this.visibleLocals = new LocalVariableBinding[this.visibleLocalsCount * 2];
            System.arraycopy(this.visibleLocals, 0, this.visibleLocals, 0, this.visibleLocalsCount);
        }
        this.visibleLocals[this.visibleLocalsCount++] = localBinding;
    }

    public void aload(int iArg) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        this.pushTypeBinding(iArg);
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 25;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 25;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void aload_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(0);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 42;
    }

    public void aload_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(1);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 43;
    }

    public void aload_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(2);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 44;
    }

    public void aload_3() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(3);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 45;
    }

    public void anewarray(TypeBinding typeBinding) {
        this.countLabels = 0;
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -67;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
        this.pushTypeBinding(1, typeBinding);
    }

    public void areturn() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -80;
        this.lastAbruptCompletion = this.position;
    }

    public void arrayAt(int typeBindingID) {
        switch (typeBindingID) {
            case 10: {
                this.iaload();
                break;
            }
            case 3: 
            case 5: {
                this.baload();
                break;
            }
            case 4: {
                this.saload();
                break;
            }
            case 2: {
                this.caload();
                break;
            }
            case 7: {
                this.laload();
                break;
            }
            case 9: {
                this.faload();
                break;
            }
            case 8: {
                this.daload();
                break;
            }
            default: {
                this.aaload();
            }
        }
    }

    public void arrayAtPut(int elementTypeID, boolean valueRequired) {
        switch (elementTypeID) {
            case 10: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.iastore();
                break;
            }
            case 3: 
            case 5: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.bastore();
                break;
            }
            case 4: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.sastore();
                break;
            }
            case 2: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.castore();
                break;
            }
            case 7: {
                if (valueRequired) {
                    this.dup2_x2();
                }
                this.lastore();
                break;
            }
            case 9: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.fastore();
                break;
            }
            case 8: {
                if (valueRequired) {
                    this.dup2_x2();
                }
                this.dastore();
                break;
            }
            default: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.aastore();
            }
        }
    }

    public void arraylength() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -66;
        this.pushTypeBinding(1, TypeBinding.INT);
    }

    public void astore(int iArg) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 58;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 58;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void astore_0() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 75;
    }

    public void astore_1() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 76;
    }

    public void astore_2() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 77;
    }

    public void astore_3() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 78;
    }

    public void athrow() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -65;
        this.lastAbruptCompletion = this.position;
    }

    public void baload() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBindingArray();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 51;
    }

    public void bastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        this.popTypeBinding(3);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 84;
    }

    public void bipush(byte b) {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.BYTE);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 1 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = 16;
        this.bCodeStream[this.classFileOffset++] = b;
    }

    public void caload() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBindingArray();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 52;
    }

    public void castore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        this.popTypeBinding(3);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 85;
    }

    public void checkcast(int baseId) {
        this.countLabels = 0;
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -64;
        switch (baseId) {
            case 3: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangByteConstantPoolName));
                break;
            }
            case 4: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangShortConstantPoolName));
                break;
            }
            case 2: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangCharacterConstantPoolName));
                break;
            }
            case 10: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangIntegerConstantPoolName));
                break;
            }
            case 7: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangLongConstantPoolName));
                break;
            }
            case 9: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangFloatConstantPoolName));
                break;
            }
            case 8: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangDoubleConstantPoolName));
                break;
            }
            case 5: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangBooleanConstantPoolName));
            }
        }
        this.pushTypeBinding(1, TypeBinding.wellKnownBaseType(baseId));
    }

    public void checkcast(TypeBinding typeBinding) {
        this.checkcast(null, typeBinding, -1);
    }

    public void checkcast(TypeReference typeReference, TypeBinding typeBinding, int currentPosition) {
        this.countLabels = 0;
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -64;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
        this.pushTypeBinding(1, typeBinding);
    }

    public void d2f() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(1, TypeBinding.FLOAT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -112;
    }

    public void d2i() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(1, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -114;
    }

    public void d2l() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -113;
        this.pushTypeBinding(1, TypeBinding.LONG);
    }

    public void dadd() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 99;
        this.pushTypeBinding(2, TypeBinding.DOUBLE);
    }

    public void daload() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 49;
        this.pushTypeBindingArray();
    }

    public void dastore() {
        this.countLabels = 0;
        this.stackDepth -= 4;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 82;
        this.popTypeBinding(3);
    }

    public void dcmpg() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -104;
        this.pushTypeBinding(2, TypeBinding.INT);
    }

    public void dcmpl() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -105;
        this.pushTypeBinding(2, TypeBinding.INT);
    }

    public void dconst_0() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.DOUBLE);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 14;
    }

    public void dconst_1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.DOUBLE);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 15;
    }

    public void ddiv() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.DOUBLE);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 111;
    }

    public void decrStackSize(int offset) {
        this.stackDepth -= offset;
    }

    public void dload(int iArg) {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < iArg + 2) {
            this.maxLocals = iArg + 2;
        }
        this.pushTypeBinding(TypeBinding.DOUBLE);
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 24;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 24;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void dload_0() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.DOUBLE);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 38;
    }

    public void dload_1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.DOUBLE);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 39;
    }

    public void dload_2() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.DOUBLE);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 40;
    }

    public void dload_3() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.DOUBLE);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 41;
    }

    public void dmul() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.DOUBLE);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 107;
    }

    public void dneg() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 119;
        this.pushTypeBinding(1, TypeBinding.DOUBLE);
    }

    public void drem() {
        this.countLabels = 0;
        this.pushTypeBinding(2, TypeBinding.DOUBLE);
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 115;
    }

    public void dreturn() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -81;
        this.lastAbruptCompletion = this.position;
    }

    public void dstore(int iArg) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals <= iArg + 1) {
            this.maxLocals = iArg + 2;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 57;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 57;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void dstore_0() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 71;
    }

    public void dstore_1() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 72;
    }

    public void dstore_2() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 73;
    }

    public void dstore_3() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 74;
    }

    public void dsub() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.DOUBLE);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 103;
    }

    public void dup() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.isSwitchStackTrackingActive()) {
            this.pushTypeBinding(this.switchSaveTypeBindings.peek());
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 89;
    }

    private void adjustTypeBindingStackForDupX1() {
        if (this.isSwitchStackTrackingActive()) {
            TypeBinding[] topStack = new TypeBinding[]{this.popTypeBinding(), this.popTypeBinding()};
            this.pushTypeBinding(topStack[0]);
            this.pushTypeBinding(topStack[1]);
            this.pushTypeBinding(topStack[0]);
        }
    }

    public void dup_x1() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.adjustTypeBindingStackForDupX1();
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 90;
    }

    private void adjustTypeBindingStackForDupX2() {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        TypeBinding val1 = this.popTypeBinding();
        TypeBinding val2 = this.popTypeBinding();
        if (TypeIds.getCategory(val1.id) == 1) {
            if (TypeIds.getCategory(val2.id) == 2) {
                this.pushTypeBinding(val1);
                this.pushTypeBinding(val2);
                this.pushTypeBinding(val1);
            } else {
                TypeBinding val3 = this.popTypeBinding();
                if (TypeIds.getCategory(val3.id) == 1) {
                    this.pushTypeBinding(val1);
                    this.pushTypeBinding(val3);
                    this.pushTypeBinding(val2);
                    this.pushTypeBinding(val1);
                }
            }
        }
    }

    public void dup_x2() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.adjustTypeBindingStackForDupX2();
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 91;
    }

    private void adjustTypeBindingStackForDup2() {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        TypeBinding val1 = this.popTypeBinding();
        if (TypeIds.getCategory(val1.id) == 2) {
            this.pushTypeBinding(val1);
            this.pushTypeBinding(val1);
        } else {
            TypeBinding val2 = this.popTypeBinding();
            if (TypeIds.getCategory(val2.id) == 1) {
                this.pushTypeBinding(val2);
                this.pushTypeBinding(val1);
                this.pushTypeBinding(val2);
                this.pushTypeBinding(val1);
            }
        }
    }

    public void dup2() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.adjustTypeBindingStackForDup2();
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 92;
    }

    private void adjustTypeBindingStackForDup2X1() {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        TypeBinding val1 = this.popTypeBinding();
        TypeBinding val2 = this.popTypeBinding();
        if (TypeIds.getCategory(val1.id) == 2) {
            if (TypeIds.getCategory(val2.id) == 1) {
                this.pushTypeBinding(val1);
                this.pushTypeBinding(val2);
                this.pushTypeBinding(val1);
            }
        } else if (TypeIds.getCategory(val2.id) == 1) {
            TypeBinding val3 = this.popTypeBinding();
            if (TypeIds.getCategory(val3.id) == 1) {
                this.pushTypeBinding(val2);
                this.pushTypeBinding(val1);
                this.pushTypeBinding(val3);
                this.pushTypeBinding(val2);
                this.pushTypeBinding(val1);
            }
        }
    }

    public void dup2_x1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.adjustTypeBindingStackForDup2X1();
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 93;
    }

    private void adjustTypeBindingStackForDup2X2() {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        TypeBinding val1 = this.popTypeBinding();
        if (TypeIds.getCategory(val1.id) == 2) {
            TypeBinding val2 = this.popTypeBinding();
            if (TypeIds.getCategory(val2.id) == 2) {
                this.pushTypeBinding(val1);
                this.pushTypeBinding(val2);
                this.pushTypeBinding(val1);
            } else {
                TypeBinding val3 = this.popTypeBinding();
                if (TypeIds.getCategory(val3.id) == 1) {
                    this.pushTypeBinding(val1);
                    this.pushTypeBinding(val3);
                    this.pushTypeBinding(val2);
                    this.pushTypeBinding(val1);
                }
            }
            this.pushTypeBinding(val1);
            this.pushTypeBinding(val1);
        } else {
            TypeBinding val2 = this.popTypeBinding();
            if (TypeIds.getCategory(val2.id) == 1) {
                TypeBinding val3 = this.popTypeBinding();
                if (TypeIds.getCategory(val3.id) == 2) {
                    this.pushTypeBinding(val2);
                    this.pushTypeBinding(val1);
                    this.pushTypeBinding(val3);
                    this.pushTypeBinding(val2);
                    this.pushTypeBinding(val1);
                } else {
                    TypeBinding val4 = this.popTypeBinding();
                    if (TypeIds.getCategory(val4.id) == 1) {
                        this.pushTypeBinding(val2);
                        this.pushTypeBinding(val1);
                        this.pushTypeBinding(val4);
                        this.pushTypeBinding(val3);
                        this.pushTypeBinding(val2);
                        this.pushTypeBinding(val1);
                    }
                }
            }
        }
    }

    public void dup2_x2() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.adjustTypeBindingStackForDup2X2();
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 94;
    }

    public void exitUserScope(BlockScope currentScope) {
        if ((this.generateAttributes & 0x1C) == 0) {
            return;
        }
        int index = this.visibleLocalsCount - 1;
        while (index >= 0) {
            LocalVariableBinding visibleLocal = this.visibleLocals[index];
            if (visibleLocal == null || visibleLocal.declaringScope != currentScope) {
                --index;
                continue;
            }
            if (visibleLocal.initializationCount > 0) {
                visibleLocal.recordInitializationEndPC(this.position);
            }
            this.visibleLocals[index--] = null;
        }
    }

    public void exitUserScope(BlockScope currentScope, LocalVariableBinding binding) {
        if ((this.generateAttributes & 0x1C) == 0) {
            return;
        }
        int index = this.visibleLocalsCount - 1;
        while (index >= 0) {
            LocalVariableBinding visibleLocal = this.visibleLocals[index];
            if (visibleLocal == null || visibleLocal.declaringScope != currentScope || visibleLocal == binding) {
                --index;
                continue;
            }
            if (visibleLocal.initializationCount > 0) {
                visibleLocal.recordInitializationEndPC(this.position);
            }
            this.visibleLocals[index--] = null;
        }
    }

    public void f2d() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(1, TypeBinding.DOUBLE);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -115;
    }

    public void f2i() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -117;
        this.pushTypeBinding(1, TypeBinding.INT);
    }

    public void f2l() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(1, TypeBinding.LONG);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -116;
    }

    public void fadd() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.FLOAT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 98;
    }

    public void faload() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBindingArray();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 48;
    }

    public void fastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        this.popTypeBinding(3);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 81;
    }

    public void fcmpg() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.FLOAT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -106;
    }

    public void fcmpl() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.FLOAT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -107;
    }

    public void fconst_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.FLOAT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 11;
    }

    public void fconst_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.FLOAT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 12;
    }

    public void fconst_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.FLOAT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 13;
    }

    public void fdiv() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.FLOAT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 110;
    }

    public void fieldAccess(byte opcode, FieldBinding fieldBinding, TypeBinding declaringClass) {
        int returnTypeSize;
        if (declaringClass == null) {
            declaringClass = fieldBinding.declaringClass;
        }
        if ((declaringClass.tagBits & 0x800L) != 0L) {
            Util.recordNestedType(this.classFile, declaringClass);
        }
        TypeBinding returnType = fieldBinding.type;
        switch (returnType.id) {
            case 7: 
            case 8: {
                returnTypeSize = 2;
                break;
            }
            default: {
                returnTypeSize = 1;
            }
        }
        this.fieldAccess(opcode, returnTypeSize, declaringClass.constantPoolName(), fieldBinding.name, returnType.signature(), returnType.id, returnType);
    }

    private void fieldAccess(byte opcode, int returnTypeSize, char[] declaringClass, char[] fieldName, char[] signature, int typeId) {
        this.fieldAccess(opcode, returnTypeSize, declaringClass, fieldName, signature, typeId, null);
    }

    private void fieldAccess(byte opcode, int returnTypeSize, char[] declaringClass, char[] fieldName, char[] signature, int typeId, TypeBinding typeBinding) {
        this.countLabels = 0;
        switch (opcode) {
            case -76: {
                if (returnTypeSize == 2) {
                    ++this.stackDepth;
                }
                this.pushTypeBinding(1, typeBinding);
                break;
            }
            case -78: {
                if (returnTypeSize == 2) {
                    this.stackDepth += 2;
                    this.pushTypeBinding(typeBinding);
                    break;
                }
                ++this.stackDepth;
                this.pushTypeBinding(typeBinding);
                break;
            }
            case -75: {
                if (returnTypeSize == 2) {
                    this.stackDepth -= 3;
                    this.popTypeBinding(2);
                    break;
                }
                this.stackDepth -= 2;
                this.popTypeBinding(2);
                break;
            }
            case -77: {
                if (returnTypeSize == 2) {
                    this.stackDepth -= 2;
                    this.popTypeBinding();
                    break;
                }
                --this.stackDepth;
                this.popTypeBinding();
            }
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = opcode;
        this.writeUnsignedShort(this.constantPool.literalIndexForField(declaringClass, fieldName, signature));
    }

    public void fload(int iArg) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        this.pushTypeBinding(TypeBinding.FLOAT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 23;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 23;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void fload_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.FLOAT);
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 34;
    }

    public void fload_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.FLOAT);
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 35;
    }

    public void fload_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.FLOAT);
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 36;
    }

    public void fload_3() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.FLOAT);
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 37;
    }

    public void fmul() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.FLOAT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 106;
    }

    public void fneg() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 118;
        this.pushTypeBinding(1, TypeBinding.FLOAT);
    }

    public void frem() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.FLOAT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 114;
    }

    public void freturn() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -82;
        this.lastAbruptCompletion = this.position;
    }

    public void fstore(int iArg) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 56;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 56;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void fstore_0() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 67;
    }

    public void fstore_1() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 68;
    }

    public void fstore_2() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 69;
    }

    public void fstore_3() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 70;
    }

    public void fsub() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.FLOAT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 102;
    }

    public void generateBoxingConversion(int unboxedTypeID) {
        switch (unboxedTypeID) {
            case 3: {
                if (this.targetLevel >= 0x310000L) {
                    this.invoke((byte)-72, 1, 1, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.ValueOf, ConstantPool.byteByteSignature, unboxedTypeID, TypeBinding.BYTE);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)-73, 2, 0, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.Init, ConstantPool.ByteConstrSignature, unboxedTypeID, TypeBinding.BYTE);
                break;
            }
            case 4: {
                if (this.targetLevel >= 0x310000L) {
                    this.invoke((byte)-72, 1, 1, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.ValueOf, ConstantPool.shortShortSignature, unboxedTypeID, TypeBinding.SHORT);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)-73, 2, 0, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.Init, ConstantPool.ShortConstrSignature, unboxedTypeID, TypeBinding.SHORT);
                break;
            }
            case 2: {
                if (this.targetLevel >= 0x310000L) {
                    this.invoke((byte)-72, 1, 1, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.ValueOf, ConstantPool.charCharacterSignature, unboxedTypeID, TypeBinding.CHAR);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)-73, 2, 0, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.Init, ConstantPool.CharConstrSignature, unboxedTypeID, TypeBinding.CHAR);
                break;
            }
            case 10: {
                if (this.targetLevel >= 0x310000L) {
                    this.invoke((byte)-72, 1, 1, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.ValueOf, ConstantPool.IntIntegerSignature, unboxedTypeID, TypeBinding.INT);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)-73, 2, 0, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.Init, ConstantPool.IntConstrSignature, unboxedTypeID, TypeBinding.INT);
                break;
            }
            case 7: {
                if (this.targetLevel >= 0x310000L) {
                    this.invoke((byte)-72, 2, 1, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.ValueOf, ConstantPool.longLongSignature, unboxedTypeID, TypeBinding.LONG);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x2();
                this.dup_x2();
                this.pop();
                this.invoke((byte)-73, 3, 0, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.Init, ConstantPool.LongConstrSignature, unboxedTypeID, TypeBinding.LONG);
                break;
            }
            case 9: {
                if (this.targetLevel >= 0x310000L) {
                    this.invoke((byte)-72, 1, 1, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.ValueOf, ConstantPool.floatFloatSignature, unboxedTypeID, TypeBinding.FLOAT);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)-73, 2, 0, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.Init, ConstantPool.FloatConstrSignature, unboxedTypeID, TypeBinding.FLOAT);
                break;
            }
            case 8: {
                if (this.targetLevel >= 0x310000L) {
                    this.invoke((byte)-72, 2, 1, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.ValueOf, ConstantPool.doubleDoubleSignature, unboxedTypeID, TypeBinding.DOUBLE);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x2();
                this.dup_x2();
                this.pop();
                this.invoke((byte)-73, 3, 0, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.Init, ConstantPool.DoubleConstrSignature, unboxedTypeID, TypeBinding.DOUBLE);
                break;
            }
            case 5: {
                if (this.targetLevel >= 0x310000L) {
                    this.invoke((byte)-72, 1, 1, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.ValueOf, ConstantPool.booleanBooleanSignature, unboxedTypeID, TypeBinding.BOOLEAN);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)-73, 2, 0, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.Init, ConstantPool.BooleanConstrSignature, unboxedTypeID, TypeBinding.BOOLEAN);
            }
        }
    }

    public void generateClassLiteralAccessForType(Scope scope, TypeBinding accessedType, FieldBinding syntheticFieldBinding) {
        if (accessedType.isBaseType() && accessedType != TypeBinding.NULL) {
            this.getTYPE(accessedType.id);
            return;
        }
        if (this.targetLevel >= 0x310000L) {
            this.ldc(accessedType);
        } else {
            BranchLabel endLabel = new BranchLabel(this);
            if (syntheticFieldBinding != null) {
                this.fieldAccess((byte)-78, syntheticFieldBinding, null);
                this.dup();
                this.ifnonnull(endLabel);
                this.pop();
            }
            ExceptionLabel classNotFoundExceptionHandler = new ExceptionLabel(this, TypeBinding.NULL);
            classNotFoundExceptionHandler.placeStart();
            this.ldc(accessedType == TypeBinding.NULL ? "java.lang.Object" : String.valueOf(accessedType.constantPoolName()).replace('/', '.'));
            this.invokeClassForName();
            classNotFoundExceptionHandler.placeEnd();
            if (syntheticFieldBinding != null) {
                this.dup();
                this.fieldAccess((byte)-77, syntheticFieldBinding, null);
            }
            this.goto_(endLabel);
            int savedStackDepth = this.stackDepth;
            int switchSaveTypeBindingsStackSize = this.switchSaveTypeBindings.size();
            this.pushExceptionOnStack(scope.getJavaLangClassNotFoundException());
            classNotFoundExceptionHandler.place();
            this.newNoClassDefFoundError();
            this.dup_x1();
            this.swap();
            this.invokeThrowableGetMessage();
            this.invokeNoClassDefFoundErrorStringConstructor();
            this.athrow();
            endLabel.place();
            this.stackDepth = savedStackDepth;
            this.popTypeBinding(this.switchSaveTypeBindings.size() - switchSaveTypeBindingsStackSize);
        }
    }

    public final void generateCodeAttributeForProblemMethod(String problemMessage) {
        this.newJavaLangError();
        this.dup();
        this.ldc(problemMessage);
        this.invokeJavaLangErrorConstructor();
        this.athrow();
    }

    public void generateConstant(Constant constant, int implicitConversionCode) {
        int targetTypeID = (implicitConversionCode & 0xFF) >> 4;
        if (targetTypeID == 0) {
            targetTypeID = constant.typeID();
        }
        switch (targetTypeID) {
            case 5: {
                this.generateInlinedValue(constant.booleanValue());
                break;
            }
            case 2: {
                this.generateInlinedValue(constant.charValue());
                break;
            }
            case 3: {
                this.generateInlinedValue(constant.byteValue());
                break;
            }
            case 4: {
                this.generateInlinedValue(constant.shortValue());
                break;
            }
            case 10: {
                this.generateInlinedValue(constant.intValue());
                break;
            }
            case 7: {
                this.generateInlinedValue(constant.longValue());
                break;
            }
            case 9: {
                this.generateInlinedValue(constant.floatValue());
                break;
            }
            case 8: {
                this.generateInlinedValue(constant.doubleValue());
                break;
            }
            case 11: {
                this.ldc(constant.stringValue());
            }
        }
        if ((implicitConversionCode & 0x200) != 0) {
            this.generateBoxingConversion(targetTypeID);
        }
    }

    public void generateEmulatedReadAccessForField(FieldBinding fieldBinding) {
        this.generateEmulationForField(fieldBinding);
        this.swap();
        this.invokeJavaLangReflectFieldGetter(fieldBinding.type);
        if (!fieldBinding.type.isBaseType()) {
            this.checkcast(fieldBinding.type);
        }
    }

    public void generateEmulatedWriteAccessForField(FieldBinding fieldBinding) {
        this.invokeJavaLangReflectFieldSetter(fieldBinding.type);
    }

    public void generateEmulationForConstructor(Scope scope, MethodBinding methodBinding) {
        this.ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
        this.invokeClassForName();
        int paramLength = methodBinding.parameters.length;
        this.generateInlinedValue(paramLength);
        this.newArray(scope.createArrayType(scope.getType(TypeConstants.JAVA_LANG_CLASS, 3), 1));
        if (paramLength > 0) {
            this.dup();
            int i = 0;
            while (i < paramLength) {
                this.generateInlinedValue(i);
                TypeBinding parameter = methodBinding.parameters[i];
                if (parameter.isBaseType()) {
                    this.getTYPE(parameter.id);
                } else if (parameter.isArrayType()) {
                    ArrayBinding array = (ArrayBinding)parameter;
                    if (array.leafComponentType.isBaseType()) {
                        this.getTYPE(array.leafComponentType.id);
                    } else {
                        this.ldc(String.valueOf(array.leafComponentType.constantPoolName()).replace('/', '.'));
                        this.invokeClassForName();
                    }
                    int dimensions = array.dimensions;
                    this.generateInlinedValue(dimensions);
                    this.newarray(10);
                    this.invokeArrayNewInstance();
                    this.invokeObjectGetClass();
                } else {
                    this.ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
                    this.invokeClassForName();
                }
                this.aastore();
                if (i < paramLength - 1) {
                    this.dup();
                }
                ++i;
            }
        }
        this.invokeClassGetDeclaredConstructor();
        this.dup();
        this.iconst_1();
        this.invokeAccessibleObjectSetAccessible();
    }

    public void generateEmulationForField(FieldBinding fieldBinding) {
        this.ldc(String.valueOf(fieldBinding.declaringClass.constantPoolName()).replace('/', '.'));
        this.invokeClassForName();
        this.ldc(String.valueOf(fieldBinding.name));
        this.invokeClassGetDeclaredField();
        this.dup();
        this.iconst_1();
        this.invokeAccessibleObjectSetAccessible();
    }

    public void generateEmulationForMethod(Scope scope, MethodBinding methodBinding) {
        this.ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
        this.invokeClassForName();
        this.ldc(String.valueOf(methodBinding.selector));
        int paramLength = methodBinding.parameters.length;
        this.generateInlinedValue(paramLength);
        this.newArray(scope.createArrayType(scope.getType(TypeConstants.JAVA_LANG_CLASS, 3), 1));
        if (paramLength > 0) {
            this.dup();
            int i = 0;
            while (i < paramLength) {
                this.generateInlinedValue(i);
                TypeBinding parameter = methodBinding.parameters[i];
                if (parameter.isBaseType()) {
                    this.getTYPE(parameter.id);
                } else if (parameter.isArrayType()) {
                    ArrayBinding array = (ArrayBinding)parameter;
                    if (array.leafComponentType.isBaseType()) {
                        this.getTYPE(array.leafComponentType.id);
                    } else {
                        this.ldc(String.valueOf(array.leafComponentType.constantPoolName()).replace('/', '.'));
                        this.invokeClassForName();
                    }
                    int dimensions = array.dimensions;
                    this.generateInlinedValue(dimensions);
                    this.newarray(10);
                    this.invokeArrayNewInstance();
                    this.invokeObjectGetClass();
                } else {
                    this.ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
                    this.invokeClassForName();
                }
                this.aastore();
                if (i < paramLength - 1) {
                    this.dup();
                }
                ++i;
            }
        }
        this.invokeClassGetDeclaredMethod();
        this.dup();
        this.iconst_1();
        this.invokeAccessibleObjectSetAccessible();
    }

    public void generateImplicitConversion(int implicitConversionCode) {
        int typeId;
        if ((implicitConversionCode & 0x400) != 0) {
            typeId = implicitConversionCode & 0xF;
            this.generateUnboxingConversion(typeId);
        }
        switch (implicitConversionCode & 0xFF) {
            case 41: {
                this.f2i();
                this.i2c();
                break;
            }
            case 40: {
                this.d2i();
                this.i2c();
                break;
            }
            case 35: 
            case 36: 
            case 42: {
                this.i2c();
                break;
            }
            case 39: {
                this.l2i();
                this.i2c();
                break;
            }
            case 146: 
            case 147: 
            case 148: 
            case 154: {
                this.i2f();
                break;
            }
            case 152: {
                this.d2f();
                break;
            }
            case 151: {
                this.l2f();
                break;
            }
            case 57: {
                this.f2i();
                this.i2b();
                break;
            }
            case 56: {
                this.d2i();
                this.i2b();
                break;
            }
            case 50: 
            case 52: 
            case 58: {
                this.i2b();
                break;
            }
            case 55: {
                this.l2i();
                this.i2b();
                break;
            }
            case 130: 
            case 131: 
            case 132: 
            case 138: {
                this.i2d();
                break;
            }
            case 137: {
                this.f2d();
                break;
            }
            case 135: {
                this.l2d();
                break;
            }
            case 66: 
            case 67: 
            case 74: {
                this.i2s();
                break;
            }
            case 72: {
                this.d2i();
                this.i2s();
                break;
            }
            case 71: {
                this.l2i();
                this.i2s();
                break;
            }
            case 73: {
                this.f2i();
                this.i2s();
                break;
            }
            case 168: {
                this.d2i();
                break;
            }
            case 169: {
                this.f2i();
                break;
            }
            case 167: {
                this.l2i();
                break;
            }
            case 114: 
            case 115: 
            case 116: 
            case 122: {
                this.i2l();
                break;
            }
            case 120: {
                this.d2l();
                break;
            }
            case 121: {
                this.f2l();
                break;
            }
            case 33: 
            case 49: 
            case 65: 
            case 81: 
            case 113: 
            case 129: 
            case 145: 
            case 161: {
                int runtimeType = (implicitConversionCode & 0xFF) >> 4;
                this.checkcast(runtimeType);
                this.generateUnboxingConversion(runtimeType);
            }
        }
        if ((implicitConversionCode & 0x200) != 0) {
            typeId = (implicitConversionCode & 0xFF) >> 4;
            this.generateBoxingConversion(typeId);
        }
    }

    public void generateInlinedValue(boolean inlinedValue) {
        if (inlinedValue) {
            this.iconst_1();
        } else {
            this.iconst_0();
        }
    }

    public void generateInlinedValue(byte inlinedValue) {
        switch (inlinedValue) {
            case -1: {
                this.iconst_m1();
                break;
            }
            case 0: {
                this.iconst_0();
                break;
            }
            case 1: {
                this.iconst_1();
                break;
            }
            case 2: {
                this.iconst_2();
                break;
            }
            case 3: {
                this.iconst_3();
                break;
            }
            case 4: {
                this.iconst_4();
                break;
            }
            case 5: {
                this.iconst_5();
                break;
            }
            default: {
                if (-128 > inlinedValue || inlinedValue > 127) break;
                this.bipush(inlinedValue);
                return;
            }
        }
    }

    public void generateInlinedValue(char inlinedValue) {
        switch (inlinedValue) {
            case '\u0000': {
                this.iconst_0();
                break;
            }
            case '\u0001': {
                this.iconst_1();
                break;
            }
            case '\u0002': {
                this.iconst_2();
                break;
            }
            case '\u0003': {
                this.iconst_3();
                break;
            }
            case '\u0004': {
                this.iconst_4();
                break;
            }
            case '\u0005': {
                this.iconst_5();
                break;
            }
            default: {
                if ('\u0006' <= inlinedValue && inlinedValue <= '\u007f') {
                    this.bipush((byte)inlinedValue);
                    return;
                }
                if ('\u0080' <= inlinedValue && inlinedValue <= Short.MAX_VALUE) {
                    this.sipush(inlinedValue);
                    return;
                }
                this.ldc(inlinedValue);
            }
        }
    }

    public void generateInlinedValue(double inlinedValue) {
        if (inlinedValue == 0.0) {
            if (Double.doubleToLongBits(inlinedValue) != 0L) {
                this.ldc2_w(inlinedValue);
            } else {
                this.dconst_0();
            }
            return;
        }
        if (inlinedValue == 1.0) {
            this.dconst_1();
            return;
        }
        this.ldc2_w(inlinedValue);
    }

    public void generateInlinedValue(float inlinedValue) {
        if (inlinedValue == 0.0f) {
            if (Float.floatToIntBits(inlinedValue) != 0) {
                this.ldc(inlinedValue);
            } else {
                this.fconst_0();
            }
            return;
        }
        if (inlinedValue == 1.0f) {
            this.fconst_1();
            return;
        }
        if (inlinedValue == 2.0f) {
            this.fconst_2();
            return;
        }
        this.ldc(inlinedValue);
    }

    public void generateInlinedValue(int inlinedValue) {
        switch (inlinedValue) {
            case -1: {
                this.iconst_m1();
                break;
            }
            case 0: {
                this.iconst_0();
                break;
            }
            case 1: {
                this.iconst_1();
                break;
            }
            case 2: {
                this.iconst_2();
                break;
            }
            case 3: {
                this.iconst_3();
                break;
            }
            case 4: {
                this.iconst_4();
                break;
            }
            case 5: {
                this.iconst_5();
                break;
            }
            default: {
                if (-128 <= inlinedValue && inlinedValue <= 127) {
                    this.bipush((byte)inlinedValue);
                    return;
                }
                if (Short.MIN_VALUE <= inlinedValue && inlinedValue <= Short.MAX_VALUE) {
                    this.sipush(inlinedValue);
                    return;
                }
                this.ldc(inlinedValue);
            }
        }
    }

    public void generateInlinedValue(long inlinedValue) {
        if (inlinedValue == 0L) {
            this.lconst_0();
            return;
        }
        if (inlinedValue == 1L) {
            this.lconst_1();
            return;
        }
        this.ldc2_w(inlinedValue);
    }

    public void generateInlinedValue(short inlinedValue) {
        switch (inlinedValue) {
            case -1: {
                this.iconst_m1();
                break;
            }
            case 0: {
                this.iconst_0();
                break;
            }
            case 1: {
                this.iconst_1();
                break;
            }
            case 2: {
                this.iconst_2();
                break;
            }
            case 3: {
                this.iconst_3();
                break;
            }
            case 4: {
                this.iconst_4();
                break;
            }
            case 5: {
                this.iconst_5();
                break;
            }
            default: {
                if (-128 <= inlinedValue && inlinedValue <= 127) {
                    this.bipush((byte)inlinedValue);
                    return;
                }
                this.sipush(inlinedValue);
            }
        }
    }

    public void generateOuterAccess(Object[] mappingSequence, ASTNode invocationSite, Binding target, Scope scope) {
        if (mappingSequence == null) {
            if (target instanceof LocalVariableBinding) {
                scope.problemReporter().needImplementation(invocationSite);
            } else {
                scope.problemReporter().noSuchEnclosingInstance((ReferenceBinding)target, invocationSite, false);
            }
            return;
        }
        if (mappingSequence == BlockScope.NoEnclosingInstanceInConstructorCall) {
            scope.problemReporter().noSuchEnclosingInstance((ReferenceBinding)target, invocationSite, true);
            return;
        }
        if (mappingSequence == BlockScope.NoEnclosingInstanceInStaticContext) {
            scope.problemReporter().noSuchEnclosingInstance((ReferenceBinding)target, invocationSite, false);
            return;
        }
        if (mappingSequence == BlockScope.EmulationPathToImplicitThis) {
            this.aload_0();
            return;
        }
        if (mappingSequence[0] instanceof FieldBinding) {
            FieldBinding fieldBinding = (FieldBinding)mappingSequence[0];
            this.aload_0();
            this.fieldAccess((byte)-76, fieldBinding, null);
        } else {
            this.load((LocalVariableBinding)mappingSequence[0]);
        }
        int i = 1;
        int length = mappingSequence.length;
        while (i < length) {
            if (mappingSequence[i] instanceof FieldBinding) {
                FieldBinding fieldBinding = (FieldBinding)mappingSequence[i];
                this.fieldAccess((byte)-76, fieldBinding, null);
            } else {
                this.invoke((byte)-72, (MethodBinding)mappingSequence[i], null);
            }
            ++i;
        }
    }

    public void generateReturnBytecode(Expression expression) {
        if (expression == null) {
            this.return_();
        } else {
            int implicitConversion = expression.implicitConversion;
            if ((implicitConversion & 0x200) != 0) {
                this.areturn();
                return;
            }
            int runtimeType = (implicitConversion & 0xFF) >> 4;
            switch (runtimeType) {
                case 5: 
                case 10: {
                    this.ireturn();
                    break;
                }
                case 9: {
                    this.freturn();
                    break;
                }
                case 7: {
                    this.lreturn();
                    break;
                }
                case 8: {
                    this.dreturn();
                    break;
                }
                default: {
                    this.areturn();
                }
            }
        }
    }

    public void generateStringConcatenationAppend(BlockScope blockScope, Expression oper1, Expression oper2) {
        int pc;
        if (oper1 == null) {
            this.newStringContatenation();
            this.dup_x1();
            this.swap();
            this.invokeStringValueOf(1);
            this.invokeStringConcatenationStringConstructor();
        } else {
            pc = this.position;
            oper1.generateOptimizedStringConcatenationCreation(blockScope, this, oper1.implicitConversion & 0xF);
            this.recordPositionsFrom(pc, oper1.sourceStart);
        }
        pc = this.position;
        oper2.generateOptimizedStringConcatenation(blockScope, this, oper2.implicitConversion & 0xF);
        this.recordPositionsFrom(pc, oper2.sourceStart);
        this.invokeStringConcatenationToString();
    }

    public void generateSyntheticBodyForConstructorAccess(SyntheticMethodBinding accessBinding) {
        TypeBinding type;
        int i;
        SyntheticArgumentBinding[] syntheticArguments;
        this.initializeMaxLocals(accessBinding);
        MethodBinding constructorBinding = accessBinding.targetMethod;
        TypeBinding[] parameters = constructorBinding.parameters;
        int length = parameters.length;
        int resolvedPosition = 1;
        this.aload_0();
        ReferenceBinding declaringClass = constructorBinding.declaringClass;
        if (declaringClass.erasure().id == 41 || ((TypeBinding)declaringClass).isEnum()) {
            this.aload_1();
            this.iload_2();
            resolvedPosition += 2;
        }
        if (declaringClass.isNestedType()) {
            NestedTypeBinding nestedType = (NestedTypeBinding)declaringClass;
            syntheticArguments = nestedType.syntheticEnclosingInstances();
            i = 0;
            while (i < (syntheticArguments == null ? 0 : syntheticArguments.length)) {
                type = syntheticArguments[i].type;
                this.load(type, resolvedPosition);
                switch (type.id) {
                    case 7: 
                    case 8: {
                        resolvedPosition += 2;
                        break;
                    }
                    default: {
                        ++resolvedPosition;
                    }
                }
                ++i;
            }
        }
        int i2 = 0;
        while (i2 < length) {
            TypeBinding parameter = parameters[i2];
            this.load(parameter, resolvedPosition);
            switch (parameter.id) {
                case 7: 
                case 8: {
                    resolvedPosition += 2;
                    break;
                }
                default: {
                    ++resolvedPosition;
                }
            }
            ++i2;
        }
        if (declaringClass.isNestedType()) {
            NestedTypeBinding nestedType = (NestedTypeBinding)declaringClass;
            syntheticArguments = nestedType.syntheticOuterLocalVariables();
            i = 0;
            while (i < (syntheticArguments == null ? 0 : syntheticArguments.length)) {
                type = syntheticArguments[i].type;
                this.load(type, resolvedPosition);
                switch (type.id) {
                    case 7: 
                    case 8: {
                        resolvedPosition += 2;
                        break;
                    }
                    default: {
                        ++resolvedPosition;
                    }
                }
                ++i;
            }
        }
        this.invoke((byte)-73, constructorBinding, null);
        this.return_();
    }

    public void generateSyntheticBodyForArrayConstructor(SyntheticMethodBinding methodBinding) {
        this.initializeMaxLocals(methodBinding);
        this.iload_0();
        this.newArray(null, null, (ArrayBinding)methodBinding.returnType);
        this.areturn();
    }

    public void generateSyntheticBodyForArrayClone(SyntheticMethodBinding methodBinding) {
        this.initializeMaxLocals(methodBinding);
        TypeBinding arrayType = methodBinding.parameters[0];
        this.aload_0();
        this.invoke((byte)-74, 1, 1, arrayType.signature(), ConstantPool.Clone, ConstantPool.CloneSignature, this.getPopularBinding(ConstantPool.JavaLangObjectConstantPoolName));
        this.checkcast(arrayType);
        this.areturn();
    }

    public void generateSyntheticBodyForFactoryMethod(SyntheticMethodBinding methodBinding) {
        this.initializeMaxLocals(methodBinding);
        MethodBinding constructorBinding = methodBinding.targetMethod;
        TypeBinding[] parameters = methodBinding.parameters;
        int length = parameters.length;
        this.new_(constructorBinding.declaringClass);
        this.dup();
        int resolvedPosition = 0;
        int i = 0;
        while (i < length) {
            TypeBinding parameter = parameters[i];
            this.load(parameter, resolvedPosition);
            switch (parameter.id) {
                case 7: 
                case 8: {
                    resolvedPosition += 2;
                    break;
                }
                default: {
                    ++resolvedPosition;
                }
            }
            ++i;
        }
        i = 0;
        while (i < methodBinding.fakePaddedParameters) {
            this.aconst_null();
            ++i;
        }
        this.invoke((byte)-73, constructorBinding, null);
        this.areturn();
    }

    public void generateSyntheticBodyForEnumValueOf(SyntheticMethodBinding methodBinding) {
        this.initializeMaxLocals(methodBinding);
        ReferenceBinding declaringClass = methodBinding.declaringClass;
        this.generateClassLiteralAccessForType(((SourceTypeBinding)methodBinding.declaringClass).scope, declaringClass, null);
        this.aload_0();
        this.invokeJavaLangEnumvalueOf(declaringClass);
        this.checkcast(declaringClass);
        this.areturn();
    }

    public void generateSyntheticBodyForDeserializeLambda(SyntheticMethodBinding methodBinding, SyntheticMethodBinding[] syntheticMethodBindings) {
        this.initializeMaxLocals(methodBinding);
        LinkedHashMap<Integer, ArrayList<SyntheticMethodBinding>> hashcodesTosynthetics = new LinkedHashMap<Integer, ArrayList<SyntheticMethodBinding>>();
        int i = 0;
        int max = syntheticMethodBindings.length;
        while (i < max) {
            SyntheticMethodBinding syntheticMethodBinding = syntheticMethodBindings[i];
            if (syntheticMethodBinding.lambda != null && syntheticMethodBinding.lambda.isSerializable || syntheticMethodBinding.serializableMethodRef != null) {
                Integer hashcode = new String(syntheticMethodBinding.selector).hashCode();
                ArrayList<SyntheticMethodBinding> syntheticssForThisHashcode = (ArrayList<SyntheticMethodBinding>)hashcodesTosynthetics.get(hashcode);
                if (syntheticssForThisHashcode == null) {
                    syntheticssForThisHashcode = new ArrayList<SyntheticMethodBinding>();
                    hashcodesTosynthetics.put(hashcode, syntheticssForThisHashcode);
                }
                syntheticssForThisHashcode.add(syntheticMethodBinding);
            }
            ++i;
        }
        ClassScope scope = ((SourceTypeBinding)methodBinding.declaringClass).scope;
        this.aload_0();
        this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetImplMethodName, ConstantPool.GetImplMethodNameSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
        this.astore_1();
        LocalVariableBinding lvb1 = new LocalVariableBinding("hashcode".toCharArray(), (TypeBinding)scope.getJavaLangString(), 0, false);
        lvb1.resolvedPosition = 1;
        this.addVariable(lvb1);
        this.iconst_m1();
        this.istore_2();
        LocalVariableBinding lvb2 = new LocalVariableBinding("id".toCharArray(), (TypeBinding)TypeBinding.INT, 0, false);
        lvb2.resolvedPosition = 2;
        this.addVariable(lvb2);
        this.aload_1();
        this.invokeStringHashCode();
        BranchLabel label = new BranchLabel(this);
        CaseLabel defaultLabel = new CaseLabel(this);
        int numberOfHashcodes = hashcodesTosynthetics.size();
        CaseLabel[] switchLabels = new CaseLabel[numberOfHashcodes];
        int[] keys = new int[numberOfHashcodes];
        int[] sortedIndexes = new int[numberOfHashcodes];
        Set hashcodes = hashcodesTosynthetics.keySet();
        Iterator hashcodeIterator = hashcodes.iterator();
        int index = 0;
        while (hashcodeIterator.hasNext()) {
            Integer hashcode = (Integer)hashcodeIterator.next();
            switchLabels[index] = new CaseLabel(this);
            keys[index] = hashcode;
            sortedIndexes[index] = index;
            ++index;
        }
        int[] localKeysCopy = new int[numberOfHashcodes];
        System.arraycopy(keys, 0, localKeysCopy, 0, numberOfHashcodes);
        CodeStream.sort(localKeysCopy, 0, numberOfHashcodes - 1, sortedIndexes);
        this.lookupswitch(defaultLabel, keys, sortedIndexes, switchLabels);
        hashcodeIterator = hashcodes.iterator();
        index = 0;
        while (hashcodeIterator.hasNext()) {
            Integer hashcode = (Integer)hashcodeIterator.next();
            List synthetics = (List)hashcodesTosynthetics.get(hashcode);
            switchLabels[index].place();
            BranchLabel nextOne = new BranchLabel(this);
            int j = 0;
            int max2 = synthetics.size();
            while (j < max2) {
                SyntheticMethodBinding syntheticMethodBinding = (SyntheticMethodBinding)synthetics.get(j);
                this.aload_1();
                this.ldc(new String(syntheticMethodBinding.selector));
                this.invokeStringEquals();
                this.ifeq(nextOne);
                this.loadInt(index);
                this.istore_2();
                this.goto_(label);
                nextOne.place();
                nextOne = new BranchLabel(this);
                ++j;
            }
            ++index;
            this.goto_(label);
        }
        defaultLabel.place();
        label.place();
        int syntheticsCount = hashcodes.size();
        switchLabels = new CaseLabel[syntheticsCount];
        keys = new int[syntheticsCount];
        sortedIndexes = new int[syntheticsCount];
        BranchLabel errorLabel = new BranchLabel(this);
        defaultLabel = new CaseLabel(this);
        this.iload_2();
        int j = 0;
        while (j < syntheticsCount) {
            switchLabels[j] = new CaseLabel(this);
            keys[j] = j;
            sortedIndexes[j] = j;
            ++j;
        }
        localKeysCopy = new int[syntheticsCount];
        System.arraycopy(keys, 0, localKeysCopy, 0, syntheticsCount);
        CodeStream.sort(localKeysCopy, 0, syntheticsCount - 1, sortedIndexes);
        this.lookupswitch(defaultLabel, keys, sortedIndexes, switchLabels);
        hashcodeIterator = hashcodes.iterator();
        int hashcodeIndex = 0;
        while (hashcodeIterator.hasNext()) {
            Integer hashcode = (Integer)hashcodeIterator.next();
            List synthetics = (List)hashcodesTosynthetics.get(hashcode);
            switchLabels[hashcodeIndex++].place();
            BranchLabel nextOne = synthetics.size() > 1 ? new BranchLabel(this) : errorLabel;
            int j2 = 0;
            int count = synthetics.size();
            while (j2 < count) {
                SyntheticMethodBinding syntheticMethodBinding = (SyntheticMethodBinding)synthetics.get(j2);
                this.aload_0();
                FunctionalExpression funcEx = syntheticMethodBinding.lambda != null ? syntheticMethodBinding.lambda : syntheticMethodBinding.serializableMethodRef;
                MethodBinding mb = funcEx.binding;
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetImplMethodKind, ConstantPool.GetImplMethodKindSignature, 10, TypeBinding.INT);
                int methodKind = 0;
                methodKind = mb.isStatic() ? 6 : (mb.isPrivate() ? 7 : (mb.isConstructor() ? 8 : (mb.declaringClass.isInterface() ? 9 : 5)));
                this.bipush((byte)methodKind);
                this.if_icmpne(nextOne);
                this.aload_0();
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetFunctionalInterfaceClass, ConstantPool.GetFunctionalInterfaceClassSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
                String functionalInterface = null;
                TypeBinding expectedType = funcEx.expectedType();
                functionalInterface = expectedType instanceof IntersectionTypeBinding18 ? new String(((IntersectionTypeBinding18)expectedType).getSAMType(scope).constantPoolName()) : new String(expectedType.constantPoolName());
                this.ldc(functionalInterface);
                this.invokeObjectEquals();
                this.ifeq(nextOne);
                this.aload_0();
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetFunctionalInterfaceMethodName, ConstantPool.GetFunctionalInterfaceMethodNameSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
                this.ldc(new String(funcEx.descriptor.selector));
                this.invokeObjectEquals();
                this.ifeq(nextOne);
                this.aload_0();
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetFunctionalInterfaceMethodSignature, ConstantPool.GetFunctionalInterfaceMethodSignatureSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
                this.ldc(new String(funcEx.descriptor.original().signature()));
                this.invokeObjectEquals();
                this.ifeq(nextOne);
                this.aload_0();
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetImplClass, ConstantPool.GetImplClassSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
                this.ldc(new String(mb.declaringClass.constantPoolName()));
                this.invokeObjectEquals();
                this.ifeq(nextOne);
                this.aload_0();
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetImplMethodSignature, ConstantPool.GetImplMethodSignatureSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
                this.ldc(new String(mb.signature()));
                this.invokeObjectEquals();
                this.ifeq(nextOne);
                StringBuffer sig = new StringBuffer("(");
                index = 0;
                boolean isLambda = funcEx instanceof LambdaExpression;
                TypeBinding receiverType = null;
                SyntheticArgumentBinding[] outerLocalVariables = null;
                if (isLambda) {
                    LambdaExpression lambdaEx = (LambdaExpression)funcEx;
                    if (lambdaEx.shouldCaptureInstance) {
                        receiverType = mb.declaringClass;
                    }
                    outerLocalVariables = lambdaEx.outerLocalVariables;
                } else {
                    ReferenceExpression refEx = (ReferenceExpression)funcEx;
                    if (refEx.haveReceiver) {
                        receiverType = ((ReferenceExpression)funcEx).receiverType;
                    }
                }
                if (receiverType != null) {
                    this.aload_0();
                    this.loadInt(index++);
                    this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetCapturedArg, ConstantPool.GetCapturedArgSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
                    this.checkcast(receiverType);
                    sig.append(receiverType.signature());
                }
                int p = 0;
                int max3 = outerLocalVariables == null ? 0 : outerLocalVariables.length;
                while (p < max3) {
                    TypeBinding varType = outerLocalVariables[p].type;
                    this.aload_0();
                    this.loadInt(index);
                    this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetCapturedArg, ConstantPool.GetCapturedArgSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
                    if (varType.isBaseType()) {
                        this.checkcast(scope.boxing(varType));
                        this.generateUnboxingConversion(varType.id);
                        if (varType.id == 30 || varType.id == 32) {
                            ++index;
                        }
                    } else {
                        this.checkcast(varType);
                    }
                    ++index;
                    sig.append(varType.signature());
                    ++p;
                }
                sig.append(")");
                if (funcEx.resolvedType instanceof IntersectionTypeBinding18) {
                    sig.append(((IntersectionTypeBinding18)funcEx.resolvedType).getSAMType(scope).signature());
                } else {
                    sig.append(funcEx.resolvedType.signature());
                }
                this.invokeDynamic(funcEx.bootstrapMethodNumber, index, 1, funcEx.descriptor.selector, sig.toString().toCharArray(), funcEx.resolvedType.id, funcEx.resolvedType);
                this.areturn();
                if (j2 < count - 1) {
                    nextOne.place();
                    nextOne = j2 < count - 2 ? new BranchLabel(this) : errorLabel;
                }
                ++j2;
            }
        }
        this.removeVariable(lvb1);
        this.removeVariable(lvb2);
        defaultLabel.place();
        errorLabel.place();
        this.new_(scope.getJavaLangIllegalArgumentException());
        this.dup();
        this.ldc("Invalid lambda deserialization");
        this.invoke((byte)-73, 2, 0, ConstantPool.JavaLangIllegalArgumentExceptionConstantPoolName, ConstantPool.Init, ConstantPool.IllegalArgumentExceptionConstructorSignature, null);
        this.athrow();
    }

    public void loadInt(int value) {
        if (value < 6) {
            if (value == 0) {
                this.iconst_0();
            } else if (value == 1) {
                this.iconst_1();
            } else if (value == 2) {
                this.iconst_2();
            } else if (value == 3) {
                this.iconst_3();
            } else if (value == 4) {
                this.iconst_4();
            } else if (value == 5) {
                this.iconst_5();
            }
        } else if (value < 128) {
            this.bipush((byte)value);
        } else {
            this.ldc(value);
        }
    }

    public void generateSyntheticBodyForEnumValues(SyntheticMethodBinding methodBinding) {
        ClassScope scope = ((SourceTypeBinding)methodBinding.declaringClass).scope;
        this.initializeMaxLocals(methodBinding);
        TypeBinding enumArray = methodBinding.returnType;
        this.fieldAccess((byte)-78, scope.referenceContext.enumValuesSyntheticfield, null);
        this.dup();
        this.astore_0();
        this.iconst_0();
        this.aload_0();
        this.arraylength();
        this.dup();
        this.istore_1();
        this.newArray((ArrayBinding)enumArray);
        this.dup();
        this.astore_2();
        this.iconst_0();
        this.iload_1();
        this.invokeSystemArraycopy();
        this.aload_2();
        this.areturn();
    }

    public void generateSyntheticBodyForEnumInitializationMethod(SyntheticMethodBinding methodBinding) {
        this.maxLocals = 0;
        SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)methodBinding.declaringClass;
        TypeDeclaration typeDeclaration = sourceTypeBinding.scope.referenceContext;
        MethodScope staticInitializerScope = typeDeclaration.staticInitializerScope;
        FieldDeclaration[] fieldDeclarations = typeDeclaration.fields;
        int i = methodBinding.startIndex;
        int max = methodBinding.endIndex;
        while (i < max) {
            FieldDeclaration fieldDecl = fieldDeclarations[i];
            if (fieldDecl.isStatic() && fieldDecl.getKind() == 3) {
                fieldDecl.generateCode(staticInitializerScope, this);
            }
            ++i;
        }
        this.return_();
    }

    public void generateSyntheticBodyForFieldReadAccess(SyntheticMethodBinding accessMethod) {
        ReferenceBinding declaringClass;
        this.initializeMaxLocals(accessMethod);
        FieldBinding fieldBinding = accessMethod.targetReadField;
        ReferenceBinding referenceBinding = declaringClass = accessMethod.purpose == 3 ? accessMethod.declaringClass.superclass() : accessMethod.declaringClass;
        if (fieldBinding.isStatic()) {
            this.fieldAccess((byte)-78, fieldBinding, declaringClass);
        } else {
            this.aload_0();
            this.fieldAccess((byte)-76, fieldBinding, declaringClass);
        }
        switch (fieldBinding.type.id) {
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 10: {
                this.ireturn();
                break;
            }
            case 7: {
                this.lreturn();
                break;
            }
            case 9: {
                this.freturn();
                break;
            }
            case 8: {
                this.dreturn();
                break;
            }
            default: {
                this.areturn();
            }
        }
    }

    public void generateSyntheticBodyForFieldWriteAccess(SyntheticMethodBinding accessMethod) {
        ReferenceBinding declaringClass;
        this.initializeMaxLocals(accessMethod);
        FieldBinding fieldBinding = accessMethod.targetWriteField;
        ReferenceBinding referenceBinding = declaringClass = accessMethod.purpose == 4 ? accessMethod.declaringClass.superclass() : accessMethod.declaringClass;
        if (fieldBinding.isStatic()) {
            this.load(fieldBinding.type, 0);
            this.fieldAccess((byte)-77, fieldBinding, declaringClass);
        } else {
            this.aload_0();
            this.load(fieldBinding.type, 1);
            this.fieldAccess((byte)-75, fieldBinding, declaringClass);
        }
        this.return_();
    }

    public void generateSyntheticBodyForMethodAccess(SyntheticMethodBinding accessMethod) {
        int resolvedPosition;
        TypeBinding[] arguments;
        this.initializeMaxLocals(accessMethod);
        MethodBinding targetMethod = accessMethod.targetMethod;
        TypeBinding[] parameters = targetMethod.parameters;
        int length = parameters.length;
        TypeBinding[] typeBindingArray = arguments = accessMethod.purpose == 8 ? accessMethod.parameters : null;
        if (targetMethod.isStatic()) {
            resolvedPosition = 0;
        } else {
            this.aload_0();
            resolvedPosition = 1;
        }
        int i = 0;
        while (i < length) {
            TypeBinding parameter = parameters[i];
            if (arguments != null) {
                TypeBinding argument = arguments[i];
                this.load(argument, resolvedPosition);
                if (TypeBinding.notEquals(argument, parameter)) {
                    this.checkcast(parameter);
                }
            } else {
                this.load(parameter, resolvedPosition);
            }
            switch (parameter.id) {
                case 7: 
                case 8: {
                    resolvedPosition += 2;
                    break;
                }
                default: {
                    ++resolvedPosition;
                }
            }
            ++i;
        }
        if (targetMethod.isStatic()) {
            this.invoke((byte)-72, targetMethod, accessMethod.declaringClass);
        } else if (targetMethod.isConstructor() || targetMethod.isPrivate() || accessMethod.purpose == 7) {
            ReferenceBinding declaringClass = accessMethod.purpose == 7 ? this.findDirectSuperTypeTowards(accessMethod, targetMethod) : accessMethod.declaringClass;
            this.invoke((byte)-73, targetMethod, declaringClass);
        } else if (targetMethod.declaringClass.isInterface()) {
            this.invoke((byte)-71, targetMethod, null);
        } else {
            this.invoke((byte)-74, targetMethod, accessMethod.declaringClass);
        }
        switch (targetMethod.returnType.id) {
            case 6: {
                this.return_();
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 10: {
                this.ireturn();
                break;
            }
            case 7: {
                this.lreturn();
                break;
            }
            case 9: {
                this.freturn();
                break;
            }
            case 8: {
                this.dreturn();
                break;
            }
            default: {
                TypeBinding accessErasure = accessMethod.returnType.erasure();
                TypeBinding match = targetMethod.returnType.findSuperTypeOriginatingFrom(accessErasure);
                if (match == null) {
                    this.checkcast(accessErasure);
                }
                this.areturn();
            }
        }
    }

    ReferenceBinding findDirectSuperTypeTowards(SyntheticMethodBinding accessMethod, MethodBinding targetMethod) {
        ReferenceBinding currentType = accessMethod.declaringClass;
        ReferenceBinding superclass = currentType.superclass();
        if (targetMethod.isDefaultMethod()) {
            ReferenceBinding targetType = targetMethod.declaringClass;
            if (superclass.isCompatibleWith(targetType)) {
                return superclass;
            }
            ReferenceBinding[] superInterfaces = currentType.superInterfaces();
            if (superInterfaces != null) {
                int i = 0;
                while (i < superInterfaces.length) {
                    ReferenceBinding superIfc = superInterfaces[i];
                    if (superIfc.isCompatibleWith(targetType)) {
                        return superIfc;
                    }
                    ++i;
                }
            }
            throw new RuntimeException("Assumption violated: some super type must be conform to the declaring class of a super method");
        }
        return superclass;
    }

    public void generateSyntheticBodyForSwitchTable(SyntheticMethodBinding methodBinding) {
        ClassScope scope = ((SourceTypeBinding)methodBinding.declaringClass).scope;
        this.initializeMaxLocals(methodBinding);
        BranchLabel nullLabel = new BranchLabel(this);
        FieldBinding syntheticFieldBinding = methodBinding.targetReadField;
        this.fieldAccess((byte)-78, syntheticFieldBinding, null);
        this.dup();
        this.ifnull(nullLabel);
        this.areturn();
        this.pushOnStack(syntheticFieldBinding.type);
        nullLabel.place();
        this.pop();
        ReferenceBinding enumBinding = (ReferenceBinding)methodBinding.targetEnumType;
        ArrayBinding arrayBinding = scope.createArrayType(enumBinding, 1);
        this.invokeJavaLangEnumValues(enumBinding, arrayBinding);
        this.arraylength();
        this.newarray(10);
        this.astore_0();
        LocalVariableBinding localVariableBinding = new LocalVariableBinding(" tab".toCharArray(), (TypeBinding)scope.createArrayType(TypeBinding.INT, 1), 0, false);
        this.addVariable(localVariableBinding);
        FieldBinding[] fields = enumBinding.fields();
        if (fields != null) {
            int i = 0;
            int max = fields.length;
            while (i < max) {
                FieldBinding fieldBinding = fields[i];
                if ((fieldBinding.getAccessFlags() & 0x4000) != 0) {
                    BranchLabel endLabel = new BranchLabel(this);
                    ExceptionLabel anyExceptionHandler = new ExceptionLabel(this, TypeBinding.LONG);
                    anyExceptionHandler.placeStart();
                    this.aload_0();
                    this.fieldAccess((byte)-78, fieldBinding, null);
                    this.invokeEnumOrdinal(enumBinding.constantPoolName());
                    this.generateInlinedValue(fieldBinding.id + 1);
                    this.iastore();
                    anyExceptionHandler.placeEnd();
                    this.goto_(endLabel);
                    this.pushExceptionOnStack(scope.getJavaLangNoSuchFieldError());
                    anyExceptionHandler.place();
                    this.pop();
                    endLabel.place();
                }
                ++i;
            }
        }
        this.aload_0();
        if (scope.compilerOptions().complianceLevel < 0x350000L || !syntheticFieldBinding.isFinal()) {
            this.dup();
            this.fieldAccess((byte)-77, syntheticFieldBinding, null);
        }
        this.areturn();
        this.removeVariable(localVariableBinding);
    }

    public void generateSyntheticEnclosingInstanceValues(BlockScope currentScope, ReferenceBinding targetType, Expression enclosingInstance, ASTNode invocationSite) {
        boolean hasExtraEnclosingInstance;
        ReferenceBinding checkedTargetType = targetType.isAnonymousType() ? (ReferenceBinding)targetType.superclass().erasure() : targetType;
        boolean bl = hasExtraEnclosingInstance = enclosingInstance != null;
        if (hasExtraEnclosingInstance && (!checkedTargetType.isNestedType() || checkedTargetType.isStatic())) {
            currentScope.problemReporter().unnecessaryEnclosingInstanceSpecification(enclosingInstance, checkedTargetType);
            return;
        }
        ReferenceBinding[] syntheticArgumentTypes = targetType.syntheticEnclosingInstanceTypes();
        if (syntheticArgumentTypes != null) {
            boolean denyEnclosingArgInConstructorCall;
            ReferenceBinding targetEnclosingType = checkedTargetType.enclosingType();
            long compliance = currentScope.compilerOptions().complianceLevel;
            if (compliance <= 0x2F0000L) {
                denyEnclosingArgInConstructorCall = invocationSite instanceof AllocationExpression;
            } else if (compliance == 0x300000L) {
                denyEnclosingArgInConstructorCall = invocationSite instanceof AllocationExpression || invocationSite instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)invocationSite).isSuperAccess();
            } else if (compliance < 0x330000L) {
                denyEnclosingArgInConstructorCall = (invocationSite instanceof AllocationExpression || invocationSite instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)invocationSite).isSuperAccess()) && !targetType.isLocalType();
            } else if (invocationSite instanceof AllocationExpression) {
                denyEnclosingArgInConstructorCall = !targetType.isLocalType();
            } else if (invocationSite instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)invocationSite).isSuperAccess()) {
                MethodScope enclosingMethodScope = currentScope.enclosingMethodScope();
                denyEnclosingArgInConstructorCall = !targetType.isLocalType() && enclosingMethodScope != null && enclosingMethodScope.isConstructorCall;
            } else {
                denyEnclosingArgInConstructorCall = false;
            }
            boolean complyTo14 = compliance >= 0x300000L;
            int i = 0;
            int max = syntheticArgumentTypes.length;
            while (i < max) {
                ReferenceBinding syntheticArgType = syntheticArgumentTypes[i];
                if (hasExtraEnclosingInstance && TypeBinding.equalsEquals(syntheticArgType, targetEnclosingType)) {
                    hasExtraEnclosingInstance = false;
                    enclosingInstance.generateCode(currentScope, this, true);
                    if (complyTo14) {
                        this.dup();
                        this.invokeObjectGetClass();
                        this.pop();
                    }
                } else {
                    Object[] emulationPath = currentScope.getEmulationPath(syntheticArgType, false, denyEnclosingArgInConstructorCall);
                    this.generateOuterAccess(emulationPath, invocationSite, syntheticArgType, currentScope);
                }
                ++i;
            }
            if (hasExtraEnclosingInstance) {
                currentScope.problemReporter().unnecessaryEnclosingInstanceSpecification(enclosingInstance, checkedTargetType);
            }
        }
    }

    public void generateSyntheticOuterArgumentValues(BlockScope currentScope, ReferenceBinding targetType, ASTNode invocationSite) {
        SyntheticArgumentBinding[] syntheticArguments = targetType.syntheticOuterLocalVariables();
        if (syntheticArguments != null) {
            int i = 0;
            int max = syntheticArguments.length;
            while (i < max) {
                LocalVariableBinding targetVariable = syntheticArguments[i].actualOuterLocalVariable;
                Object[] emulationPath = currentScope.getEmulationPath(targetVariable);
                this.generateOuterAccess(emulationPath, invocationSite, targetVariable, currentScope);
                ++i;
            }
        }
    }

    public void generateSyntheticBodyForRecordCanonicalConstructor(SyntheticMethodBinding canonConstructor) {
        this.initializeMaxLocals(canonConstructor);
        SourceTypeBinding declaringClass = (SourceTypeBinding)canonConstructor.declaringClass;
        ReferenceBinding superClass = declaringClass.superclass();
        MethodBinding superCons = superClass.getExactConstructor(new TypeBinding[0]);
        this.aload_0();
        this.invoke((byte)-73, superCons, superClass);
        FieldBinding[] fields = declaringClass.getImplicitComponentFields();
        int len = fields != null ? fields.length : 0;
        int resolvedPosition = 1;
        int i = 0;
        while (i < len) {
            FieldBinding field = fields[i];
            this.aload_0();
            TypeBinding type = field.type;
            this.load(type, resolvedPosition);
            switch (type.id) {
                case 7: 
                case 8: {
                    resolvedPosition += 2;
                    break;
                }
                default: {
                    ++resolvedPosition;
                }
            }
            this.fieldAccess((byte)-75, field, declaringClass);
            ++i;
        }
        this.return_();
    }

    public void generateSyntheticBodyForRecordEquals(SyntheticMethodBinding methodBinding, int index) {
        this.initializeMaxLocals(methodBinding);
        this.aload_0();
        this.aload_1();
        String sig = new String(methodBinding.signature());
        sig = String.valueOf(sig.substring(0, 1)) + new String(methodBinding.declaringClass.signature()) + sig.substring(1);
        this.invokeDynamic(index, methodBinding.parameters.length, 1, methodBinding.selector, sig.toCharArray(), 5, TypeBinding.BOOLEAN);
        this.ireturn();
    }

    public void generateSyntheticBodyForRecordHashCode(SyntheticMethodBinding methodBinding, int index) {
        this.initializeMaxLocals(methodBinding);
        this.aload_0();
        String sig = new String(methodBinding.signature());
        sig = String.valueOf(sig.substring(0, 1)) + new String(methodBinding.declaringClass.signature()) + sig.substring(1);
        this.invokeDynamic(index, methodBinding.parameters.length, 1, methodBinding.selector, sig.toCharArray(), 10, TypeBinding.INT);
        this.ireturn();
    }

    public void generateSyntheticBodyForRecordToString(SyntheticMethodBinding methodBinding, int index) {
        this.initializeMaxLocals(methodBinding);
        this.aload_0();
        String sig = new String(methodBinding.signature());
        sig = String.valueOf(sig.substring(0, 1)) + new String(methodBinding.declaringClass.signature()) + sig.substring(1);
        this.invokeDynamic(index, methodBinding.parameters.length, 1, methodBinding.selector, sig.toCharArray(), 1, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
        this.areturn();
    }

    public void generateUnboxingConversion(int unboxedTypeID) {
        switch (unboxedTypeID) {
            case 3: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.BYTEVALUE_BYTE_METHOD_NAME, ConstantPool.BYTEVALUE_BYTE_METHOD_SIGNATURE, unboxedTypeID, TypeBinding.wellKnownBaseType(unboxedTypeID));
                break;
            }
            case 4: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.SHORTVALUE_SHORT_METHOD_NAME, ConstantPool.SHORTVALUE_SHORT_METHOD_SIGNATURE, unboxedTypeID, TypeBinding.wellKnownBaseType(unboxedTypeID));
                break;
            }
            case 2: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.CHARVALUE_CHARACTER_METHOD_NAME, ConstantPool.CHARVALUE_CHARACTER_METHOD_SIGNATURE, unboxedTypeID, TypeBinding.wellKnownBaseType(unboxedTypeID));
                break;
            }
            case 10: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.INTVALUE_INTEGER_METHOD_NAME, ConstantPool.INTVALUE_INTEGER_METHOD_SIGNATURE, unboxedTypeID, TypeBinding.wellKnownBaseType(unboxedTypeID));
                break;
            }
            case 7: {
                this.invoke((byte)-74, 1, 2, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.LONGVALUE_LONG_METHOD_NAME, ConstantPool.LONGVALUE_LONG_METHOD_SIGNATURE, unboxedTypeID, TypeBinding.wellKnownBaseType(unboxedTypeID));
                break;
            }
            case 9: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.FLOATVALUE_FLOAT_METHOD_NAME, ConstantPool.FLOATVALUE_FLOAT_METHOD_SIGNATURE, unboxedTypeID, TypeBinding.wellKnownBaseType(unboxedTypeID));
                break;
            }
            case 8: {
                this.invoke((byte)-74, 1, 2, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_NAME, ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_SIGNATURE, unboxedTypeID, TypeBinding.wellKnownBaseType(unboxedTypeID));
                break;
            }
            case 5: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_NAME, ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_SIGNATURE, unboxedTypeID, TypeBinding.wellKnownBaseType(unboxedTypeID));
            }
        }
    }

    public void generateWideRevertedConditionalBranch(byte revertedOpcode, BranchLabel wideTarget) {
        BranchLabel intermediate = new BranchLabel(this);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = revertedOpcode;
        intermediate.branch();
        this.goto_w(wideTarget);
        intermediate.place();
    }

    public void getBaseTypeValue(int baseTypeID) {
        switch (baseTypeID) {
            case 3: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.BYTEVALUE_BYTE_METHOD_NAME, ConstantPool.BYTEVALUE_BYTE_METHOD_SIGNATURE, baseTypeID, TypeBinding.wellKnownBaseType(baseTypeID));
                break;
            }
            case 4: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.SHORTVALUE_SHORT_METHOD_NAME, ConstantPool.SHORTVALUE_SHORT_METHOD_SIGNATURE, baseTypeID, TypeBinding.wellKnownBaseType(baseTypeID));
                break;
            }
            case 2: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.CHARVALUE_CHARACTER_METHOD_NAME, ConstantPool.CHARVALUE_CHARACTER_METHOD_SIGNATURE, baseTypeID, TypeBinding.wellKnownBaseType(baseTypeID));
                break;
            }
            case 10: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.INTVALUE_INTEGER_METHOD_NAME, ConstantPool.INTVALUE_INTEGER_METHOD_SIGNATURE, baseTypeID, TypeBinding.wellKnownBaseType(baseTypeID));
                break;
            }
            case 7: {
                this.invoke((byte)-74, 1, 2, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.LONGVALUE_LONG_METHOD_NAME, ConstantPool.LONGVALUE_LONG_METHOD_SIGNATURE, baseTypeID, TypeBinding.wellKnownBaseType(baseTypeID));
                break;
            }
            case 9: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.FLOATVALUE_FLOAT_METHOD_NAME, ConstantPool.FLOATVALUE_FLOAT_METHOD_SIGNATURE, baseTypeID, TypeBinding.wellKnownBaseType(baseTypeID));
                break;
            }
            case 8: {
                this.invoke((byte)-74, 1, 2, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_NAME, ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_SIGNATURE, baseTypeID, TypeBinding.wellKnownBaseType(baseTypeID));
                break;
            }
            case 5: {
                this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_NAME, ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_SIGNATURE, baseTypeID, TypeBinding.wellKnownBaseType(baseTypeID));
            }
        }
    }

    public final byte[] getContents() {
        byte[] contents = new byte[this.position];
        System.arraycopy(this.bCodeStream, 0, contents, 0, this.position);
        return contents;
    }

    public static TypeBinding getConstantPoolDeclaringClass(Scope currentScope, FieldBinding codegenBinding, TypeBinding actualReceiverType, boolean isImplicitThisReceiver) {
        ReferenceBinding constantPoolDeclaringClass = codegenBinding.declaringClass;
        if (TypeBinding.notEquals(constantPoolDeclaringClass, actualReceiverType.erasure()) && !actualReceiverType.isArrayType() && constantPoolDeclaringClass != null && codegenBinding.constant() == Constant.NotAConstant) {
            CompilerOptions options = currentScope.compilerOptions();
            if (!((options.targetJDK < 0x2E0000L || options.complianceLevel < 0x300000L && isImplicitThisReceiver && codegenBinding.isStatic() || constantPoolDeclaringClass.id == 1) && constantPoolDeclaringClass.canBeSeenBy(currentScope))) {
                return actualReceiverType.erasure();
            }
        }
        return constantPoolDeclaringClass;
    }

    public static TypeBinding getConstantPoolDeclaringClass(Scope currentScope, MethodBinding codegenBinding, TypeBinding actualReceiverType, boolean isImplicitThisReceiver) {
        TypeBinding constantPoolDeclaringClass = codegenBinding.declaringClass;
        if (ArrayBinding.isArrayClone(actualReceiverType, codegenBinding)) {
            CompilerOptions options = currentScope.compilerOptions();
            if (options.sourceLevel > 0x300000L) {
                constantPoolDeclaringClass = actualReceiverType.erasure();
            }
        } else if (TypeBinding.notEquals(constantPoolDeclaringClass, actualReceiverType.erasure()) && !actualReceiverType.isArrayType()) {
            CompilerOptions options = currentScope.compilerOptions();
            if (!((options.targetJDK < 0x2E0000L || options.complianceLevel < 0x300000L && isImplicitThisReceiver && codegenBinding.isStatic() || codegenBinding.declaringClass.id == 1) && codegenBinding.declaringClass.canBeSeenBy(currentScope))) {
                TypeBinding erasedReceiverType = actualReceiverType.erasure();
                if (erasedReceiverType.isIntersectionType18()) {
                    actualReceiverType = erasedReceiverType;
                }
                if (actualReceiverType.isIntersectionType18()) {
                    ReferenceBinding[] intersectingTypes = ((IntersectionTypeBinding18)actualReceiverType).getIntersectingTypes();
                    int i = 0;
                    while (i < intersectingTypes.length) {
                        if (intersectingTypes[i].findSuperTypeOriginatingFrom(constantPoolDeclaringClass) != null) {
                            constantPoolDeclaringClass = intersectingTypes[i].erasure();
                            break;
                        }
                        ++i;
                    }
                } else {
                    constantPoolDeclaringClass = erasedReceiverType;
                }
            }
        }
        return constantPoolDeclaringClass;
    }

    protected int getPosition() {
        return this.position;
    }

    public void getTYPE(int baseTypeID) {
        this.countLabels = 0;
        switch (baseTypeID) {
            case 3: {
                this.fieldAccess((byte)-78, 1, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature, baseTypeID);
                break;
            }
            case 4: {
                this.fieldAccess((byte)-78, 1, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature, baseTypeID);
                break;
            }
            case 2: {
                this.fieldAccess((byte)-78, 1, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature, baseTypeID);
                break;
            }
            case 10: {
                this.fieldAccess((byte)-78, 1, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature, baseTypeID);
                break;
            }
            case 7: {
                this.fieldAccess((byte)-78, 1, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature, baseTypeID);
                break;
            }
            case 9: {
                this.fieldAccess((byte)-78, 1, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature, baseTypeID);
                break;
            }
            case 8: {
                this.fieldAccess((byte)-78, 1, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature, baseTypeID);
                break;
            }
            case 5: {
                this.fieldAccess((byte)-78, 1, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature, baseTypeID);
                break;
            }
            case 6: {
                this.fieldAccess((byte)-78, 1, ConstantPool.JavaLangVoidConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature, baseTypeID);
            }
        }
    }

    public void goto_(BranchLabel label) {
        boolean chained;
        if (this.wideMode) {
            this.goto_w(label);
            return;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        if ((chained = this.inlineForwardReferencesFromLabelsTargeting(label, this.position)) && this.lastAbruptCompletion == this.position) {
            if (label.position != -1) {
                int[] forwardRefs = label.forwardReferences();
                int i = 0;
                int max = label.forwardReferenceCount();
                while (i < max) {
                    this.writePosition(label, forwardRefs[i]);
                    ++i;
                }
                this.countLabels = 0;
            }
            return;
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -89;
        label.branch();
        this.lastAbruptCompletion = this.position;
    }

    public void goto_w(BranchLabel label) {
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -56;
        label.branchWide();
        this.lastAbruptCompletion = this.position;
    }

    public void i2b() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -111;
        this.pushTypeBinding(1, TypeBinding.INT);
    }

    public void i2c() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -110;
        this.pushTypeBinding(1, TypeBinding.INT);
    }

    public void i2d() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(1, TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -121;
    }

    public void i2f() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -122;
        this.pushTypeBinding(1, TypeBinding.FLOAT);
    }

    public void i2l() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(1, TypeBinding.LONG);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -123;
    }

    public void i2s() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -109;
        this.pushTypeBinding(1, TypeBinding.INT);
    }

    public void iadd() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 96;
    }

    public void iaload() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBindingArray();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 46;
    }

    public void iand() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 126;
    }

    public void iastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        this.popTypeBinding(3);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 79;
    }

    public void iconst_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 3;
    }

    public void iconst_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 4;
    }

    public void iconst_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 5;
    }

    public void iconst_3() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 6;
    }

    public void iconst_4() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 7;
    }

    public void iconst_5() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 8;
    }

    public void iconst_m1() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 2;
    }

    public void idiv() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 108;
    }

    public void if_acmpeq(BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding(2);
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-90, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -91;
            lbl.branch();
        }
    }

    public void if_acmpne(BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding(2);
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-91, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -90;
            lbl.branch();
        }
    }

    public void if_icmpeq(BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding(2);
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-96, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -97;
            lbl.branch();
        }
    }

    public void if_icmpge(BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding(2);
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-95, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -94;
            lbl.branch();
        }
    }

    public void if_icmpgt(BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding(2);
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-92, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -93;
            lbl.branch();
        }
    }

    public void if_icmple(BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding(2);
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-93, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -92;
            lbl.branch();
        }
    }

    public void if_icmplt(BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding(2);
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-94, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -95;
            lbl.branch();
        }
    }

    public void if_icmpne(BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding(2);
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-97, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -96;
            lbl.branch();
        }
    }

    public void ifeq(BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-102, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -103;
            lbl.branch();
        }
    }

    public void ifge(BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-101, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -100;
            lbl.branch();
        }
    }

    public void ifgt(BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-98, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -99;
            lbl.branch();
        }
    }

    public void ifle(BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-99, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -98;
            lbl.branch();
        }
    }

    public void iflt(BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-100, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -101;
            lbl.branch();
        }
    }

    public void ifne(BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-103, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -102;
            lbl.branch();
        }
    }

    public void ifnonnull(BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-58, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -57;
            lbl.branch();
        }
    }

    public void ifnull(BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)-57, lbl);
        } else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -58;
            lbl.branch();
        }
    }

    public final void iinc(int index, int value) {
        this.countLabels = 0;
        if (index > 255 || value < -128 || value > 127) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = -124;
            this.writeUnsignedShort(index);
            this.writeSignedShort(value);
        } else {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 3;
            this.bCodeStream[this.classFileOffset++] = -124;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
            this.bCodeStream[this.classFileOffset++] = (byte)value;
        }
    }

    public void iload(int iArg) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 21;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 21;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void iload_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.maxLocals <= 0) {
            this.maxLocals = 1;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 26;
    }

    public void iload_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 27;
    }

    public void iload_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 28;
    }

    public void iload_3() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 29;
    }

    public void imul() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 104;
    }

    public void ineg() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 116;
        this.pushTypeBinding(1, TypeBinding.INT);
    }

    public void init(ClassFile targetClassFile) {
        this.classFile = targetClassFile;
        this.constantPool = targetClassFile.constantPool;
        this.bCodeStream = targetClassFile.contents;
        this.startingClassFileOffset = this.classFileOffset = targetClassFile.contentsOffset;
        this.pcToSourceMapSize = 0;
        this.lastEntryPC = 0;
        this.visibleLocalsCount = 0;
        this.allLocalsCounter = 0;
        this.exceptionLabelsCounter = 0;
        this.countLabels = 0;
        this.lastAbruptCompletion = -1;
        this.stackMax = 0;
        this.stackDepth = 0;
        this.maxLocals = 0;
        this.position = 0;
        this.clearTypeBindingStack();
        this.lastSwitchCumulativeSyntheticVars = 0;
    }

    public void initializeMaxLocals(MethodBinding methodBinding) {
        if (methodBinding == null) {
            this.maxLocals = 0;
            return;
        }
        this.maxLocals = methodBinding.isStatic() ? 0 : 1;
        ReferenceBinding declaringClass = methodBinding.declaringClass;
        if (methodBinding.isConstructor() && declaringClass.isEnum()) {
            this.maxLocals += 2;
        }
        if (methodBinding.isConstructor() && declaringClass.isNestedType()) {
            this.maxLocals += declaringClass.getEnclosingInstancesSlotSize();
            this.maxLocals += declaringClass.getOuterLocalVariablesSlotSize();
        }
        TypeBinding[] parameterTypes = methodBinding.parameters;
        if (methodBinding.parameters != null) {
            int i = 0;
            int max = parameterTypes.length;
            while (i < max) {
                switch (parameterTypes[i].id) {
                    case 7: 
                    case 8: {
                        this.maxLocals += 2;
                        break;
                    }
                    default: {
                        ++this.maxLocals;
                    }
                }
                ++i;
            }
        }
    }

    public boolean inlineForwardReferencesFromLabelsTargeting(BranchLabel targetLabel, int gotoLocation) {
        if (targetLabel.delegate != null) {
            return false;
        }
        int chaining = 0;
        int i = this.countLabels - 1;
        while (i >= 0) {
            BranchLabel currentLabel = this.labels[i];
            if (currentLabel.position != gotoLocation) break;
            if (currentLabel == targetLabel) {
                chaining |= 4;
            } else if (currentLabel.isStandardLabel()) {
                if (currentLabel.delegate == null) {
                    targetLabel.becomeDelegateFor(currentLabel);
                    chaining |= 2;
                }
            } else {
                chaining |= 4;
            }
            --i;
        }
        return (chaining & 6) == 2;
    }

    public void instance_of(TypeBinding typeBinding) {
        this.instance_of(null, typeBinding);
    }

    public void instance_of(TypeReference typeReference, TypeBinding typeBinding) {
        this.countLabels = 0;
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -63;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
        this.pushTypeBinding(1, TypeBinding.INT);
    }

    protected void invoke(byte opcode, int receiverAndArgsSize, int returnTypeSize, char[] declaringClass, char[] selector, char[] signature, TypeBinding type) {
        this.invoke(opcode, receiverAndArgsSize, returnTypeSize, declaringClass, selector, signature, 1, type);
    }

    protected void _invoke(byte opcode, int receiverAndArgsSize, int returnTypeSize, char[] declaringClass, char[] selector, char[] signature, int typeId) {
    }

    protected void invoke(byte opcode, int receiverAndArgsSize, int returnTypeSize, char[] declaringClass, char[] selector, char[] signature, int typeId, TypeBinding type) {
        this.invoke18(opcode, receiverAndArgsSize, returnTypeSize, declaringClass, opcode == -71, selector, signature, typeId, type);
    }

    private void popInvokeTypeBinding(int receiverAndArgsSize) {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        int i = 0;
        while (i < receiverAndArgsSize) {
            TypeBinding typeBinding = this.popTypeBinding();
            if (TypeIds.getCategory(typeBinding.id) == 2) {
                i += 2;
                continue;
            }
            ++i;
        }
    }

    private void invoke18(byte opcode, int receiverAndArgsSize, int returnTypeSize, char[] declaringClass, boolean isInterface, char[] selector, char[] signature, int typeId, TypeBinding type) {
        this.countLabels = 0;
        if (opcode == -71) {
            if (this.classFileOffset + 4 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 3;
            this.bCodeStream[this.classFileOffset++] = opcode;
            this.writeUnsignedShort(this.constantPool.literalIndexForMethod(declaringClass, selector, signature, true));
            this.bCodeStream[this.classFileOffset++] = (byte)receiverAndArgsSize;
            this.bCodeStream[this.classFileOffset++] = 0;
        } else {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = opcode;
            this.writeUnsignedShort(this.constantPool.literalIndexForMethod(declaringClass, selector, signature, isInterface));
        }
        this.stackDepth += returnTypeSize - receiverAndArgsSize;
        this.popInvokeTypeBinding(receiverAndArgsSize);
        if (returnTypeSize > 0) {
            this.pushTypeBinding(type);
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
    }

    public void invokeDynamic(int bootStrapIndex, int argsSize, int returnTypeSize, char[] selector, char[] signature, int typeId, TypeBinding type) {
        this.invokeDynamic(bootStrapIndex, argsSize, returnTypeSize, selector, signature, false, null, null, typeId, type);
    }

    public void invokeDynamic(int bootStrapIndex, int argsSize, int returnTypeSize, char[] selector, char[] signature, boolean isConstructorReference, TypeReference lhsTypeReference, TypeReference[] typeArguments, int typeId, TypeBinding type) {
        if (this.classFileOffset + 4 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        int invokeDynamicIndex = this.constantPool.literalIndexForInvokeDynamic(bootStrapIndex, selector, signature);
        this.position += 3;
        this.bCodeStream[this.classFileOffset++] = -70;
        this.writeUnsignedShort(invokeDynamicIndex);
        this.bCodeStream[this.classFileOffset++] = 0;
        this.bCodeStream[this.classFileOffset++] = 0;
        this.stackDepth += returnTypeSize - argsSize;
        this.popInvokeTypeBinding(argsSize);
        if (returnTypeSize > 0) {
            this.pushTypeBinding(type);
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
    }

    public void invoke(byte opcode, MethodBinding methodBinding, TypeBinding declaringClass) {
        this.invoke(opcode, methodBinding, declaringClass, null);
    }

    public void invoke(byte opcode, MethodBinding methodBinding, TypeBinding declaringClass, TypeReference[] typeArguments) {
        int returnTypeSize;
        int receiverAndArgsSize;
        if (declaringClass == null) {
            declaringClass = methodBinding.declaringClass;
        }
        if ((declaringClass.tagBits & 0x800L) != 0L) {
            Util.recordNestedType(this.classFile, declaringClass);
        }
        switch (opcode) {
            case -72: {
                receiverAndArgsSize = 0;
                break;
            }
            case -74: 
            case -71: {
                receiverAndArgsSize = 1;
                break;
            }
            case -73: {
                receiverAndArgsSize = 1;
                if (!methodBinding.isConstructor()) break;
                if (declaringClass.isNestedType()) {
                    ReferenceBinding nestedType = (ReferenceBinding)declaringClass;
                    receiverAndArgsSize += nestedType.getEnclosingInstancesSlotSize();
                    SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticOuterLocalVariables();
                    if (syntheticArguments != null) {
                        int i = 0;
                        int max = syntheticArguments.length;
                        while (i < max) {
                            switch (syntheticArguments[i].id) {
                                case 7: 
                                case 8: {
                                    receiverAndArgsSize += 2;
                                    break;
                                }
                                default: {
                                    ++receiverAndArgsSize;
                                }
                            }
                            ++i;
                        }
                    }
                }
                if (!declaringClass.isEnum()) break;
                receiverAndArgsSize += 2;
                break;
            }
            default: {
                return;
            }
        }
        int i = methodBinding.parameters.length - 1;
        while (i >= 0) {
            switch (methodBinding.parameters[i].id) {
                case 7: 
                case 8: {
                    receiverAndArgsSize += 2;
                    break;
                }
                default: {
                    ++receiverAndArgsSize;
                }
            }
            --i;
        }
        switch (methodBinding.returnType.id) {
            case 7: 
            case 8: {
                returnTypeSize = 2;
                break;
            }
            case 6: {
                returnTypeSize = 0;
                break;
            }
            default: {
                returnTypeSize = 1;
            }
        }
        this.invoke18(opcode, receiverAndArgsSize, returnTypeSize, declaringClass.constantPoolName(), declaringClass.isInterface(), methodBinding.selector, methodBinding.signature(this.classFile), methodBinding.returnType.id, methodBinding.returnType);
    }

    protected void invokeAccessibleObjectSetAccessible() {
        this.invoke((byte)-74, 2, 0, ConstantPool.JAVALANGREFLECTACCESSIBLEOBJECT_CONSTANTPOOLNAME, ConstantPool.SETACCESSIBLE_NAME, ConstantPool.SETACCESSIBLE_SIGNATURE, null);
    }

    protected void invokeArrayNewInstance() {
        this.invoke((byte)-72, 2, 1, ConstantPool.JAVALANGREFLECTARRAY_CONSTANTPOOLNAME, ConstantPool.NewInstance, ConstantPool.NewInstanceSignature, this.getPopularBinding(ConstantPool.JavaLangObjectConstantPoolName));
    }

    public void invokeClassForName() {
        this.invoke((byte)-72, 1, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.ForName, ConstantPool.ForNameSignature, this.getPopularBinding(ConstantPool.JavaLangClassConstantPoolName));
    }

    protected void invokeClassGetDeclaredConstructor() {
        this.invoke((byte)-74, 2, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.GETDECLAREDCONSTRUCTOR_NAME, ConstantPool.GETDECLAREDCONSTRUCTOR_SIGNATURE, this.getPopularBinding(ConstantPool.JavaLangReflectConstructorConstantPoolName));
    }

    protected void invokeClassGetDeclaredField() {
        this.invoke((byte)-74, 2, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.GETDECLAREDFIELD_NAME, ConstantPool.GETDECLAREDFIELD_SIGNATURE, this.getPopularBinding(ConstantPool.JAVALANGREFLECTFIELD_CONSTANTPOOLNAME));
    }

    protected void invokeClassGetDeclaredMethod() {
        this.invoke((byte)-74, 3, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.GETDECLAREDMETHOD_NAME, ConstantPool.GETDECLAREDMETHOD_SIGNATURE, this.getPopularBinding(ConstantPool.JAVALANGREFLECTMETHOD_CONSTANTPOOLNAME));
    }

    public void invokeEnumOrdinal(char[] enumTypeConstantPoolName) {
        this.invoke((byte)-74, 1, 1, enumTypeConstantPoolName, ConstantPool.Ordinal, ConstantPool.OrdinalSignature, 10, TypeBinding.INT);
    }

    public void invokeIterableIterator(TypeBinding iterableReceiverType) {
        if ((iterableReceiverType.tagBits & 0x800L) != 0L) {
            Util.recordNestedType(this.classFile, iterableReceiverType);
        }
        this.invoke(iterableReceiverType.isInterface() ? (byte)-71 : -74, 1, 1, iterableReceiverType.constantPoolName(), ConstantPool.ITERATOR_NAME, ConstantPool.ITERATOR_SIGNATURE, this.getPopularBinding(ConstantPool.JavaUtilIteratorConstantPoolName));
    }

    public void invokeAutoCloseableClose(TypeBinding resourceType) {
        this.invoke(resourceType.erasure().isInterface() ? (byte)-71 : -74, 1, 0, resourceType.constantPoolName(), ConstantPool.Close, ConstantPool.CloseSignature, null);
    }

    public void invokeThrowableAddSuppressed() {
        this.invoke((byte)-74, 2, 0, ConstantPool.JavaLangThrowableConstantPoolName, ConstantPool.AddSuppressed, ConstantPool.AddSuppressedSignature, null);
    }

    public void invokeJavaLangAssertionErrorConstructor(int typeBindingID) {
        int receiverAndArgsSize;
        char[] signature;
        switch (typeBindingID) {
            case 3: 
            case 4: 
            case 10: {
                signature = ConstantPool.IntConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 7: {
                signature = ConstantPool.LongConstrSignature;
                receiverAndArgsSize = 3;
                break;
            }
            case 9: {
                signature = ConstantPool.FloatConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 8: {
                signature = ConstantPool.DoubleConstrSignature;
                receiverAndArgsSize = 3;
                break;
            }
            case 2: {
                signature = ConstantPool.CharConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 5: {
                signature = ConstantPool.BooleanConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 1: 
            case 11: 
            case 12: {
                signature = ConstantPool.ObjectConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            default: {
                return;
            }
        }
        this.invoke((byte)-73, receiverAndArgsSize, 0, ConstantPool.JavaLangAssertionErrorConstantPoolName, ConstantPool.Init, signature, null);
    }

    public void invokeJavaLangAssertionErrorDefaultConstructor() {
        this.invoke((byte)-73, 1, 0, ConstantPool.JavaLangAssertionErrorConstantPoolName, ConstantPool.Init, ConstantPool.DefaultConstructorSignature, null);
    }

    public void invokeJavaLangIncompatibleClassChangeErrorDefaultConstructor() {
        this.invoke((byte)-73, 1, 0, ConstantPool.JavaLangIncompatibleClassChangeErrorConstantPoolName, ConstantPool.Init, ConstantPool.DefaultConstructorSignature, null);
    }

    public void invokeJavaLangClassDesiredAssertionStatus() {
        this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.DesiredAssertionStatus, ConstantPool.DesiredAssertionStatusSignature, 5, TypeBinding.BOOLEAN);
    }

    public void invokeJavaLangEnumvalueOf(ReferenceBinding binding) {
        this.invoke((byte)-72, 2, 1, ConstantPool.JavaLangEnumConstantPoolName, ConstantPool.ValueOf, ConstantPool.ValueOfStringClassSignature, this.getPopularBinding(ConstantPool.JavaLangEnumConstantPoolName));
    }

    public void invokeJavaLangEnumValues(TypeBinding enumBinding, ArrayBinding arrayBinding) {
        char[] signature = "()".toCharArray();
        signature = CharOperation.concat(signature, arrayBinding.constantPoolName());
        this.invoke((byte)-72, 0, 1, enumBinding.constantPoolName(), TypeConstants.VALUES, signature, arrayBinding);
    }

    public void invokeJavaLangErrorConstructor() {
        this.invoke((byte)-73, 2, 0, ConstantPool.JavaLangErrorConstantPoolName, ConstantPool.Init, ConstantPool.StringConstructorSignature, null);
    }

    public void invokeJavaLangReflectConstructorNewInstance() {
        this.invoke((byte)-74, 2, 1, ConstantPool.JavaLangReflectConstructorConstantPoolName, ConstantPool.NewInstance, ConstantPool.JavaLangReflectConstructorNewInstanceSignature, this.getPopularBinding(ConstantPool.JavaLangObjectSignature));
    }

    protected void invokeJavaLangReflectFieldGetter(TypeBinding type) {
        int returnTypeSize;
        char[] signature;
        char[] selector;
        int typeID = type.id;
        switch (typeID) {
            case 10: {
                selector = ConstantPool.GET_INT_METHOD_NAME;
                signature = ConstantPool.GET_INT_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 3: {
                selector = ConstantPool.GET_BYTE_METHOD_NAME;
                signature = ConstantPool.GET_BYTE_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 4: {
                selector = ConstantPool.GET_SHORT_METHOD_NAME;
                signature = ConstantPool.GET_SHORT_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 7: {
                selector = ConstantPool.GET_LONG_METHOD_NAME;
                signature = ConstantPool.GET_LONG_METHOD_SIGNATURE;
                returnTypeSize = 2;
                break;
            }
            case 9: {
                selector = ConstantPool.GET_FLOAT_METHOD_NAME;
                signature = ConstantPool.GET_FLOAT_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 8: {
                selector = ConstantPool.GET_DOUBLE_METHOD_NAME;
                signature = ConstantPool.GET_DOUBLE_METHOD_SIGNATURE;
                returnTypeSize = 2;
                break;
            }
            case 2: {
                selector = ConstantPool.GET_CHAR_METHOD_NAME;
                signature = ConstantPool.GET_CHAR_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 5: {
                selector = ConstantPool.GET_BOOLEAN_METHOD_NAME;
                signature = ConstantPool.GET_BOOLEAN_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            default: {
                selector = ConstantPool.GET_OBJECT_METHOD_NAME;
                signature = ConstantPool.GET_OBJECT_METHOD_SIGNATURE;
                returnTypeSize = 1;
            }
        }
        this.invoke((byte)-74, 2, returnTypeSize, ConstantPool.JAVALANGREFLECTFIELD_CONSTANTPOOLNAME, selector, signature, typeID, type);
    }

    protected void invokeJavaLangReflectFieldSetter(TypeBinding type) {
        int receiverAndArgsSize;
        char[] signature;
        char[] selector;
        int typeID = type.id;
        switch (typeID) {
            case 10: {
                selector = ConstantPool.SET_INT_METHOD_NAME;
                signature = ConstantPool.SET_INT_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 3: {
                selector = ConstantPool.SET_BYTE_METHOD_NAME;
                signature = ConstantPool.SET_BYTE_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 4: {
                selector = ConstantPool.SET_SHORT_METHOD_NAME;
                signature = ConstantPool.SET_SHORT_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 7: {
                selector = ConstantPool.SET_LONG_METHOD_NAME;
                signature = ConstantPool.SET_LONG_METHOD_SIGNATURE;
                receiverAndArgsSize = 4;
                break;
            }
            case 9: {
                selector = ConstantPool.SET_FLOAT_METHOD_NAME;
                signature = ConstantPool.SET_FLOAT_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 8: {
                selector = ConstantPool.SET_DOUBLE_METHOD_NAME;
                signature = ConstantPool.SET_DOUBLE_METHOD_SIGNATURE;
                receiverAndArgsSize = 4;
                break;
            }
            case 2: {
                selector = ConstantPool.SET_CHAR_METHOD_NAME;
                signature = ConstantPool.SET_CHAR_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 5: {
                selector = ConstantPool.SET_BOOLEAN_METHOD_NAME;
                signature = ConstantPool.SET_BOOLEAN_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            default: {
                selector = ConstantPool.SET_OBJECT_METHOD_NAME;
                signature = ConstantPool.SET_OBJECT_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
            }
        }
        this.invoke((byte)-74, receiverAndArgsSize, 0, ConstantPool.JAVALANGREFLECTFIELD_CONSTANTPOOLNAME, selector, signature, typeID, type);
    }

    public void invokeJavaLangReflectMethodInvoke() {
        this.invoke((byte)-74, 3, 1, ConstantPool.JAVALANGREFLECTMETHOD_CONSTANTPOOLNAME, ConstantPool.INVOKE_METHOD_METHOD_NAME, ConstantPool.INVOKE_METHOD_METHOD_SIGNATURE, this.getPopularBinding(ConstantPool.JavaLangObjectSignature));
    }

    public void invokeJavaUtilIteratorHasNext() {
        this.invoke((byte)-71, 1, 1, ConstantPool.JavaUtilIteratorConstantPoolName, ConstantPool.HasNext, ConstantPool.HasNextSignature, 5, TypeBinding.BOOLEAN);
    }

    public void invokeJavaUtilIteratorNext() {
        this.invoke((byte)-71, 1, 1, ConstantPool.JavaUtilIteratorConstantPoolName, ConstantPool.Next, ConstantPool.NextSignature, this.getPopularBinding(ConstantPool.JavaLangObjectSignature));
    }

    public void invokeNoClassDefFoundErrorStringConstructor() {
        this.invoke((byte)-73, 2, 0, ConstantPool.JavaLangNoClassDefFoundErrorConstantPoolName, ConstantPool.Init, ConstantPool.StringConstructorSignature, null);
    }

    public void invokeObjectGetClass() {
        this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangObjectConstantPoolName, ConstantPool.GetClass, ConstantPool.GetClassSignature, this.getPopularBinding(ConstantPool.JavaLangClassConstantPoolName));
    }

    public void invokeStringConcatenationAppendForType(int typeID) {
        int receiverAndArgsSize;
        char[] declaringClass = null;
        char[] selector = ConstantPool.Append;
        char[] signature = null;
        switch (typeID) {
            case 3: 
            case 4: 
            case 10: {
                if (this.targetLevel >= 0x310000L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendIntSignature;
                } else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendIntSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            case 7: {
                if (this.targetLevel >= 0x310000L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendLongSignature;
                } else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendLongSignature;
                }
                receiverAndArgsSize = 3;
                break;
            }
            case 9: {
                if (this.targetLevel >= 0x310000L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendFloatSignature;
                } else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendFloatSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            case 8: {
                if (this.targetLevel >= 0x310000L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendDoubleSignature;
                } else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendDoubleSignature;
                }
                receiverAndArgsSize = 3;
                break;
            }
            case 2: {
                if (this.targetLevel >= 0x310000L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendCharSignature;
                } else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendCharSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            case 5: {
                if (this.targetLevel >= 0x310000L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendBooleanSignature;
                } else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendBooleanSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            case 11: {
                if (this.targetLevel >= 0x310000L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendStringSignature;
                } else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendStringSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            default: {
                if (this.targetLevel >= 0x310000L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendObjectSignature;
                } else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendObjectSignature;
                }
                receiverAndArgsSize = 2;
            }
        }
        TypeBinding type = this.targetLevel >= 0x3A0000L ? this.getPopularBinding(ConstantPool.JavaLangStringBuilderConstantPoolName) : null;
        this.invoke((byte)-74, receiverAndArgsSize, 1, declaringClass, selector, signature, typeID, type);
    }

    public void invokeStringConcatenationDefaultConstructor() {
        char[] declaringClass = this.targetLevel < 0x310000L ? ConstantPool.JavaLangStringBufferConstantPoolName : ConstantPool.JavaLangStringBuilderConstantPoolName;
        this.invoke((byte)-73, 1, 0, declaringClass, ConstantPool.Init, ConstantPool.DefaultConstructorSignature, null);
    }

    public void invokeStringConcatenationStringConstructor() {
        char[] declaringClass = this.targetLevel < 0x310000L ? ConstantPool.JavaLangStringBufferConstantPoolName : ConstantPool.JavaLangStringBuilderConstantPoolName;
        this.invoke((byte)-73, 2, 0, declaringClass, ConstantPool.Init, ConstantPool.StringConstructorSignature, null);
    }

    public void invokeStringConcatenationToString() {
        char[] declaringClass = this.targetLevel < 0x310000L ? ConstantPool.JavaLangStringBufferConstantPoolName : ConstantPool.JavaLangStringBuilderConstantPoolName;
        this.invoke((byte)-74, 1, 1, declaringClass, ConstantPool.ToString, ConstantPool.ToStringSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
    }

    public void invokeStringEquals() {
        this.invoke((byte)-74, 2, 1, ConstantPool.JavaLangStringConstantPoolName, ConstantPool.Equals, ConstantPool.EqualsSignature, 5, TypeBinding.BOOLEAN);
    }

    public void invokeObjectEquals() {
        this.invoke((byte)-74, 2, 1, ConstantPool.JavaLangObjectConstantPoolName, ConstantPool.Equals, ConstantPool.EqualsSignature, 5, TypeBinding.BOOLEAN);
    }

    public void invokeStringHashCode() {
        this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangStringConstantPoolName, ConstantPool.HashCode, ConstantPool.HashCodeSignature, 10, TypeBinding.INT);
    }

    public void invokeStringIntern() {
        this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangStringConstantPoolName, ConstantPool.Intern, ConstantPool.InternSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
    }

    public void invokeStringValueOf(int typeID) {
        int receiverAndArgsSize;
        char[] signature;
        switch (typeID) {
            case 3: 
            case 4: 
            case 10: {
                signature = ConstantPool.ValueOfIntSignature;
                receiverAndArgsSize = 1;
                break;
            }
            case 7: {
                signature = ConstantPool.ValueOfLongSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 9: {
                signature = ConstantPool.ValueOfFloatSignature;
                receiverAndArgsSize = 1;
                break;
            }
            case 8: {
                signature = ConstantPool.ValueOfDoubleSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 2: {
                signature = ConstantPool.ValueOfCharSignature;
                receiverAndArgsSize = 1;
                break;
            }
            case 5: {
                signature = ConstantPool.ValueOfBooleanSignature;
                receiverAndArgsSize = 1;
                break;
            }
            case 0: 
            case 1: 
            case 11: 
            case 12: {
                signature = ConstantPool.ValueOfObjectSignature;
                receiverAndArgsSize = 1;
                break;
            }
            default: {
                return;
            }
        }
        this.invoke((byte)-72, receiverAndArgsSize, 1, ConstantPool.JavaLangStringConstantPoolName, ConstantPool.ValueOf, signature, typeID, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
    }

    public void invokeSystemArraycopy() {
        this.invoke((byte)-72, 5, 0, ConstantPool.JavaLangSystemConstantPoolName, ConstantPool.ArrayCopy, ConstantPool.ArrayCopySignature, null);
    }

    public void invokeThrowableGetMessage() {
        this.invoke((byte)-74, 1, 1, ConstantPool.JavaLangThrowableConstantPoolName, ConstantPool.GetMessage, ConstantPool.GetMessageSignature, this.getPopularBinding(ConstantPool.JavaLangStringConstantPoolName));
    }

    public void ior() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -128;
    }

    public void irem() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 112;
    }

    public void ireturn() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -84;
        this.lastAbruptCompletion = this.position;
    }

    public boolean isDefinitelyAssigned(Scope scope, int initStateIndex, LocalVariableBinding local) {
        if ((local.tagBits & 0x400L) != 0L) {
            return true;
        }
        if (initStateIndex == -1) {
            return false;
        }
        int localPosition = local.id + this.maxFieldCount;
        MethodScope methodScope = scope.methodScope();
        if (localPosition < 64) {
            return (methodScope.definiteInits[initStateIndex] & 1L << localPosition) != 0L;
        }
        long[] extraInits = methodScope.extraDefiniteInits[initStateIndex];
        if (extraInits == null) {
            return false;
        }
        int vectorIndex = localPosition / 64 - 1;
        if (vectorIndex >= extraInits.length) {
            return false;
        }
        return (extraInits[vectorIndex] & 1L << localPosition % 64) != 0L;
    }

    public void ishl() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 120;
    }

    public void ishr() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 122;
    }

    public void istore(int iArg) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 54;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 54;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void istore_0() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 59;
    }

    public void istore_1() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 60;
    }

    public void istore_2() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 61;
    }

    public void istore_3() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 62;
    }

    public void isub() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 100;
    }

    public void iushr() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 124;
    }

    public void ixor() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -126;
    }

    public final void jsr(BranchLabel lbl) {
        if (this.wideMode) {
            this.jsr_w(lbl);
            return;
        }
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -88;
        lbl.branch();
    }

    public final void jsr_w(BranchLabel lbl) {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -55;
        lbl.branchWide();
    }

    public void l2d() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -118;
        this.pushTypeBinding(1, TypeBinding.DOUBLE);
    }

    public void l2f() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(1, TypeBinding.FLOAT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -119;
    }

    public void l2i() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(1, TypeBinding.INT);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -120;
    }

    public void ladd() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 97;
    }

    public void laload() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 47;
        this.pushTypeBindingArray();
    }

    public void land() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 127;
    }

    public void lastore() {
        this.countLabels = 0;
        this.stackDepth -= 4;
        this.popTypeBinding(3);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 80;
    }

    public void lcmp() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -108;
    }

    public void lconst_0() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.LONG);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 9;
    }

    public void lconst_1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.LONG);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 10;
    }

    public void ldc(float constant) {
        this.countLabels = 0;
        int index = this.constantPool.literalIndex(constant);
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.FLOAT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (index > 255) {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 19;
            this.writeUnsignedShort(index);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 18;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }

    public void ldc(int constant) {
        this.countLabels = 0;
        int index = this.constantPool.literalIndex(constant);
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (index > 255) {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 19;
            this.writeUnsignedShort(index);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 18;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }

    public void ldc(String constant) {
        this.countLabels = 0;
        int currentCodeStreamPosition = this.position;
        char[] constantChars = constant.toCharArray();
        int index = this.constantPool.literalIndexForLdc(constantChars);
        if (index > 0) {
            this.ldcForIndex(index);
        } else {
            this.position = currentCodeStreamPosition;
            int i = 0;
            int length = 0;
            int constantLength = constant.length();
            byte[] utf8encoding = new byte[Math.min(constantLength + 100, 65535)];
            int utf8encodingLength = 0;
            while (length < 65532 && i < constantLength) {
                char current = constantChars[i];
                utf8encodingLength = utf8encoding.length;
                if (length + 3 > utf8encodingLength) {
                    byte[] byArray = utf8encoding;
                    utf8encoding = new byte[Math.min(utf8encodingLength + 100, 65535)];
                    System.arraycopy(byArray, 0, utf8encoding, 0, length);
                }
                if (current >= '\u0001' && current <= '\u007f') {
                    utf8encoding[length++] = (byte)current;
                } else if (current > '\u07ff') {
                    utf8encoding[length++] = (byte)(0xE0 | current >> 12 & 0xF);
                    utf8encoding[length++] = (byte)(0x80 | current >> 6 & 0x3F);
                    utf8encoding[length++] = (byte)(0x80 | current & 0x3F);
                } else {
                    utf8encoding[length++] = (byte)(0xC0 | current >> 6 & 0x1F);
                    utf8encoding[length++] = (byte)(0x80 | current & 0x3F);
                }
                ++i;
            }
            this.newStringContatenation();
            this.dup();
            char[] subChars = new char[i];
            System.arraycopy(constantChars, 0, subChars, 0, i);
            byte[] byArray = utf8encoding;
            utf8encoding = new byte[length];
            System.arraycopy(byArray, 0, utf8encoding, 0, length);
            index = this.constantPool.literalIndex(subChars, utf8encoding);
            this.ldcForIndex(index);
            this.invokeStringConcatenationStringConstructor();
            while (i < constantLength) {
                length = 0;
                utf8encoding = new byte[Math.min(constantLength - i + 100, 65535)];
                int startIndex = i;
                while (length < 65532 && i < constantLength) {
                    char current = constantChars[i];
                    utf8encodingLength = utf8encoding.length;
                    if (length + 3 > utf8encodingLength) {
                        byte[] byArray2 = utf8encoding;
                        utf8encoding = new byte[Math.min(utf8encodingLength + 100, 65535)];
                        System.arraycopy(byArray2, 0, utf8encoding, 0, length);
                    }
                    if (current >= '\u0001' && current <= '\u007f') {
                        utf8encoding[length++] = (byte)current;
                    } else if (current > '\u07ff') {
                        utf8encoding[length++] = (byte)(0xE0 | current >> 12 & 0xF);
                        utf8encoding[length++] = (byte)(0x80 | current >> 6 & 0x3F);
                        utf8encoding[length++] = (byte)(0x80 | current & 0x3F);
                    } else {
                        utf8encoding[length++] = (byte)(0xC0 | current >> 6 & 0x1F);
                        utf8encoding[length++] = (byte)(0x80 | current & 0x3F);
                    }
                    ++i;
                }
                int newCharLength = i - startIndex;
                subChars = new char[newCharLength];
                System.arraycopy(constantChars, startIndex, subChars, 0, newCharLength);
                byte[] byArray3 = utf8encoding;
                utf8encoding = new byte[length];
                System.arraycopy(byArray3, 0, utf8encoding, 0, length);
                index = this.constantPool.literalIndex(subChars, utf8encoding);
                this.ldcForIndex(index);
                this.invokeStringConcatenationAppendForType(11);
            }
            this.invokeStringConcatenationToString();
            this.invokeStringIntern();
        }
    }

    public void ldc(TypeBinding typeBinding) {
        this.countLabels = 0;
        int index = this.constantPool.literalIndexForType(typeBinding);
        ++this.stackDepth;
        this.pushTypeBinding(typeBinding);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (index > 255) {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 19;
            this.writeUnsignedShort(index);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 18;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }

    public void ldc2_w(double constant) {
        this.countLabels = 0;
        int index = this.constantPool.literalIndex(constant);
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.DOUBLE);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 20;
        this.writeUnsignedShort(index);
    }

    public void ldc2_w(long constant) {
        this.countLabels = 0;
        int index = this.constantPool.literalIndex(constant);
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.LONG);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 20;
        this.writeUnsignedShort(index);
    }

    public void ldcForIndex(int index) {
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.INT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (index > 255) {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 19;
            this.writeUnsignedShort(index);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 18;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }

    public void ldiv() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 109;
    }

    public void lload(int iArg) {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.maxLocals <= iArg + 1) {
            this.maxLocals = iArg + 2;
        }
        this.pushTypeBinding(TypeBinding.LONG);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 22;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 22;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void lload_0() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.LONG);
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 30;
    }

    public void lload_1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.LONG);
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 31;
    }

    public void lload_2() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.LONG);
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 32;
    }

    public void lload_3() {
        this.countLabels = 0;
        this.stackDepth += 2;
        this.pushTypeBinding(TypeBinding.LONG);
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 33;
    }

    public void lmul() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 105;
    }

    public void lneg() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 117;
        this.pushTypeBinding(1, TypeBinding.LONG);
    }

    public final void load(LocalVariableBinding localBinding) {
        this.load(localBinding.type, localBinding.resolvedPosition);
    }

    protected final void load(TypeBinding typeBinding, int resolvedPosition) {
        this.countLabels = 0;
        block0 : switch (typeBinding.id) {
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 10: {
                switch (resolvedPosition) {
                    case 0: {
                        this.iload_0();
                        break block0;
                    }
                    case 1: {
                        this.iload_1();
                        break block0;
                    }
                    case 2: {
                        this.iload_2();
                        break block0;
                    }
                    case 3: {
                        this.iload_3();
                        break block0;
                    }
                }
                this.iload(resolvedPosition);
                break;
            }
            case 9: {
                switch (resolvedPosition) {
                    case 0: {
                        this.fload_0();
                        break block0;
                    }
                    case 1: {
                        this.fload_1();
                        break block0;
                    }
                    case 2: {
                        this.fload_2();
                        break block0;
                    }
                    case 3: {
                        this.fload_3();
                        break block0;
                    }
                }
                this.fload(resolvedPosition);
                break;
            }
            case 7: {
                switch (resolvedPosition) {
                    case 0: {
                        this.lload_0();
                        break block0;
                    }
                    case 1: {
                        this.lload_1();
                        break block0;
                    }
                    case 2: {
                        this.lload_2();
                        break block0;
                    }
                    case 3: {
                        this.lload_3();
                        break block0;
                    }
                }
                this.lload(resolvedPosition);
                break;
            }
            case 8: {
                switch (resolvedPosition) {
                    case 0: {
                        this.dload_0();
                        break block0;
                    }
                    case 1: {
                        this.dload_1();
                        break block0;
                    }
                    case 2: {
                        this.dload_2();
                        break block0;
                    }
                    case 3: {
                        this.dload_3();
                        break block0;
                    }
                }
                this.dload(resolvedPosition);
                break;
            }
            default: {
                switch (resolvedPosition) {
                    case 0: {
                        this.aload_0();
                        break block0;
                    }
                    case 1: {
                        this.aload_1();
                        break block0;
                    }
                    case 2: {
                        this.aload_2();
                        break block0;
                    }
                    case 3: {
                        this.aload_3();
                        break block0;
                    }
                }
                this.aload(resolvedPosition);
            }
        }
    }

    public void lookupswitch(CaseLabel defaultLabel, int[] keys, int[] sortedIndexes, CaseLabel[] casesLabel) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        int length = keys.length;
        int pos = this.position;
        defaultLabel.placeInstruction();
        int i = 0;
        while (i < length) {
            casesLabel[i].placeInstruction();
            ++i;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -85;
        i = 3 - (pos & 3);
        while (i > 0) {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 0;
            --i;
        }
        defaultLabel.branch();
        this.writeSignedWord(length);
        i = 0;
        while (i < length) {
            this.writeSignedWord(keys[sortedIndexes[i]]);
            casesLabel[sortedIndexes[i]].branch();
            ++i;
        }
    }

    public void lor() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -127;
    }

    public void lrem() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 113;
    }

    public void lreturn() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -83;
        this.lastAbruptCompletion = this.position;
    }

    public void lshl() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 121;
    }

    public void lshr() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 123;
    }

    public void lstore(int iArg) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals <= iArg + 1) {
            this.maxLocals = iArg + 2;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 55;
            this.writeUnsignedShort(iArg);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 55;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }

    public void lstore_0() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 63;
    }

    public void lstore_1() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 64;
    }

    public void lstore_2() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 65;
    }

    public void lstore_3() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.popTypeBinding();
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 66;
    }

    public void lsub() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 101;
    }

    public void lushr() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 125;
    }

    public void lxor() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.pushTypeBinding(2, TypeBinding.LONG);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -125;
    }

    public void monitorenter() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -62;
    }

    public void monitorexit() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -61;
    }

    public void multianewarray(TypeReference typeReference, TypeBinding typeBinding, int dimensions, ArrayAllocationExpression allocationExpression) {
        this.countLabels = 0;
        this.stackDepth += 1 - dimensions;
        this.pushTypeBinding(dimensions, typeBinding);
        if (this.classFileOffset + 3 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = -59;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
        this.bCodeStream[this.classFileOffset++] = (byte)dimensions;
    }

    public void new_(TypeBinding typeBinding) {
        this.new_(null, typeBinding);
    }

    public void new_(TypeReference typeReference, TypeBinding typeBinding) {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(typeBinding);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 3 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
    }

    public void newarray(int array_Type) {
        this.countLabels = 0;
        if (this.classFileOffset + 1 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = -68;
        this.bCodeStream[this.classFileOffset++] = (byte)array_Type;
        this.pushTypeBinding(1, TypeBinding.wellKnownBaseType(array_Type));
    }

    public void newArray(ArrayBinding arrayBinding) {
        this.newArray(null, null, arrayBinding);
    }

    public void newArray(TypeReference typeReference, ArrayAllocationExpression allocationExpression, ArrayBinding arrayBinding) {
        TypeBinding component = arrayBinding.elementsType();
        switch (component.id) {
            case 10: {
                this.newarray(10);
                break;
            }
            case 3: {
                this.newarray(8);
                break;
            }
            case 5: {
                this.newarray(4);
                break;
            }
            case 4: {
                this.newarray(9);
                break;
            }
            case 2: {
                this.newarray(5);
                break;
            }
            case 7: {
                this.newarray(11);
                break;
            }
            case 9: {
                this.newarray(6);
                break;
            }
            case 8: {
                this.newarray(7);
                break;
            }
            default: {
                this.anewarray(component);
            }
        }
    }

    public void newJavaLangAssertionError() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(ConstantPool.JavaLangAssertionErrorConstantPoolName);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangAssertionErrorConstantPoolName));
    }

    public void newJavaLangError() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(ConstantPool.JavaLangErrorConstantPoolName);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangErrorConstantPoolName));
    }

    public void newJavaLangIncompatibleClassChangeError() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(ConstantPool.JavaLangIncompatibleClassChangeErrorConstantPoolName);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangIncompatibleClassChangeErrorConstantPoolName));
    }

    public void newNoClassDefFoundError() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(ConstantPool.JavaLangNoClassDefFoundErrorConstantPoolName);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangNoClassDefFoundErrorConstantPoolName));
    }

    public void newStringContatenation() {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(ConstantPool.JavaLangStringBufferConstantPoolName);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        if (this.targetLevel >= 0x310000L) {
            this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangStringBuilderConstantPoolName));
        } else {
            this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangStringBufferConstantPoolName));
        }
    }

    public void newWrapperFor(int typeID) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        switch (typeID) {
            case 10: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangIntegerConstantPoolName));
                this.pushTypeBinding(ConstantPool.JavaLangIntegerConstantPoolName);
                break;
            }
            case 5: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangBooleanConstantPoolName));
                this.pushTypeBinding(ConstantPool.JavaLangBooleanConstantPoolName);
                break;
            }
            case 3: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangByteConstantPoolName));
                this.pushTypeBinding(ConstantPool.JavaLangByteConstantPoolName);
                break;
            }
            case 2: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangCharacterConstantPoolName));
                this.pushTypeBinding(ConstantPool.JavaLangCharacterConstantPoolName);
                break;
            }
            case 9: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangFloatConstantPoolName));
                this.pushTypeBinding(ConstantPool.JavaLangFloatConstantPoolName);
                break;
            }
            case 8: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangDoubleConstantPoolName));
                this.pushTypeBinding(ConstantPool.JavaLangDoubleConstantPoolName);
                break;
            }
            case 4: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangShortConstantPoolName));
                this.pushTypeBinding(ConstantPool.JavaLangShortConstantPoolName);
                break;
            }
            case 7: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangLongConstantPoolName));
                this.pushTypeBinding(ConstantPool.JavaLangLongConstantPoolName);
                break;
            }
            case 6: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangVoidConstantPoolName));
                this.pushTypeBinding(ConstantPool.JavaLangVoidConstantPoolName);
            }
        }
    }

    public void nop() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 0;
    }

    public void optimizeBranch(int oldPosition, BranchLabel lbl) {
        int i = 0;
        while (i < this.countLabels) {
            BranchLabel label = this.labels[i];
            if (oldPosition == label.position) {
                label.position = this.position;
                if (label instanceof CaseLabel) {
                    int offset = this.position - ((CaseLabel)label).instructionPosition;
                    int[] forwardRefs = label.forwardReferences();
                    int j = 0;
                    int length = label.forwardReferenceCount();
                    while (j < length) {
                        int forwardRef = forwardRefs[j];
                        this.writeSignedWord(forwardRef, offset);
                        ++j;
                    }
                } else {
                    int[] forwardRefs = label.forwardReferences();
                    int j = 0;
                    int length = label.forwardReferenceCount();
                    while (j < length) {
                        int forwardRef = forwardRefs[j];
                        this.writePosition(lbl, forwardRef);
                        ++j;
                    }
                }
            }
            ++i;
        }
    }

    public void pop() {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 87;
    }

    private void adjustTypeBindingStackForPop2() {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        TypeBinding v1 = this.switchSaveTypeBindings.peek();
        if (TypeIds.getCategory(v1.id) == 1) {
            TypeBinding v2 = this.switchSaveTypeBindings.peek();
            if (TypeIds.getCategory(v2.id) == 1) {
                this.popTypeBinding(2);
            }
        } else {
            this.popTypeBinding();
        }
    }

    public void pop2() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        this.adjustTypeBindingStackForPop2();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 88;
    }

    public void pushExceptionOnStack(TypeBinding binding) {
        this.stackDepth = 1;
        this.pushTypeBinding(binding);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
    }

    public void pushOnStack(TypeBinding binding) {
        if (++this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        this.pushTypeBinding(binding);
    }

    public void record(LocalVariableBinding local) {
        if ((this.generateAttributes & 0x1C) == 0) {
            return;
        }
        if (this.allLocalsCounter == this.locals.length) {
            this.locals = new LocalVariableBinding[this.allLocalsCounter + 10];
            System.arraycopy(this.locals, 0, this.locals, 0, this.allLocalsCounter);
        }
        this.locals[this.allLocalsCounter++] = local;
        local.initializationPCs = new int[4];
        local.initializationCount = 0;
    }

    public void recordExpressionType(TypeBinding typeBinding) {
    }

    public void recordExpressionType(TypeBinding typeBinding, int delta, boolean adjustStackDepth) {
    }

    public void recordPositionsFrom(int startPC, int sourcePos) {
        this.recordPositionsFrom(startPC, sourcePos, false);
    }

    public void recordPositionsFrom(int startPC, int sourcePos, boolean widen) {
        if ((this.generateAttributes & 2) == 0 || sourcePos == 0 || startPC == this.position && !widen || startPC > this.position) {
            return;
        }
        if (this.pcToSourceMapSize + 4 > this.pcToSourceMap.length) {
            this.pcToSourceMap = new int[this.pcToSourceMapSize << 1];
            System.arraycopy(this.pcToSourceMap, 0, this.pcToSourceMap, 0, this.pcToSourceMapSize);
        }
        if (this.pcToSourceMapSize > 0) {
            int insertionIndex;
            int lineNumber = -1;
            int previousLineNumber = this.pcToSourceMap[this.pcToSourceMapSize - 1];
            if (this.lineNumberStart == this.lineNumberEnd) {
                lineNumber = this.lineNumberStart;
            } else {
                int[] lineSeparatorPositions2 = this.lineSeparatorPositions;
                int length = lineSeparatorPositions2.length;
                if (previousLineNumber == 1) {
                    if (sourcePos < lineSeparatorPositions2[0]) {
                        lineNumber = 1;
                    } else if (length == 1 || sourcePos < lineSeparatorPositions2[1]) {
                        lineNumber = 2;
                    }
                } else if (previousLineNumber < length) {
                    if (lineSeparatorPositions2[previousLineNumber - 2] < sourcePos) {
                        if (sourcePos < lineSeparatorPositions2[previousLineNumber - 1]) {
                            lineNumber = previousLineNumber;
                        } else if (sourcePos < lineSeparatorPositions2[previousLineNumber]) {
                            lineNumber = previousLineNumber + 1;
                        }
                    }
                } else if (lineSeparatorPositions2[length - 1] < sourcePos) {
                    lineNumber = length + 1;
                }
                if (lineNumber == -1) {
                    lineNumber = Util.getLineNumber(sourcePos, lineSeparatorPositions2, this.lineNumberStart - 1, this.lineNumberEnd - 1);
                }
            }
            if (previousLineNumber != lineNumber) {
                if (startPC <= this.lastEntryPC) {
                    int insertionIndex2 = CodeStream.insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
                    if (insertionIndex2 != -1) {
                        if (insertionIndex2 <= 1 || this.pcToSourceMap[insertionIndex2 - 1] != lineNumber) {
                            if (insertionIndex2 < this.pcToSourceMapSize && this.pcToSourceMap[insertionIndex2 + 1] == lineNumber) {
                                this.pcToSourceMap[insertionIndex2] = startPC;
                            } else {
                                System.arraycopy(this.pcToSourceMap, insertionIndex2, this.pcToSourceMap, insertionIndex2 + 2, this.pcToSourceMapSize - insertionIndex2);
                                this.pcToSourceMap[insertionIndex2++] = startPC;
                                this.pcToSourceMap[insertionIndex2] = lineNumber;
                                this.pcToSourceMapSize += 2;
                            }
                        }
                    } else if (this.position != this.lastEntryPC) {
                        if (this.lastEntryPC == startPC || this.lastEntryPC == this.pcToSourceMap[this.pcToSourceMapSize - 2]) {
                            this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                        } else {
                            this.pcToSourceMap[this.pcToSourceMapSize++] = this.lastEntryPC;
                            this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                        }
                    } else if (this.pcToSourceMap[this.pcToSourceMapSize - 1] < lineNumber && widen) {
                        this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                    }
                } else {
                    this.pcToSourceMap[this.pcToSourceMapSize++] = startPC;
                    this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                }
            } else if (startPC < this.pcToSourceMap[this.pcToSourceMapSize - 2] && (insertionIndex = CodeStream.insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC)) != -1 && (insertionIndex <= 1 || this.pcToSourceMap[insertionIndex - 1] != lineNumber)) {
                if (this.pcToSourceMap[insertionIndex + 1] != lineNumber) {
                    System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - insertionIndex);
                    this.pcToSourceMap[insertionIndex++] = startPC;
                    this.pcToSourceMap[insertionIndex] = lineNumber;
                    this.pcToSourceMapSize += 2;
                } else {
                    this.pcToSourceMap[insertionIndex] = startPC;
                }
            }
            this.lastEntryPC = this.position;
        } else {
            int lineNumber = 0;
            lineNumber = this.lineNumberStart == this.lineNumberEnd ? this.lineNumberStart : Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
            this.pcToSourceMap[this.pcToSourceMapSize++] = startPC;
            this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
            this.lastEntryPC = this.position;
        }
    }

    public void registerExceptionHandler(ExceptionLabel anExceptionLabel) {
        int length = this.exceptionLabels.length;
        if (this.exceptionLabelsCounter == length) {
            this.exceptionLabels = new ExceptionLabel[length + 5];
            System.arraycopy(this.exceptionLabels, 0, this.exceptionLabels, 0, length);
        }
        this.exceptionLabels[this.exceptionLabelsCounter++] = anExceptionLabel;
    }

    public void removeNotDefinitelyAssignedVariables(Scope scope, int initStateIndex) {
        if ((this.generateAttributes & 0x1C) == 0) {
            return;
        }
        int i = 0;
        while (i < this.visibleLocalsCount) {
            LocalVariableBinding localBinding = this.visibleLocals[i];
            if (localBinding != null && !this.isDefinitelyAssigned(scope, initStateIndex, localBinding) && localBinding.initializationCount > 0) {
                localBinding.recordInitializationEndPC(this.position);
            }
            ++i;
        }
    }

    public void removeUnusedPcToSourceMapEntries() {
        if (this.pcToSourceMapSize != 0) {
            while (this.pcToSourceMapSize >= 2 && this.pcToSourceMap[this.pcToSourceMapSize - 2] > this.position) {
                this.pcToSourceMapSize -= 2;
            }
        }
    }

    public void removeVariable(LocalVariableBinding localBinding) {
        if (localBinding == null) {
            return;
        }
        if (localBinding.initializationCount > 0) {
            localBinding.recordInitializationEndPC(this.position);
        }
        int i = this.visibleLocalsCount - 1;
        while (i >= 0) {
            LocalVariableBinding visibleLocal = this.visibleLocals[i];
            if (visibleLocal == localBinding) {
                this.visibleLocals[i] = null;
                return;
            }
            --i;
        }
    }

    public void reset(AbstractMethodDeclaration referenceMethod, ClassFile targetClassFile) {
        this.init(targetClassFile);
        this.methodDeclaration = referenceMethod;
        this.lambdaExpression = null;
        int[] lineSeparatorPositions2 = this.lineSeparatorPositions;
        if (lineSeparatorPositions2 != null) {
            int length = lineSeparatorPositions2.length;
            int lineSeparatorPositionsEnd = length - 1;
            if (referenceMethod.isClinit() || referenceMethod.isConstructor()) {
                this.lineNumberStart = 1;
                this.lineNumberEnd = length == 0 ? 1 : length;
            } else {
                int start;
                this.lineNumberStart = start = Util.getLineNumber(referenceMethod.bodyStart, lineSeparatorPositions2, 0, lineSeparatorPositionsEnd);
                if (start > lineSeparatorPositionsEnd) {
                    this.lineNumberEnd = start;
                } else {
                    int end = Util.getLineNumber(referenceMethod.bodyEnd, lineSeparatorPositions2, start - 1, lineSeparatorPositionsEnd);
                    if (end >= lineSeparatorPositionsEnd) {
                        end = length;
                    }
                    this.lineNumberEnd = end == 0 ? 1 : end;
                }
            }
        }
        this.preserveUnusedLocals = referenceMethod.scope.compilerOptions().preserveAllLocalVariables;
        this.initializeMaxLocals(referenceMethod.binding);
    }

    public void reset(LambdaExpression lambda, ClassFile targetClassFile) {
        this.init(targetClassFile);
        this.lambdaExpression = lambda;
        this.methodDeclaration = null;
        int[] lineSeparatorPositions2 = this.lineSeparatorPositions;
        if (lineSeparatorPositions2 != null) {
            int start;
            int length = lineSeparatorPositions2.length;
            int lineSeparatorPositionsEnd = length - 1;
            this.lineNumberStart = start = Util.getLineNumber(lambda.body().sourceStart, lineSeparatorPositions2, 0, lineSeparatorPositionsEnd);
            if (start > lineSeparatorPositionsEnd) {
                this.lineNumberEnd = start;
            } else {
                int end = Util.getLineNumber(lambda.body().sourceEnd, lineSeparatorPositions2, start - 1, lineSeparatorPositionsEnd);
                if (end >= lineSeparatorPositionsEnd) {
                    end = length;
                }
                this.lineNumberEnd = end == 0 ? 1 : end;
            }
        }
        this.preserveUnusedLocals = lambda.scope.compilerOptions().preserveAllLocalVariables;
        this.initializeMaxLocals(lambda.binding);
    }

    public void reset(ClassFile givenClassFile) {
        int produceAttributes;
        this.targetLevel = givenClassFile.targetJDK;
        this.generateAttributes = produceAttributes = givenClassFile.produceAttributes;
        this.lineSeparatorPositions = (int[])((produceAttributes & 2) != 0 && givenClassFile.referenceBinding != null ? givenClassFile.referenceBinding.scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions() : null);
    }

    public void resetForProblemClinit(ClassFile targetClassFile) {
        this.init(targetClassFile);
        this.initializeMaxLocals(null);
    }

    public void resetInWideMode() {
        this.wideMode = true;
    }

    public void resetForCodeGenUnusedLocals() {
    }

    private final void resizeByteArray() {
        int length = this.bCodeStream.length;
        int requiredSize = length + length;
        if (this.classFileOffset >= requiredSize) {
            requiredSize = this.classFileOffset + length;
        }
        this.bCodeStream = new byte[requiredSize];
        System.arraycopy(this.bCodeStream, 0, this.bCodeStream, 0, length);
    }

    public final void ret(int index) {
        this.countLabels = 0;
        if (index > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = -87;
            this.writeUnsignedShort(index);
        } else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -87;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }

    public void return_() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -79;
        this.lastAbruptCompletion = this.position;
    }

    public void saload() {
        this.countLabels = 0;
        --this.stackDepth;
        this.pushTypeBindingArray();
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 53;
    }

    public void sastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        this.popTypeBinding(3);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 86;
    }

    public void sendOperator(int operatorConstant, int type_ID) {
        block0 : switch (type_ID) {
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 10: {
                switch (operatorConstant) {
                    case 14: {
                        this.iadd();
                        break;
                    }
                    case 13: {
                        this.isub();
                        break;
                    }
                    case 15: {
                        this.imul();
                        break;
                    }
                    case 9: {
                        this.idiv();
                        break;
                    }
                    case 16: {
                        this.irem();
                        break;
                    }
                    case 10: {
                        this.ishl();
                        break;
                    }
                    case 17: {
                        this.ishr();
                        break;
                    }
                    case 19: {
                        this.iushr();
                        break;
                    }
                    case 2: {
                        this.iand();
                        break;
                    }
                    case 3: {
                        this.ior();
                        break;
                    }
                    case 8: {
                        this.ixor();
                    }
                }
                break;
            }
            case 7: {
                switch (operatorConstant) {
                    case 14: {
                        this.ladd();
                        break;
                    }
                    case 13: {
                        this.lsub();
                        break;
                    }
                    case 15: {
                        this.lmul();
                        break;
                    }
                    case 9: {
                        this.ldiv();
                        break;
                    }
                    case 16: {
                        this.lrem();
                        break;
                    }
                    case 10: {
                        this.lshl();
                        break;
                    }
                    case 17: {
                        this.lshr();
                        break;
                    }
                    case 19: {
                        this.lushr();
                        break;
                    }
                    case 2: {
                        this.land();
                        break;
                    }
                    case 3: {
                        this.lor();
                        break;
                    }
                    case 8: {
                        this.lxor();
                    }
                }
                break;
            }
            case 9: {
                switch (operatorConstant) {
                    case 14: {
                        this.fadd();
                        break;
                    }
                    case 13: {
                        this.fsub();
                        break;
                    }
                    case 15: {
                        this.fmul();
                        break;
                    }
                    case 9: {
                        this.fdiv();
                        break;
                    }
                    case 16: {
                        this.frem();
                    }
                }
                break;
            }
            case 8: {
                switch (operatorConstant) {
                    case 14: {
                        this.dadd();
                        break block0;
                    }
                    case 13: {
                        this.dsub();
                        break block0;
                    }
                    case 15: {
                        this.dmul();
                        break block0;
                    }
                    case 9: {
                        this.ddiv();
                        break block0;
                    }
                    case 16: {
                        this.drem();
                    }
                }
            }
        }
    }

    public void sipush(int s) {
        this.countLabels = 0;
        ++this.stackDepth;
        this.pushTypeBinding(TypeBinding.SHORT);
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 17;
        this.writeSignedShort(s);
    }

    public void store(LocalVariableBinding localBinding, boolean valueRequired) {
        int localPosition = localBinding.resolvedPosition;
        block0 : switch (localBinding.type.id) {
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 10: {
                if (valueRequired) {
                    this.dup();
                }
                switch (localPosition) {
                    case 0: {
                        this.istore_0();
                        break block0;
                    }
                    case 1: {
                        this.istore_1();
                        break block0;
                    }
                    case 2: {
                        this.istore_2();
                        break block0;
                    }
                    case 3: {
                        this.istore_3();
                        break block0;
                    }
                }
                this.istore(localPosition);
                break;
            }
            case 9: {
                if (valueRequired) {
                    this.dup();
                }
                switch (localPosition) {
                    case 0: {
                        this.fstore_0();
                        break block0;
                    }
                    case 1: {
                        this.fstore_1();
                        break block0;
                    }
                    case 2: {
                        this.fstore_2();
                        break block0;
                    }
                    case 3: {
                        this.fstore_3();
                        break block0;
                    }
                }
                this.fstore(localPosition);
                break;
            }
            case 8: {
                if (valueRequired) {
                    this.dup2();
                }
                switch (localPosition) {
                    case 0: {
                        this.dstore_0();
                        break block0;
                    }
                    case 1: {
                        this.dstore_1();
                        break block0;
                    }
                    case 2: {
                        this.dstore_2();
                        break block0;
                    }
                    case 3: {
                        this.dstore_3();
                        break block0;
                    }
                }
                this.dstore(localPosition);
                break;
            }
            case 7: {
                if (valueRequired) {
                    this.dup2();
                }
                switch (localPosition) {
                    case 0: {
                        this.lstore_0();
                        break block0;
                    }
                    case 1: {
                        this.lstore_1();
                        break block0;
                    }
                    case 2: {
                        this.lstore_2();
                        break block0;
                    }
                    case 3: {
                        this.lstore_3();
                        break block0;
                    }
                }
                this.lstore(localPosition);
                break;
            }
            default: {
                if (valueRequired) {
                    this.dup();
                }
                switch (localPosition) {
                    case 0: {
                        this.astore_0();
                        break block0;
                    }
                    case 1: {
                        this.astore_1();
                        break block0;
                    }
                    case 2: {
                        this.astore_2();
                        break block0;
                    }
                    case 3: {
                        this.astore_3();
                        break block0;
                    }
                }
                this.astore(localPosition);
            }
        }
    }

    public void swap() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 95;
    }

    public void tableswitch(CaseLabel defaultLabel, int low, int high, int[] keys, int[] sortedIndexes, int[] mapping, CaseLabel[] casesLabel) {
        this.countLabels = 0;
        --this.stackDepth;
        this.popTypeBinding();
        int length = casesLabel.length;
        int pos = this.position;
        defaultLabel.placeInstruction();
        int i = 0;
        while (i < length) {
            casesLabel[i].placeInstruction();
            ++i;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -86;
        i = 3 - (pos & 3);
        while (i > 0) {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 0;
            --i;
        }
        defaultLabel.branch();
        this.writeSignedWord(low);
        this.writeSignedWord(high);
        i = low;
        int j = low;
        while (true) {
            int index;
            int key;
            if ((key = keys[index = sortedIndexes[j - low]]) == i) {
                casesLabel[mapping[index]].branch();
                ++j;
                if (i == high) {
                    break;
                }
            } else {
                defaultLabel.branch();
            }
            ++i;
        }
    }

    public void throwAnyException(LocalVariableBinding anyExceptionVariable) {
        this.load(anyExceptionVariable);
        this.athrow();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("( position:");
        buffer.append(this.position);
        buffer.append(",\nstackDepth:");
        buffer.append(this.stackDepth);
        buffer.append(",\nmaxStack:");
        buffer.append(this.stackMax);
        buffer.append(",\nmaxLocals:");
        buffer.append(this.maxLocals);
        buffer.append(")");
        return buffer.toString();
    }

    protected void writePosition(BranchLabel label) {
        int offset = label.position - this.position + 1;
        if (Math.abs(offset) > Short.MAX_VALUE && !this.wideMode) {
            throw new AbortMethod(RESTART_IN_WIDE_MODE, null);
        }
        this.writeSignedShort(offset);
        int[] forwardRefs = label.forwardReferences();
        int i = 0;
        int max = label.forwardReferenceCount();
        while (i < max) {
            this.writePosition(label, forwardRefs[i]);
            ++i;
        }
    }

    protected void writePosition(BranchLabel label, int forwardReference) {
        int offset = label.position - forwardReference + 1;
        if (Math.abs(offset) > Short.MAX_VALUE && !this.wideMode) {
            throw new AbortMethod(RESTART_IN_WIDE_MODE, null);
        }
        if (this.wideMode) {
            if ((label.tagBits & 1) != 0) {
                this.writeSignedWord(forwardReference, offset);
            } else {
                this.writeSignedShort(forwardReference, offset);
            }
        } else {
            this.writeSignedShort(forwardReference, offset);
        }
    }

    private final void writeSignedShort(int value) {
        if (this.classFileOffset + 1 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = (byte)(value >> 8);
        this.bCodeStream[this.classFileOffset++] = (byte)value;
    }

    private final void writeSignedShort(int pos, int value) {
        int currentOffset = this.startingClassFileOffset + pos;
        if (currentOffset + 1 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.bCodeStream[currentOffset] = (byte)(value >> 8);
        this.bCodeStream[currentOffset + 1] = (byte)value;
    }

    protected final void writeSignedWord(int value) {
        if (this.classFileOffset + 3 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 4;
        this.bCodeStream[this.classFileOffset++] = (byte)((value & 0xFF000000) >> 24);
        this.bCodeStream[this.classFileOffset++] = (byte)((value & 0xFF0000) >> 16);
        this.bCodeStream[this.classFileOffset++] = (byte)((value & 0xFF00) >> 8);
        this.bCodeStream[this.classFileOffset++] = (byte)(value & 0xFF);
    }

    protected void writeSignedWord(int pos, int value) {
        int currentOffset = this.startingClassFileOffset + pos;
        if (currentOffset + 3 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.bCodeStream[currentOffset++] = (byte)((value & 0xFF000000) >> 24);
        this.bCodeStream[currentOffset++] = (byte)((value & 0xFF0000) >> 16);
        this.bCodeStream[currentOffset++] = (byte)((value & 0xFF00) >> 8);
        this.bCodeStream[currentOffset++] = (byte)(value & 0xFF);
    }

    private final void writeUnsignedShort(int value) {
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = (byte)(value >>> 8);
        this.bCodeStream[this.classFileOffset++] = (byte)value;
    }

    protected void writeWidePosition(BranchLabel label) {
        int labelPos = label.position;
        int offset = labelPos - this.position + 1;
        this.writeSignedWord(offset);
        int[] forwardRefs = label.forwardReferences();
        int i = 0;
        int max = label.forwardReferenceCount();
        while (i < max) {
            int forward = forwardRefs[i];
            offset = labelPos - forward + 1;
            this.writeSignedWord(forward, offset);
            ++i;
        }
    }

    private boolean isSwitchStackTrackingActive() {
        return this.methodDeclaration != null && this.methodDeclaration.containsSwitchWithTry;
    }

    private TypeBinding retrieveLocalType(int currentPC, int resolvedPosition) {
        int i = this.allLocalsCounter - 1;
        while (i >= 0) {
            LocalVariableBinding localVariable = this.locals[i];
            if (localVariable != null && resolvedPosition == localVariable.resolvedPosition) {
                int j = 0;
                while (j < localVariable.initializationCount) {
                    int startPC = localVariable.initializationPCs[j << 1];
                    int endPC = localVariable.initializationPCs[(j << 1) + 1];
                    if (currentPC >= startPC) {
                        if (endPC == -1) {
                            return localVariable.type;
                        }
                        if (currentPC < endPC) {
                            return localVariable.type;
                        }
                    }
                    ++j;
                }
            }
            --i;
        }
        return null;
    }

    private void pushTypeBinding(int resolvedPosition) {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        assert (resolvedPosition < this.maxLocals);
        TypeBinding type = this.retrieveLocalType(this.position, resolvedPosition);
        if (type == null && resolvedPosition == 0 && !this.methodDeclaration.isStatic()) {
            type = this.methodDeclaration.binding.declaringClass;
        }
        assert (type != null);
        this.pushTypeBinding(type);
    }

    private void pushTypeBindingArray() {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        assert (this.switchSaveTypeBindings.size() >= 2);
        TypeBinding[] arrayref_t = new TypeBinding[]{this.popTypeBinding(), this.popTypeBinding()};
        TypeBinding type = arrayref_t[1];
        assert (type instanceof ArrayBinding);
        this.pushTypeBinding(((ArrayBinding)type).leafComponentType);
    }

    private TypeBinding getPopularBinding(char[] typeName) {
        ClassScope scope = this.classFile.referenceBinding.scope;
        assert (scope != null);
        Supplier<ReferenceBinding> finder = scope.getCommonReferenceBinding(typeName);
        return finder != null ? (TypeBinding)finder.get() : TypeBinding.NULL;
    }

    private void pushTypeBinding(char[] typeName) {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        this.pushTypeBinding(this.getPopularBinding(typeName));
    }

    private void pushTypeBinding(TypeBinding typeBinding) {
        if (this.isSwitchStackTrackingActive()) {
            assert (typeBinding != null);
            this.switchSaveTypeBindings.push(typeBinding);
        }
    }

    private void pushTypeBinding(int nPop, TypeBinding typeBinding) {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        this.popTypeBinding(nPop);
        this.pushTypeBinding(typeBinding);
    }

    private TypeBinding popTypeBinding() {
        return this.isSwitchStackTrackingActive() ? this.switchSaveTypeBindings.pop() : null;
    }

    private void popTypeBinding(int nPop) {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        int i = 0;
        while (i < nPop) {
            this.popTypeBinding();
            ++i;
        }
    }

    public void clearTypeBindingStack() {
        if (!this.isSwitchStackTrackingActive()) {
            return;
        }
        this.switchSaveTypeBindings.clear();
    }
}


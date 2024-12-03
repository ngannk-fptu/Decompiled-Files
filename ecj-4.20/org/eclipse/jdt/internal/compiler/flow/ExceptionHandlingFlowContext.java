/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.codegen.ObjectCache;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CatchParameterBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ExceptionHandlingFlowContext
extends FlowContext {
    public static final int BitCacheSize = 32;
    public ReferenceBinding[] handledExceptions;
    int[] isReached;
    int[] isNeeded;
    UnconditionalFlowInfo[] initsOnExceptions;
    ObjectCache indexes = new ObjectCache();
    boolean isMethodContext;
    public UnconditionalFlowInfo initsOnReturn;
    public FlowContext initializationParent;
    public List extendedExceptions;
    private static final Argument[] NO_ARGUMENTS = new Argument[0];
    public Argument[] catchArguments;
    private int[] exceptionToCatchBlockMap;

    public ExceptionHandlingFlowContext(FlowContext parent, ASTNode associatedNode, ReferenceBinding[] handledExceptions, FlowContext initializationParent, BlockScope scope, UnconditionalFlowInfo flowInfo) {
        this(parent, associatedNode, handledExceptions, null, NO_ARGUMENTS, initializationParent, scope, flowInfo);
    }

    public ExceptionHandlingFlowContext(FlowContext parent, TryStatement tryStatement, ReferenceBinding[] handledExceptions, int[] exceptionToCatchBlockMap, FlowContext initializationParent, BlockScope scope, FlowInfo flowInfo) {
        this(parent, tryStatement, handledExceptions, exceptionToCatchBlockMap, tryStatement.catchArguments, initializationParent, scope, flowInfo.unconditionalInits());
        UnconditionalFlowInfo unconditionalCopy = flowInfo.unconditionalCopy();
        unconditionalCopy.acceptAllIncomingNullness();
        unconditionalCopy.tagBits |= 0x40;
        this.initsOnFinally = unconditionalCopy;
    }

    ExceptionHandlingFlowContext(FlowContext parent, ASTNode associatedNode, ReferenceBinding[] handledExceptions, int[] exceptionToCatchBlockMap, Argument[] catchArguments, FlowContext initializationParent, BlockScope scope, UnconditionalFlowInfo flowInfo) {
        super(parent, associatedNode, true);
        this.isMethodContext = scope == scope.methodScope();
        this.handledExceptions = handledExceptions;
        this.catchArguments = catchArguments;
        this.exceptionToCatchBlockMap = exceptionToCatchBlockMap;
        int count = handledExceptions.length;
        int cacheSize = count / 32 + 1;
        this.isReached = new int[cacheSize];
        this.isNeeded = new int[cacheSize];
        this.initsOnExceptions = new UnconditionalFlowInfo[count];
        boolean markExceptionsAndThrowableAsReached = !this.isMethodContext || scope.compilerOptions().reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable;
        int i = 0;
        while (i < count) {
            ReferenceBinding handledException = handledExceptions[i];
            int catchBlock = this.exceptionToCatchBlockMap != null ? this.exceptionToCatchBlockMap[i] : i;
            this.indexes.put(handledException, i);
            if (handledException.isUncheckedException(true)) {
                if (markExceptionsAndThrowableAsReached || handledException.id != 21 && handledException.id != 25) {
                    int n = i / 32;
                    this.isReached[n] = this.isReached[n] | 1 << i % 32;
                }
                this.initsOnExceptions[catchBlock] = flowInfo.unconditionalCopy();
            } else {
                this.initsOnExceptions[catchBlock] = FlowInfo.DEAD_END;
            }
            ++i;
        }
        if (!this.isMethodContext) {
            System.arraycopy(this.isReached, 0, this.isNeeded, 0, cacheSize);
        }
        this.initsOnReturn = FlowInfo.DEAD_END;
        this.initializationParent = initializationParent;
    }

    public void complainIfUnusedExceptionHandlers(AbstractMethodDeclaration method) {
        int i;
        MethodScope scope = method.scope;
        if ((method.binding.modifiers & 0x30000000) != 0 && !scope.compilerOptions().reportUnusedDeclaredThrownExceptionWhenOverriding) {
            return;
        }
        TypeBinding[] docCommentReferences = null;
        int docCommentReferencesLength = 0;
        if (scope.compilerOptions().reportUnusedDeclaredThrownExceptionIncludeDocCommentReference && method.javadoc != null && method.javadoc.exceptionReferences != null && (docCommentReferencesLength = method.javadoc.exceptionReferences.length) > 0) {
            docCommentReferences = new TypeBinding[docCommentReferencesLength];
            i = 0;
            while (i < docCommentReferencesLength) {
                docCommentReferences[i] = method.javadoc.exceptionReferences[i].resolvedType;
                ++i;
            }
        }
        i = 0;
        int count = this.handledExceptions.length;
        while (i < count) {
            block7: {
                int index = this.indexes.get(this.handledExceptions[i]);
                if ((this.isReached[index / 32] & 1 << index % 32) == 0) {
                    int j = 0;
                    while (j < docCommentReferencesLength) {
                        if (!TypeBinding.equalsEquals(docCommentReferences[j], this.handledExceptions[i])) {
                            ++j;
                            continue;
                        }
                        break block7;
                    }
                    scope.problemReporter().unusedDeclaredThrownException(this.handledExceptions[index], method, method.thrownExceptions[index]);
                }
            }
            ++i;
        }
    }

    public void complainIfUnusedExceptionHandlers(BlockScope scope, TryStatement tryStatement) {
        int index = 0;
        int count = this.handledExceptions.length;
        while (index < count) {
            int cacheIndex = index / 32;
            int bitMask = 1 << index % 32;
            if ((this.isReached[cacheIndex] & bitMask) == 0) {
                scope.problemReporter().unreachableCatchBlock(this.handledExceptions[index], this.getExceptionType(index));
            } else if ((this.isNeeded[cacheIndex] & bitMask) == 0) {
                scope.problemReporter().hiddenCatchBlock(this.handledExceptions[index], this.getExceptionType(index));
            }
            ++index;
        }
    }

    private ASTNode getExceptionType(int index) {
        if (this.exceptionToCatchBlockMap == null) {
            return this.catchArguments[index].type;
        }
        int catchBlock = this.exceptionToCatchBlockMap[index];
        TypeReference node = this.catchArguments[catchBlock].type;
        if (node instanceof UnionTypeReference) {
            TypeReference[] typeRefs = ((UnionTypeReference)node).typeReferences;
            int i = 0;
            int len = typeRefs.length;
            while (i < len) {
                TypeReference typeRef = typeRefs[i];
                if (TypeBinding.equalsEquals(typeRef.resolvedType, this.handledExceptions[index])) {
                    return typeRef;
                }
                ++i;
            }
        }
        return node;
    }

    @Override
    public FlowContext getInitializationContext() {
        return this.initializationParent;
    }

    @Override
    public String individualToString() {
        StringBuffer buffer = new StringBuffer("Exception flow context");
        int length = this.handledExceptions.length;
        int i = 0;
        while (i < length) {
            int cacheIndex = i / 32;
            int bitMask = 1 << i % 32;
            buffer.append('[').append(this.handledExceptions[i].readableName());
            if ((this.isReached[cacheIndex] & bitMask) != 0) {
                if ((this.isNeeded[cacheIndex] & bitMask) == 0) {
                    buffer.append("-masked");
                } else {
                    buffer.append("-reached");
                }
            } else {
                buffer.append("-not reached");
            }
            int catchBlock = this.exceptionToCatchBlockMap != null ? this.exceptionToCatchBlockMap[i] : i;
            buffer.append('-').append(this.initsOnExceptions[catchBlock].toString()).append(']');
            ++i;
        }
        buffer.append("[initsOnReturn -").append(this.initsOnReturn.toString()).append(']');
        return buffer.toString();
    }

    public UnconditionalFlowInfo initsOnException(int index) {
        return this.initsOnExceptions[index];
    }

    @Override
    public UnconditionalFlowInfo initsOnReturn() {
        return this.initsOnReturn;
    }

    public void mergeUnhandledException(TypeBinding newException) {
        if (this.extendedExceptions == null) {
            this.extendedExceptions = new ArrayList<ReferenceBinding>(Arrays.asList(this.handledExceptions));
        }
        boolean isRedundant = false;
        int i = this.extendedExceptions.size() - 1;
        while (i >= 0) {
            switch (Scope.compareTypes(newException, (TypeBinding)this.extendedExceptions.get(i))) {
                case 1: {
                    this.extendedExceptions.remove(i);
                    break;
                }
                case -1: {
                    isRedundant = true;
                    break;
                }
            }
            --i;
        }
        if (!isRedundant) {
            this.extendedExceptions.add(newException);
        }
    }

    public void recordHandlingException(ReferenceBinding exceptionType, UnconditionalFlowInfo flowInfo, TypeBinding raisedException, TypeBinding caughtException, ASTNode invocationSite, boolean wasAlreadyDefinitelyCaught) {
        int catchBlock;
        int index = this.indexes.get(exceptionType);
        int cacheIndex = index / 32;
        int bitMask = 1 << index % 32;
        if (!wasAlreadyDefinitelyCaught) {
            int n = cacheIndex;
            this.isNeeded[n] = this.isNeeded[n] | bitMask;
        }
        int n = cacheIndex;
        this.isReached[n] = this.isReached[n] | bitMask;
        int n2 = catchBlock = this.exceptionToCatchBlockMap != null ? this.exceptionToCatchBlockMap[index] : index;
        if (caughtException != null && this.catchArguments != null && this.catchArguments.length > 0 && !wasAlreadyDefinitelyCaught) {
            CatchParameterBinding catchParameter = (CatchParameterBinding)this.catchArguments[catchBlock].binding;
            catchParameter.setPreciseType(caughtException);
        }
        this.initsOnExceptions[catchBlock] = (this.initsOnExceptions[catchBlock].tagBits & 3) == 0 ? this.initsOnExceptions[catchBlock].mergedWith(flowInfo) : flowInfo.unconditionalCopy();
    }

    @Override
    public void recordReturnFrom(UnconditionalFlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) == 0) {
            this.initsOnReturn = (this.initsOnReturn.tagBits & 1) == 0 ? this.initsOnReturn.mergedWith(flowInfo) : (UnconditionalFlowInfo)flowInfo.copy();
        }
    }

    @Override
    public SubRoutineStatement subroutine() {
        if (this.associatedNode instanceof SubRoutineStatement) {
            if (this.parent.subroutine() == this.associatedNode) {
                return null;
            }
            return (SubRoutineStatement)this.associatedNode;
        }
        return null;
    }
}


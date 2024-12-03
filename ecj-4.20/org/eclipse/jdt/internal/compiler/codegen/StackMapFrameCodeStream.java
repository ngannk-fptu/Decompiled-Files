/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;

public class StackMapFrameCodeStream
extends CodeStream {
    public int[] stateIndexes;
    public int stateIndexesCounter;
    private HashMap framePositions;
    public Set exceptionMarkers;
    public ArrayList stackDepthMarkers;
    public ArrayList stackMarkers;

    public StackMapFrameCodeStream(ClassFile givenClassFile) {
        super(givenClassFile);
        this.generateAttributes |= 0x10;
    }

    @Override
    public void addDefinitelyAssignedVariables(Scope scope, int initStateIndex) {
        int i = 0;
        while (i < this.visibleLocalsCount) {
            boolean isDefinitelyAssigned;
            LocalVariableBinding localBinding = this.visibleLocals[i];
            if (localBinding != null && (isDefinitelyAssigned = this.isDefinitelyAssigned(scope, initStateIndex, localBinding)) && (localBinding.initializationCount == 0 || localBinding.initializationPCs[(localBinding.initializationCount - 1 << 1) + 1] != -1)) {
                localBinding.recordInitializationStartPC(this.position);
            }
            ++i;
        }
    }

    public void addExceptionMarker(int pc, TypeBinding typeBinding) {
        if (this.exceptionMarkers == null) {
            this.exceptionMarkers = new HashSet();
        }
        this.exceptionMarkers.add(new ExceptionMarker(pc, typeBinding));
    }

    public void addFramePosition(int pc) {
        Integer newEntry = pc;
        FramePosition value = (FramePosition)this.framePositions.get(newEntry);
        if (value != null) {
            ++value.counter;
        } else {
            this.framePositions.put(newEntry, new FramePosition());
        }
    }

    @Override
    public void optimizeBranch(int oldPosition, BranchLabel lbl) {
        super.optimizeBranch(oldPosition, lbl);
        this.removeFramePosition(oldPosition);
    }

    public void removeFramePosition(int pc) {
        Integer entry = pc;
        FramePosition value = (FramePosition)this.framePositions.get(entry);
        if (value != null) {
            --value.counter;
            if (value.counter <= 0) {
                this.framePositions.remove(entry);
            }
        }
    }

    @Override
    public void addVariable(LocalVariableBinding localBinding) {
        if (localBinding.initializationPCs == null) {
            this.record(localBinding);
        }
        localBinding.recordInitializationStartPC(this.position);
    }

    @Override
    public void recordExpressionType(TypeBinding typeBinding, int delta, boolean adjustStackDepth) {
        if (adjustStackDepth) {
            switch (typeBinding.id) {
                case 7: 
                case 8: {
                    this.stackDepth += 2;
                    break;
                }
                case 6: {
                    break;
                }
                default: {
                    ++this.stackDepth;
                }
            }
        }
    }

    @Override
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
        }
    }

    @Override
    public void generateOuterAccess(Object[] mappingSequence, ASTNode invocationSite, Binding target, Scope scope) {
        int currentPosition = this.position;
        super.generateOuterAccess(mappingSequence, invocationSite, target, scope);
        if (currentPosition == this.position) {
            throw new AbortMethod(scope.referenceCompilationUnit().compilationResult, null);
        }
    }

    public ExceptionMarker[] getExceptionMarkers() {
        Set exceptionMarkerSet = this.exceptionMarkers;
        if (this.exceptionMarkers == null) {
            return null;
        }
        int size = exceptionMarkerSet.size();
        Object[] markers = new ExceptionMarker[size];
        int n = 0;
        Iterator iterator = exceptionMarkerSet.iterator();
        while (iterator.hasNext()) {
            markers[n++] = (ExceptionMarker)iterator.next();
        }
        Arrays.sort(markers);
        return markers;
    }

    public int[] getFramePositions() {
        Set set = this.framePositions.keySet();
        int size = set.size();
        int[] positions = new int[size];
        int n = 0;
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            positions[n++] = (Integer)iterator.next();
        }
        Arrays.sort(positions);
        return positions;
    }

    public boolean hasFramePositions() {
        return this.framePositions.size() != 0;
    }

    @Override
    public void init(ClassFile targetClassFile) {
        super.init(targetClassFile);
        this.stateIndexesCounter = 0;
        if (this.framePositions != null) {
            this.framePositions.clear();
        }
        if (this.exceptionMarkers != null) {
            this.exceptionMarkers.clear();
        }
        if (this.stackDepthMarkers != null) {
            this.stackDepthMarkers.clear();
        }
        if (this.stackMarkers != null) {
            this.stackMarkers.clear();
        }
    }

    @Override
    public void initializeMaxLocals(MethodBinding methodBinding) {
        super.initializeMaxLocals(methodBinding);
        if (this.framePositions == null) {
            this.framePositions = new HashMap();
        } else {
            this.framePositions.clear();
        }
    }

    public void popStateIndex() {
        --this.stateIndexesCounter;
    }

    public void pushStateIndex(int naturalExitMergeInitStateIndex) {
        int length;
        if (this.stateIndexes == null) {
            this.stateIndexes = new int[3];
        }
        if ((length = this.stateIndexes.length) == this.stateIndexesCounter) {
            this.stateIndexes = new int[length * 2];
            System.arraycopy(this.stateIndexes, 0, this.stateIndexes, 0, length);
        }
        this.stateIndexes[this.stateIndexesCounter++] = naturalExitMergeInitStateIndex;
    }

    @Override
    public void removeNotDefinitelyAssignedVariables(Scope scope, int initStateIndex) {
        int index = this.visibleLocalsCount;
        int i = 0;
        while (i < index) {
            boolean isDefinitelyAssigned;
            LocalVariableBinding localBinding = this.visibleLocals[i];
            if (localBinding != null && localBinding.initializationCount > 0 && !(isDefinitelyAssigned = this.isDefinitelyAssigned(scope, initStateIndex, localBinding))) {
                if (this.stateIndexes != null) {
                    int j = 0;
                    int max = this.stateIndexesCounter;
                    while (j < max) {
                        if (!this.isDefinitelyAssigned(scope, this.stateIndexes[j], localBinding)) {
                            ++j;
                            continue;
                        }
                        break;
                    }
                } else {
                    localBinding.recordInitializationEndPC(this.position);
                }
            }
            ++i;
        }
    }

    @Override
    public void reset(ClassFile givenClassFile) {
        super.reset(givenClassFile);
        this.stateIndexesCounter = 0;
        if (this.framePositions != null) {
            this.framePositions.clear();
        }
        if (this.exceptionMarkers != null) {
            this.exceptionMarkers.clear();
        }
        if (this.stackDepthMarkers != null) {
            this.stackDepthMarkers.clear();
        }
        if (this.stackMarkers != null) {
            this.stackMarkers.clear();
        }
    }

    @Override
    protected void writePosition(BranchLabel label) {
        super.writePosition(label);
        this.addFramePosition(label.position);
    }

    @Override
    protected void writePosition(BranchLabel label, int forwardReference) {
        super.writePosition(label, forwardReference);
        this.addFramePosition(label.position);
    }

    @Override
    protected void writeSignedWord(int pos, int value) {
        super.writeSignedWord(pos, value);
        this.addFramePosition(this.position);
    }

    @Override
    protected void writeWidePosition(BranchLabel label) {
        super.writeWidePosition(label);
        this.addFramePosition(label.position);
    }

    @Override
    public void areturn() {
        super.areturn();
        this.addFramePosition(this.position);
    }

    @Override
    public void ireturn() {
        super.ireturn();
        this.addFramePosition(this.position);
    }

    @Override
    public void lreturn() {
        super.lreturn();
        this.addFramePosition(this.position);
    }

    @Override
    public void freturn() {
        super.freturn();
        this.addFramePosition(this.position);
    }

    @Override
    public void dreturn() {
        super.dreturn();
        this.addFramePosition(this.position);
    }

    @Override
    public void return_() {
        super.return_();
        this.addFramePosition(this.position);
    }

    @Override
    public void athrow() {
        super.athrow();
        this.addFramePosition(this.position);
    }

    @Override
    public void pushExceptionOnStack(TypeBinding binding) {
        super.pushExceptionOnStack(binding);
        this.addExceptionMarker(this.position, binding);
    }

    @Override
    public void goto_(BranchLabel label) {
        super.goto_(label);
        this.addFramePosition(this.position);
    }

    @Override
    public void goto_w(BranchLabel label) {
        super.goto_w(label);
        this.addFramePosition(this.position);
    }

    @Override
    public void resetInWideMode() {
        this.resetSecretLocals();
        super.resetInWideMode();
    }

    @Override
    public void resetForCodeGenUnusedLocals() {
        this.resetSecretLocals();
        super.resetForCodeGenUnusedLocals();
    }

    public void resetSecretLocals() {
        int i = 0;
        int max = this.locals.length;
        while (i < max) {
            LocalVariableBinding localVariableBinding = this.locals[i];
            if (localVariableBinding != null && localVariableBinding.isSecret()) {
                localVariableBinding.resetInitializations();
            }
            ++i;
        }
    }

    public static class ExceptionMarker
    implements Comparable {
        private TypeBinding binding;
        public int pc;

        public ExceptionMarker(int pc, TypeBinding typeBinding) {
            this.pc = pc;
            this.binding = typeBinding;
        }

        public int compareTo(Object o) {
            if (o instanceof ExceptionMarker) {
                return this.pc - ((ExceptionMarker)o).pc;
            }
            return 0;
        }

        public boolean equals(Object obj) {
            if (obj instanceof ExceptionMarker) {
                ExceptionMarker marker = (ExceptionMarker)obj;
                return this.pc == marker.pc && this.binding.equals(marker.binding);
            }
            return false;
        }

        public TypeBinding getBinding() {
            return this.binding;
        }

        public int hashCode() {
            return this.pc + CharOperation.hashCode(this.binding.constantPoolName());
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append('(').append(this.pc).append(',').append(this.binding.constantPoolName()).append(')');
            return String.valueOf(buffer);
        }
    }

    static class FramePosition {
        int counter;

        FramePosition() {
        }
    }
}


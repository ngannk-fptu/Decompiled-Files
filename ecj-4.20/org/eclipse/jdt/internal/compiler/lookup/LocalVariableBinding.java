/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

public class LocalVariableBinding
extends VariableBinding {
    public int resolvedPosition;
    public static final int UNUSED = 0;
    public static final int USED = 1;
    public static final int FAKE_USED = 2;
    public int useFlag;
    public BlockScope declaringScope;
    public LocalDeclaration declaration;
    public int[] initializationPCs;
    public int initializationCount = 0;
    public FakedTrackingVariable closeTracker;
    public Set<MethodScope> uninitializedInMethod;

    public LocalVariableBinding(char[] name, TypeBinding type, int modifiers, boolean isArgument) {
        super(name, type, modifiers, isArgument ? Constant.NotAConstant : null);
        if (isArgument) {
            this.tagBits |= 0x400L;
        }
        this.tagBits |= 0x800L;
    }

    public LocalVariableBinding(LocalDeclaration declaration, TypeBinding type, int modifiers, boolean isArgument) {
        this(declaration.name, type, modifiers, isArgument);
        this.declaration = declaration;
    }

    public LocalVariableBinding(LocalDeclaration declaration, TypeBinding type, int modifiers, MethodScope declaringScope) {
        this(declaration, type, modifiers, true);
        this.declaringScope = declaringScope;
    }

    @Override
    public final int kind() {
        return 2;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        boolean addParameterRank;
        int i;
        StringBuffer buffer = new StringBuffer();
        BlockScope scope = this.declaringScope;
        int occurenceCount = 0;
        if (scope != null) {
            MethodBinding methodBinding;
            MethodScope methodScope = scope instanceof MethodScope ? (MethodScope)scope : scope.enclosingMethodScope();
            ReferenceContext referenceContext = methodScope.referenceContext;
            if (referenceContext instanceof AbstractMethodDeclaration) {
                methodBinding = ((AbstractMethodDeclaration)referenceContext).binding;
                if (methodBinding != null) {
                    buffer.append(methodBinding.computeUniqueKey(false));
                }
            } else if (referenceContext instanceof TypeDeclaration) {
                SourceTypeBinding typeBinding = ((TypeDeclaration)referenceContext).binding;
                if (typeBinding != null) {
                    buffer.append(((Binding)typeBinding).computeUniqueKey(false));
                }
            } else if (referenceContext instanceof LambdaExpression && (methodBinding = ((LambdaExpression)referenceContext).binding) != null) {
                buffer.append(methodBinding.computeUniqueKey(false));
            }
            this.getScopeKey(scope, buffer);
            LocalVariableBinding[] locals = scope.locals;
            i = 0;
            while (i < scope.localIndex) {
                LocalVariableBinding local = locals[i];
                if (CharOperation.equals(this.name, local.name)) {
                    if (this == local) break;
                    ++occurenceCount;
                }
                ++i;
            }
        }
        buffer.append('#');
        buffer.append(this.name);
        boolean bl = addParameterRank = this.isParameter() && this.declaringScope != null;
        if (occurenceCount > 0 || addParameterRank) {
            buffer.append('#');
            buffer.append(occurenceCount);
            if (addParameterRank) {
                int pos = -1;
                LocalVariableBinding[] params = this.declaringScope.locals;
                i = 0;
                while (i < params.length) {
                    if (params[i] == this) {
                        pos = i;
                        break;
                    }
                    ++i;
                }
                if (pos > -1) {
                    buffer.append('#');
                    buffer.append(pos);
                }
            }
        }
        int length = buffer.length();
        char[] uniqueKey = new char[length];
        buffer.getChars(0, length, uniqueKey, 0);
        return uniqueKey;
    }

    @Override
    public AnnotationBinding[] getAnnotations() {
        Annotation[] annotationNodes;
        if (this.declaringScope == null) {
            if ((this.tagBits & 0x200000000L) != 0L) {
                if (this.declaration == null) {
                    return Binding.NO_ANNOTATIONS;
                }
                Annotation[] annotations = this.declaration.annotations;
                if (annotations != null) {
                    int length = annotations.length;
                    AnnotationBinding[] annotationBindings = new AnnotationBinding[length];
                    int i = 0;
                    while (i < length) {
                        AnnotationBinding compilerAnnotation = annotations[i].getCompilerAnnotation();
                        if (compilerAnnotation == null) {
                            return Binding.NO_ANNOTATIONS;
                        }
                        annotationBindings[i] = compilerAnnotation;
                        ++i;
                    }
                    return annotationBindings;
                }
            }
            return Binding.NO_ANNOTATIONS;
        }
        SourceTypeBinding sourceType = this.declaringScope.enclosingSourceType();
        if (sourceType == null) {
            return Binding.NO_ANNOTATIONS;
        }
        if ((this.tagBits & 0x200000000L) == 0L && (this.tagBits & 0x400L) != 0L && this.declaration != null && (annotationNodes = this.declaration.annotations) != null) {
            ASTNode.resolveAnnotations(this.declaringScope, annotationNodes, this, true);
        }
        return sourceType.retrieveAnnotations(this);
    }

    private void getScopeKey(BlockScope scope, StringBuffer buffer) {
        int scopeIndex = scope.scopeIndex();
        if (scopeIndex != -1) {
            this.getScopeKey((BlockScope)scope.parent, buffer);
            buffer.append('#');
            buffer.append(scopeIndex);
        }
    }

    public boolean isSecret() {
        return this.declaration == null && (this.tagBits & 0x400L) == 0L;
    }

    public void recordInitializationEndPC(int pc) {
        if (this.initializationPCs[(this.initializationCount - 1 << 1) + 1] == -1) {
            this.initializationPCs[(this.initializationCount - 1 << 1) + 1] = pc;
        }
    }

    public void recordInitializationStartPC(int pc) {
        int index;
        if (this.initializationPCs == null) {
            return;
        }
        if (this.initializationCount > 0) {
            int previousEndPC = this.initializationPCs[(this.initializationCount - 1 << 1) + 1];
            if (previousEndPC == -1) {
                return;
            }
            if (previousEndPC == pc) {
                this.initializationPCs[(this.initializationCount - 1 << 1) + 1] = -1;
                return;
            }
        }
        if ((index = this.initializationCount << 1) == this.initializationPCs.length) {
            this.initializationPCs = new int[this.initializationCount << 2];
            System.arraycopy(this.initializationPCs, 0, this.initializationPCs, 0, index);
        }
        this.initializationPCs[index] = pc;
        this.initializationPCs[index + 1] = -1;
        ++this.initializationCount;
    }

    @Override
    public void setAnnotations(AnnotationBinding[] annotations, Scope scope, boolean forceStore) {
        if (scope == null) {
            return;
        }
        SourceTypeBinding sourceType = scope.enclosingSourceType();
        if (sourceType != null) {
            sourceType.storeAnnotations(this, annotations, forceStore);
        }
    }

    public void resetInitializations() {
        this.initializationCount = 0;
        this.initializationPCs = null;
    }

    @Override
    public String toString() {
        String s = super.toString();
        switch (this.useFlag) {
            case 1: {
                s = String.valueOf(s) + "[pos: " + String.valueOf(this.resolvedPosition) + "]";
                break;
            }
            case 0: {
                s = String.valueOf(s) + "[pos: unused]";
                break;
            }
            case 2: {
                s = String.valueOf(s) + "[pos: fake_used]";
            }
        }
        s = String.valueOf(s) + "[id:" + String.valueOf(this.id) + "]";
        if (this.initializationCount > 0) {
            s = String.valueOf(s) + "[pc: ";
            int i = 0;
            while (i < this.initializationCount) {
                if (i > 0) {
                    s = String.valueOf(s) + ", ";
                }
                s = String.valueOf(s) + String.valueOf(this.initializationPCs[i << 1]) + "-" + (this.initializationPCs[(i << 1) + 1] == -1 ? "?" : String.valueOf(this.initializationPCs[(i << 1) + 1]));
                ++i;
            }
            s = String.valueOf(s) + "]";
        }
        return s;
    }

    @Override
    public boolean isParameter() {
        return (this.tagBits & 0x400L) != 0L;
    }

    public boolean isCatchParameter() {
        return false;
    }

    public boolean isPatternVariable() {
        return (this.modifiers & 0x10000000) != 0;
    }

    public MethodBinding getEnclosingMethod() {
        BlockScope blockScope = this.declaringScope;
        if (blockScope != null) {
            ReferenceContext referenceContext = blockScope.referenceContext();
            if (referenceContext instanceof Initializer) {
                return null;
            }
            if (referenceContext instanceof AbstractMethodDeclaration) {
                return ((AbstractMethodDeclaration)referenceContext).binding;
            }
        }
        return null;
    }

    public void markInitialized() {
    }

    public void markReferenced() {
    }

    public boolean isUninitializedIn(Scope scope) {
        if (this.uninitializedInMethod != null) {
            return this.uninitializedInMethod.contains(scope.methodScope());
        }
        return false;
    }

    public void markAsUninitializedIn(Scope scope) {
        if (this.uninitializedInMethod == null) {
            this.uninitializedInMethod = new HashSet<MethodScope>();
        }
        this.uninitializedInMethod.add(scope.methodScope());
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class ProvidesStatement
extends ModuleStatement {
    public TypeReference serviceInterface;
    public TypeReference[] implementations;

    public boolean resolve(BlockScope scope) {
        ModuleDeclaration module = scope.referenceCompilationUnit().moduleDeclaration;
        SourceModuleBinding src = module.binding;
        TypeBinding infBinding = this.serviceInterface.resolveType(scope);
        boolean hasErrors = false;
        if (infBinding == null || !infBinding.isValidBinding()) {
            return false;
        }
        if (!(infBinding.isClass() || infBinding.isInterface() || infBinding.isAnnotationType())) {
            scope.problemReporter().invalidServiceRef(8389924, this.serviceInterface);
        }
        ReferenceBinding intf = (ReferenceBinding)this.serviceInterface.resolvedType;
        HashSet<ReferenceBinding> impls = new HashSet<ReferenceBinding>();
        int i = 0;
        while (i < this.implementations.length) {
            ReferenceBinding impl = (ReferenceBinding)this.implementations[i].resolveType(scope);
            if (impl == null || !impl.isValidBinding() || !impl.canBeSeenBy(scope)) {
                hasErrors = true;
            } else if (!impls.add(impl)) {
                scope.problemReporter().duplicateTypeReference(8389912, this.implementations[i]);
            } else {
                int problemId = 0;
                ModuleBinding declaringModule = impl.module();
                if (declaringModule != src) {
                    problemId = 16778526;
                } else if (!impl.isClass() && !impl.isInterface()) {
                    problemId = 8389925;
                } else if (impl.isNestedType() && !impl.isStatic()) {
                    problemId = 16778525;
                } else {
                    MethodBinding provider = impl.getExactMethod(TypeConstants.PROVIDER, Binding.NO_PARAMETERS, scope.compilationUnitScope());
                    if (!(provider == null || provider.isValidBinding() && provider.isPublic() && provider.isStatic())) {
                        provider = null;
                    }
                    TypeBinding implType = impl;
                    if (provider != null) {
                        implType = provider.returnType;
                        if (implType instanceof ReferenceBinding && !implType.canBeSeenBy(scope)) {
                            ReferenceBinding referenceBinding = (ReferenceBinding)implType;
                            scope.problemReporter().invalidType(this.implementations[i], new ProblemReferenceBinding(referenceBinding.compoundName, referenceBinding, 2));
                            hasErrors = true;
                        }
                    } else if (impl.isAbstract()) {
                        problemId = 16778522;
                    } else {
                        MethodBinding defaultConstructor = impl.getExactConstructor(Binding.NO_PARAMETERS);
                        if (defaultConstructor == null || !defaultConstructor.isValidBinding()) {
                            problemId = 16778523;
                        } else if (!defaultConstructor.isPublic()) {
                            problemId = 16778524;
                        }
                    }
                    if (implType.findSuperTypeOriginatingFrom(intf) == null) {
                        scope.problemReporter().typeMismatchError(implType, intf, this.implementations[i], null);
                        hasErrors = true;
                    }
                }
                if (problemId != 0) {
                    scope.problemReporter().invalidServiceRef(problemId, this.implementations[i]);
                    hasErrors = true;
                }
            }
            ++i;
        }
        return hasErrors;
    }

    public List<TypeBinding> getResolvedImplementations() {
        ArrayList<TypeBinding> resolved = new ArrayList<TypeBinding>();
        if (this.implementations != null) {
            TypeReference[] typeReferenceArray = this.implementations;
            int n = this.implementations.length;
            int n2 = 0;
            while (n2 < n) {
                TypeReference implRef = typeReferenceArray[n2];
                TypeBinding one = implRef.resolvedType;
                if (one != null) {
                    resolved.add(one);
                }
                ++n2;
            }
        }
        return resolved;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        ProvidesStatement.printIndent(indent, output);
        output.append("provides ");
        this.serviceInterface.print(0, output);
        output.append(" with ");
        int i = 0;
        while (i < this.implementations.length) {
            this.implementations[i].print(0, output);
            if (i < this.implementations.length - 1) {
                output.append(", ");
            }
            ++i;
        }
        output.append(";");
        return output;
    }
}


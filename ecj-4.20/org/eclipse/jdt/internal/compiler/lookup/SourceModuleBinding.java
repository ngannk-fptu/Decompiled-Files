/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public class SourceModuleBinding
extends ModuleBinding {
    public final CompilationUnitScope scope;

    public SourceModuleBinding(char[] moduleName, CompilationUnitScope scope, LookupEnvironment rootEnv) {
        super(moduleName);
        rootEnv.knownModules.put(moduleName, this);
        this.environment = new LookupEnvironment(rootEnv, this);
        this.scope = scope;
        scope.environment = this.environment;
    }

    public void setRequires(ModuleBinding[] requires, ModuleBinding[] requiresTransitive) {
        ModuleBinding javaBase = this.environment.javaBaseModule();
        this.requires = this.merge(this.requires, requires, javaBase, ModuleBinding[]::new);
        this.requiresTransitive = this.merge(this.requiresTransitive, requiresTransitive, null, ModuleBinding[]::new);
    }

    public void setUses(TypeBinding[] uses) {
        this.uses = this.merge(this.uses, uses, null, TypeBinding[]::new);
    }

    @Override
    public TypeBinding[] getUses() {
        this.resolveTypes();
        return super.getUses();
    }

    @Override
    public TypeBinding[] getServices() {
        this.resolveTypes();
        return super.getServices();
    }

    @Override
    public TypeBinding[] getImplementations(TypeBinding binding) {
        this.resolveTypes();
        return super.getImplementations(binding);
    }

    private void resolveTypes() {
        ModuleDeclaration ast;
        if (this.scope != null && (ast = this.scope.referenceCompilationUnit().moduleDeclaration) != null) {
            ast.resolveTypeDirectives(this.scope);
        }
    }

    public void setServices(TypeBinding[] services) {
        this.services = this.merge(this.services, services, null, TypeBinding[]::new);
    }

    public void setImplementations(TypeBinding infBinding, Collection<TypeBinding> resolvedImplementations) {
        if (this.implementations == null) {
            this.implementations = new HashMap();
        }
        this.implementations.put(infBinding, resolvedImplementations.toArray(new TypeBinding[resolvedImplementations.size()]));
    }

    private <T> T[] merge(T[] one, T[] two, T extra, IntFunction<T[]> supplier) {
        if (one.length == 0 && extra == null) {
            if (two.length > 0) {
                return two;
            }
            return one;
        }
        int len0 = extra == null ? 0 : 1;
        int len1 = one.length;
        int len2 = two.length;
        T[] result = supplier.apply(len0 + len1 + len2);
        if (extra != null) {
            result[0] = extra;
        }
        System.arraycopy(one, 0, result, len0, len1);
        System.arraycopy(two, 0, result, len0 + len1, len2);
        return result;
    }

    @Override
    Stream<ModuleBinding> getRequiredModules(boolean transitiveOnly) {
        this.scope.referenceContext.moduleDeclaration.resolveModuleDirectives(this.scope);
        return super.getRequiredModules(transitiveOnly);
    }

    @Override
    public ModuleBinding[] getAllRequiredModules() {
        this.scope.referenceContext.moduleDeclaration.resolveModuleDirectives(this.scope);
        return super.getAllRequiredModules();
    }

    @Override
    public PlainPackageBinding[] getExports() {
        this.scope.referenceContext.moduleDeclaration.resolvePackageDirectives(this.scope);
        return super.getExports();
    }

    @Override
    public PlainPackageBinding[] getOpens() {
        this.scope.referenceContext.moduleDeclaration.resolvePackageDirectives(this.scope);
        return super.getOpens();
    }

    @Override
    public long getAnnotationTagBits() {
        this.ensureAnnotationsResolved();
        return this.tagBits;
    }

    protected void ensureAnnotationsResolved() {
        if ((this.tagBits & 0x200000000L) == 0L && this.scope != null) {
            ModuleDeclaration module = this.scope.referenceContext.moduleDeclaration;
            ASTNode.resolveAnnotations((BlockScope)module.scope, module.annotations, this);
            if ((this.tagBits & 0x400000000000L) != 0L) {
                this.modifiers |= 0x100000;
                this.tagBits |= 0x400000000L;
            }
            this.tagBits |= 0x200000000L;
        }
    }

    @Override
    public AnnotationBinding[] getAnnotations() {
        this.ensureAnnotationsResolved();
        return this.retrieveAnnotations(this);
    }

    @Override
    SimpleLookupTable storedAnnotations(boolean forceInitialize, boolean forceStore) {
        if (this.scope != null) {
            SimpleLookupTable annotationTable = super.storedAnnotations(forceInitialize, forceStore);
            if (annotationTable != null) {
                this.scope.referenceCompilationUnit().compilationResult.hasAnnotations = true;
            }
            return annotationTable;
        }
        return null;
    }
}


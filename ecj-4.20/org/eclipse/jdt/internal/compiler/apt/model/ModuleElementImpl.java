/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.model.NameImpl;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ModuleElementImpl
extends ElementImpl
implements ModuleElement {
    ModuleBinding binding;
    private List<ModuleElement.Directive> directives;
    private static List<ModuleElement.Directive> EMPTY_DIRECTIVES = Collections.emptyList();

    ModuleElementImpl(BaseProcessingEnvImpl env, ModuleBinding binding) {
        super(env, binding);
        this.binding = binding;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.MODULE;
    }

    @Override
    public Set<Modifier> getModifiers() {
        int modifiers = this.binding.modifiers;
        return Factory.getModifiers(modifiers, this.getKind(), false);
    }

    @Override
    public Name getQualifiedName() {
        return new NameImpl(this.binding.moduleName);
    }

    @Override
    public Name getSimpleName() {
        char[] simpleName = this.binding.moduleName;
        int i = simpleName.length - 1;
        while (i >= 0) {
            if (simpleName[i] == '.') {
                simpleName = Arrays.copyOfRange(simpleName, i + 1, simpleName.length);
                break;
            }
            --i;
        }
        return new NameImpl(simpleName);
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        ModuleBinding module = this.binding;
        HashSet<PlainPackageBinding> unique = new HashSet<PlainPackageBinding>();
        for (PlainPackageBinding p : module.declaredPackages.values()) {
            if (!p.hasCompilationUnit(true)) continue;
            unique.add(p);
        }
        if (module.isUnnamed()) {
            PlainPackageBinding def = module.environment.defaultPackage;
            if (def != null && def.hasCompilationUnit(true)) {
                unique.add(def);
            }
        } else {
            PlainPackageBinding pBinding;
            PlainPackageBinding[] plainPackageBindingArray = this.binding.getExports();
            int n = plainPackageBindingArray.length;
            int n2 = 0;
            while (n2 < n) {
                pBinding = plainPackageBindingArray[n2];
                unique.add(pBinding);
                ++n2;
            }
            plainPackageBindingArray = this.binding.getOpens();
            n = plainPackageBindingArray.length;
            n2 = 0;
            while (n2 < n) {
                pBinding = plainPackageBindingArray[n2];
                unique.add(pBinding);
                ++n2;
            }
        }
        ArrayList<PackageElement> enclosed = new ArrayList<PackageElement>(unique.size());
        for (PlainPackageBinding p : unique) {
            PackageElement pElement = (PackageElement)this._env.getFactory().newElement(p);
            enclosed.add(pElement);
        }
        return Collections.unmodifiableList(enclosed);
    }

    @Override
    public boolean isOpen() {
        return (this.binding.modifiers & 0x20) != 0;
    }

    @Override
    public boolean isUnnamed() {
        return this.binding.moduleName.length == 0;
    }

    @Override
    public Element getEnclosingElement() {
        return null;
    }

    @Override
    public List<? extends ModuleElement.Directive> getDirectives() {
        ModuleBinding[] required;
        PlainPackageBinding[] packs;
        if (this.isUnnamed()) {
            return EMPTY_DIRECTIVES;
        }
        if (this.directives == null) {
            this.directives = new ArrayList<ModuleElement.Directive>();
        }
        PlainPackageBinding[] plainPackageBindingArray = packs = this.binding.getExports();
        int n = packs.length;
        int n2 = 0;
        while (n2 < n) {
            PlainPackageBinding exp = plainPackageBindingArray[n2];
            this.directives.add(new ExportsDirectiveImpl(exp));
            ++n2;
        }
        HashSet<ModuleBinding> transitive = new HashSet<ModuleBinding>();
        ModuleBinding[] moduleBindingArray = this.binding.getRequiresTransitive();
        int n3 = moduleBindingArray.length;
        n = 0;
        while (n < n3) {
            ModuleBinding mBinding = moduleBindingArray[n];
            transitive.add(mBinding);
            ++n;
        }
        ModuleBinding[] moduleBindingArray2 = required = this.binding.getRequires();
        int n4 = required.length;
        n3 = 0;
        while (n3 < n4) {
            ModuleBinding mBinding = moduleBindingArray2[n3];
            if (transitive.contains(mBinding)) {
                this.directives.add(new RequiresDirectiveImpl(mBinding, true));
            } else {
                this.directives.add(new RequiresDirectiveImpl(mBinding, false));
            }
            ++n3;
        }
        TypeBinding[] tBindings = this.binding.getUses();
        Binding[] bindingArray = tBindings;
        int n5 = tBindings.length;
        n4 = 0;
        while (n4 < n5) {
            TypeBinding tBinding = bindingArray[n4];
            this.directives.add(new UsesDirectiveImpl(tBinding));
            ++n4;
        }
        tBindings = this.binding.getServices();
        bindingArray = tBindings;
        n5 = tBindings.length;
        n4 = 0;
        while (n4 < n5) {
            TypeBinding tBinding = bindingArray[n4];
            this.directives.add(new ProvidesDirectiveImpl(tBinding));
            ++n4;
        }
        packs = this.binding.getOpens();
        bindingArray = packs;
        n5 = packs.length;
        n4 = 0;
        while (n4 < n5) {
            Binding exp = bindingArray[n4];
            this.directives.add(new OpensDirectiveImpl((PackageBinding)exp));
            ++n4;
        }
        return this.directives;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> visitor, P param) {
        return visitor.visitModule(this, param);
    }

    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((ModuleBinding)this._binding).getAnnotations();
    }

    class ExportsDirectiveImpl
    extends PackageDirectiveImpl
    implements ModuleElement.ExportsDirective {
        ExportsDirectiveImpl(PackageBinding pBinding) {
            super(pBinding);
        }

        @Override
        public <R, P> R accept(ModuleElement.DirectiveVisitor<R, P> visitor, P param) {
            return visitor.visitExports(this, param);
        }

        @Override
        public ModuleElement.DirectiveKind getKind() {
            return ModuleElement.DirectiveKind.EXPORTS;
        }

        @Override
        public PackageElement getPackage() {
            return ModuleElementImpl.this._env.getFactory().newPackageElement(this.binding);
        }

        @Override
        public List<? extends ModuleElement> getTargetModules() {
            if (this.targets != null) {
                return this.targets;
            }
            return this.getTargetModules(ModuleElementImpl.this.binding.getExportRestrictions(this.binding));
        }
    }

    class OpensDirectiveImpl
    extends PackageDirectiveImpl
    implements ModuleElement.OpensDirective {
        OpensDirectiveImpl(PackageBinding pBinding) {
            super(pBinding);
        }

        @Override
        public <R, P> R accept(ModuleElement.DirectiveVisitor<R, P> visitor, P param) {
            return visitor.visitOpens(this, param);
        }

        @Override
        public ModuleElement.DirectiveKind getKind() {
            return ModuleElement.DirectiveKind.OPENS;
        }

        @Override
        public List<? extends ModuleElement> getTargetModules() {
            if (this.targets != null) {
                return this.targets;
            }
            return this.getTargetModules(ModuleElementImpl.this.binding.getOpenRestrictions(this.binding));
        }
    }

    abstract class PackageDirectiveImpl {
        PackageBinding binding;
        List<ModuleElement> targets;

        PackageDirectiveImpl(PackageBinding pBinding) {
            this.binding = pBinding;
        }

        public PackageElement getPackage() {
            return ModuleElementImpl.this._env.getFactory().newPackageElement(this.binding);
        }

        public List<? extends ModuleElement> getTargetModules(String[] restrictions) {
            if (this.targets != null) {
                return this.targets;
            }
            if (restrictions.length == 0) {
                this.targets = null;
                return null;
            }
            ArrayList<ModuleElement> targets = new ArrayList<ModuleElement>(restrictions.length);
            String[] stringArray = restrictions;
            int n = restrictions.length;
            int n2 = 0;
            while (n2 < n) {
                String string = stringArray[n2];
                ModuleBinding target = ModuleElementImpl.this.binding.environment.getModule(string.toCharArray());
                if (target != null) {
                    ModuleElement element = (ModuleElement)ModuleElementImpl.this._env.getFactory().newElement(target);
                    targets.add(element);
                }
                ++n2;
            }
            this.targets = Collections.unmodifiableList(targets);
            return this.targets;
        }
    }

    class ProvidesDirectiveImpl
    implements ModuleElement.ProvidesDirective {
        TypeBinding service;
        public List<? extends TypeElement> implementations;

        ProvidesDirectiveImpl(TypeBinding service) {
            this.service = service;
        }

        @Override
        public <R, P> R accept(ModuleElement.DirectiveVisitor<R, P> visitor, P param) {
            return visitor.visitProvides(this, param);
        }

        @Override
        public ModuleElement.DirectiveKind getKind() {
            return ModuleElement.DirectiveKind.PROVIDES;
        }

        @Override
        public List<? extends TypeElement> getImplementations() {
            if (this.implementations != null) {
                return this.implementations;
            }
            TypeBinding[] implementations2 = ModuleElementImpl.this.binding.getImplementations(this.service);
            if (implementations2.length == 0) {
                this.implementations = Collections.emptyList();
                return this.implementations;
            }
            ArrayList<TypeElement> list = new ArrayList<TypeElement>(implementations2.length);
            Factory factory = ModuleElementImpl.this._env.getFactory();
            TypeBinding[] typeBindingArray = implementations2;
            int n = implementations2.length;
            int n2 = 0;
            while (n2 < n) {
                TypeBinding type = typeBindingArray[n2];
                TypeElement element = (TypeElement)factory.newElement(type);
                list.add(element);
                ++n2;
            }
            return Collections.unmodifiableList(list);
        }

        @Override
        public TypeElement getService() {
            return (TypeElement)ModuleElementImpl.this._env.getFactory().newElement(this.service);
        }
    }

    class RequiresDirectiveImpl
    implements ModuleElement.RequiresDirective {
        ModuleBinding dependency;
        boolean transitive;

        RequiresDirectiveImpl(ModuleBinding dependency, boolean transitive) {
            this.dependency = dependency;
            this.transitive = transitive;
        }

        @Override
        public <R, P> R accept(ModuleElement.DirectiveVisitor<R, P> visitor, P param) {
            return visitor.visitRequires(this, param);
        }

        @Override
        public ModuleElement.DirectiveKind getKind() {
            return ModuleElement.DirectiveKind.REQUIRES;
        }

        @Override
        public ModuleElement getDependency() {
            return (ModuleElement)ModuleElementImpl.this._env.getFactory().newElement(this.dependency, ElementKind.MODULE);
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public boolean isTransitive() {
            return this.transitive;
        }
    }

    class UsesDirectiveImpl
    implements ModuleElement.UsesDirective {
        TypeBinding binding = null;

        UsesDirectiveImpl(TypeBinding binding) {
            this.binding = binding;
        }

        @Override
        public <R, P> R accept(ModuleElement.DirectiveVisitor<R, P> visitor, P param) {
            return visitor.visitUses(this, param);
        }

        @Override
        public ModuleElement.DirectiveKind getKind() {
            return ModuleElement.DirectiveKind.USES;
        }

        @Override
        public TypeElement getService() {
            return (TypeElement)ModuleElementImpl.this._env.getFactory().newElement(this.binding);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ElementsImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ModuleElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.util.HashtableOfModule;

public class ElementsImpl9
extends ElementsImpl {
    public ElementsImpl9(BaseProcessingEnvImpl env) {
        super(env);
    }

    @Override
    public TypeElement getTypeElement(CharSequence name) {
        char[][] compoundName = CharOperation.splitOn('.', name.toString().toCharArray());
        Set<? extends ModuleElement> allModuleElements = this.getAllModuleElements();
        for (ModuleElement moduleElement : allModuleElements) {
            TypeElement t = this.getTypeElement(compoundName, ((ModuleElementImpl)moduleElement).binding);
            if (t == null) continue;
            return t;
        }
        return null;
    }

    @Override
    public TypeElement getTypeElement(ModuleElement module, CharSequence name) {
        ModuleBinding mBinding = ((ModuleElementImpl)module).binding;
        char[][] compoundName = CharOperation.splitOn('.', name.toString().toCharArray());
        return this.getTypeElement(compoundName, mBinding);
    }

    private TypeElement getTypeElement(char[][] compoundName, ModuleBinding mBinding) {
        ReferenceBinding binding;
        LookupEnvironment le = mBinding == null ? this._env.getLookupEnvironment() : mBinding.environment;
        ReferenceBinding referenceBinding = binding = mBinding == null ? le.getType(compoundName) : le.getType(compoundName, mBinding);
        if (binding == null) {
            ReferenceBinding topLevelBinding = null;
            int topLevelSegments = compoundName.length;
            while (--topLevelSegments > 0) {
                char[][] topLevelName = new char[topLevelSegments][];
                int i = 0;
                while (i < topLevelSegments) {
                    topLevelName[i] = compoundName[i];
                    ++i;
                }
                topLevelBinding = le.getType(topLevelName);
                if (topLevelBinding != null) break;
            }
            if (topLevelBinding == null) {
                return null;
            }
            binding = topLevelBinding;
            int i = topLevelSegments;
            while (binding != null && i < compoundName.length) {
                binding = binding.getMemberType(compoundName[i]);
                ++i;
            }
        }
        if (binding == null) {
            return null;
        }
        if ((binding.tagBits & 0x80L) != 0L) {
            return null;
        }
        return new TypeElementImpl(this._env, binding, null);
    }

    @Override
    public Elements.Origin getOrigin(Element e) {
        return Elements.Origin.EXPLICIT;
    }

    @Override
    public Elements.Origin getOrigin(AnnotatedConstruct c, AnnotationMirror a) {
        return Elements.Origin.EXPLICIT;
    }

    @Override
    public Elements.Origin getOrigin(ModuleElement m, ModuleElement.Directive directive) {
        return Elements.Origin.EXPLICIT;
    }

    @Override
    public boolean isBridge(ExecutableElement e) {
        MethodBinding methodBinding = (MethodBinding)((ExecutableElementImpl)e)._binding;
        return methodBinding.isBridge();
    }

    @Override
    public ModuleElement getModuleOf(Element elem) {
        if (elem instanceof ModuleElement) {
            return (ModuleElement)elem;
        }
        Element parent = elem.getEnclosingElement();
        while (parent != null) {
            if (parent instanceof ModuleElement) {
                return (ModuleElement)parent;
            }
            parent = parent.getEnclosingElement();
        }
        return null;
    }

    @Override
    public ModuleElement getModuleElement(CharSequence name) {
        LookupEnvironment lookup = this._env.getLookupEnvironment();
        ModuleBinding binding = lookup.getModule(name.length() == 0 ? ModuleBinding.UNNAMED : name.toString().toCharArray());
        if (binding == null) {
            return null;
        }
        return new ModuleElementImpl(this._env, binding);
    }

    @Override
    public Set<? extends ModuleElement> getAllModuleElements() {
        LookupEnvironment lookup = this._env.getLookupEnvironment();
        HashtableOfModule knownModules = lookup.knownModules;
        ModuleBinding[] modules = knownModules.valueTable;
        if (modules == null || modules.length == 0) {
            return Collections.emptySet();
        }
        HashSet<ModuleElement> mods = new HashSet<ModuleElement>(modules.length);
        ModuleBinding[] moduleBindingArray = modules;
        int n = modules.length;
        int n2 = 0;
        while (n2 < n) {
            ModuleBinding moduleBinding = moduleBindingArray[n2];
            if (moduleBinding != null) {
                ModuleElement element = (ModuleElement)this._env.getFactory().newElement(moduleBinding);
                mods.add(element);
            }
            ++n2;
        }
        mods.add((ModuleElement)this._env.getFactory().newElement(lookup.UnNamedModule));
        return mods;
    }

    @Override
    public PackageElement getPackageElement(ModuleElement module, CharSequence name) {
        ModuleBinding mBinding = ((ModuleElementImpl)module).binding;
        char[][] compoundName = CharOperation.splitOn('.', name.toString().toCharArray());
        PackageBinding p = null;
        p = mBinding != null ? mBinding.getVisiblePackage(compoundName) : this._env.getLookupEnvironment().createPackage(compoundName);
        if (p == null || !p.isValidBinding()) {
            return null;
        }
        return (PackageElement)this._env.getFactory().newElement(p);
    }
}


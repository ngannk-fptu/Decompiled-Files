/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ModuleElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.NameImpl;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class PackageElementImpl
extends ElementImpl
implements PackageElement {
    PackageElementImpl(BaseProcessingEnvImpl env, PackageBinding binding) {
        super(env, binding);
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitPackage(this, p);
    }

    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        PackageBinding packageBinding = (PackageBinding)this._binding;
        char[][] compoundName = CharOperation.arrayConcat(packageBinding.compoundName, TypeConstants.PACKAGE_INFO_NAME);
        ReferenceBinding type = packageBinding.environment.getType(compoundName);
        AnnotationBinding[] annotations = null;
        if (type != null && type.isValidBinding()) {
            annotations = type.getAnnotations();
        }
        return annotations;
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        Element newElement;
        int n;
        PackageBinding binding = (PackageBinding)this._binding;
        LookupEnvironment environment = binding.environment;
        char[][][] typeNames = null;
        INameEnvironment nameEnvironment = binding.environment.nameEnvironment;
        if (nameEnvironment instanceof FileSystem) {
            typeNames = ((FileSystem)nameEnvironment).findTypeNames(binding.compoundName);
        }
        HashSet<Element> set = new HashSet<Element>();
        HashSet<ReferenceBinding> types = new HashSet<ReferenceBinding>();
        if (typeNames != null) {
            char[][][] cArray = typeNames;
            n = typeNames.length;
            int n2 = 0;
            while (n2 < n) {
                ReferenceBinding type;
                char[][] typeName = cArray[n2];
                if (typeName != null && (type = environment.getType(typeName)) != null && !type.isMemberType() && type.isValidBinding() && (newElement = this._env.getFactory().newElement(type)).getKind() != ElementKind.PACKAGE) {
                    set.add(newElement);
                    types.add(type);
                }
                ++n2;
            }
        }
        if (binding.knownTypes != null) {
            ReferenceBinding[] knownTypes;
            ReferenceBinding[] referenceBindingArray = knownTypes = binding.knownTypes.valueTable;
            int n3 = knownTypes.length;
            n = 0;
            while (n < n3) {
                ReferenceBinding referenceBinding = referenceBindingArray[n];
                if (referenceBinding != null && referenceBinding.isValidBinding() && referenceBinding.enclosingType() == null && !types.contains(referenceBinding) && (newElement = this._env.getFactory().newElement(referenceBinding)).getKind() != ElementKind.PACKAGE) {
                    set.add(newElement);
                }
                ++n;
            }
        }
        ArrayList<Element> list = new ArrayList<Element>(set.size());
        list.addAll(set);
        return Collections.unmodifiableList(list);
    }

    @Override
    public Element getEnclosingElement() {
        if (this._env.getCompiler().options.sourceLevel < 0x350000L) {
            return null;
        }
        PackageBinding pBinding = (PackageBinding)this._binding;
        ModuleBinding module = pBinding.enclosingModule;
        if (module == null) {
            return null;
        }
        return new ModuleElementImpl(this._env, module);
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.PACKAGE;
    }

    @Override
    PackageElement getPackage() {
        return this;
    }

    @Override
    public Name getSimpleName() {
        char[][] compoundName = ((PackageBinding)this._binding).compoundName;
        int length = compoundName.length;
        if (length == 0) {
            return new NameImpl(CharOperation.NO_CHAR);
        }
        return new NameImpl(compoundName[length - 1]);
    }

    @Override
    public Name getQualifiedName() {
        return new NameImpl(CharOperation.concatWith(((PackageBinding)this._binding).compoundName, '.'));
    }

    @Override
    public boolean isUnnamed() {
        PackageBinding binding = (PackageBinding)this._binding;
        return binding.compoundName == CharOperation.NO_CHAR_CHAR;
    }
}


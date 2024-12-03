/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.model.NameImpl;
import org.eclipse.jdt.internal.compiler.apt.model.RecordComponentElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.VariableElementImpl;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

public class TypeElementImpl
extends ElementImpl
implements TypeElement {
    private final ElementKind _kindHint;

    TypeElementImpl(BaseProcessingEnvImpl env, ReferenceBinding binding, ElementKind kindHint) {
        super(env, binding);
        this._kindHint = kindHint;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitType(this, p);
    }

    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((ReferenceBinding)this._binding).getAnnotations();
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        Object variable;
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        ArrayList<Object> enclosed = new ArrayList<Object>(binding.fieldCount() + binding.methods().length + binding.memberTypes().length);
        Binding[] bindingArray = binding.methods();
        int n = bindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            MethodBinding method = bindingArray[n2];
            ExecutableElementImpl executable = new ExecutableElementImpl(this._env, method);
            enclosed.add(executable);
            ++n2;
        }
        bindingArray = binding.fields();
        n = bindingArray.length;
        n2 = 0;
        while (n2 < n) {
            Binding field = bindingArray[n2];
            if (!((FieldBinding)field).isSynthetic()) {
                variable = new VariableElementImpl(this._env, (VariableBinding)field);
                enclosed.add(variable);
            }
            ++n2;
        }
        if (binding.isRecord()) {
            RecordComponentBinding[] components;
            variable = components = binding.components();
            int n3 = components.length;
            n = 0;
            while (n < n3) {
                RecordComponentBinding comp = variable[n];
                RecordComponentElementImpl rec = new RecordComponentElementImpl(this._env, comp);
                enclosed.add(rec);
                ++n;
            }
        }
        ReferenceBinding[] referenceBindingArray = binding.memberTypes();
        n = referenceBindingArray.length;
        int n4 = 0;
        while (n4 < n) {
            ReferenceBinding memberType = referenceBindingArray[n4];
            TypeElementImpl type = new TypeElementImpl(this._env, memberType, null);
            enclosed.add(type);
            ++n4;
        }
        Collections.sort(enclosed, new SourceLocationComparator());
        return Collections.unmodifiableList(enclosed);
    }

    @Override
    public List<? extends RecordComponentElement> getRecordComponents() {
        if (this._binding instanceof SourceTypeBinding) {
            SourceTypeBinding binding = (SourceTypeBinding)this._binding;
            ArrayList<RecordComponentElementImpl> enclosed = new ArrayList<RecordComponentElementImpl>();
            RecordComponentBinding[] recordComponentBindingArray = binding.components();
            int n = recordComponentBindingArray.length;
            int n2 = 0;
            while (n2 < n) {
                RecordComponentBinding comp = recordComponentBindingArray[n2];
                RecordComponentElementImpl variable = new RecordComponentElementImpl(this._env, comp);
                enclosed.add(variable);
                ++n2;
            }
            Collections.sort(enclosed, new SourceLocationComparator());
            return Collections.unmodifiableList(enclosed);
        }
        return Collections.emptyList();
    }

    @Override
    public List<? extends TypeMirror> getPermittedSubclasses() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        if (binding.isSealed()) {
            ArrayList<TypeMirror> permitted = new ArrayList<TypeMirror>();
            ReferenceBinding[] referenceBindingArray = binding.permittedTypes();
            int n = referenceBindingArray.length;
            int n2 = 0;
            while (n2 < n) {
                ReferenceBinding type = referenceBindingArray[n2];
                TypeMirror typeMirror = this._env.getFactory().newTypeMirror(type);
                permitted.add(typeMirror);
                ++n2;
            }
            return Collections.unmodifiableList(permitted);
        }
        return Collections.emptyList();
    }

    @Override
    public Element getEnclosingElement() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        ReferenceBinding enclosingType = binding.enclosingType();
        if (enclosingType == null) {
            return this._env.getFactory().newPackageElement(binding.fPackage);
        }
        return this._env.getFactory().newElement(binding.enclosingType());
    }

    @Override
    public String getFileName() {
        char[] name = ((ReferenceBinding)this._binding).getFileName();
        if (name == null) {
            return null;
        }
        return new String(name);
    }

    @Override
    public List<? extends TypeMirror> getInterfaces() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        if (binding.superInterfaces() == null || binding.superInterfaces().length == 0) {
            return Collections.emptyList();
        }
        ArrayList<TypeMirror> interfaces = new ArrayList<TypeMirror>(binding.superInterfaces().length);
        ReferenceBinding[] referenceBindingArray = binding.superInterfaces();
        int n = referenceBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            ReferenceBinding interfaceBinding = referenceBindingArray[n2];
            TypeMirror interfaceType = this._env.getFactory().newTypeMirror(interfaceBinding);
            if (interfaceType.getKind() == TypeKind.ERROR) {
                if (this._env.getSourceVersion().compareTo(SourceVersion.RELEASE_6) > 0) {
                    interfaces.add(interfaceType);
                }
            } else {
                interfaces.add(interfaceType);
            }
            ++n2;
        }
        return Collections.unmodifiableList(interfaces);
    }

    @Override
    public ElementKind getKind() {
        if (this._kindHint != null) {
            return this._kindHint;
        }
        ReferenceBinding refBinding = (ReferenceBinding)this._binding;
        if (refBinding.isEnum()) {
            return ElementKind.ENUM;
        }
        if (refBinding.isRecord()) {
            return ElementKind.RECORD;
        }
        if (refBinding.isAnnotationType()) {
            return ElementKind.ANNOTATION_TYPE;
        }
        if (refBinding.isInterface()) {
            return ElementKind.INTERFACE;
        }
        if (refBinding.isClass()) {
            return ElementKind.CLASS;
        }
        throw new IllegalArgumentException("TypeElement " + new String(refBinding.shortReadableName()) + " has unexpected attributes " + refBinding.modifiers);
    }

    @Override
    public Set<Modifier> getModifiers() {
        ReferenceBinding refBinding = (ReferenceBinding)this._binding;
        int modifiers = refBinding.modifiers;
        if (refBinding.isInterface() && refBinding.isNestedType()) {
            modifiers |= 8;
        }
        return Factory.getModifiers(modifiers, this.getKind(), refBinding.isBinaryBinding());
    }

    @Override
    public NestingKind getNestingKind() {
        ReferenceBinding refBinding = (ReferenceBinding)this._binding;
        if (refBinding.isAnonymousType()) {
            return NestingKind.ANONYMOUS;
        }
        if (refBinding.isLocalType()) {
            return NestingKind.LOCAL;
        }
        if (refBinding.isMemberType()) {
            return NestingKind.MEMBER;
        }
        return NestingKind.TOP_LEVEL;
    }

    @Override
    PackageElement getPackage() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        return this._env.getFactory().newPackageElement(binding.fPackage);
    }

    @Override
    public Name getQualifiedName() {
        char[] qName;
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        if (binding.isMemberType()) {
            qName = CharOperation.concatWith(binding.enclosingType().compoundName, binding.sourceName, '.');
            CharOperation.replace(qName, '$', '.');
        } else {
            qName = CharOperation.concatWith(binding.compoundName, '.');
        }
        return new NameImpl(qName);
    }

    @Override
    public Name getSimpleName() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        return new NameImpl(binding.sourceName());
    }

    @Override
    public TypeMirror getSuperclass() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        ReferenceBinding superBinding = binding.superclass();
        if (superBinding == null || binding.isInterface()) {
            return this._env.getFactory().getNoType(TypeKind.NONE);
        }
        return this._env.getFactory().newTypeMirror(superBinding);
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        TypeVariableBinding[] variables = binding.typeVariables();
        if (variables.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<TypeParameterElement> params = new ArrayList<TypeParameterElement>(variables.length);
        TypeVariableBinding[] typeVariableBindingArray = variables;
        int n = variables.length;
        int n2 = 0;
        while (n2 < n) {
            TypeVariableBinding variable = typeVariableBindingArray[n2];
            params.add(this._env.getFactory().newTypeParameterElement(variable, this));
            ++n2;
        }
        return Collections.unmodifiableList(params);
    }

    @Override
    public boolean hides(Element hidden) {
        if (!(hidden instanceof TypeElementImpl)) {
            return false;
        }
        ReferenceBinding hiddenBinding = (ReferenceBinding)((TypeElementImpl)hidden)._binding;
        if (hiddenBinding.isPrivate()) {
            return false;
        }
        ReferenceBinding hiderBinding = (ReferenceBinding)this._binding;
        if (TypeBinding.equalsEquals(hiddenBinding, hiderBinding)) {
            return false;
        }
        if (!hiddenBinding.isMemberType() || !hiderBinding.isMemberType()) {
            return false;
        }
        if (!CharOperation.equals(hiddenBinding.sourceName, hiderBinding.sourceName)) {
            return false;
        }
        return hiderBinding.enclosingType().findSuperTypeOriginatingFrom(hiddenBinding.enclosingType()) != null;
    }

    @Override
    public String toString() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        char[] concatWith = CharOperation.concatWith(binding.compoundName, '.');
        if (binding.isNestedType()) {
            CharOperation.replace(concatWith, '$', '.');
            return new String(concatWith);
        }
        return new String(concatWith);
    }

    private static final class SourceLocationComparator
    implements Comparator<Element> {
        private final IdentityHashMap<ElementImpl, Integer> sourceStartCache = new IdentityHashMap();

        private SourceLocationComparator() {
        }

        @Override
        public int compare(Element o1, Element o2) {
            ElementImpl e1 = (ElementImpl)o1;
            ElementImpl e2 = (ElementImpl)o2;
            return this.getSourceStart(e1) - this.getSourceStart(e2);
        }

        private int getSourceStart(ElementImpl e) {
            Integer value = this.sourceStartCache.get(e);
            if (value == null) {
                value = this.determineSourceStart(e);
                this.sourceStartCache.put(e, value);
            }
            return value;
        }

        private int determineSourceStart(ElementImpl e) {
            switch (e.getKind()) {
                case ENUM: 
                case CLASS: 
                case ANNOTATION_TYPE: 
                case INTERFACE: 
                case RECORD: {
                    TypeElementImpl typeElementImpl = (TypeElementImpl)e;
                    Binding typeBinding = typeElementImpl._binding;
                    if (!(typeBinding instanceof SourceTypeBinding)) break;
                    SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)typeBinding;
                    TypeDeclaration typeDeclaration = (TypeDeclaration)sourceTypeBinding.scope.referenceContext();
                    return typeDeclaration.sourceStart;
                }
                case METHOD: 
                case CONSTRUCTOR: {
                    ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)e;
                    Binding binding = executableElementImpl._binding;
                    if (!(binding instanceof MethodBinding)) break;
                    MethodBinding methodBinding = (MethodBinding)binding;
                    return methodBinding.sourceStart();
                }
                case ENUM_CONSTANT: 
                case FIELD: 
                case RECORD_COMPONENT: {
                    FieldBinding fieldBinding;
                    FieldDeclaration fieldDeclaration;
                    VariableElementImpl variableElementImpl = (VariableElementImpl)e;
                    Binding binding = variableElementImpl._binding;
                    if (!(binding instanceof FieldBinding) || (fieldDeclaration = (fieldBinding = (FieldBinding)binding).sourceField()) == null) break;
                    return fieldDeclaration.sourceStart;
                }
            }
            return -1;
        }
    }
}


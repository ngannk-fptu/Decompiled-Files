/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.DeclaredTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.NoTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.PrimitiveTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeMirrorImpl;
import org.eclipse.jdt.internal.compiler.apt.model.WildcardTypeImpl;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class TypesImpl
implements Types {
    private final BaseProcessingEnvImpl _env;

    public TypesImpl(BaseProcessingEnvImpl env) {
        this._env = env;
    }

    @Override
    public Element asElement(TypeMirror t) {
        switch (t.getKind()) {
            case DECLARED: 
            case TYPEVAR: {
                return this._env.getFactory().newElement(((TypeMirrorImpl)t).binding());
            }
        }
        return null;
    }

    @Override
    public TypeMirror asMemberOf(DeclaredType containing, Element element) {
        ElementImpl elementImpl = (ElementImpl)element;
        DeclaredTypeImpl declaredTypeImpl = (DeclaredTypeImpl)containing;
        ReferenceBinding referenceBinding = (ReferenceBinding)declaredTypeImpl._binding;
        switch (element.getKind()) {
            case METHOD: 
            case CONSTRUCTOR: {
                TypeMirror typeMirror = this.findMemberInHierarchy(referenceBinding, elementImpl._binding, new MemberInTypeFinder(){

                    @Override
                    public TypeMirror find(ReferenceBinding typeBinding, Binding memberBinding) {
                        MethodBinding methodBinding = (MethodBinding)memberBinding;
                        MethodBinding[] methodBindingArray = typeBinding.methods();
                        int n = methodBindingArray.length;
                        int n2 = 0;
                        while (n2 < n) {
                            MethodBinding method = methodBindingArray[n2];
                            if (CharOperation.equals(method.selector, methodBinding.selector) && (method.original() == methodBinding || method.areParameterErasuresEqual(methodBinding))) {
                                return TypesImpl.this._env.getFactory().newTypeMirror(method);
                            }
                            ++n2;
                        }
                        return null;
                    }
                });
                if (typeMirror == null) break;
                return typeMirror;
            }
            case TYPE_PARAMETER: {
                TypeMirror typeMirror = this.findMemberInHierarchy(referenceBinding, elementImpl._binding, new MemberInTypeFinder(){

                    @Override
                    public TypeMirror find(ReferenceBinding typeBinding, Binding memberBinding) {
                        if (typeBinding instanceof ParameterizedTypeBinding) {
                            TypeBinding[] typeArguments;
                            TypeVariableBinding[] typeVariables;
                            TypeVariableBinding variableBinding = (TypeVariableBinding)memberBinding;
                            ReferenceBinding binding = ((ParameterizedTypeBinding)typeBinding).genericType();
                            if (variableBinding.declaringElement == binding && (typeVariables = binding.typeVariables()).length == (typeArguments = ((ParameterizedTypeBinding)typeBinding).typeArguments()).length) {
                                int i = 0;
                                while (i < typeVariables.length) {
                                    if (typeVariables[i] == memberBinding) {
                                        return TypesImpl.this._env.getFactory().newTypeMirror(typeArguments[i]);
                                    }
                                    ++i;
                                }
                            }
                        }
                        return null;
                    }
                });
                if (typeMirror == null) break;
                return typeMirror;
            }
            case ENUM_CONSTANT: 
            case FIELD: 
            case RECORD_COMPONENT: {
                TypeMirror typeMirror = this.findMemberInHierarchy(referenceBinding, elementImpl._binding, new MemberInTypeFinder(){

                    @Override
                    public TypeMirror find(ReferenceBinding typeBinding, Binding memberBinding) {
                        FieldBinding fieldBinding = (FieldBinding)memberBinding;
                        FieldBinding[] fieldBindingArray = typeBinding.fields();
                        int n = fieldBindingArray.length;
                        int n2 = 0;
                        while (n2 < n) {
                            FieldBinding field = fieldBindingArray[n2];
                            if (CharOperation.equals(field.name, fieldBinding.name)) {
                                return TypesImpl.this._env.getFactory().newTypeMirror(field);
                            }
                            ++n2;
                        }
                        return null;
                    }
                });
                if (typeMirror == null) break;
                return typeMirror;
            }
            case ENUM: 
            case CLASS: 
            case ANNOTATION_TYPE: 
            case INTERFACE: 
            case RECORD: {
                TypeMirror typeMirror = this.findMemberInHierarchy(referenceBinding, elementImpl._binding, new MemberInTypeFinder(){

                    @Override
                    public TypeMirror find(ReferenceBinding typeBinding, Binding memberBinding) {
                        ReferenceBinding elementBinding = (ReferenceBinding)memberBinding;
                        ReferenceBinding[] referenceBindingArray = typeBinding.memberTypes();
                        int n = referenceBindingArray.length;
                        int n2 = 0;
                        while (n2 < n) {
                            ReferenceBinding memberReferenceBinding = referenceBindingArray[n2];
                            if (CharOperation.equals(elementBinding.compoundName, memberReferenceBinding.compoundName)) {
                                return TypesImpl.this._env.getFactory().newTypeMirror(memberReferenceBinding);
                            }
                            ++n2;
                        }
                        return null;
                    }
                });
                if (typeMirror == null) break;
                return typeMirror;
            }
            default: {
                throw new IllegalArgumentException("element " + element + " has unrecognized element kind " + (Object)((Object)element.getKind()));
            }
        }
        throw new IllegalArgumentException("element " + element + " is not a member of the containing type " + containing + " nor any of its superclasses");
    }

    private TypeMirror findMemberInHierarchy(ReferenceBinding typeBinding, Binding memberBinding, MemberInTypeFinder finder) {
        TypeMirror result = null;
        if (typeBinding == null) {
            return null;
        }
        result = finder.find(typeBinding, memberBinding);
        if (result != null) {
            return result;
        }
        result = this.findMemberInHierarchy(typeBinding.superclass(), memberBinding, finder);
        if (result != null) {
            return result;
        }
        ReferenceBinding[] referenceBindingArray = typeBinding.superInterfaces();
        int n = referenceBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            ReferenceBinding superInterface = referenceBindingArray[n2];
            result = this.findMemberInHierarchy(superInterface, memberBinding, finder);
            if (result != null) {
                return result;
            }
            ++n2;
        }
        return null;
    }

    private void validateRealType(TypeMirror t) {
        switch (t.getKind()) {
            case PACKAGE: 
            case EXECUTABLE: 
            case MODULE: {
                throw new IllegalArgumentException("Executable, package and module are illegal argument for Types.contains(..)");
            }
        }
    }

    private void validateRealTypes(TypeMirror t1, TypeMirror t2) {
        this.validateRealType(t1);
        this.validateRealType(t2);
    }

    @Override
    public TypeElement boxedClass(PrimitiveType p) {
        PrimitiveTypeImpl primitiveTypeImpl = (PrimitiveTypeImpl)p;
        BaseTypeBinding baseTypeBinding = (BaseTypeBinding)primitiveTypeImpl._binding;
        TypeBinding boxed = this._env.getLookupEnvironment().computeBoxingType(baseTypeBinding);
        return (TypeElement)this._env.getFactory().newElement(boxed);
    }

    @Override
    public TypeMirror capture(TypeMirror t) {
        this.validateRealType(t);
        TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)t;
        if (typeMirrorImpl._binding instanceof ParameterizedTypeBinding) {
            throw new UnsupportedOperationException("NYI: TypesImpl.capture(...)");
        }
        return t;
    }

    @Override
    public boolean contains(TypeMirror t1, TypeMirror t2) {
        this.validateRealTypes(t1, t2);
        throw new UnsupportedOperationException("NYI: TypesImpl.contains(" + t1 + ", " + t2 + ")");
    }

    @Override
    public List<? extends TypeMirror> directSupertypes(TypeMirror t) {
        this.validateRealType(t);
        TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)t;
        Binding binding = typeMirrorImpl._binding;
        if (binding instanceof ReferenceBinding) {
            ReferenceBinding referenceBinding = (ReferenceBinding)binding;
            ArrayList<TypeMirror> list = new ArrayList<TypeMirror>();
            ReferenceBinding superclass = referenceBinding.superclass();
            if (superclass != null) {
                list.add(this._env.getFactory().newTypeMirror(superclass));
            }
            ReferenceBinding[] referenceBindingArray = referenceBinding.superInterfaces();
            int n = referenceBindingArray.length;
            int n2 = 0;
            while (n2 < n) {
                ReferenceBinding interfaceBinding = referenceBindingArray[n2];
                list.add(this._env.getFactory().newTypeMirror(interfaceBinding));
                ++n2;
            }
            return Collections.unmodifiableList(list);
        }
        return Collections.emptyList();
    }

    @Override
    public TypeMirror erasure(TypeMirror t) {
        this.validateRealType(t);
        TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)t;
        Binding binding = typeMirrorImpl._binding;
        if (binding instanceof ReferenceBinding) {
            TypeBinding type = ((ReferenceBinding)binding).erasure();
            if (type.isGenericType()) {
                type = this._env.getLookupEnvironment().convertToRawType(type, false);
            }
            return this._env.getFactory().newTypeMirror(type);
        }
        if (binding instanceof ArrayBinding) {
            TypeBinding typeBinding = (TypeBinding)binding;
            TypeBinding leafType = typeBinding.leafComponentType().erasure();
            if (leafType.isGenericType()) {
                leafType = this._env.getLookupEnvironment().convertToRawType(leafType, false);
            }
            return this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createArrayType(leafType, typeBinding.dimensions()));
        }
        return t;
    }

    @Override
    public ArrayType getArrayType(TypeMirror componentType) {
        TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)componentType;
        TypeBinding typeBinding = (TypeBinding)typeMirrorImpl._binding;
        return (ArrayType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createArrayType(typeBinding.leafComponentType(), typeBinding.dimensions() + 1));
    }

    @Override
    public DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror ... typeArgs) {
        int typeArgsLength = typeArgs.length;
        TypeElementImpl typeElementImpl = (TypeElementImpl)typeElem;
        ReferenceBinding elementBinding = (ReferenceBinding)typeElementImpl._binding;
        TypeVariableBinding[] typeVariables = elementBinding.typeVariables();
        int typeVariablesLength = typeVariables.length;
        if (typeArgsLength == 0) {
            if (elementBinding.isGenericType()) {
                return (DeclaredType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createRawType(elementBinding, null));
            }
            return (DeclaredType)typeElem.asType();
        }
        if (typeArgsLength != typeVariablesLength) {
            throw new IllegalArgumentException("Number of typeArguments doesn't match the number of formal parameters of typeElem");
        }
        TypeBinding[] typeArguments = new TypeBinding[typeArgsLength];
        int i = 0;
        while (i < typeArgsLength) {
            TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)typeArgs[i];
            Binding binding = typeMirrorImpl._binding;
            if (!(binding instanceof TypeBinding)) {
                throw new IllegalArgumentException("Invalid type argument: " + typeMirrorImpl);
            }
            typeArguments[i] = (TypeBinding)binding;
            ++i;
        }
        ReferenceBinding enclosing = elementBinding.enclosingType();
        if (enclosing != null) {
            enclosing = this._env.getLookupEnvironment().createRawType(enclosing, null);
        }
        return (DeclaredType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createParameterizedType(elementBinding, typeArguments, enclosing));
    }

    @Override
    public DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem, TypeMirror ... typeArgs) {
        int typeArgsLength = typeArgs.length;
        TypeElementImpl typeElementImpl = (TypeElementImpl)typeElem;
        ReferenceBinding elementBinding = (ReferenceBinding)typeElementImpl._binding;
        TypeVariableBinding[] typeVariables = elementBinding.typeVariables();
        int typeVariablesLength = typeVariables.length;
        DeclaredTypeImpl declaredTypeImpl = (DeclaredTypeImpl)containing;
        ReferenceBinding enclosingType = (ReferenceBinding)declaredTypeImpl._binding;
        if (typeArgsLength == 0) {
            if (elementBinding.isGenericType()) {
                return (DeclaredType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createRawType(elementBinding, enclosingType));
            }
            ParameterizedTypeBinding ptb = this._env.getLookupEnvironment().createParameterizedType(elementBinding, null, enclosingType);
            return (DeclaredType)this._env.getFactory().newTypeMirror(ptb);
        }
        if (typeArgsLength != typeVariablesLength) {
            throw new IllegalArgumentException("Number of typeArguments doesn't match the number of formal parameters of typeElem");
        }
        TypeBinding[] typeArguments = new TypeBinding[typeArgsLength];
        int i = 0;
        while (i < typeArgsLength) {
            TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)typeArgs[i];
            Binding binding = typeMirrorImpl._binding;
            if (!(binding instanceof TypeBinding)) {
                throw new IllegalArgumentException("Invalid type for a type arguments : " + typeMirrorImpl);
            }
            typeArguments[i] = (TypeBinding)binding;
            ++i;
        }
        return (DeclaredType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createParameterizedType(elementBinding, typeArguments, enclosingType));
    }

    @Override
    public NoType getNoType(TypeKind kind) {
        return this._env.getFactory().getNoType(kind);
    }

    @Override
    public NullType getNullType() {
        return this._env.getFactory().getNullType();
    }

    @Override
    public PrimitiveType getPrimitiveType(TypeKind kind) {
        return this._env.getFactory().getPrimitiveType(kind);
    }

    @Override
    public WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound) {
        if (extendsBound != null && superBound != null) {
            throw new IllegalArgumentException("Extends and super bounds cannot be set at the same time");
        }
        if (extendsBound != null) {
            TypeMirrorImpl extendsBoundMirrorType = (TypeMirrorImpl)extendsBound;
            TypeBinding typeBinding = (TypeBinding)extendsBoundMirrorType._binding;
            return (WildcardType)this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createWildcard(null, 0, typeBinding, null, 1));
        }
        if (superBound != null) {
            TypeMirrorImpl superBoundMirrorType = (TypeMirrorImpl)superBound;
            TypeBinding typeBinding = (TypeBinding)superBoundMirrorType._binding;
            return new WildcardTypeImpl(this._env, this._env.getLookupEnvironment().createWildcard(null, 0, typeBinding, null, 2));
        }
        return new WildcardTypeImpl(this._env, this._env.getLookupEnvironment().createWildcard(null, 0, null, null, 0));
    }

    @Override
    public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
        this.validateRealTypes(t1, t2);
        if (!(t1 instanceof TypeMirrorImpl) || !(t2 instanceof TypeMirrorImpl)) {
            return false;
        }
        Binding b1 = ((TypeMirrorImpl)t1).binding();
        Binding b2 = ((TypeMirrorImpl)t2).binding();
        if (!(b1 instanceof TypeBinding) || !(b2 instanceof TypeBinding)) {
            throw new IllegalArgumentException();
        }
        if (((TypeBinding)b1).isCompatibleWith((TypeBinding)b2)) {
            return true;
        }
        TypeBinding convertedType = this._env.getLookupEnvironment().computeBoxingType((TypeBinding)b1);
        return convertedType != null && convertedType.isCompatibleWith((TypeBinding)b2);
    }

    @Override
    public boolean isSameType(TypeMirror t1, TypeMirror t2) {
        Binding b2;
        if (t1 instanceof NoTypeImpl) {
            if (t2 instanceof NoTypeImpl) {
                return ((NoTypeImpl)t1).getKind() == ((NoTypeImpl)t2).getKind();
            }
            return false;
        }
        if (t2 instanceof NoTypeImpl) {
            return false;
        }
        if (t1.getKind() == TypeKind.WILDCARD || t2.getKind() == TypeKind.WILDCARD) {
            return false;
        }
        if (t1 == t2) {
            return true;
        }
        if (!(t1 instanceof TypeMirrorImpl) || !(t2 instanceof TypeMirrorImpl)) {
            return false;
        }
        Binding b1 = ((TypeMirrorImpl)t1).binding();
        if (b1 == (b2 = ((TypeMirrorImpl)t2).binding())) {
            return true;
        }
        if (!(b1 instanceof TypeBinding) || !(b2 instanceof TypeBinding)) {
            return false;
        }
        TypeBinding type1 = (TypeBinding)b1;
        TypeBinding type2 = (TypeBinding)b2;
        if (TypeBinding.equalsEquals(type1, type2)) {
            return true;
        }
        return CharOperation.equals(type1.computeUniqueKey(), type2.computeUniqueKey());
    }

    @Override
    public boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
        MethodBinding methodBinding1 = (MethodBinding)((ExecutableTypeImpl)m1)._binding;
        MethodBinding methodBinding2 = (MethodBinding)((ExecutableTypeImpl)m2)._binding;
        if (!CharOperation.equals(methodBinding1.selector, methodBinding2.selector)) {
            return false;
        }
        return methodBinding1.areParameterErasuresEqual(methodBinding2) && methodBinding1.areTypeVariableErasuresEqual(methodBinding2);
    }

    @Override
    public boolean isSubtype(TypeMirror t1, TypeMirror t2) {
        Binding b2;
        this.validateRealTypes(t1, t2);
        if (t1 instanceof NoTypeImpl) {
            if (t2 instanceof NoTypeImpl) {
                return ((NoTypeImpl)t1).getKind() == ((NoTypeImpl)t2).getKind();
            }
            return false;
        }
        if (t2 instanceof NoTypeImpl) {
            return false;
        }
        if (!(t1 instanceof TypeMirrorImpl) || !(t2 instanceof TypeMirrorImpl)) {
            throw new IllegalArgumentException();
        }
        if (t1 == t2) {
            return true;
        }
        Binding b1 = ((TypeMirrorImpl)t1).binding();
        if (b1 == (b2 = ((TypeMirrorImpl)t2).binding())) {
            return true;
        }
        if (!(b1 instanceof TypeBinding) || !(b2 instanceof TypeBinding)) {
            throw new IllegalArgumentException();
        }
        if (b1.kind() == 132 || b2.kind() == 132) {
            if (b1.kind() != b2.kind()) {
                return false;
            }
            return ((TypeBinding)b1).isCompatibleWith((TypeBinding)b2);
        }
        return ((TypeBinding)b1).isCompatibleWith((TypeBinding)b2);
    }

    @Override
    public PrimitiveType unboxedType(TypeMirror t) {
        if (!(((TypeMirrorImpl)t)._binding instanceof ReferenceBinding)) {
            throw new IllegalArgumentException("Given type mirror cannot be unboxed");
        }
        ReferenceBinding boxed = (ReferenceBinding)((TypeMirrorImpl)t)._binding;
        TypeBinding unboxed = this._env.getLookupEnvironment().computeBoxingType(boxed);
        if (unboxed.kind() != 132) {
            throw new IllegalArgumentException();
        }
        return (PrimitiveType)this._env.getFactory().newTypeMirror((BaseTypeBinding)unboxed);
    }

    private static interface MemberInTypeFinder {
        public TypeMirror find(ReferenceBinding var1, Binding var2);
    }
}


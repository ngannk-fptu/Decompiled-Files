/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.util.ManyToMany;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class AnnotationDiscoveryVisitor
extends ASTVisitor {
    final BaseProcessingEnvImpl _env;
    final Factory _factory;
    final ManyToMany<TypeElement, Element> _annoToElement;

    public AnnotationDiscoveryVisitor(BaseProcessingEnvImpl env) {
        this._env = env;
        this._factory = env.getFactory();
        this._annoToElement = new ManyToMany();
    }

    @Override
    public boolean visit(Argument argument, BlockScope scope) {
        Annotation[] annotations = argument.annotations;
        ReferenceContext referenceContext = scope.referenceContext();
        if (referenceContext instanceof AbstractMethodDeclaration) {
            MethodBinding binding = ((AbstractMethodDeclaration)referenceContext).binding;
            if (binding != null) {
                TypeDeclaration typeDeclaration = scope.referenceType();
                typeDeclaration.binding.resolveTypesFor(binding);
                if (argument.binding != null) {
                    argument.binding = new AptSourceLocalVariableBinding(argument.binding, binding);
                }
            }
            if (annotations != null && argument.binding != null) {
                this.resolveAnnotations(scope, annotations, argument.binding);
            }
        }
        return false;
    }

    @Override
    public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
        Argument[] arguments;
        TypeParameter[] typeParameters;
        Annotation[] annotations = constructorDeclaration.annotations;
        if (annotations != null) {
            MethodBinding constructorBinding = constructorDeclaration.binding;
            if (constructorBinding == null) {
                return false;
            }
            ((SourceTypeBinding)constructorBinding.declaringClass).resolveTypesFor(constructorBinding);
            this.resolveAnnotations(constructorDeclaration.scope, annotations, constructorBinding);
        }
        if ((typeParameters = constructorDeclaration.typeParameters) != null) {
            int typeParametersLength = typeParameters.length;
            int i = 0;
            while (i < typeParametersLength) {
                typeParameters[i].traverse((ASTVisitor)this, constructorDeclaration.scope);
                ++i;
            }
        }
        if ((arguments = constructorDeclaration.arguments) != null) {
            int argumentLength = arguments.length;
            int i = 0;
            while (i < argumentLength) {
                arguments[i].traverse((ASTVisitor)this, constructorDeclaration.scope);
                ++i;
            }
        }
        return false;
    }

    @Override
    public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
        Annotation[] annotations = fieldDeclaration.annotations;
        if (annotations != null) {
            FieldBinding fieldBinding = fieldDeclaration.binding;
            if (fieldBinding == null) {
                return false;
            }
            ((SourceTypeBinding)fieldBinding.declaringClass).resolveTypeFor(fieldBinding);
            if (fieldDeclaration.binding == null) {
                return false;
            }
            this.resolveAnnotations(scope, annotations, fieldBinding);
        }
        return false;
    }

    @Override
    public boolean visit(RecordComponent recordComponent, BlockScope scope) {
        Annotation[] annotations = recordComponent.annotations;
        if (annotations != null) {
            RecordComponentBinding recordComponentBinding = recordComponent.binding;
            if (recordComponentBinding == null) {
                return false;
            }
            ((SourceTypeBinding)recordComponentBinding.declaringRecord).resolveTypeFor(recordComponentBinding);
            if (recordComponent.binding == null) {
                return false;
            }
            this.resolveAnnotations(scope, annotations, recordComponentBinding);
        }
        return false;
    }

    @Override
    public boolean visit(TypeParameter typeParameter, ClassScope scope) {
        Annotation[] annotations = typeParameter.annotations;
        if (annotations != null) {
            TypeVariableBinding binding = typeParameter.binding;
            if (binding == null) {
                return false;
            }
            this.resolveAnnotations(scope.referenceContext.initializerScope, annotations, binding);
        }
        return false;
    }

    @Override
    public boolean visit(TypeParameter typeParameter, BlockScope scope) {
        Annotation[] annotations = typeParameter.annotations;
        if (annotations != null) {
            TypeVariableBinding binding = typeParameter.binding;
            if (binding == null) {
                return false;
            }
            MethodBinding methodBinding = (MethodBinding)binding.declaringElement;
            ((SourceTypeBinding)methodBinding.declaringClass).resolveTypesFor(methodBinding);
            this.resolveAnnotations(scope, annotations, binding);
        }
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
        Argument[] arguments;
        TypeParameter[] typeParameters;
        Annotation[] annotations = methodDeclaration.annotations;
        if (annotations != null) {
            MethodBinding methodBinding = methodDeclaration.binding;
            if (methodBinding == null) {
                return false;
            }
            ((SourceTypeBinding)methodBinding.declaringClass).resolveTypesFor(methodBinding);
            this.resolveAnnotations(methodDeclaration.scope, annotations, methodBinding);
        }
        if ((typeParameters = methodDeclaration.typeParameters) != null) {
            int typeParametersLength = typeParameters.length;
            int i = 0;
            while (i < typeParametersLength) {
                typeParameters[i].traverse((ASTVisitor)this, methodDeclaration.scope);
                ++i;
            }
        }
        if ((arguments = methodDeclaration.arguments) != null) {
            int argumentLength = arguments.length;
            int i = 0;
            while (i < argumentLength) {
                arguments[i].traverse((ASTVisitor)this, methodDeclaration.scope);
                ++i;
            }
        }
        return false;
    }

    @Override
    public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
        SourceTypeBinding binding = memberTypeDeclaration.binding;
        if (binding == null) {
            return false;
        }
        Annotation[] annotations = memberTypeDeclaration.annotations;
        if (annotations != null) {
            this.resolveAnnotations(memberTypeDeclaration.staticInitializerScope, annotations, binding);
        }
        return true;
    }

    @Override
    public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
        SourceTypeBinding binding = typeDeclaration.binding;
        if (binding == null) {
            return false;
        }
        Annotation[] annotations = typeDeclaration.annotations;
        if (annotations != null) {
            this.resolveAnnotations(typeDeclaration.staticInitializerScope, annotations, binding);
        }
        return true;
    }

    @Override
    public boolean visit(ModuleDeclaration module, CompilationUnitScope scope) {
        SourceModuleBinding binding = module.binding;
        if (binding == null) {
            return false;
        }
        Annotation[] annotations = module.annotations;
        if (annotations != null) {
            this.resolveAnnotations(module.scope, annotations, binding);
        }
        return true;
    }

    private void resolveAnnotations(BlockScope scope, Annotation[] annotations, Binding currentBinding) {
        AnnotationBinding[] annotationBindings;
        int length;
        int n = length = annotations == null ? 0 : annotations.length;
        if (length == 0) {
            return;
        }
        boolean old = scope.insideTypeAnnotation;
        scope.insideTypeAnnotation = true;
        currentBinding.getAnnotationTagBits();
        scope.insideTypeAnnotation = old;
        ElementImpl element = (ElementImpl)this._factory.newElement(currentBinding);
        AnnotationBinding[] annotationBindingArray = annotationBindings = element.getPackedAnnotationBindings();
        int n2 = annotationBindings.length;
        int n3 = 0;
        while (n3 < n2) {
            AnnotationBinding binding = annotationBindingArray[n3];
            ReferenceBinding annotationType = binding.getAnnotationType();
            if (binding != null && Annotation.isAnnotationTargetAllowed(scope, annotationType, currentBinding)) {
                TypeElement anno = (TypeElement)this._factory.newElement(annotationType);
                this._annoToElement.put(anno, element);
            }
            ++n3;
        }
    }
}


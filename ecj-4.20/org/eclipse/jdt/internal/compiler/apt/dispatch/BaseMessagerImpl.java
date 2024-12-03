/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.apt.dispatch.AptProblem;
import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMemberValue;
import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMirrorImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ModuleElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.VariableElementImpl;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.util.Util;

public class BaseMessagerImpl {
    static final String[] NO_ARGUMENTS = new String[0];

    public static AptProblem createProblem(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {
        int severity;
        AnnotationBinding annotationBinding;
        Annotation annotation;
        ReferenceContext referenceContext = null;
        Annotation[] elementAnnotations = null;
        int startPosition = 0;
        int endPosition = 0;
        if (e != null) {
            switch (e.getKind()) {
                case MODULE: {
                    ModuleElementImpl moduleElementImpl = (ModuleElementImpl)e;
                    Binding moduleBinding = moduleElementImpl._binding;
                    if (!(moduleBinding instanceof SourceModuleBinding)) break;
                    SourceModuleBinding sourceModuleBinding = (SourceModuleBinding)moduleBinding;
                    CompilationUnitDeclaration unitDeclaration = (CompilationUnitDeclaration)sourceModuleBinding.scope.referenceContext();
                    referenceContext = unitDeclaration;
                    elementAnnotations = unitDeclaration.moduleDeclaration.annotations;
                    startPosition = unitDeclaration.moduleDeclaration.sourceStart;
                    endPosition = unitDeclaration.moduleDeclaration.sourceEnd;
                    break;
                }
                case ENUM: 
                case CLASS: 
                case ANNOTATION_TYPE: 
                case INTERFACE: {
                    TypeElementImpl typeElementImpl = (TypeElementImpl)e;
                    Binding typeBinding = typeElementImpl._binding;
                    if (!(typeBinding instanceof SourceTypeBinding)) break;
                    SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)typeBinding;
                    TypeDeclaration typeDeclaration = (TypeDeclaration)sourceTypeBinding.scope.referenceContext();
                    referenceContext = typeDeclaration;
                    elementAnnotations = typeDeclaration.annotations;
                    startPosition = typeDeclaration.sourceStart;
                    endPosition = typeDeclaration.sourceEnd;
                    break;
                }
                case PACKAGE: {
                    break;
                }
                case METHOD: 
                case CONSTRUCTOR: {
                    MethodBinding methodBinding;
                    AbstractMethodDeclaration sourceMethod;
                    ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)e;
                    Binding binding = executableElementImpl._binding;
                    if (!(binding instanceof MethodBinding) || (sourceMethod = (methodBinding = (MethodBinding)binding).sourceMethod()) == null) break;
                    referenceContext = sourceMethod;
                    elementAnnotations = sourceMethod.annotations;
                    startPosition = sourceMethod.sourceStart;
                    endPosition = sourceMethod.sourceEnd;
                    break;
                }
                case ENUM_CONSTANT: {
                    break;
                }
                case EXCEPTION_PARAMETER: {
                    break;
                }
                case FIELD: 
                case PARAMETER: {
                    VariableElementImpl variableElementImpl = (VariableElementImpl)e;
                    Binding binding = variableElementImpl._binding;
                    if (binding instanceof FieldBinding) {
                        FieldBinding fieldBinding = (FieldBinding)binding;
                        FieldDeclaration fieldDeclaration = fieldBinding.sourceField();
                        if (fieldDeclaration == null) break;
                        ReferenceBinding declaringClass = fieldBinding.declaringClass;
                        if (declaringClass instanceof SourceTypeBinding) {
                            SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)declaringClass;
                            TypeDeclaration typeDeclaration = (TypeDeclaration)sourceTypeBinding.scope.referenceContext();
                            referenceContext = typeDeclaration;
                        }
                        elementAnnotations = fieldDeclaration.annotations;
                        startPosition = fieldDeclaration.sourceStart;
                        endPosition = fieldDeclaration.sourceEnd;
                        break;
                    }
                    if (!(binding instanceof AptSourceLocalVariableBinding)) break;
                    AptSourceLocalVariableBinding parameterBinding = (AptSourceLocalVariableBinding)binding;
                    LocalDeclaration parameterDeclaration = parameterBinding.declaration;
                    if (parameterDeclaration == null) break;
                    MethodBinding methodBinding = parameterBinding.methodBinding;
                    if (methodBinding != null) {
                        referenceContext = methodBinding.sourceMethod();
                    }
                    elementAnnotations = parameterDeclaration.annotations;
                    startPosition = parameterDeclaration.sourceStart;
                    endPosition = parameterDeclaration.sourceEnd;
                    break;
                }
                case STATIC_INIT: 
                case INSTANCE_INIT: {
                    break;
                }
                case LOCAL_VARIABLE: {
                    break;
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        if (msg != null) {
            builder.append(msg);
        }
        if (a != null && elementAnnotations != null && (annotation = BaseMessagerImpl.findAnnotation(elementAnnotations, annotationBinding = ((AnnotationMirrorImpl)a)._binding)) != null) {
            startPosition = annotation.sourceStart;
            endPosition = annotation.sourceEnd;
            if (v != null && v instanceof AnnotationMemberValue) {
                MethodBinding methodBinding = ((AnnotationMemberValue)v).getMethodBinding();
                MemberValuePair[] memberValuePairs = annotation.memberValuePairs();
                MemberValuePair memberValuePair = null;
                int i = 0;
                while (memberValuePair == null && i < memberValuePairs.length) {
                    if (methodBinding == memberValuePairs[i].binding) {
                        memberValuePair = memberValuePairs[i];
                    }
                    ++i;
                }
                if (memberValuePair != null) {
                    startPosition = memberValuePair.sourceStart;
                    endPosition = memberValuePair.sourceEnd;
                }
            }
        }
        int lineNumber = 0;
        int columnNumber = 1;
        char[] fileName = null;
        if (referenceContext != null) {
            int n;
            CompilationResult result = referenceContext.compilationResult();
            fileName = result.fileName;
            int[] lineEnds = null;
            if (startPosition >= 0) {
                lineEnds = result.getLineSeparatorPositions();
                n = Util.getLineNumber(startPosition, lineEnds, 0, lineEnds.length - 1);
            } else {
                n = 0;
            }
            lineNumber = n;
            columnNumber = startPosition >= 0 ? Util.searchColumnNumber(result.getLineSeparatorPositions(), lineNumber, startPosition) : 0;
        }
        switch (kind) {
            case ERROR: {
                severity = 1;
                break;
            }
            case NOTE: 
            case OTHER: {
                severity = 1024;
                break;
            }
            default: {
                severity = 0;
            }
        }
        return new AptProblem(referenceContext, fileName, String.valueOf(builder), 0, NO_ARGUMENTS, severity, startPosition, endPosition, lineNumber, columnNumber);
    }

    private static Annotation findAnnotation(Annotation[] elementAnnotations, AnnotationBinding annotationBinding) {
        int i = 0;
        while (i < elementAnnotations.length) {
            Annotation annotation = BaseMessagerImpl.findAnnotation(elementAnnotations[i], annotationBinding);
            if (annotation != null) {
                return annotation;
            }
            ++i;
        }
        return null;
    }

    private static Annotation findAnnotation(Annotation elementAnnotation, AnnotationBinding annotationBinding) {
        MemberValuePair[] memberValuePairs;
        if (annotationBinding == elementAnnotation.getCompilerAnnotation()) {
            return elementAnnotation;
        }
        MemberValuePair[] memberValuePairArray = memberValuePairs = elementAnnotation.memberValuePairs();
        int n = memberValuePairs.length;
        int n2 = 0;
        while (n2 < n) {
            MemberValuePair mvp = memberValuePairArray[n2];
            Expression v = mvp.value;
            if (v instanceof Annotation) {
                Annotation a = BaseMessagerImpl.findAnnotation((Annotation)v, annotationBinding);
                if (a != null) {
                    return a;
                }
            } else if (v instanceof ArrayInitializer) {
                Expression[] expressions;
                Expression[] expressionArray = expressions = ((ArrayInitializer)v).expressions;
                int n3 = expressions.length;
                int n4 = 0;
                while (n4 < n3) {
                    Annotation a;
                    Expression e = expressionArray[n4];
                    if (e instanceof Annotation && (a = BaseMessagerImpl.findAnnotation((Annotation)e, annotationBinding)) != null) {
                        return a;
                    }
                    ++n4;
                }
            }
            ++n2;
        }
        return null;
    }
}


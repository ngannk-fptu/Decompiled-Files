/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.apt.dispatch.AnnotationDiscoveryVisitor;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import org.eclipse.jdt.internal.compiler.apt.util.ManyToMany;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;

public class RoundEnvImpl
implements RoundEnvironment {
    private final BaseProcessingEnvImpl _processingEnv;
    private final boolean _isLastRound;
    private final CompilationUnitDeclaration[] _units;
    private final ManyToMany<TypeElement, Element> _annoToUnit;
    private final ReferenceBinding[] _binaryTypes;
    private final Factory _factory;
    private Set<Element> _rootElements = null;

    public RoundEnvImpl(CompilationUnitDeclaration[] units, ReferenceBinding[] binaryTypeBindings, boolean isLastRound, BaseProcessingEnvImpl env) {
        this._processingEnv = env;
        this._isLastRound = isLastRound;
        this._units = units;
        this._factory = this._processingEnv.getFactory();
        AnnotationDiscoveryVisitor visitor = new AnnotationDiscoveryVisitor(this._processingEnv);
        if (this._units != null) {
            CompilationUnitDeclaration[] compilationUnitDeclarationArray = this._units;
            int n = this._units.length;
            int n2 = 0;
            while (n2 < n) {
                CompilationUnitDeclaration unit = compilationUnitDeclarationArray[n2];
                unit.scope.environment.suppressImportErrors = true;
                unit.traverse((ASTVisitor)visitor, unit.scope);
                unit.scope.environment.suppressImportErrors = false;
                ++n2;
            }
        }
        this._annoToUnit = visitor._annoToElement;
        if (binaryTypeBindings != null) {
            this.collectAnnotations(binaryTypeBindings);
        }
        this._binaryTypes = binaryTypeBindings;
    }

    private void collectAnnotations(ReferenceBinding[] referenceBindings) {
        ReferenceBinding[] referenceBindingArray = referenceBindings;
        int n = referenceBindings.length;
        int n2 = 0;
        while (n2 < n) {
            MethodBinding[] methodBindings;
            int n3;
            FieldBinding[] fieldBindings;
            AnnotationBinding[] annotationBindings;
            ReferenceBinding referenceBinding = referenceBindingArray[n2];
            if (referenceBinding instanceof ParameterizedTypeBinding) {
                referenceBinding = ((ParameterizedTypeBinding)referenceBinding).genericType();
            }
            AnnotationBinding[] annotationBindingArray = annotationBindings = Factory.getPackedAnnotationBindings(referenceBinding.getAnnotations());
            int n4 = annotationBindings.length;
            int n5 = 0;
            while (n5 < n4) {
                AnnotationBinding annotationBinding = annotationBindingArray[n5];
                TypeElement anno = (TypeElement)this._factory.newElement(annotationBinding.getAnnotationType());
                Element element = this._factory.newElement(referenceBinding);
                this._annoToUnit.put(anno, element);
                ++n5;
            }
            FieldBinding[] fieldBindingArray = fieldBindings = referenceBinding.fields();
            int n6 = fieldBindings.length;
            n4 = 0;
            while (n4 < n6) {
                FieldBinding fieldBinding = fieldBindingArray[n4];
                AnnotationBinding[] annotationBindingArray2 = annotationBindings = Factory.getPackedAnnotationBindings(fieldBinding.getAnnotations());
                n3 = annotationBindings.length;
                int n7 = 0;
                while (n7 < n3) {
                    AnnotationBinding annotationBinding = annotationBindingArray2[n7];
                    TypeElement anno = (TypeElement)this._factory.newElement(annotationBinding.getAnnotationType());
                    Element element = this._factory.newElement(fieldBinding);
                    this._annoToUnit.put(anno, element);
                    ++n7;
                }
                ++n4;
            }
            MethodBinding[] methodBindingArray = methodBindings = referenceBinding.methods();
            int n8 = methodBindings.length;
            n6 = 0;
            while (n6 < n8) {
                MethodBinding methodBinding = methodBindingArray[n6];
                AnnotationBinding[] annotationBindingArray3 = annotationBindings = Factory.getPackedAnnotationBindings(methodBinding.getAnnotations());
                int n9 = annotationBindings.length;
                n3 = 0;
                while (n3 < n9) {
                    AnnotationBinding annotationBinding = annotationBindingArray3[n3];
                    TypeElement anno = (TypeElement)this._factory.newElement(annotationBinding.getAnnotationType());
                    Element element = this._factory.newElement(methodBinding);
                    this._annoToUnit.put(anno, element);
                    ++n3;
                }
                ++n6;
            }
            ReferenceBinding[] memberTypes = referenceBinding.memberTypes();
            this.collectAnnotations(memberTypes);
            ++n2;
        }
    }

    public Set<TypeElement> getRootAnnotations() {
        return Collections.unmodifiableSet(this._annoToUnit.getKeySet());
    }

    @Override
    public boolean errorRaised() {
        return this._processingEnv.errorRaised();
    }

    @Override
    public Set<? extends Element> getElementsAnnotatedWith(TypeElement a) {
        if (a.getKind() != ElementKind.ANNOTATION_TYPE) {
            throw new IllegalArgumentException("Argument must represent an annotation type");
        }
        Binding annoBinding = ((TypeElementImpl)a)._binding;
        if (0L != (annoBinding.getAnnotationTagBits() & 0x1000000000000L)) {
            HashSet<Element> annotatedElements = new HashSet<Element>(this._annoToUnit.getValues(a));
            ReferenceBinding annoTypeBinding = (ReferenceBinding)annoBinding;
            for (TypeElement element : ElementFilter.typesIn(this.getRootElements())) {
                ReferenceBinding typeBinding = (ReferenceBinding)((TypeElementImpl)element)._binding;
                this.addAnnotatedElements(annoTypeBinding, typeBinding, annotatedElements);
            }
            return Collections.unmodifiableSet(annotatedElements);
        }
        return Collections.unmodifiableSet(this._annoToUnit.getValues(a));
    }

    private void addAnnotatedElements(ReferenceBinding anno, ReferenceBinding type, Set<Element> result) {
        if (type.isClass() && this.inheritsAnno(type, anno)) {
            result.add(this._factory.newElement(type));
        }
        ReferenceBinding[] referenceBindingArray = type.memberTypes();
        int n = referenceBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            ReferenceBinding element = referenceBindingArray[n2];
            this.addAnnotatedElements(anno, element, result);
            ++n2;
        }
    }

    private boolean inheritsAnno(ReferenceBinding element, ReferenceBinding anno) {
        ReferenceBinding searchedElement = element;
        do {
            AnnotationBinding[] annos;
            if (searchedElement instanceof ParameterizedTypeBinding) {
                searchedElement = ((ParameterizedTypeBinding)searchedElement).genericType();
            }
            AnnotationBinding[] annotationBindingArray = annos = Factory.getPackedAnnotationBindings(searchedElement.getAnnotations());
            int n = annos.length;
            int n2 = 0;
            while (n2 < n) {
                AnnotationBinding annoBinding = annotationBindingArray[n2];
                if (annoBinding.getAnnotationType() == anno) {
                    return true;
                }
                ++n2;
            }
        } while ((searchedElement = searchedElement.superclass()) != null);
        return false;
    }

    @Override
    public Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> a) {
        String canonicalName = a.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Argument must represent an annotation type");
        }
        TypeElement annoType = this._processingEnv.getElementUtils().getTypeElement(canonicalName);
        if (annoType == null) {
            return Collections.emptySet();
        }
        return this.getElementsAnnotatedWith(annoType);
    }

    @Override
    public Set<? extends Element> getRootElements() {
        if (this._units == null) {
            return Collections.emptySet();
        }
        if (this._rootElements == null) {
            HashSet<Element> elements = new HashSet<Element>(this._units.length);
            Object[] objectArray = this._units;
            int n = this._units.length;
            int n2 = 0;
            while (n2 < n) {
                CompilationUnitDeclaration unit = objectArray[n2];
                if (unit.moduleDeclaration != null && unit.moduleDeclaration.binding != null) {
                    Element m = this._factory.newElement(unit.moduleDeclaration.binding);
                    elements.add(m);
                } else if (unit.scope != null && unit.scope.topLevelTypes != null) {
                    SourceTypeBinding[] sourceTypeBindingArray = unit.scope.topLevelTypes;
                    int n3 = unit.scope.topLevelTypes.length;
                    int n4 = 0;
                    while (n4 < n3) {
                        SourceTypeBinding binding = sourceTypeBindingArray[n4];
                        Element element = this._factory.newElement(binding);
                        if (element == null) {
                            throw new IllegalArgumentException("Top-level type binding could not be converted to element: " + binding);
                        }
                        elements.add(element);
                        ++n4;
                    }
                }
                ++n2;
            }
            if (this._binaryTypes != null) {
                objectArray = this._binaryTypes;
                n = this._binaryTypes.length;
                n2 = 0;
                while (n2 < n) {
                    Object typeBinding = objectArray[n2];
                    Element element = this._factory.newElement((Binding)typeBinding);
                    if (element == null) {
                        throw new IllegalArgumentException("Top-level type binding could not be converted to element: " + typeBinding);
                    }
                    elements.add(element);
                    ModuleBinding binding = ((ReferenceBinding)typeBinding).module();
                    if (binding != null) {
                        Element m = this._factory.newElement(binding);
                        elements.add(m);
                    }
                    ++n2;
                }
            }
            this._rootElements = elements;
        }
        return this._rootElements;
    }

    @Override
    public boolean processingOver() {
        return this._isLastRound;
    }
}


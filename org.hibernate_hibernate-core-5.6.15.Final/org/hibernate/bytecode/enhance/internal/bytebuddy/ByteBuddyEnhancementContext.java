/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.description.NamedElement
 *  net.bytebuddy.description.field.FieldDescription
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.description.method.MethodList
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.dynamic.scaffold.MethodGraph$Compiler
 *  net.bytebuddy.matcher.ElementMatcher$Junction
 *  net.bytebuddy.matcher.ElementMatchers
 */
package org.hibernate.bytecode.enhance.internal.bytebuddy;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.bytecode.enhance.internal.bytebuddy.EnhancerImpl;
import org.hibernate.bytecode.enhance.internal.bytebuddy.UnloadedTypeDescription;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;

class ByteBuddyEnhancementContext {
    private static final ElementMatcher.Junction<MethodDescription> IS_GETTER = ElementMatchers.isGetter();
    private final EnhancementContext enhancementContext;
    private final ConcurrentHashMap<TypeDescription, Map<String, MethodDescription>> getterByTypeMap = new ConcurrentHashMap();

    ByteBuddyEnhancementContext(EnhancementContext enhancementContext) {
        this.enhancementContext = enhancementContext;
    }

    public ClassLoader getLoadingClassLoader() {
        return this.enhancementContext.getLoadingClassLoader();
    }

    public boolean isEntityClass(TypeDescription classDescriptor) {
        return this.enhancementContext.isEntityClass(new UnloadedTypeDescription(classDescriptor));
    }

    public boolean isCompositeClass(TypeDescription classDescriptor) {
        return this.enhancementContext.isCompositeClass(new UnloadedTypeDescription(classDescriptor));
    }

    public boolean isMappedSuperclassClass(TypeDescription classDescriptor) {
        return this.enhancementContext.isMappedSuperclassClass(new UnloadedTypeDescription(classDescriptor));
    }

    public boolean doDirtyCheckingInline(TypeDescription classDescriptor) {
        return this.enhancementContext.doDirtyCheckingInline(new UnloadedTypeDescription(classDescriptor));
    }

    public boolean doExtendedEnhancement(TypeDescription classDescriptor) {
        return this.enhancementContext.doExtendedEnhancement(new UnloadedTypeDescription(classDescriptor));
    }

    public boolean hasLazyLoadableAttributes(TypeDescription classDescriptor) {
        return this.enhancementContext.hasLazyLoadableAttributes(new UnloadedTypeDescription(classDescriptor));
    }

    public boolean isPersistentField(EnhancerImpl.AnnotatedFieldDescription field) {
        return this.enhancementContext.isPersistentField(field);
    }

    public EnhancerImpl.AnnotatedFieldDescription[] order(EnhancerImpl.AnnotatedFieldDescription[] persistentFields) {
        return (EnhancerImpl.AnnotatedFieldDescription[])this.enhancementContext.order(persistentFields);
    }

    public boolean isLazyLoadable(EnhancerImpl.AnnotatedFieldDescription field) {
        return this.enhancementContext.isLazyLoadable(field);
    }

    public boolean isMappedCollection(EnhancerImpl.AnnotatedFieldDescription field) {
        return this.enhancementContext.isMappedCollection(field);
    }

    public boolean doBiDirectionalAssociationManagement(EnhancerImpl.AnnotatedFieldDescription field) {
        return this.enhancementContext.doBiDirectionalAssociationManagement(field);
    }

    Optional<MethodDescription> resolveGetter(FieldDescription fieldDescription) {
        Map getters = this.getterByTypeMap.computeIfAbsent(fieldDescription.getDeclaringType().asErasure(), declaringType -> ((MethodList)MethodGraph.Compiler.DEFAULT.compile(declaringType).listNodes().asMethodList().filter(IS_GETTER)).stream().collect(Collectors.toMap(NamedElement::getActualName, Function.identity())));
        String capitalizedFieldName = Character.toUpperCase(fieldDescription.getName().charAt(0)) + fieldDescription.getName().substring(1);
        MethodDescription getCandidate = (MethodDescription)getters.get("get" + capitalizedFieldName);
        MethodDescription isCandidate = (MethodDescription)getters.get("is" + capitalizedFieldName);
        if (getCandidate != null) {
            if (isCandidate != null) {
                return Optional.empty();
            }
            return Optional.of(getCandidate);
        }
        return Optional.ofNullable(isCandidate);
    }
}


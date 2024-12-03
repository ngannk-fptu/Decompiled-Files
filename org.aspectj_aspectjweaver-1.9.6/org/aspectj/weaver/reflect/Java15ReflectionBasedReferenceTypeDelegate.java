/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableReferenceType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.reflect.ArgNameFinder;
import org.aspectj.weaver.reflect.DeferredResolvedPointcutDefinition;
import org.aspectj.weaver.reflect.InternalUseOnlyPointcutParser;
import org.aspectj.weaver.reflect.Java15AnnotationFinder;
import org.aspectj.weaver.reflect.Java15GenericSignatureInformationProvider;
import org.aspectj.weaver.reflect.JavaLangTypeToResolvedTypeConverter;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegate;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegateFactory;
import org.aspectj.weaver.reflect.ReflectionBasedResolvedMemberImpl;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.PointcutParameter;

public class Java15ReflectionBasedReferenceTypeDelegate
extends ReflectionBasedReferenceTypeDelegate {
    private AjType<?> myType;
    private ResolvedType[] annotations;
    private ResolvedMember[] pointcuts;
    private ResolvedMember[] methods;
    private ResolvedMember[] fields;
    private TypeVariable[] typeVariables;
    private ResolvedType superclass;
    private ResolvedType[] superInterfaces;
    private String genericSignature = null;
    private JavaLangTypeToResolvedTypeConverter typeConverter;
    private Java15AnnotationFinder annotationFinder = null;
    private ArgNameFinder argNameFinder = null;

    public void initialize(ReferenceType aType, Class aClass, ClassLoader classLoader, World aWorld) {
        super.initialize(aType, aClass, classLoader, aWorld);
        this.myType = AjTypeSystem.getAjType(aClass);
        this.annotationFinder = new Java15AnnotationFinder();
        this.argNameFinder = this.annotationFinder;
        this.annotationFinder.setClassLoader(this.classLoaderReference.getClassLoader());
        this.annotationFinder.setWorld(aWorld);
        this.typeConverter = new JavaLangTypeToResolvedTypeConverter(aWorld);
    }

    @Override
    public ReferenceType buildGenericType() {
        return (ReferenceType)UnresolvedType.forGenericTypeVariables(this.getResolvedTypeX().getSignature(), this.getTypeVariables()).resolve(this.getWorld());
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        return super.getAnnotations();
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        if (this.annotations == null) {
            this.annotations = this.annotationFinder.getAnnotations(this.getBaseClass(), this.getWorld());
        }
        return this.annotations;
    }

    @Override
    public boolean hasAnnotations() {
        if (this.annotations == null) {
            this.annotations = this.annotationFinder.getAnnotations(this.getBaseClass(), this.getWorld());
        }
        return this.annotations.length != 0;
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        ResolvedType[] myAnns = this.getAnnotationTypes();
        ResolvedType toLookFor = ofType.resolve(this.getWorld());
        for (int i = 0; i < myAnns.length; ++i) {
            if (myAnns[i] != toLookFor) continue;
            return true;
        }
        return false;
    }

    @Override
    public ResolvedMember[] getDeclaredFields() {
        if (this.fields == null) {
            Field[] reflectFields = this.myType.getDeclaredFields();
            ResolvedMember[] rFields = new ResolvedMember[reflectFields.length];
            for (int i = 0; i < reflectFields.length; ++i) {
                rFields[i] = this.createGenericFieldMember(reflectFields[i]);
            }
            this.fields = rFields;
        }
        return this.fields;
    }

    @Override
    public String getDeclaredGenericSignature() {
        if (this.genericSignature != null || this.isGeneric()) {
            // empty if block
        }
        return this.genericSignature;
    }

    @Override
    public ResolvedType[] getDeclaredInterfaces() {
        if (this.superInterfaces == null) {
            Type[] genericInterfaces = this.getBaseClass().getGenericInterfaces();
            this.superInterfaces = this.typeConverter.fromTypes(genericInterfaces);
        }
        return this.superInterfaces;
    }

    @Override
    public ResolvedType getSuperclass() {
        if (this.superclass == null && this.getBaseClass() != Object.class) {
            Type t = this.getBaseClass().getGenericSuperclass();
            if (t != null) {
                this.superclass = this.typeConverter.fromType(t);
            }
            if (t == null) {
                this.superclass = this.getWorld().resolve(UnresolvedType.OBJECT);
            }
        }
        return this.superclass;
    }

    @Override
    public TypeVariable[] getTypeVariables() {
        TypeVariable[] workInProgressSetOfVariables = this.getResolvedTypeX().getWorld().getTypeVariablesCurrentlyBeingProcessed(this.getBaseClass());
        if (workInProgressSetOfVariables != null) {
            return workInProgressSetOfVariables;
        }
        if (this.typeVariables == null) {
            int i;
            java.lang.reflect.TypeVariable<Class<T>>[] tVars = this.getBaseClass().getTypeParameters();
            TypeVariable[] rTypeVariables = new TypeVariable[tVars.length];
            for (i = 0; i < tVars.length; ++i) {
                rTypeVariables[i] = new TypeVariable(tVars[i].getName());
            }
            this.getResolvedTypeX().getWorld().recordTypeVariablesCurrentlyBeingProcessed(this.getBaseClass(), rTypeVariables);
            for (i = 0; i < tVars.length; ++i) {
                TypeVariableReferenceType tvrt = (TypeVariableReferenceType)this.typeConverter.fromType(tVars[i]);
                TypeVariable tv = tvrt.getTypeVariable();
                rTypeVariables[i].setSuperclass(tv.getSuperclass());
                rTypeVariables[i].setAdditionalInterfaceBounds(tv.getSuperInterfaces());
                rTypeVariables[i].setDeclaringElement(tv.getDeclaringElement());
                rTypeVariables[i].setDeclaringElementKind(tv.getDeclaringElementKind());
                rTypeVariables[i].setRank(tv.getRank());
            }
            this.typeVariables = rTypeVariables;
            this.getResolvedTypeX().getWorld().forgetTypeVariablesCurrentlyBeingProcessed(this.getBaseClass());
        }
        return this.typeVariables;
    }

    @Override
    public ResolvedMember[] getDeclaredMethods() {
        if (this.methods == null) {
            int i;
            Method[] reflectMethods = this.myType.getDeclaredMethods();
            Constructor[] reflectCons = this.myType.getDeclaredConstructors();
            ResolvedMember[] rMethods = new ResolvedMember[reflectMethods.length + reflectCons.length];
            for (i = 0; i < reflectMethods.length; ++i) {
                rMethods[i] = this.createGenericMethodMember(reflectMethods[i]);
            }
            for (i = 0; i < reflectCons.length; ++i) {
                rMethods[i + reflectMethods.length] = this.createGenericConstructorMember(reflectCons[i]);
            }
            this.methods = rMethods;
        }
        return this.methods;
    }

    public ResolvedType getGenericResolvedType() {
        ReferenceType rt = this.getResolvedTypeX();
        if (rt.isParameterizedType() || rt.isRawType()) {
            return ((ResolvedType)rt).getGenericType();
        }
        return rt;
    }

    private ResolvedMember createGenericMethodMember(Method forMethod) {
        ReflectionBasedResolvedMemberImpl ret = new ReflectionBasedResolvedMemberImpl(Member.METHOD, (UnresolvedType)this.getGenericResolvedType(), forMethod.getModifiers(), (UnresolvedType)this.typeConverter.fromType(forMethod.getReturnType()), forMethod.getName(), (UnresolvedType[])this.typeConverter.fromTypes(forMethod.getParameterTypes()), (UnresolvedType[])this.typeConverter.fromTypes(forMethod.getExceptionTypes()), forMethod);
        ret.setAnnotationFinder(this.annotationFinder);
        ret.setGenericSignatureInformationProvider(new Java15GenericSignatureInformationProvider(this.getWorld()));
        return ret;
    }

    private ResolvedMember createGenericConstructorMember(Constructor forConstructor) {
        ReflectionBasedResolvedMemberImpl ret = new ReflectionBasedResolvedMemberImpl(Member.METHOD, (UnresolvedType)this.getGenericResolvedType(), forConstructor.getModifiers(), UnresolvedType.VOID, "<init>", (UnresolvedType[])this.typeConverter.fromTypes(forConstructor.getParameterTypes()), (UnresolvedType[])this.typeConverter.fromTypes(forConstructor.getExceptionTypes()), forConstructor);
        ret.setAnnotationFinder(this.annotationFinder);
        ret.setGenericSignatureInformationProvider(new Java15GenericSignatureInformationProvider(this.getWorld()));
        return ret;
    }

    private ResolvedMember createGenericFieldMember(Field forField) {
        ReflectionBasedResolvedMemberImpl ret = new ReflectionBasedResolvedMemberImpl(Member.FIELD, (UnresolvedType)this.getGenericResolvedType(), forField.getModifiers(), (UnresolvedType)this.typeConverter.fromType(forField.getType()), forField.getName(), new UnresolvedType[0], forField);
        ret.setAnnotationFinder(this.annotationFinder);
        ret.setGenericSignatureInformationProvider(new Java15GenericSignatureInformationProvider(this.getWorld()));
        return ret;
    }

    @Override
    public ResolvedMember[] getDeclaredPointcuts() {
        if (this.pointcuts == null) {
            int i;
            org.aspectj.lang.reflect.Pointcut[] pcs = this.myType.getDeclaredPointcuts();
            this.pointcuts = new ResolvedMember[pcs.length];
            InternalUseOnlyPointcutParser parser = null;
            World world = this.getWorld();
            parser = world instanceof ReflectionWorld ? new InternalUseOnlyPointcutParser(this.classLoaderReference.getClassLoader(), (ReflectionWorld)this.getWorld()) : new InternalUseOnlyPointcutParser(this.classLoaderReference.getClassLoader());
            Set<PointcutDesignatorHandler> additionalPointcutHandlers = world.getRegisteredPointcutHandlers();
            for (PointcutDesignatorHandler handler : additionalPointcutHandlers) {
                parser.registerPointcutDesignatorHandler(handler);
            }
            for (int i2 = 0; i2 < pcs.length; ++i2) {
                AjType<?>[] ptypes = pcs[i2].getParameterTypes();
                UnresolvedType[] weaverPTypes = new UnresolvedType[ptypes.length];
                for (int j = 0; j < weaverPTypes.length; ++j) {
                    weaverPTypes[j] = this.typeConverter.fromType(ptypes[j].getJavaClass());
                }
                this.pointcuts[i2] = new DeferredResolvedPointcutDefinition(this.getResolvedTypeX(), pcs[i2].getModifiers(), pcs[i2].getName(), weaverPTypes);
            }
            PointcutParameter[][] parameters = new PointcutParameter[pcs.length][];
            for (i = 0; i < pcs.length; ++i) {
                AjType<?>[] ptypes = pcs[i].getParameterTypes();
                String[] pnames = pcs[i].getParameterNames();
                if (pnames.length != ptypes.length && ((pnames = this.tryToDiscoverParameterNames(pcs[i])) == null || pnames.length != ptypes.length)) {
                    throw new IllegalStateException("Required parameter names not available when parsing pointcut " + pcs[i].getName() + " in type " + this.getResolvedTypeX().getName());
                }
                parameters[i] = new PointcutParameter[ptypes.length];
                for (int j = 0; j < parameters[i].length; ++j) {
                    parameters[i][j] = parser.createPointcutParameter(pnames[j], ptypes[j].getJavaClass());
                }
                String pcExpr = pcs[i].getPointcutExpression().toString();
                Pointcut pc = parser.resolvePointcutExpression(pcExpr, this.getBaseClass(), parameters[i]);
                ((ResolvedPointcutDefinition)this.pointcuts[i]).setParameterNames(pnames);
                ((ResolvedPointcutDefinition)this.pointcuts[i]).setPointcut(pc);
            }
            for (i = 0; i < this.pointcuts.length; ++i) {
                ResolvedPointcutDefinition rpd = (ResolvedPointcutDefinition)this.pointcuts[i];
                rpd.setPointcut(parser.concretizePointcutExpression(rpd.getPointcut(), this.getBaseClass(), parameters[i]));
            }
        }
        return this.pointcuts;
    }

    private String[] tryToDiscoverParameterNames(org.aspectj.lang.reflect.Pointcut pcut) {
        Method[] ms;
        for (Method m : ms = pcut.getDeclaringType().getJavaClass().getDeclaredMethods()) {
            if (!m.getName().equals(pcut.getName())) continue;
            return this.argNameFinder.getParameterNames(m);
        }
        return null;
    }

    @Override
    public boolean isAnnotation() {
        return this.getBaseClass().isAnnotation();
    }

    @Override
    public boolean isAnnotationStyleAspect() {
        return this.getBaseClass().isAnnotationPresent(Aspect.class);
    }

    @Override
    public boolean isAnnotationWithRuntimeRetention() {
        if (!this.isAnnotation()) {
            return false;
        }
        if (this.getBaseClass().isAnnotationPresent(Retention.class)) {
            Retention retention = this.getBaseClass().getAnnotation(Retention.class);
            RetentionPolicy policy = retention.value();
            return policy == RetentionPolicy.RUNTIME;
        }
        return false;
    }

    @Override
    public boolean isAspect() {
        return this.myType.isAspect();
    }

    @Override
    public boolean isEnum() {
        return this.getBaseClass().isEnum();
    }

    @Override
    public boolean isGeneric() {
        return this.getBaseClass().getTypeParameters().length > 0;
    }

    @Override
    public boolean isAnonymous() {
        return this.myClass.isAnonymousClass();
    }

    @Override
    public boolean isNested() {
        return this.myClass.isMemberClass();
    }

    @Override
    public ResolvedType getOuterClass() {
        return ReflectionBasedReferenceTypeDelegateFactory.resolveTypeInWorld(this.myClass.getEnclosingClass(), this.world);
    }
}


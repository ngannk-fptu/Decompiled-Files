/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hibernate.annotations.common.Version;
import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.annotations.common.reflection.ClassLoaderDelegate;
import org.hibernate.annotations.common.reflection.ClassLoadingException;
import org.hibernate.annotations.common.reflection.MetadataProvider;
import org.hibernate.annotations.common.reflection.MetadataProviderInjector;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XMethod;
import org.hibernate.annotations.common.reflection.XPackage;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.annotations.common.reflection.java.JavaMetadataProvider;
import org.hibernate.annotations.common.reflection.java.JavaXAnnotatedElement;
import org.hibernate.annotations.common.reflection.java.JavaXArrayType;
import org.hibernate.annotations.common.reflection.java.JavaXClass;
import org.hibernate.annotations.common.reflection.java.JavaXCollectionType;
import org.hibernate.annotations.common.reflection.java.JavaXMethod;
import org.hibernate.annotations.common.reflection.java.JavaXPackage;
import org.hibernate.annotations.common.reflection.java.JavaXProperty;
import org.hibernate.annotations.common.reflection.java.JavaXSimpleType;
import org.hibernate.annotations.common.reflection.java.JavaXType;
import org.hibernate.annotations.common.reflection.java.TypeEnvironmentMap;
import org.hibernate.annotations.common.reflection.java.generics.IdentityTypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironmentFactory;
import org.hibernate.annotations.common.reflection.java.generics.TypeSwitch;
import org.hibernate.annotations.common.reflection.java.generics.TypeUtils;
import org.hibernate.annotations.common.util.ReflectHelper;
import org.hibernate.annotations.common.util.StandardClassLoaderDelegateImpl;
import org.hibernate.annotations.common.util.impl.LoggerFactory;

public final class JavaReflectionManager
implements ReflectionManager,
MetadataProviderInjector {
    private static final boolean METADATA_CACHE_DIAGNOSTICS = Boolean.getBoolean("org.hibernate.annotations.common.METADATA_CACHE_DIAGNOSTICS");
    private MetadataProvider metadataProvider;
    private ClassLoaderDelegate classLoaderDelegate = StandardClassLoaderDelegateImpl.INSTANCE;
    private final AtomicBoolean empty = new AtomicBoolean(true);
    private final TypeEnvironmentMap<Class, JavaXClass> xClasses = new TypeEnvironmentMap<Class, JavaXClass>(this::javaXClassConstruction);
    private Map<Package, JavaXPackage> packagesToXPackages;
    private final TypeEnvironmentMap<Member, JavaXProperty> xProperties = new TypeEnvironmentMap<Member, JavaXProperty>(this::javaXPropertyConstruction);
    private final TypeEnvironmentMap<Member, JavaXMethod> xMethods = new TypeEnvironmentMap<Member, JavaXMethod>(this::javaJavaXMethodConstruction);

    @Override
    public MetadataProvider getMetadataProvider() {
        if (this.metadataProvider == null) {
            this.setMetadataProvider(new JavaMetadataProvider());
        }
        return this.metadataProvider;
    }

    @Override
    public void setMetadataProvider(MetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    @Override
    public void injectClassLoaderDelegate(ClassLoaderDelegate delegate) {
        this.classLoaderDelegate = delegate == null ? StandardClassLoaderDelegateImpl.INSTANCE : delegate;
    }

    @Override
    public ClassLoaderDelegate getClassLoaderDelegate() {
        return this.classLoaderDelegate;
    }

    private JavaXClass javaXClassConstruction(Class classType, TypeEnvironment typeEnvironment) {
        this.used();
        return new JavaXClass(classType, typeEnvironment, this);
    }

    private JavaXProperty javaXPropertyConstruction(Member member, TypeEnvironment typeEnvironment) {
        this.used();
        return JavaXProperty.create(member, typeEnvironment, this);
    }

    private JavaXMethod javaJavaXMethodConstruction(Member member, TypeEnvironment typeEnvironment) {
        this.used();
        return JavaXMethod.create(member, typeEnvironment, this);
    }

    public XClass toXClass(Class clazz) {
        return this.toXClass(clazz, IdentityTypeEnvironment.INSTANCE);
    }

    @Override
    public Class toClass(XClass xClazz) {
        if (!(xClazz instanceof JavaXClass)) {
            throw new IllegalArgumentException("XClass not coming from this ReflectionManager implementation");
        }
        return (Class)((JavaXClass)xClazz).toAnnotatedElement();
    }

    @Override
    public Method toMethod(XMethod xMethod) {
        if (!(xMethod instanceof JavaXMethod)) {
            throw new IllegalArgumentException("XMethod not coming from this ReflectionManager implementation");
        }
        return (Method)((JavaXAnnotatedElement)((Object)xMethod)).toAnnotatedElement();
    }

    @Deprecated
    public XClass classForName(String name, Class caller) throws ClassNotFoundException {
        return this.toXClass(ReflectHelper.classForName(name, caller));
    }

    @Override
    public XClass classForName(String name) throws ClassLoadingException {
        return this.toXClass(this.getClassLoaderDelegate().classForName(name));
    }

    @Override
    public XPackage packageForName(String packageName) {
        return this.getXAnnotatedElement(this.getClassLoaderDelegate().classForName(packageName + ".package-info").getPackage());
    }

    XClass toXClass(Type t, final TypeEnvironment context) {
        return (XClass)new TypeSwitch<XClass>(){

            @Override
            public XClass caseClass(Class classType) {
                return (XClass)JavaReflectionManager.this.xClasses.getOrCompute(context, classType);
            }

            @Override
            public XClass caseParameterizedType(ParameterizedType parameterizedType) {
                return JavaReflectionManager.this.toXClass(parameterizedType.getRawType(), TypeEnvironmentFactory.getEnvironment(parameterizedType, context));
            }
        }.doSwitch(context.bind(t));
    }

    @Deprecated
    XPackage getXAnnotatedElement(Package pkg) {
        return this.toXPackage(pkg);
    }

    @Override
    public XPackage toXPackage(Package pkg) {
        Map<Package, JavaXPackage> packagesToXPackagesMap = this.getPackagesToXPackagesMap();
        JavaXPackage xPackage = packagesToXPackagesMap.get(pkg);
        if (xPackage == null) {
            xPackage = new JavaXPackage(pkg, this);
            this.used();
            packagesToXPackagesMap.put(pkg, xPackage);
        }
        return xPackage;
    }

    private Map<Package, JavaXPackage> getPackagesToXPackagesMap() {
        if (this.packagesToXPackages == null) {
            this.packagesToXPackages = new HashMap<Package, JavaXPackage>(8, 0.5f);
        }
        return this.packagesToXPackages;
    }

    XProperty getXProperty(Member member, TypeEnvironment context) {
        return this.xProperties.getOrCompute(context, member);
    }

    XMethod getXMethod(Member member, TypeEnvironment context) {
        return this.xMethods.getOrCompute(context, member);
    }

    TypeEnvironment getTypeEnvironment(Type t) {
        return (TypeEnvironment)new TypeSwitch<TypeEnvironment>(){

            @Override
            public TypeEnvironment caseClass(Class classType) {
                return TypeEnvironmentFactory.getEnvironment(classType);
            }

            @Override
            public TypeEnvironment caseParameterizedType(ParameterizedType parameterizedType) {
                return TypeEnvironmentFactory.getEnvironment(parameterizedType);
            }

            @Override
            public TypeEnvironment defaultCase(Type type) {
                return IdentityTypeEnvironment.INSTANCE;
            }
        }.doSwitch(t);
    }

    public JavaXType toXType(TypeEnvironment context, Type propType) {
        Type boundType = this.toApproximatingEnvironment(context).bind(propType);
        if (TypeUtils.isArray(boundType)) {
            return new JavaXArrayType(propType, context, this);
        }
        if (TypeUtils.isCollection(boundType)) {
            return new JavaXCollectionType(propType, context, this);
        }
        if (TypeUtils.isSimple(boundType)) {
            return new JavaXSimpleType(propType, context, this);
        }
        throw new IllegalArgumentException("No PropertyTypeExtractor available for type void ");
    }

    public boolean equals(XClass class1, Class class2) {
        if (class1 == null) {
            return class2 == null;
        }
        return ((JavaXClass)class1).toClass().equals(class2);
    }

    public TypeEnvironment toApproximatingEnvironment(TypeEnvironment context) {
        return TypeEnvironmentFactory.toApproximatingEnvironment(context);
    }

    @Override
    public AnnotationReader buildAnnotationReader(AnnotatedElement annotatedElement) {
        return this.getMetadataProvider().getAnnotationReader(annotatedElement);
    }

    @Override
    public Map getDefaults() {
        return this.getMetadataProvider().getDefaults();
    }

    @Override
    public void reset() {
        boolean wasEmpty = this.empty.getAndSet(true);
        if (!wasEmpty) {
            this.xClasses.clear();
            this.packagesToXPackages = null;
            this.xProperties.clear();
            this.xMethods.clear();
            if (METADATA_CACHE_DIAGNOSTICS) {
                new RuntimeException("Diagnostics message : Caches now empty").printStackTrace();
            }
        }
        if (this.metadataProvider != null) {
            this.metadataProvider.reset();
        }
    }

    private void used() {
        boolean wasEmpty = this.empty.getAndSet(false);
        if (wasEmpty && METADATA_CACHE_DIAGNOSTICS) {
            new RuntimeException("Diagnostics message : Caches now being used").printStackTrace();
        }
    }

    static {
        LoggerFactory.make(Version.class.getName()).version(Version.getVersionString());
    }
}


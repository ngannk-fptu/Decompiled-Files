/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.annotations.common.reflection.ClassLoaderDelegate;
import org.hibernate.annotations.common.reflection.ClassLoadingException;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XMethod;
import org.hibernate.annotations.common.reflection.XPackage;

public interface ReflectionManager {
    @Deprecated
    public void injectClassLoaderDelegate(ClassLoaderDelegate var1);

    @Deprecated
    public ClassLoaderDelegate getClassLoaderDelegate();

    public <T> XClass toXClass(Class<T> var1);

    public Class toClass(XClass var1);

    public Method toMethod(XMethod var1);

    @Deprecated
    public <T> XClass classForName(String var1, Class<T> var2) throws ClassNotFoundException;

    @Deprecated
    public XClass classForName(String var1) throws ClassLoadingException;

    @Deprecated
    public XPackage packageForName(String var1) throws ClassNotFoundException;

    public XPackage toXPackage(Package var1);

    public <T> boolean equals(XClass var1, Class<T> var2);

    public AnnotationReader buildAnnotationReader(AnnotatedElement var1);

    public Map getDefaults();

    default public void reset() {
    }
}


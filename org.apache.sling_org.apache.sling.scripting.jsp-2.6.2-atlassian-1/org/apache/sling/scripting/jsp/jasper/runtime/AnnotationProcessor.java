/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import java.lang.reflect.InvocationTargetException;
import javax.naming.NamingException;

public interface AnnotationProcessor {
    public void postConstruct(Object var1) throws IllegalAccessException, InvocationTargetException;

    public void preDestroy(Object var1) throws IllegalAccessException, InvocationTargetException;

    public void processAnnotations(Object var1) throws IllegalAccessException, InvocationTargetException, NamingException;
}


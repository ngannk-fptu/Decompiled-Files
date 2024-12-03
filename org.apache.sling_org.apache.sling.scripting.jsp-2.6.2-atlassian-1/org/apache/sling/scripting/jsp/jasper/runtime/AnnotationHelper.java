/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import java.lang.reflect.InvocationTargetException;
import javax.naming.NamingException;
import org.apache.sling.scripting.jsp.jasper.runtime.AnnotationProcessor;

public class AnnotationHelper {
    public static void postConstruct(AnnotationProcessor processor, Object instance) throws IllegalAccessException, InvocationTargetException, NamingException {
        if (processor != null) {
            processor.processAnnotations(instance);
            processor.postConstruct(instance);
        }
    }

    public static void preDestroy(AnnotationProcessor processor, Object instance) throws IllegalAccessException, InvocationTargetException {
        if (processor != null) {
            processor.preDestroy(instance);
        }
    }
}


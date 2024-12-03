/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import java.lang.reflect.Constructor;
import org.codehaus.groovy.reflection.GroovyClassValue;
import org.codehaus.groovy.reflection.GroovyClassValuePreJava7;

class GroovyClassValueFactory {
    private static final boolean USE_CLASSVALUE;
    private static final Constructor groovyClassValueConstructor;

    GroovyClassValueFactory() {
    }

    public static <T> GroovyClassValue<T> createGroovyClassValue(GroovyClassValue.ComputeValue<T> computeValue) {
        try {
            return (GroovyClassValue)groovyClassValueConstructor.newInstance(computeValue);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static {
        Class groovyClassValueClass;
        USE_CLASSVALUE = Boolean.valueOf(System.getProperty("groovy.use.classvalue", "IBM J9 VM".equals(System.getProperty("java.vm.name")) ? "true" : "false"));
        if (USE_CLASSVALUE) {
            try {
                Class.forName("java.lang.ClassValue");
                try {
                    groovyClassValueClass = Class.forName("org.codehaus.groovy.reflection.v7.GroovyClassValueJava7");
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            catch (ClassNotFoundException e) {
                groovyClassValueClass = GroovyClassValuePreJava7.class;
            }
        } else {
            groovyClassValueClass = GroovyClassValuePreJava7.class;
        }
        try {
            groovyClassValueConstructor = groovyClassValueClass.getConstructor(GroovyClassValue.ComputeValue.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


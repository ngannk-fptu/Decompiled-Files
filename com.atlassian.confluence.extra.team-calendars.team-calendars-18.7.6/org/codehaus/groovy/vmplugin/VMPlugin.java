/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin;

import java.lang.reflect.Method;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;

public interface VMPlugin {
    public void setAdditionalClassInformation(ClassNode var1);

    public Class[] getPluginDefaultGroovyMethods();

    public Class[] getPluginStaticGroovyMethods();

    public void configureAnnotation(AnnotationNode var1);

    public void configureClassNode(CompileUnit var1, ClassNode var2);

    public void invalidateCallSites();

    public Object getInvokeSpecialHandle(Method var1, Object var2);

    public Object invokeHandle(Object var1, Object[] var2) throws Throwable;

    public int getVersion();
}


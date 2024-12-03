/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaMethod;
import groovy.lang.MetaObjectProtocol;
import groovy.lang.MetaProperty;
import java.util.List;
import org.codehaus.groovy.ast.ClassNode;

public interface MetaClass
extends MetaObjectProtocol {
    public Object invokeMethod(Class var1, Object var2, String var3, Object[] var4, boolean var5, boolean var6);

    public Object getProperty(Class var1, Object var2, String var3, boolean var4, boolean var5);

    public void setProperty(Class var1, Object var2, String var3, Object var4, boolean var5, boolean var6);

    public Object invokeMissingMethod(Object var1, String var2, Object[] var3);

    public Object invokeMissingProperty(Object var1, String var2, Object var3, boolean var4);

    public Object getAttribute(Class var1, Object var2, String var3, boolean var4);

    public void setAttribute(Class var1, Object var2, String var3, Object var4, boolean var5, boolean var6);

    public void initialize();

    @Override
    public List<MetaProperty> getProperties();

    @Override
    public List<MetaMethod> getMethods();

    public ClassNode getClassNode();

    public List<MetaMethod> getMetaMethods();

    public int selectConstructorAndTransformArguments(int var1, Object[] var2);

    public MetaMethod pickMethod(String var1, Class[] var2);
}


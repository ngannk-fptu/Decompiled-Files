/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import java.util.List;

public interface MetaObjectProtocol {
    public List<MetaProperty> getProperties();

    public List<MetaMethod> getMethods();

    public List<MetaMethod> respondsTo(Object var1, String var2, Object[] var3);

    public List<MetaMethod> respondsTo(Object var1, String var2);

    public MetaProperty hasProperty(Object var1, String var2);

    public MetaProperty getMetaProperty(String var1);

    public MetaMethod getStaticMetaMethod(String var1, Object[] var2);

    public MetaMethod getMetaMethod(String var1, Object[] var2);

    public Class getTheClass();

    public Object invokeConstructor(Object[] var1);

    public Object invokeMethod(Object var1, String var2, Object[] var3);

    public Object invokeMethod(Object var1, String var2, Object var3);

    public Object invokeStaticMethod(Object var1, String var2, Object[] var3);

    public Object getProperty(Object var1, String var2);

    public void setProperty(Object var1, String var2, Object var3);

    public Object getAttribute(Object var1, String var2);

    public void setAttribute(Object var1, String var2, Object var3);
}


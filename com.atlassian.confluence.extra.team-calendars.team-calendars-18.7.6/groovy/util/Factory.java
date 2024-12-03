/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;
import java.util.Map;

public interface Factory {
    public boolean isLeaf();

    public boolean isHandlesNodeChildren();

    public void onFactoryRegistration(FactoryBuilderSupport var1, String var2, String var3);

    public Object newInstance(FactoryBuilderSupport var1, Object var2, Object var3, Map var4) throws InstantiationException, IllegalAccessException;

    public boolean onHandleNodeAttributes(FactoryBuilderSupport var1, Object var2, Map var3);

    public boolean onNodeChildren(FactoryBuilderSupport var1, Object var2, Closure var3);

    public void onNodeCompleted(FactoryBuilderSupport var1, Object var2, Object var3);

    public void setParent(FactoryBuilderSupport var1, Object var2, Object var3);

    public void setChild(FactoryBuilderSupport var1, Object var2, Object var3);
}


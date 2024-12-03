/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;
import java.util.Map;

public abstract class AbstractFactory
implements Factory {
    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isHandlesNodeChildren() {
        return false;
    }

    @Override
    public void onFactoryRegistration(FactoryBuilderSupport builder, String registeredName, String group) {
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        return true;
    }

    @Override
    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        return true;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
    }

    @Override
    public void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    }

    @Override
    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
    }
}


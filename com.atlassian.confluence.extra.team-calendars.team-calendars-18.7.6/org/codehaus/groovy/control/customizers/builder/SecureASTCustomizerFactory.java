/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.customizers.builder;

import groovy.lang.Closure;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.util.Map;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

public class SecureASTCustomizerFactory
extends AbstractFactory {
    @Override
    public boolean isHandlesNodeChildren() {
        return true;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        return new SecureASTCustomizer();
    }

    @Override
    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        if (node instanceof SecureASTCustomizer) {
            Closure clone = (Closure)childContent.clone();
            clone.setDelegate(node);
            clone.setResolveStrategy(1);
            clone.call();
        }
        return false;
    }
}


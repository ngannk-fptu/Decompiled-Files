/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.customizers.builder;

import groovy.lang.Closure;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.builder.PostCompletionFactory;
import org.codehaus.groovy.runtime.ProxyGeneratorAdapter;

public class InlinedASTCustomizerFactory
extends AbstractFactory
implements PostCompletionFactory {
    @Override
    public boolean isHandlesNodeChildren() {
        return true;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (attributes.isEmpty() || !attributes.containsKey("phase")) {
            throw new RuntimeException("You must specify a CompilePhase to run at, using the [phase] attribute");
        }
        HashMap result = new HashMap(1 + attributes.size());
        result.putAll(attributes);
        return result;
    }

    @Override
    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        if (node instanceof Map) {
            ((Map)node).put("call", childContent.clone());
        }
        return false;
    }

    @Override
    public Object postCompleteNode(FactoryBuilderSupport factory, Object parent, Object node) {
        if (node instanceof Map) {
            Map map;
            ProxyGeneratorAdapter adapter = new ProxyGeneratorAdapter(map, (map = (Map)node).containsKey("superClass") ? (Class)map.get("superClass") : CompilationCustomizer.class, map.containsKey("interfaces") ? (Class[])map.get("interfaces") : null, this.getClass().getClassLoader(), false, null);
            Object phase = map.get("phase");
            if (!(phase instanceof CompilePhase)) {
                phase = CompilePhase.valueOf(phase.toString());
            }
            return adapter.proxy(map, phase);
        }
        return node;
    }
}


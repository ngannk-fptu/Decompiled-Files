/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.customizers.builder;

import groovy.lang.Closure;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.SourceAwareCustomizer;
import org.codehaus.groovy.control.customizers.builder.PostCompletionFactory;

public class SourceAwareCustomizerFactory
extends AbstractFactory
implements PostCompletionFactory {
    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        SourceOptions data = new SourceOptions();
        if (value instanceof CompilationCustomizer) {
            data.delegate = (CompilationCustomizer)value;
        }
        return data;
    }

    @Override
    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof CompilationCustomizer && parent instanceof SourceOptions) {
            ((SourceOptions)parent).delegate = (CompilationCustomizer)child;
        }
    }

    @Override
    public Object postCompleteNode(FactoryBuilderSupport factory, Object parent, Object node) {
        SourceOptions data = (SourceOptions)node;
        SourceAwareCustomizer sourceAwareCustomizer = new SourceAwareCustomizer(data.delegate);
        if (data.extensionValidator != null && (data.extension != null || data.extensions != null)) {
            throw new RuntimeException("You must choose between an extension name validator or an explicit extension name");
        }
        if (data.basenameValidator != null && (data.basename != null || data.basenames != null)) {
            throw new RuntimeException("You must choose between an base name validator or an explicit base name");
        }
        SourceAwareCustomizerFactory.addExtensionValidator(sourceAwareCustomizer, data);
        SourceAwareCustomizerFactory.addBasenameValidator(sourceAwareCustomizer, data);
        if (data.unitValidator != null) {
            sourceAwareCustomizer.setSourceUnitValidator(data.unitValidator);
        }
        if (data.classValidator != null) {
            sourceAwareCustomizer.setClassValidator(data.classValidator);
        }
        return sourceAwareCustomizer;
    }

    private static void addExtensionValidator(SourceAwareCustomizer sourceAwareCustomizer, SourceOptions data) {
        Closure<Boolean> extensionValidator;
        LinkedList<String> extensions;
        LinkedList<String> linkedList = extensions = data.extensions != null ? data.extensions : new LinkedList<String>();
        if (data.extension != null) {
            extensions.add(data.extension);
        }
        if ((extensionValidator = data.extensionValidator) == null && !extensions.isEmpty()) {
            extensionValidator = new Closure<Boolean>((Object)sourceAwareCustomizer){

                @Override
                public Boolean call(Object arguments) {
                    return extensions.contains(arguments);
                }
            };
        }
        sourceAwareCustomizer.setExtensionValidator(extensionValidator);
    }

    private static void addBasenameValidator(SourceAwareCustomizer sourceAwareCustomizer, SourceOptions data) {
        Closure<Boolean> basenameValidator;
        LinkedList<String> basenames;
        LinkedList<String> linkedList = basenames = data.basenames != null ? data.basenames : new LinkedList<String>();
        if (data.basename != null) {
            basenames.add(data.basename);
        }
        if ((basenameValidator = data.basenameValidator) == null && !basenames.isEmpty()) {
            basenameValidator = new Closure<Boolean>((Object)sourceAwareCustomizer){

                @Override
                public Boolean call(Object arguments) {
                    return basenames.contains(arguments);
                }
            };
        }
        sourceAwareCustomizer.setBaseNameValidator(basenameValidator);
    }

    public static class SourceOptions {
        public CompilationCustomizer delegate;
        public Closure<Boolean> extensionValidator;
        public Closure<Boolean> unitValidator;
        public Closure<Boolean> basenameValidator;
        public Closure<Boolean> classValidator;
        public String extension;
        public String basename;
        public List<String> extensions;
        public List<String> basenames;
    }
}


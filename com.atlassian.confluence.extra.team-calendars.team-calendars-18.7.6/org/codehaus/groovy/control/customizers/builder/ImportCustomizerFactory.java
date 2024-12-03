/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.customizers.builder;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.util.Collection;
import java.util.Map;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

public class ImportCustomizerFactory
extends AbstractFactory {
    @Override
    public boolean isHandlesNodeChildren() {
        return true;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        ImportCustomizer customizer = new ImportCustomizer();
        this.addImport(customizer, value);
        return customizer;
    }

    private void addImport(ImportCustomizer customizer, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Collection) {
            for (Object e : (Collection)value) {
                this.addImport(customizer, e);
            }
        } else if (value instanceof String) {
            customizer.addImports((String)value);
        } else if (value instanceof Class) {
            customizer.addImports(((Class)value).getName());
        } else if (value instanceof GString) {
            customizer.addImports(value.toString());
        } else {
            throw new RuntimeException("Unsupported import value type [" + value + "]");
        }
    }

    @Override
    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        if (node instanceof ImportCustomizer) {
            Closure clone = (Closure)childContent.clone();
            clone.setDelegate(new ImportHelper((ImportCustomizer)node));
            clone.call();
        }
        return false;
    }

    private static final class ImportHelper {
        private final ImportCustomizer customizer;

        private ImportHelper(ImportCustomizer customizer) {
            this.customizer = customizer;
        }

        protected void normal(String ... names) {
            this.customizer.addImports(names);
        }

        protected void normal(Class ... classes) {
            for (Class aClass : classes) {
                this.customizer.addImports(aClass.getName());
            }
        }

        protected void alias(String alias, String name) {
            this.customizer.addImport(alias, name);
        }

        protected void alias(String alias, Class clazz) {
            this.customizer.addImport(alias, clazz.getName());
        }

        protected void star(String ... packages) {
            this.customizer.addStarImports(packages);
        }

        protected void staticStar(String ... classNames) {
            this.customizer.addStaticStars(classNames);
        }

        protected void staticStar(Class ... classes) {
            for (Class aClass : classes) {
                this.customizer.addStaticStars(aClass.getName());
            }
        }

        protected void staticMember(String name, String field) {
            this.customizer.addStaticImport(name, field);
        }

        protected void staticMember(String alias, String name, String field) {
            this.customizer.addStaticImport(alias, name, field);
        }
    }
}


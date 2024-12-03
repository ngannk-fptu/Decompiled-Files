/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml.streamingmarkupsupport;

import groovy.lang.Closure;
import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObjectSupport;
import groovy.xml.streamingmarkupsupport.Builder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseMarkupBuilder
extends Builder {
    public BaseMarkupBuilder(Map namespaceMethodMap) {
        super(namespaceMethodMap);
    }

    @Override
    public Object bind(Closure root) {
        return new Document(root, this.namespaceMethodMap);
    }

    private static class Document
    extends Builder.Built
    implements GroovyInterceptable {
        private Object out;
        private final Map pendingNamespaces = new HashMap();
        private final Map namespaces = new HashMap();
        private final Map specialProperties = new HashMap();
        private String prefix = "";

        public Document(Closure root, Map namespaceMethodMap) {
            super(root, namespaceMethodMap);
            this.namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
            this.namespaces.put("mkp", "http://www.codehaus.org/Groovy/markup/keywords");
            this.specialProperties.put("out", new OutputSink("out"){

                @Override
                public Object leftShift(Object value) {
                    return this.leftShift("yield", value);
                }
            });
            this.specialProperties.put("unescaped", new OutputSink("unescaped"){

                @Override
                public Object leftShift(Object value) {
                    return this.leftShift("yieldUnescaped", value);
                }
            });
            this.specialProperties.put("namespaces", new OutputSink("namespaces"){

                @Override
                public Object leftShift(Object value) {
                    return this.leftShift("declareNamespace", value);
                }
            });
            this.specialProperties.put("pi", new OutputSink("pi"){

                @Override
                public Object leftShift(Object value) {
                    return this.leftShift("pi", value);
                }
            });
            this.specialProperties.put("comment", new OutputSink("comment"){

                @Override
                public Object leftShift(Object value) {
                    return this.leftShift("comment", value);
                }
            });
        }

        @Override
        public Object invokeMethod(String name, Object args) {
            Object[] arguments = (Object[])args;
            Map attrs = Collections.EMPTY_MAP;
            Object body = null;
            for (int i = 0; i != arguments.length; ++i) {
                Object arg = arguments[i];
                if (arg instanceof Map) {
                    attrs = (Map)arg;
                    continue;
                }
                if (arg instanceof Closure) {
                    Closure c = (Closure)arg;
                    c.setDelegate(this);
                    body = c.asWritable();
                    continue;
                }
                body = arg;
            }
            Object uri = this.pendingNamespaces.containsKey(this.prefix) ? this.pendingNamespaces.get(this.prefix) : (this.namespaces.containsKey(this.prefix) ? this.namespaces.get(this.prefix) : ":");
            Object[] info = (Object[])this.namespaceSpecificTags.get(uri);
            Map tagMap = (Map)info[2];
            Closure defaultTagClosure = (Closure)info[0];
            String prefix = this.prefix;
            this.prefix = "";
            if (tagMap.containsKey(name)) {
                return ((Closure)tagMap.get(name)).call(this, this.pendingNamespaces, this.namespaces, this.namespaceSpecificTags, prefix, attrs, body, this.out);
            }
            return defaultTagClosure.call(name, this, this.pendingNamespaces, this.namespaces, this.namespaceSpecificTags, prefix, attrs, body, this.out);
        }

        @Override
        public Object getProperty(String property) {
            Object special = this.specialProperties.get(property);
            if (special == null) {
                this.prefix = property;
                return this;
            }
            return special;
        }

        @Override
        public void setProperty(String property, Object newValue) {
            if ("trigger".equals(property)) {
                this.out = newValue;
                this.root.call((Object)this);
            } else {
                super.setProperty(property, newValue);
            }
        }

        private abstract class OutputSink
        extends GroovyObjectSupport {
            private final String name;

            public OutputSink(String name) {
                this.name = name;
            }

            @Override
            public Object invokeMethod(String name, Object args) {
                Document.this.prefix = this.name;
                return Document.this.invokeMethod(name, args);
            }

            public abstract Object leftShift(Object var1);

            protected Object leftShift(String command, Object value) {
                Document.this.getProperty("mkp");
                Document.this.invokeMethod(command, new Object[]{value});
                return this;
            }
        }
    }
}


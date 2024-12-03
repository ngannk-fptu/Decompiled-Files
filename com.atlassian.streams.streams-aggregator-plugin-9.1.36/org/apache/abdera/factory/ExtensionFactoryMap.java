/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExtensionFactoryMap
implements ExtensionFactory {
    private final List<ExtensionFactory> factories;

    public ExtensionFactoryMap(List<ExtensionFactory> factories) {
        this.factories = Collections.synchronizedList(factories);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T extends Element> T getElementWrapper(Element internal) {
        if (internal == null) {
            return null;
        }
        Element t = null;
        List<ExtensionFactory> list = this.factories;
        synchronized (list) {
            for (ExtensionFactory factory : this.factories) {
                t = factory.getElementWrapper(internal);
                if (t == null || t == internal) continue;
                return (T)t;
            }
        }
        return (T)(t != null ? t : internal);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getNamespaces() {
        ArrayList<String> ns = new ArrayList<String>();
        List<ExtensionFactory> list = this.factories;
        synchronized (list) {
            for (ExtensionFactory factory : this.factories) {
                String[] namespaces;
                for (String uri : namespaces = factory.getNamespaces()) {
                    if (ns.contains(uri)) continue;
                    ns.add(uri);
                }
            }
        }
        return ns.toArray(new String[ns.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean handlesNamespace(String namespace) {
        List<ExtensionFactory> list = this.factories;
        synchronized (list) {
            for (ExtensionFactory factory : this.factories) {
                if (!factory.handlesNamespace(namespace)) continue;
                return true;
            }
        }
        return false;
    }

    public ExtensionFactoryMap addFactory(ExtensionFactory factory) {
        if (!this.factories.contains(factory)) {
            this.factories.add(factory);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T extends Base> String getMimeType(T base) {
        Element element = base instanceof Element ? (Element)base : ((Document)base).getRoot();
        String namespace = element.getQName().getNamespaceURI();
        List<ExtensionFactory> list = this.factories;
        synchronized (list) {
            for (ExtensionFactory factory : this.factories) {
                if (!factory.handlesNamespace(namespace)) continue;
                return factory.getMimeType(base);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] listExtensionFactories() {
        ArrayList<String> names = new ArrayList<String>();
        List<ExtensionFactory> list = this.factories;
        synchronized (list) {
            for (ExtensionFactory factory : this.factories) {
                String name = factory.getClass().getName();
                if (names.contains(name)) continue;
                names.add(name);
            }
        }
        return names.toArray(new String[names.size()]);
    }
}


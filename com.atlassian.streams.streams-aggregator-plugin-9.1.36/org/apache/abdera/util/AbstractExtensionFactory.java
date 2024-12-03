/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractExtensionFactory
implements ExtensionFactory {
    private final List<String> namespaces = new ArrayList<String>();
    private final Map<QName, String> mimetypes = new HashMap<QName, String>();
    private final Map<QName, Class<? extends ElementWrapper>> impls = new HashMap<QName, Class<? extends ElementWrapper>>();

    protected AbstractExtensionFactory(String ... namespaces) {
        for (String ns : namespaces) {
            this.namespaces.add(ns);
        }
    }

    @Override
    public <T extends Element> T getElementWrapper(Element internal) {
        Element t = null;
        QName qname = internal.getQName();
        Class<? extends ElementWrapper> impl = this.impls.get(qname);
        if (impl != null) {
            try {
                t = impl.getConstructor(Element.class).newInstance(internal);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        return (T)(t != null ? t : internal);
    }

    protected AbstractExtensionFactory addMimeType(QName qname, String mimetype) {
        this.mimetypes.put(qname, mimetype);
        return this;
    }

    protected AbstractExtensionFactory addImpl(QName qname, Class<? extends ElementWrapper> impl) {
        this.impls.put(qname, impl);
        return this;
    }

    @Override
    public <T extends Base> String getMimeType(T base) {
        Element element = base instanceof Element ? (Element)base : (base instanceof Document ? ((Document)base).getRoot() : null);
        QName qname = element != null ? element.getQName() : null;
        return element != null && qname != null ? this.mimetypes.get(qname) : null;
    }

    @Override
    public String[] getNamespaces() {
        return this.namespaces.toArray(new String[this.namespaces.size()]);
    }

    @Override
    public boolean handlesNamespace(String namespace) {
        return this.namespaces.contains(namespace);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import org.aspectj.weaver.bcel.BcelWeakClassLoaderReference;
import org.aspectj.weaver.loadtime.ClassLoaderWeavingAdaptor;
import org.aspectj.weaver.loadtime.IWeavingContext;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;
import org.aspectj.weaver.tools.WeavingAdaptor;

public class DefaultWeavingContext
implements IWeavingContext {
    protected BcelWeakClassLoaderReference loaderRef;
    private String shortName;
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(DefaultWeavingContext.class);

    public DefaultWeavingContext(ClassLoader loader) {
        this.loaderRef = new BcelWeakClassLoaderReference(loader);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return this.getClassLoader().getResources(name);
    }

    @Override
    public String getBundleIdFromURL(URL url) {
        return "";
    }

    @Override
    public String getClassLoaderName() {
        ClassLoader loader = this.getClassLoader();
        return loader != null ? loader.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(loader)) : "null";
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.loaderRef.getClassLoader();
    }

    @Override
    public String getFile(URL url) {
        return url.getFile();
    }

    @Override
    public String getId() {
        if (this.shortName == null) {
            this.shortName = this.getClassLoaderName().replace('$', '.');
            int index = this.shortName.lastIndexOf(".");
            if (index != -1) {
                this.shortName = this.shortName.substring(index + 1);
            }
        }
        return this.shortName;
    }

    public String getSuffix() {
        return this.getClassLoaderName();
    }

    @Override
    public boolean isLocallyDefined(String classname) {
        URL parentURL;
        String asResource = classname.replace('.', '/').concat(".class");
        ClassLoader loader = this.getClassLoader();
        URL localURL = loader.getResource(asResource);
        if (localURL == null) {
            return false;
        }
        boolean isLocallyDefined = true;
        ClassLoader parent = loader.getParent();
        if (parent != null && localURL.equals(parentURL = parent.getResource(asResource))) {
            isLocallyDefined = false;
        }
        return isLocallyDefined;
    }

    @Override
    public List<Definition> getDefinitions(ClassLoader loader, WeavingAdaptor adaptor) {
        if (trace.isTraceEnabled()) {
            trace.enter("getDefinitions", (Object)this, new Object[]{"goo", adaptor});
        }
        List<Definition> definitions = ((ClassLoaderWeavingAdaptor)adaptor).parseDefinitions(loader);
        if (trace.isTraceEnabled()) {
            trace.exit("getDefinitions", definitions);
        }
        return definitions;
    }
}


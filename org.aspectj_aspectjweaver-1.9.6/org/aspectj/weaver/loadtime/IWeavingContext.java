/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.tools.WeavingAdaptor;

public interface IWeavingContext {
    public Enumeration<URL> getResources(String var1) throws IOException;

    public String getBundleIdFromURL(URL var1);

    public String getClassLoaderName();

    public ClassLoader getClassLoader();

    public String getFile(URL var1);

    public String getId();

    public boolean isLocallyDefined(String var1);

    public List<Definition> getDefinitions(ClassLoader var1, WeavingAdaptor var2);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io;

import com.rometools.rome.feed.module.Module;
import java.util.Set;
import org.jdom2.Element;
import org.jdom2.Namespace;

public interface ModuleGenerator {
    public String getNamespaceUri();

    public Set<Namespace> getNamespaces();

    public void generate(Module var1, Element var2);
}


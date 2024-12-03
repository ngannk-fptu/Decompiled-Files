/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Element
 */
package com.sun.syndication.io;

import com.sun.syndication.feed.module.Module;
import java.util.Set;
import org.jdom.Element;

public interface ModuleGenerator {
    public String getNamespaceUri();

    public Set getNamespaces();

    public void generate(Module var1, Element var2);
}


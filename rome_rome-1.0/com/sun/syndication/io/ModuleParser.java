/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Element
 */
package com.sun.syndication.io;

import com.sun.syndication.feed.module.Module;
import org.jdom.Element;

public interface ModuleParser {
    public String getNamespaceUri();

    public Module parse(Element var1);
}


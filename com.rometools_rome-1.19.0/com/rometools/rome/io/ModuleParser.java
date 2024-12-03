/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Element
 */
package com.rometools.rome.io;

import com.rometools.rome.feed.module.Module;
import java.util.Locale;
import org.jdom2.Element;

public interface ModuleParser {
    public String getNamespaceUri();

    public Module parse(Element var1, Locale var2);
}


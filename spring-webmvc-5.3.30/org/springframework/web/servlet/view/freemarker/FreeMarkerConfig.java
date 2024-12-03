/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.ext.jsp.TaglibFactory
 *  freemarker.template.Configuration
 */
package org.springframework.web.servlet.view.freemarker;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.Configuration;

public interface FreeMarkerConfig {
    public Configuration getConfiguration();

    public TaglibFactory getTaglibFactory();
}


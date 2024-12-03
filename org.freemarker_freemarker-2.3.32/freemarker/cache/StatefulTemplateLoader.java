/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateLoader;

public interface StatefulTemplateLoader
extends TemplateLoader {
    public void resetState();
}


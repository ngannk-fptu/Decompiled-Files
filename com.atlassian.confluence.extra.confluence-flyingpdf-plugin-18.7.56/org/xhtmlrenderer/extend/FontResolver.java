/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.extend;

import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.FSFont;

public interface FontResolver {
    public FSFont resolveFont(SharedContext var1, FontSpecification var2);

    public void flushCache();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view.i18n;

import java.util.Locale;
import org.apache.velocity.tools.view.IncludeTool;
import org.apache.velocity.tools.view.ViewToolContext;

@Deprecated
public class MultiViewsTool
extends IncludeTool {
    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewToolContext) {
            this.configure((ViewToolContext)obj);
        }
    }

    @Deprecated
    public String findLocalizedResource(String name, Locale locale) {
        return this.find(name, locale);
    }

    @Deprecated
    public String findLocalizedResource(String name) {
        return this.find(name);
    }

    @Deprecated
    public String findLocalizedResource(String name, String language) {
        return this.find(name, language);
    }
}


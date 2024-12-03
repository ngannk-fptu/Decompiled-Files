/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Term;
import java.util.Set;

public interface SupportedCSS {
    public int getTotalProperties();

    public Set<String> getDefinedPropertyNames();

    public boolean isSupportedMedia(String var1);

    public boolean isSupportedCSSProperty(String var1);

    public CSSProperty getDefaultProperty(String var1);

    public Term<?> getDefaultValue(String var1);

    public String getRandomPropertyName();

    public int getOrdinal(String var1);

    public String getPropertyName(int var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.plugin;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.Resourced;
import com.atlassian.plugin.ScopeAware;
import java.util.Map;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public interface ModuleDescriptor<T>
extends ScopeAware,
Resourced {
    public String getCompleteKey();

    public String getPluginKey();

    public String getKey();

    public String getName();

    public String getDescription();

    public Class<T> getModuleClass();

    public T getModule();

    public void init(@Nonnull Plugin var1, @Nonnull Element var2);

    public boolean isEnabledByDefault();

    public boolean isSystemModule();

    public void destroy();

    public Float getMinJavaVersion();

    public boolean satisfiesMinJavaVersion();

    public Map<String, String> getParams();

    public String getI18nNameKey();

    public String getDescriptionKey();

    public Plugin getPlugin();

    public boolean equals(Object var1);

    public int hashCode();

    public boolean isEnabled();

    default public void setBroken() {
    }

    default public boolean isBroken() {
        return false;
    }

    default public String getDisplayName() {
        return this.getName() == null ? this.getKey() : this.getName();
    }
}


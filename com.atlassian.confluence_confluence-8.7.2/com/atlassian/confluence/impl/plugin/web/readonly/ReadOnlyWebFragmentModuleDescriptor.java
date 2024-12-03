/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLabel
 *  com.atlassian.plugin.web.model.WebParam
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyCondition;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyContextProvider;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebLabel;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebParam;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebLabel;
import com.atlassian.plugin.web.model.WebParam;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class ReadOnlyWebFragmentModuleDescriptor<T>
implements WebFragmentModuleDescriptor<T> {
    private final WebFragmentModuleDescriptor<T> delegate;

    public ReadOnlyWebFragmentModuleDescriptor(WebFragmentModuleDescriptor<T> delegate) {
        this.delegate = delegate;
    }

    public int getWeight() {
        return this.delegate.getWeight();
    }

    public WebLabel getWebLabel() {
        return GeneralUtil.applyIfNonNull(this.delegate.getWebLabel(), ReadOnlyWebLabel::new);
    }

    public WebLabel getLabel() {
        return this.getWebLabel();
    }

    public WebLabel getTooltip() {
        return GeneralUtil.applyIfNonNull(this.delegate.getTooltip(), ReadOnlyWebLabel::new);
    }

    public Condition getCondition() {
        return GeneralUtil.applyIfNonNull(this.delegate.getCondition(), ReadOnlyCondition::new);
    }

    public WebParam getWebParams() {
        return GeneralUtil.applyIfNonNull(this.delegate.getWebParams(), ReadOnlyWebParam::new);
    }

    public String getCompleteKey() {
        return this.delegate.getCompleteKey();
    }

    public String getPluginKey() {
        return this.delegate.getPluginKey();
    }

    public String getKey() {
        return this.delegate.getKey();
    }

    public String getName() {
        return this.delegate.getName();
    }

    public String getDescription() {
        return this.delegate.getDescription();
    }

    public Class<T> getModuleClass() {
        throw new UnsupportedOperationException();
    }

    public T getModule() {
        throw new UnsupportedOperationException();
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        throw new UnsupportedOperationException();
    }

    public boolean isEnabledByDefault() {
        return this.delegate.isEnabledByDefault();
    }

    public boolean isSystemModule() {
        return this.delegate.isSystemModule();
    }

    public void destroy() {
        throw new UnsupportedOperationException();
    }

    public Float getMinJavaVersion() {
        return this.delegate.getMinJavaVersion();
    }

    public boolean satisfiesMinJavaVersion() {
        return this.delegate.satisfiesMinJavaVersion();
    }

    public Map<String, String> getParams() {
        return this.delegate.getParams();
    }

    public String getI18nNameKey() {
        return this.delegate.getI18nNameKey();
    }

    public String getDescriptionKey() {
        return this.delegate.getDescriptionKey();
    }

    public Plugin getPlugin() {
        throw new UnsupportedOperationException();
    }

    public boolean isEnabled() {
        return this.delegate.isEnabled();
    }

    public void setBroken() {
        throw new UnsupportedOperationException();
    }

    public boolean isBroken() {
        return this.delegate.isBroken();
    }

    public String getDisplayName() {
        return this.delegate.getDisplayName();
    }

    public List<ResourceDescriptor> getResourceDescriptors() {
        return this.delegate.getResourceDescriptors();
    }

    public ResourceDescriptor getResourceDescriptor(String s, String s1) {
        return this.delegate.getResourceDescriptor(s, s1);
    }

    public ResourceLocation getResourceLocation(String s, String s1) {
        return this.delegate.getResourceLocation(s, s1);
    }

    public void enabled() {
        throw new UnsupportedOperationException();
    }

    public void disabled() {
        throw new UnsupportedOperationException();
    }

    public ContextProvider getContextProvider() {
        return GeneralUtil.applyIfNonNull(this.delegate.getContextProvider(), ReadOnlyContextProvider::new);
    }

    public Optional<String> getScopeKey() {
        return this.delegate.getScopeKey();
    }
}


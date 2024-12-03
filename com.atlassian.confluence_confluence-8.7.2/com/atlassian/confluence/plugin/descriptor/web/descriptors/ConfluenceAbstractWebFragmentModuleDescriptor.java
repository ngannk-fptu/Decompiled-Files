/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.ModuleDescriptors$EqualsBuilder
 *  com.atlassian.plugin.descriptors.ModuleDescriptors$HashCodeBuilder
 *  com.atlassian.plugin.web.descriptors.DefaultAbstractWebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLabel
 */
package com.atlassian.confluence.plugin.descriptor.web.descriptors;

import com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebLabel;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.ModuleDescriptors;
import com.atlassian.plugin.web.descriptors.DefaultAbstractWebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebLabel;

public abstract class ConfluenceAbstractWebFragmentModuleDescriptor
extends DefaultAbstractWebFragmentModuleDescriptor<Void> {
    private ConfluenceWebLabel webLabel;
    private ConfluenceWebLabel tooltipLabel;

    public ConfluenceAbstractWebFragmentModuleDescriptor(WebFragmentModuleDescriptor abstractDescriptor) {
        super(abstractDescriptor);
    }

    public void enabled() {
        WebLabel tooltip;
        super.enabled();
        WebLabel label = this.getDecoratedDescriptor().getWebLabel();
        if (label != null) {
            this.webLabel = new ConfluenceWebLabel(label);
        }
        if ((tooltip = this.getDecoratedDescriptor().getTooltip()) != null) {
            this.tooltipLabel = new ConfluenceWebLabel(tooltip);
        }
    }

    public void disabled() {
        this.webLabel = null;
        this.tooltipLabel = null;
        super.disabled();
    }

    public ConfluenceWebLabel getWebLabel() {
        return this.webLabel;
    }

    public ConfluenceWebLabel getLabel() {
        return this.getWebLabel();
    }

    public ConfluenceWebLabel getTooltip() {
        return this.tooltipLabel;
    }

    public boolean equals(Object obj) {
        return new ModuleDescriptors.EqualsBuilder().descriptor((ModuleDescriptor)this).isEqualTo(obj);
    }

    public int hashCode() {
        return new ModuleDescriptors.HashCodeBuilder().descriptor((ModuleDescriptor)this).hashCode();
    }
}


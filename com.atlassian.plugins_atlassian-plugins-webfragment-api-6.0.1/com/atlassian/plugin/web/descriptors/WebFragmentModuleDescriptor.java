/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.StateAware
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.descriptors.ConditionalDescriptor;
import com.atlassian.plugin.web.descriptors.ContextAware;
import com.atlassian.plugin.web.descriptors.WeightedDescriptor;
import com.atlassian.plugin.web.model.WebLabel;
import com.atlassian.plugin.web.model.WebParam;

public interface WebFragmentModuleDescriptor<T>
extends ModuleDescriptor<T>,
WeightedDescriptor,
StateAware,
ContextAware,
ConditionalDescriptor {
    @Override
    public int getWeight();

    public WebLabel getWebLabel();

    public WebLabel getTooltip();

    @Override
    public Condition getCondition();

    public WebParam getWebParams();
}


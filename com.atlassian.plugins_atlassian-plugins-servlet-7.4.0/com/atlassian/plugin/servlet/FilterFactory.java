/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor;
import com.atlassian.plugin.servlet.filter.DelegatingPluginFilter;
import javax.servlet.Filter;

class FilterFactory {
    FilterFactory() {
    }

    Filter newFilter(ServletFilterModuleDescriptor descriptor) {
        return new DelegatingPluginFilter(descriptor);
    }
}


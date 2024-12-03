/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 */
package com.atlassian.confluence.web;

import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebMenuSection {
    private final String id;
    private final String className;
    private final String label;
    private final String ariaLabel;
    private final List<WebItemModuleDescriptor> items = new ArrayList<WebItemModuleDescriptor>();

    public WebMenuSection(String id, String className, String label, String ariaLabel, List<? extends WebItemModuleDescriptor> items) {
        this.id = id;
        this.className = className;
        this.label = label;
        this.items.addAll(items);
        this.ariaLabel = ariaLabel;
    }

    public String getId() {
        return this.id;
    }

    public String getClassName() {
        return this.className;
    }

    public String getLabel() {
        return this.label;
    }

    public String getAriaLabel() {
        return this.ariaLabel;
    }

    public List<WebItemModuleDescriptor> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    public String toString() {
        return this.getClass().getName() + "[" + this.id + "]";
    }
}


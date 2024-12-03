/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 */
package com.atlassian.confluence.web;

import com.atlassian.confluence.web.WebMenuSection;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebMenu {
    private final String id;
    private final List<WebMenuSection> sections = new ArrayList<WebMenuSection>();

    public WebMenu(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void addSection(String sectionName, String label, String ariaLabel, List<? extends WebItemModuleDescriptor> items) {
        if (items.isEmpty()) {
            return;
        }
        String className = "section-" + sectionName;
        if (this.sections.isEmpty()) {
            className = className + " first";
        }
        WebMenuSection section = new WebMenuSection(this.id + "-" + sectionName, className, label, ariaLabel, items);
        this.sections.add(section);
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public List<WebMenuSection> getSections() {
        return Collections.unmodifiableList(this.sections);
    }

    public String toString() {
        return this.getClass().getName() + "[" + this.id + "]";
    }
}


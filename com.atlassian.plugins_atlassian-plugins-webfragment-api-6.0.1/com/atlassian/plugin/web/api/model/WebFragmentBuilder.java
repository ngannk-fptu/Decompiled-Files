/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.Assertions
 */
package com.atlassian.plugin.web.api.model;

import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.web.api.WebItem;
import com.atlassian.plugin.web.api.WebSection;
import com.atlassian.plugin.web.api.model.WebItemImpl;
import com.atlassian.plugin.web.api.model.WebSectionImpl;
import java.util.HashMap;
import java.util.Map;

public class WebFragmentBuilder {
    private String completeKey;
    private String label;
    private String title;
    private String styleClass;
    private String id;
    private Map<String, String> params;
    private final int weight;

    public WebFragmentBuilder(int weight) {
        this.weight = weight;
        this.params = new HashMap<String, String>(0);
    }

    public WebFragmentBuilder(String completeKey, int weight) {
        this.completeKey = completeKey;
        this.weight = weight;
        this.params = new HashMap<String, String>(0);
    }

    public WebFragmentBuilder label(String label) {
        this.label = label;
        return this;
    }

    public WebFragmentBuilder title(String title) {
        this.title = title;
        return this;
    }

    public WebFragmentBuilder styleClass(String styleClass) {
        this.styleClass = styleClass;
        return this;
    }

    public WebFragmentBuilder id(String id) {
        this.id = id;
        return this;
    }

    public WebFragmentBuilder params(Map<String, String> params) {
        this.params = new HashMap<String, String>(params);
        return this;
    }

    public WebFragmentBuilder addParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public WebItemBuilder webItem(String section) {
        return new WebItemBuilder(this, section);
    }

    public WebItemBuilder webItem(String section, String entryPoint) {
        WebItemBuilder webItemBuilder = new WebItemBuilder(this, section);
        return webItemBuilder.entryPoint(entryPoint);
    }

    public WebSectionBuilder webSection(String location) {
        return new WebSectionBuilder(this, location);
    }

    public static class WebSectionBuilder {
        private final WebFragmentBuilder fragmentBuilder;
        private final String location;

        public WebSectionBuilder(WebFragmentBuilder fragmentBuilder, String location) {
            this.fragmentBuilder = fragmentBuilder;
            this.location = location;
        }

        public WebSection build() {
            return new WebSectionImpl(this.fragmentBuilder.completeKey, this.fragmentBuilder.label, this.fragmentBuilder.title, this.fragmentBuilder.styleClass, this.fragmentBuilder.id, this.fragmentBuilder.params, this.fragmentBuilder.weight, (String)Assertions.notNull((String)"location", (Object)this.location));
        }
    }

    public static class WebItemBuilder {
        private final WebFragmentBuilder fragmentBuilder;
        private final String section;
        private String accessKey;
        private String entryPoint;
        private String url;

        public WebItemBuilder(WebFragmentBuilder fragmentBuilder, String section) {
            this.fragmentBuilder = fragmentBuilder;
            this.section = section;
        }

        public WebItemBuilder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public WebItemBuilder entryPoint(String entryPoint) {
            this.entryPoint = entryPoint;
            return this;
        }

        public WebItemBuilder url(String url) {
            this.url = url;
            return this;
        }

        public WebItem build() {
            return new WebItemImpl(this.fragmentBuilder.completeKey, this.fragmentBuilder.label, this.fragmentBuilder.title, this.fragmentBuilder.styleClass, this.fragmentBuilder.id, this.fragmentBuilder.params, this.fragmentBuilder.weight, (String)Assertions.notNull((String)"section", (Object)this.section), this.url, this.accessKey, this.entryPoint);
        }
    }
}


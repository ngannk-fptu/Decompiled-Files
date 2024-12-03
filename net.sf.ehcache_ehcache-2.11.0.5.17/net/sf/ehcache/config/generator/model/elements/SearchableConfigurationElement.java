/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;

public class SearchableConfigurationElement
extends SimpleNodeElement {
    private final Searchable searchable;

    public SearchableConfigurationElement(NodeElement parent, Searchable searchable) {
        super(parent, "searchable");
        if (searchable == null) {
            throw new NullPointerException();
        }
        this.searchable = searchable;
        this.init();
    }

    private void init() {
        for (SearchAttribute sa : this.searchable.getUserDefinedSearchAttributes().values()) {
            this.addChildElement(sa.asConfigElement(this));
        }
        this.addAttribute(new SimpleNodeAttribute("keys", this.searchable.keys()).optional(true).defaultValue(true));
        this.addAttribute(new SimpleNodeAttribute("values", this.searchable.values()).optional(true).defaultValue(true));
        this.addAttribute(new SimpleNodeAttribute("allowDynamicIndexing", this.searchable.isDynamicIndexingAllowed()).optional(true).defaultValue(false));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  org.dom4j.Element
 */
package com.atlassian.plugin;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.Resourced;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.util.Assertions;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.dom4j.Element;

public class Resources
implements Resourced {
    public static final Resources EMPTY_RESOURCES = new Resources(null);
    private final List<ResourceDescriptor> resourceDescriptors;

    public static Resources fromXml(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot parse resources from null XML element");
        }
        List elements = element.elements("resource");
        HashSet<ResourceDescriptor> templates = new HashSet<ResourceDescriptor>();
        for (Element e : elements) {
            ResourceDescriptor resourceDescriptor = new ResourceDescriptor(e);
            if (templates.contains(resourceDescriptor)) {
                throw new PluginParseException("Duplicate resource with type '" + resourceDescriptor.getType() + "' and name '" + resourceDescriptor.getName() + "' found");
            }
            templates.add(resourceDescriptor);
        }
        return new Resources(element);
    }

    private Resources(Element element) {
        this.resourceDescriptors = element != null ? element.elements("resource").stream().map(ResourceDescriptor::new).collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public List<ResourceDescriptor> getResourceDescriptors() {
        return this.resourceDescriptors;
    }

    @Override
    public ResourceLocation getResourceLocation(String type, String name) {
        for (ResourceDescriptor resourceDescriptor : this.getResourceDescriptors()) {
            if (!resourceDescriptor.doesTypeAndNameMatch(type, name)) continue;
            return resourceDescriptor.getResourceLocationForName(name);
        }
        return null;
    }

    @Override
    public ResourceDescriptor getResourceDescriptor(String type, String name) {
        for (ResourceDescriptor resourceDescriptor : this.getResourceDescriptors()) {
            if (!resourceDescriptor.getType().equalsIgnoreCase(type) || !resourceDescriptor.getName().equalsIgnoreCase(name)) continue;
            return resourceDescriptor;
        }
        return null;
    }

    public static class TypeFilterPredicate
    implements Predicate<ResourceDescriptor> {
        private final String type;

        public TypeFilterPredicate(String type) {
            this.type = Assertions.notNull("type", type);
        }

        @Override
        public boolean test(ResourceDescriptor input) {
            return this.type.equals(input.getType());
        }
    }

    @Deprecated
    public static class TypeFilter
    implements com.google.common.base.Predicate<ResourceDescriptor> {
        private final String type;

        public TypeFilter(String type) {
            this.type = Assertions.notNull("type", type);
        }

        public boolean apply(ResourceDescriptor input) {
            return this.type.equals(input.getType());
        }
    }
}


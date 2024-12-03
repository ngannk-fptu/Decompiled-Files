/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.generator.model.NodeAttribute;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.NodeElementVisitor;
import net.sf.ehcache.config.generator.model.elements.FactoryConfigurationElement;

public abstract class AbstractNodeElement
implements NodeElement {
    protected final List<NodeAttribute> attributes = new ArrayList<NodeAttribute>();
    protected final List<NodeElement> children = new ArrayList<NodeElement>();
    protected NodeElement parent;
    protected boolean optional;
    protected String innerContent;

    public AbstractNodeElement(NodeElement parent) {
        this.parent = parent;
    }

    @Override
    public abstract String getName();

    @Override
    public NodeElement getParent() {
        return this.parent;
    }

    @Override
    public List<NodeAttribute> getAttributes() {
        return this.attributes;
    }

    @Override
    public List<NodeElement> getChildElements() {
        return this.children;
    }

    @Override
    public void addAttribute(NodeAttribute attribute) {
        if (attribute == null) {
            return;
        }
        this.attributes.add(attribute);
    }

    @Override
    public void addChildElement(NodeElement childElement) {
        if (childElement == null) {
            return;
        }
        this.children.add(childElement);
    }

    @Override
    public boolean isOptional() {
        return this.optional;
    }

    @Override
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    @Override
    public String getInnerContent() {
        return this.innerContent;
    }

    @Override
    public void setInnerContent(String content) {
        this.innerContent = content;
    }

    public static void addAllFactoryConfigsAsChildElements(NodeElement element, String name, Collection<? extends FactoryConfiguration> factoryConfigurations) {
        if (factoryConfigurations == null || factoryConfigurations.size() == 0) {
            return;
        }
        for (NodeElement nodeElement : AbstractNodeElement.getAllFactoryElements(element, name, factoryConfigurations)) {
            element.addChildElement(nodeElement);
        }
    }

    public static List<FactoryConfigurationElement> getAllFactoryElements(NodeElement parent, String name, Collection factoryConfigurations1) {
        Collection factoryConfigurations = factoryConfigurations1;
        ArrayList<FactoryConfigurationElement> elements = new ArrayList<FactoryConfigurationElement>();
        for (FactoryConfiguration config : factoryConfigurations) {
            elements.add(new FactoryConfigurationElement(parent, name, config));
        }
        return elements;
    }

    @Override
    public String getFQName() {
        return AbstractNodeElement.getFQName(this, ".");
    }

    @Override
    public String getFQName(String delimiter) {
        return AbstractNodeElement.getFQName(this, delimiter);
    }

    private static String getFQName(NodeElement element, String delimiter) {
        LinkedList<NodeElement> hierarchy = new LinkedList<NodeElement>();
        for (NodeElement curr = element; curr != null; curr = curr.getParent()) {
            hierarchy.addFirst(curr);
        }
        StringBuilder sb = new StringBuilder();
        while (!hierarchy.isEmpty()) {
            sb.append(((NodeElement)hierarchy.removeFirst()).getName());
            if (hierarchy.isEmpty()) continue;
            sb.append(delimiter);
        }
        return sb.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.getFQName() == null ? 0 : this.getFQName().hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NodeElement)) {
            return false;
        }
        NodeElement other = (NodeElement)obj;
        return !(this.getFQName() == null ? other.getFQName() != null : !this.getFQName().equals(other.getFQName()));
    }

    public String toString() {
        return "AbstractElement [FQName=" + this.getFQName() + "]";
    }

    @Override
    public void accept(NodeElementVisitor visitor) {
        visitor.visit(this);
    }
}


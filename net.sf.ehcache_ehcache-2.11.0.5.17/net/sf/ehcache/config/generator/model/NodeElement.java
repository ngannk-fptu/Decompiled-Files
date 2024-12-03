/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model;

import java.util.List;
import net.sf.ehcache.config.generator.model.NodeAttribute;
import net.sf.ehcache.config.generator.model.NodeElementVisitor;

public interface NodeElement {
    public String getName();

    public String getFQName();

    public String getFQName(String var1);

    public List<NodeAttribute> getAttributes();

    public NodeElement getParent();

    public List<NodeElement> getChildElements();

    public boolean hasChildren();

    public String getInnerContent();

    public void addAttribute(NodeAttribute var1);

    public void addChildElement(NodeElement var1);

    public void accept(NodeElementVisitor var1);

    public boolean isOptional();

    public void setOptional(boolean var1);

    public void setInnerContent(String var1);
}


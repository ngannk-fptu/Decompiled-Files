/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.velocity.tools.config.Configuration;

public class CompoundConfiguration<C extends Configuration>
extends Configuration {
    private final SortedSet<C> children = new TreeSet<C>();

    protected void addChild(C newKid) {
        C child = this.getChild(newKid);
        if (child != null) {
            if (child instanceof CompoundConfiguration) {
                ((CompoundConfiguration)child).addConfiguration((CompoundConfiguration)newKid);
            } else {
                ((Configuration)child).addConfiguration((Configuration)newKid);
            }
        } else {
            this.children.add(newKid);
        }
    }

    protected boolean removeChild(C config) {
        return this.children.remove(config);
    }

    protected boolean hasChildren() {
        return !this.children.isEmpty();
    }

    protected Collection<C> getChildren() {
        return this.children;
    }

    protected void setChildren(Collection<C> kids) {
        for (Configuration kid : kids) {
            this.addChild(kid);
        }
    }

    protected C getChild(C kid) {
        for (Configuration child : this.children) {
            if (!child.equals(kid)) continue;
            return (C)child;
        }
        return null;
    }

    public void addConfiguration(CompoundConfiguration<C> config) {
        this.setChildren(config.getChildren());
        super.addConfiguration(config);
    }

    @Override
    public void validate() {
        super.validate();
        for (Configuration child : this.children) {
            child.validate();
        }
    }

    protected void appendChildren(StringBuilder out, String childrenName, String childDelim) {
        if (this.hasChildren()) {
            if (this.hasProperties()) {
                out.append(" and ");
            } else {
                out.append(" with ");
            }
            out.append(this.children.size());
            out.append(' ');
            out.append(childrenName);
            for (Configuration child : this.children) {
                out.append(child);
                out.append(childDelim);
            }
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.children.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CompoundConfiguration) || !super.equals(obj)) {
            return false;
        }
        CompoundConfiguration that = (CompoundConfiguration)obj;
        return this.children.equals(that.children);
    }
}


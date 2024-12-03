/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.jxpath.ri.QName
 *  org.apache.commons.jxpath.ri.model.NodeIterator
 *  org.apache.commons.jxpath.ri.model.NodePointer
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.tree.xpath;

import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodePointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.lang3.StringUtils;

abstract class ConfigurationNodeIteratorBase<T>
implements NodeIterator {
    private static final String PREFIX_SEPARATOR = ":";
    private static final String FMT_NAMESPACE = "%s:%s";
    private final ConfigurationNodePointer<T> parent;
    private int position;
    private int startOffset;
    private final boolean reverse;

    protected ConfigurationNodeIteratorBase(ConfigurationNodePointer<T> parent, boolean reverse) {
        this.parent = parent;
        this.reverse = reverse;
    }

    public int getPosition() {
        return this.position;
    }

    public boolean setPosition(int pos) {
        this.position = pos;
        return pos >= 1 && pos <= this.getMaxPosition();
    }

    public NodePointer getNodePointer() {
        if (this.getPosition() < 1 && !this.setPosition(1)) {
            return null;
        }
        return this.createNodePointer(this.positionToIndex(this.getPosition()));
    }

    protected ConfigurationNodePointer<T> getParent() {
        return this.parent;
    }

    protected NodeHandler<T> getNodeHandler() {
        return this.getParent().getNodeHandler();
    }

    protected int getStartOffset() {
        return this.startOffset;
    }

    protected void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
        this.startOffset = this.reverse ? --this.startOffset : ++this.startOffset;
    }

    protected int getMaxPosition() {
        return this.reverse ? this.getStartOffset() + 1 : this.size() - this.getStartOffset();
    }

    protected int positionToIndex(int pos) {
        return (this.reverse ? 1 - pos : pos - 1) + this.getStartOffset();
    }

    protected abstract NodePointer createNodePointer(int var1);

    protected abstract int size();

    protected static String prefixName(String prefix, String name) {
        return String.format(FMT_NAMESPACE, prefix, StringUtils.defaultString((String)name));
    }

    protected static String qualifiedName(QName name) {
        return name.getPrefix() == null ? name.getName() : ConfigurationNodeIteratorBase.prefixName(name.getPrefix(), name.getName());
    }
}


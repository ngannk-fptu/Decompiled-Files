/*
 * Decompiled with CFR 0.152.
 */
package net.customware.confluence.plugin.toc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import net.customware.confluence.plugin.toc.DepthFirstDocumentOutlineBuilder;
import net.customware.confluence.plugin.toc.DocumentOutline;
import net.customware.confluence.plugin.toc.DocumentOutlineImpl;

public class DefaultDocumentOutlineBuilder
implements DepthFirstDocumentOutlineBuilder {
    protected List<BuildableHeading> topOfOutline = new ArrayList<BuildableHeading>();
    private Stack<BuildableHeading> ancestors = new Stack();

    @Override
    public DepthFirstDocumentOutlineBuilder add(String name, String anchor, int type) {
        BuildableHeading heading = new BuildableHeading(name, anchor, type);
        if (this.ancestors.empty()) {
            this.topOfOutline.add(heading);
        } else {
            this.ancestors.peek().addChild(heading);
        }
        return this;
    }

    @Override
    public DepthFirstDocumentOutlineBuilder nextLevel() {
        if (this.ancestors.isEmpty()) {
            if (this.topOfOutline.isEmpty()) {
                this.addPlaceholder();
            }
            this.ancestors.push(this.topOfOutline.get(this.topOfOutline.size() - 1));
        } else {
            BuildableHeading currentAncestor = this.ancestors.peek();
            if (!currentAncestor.hasChildren()) {
                this.addPlaceholder();
            }
            this.ancestors.push(currentAncestor.getLastChild());
        }
        return this;
    }

    @Override
    public DepthFirstDocumentOutlineBuilder previousLevel() {
        if (this.ancestors.isEmpty()) {
            throw new IllegalStateException("Already building the top level of the document.");
        }
        this.ancestors.pop();
        return this;
    }

    @Override
    public DocumentOutline getDocumentOutline() {
        return new DocumentOutlineImpl(this.topOfOutline);
    }

    private void addPlaceholder() {
        this.add(null, null, 0);
    }

    static class BuildableHeading
    implements DocumentOutline.Heading {
        private final String name;
        private final String anchor;
        private final List<BuildableHeading> children;
        private BuildableHeading parent;
        private final int type;

        BuildableHeading(String name, String anchor, int type) {
            this.name = name;
            this.anchor = anchor;
            this.children = new ArrayList<BuildableHeading>();
            this.parent = null;
            this.type = type;
        }

        void addChild(BuildableHeading child) {
            child.setParent(this);
            this.children.add(child);
        }

        void addChildren(Collection<BuildableHeading> children) {
            for (BuildableHeading child : children) {
                this.addChild(child);
            }
        }

        BuildableHeading getLastChild() {
            if (this.children.isEmpty()) {
                return null;
            }
            return this.children.get(this.children.size() - 1);
        }

        List<BuildableHeading> getChildren() {
            return Collections.unmodifiableList(this.children);
        }

        void clearChildren() {
            this.children.clear();
        }

        void setParent(BuildableHeading parent) {
            this.parent = parent;
        }

        boolean hasChildren() {
            return !this.children.isEmpty();
        }

        public BuildableHeading getChild(int index) {
            return this.children.get(index);
        }

        @Override
        public int getChildCount() {
            return this.children.size();
        }

        @Override
        public int getEffectiveLevel() {
            int level = 1;
            for (BuildableHeading ancestor = this.parent; ancestor != null; ancestor = ancestor.getParent()) {
                ++level;
            }
            return level;
        }

        @Override
        public int getType() {
            return this.type;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getAnchor() {
            return this.anchor;
        }

        public BuildableHeading getParent() {
            return this.parent;
        }

        public String toString() {
            return "BuildableHeading[" + (this.name != null ? this.name : "_placeholder_") + "]";
        }
    }
}


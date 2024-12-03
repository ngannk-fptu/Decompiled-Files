/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.outerj.daisy.diff.html.ancestor.TextOnlyComparator;
import org.outerj.daisy.diff.html.dom.Node;
import org.outerj.daisy.diff.html.dom.WhiteSpaceNode;
import org.outerj.daisy.diff.html.dom.helper.AttributesMap;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class TagNode
extends Node
implements Iterable<Node> {
    private List<Node> children = new ArrayList<Node>();
    private String qName;
    private final Attributes attributes;
    private IdentityHashMap<Attributes, Boolean> attributesEqualityTests = new IdentityHashMap();
    private static Set<String> blocks = new HashSet<String>();

    public TagNode(TagNode parent, String qName, Attributes attributesarg) {
        super(parent);
        this.qName = qName;
        this.attributes = new AttributesImpl(attributesarg);
    }

    public void addChild(Node node) {
        if (node.getParent() != this) {
            throw new IllegalStateException("The new child must have this node as a parent.");
        }
        this.children.add(node);
    }

    @Override
    protected void setRoot(TagNode root) {
        super.setRoot(root);
        for (Node child : this.children) {
            child.setRoot(root);
        }
    }

    public int getIndexOf(Node child) {
        return this.children.indexOf(child);
    }

    public void addChild(int index, Node node) {
        if (node.getParent() != this) {
            throw new IllegalStateException("The new child must have this node as a parent.");
        }
        this.children.add(index, node);
    }

    public Node getChild(int i) {
        return this.children.get(i);
    }

    @Override
    public Iterator<Node> iterator() {
        return this.children.iterator();
    }

    public int getNbChildren() {
        if (this.children == null) {
            return 0;
        }
        return this.children.size();
    }

    public String getQName() {
        return this.qName;
    }

    public Attributes getAttributes() {
        return this.attributes;
    }

    public boolean isSameTag(TagNode other) {
        if (other == null) {
            return false;
        }
        return this.equals(other);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TagNode)) {
            return false;
        }
        return this.equals((TagNode)obj);
    }

    private boolean equals(TagNode tagNode) {
        if (tagNode == this) {
            return true;
        }
        if (this.getRoot() == tagNode.getRoot()) {
            return false;
        }
        return this.isSimilarTag(tagNode);
    }

    private boolean isSimilarTag(TagNode otherNode) {
        if (!this.getQName().equalsIgnoreCase(otherNode.getQName())) {
            return false;
        }
        return this.hasSameAttributes(otherNode.getAttributes());
    }

    private boolean hasSameAttributes(Attributes otherAttributes) {
        if (otherAttributes == null) {
            return false;
        }
        if (this.attributesEqualityTests.get(otherAttributes) != null) {
            return this.attributesEqualityTests.get(otherAttributes);
        }
        boolean result = this.getAttributesMap().hasSameAttributes(otherAttributes);
        this.attributesEqualityTests.put(otherAttributes, result);
        return result;
    }

    private AttributesMap getAttributesMap() {
        return new AttributesMap(this.getAttributes());
    }

    public int hashCode() {
        int simple = 29;
        int result = this.getQName().hashCode();
        AttributesMap attrs = this.getAttributesMap();
        result = result * 29 + attrs.hashCode();
        return result;
    }

    public String getOpeningTag() {
        String s = "<" + this.getQName();
        Attributes localAttributes = this.getAttributes();
        for (int i = 0; i < localAttributes.getLength(); ++i) {
            s = s + " " + localAttributes.getQName(i) + "=\"" + localAttributes.getValue(i) + "\"";
        }
        s = s + ">";
        return s;
    }

    public String getEndTag() {
        return "</" + this.getQName() + ">";
    }

    @Override
    public List<Node> getMinimalDeletedSet(long id) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        if (this.children.size() == 0) {
            return nodes;
        }
        boolean allDescendentsAreDeleted = true;
        for (Node child : this) {
            List<Node> childrenChildren = child.getMinimalDeletedSet(id);
            nodes.addAll(childrenChildren);
            if (!allDescendentsAreDeleted || childrenChildren.size() == 1 && childrenChildren.contains(child)) continue;
            allDescendentsAreDeleted = false;
        }
        if (allDescendentsAreDeleted) {
            nodes.clear();
            nodes.add(this);
        }
        return nodes;
    }

    public String toString() {
        return this.getOpeningTag();
    }

    public boolean splitUntill(TagNode parent, Node split, boolean includeLeft) {
        boolean splitOccured = false;
        if (parent != this) {
            int i;
            TagNode part1 = new TagNode(null, this.getQName(), this.getAttributes());
            TagNode part2 = new TagNode(null, this.getQName(), this.getAttributes());
            part1.setParent(this.getParent());
            part2.setParent(this.getParent());
            for (i = 0; i < this.children.size() && this.children.get(i) != split; ++i) {
                this.children.get(i).setParent(part1);
                part1.addChild(this.children.get(i));
            }
            if (i < this.children.size()) {
                if (includeLeft) {
                    this.children.get(i).setParent(part1);
                    part1.addChild(this.children.get(i));
                } else {
                    this.children.get(i).setParent(part2);
                    part2.addChild(this.children.get(i));
                }
                ++i;
            }
            while (i < this.children.size()) {
                this.children.get(i).setParent(part2);
                part2.addChild(this.children.get(i));
                ++i;
            }
            if (part1.getNbChildren() > 0) {
                this.getParent().addChild(this.getParent().getIndexOf(this), part1);
            }
            if (part2.getNbChildren() > 0) {
                this.getParent().addChild(this.getParent().getIndexOf(this), part2);
            }
            if (part1.getNbChildren() > 0 && part2.getNbChildren() > 0) {
                splitOccured = true;
            }
            this.getParent().removeChild(this);
            if (includeLeft) {
                this.getParent().splitUntill(parent, part1, includeLeft);
            } else {
                this.getParent().splitUntill(parent, part2, includeLeft);
            }
        }
        return splitOccured;
    }

    private void removeChild(Node node) {
        this.children.remove(node);
    }

    public static boolean isBlockLevel(String qName) {
        return blocks.contains(qName.toLowerCase());
    }

    public static boolean isBlockLevel(Node node) {
        try {
            TagNode tagnode = (TagNode)node;
            return TagNode.isBlockLevel(tagnode.getQName());
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    public boolean isBlockLevel() {
        return TagNode.isBlockLevel(this);
    }

    public static boolean isInline(String qName) {
        return !TagNode.isBlockLevel(qName);
    }

    public static boolean isInline(Node node) {
        return !TagNode.isBlockLevel(node);
    }

    public boolean isInline() {
        return TagNode.isInline(this);
    }

    @Override
    public Node copyTree() {
        TagNode newThis = new TagNode(null, this.getQName(), new AttributesImpl(this.getAttributes()));
        newThis.setWhiteBefore(this.isWhiteBefore());
        newThis.setWhiteAfter(this.isWhiteAfter());
        for (Node child : this) {
            Node newChild = child.copyTree();
            newChild.setParent(newThis);
            newThis.addChild(newChild);
        }
        return newThis;
    }

    public double getMatchRatio(TagNode other, IProgressMonitor progressMonitor) {
        TextOnlyComparator txtComp = new TextOnlyComparator(other);
        return txtComp.getMatchRatio(new TextOnlyComparator(this), progressMonitor);
    }

    public void expandWhiteSpace() {
        int shift = 0;
        boolean spaceAdded = false;
        int nbOriginalChildren = this.getNbChildren();
        for (int i = 0; i < nbOriginalChildren; ++i) {
            WhiteSpaceNode ws;
            Node child = this.getChild(i + shift);
            try {
                TagNode tagChild = (TagNode)child;
                if (!tagChild.isPre()) {
                    tagChild.expandWhiteSpace();
                }
            }
            catch (ClassCastException e) {
                // empty catch block
            }
            if (!spaceAdded && child.isWhiteBefore()) {
                ws = new WhiteSpaceNode(null, " ", child.getLeftMostChild());
                ws.setParent(this);
                this.addChild(i + shift++, ws);
            }
            if (child.isWhiteAfter()) {
                ws = new WhiteSpaceNode(null, " ", child.getRightMostChild());
                ws.setParent(this);
                this.addChild(i + 1 + shift++, ws);
                spaceAdded = true;
                continue;
            }
            spaceAdded = false;
        }
    }

    @Override
    public Node getLeftMostChild() {
        if (this.getNbChildren() < 1) {
            return this;
        }
        Node child = this.getChild(0);
        return child.getLeftMostChild();
    }

    @Override
    public Node getRightMostChild() {
        if (this.getNbChildren() < 1) {
            return this;
        }
        Node child = this.getChild(this.getNbChildren() - 1);
        return child.getRightMostChild();
    }

    public boolean isPre() {
        return this.getQName().equalsIgnoreCase("pre");
    }

    static {
        blocks.add("html");
        blocks.add("body");
        blocks.add("p");
        blocks.add("blockquote");
        blocks.add("h1");
        blocks.add("h2");
        blocks.add("h3");
        blocks.add("h4");
        blocks.add("h5");
        blocks.add("pre");
        blocks.add("div");
        blocks.add("ul");
        blocks.add("ol");
        blocks.add("li");
        blocks.add("table");
        blocks.add("tbody");
        blocks.add("tr");
        blocks.add("td");
        blocks.add("th");
        blocks.add("br");
        blocks.add("thead");
        blocks.add("tfoot");
    }
}


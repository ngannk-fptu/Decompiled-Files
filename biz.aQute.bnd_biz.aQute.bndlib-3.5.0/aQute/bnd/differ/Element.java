/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.differ;

import aQute.bnd.differ.DiffImpl;
import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Diff;
import aQute.bnd.service.diff.Tree;
import aQute.bnd.service.diff.Type;
import java.util.Arrays;
import java.util.Collection;

class Element
implements Tree {
    static final Element[] EMPTY = new Element[0];
    final Type type;
    final String name;
    final Delta add;
    final Delta remove;
    final String comment;
    final Element[] children;

    Element(Type type, String name) {
        this(type, name, null, Delta.MINOR, Delta.MAJOR, null);
    }

    Element(Type type, String name, Element ... children) {
        this(type, name, Arrays.asList(children), Delta.MINOR, Delta.MAJOR, null);
    }

    Element(Type type, String name, Collection<? extends Element> children, Delta add, Delta remove, String comment) {
        this.type = type;
        this.name = name;
        this.add = add;
        this.remove = remove;
        this.comment = comment;
        if (children != null && children.size() > 0) {
            this.children = children.toArray(EMPTY);
            Arrays.sort(this.children);
        } else {
            this.children = EMPTY;
        }
    }

    public Element(Tree.Data data) {
        this.name = data.name;
        this.type = data.type;
        this.comment = data.comment;
        this.add = data.add;
        this.remove = data.rem;
        if (data.children == null) {
            this.children = EMPTY;
        } else {
            this.children = new Element[data.children.length];
            for (int i = 0; i < this.children.length; ++i) {
                this.children[i] = new Element(data.children[i]);
            }
            Arrays.sort(this.children);
        }
    }

    @Override
    public Tree.Data serialize() {
        Tree.Data data = new Tree.Data();
        data.type = this.type;
        data.name = this.name;
        data.add = this.add;
        data.rem = this.remove;
        data.comment = this.comment;
        if (this.children.length != 0) {
            data.children = new Tree.Data[this.children.length];
            for (int i = 0; i < this.children.length; ++i) {
                data.children[i] = this.children[i].serialize();
            }
        }
        return data;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    String getComment() {
        return this.comment;
    }

    @Override
    public int compareTo(Tree other) {
        if (this.type == other.getType()) {
            return this.name.compareTo(other.getName());
        }
        return this.type.compareTo(other.getType());
    }

    public boolean equals(Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        return this.compareTo((Element)other) == 0;
    }

    public int hashCode() {
        return this.type.hashCode() ^ this.name.hashCode();
    }

    @Override
    public Tree[] getChildren() {
        return this.children;
    }

    @Override
    public Delta ifAdded() {
        return this.add;
    }

    @Override
    public Delta ifRemoved() {
        return this.remove;
    }

    @Override
    public Diff diff(Tree older) {
        return new DiffImpl(this, older);
    }

    @Override
    public Element get(String name) {
        for (Element e : this.children) {
            if (!e.name.equals(name)) continue;
            return e;
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.toString(sb, "");
        return sb.toString();
    }

    private void toString(StringBuilder sb, String indent) {
        sb.append(indent).append((Object)this.type).append(" ").append(this.name).append(" (").append((Object)this.add).append("/").append((Object)this.remove).append(")").append("\n");
        for (Element e : this.children) {
            e.toString(sb, indent + " ");
        }
    }
}


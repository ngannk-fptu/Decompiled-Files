/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.differ;

import aQute.bnd.differ.Element;
import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Diff;
import aQute.bnd.service.diff.Tree;
import aQute.bnd.service.diff.Type;
import aQute.libg.generics.Create;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Formattable;
import java.util.Formatter;
import java.util.List;
import java.util.Set;

public class DiffImpl
implements Diff,
Comparable<DiffImpl>,
Formattable {
    final Tree older;
    final Tree newer;
    final Collection<DiffImpl> children;
    final Delta delta;
    static final Delta[][] TRANSITIONS = new Delta[][]{{Delta.IGNORED, Delta.UNCHANGED, Delta.CHANGED, Delta.MICRO, Delta.MINOR, Delta.MAJOR}, {Delta.IGNORED, Delta.UNCHANGED, Delta.CHANGED, Delta.MICRO, Delta.MINOR, Delta.MAJOR}, {Delta.IGNORED, Delta.CHANGED, Delta.CHANGED, Delta.MICRO, Delta.MINOR, Delta.MAJOR}, {Delta.IGNORED, Delta.MICRO, Delta.MICRO, Delta.MICRO, Delta.MINOR, Delta.MAJOR}, {Delta.IGNORED, Delta.MINOR, Delta.MINOR, Delta.MINOR, Delta.MINOR, Delta.MAJOR}, {Delta.IGNORED, Delta.MAJOR, Delta.MAJOR, Delta.MAJOR, Delta.MAJOR, Delta.MAJOR}, {Delta.IGNORED, Delta.MAJOR, Delta.MAJOR, Delta.MAJOR, Delta.MAJOR, Delta.MAJOR}, {Delta.IGNORED, Delta.MINOR, Delta.MINOR, Delta.MINOR, Delta.MINOR, Delta.MAJOR}};

    public DiffImpl(Tree newer, Tree older) {
        assert (newer != null || older != null);
        this.older = older;
        this.newer = newer;
        Tree[] newerChildren = newer == null ? Element.EMPTY : newer.getChildren();
        Tree[] olderChildren = older == null ? Element.EMPTY : older.getChildren();
        int o = 0;
        int n = 0;
        ArrayList<DiffImpl> children = new ArrayList<DiffImpl>();
        while (true) {
            DiffImpl diff;
            Tree ol;
            Tree nw = n < newerChildren.length ? newerChildren[n] : null;
            Tree tree = ol = o < olderChildren.length ? olderChildren[o] : null;
            if (nw == null && ol == null) break;
            if (nw != null && ol != null) {
                int result = nw.compareTo(ol);
                if (result == 0) {
                    diff = new DiffImpl(nw, ol);
                    ++n;
                    ++o;
                } else if (result > 0) {
                    diff = new DiffImpl(null, ol);
                    ++o;
                } else {
                    diff = new DiffImpl(nw, null);
                    ++n;
                }
            } else {
                diff = new DiffImpl(nw, ol);
                ++n;
                ++o;
            }
            children.add(diff);
        }
        this.children = Collections.unmodifiableCollection(children);
        this.delta = this.getDelta(null);
    }

    @Override
    public Delta getDelta() {
        return this.delta;
    }

    @Override
    public Delta getDelta(Diff.Ignore ignore) {
        if (ignore != null && ignore.contains(this)) {
            return Delta.IGNORED;
        }
        if (this.newer == null) {
            return Delta.REMOVED;
        }
        if (this.older == null) {
            return Delta.ADDED;
        }
        assert (this.newer != null && this.older != null);
        assert (this.newer.getClass() == this.older.getClass());
        Delta local = Delta.UNCHANGED;
        for (DiffImpl child : this.children) {
            Delta sub = child.getDelta(ignore);
            if (sub == Delta.REMOVED) {
                sub = child.older.ifRemoved();
            } else if (sub == Delta.ADDED) {
                sub = child.newer.ifAdded();
            }
            local = TRANSITIONS[sub.ordinal()][local.ordinal()];
        }
        return local;
    }

    @Override
    public Type getType() {
        return (this.newer == null ? this.older : this.newer).getType();
    }

    @Override
    public String getName() {
        return (this.newer == null ? this.older : this.newer).getName();
    }

    @Override
    public Collection<? extends Diff> getChildren() {
        return this.children;
    }

    public String toString() {
        return String.format("%-10s %-10s %s", new Object[]{this.getDelta(), this.getType(), this.getName()});
    }

    public boolean equals(Object other) {
        if (other instanceof DiffImpl) {
            DiffImpl o = (DiffImpl)other;
            return this.getDelta() == o.getDelta() && this.getType() == o.getType() && this.getName().equals(o.getName());
        }
        return false;
    }

    public int hashCode() {
        return this.getDelta().hashCode() ^ this.getType().hashCode() ^ this.getName().hashCode();
    }

    @Override
    public int compareTo(DiffImpl other) {
        if (this.getDelta() == other.getDelta()) {
            if (this.getType() == other.getType()) {
                return this.getName().compareTo(other.getName());
            }
            return this.getType().compareTo(other.getType());
        }
        return this.getDelta().compareTo(other.getDelta());
    }

    @Override
    public Diff get(String name) {
        for (DiffImpl child : this.children) {
            if (!child.getName().equals(name)) continue;
            return child;
        }
        return null;
    }

    @Override
    public Tree getOlder() {
        return this.older;
    }

    @Override
    public Tree getNewer() {
        return this.newer;
    }

    @Override
    public Diff.Data serialize() {
        Diff.Data data = new Diff.Data();
        data.type = this.getType();
        data.delta = this.delta;
        data.name = this.getName();
        data.children = new Diff.Data[this.children.size()];
        int i = 0;
        for (Diff diff : this.children) {
            data.children[i++] = diff.serialize();
        }
        return data;
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        boolean alternate;
        boolean bl = alternate = (flags & 4) != 0;
        if (alternate) {
            EnumSet<Delta> deltas = EnumSet.allOf(Delta.class);
            if ((flags & 2) != 0) {
                deltas.remove((Object)Delta.UNCHANGED);
            }
            int indent = Math.max(width, 0);
            DiffImpl.format(formatter, this, Create.list(), deltas, indent, 0);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('%');
            if ((flags & 1) != 0) {
                sb.append('-');
            }
            if (width != -1) {
                sb.append(width);
            }
            if (precision != -1) {
                sb.append('.');
                sb.append(precision);
            }
            if ((flags & 2) != 0) {
                sb.append('S');
            } else {
                sb.append('s');
            }
            formatter.format(sb.toString(), this.toString());
        }
    }

    /*
     * WARNING - void declaration
     */
    private static void format(Formatter formatter, Diff diff, List<String> formats, Set<Delta> deltas, int indent, int depth) {
        if (depth == formats.size()) {
            void var8_10;
            StringBuilder sb = new StringBuilder();
            if (depth > 0) {
                sb.append("%n");
            }
            int width = depth * 2;
            int n = width + indent;
            while (var8_10 > 0) {
                sb.append(' ');
                --var8_10;
            }
            sb.append("%-");
            sb.append(Math.max(20 - width, 1));
            sb.append("s %-10s %s");
            formats.add(sb.toString());
        }
        String format = formats.get(depth);
        formatter.format(format, new Object[]{diff.getDelta(), diff.getType(), diff.getName()});
        for (Diff diff2 : diff.getChildren()) {
            if (!deltas.contains((Object)diff2.getDelta())) continue;
            DiffImpl.format(formatter, diff2, formats, deltas, indent, depth + 1);
        }
    }
}


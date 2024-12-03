/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.diff;

import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Tree;
import aQute.bnd.service.diff.Type;
import java.util.Collection;

public interface Diff {
    public Data serialize();

    public Delta getDelta();

    public Delta getDelta(Ignore var1);

    public Type getType();

    public String getName();

    public Tree getOlder();

    public Tree getNewer();

    public Collection<? extends Diff> getChildren();

    public Diff get(String var1);

    public static class Data {
        public Type type;
        public Delta delta;
        public String name;
        public Data[] children;
        public String comment;
    }

    public static interface Ignore {
        public boolean contains(Diff var1);
    }
}


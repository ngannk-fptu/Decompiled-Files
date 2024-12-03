/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.diff;

import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Diff;
import aQute.bnd.service.diff.Type;

public interface Tree
extends Comparable<Tree> {
    public Data serialize();

    public Tree[] getChildren();

    public String getName();

    public Type getType();

    public Delta ifAdded();

    public Delta ifRemoved();

    public Diff diff(Tree var1);

    public Tree get(String var1);

    public static class Data {
        public String name;
        public Type type = Type.METHOD;
        public Delta add = Delta.MINOR;
        public Delta rem = Delta.MAJOR;
        public Data[] children = null;
        public String comment = null;
    }
}


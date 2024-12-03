/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff;

import java.util.List;
import org.apache.commons.jrcs.diff.Chunk;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.PatchFailedException;
import org.apache.commons.jrcs.diff.RevisionVisitor;
import org.apache.commons.jrcs.util.ToString;

public abstract class Delta
extends ToString {
    protected Chunk original;
    protected Chunk revised;
    static Class[][] DeltaClass = new Class[2][2];
    static /* synthetic */ Class class$0;
    static /* synthetic */ Class class$1;
    static /* synthetic */ Class class$2;

    static {
        try {
            Class[] classArray = DeltaClass[0];
            Class<?> clazz = class$0;
            if (clazz == null) {
                try {
                    clazz = class$0 = Class.forName("org.apache.commons.jrcs.diff.ChangeDelta");
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new NoClassDefFoundError(classNotFoundException.getMessage());
                }
            }
            classArray[0] = clazz;
            Class[] classArray2 = DeltaClass[0];
            Class<?> clazz2 = class$1;
            if (clazz2 == null) {
                try {
                    clazz2 = class$1 = Class.forName("org.apache.commons.jrcs.diff.AddDelta");
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new NoClassDefFoundError(classNotFoundException.getMessage());
                }
            }
            classArray2[1] = clazz2;
            Class[] classArray3 = DeltaClass[1];
            Class<?> clazz3 = class$2;
            if (clazz3 == null) {
                try {
                    clazz3 = class$2 = Class.forName("org.apache.commons.jrcs.diff.DeleteDelta");
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new NoClassDefFoundError(classNotFoundException.getMessage());
                }
            }
            classArray3[0] = clazz3;
            Class[] classArray4 = DeltaClass[1];
            Class<?> clazz4 = class$0;
            if (clazz4 == null) {
                try {
                    clazz4 = class$0 = Class.forName("org.apache.commons.jrcs.diff.ChangeDelta");
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new NoClassDefFoundError(classNotFoundException.getMessage());
                }
            }
            classArray4[1] = clazz4;
        }
        catch (Throwable o) {
            // empty catch block
        }
    }

    public static Delta newDelta(Chunk orig, Chunk rev) {
        Delta result;
        Class c = DeltaClass[orig.size() > 0 ? 1 : 0][rev.size() > 0 ? 1 : 0];
        try {
            result = (Delta)c.newInstance();
        }
        catch (Throwable e) {
            return null;
        }
        result.init(orig, rev);
        return result;
    }

    protected Delta() {
    }

    protected Delta(Chunk orig, Chunk rev) {
        this.init(orig, rev);
    }

    protected void init(Chunk orig, Chunk rev) {
        this.original = orig;
        this.revised = rev;
    }

    public abstract void verify(List var1) throws PatchFailedException;

    public final void patch(List target) throws PatchFailedException {
        this.verify(target);
        try {
            this.applyTo(target);
        }
        catch (Exception e) {
            throw new PatchFailedException(e.getMessage());
        }
    }

    public abstract void applyTo(List var1);

    public void toString(StringBuffer s) {
        this.original.rangeString(s);
        s.append("x");
        this.revised.rangeString(s);
        s.append(Diff.NL);
        this.original.toString(s, "> ", "\n");
        s.append("---");
        s.append(Diff.NL);
        this.revised.toString(s, "< ", "\n");
    }

    public abstract void toRCSString(StringBuffer var1, String var2);

    public String toRCSString(String EOL) {
        StringBuffer s = new StringBuffer();
        this.toRCSString(s, EOL);
        return s.toString();
    }

    public Chunk getOriginal() {
        return this.original;
    }

    public Chunk getRevised() {
        return this.revised;
    }

    public abstract void accept(RevisionVisitor var1);
}


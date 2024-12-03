/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import groovy.lang.Buildable;
import groovy.lang.GroovyObject;
import groovy.lang.Writable;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public abstract class ReplacementNode
implements Buildable,
Writable {
    public abstract void build(GroovyObject var1, Map var2, Map<String, String> var3);

    @Override
    public void build(GroovyObject builder) {
        this.build(builder, null, null);
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        return out;
    }
}


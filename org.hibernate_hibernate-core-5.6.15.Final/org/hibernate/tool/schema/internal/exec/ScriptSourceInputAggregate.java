/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.hibernate.tool.schema.spi.ScriptSourceInput;

public class ScriptSourceInputAggregate
implements ScriptSourceInput {
    private final ScriptSourceInput[] inputs;

    public ScriptSourceInputAggregate(ScriptSourceInput[] inputs) {
        this.inputs = inputs;
    }

    @Override
    public void prepare() {
        for (ScriptSourceInput input : this.inputs) {
            input.prepare();
        }
    }

    @Override
    public void release() {
        Throwable t = null;
        for (ScriptSourceInput input : this.inputs) {
            try {
                input.release();
            }
            catch (Throwable t2) {
                if (t == null) {
                    t = t2;
                    continue;
                }
                t.addSuppressed(t2);
            }
        }
        if (t != null) {
            ScriptSourceInputAggregate.doThrow(t);
        }
    }

    private static <T extends Throwable> void doThrow(Throwable e) throws T {
        throw e;
    }

    @Override
    public List<String> read(ImportSqlCommandExtractor commandExtractor) {
        List[] lists = new List[this.inputs.length];
        int size = 0;
        for (int i = 0; i < this.inputs.length; ++i) {
            lists[i] = this.inputs[i].read(commandExtractor);
            size += lists[i].size();
        }
        ArrayList<String> list = new ArrayList<String>(size);
        for (List strings : lists) {
            list.addAll(strings);
        }
        return list;
    }

    public String toString() {
        return "ScriptSourceInputAggregate(" + Arrays.toString(this.inputs) + ")";
    }
}


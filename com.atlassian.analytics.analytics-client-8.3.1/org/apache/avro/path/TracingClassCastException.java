/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.path;

import java.util.ArrayList;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.path.PathElement;
import org.apache.avro.path.PathTracingException;
import org.apache.avro.util.SchemaUtil;

public class TracingClassCastException
extends ClassCastException
implements PathTracingException<ClassCastException> {
    private final ClassCastException cause;
    private final Object datum;
    private final Schema expected;
    private final boolean customCoderUsed;
    private final List<PathElement> reversePath;

    public TracingClassCastException(ClassCastException cause, Object datum, Schema expected, boolean customCoderUsed) {
        this.cause = cause;
        this.datum = datum;
        this.expected = expected;
        this.customCoderUsed = customCoderUsed;
        this.reversePath = new ArrayList<PathElement>(3);
    }

    @Override
    public void tracePath(PathElement step) {
        this.reversePath.add(step);
    }

    @Override
    public synchronized ClassCastException getCause() {
        return this.cause;
    }

    @Override
    public ClassCastException summarize(Schema root) {
        StringBuilder sb = new StringBuilder();
        sb.append("value ").append(SchemaUtil.describe(this.datum));
        sb.append(" cannot be cast to expected type ").append(SchemaUtil.describe(this.expected));
        if (this.reversePath == null || this.reversePath.isEmpty()) {
            if (this.customCoderUsed) {
                sb.append(". No further details available as custom coders were used");
            }
        } else {
            sb.append(" at ");
            if (root != null) {
                sb.append(SchemaUtil.describe(root));
            }
            for (int i = this.reversePath.size() - 1; i >= 0; --i) {
                PathElement step = this.reversePath.get(i);
                sb.append(step.toString());
            }
        }
        ClassCastException summary = new ClassCastException(sb.toString());
        summary.initCause(this.cause);
        return summary;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import org.apache.lucene.search.spans.SpanPositionCheckQuery;
import org.apache.lucene.search.spans.SpanPositionRangeQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.ToStringUtils;

public class SpanFirstQuery
extends SpanPositionRangeQuery {
    public SpanFirstQuery(SpanQuery match, int end) {
        super(match, 0, end);
    }

    @Override
    protected SpanPositionCheckQuery.AcceptStatus acceptPosition(Spans spans) throws IOException {
        assert (spans.start() != spans.end()) : "start equals end: " + spans.start();
        if (spans.start() >= this.end) {
            return SpanPositionCheckQuery.AcceptStatus.NO_AND_ADVANCE;
        }
        if (spans.end() <= this.end) {
            return SpanPositionCheckQuery.AcceptStatus.YES;
        }
        return SpanPositionCheckQuery.AcceptStatus.NO;
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("spanFirst(");
        buffer.append(this.match.toString(field));
        buffer.append(", ");
        buffer.append(this.end);
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public SpanFirstQuery clone() {
        SpanFirstQuery spanFirstQuery = new SpanFirstQuery((SpanQuery)this.match.clone(), this.end);
        spanFirstQuery.setBoost(this.getBoost());
        return spanFirstQuery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpanFirstQuery)) {
            return false;
        }
        SpanFirstQuery other = (SpanFirstQuery)o;
        return this.end == other.end && this.match.equals(other.match) && this.getBoost() == other.getBoost();
    }

    @Override
    public int hashCode() {
        int h = this.match.hashCode();
        h ^= h << 8 | h >>> 25;
        return h ^= Float.floatToRawIntBits(this.getBoost()) ^ this.end;
    }
}


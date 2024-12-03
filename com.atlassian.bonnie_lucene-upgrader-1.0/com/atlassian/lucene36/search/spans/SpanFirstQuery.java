/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.search.spans.SpanPositionCheckQuery;
import com.atlassian.lucene36.search.spans.SpanPositionRangeQuery;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.Spans;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;

public class SpanFirstQuery
extends SpanPositionRangeQuery {
    public SpanFirstQuery(SpanQuery match, int end) {
        super(match, 0, end);
    }

    protected SpanPositionCheckQuery.AcceptStatus acceptPosition(Spans spans) throws IOException {
        assert (spans.start() != spans.end());
        if (spans.start() >= this.end) {
            return SpanPositionCheckQuery.AcceptStatus.NO_AND_ADVANCE;
        }
        if (spans.end() <= this.end) {
            return SpanPositionCheckQuery.AcceptStatus.YES;
        }
        return SpanPositionCheckQuery.AcceptStatus.NO;
    }

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

    public Object clone() {
        SpanFirstQuery spanFirstQuery = new SpanFirstQuery((SpanQuery)this.match.clone(), this.end);
        spanFirstQuery.setBoost(this.getBoost());
        return spanFirstQuery;
    }

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

    public int hashCode() {
        int h = this.match.hashCode();
        h ^= h << 8 | h >>> 25;
        return h ^= Float.floatToRawIntBits(this.getBoost()) ^ this.end;
    }
}


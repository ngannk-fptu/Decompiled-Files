/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.search.spans.SpanPositionCheckQuery;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.Spans;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;

public class SpanPositionRangeQuery
extends SpanPositionCheckQuery {
    protected int start = 0;
    protected int end;

    public SpanPositionRangeQuery(SpanQuery match, int start, int end) {
        super(match);
        this.start = start;
        this.end = end;
    }

    protected SpanPositionCheckQuery.AcceptStatus acceptPosition(Spans spans) throws IOException {
        assert (spans.start() != spans.end());
        if (spans.start() >= this.end) {
            return SpanPositionCheckQuery.AcceptStatus.NO_AND_ADVANCE;
        }
        if (spans.start() >= this.start && spans.end() <= this.end) {
            return SpanPositionCheckQuery.AcceptStatus.YES;
        }
        return SpanPositionCheckQuery.AcceptStatus.NO;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("spanPosRange(");
        buffer.append(this.match.toString(field));
        buffer.append(", ").append(this.start).append(", ");
        buffer.append(this.end);
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    public Object clone() {
        SpanPositionRangeQuery result = new SpanPositionRangeQuery((SpanQuery)this.match.clone(), this.start, this.end);
        result.setBoost(this.getBoost());
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpanPositionRangeQuery)) {
            return false;
        }
        SpanPositionRangeQuery other = (SpanPositionRangeQuery)o;
        return this.end == other.end && this.start == other.start && this.match.equals(other.match) && this.getBoost() == other.getBoost();
    }

    public int hashCode() {
        int h = this.match.hashCode();
        h ^= h << 8 | h >>> 25;
        return h ^= Float.floatToRawIntBits(this.getBoost()) ^ this.end ^ this.start;
    }
}


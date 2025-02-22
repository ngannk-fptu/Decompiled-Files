/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.resolver;

import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.Objects;
import java.util.regex.Pattern;

final class ResolverTuple {
    private final Tag tag;
    private final Pattern regexp;

    public ResolverTuple(Tag tag, Pattern regexp) {
        Objects.requireNonNull(tag, "Tag must be provided");
        Objects.requireNonNull(regexp, "regexp must be provided");
        this.tag = tag;
        this.regexp = regexp;
    }

    public Tag getTag() {
        return this.tag;
    }

    public Pattern getRegexp() {
        return this.regexp;
    }

    public String toString() {
        return "Tuple tag=" + this.tag + " regexp=" + this.regexp;
    }
}


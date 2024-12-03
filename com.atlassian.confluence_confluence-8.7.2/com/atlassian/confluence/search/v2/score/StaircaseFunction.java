/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.FieldValueSource;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated
public final class StaircaseFunction
implements ComposableScoreFunction {
    private final FieldValueSource source;
    private final SortedMap<Double, Double> staircases;

    public StaircaseFunction(@NonNull FieldValueSource source, @NonNull Map<? extends Number, ? extends Number> staircases) {
        this.source = Objects.requireNonNull(source);
        this.staircases = Objects.requireNonNull(staircases).entrySet().stream().collect(Collectors.toMap(x -> ((Number)x.getKey()).doubleValue(), x -> ((Number)x.getValue()).doubleValue(), (a, b) -> a, TreeMap::new));
    }

    public @NonNull FieldValueSource getSource() {
        return this.source;
    }

    public @NonNull SortedMap<Double, Double> getStaircases() {
        return this.staircases;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StaircaseFunction)) {
            return false;
        }
        StaircaseFunction that = (StaircaseFunction)o;
        return Objects.equals(this.getSource(), that.getSource()) && Objects.equals(this.getStaircases(), that.getStaircases());
    }

    public int hashCode() {
        return Objects.hash(this.getSource(), this.getStaircases());
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Objects;
import java.util.Set;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;

public interface ManyToManyShortestPathsAlgorithm<V, E>
extends ShortestPathAlgorithm<V, E> {
    public ManyToManyShortestPaths<V, E> getManyToManyPaths(Set<V> var1, Set<V> var2);

    public static abstract class BaseManyToManyShortestPathsImpl<V, E>
    implements ManyToManyShortestPaths<V, E> {
        private final Set<V> sources;
        private final Set<V> targets;

        @Override
        public Set<V> getSources() {
            return this.sources;
        }

        @Override
        public Set<V> getTargets() {
            return this.targets;
        }

        protected BaseManyToManyShortestPathsImpl(Set<V> sources, Set<V> targets) {
            this.sources = sources;
            this.targets = targets;
        }

        protected void assertCorrectSourceAndTarget(V source, V target) {
            Objects.requireNonNull(source, "source should not be null!");
            Objects.requireNonNull(target, "target should not be null!");
            if (!this.sources.contains(source) || !this.targets.contains(target)) {
                throw new IllegalArgumentException("paths between " + source + " and " + target + " is not computed");
            }
        }
    }

    public static interface ManyToManyShortestPaths<V, E> {
        public Set<V> getSources();

        public Set<V> getTargets();

        public GraphPath<V, E> getPath(V var1, V var2);

        public double getWeight(V var1, V var2);
    }
}


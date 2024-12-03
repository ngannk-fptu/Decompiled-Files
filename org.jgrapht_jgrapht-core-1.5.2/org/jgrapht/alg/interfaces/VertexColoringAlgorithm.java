/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface VertexColoringAlgorithm<V> {
    public Coloring<V> getColoring();

    public static class ColoringImpl<V>
    implements Coloring<V>,
    Serializable {
        private static final long serialVersionUID = -8456580091672353150L;
        private final int numberColors;
        private final Map<V, Integer> colors;

        public ColoringImpl(Map<V, Integer> colors, int numberColors) {
            this.numberColors = numberColors;
            this.colors = colors;
        }

        @Override
        public int getNumberColors() {
            return this.numberColors;
        }

        @Override
        public Map<V, Integer> getColors() {
            return this.colors;
        }

        @Override
        public List<Set<V>> getColorClasses() {
            HashMap groups = new HashMap();
            this.colors.forEach((v, color) -> {
                Set g = groups.computeIfAbsent(color, k -> new HashSet());
                g.add(v);
            });
            ArrayList<Set<V>> classes = new ArrayList<Set<V>>(this.numberColors);
            classes.addAll(groups.values());
            return classes;
        }

        public String toString() {
            return "Coloring [number-of-colors=" + this.numberColors + ", colors=" + this.colors + "]";
        }
    }

    public static interface Coloring<V> {
        public int getNumberColors();

        public Map<V, Integer> getColors();

        public List<Set<V>> getColorClasses();
    }
}


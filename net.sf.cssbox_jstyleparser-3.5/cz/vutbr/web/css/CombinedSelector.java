/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Rule;
import cz.vutbr.web.css.Selector;

public interface CombinedSelector
extends Rule<Selector> {
    public Selector getLastSelector() throws UnsupportedOperationException;

    public Selector.PseudoElementType getPseudoElementType();

    public Specificity computeSpecificity();

    public static interface Specificity
    extends Comparable<Specificity> {
        @Override
        public int compareTo(Specificity var1);

        public int get(Level var1);

        public void add(Level var1);

        public static enum Level {
            A,
            B,
            C,
            D;

        }
    }
}


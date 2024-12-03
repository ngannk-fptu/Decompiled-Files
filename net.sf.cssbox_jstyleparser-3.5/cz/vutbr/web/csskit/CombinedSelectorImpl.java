/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.csskit.AbstractRule;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.Arrays;

public class CombinedSelectorImpl
extends AbstractRule<Selector>
implements CombinedSelector {
    protected CombinedSelectorImpl() {
    }

    @Override
    public Selector getLastSelector() throws UnsupportedOperationException {
        if (this.list.size() == 0) {
            throw new UnsupportedOperationException("There is no \"last\" simple selector");
        }
        return (Selector)this.list.get(this.list.size() - 1);
    }

    @Override
    public Selector.PseudoElementType getPseudoElementType() {
        return this.getLastSelector().getPseudoElementType();
    }

    @Override
    public CombinedSelector.Specificity computeSpecificity() {
        SpecificityImpl spec = new SpecificityImpl();
        for (Selector s : this.list) {
            s.computeSpecificity(spec);
        }
        return spec;
    }

    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb = OutputUtil.appendList(sb, this.list, "");
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    public static class SpecificityImpl
    implements CombinedSelector.Specificity {
        protected int[] spec = new int[CombinedSelector.Specificity.Level.values().length];

        @Override
        public int compareTo(CombinedSelector.Specificity o) {
            if (this.get(CombinedSelector.Specificity.Level.A) > o.get(CombinedSelector.Specificity.Level.A)) {
                return 1;
            }
            if (this.get(CombinedSelector.Specificity.Level.A) < o.get(CombinedSelector.Specificity.Level.A)) {
                return -1;
            }
            if (this.get(CombinedSelector.Specificity.Level.B) > o.get(CombinedSelector.Specificity.Level.B)) {
                return 1;
            }
            if (this.get(CombinedSelector.Specificity.Level.B) < o.get(CombinedSelector.Specificity.Level.B)) {
                return -1;
            }
            if (this.get(CombinedSelector.Specificity.Level.C) > o.get(CombinedSelector.Specificity.Level.C)) {
                return 1;
            }
            if (this.get(CombinedSelector.Specificity.Level.C) < o.get(CombinedSelector.Specificity.Level.C)) {
                return -1;
            }
            if (this.get(CombinedSelector.Specificity.Level.D) > o.get(CombinedSelector.Specificity.Level.D)) {
                return 1;
            }
            if (this.get(CombinedSelector.Specificity.Level.D) < o.get(CombinedSelector.Specificity.Level.D)) {
                return -1;
            }
            return 0;
        }

        @Override
        public int get(CombinedSelector.Specificity.Level level) {
            switch (level) {
                case A: {
                    return this.spec[0];
                }
                case B: {
                    return this.spec[1];
                }
                case C: {
                    return this.spec[2];
                }
                case D: {
                    return this.spec[3];
                }
            }
            return 0;
        }

        @Override
        public void add(CombinedSelector.Specificity.Level level) {
            switch (level) {
                case A: {
                    this.spec[0] = this.spec[0] + 1;
                }
                case B: {
                    this.spec[1] = this.spec[1] + 1;
                }
                case C: {
                    this.spec[2] = this.spec[2] + 1;
                }
                case D: {
                    this.spec[3] = this.spec[3] + 1;
                }
            }
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + Arrays.hashCode(this.spec);
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof SpecificityImpl)) {
                return false;
            }
            SpecificityImpl other = (SpecificityImpl)obj;
            return Arrays.equals(this.spec, other.spec);
        }
    }
}


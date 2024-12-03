/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.summary;

import java.util.ArrayList;
import java.util.List;

public class Summary {
    private static final Fragment[] FRAGMENT_PROTO = new Fragment[0];
    private final List<Fragment> fragments = new ArrayList<Fragment>();

    public void add(Fragment fragment) {
        this.fragments.add(fragment);
    }

    public Fragment[] getFragments() {
        return this.fragments.toArray(FRAGMENT_PROTO);
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (Fragment fragment : this.fragments) {
            buffer.append(fragment);
        }
        return buffer.toString();
    }

    public static class Ellipsis
    extends Fragment {
        public Ellipsis() {
            super(" ... ");
        }

        @Override
        public boolean isEllipsis() {
            return true;
        }

        @Override
        public String toString() {
            return " ... ";
        }
    }

    public static class Highlight
    extends Fragment {
        public Highlight(String text) {
            super(text);
        }

        @Override
        public boolean isHighlight() {
            return true;
        }
    }

    public static class Fragment {
        private final String text;

        public Fragment(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public boolean isHighlight() {
            return false;
        }

        public boolean isEllipsis() {
            return false;
        }

        public String toString() {
            return this.text;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Fragment fragment = (Fragment)o;
            if (!this.text.equals(fragment.text)) {
                return false;
            }
            if (this.isHighlight() != fragment.isHighlight()) {
                return false;
            }
            return this.isEllipsis() == fragment.isEllipsis();
        }

        public int hashCode() {
            return this.text.hashCode();
        }
    }
}


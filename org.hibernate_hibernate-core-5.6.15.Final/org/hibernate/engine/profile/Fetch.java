/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.profile;

import org.hibernate.engine.profile.Association;

public class Fetch {
    private final Association association;
    private final Style style;

    public Fetch(Association association, Style style) {
        this.association = association;
        this.style = style;
    }

    public Association getAssociation() {
        return this.association;
    }

    public Style getStyle() {
        return this.style;
    }

    public String toString() {
        return "Fetch[" + (Object)((Object)this.style) + "{" + this.association.getRole() + "}]";
    }

    public static enum Style {
        JOIN("join"),
        SELECT("select");

        private final String name;

        private Style(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public static Style parse(String name) {
            if (Style.SELECT.name.equals(name)) {
                return SELECT;
            }
            return JOIN;
        }
    }
}


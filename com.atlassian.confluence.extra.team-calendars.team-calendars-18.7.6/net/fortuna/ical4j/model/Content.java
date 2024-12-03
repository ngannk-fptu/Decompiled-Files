/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public abstract class Content
implements Serializable {
    private static final long serialVersionUID = -2892694760294583989L;

    public abstract String getName();

    public abstract String getValue();

    public static abstract class Factory
    implements Serializable {
        private final List<String> supportedNames;

        public Factory(String ... supportedNames) {
            this.supportedNames = Arrays.asList(supportedNames);
        }

        public final boolean supports(String name) {
            return this.supportedNames.contains(name);
        }
    }
}


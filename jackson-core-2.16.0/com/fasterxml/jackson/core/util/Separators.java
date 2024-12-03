/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.util;

import java.io.Serializable;

public class Separators
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_ROOT_VALUE_SEPARATOR = " ";
    private final char objectFieldValueSeparator;
    private final Spacing objectFieldValueSpacing;
    private final char objectEntrySeparator;
    private final Spacing objectEntrySpacing;
    private final char arrayValueSeparator;
    private final Spacing arrayValueSpacing;
    private final String rootSeparator;

    public static Separators createDefaultInstance() {
        return new Separators();
    }

    public Separators() {
        this(':', ',', ',');
    }

    public Separators(char objectFieldValueSeparator, char objectEntrySeparator, char arrayValueSeparator) {
        this(DEFAULT_ROOT_VALUE_SEPARATOR, objectFieldValueSeparator, Spacing.BOTH, objectEntrySeparator, Spacing.NONE, arrayValueSeparator, Spacing.NONE);
    }

    public Separators(String rootSeparator, char objectFieldValueSeparator, Spacing objectFieldValueSpacing, char objectEntrySeparator, Spacing objectEntrySpacing, char arrayValueSeparator, Spacing arrayValueSpacing) {
        this.rootSeparator = rootSeparator;
        this.objectFieldValueSeparator = objectFieldValueSeparator;
        this.objectFieldValueSpacing = objectFieldValueSpacing;
        this.objectEntrySeparator = objectEntrySeparator;
        this.objectEntrySpacing = objectEntrySpacing;
        this.arrayValueSeparator = arrayValueSeparator;
        this.arrayValueSpacing = arrayValueSpacing;
    }

    public Separators withRootSeparator(String sep) {
        return this.rootSeparator.equals(sep) ? this : new Separators(sep, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, this.arrayValueSeparator, this.arrayValueSpacing);
    }

    public Separators withObjectFieldValueSeparator(char sep) {
        return this.objectFieldValueSeparator == sep ? this : new Separators(this.rootSeparator, sep, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, this.arrayValueSeparator, this.arrayValueSpacing);
    }

    public Separators withObjectFieldValueSpacing(Spacing spacing) {
        return this.objectFieldValueSpacing == spacing ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, spacing, this.objectEntrySeparator, this.objectEntrySpacing, this.arrayValueSeparator, this.arrayValueSpacing);
    }

    public Separators withObjectEntrySeparator(char sep) {
        return this.objectEntrySeparator == sep ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, sep, this.objectEntrySpacing, this.arrayValueSeparator, this.arrayValueSpacing);
    }

    public Separators withObjectEntrySpacing(Spacing spacing) {
        return this.objectEntrySpacing == spacing ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, spacing, this.arrayValueSeparator, this.arrayValueSpacing);
    }

    public Separators withArrayValueSeparator(char sep) {
        return this.arrayValueSeparator == sep ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, sep, this.arrayValueSpacing);
    }

    public Separators withArrayValueSpacing(Spacing spacing) {
        return this.arrayValueSpacing == spacing ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, this.arrayValueSeparator, spacing);
    }

    public String getRootSeparator() {
        return this.rootSeparator;
    }

    public char getObjectFieldValueSeparator() {
        return this.objectFieldValueSeparator;
    }

    public Spacing getObjectFieldValueSpacing() {
        return this.objectFieldValueSpacing;
    }

    public char getObjectEntrySeparator() {
        return this.objectEntrySeparator;
    }

    public Spacing getObjectEntrySpacing() {
        return this.objectEntrySpacing;
    }

    public char getArrayValueSeparator() {
        return this.arrayValueSeparator;
    }

    public Spacing getArrayValueSpacing() {
        return this.arrayValueSpacing;
    }

    public static enum Spacing {
        NONE("", ""),
        BEFORE(" ", ""),
        AFTER("", " "),
        BOTH(" ", " ");

        private final String spacesBefore;
        private final String spacesAfter;

        private Spacing(String spacesBefore, String spacesAfter) {
            this.spacesBefore = spacesBefore;
            this.spacesAfter = spacesAfter;
        }

        public String spacesBefore() {
            return this.spacesBefore;
        }

        public String spacesAfter() {
            return this.spacesAfter;
        }

        public String apply(char separator) {
            return this.spacesBefore + separator + this.spacesAfter;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class DecorationPolicy
implements Serializable {
    private final Set<DecorationComponent> components;

    public static DecorationPolicy space() {
        return new DecorationPolicy(EnumSet.of(DecorationComponent.HEADER, DecorationComponent.FOOTER, DecorationComponent.TITLE_PAGE));
    }

    public static DecorationPolicy none() {
        return new DecorationPolicy(Collections.emptySet());
    }

    public static DecorationPolicy headerAndFooter() {
        return new DecorationPolicy(EnumSet.of(DecorationComponent.HEADER, DecorationComponent.FOOTER));
    }

    public static DecorationPolicy titlePage() {
        return new DecorationPolicy(EnumSet.of(DecorationComponent.TITLE_PAGE));
    }

    public static DecorationPolicy pageNumbers() {
        return new DecorationPolicy(EnumSet.of(DecorationComponent.PAGE_NUMBERS));
    }

    public DecorationPolicy(Set<DecorationComponent> components) {
        this.components = components;
    }

    public DecorationPolicy combine(DecorationPolicy other) {
        this.components().addAll(other.components());
        return this;
    }

    public Set<DecorationComponent> components() {
        return this.components;
    }

    public static enum DecorationComponent {
        HEADER,
        FOOTER,
        TITLE_PAGE,
        PAGE_NUMBERS;

    }
}


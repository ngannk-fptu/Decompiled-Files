/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPluralCaseSpec;

public final class SoyMsgPluralPart
extends SoyMsgPart {
    private final String pluralVarName;
    private final int offset;
    private final ImmutableList<Pair<SoyMsgPluralCaseSpec, ImmutableList<SoyMsgPart>>> cases;

    public SoyMsgPluralPart(String pluralVarName, int offset, ImmutableList<Pair<SoyMsgPluralCaseSpec, ImmutableList<SoyMsgPart>>> cases) {
        this.pluralVarName = pluralVarName;
        this.offset = offset;
        this.cases = cases;
    }

    public String getPluralVarName() {
        return this.pluralVarName;
    }

    public int getOffset() {
        return this.offset;
    }

    public ImmutableList<Pair<SoyMsgPluralCaseSpec, ImmutableList<SoyMsgPart>>> getCases() {
        return this.cases;
    }

    public boolean equals(Object other) {
        if (!(other instanceof SoyMsgPluralPart)) {
            return false;
        }
        SoyMsgPluralPart otherPlural = (SoyMsgPluralPart)other;
        return this.offset == otherPlural.offset && this.pluralVarName.equals(otherPlural.pluralVarName) && this.cases.equals(otherPlural.cases);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{SoyMsgPluralPart.class, this.offset, this.pluralVarName, this.cases});
    }
}


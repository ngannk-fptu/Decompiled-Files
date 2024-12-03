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

public final class SoyMsgSelectPart
extends SoyMsgPart {
    private final String selectVarName;
    private final ImmutableList<Pair<String, ImmutableList<SoyMsgPart>>> cases;

    public SoyMsgSelectPart(String selectVarName, ImmutableList<Pair<String, ImmutableList<SoyMsgPart>>> cases) {
        this.selectVarName = selectVarName;
        this.cases = cases;
    }

    public String getSelectVarName() {
        return this.selectVarName;
    }

    public ImmutableList<Pair<String, ImmutableList<SoyMsgPart>>> getCases() {
        return this.cases;
    }

    public boolean equals(Object other) {
        if (!(other instanceof SoyMsgSelectPart)) {
            return false;
        }
        SoyMsgSelectPart otherSelect = (SoyMsgSelectPart)other;
        return this.selectVarName.equals(otherSelect.selectVarName) && this.cases.equals(otherSelect.cases);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{SoyMsgSelectPart.class, this.selectVarName, this.cases});
    }
}


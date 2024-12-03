/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.base.Objects;
import com.google.template.soy.msgs.restricted.SoyMsgPart;

public final class SoyMsgPluralRemainderPart
extends SoyMsgPart {
    private final String pluralVarName;

    public SoyMsgPluralRemainderPart(String pluralVarName) {
        this.pluralVarName = pluralVarName;
    }

    public String getPluralVarName() {
        return this.pluralVarName;
    }

    public boolean equals(Object other) {
        return other instanceof SoyMsgPluralRemainderPart && this.pluralVarName.equals(((SoyMsgPluralRemainderPart)other).pluralVarName);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{SoyMsgPluralRemainderPart.class, this.pluralVarName});
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.base.Objects;
import com.google.template.soy.msgs.restricted.SoyMsgPart;

public final class SoyMsgPlaceholderPart
extends SoyMsgPart {
    private final String placeholderName;

    public SoyMsgPlaceholderPart(String placeholderName) {
        this.placeholderName = placeholderName;
    }

    public String getPlaceholderName() {
        return this.placeholderName;
    }

    public boolean equals(Object other) {
        return other instanceof SoyMsgPlaceholderPart && this.placeholderName.equals(((SoyMsgPlaceholderPart)other).placeholderName);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{SoyMsgPlaceholderPart.class, this.placeholderName.hashCode()});
    }
}


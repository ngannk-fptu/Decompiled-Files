/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.MediaSpec;

public class MediaSpecType
extends MediaSpec {
    public MediaSpecType(String type) {
        super(type);
    }

    @Override
    public boolean matches(MediaExpression e) {
        return true;
    }

    @Override
    public String toString() {
        return this.type + "[*]";
    }
}


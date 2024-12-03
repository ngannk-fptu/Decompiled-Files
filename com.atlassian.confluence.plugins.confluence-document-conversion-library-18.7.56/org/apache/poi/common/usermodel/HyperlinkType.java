/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.common.usermodel;

import org.apache.poi.util.Internal;

public enum HyperlinkType {
    NONE(-1),
    URL(1),
    DOCUMENT(2),
    EMAIL(3),
    FILE(4);

    @Internal(since="3.15 beta 3")
    @Deprecated
    private final int code;

    @Internal(since="3.15 beta 3")
    @Deprecated
    private HyperlinkType(int code) {
        this.code = code;
    }

    @Deprecated
    @Internal(since="3.15 beta 3")
    int getCode() {
        return this.code;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.xml;

import java.io.Serializable;
import org.hibernate.internal.util.xml.Origin;

public class OriginImpl
implements Origin,
Serializable {
    private final String type;
    private final String name;

    public OriginImpl(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }
}


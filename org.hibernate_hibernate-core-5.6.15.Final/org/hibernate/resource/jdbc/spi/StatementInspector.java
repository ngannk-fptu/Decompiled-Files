/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.jdbc.spi;

import java.io.Serializable;

public interface StatementInspector
extends Serializable {
    public String inspect(String var1);
}


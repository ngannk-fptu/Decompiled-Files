/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.qom.Source;

public interface Selector
extends Source {
    public String getNodeTypeName();

    public String getSelectorName();
}


/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.io.Serializable;

public interface Negotiable
extends Serializable {
    public Negotiable negotiate(Negotiable var1);

    public Object getNegotiatedValue();

    public Class getNegotiatedValueClass();
}


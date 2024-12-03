/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package org.apache.axiom.ext.activation;

import javax.activation.DataSource;

public interface SizeAwareDataSource
extends DataSource {
    public long getSize();
}


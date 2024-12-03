/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.jandex.IndexView
 */
package org.hibernate.boot.spi;

import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.jboss.jandex.IndexView;

public interface MetadataContributor {
    public void contribute(InFlightMetadataCollector var1, IndexView var2);
}


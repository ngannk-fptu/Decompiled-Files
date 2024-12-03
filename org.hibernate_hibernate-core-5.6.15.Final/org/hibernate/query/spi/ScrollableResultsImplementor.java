/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import org.hibernate.Incubating;
import org.hibernate.ScrollableResults;

@Incubating
public interface ScrollableResultsImplementor
extends ScrollableResults {
    public boolean isClosed();

    public int getNumberOfTypes();
}


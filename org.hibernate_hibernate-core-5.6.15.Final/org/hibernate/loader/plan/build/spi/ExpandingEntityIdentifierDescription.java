/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
import org.hibernate.loader.plan.spi.EntityIdentifierDescription;

public interface ExpandingEntityIdentifierDescription
extends EntityIdentifierDescription,
ExpandingFetchSource {
}


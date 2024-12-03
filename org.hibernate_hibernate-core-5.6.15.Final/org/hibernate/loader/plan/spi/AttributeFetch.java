/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.persister.walking.spi.AttributeDefinition;

public interface AttributeFetch
extends Fetch {
    public AttributeDefinition getFetchedAttributeDefinition();
}


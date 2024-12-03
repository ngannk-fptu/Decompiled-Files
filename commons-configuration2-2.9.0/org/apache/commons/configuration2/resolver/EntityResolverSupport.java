/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.resolver;

import org.xml.sax.EntityResolver;

public interface EntityResolverSupport {
    public EntityResolver getEntityResolver();

    public void setEntityResolver(EntityResolver var1);
}


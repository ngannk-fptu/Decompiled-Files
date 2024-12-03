/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.utils.resolver;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;

public abstract class ResourceResolverSpi {
    public abstract XMLSignatureInput engineResolveURI(ResourceResolverContext var1) throws ResourceResolverException;

    public abstract boolean engineCanResolveURI(ResourceResolverContext var1);
}


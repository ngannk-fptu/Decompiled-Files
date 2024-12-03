/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.service.resolver;

import java.util.List;
import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.resolver.ResolveContext;

@ProviderType
public interface Resolver {
    public Map<Resource, List<Wire>> resolve(ResolveContext var1) throws ResolutionException;

    public Map<Resource, List<Wire>> resolveDynamic(ResolveContext var1, Wiring var2, Requirement var3) throws ResolutionException;
}


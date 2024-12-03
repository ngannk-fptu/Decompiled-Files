/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Session;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.CachingNameResolver;
import org.apache.jackrabbit.spi.commons.conversion.CachingPathResolver;
import org.apache.jackrabbit.spi.commons.conversion.IdentifierResolver;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.conversion.ParsingNameResolver;
import org.apache.jackrabbit.spi.commons.conversion.ParsingPathResolver;
import org.apache.jackrabbit.spi.commons.conversion.PathResolver;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;
import org.apache.jackrabbit.spi.commons.namespace.RegistryNamespaceResolver;
import org.apache.jackrabbit.spi.commons.namespace.SessionNamespaceResolver;

public class DefaultNamePathResolver
implements NamePathResolver {
    private final NameResolver nResolver;
    private final PathResolver pResolver;

    public DefaultNamePathResolver(NamespaceResolver nsResolver) {
        this(nsResolver, false);
    }

    public DefaultNamePathResolver(Session session) {
        this(new SessionNamespaceResolver(session), session instanceof IdentifierResolver ? (IdentifierResolver)((Object)session) : null, false);
    }

    public DefaultNamePathResolver(NamespaceRegistry registry) {
        this(new RegistryNamespaceResolver(registry));
    }

    public DefaultNamePathResolver(NamespaceResolver nsResolver, boolean enableCaching) {
        this(nsResolver, null, enableCaching);
    }

    public DefaultNamePathResolver(NamespaceResolver nsResolver, IdentifierResolver idResolver, boolean enableCaching) {
        ParsingNameResolver nr = new ParsingNameResolver(NameFactoryImpl.getInstance(), nsResolver);
        ParsingPathResolver pr = new ParsingPathResolver(PathFactoryImpl.getInstance(), nr, idResolver);
        if (enableCaching) {
            this.nResolver = new CachingNameResolver(nr);
            this.pResolver = new CachingPathResolver(pr);
        } else {
            this.nResolver = nr;
            this.pResolver = pr;
        }
    }

    public DefaultNamePathResolver(NameResolver nResolver, PathResolver pResolver) {
        this.nResolver = nResolver;
        this.pResolver = pResolver;
    }

    @Override
    public Name getQName(String name) throws IllegalNameException, NamespaceException {
        return this.nResolver.getQName(name);
    }

    @Override
    public String getJCRName(Name name) throws NamespaceException {
        return this.nResolver.getJCRName(name);
    }

    @Override
    public Path getQPath(String path) throws MalformedPathException, IllegalNameException, NamespaceException {
        return this.pResolver.getQPath(path);
    }

    @Override
    public Path getQPath(String path, boolean normalizeIdentifier) throws MalformedPathException, IllegalNameException, NamespaceException {
        return this.pResolver.getQPath(path, normalizeIdentifier);
    }

    @Override
    public String getJCRPath(Path path) throws NamespaceException {
        return this.pResolver.getJCRPath(path);
    }
}


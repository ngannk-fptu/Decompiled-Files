/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.GenerationalCache;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.conversion.PathResolver;

public class CachingPathResolver
implements PathResolver {
    private final PathResolver resolver;
    private final GenerationalCache cache;

    public CachingPathResolver(PathResolver resolver, GenerationalCache cache) {
        this.resolver = resolver;
        this.cache = cache;
    }

    public CachingPathResolver(PathResolver resolver) {
        this(resolver, new GenerationalCache());
    }

    @Override
    public Path getQPath(String path) throws MalformedPathException, IllegalNameException, NamespaceException {
        return this.getQPath(path, true);
    }

    @Override
    public Path getQPath(String path, boolean normalizeIdentifier) throws MalformedPathException, IllegalNameException, NamespaceException {
        Path qpath;
        if (path.startsWith("[") && !normalizeIdentifier) {
            qpath = this.resolver.getQPath(path, normalizeIdentifier);
        } else {
            qpath = (Path)this.cache.get(path);
            if (qpath == null) {
                qpath = this.resolver.getQPath(path, normalizeIdentifier);
                this.cache.put(path, qpath);
            }
        }
        return qpath;
    }

    @Override
    public String getJCRPath(Path path) throws NamespaceException {
        String jcrPath = (String)this.cache.get(path);
        if (jcrPath == null) {
            jcrPath = this.resolver.getJCRPath(path);
            this.cache.put(path, jcrPath);
        }
        return jcrPath;
    }
}


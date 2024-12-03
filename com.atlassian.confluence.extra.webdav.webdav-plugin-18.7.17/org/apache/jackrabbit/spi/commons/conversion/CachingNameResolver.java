/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.GenerationalCache;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;

public class CachingNameResolver
implements NameResolver {
    private final NameResolver resolver;
    private final GenerationalCache cache;

    public CachingNameResolver(NameResolver resolver, GenerationalCache cache) {
        this.resolver = resolver;
        this.cache = cache;
    }

    public CachingNameResolver(NameResolver resolver) {
        this(resolver, new GenerationalCache());
    }

    @Override
    public Name getQName(String jcrName) throws IllegalNameException, NamespaceException {
        Name name = (Name)this.cache.get(jcrName);
        if (name == null) {
            name = this.resolver.getQName(jcrName);
            this.cache.put(jcrName, name);
        }
        return name;
    }

    @Override
    public String getJCRName(Name name) throws NamespaceException {
        if (name.getNamespaceURI().length() == 0) {
            return name.getLocalName();
        }
        String jcrName = (String)this.cache.get(name);
        if (jcrName == null) {
            jcrName = this.resolver.getJCRName(name);
            this.cache.put(name, jcrName);
        }
        return jcrName;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.NameParser;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;

public class ParsingNameResolver
implements NameResolver {
    private final NameFactory nameFactory;
    private final NamespaceResolver resolver;

    public ParsingNameResolver(NameFactory nameFactory, NamespaceResolver resolver) {
        this.nameFactory = nameFactory;
        this.resolver = resolver;
    }

    @Override
    public Name getQName(String jcrName) throws IllegalNameException, NamespaceException {
        return NameParser.parse(jcrName, this.resolver, this.nameFactory);
    }

    @Override
    public String getJCRName(Name name) throws NamespaceException {
        String uri = name.getNamespaceURI();
        if (uri.length() == 0) {
            return name.getLocalName();
        }
        return this.resolver.getPrefix(uri) + ":" + name.getLocalName();
    }
}


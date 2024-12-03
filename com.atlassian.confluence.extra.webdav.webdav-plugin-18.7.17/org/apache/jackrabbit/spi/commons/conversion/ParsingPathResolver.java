/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.conversion.IdentifierResolver;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.conversion.PathParser;
import org.apache.jackrabbit.spi.commons.conversion.PathResolver;

public class ParsingPathResolver
implements PathResolver {
    private final PathFactory pathFactory;
    private final NameResolver nameResolver;
    private final IdentifierResolver idResolver;

    public ParsingPathResolver(PathFactory pathFactory, NameResolver resolver) {
        this(pathFactory, resolver, null);
    }

    public ParsingPathResolver(PathFactory pathFactory, NameResolver nameResolver, IdentifierResolver idResolver) {
        this.pathFactory = pathFactory;
        this.nameResolver = nameResolver;
        this.idResolver = idResolver;
    }

    @Override
    public Path getQPath(String jcrPath) throws MalformedPathException, IllegalNameException, NamespaceException {
        return PathParser.parse(jcrPath, this.nameResolver, this.idResolver, this.pathFactory);
    }

    @Override
    public Path getQPath(String jcrPath, boolean normalizeIdentifier) throws MalformedPathException, IllegalNameException, NamespaceException {
        return PathParser.parse(jcrPath, this.nameResolver, this.idResolver, this.pathFactory, normalizeIdentifier);
    }

    @Override
    public String getJCRPath(Path path) throws NamespaceException {
        StringBuffer buffer = new StringBuffer();
        Path.Element[] elements = path.getElements();
        for (int i = 0; i < elements.length; ++i) {
            if (i > 0) {
                buffer.append('/');
            }
            if (i == 0 && elements.length == 1 && elements[i].denotesRoot()) {
                buffer.append('/');
                continue;
            }
            if (elements[i].denotesCurrent()) {
                buffer.append('.');
                continue;
            }
            if (elements[i].denotesParent()) {
                buffer.append("..");
                continue;
            }
            if (elements[i].denotesIdentifier()) {
                buffer.append(elements[i].getString());
                continue;
            }
            buffer.append(this.nameResolver.getJCRName(elements[i].getName()));
            if (elements[i].getIndex() <= 1) continue;
            buffer.append('[');
            buffer.append(elements[i].getIndex());
            buffer.append(']');
        }
        return buffer.toString();
    }
}


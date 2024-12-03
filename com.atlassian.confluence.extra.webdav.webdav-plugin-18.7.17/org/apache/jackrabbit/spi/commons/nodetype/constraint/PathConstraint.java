/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype.constraint;

import javax.jcr.NamespaceException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.conversion.NameException;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.conversion.PathResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;

class PathConstraint
extends ValueConstraint {
    static final String WILDCARD = '\t' + NameConstants.ANY_NAME.toString();
    static final String JCR_WILDCARD = "/*";
    private static final PathFactory PATH_FACTORY = PathFactoryImpl.getInstance();
    private final Path path;
    private final boolean deep;

    static PathConstraint create(String pathString) {
        if (WILDCARD.equals(pathString)) {
            return new PathConstraint(pathString, PATH_FACTORY.getRootPath(), true);
        }
        boolean deep = pathString.endsWith(WILDCARD);
        Path path = deep ? PATH_FACTORY.create(pathString.substring(0, pathString.length() - WILDCARD.length())) : PATH_FACTORY.create(pathString);
        return new PathConstraint(pathString, path, deep);
    }

    static PathConstraint create(String jcrPath, PathResolver resolver) throws InvalidConstraintException {
        try {
            Path path;
            boolean deep = jcrPath.endsWith(JCR_WILDCARD);
            if (JCR_WILDCARD.equals(jcrPath)) {
                path = PATH_FACTORY.getRootPath();
            } else {
                if (deep) {
                    jcrPath = jcrPath.substring(0, jcrPath.length() - JCR_WILDCARD.length());
                }
                path = resolver.getQPath(jcrPath);
            }
            StringBuffer definition = new StringBuffer(path.getString());
            if (deep) {
                definition.append(WILDCARD);
            }
            return new PathConstraint(definition.toString(), path, deep);
        }
        catch (NameException e) {
            String msg = "Invalid path expression specified as value constraint: " + jcrPath;
            log.debug(msg);
            throw new InvalidConstraintException(msg, e);
        }
        catch (NamespaceException e) {
            String msg = "Invalid path expression specified as value constraint: " + jcrPath;
            log.debug(msg);
            throw new InvalidConstraintException(msg, e);
        }
    }

    private PathConstraint(String pathString, Path path, boolean deep) {
        super(pathString);
        this.path = path;
        this.deep = deep;
    }

    @Override
    public String getDefinition(NamePathResolver resolver) {
        try {
            String p = resolver.getJCRPath(this.path);
            if (!this.deep) {
                return p;
            }
            if (this.path.denotesRoot()) {
                return p + "*";
            }
            return p + JCR_WILDCARD;
        }
        catch (NamespaceException e) {
            return this.getString();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void check(QValue value) throws ConstraintViolationException, RepositoryException {
        if (value == null) {
            throw new ConstraintViolationException("null value does not satisfy the constraint '" + this.getString() + "'");
        }
        switch (value.getType()) {
            case 8: {
                Path p1;
                Path p0;
                Path p = value.getPath();
                try {
                    p0 = this.path.getNormalizedPath();
                    p1 = p.getNormalizedPath();
                }
                catch (RepositoryException e) {
                    throw new ConstraintViolationException("path not valid: " + e);
                }
                if (this.deep) {
                    try {
                        if (p0.isAncestorOf(p1)) return;
                        throw new ConstraintViolationException(p + " does not satisfy the constraint '" + this.getString() + "'");
                    }
                    catch (RepositoryException e) {
                        throw new ConstraintViolationException(p + " does not satisfy the constraint '" + this.getString() + "'");
                    }
                }
                if (p0.equals(p1)) return;
                throw new ConstraintViolationException(p + " does not satisfy the constraint '" + this.getString() + "'");
            }
        }
        String msg = "PATH constraint can not be applied to value of type: " + PropertyType.nameFromValue(value.getType());
        log.debug(msg);
        throw new RepositoryException(msg);
    }
}


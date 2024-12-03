/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class DialectResolverSet
implements DialectResolver {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DialectResolverSet.class);
    private List<DialectResolver> resolvers;

    public DialectResolverSet() {
        this(new ArrayList<DialectResolver>());
    }

    public DialectResolverSet(List<DialectResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public DialectResolverSet(DialectResolver ... resolvers) {
        this(Arrays.asList(resolvers));
    }

    @Override
    public Dialect resolveDialect(DialectResolutionInfo info) {
        for (DialectResolver resolver : this.resolvers) {
            try {
                Dialect dialect = resolver.resolveDialect(info);
                if (dialect == null) continue;
                return dialect;
            }
            catch (JDBCConnectionException e) {
                throw e;
            }
            catch (Exception e) {
                LOG.exceptionInSubResolver(e.getMessage());
            }
        }
        return null;
    }

    public void addResolver(DialectResolver resolver) {
        this.resolvers.add(resolver);
    }

    public void addResolverAtFirst(DialectResolver resolver) {
        this.resolvers.add(0, resolver);
    }
}


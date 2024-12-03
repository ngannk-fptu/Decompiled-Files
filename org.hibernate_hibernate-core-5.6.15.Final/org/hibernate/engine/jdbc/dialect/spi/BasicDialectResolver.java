/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.spi;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

public class BasicDialectResolver
implements DialectResolver {
    private final String nameToMatch;
    private final int majorVersionToMatch;
    private final int minorVersionToMatch;
    private final Class dialectClass;

    public BasicDialectResolver(String nameToMatch, Class dialectClass) {
        this(nameToMatch, -9999, dialectClass);
    }

    public BasicDialectResolver(String nameToMatch, int majorVersionToMatch, Class dialectClass) {
        this(nameToMatch, majorVersionToMatch, -9999, dialectClass);
    }

    public BasicDialectResolver(String nameToMatch, int majorVersionToMatch, int minorVersionToMatch, Class dialectClass) {
        this.nameToMatch = nameToMatch;
        this.majorVersionToMatch = majorVersionToMatch;
        this.minorVersionToMatch = minorVersionToMatch;
        this.dialectClass = dialectClass;
    }

    @Override
    public final Dialect resolveDialect(DialectResolutionInfo info) {
        String databaseName = info.getDatabaseName();
        int databaseMajorVersion = info.getDatabaseMajorVersion();
        int databaseMinorVersion = info.getDatabaseMinorVersion();
        if (!(!this.nameToMatch.equalsIgnoreCase(databaseName) || this.majorVersionToMatch != -9999 && this.majorVersionToMatch != databaseMajorVersion || this.minorVersionToMatch != -9999 && this.minorVersionToMatch != databaseMinorVersion)) {
            try {
                return (Dialect)this.dialectClass.newInstance();
            }
            catch (HibernateException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new HibernateException("Could not instantiate specified Dialect class [" + this.dialectClass.getName() + "]", t);
            }
        }
        return null;
    }
}


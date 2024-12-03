/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.internal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class ParameterBinder {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)ParameterBinder.class.getName());

    private ParameterBinder() {
    }

    public static int bindQueryParameters(PreparedStatement st, QueryParameters queryParameters, int start, NamedParameterSource source, SessionImplementor session) throws SQLException, HibernateException {
        int col = start;
        col += ParameterBinder.bindPositionalParameters(st, queryParameters, col, session);
        col += ParameterBinder.bindNamedParameters(st, queryParameters, col, source, session);
        return col;
    }

    private static int bindPositionalParameters(PreparedStatement st, QueryParameters queryParameters, int start, SessionImplementor session) throws SQLException, HibernateException {
        return ParameterBinder.bindPositionalParameters(st, queryParameters.getPositionalParameterValues(), queryParameters.getPositionalParameterTypes(), start, session);
    }

    private static int bindPositionalParameters(PreparedStatement st, Object[] values, Type[] types, int start, SessionImplementor session) throws SQLException, HibernateException {
        int span = 0;
        for (int i = 0; i < values.length; ++i) {
            types[i].nullSafeSet(st, values[i], start + span, session);
            span += types[i].getColumnSpan(session.getFactory());
        }
        return span;
    }

    private static int bindNamedParameters(PreparedStatement ps, QueryParameters queryParameters, int start, NamedParameterSource source, SessionImplementor session) throws SQLException, HibernateException {
        return ParameterBinder.bindNamedParameters(ps, queryParameters.getNamedParameters(), start, source, session);
    }

    private static int bindNamedParameters(PreparedStatement ps, Map namedParams, int start, NamedParameterSource source, SessionImplementor session) throws SQLException, HibernateException {
        if (namedParams != null) {
            Iterator iter = namedParams.entrySet().iterator();
            int result = 0;
            while (iter.hasNext()) {
                int[] locations;
                Map.Entry e = iter.next();
                String name = (String)e.getKey();
                TypedValue typedVal = (TypedValue)e.getValue();
                for (int location : locations = source.getNamedParameterLocations(name)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debugf("bindNamedParameters() %s -> %s [%s]", typedVal.getValue(), name, location + start);
                    }
                    typedVal.getType().nullSafeSet(ps, typedVal.getValue(), location + start, session);
                }
                result += locations.length;
            }
            return result;
        }
        return 0;
    }

    public static interface NamedParameterSource {
        public int[] getNamedParameterLocations(String var1);
    }
}


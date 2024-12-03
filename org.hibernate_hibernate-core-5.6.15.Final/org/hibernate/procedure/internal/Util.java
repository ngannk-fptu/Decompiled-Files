/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.procedure.internal;

import java.util.Map;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.loader.custom.sql.SQLQueryReturnProcessor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.procedure.UnknownSqlResultSetMappingException;
import org.jboss.logging.Logger;

public class Util {
    private static final Logger log = Logger.getLogger(Util.class);

    private Util() {
    }

    public static NativeSQLQueryReturn[] copy(NativeSQLQueryReturn[] queryReturns) {
        if (queryReturns == null) {
            return new NativeSQLQueryReturn[0];
        }
        NativeSQLQueryReturn[] copy = new NativeSQLQueryReturn[queryReturns.length];
        System.arraycopy(queryReturns, 0, copy, 0, queryReturns.length);
        return copy;
    }

    public static Set<String> copy(Set<String> synchronizedQuerySpaces) {
        return CollectionHelper.makeCopy(synchronizedQuerySpaces);
    }

    public static Map<String, Object> copy(Map<String, Object> hints) {
        return CollectionHelper.makeCopy(hints);
    }

    public static void resolveResultSetMappings(ResultSetMappingResolutionContext context, String ... resultSetMappingNames) {
        for (String resultSetMappingName : resultSetMappingNames) {
            log.tracef("Starting attempt resolve named result-set-mapping : %s", (Object)resultSetMappingName);
            ResultSetMappingDefinition mapping = context.findResultSetMapping(resultSetMappingName);
            if (mapping == null) {
                throw new UnknownSqlResultSetMappingException("Unknown SqlResultSetMapping [" + resultSetMappingName + "]");
            }
            log.tracef("Found result-set-mapping : %s", (Object)mapping.traceLoggableFormat());
            context.addQueryReturns(mapping.getQueryReturns());
            SQLQueryReturnProcessor processor = new SQLQueryReturnProcessor(mapping.getQueryReturns(), context.getSessionFactory());
            SQLQueryReturnProcessor.ResultAliasContext processResult = processor.process();
            context.addQuerySpaces(processResult.collectQuerySpaces());
        }
    }

    public static void resolveResultClasses(ResultClassesResolutionContext context, Class ... resultClasses) {
        int i = 0;
        for (Class resultClass : resultClasses) {
            context.addQueryReturns(new NativeSQLQueryRootReturn("alias" + ++i, resultClass.getName(), LockMode.READ));
            try {
                EntityPersister persister = context.getSessionFactory().getEntityPersister(resultClass.getName());
                context.addQuerySpaces((String[])persister.getQuerySpaces());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public static interface ResultClassesResolutionContext {
        public SessionFactoryImplementor getSessionFactory();

        public void addQueryReturns(NativeSQLQueryReturn ... var1);

        public void addQuerySpaces(String ... var1);
    }

    public static interface ResultSetMappingResolutionContext {
        public SessionFactoryImplementor getSessionFactory();

        public ResultSetMappingDefinition findResultSetMapping(String var1);

        public void addQueryReturns(NativeSQLQueryReturn ... var1);

        public void addQuerySpaces(String ... var1);
    }
}


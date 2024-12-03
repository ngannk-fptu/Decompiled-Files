/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.hql.spi.ParameterTranslations;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.type.Type;

public interface QueryTranslator {
    public static final String ERROR_CANNOT_FETCH_WITH_ITERATE = "fetch may not be used with scroll() or iterate()";
    public static final String ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR = "Named parameter does not appear in Query: ";
    public static final String ERROR_ORDINAL_PARAMETER_DOES_NOT_APPEAR = "Ordinal parameter [%s] does not appear in Query [%s] ";
    public static final String ERROR_LEGACY_ORDINAL_PARAMS_NO_LONGER_SUPPORTED = "Legacy-style query parameters (`?`) are no longer supported; use JPA-style ordinal parameters (e.g., `?1`) instead : %s";
    public static final String ERROR_CANNOT_DETERMINE_TYPE = "Could not determine type of: ";
    public static final String ERROR_CANNOT_FORMAT_LITERAL = "Could not format constant value to SQL literal: ";

    public void compile(Map var1, boolean var2) throws QueryException, MappingException;

    public List list(SharedSessionContractImplementor var1, QueryParameters var2) throws HibernateException;

    public Iterator iterate(QueryParameters var1, EventSource var2) throws HibernateException;

    public ScrollableResultsImplementor scroll(QueryParameters var1, SharedSessionContractImplementor var2) throws HibernateException;

    public int executeUpdate(QueryParameters var1, SharedSessionContractImplementor var2) throws HibernateException;

    public Set<Serializable> getQuerySpaces();

    public String getQueryIdentifier();

    public String getSQLString();

    public List<String> collectSqlStrings();

    public String getQueryString();

    public Map getEnabledFilters();

    public Type[] getReturnTypes();

    public String[] getReturnAliases();

    public String[][] getColumnNames();

    public ParameterTranslations getParameterTranslations();

    public void validateScrollability() throws HibernateException;

    public boolean containsCollectionFetches();

    public boolean isManipulationStatement();

    default public boolean isUpdateStatement() {
        return this.getQueryString().toLowerCase().trim().startsWith("update");
    }

    default public List<String> getPrimaryFromClauseTables() {
        return new ArrayList<String>();
    }

    public Class getDynamicInstantiationResultType();
}


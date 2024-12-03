/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.spi;

import java.util.Map;
import java.util.Set;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.PropertyMapping;
import org.jboss.logging.Logger;

public class SubselectFetch {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)SubselectFetch.class.getName());
    private static final String FROM_STRING = " from ";
    private final Set resultingEntityKeys;
    private final String queryString;
    private final String alias;
    private final Loadable loadable;
    private final QueryParameters queryParameters;
    private final Map namedParameterLocMap;

    public SubselectFetch(String alias, Loadable loadable, QueryParameters queryParameters, Set resultingEntityKeys, Map namedParameterLocMap) {
        this(SubselectFetch.createSubselectFetchQueryFragment(queryParameters), alias, loadable, queryParameters, resultingEntityKeys, namedParameterLocMap);
    }

    public SubselectFetch(String subselectFetchQueryFragment, String alias, Loadable loadable, QueryParameters queryParameters, Set resultingEntityKeys, Map namedParameterLocMap) {
        this.resultingEntityKeys = resultingEntityKeys;
        this.queryParameters = queryParameters;
        this.namedParameterLocMap = namedParameterLocMap;
        this.loadable = loadable;
        this.alias = alias;
        this.queryString = subselectFetchQueryFragment;
    }

    public static String createSubselectFetchQueryFragment(QueryParameters queryParameters) {
        String subselectQueryFragment;
        String queryString = queryParameters.getFilteredSQL();
        int fromIndex = SubselectFetch.getFromIndex(queryString);
        int orderByIndex = queryString.lastIndexOf("order by");
        String string = subselectQueryFragment = orderByIndex > 0 ? queryString.substring(fromIndex, orderByIndex) : queryString.substring(fromIndex);
        if (LOG.isTraceEnabled()) {
            LOG.tracef("SubselectFetch query fragment: %s", subselectQueryFragment);
        }
        return subselectQueryFragment;
    }

    private static int getFromIndex(String queryString) {
        int index = queryString.indexOf(FROM_STRING);
        if (index < 0) {
            return index;
        }
        while (!SubselectFetch.parenthesesMatch(queryString.substring(0, index))) {
            String subString = queryString.substring(index + FROM_STRING.length());
            int subIndex = subString.indexOf(FROM_STRING);
            if (subIndex < 0) {
                return subIndex;
            }
            index += FROM_STRING.length() + subIndex;
        }
        return index;
    }

    private static boolean parenthesesMatch(String string) {
        int parenCount = 0;
        for (int i = 0; i < string.length(); ++i) {
            char character = string.charAt(i);
            if (character == '(') {
                ++parenCount;
                continue;
            }
            if (character != ')') continue;
            --parenCount;
        }
        return parenCount == 0;
    }

    public QueryParameters getQueryParameters() {
        return this.queryParameters;
    }

    public Set getResult() {
        return this.resultingEntityKeys;
    }

    public String toSubselectString(String ukname) {
        CharSequence[] joinColumns = ukname == null ? StringHelper.qualify(this.alias, this.loadable.getIdentifierColumnNames()) : ((PropertyMapping)((Object)this.loadable)).toColumns(this.alias, ukname);
        return "select " + String.join((CharSequence)", ", joinColumns) + this.queryString;
    }

    public String toString() {
        return "SubselectFetch(" + this.queryString + ')';
    }

    public Map getNamedParameterLocMap() {
        return this.namedParameterLocMap;
    }
}


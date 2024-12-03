/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityGraph;
import org.hibernate.Filter;
import org.hibernate.UnknownProfileException;
import org.hibernate.engine.spi.EffectiveEntityGraph;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.internal.FilterImpl;
import org.hibernate.type.Type;

public class LoadQueryInfluencers
implements Serializable {
    public static final LoadQueryInfluencers NONE = new LoadQueryInfluencers();
    private final SessionFactoryImplementor sessionFactory;
    private String internalFetchProfile;
    private HashSet<String> enabledFetchProfileNames;
    private HashMap<String, Filter> enabledFilters;
    private final EffectiveEntityGraph effectiveEntityGraph = new EffectiveEntityGraph();
    private Boolean readOnly;

    public LoadQueryInfluencers() {
        this(null, null);
    }

    public LoadQueryInfluencers(SessionFactoryImplementor sessionFactory) {
        this(sessionFactory, null);
    }

    public LoadQueryInfluencers(SessionFactoryImplementor sessionFactory, Boolean readOnly) {
        this.sessionFactory = sessionFactory;
        this.readOnly = readOnly;
    }

    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    public String getInternalFetchProfile() {
        return this.internalFetchProfile;
    }

    public void setInternalFetchProfile(String internalFetchProfile) {
        if (this.sessionFactory == null) {
            throw new IllegalStateException("Cannot modify context-less LoadQueryInfluencers");
        }
        this.internalFetchProfile = internalFetchProfile;
    }

    public boolean hasEnabledFilters() {
        return this.enabledFilters != null && !this.enabledFilters.isEmpty();
    }

    public Map<String, Filter> getEnabledFilters() {
        if (this.enabledFilters == null) {
            return Collections.EMPTY_MAP;
        }
        for (Filter filter : this.enabledFilters.values()) {
            filter.validate();
        }
        return this.enabledFilters;
    }

    public Set<String> getEnabledFilterNames() {
        if (this.enabledFilters == null) {
            return Collections.EMPTY_SET;
        }
        return Collections.unmodifiableSet(this.enabledFilters.keySet());
    }

    public Filter getEnabledFilter(String filterName) {
        if (this.enabledFilters == null) {
            return null;
        }
        return this.enabledFilters.get(filterName);
    }

    public Filter enableFilter(String filterName) {
        FilterImpl filter = new FilterImpl(this.sessionFactory.getFilterDefinition(filterName));
        if (this.enabledFilters == null) {
            this.enabledFilters = new HashMap();
        }
        this.enabledFilters.put(filterName, filter);
        return filter;
    }

    public void disableFilter(String filterName) {
        if (this.enabledFilters != null) {
            this.enabledFilters.remove(filterName);
        }
    }

    public Object getFilterParameterValue(String filterParameterName) {
        String[] parsed = LoadQueryInfluencers.parseFilterParameterName(filterParameterName);
        if (this.enabledFilters == null) {
            throw new IllegalArgumentException("Filter [" + parsed[0] + "] currently not enabled");
        }
        FilterImpl filter = (FilterImpl)this.enabledFilters.get(parsed[0]);
        if (filter == null) {
            throw new IllegalArgumentException("Filter [" + parsed[0] + "] currently not enabled");
        }
        return filter.getParameter(parsed[1]);
    }

    public Type getFilterParameterType(String filterParameterName) {
        String[] parsed = LoadQueryInfluencers.parseFilterParameterName(filterParameterName);
        FilterDefinition filterDef = this.sessionFactory.getFilterDefinition(parsed[0]);
        if (filterDef == null) {
            throw new IllegalArgumentException("Filter [" + parsed[0] + "] not defined");
        }
        Type type = filterDef.getParameterType(parsed[1]);
        if (type == null) {
            throw new InternalError("Unable to locate type for filter parameter");
        }
        return type;
    }

    public static String[] parseFilterParameterName(String filterParameterName) {
        int dot = filterParameterName.lastIndexOf(46);
        if (dot <= 0) {
            throw new IllegalArgumentException("Invalid filter-parameter name format [" + filterParameterName + "]; expecting {filter-name}.{param-name}");
        }
        String filterName = filterParameterName.substring(0, dot);
        String parameterName = filterParameterName.substring(dot + 1);
        return new String[]{filterName, parameterName};
    }

    public boolean hasEnabledFetchProfiles() {
        return this.enabledFetchProfileNames != null && !this.enabledFetchProfileNames.isEmpty();
    }

    public Set<String> getEnabledFetchProfileNames() {
        if (this.enabledFetchProfileNames == null) {
            return Collections.EMPTY_SET;
        }
        return this.enabledFetchProfileNames;
    }

    private void checkFetchProfileName(String name) {
        if (!this.sessionFactory.containsFetchProfileDefinition(name)) {
            throw new UnknownProfileException(name);
        }
    }

    public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
        this.checkFetchProfileName(name);
        return this.enabledFetchProfileNames != null && this.enabledFetchProfileNames.contains(name);
    }

    public void enableFetchProfile(String name) throws UnknownProfileException {
        this.checkFetchProfileName(name);
        if (this.enabledFetchProfileNames == null) {
            this.enabledFetchProfileNames = new HashSet();
        }
        this.enabledFetchProfileNames.add(name);
    }

    public void disableFetchProfile(String name) throws UnknownProfileException {
        this.checkFetchProfileName(name);
        if (this.enabledFetchProfileNames != null) {
            this.enabledFetchProfileNames.remove(name);
        }
    }

    public EffectiveEntityGraph getEffectiveEntityGraph() {
        return this.effectiveEntityGraph;
    }

    @Deprecated
    public EntityGraph getFetchGraph() {
        if (this.effectiveEntityGraph.getSemantic() != GraphSemantic.FETCH) {
            return null;
        }
        return this.effectiveEntityGraph.getGraph();
    }

    @Deprecated
    public void setFetchGraph(EntityGraph fetchGraph) {
        this.effectiveEntityGraph.applyGraph((RootGraphImplementor)fetchGraph, GraphSemantic.FETCH);
    }

    @Deprecated
    public EntityGraph getLoadGraph() {
        if (this.effectiveEntityGraph.getSemantic() != GraphSemantic.LOAD) {
            return null;
        }
        return this.effectiveEntityGraph.getGraph();
    }

    @Deprecated
    public void setLoadGraph(EntityGraph loadGraph) {
        this.effectiveEntityGraph.applyGraph((RootGraphImplementor)loadGraph, GraphSemantic.LOAD);
    }

    public Boolean getReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }
}


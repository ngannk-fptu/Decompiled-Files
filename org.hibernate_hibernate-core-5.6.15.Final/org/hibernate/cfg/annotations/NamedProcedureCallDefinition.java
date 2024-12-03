/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.NamedStoredProcedureQuery
 *  javax.persistence.ParameterMode
 *  javax.persistence.StoredProcedureParameter
 */
package org.hibernate.cfg.annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import org.hibernate.MappingException;
import org.hibernate.cfg.annotations.QueryHintDefinition;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.procedure.ProcedureCallMemento;
import org.hibernate.procedure.internal.ProcedureCallMementoImpl;
import org.hibernate.procedure.internal.Util;
import org.hibernate.procedure.spi.ParameterStrategy;

public class NamedProcedureCallDefinition {
    private final String registeredName;
    private final String procedureName;
    private final Class[] resultClasses;
    private final String[] resultSetMappings;
    private final ParameterDefinitions parameterDefinitions;
    private final Map<String, Object> hints;

    NamedProcedureCallDefinition(NamedStoredProcedureQuery annotation) {
        boolean specifiesResultSetMappings;
        this.registeredName = annotation.name();
        this.procedureName = annotation.procedureName();
        this.hints = new QueryHintDefinition(annotation.hints()).getHintsMap();
        this.resultClasses = annotation.resultClasses();
        this.resultSetMappings = annotation.resultSetMappings();
        this.parameterDefinitions = new ParameterDefinitions(annotation.parameters(), this.hints);
        boolean specifiesResultClasses = this.resultClasses != null && this.resultClasses.length > 0;
        boolean bl = specifiesResultSetMappings = this.resultSetMappings != null && this.resultSetMappings.length > 0;
        if (specifiesResultClasses && specifiesResultSetMappings) {
            throw new MappingException(String.format("NamedStoredProcedureQuery [%s] specified both resultClasses and resultSetMappings", this.registeredName));
        }
    }

    public String getRegisteredName() {
        return this.registeredName;
    }

    public String getProcedureName() {
        return this.procedureName;
    }

    public ProcedureCallMemento toMemento(final SessionFactoryImpl sessionFactory, final Map<String, ResultSetMappingDefinition> resultSetMappingDefinitions) {
        boolean specifiesResultSetMappings;
        final ArrayList collectedQueryReturns = new ArrayList();
        final HashSet<String> collectedQuerySpaces = new HashSet<String>();
        boolean specifiesResultClasses = this.resultClasses != null && this.resultClasses.length > 0;
        boolean bl = specifiesResultSetMappings = this.resultSetMappings != null && this.resultSetMappings.length > 0;
        if (specifiesResultClasses) {
            Util.resolveResultClasses(new Util.ResultClassesResolutionContext(){

                @Override
                public SessionFactoryImplementor getSessionFactory() {
                    return sessionFactory;
                }

                @Override
                public void addQueryReturns(NativeSQLQueryReturn ... queryReturns) {
                    Collections.addAll(collectedQueryReturns, queryReturns);
                }

                @Override
                public void addQuerySpaces(String ... spaces) {
                    Collections.addAll(collectedQuerySpaces, spaces);
                }
            }, this.resultClasses);
        } else if (specifiesResultSetMappings) {
            Util.resolveResultSetMappings(new Util.ResultSetMappingResolutionContext(){

                @Override
                public SessionFactoryImplementor getSessionFactory() {
                    return sessionFactory;
                }

                @Override
                public ResultSetMappingDefinition findResultSetMapping(String name) {
                    return (ResultSetMappingDefinition)resultSetMappingDefinitions.get(name);
                }

                @Override
                public void addQueryReturns(NativeSQLQueryReturn ... queryReturns) {
                    Collections.addAll(collectedQueryReturns, queryReturns);
                }

                @Override
                public void addQuerySpaces(String ... spaces) {
                    Collections.addAll(collectedQuerySpaces, spaces);
                }
            }, this.resultSetMappings);
        }
        return new ProcedureCallMementoImpl(this.procedureName, collectedQueryReturns.toArray(new NativeSQLQueryReturn[collectedQueryReturns.size()]), this.parameterDefinitions.getParameterStrategy(), this.parameterDefinitions.toMementos(sessionFactory), collectedQuerySpaces, this.hints);
    }

    private static String normalize(String name) {
        return StringHelper.isNotEmpty(name) ? name : null;
    }

    static class ParameterDefinition {
        private final Integer position;
        private final String name;
        private final ParameterMode parameterMode;
        private final Class type;
        private final Boolean explicitPassNullSetting;

        static ParameterDefinition from(ParameterStrategy parameterStrategy, StoredProcedureParameter parameterAnnotation, int adjustedPosition, Map<String, Object> queryHintMap) {
            Object explicitNullPassingHint = parameterStrategy == ParameterStrategy.NAMED ? queryHintMap.get("hibernate.proc.param_null_passing." + parameterAnnotation.name()) : queryHintMap.get("hibernate.proc.param_null_passing." + adjustedPosition);
            return new ParameterDefinition(adjustedPosition, parameterAnnotation, ParameterDefinition.interpretBoolean(explicitNullPassingHint));
        }

        private static Boolean interpretBoolean(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof Boolean) {
                return (Boolean)value;
            }
            return Boolean.valueOf(value.toString());
        }

        ParameterDefinition(int position, StoredProcedureParameter annotation, Boolean explicitPassNullSetting) {
            this.position = position;
            this.name = NamedProcedureCallDefinition.normalize(annotation.name());
            this.parameterMode = annotation.mode();
            this.type = annotation.type();
            this.explicitPassNullSetting = explicitPassNullSetting;
        }

        public ProcedureCallMementoImpl.ParameterMemento toMemento(SessionFactoryImpl sessionFactory) {
            boolean initialPassNullSetting = this.explicitPassNullSetting != null ? this.explicitPassNullSetting.booleanValue() : sessionFactory.getSessionFactoryOptions().isProcedureParameterNullPassingEnabled();
            return new ProcedureCallMementoImpl.ParameterMemento(this.position, this.name, this.parameterMode, this.type, sessionFactory.getTypeResolver().heuristicType(this.type.getName()), initialPassNullSetting);
        }
    }

    static class ParameterDefinitions {
        private final ParameterStrategy parameterStrategy;
        private final ParameterDefinition[] parameterDefinitions;

        ParameterDefinitions(StoredProcedureParameter[] parameters, Map<String, Object> queryHintMap) {
            if (parameters == null || parameters.length == 0) {
                this.parameterStrategy = ParameterStrategy.POSITIONAL;
                this.parameterDefinitions = new ParameterDefinition[0];
            } else {
                this.parameterStrategy = StringHelper.isNotEmpty(parameters[0].name()) ? ParameterStrategy.NAMED : ParameterStrategy.POSITIONAL;
                this.parameterDefinitions = new ParameterDefinition[parameters.length];
                for (int i = 0; i < parameters.length; ++i) {
                    this.parameterDefinitions[i] = ParameterDefinition.from(this.parameterStrategy, parameters[i], i + 1, queryHintMap);
                }
            }
        }

        public ParameterStrategy getParameterStrategy() {
            return this.parameterStrategy;
        }

        public List<ProcedureCallMementoImpl.ParameterMemento> toMementos(SessionFactoryImpl sessionFactory) {
            ArrayList<ProcedureCallMementoImpl.ParameterMemento> mementos = new ArrayList<ProcedureCallMementoImpl.ParameterMemento>();
            for (ParameterDefinition definition : this.parameterDefinitions) {
                mementos.add(definition.toMemento(sessionFactory));
            }
            return mementos;
        }
    }
}


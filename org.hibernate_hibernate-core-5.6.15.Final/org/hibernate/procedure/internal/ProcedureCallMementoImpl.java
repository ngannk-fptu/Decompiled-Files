/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 */
package org.hibernate.procedure.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.ParameterMode;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureCallMemento;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.hibernate.procedure.spi.ParameterStrategy;
import org.hibernate.type.Type;

public class ProcedureCallMementoImpl
implements ProcedureCallMemento {
    private final String procedureName;
    private final NativeSQLQueryReturn[] queryReturns;
    private final ParameterStrategy parameterStrategy;
    private final List<ParameterMemento> parameterDeclarations;
    private final Set<String> synchronizedQuerySpaces;
    private final Map<String, Object> hintsMap;

    public ProcedureCallMementoImpl(String procedureName, NativeSQLQueryReturn[] queryReturns, ParameterStrategy parameterStrategy, List<ParameterMemento> parameterDeclarations, Set<String> synchronizedQuerySpaces, Map<String, Object> hintsMap) {
        this.procedureName = procedureName;
        this.queryReturns = queryReturns;
        this.parameterStrategy = parameterStrategy;
        this.parameterDeclarations = parameterDeclarations;
        this.synchronizedQuerySpaces = synchronizedQuerySpaces;
        this.hintsMap = hintsMap;
    }

    @Override
    public ProcedureCall makeProcedureCall(SharedSessionContractImplementor session) {
        return new ProcedureCallImpl(session, this);
    }

    public String getProcedureName() {
        return this.procedureName;
    }

    public NativeSQLQueryReturn[] getQueryReturns() {
        return this.queryReturns;
    }

    public ParameterStrategy getParameterStrategy() {
        return this.parameterStrategy;
    }

    public List<ParameterMemento> getParameterDeclarations() {
        return this.parameterDeclarations;
    }

    public Set<String> getSynchronizedQuerySpaces() {
        return this.synchronizedQuerySpaces;
    }

    @Override
    public Map<String, Object> getHintsMap() {
        return this.hintsMap;
    }

    public static class ParameterMemento {
        private final Integer position;
        private final String name;
        private final ParameterMode mode;
        private final Class type;
        private final Type hibernateType;
        private final boolean passNulls;

        public ParameterMemento(int position, String name, ParameterMode mode, Class type, Type hibernateType, boolean passNulls) {
            this.position = position;
            this.name = name;
            this.mode = mode;
            this.type = type;
            this.hibernateType = hibernateType;
            this.passNulls = passNulls;
        }

        public Integer getPosition() {
            return this.position;
        }

        public String getName() {
            return this.name;
        }

        public ParameterMode getMode() {
            return this.mode;
        }

        public Class getType() {
            return this.type;
        }

        public Type getHibernateType() {
            return this.hibernateType;
        }

        public boolean isPassNullsEnabled() {
            return this.passNulls;
        }

        public static ParameterMemento fromRegistration(ParameterRegistrationImplementor registration) {
            return new ParameterMemento(registration.getPosition(), registration.getName(), registration.getMode(), registration.getParameterType(), registration.getHibernateType(), registration.isPassNullsEnabled());
        }
    }
}


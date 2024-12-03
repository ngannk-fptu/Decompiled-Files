/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 *  javax.persistence.StoredProcedureQuery
 */
package org.hibernate.procedure;

import java.util.List;
import java.util.Map;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.BasicQueryContract;
import org.hibernate.MappingException;
import org.hibernate.SynchronizeableQuery;
import org.hibernate.procedure.NamedParametersNotSupportedException;
import org.hibernate.procedure.ParameterRegistration;
import org.hibernate.procedure.ProcedureCallMemento;
import org.hibernate.procedure.ProcedureOutputs;
import org.hibernate.query.CommonQueryContract;

public interface ProcedureCall
extends BasicQueryContract<CommonQueryContract>,
SynchronizeableQuery,
StoredProcedureQuery {
    public ProcedureCall addSynchronizedQuerySpace(String var1);

    public ProcedureCall addSynchronizedEntityName(String var1) throws MappingException;

    public ProcedureCall addSynchronizedEntityClass(Class var1) throws MappingException;

    public String getProcedureName();

    public <T> ParameterRegistration<T> registerParameter(int var1, Class<T> var2, ParameterMode var3);

    public ProcedureCall registerParameter0(int var1, Class var2, ParameterMode var3);

    public ParameterRegistration getParameterRegistration(int var1);

    public <T> ParameterRegistration<T> registerParameter(String var1, Class<T> var2, ParameterMode var3) throws NamedParametersNotSupportedException;

    public ProcedureCall registerParameter0(String var1, Class var2, ParameterMode var3) throws NamedParametersNotSupportedException;

    public ParameterRegistration getParameterRegistration(String var1);

    public List<ParameterRegistration> getRegisteredParameters();

    public ProcedureOutputs getOutputs();

    public ProcedureCallMemento extractMemento(Map<String, Object> var1);

    public ProcedureCallMemento extractMemento();
}


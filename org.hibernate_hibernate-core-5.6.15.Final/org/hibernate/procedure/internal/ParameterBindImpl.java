/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 *  javax.persistence.TemporalType
 *  org.jboss.logging.Logger
 */
package org.hibernate.procedure.internal;

import javax.persistence.ParameterMode;
import javax.persistence.TemporalType;
import org.hibernate.procedure.ParameterBind;
import org.hibernate.query.internal.BindingTypeHelper;
import org.hibernate.query.procedure.internal.ProcedureParamBindings;
import org.hibernate.query.procedure.spi.ProcedureParameterImplementor;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class ParameterBindImpl<T>
implements ParameterBind<T> {
    private static final Logger log = Logger.getLogger(ParameterBindImpl.class);
    private final ProcedureParameterImplementor procedureParameter;
    private final ProcedureParamBindings procedureParamBindings;
    private boolean isBound;
    private T value;
    private Type hibernateType;
    private TemporalType explicitTemporalType;

    public ParameterBindImpl(ProcedureParameterImplementor procedureParameter, ProcedureParamBindings procedureParamBindings) {
        this.procedureParameter = procedureParameter;
        this.procedureParamBindings = procedureParamBindings;
        this.hibernateType = procedureParameter.getHibernateType();
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public TemporalType getExplicitTemporalType() {
        return this.explicitTemporalType;
    }

    @Override
    public boolean isBound() {
        return this.isBound;
    }

    @Override
    public void setBindValue(T value) {
        this.internalSetValue(value);
        if (value != null && this.hibernateType == null) {
            this.hibernateType = this.procedureParamBindings.getProcedureCall().getSession().getFactory().getTypeResolver().heuristicType(value.getClass().getName());
            log.debugf("Using heuristic type [%s] based on bind value [%s] as `bindType`", (Object)this.hibernateType, value);
        }
    }

    private void internalSetValue(T value) {
        if (this.procedureParameter.getMode() != ParameterMode.IN && this.procedureParameter.getMode() != ParameterMode.INOUT) {
            throw new IllegalStateException("Can only bind values for IN/INOUT parameters : " + this.procedureParameter);
        }
        if (this.procedureParameter.getParameterType() != null) {
            if (value == null) {
                if (!this.procedureParameter.isPassNullsEnabled()) {
                    throw new IllegalArgumentException("The parameter " + (this.procedureParameter.getName() != null ? "named [" + this.procedureParameter.getName() + "]" : "at position [" + this.procedureParameter.getPosition() + "]") + " was null. You need to call ParameterRegistration#enablePassingNulls(true) in order to pass null parameters.");
                }
            } else if (!this.procedureParameter.getParameterType().isInstance(value) && !this.procedureParameter.getHibernateType().getReturnedClass().isInstance(value)) {
                throw new IllegalArgumentException("Bind value [" + value + "] was not of specified type [" + this.procedureParameter.getParameterType());
            }
        }
        this.value = value;
        this.isBound = true;
    }

    @Override
    public void setBindValue(T value, Type clarifiedType) {
        this.internalSetValue(value);
        this.hibernateType = clarifiedType;
        log.debugf("Using explicit type [%s] as `bindType`", (Object)this.hibernateType, value);
    }

    @Override
    public void setBindValue(T value, TemporalType clarifiedTemporalType) {
        this.internalSetValue(value);
        this.hibernateType = BindingTypeHelper.INSTANCE.determineTypeForTemporalType(clarifiedTemporalType, this.hibernateType, value);
        this.explicitTemporalType = clarifiedTemporalType;
        log.debugf("Using type [%s] (based on TemporalType [%s] as `bindType`", (Object)this.hibernateType, (Object)clarifiedTemporalType);
    }

    @Override
    public T getBindValue() {
        if (!this.isBound) {
            throw new IllegalStateException("Value not yet bound");
        }
        return this.value;
    }

    @Override
    public Type getBindType() {
        return this.hibernateType;
    }
}


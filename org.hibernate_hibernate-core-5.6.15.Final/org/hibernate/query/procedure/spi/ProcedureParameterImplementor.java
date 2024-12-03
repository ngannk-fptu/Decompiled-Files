/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.procedure.spi;

import org.hibernate.Incubating;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.hibernate.query.procedure.ProcedureParameter;

@Incubating
public interface ProcedureParameterImplementor<T>
extends ProcedureParameter<T>,
ParameterRegistrationImplementor<T> {
}


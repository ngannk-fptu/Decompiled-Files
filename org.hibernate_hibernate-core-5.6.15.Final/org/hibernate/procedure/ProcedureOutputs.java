/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.procedure;

import org.hibernate.procedure.ParameterRegistration;
import org.hibernate.result.Outputs;

public interface ProcedureOutputs
extends Outputs {
    public <T> T getOutputParameterValue(ParameterRegistration<T> var1);

    public Object getOutputParameterValue(String var1);

    public Object getOutputParameterValue(int var1);
}


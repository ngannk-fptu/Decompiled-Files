/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.procedure.internal;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import org.hibernate.engine.jdbc.cursor.spi.RefCursorSupport;
import org.hibernate.procedure.ParameterRegistration;
import org.hibernate.procedure.ProcedureOutputs;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.hibernate.result.Output;
import org.hibernate.result.internal.OutputsImpl;

public class ProcedureOutputsImpl
extends OutputsImpl
implements ProcedureOutputs {
    private final ProcedureCallImpl procedureCall;
    private final CallableStatement callableStatement;
    private final ParameterRegistrationImplementor[] refCursorParameters;
    private int refCursorParamIndex;

    ProcedureOutputsImpl(ProcedureCallImpl procedureCall, CallableStatement callableStatement) {
        super(procedureCall, callableStatement);
        this.procedureCall = procedureCall;
        this.callableStatement = callableStatement;
        this.refCursorParameters = procedureCall.collectRefCursorParameters();
    }

    @Override
    public <T> T getOutputParameterValue(ParameterRegistration<T> parameterRegistration) {
        return ((ParameterRegistrationImplementor)parameterRegistration).extract(this.callableStatement);
    }

    @Override
    public Object getOutputParameterValue(String name) {
        return this.procedureCall.getParameterRegistration(name).extract(this.callableStatement);
    }

    @Override
    public Object getOutputParameterValue(int position) {
        return this.procedureCall.getParameterRegistration(position).extract(this.callableStatement);
    }

    @Override
    protected OutputsImpl.CurrentReturnState buildCurrentReturnState(boolean isResultSet, int updateCount) {
        return new ProcedureCurrentReturnState(isResultSet, updateCount, this.refCursorParamIndex);
    }

    protected class ProcedureCurrentReturnState
    extends OutputsImpl.CurrentReturnState {
        private final int refCursorParamIndex;

        private ProcedureCurrentReturnState(boolean isResultSet, int updateCount, int refCursorParamIndex) {
            super(ProcedureOutputsImpl.this, isResultSet, updateCount);
            this.refCursorParamIndex = refCursorParamIndex;
        }

        @Override
        public boolean indicatesMoreOutputs() {
            return super.indicatesMoreOutputs() || ProcedureOutputsImpl.this.refCursorParamIndex < ProcedureOutputsImpl.this.refCursorParameters.length;
        }

        @Override
        protected boolean hasExtendedReturns() {
            return this.refCursorParamIndex < ProcedureOutputsImpl.this.refCursorParameters.length;
        }

        @Override
        protected Output buildExtendedReturn() {
            ProcedureOutputsImpl.this.refCursorParamIndex++;
            ParameterRegistrationImplementor refCursorParam = ProcedureOutputsImpl.this.refCursorParameters[this.refCursorParamIndex];
            ResultSet resultSet = refCursorParam.getName() != null ? ProcedureOutputsImpl.this.procedureCall.getSession().getFactory().getServiceRegistry().getService(RefCursorSupport.class).getResultSet(ProcedureOutputsImpl.this.callableStatement, refCursorParam.getName()) : ProcedureOutputsImpl.this.procedureCall.getSession().getFactory().getServiceRegistry().getService(RefCursorSupport.class).getResultSet(ProcedureOutputsImpl.this.callableStatement, refCursorParam.getPosition());
            return this.buildResultSetOutput(() -> ProcedureOutputsImpl.this.extractResults(resultSet));
        }
    }
}


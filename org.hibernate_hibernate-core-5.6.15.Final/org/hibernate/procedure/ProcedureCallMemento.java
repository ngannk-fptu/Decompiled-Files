/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.procedure;

import java.util.Map;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.procedure.ProcedureCall;

public interface ProcedureCallMemento {
    default public ProcedureCall makeProcedureCall(Session session) {
        return this.makeProcedureCall((SharedSessionContractImplementor)((Object)session));
    }

    default public ProcedureCall makeProcedureCall(SessionImplementor session) {
        return this.makeProcedureCall((SharedSessionContractImplementor)session);
    }

    public ProcedureCall makeProcedureCall(SharedSessionContractImplementor var1);

    public Map<String, Object> getHintsMap();
}


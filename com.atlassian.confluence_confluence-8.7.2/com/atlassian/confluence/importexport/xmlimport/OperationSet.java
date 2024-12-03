/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.Operation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Deprecated
public class OperationSet {
    private final Set<TransientHibernateHandle> waitingFor;
    private List<Operation> operations = new ArrayList<Operation>();

    public OperationSet(Set<TransientHibernateHandle> waitingFor) {
        this.waitingFor = new HashSet<TransientHibernateHandle>(waitingFor);
    }

    public void addOperation(Operation op) {
        this.operations.add(op);
    }

    public Set getWaitingFor() {
        return this.waitingFor;
    }

    public List<Operation> getOperations() {
        return this.operations;
    }
}


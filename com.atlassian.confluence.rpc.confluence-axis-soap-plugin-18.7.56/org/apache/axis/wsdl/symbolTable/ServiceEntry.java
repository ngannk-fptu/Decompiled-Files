/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Service
 */
package org.apache.axis.wsdl.symbolTable;

import javax.wsdl.Service;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;

public class ServiceEntry
extends SymTabEntry {
    private Service service;

    public ServiceEntry(Service service) {
        super(service.getQName());
        this.service = service;
    }

    public Service getService() {
        return this.service;
    }
}


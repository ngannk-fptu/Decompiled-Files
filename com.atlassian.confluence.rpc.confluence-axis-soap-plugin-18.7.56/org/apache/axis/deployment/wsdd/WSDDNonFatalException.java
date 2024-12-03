/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import org.apache.axis.deployment.wsdd.WSDDException;

public class WSDDNonFatalException
extends WSDDException {
    public WSDDNonFatalException(String msg) {
        super(msg);
    }

    public WSDDNonFatalException(Exception e) {
        super(e);
    }
}


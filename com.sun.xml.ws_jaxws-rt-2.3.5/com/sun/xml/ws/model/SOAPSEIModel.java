/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebParam$Mode
 */
package com.sun.xml.ws.model;

import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public class SOAPSEIModel
extends AbstractSEIModelImpl {
    private final Lock lock = new ReentrantLock();

    public SOAPSEIModel(WebServiceFeatureList features) {
        super(features);
    }

    @Override
    protected void populateMaps() {
        int emptyBodyCount = 0;
        for (JavaMethodImpl jm : this.getJavaMethods()) {
            this.put(jm.getMethod(), jm);
            boolean bodyFound = false;
            for (ParameterImpl p : jm.getRequestParameters()) {
                ParameterBinding binding = p.getBinding();
                if (!binding.isBody()) continue;
                this.put(p.getName(), jm);
                bodyFound = true;
            }
            if (bodyFound) continue;
            this.put(this.emptyBodyName, jm);
            ++emptyBodyCount;
        }
        if (emptyBodyCount > 1) {
            // empty if block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<QName> getKnownHeaders() {
        HashSet<QName> headers = new HashSet<QName>();
        try {
            this.lock.lock();
            for (JavaMethodImpl method : this.getJavaMethods()) {
                Iterator<ParameterImpl> params = method.getRequestParameters().iterator();
                this.fillHeaders(params, headers, WebParam.Mode.IN);
                params = method.getResponseParameters().iterator();
                this.fillHeaders(params, headers, WebParam.Mode.OUT);
            }
        }
        finally {
            this.lock.unlock();
        }
        return headers;
    }

    private void fillHeaders(Iterator<ParameterImpl> params, Set<QName> headers, WebParam.Mode mode) {
        while (params.hasNext()) {
            ParameterImpl param = params.next();
            ParameterBinding binding = mode == WebParam.Mode.IN ? param.getInBinding() : param.getOutBinding();
            QName name = param.getName();
            if (!binding.isHeader() || headers.contains(name)) continue;
            headers.add(name);
        }
    }
}


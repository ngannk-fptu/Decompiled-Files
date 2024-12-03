/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import org.apache.tomcat.util.modeler.FeatureInfo;
import org.apache.tomcat.util.modeler.ParameterInfo;

public class OperationInfo
extends FeatureInfo {
    private static final long serialVersionUID = 4418342922072614875L;
    protected String impact = "UNKNOWN";
    protected String role = "operation";
    protected final ReadWriteLock parametersLock = new ReentrantReadWriteLock();
    protected ParameterInfo[] parameters = new ParameterInfo[0];

    public String getImpact() {
        return this.impact;
    }

    public void setImpact(String impact) {
        this.impact = impact == null ? null : impact.toUpperCase(Locale.ENGLISH);
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReturnType() {
        if (this.type == null) {
            this.type = "void";
        }
        return this.type;
    }

    public void setReturnType(String returnType) {
        this.type = returnType;
    }

    public ParameterInfo[] getSignature() {
        Lock readLock = this.parametersLock.readLock();
        readLock.lock();
        try {
            ParameterInfo[] parameterInfoArray = this.parameters;
            return parameterInfoArray;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addParameter(ParameterInfo parameter) {
        Lock writeLock = this.parametersLock.writeLock();
        writeLock.lock();
        try {
            ParameterInfo[] results = new ParameterInfo[this.parameters.length + 1];
            System.arraycopy(this.parameters, 0, results, 0, this.parameters.length);
            results[this.parameters.length] = parameter;
            this.parameters = results;
            this.info = null;
        }
        finally {
            writeLock.unlock();
        }
    }

    MBeanOperationInfo createOperationInfo() {
        if (this.info == null) {
            int impact = 3;
            if ("ACTION".equals(this.getImpact())) {
                impact = 1;
            } else if ("ACTION_INFO".equals(this.getImpact())) {
                impact = 2;
            } else if ("INFO".equals(this.getImpact())) {
                impact = 0;
            }
            this.info = new MBeanOperationInfo(this.getName(), this.getDescription(), this.getMBeanParameterInfo(), this.getReturnType(), impact);
        }
        return (MBeanOperationInfo)this.info;
    }

    protected MBeanParameterInfo[] getMBeanParameterInfo() {
        ParameterInfo[] params = this.getSignature();
        MBeanParameterInfo[] parameters = new MBeanParameterInfo[params.length];
        for (int i = 0; i < params.length; ++i) {
            parameters[i] = params[i].createParameterInfo();
        }
        return parameters;
    }
}


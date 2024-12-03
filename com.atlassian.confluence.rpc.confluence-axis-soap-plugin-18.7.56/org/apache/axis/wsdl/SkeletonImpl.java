/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl;

import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.wsdl.Skeleton;

public class SkeletonImpl
implements Skeleton {
    private static HashMap table = null;

    public SkeletonImpl() {
        if (table == null) {
            table = new HashMap();
        }
    }

    public void add(String operation, QName[] names, ParameterMode[] modes, String inputNamespace, String outputNamespace, String soapAction) {
        table.put(operation, new MetaInfo(names, modes, inputNamespace, outputNamespace, soapAction));
    }

    public void add(String operation, String[] names, ParameterMode[] modes, String inputNamespace, String outputNamespace, String soapAction) {
        QName[] qnames = new QName[names.length];
        for (int i = 0; i < names.length; ++i) {
            QName qname;
            qnames[i] = qname = new QName(null, names[i]);
        }
        this.add(operation, qnames, modes, inputNamespace, outputNamespace, soapAction);
    }

    public QName getParameterName(String operationName, int n) {
        MetaInfo value = (MetaInfo)table.get(operationName);
        if (value == null || value.names == null || value.names.length <= n + 1) {
            return null;
        }
        return value.names[n + 1];
    }

    public ParameterMode getParameterMode(String operationName, int n) {
        MetaInfo value = (MetaInfo)table.get(operationName);
        if (value == null || value.modes == null || value.modes.length <= n + 1) {
            return null;
        }
        return value.modes[n + 1];
    }

    public String getInputNamespace(String operationName) {
        MetaInfo value = (MetaInfo)table.get(operationName);
        if (value == null) {
            return null;
        }
        return value.inputNamespace;
    }

    public String getOutputNamespace(String operationName) {
        MetaInfo value = (MetaInfo)table.get(operationName);
        if (value == null) {
            return null;
        }
        return value.outputNamespace;
    }

    public String getSOAPAction(String operationName) {
        MetaInfo value = (MetaInfo)table.get(operationName);
        if (value == null) {
            return null;
        }
        return value.soapAction;
    }

    class MetaInfo {
        QName[] names;
        ParameterMode[] modes;
        String inputNamespace;
        String outputNamespace;
        String soapAction;

        MetaInfo(QName[] names, ParameterMode[] modes, String inputNamespace, String outputNamespace, String soapAction) {
            this.names = names;
            this.modes = modes;
            this.inputNamespace = inputNamespace;
            this.outputNamespace = outputNamespace;
            this.soapAction = soapAction;
        }
    }
}


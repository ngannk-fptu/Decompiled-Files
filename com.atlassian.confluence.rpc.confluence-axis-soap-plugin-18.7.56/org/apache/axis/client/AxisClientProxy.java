/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.Holder;
import org.apache.axis.client.Call;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.utils.JavaUtils;

public class AxisClientProxy
implements InvocationHandler {
    private Call call;
    private QName portName;
    static /* synthetic */ Class class$java$lang$Object;

    AxisClientProxy(Call call, QName portName) {
        this.call = call;
        this.portName = portName;
    }

    private Object[] proxyParams2CallParams(Object[] proxyParams) throws JavaUtils.HolderException {
        OperationDesc operationDesc = this.call.getOperation();
        if (operationDesc == null) {
            return proxyParams;
        }
        Vector<Object> paramsCall = new Vector<Object>();
        for (int i = 0; proxyParams != null && i < proxyParams.length; ++i) {
            Object param = proxyParams[i];
            ParameterDesc paramDesc = operationDesc.getParameter(i);
            if (paramDesc.getMode() == 3) {
                paramsCall.add(JavaUtils.getHolderValue((Holder)param));
                continue;
            }
            if (paramDesc.getMode() != 1) continue;
            paramsCall.add(param);
        }
        return paramsCall.toArray();
    }

    private void callOutputParams2proxyParams(Object[] proxyParams) throws JavaUtils.HolderException {
        OperationDesc operationDesc = this.call.getOperation();
        if (operationDesc == null) {
            return;
        }
        Map outputParams = this.call.getOutputParams();
        for (int i = 0; i < operationDesc.getNumParams(); ++i) {
            Object param = proxyParams[i];
            ParameterDesc paramDesc = operationDesc.getParameter(i);
            if (paramDesc.getMode() != 3 && paramDesc.getMode() != 2) continue;
            JavaUtils.setHolderValue((Holder)param, outputParams.get(paramDesc.getQName()));
        }
    }

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Object outValue;
        if (method.getName().equals("_setProperty")) {
            this.call.setProperty((String)objects[0], objects[1]);
            return null;
        }
        if (method.getName().equals("_getProperty")) {
            return this.call.getProperty((String)objects[0]);
        }
        if (method.getName().equals("_getPropertyNames")) {
            return this.call.getPropertyNames();
        }
        if ((class$java$lang$Object == null ? (class$java$lang$Object = AxisClientProxy.class$("java.lang.Object")) : class$java$lang$Object).equals(method.getDeclaringClass())) {
            return method.invoke((Object)this.call, objects);
        }
        if (this.call.getTargetEndpointAddress() != null && this.call.getPortName() != null) {
            this.call.setOperation(method.getName());
            Object[] paramsCall = this.proxyParams2CallParams(objects);
            outValue = this.call.invoke(paramsCall);
        } else if (this.portName != null) {
            this.call.setOperation(this.portName, method.getName());
            Object[] paramsCall = this.proxyParams2CallParams(objects);
            outValue = this.call.invoke(paramsCall);
        } else {
            Object[] paramsCall = objects;
            outValue = this.call.invoke(method.getName(), paramsCall);
        }
        this.callOutputParams2proxyParams(objects);
        return outValue;
    }

    public Call getCall() {
        return this.call;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


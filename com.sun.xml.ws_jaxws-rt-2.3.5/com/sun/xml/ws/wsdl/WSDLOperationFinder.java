/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.WSDLOperationMapping;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.wsdl.DispatchException;
import javax.xml.namespace.QName;

public abstract class WSDLOperationFinder {
    protected final WSDLPort wsdlModel;
    protected final WSBinding binding;
    protected final SEIModel seiModel;

    public WSDLOperationFinder(@NotNull WSDLPort wsdlModel, @NotNull WSBinding binding, @Nullable SEIModel seiModel) {
        this.wsdlModel = wsdlModel;
        this.binding = binding;
        this.seiModel = seiModel;
    }

    public QName getWSDLOperationQName(Packet request) throws DispatchException {
        WSDLOperationMapping m = this.getWSDLOperationMapping(request);
        return m != null ? m.getOperationName() : null;
    }

    public WSDLOperationMapping getWSDLOperationMapping(Packet request) throws DispatchException {
        return null;
    }

    protected WSDLOperationMapping wsdlOperationMapping(JavaMethodImpl j) {
        return new WSDLOperationMappingImpl(j.getOperation(), j);
    }

    protected WSDLOperationMapping wsdlOperationMapping(WSDLBoundOperation o) {
        return new WSDLOperationMappingImpl(o, null);
    }

    static class WSDLOperationMappingImpl
    implements WSDLOperationMapping {
        private WSDLBoundOperation wsdlOperation;
        private JavaMethod javaMethod;
        private QName operationName;

        WSDLOperationMappingImpl(WSDLBoundOperation wsdlOperation, JavaMethodImpl javaMethod) {
            this.wsdlOperation = wsdlOperation;
            this.javaMethod = javaMethod;
            this.operationName = javaMethod != null ? javaMethod.getOperationQName() : wsdlOperation.getName();
        }

        @Override
        public WSDLBoundOperation getWSDLBoundOperation() {
            return this.wsdlOperation;
        }

        @Override
        public JavaMethod getJavaMethod() {
            return this.javaMethod;
        }

        @Override
        public QName getOperationName() {
            return this.operationName;
        }
    }
}


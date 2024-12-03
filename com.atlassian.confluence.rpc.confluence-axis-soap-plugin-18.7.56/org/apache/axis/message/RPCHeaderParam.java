/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPHeaderElement;

public class RPCHeaderParam
extends SOAPHeaderElement {
    public RPCHeaderParam(RPCParam rpcParam) {
        super(rpcParam.getQName().getNamespaceURI(), rpcParam.getQName().getLocalPart(), rpcParam);
    }

    protected void outputImpl(SerializationContext context) throws Exception {
        MessageContext msgContext = context.getMessageContext();
        RPCParam rpcParam = (RPCParam)this.getObjectValue();
        if (this.encodingStyle != null && this.encodingStyle.equals("")) {
            context.registerPrefixForURI("", rpcParam.getQName().getNamespaceURI());
        }
        rpcParam.serialize(context);
    }
}


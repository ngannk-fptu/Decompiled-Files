/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import org.apache.axis.encoding.Target;
import org.apache.axis.message.RPCParam;
import org.xml.sax.SAXException;

public class RPCParamTarget
implements Target {
    private RPCParam param;

    public RPCParamTarget(RPCParam param) {
        this.param = param;
    }

    public void set(Object value) throws SAXException {
        this.param.set(value);
    }
}


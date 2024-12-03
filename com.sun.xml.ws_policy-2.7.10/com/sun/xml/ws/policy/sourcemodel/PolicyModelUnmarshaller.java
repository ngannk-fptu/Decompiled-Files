/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.XmlPolicyModelUnmarshaller;

public abstract class PolicyModelUnmarshaller {
    private static final PolicyModelUnmarshaller xmlUnmarshaller = new XmlPolicyModelUnmarshaller();

    PolicyModelUnmarshaller() {
    }

    public abstract PolicySourceModel unmarshalModel(Object var1) throws PolicyException;

    public static PolicyModelUnmarshaller getXmlUnmarshaller() {
        return xmlUnmarshaller;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.XmlPolicyModelMarshaller;
import java.util.Collection;

public abstract class PolicyModelMarshaller {
    private static final PolicyModelMarshaller defaultXmlMarshaller = new XmlPolicyModelMarshaller(false);
    private static final PolicyModelMarshaller invisibleAssertionXmlMarshaller = new XmlPolicyModelMarshaller(true);

    PolicyModelMarshaller() {
    }

    public abstract void marshal(PolicySourceModel var1, Object var2) throws PolicyException;

    public abstract void marshal(Collection<PolicySourceModel> var1, Object var2) throws PolicyException;

    public static PolicyModelMarshaller getXmlMarshaller(boolean marshallInvisible) {
        return marshallInvisible ? invisibleAssertionXmlMarshaller : defaultXmlMarshaller;
    }
}


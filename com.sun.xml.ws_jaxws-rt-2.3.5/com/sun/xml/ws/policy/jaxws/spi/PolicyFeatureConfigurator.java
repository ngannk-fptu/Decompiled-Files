/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapKey
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.policy.jaxws.spi;

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import java.util.Collection;
import javax.xml.ws.WebServiceFeature;

public interface PolicyFeatureConfigurator {
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey var1, PolicyMap var2) throws PolicyException;
}


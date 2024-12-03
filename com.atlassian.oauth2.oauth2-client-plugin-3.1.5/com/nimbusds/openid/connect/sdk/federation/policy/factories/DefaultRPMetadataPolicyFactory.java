/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.factories;

import com.nimbusds.openid.connect.sdk.federation.policy.MetadataPolicy;
import com.nimbusds.openid.connect.sdk.federation.policy.MetadataPolicyEntry;
import com.nimbusds.openid.connect.sdk.federation.policy.factories.PolicyFormulationException;
import com.nimbusds.openid.connect.sdk.federation.policy.factories.RPMetadataPolicyFactory;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.ValueOperation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;
import net.minidev.json.JSONObject;

@ThreadSafe
public class DefaultRPMetadataPolicyFactory
implements RPMetadataPolicyFactory {
    @Override
    public MetadataPolicy create(OIDCClientMetadata initialMetadata, OIDCClientInformation target) throws PolicyFormulationException {
        MetadataPolicy policy = new MetadataPolicy();
        JSONObject initialJSONObject = initialMetadata.toJSONObject();
        for (Map.Entry<String, Object> entry : target.toJSONObject().entrySet()) {
            if (entry.equals(new AbstractMap.SimpleImmutableEntry(entry.getKey(), initialJSONObject.get(entry.getKey())))) continue;
            MetadataPolicyEntry policyEntry = new MetadataPolicyEntry((String)entry.getKey(), Collections.singletonList(DefaultRPMetadataPolicyFactory.createValueOperation(entry)));
            policy.put(policyEntry);
        }
        return policy;
    }

    private static ValueOperation createValueOperation(Map.Entry<String, Object> objectEntry) throws PolicyFormulationException {
        ValueOperation valueOperation = new ValueOperation();
        if (objectEntry.getValue() instanceof String) {
            valueOperation.configure((String)objectEntry.getValue());
        } else if (objectEntry.getValue() instanceof Boolean) {
            valueOperation.configure((Boolean)objectEntry.getValue());
        } else if (objectEntry.getValue() instanceof Number) {
            valueOperation.configure((Number)objectEntry.getValue());
        } else if (objectEntry.getValue() instanceof List) {
            LinkedList<String> stringList = new LinkedList<String>();
            for (Object item : (List)objectEntry.getValue()) {
                if (item instanceof String) {
                    stringList.add((String)item);
                    continue;
                }
                stringList.add(null);
            }
            valueOperation.configure(stringList);
        } else if (objectEntry.getValue() == null) {
            valueOperation.configure((String)null);
        } else {
            throw new PolicyFormulationException("Unsupported type for " + objectEntry.getKey() + ": " + objectEntry.getValue().getClass() + ": " + objectEntry.getValue());
        }
        return valueOperation;
    }
}


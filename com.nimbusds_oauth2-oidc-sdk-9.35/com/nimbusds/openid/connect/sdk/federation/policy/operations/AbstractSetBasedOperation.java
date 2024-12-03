/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONUtils;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringListConfiguration;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.ConfigurationType;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

abstract class AbstractSetBasedOperation
implements PolicyOperation,
StringListConfiguration {
    protected Set<String> setConfig;
    protected ConfigurationType configType;

    AbstractSetBasedOperation() {
    }

    @Override
    public void configure(List<String> parameter) {
        this.configType = ConfigurationType.STRING_LIST;
        this.setConfig = new LinkedHashSet<String>(parameter);
    }

    @Override
    public void parseConfiguration(Object jsonEntity) throws ParseException {
        this.configure(JSONUtils.toStringList(jsonEntity));
    }

    @Override
    public List<String> getStringListConfiguration() {
        return new LinkedList<String>(this.setConfig);
    }
}


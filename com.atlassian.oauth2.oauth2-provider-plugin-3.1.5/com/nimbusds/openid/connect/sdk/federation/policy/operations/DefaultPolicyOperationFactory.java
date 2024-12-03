/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.openid.connect.sdk.federation.policy.language.OperationName;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.AddOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.DefaultOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.EssentialOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.OneOfOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.PolicyOperationFactory;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.SubsetOfOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.SupersetOfOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.ValueOperation;

public class DefaultPolicyOperationFactory
implements PolicyOperationFactory {
    @Override
    public PolicyOperation createForName(OperationName name) {
        if (SubsetOfOperation.NAME.equals(name)) {
            return new SubsetOfOperation();
        }
        if (OneOfOperation.NAME.equals(name)) {
            return new OneOfOperation();
        }
        if (SupersetOfOperation.NAME.equals(name)) {
            return new SupersetOfOperation();
        }
        if (AddOperation.NAME.equals(name)) {
            return new AddOperation();
        }
        if (ValueOperation.NAME.equals(name)) {
            return new ValueOperation();
        }
        if (DefaultOperation.NAME.equals(name)) {
            return new DefaultOperation();
        }
        if (EssentialOperation.NAME.equals(name)) {
            return new EssentialOperation();
        }
        return null;
    }
}


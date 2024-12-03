/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.azure.core.http.HttpPipeline
 *  com.azure.core.http.HttpPipelineBuilder
 *  com.azure.core.http.policy.HttpLogOptions
 *  com.azure.core.http.policy.HttpLoggingPolicy
 *  com.azure.core.http.policy.HttpPipelinePolicy
 *  com.azure.core.http.policy.HttpPolicyProviders
 *  com.azure.core.http.policy.RetryPolicy
 */
package com.microsoft.sqlserver.jdbc;

import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpLoggingPolicy;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.http.policy.HttpPolicyProviders;
import com.azure.core.http.policy.RetryPolicy;
import com.microsoft.sqlserver.jdbc.KeyVaultCustomCredentialPolicy;
import com.microsoft.sqlserver.jdbc.KeyVaultTokenCredential;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

final class KeyVaultHttpPipelineBuilder {
    private final List<HttpPipelinePolicy> policies;
    private KeyVaultTokenCredential credential;
    private HttpLogOptions httpLogOptions;
    private final RetryPolicy retryPolicy = new RetryPolicy();

    KeyVaultHttpPipelineBuilder() {
        this.httpLogOptions = new HttpLogOptions();
        this.policies = new ArrayList<HttpPipelinePolicy>();
    }

    HttpPipeline buildPipeline() throws SQLServerException {
        ArrayList<Object> pol = new ArrayList<Object>();
        HttpPolicyProviders.addBeforeRetryPolicies(pol);
        pol.add(this.retryPolicy);
        pol.add(new KeyVaultCustomCredentialPolicy(this.credential));
        pol.addAll(this.policies);
        HttpPolicyProviders.addAfterRetryPolicies(pol);
        pol.add(new HttpLoggingPolicy(this.httpLogOptions));
        return new HttpPipelineBuilder().policies(pol.toArray(new HttpPipelinePolicy[0])).build();
    }

    KeyVaultHttpPipelineBuilder credential(KeyVaultTokenCredential credential) throws SQLServerException {
        if (null == credential) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NullValue"));
            Object[] msgArgs1 = new Object[]{"Credential"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        this.credential = credential;
        return this;
    }
}


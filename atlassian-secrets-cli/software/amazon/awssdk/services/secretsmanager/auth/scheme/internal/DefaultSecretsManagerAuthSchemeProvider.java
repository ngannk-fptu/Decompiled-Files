/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.auth.scheme.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.services.secretsmanager.auth.scheme.SecretsManagerAuthSchemeParams;
import software.amazon.awssdk.services.secretsmanager.auth.scheme.SecretsManagerAuthSchemeProvider;

@SdkInternalApi
public final class DefaultSecretsManagerAuthSchemeProvider
implements SecretsManagerAuthSchemeProvider {
    private static final DefaultSecretsManagerAuthSchemeProvider DEFAULT = new DefaultSecretsManagerAuthSchemeProvider();

    private DefaultSecretsManagerAuthSchemeProvider() {
    }

    public static DefaultSecretsManagerAuthSchemeProvider create() {
        return DEFAULT;
    }

    @Override
    public List<AuthSchemeOption> resolveAuthScheme(SecretsManagerAuthSchemeParams params) {
        ArrayList options = new ArrayList();
        options.add(AuthSchemeOption.builder().schemeId("aws.auth#sigv4").putSignerProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, "secretsmanager").putSignerProperty(AwsV4HttpSigner.REGION_NAME, params.region().id()).build());
        return Collections.unmodifiableList(options);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme;
import software.amazon.awssdk.awscore.endpoints.authscheme.SigV4AuthScheme;
import software.amazon.awssdk.awscore.endpoints.authscheme.SigV4aAuthScheme;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class AuthSchemeUtils {
    private static final Logger LOG = Logger.loggerFor(AuthSchemeUtils.class);
    private static final String SIGV4_NAME = "sigv4";
    private static final String SIGV4A_NAME = "sigv4a";
    private static final Set<String> KNOWN_AUTH_SCHEMES;

    private AuthSchemeUtils() {
    }

    public static EndpointAuthScheme chooseAuthScheme(List<EndpointAuthScheme> authSchemes) {
        for (EndpointAuthScheme authScheme : authSchemes) {
            if (!KNOWN_AUTH_SCHEMES.contains(authScheme.name())) continue;
            return authScheme;
        }
        throw SdkClientException.create("Endpoint did not contain any known auth schemes: " + authSchemes);
    }

    public static List<EndpointAuthScheme> createAuthSchemes(Value authSchemesValue) {
        Value.Array schemesArray = authSchemesValue.expectArray();
        ArrayList<EndpointAuthScheme> authSchemes = new ArrayList<EndpointAuthScheme>();
        block8: for (int i = 0; i < schemesArray.size(); ++i) {
            String authSchemeName;
            Value.Record scheme = schemesArray.get(i).expectRecord();
            switch (authSchemeName = scheme.get(Identifier.of("name")).expectString()) {
                case "sigv4a": {
                    Value disableDoubleEncoding;
                    Value signingRegionSet;
                    Object schemeBuilder = SigV4aAuthScheme.builder();
                    Value signingName = scheme.get(Identifier.of("signingName"));
                    if (signingName != null) {
                        ((SigV4aAuthScheme.Builder)schemeBuilder).signingName(signingName.expectString());
                    }
                    if ((signingRegionSet = scheme.get(Identifier.of("signingRegionSet"))) != null) {
                        Value.Array signingRegionSetArray = signingRegionSet.expectArray();
                        for (int j = 0; j < signingRegionSetArray.size(); ++j) {
                            ((SigV4aAuthScheme.Builder)schemeBuilder).addSigningRegion(signingRegionSetArray.get(j).expectString());
                        }
                    }
                    if ((disableDoubleEncoding = scheme.get(Identifier.of("disableDoubleEncoding"))) != null) {
                        ((SigV4aAuthScheme.Builder)schemeBuilder).disableDoubleEncoding(disableDoubleEncoding.expectBool());
                    }
                    authSchemes.add(((SigV4aAuthScheme.Builder)schemeBuilder).build());
                    continue block8;
                }
                case "sigv4": {
                    Value signingRegion;
                    Value disableDoubleEncoding;
                    Object schemeBuilder = SigV4AuthScheme.builder();
                    Value signingName = scheme.get(Identifier.of("signingName"));
                    if (signingName != null) {
                        ((SigV4AuthScheme.Builder)schemeBuilder).signingName(signingName.expectString());
                    }
                    if ((signingRegion = scheme.get(Identifier.of("signingRegion"))) != null) {
                        ((SigV4AuthScheme.Builder)schemeBuilder).signingRegion(signingRegion.expectString());
                    }
                    if ((disableDoubleEncoding = scheme.get(Identifier.of("disableDoubleEncoding"))) != null) {
                        ((SigV4AuthScheme.Builder)schemeBuilder).disableDoubleEncoding(disableDoubleEncoding.expectBool());
                    }
                    authSchemes.add(((SigV4AuthScheme.Builder)schemeBuilder).build());
                    continue block8;
                }
                default: {
                    LOG.debug(() -> "Ignoring unknown auth scheme: " + authSchemeName);
                }
            }
        }
        return authSchemes;
    }

    public static <T extends Identity> void putSelectedAuthScheme(ExecutionAttributes attributes, SelectedAuthScheme<T> selectedAuthScheme) {
        SelectedAuthScheme<?> existingAuthScheme = attributes.getAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME);
        if (existingAuthScheme != null) {
            AuthSchemeOption.Builder selectedOption = (AuthSchemeOption.Builder)selectedAuthScheme.authSchemeOption().toBuilder();
            existingAuthScheme.authSchemeOption().forEachIdentityProperty(selectedOption::putIdentityPropertyIfAbsent);
            existingAuthScheme.authSchemeOption().forEachSignerProperty(selectedOption::putSignerPropertyIfAbsent);
            selectedAuthScheme = new SelectedAuthScheme<T>(selectedAuthScheme.identity(), selectedAuthScheme.signer(), (AuthSchemeOption)selectedOption.build());
        }
        attributes.putAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME, selectedAuthScheme);
    }

    static {
        HashSet<String> schemes = new HashSet<String>();
        schemes.add(SIGV4_NAME);
        schemes.add(SIGV4A_NAME);
        KNOWN_AUTH_SCHEMES = Collections.unmodifiableSet(schemes);
    }
}


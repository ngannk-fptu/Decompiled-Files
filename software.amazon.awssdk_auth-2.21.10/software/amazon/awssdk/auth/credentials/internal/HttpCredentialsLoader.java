/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 *  software.amazon.awssdk.protocols.jsoncore.JsonNodeParser
 *  software.amazon.awssdk.regions.util.HttpResourcesUtils
 *  software.amazon.awssdk.regions.util.ResourcesEndpointProvider
 *  software.amazon.awssdk.utils.DateUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.auth.credentials.internal;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.regions.util.HttpResourcesUtils;
import software.amazon.awssdk.regions.util.ResourcesEndpointProvider;
import software.amazon.awssdk.utils.DateUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class HttpCredentialsLoader {
    private static final JsonNodeParser SENSITIVE_PARSER = JsonNodeParser.builder().removeErrorLocations(true).build();
    private static final Pattern TRAILING_ZERO_OFFSET_TIME_PATTERN = Pattern.compile("\\+0000$");

    private HttpCredentialsLoader() {
    }

    public static HttpCredentialsLoader create() {
        return new HttpCredentialsLoader();
    }

    public LoadedCredentials loadCredentials(ResourcesEndpointProvider endpoint) {
        try {
            String credentialsResponse = HttpResourcesUtils.instance().readResource(endpoint);
            Map node = SENSITIVE_PARSER.parse(credentialsResponse).asObject();
            JsonNode accessKey = (JsonNode)node.get("AccessKeyId");
            JsonNode secretKey = (JsonNode)node.get("SecretAccessKey");
            JsonNode token = (JsonNode)node.get("Token");
            JsonNode expiration = (JsonNode)node.get("Expiration");
            Validate.notNull((Object)accessKey, (String)"Failed to load access key from metadata service.", (Object[])new Object[0]);
            Validate.notNull((Object)secretKey, (String)"Failed to load secret key from metadata service.", (Object[])new Object[0]);
            return new LoadedCredentials(accessKey.text(), secretKey.text(), token != null ? token.text() : null, expiration != null ? expiration.text() : null);
        }
        catch (SdkClientException e) {
            throw e;
        }
        catch (IOException | RuntimeException e) {
            throw SdkClientException.builder().message("Failed to load credentials from metadata service.").cause((Throwable)e).build();
        }
    }

    public static final class LoadedCredentials {
        private final String accessKeyId;
        private final String secretKey;
        private final String token;
        private final Instant expiration;

        private LoadedCredentials(String accessKeyId, String secretKey, String token, String expiration) {
            this.accessKeyId = (String)Validate.paramNotBlank((CharSequence)accessKeyId, (String)"accessKeyId");
            this.secretKey = (String)Validate.paramNotBlank((CharSequence)secretKey, (String)"secretKey");
            this.token = token;
            this.expiration = expiration == null ? null : LoadedCredentials.parseExpiration(expiration);
        }

        public AwsCredentials getAwsCredentials() {
            if (this.token == null) {
                return AwsBasicCredentials.create(this.accessKeyId, this.secretKey);
            }
            return AwsSessionCredentials.create(this.accessKeyId, this.secretKey, this.token);
        }

        public Optional<Instant> getExpiration() {
            return Optional.ofNullable(this.expiration);
        }

        private static Instant parseExpiration(String expiration) {
            if (expiration == null) {
                return null;
            }
            String expirationValue = TRAILING_ZERO_OFFSET_TIME_PATTERN.matcher(expiration).replaceAll("Z");
            try {
                return DateUtils.parseIso8601Date((String)expirationValue);
            }
            catch (RuntimeException e) {
                throw new IllegalStateException("Unable to parse credentials expiration date from metadata service.", e);
            }
        }
    }
}


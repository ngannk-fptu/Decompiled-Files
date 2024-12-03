/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 *  software.amazon.awssdk.protocols.jsoncore.JsonNodeParser
 *  software.amazon.awssdk.utils.DateUtils
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.Platform
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 *  software.amazon.awssdk.utils.cache.CachedSupplier
 *  software.amazon.awssdk.utils.cache.CachedSupplier$Builder
 *  software.amazon.awssdk.utils.cache.CachedSupplier$PrefetchStrategy
 *  software.amazon.awssdk.utils.cache.NonBlocking
 *  software.amazon.awssdk.utils.cache.RefreshResult
 */
package software.amazon.awssdk.auth.credentials;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.utils.DateUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Platform;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;
import software.amazon.awssdk.utils.cache.CachedSupplier;
import software.amazon.awssdk.utils.cache.NonBlocking;
import software.amazon.awssdk.utils.cache.RefreshResult;

@SdkPublicApi
public final class ProcessCredentialsProvider
implements AwsCredentialsProvider,
SdkAutoCloseable,
ToCopyableBuilder<Builder, ProcessCredentialsProvider> {
    private static final JsonNodeParser PARSER = JsonNodeParser.builder().removeErrorLocations(true).build();
    private final List<String> executableCommand;
    private final Duration credentialRefreshThreshold;
    private final long processOutputLimit;
    private final CachedSupplier<AwsCredentials> processCredentialCache;
    private final String commandFromBuilder;
    private final Boolean asyncCredentialUpdateEnabled;

    private ProcessCredentialsProvider(Builder builder) {
        ArrayList<String> cmd = new ArrayList<String>();
        if (Platform.isWindows()) {
            cmd.add("cmd.exe");
            cmd.add("/C");
        } else {
            cmd.add("sh");
            cmd.add("-c");
        }
        String builderCommand = (String)Validate.paramNotNull((Object)builder.command, (String)"command");
        cmd.add(builderCommand);
        this.executableCommand = Collections.unmodifiableList(cmd);
        this.processOutputLimit = Validate.isPositive((long)builder.processOutputLimit, (String)"processOutputLimit");
        this.credentialRefreshThreshold = Validate.isPositive((Duration)builder.credentialRefreshThreshold, (String)"expirationBuffer");
        this.commandFromBuilder = builder.command;
        this.asyncCredentialUpdateEnabled = builder.asyncCredentialUpdateEnabled;
        CachedSupplier.Builder cacheBuilder = CachedSupplier.builder(this::refreshCredentials).cachedValueName(this.toString());
        if (builder.asyncCredentialUpdateEnabled.booleanValue()) {
            cacheBuilder.prefetchStrategy((CachedSupplier.PrefetchStrategy)new NonBlocking("process-credentials-provider"));
        }
        this.processCredentialCache = cacheBuilder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return (AwsCredentials)this.processCredentialCache.get();
    }

    private RefreshResult<AwsCredentials> refreshCredentials() {
        try {
            String processOutput = this.executeCommand();
            JsonNode credentialsJson = this.parseProcessOutput(processOutput);
            AwsCredentials credentials = this.credentials(credentialsJson);
            Instant credentialExpirationTime = this.credentialExpirationTime(credentialsJson);
            return RefreshResult.builder((Object)credentials).staleTime(credentialExpirationTime).prefetchTime(credentialExpirationTime.minusMillis(this.credentialRefreshThreshold.toMillis())).build();
        }
        catch (InterruptedException e) {
            throw new IllegalStateException("Process-based credential refreshing has been interrupted.", e);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to refresh process-based credentials.", e);
        }
    }

    private JsonNode parseProcessOutput(String processOutput) {
        JsonNode credentialsJson = PARSER.parse(processOutput);
        if (!credentialsJson.isObject()) {
            throw new IllegalStateException("Process did not return a JSON object.");
        }
        JsonNode version = credentialsJson.field("Version").orElse(null);
        if (version == null || !version.isNumber() || !version.asNumber().equals("1")) {
            throw new IllegalStateException("Unsupported credential version: " + version);
        }
        return credentialsJson;
    }

    private AwsCredentials credentials(JsonNode credentialsJson) {
        String accessKeyId = this.getText(credentialsJson, "AccessKeyId");
        String secretAccessKey = this.getText(credentialsJson, "SecretAccessKey");
        String sessionToken = this.getText(credentialsJson, "SessionToken");
        Validate.notEmpty((CharSequence)accessKeyId, (String)"AccessKeyId cannot be empty.", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)secretAccessKey, (String)"SecretAccessKey cannot be empty.", (Object[])new Object[0]);
        if (sessionToken != null) {
            return AwsSessionCredentials.create(accessKeyId, secretAccessKey, sessionToken);
        }
        return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
    }

    private Instant credentialExpirationTime(JsonNode credentialsJson) {
        String expiration = this.getText(credentialsJson, "Expiration");
        if (expiration != null) {
            return DateUtils.parseIso8601Date((String)expiration);
        }
        return Instant.MAX;
    }

    private String getText(JsonNode jsonObject, String nodeName) {
        return jsonObject.field(nodeName).map(JsonNode::text).orElse(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String executeCommand() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(this.executableCommand);
        ByteArrayOutputStream commandOutput = new ByteArrayOutputStream();
        Process process = processBuilder.start();
        try {
            IoUtils.copy((InputStream)process.getInputStream(), (OutputStream)commandOutput, (long)this.processOutputLimit);
            process.waitFor();
            if (process.exitValue() != 0) {
                InputStream errorStream = process.getErrorStream();
                Throwable throwable = null;
                try {
                    try {
                        String errorMessage = IoUtils.toUtf8String((InputStream)errorStream);
                        throw new IllegalStateException(String.format("Command returned non-zero exit value (%s) with error message: %s", process.exitValue(), errorMessage));
                    }
                    catch (Throwable throwable2) {
                        throwable = throwable2;
                        throw throwable2;
                    }
                }
                catch (Throwable throwable3) {
                    if (errorStream != null) {
                        if (throwable != null) {
                            try {
                                errorStream.close();
                            }
                            catch (Throwable throwable4) {
                                throwable.addSuppressed(throwable4);
                            }
                        } else {
                            errorStream.close();
                        }
                    }
                    throw throwable3;
                }
            }
            String string = new String(commandOutput.toByteArray(), StandardCharsets.UTF_8);
            return string;
        }
        finally {
            process.destroy();
        }
    }

    public void close() {
        this.processCredentialCache.close();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public String toString() {
        return ToString.builder((String)"ProcessCredentialsProvider").add("cmd", this.executableCommand).build();
    }

    public static class Builder
    implements CopyableBuilder<Builder, ProcessCredentialsProvider> {
        private Boolean asyncCredentialUpdateEnabled = false;
        private String command;
        private Duration credentialRefreshThreshold = Duration.ofSeconds(15L);
        private long processOutputLimit = 64000L;

        private Builder() {
        }

        private Builder(ProcessCredentialsProvider provider) {
            this.asyncCredentialUpdateEnabled = provider.asyncCredentialUpdateEnabled;
            this.command = provider.commandFromBuilder;
            this.credentialRefreshThreshold = provider.credentialRefreshThreshold;
            this.processOutputLimit = provider.processOutputLimit;
        }

        public Builder asyncCredentialUpdateEnabled(Boolean asyncCredentialUpdateEnabled) {
            this.asyncCredentialUpdateEnabled = asyncCredentialUpdateEnabled;
            return this;
        }

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        public Builder credentialRefreshThreshold(Duration credentialRefreshThreshold) {
            this.credentialRefreshThreshold = credentialRefreshThreshold;
            return this;
        }

        public Builder processOutputLimit(long outputByteLimit) {
            this.processOutputLimit = outputByteLimit;
            return this;
        }

        public ProcessCredentialsProvider build() {
            return new ProcessCredentialsProvider(this);
        }
    }
}


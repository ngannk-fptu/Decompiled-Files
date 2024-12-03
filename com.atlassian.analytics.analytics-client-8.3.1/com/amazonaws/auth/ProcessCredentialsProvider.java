/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.Platform;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.ValidationUtils;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;

public final class ProcessCredentialsProvider
implements AWSCredentialsProvider {
    private final List<String> command;
    private final int expirationBufferValue;
    private final TimeUnit expirationBufferUnit;
    private final long processOutputLimit;
    private final Object credentialLock = new Object();
    private volatile AWSCredentials credentials = null;
    private volatile DateTime credentialExpirationTime = DateTime.now();

    private ProcessCredentialsProvider(Builder builder) {
        ArrayList<String> cmd = new ArrayList<String>();
        if (Platform.isWindows()) {
            cmd.add("cmd.exe");
            cmd.add("/C");
        } else {
            cmd.add("sh");
            cmd.add("-c");
        }
        String builderCommand = ValidationUtils.assertNotNull(builder.command, "command");
        cmd.add(builderCommand);
        this.command = Collections.unmodifiableList(cmd);
        this.processOutputLimit = ValidationUtils.assertNotNull(builder.processOutputLimit, "processOutputLimit");
        this.expirationBufferValue = ValidationUtils.assertNotNull(builder.expirationBufferValue, "expirationBufferValue");
        this.expirationBufferUnit = ValidationUtils.assertNotNull(builder.expirationBufferUnit, "expirationBufferUnit");
    }

    public static Builder builder() {
        return new Builder();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AWSCredentials getCredentials() {
        if (this.credentialsNeedUpdating()) {
            Object object = this.credentialLock;
            synchronized (object) {
                if (this.credentialsNeedUpdating()) {
                    this.refresh();
                }
            }
        }
        return this.credentials;
    }

    @Override
    public void refresh() {
        try {
            String processOutput = this.executeCommand();
            JsonNode credentialsJson = this.parseProcessOutput(processOutput);
            AWSCredentials credentials = this.credentials(credentialsJson);
            DateTime credentialExpirationTime = this.credentialExpirationTime(credentialsJson);
            this.credentials = credentials;
            this.credentialExpirationTime = credentialExpirationTime;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Process-based credential refreshing has been interrupted.", e);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to refresh process-based credentials.", e);
        }
    }

    public DateTime getCredentialExpirationTime() {
        return this.credentialExpirationTime;
    }

    private boolean credentialsNeedUpdating() {
        return this.credentials == null || this.credentialExpirationTime.isBeforeNow();
    }

    private JsonNode parseProcessOutput(String processOutput) {
        JsonNode credentialsJson = Jackson.fromSensitiveJsonString(processOutput, JsonNode.class);
        if (!credentialsJson.isObject()) {
            throw new IllegalStateException("Process did not return a JSON object.");
        }
        JsonNode version = credentialsJson.get("Version");
        if (version == null || !version.isInt() || version.asInt() != 1) {
            throw new IllegalStateException("Unsupported credential version: " + version);
        }
        return credentialsJson;
    }

    private AWSCredentials credentials(JsonNode credentialsJson) {
        String accessKeyId = this.getText(credentialsJson, "AccessKeyId");
        String secretAccessKey = this.getText(credentialsJson, "SecretAccessKey");
        String sessionToken = this.getText(credentialsJson, "SessionToken");
        ValidationUtils.assertStringNotEmpty(accessKeyId, "AccessKeyId");
        ValidationUtils.assertStringNotEmpty(secretAccessKey, "SecretAccessKey");
        if (sessionToken != null) {
            return new BasicSessionCredentials(accessKeyId, secretAccessKey, sessionToken);
        }
        return new BasicAWSCredentials(accessKeyId, secretAccessKey);
    }

    private DateTime credentialExpirationTime(JsonNode credentialsJson) {
        String expiration = this.getText(credentialsJson, "Expiration");
        if (expiration != null) {
            DateTime credentialExpiration = new DateTime((Object)DateUtils.parseISO8601Date(expiration));
            credentialExpiration = credentialExpiration.minus(this.expirationBufferUnit.toMillis(this.expirationBufferValue));
            return credentialExpiration;
        }
        return DateTime.now().plusYears(9999);
    }

    private String getText(JsonNode jsonObject, String nodeName) {
        JsonNode subNode = jsonObject.get(nodeName);
        if (subNode == null) {
            return null;
        }
        if (!subNode.isTextual()) {
            throw new IllegalStateException(nodeName + " from credential process should be textual, but was " + (Object)((Object)subNode.getNodeType()));
        }
        return subNode.asText();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String executeCommand() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(this.command);
        ByteArrayOutputStream commandOutput = new ByteArrayOutputStream();
        Process process = processBuilder.start();
        try {
            IOUtils.copy(process.getInputStream(), commandOutput, this.processOutputLimit);
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new IllegalStateException("Command returned non-zero exit value: " + process.exitValue());
            }
            String string = new String(commandOutput.toByteArray(), StringUtils.UTF8);
            return string;
        }
        finally {
            process.destroy();
        }
    }

    public static class Builder {
        private String command;
        private int expirationBufferValue = 15;
        private TimeUnit expirationBufferUnit = TimeUnit.SECONDS;
        private long processOutputLimit = 64000L;

        private Builder() {
        }

        private void setCommand(String command) {
            this.command = command;
        }

        public Builder withCommand(String command) {
            this.setCommand(command);
            return this;
        }

        public void setCredentialExpirationBuffer(int value, TimeUnit unit) {
            this.expirationBufferValue = value;
            this.expirationBufferUnit = unit;
        }

        public Builder withCredentialExpirationBuffer(int value, TimeUnit unit) {
            this.setCredentialExpirationBuffer(value, unit);
            return this;
        }

        private void setProcessOutputLimit(long outputByteLimit) {
            this.processOutputLimit = outputByteLimit;
        }

        public Builder withProcessOutputLimit(long outputByteLimit) {
            this.setProcessOutputLimit(outputByteLimit);
            return this;
        }

        public ProcessCredentialsProvider build() {
            return new ProcessCredentialsProvider(this);
        }
    }
}


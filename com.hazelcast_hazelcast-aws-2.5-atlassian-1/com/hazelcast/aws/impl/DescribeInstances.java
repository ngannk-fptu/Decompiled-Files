/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.InvalidConfigurationException
 *  com.hazelcast.internal.json.JsonObject
 *  com.hazelcast.nio.IOUtil
 */
package com.hazelcast.aws.impl;

import com.hazelcast.aws.AwsConfig;
import com.hazelcast.aws.exception.AwsConnectionException;
import com.hazelcast.aws.impl.Filter;
import com.hazelcast.aws.security.EC2RequestSigner;
import com.hazelcast.aws.utility.CloudyUtility;
import com.hazelcast.aws.utility.Environment;
import com.hazelcast.aws.utility.MetadataUtil;
import com.hazelcast.aws.utility.RetryUtils;
import com.hazelcast.aws.utility.StringUtil;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.nio.IOUtil;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescribeInstances {
    public static final String IAM_TASK_ROLE_ENDPOINT = "http://169.254.170.2";
    private static final int MIN_HTTP_CODE_FOR_AWS_ERROR = 400;
    private static final int MAX_HTTP_CODE_FOR_AWS_ERROR = 600;
    private static final String UTF8_ENCODING = "UTF-8";
    private EC2RequestSigner rs;
    private AwsConfig awsConfig;
    private String endpoint;
    private Map<String, String> attributes = new HashMap<String, String>();

    public DescribeInstances(AwsConfig awsConfig, String endpoint) throws IOException {
        this.awsConfig = awsConfig;
        this.endpoint = endpoint;
    }

    DescribeInstances(AwsConfig awsConfig) {
        this.awsConfig = awsConfig;
    }

    private static boolean isAwsError(int responseCode) {
        return responseCode >= 400 && responseCode < 600;
    }

    private static String extractErrorMessage(HttpURLConnection httpConnection) {
        InputStream errorStream = httpConnection.getErrorStream();
        if (errorStream == null) {
            return "";
        }
        return DescribeInstances.readFrom(errorStream);
    }

    private static String readFrom(InputStream stream) {
        Scanner scanner = new Scanner(stream, UTF8_ENCODING).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    void fillKeysFromIamRoles() throws IOException {
        if (StringUtil.isEmpty(this.awsConfig.getIamRole()) || "DEFAULT".equals(this.awsConfig.getIamRole())) {
            String defaultIAMRole = this.getDefaultIamRole();
            this.awsConfig.setIamRole(defaultIAMRole);
        }
        if (StringUtil.isNotEmpty(this.awsConfig.getIamRole())) {
            this.fillKeysFromIamRole();
        } else {
            this.fillKeysFromIamTaskRole(this.getEnvironment());
        }
    }

    private String getDefaultIamRole() throws IOException {
        String uri = "http://169.254.169.254/latest/meta-data/".concat("iam/security-credentials/");
        return this.retrieveRoleFromURI(uri);
    }

    private void fillKeysFromIamRole() {
        try {
            String query = "iam/security-credentials/".concat(this.awsConfig.getIamRole());
            String uri = "http://169.254.169.254/latest/meta-data/".concat(query);
            String json = this.retrieveRoleFromURI(uri);
            this.parseAndStoreRoleCreds(json);
        }
        catch (Exception io) {
            throw new InvalidConfigurationException("Unable to retrieve credentials from IAM Role: " + this.awsConfig.getIamRole(), (Throwable)io);
        }
    }

    private void fillKeysFromIamTaskRole(Environment env) throws IOException {
        String uri = env.getEnvVar("AWS_CONTAINER_CREDENTIALS_RELATIVE_URI");
        if (uri == null) {
            throw new IllegalArgumentException("Could not acquire credentials! Did not find declared AWS access key or IAM Role, and could not discover IAM Task Role or default role.");
        }
        uri = IAM_TASK_ROLE_ENDPOINT + uri;
        String json = "";
        try {
            json = this.retrieveRoleFromURI(uri);
            this.parseAndStoreRoleCreds(json);
        }
        catch (Exception io) {
            throw new InvalidConfigurationException("Unable to retrieve credentials from IAM Task Role. URI: " + uri + ". \n HTTP Response content: " + json, (Throwable)io);
        }
    }

    String retrieveRoleFromURI(String uri) {
        return MetadataUtil.retrieveMetadataFromURI(uri, this.awsConfig.getConnectionTimeoutSeconds(), this.awsConfig.getConnectionRetries());
    }

    private void parseAndStoreRoleCreds(String json) {
        JsonObject roleAsJson = JsonObject.readFrom((String)json);
        this.awsConfig.setAccessKey(roleAsJson.getString("AccessKeyId", null));
        this.awsConfig.setSecretKey(roleAsJson.getString("SecretAccessKey", null));
        this.attributes.put("X-Amz-Security-Token", roleAsJson.getString("Token", null));
    }

    @Deprecated
    public Map<String, String> parseIamRole(BufferedReader reader) throws IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        Pattern keyPattern = Pattern.compile("\"(.*?)\" : ");
        Pattern valuePattern = Pattern.compile(" : \"(.*?)\",");
        String line = reader.readLine();
        while (line != null) {
            if (line.contains(":")) {
                Matcher keyMatcher = keyPattern.matcher(line);
                Matcher valueMatcher = valuePattern.matcher(line);
                if (keyMatcher.find() && valueMatcher.find()) {
                    String key = keyMatcher.group(1);
                    String value = valueMatcher.group(1);
                    map.put(key, value);
                }
            }
            line = reader.readLine();
        }
        return map;
    }

    private String getFormattedTimestamp() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(new Date());
    }

    private void addFilters() {
        Filter filter = new Filter();
        if (StringUtil.isNotEmpty(this.awsConfig.getTagKey())) {
            if (StringUtil.isNotEmpty(this.awsConfig.getTagValue())) {
                filter.addFilter("tag:" + this.awsConfig.getTagKey(), this.awsConfig.getTagValue());
            } else {
                filter.addFilter("tag-key", this.awsConfig.getTagKey());
            }
        } else if (StringUtil.isNotEmpty(this.awsConfig.getTagValue())) {
            filter.addFilter("tag-value", this.awsConfig.getTagValue());
        }
        if (StringUtil.isNotEmpty(this.awsConfig.getSecurityGroupName())) {
            filter.addFilter("instance.group-name", this.awsConfig.getSecurityGroupName());
        }
        filter.addFilter("instance-state-name", "running");
        this.attributes.putAll(filter.getFilters());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, String> execute() throws Exception {
        Map<String, String> map;
        if (StringUtil.isNotEmpty(this.awsConfig.getIamRole()) || StringUtil.isEmpty(this.awsConfig.getAccessKey())) {
            this.fillKeysFromIamRoles();
        }
        String signature = this.getRequestSigner().sign("ec2", this.attributes);
        InputStream stream = null;
        this.attributes.put("X-Amz-Signature", signature);
        try {
            Map<String, String> response;
            stream = this.callServiceWithRetries(this.endpoint);
            map = response = CloudyUtility.unmarshalTheResponse(stream);
        }
        catch (Throwable throwable) {
            IOUtil.closeResource(stream);
            throw throwable;
        }
        IOUtil.closeResource((Closeable)stream);
        return map;
    }

    private InputStream callServiceWithRetries(final String endpoint) throws Exception {
        return RetryUtils.retry(new Callable<InputStream>(){

            @Override
            public InputStream call() throws Exception {
                return DescribeInstances.this.callService(endpoint);
            }
        }, this.awsConfig.getConnectionRetries());
    }

    InputStream callService(String endpoint) throws Exception {
        String query = this.getRequestSigner().getCanonicalizedQueryString(this.attributes);
        URL url = new URL("https", endpoint, -1, "/?" + query);
        HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(this.awsConfig.getConnectionTimeoutSeconds()));
        httpConnection.setDoOutput(false);
        httpConnection.connect();
        this.checkNoAwsErrors(httpConnection);
        return httpConnection.getInputStream();
    }

    void checkNoAwsErrors(HttpURLConnection httpConnection) throws IOException {
        int responseCode = httpConnection.getResponseCode();
        if (DescribeInstances.isAwsError(responseCode)) {
            String errorMessage = DescribeInstances.extractErrorMessage(httpConnection);
            throw new AwsConnectionException(responseCode, errorMessage);
        }
    }

    public EC2RequestSigner getRequestSigner() {
        if (null == this.rs) {
            String timeStamp = this.getFormattedTimestamp();
            this.rs = new EC2RequestSigner(this.awsConfig, timeStamp, this.endpoint);
            this.attributes.put("Action", this.getClass().getSimpleName());
            this.attributes.put("Version", "2016-11-15");
            this.attributes.put("X-Amz-Algorithm", "AWS4-HMAC-SHA256");
            this.attributes.put("X-Amz-Credential", this.rs.createFormattedCredential());
            this.attributes.put("X-Amz-Date", timeStamp);
            this.attributes.put("X-Amz-SignedHeaders", "host");
            this.attributes.put("X-Amz-Expires", "30");
            this.addFilters();
        }
        return this.rs;
    }

    Environment getEnvironment() {
        return new Environment();
    }
}


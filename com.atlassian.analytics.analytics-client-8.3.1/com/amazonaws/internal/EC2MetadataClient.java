/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.internal;

import com.amazonaws.SdkClientException;
import com.amazonaws.util.VersionInfoUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
public class EC2MetadataClient {
    private static final String EC2_METADATA_SERVICE_URL = "http://169.254.169.254";
    public static final String SECURITY_CREDENTIALS_RESOURCE = "/latest/meta-data/iam/security-credentials/";
    private static final Log log = LogFactory.getLog(EC2MetadataClient.class);
    private static final String USER_AGENT = String.format("aws-sdk-java/%s", VersionInfoUtils.getVersion());

    public String getDefaultCredentials() throws IOException {
        String securityCredentialsList = this.readResource(SECURITY_CREDENTIALS_RESOURCE);
        String[] securityCredentials = (securityCredentialsList = securityCredentialsList.trim()).split("\n");
        if (securityCredentials.length == 0) {
            return null;
        }
        String securityCredentialsName = securityCredentials[0];
        return this.readResource(SECURITY_CREDENTIALS_RESOURCE + securityCredentialsName);
    }

    public String readResource(String resourcePath) throws IOException, SdkClientException {
        URL url = this.getEc2MetadataServiceUrlForResource(resourcePath);
        log.debug((Object)("Connecting to EC2 instance metadata service at URL: " + url.toString()));
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.addRequestProperty("User-Agent", USER_AGENT);
        connection.connect();
        return this.readResponse(connection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() == 404) {
            throw new SdkClientException("The requested metadata is not found at " + connection.getURL());
        }
        try (InputStream inputStream = connection.getInputStream();){
            int c;
            StringBuilder buffer = new StringBuilder();
            while ((c = inputStream.read()) != -1) {
                buffer.append((char)c);
            }
            String string = buffer.toString();
            return string;
        }
    }

    private URL getEc2MetadataServiceUrlForResource(String resourcePath) throws IOException {
        String endpoint = EC2_METADATA_SERVICE_URL;
        if (System.getProperty("com.amazonaws.sdk.ec2MetadataServiceEndpointOverride") != null) {
            endpoint = System.getProperty("com.amazonaws.sdk.ec2MetadataServiceEndpointOverride");
        }
        return new URL(endpoint + resourcePath);
    }
}


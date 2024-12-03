/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.SdkClientException;
import com.amazonaws.internal.InstanceMetadataServiceResourceFetcher;
import com.amazonaws.retry.internal.CredentialsEndpointRetryParameters;
import com.amazonaws.retry.internal.CredentialsEndpointRetryPolicy;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EC2MetadataUtils {
    private static final String REGION = "region";
    private static final String INSTANCE_IDENTITY_DOCUMENT = "instance-identity/document";
    private static final String INSTANCE_IDENTITY_SIGNATURE = "instance-identity/signature";
    private static final String EC2_METADATA_ROOT = "/latest/meta-data";
    private static final String EC2_USERDATA_ROOT = "/latest/user-data/";
    private static final String EC2_DYNAMICDATA_ROOT = "/latest/dynamic/";
    private static final String EC2_METADATA_SERVICE_URL = "http://169.254.169.254";
    public static final String SECURITY_CREDENTIALS_RESOURCE = "/latest/meta-data/iam/security-credentials/";
    private static final int DEFAULT_QUERY_RETRIES = 3;
    private static final int MINIMUM_RETRY_WAIT_TIME_MILLISECONDS = 250;
    private static Map<String, String> cache = new ConcurrentHashMap<String, String>();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Log log;

    public static String getAmiId() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/ami-id");
    }

    public static String getAmiLaunchIndex() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/ami-launch-index");
    }

    public static String getAmiManifestPath() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/ami-manifest-path");
    }

    public static List<String> getAncestorAmiIds() {
        return EC2MetadataUtils.getItems("/latest/meta-data/ancestor-ami-ids");
    }

    public static String getInstanceAction() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/instance-action");
    }

    public static String getInstanceId() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/instance-id");
    }

    public static String getInstanceType() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/instance-type");
    }

    public static String getLocalHostName() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/local-hostname");
    }

    public static String getMacAddress() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/mac");
    }

    public static String getPrivateIpAddress() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/local-ipv4");
    }

    public static String getAvailabilityZone() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/placement/availability-zone");
    }

    public static List<String> getProductCodes() {
        return EC2MetadataUtils.getItems("/latest/meta-data/product-codes");
    }

    public static String getPublicKey() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/public-keys/0/openssh-key");
    }

    public static String getRamdiskId() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/ramdisk-id");
    }

    public static String getReservationId() {
        return EC2MetadataUtils.fetchData("/latest/meta-data/reservation-id");
    }

    public static List<String> getSecurityGroups() {
        return EC2MetadataUtils.getItems("/latest/meta-data/security-groups");
    }

    public static IAMInfo getIAMInstanceProfileInfo() {
        String json = EC2MetadataUtils.getData("/latest/meta-data/iam/info");
        if (null == json) {
            return null;
        }
        try {
            return mapper.readValue(json, IAMInfo.class);
        }
        catch (Exception e) {
            log.warn((Object)("Unable to parse IAM Instance profile info (" + json + "): " + e.getMessage()), (Throwable)e);
            return null;
        }
    }

    public static InstanceInfo getInstanceInfo() {
        return EC2MetadataUtils.doGetInstanceInfo(EC2MetadataUtils.getData("/latest/dynamic/instance-identity/document"));
    }

    public static String getInstanceSignature() {
        return EC2MetadataUtils.fetchData("/latest/dynamic/instance-identity/signature");
    }

    static InstanceInfo doGetInstanceInfo(String json) {
        if (null != json) {
            try {
                InstanceInfo instanceInfo = Jackson.fromJsonString(json, InstanceInfo.class);
                return instanceInfo;
            }
            catch (Exception e) {
                log.warn((Object)("Unable to parse dynamic EC2 instance info (" + json + ") : " + e.getMessage()), (Throwable)e);
            }
        }
        return null;
    }

    public static String getEC2InstanceRegion() {
        return EC2MetadataUtils.doGetEC2InstanceRegion(EC2MetadataUtils.getData("/latest/dynamic/instance-identity/document"));
    }

    static String doGetEC2InstanceRegion(String json) {
        if (null != json) {
            try {
                JsonNode node = mapper.readTree(json.getBytes(StringUtils.UTF8));
                JsonNode region = node.findValue(REGION);
                return region.asText();
            }
            catch (Exception e) {
                log.warn((Object)("Unable to parse EC2 instance info (" + json + ") : " + e.getMessage()), (Throwable)e);
            }
        }
        return null;
    }

    public static Map<String, IAMSecurityCredential> getIAMSecurityCredentials() {
        HashMap<String, IAMSecurityCredential> credentialsInfoMap = new HashMap<String, IAMSecurityCredential>();
        List<String> credentials = EC2MetadataUtils.getItems("/latest/meta-data/iam/security-credentials");
        if (credentials != null) {
            for (String credential : credentials) {
                String json = EC2MetadataUtils.getData(SECURITY_CREDENTIALS_RESOURCE + credential);
                try {
                    IAMSecurityCredential credentialInfo = mapper.readValue(json, IAMSecurityCredential.class);
                    credentialsInfoMap.put(credential, credentialInfo);
                }
                catch (Exception e) {
                    log.warn((Object)("Unable to process the credential (" + credential + "). " + e.getMessage()), (Throwable)e);
                }
            }
        }
        return credentialsInfoMap;
    }

    public static Map<String, String> getBlockDeviceMapping() {
        HashMap<String, String> blockDeviceMapping = new HashMap<String, String>();
        List<String> devices = EC2MetadataUtils.getItems("/latest/meta-data/block-device-mapping");
        if (devices != null) {
            for (String device : devices) {
                blockDeviceMapping.put(device, EC2MetadataUtils.getData("/latest/meta-data/block-device-mapping/" + device));
            }
        }
        return blockDeviceMapping;
    }

    public static List<NetworkInterface> getNetworkInterfaces() {
        LinkedList<NetworkInterface> networkInterfaces = new LinkedList<NetworkInterface>();
        List<String> macs = EC2MetadataUtils.getItems("/latest/meta-data/network/interfaces/macs/");
        if (macs != null) {
            for (String mac : macs) {
                String key = mac.trim();
                if (key.endsWith("/")) {
                    key = key.substring(0, key.length() - 1);
                }
                networkInterfaces.add(new NetworkInterface(key));
            }
        }
        return networkInterfaces;
    }

    public static String getUserData() {
        return EC2MetadataUtils.getData(EC2_USERDATA_ROOT);
    }

    public static String getData(String path) {
        return EC2MetadataUtils.getData(path, 3);
    }

    public static String getData(String path, int tries) {
        List<String> items = EC2MetadataUtils.getItems(path, tries, true);
        if (null != items && items.size() > 0) {
            return items.get(0);
        }
        return null;
    }

    public static List<String> getItems(String path) {
        return EC2MetadataUtils.getItems(path, 3, false);
    }

    public static List<String> getItems(String path, int tries) {
        return EC2MetadataUtils.getItems(path, tries, false);
    }

    private static List<String> getItems(String path, int tries, boolean slurp) {
        if (tries == 0) {
            throw new SdkClientException("Unable to contact EC2 metadata service.");
        }
        try {
            String hostAddress = EC2MetadataUtils.getHostAddressForEC2MetadataService();
            String response = InstanceMetadataServiceResourceFetcher.getInstance().readResource(new URI(hostAddress + path), EC2MetadataUtilsRetryPolicy.INSTANCE);
            List<String> items = slurp ? Collections.singletonList(response) : Arrays.asList(response.split("\n"));
            return items;
        }
        catch (Exception ace) {
            log.warn((Object)("Unable to retrieve the requested metadata (" + path + "). " + ace.getMessage()), (Throwable)ace);
            return null;
        }
    }

    private static String fetchData(String path) {
        return EC2MetadataUtils.fetchData(path, false);
    }

    private static String fetchData(String path, boolean force) {
        try {
            if (force || !cache.containsKey(path)) {
                cache.put(path, EC2MetadataUtils.getData(path));
            }
            return cache.get(path);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getHostAddressForEC2MetadataService() {
        String host = System.getProperty("com.amazonaws.sdk.ec2MetadataServiceEndpointOverride");
        if (host == null) {
            host = System.getenv("AWS_EC2_METADATA_SERVICE_ENDPOINT");
        }
        return host != null ? host : EC2_METADATA_SERVICE_URL;
    }

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        }
        catch (LinkageError e) {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
        }
        log = LogFactory.getLog(EC2MetadataUtils.class);
    }

    private static final class EC2MetadataUtilsRetryPolicy
    implements CredentialsEndpointRetryPolicy {
        private static final EC2MetadataUtilsRetryPolicy INSTANCE = new EC2MetadataUtilsRetryPolicy();

        private EC2MetadataUtilsRetryPolicy() {
        }

        @Override
        public boolean shouldRetry(int retriesAttempted, CredentialsEndpointRetryParameters retryParams) {
            if (retriesAttempted >= 3) {
                return false;
            }
            if (retryParams.getException() instanceof AmazonClientException) {
                return false;
            }
            int pause = (int)(Math.pow(2.0, 3 - retriesAttempted) * 250.0);
            try {
                Thread.sleep(Math.max(pause, 250));
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return true;
        }
    }

    public static class NetworkInterface {
        private String path;
        private String mac;
        private List<String> availableKeys;
        private Map<String, String> data = new HashMap<String, String>();

        public NetworkInterface(String macAddress) {
            this.mac = macAddress;
            this.path = "/network/interfaces/macs/" + this.mac + "/";
        }

        public String getMacAddress() {
            return this.mac;
        }

        public String getOwnerId() {
            return this.getData("owner-id");
        }

        public String getProfile() {
            return this.getData("profile");
        }

        public String getHostname() {
            return this.getData("local-hostname");
        }

        public List<String> getLocalIPv4s() {
            return this.getItems("local-ipv4s");
        }

        public String getPublicHostname() {
            return this.getData("public-hostname");
        }

        public List<String> getPublicIPv4s() {
            return this.getItems("public-ipv4s");
        }

        public List<String> getSecurityGroups() {
            return this.getItems("security-groups");
        }

        public List<String> getSecurityGroupIds() {
            return this.getItems("security-group-ids");
        }

        public String getSubnetIPv4CidrBlock() {
            return this.getData("subnet-ipv4-cidr-block");
        }

        public String getSubnetId() {
            return this.getData("subnet-id");
        }

        public String getVpcIPv4CidrBlock() {
            return this.getData("vpc-ipv4-cidr-block");
        }

        public String getVpcId() {
            return this.getData("vpc-id");
        }

        public List<String> getIPv4Association(String publicIp) {
            return EC2MetadataUtils.getItems(EC2MetadataUtils.EC2_METADATA_ROOT + this.path + "ipv4-associations/" + publicIp);
        }

        private String getData(String key) {
            if (this.data.containsKey(key)) {
                return this.data.get(key);
            }
            if (null == this.availableKeys) {
                this.availableKeys = EC2MetadataUtils.getItems(EC2MetadataUtils.EC2_METADATA_ROOT + this.path);
            }
            if (this.availableKeys != null && this.availableKeys.contains(key)) {
                this.data.put(key, EC2MetadataUtils.getData(EC2MetadataUtils.EC2_METADATA_ROOT + this.path + key));
                return this.data.get(key);
            }
            return null;
        }

        private List<String> getItems(String key) {
            if (null == this.availableKeys) {
                this.availableKeys = EC2MetadataUtils.getItems(EC2MetadataUtils.EC2_METADATA_ROOT + this.path);
            }
            if (this.availableKeys != null && this.availableKeys.contains(key)) {
                return EC2MetadataUtils.getItems(EC2MetadataUtils.EC2_METADATA_ROOT + this.path + key);
            }
            return new LinkedList<String>();
        }
    }

    public static class InstanceInfo {
        private final String pendingTime;
        private final String instanceType;
        private final String imageId;
        private final String instanceId;
        private final String[] billingProducts;
        private final String architecture;
        private final String accountId;
        private final String kernelId;
        private final String ramdiskId;
        private final String region;
        private final String version;
        private final String availabilityZone;
        private final String privateIp;
        private final String[] devpayProductCodes;

        @JsonCreator
        public InstanceInfo(@JsonProperty(value="pendingTime", required=true) String pendingTime, @JsonProperty(value="instanceType", required=true) String instanceType, @JsonProperty(value="imageId", required=true) String imageId, @JsonProperty(value="instanceId", required=true) String instanceId, @JsonProperty(value="billingProducts", required=false) String[] billingProducts, @JsonProperty(value="architecture", required=true) String architecture, @JsonProperty(value="accountId", required=true) String accountId, @JsonProperty(value="kernelId", required=true) String kernelId, @JsonProperty(value="ramdiskId", required=false) String ramdiskId, @JsonProperty(value="region", required=true) String region, @JsonProperty(value="version", required=true) String version, @JsonProperty(value="availabilityZone", required=true) String availabilityZone, @JsonProperty(value="privateIp", required=true) String privateIp, @JsonProperty(value="devpayProductCodes", required=false) String[] devpayProductCodes) {
            this.pendingTime = pendingTime;
            this.instanceType = instanceType;
            this.imageId = imageId;
            this.instanceId = instanceId;
            this.billingProducts = billingProducts == null ? null : (String[])billingProducts.clone();
            this.architecture = architecture;
            this.accountId = accountId;
            this.kernelId = kernelId;
            this.ramdiskId = ramdiskId;
            this.region = region;
            this.version = version;
            this.availabilityZone = availabilityZone;
            this.privateIp = privateIp;
            this.devpayProductCodes = devpayProductCodes == null ? null : (String[])devpayProductCodes.clone();
        }

        public String getPendingTime() {
            return this.pendingTime;
        }

        public String getInstanceType() {
            return this.instanceType;
        }

        public String getImageId() {
            return this.imageId;
        }

        public String getInstanceId() {
            return this.instanceId;
        }

        public String[] getBillingProducts() {
            return this.billingProducts == null ? null : (String[])this.billingProducts.clone();
        }

        public String getArchitecture() {
            return this.architecture;
        }

        public String getAccountId() {
            return this.accountId;
        }

        public String getKernelId() {
            return this.kernelId;
        }

        public String getRamdiskId() {
            return this.ramdiskId;
        }

        public String getRegion() {
            return this.region;
        }

        public String getVersion() {
            return this.version;
        }

        public String getAvailabilityZone() {
            return this.availabilityZone;
        }

        public String getPrivateIp() {
            return this.privateIp;
        }

        public String[] getDevpayProductCodes() {
            return this.devpayProductCodes == null ? null : (String[])this.devpayProductCodes.clone();
        }
    }

    public static class IAMSecurityCredential {
        public String code;
        public String message;
        public String lastUpdated;
        public String type;
        public String accessKeyId;
        public String secretAccessKey;
        public String token;
        public String expiration;
        @Deprecated
        public String secretAcessKey;
    }

    public static class IAMInfo {
        public String code;
        public String message;
        public String lastUpdated;
        public String instanceProfileArn;
        public String instanceProfileId;
    }
}


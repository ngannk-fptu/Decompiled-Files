/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.internal.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.util.SdkUserAgent;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.regions.internal.util.Ec2MetadataConfigProvider;
import software.amazon.awssdk.regions.internal.util.InstanceProviderTokenEndpointProvider;
import software.amazon.awssdk.regions.util.HttpResourcesUtils;
import software.amazon.awssdk.regions.util.ResourcesEndpointProvider;

@SdkInternalApi
public final class EC2MetadataUtils {
    private static final JsonNodeParser JSON_PARSER = JsonNode.parser();
    private static final String REGION = "region";
    private static final String INSTANCE_IDENTITY_DOCUMENT = "instance-identity/document";
    private static final String INSTANCE_IDENTITY_SIGNATURE = "instance-identity/signature";
    private static final String EC2_METADATA_ROOT = "/latest/meta-data";
    private static final String EC2_USERDATA_ROOT = "/latest/user-data/";
    private static final String EC2_DYNAMICDATA_ROOT = "/latest/dynamic/";
    private static final String EC2_METADATA_TOKEN_HEADER = "x-aws-ec2-metadata-token";
    private static final int DEFAULT_QUERY_ATTEMPTS = 3;
    private static final int MINIMUM_RETRY_WAIT_TIME_MILLISECONDS = 250;
    private static final Logger log = LoggerFactory.getLogger(EC2MetadataUtils.class);
    private static final Map<String, String> CACHE = new ConcurrentHashMap<String, String>();
    private static final InstanceProviderTokenEndpointProvider TOKEN_ENDPOINT_PROVIDER = new InstanceProviderTokenEndpointProvider();
    private static final Ec2MetadataConfigProvider EC2_METADATA_CONFIG_PROVIDER = Ec2MetadataConfigProvider.builder().build();

    private EC2MetadataUtils() {
    }

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

    public static String getInstanceSignature() {
        return EC2MetadataUtils.fetchData("/latest/dynamic/instance-identity/signature");
    }

    public static String getEC2InstanceRegion() {
        return EC2MetadataUtils.doGetEC2InstanceRegion(EC2MetadataUtils.getData("/latest/dynamic/instance-identity/document"));
    }

    static String doGetEC2InstanceRegion(String json) {
        if (null != json) {
            try {
                return JSON_PARSER.parse(json).field(REGION).map(JsonNode::text).orElseThrow(() -> new IllegalStateException("Region not included in metadata."));
            }
            catch (Exception e) {
                log.warn("Unable to parse EC2 instance info (" + json + ") : " + e.getMessage(), e);
            }
        }
        return null;
    }

    public static Map<String, String> getBlockDeviceMapping() {
        HashMap<String, String> blockDeviceMapping = new HashMap<String, String>();
        List<String> devices = EC2MetadataUtils.getItems("/latest/meta-data/block-device-mapping");
        for (String device : devices) {
            blockDeviceMapping.put(device, EC2MetadataUtils.getData("/latest/meta-data/block-device-mapping/" + device));
        }
        return blockDeviceMapping;
    }

    public static List<NetworkInterface> getNetworkInterfaces() {
        LinkedList<NetworkInterface> networkInterfaces = new LinkedList<NetworkInterface>();
        List<String> macs = EC2MetadataUtils.getItems("/latest/meta-data/network/interfaces/macs/");
        for (String mac : macs) {
            String key = mac.trim();
            if (key.endsWith("/")) {
                key = key.substring(0, key.length() - 1);
            }
            networkInterfaces.add(new NetworkInterface(key));
        }
        return networkInterfaces;
    }

    public static String getUserData() {
        return EC2MetadataUtils.getData(EC2_USERDATA_ROOT);
    }

    public static InstanceInfo getInstanceInfo() {
        return EC2MetadataUtils.doGetInstanceInfo(EC2MetadataUtils.getData("/latest/dynamic/instance-identity/document"));
    }

    static InstanceInfo doGetInstanceInfo(String json) {
        if (json != null) {
            try {
                Map<String, JsonNode> jsonNode = JSON_PARSER.parse(json).asObject();
                return new InstanceInfo(EC2MetadataUtils.stringValue(jsonNode.get("pendingTime")), EC2MetadataUtils.stringValue(jsonNode.get("instanceType")), EC2MetadataUtils.stringValue(jsonNode.get("imageId")), EC2MetadataUtils.stringValue(jsonNode.get("instanceId")), EC2MetadataUtils.stringArrayValue(jsonNode.get("billingProducts")), EC2MetadataUtils.stringValue(jsonNode.get("architecture")), EC2MetadataUtils.stringValue(jsonNode.get("accountId")), EC2MetadataUtils.stringValue(jsonNode.get("kernelId")), EC2MetadataUtils.stringValue(jsonNode.get("ramdiskId")), EC2MetadataUtils.stringValue(jsonNode.get(REGION)), EC2MetadataUtils.stringValue(jsonNode.get("version")), EC2MetadataUtils.stringValue(jsonNode.get("availabilityZone")), EC2MetadataUtils.stringValue(jsonNode.get("privateIp")), EC2MetadataUtils.stringArrayValue(jsonNode.get("devpayProductCodes")), EC2MetadataUtils.stringArrayValue(jsonNode.get("marketplaceProductCodes")));
            }
            catch (Exception e) {
                log.warn("Unable to parse dynamic EC2 instance info (" + json + ") : " + e.getMessage(), e);
            }
        }
        return null;
    }

    private static String stringValue(JsonNode jsonNode) {
        if (jsonNode == null || !jsonNode.isString()) {
            return null;
        }
        return jsonNode.asString();
    }

    private static String[] stringArrayValue(JsonNode jsonNode) {
        if (jsonNode == null || !jsonNode.isArray()) {
            return null;
        }
        return (String[])jsonNode.asArray().stream().filter(JsonNode::isString).map(JsonNode::asString).toArray(String[]::new);
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

    @SdkTestInternalApi
    public static void clearCache() {
        CACHE.clear();
    }

    private static List<String> getItems(String path, int tries, boolean slurp) {
        if (tries == 0) {
            throw SdkClientException.builder().message("Unable to contact EC2 metadata service.").build();
        }
        if (SdkSystemSetting.AWS_EC2_METADATA_DISABLED.getBooleanValueOrThrow().booleanValue()) {
            throw SdkClientException.builder().message("EC2 metadata usage is disabled.").build();
        }
        String token = EC2MetadataUtils.getToken();
        try {
            String hostAddress = EC2_METADATA_CONFIG_PROVIDER.getEndpoint();
            String response = EC2MetadataUtils.doReadResource(new URI(hostAddress + path), token);
            List<String> items = slurp ? Collections.singletonList(response) : Arrays.asList(response.split("\n"));
            return items;
        }
        catch (SdkClientException ace) {
            log.warn("Unable to retrieve the requested metadata.");
            return null;
        }
        catch (IOException | RuntimeException | URISyntaxException e) {
            if (tries - 1 == 0) {
                throw SdkClientException.builder().message("Unable to contact EC2 metadata service.").cause(e).build();
            }
            int pause = (int)(Math.pow(2.0, 3 - tries) * 250.0);
            try {
                Thread.sleep(pause < 250 ? 250L : (long)pause);
            }
            catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
            }
            return EC2MetadataUtils.getItems(path, tries - 1, slurp);
        }
    }

    private static String doReadResource(URI resource, String token) throws IOException {
        return HttpResourcesUtils.instance().readResource(new DefaultEndpointProvider(resource, token), "GET");
    }

    public static String getToken() {
        try {
            return HttpResourcesUtils.instance().readResource(TOKEN_ENDPOINT_PROVIDER, "PUT");
        }
        catch (Exception e) {
            boolean is400ServiceException;
            boolean bl = is400ServiceException = e instanceof SdkServiceException && ((SdkServiceException)e).statusCode() == 400;
            if (is400ServiceException) {
                throw SdkClientException.builder().message("Unable to fetch metadata token").cause(e).build();
            }
            return null;
        }
    }

    private static String fetchData(String path) {
        return EC2MetadataUtils.fetchData(path, false);
    }

    private static String fetchData(String path, boolean force) {
        return EC2MetadataUtils.fetchData(path, force, 3);
    }

    public static String fetchData(String path, boolean force, int attempts) {
        if (SdkSystemSetting.AWS_EC2_METADATA_DISABLED.getBooleanValueOrThrow().booleanValue()) {
            throw SdkClientException.builder().message("EC2 metadata usage is disabled.").build();
        }
        try {
            if (force || !CACHE.containsKey(path)) {
                CACHE.put(path, EC2MetadataUtils.getData(path, attempts));
            }
            return CACHE.get(path);
        }
        catch (SdkClientException e) {
            throw e;
        }
        catch (RuntimeException e) {
            return null;
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
        private final String[] marketplaceProductCodes;

        public InstanceInfo(String pendingTime, String instanceType, String imageId, String instanceId, String[] billingProducts, String architecture, String accountId, String kernelId, String ramdiskId, String region, String version, String availabilityZone, String privateIp, String[] devpayProductCodes, String[] marketplaceProductCodes) {
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
            this.marketplaceProductCodes = marketplaceProductCodes == null ? null : (String[])marketplaceProductCodes.clone();
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

        public String[] getMarketplaceProductCodes() {
            return this.marketplaceProductCodes == null ? null : (String[])this.marketplaceProductCodes.clone();
        }
    }

    private static final class DefaultEndpointProvider
    implements ResourcesEndpointProvider {
        private final URI endpoint;
        private final String metadataToken;

        private DefaultEndpointProvider(URI endpoint, String metadataToken) {
            this.endpoint = endpoint;
            this.metadataToken = metadataToken;
        }

        @Override
        public URI endpoint() {
            return this.endpoint;
        }

        @Override
        public Map<String, String> headers() {
            HashMap<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put("User-Agent", SdkUserAgent.create().userAgent());
            requestHeaders.put("Accept", "*/*");
            requestHeaders.put("Connection", "keep-alive");
            if (this.metadataToken != null) {
                requestHeaders.put(EC2MetadataUtils.EC2_METADATA_TOKEN_HEADER, this.metadataToken);
            }
            return requestHeaders;
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
            return this.getItems(EC2MetadataUtils.EC2_METADATA_ROOT + this.path + "ipv4-associations/" + publicIp);
        }

        private String getData(String key) {
            if (this.data.containsKey(key)) {
                return this.data.get(key);
            }
            if (null == this.availableKeys) {
                this.availableKeys = EC2MetadataUtils.getItems(EC2MetadataUtils.EC2_METADATA_ROOT + this.path);
            }
            if (this.availableKeys.contains(key)) {
                this.data.put(key, EC2MetadataUtils.getData(EC2MetadataUtils.EC2_METADATA_ROOT + this.path + key));
                return this.data.get(key);
            }
            return null;
        }

        private List<String> getItems(String key) {
            if (null == this.availableKeys) {
                this.availableKeys = EC2MetadataUtils.getItems(EC2MetadataUtils.EC2_METADATA_ROOT + this.path);
            }
            if (this.availableKeys.contains(key)) {
                return EC2MetadataUtils.getItems(EC2MetadataUtils.EC2_METADATA_ROOT + this.path + key);
            }
            return Collections.emptyList();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.internal.json.Json
 *  com.hazelcast.internal.json.JsonArray
 *  com.hazelcast.internal.json.JsonObject
 *  com.hazelcast.internal.json.JsonObject$Member
 *  com.hazelcast.internal.json.JsonValue
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 */
package com.hazelcast.kubernetes;

import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.kubernetes.KubernetesClientException;
import com.hazelcast.kubernetes.RestClient;
import com.hazelcast.kubernetes.RestClientException;
import com.hazelcast.kubernetes.RetryUtils;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

class KubernetesClient {
    private static final ILogger LOGGER = Logger.getLogger(KubernetesClient.class);
    private static final List<String> NON_RETRYABLE_KEYWORDS = Arrays.asList("\"reason\":\"Forbidden\"", "\"reason\":\"Unauthorized\"", "Failure in generating SSLSocketFactory");
    private final String namespace;
    private final String kubernetesMaster;
    private final String apiToken;
    private final String caCertificate;
    private final int retries;
    private boolean useNodeNameAsExternalAddress;
    private boolean isNoPublicIpAlreadyLogged;

    KubernetesClient(String namespace, String kubernetesMaster, String apiToken, String caCertificate, int retries, boolean useNodeNameAsExternalAddress) {
        this.namespace = namespace;
        this.kubernetesMaster = kubernetesMaster;
        this.apiToken = apiToken;
        this.caCertificate = caCertificate;
        this.retries = retries;
        this.useNodeNameAsExternalAddress = useNodeNameAsExternalAddress;
    }

    List<Endpoint> endpoints() {
        try {
            String urlString = String.format("%s/api/v1/namespaces/%s/pods", this.kubernetesMaster, this.namespace);
            return this.enrichWithPublicAddresses(KubernetesClient.parsePodsList(this.callGet(urlString)));
        }
        catch (RestClientException e) {
            return KubernetesClient.handleKnownException(e);
        }
    }

    List<Endpoint> endpointsByServiceLabel(String serviceLabel, String serviceLabelValue) {
        try {
            String param = String.format("labelSelector=%s=%s", serviceLabel, serviceLabelValue);
            String urlString = String.format("%s/api/v1/namespaces/%s/endpoints?%s", this.kubernetesMaster, this.namespace, param);
            return this.enrichWithPublicAddresses(KubernetesClient.parseEndpointsList(this.callGet(urlString)));
        }
        catch (RestClientException e) {
            return KubernetesClient.handleKnownException(e);
        }
    }

    List<Endpoint> endpointsByName(String endpointName) {
        try {
            String urlString = String.format("%s/api/v1/namespaces/%s/endpoints/%s", this.kubernetesMaster, this.namespace, endpointName);
            return this.enrichWithPublicAddresses(KubernetesClient.parseEndpoints((JsonValue)this.callGet(urlString)));
        }
        catch (RestClientException e) {
            return KubernetesClient.handleKnownException(e);
        }
    }

    List<Endpoint> endpointsByPodLabel(String podLabel, String podLabelValue) {
        try {
            String param = String.format("labelSelector=%s=%s", podLabel, podLabelValue);
            String urlString = String.format("%s/api/v1/namespaces/%s/pods?%s", this.kubernetesMaster, this.namespace, param);
            return this.enrichWithPublicAddresses(KubernetesClient.parsePodsList(this.callGet(urlString)));
        }
        catch (RestClientException e) {
            return KubernetesClient.handleKnownException(e);
        }
    }

    String zone(String podName) {
        String nodeUrlString = String.format("%s/api/v1/nodes/%s", this.kubernetesMaster, this.nodeName(podName));
        return KubernetesClient.extractZone(this.callGet(nodeUrlString));
    }

    String nodeName(String podName) {
        String podUrlString = String.format("%s/api/v1/namespaces/%s/pods/%s", this.kubernetesMaster, this.namespace, podName);
        return KubernetesClient.extractNodeName(this.callGet(podUrlString));
    }

    private static List<Endpoint> parsePodsList(JsonObject podsListJson) {
        ArrayList<Endpoint> addresses = new ArrayList<Endpoint>();
        for (JsonValue item : KubernetesClient.toJsonArray(podsListJson.get("items"))) {
            JsonObject status = item.asObject().get("status").asObject();
            String ip = KubernetesClient.toString(status.get("podIP"));
            if (ip == null) continue;
            Integer port = KubernetesClient.extractContainerPort(item);
            addresses.add(new Endpoint(new EndpointAddress(ip, port), KubernetesClient.isReady(status)));
        }
        return addresses;
    }

    private static Integer extractContainerPort(JsonValue podItemJson) {
        JsonValue port;
        JsonValue containerPort;
        JsonValue container;
        JsonArray ports;
        JsonArray containers = KubernetesClient.toJsonArray(podItemJson.asObject().get("spec").asObject().get("containers"));
        if (containers.size() == 1 && (ports = KubernetesClient.toJsonArray((container = containers.get(0)).asObject().get("ports"))).size() == 1 && (containerPort = (port = ports.get(0)).asObject().get("containerPort")) != null && containerPort.isNumber()) {
            return containerPort.asInt();
        }
        return null;
    }

    private static boolean isReady(JsonObject podItemStatusJson) {
        for (JsonValue containerStatus : KubernetesClient.toJsonArray(podItemStatusJson.get("containerStatuses"))) {
            if (containerStatus.asObject().get("ready").asBoolean()) continue;
            return false;
        }
        return true;
    }

    private static List<Endpoint> parseEndpointsList(JsonObject endpointsListJson) {
        ArrayList<Endpoint> endpoints = new ArrayList<Endpoint>();
        for (JsonValue item : KubernetesClient.toJsonArray(endpointsListJson.get("items"))) {
            endpoints.addAll(KubernetesClient.parseEndpoints(item));
        }
        return endpoints;
    }

    private static List<Endpoint> parseEndpoints(JsonValue endpointItemJson) {
        ArrayList<Endpoint> addresses = new ArrayList<Endpoint>();
        for (JsonValue subset : KubernetesClient.toJsonArray(endpointItemJson.asObject().get("subsets"))) {
            Integer endpointPort = KubernetesClient.extractPort(subset);
            for (JsonValue address : KubernetesClient.toJsonArray(subset.asObject().get("addresses"))) {
                addresses.add(KubernetesClient.extractEntrypointAddress(address, endpointPort, true));
            }
            for (JsonValue address : KubernetesClient.toJsonArray(subset.asObject().get("notReadyAddresses"))) {
                addresses.add(KubernetesClient.extractEntrypointAddress(address, endpointPort, false));
            }
        }
        return addresses;
    }

    private static Integer extractPort(JsonValue subsetJson) {
        JsonArray ports = KubernetesClient.toJsonArray(subsetJson.asObject().get("ports"));
        if (ports.size() == 1) {
            JsonValue port = ports.get(0);
            return port.asObject().get("port").asInt();
        }
        return null;
    }

    private static Endpoint extractEntrypointAddress(JsonValue endpointAddressJson, Integer endpointPort, boolean isReady) {
        String ip = endpointAddressJson.asObject().get("ip").asString();
        Integer port = KubernetesClient.extractHazelcastServicePortFrom(endpointAddressJson, endpointPort);
        Map<String, Object> additionalProperties = KubernetesClient.extractAdditionalPropertiesFrom(endpointAddressJson);
        return new Endpoint(new EndpointAddress(ip, port), isReady, additionalProperties);
    }

    private static Integer extractHazelcastServicePortFrom(JsonValue endpointAddressJson, Integer endpointPort) {
        JsonValue servicePort = endpointAddressJson.asObject().get("hazelcast-service-port");
        if (servicePort != null && servicePort.isNumber()) {
            return servicePort.asInt();
        }
        return endpointPort;
    }

    private static Map<String, Object> extractAdditionalPropertiesFrom(JsonValue endpointAddressJson) {
        HashSet<String> knownFieldNames = new HashSet<String>(Arrays.asList("ip", "nodeName", "targetRef", "hostname", "hazelcast-service-port"));
        HashMap<String, Object> result = new HashMap<String, Object>();
        for (JsonObject.Member member : endpointAddressJson.asObject()) {
            if (knownFieldNames.contains(member.getName())) continue;
            result.put(member.getName(), KubernetesClient.toString(member.getValue()));
        }
        return result;
    }

    private static String extractNodeName(JsonObject podJson) {
        return KubernetesClient.toString(podJson.get("spec").asObject().get("nodeName"));
    }

    private static String extractZone(JsonObject nodeJson) {
        JsonObject labels = nodeJson.get("metadata").asObject().get("labels").asObject();
        JsonValue zone = labels.get("failure-domain.kubernetes.io/zone");
        if (zone != null) {
            return KubernetesClient.toString(zone);
        }
        return KubernetesClient.toString(labels.get("failure-domain.beta.kubernetes.io/zone"));
    }

    private List<Endpoint> enrichWithPublicAddresses(List<Endpoint> endpoints) {
        try {
            String endpointsUrl = String.format("%s/api/v1/namespaces/%s/endpoints", this.kubernetesMaster, this.namespace);
            JsonObject endpointsJson = this.callGet(endpointsUrl);
            List<EndpointAddress> privateAddresses = KubernetesClient.privateAddresses(endpoints);
            Map<EndpointAddress, String> services = KubernetesClient.extractServices(endpointsJson, privateAddresses);
            Map<EndpointAddress, String> nodes = KubernetesClient.extractNodes(endpointsJson, privateAddresses);
            HashMap<EndpointAddress, String> publicIps = new HashMap<EndpointAddress, String>();
            HashMap<EndpointAddress, Integer> publicPorts = new HashMap<EndpointAddress, Integer>();
            HashMap<String, String> cachedNodePublicIps = new HashMap<String, String>();
            for (Map.Entry<EndpointAddress, String> serviceEntry : services.entrySet()) {
                EndpointAddress privateAddress = serviceEntry.getKey();
                String service = serviceEntry.getValue();
                String serviceUrl = String.format("%s/api/v1/namespaces/%s/services/%s", this.kubernetesMaster, this.namespace, service);
                JsonObject serviceJson = this.callGet(serviceUrl);
                try {
                    String loadBalancerIp = KubernetesClient.extractLoadBalancerIp(serviceJson);
                    Integer servicePort = KubernetesClient.extractServicePort(serviceJson);
                    publicIps.put(privateAddress, loadBalancerIp);
                    publicPorts.put(privateAddress, servicePort);
                }
                catch (Exception e) {
                    String nodePublicAddress;
                    Integer nodePort = KubernetesClient.extractNodePort(serviceJson);
                    String node = nodes.get(privateAddress);
                    if (cachedNodePublicIps.containsKey(node)) {
                        nodePublicAddress = (String)cachedNodePublicIps.get(node);
                    } else {
                        nodePublicAddress = this.externalAddressForNode(node);
                        cachedNodePublicIps.put(node, nodePublicAddress);
                    }
                    publicIps.put(privateAddress, nodePublicAddress);
                    publicPorts.put(privateAddress, nodePort);
                }
            }
            return KubernetesClient.createEndpoints(endpoints, publicIps, publicPorts);
        }
        catch (Exception e) {
            LOGGER.finest((Throwable)e);
            if (!this.isNoPublicIpAlreadyLogged) {
                LOGGER.warning("Cannot fetch public IPs of Hazelcast Member PODs, you won't be able to use Hazelcast Smart Client from outside of the Kubernetes network");
                this.isNoPublicIpAlreadyLogged = true;
            }
            return endpoints;
        }
    }

    private static List<EndpointAddress> privateAddresses(List<Endpoint> endpoints) {
        ArrayList<EndpointAddress> result = new ArrayList<EndpointAddress>();
        for (Endpoint endpoint : endpoints) {
            result.add(endpoint.getPrivateAddress());
        }
        return result;
    }

    private static Map<EndpointAddress, String> extractServices(JsonObject endpointsListJson, List<EndpointAddress> privateAddresses) {
        HashMap<EndpointAddress, String> result = new HashMap<EndpointAddress, String>();
        HashSet<EndpointAddress> left = new HashSet<EndpointAddress>(privateAddresses);
        for (JsonValue item : KubernetesClient.toJsonArray(endpointsListJson.get("items"))) {
            EndpointAddress address;
            String service = KubernetesClient.toString(item.asObject().get("metadata").asObject().get("name"));
            List<Endpoint> endpoints = KubernetesClient.parseEndpoints(item);
            if (endpoints.size() != 1 || !left.contains(address = endpoints.get(0).getPrivateAddress())) continue;
            result.put(address, service);
            left.remove(address);
        }
        if (!left.isEmpty()) {
            throw new KubernetesClientException(String.format("Cannot fetch services dedicated to the following PODs: %s", left));
        }
        return result;
    }

    private static Map<EndpointAddress, String> extractNodes(JsonObject endpointsListJson, List<EndpointAddress> privateAddresses) {
        HashMap<EndpointAddress, String> result = new HashMap<EndpointAddress, String>();
        HashSet<EndpointAddress> left = new HashSet<EndpointAddress>(privateAddresses);
        for (JsonValue item : KubernetesClient.toJsonArray(endpointsListJson.get("items"))) {
            for (JsonValue subset : KubernetesClient.toJsonArray(item.asObject().get("subsets"))) {
                JsonObject subsetObject = subset.asObject();
                ArrayList<Integer> ports = new ArrayList<Integer>();
                for (JsonValue port : KubernetesClient.toJsonArray(subsetObject.get("ports"))) {
                    ports.add(port.asObject().get("port").asInt());
                }
                HashMap<EndpointAddress, String> nodes = new HashMap<EndpointAddress, String>();
                nodes.putAll(KubernetesClient.extractNodes(subsetObject.get("addresses"), ports));
                nodes.putAll(KubernetesClient.extractNodes(subsetObject.get("notReadyAddresses"), ports));
                for (Map.Entry nodeEntry : nodes.entrySet()) {
                    EndpointAddress address = (EndpointAddress)nodeEntry.getKey();
                    if (!privateAddresses.contains(address)) continue;
                    result.put(address, (String)nodes.get(address));
                    left.remove(address);
                }
            }
        }
        if (!left.isEmpty()) {
            throw new KubernetesClientException(String.format("Cannot fetch nodeName from the following PODs: %s", left));
        }
        return result;
    }

    private static Map<EndpointAddress, String> extractNodes(JsonValue addressesJson, List<Integer> ports) {
        HashMap<EndpointAddress, String> result = new HashMap<EndpointAddress, String>();
        for (JsonValue address : KubernetesClient.toJsonArray(addressesJson)) {
            String ip = address.asObject().get("ip").asString();
            String nodeName = KubernetesClient.toString(address.asObject().get("nodeName"));
            for (Integer port : ports) {
                result.put(new EndpointAddress(ip, port), nodeName);
            }
        }
        return result;
    }

    private static String extractLoadBalancerIp(JsonObject serviceResponse) {
        return serviceResponse.get("status").asObject().get("loadBalancer").asObject().get("ingress").asArray().get(0).asObject().get("ip").asString();
    }

    private static Integer extractServicePort(JsonObject serviceJson) {
        JsonArray ports = KubernetesClient.toJsonArray(serviceJson.get("spec").asObject().get("ports"));
        if (ports.size() != 1) {
            throw new KubernetesClientException("Cannot fetch nodePort from the service");
        }
        return ports.get(0).asObject().get("port").asInt();
    }

    private static Integer extractNodePort(JsonObject serviceJson) {
        JsonArray ports = KubernetesClient.toJsonArray(serviceJson.get("spec").asObject().get("ports"));
        if (ports.size() != 1) {
            throw new KubernetesClientException("Cannot fetch nodePort from the service");
        }
        return ports.get(0).asObject().get("nodePort").asInt();
    }

    private String externalAddressForNode(String node) {
        String nodeExternalAddress;
        if (this.useNodeNameAsExternalAddress) {
            LOGGER.info("Using node name instead of public IP for node, must be available from client: " + node);
            nodeExternalAddress = node;
        } else {
            String nodeUrl = String.format("%s/api/v1/nodes/%s", this.kubernetesMaster, node);
            nodeExternalAddress = KubernetesClient.extractNodePublicIp(this.callGet(nodeUrl));
        }
        return nodeExternalAddress;
    }

    private static String extractNodePublicIp(JsonObject nodeJson) {
        for (JsonValue address : KubernetesClient.toJsonArray(nodeJson.get("status").asObject().get("addresses"))) {
            if (!"ExternalIP".equals(address.asObject().get("type").asString())) continue;
            return address.asObject().get("address").asString();
        }
        throw new KubernetesClientException("Node does not have ExternalIP assigned");
    }

    private static List<Endpoint> createEndpoints(List<Endpoint> endpoints, Map<EndpointAddress, String> publicIps, Map<EndpointAddress, Integer> publicPorts) {
        ArrayList<Endpoint> result = new ArrayList<Endpoint>();
        for (Endpoint endpoint : endpoints) {
            EndpointAddress privateAddress = endpoint.getPrivateAddress();
            EndpointAddress publicAddress = new EndpointAddress(publicIps.get(privateAddress), publicPorts.get(privateAddress));
            result.add(new Endpoint(privateAddress, publicAddress, endpoint.isReady(), endpoint.getAdditionalProperties()));
        }
        return result;
    }

    private JsonObject callGet(final String urlString) {
        return RetryUtils.retry(new Callable<JsonObject>(){

            @Override
            public JsonObject call() {
                return Json.parse((String)RestClient.create(urlString).withHeader("Authorization", String.format("Bearer %s", KubernetesClient.this.apiToken)).withCaCertificates(KubernetesClient.this.caCertificate).get()).asObject();
            }
        }, this.retries, NON_RETRYABLE_KEYWORDS);
    }

    private static List<Endpoint> handleKnownException(RestClientException e) {
        if (e.getHttpErrorCode() == 401) {
            LOGGER.severe("Kubernetes API authorization failure, please check your 'api-token' property");
        } else if (e.getHttpErrorCode() == 403) {
            LOGGER.severe("Kubernetes API forbidden access, please check that your Service Account have the correct (Cluster) Role rules");
        } else {
            throw e;
        }
        LOGGER.finest((Throwable)e);
        return Collections.emptyList();
    }

    private static JsonArray toJsonArray(JsonValue jsonValue) {
        if (jsonValue == null || jsonValue.isNull()) {
            return new JsonArray();
        }
        return jsonValue.asArray();
    }

    private static String toString(JsonValue jsonValue) {
        if (jsonValue == null || jsonValue.isNull()) {
            return null;
        }
        if (jsonValue.isString()) {
            return jsonValue.asString();
        }
        return jsonValue.toString();
    }

    static final class EndpointAddress {
        private final String ip;
        private final Integer port;

        EndpointAddress(String ip, Integer port) {
            this.ip = ip;
            this.port = port;
        }

        String getIp() {
            return this.ip;
        }

        Integer getPort() {
            return this.port;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            EndpointAddress address = (EndpointAddress)o;
            if (this.ip != null ? !this.ip.equals(address.ip) : address.ip != null) {
                return false;
            }
            return this.port != null ? this.port.equals(address.port) : address.port == null;
        }

        public int hashCode() {
            int result = this.ip != null ? this.ip.hashCode() : 0;
            result = 31 * result + (this.port != null ? this.port.hashCode() : 0);
            return result;
        }

        public String toString() {
            return String.format("%s:%s", this.ip, this.port);
        }
    }

    static final class Endpoint {
        private final EndpointAddress privateAddress;
        private final EndpointAddress publicAddress;
        private final boolean isReady;
        private final Map<String, Object> additionalProperties;

        Endpoint(EndpointAddress privateAddress, boolean isReady) {
            this.privateAddress = privateAddress;
            this.publicAddress = null;
            this.isReady = isReady;
            this.additionalProperties = Collections.emptyMap();
        }

        Endpoint(EndpointAddress privateAddress, boolean isReady, Map<String, Object> additionalProperties) {
            this.privateAddress = privateAddress;
            this.publicAddress = null;
            this.isReady = isReady;
            this.additionalProperties = additionalProperties;
        }

        Endpoint(EndpointAddress privateAddress, EndpointAddress publicAddress, boolean isReady, Map<String, Object> additionalProperties) {
            this.privateAddress = privateAddress;
            this.publicAddress = publicAddress;
            this.isReady = isReady;
            this.additionalProperties = additionalProperties;
        }

        EndpointAddress getPublicAddress() {
            return this.publicAddress;
        }

        EndpointAddress getPrivateAddress() {
            return this.privateAddress;
        }

        boolean isReady() {
            return this.isReady;
        }

        Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }
    }
}


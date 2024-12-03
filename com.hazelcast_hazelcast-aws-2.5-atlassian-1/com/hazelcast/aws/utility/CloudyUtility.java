/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.DomConfigHelper
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 */
package com.hazelcast.aws.utility;

import com.hazelcast.config.DomConfigHelper;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class CloudyUtility {
    private static final String NODE_ITEM = "item";
    private static final String NODE_VALUE = "value";
    private static final String NODE_KEY = "key";
    private static final ILogger LOGGER = Logger.getLogger(CloudyUtility.class);

    private CloudyUtility() {
    }

    public static Map<String, String> unmarshalTheResponse(InputStream stream) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(stream);
            Element element = doc.getDocumentElement();
            NodeHolder elementNodeHolder = new NodeHolder(element);
            LinkedHashMap<String, String> addresses = new LinkedHashMap<String, String>();
            List<NodeHolder> reservationSet = elementNodeHolder.getSubNodes("reservationset");
            for (NodeHolder reservation : reservationSet) {
                List<NodeHolder> items = reservation.getSubNodes(NODE_ITEM);
                for (NodeHolder item : items) {
                    NodeHolder instancesSet = item.getFirstSubNode("instancesset");
                    addresses.putAll(instancesSet.getAddresses());
                }
            }
            return addresses;
        }
        catch (Exception e) {
            LOGGER.warning((Throwable)e);
            return new LinkedHashMap<String, String>();
        }
    }

    private static class NodeHolder {
        private final Node node;

        NodeHolder(Node node) {
            this.node = node;
        }

        private static String getInstanceName(NodeHolder nodeHolder) {
            NodeHolder tagSetHolder = nodeHolder.getFirstSubNode("tagset");
            if (tagSetHolder.getNode() == null) {
                return null;
            }
            for (NodeHolder itemHolder : tagSetHolder.getSubNodes(CloudyUtility.NODE_ITEM)) {
                Node valueNode;
                String nodeValue;
                Node keyNode = itemHolder.getFirstSubNode(CloudyUtility.NODE_KEY).getNode();
                if (keyNode == null || keyNode.getFirstChild() == null || !"Name".equals(nodeValue = keyNode.getFirstChild().getNodeValue()) || (valueNode = itemHolder.getFirstSubNode(CloudyUtility.NODE_VALUE).getNode()) == null || valueNode.getFirstChild() == null) continue;
                return valueNode.getFirstChild().getNodeValue();
            }
            return null;
        }

        private static String getIp(String name, NodeHolder nodeHolder) {
            Node child = nodeHolder.getFirstSubNode(name).getNode();
            return child == null ? null : child.getFirstChild().getNodeValue();
        }

        Node getNode() {
            return this.node;
        }

        NodeHolder getFirstSubNode(String name) {
            if (this.node == null) {
                return new NodeHolder(null);
            }
            for (Node child : DomConfigHelper.childElements((Node)this.node)) {
                if (!name.equals(DomConfigHelper.cleanNodeName((Node)child))) continue;
                return new NodeHolder(child);
            }
            return new NodeHolder(null);
        }

        List<NodeHolder> getSubNodes(String name) {
            ArrayList<NodeHolder> result = new ArrayList<NodeHolder>();
            if (this.node == null) {
                return result;
            }
            for (Node child : DomConfigHelper.childElements((Node)this.node)) {
                if (!name.equals(DomConfigHelper.cleanNodeName((Node)child))) continue;
                result.add(new NodeHolder(child));
            }
            return result;
        }

        Map<String, String> getAddresses() {
            LinkedHashMap<String, String> privatePublicPairs = new LinkedHashMap<String, String>();
            if (this.node == null) {
                return privatePublicPairs;
            }
            for (NodeHolder childHolder : this.getSubNodes(CloudyUtility.NODE_ITEM)) {
                String privateIp = NodeHolder.getIp("privateipaddress", childHolder);
                String publicIp = NodeHolder.getIp("ipaddress", childHolder);
                String instanceName = NodeHolder.getInstanceName(childHolder);
                if (privateIp == null) continue;
                privatePublicPairs.put(privateIp, publicIp);
                LOGGER.finest(String.format("Accepting EC2 instance [%s][%s]", instanceName, privateIp));
            }
            return privatePublicPairs;
        }
    }
}


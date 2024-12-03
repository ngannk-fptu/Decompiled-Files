/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ClassFilter;
import com.hazelcast.config.DomConfigHelper;
import com.hazelcast.config.DomConfigProcessor;
import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.JavaSerializationFilterConfig;
import com.hazelcast.config.NativeMemoryConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.memory.MemorySize;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.util.StringUtil;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class AbstractDomConfigProcessor
implements DomConfigProcessor {
    protected final Set<String> occurrenceSet = new HashSet<String>();
    private final boolean domLevel3;

    protected AbstractDomConfigProcessor(boolean domLevel3) {
        this.domLevel3 = domLevel3;
    }

    protected String getTextContent(Node node) {
        return DomConfigHelper.getTextContent(node, this.domLevel3);
    }

    protected String getAttribute(Node node, String attName) {
        return DomConfigHelper.getAttribute(node, attName, this.domLevel3);
    }

    protected void fillProperties(Node node, Map<String, Comparable> properties) {
        DomConfigHelper.fillProperties(node, properties, this.domLevel3);
    }

    protected void fillProperties(Node node, Properties properties) {
        DomConfigHelper.fillProperties(node, properties, this.domLevel3);
    }

    protected SocketInterceptorConfig parseSocketInterceptorConfig(Node node) {
        SocketInterceptorConfig socketInterceptorConfig = new SocketInterceptorConfig();
        NamedNodeMap atts = node.getAttributes();
        Node enabledNode = atts.getNamedItem("enabled");
        boolean enabled = enabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(enabledNode).trim());
        socketInterceptorConfig.setEnabled(enabled);
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("class-name".equals(nodeName)) {
                socketInterceptorConfig.setClassName(this.getTextContent(n).trim());
                continue;
            }
            if (!"properties".equals(nodeName)) continue;
            this.fillProperties(n, socketInterceptorConfig.getProperties());
        }
        return socketInterceptorConfig;
    }

    protected SerializationConfig parseSerialization(Node node) {
        SerializationConfig serializationConfig = new SerializationConfig();
        for (Node child : DomConfigHelper.childElements(node)) {
            String value;
            String name = DomConfigHelper.cleanNodeName(child);
            if ("portable-version".equals(name)) {
                value = this.getTextContent(child);
                serializationConfig.setPortableVersion(DomConfigHelper.getIntegerValue(name, value));
                continue;
            }
            if ("check-class-def-errors".equals(name)) {
                value = this.getTextContent(child);
                serializationConfig.setCheckClassDefErrors(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("use-native-byte-order".equals(name)) {
                serializationConfig.setUseNativeByteOrder(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("byte-order".equals(name)) {
                value = this.getTextContent(child);
                ByteOrder byteOrder = null;
                if (ByteOrder.BIG_ENDIAN.toString().equals(value)) {
                    byteOrder = ByteOrder.BIG_ENDIAN;
                } else if (ByteOrder.LITTLE_ENDIAN.toString().equals(value)) {
                    byteOrder = ByteOrder.LITTLE_ENDIAN;
                }
                serializationConfig.setByteOrder(byteOrder != null ? byteOrder : ByteOrder.BIG_ENDIAN);
                continue;
            }
            if ("enable-compression".equals(name)) {
                serializationConfig.setEnableCompression(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("enable-shared-object".equals(name)) {
                serializationConfig.setEnableSharedObject(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("allow-unsafe".equals(name)) {
                serializationConfig.setAllowUnsafe(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("data-serializable-factories".equals(name)) {
                this.fillDataSerializableFactories(child, serializationConfig);
                continue;
            }
            if ("portable-factories".equals(name)) {
                this.fillPortableFactories(child, serializationConfig);
                continue;
            }
            if ("serializers".equals(name)) {
                this.fillSerializers(child, serializationConfig);
                continue;
            }
            if (!"java-serialization-filter".equals(name)) continue;
            this.fillJavaSerializationFilter(child, serializationConfig);
        }
        return serializationConfig;
    }

    protected void fillDataSerializableFactories(Node node, SerializationConfig serializationConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(child);
            if (!"data-serializable-factory".equals(name)) continue;
            String value = this.getTextContent(child);
            Node factoryIdNode = child.getAttributes().getNamedItem("factory-id");
            if (factoryIdNode == null) {
                throw new IllegalArgumentException("'factory-id' attribute of 'data-serializable-factory' is required!");
            }
            int factoryId = Integer.parseInt(this.getTextContent(factoryIdNode));
            serializationConfig.addDataSerializableFactoryClass(factoryId, value);
        }
    }

    protected void fillPortableFactories(Node node, SerializationConfig serializationConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(child);
            if (!"portable-factory".equals(name)) continue;
            String value = this.getTextContent(child);
            Node factoryIdNode = child.getAttributes().getNamedItem("factory-id");
            if (factoryIdNode == null) {
                throw new IllegalArgumentException("'factory-id' attribute of 'portable-factory' is required!");
            }
            int factoryId = Integer.parseInt(this.getTextContent(factoryIdNode));
            serializationConfig.addPortableFactoryClass(factoryId, value);
        }
    }

    protected void fillSerializers(Node node, SerializationConfig serializationConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(child);
            String value = this.getTextContent(child);
            if ("serializer".equals(name)) {
                SerializerConfig serializerConfig = new SerializerConfig();
                String typeClassName = this.getAttribute(child, "type-class");
                String className = this.getAttribute(child, "class-name");
                serializerConfig.setTypeClassName(typeClassName);
                serializerConfig.setClassName(className);
                serializationConfig.addSerializerConfig(serializerConfig);
                continue;
            }
            if (!"global-serializer".equals(name)) continue;
            GlobalSerializerConfig globalSerializerConfig = new GlobalSerializerConfig();
            globalSerializerConfig.setClassName(value);
            String attrValue = this.getAttribute(child, "override-java-serialization");
            boolean overrideJavaSerialization = attrValue != null && DomConfigHelper.getBooleanValue(attrValue.trim());
            globalSerializerConfig.setOverrideJavaSerialization(overrideJavaSerialization);
            serializationConfig.setGlobalSerializerConfig(globalSerializerConfig);
        }
    }

    protected void fillJavaSerializationFilter(Node node, SerializationConfig serializationConfig) {
        JavaSerializationFilterConfig filterConfig = new JavaSerializationFilterConfig();
        serializationConfig.setJavaSerializationFilterConfig(filterConfig);
        Node defaultsDisabledNode = node.getAttributes().getNamedItem("defaults-disabled");
        boolean defaultsDisabled = defaultsDisabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(defaultsDisabledNode));
        filterConfig.setDefaultsDisabled(defaultsDisabled);
        for (Node child : DomConfigHelper.childElements(node)) {
            ClassFilter list;
            String name = DomConfigHelper.cleanNodeName(child);
            if ("blacklist".equals(name)) {
                list = this.parseClassFilterList(child);
                filterConfig.setBlacklist(list);
                continue;
            }
            if (!"whitelist".equals(name)) continue;
            list = this.parseClassFilterList(child);
            filterConfig.setWhitelist(list);
        }
    }

    protected ClassFilter parseClassFilterList(Node node) {
        ClassFilter list = new ClassFilter();
        for (Node child : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(child);
            if ("class".equals(name)) {
                list.addClasses(this.getTextContent(child));
                continue;
            }
            if ("package".equals(name)) {
                list.addPackages(this.getTextContent(child));
                continue;
            }
            if (!"prefix".equals(name)) continue;
            list.addPrefixes(this.getTextContent(child));
        }
        return list;
    }

    protected SSLConfig parseSslConfig(Node node) {
        SSLConfig sslConfig = new SSLConfig();
        NamedNodeMap atts = node.getAttributes();
        Node enabledNode = atts.getNamedItem("enabled");
        boolean enabled = enabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(enabledNode));
        sslConfig.setEnabled(enabled);
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("factory-class-name".equals(nodeName)) {
                sslConfig.setFactoryClassName(this.getTextContent(n));
                continue;
            }
            if (!"properties".equals(nodeName)) continue;
            this.fillProperties(n, sslConfig.getProperties());
        }
        return sslConfig;
    }

    protected void fillNativeMemoryConfig(Node node, NativeMemoryConfig nativeMemoryConfig) {
        NamedNodeMap atts = node.getAttributes();
        Node enabledNode = atts.getNamedItem("enabled");
        boolean enabled = enabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(enabledNode).trim());
        nativeMemoryConfig.setEnabled(enabled);
        Node allocTypeNode = atts.getNamedItem("allocator-type");
        String allocType = this.getTextContent(allocTypeNode);
        if (allocType != null && !"".equals(allocType)) {
            nativeMemoryConfig.setAllocatorType(NativeMemoryConfig.MemoryAllocatorType.valueOf(StringUtil.upperCaseInternal(allocType)));
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            String value;
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("size".equals(nodeName)) {
                NamedNodeMap attrs = n.getAttributes();
                String value2 = this.getTextContent(attrs.getNamedItem("value"));
                MemoryUnit unit = MemoryUnit.valueOf(this.getTextContent(attrs.getNamedItem("unit")));
                MemorySize memorySize = new MemorySize(Long.parseLong(value2), unit);
                nativeMemoryConfig.setSize(memorySize);
                continue;
            }
            if ("min-block-size".equals(nodeName)) {
                value = this.getTextContent(n);
                nativeMemoryConfig.setMinBlockSize(Integer.parseInt(value));
                continue;
            }
            if ("page-size".equals(nodeName)) {
                value = this.getTextContent(n);
                nativeMemoryConfig.setPageSize(Integer.parseInt(value));
                continue;
            }
            if (!"metadata-space-percentage".equals(nodeName)) continue;
            value = this.getTextContent(n);
            nativeMemoryConfig.setMetadataSpacePercentage(Float.parseFloat(value));
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.apache.commons.collections4.CollectionUtils
 *  org.slf4j.Logger
 *  org.springframework.core.io.ClassPathResource
 */
package com.atlassian.confluence.schedule.jobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;

public class JmxLoggingHelper {
    private static final MBeanServer M_BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /*
     * Enabled aggressive exception aggregation
     */
    public List<InstrumentQuery> readQueriesFromConfig(String filename, Logger log) {
        try (InputStream in = new ClassPathResource(filename).getInputStream();){
            List<InstrumentQuery> list;
            try (InputStreamReader reader = new InputStreamReader(in, Charset.defaultCharset());){
                log.debug("Reading {}: {} bytes", (Object)filename, (Object)in.available());
                InstrumentQuery[] queries = (InstrumentQuery[])this.objectMapper.readerForArrayOf(InstrumentQuery.class).readValue((Reader)reader);
                log.debug("Number of queries found: {}", (Object)queries.length);
                list = List.of(queries);
            }
            return list;
        }
        catch (IOException | RuntimeException e) {
            log.warn("Couldn't read {}, JMX instruments will not be logged: {}", (Object)filename, (Object)e.getMessage());
            return Collections.emptyList();
        }
    }

    public void logQueryResults(InstrumentQuery instrumentQuery, Logger log) {
        try {
            instrumentQuery.tryInit();
            ArrayList<InstrumentResult> result = new ArrayList<InstrumentResult>();
            log.debug("Running query: Application Metrics");
            for (ObjectName objectName : instrumentQuery.objectNames) {
                AttributeList attributeList = M_BEAN_SERVER.getAttributes(objectName, instrumentQuery.attributes);
                result.add(new InstrumentResult(JmxLoggingHelper.getCurrentTimestamp(), instrumentQuery.label, objectName.getCanonicalName(), attributeList.asList()));
            }
            log.info("{}: {}", (Object)instrumentQuery.label, (Object)this.objectMapper.writeValueAsString(result));
        }
        catch (JsonProcessingException | InstanceNotFoundException | MalformedObjectNameException | ReflectionException e) {
            log.error("Error occurred while querying the JMX instrument info: {}", (Object)e.getMessage());
        }
    }

    private static String getCurrentTimestamp() {
        return String.valueOf(Instant.now().getEpochSecond());
    }

    private static class InstrumentResult {
        private final String timestamp;
        private final String label;
        private final String objectName;
        private final List<Attribute> attributes;

        public InstrumentResult(String timestamp, String label, String objectName, List<Attribute> attributes) {
            this.timestamp = timestamp;
            this.label = label;
            this.objectName = objectName;
            this.attributes = attributes;
        }

        public String getTimestamp() {
            return this.timestamp;
        }

        public String getLabel() {
            return this.label;
        }

        public String getObjectName() {
            return this.objectName;
        }

        public List<Attribute> getAttributes() {
            return this.attributes;
        }
    }

    public static class InstrumentQuery {
        private final String label;
        private final String name;
        private final String[] attributes;
        private Set<ObjectName> objectNames;

        @JsonCreator
        public InstrumentQuery(@JsonProperty(value="label") String label, @JsonProperty(value="name") String name, @JsonProperty(value="attributes") String[] attributes) {
            this.label = label;
            this.name = name;
            this.attributes = Arrays.copyOf(attributes, attributes.length);
        }

        void tryInit() throws MalformedObjectNameException {
            if (CollectionUtils.isEmpty(this.objectNames)) {
                this.objectNames = M_BEAN_SERVER.queryNames(new ObjectName(this.name), null);
            }
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            InstrumentQuery that = (InstrumentQuery)o;
            return Objects.equals(this.label, that.label) && Objects.equals(this.name, that.name) && Arrays.equals(this.attributes, that.attributes);
        }

        public int hashCode() {
            int result = Objects.hash(this.label, this.name);
            result = 31 * result + Arrays.hashCode(this.attributes);
            return result;
        }
    }
}


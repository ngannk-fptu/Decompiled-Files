/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  org.apache.http.annotation.Experimental
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.thready.rest;

import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.sun.jersey.spi.resource.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.http.annotation.Experimental;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="threadDump")
@Produces(value={"application/json"})
@Singleton
@Experimental
public class ThreadDumpResource {
    private final PermissionValidationService permissionValidationService;

    public ThreadDumpResource(PermissionValidationService permissionValidationService) {
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
    }

    @GET
    public ThreadDump getThreadDump() {
        this.permissionValidationService.validateIsSysadmin();
        return new ThreadDump();
    }

    public static class ThreadData {
        private String name;
        private List<String> stackTrace;

        @JsonCreator
        public ThreadData(@JsonProperty(value="name") String name, @JsonProperty(value="stackTrace") List<String> stackTrace) {
            this.name = name;
            this.stackTrace = stackTrace;
        }

        public ThreadData() {
        }

        @JsonProperty
        public String getName() {
            return this.name;
        }

        @JsonProperty
        public List<String> getStackTrace() {
            return this.stackTrace;
        }

        @JsonProperty
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty
        public void setStackTrace(List<String> stackTrace) {
            this.stackTrace = stackTrace;
        }
    }

    public static class ThreadDump {
        private final List<ThreadData> threads;

        @JsonCreator
        public ThreadDump(@JsonProperty(value="threads") List<ThreadData> threads) {
            this.threads = threads;
        }

        private ThreadDump() {
            this.threads = new ArrayList<ThreadData>();
            for (Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet()) {
                this.threads.add(new ThreadData(e.getKey().getName(), Arrays.stream((Object[])e.getValue()).map(StackTraceElement::toString).collect(Collectors.toList())));
            }
        }

        @JsonProperty
        public List<ThreadData> getThreads() {
            return this.threads;
        }
    }
}


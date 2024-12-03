/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.container.filter;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class PostReplaceFilter
implements ContainerRequestFilter {
    public static final String PROPERTY_POST_REPLACE_FILTER_CONFIG = "com.sun.jersey.api.container.filter.PostReplaceFilterConfig";
    private final int config;

    public PostReplaceFilter(@Context ResourceConfig rc) {
        this(PostReplaceFilter.configStringToConfig((String)rc.getProperty(PROPERTY_POST_REPLACE_FILTER_CONFIG)));
    }

    public PostReplaceFilter(ConfigFlag ... configFlags) {
        int c = 0;
        for (ConfigFlag cf : configFlags) {
            c |= cf.getFlag();
        }
        if (c == 0) {
            c = 3;
        }
        this.config = c;
    }

    private static ConfigFlag[] configStringToConfig(String configString) {
        if (configString == null) {
            return new ConfigFlag[0];
        }
        String[] parts = configString.toUpperCase().split(",");
        ArrayList<ConfigFlag> result = new ArrayList<ConfigFlag>(parts.length);
        for (String part : parts) {
            if ((part = part.trim()).length() <= 0) continue;
            try {
                result.add(ConfigFlag.valueOf(part));
            }
            catch (IllegalArgumentException e) {
                Logger.getLogger(PostReplaceFilter.class.getName()).log(Level.WARNING, "Invalid config flag for com.sun.jersey.api.container.filter.PostReplaceFilterConfig property: {0}", part.trim());
            }
        }
        return result.toArray(new ConfigFlag[result.size()]);
    }

    private String getParamValue(ConfigFlag configFlag, MultivaluedMap<String, String> paramsMap, String paramName) {
        String value;
        String string = value = configFlag.isPresentIn(this.config) ? paramsMap.getFirst(paramName) : null;
        if (value == null) {
            return null;
        }
        return (value = value.trim()).length() == 0 ? null : value.toUpperCase();
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        String override;
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return request;
        }
        String header = this.getParamValue(ConfigFlag.HEADER, request.getRequestHeaders(), "X-HTTP-Method-Override");
        String query = this.getParamValue(ConfigFlag.QUERY, request.getQueryParameters(), "_method");
        if (header == null) {
            override = query;
        } else {
            override = header;
            if (query != null && !query.equals(header)) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain").entity("Inconsistent POST override.\nX-HTTP-Method-Override: " + header + "\n_method: " + query).build());
            }
        }
        if (override == null) {
            return request;
        }
        request.setMethod(override);
        if (override.equals("GET") && MediaTypes.typeEquals(MediaType.APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {
            UriBuilder ub = request.getRequestUriBuilder();
            Form f = request.getFormParameters();
            for (Map.Entry param : f.entrySet()) {
                ub.queryParam((String)param.getKey(), ((List)param.getValue()).toArray());
            }
            request.setUris(request.getBaseUri(), ub.build(new Object[0]));
        }
        return request;
    }

    public static enum ConfigFlag {
        HEADER(1),
        QUERY(2);

        private final int flag;

        private ConfigFlag(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return this.flag;
        }

        public boolean isPresentIn(int config) {
            return (config & this.flag) == this.flag;
        }
    }
}


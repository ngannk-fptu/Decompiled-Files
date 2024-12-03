/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.web.client.HttpStatusCodeException
 */
package org.springframework.vault.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.client.HttpStatusCodeException;

public abstract class VaultResponses {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(OBJECT_MAPPER);

    public static VaultException buildException(HttpStatusCodeException e) {
        Assert.notNull((Object)((Object)e), (String)"HttpStatusCodeException must not be null");
        String message = VaultResponses.getError(e.getResponseBodyAsString());
        if (StringUtils.hasText((String)message)) {
            return new VaultException(String.format("Status %s %s: %s", e.getRawStatusCode(), e.getStatusText(), message), e);
        }
        return new VaultException(String.format("Status %s %s", e.getRawStatusCode(), e.getStatusText()), e);
    }

    public static VaultException buildException(HttpStatusCodeException e, String path) {
        Assert.notNull((Object)((Object)e), (String)"HttpStatusCodeException must not be null");
        String message = VaultResponses.getError(e.getResponseBodyAsString());
        if (StringUtils.hasText((String)message)) {
            return new VaultException(String.format("Status %s %s [%s]: %s", e.getRawStatusCode(), e.getStatusText(), path, message), e);
        }
        return new VaultException(String.format("Status %s %s [%s]", e.getRawStatusCode(), e.getStatusText(), path), e);
    }

    public static VaultException buildException(HttpStatus statusCode, String path, String message) {
        if (StringUtils.hasText((String)message)) {
            return new VaultException(String.format("Status %s [%s]: %s", statusCode, path, message));
        }
        return new VaultException(String.format("Status %s [%s]", statusCode, path));
    }

    public static <T> ParameterizedTypeReference<VaultResponseSupport<T>> getTypeReference(final Class<T> responseType) {
        Assert.notNull(responseType, (String)"Response type must not be null");
        final ParameterizedType supportType = new ParameterizedType(){

            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{responseType};
            }

            @Override
            public Type getRawType() {
                return VaultResponseSupport.class;
            }

            @Override
            public Type getOwnerType() {
                return VaultResponseSupport.class;
            }
        };
        return new ParameterizedTypeReference<VaultResponseSupport<T>>(){

            public Type getType() {
                return supportType;
            }
        };
    }

    public static String getError(String json) {
        Assert.notNull((Object)json, (String)"Error JSON must not be null");
        if (json.contains("\"errors\":")) {
            try {
                Map map = (Map)OBJECT_MAPPER.readValue(json.getBytes(), Map.class);
                if (map.containsKey("errors")) {
                    Collection errors = (Collection)map.get("errors");
                    if (errors.size() == 1) {
                        return (String)errors.iterator().next();
                    }
                    return errors.toString();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return json;
    }

    public static <T> T unwrap(final String wrappedResponse, Class<T> responseType) {
        Assert.hasText((String)wrappedResponse, (String)"Wrapped response must not be empty");
        try {
            return (T)converter.read(responseType, new HttpInputMessage(){

                public InputStream getBody() throws IOException {
                    return new ByteArrayInputStream(wrappedResponse.getBytes());
                }

                public HttpHeaders getHeaders() {
                    return new HttpHeaders();
                }
            });
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}


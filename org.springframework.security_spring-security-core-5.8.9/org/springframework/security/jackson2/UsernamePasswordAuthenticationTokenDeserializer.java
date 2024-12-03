/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParseException
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.ObjectCodec
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  com.fasterxml.jackson.databind.JsonMappingException
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.MissingNode
 */
package org.springframework.security.jackson2;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

class UsernamePasswordAuthenticationTokenDeserializer
extends JsonDeserializer<UsernamePasswordAuthenticationToken> {
    private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<List<GrantedAuthority>>(){};
    private static final TypeReference<Object> OBJECT = new TypeReference<Object>(){};

    UsernamePasswordAuthenticationTokenDeserializer() {
    }

    public UsernamePasswordAuthenticationToken deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
        JsonNode jsonNode = (JsonNode)mapper.readTree(jp);
        Boolean authenticated = this.readJsonNode(jsonNode, "authenticated").asBoolean();
        JsonNode principalNode = this.readJsonNode(jsonNode, "principal");
        Object principal = this.getPrincipal(mapper, principalNode);
        JsonNode credentialsNode = this.readJsonNode(jsonNode, "credentials");
        Object credentials = this.getCredentials(credentialsNode);
        List authorities = (List)mapper.readValue(this.readJsonNode(jsonNode, "authorities").traverse((ObjectCodec)mapper), GRANTED_AUTHORITY_LIST);
        UsernamePasswordAuthenticationToken token = authenticated == false ? UsernamePasswordAuthenticationToken.unauthenticated(principal, credentials) : UsernamePasswordAuthenticationToken.authenticated(principal, credentials, authorities);
        JsonNode detailsNode = this.readJsonNode(jsonNode, "details");
        if (detailsNode.isNull() || detailsNode.isMissingNode()) {
            token.setDetails(null);
        } else {
            Object details = mapper.readValue(detailsNode.toString(), OBJECT);
            token.setDetails(details);
        }
        return token;
    }

    private Object getCredentials(JsonNode credentialsNode) {
        if (credentialsNode.isNull() || credentialsNode.isMissingNode()) {
            return null;
        }
        return credentialsNode.asText();
    }

    private Object getPrincipal(ObjectMapper mapper, JsonNode principalNode) throws IOException, JsonParseException, JsonMappingException {
        if (principalNode.isObject()) {
            return mapper.readValue(principalNode.traverse((ObjectCodec)mapper), Object.class);
        }
        return principalNode.asText();
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}


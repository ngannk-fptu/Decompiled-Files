/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.MissingNode
 */
package org.springframework.security.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import java.io.IOException;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

class UserDeserializer
extends JsonDeserializer<User> {
    private static final TypeReference<Set<SimpleGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET = new TypeReference<Set<SimpleGrantedAuthority>>(){};

    UserDeserializer() {
    }

    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
        JsonNode jsonNode = (JsonNode)mapper.readTree(jp);
        Set authorities = (Set)mapper.convertValue((Object)jsonNode.get("authorities"), SIMPLE_GRANTED_AUTHORITY_SET);
        JsonNode passwordNode = this.readJsonNode(jsonNode, "password");
        String username = this.readJsonNode(jsonNode, "username").asText();
        String password = passwordNode.asText("");
        boolean enabled = this.readJsonNode(jsonNode, "enabled").asBoolean();
        boolean accountNonExpired = this.readJsonNode(jsonNode, "accountNonExpired").asBoolean();
        boolean credentialsNonExpired = this.readJsonNode(jsonNode, "credentialsNonExpired").asBoolean();
        boolean accountNonLocked = this.readJsonNode(jsonNode, "accountNonLocked").asBoolean();
        User result = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        if (passwordNode.asText(null) == null) {
            result.eraseCredentials();
        }
        return result;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}


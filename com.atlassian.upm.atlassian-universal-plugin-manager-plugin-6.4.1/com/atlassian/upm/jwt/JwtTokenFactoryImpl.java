/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.map.MappingJsonFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.jwt;

import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder;
import com.atlassian.jwt.core.writer.NimbusJwtWriterFactory;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import com.atlassian.jwt.writer.JwtWriterFactory;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.jwt.JwtTokenFactory;
import com.atlassian.upm.jwt.UpmJwtToken;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;

public class JwtTokenFactoryImpl
implements JwtTokenFactory {
    private JwtWriterFactory jwtWriterFactory;
    private final ObjectMapper mapper;

    JwtTokenFactoryImpl(JwtWriterFactory jwtWriterFactory, ObjectMapper mapper) {
        this.jwtWriterFactory = Objects.requireNonNull(jwtWriterFactory);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public JwtTokenFactoryImpl() {
        this(new NimbusJwtWriterFactory(), new ObjectMapper((JsonFactory)new MappingJsonFactory()));
    }

    @Override
    public UpmJwtToken generateToken(String sharedSecret, Map<String, String> claims, Option<? extends Object> postBody) {
        DateTime now = new DateTime();
        long issuedAt = now.getMillis();
        long expiresAt = now.plusMinutes(3).getMillis();
        String key = "atlassian-universal-plugin-manager-plugin";
        JwtJsonBuilder jwtBuilder = new JsonSmartJwtJsonBuilder().issuedAt(issuedAt).expirationTime(expiresAt).issuer(key);
        Map<String, String> allClaims = this.conditionallyAddPostBodyHashClaim(claims, postBody);
        for (String claimKey : allClaims.keySet()) {
            jwtBuilder.claim(claimKey, allClaims.get(claimKey));
        }
        return new UpmJwtToken(this.jwtWriterFactory.macSigningWriter(SigningAlgorithm.HS256, sharedSecret).jsonToJwt(jwtBuilder.build()));
    }

    private Map<String, String> conditionallyAddPostBodyHashClaim(Map<String, String> claims, Option<? extends Object> postBody) {
        Iterator<? extends Object> iterator = postBody.iterator();
        if (iterator.hasNext()) {
            Object pb = iterator.next();
            HashMap<String, String> allClaims = new HashMap<String, String>(claims);
            try {
                allClaims.put("rbSha", DigestUtils.shaHex((String)this.mapper.writeValueAsString(pb)));
            }
            catch (IOException e) {
                throw new RuntimeException("Could not serialize post body", e);
            }
            return Collections.unmodifiableMap(allClaims);
        }
        return claims;
    }
}


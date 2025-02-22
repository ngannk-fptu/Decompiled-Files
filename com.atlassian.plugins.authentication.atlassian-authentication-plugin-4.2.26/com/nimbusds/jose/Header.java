/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.PlainHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public abstract class Header
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Algorithm alg;
    private final JOSEObjectType typ;
    private final String cty;
    private final Set<String> crit;
    private final Map<String, Object> customParams;
    private static final Map<String, Object> EMPTY_CUSTOM_PARAMS = Collections.unmodifiableMap(new HashMap());
    private final Base64URL parsedBase64URL;

    protected Header(Algorithm alg, JOSEObjectType typ, String cty, Set<String> crit, Map<String, Object> customParams, Base64URL parsedBase64URL) {
        if (alg == null) {
            throw new IllegalArgumentException("The algorithm \"alg\" header parameter must not be null");
        }
        this.alg = alg;
        this.typ = typ;
        this.cty = cty;
        this.crit = crit != null ? Collections.unmodifiableSet(new HashSet<String>(crit)) : null;
        this.customParams = customParams != null ? Collections.unmodifiableMap(new HashMap<String, Object>(customParams)) : EMPTY_CUSTOM_PARAMS;
        this.parsedBase64URL = parsedBase64URL;
    }

    protected Header(Header header) {
        this(header.getAlgorithm(), header.getType(), header.getContentType(), header.getCriticalParams(), header.getCustomParams(), header.getParsedBase64URL());
    }

    public Algorithm getAlgorithm() {
        return this.alg;
    }

    public JOSEObjectType getType() {
        return this.typ;
    }

    public String getContentType() {
        return this.cty;
    }

    public Set<String> getCriticalParams() {
        return this.crit;
    }

    public Object getCustomParam(String name) {
        return this.customParams.get(name);
    }

    public Map<String, Object> getCustomParams() {
        return this.customParams;
    }

    public Base64URL getParsedBase64URL() {
        return this.parsedBase64URL;
    }

    public Set<String> getIncludedParams() {
        HashSet<String> includedParameters = new HashSet<String>(this.getCustomParams().keySet());
        includedParameters.add("alg");
        if (this.getType() != null) {
            includedParameters.add("typ");
        }
        if (this.getContentType() != null) {
            includedParameters.add("cty");
        }
        if (this.getCriticalParams() != null && !this.getCriticalParams().isEmpty()) {
            includedParameters.add("crit");
        }
        return includedParameters;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject(this.customParams);
        o.put("alg", this.alg.toString());
        if (this.typ != null) {
            o.put("typ", this.typ.toString());
        }
        if (this.cty != null) {
            o.put("cty", this.cty);
        }
        if (this.crit != null && !this.crit.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            for (String c : this.crit) {
                jsonArray.add(c);
            }
            o.put("crit", jsonArray);
        }
        return o;
    }

    public String toString() {
        return this.toJSONObject().toString();
    }

    public Base64URL toBase64URL() {
        if (this.parsedBase64URL == null) {
            return Base64URL.encode(this.toString());
        }
        return this.parsedBase64URL;
    }

    public static Algorithm parseAlgorithm(JSONObject json) throws ParseException {
        String algName = JSONObjectUtils.getString(json, "alg");
        if (algName == null) {
            throw new ParseException("Missing \"alg\" in header JSON object", 0);
        }
        if (algName.equals(Algorithm.NONE.getName())) {
            return Algorithm.NONE;
        }
        if (json.containsKey("enc")) {
            return JWEAlgorithm.parse(algName);
        }
        return JWSAlgorithm.parse(algName);
    }

    public static Header parse(JSONObject jsonObject) throws ParseException {
        return Header.parse(jsonObject, null);
    }

    public static Header parse(JSONObject jsonObject, Base64URL parsedBase64URL) throws ParseException {
        Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (alg.equals(Algorithm.NONE)) {
            return PlainHeader.parse(jsonObject, parsedBase64URL);
        }
        if (alg instanceof JWSAlgorithm) {
            return JWSHeader.parse(jsonObject, parsedBase64URL);
        }
        if (alg instanceof JWEAlgorithm) {
            return JWEHeader.parse(jsonObject, parsedBase64URL);
        }
        throw new AssertionError((Object)("Unexpected algorithm type: " + alg));
    }

    public static Header parse(String jsonString) throws ParseException {
        return Header.parse(jsonString, null);
    }

    public static Header parse(String jsonString, Base64URL parsedBase64URL) throws ParseException {
        JSONObject jsonObject = JSONObjectUtils.parse(jsonString);
        return Header.parse(jsonObject, parsedBase64URL);
    }

    public static Header parse(Base64URL base64URL) throws ParseException {
        return Header.parse(base64URL.decodeToString(), base64URL);
    }
}


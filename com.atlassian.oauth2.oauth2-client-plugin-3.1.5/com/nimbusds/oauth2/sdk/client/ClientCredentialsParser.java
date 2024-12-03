/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import java.util.Date;
import net.minidev.json.JSONObject;

public class ClientCredentialsParser {
    public static ClientID parseID(JSONObject jsonObject) throws ParseException {
        return new ClientID(JSONObjectUtils.getString(jsonObject, "client_id"));
    }

    public static Date parseIDIssueDate(JSONObject jsonObject) throws ParseException {
        if (jsonObject.containsKey("client_id_issued_at")) {
            return new Date(JSONObjectUtils.getLong(jsonObject, "client_id_issued_at") * 1000L);
        }
        return null;
    }

    public static Secret parseSecret(JSONObject jsonObject) throws ParseException {
        if (jsonObject.containsKey("client_secret")) {
            long t;
            String value = JSONObjectUtils.getString(jsonObject, "client_secret");
            Date exp = null;
            if (jsonObject.containsKey("client_secret_expires_at") && (t = JSONObjectUtils.getLong(jsonObject, "client_secret_expires_at")) > 0L) {
                exp = new Date(t * 1000L);
            }
            return new Secret(value, exp);
        }
        return null;
    }

    public static URI parseRegistrationURI(JSONObject jsonObject) throws ParseException {
        return JSONObjectUtils.getURI(jsonObject, "registration_client_uri", null);
    }

    public static BearerAccessToken parseRegistrationAccessToken(JSONObject jsonObject) throws ParseException {
        if (jsonObject.containsKey("registration_access_token")) {
            return new BearerAccessToken(JSONObjectUtils.getString(jsonObject, "registration_access_token"));
        }
        return null;
    }
}


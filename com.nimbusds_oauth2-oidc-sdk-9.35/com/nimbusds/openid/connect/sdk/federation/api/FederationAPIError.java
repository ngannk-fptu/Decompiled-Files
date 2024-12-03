/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.federation.api.OperationType;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class FederationAPIError
extends ErrorObject {
    private static final long serialVersionUID = 2116693118039606386L;
    private final OperationType operationType;

    public FederationAPIError(OperationType operationType, String code, String description) {
        this(operationType, code, description, 0);
    }

    public FederationAPIError(OperationType operationType, String code, String description, int httpStatusCode) {
        super(code, description, httpStatusCode);
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return this.operationType;
    }

    public FederationAPIError withStatusCode(int httpStatusCode) {
        return new FederationAPIError(this.getOperationType(), this.getCode(), this.getDescription(), httpStatusCode);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();
        if (this.getOperationType() != null) {
            jsonObject.put((Object)"operation", (Object)this.getOperationType().getValue());
        }
        return jsonObject;
    }

    public static FederationAPIError parse(JSONObject jsonObject) {
        ErrorObject errorObject = ErrorObject.parse(jsonObject);
        OperationType operationType = null;
        try {
            operationType = new OperationType(JSONObjectUtils.getString(jsonObject, "operation"));
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        return new FederationAPIError(operationType, errorObject.getCode(), errorObject.getDescription());
    }

    public static FederationAPIError parse(HTTPResponse httpResponse) {
        JSONObject jsonObject;
        try {
            jsonObject = httpResponse.getContentAsJSONObject();
        }
        catch (ParseException e) {
            jsonObject = new JSONObject();
        }
        return FederationAPIError.parse(jsonObject).withStatusCode(httpResponse.getStatusCode());
    }
}


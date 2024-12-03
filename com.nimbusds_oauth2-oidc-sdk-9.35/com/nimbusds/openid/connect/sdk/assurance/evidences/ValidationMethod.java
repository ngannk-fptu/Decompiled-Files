/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.Policy;
import com.nimbusds.openid.connect.sdk.assurance.Procedure;
import com.nimbusds.openid.connect.sdk.assurance.Status;
import com.nimbusds.openid.connect.sdk.assurance.evidences.CommonMethodAttributes;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ValidationMethodType;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class ValidationMethod
extends CommonMethodAttributes {
    private final ValidationMethodType type;

    public ValidationMethod(ValidationMethodType type, Policy policy, Procedure procedure, Status status) {
        super(policy, procedure, status);
        Objects.requireNonNull(type);
        this.type = type;
    }

    public ValidationMethodType getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValidationMethod)) {
            return false;
        }
        ValidationMethod that = (ValidationMethod)o;
        return this.getType().equals(that.getType()) && Objects.equals(this.getPolicy(), that.getPolicy()) && Objects.equals(this.getProcedure(), that.getProcedure()) && Objects.equals(this.getStatus(), that.getStatus());
    }

    public int hashCode() {
        return Objects.hash(this.getType(), this.getPolicy(), this.getProcedure(), this.getStatus());
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        o.put((Object)"type", (Object)this.getType().getValue());
        return o;
    }

    public static ValidationMethod parse(JSONObject jsonObject) throws ParseException {
        try {
            ValidationMethodType type = new ValidationMethodType(JSONObjectUtils.getString(jsonObject, "type"));
            Policy policy = null;
            if (jsonObject.get((Object)"policy") != null) {
                policy = new Policy(JSONObjectUtils.getString(jsonObject, "policy"));
            }
            Procedure procedure = null;
            if (jsonObject.get((Object)"procedure") != null) {
                procedure = new Procedure(JSONObjectUtils.getString(jsonObject, "procedure"));
            }
            Status status = null;
            if (jsonObject.get((Object)"status") != null) {
                status = new Status(JSONObjectUtils.getString(jsonObject, "status"));
            }
            return new ValidationMethod(type, policy, procedure, status);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}


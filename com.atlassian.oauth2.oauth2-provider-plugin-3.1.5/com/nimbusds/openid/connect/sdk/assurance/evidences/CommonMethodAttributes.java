/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.openid.connect.sdk.assurance.Policy;
import com.nimbusds.openid.connect.sdk.assurance.Procedure;
import com.nimbusds.openid.connect.sdk.assurance.Status;
import net.minidev.json.JSONObject;

class CommonMethodAttributes {
    private final Policy policy;
    private final Procedure procedure;
    private final Status status;

    CommonMethodAttributes(Policy policy, Procedure procedure, Status status) {
        this.policy = policy;
        this.procedure = procedure;
        this.status = status;
    }

    public Policy getPolicy() {
        return this.policy;
    }

    public Procedure getProcedure() {
        return this.procedure;
    }

    public Status getStatus() {
        return this.status;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        if (this.getPolicy() != null) {
            o.put("policy", this.getPolicy().getValue());
        }
        if (this.getProcedure() != null) {
            o.put("procedure", this.getProcedure().getValue());
        }
        if (this.getStatus() != null) {
            o.put("status", this.getStatus().getValue());
        }
        return o;
    }
}


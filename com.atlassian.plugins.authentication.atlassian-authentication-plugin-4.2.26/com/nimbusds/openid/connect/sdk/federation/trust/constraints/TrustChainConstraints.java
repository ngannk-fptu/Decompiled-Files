/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust.constraints;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.trust.constraints.EntityIDConstraint;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Immutable
public final class TrustChainConstraints
implements JSONAware {
    public static final TrustChainConstraints NO_CONSTRAINTS = new TrustChainConstraints();
    private final int maxPathLength;
    private final List<EntityIDConstraint> permittedEntities;
    private final List<EntityIDConstraint> excludedEntities;

    public TrustChainConstraints() {
        this(-1, null, null);
    }

    public TrustChainConstraints(int maxPathLength) {
        this(maxPathLength, null, null);
    }

    public TrustChainConstraints(int maxPathLength, List<EntityIDConstraint> permittedEntities, List<EntityIDConstraint> excludedEntities) {
        this.maxPathLength = maxPathLength;
        this.permittedEntities = permittedEntities != null ? permittedEntities : Collections.emptyList();
        this.excludedEntities = excludedEntities != null ? excludedEntities : Collections.emptyList();
    }

    public boolean isPermitted(int numIntermediatesInPath) {
        if (numIntermediatesInPath < 0) {
            throw new IllegalArgumentException("The path length must not be negative");
        }
        return this.getMaxPathLength() <= -1 || numIntermediatesInPath <= this.getMaxPathLength();
    }

    public boolean isPermitted(EntityID entityID) {
        if (this.getExcludedEntities().isEmpty() && this.getPermittedEntities().isEmpty()) {
            return true;
        }
        if (!this.getExcludedEntities().isEmpty()) {
            for (EntityIDConstraint constraint : this.getExcludedEntities()) {
                if (!constraint.matches(entityID)) continue;
                return false;
            }
        }
        if (!this.getPermittedEntities().isEmpty()) {
            for (EntityIDConstraint constraint : this.getPermittedEntities()) {
                if (!constraint.matches(entityID)) continue;
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean isPermitted(int numIntermediatesInPath, EntityID entityID) {
        return this.isPermitted(numIntermediatesInPath) && this.isPermitted(entityID);
    }

    public int getMaxPathLength() {
        return this.maxPathLength;
    }

    public List<EntityIDConstraint> getPermittedEntities() {
        return this.permittedEntities;
    }

    public List<EntityIDConstraint> getExcludedEntities() {
        return this.excludedEntities;
    }

    public JSONObject toJSONObject() {
        LinkedList<String> vals;
        JSONObject o = new JSONObject();
        if (this.maxPathLength > -1) {
            o.put("max_path_length", this.maxPathLength);
        }
        JSONObject namingConstraints = new JSONObject();
        if (CollectionUtils.isNotEmpty(this.permittedEntities)) {
            vals = new LinkedList<String>();
            for (EntityIDConstraint v : this.permittedEntities) {
                vals.add(v.toString());
            }
            namingConstraints.put("permitted", vals);
        }
        if (CollectionUtils.isNotEmpty(this.excludedEntities)) {
            vals = new LinkedList();
            for (EntityIDConstraint v : this.excludedEntities) {
                vals.add(v.toString());
            }
            namingConstraints.put("excluded", vals);
        }
        if (!namingConstraints.isEmpty()) {
            o.put("naming_constraints", namingConstraints);
        }
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrustChainConstraints)) {
            return false;
        }
        TrustChainConstraints that = (TrustChainConstraints)o;
        return this.getMaxPathLength() == that.getMaxPathLength() && Objects.equals(this.getPermittedEntities(), that.getPermittedEntities()) && Objects.equals(this.getExcludedEntities(), that.getExcludedEntities());
    }

    public int hashCode() {
        return Objects.hash(this.getMaxPathLength(), this.getPermittedEntities(), this.getExcludedEntities());
    }

    public static TrustChainConstraints parse(JSONObject jsonObject) throws ParseException {
        int maxPathLength = JSONObjectUtils.getInt(jsonObject, "max_path_length", -1);
        JSONObject namingConstraints = JSONObjectUtils.getJSONObject(jsonObject, "naming_constraints", new JSONObject());
        LinkedList<EntityIDConstraint> permitted = null;
        List<String> values = JSONObjectUtils.getStringList(namingConstraints, "permitted", null);
        if (values != null) {
            permitted = new LinkedList<EntityIDConstraint>();
            for (String v : values) {
                if (v == null) continue;
                permitted.add(EntityIDConstraint.parse(v));
            }
        }
        LinkedList<EntityIDConstraint> excluded = null;
        values = JSONObjectUtils.getStringList(namingConstraints, "excluded", null);
        if (values != null) {
            excluded = new LinkedList<EntityIDConstraint>();
            for (String v : values) {
                if (v == null) continue;
                excluded.add(EntityIDConstraint.parse(v));
            }
        }
        return new TrustChainConstraints(maxPathLength, permitted, excluded);
    }
}


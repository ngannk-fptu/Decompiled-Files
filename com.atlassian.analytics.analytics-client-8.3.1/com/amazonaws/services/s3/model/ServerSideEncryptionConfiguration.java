/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ServerSideEncryptionRule;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerSideEncryptionConfiguration
implements Serializable,
Cloneable {
    private List<ServerSideEncryptionRule> rules;

    public List<ServerSideEncryptionRule> getRules() {
        return this.rules;
    }

    public void setRules(Collection<ServerSideEncryptionRule> rules) {
        if (rules == null) {
            this.rules = null;
            return;
        }
        this.rules = new ArrayList<ServerSideEncryptionRule>(rules);
    }

    public ServerSideEncryptionConfiguration withRules(ServerSideEncryptionRule ... rules) {
        if (this.rules == null) {
            this.setRules(new ArrayList<ServerSideEncryptionRule>(rules.length));
        }
        for (ServerSideEncryptionRule ele : rules) {
            this.rules.add(ele);
        }
        return this;
    }

    public ServerSideEncryptionConfiguration withRules(Collection<ServerSideEncryptionRule> rules) {
        this.setRules(rules);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getRules() != null) {
            sb.append("Rules: ").append(this.getRules()).append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ServerSideEncryptionConfiguration)) {
            return false;
        }
        ServerSideEncryptionConfiguration other = (ServerSideEncryptionConfiguration)obj;
        if (other.getRules() == null ^ this.getRules() == null) {
            return false;
        }
        return other.getRules() == null || other.getRules().equals(this.getRules());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getRules() == null ? 0 : this.getRules().hashCode());
        return hashCode;
    }

    public ServerSideEncryptionConfiguration clone() {
        try {
            return (ServerSideEncryptionConfiguration)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}


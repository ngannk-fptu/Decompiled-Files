/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.policy;

import com.amazonaws.auth.policy.PolicyReaderOptions;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.internal.JsonPolicyReader;
import com.amazonaws.auth.policy.internal.JsonPolicyWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Policy {
    private static final String DEFAULT_POLICY_VERSION = "2012-10-17";
    private String id;
    private String version = "2012-10-17";
    private List<Statement> statements = new ArrayList<Statement>();

    public Policy() {
    }

    public Policy(String id) {
        this.id = id;
    }

    public Policy(String id, Collection<Statement> statements) {
        this(id);
        this.setStatements(statements);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Policy withId(String id) {
        this.setId(id);
        return this;
    }

    public String getVersion() {
        return this.version;
    }

    public Collection<Statement> getStatements() {
        return this.statements;
    }

    public void setStatements(Collection<Statement> statements) {
        this.statements = new ArrayList<Statement>(statements);
        this.assignUniqueStatementIds();
    }

    public Policy withStatements(Statement ... statements) {
        this.setStatements(Arrays.asList(statements));
        return this;
    }

    public String toJson() {
        return new JsonPolicyWriter().writePolicyToString(this);
    }

    public static Policy fromJson(String jsonString) {
        return Policy.fromJson(jsonString, new PolicyReaderOptions());
    }

    public static Policy fromJson(String jsonString, PolicyReaderOptions options) {
        return new JsonPolicyReader(options).createPolicyFromJsonString(jsonString);
    }

    private void assignUniqueStatementIds() {
        HashSet<String> usedStatementIds = new HashSet<String>();
        for (Statement statement : this.statements) {
            if (statement.getId() == null) continue;
            usedStatementIds.add(statement.getId());
        }
        int counter = 0;
        for (Statement statement : this.statements) {
            if (statement.getId() != null) continue;
            while (usedStatementIds.contains(Integer.toString(++counter))) {
            }
            statement.setId(Integer.toString(counter));
        }
    }
}


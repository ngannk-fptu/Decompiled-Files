/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONAware
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Requirement;
import java.io.Serializable;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Immutable
public class Algorithm
implements JSONAware,
Serializable {
    private static final long serialVersionUID = 1L;
    public static final Algorithm NONE = new Algorithm("none", Requirement.REQUIRED);
    private final String name;
    private final Requirement requirement;

    public Algorithm(String name, Requirement req) {
        if (name == null) {
            throw new IllegalArgumentException("The algorithm name must not be null");
        }
        this.name = name;
        this.requirement = req;
    }

    public Algorithm(String name) {
        this(name, null);
    }

    public final String getName() {
        return this.name;
    }

    public final Requirement getRequirement() {
        return this.requirement;
    }

    public final int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object object) {
        return object != null && object instanceof Algorithm && this.toString().equals(object.toString());
    }

    public final String toString() {
        return this.name;
    }

    public final String toJSONString() {
        return "\"" + JSONObject.escape((String)this.name) + '\"';
    }

    public static Algorithm parse(String s) {
        if (s == null) {
            return null;
        }
        return new Algorithm(s);
    }
}


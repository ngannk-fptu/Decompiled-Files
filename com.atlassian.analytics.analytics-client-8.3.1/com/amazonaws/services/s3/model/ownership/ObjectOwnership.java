/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.ownership;

public enum ObjectOwnership {
    BucketOwnerPreferred("BucketOwnerPreferred"),
    ObjectWriter("ObjectWriter"),
    BucketOwnerEnforced("BucketOwnerEnforced");

    private final String objectOwnershipId;

    public static ObjectOwnership fromValue(String s3OwnershipString) throws IllegalArgumentException {
        for (ObjectOwnership objectOwnership : ObjectOwnership.values()) {
            if (!objectOwnership.toString().equals(s3OwnershipString)) continue;
            return objectOwnership;
        }
        throw new IllegalArgumentException("Cannot create enum from " + s3OwnershipString + " value!");
    }

    private ObjectOwnership(String id) {
        this.objectOwnershipId = id;
    }

    public String toString() {
        return this.objectOwnershipId;
    }
}


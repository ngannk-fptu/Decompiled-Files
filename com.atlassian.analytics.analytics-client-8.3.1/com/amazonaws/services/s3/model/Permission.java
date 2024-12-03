/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum Permission {
    FullControl("FULL_CONTROL", "x-amz-grant-full-control"),
    Read("READ", "x-amz-grant-read"),
    Write("WRITE", "x-amz-grant-write"),
    ReadAcp("READ_ACP", "x-amz-grant-read-acp"),
    WriteAcp("WRITE_ACP", "x-amz-grant-write-acp");

    private String permissionString;
    private String headerName;

    private Permission(String permissionString, String headerName) {
        this.permissionString = permissionString;
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    public String toString() {
        return this.permissionString;
    }

    public static Permission parsePermission(String str) {
        for (Permission permission : Permission.values()) {
            if (!permission.permissionString.equals(str)) continue;
            return permission;
        }
        return null;
    }
}


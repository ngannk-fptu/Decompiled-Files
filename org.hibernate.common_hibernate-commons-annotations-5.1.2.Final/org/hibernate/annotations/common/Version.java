/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common;

public class Version {
    public static String getVersionString() {
        return "5.1.2.Final";
    }

    public static void touch() {
    }

    public static void main(String[] args) {
        System.out.println("Hibernate Commons Annotations {" + Version.getVersionString() + "}");
    }
}


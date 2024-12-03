/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.spring.scanner;

public enum ProductFilter {
    ALL,
    JIRA,
    CONFLUENCE,
    BAMBOO,
    BITBUCKET,
    STASH,
    CROWD,
    FECRU,
    REFAPP;


    public static boolean hasProduct(String productName) {
        try {
            ProductFilter filter = ProductFilter.valueOf(productName);
            return null != filter;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getPerProductFile(String fileStem) {
        return fileStem + "-" + this.name().toLowerCase();
    }
}


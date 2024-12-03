/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.logging.log4j;

import org.apache.commons.lang3.StringUtils;

public class FqNameCollapser {
    public static final int NO_COLLAPSING_PRECISION = -1;
    public static FqNameCollapser NO_COLLAPSING = new FqNameCollapser(-1, Strategy.PACKAGE_LENGTH);
    private final int fqNameCollapsePrecision;
    private final Strategy strategy;

    public FqNameCollapser(int fqNameCollapsePrecision, Strategy strategy) {
        this.fqNameCollapsePrecision = fqNameCollapsePrecision;
        this.strategy = strategy;
    }

    public String collapse(String fqName) {
        if (this.fqNameCollapsePrecision < 0) {
            return fqName;
        }
        if (StringUtils.isBlank((CharSequence)fqName)) {
            return fqName;
        }
        String[] packages = StringUtils.splitPreserveAllTokens((String)fqName, (String)".");
        String rightMostClassName = packages[packages.length - 1];
        if (packages.length < 2) {
            return fqName;
        }
        if (this.fqNameCollapsePrecision == 0) {
            return rightMostClassName;
        }
        if (this.strategy == Strategy.PACKAGE_SEGMENTS) {
            return this.segmentStrategy(fqName.length(), packages, rightMostClassName);
        }
        return this.lengthStrategy(fqName.length(), packages, rightMostClassName);
    }

    private String segmentStrategy(int length, String[] packages, String rightMostClassName) {
        StringBuilder sb = new StringBuilder(length);
        sb.append(".").append(rightMostClassName);
        int segmentCount = 1;
        for (int i = packages.length - 2; i >= 0; --i) {
            String packageName = StringUtils.defaultString((String)packages[i]);
            if (packageName.length() > 0) {
                if (segmentCount <= this.fqNameCollapsePrecision) {
                    sb.insert(0, packageName);
                } else {
                    sb.insert(0, this.firstCharOf(packageName));
                }
                ++segmentCount;
            }
            if (i <= 0) continue;
            sb.insert(0, ".");
        }
        return sb.toString();
    }

    private String lengthStrategy(int length, String[] packages, String rightMostClassName) {
        StringBuilder sb = new StringBuilder(length);
        int spaceLeft = this.fqNameCollapsePrecision;
        sb.append(".").append(rightMostClassName);
        for (int i = packages.length - 2; i >= 0; --i) {
            String packageName = StringUtils.defaultString((String)packages[i]);
            if (packageName.length() > 0) {
                if (spaceLeft <= this.fqNameCollapsePrecision) {
                    if (packageName.length() <= spaceLeft) {
                        sb.insert(0, packageName);
                    } else {
                        sb.insert(0, this.firstCharOf(packageName));
                    }
                    spaceLeft -= packageName.length();
                } else {
                    sb.insert(0, this.firstCharOf(packageName));
                }
            }
            if (i <= 0) continue;
            sb.insert(0, ".");
        }
        return sb.toString();
    }

    private String firstCharOf(String packageName) {
        return packageName.length() > 1 ? packageName.substring(0, 1) : packageName;
    }

    public static enum Strategy {
        PACKAGE_LENGTH,
        PACKAGE_SEGMENTS;

    }
}


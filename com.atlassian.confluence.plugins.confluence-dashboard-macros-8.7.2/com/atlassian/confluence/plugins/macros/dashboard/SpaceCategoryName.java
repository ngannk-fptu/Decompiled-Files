/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 */
package com.atlassian.confluence.plugins.macros.dashboard;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class SpaceCategoryName
implements Comparable<SpaceCategoryName> {
    private final String spaceCategoryName;

    public SpaceCategoryName(String spaceCategoryName) {
        this.spaceCategoryName = spaceCategoryName;
    }

    public String getName() {
        return this.spaceCategoryName;
    }

    public String getCapitalised() {
        return StringUtils.capitalize((String)this.spaceCategoryName);
    }

    public String toString() {
        return this.spaceCategoryName;
    }

    public int hashCode() {
        return this.spaceCategoryName.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof SpaceCategoryName)) {
            return false;
        }
        SpaceCategoryName that = (SpaceCategoryName)o;
        return this.spaceCategoryName.equals(that.spaceCategoryName);
    }

    @Override
    public int compareTo(SpaceCategoryName o) {
        if (this.spaceCategoryName != null) {
            return this.spaceCategoryName.compareTo(o.spaceCategoryName);
        }
        if (o.spaceCategoryName != null) {
            return 1;
        }
        return 0;
    }
}


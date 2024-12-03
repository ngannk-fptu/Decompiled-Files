/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.extras.common;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.extras.api.LicenseType;
import com.atlassian.extras.common.LicenseException;

public class LicenseTypeAndEditionResolver {
    public static LicenseEdition getLicenseEdition(String editionName) {
        try {
            return LicenseEdition.valueOf((String)editionName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new LicenseException("Failed to lookup license edition <" + editionName + ">");
        }
        catch (NullPointerException e) {
            throw new LicenseException("Failed to lookup license edition <" + editionName + ">");
        }
    }

    public static LicenseType getLicenseType(String typeName) {
        try {
            return LicenseType.valueOf((String)typeName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new LicenseException("Failed to lookup license type <" + typeName + ">");
        }
        catch (NullPointerException e) {
            throw new LicenseException("Failed to lookup license type <" + typeName + ">");
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.config;

import java.security.BasicPermission;
import java.security.Permission;
import java.util.StringTokenizer;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ProviderConfigurationPermission
extends BasicPermission {
    private static final int THREAD_LOCAL_EC_IMPLICITLY_CA = 1;
    private static final int EC_IMPLICITLY_CA = 2;
    private static final int THREAD_LOCAL_DH_DEFAULT_PARAMS = 4;
    private static final int DH_DEFAULT_PARAMS = 8;
    private static final int ACCEPTABLE_EC_CURVES = 16;
    private static final int ADDITIONAL_EC_PARAMETERS = 32;
    private static final int ALL = 63;
    private static final String THREAD_LOCAL_EC_IMPLICITLY_CA_STR = "threadlocalecimplicitlyca";
    private static final String EC_IMPLICITLY_CA_STR = "ecimplicitlyca";
    private static final String THREAD_LOCAL_DH_DEFAULT_PARAMS_STR = "threadlocaldhdefaultparams";
    private static final String DH_DEFAULT_PARAMS_STR = "dhdefaultparams";
    private static final String ACCEPTABLE_EC_CURVES_STR = "acceptableeccurves";
    private static final String ADDITIONAL_EC_PARAMETERS_STR = "additionalecparameters";
    private static final String ALL_STR = "all";
    private final String actions;
    private final int permissionMask;

    public ProviderConfigurationPermission(String name) {
        super(name);
        this.actions = ALL_STR;
        this.permissionMask = 63;
    }

    public ProviderConfigurationPermission(String name, String actions) {
        super(name, actions);
        this.actions = actions;
        this.permissionMask = this.calculateMask(actions);
    }

    private int calculateMask(String actions) {
        StringTokenizer tok = new StringTokenizer(Strings.toLowerCase(actions), " ,");
        int mask = 0;
        while (tok.hasMoreTokens()) {
            String s = tok.nextToken();
            if (s.equals(THREAD_LOCAL_EC_IMPLICITLY_CA_STR)) {
                mask |= 1;
                continue;
            }
            if (s.equals(EC_IMPLICITLY_CA_STR)) {
                mask |= 2;
                continue;
            }
            if (s.equals(THREAD_LOCAL_DH_DEFAULT_PARAMS_STR)) {
                mask |= 4;
                continue;
            }
            if (s.equals(DH_DEFAULT_PARAMS_STR)) {
                mask |= 8;
                continue;
            }
            if (s.equals(ACCEPTABLE_EC_CURVES_STR)) {
                mask |= 0x10;
                continue;
            }
            if (s.equals(ADDITIONAL_EC_PARAMETERS_STR)) {
                mask |= 0x20;
                continue;
            }
            if (!s.equals(ALL_STR)) continue;
            mask |= 0x3F;
        }
        if (mask == 0) {
            throw new IllegalArgumentException("unknown permissions passed to mask");
        }
        return mask;
    }

    @Override
    public String getActions() {
        return this.actions;
    }

    @Override
    public boolean implies(Permission permission) {
        if (!(permission instanceof ProviderConfigurationPermission)) {
            return false;
        }
        if (!this.getName().equals(permission.getName())) {
            return false;
        }
        ProviderConfigurationPermission other = (ProviderConfigurationPermission)permission;
        return (this.permissionMask & other.permissionMask) == other.permissionMask;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ProviderConfigurationPermission) {
            ProviderConfigurationPermission other = (ProviderConfigurationPermission)obj;
            return this.permissionMask == other.permissionMask && this.getName().equals(other.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode() + this.permissionMask;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.common.LicenseException
 */
package com.atlassian.extras.legacy.util;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.common.LicenseException;
import com.atlassian.license.LicenseManager;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import com.atlassian.license.MemoryLicenseRegistry;
import com.atlassian.license.applications.bamboo.BambooLicenseTypeStore;
import com.atlassian.license.applications.clover.CloverLicenseTypeStore;
import com.atlassian.license.applications.confluence.ConfluenceLicenseTypeStore;
import com.atlassian.license.applications.crowd.CrowdLicenseTypeStore;
import com.atlassian.license.applications.crucible.CrucibleLicenseTypeStore;
import com.atlassian.license.applications.editliveplugin.EditlivePluginLicenseTypeStore;
import com.atlassian.license.applications.fisheye.FishEyeLicenseTypeStore;
import com.atlassian.license.applications.greenhopper.GreenHopperLicenseTypeStore;
import com.atlassian.license.applications.jira.JiraLicenseTypeStore;
import com.atlassian.license.applications.perforceplugin.PerforcePluginLicenseTypeStore;
import com.atlassian.license.applications.sharepoint.SharePointPluginLicenseTypeStore;
import com.atlassian.license.applications.vssplugin.VSSPluginLicenseTypeStore;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class OldLicenseTypeResolver {
    private static final Map<LicenseTypeKey, LicenseType> OLD_LICENSE_TYPES = new HashMap<LicenseTypeKey, LicenseType>();

    public static LicenseType getLicenseType(Product product, String type, boolean isEvaluation, LicenseEdition edition) {
        LicenseType licenseType = OLD_LICENSE_TYPES.get(new LicenseTypeKey(product, type.toUpperCase(), isEvaluation, edition));
        if (licenseType == null) {
            throw new LicenseException("Could not find license type matching <" + product + ", " + type + ", " + isEvaluation + ", " + edition + ">");
        }
        return licenseType;
    }

    private static void registerLicenseTypes(Product product, LicenseTypeStore licenseTypeStore) {
        for (LicenseType licenseType : licenseTypeStore.getAllLicenses()) {
            OLD_LICENSE_TYPES.put(new LicenseTypeKey(product, licenseType.getNewLicenseTypeName(), licenseType.isEvaluationLicenseType(), licenseType.getEdition()), licenseType);
        }
        if (LicenseManager.getInstance().lookupLicenseTypeStore(product.getName()) == null) {
            LicenseManager.getInstance().addLicenseConfiguration(product.getName(), licenseTypeStore, new MemoryLicenseRegistry());
        }
    }

    public static LicenseTypeStore getLicenseTypeStore(Product product) {
        return LicenseManager.getInstance().getLicenseTypeStore(product.getName());
    }

    static {
        OldLicenseTypeResolver.registerLicenseTypes(Product.JIRA, new JiraLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.CONFLUENCE, new ConfluenceLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.BAMBOO, new BambooLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.CROWD, new CrowdLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.CLOVER, new CloverLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.FISHEYE, new FishEyeLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.CRUCIBLE, new CrucibleLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.VSS_PLUGIN, new VSSPluginLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.PERFORCE_PLUGIN, new PerforcePluginLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.EDIT_LIVE_PLUGIN, new EditlivePluginLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.SHAREPOINT_PLUGIN, new SharePointPluginLicenseTypeStore());
        OldLicenseTypeResolver.registerLicenseTypes(Product.GREENHOPPER, new GreenHopperLicenseTypeStore());
    }

    private static final class LicenseTypeKey {
        private final Product product;
        private final String typeName;
        private final boolean evaluation;
        private final LicenseEdition edition;

        LicenseTypeKey(Product product, String typeName, boolean evaluation, LicenseEdition edition) {
            this.product = product;
            this.typeName = typeName;
            this.evaluation = evaluation;
            this.edition = edition;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            LicenseTypeKey that = (LicenseTypeKey)o;
            if (this.evaluation != that.evaluation) {
                return false;
            }
            if (this.edition != that.edition) {
                return false;
            }
            if (this.product != that.product) {
                return false;
            }
            return !(this.typeName != null ? !this.typeName.equals(that.typeName) : that.typeName != null);
        }

        public int hashCode() {
            int result = this.product != null ? this.product.hashCode() : 0;
            result = 31 * result + (this.typeName != null ? this.typeName.hashCode() : 0);
            result = 31 * result + (this.evaluation ? 1 : 0);
            result = 31 * result + (this.edition != null ? this.edition.hashCode() : 0);
            return result;
        }
    }
}


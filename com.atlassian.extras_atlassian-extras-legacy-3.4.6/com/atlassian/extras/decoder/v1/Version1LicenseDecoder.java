/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.common.LicenseException
 *  com.atlassian.extras.common.log.Logger
 *  com.atlassian.extras.common.log.Logger$Log
 *  com.atlassian.extras.decoder.api.AbstractLicenseDecoder
 */
package com.atlassian.extras.decoder.v1;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.common.LicenseException;
import com.atlassian.extras.common.log.Logger;
import com.atlassian.extras.decoder.api.AbstractLicenseDecoder;
import com.atlassian.extras.decoder.v1.DefaultLicenseTranslator;
import com.atlassian.extras.decoder.v1.LicenseTranslator;
import com.atlassian.extras.decoder.v1.confluence.ConfluenceLicenseTranslator;
import com.atlassian.extras.legacy.util.OldLicenseTypeResolver;
import com.atlassian.license.License;
import com.atlassian.license.LicensePair;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import com.atlassian.license.LicenseUtils;
import com.atlassian.license.decoder.LicenseDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

@Deprecated
public class Version1LicenseDecoder
extends AbstractLicenseDecoder {
    protected final Logger.Log log = Logger.getInstance(((Object)((Object)this)).getClass());
    private static final Map<Product, LicenseTranslator> LICENSE_TRANSLATORS = new HashMap<Product, LicenseTranslator>();

    public Properties doDecode(String licenseText) {
        LicensePair licensePair;
        try {
            licensePair = Version1LicenseDecoder.splitLicense(licenseText);
        }
        catch (com.atlassian.license.LicenseException e) {
            throw new LicenseException((Throwable)e);
        }
        String messageString = new String(licensePair.getLicense(), StandardCharsets.UTF_8);
        StringTokenizer tokenizer = new StringTokenizer(messageString, "^");
        if (tokenizer.hasMoreTokens()) {
            int licenseTypeCode;
            try {
                licenseTypeCode = Integer.parseInt(tokenizer.nextToken());
            }
            catch (NumberFormatException e) {
                throw new LicenseException("Could NOT parse license type code", (Throwable)e);
            }
            for (Product product : LICENSE_TRANSLATORS.keySet()) {
                PublicKey publicKey;
                LicenseTypeStore typeStore = OldLicenseTypeResolver.getLicenseTypeStore(product);
                LicenseType licenseType = typeStore.lookupLicenseType(licenseTypeCode);
                if (licenseType == null) continue;
                try {
                    publicKey = LicenseDecoder.loadPublicKeyFromFile(typeStore.getPublicKeyFileName());
                }
                catch (Exception e) {
                    throw new LicenseException((Throwable)e);
                }
                try {
                    License oldLicense = LicenseDecoder.parseOldLicense(licensePair, publicKey, product.getName());
                    if (oldLicense == null) continue;
                    return LICENSE_TRANSLATORS.get(product).translate(oldLicense);
                }
                catch (InvalidKeyException e) {
                    this.log.warn((Object)"This exception should NOT have happened", (Throwable)e);
                    return null;
                }
                catch (NoSuchAlgorithmException e) {
                    this.log.error((Object)"Couldn't find the algorithm", (Throwable)e);
                    return null;
                }
                catch (SignatureException e) {
                    this.log.warn((Object)"Error in the signature (forged license)", (Throwable)e);
                    return null;
                }
                catch (com.atlassian.license.LicenseException e) {
                    this.log.warn((Object)"Invalid license", (Throwable)e);
                }
            }
            return null;
        }
        this.log.error((Object)("License <" + messageString + "> has no data."));
        return null;
    }

    public boolean canDecode(String licenseText) {
        try {
            Version1LicenseDecoder.splitLicense(licenseText);
            return true;
        }
        catch (com.atlassian.license.LicenseException e) {
            this.log.debug((Object)"Couldn't split the license, must be some kind of new license.", (Throwable)e);
            return false;
        }
    }

    protected int getLicenseVersion() {
        return 1;
    }

    public static LicensePair splitLicense(String concatLicense) throws com.atlassian.license.LicenseException {
        StringTokenizer tokenizer = new StringTokenizer(concatLicense, " \n\t\r");
        if (tokenizer.countTokens() < 3) {
            throw new com.atlassian.license.LicenseException("License string is too short.");
        }
        try {
            byte[] hash = LicenseUtils.getBytes(tokenizer.nextToken() + tokenizer.nextToken());
            String licenseStr = "";
            while (tokenizer.hasMoreTokens()) {
                licenseStr = licenseStr + tokenizer.nextToken();
            }
            byte[] license = LicenseUtils.getBytes(licenseStr);
            return new LicensePair(license, hash, concatLicense);
        }
        catch (Exception e) {
            throw new com.atlassian.license.LicenseException("Exception generating license: " + e);
        }
    }

    static {
        LICENSE_TRANSLATORS.put(Product.BAMBOO, new DefaultLicenseTranslator(Product.BAMBOO));
        LICENSE_TRANSLATORS.put(Product.CLOVER, new DefaultLicenseTranslator(Product.CLOVER));
        LICENSE_TRANSLATORS.put(Product.CONFLUENCE, new ConfluenceLicenseTranslator(Product.CONFLUENCE));
        LICENSE_TRANSLATORS.put(Product.CROWD, new DefaultLicenseTranslator(Product.CROWD));
        LICENSE_TRANSLATORS.put(Product.FISHEYE, new DefaultLicenseTranslator(Product.FISHEYE));
        LICENSE_TRANSLATORS.put(Product.JIRA, new DefaultLicenseTranslator(Product.JIRA));
        LICENSE_TRANSLATORS.put(Product.CRUCIBLE, new DefaultLicenseTranslator(Product.CRUCIBLE));
        LICENSE_TRANSLATORS.put(Product.EDIT_LIVE_PLUGIN, new DefaultLicenseTranslator(Product.EDIT_LIVE_PLUGIN));
        LICENSE_TRANSLATORS.put(Product.PERFORCE_PLUGIN, new DefaultLicenseTranslator(Product.PERFORCE_PLUGIN));
        LICENSE_TRANSLATORS.put(Product.SHAREPOINT_PLUGIN, new DefaultLicenseTranslator(Product.SHAREPOINT_PLUGIN));
        LICENSE_TRANSLATORS.put(Product.VSS_PLUGIN, new DefaultLicenseTranslator(Product.VSS_PLUGIN));
        LICENSE_TRANSLATORS.put(Product.GREENHOPPER, new DefaultLicenseTranslator(Product.GREENHOPPER));
    }
}


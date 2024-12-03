/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.common.DateEditor
 *  com.atlassian.extras.common.LicenseTypeAndEditionResolver
 *  com.atlassian.extras.common.log.Logger
 *  com.atlassian.extras.common.log.Logger$Log
 *  com.atlassian.extras.common.util.LicenseProperties
 *  com.atlassian.extras.common.util.ProductLicenseProperties
 *  com.atlassian.extras.decoder.v2.Version2LicenseDecoder
 */
package com.atlassian.license.decoder;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.common.DateEditor;
import com.atlassian.extras.common.LicenseTypeAndEditionResolver;
import com.atlassian.extras.common.log.Logger;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.common.util.ProductLicenseProperties;
import com.atlassian.extras.decoder.v2.Version2LicenseDecoder;
import com.atlassian.extras.legacy.util.OldLicenseTypeResolver;
import com.atlassian.license.DefaultLicense;
import com.atlassian.license.License;
import com.atlassian.license.LicenseException;
import com.atlassian.license.LicenseManager;
import com.atlassian.license.LicensePair;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseUtils;
import com.atlassian.license.decoder.LicenseAdaptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

@Deprecated
public class LicenseDecoder {
    private static final Logger.Log log = Logger.getInstance(LicenseDecoder.class);
    public static final String DURATION_PREFIX = "Duration:";
    public static final String JIRA_APPLICATION_NAME = "JIRA";
    public static final String CONF_APPLICATION_NAME = "CONFLUENCE";

    public static License getLicense(LicensePair pair, String applicationName) {
        try {
            return LicenseDecoder.loadLicense(pair, LicenseDecoder.getPublicKey(applicationName), applicationName);
        }
        catch (LicenseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean isValid(LicensePair pair, String applicationName) {
        return LicenseDecoder.getLicense(pair, applicationName) != null;
    }

    private static PublicKey getPublicKey(String applicationName) throws LicenseException {
        String publicKeyFileName = LicenseDecoder.getPublicKeyFilename(applicationName);
        if (publicKeyFileName == null || "".equals(publicKeyFileName)) {
            throw new LicenseException("The filename for the public key is null. This must be set before a public key can be located.");
        }
        try {
            return LicenseDecoder.loadPublicKeyFromFile(publicKeyFileName);
        }
        catch (Exception e) {
            log.error((Object)("Exception looking up public key: " + e.getMessage()), (Throwable)e);
            throw new LicenseException("Exception getting verification from file - possible classloader problem, or corrupt JIRA installation ");
        }
    }

    public static PublicKey loadPublicKeyFromFile(String publicKeyFileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        InputStream keyfis = null;
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader != null) {
            keyfis = contextLoader.getResourceAsStream(publicKeyFileName);
        }
        if (keyfis == null) {
            keyfis = LicenseDecoder.class.getClassLoader().getResourceAsStream(publicKeyFileName);
        }
        byte[] encKey = LicenseUtils.readKey(keyfis);
        keyfis.close();
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(pubKeySpec);
    }

    private static License loadLicense(LicensePair pair, PublicKey publicKey, String applicationName) {
        try {
            if (pair.isNG()) {
                return LicenseDecoder.parseNewLicense(pair, applicationName);
            }
            return LicenseDecoder.parseOldLicense(pair, publicKey, applicationName);
        }
        catch (Exception e) {
            log.error((Object)e);
            return null;
        }
    }

    public static License parseOldLicense(LicensePair pair, PublicKey publicKey, String applicationName) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, LicenseException {
        Signature signature = Signature.getInstance("SHA1withDSA");
        signature.initVerify(publicKey);
        signature.update(pair.getLicense());
        if (signature.verify(pair.getHash())) {
            String messageString = LicenseDecoder.getDecodedMessage(pair.getLicense());
            StringTokenizer tokenizer = new StringTokenizer(messageString, "^");
            if (tokenizer.hasMoreTokens()) {
                int licenseTypeCode = Integer.parseInt(tokenizer.nextToken());
                LicenseType licenseType = LicenseManager.getInstance().getLicenseType(applicationName, licenseTypeCode);
                Date dateCreated = DateEditor.getDate((String)tokenizer.nextToken());
                Date datePurchased = DateEditor.getDate((String)tokenizer.nextToken());
                Date dateExpires = null;
                if (licenseType.expires()) {
                    dateExpires = DateEditor.getDate((String)tokenizer.nextToken());
                }
                String organisation = tokenizer.nextToken();
                String licenseId = LicenseDecoder.getLicenseIdFromLicenseString(pair.getOriginalLicenseString());
                int users = -1;
                int clusterCount = 0;
                if (licenseType.requiresUserLimit()) {
                    String usersAndClustersLimits = tokenizer.nextToken();
                    String[] parts = usersAndClustersLimits.split("\\|");
                    if (parts.length != 1 && parts.length != 2) {
                        throw new LicenseException("License contained invalid user limit:" + usersAndClustersLimits);
                    }
                    users = Integer.parseInt(parts[0]);
                    if (parts.length == 2) {
                        clusterCount = Integer.parseInt(parts[1]);
                    }
                }
                String partnerName = null;
                if (tokenizer.hasMoreTokens()) {
                    partnerName = tokenizer.nextToken();
                }
                return new DefaultLicense(dateCreated, datePurchased, dateExpires, organisation, licenseType, users, partnerName, licenseId, clusterCount, null);
            }
            return null;
        }
        log.warn((Object)"Signature did not verify properly.");
        return null;
    }

    private static License parseNewLicense(LicensePair pair, String applicationName) throws LicenseException {
        Version2LicenseDecoder licenseDecoder = new Version2LicenseDecoder();
        if (!licenseDecoder.canDecode(pair.getOriginalLicenseString())) {
            throw new LicenseException("Failed to decode as V2 license:\n" + pair);
        }
        Properties prop = licenseDecoder.decode(pair.getOriginalLicenseString());
        Product product = LicenseDecoder.lookupProduct(applicationName);
        ProductLicenseProperties productProperties = new ProductLicenseProperties(product, prop);
        String editionName = productProperties.getProperty("LicenseEdition");
        String licenseTypeString = productProperties.getProperty("LicenseType");
        LicenseType licenseType = licenseTypeString != null ? LicenseManager.getInstance().getLicenseType(applicationName, licenseTypeString) : OldLicenseTypeResolver.getLicenseType(product, productProperties.getProperty("LicenseTypeName"), productProperties.getBoolean("Evaluation"), editionName == null ? null : LicenseTypeAndEditionResolver.getLicenseEdition((String)editionName));
        return new LicenseAdaptor((LicenseProperties)productProperties, licenseType);
    }

    private static Product lookupProduct(String key) {
        for (Product product : Product.ATLASSIAN_PRODUCTS) {
            if (!product.getName().equalsIgnoreCase(key) && !product.getNamespace().equalsIgnoreCase(key.replace(' ', '_'))) continue;
            return product;
        }
        throw new IllegalArgumentException("Could not find product for key <" + key + ">");
    }

    private static String getLicenseIdFromLicenseString(String originalLicenseString) {
        if (originalLicenseString != null && originalLicenseString.length() > 70) {
            return originalLicenseString.replace("\n", "").replace("\r", "").replace("\t", "").substring(59, 69).toUpperCase();
        }
        return "";
    }

    private static String getDecodedMessage(byte[] message) {
        return new String(message, StandardCharsets.UTF_8);
    }

    public static boolean isValid(LicensePair pair, PublicKey publicKey, String applicationName) {
        return LicenseDecoder.loadLicense(pair, publicKey, applicationName) != null;
    }

    private static String getPublicKeyFilename(String applicationName) {
        return LicenseManager.getInstance().getLicenseTypeStore(applicationName).getPublicKeyFileName();
    }
}


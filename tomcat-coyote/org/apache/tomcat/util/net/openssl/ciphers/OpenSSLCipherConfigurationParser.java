/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net.openssl.ciphers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.net.openssl.ciphers.Encryption;
import org.apache.tomcat.util.net.openssl.ciphers.EncryptionLevel;
import org.apache.tomcat.util.net.openssl.ciphers.KeyExchange;
import org.apache.tomcat.util.net.openssl.ciphers.MessageDigest;
import org.apache.tomcat.util.net.openssl.ciphers.Protocol;
import org.apache.tomcat.util.res.StringManager;

public class OpenSSLCipherConfigurationParser {
    private static final Log log = LogFactory.getLog(OpenSSLCipherConfigurationParser.class);
    private static final StringManager sm = StringManager.getManager(OpenSSLCipherConfigurationParser.class);
    private static boolean initialized = false;
    private static final String SEPARATOR = ":|,| ";
    private static final String EXCLUDE = "!";
    private static final String DELETE = "-";
    private static final String TO_END = "+";
    private static final String AND = "+";
    private static final Map<String, List<Cipher>> aliases = new LinkedHashMap<String, List<Cipher>>();
    private static final String eNULL = "eNULL";
    private static final String aNULL = "aNULL";
    private static final String HIGH = "HIGH";
    private static final String MEDIUM = "MEDIUM";
    private static final String LOW = "LOW";
    private static final String EXPORT = "EXPORT";
    private static final String EXPORT40 = "EXPORT40";
    private static final String EXPORT56 = "EXPORT56";
    private static final String kRSA = "kRSA";
    private static final String aRSA = "aRSA";
    private static final String RSA = "RSA";
    private static final String kEDH = "kEDH";
    private static final String kDHE = "kDHE";
    private static final String EDH = "EDH";
    private static final String DHE = "DHE";
    private static final String kDHr = "kDHr";
    private static final String kDHd = "kDHd";
    private static final String kDH = "kDH";
    private static final String kECDHr = "kECDHr";
    private static final String kECDHe = "kECDHe";
    private static final String kECDH = "kECDH";
    private static final String kEECDH = "kEECDH";
    private static final String EECDH = "EECDH";
    private static final String ECDH = "ECDH";
    private static final String kECDHE = "kECDHE";
    private static final String ECDHE = "ECDHE";
    private static final String AECDH = "AECDH";
    private static final String DSS = "DSS";
    private static final String aDSS = "aDSS";
    private static final String aDH = "aDH";
    private static final String aECDH = "aECDH";
    private static final String aECDSA = "aECDSA";
    private static final String ECDSA = "ECDSA";
    private static final String kFZA = "kFZA";
    private static final String aFZA = "aFZA";
    private static final String eFZA = "eFZA";
    private static final String FZA = "FZA";
    private static final String DH = "DH";
    private static final String ADH = "ADH";
    private static final String AES128 = "AES128";
    private static final String AES256 = "AES256";
    private static final String AES = "AES";
    private static final String AESGCM = "AESGCM";
    private static final String AESCCM = "AESCCM";
    private static final String AESCCM8 = "AESCCM8";
    private static final String ARIA128 = "ARIA128";
    private static final String ARIA256 = "ARIA256";
    private static final String ARIA = "ARIA";
    private static final String CAMELLIA128 = "CAMELLIA128";
    private static final String CAMELLIA256 = "CAMELLIA256";
    private static final String CAMELLIA = "CAMELLIA";
    private static final String CHACHA20 = "CHACHA20";
    private static final String TRIPLE_DES = "3DES";
    private static final String DES = "DES";
    private static final String RC4 = "RC4";
    private static final String RC2 = "RC2";
    private static final String IDEA = "IDEA";
    private static final String SEED = "SEED";
    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA1";
    private static final String SHA = "SHA";
    private static final String SHA256 = "SHA256";
    private static final String SHA384 = "SHA384";
    private static final String KRB5 = "KRB5";
    private static final String aGOST = "aGOST";
    private static final String aGOST01 = "aGOST01";
    private static final String aGOST94 = "aGOST94";
    private static final String kGOST = "kGOST";
    private static final String GOST94 = "GOST94";
    private static final String GOST89MAC = "GOST89MAC";
    private static final String aSRP = "aSRP";
    private static final String kSRP = "kSRP";
    private static final String SRP = "SRP";
    private static final String PSK = "PSK";
    private static final String aPSK = "aPSK";
    private static final String kPSK = "kPSK";
    private static final String kRSAPSK = "kRSAPSK";
    private static final String kECDHEPSK = "kECDHEPSK";
    private static final String kDHEPSK = "kDHEPSK";
    private static final String DEFAULT = "DEFAULT";
    private static final String COMPLEMENTOFDEFAULT = "COMPLEMENTOFDEFAULT";
    private static final String ALL = "ALL";
    private static final String COMPLEMENTOFALL = "COMPLEMENTOFALL";
    private static final Map<String, String> jsseToOpenSSL = new HashMap<String, String>();

    private static void init() {
        for (Cipher cipher : Cipher.values()) {
            String alias = cipher.getOpenSSLAlias();
            if (aliases.containsKey(alias)) {
                aliases.get(alias).add(cipher);
            } else {
                ArrayList<Cipher> list = new ArrayList<Cipher>();
                list.add(cipher);
                aliases.put(alias, list);
            }
            aliases.put(cipher.name(), Collections.singletonList(cipher));
            for (String string : cipher.getOpenSSLAltNames()) {
                if (aliases.containsKey(string)) {
                    aliases.get(string).add(cipher);
                    continue;
                }
                ArrayList<Cipher> list = new ArrayList<Cipher>();
                list.add(cipher);
                aliases.put(string, list);
            }
            jsseToOpenSSL.put(cipher.name(), cipher.getOpenSSLAlias());
            Set<String> jsseNames = cipher.getJsseNames();
            for (String jsseName : jsseNames) {
                jsseToOpenSSL.put(jsseName, cipher.getOpenSSLAlias());
            }
        }
        List<Cipher> allCiphersList = Arrays.asList(Cipher.values());
        Collections.reverse(allCiphersList);
        LinkedHashSet<Cipher> allCiphers = OpenSSLCipherConfigurationParser.defaultSort(new LinkedHashSet<Cipher>(allCiphersList));
        OpenSSLCipherConfigurationParser.addListAlias(eNULL, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.eNULL)));
        LinkedHashSet<Cipher> all = new LinkedHashSet<Cipher>(allCiphers);
        OpenSSLCipherConfigurationParser.remove(all, eNULL);
        OpenSSLCipherConfigurationParser.addListAlias(ALL, all);
        OpenSSLCipherConfigurationParser.addListAlias(HIGH, OpenSSLCipherConfigurationParser.filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.HIGH)));
        OpenSSLCipherConfigurationParser.addListAlias(MEDIUM, OpenSSLCipherConfigurationParser.filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.MEDIUM)));
        OpenSSLCipherConfigurationParser.addListAlias(LOW, OpenSSLCipherConfigurationParser.filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.LOW)));
        OpenSSLCipherConfigurationParser.addListAlias(EXPORT, OpenSSLCipherConfigurationParser.filterByEncryptionLevel(allCiphers, new HashSet<EncryptionLevel>(Arrays.asList(EncryptionLevel.EXP40, EncryptionLevel.EXP56))));
        aliases.put("EXP", aliases.get(EXPORT));
        OpenSSLCipherConfigurationParser.addListAlias(EXPORT40, OpenSSLCipherConfigurationParser.filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.EXP40)));
        OpenSSLCipherConfigurationParser.addListAlias(EXPORT56, OpenSSLCipherConfigurationParser.filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.EXP56)));
        aliases.put("NULL", aliases.get(eNULL));
        aliases.put(COMPLEMENTOFALL, aliases.get(eNULL));
        OpenSSLCipherConfigurationParser.addListAlias(aNULL, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.aNULL)));
        OpenSSLCipherConfigurationParser.addListAlias(kRSA, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.RSA)));
        OpenSSLCipherConfigurationParser.addListAlias(aRSA, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.RSA)));
        aliases.put(RSA, aliases.get(kRSA));
        OpenSSLCipherConfigurationParser.addListAlias(kEDH, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EDH)));
        OpenSSLCipherConfigurationParser.addListAlias(kDHE, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EDH)));
        LinkedHashSet<Cipher> edh = OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EDH));
        edh.removeAll(OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.aNULL)));
        OpenSSLCipherConfigurationParser.addListAlias(EDH, edh);
        OpenSSLCipherConfigurationParser.addListAlias(DHE, edh);
        OpenSSLCipherConfigurationParser.addListAlias(kDHr, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.DHr)));
        OpenSSLCipherConfigurationParser.addListAlias(kDHd, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.DHd)));
        OpenSSLCipherConfigurationParser.addListAlias(kDH, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, new HashSet<KeyExchange>(Arrays.asList(KeyExchange.DHr, KeyExchange.DHd))));
        OpenSSLCipherConfigurationParser.addListAlias(kECDHr, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.ECDHr)));
        OpenSSLCipherConfigurationParser.addListAlias(kECDHe, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.ECDHe)));
        OpenSSLCipherConfigurationParser.addListAlias(kECDH, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, new HashSet<KeyExchange>(Arrays.asList(KeyExchange.ECDHe, KeyExchange.ECDHr))));
        OpenSSLCipherConfigurationParser.addListAlias(ECDH, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, new HashSet<KeyExchange>(Arrays.asList(KeyExchange.ECDHe, KeyExchange.ECDHr, KeyExchange.EECDH))));
        OpenSSLCipherConfigurationParser.addListAlias(kECDHE, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH)));
        LinkedHashSet<Cipher> ecdhe = OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH));
        OpenSSLCipherConfigurationParser.remove(ecdhe, aNULL);
        OpenSSLCipherConfigurationParser.addListAlias(ECDHE, ecdhe);
        OpenSSLCipherConfigurationParser.addListAlias(kEECDH, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH)));
        LinkedHashSet<Cipher> eecdh = OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH));
        eecdh.removeAll(OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.aNULL)));
        OpenSSLCipherConfigurationParser.addListAlias(EECDH, eecdh);
        OpenSSLCipherConfigurationParser.addListAlias(aDSS, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.DSS)));
        aliases.put(DSS, aliases.get(aDSS));
        OpenSSLCipherConfigurationParser.addListAlias(aDH, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.DH)));
        LinkedHashSet<Cipher> linkedHashSet = OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH));
        OpenSSLCipherConfigurationParser.addListAlias(AECDH, OpenSSLCipherConfigurationParser.filterByAuthentication(linkedHashSet, Collections.singleton(Authentication.aNULL)));
        OpenSSLCipherConfigurationParser.addListAlias(aECDH, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.ECDH)));
        OpenSSLCipherConfigurationParser.addListAlias(ECDSA, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.ECDSA)));
        aliases.put(aECDSA, aliases.get(ECDSA));
        OpenSSLCipherConfigurationParser.addListAlias(kFZA, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.FZA)));
        OpenSSLCipherConfigurationParser.addListAlias(aFZA, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.FZA)));
        OpenSSLCipherConfigurationParser.addListAlias(eFZA, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.FZA)));
        OpenSSLCipherConfigurationParser.addListAlias(FZA, OpenSSLCipherConfigurationParser.filter(allCiphers, null, Collections.singleton(KeyExchange.FZA), Collections.singleton(Authentication.FZA), Collections.singleton(Encryption.FZA), null, null));
        OpenSSLCipherConfigurationParser.addListAlias("TLSv1.2", OpenSSLCipherConfigurationParser.filterByProtocol(allCiphers, Collections.singleton(Protocol.TLSv1_2)));
        OpenSSLCipherConfigurationParser.addListAlias("TLSv1.0", OpenSSLCipherConfigurationParser.filterByProtocol(allCiphers, Collections.singleton(Protocol.TLSv1)));
        OpenSSLCipherConfigurationParser.addListAlias("SSLv3", OpenSSLCipherConfigurationParser.filterByProtocol(allCiphers, Collections.singleton(Protocol.SSLv3)));
        aliases.put("TLSv1", aliases.get("TLSv1.0"));
        OpenSSLCipherConfigurationParser.addListAlias("SSLv2", OpenSSLCipherConfigurationParser.filterByProtocol(allCiphers, Collections.singleton(Protocol.SSLv2)));
        OpenSSLCipherConfigurationParser.addListAlias(DH, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, new HashSet<KeyExchange>(Arrays.asList(KeyExchange.DHr, KeyExchange.DHd, KeyExchange.EDH))));
        LinkedHashSet<Cipher> adh = OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EDH));
        adh.retainAll(OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.aNULL)));
        OpenSSLCipherConfigurationParser.addListAlias(ADH, adh);
        OpenSSLCipherConfigurationParser.addListAlias(AES128, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, new HashSet<Encryption>(Arrays.asList(Encryption.AES128, Encryption.AES128CCM, Encryption.AES128CCM8, Encryption.AES128GCM))));
        OpenSSLCipherConfigurationParser.addListAlias(AES256, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, new HashSet<Encryption>(Arrays.asList(Encryption.AES256, Encryption.AES256CCM, Encryption.AES256CCM8, Encryption.AES256GCM))));
        OpenSSLCipherConfigurationParser.addListAlias(AES, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, new HashSet<Encryption>(Arrays.asList(Encryption.AES128, Encryption.AES128CCM, Encryption.AES128CCM8, Encryption.AES128GCM, Encryption.AES256, Encryption.AES256CCM, Encryption.AES256CCM8, Encryption.AES256GCM))));
        OpenSSLCipherConfigurationParser.addListAlias(ARIA128, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.ARIA128GCM)));
        OpenSSLCipherConfigurationParser.addListAlias(ARIA256, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.ARIA256GCM)));
        OpenSSLCipherConfigurationParser.addListAlias(ARIA, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, new HashSet<Encryption>(Arrays.asList(Encryption.ARIA128GCM, Encryption.ARIA256GCM))));
        OpenSSLCipherConfigurationParser.addListAlias(AESGCM, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, new HashSet<Encryption>(Arrays.asList(Encryption.AES128GCM, Encryption.AES256GCM))));
        OpenSSLCipherConfigurationParser.addListAlias(AESCCM, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, new HashSet<Encryption>(Arrays.asList(Encryption.AES128CCM, Encryption.AES128CCM8, Encryption.AES256CCM, Encryption.AES256CCM8))));
        OpenSSLCipherConfigurationParser.addListAlias(AESCCM8, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, new HashSet<Encryption>(Arrays.asList(Encryption.AES128CCM8, Encryption.AES256CCM8))));
        OpenSSLCipherConfigurationParser.addListAlias(CAMELLIA, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, new HashSet<Encryption>(Arrays.asList(Encryption.CAMELLIA128, Encryption.CAMELLIA256))));
        OpenSSLCipherConfigurationParser.addListAlias(CAMELLIA128, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.CAMELLIA128)));
        OpenSSLCipherConfigurationParser.addListAlias(CAMELLIA256, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.CAMELLIA256)));
        OpenSSLCipherConfigurationParser.addListAlias(CHACHA20, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.CHACHA20POLY1305)));
        OpenSSLCipherConfigurationParser.addListAlias(TRIPLE_DES, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.TRIPLE_DES)));
        OpenSSLCipherConfigurationParser.addListAlias(DES, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.DES)));
        OpenSSLCipherConfigurationParser.addListAlias(RC4, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.RC4)));
        OpenSSLCipherConfigurationParser.addListAlias(RC2, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.RC2)));
        OpenSSLCipherConfigurationParser.addListAlias(IDEA, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.IDEA)));
        OpenSSLCipherConfigurationParser.addListAlias(SEED, OpenSSLCipherConfigurationParser.filterByEncryption(allCiphers, Collections.singleton(Encryption.SEED)));
        OpenSSLCipherConfigurationParser.addListAlias(MD5, OpenSSLCipherConfigurationParser.filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.MD5)));
        OpenSSLCipherConfigurationParser.addListAlias(SHA1, OpenSSLCipherConfigurationParser.filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.SHA1)));
        aliases.put(SHA, aliases.get(SHA1));
        OpenSSLCipherConfigurationParser.addListAlias(SHA256, OpenSSLCipherConfigurationParser.filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.SHA256)));
        OpenSSLCipherConfigurationParser.addListAlias(SHA384, OpenSSLCipherConfigurationParser.filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.SHA384)));
        OpenSSLCipherConfigurationParser.addListAlias(aGOST, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, new HashSet<Authentication>(Arrays.asList(Authentication.GOST01, Authentication.GOST94))));
        OpenSSLCipherConfigurationParser.addListAlias(aGOST01, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.GOST01)));
        OpenSSLCipherConfigurationParser.addListAlias(aGOST94, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.GOST94)));
        OpenSSLCipherConfigurationParser.addListAlias(kGOST, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.GOST)));
        OpenSSLCipherConfigurationParser.addListAlias(GOST94, OpenSSLCipherConfigurationParser.filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.GOST94)));
        OpenSSLCipherConfigurationParser.addListAlias(GOST89MAC, OpenSSLCipherConfigurationParser.filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.GOST89MAC)));
        OpenSSLCipherConfigurationParser.addListAlias(PSK, OpenSSLCipherConfigurationParser.filter(allCiphers, null, new HashSet<KeyExchange>(Arrays.asList(KeyExchange.PSK, KeyExchange.RSAPSK, KeyExchange.DHEPSK, KeyExchange.ECDHEPSK)), Collections.singleton(Authentication.PSK), null, null, null));
        OpenSSLCipherConfigurationParser.addListAlias(aPSK, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.PSK)));
        OpenSSLCipherConfigurationParser.addListAlias(kPSK, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.PSK)));
        OpenSSLCipherConfigurationParser.addListAlias(kRSAPSK, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.RSAPSK)));
        OpenSSLCipherConfigurationParser.addListAlias(kECDHEPSK, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.ECDHEPSK)));
        OpenSSLCipherConfigurationParser.addListAlias(kDHEPSK, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.DHEPSK)));
        OpenSSLCipherConfigurationParser.addListAlias(KRB5, OpenSSLCipherConfigurationParser.filter(allCiphers, null, Collections.singleton(KeyExchange.KRB5), Collections.singleton(Authentication.KRB5), null, null, null));
        OpenSSLCipherConfigurationParser.addListAlias(aSRP, OpenSSLCipherConfigurationParser.filterByAuthentication(allCiphers, Collections.singleton(Authentication.SRP)));
        OpenSSLCipherConfigurationParser.addListAlias(kSRP, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.SRP)));
        OpenSSLCipherConfigurationParser.addListAlias(SRP, OpenSSLCipherConfigurationParser.filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.SRP)));
        initialized = true;
        OpenSSLCipherConfigurationParser.addListAlias(DEFAULT, OpenSSLCipherConfigurationParser.parse("ALL:!EXPORT:!eNULL:!aNULL:!SSLv2:!DES:!RC2:!RC4:!DSS:!SEED:!IDEA:!CAMELLIA:!AESCCM:!3DES:!ARIA"));
        LinkedHashSet<Cipher> complementOfDefault = OpenSSLCipherConfigurationParser.filterByKeyExchange(all, new HashSet<KeyExchange>(Arrays.asList(KeyExchange.EDH, KeyExchange.EECDH)));
        complementOfDefault = OpenSSLCipherConfigurationParser.filterByAuthentication(complementOfDefault, Collections.singleton(Authentication.aNULL));
        aliases.get(eNULL).forEach(complementOfDefault::remove);
        complementOfDefault.addAll((Collection<Cipher>)aliases.get("SSLv2"));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(EXPORT));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(DES));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(TRIPLE_DES));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(RC2));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(RC4));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(aDSS));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(SEED));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(IDEA));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(CAMELLIA));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(AESCCM));
        complementOfDefault.addAll((Collection<Cipher>)aliases.get(ARIA));
        OpenSSLCipherConfigurationParser.defaultSort(complementOfDefault);
        OpenSSLCipherConfigurationParser.addListAlias(COMPLEMENTOFDEFAULT, complementOfDefault);
    }

    static void addListAlias(String alias, Set<Cipher> ciphers) {
        aliases.put(alias, new ArrayList<Cipher>(ciphers));
    }

    static void moveToEnd(LinkedHashSet<Cipher> ciphers, String alias) {
        OpenSSLCipherConfigurationParser.moveToEnd(ciphers, (Collection<Cipher>)aliases.get(alias));
    }

    static void moveToEnd(LinkedHashSet<Cipher> ciphers, Collection<Cipher> toBeMovedCiphers) {
        ArrayList<Cipher> movedCiphers = new ArrayList<Cipher>(toBeMovedCiphers);
        movedCiphers.retainAll(ciphers);
        movedCiphers.forEach(ciphers::remove);
        ciphers.addAll(movedCiphers);
    }

    static void moveToStart(LinkedHashSet<Cipher> ciphers, Collection<Cipher> toBeMovedCiphers) {
        ArrayList<Cipher> movedCiphers = new ArrayList<Cipher>(toBeMovedCiphers);
        ArrayList<Cipher> originalCiphers = new ArrayList<Cipher>(ciphers);
        movedCiphers.retainAll(ciphers);
        ciphers.clear();
        ciphers.addAll(movedCiphers);
        ciphers.addAll(originalCiphers);
    }

    static void add(LinkedHashSet<Cipher> ciphers, String alias) {
        ciphers.addAll((Collection<Cipher>)aliases.get(alias));
    }

    static void remove(Set<Cipher> ciphers, String alias) {
        aliases.get(alias).forEach(ciphers::remove);
    }

    static LinkedHashSet<Cipher> strengthSort(LinkedHashSet<Cipher> ciphers) {
        HashSet<Integer> keySizes = new HashSet<Integer>();
        for (Cipher cipher : ciphers) {
            keySizes.add(cipher.getStrength_bits());
        }
        ArrayList strength_bits = new ArrayList(keySizes);
        Collections.sort(strength_bits);
        Collections.reverse(strength_bits);
        LinkedHashSet<Cipher> result = new LinkedHashSet<Cipher>(ciphers);
        Iterator iterator = strength_bits.iterator();
        while (iterator.hasNext()) {
            int strength = (Integer)iterator.next();
            OpenSSLCipherConfigurationParser.moveToEnd(result, OpenSSLCipherConfigurationParser.filterByStrengthBits(ciphers, strength));
        }
        return result;
    }

    static LinkedHashSet<Cipher> defaultSort(LinkedHashSet<Cipher> ciphers) {
        LinkedHashSet<Cipher> result = new LinkedHashSet<Cipher>(ciphers.size());
        LinkedHashSet<Cipher> ecdh = new LinkedHashSet<Cipher>(ciphers.size());
        ecdh.addAll(OpenSSLCipherConfigurationParser.filterByKeyExchange(ciphers, Collections.singleton(KeyExchange.EECDH)));
        HashSet<Encryption> aes = new HashSet<Encryption>(Arrays.asList(Encryption.AES128, Encryption.AES128CCM, Encryption.AES128CCM8, Encryption.AES128GCM, Encryption.AES256, Encryption.AES256CCM, Encryption.AES256CCM8, Encryption.AES256GCM));
        result.addAll(OpenSSLCipherConfigurationParser.filterByEncryption(ecdh, aes));
        result.addAll(OpenSSLCipherConfigurationParser.filterByEncryption(ciphers, aes));
        result.addAll(ecdh);
        result.addAll(ciphers);
        OpenSSLCipherConfigurationParser.moveToEnd(result, OpenSSLCipherConfigurationParser.filterByMessageDigest(result, Collections.singleton(MessageDigest.MD5)));
        OpenSSLCipherConfigurationParser.moveToEnd(result, OpenSSLCipherConfigurationParser.filterByAuthentication(result, Collections.singleton(Authentication.aNULL)));
        OpenSSLCipherConfigurationParser.moveToEnd(result, OpenSSLCipherConfigurationParser.filterByAuthentication(result, Collections.singleton(Authentication.ECDH)));
        OpenSSLCipherConfigurationParser.moveToEnd(result, OpenSSLCipherConfigurationParser.filterByKeyExchange(result, Collections.singleton(KeyExchange.RSA)));
        OpenSSLCipherConfigurationParser.moveToEnd(result, OpenSSLCipherConfigurationParser.filterByKeyExchange(result, Collections.singleton(KeyExchange.PSK)));
        OpenSSLCipherConfigurationParser.moveToEnd(result, OpenSSLCipherConfigurationParser.filterByEncryption(result, Collections.singleton(Encryption.RC4)));
        return OpenSSLCipherConfigurationParser.strengthSort(result);
    }

    static Set<Cipher> filterByStrengthBits(Set<Cipher> ciphers, int strength_bits) {
        LinkedHashSet<Cipher> result = new LinkedHashSet<Cipher>(ciphers.size());
        for (Cipher cipher : ciphers) {
            if (cipher.getStrength_bits() != strength_bits) continue;
            result.add(cipher);
        }
        return result;
    }

    static Set<Cipher> filterByProtocol(Set<Cipher> ciphers, Set<Protocol> protocol) {
        return OpenSSLCipherConfigurationParser.filter(ciphers, protocol, null, null, null, null, null);
    }

    static LinkedHashSet<Cipher> filterByKeyExchange(Set<Cipher> ciphers, Set<KeyExchange> kx) {
        return OpenSSLCipherConfigurationParser.filter(ciphers, null, kx, null, null, null, null);
    }

    static LinkedHashSet<Cipher> filterByAuthentication(Set<Cipher> ciphers, Set<Authentication> au) {
        return OpenSSLCipherConfigurationParser.filter(ciphers, null, null, au, null, null, null);
    }

    static Set<Cipher> filterByEncryption(Set<Cipher> ciphers, Set<Encryption> enc) {
        return OpenSSLCipherConfigurationParser.filter(ciphers, null, null, null, enc, null, null);
    }

    static Set<Cipher> filterByEncryptionLevel(Set<Cipher> ciphers, Set<EncryptionLevel> level) {
        return OpenSSLCipherConfigurationParser.filter(ciphers, null, null, null, null, level, null);
    }

    static Set<Cipher> filterByMessageDigest(Set<Cipher> ciphers, Set<MessageDigest> mac) {
        return OpenSSLCipherConfigurationParser.filter(ciphers, null, null, null, null, null, mac);
    }

    static LinkedHashSet<Cipher> filter(Set<Cipher> ciphers, Set<Protocol> protocol, Set<KeyExchange> kx, Set<Authentication> au, Set<Encryption> enc, Set<EncryptionLevel> level, Set<MessageDigest> mac) {
        LinkedHashSet<Cipher> result = new LinkedHashSet<Cipher>(ciphers.size());
        for (Cipher cipher : ciphers) {
            if (protocol != null && protocol.contains((Object)cipher.getProtocol())) {
                result.add(cipher);
            }
            if (kx != null && kx.contains((Object)cipher.getKx())) {
                result.add(cipher);
            }
            if (au != null && au.contains((Object)cipher.getAu())) {
                result.add(cipher);
            }
            if (enc != null && enc.contains((Object)cipher.getEnc())) {
                result.add(cipher);
            }
            if (level != null && level.contains((Object)cipher.getLevel())) {
                result.add(cipher);
            }
            if (mac == null || !mac.contains((Object)cipher.getMac())) continue;
            result.add(cipher);
        }
        return result;
    }

    public static LinkedHashSet<Cipher> parse(String expression) {
        if (!initialized) {
            OpenSSLCipherConfigurationParser.init();
        }
        String[] elements = expression.split(SEPARATOR);
        LinkedHashSet<Cipher> ciphers = new LinkedHashSet<Cipher>();
        HashSet removedCiphers = new HashSet();
        for (String element : elements) {
            String[] intersections;
            String alias;
            if (element.startsWith(DELETE)) {
                alias = element.substring(1);
                if (!aliases.containsKey(alias)) continue;
                OpenSSLCipherConfigurationParser.remove(ciphers, alias);
                continue;
            }
            if (element.startsWith(EXCLUDE)) {
                alias = element.substring(1);
                if (aliases.containsKey(alias)) {
                    removedCiphers.addAll(aliases.get(alias));
                    continue;
                }
                log.warn((Object)sm.getString("opensslCipherConfigurationParser.unknownElement", new Object[]{alias}));
                continue;
            }
            if (element.startsWith("+")) {
                alias = element.substring(1);
                if (!aliases.containsKey(alias)) continue;
                OpenSSLCipherConfigurationParser.moveToEnd(ciphers, alias);
                continue;
            }
            if ("@STRENGTH".equals(element)) {
                OpenSSLCipherConfigurationParser.strengthSort(ciphers);
                break;
            }
            if (aliases.containsKey(element)) {
                OpenSSLCipherConfigurationParser.add(ciphers, element);
                continue;
            }
            if (!element.contains("+") || (intersections = element.split("\\+")).length <= 0 || !aliases.containsKey(intersections[0])) continue;
            ArrayList result = new ArrayList(aliases.get(intersections[0]));
            for (int i = 1; i < intersections.length; ++i) {
                if (!aliases.containsKey(intersections[i])) continue;
                result.retainAll((Collection)aliases.get(intersections[i]));
            }
            ciphers.addAll(result);
        }
        ciphers.removeAll(removedCiphers);
        return ciphers;
    }

    public static List<String> convertForJSSE(Collection<Cipher> ciphers) {
        ArrayList<String> result = new ArrayList<String>(ciphers.size());
        for (Cipher cipher : ciphers) {
            result.addAll(cipher.getJsseNames());
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("opensslCipherConfigurationParser.effectiveCiphers", new Object[]{OpenSSLCipherConfigurationParser.displayResult(ciphers, true, ",")}));
        }
        return result;
    }

    public static List<String> parseExpression(String expression) {
        return OpenSSLCipherConfigurationParser.convertForJSSE(OpenSSLCipherConfigurationParser.parse(expression));
    }

    public static String jsseToOpenSSL(String jsseCipherName) {
        if (!initialized) {
            OpenSSLCipherConfigurationParser.init();
        }
        return jsseToOpenSSL.get(jsseCipherName);
    }

    public static String openSSLToJsse(String opensslCipherName) {
        List<Cipher> ciphers;
        if (!initialized) {
            OpenSSLCipherConfigurationParser.init();
        }
        if ((ciphers = aliases.get(opensslCipherName)) == null || ciphers.size() != 1) {
            return null;
        }
        Cipher cipher = ciphers.get(0);
        return cipher.getJsseNames().iterator().next();
    }

    static String displayResult(Collection<Cipher> ciphers, boolean useJSSEFormat, String separator) {
        if (ciphers.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(ciphers.size() * 16);
        for (Cipher cipher : ciphers) {
            if (useJSSEFormat) {
                for (String name : cipher.getJsseNames()) {
                    builder.append(name);
                    builder.append(separator);
                }
            } else {
                builder.append(cipher.getOpenSSLAlias());
            }
            builder.append(separator);
        }
        return builder.toString().substring(0, builder.length() - 1);
    }

    public static void usage() {
        System.out.println("Usage: java " + OpenSSLCipherConfigurationParser.class.getName() + " [options] cipherspec");
        System.out.println();
        System.out.println("Displays the TLS cipher suites matching the cipherspec.");
        System.out.println();
        System.out.println(" --help,");
        System.out.println(" -h          Print this help message");
        System.out.println(" --openssl   Show OpenSSL cipher suite names instead of IANA cipher suite names.");
        System.out.println(" --verbose,");
        System.out.println(" -v          Provide detailed cipher listing");
    }

    public static void main(String[] args) throws Exception {
        int argindex;
        boolean verbose = false;
        boolean useOpenSSLNames = false;
        for (argindex = 0; argindex < args.length; ++argindex) {
            String arg = args[argindex];
            if ("--verbose".equals(arg) || "-v".equals(arg)) {
                verbose = true;
                continue;
            }
            if ("--openssl".equals(arg)) {
                useOpenSSLNames = true;
                continue;
            }
            if ("--help".equals(arg) || "-h".equals(arg)) {
                OpenSSLCipherConfigurationParser.usage();
                System.exit(0);
                continue;
            }
            if ("--".equals(arg)) {
                ++argindex;
                break;
            }
            if (!arg.startsWith(DELETE)) break;
            System.out.println("Unknown option: " + arg);
            OpenSSLCipherConfigurationParser.usage();
            System.exit(1);
        }
        String cipherSpec = argindex < args.length ? args[argindex] : DEFAULT;
        LinkedHashSet<Cipher> ciphers = OpenSSLCipherConfigurationParser.parse(cipherSpec);
        boolean first = true;
        if (null != ciphers && 0 < ciphers.size()) {
            for (Cipher cipher : ciphers) {
                if (first) {
                    first = false;
                } else if (!verbose) {
                    System.out.print(',');
                }
                if (useOpenSSLNames) {
                    System.out.print(cipher.getOpenSSLAlias());
                } else {
                    System.out.print(cipher.name());
                }
                if (!verbose) continue;
                System.out.println("\t" + (Object)((Object)cipher.getProtocol()) + "\tKx=" + (Object)((Object)cipher.getKx()) + "\tAu=" + (Object)((Object)cipher.getAu()) + "\tEnc=" + (Object)((Object)cipher.getEnc()) + "\tMac=" + (Object)((Object)cipher.getMac()));
            }
            System.out.println();
        } else {
            System.out.println("No ciphers match '" + cipherSpec + "'");
        }
    }
}


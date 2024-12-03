/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha2CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha3CountryCode;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ISO3166_1AlphaCountryCodeMapper {
    public static final String RESOURCE_FILE_NAME = "iso3166_1alpha-2-3-map.properties";
    private static final Properties MAP_2_3 = new Properties();
    private static final Properties MAP_3_2 = new Properties();

    private static void lazyLoadMap_2_3() {
        if (!MAP_2_3.isEmpty()) {
            return;
        }
        InputStream is = ISO3166_1AlphaCountryCodeMapper.class.getClassLoader().getResourceAsStream(RESOURCE_FILE_NAME);
        try {
            MAP_2_3.load(is);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static void lazyLoadMap_3_2() {
        if (!MAP_3_2.isEmpty()) {
            return;
        }
        if (MAP_2_3.isEmpty()) {
            ISO3166_1AlphaCountryCodeMapper.lazyLoadMap_2_3();
        }
        for (String code2 : MAP_2_3.stringPropertyNames()) {
            String code3 = MAP_2_3.getProperty(code2);
            MAP_3_2.put(code3, code2);
        }
    }

    public static ISO3166_1Alpha3CountryCode toAlpha3CountryCode(ISO3166_1Alpha2CountryCode alpha2Code) {
        ISO3166_1AlphaCountryCodeMapper.lazyLoadMap_2_3();
        String alpha3Code = MAP_2_3.getProperty(alpha2Code.getValue());
        return alpha3Code != null ? new ISO3166_1Alpha3CountryCode(alpha3Code) : null;
    }

    public static ISO3166_1Alpha2CountryCode toAlpha2CountryCode(ISO3166_1Alpha3CountryCode alpha3Code) {
        ISO3166_1AlphaCountryCodeMapper.lazyLoadMap_3_2();
        String alpha2Code = MAP_3_2.getProperty(alpha3Code.getValue());
        return alpha2Code != null ? new ISO3166_1Alpha2CountryCode(alpha2Code) : null;
    }
}


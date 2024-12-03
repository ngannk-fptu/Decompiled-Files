/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 */
package org.springframework.data.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.GeoModule;
import org.springframework.data.web.config.SpringDataJacksonModules;

public class SpringDataJacksonConfiguration
implements SpringDataJacksonModules {
    @Bean
    public GeoModule jacksonGeoModule() {
        return new GeoModule();
    }
}


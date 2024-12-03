/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.util.StringUtils
 *  io.micrometer.core.ipc.http.HttpSender$Request$Builder
 */
package io.micrometer.influx;

import io.micrometer.common.util.StringUtils;
import io.micrometer.core.ipc.http.HttpSender;
import io.micrometer.influx.InfluxConfig;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public enum InfluxApiVersion {
    V1{

        @Override
        String writeEndpoint(InfluxConfig config) {
            String influxEndpoint = config.uri() + "/write?consistency=" + config.consistency().name().toLowerCase() + "&precision=ms&db=" + config.db();
            if (StringUtils.isNotBlank((String)config.retentionPolicy())) {
                influxEndpoint = influxEndpoint + "&rp=" + config.retentionPolicy();
            }
            return influxEndpoint;
        }

        @Override
        void addHeaderToken(InfluxConfig config, HttpSender.Request.Builder requestBuilder) {
            if (config.token() != null) {
                requestBuilder.withHeader("Authorization", "Bearer " + config.token());
            }
        }
    }
    ,
    V2{

        @Override
        String writeEndpoint(InfluxConfig config) throws UnsupportedEncodingException {
            String bucket = URLEncoder.encode(config.bucket(), "UTF-8");
            String org = URLEncoder.encode(config.org(), "UTF-8");
            return config.uri() + "/api/v2/write?precision=ms&bucket=" + bucket + "&org=" + org;
        }

        @Override
        void addHeaderToken(InfluxConfig config, HttpSender.Request.Builder requestBuilder) {
            if (config.token() != null) {
                requestBuilder.withHeader("Authorization", "Token " + config.token());
            }
        }
    };


    abstract String writeEndpoint(InfluxConfig var1) throws UnsupportedEncodingException;

    abstract void addHeaderToken(InfluxConfig var1, HttpSender.Request.Builder var2);
}


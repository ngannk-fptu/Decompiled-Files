/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.status.service;

import com.atlassian.confluence.status.service.SystemCompatibilityService;
import com.atlassian.core.util.ClassLoaderUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.InitializingBean;

public class DefaultSystemCompatibilityService
implements SystemCompatibilityService,
InitializingBean {
    private Collection<String> javaVersions;
    private Collection<String> javaRuntimes;
    private Collection<String> operatingSystems = new ArrayList<String>();
    private Collection<String> databases = new ArrayList<String>();
    private Collection<String> tomcatVersions = new ArrayList<String>();

    public void afterPropertiesSet() throws Exception {
        JsonObject json = DefaultSystemCompatibilityService.getJsonObject();
        if (json != null) {
            this.javaVersions = DefaultSystemCompatibilityService.extract((Iterable<JsonElement>)json.getAsJsonArray("java.versions"));
            this.javaRuntimes = DefaultSystemCompatibilityService.extract((Iterable<JsonElement>)json.getAsJsonArray("java.runtimes"));
            this.operatingSystems = DefaultSystemCompatibilityService.extract((Iterable<JsonElement>)json.getAsJsonArray("operating.systems"));
            this.databases = DefaultSystemCompatibilityService.extract((Iterable<JsonElement>)json.getAsJsonArray("databases"));
            this.tomcatVersions = DefaultSystemCompatibilityService.extract((Iterable<JsonElement>)json.getAsJsonArray("tomcat.versions"));
        }
    }

    private static Collection<String> extract(Iterable<JsonElement> items) {
        return StreamSupport.stream(items.spliterator(), false).map(JsonElement::getAsString).collect(Collectors.toList());
    }

    @Override
    public Collection<String> getSupportedJavaVersions() {
        return this.javaVersions;
    }

    @Override
    public Collection<String> getSupportedJavaRuntimes() {
        return this.javaRuntimes;
    }

    @Override
    public String getSupportedJavaRuntime() {
        Iterator<String> iterator = this.javaRuntimes.iterator();
        if (iterator.hasNext()) {
            String runtime = iterator.next();
            return runtime;
        }
        return null;
    }

    @Override
    public Collection<String> getSupportedOperatingSystems() {
        return this.operatingSystems;
    }

    @Override
    public Collection<String> getSupportedDatabases() {
        return this.databases;
    }

    @Override
    public Collection<String> getSupportedTomcatVersions() {
        return this.tomcatVersions;
    }

    private static JsonObject getJsonObject() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoaderUtils.getResourceAsStream((String)"supported-applications.json", DefaultSystemCompatibilityService.class)));){
            JsonObject jsonObject = (JsonObject)new JsonParser().parse((Reader)bufferedReader);
            return jsonObject;
        }
    }
}


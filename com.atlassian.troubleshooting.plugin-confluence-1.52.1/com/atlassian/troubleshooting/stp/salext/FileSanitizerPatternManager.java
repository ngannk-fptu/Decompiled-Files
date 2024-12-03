/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.troubleshooting.stp.salext;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class FileSanitizerPatternManager {
    public static final String APPLICATION_XML = "application.xml";
    private static final String SERVER_XML = "server.xml";
    private static final String CROWD_PROPERTIES = "crowd.properties";
    private static final String DIRECTORY_CONFIG_SUMMARY = "directoryConfigurationSummary.txt";
    private static final String TOMCAT_USERS = "tomcat-users.xml";
    private static final String ATLASSIAN_USER = "atlassian-user.xml";
    private static final Pattern TOMCAT_USERS_SANITIZER_PATTERN = Pattern.compile("(?:.*(?:username|password|name)[ ]*=[ ]*[\"']?([^\"'> ]*)[\"']?.*)", 2);
    private final Map<String, List<Pattern>> filesAndPatternsToSanitizeThem = new HashMap<String, List<Pattern>>();

    public FileSanitizerPatternManager() {
        this.initializeCommonSanitizations(this.filesAndPatternsToSanitizeThem);
        this.initializeProductSpecificSanitizations();
    }

    private void initializeCommonSanitizations(Map<String, List<Pattern>> filePatterns) {
        filePatterns.put(SERVER_XML, Lists.newArrayList((Object[])new Pattern[]{Pattern.compile("(?:.*(?:username|password|keystorePass|truststorePass|connectionPassword|connectionName)[ ]*=[ ]*[\"']?([^\"'> ]*)[\"']?.*)", 2)}));
        filePatterns.put(CROWD_PROPERTIES, Lists.newArrayList((Object[])new Pattern[]{Pattern.compile("application\\.password\\s+(.+)\\s*", 2)}));
        filePatterns.put(DIRECTORY_CONFIG_SUMMARY, Lists.newArrayList((Object[])new Pattern[]{Pattern.compile("(?:password.*:)\\s+(.+)", 2)}));
        filePatterns.put(TOMCAT_USERS, Lists.newArrayList((Object[])new Pattern[]{TOMCAT_USERS_SANITIZER_PATTERN}));
        filePatterns.put(ATLASSIAN_USER, Lists.newArrayList((Object[])new Pattern[]{Pattern.compile("(?:securityPrincipal\\s*>)(.*)(?:</\\s*securityPrincipal)"), Pattern.compile("(?:securityCredential\\s*>)(.*)(?:</\\s*securityCredential)")}));
        filePatterns.put(APPLICATION_XML, Lists.newArrayList((Object[])new Pattern[]{Pattern.compile("(?:-D[\\w\\.]*password[\\w\\.]*=)(.*?)(?:\\s-D|\\s-X|<)", 2), Pattern.compile("(?:<.*password.*>)(.*?)(?:</.*password.*>)", 2), Pattern.compile("(?:<.*jdbc_user.*>)(.*?)(?:</.*jdbc_user.*>)", 2)}));
        Stream.concat(this.standardShAndBatFiles(), this.productSpecificShAndBatFiles()).forEach(f -> Stream.of(".sh", ".bat").forEach(suffix -> {
            List cfr_ignored_0 = filePatterns.put(f + suffix, Lists.newArrayList((Object[])new Pattern[]{Pattern.compile("(?:-D[\\w\\.]*password[\\w\\.]*=)(.*?)(?:\\s-D|\\s-X|\")", 2)}));
        }));
    }

    private Stream<String> standardShAndBatFiles() {
        return Stream.of("setenv", "setclasspath", "startup", "shutdown");
    }

    public void addSanitizerPattern(String filename, Pattern ... patterns) {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(patterns);
        if (this.filesAndPatternsToSanitizeThem.putIfAbsent(filename, Lists.newArrayList((Object[])patterns)) != null) {
            this.filesAndPatternsToSanitizeThem.get(filename).addAll(Arrays.asList(patterns));
        }
    }

    public List<Pattern> getSanitizationsForFile(String file) {
        if (file == null) {
            return Lists.newArrayList();
        }
        return this.filesAndPatternsToSanitizeThem.getOrDefault(file, Lists.newArrayList());
    }

    protected abstract void initializeProductSpecificSanitizations();

    protected abstract Stream<String> productSpecificShAndBatFiles();
}


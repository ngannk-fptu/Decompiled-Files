/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.confluence;

import com.atlassian.troubleshooting.stp.salext.FileSanitizerPatternManager;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ConfluenceFileSanitizerPatternManager
extends FileSanitizerPatternManager {
    private static final String CONFLUENCE_CFG_XML = "confluence.cfg.xml";

    @Override
    protected void initializeProductSpecificSanitizations() {
        this.addSanitizerPattern(CONFLUENCE_CFG_XML, Pattern.compile("(?:.*<property name=\"confluence\\.license\\.message\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"hibernate\\.connection\\.username\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"hibernate\\.connection\\.password\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"license\\.string\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"confluence\\.cluster\\.authentication\\.secret\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"synchrony\\.service\\.authtoken\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"jwt\\.private\\.key\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"jwt\\.public\\.key\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"confluence\\.cluster\\.authentication\\.secret\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"atlassian\\.license\\.message\">)(.*)(?:</property>.*)"));
        this.addSanitizerPattern("application.xml", Pattern.compile("<JwtPublicKey>(.*?)</JwtPublicKey>"), Pattern.compile("<JwtPrivateKey>(.*?)</JwtPrivateKey>"), Pattern.compile("<JdbcPassword>(.*?)</JdbcPassword>"), Pattern.compile("<ClusterAuthenticationSecret>(.*?)</ClusterAuthenticationSecret>"), Pattern.compile("<AuthTokens>(.*?)</AuthTokens>"));
    }

    @Override
    protected Stream<String> productSpecificShAndBatFiles() {
        return Stream.of("start-confluence", "stop-confluence");
    }
}


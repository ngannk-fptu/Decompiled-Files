/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tuckey.web.filters.urlrewrite.Condition;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.NormalRule;
import org.tuckey.web.filters.urlrewrite.SetAttribute;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.NumberUtils;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

public class ModRewriteConfLoader {
    private static Log log = Log.getLog(ModRewriteConfLoader.class);
    private final Pattern LOG_LEVEL_PATTERN = Pattern.compile("RewriteLogLevel\\s+([0-9]+)\\s*$");
    private final Pattern LOG_TYPE_PATTERN = Pattern.compile("RewriteLog\\s+(.*)$");
    private final Pattern ENGINE_PATTERN = Pattern.compile("RewriteEngine\\s+([a-zA-Z0-9]+)\\s*$");
    private final Pattern CONDITION_PATTERN = Pattern.compile("RewriteCond\\s+(.*)$");
    private final Pattern RULE_PATTERN = Pattern.compile("RewriteRule\\s+(.*)$");

    public void process(InputStream is, Conf conf) throws IOException {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        while ((line = in.readLine()) != null) {
            buffer.append(line);
            buffer.append("\n");
        }
        this.process(buffer.toString(), conf);
    }

    public void process(String modRewriteStyleConf, Conf conf) {
        String[] lines = modRewriteStyleConf.split("\n");
        ArrayList<Condition> conditionsBuffer = new ArrayList<Condition>();
        StringBuffer notesBuffer = new StringBuffer();
        String logLevelStr = null;
        String logTypeStr = null;
        for (int i = 0; i < lines.length; ++i) {
            String line = StringUtils.trimToNull(lines[i]);
            if (line == null) continue;
            log.debug("processing line: " + line);
            if (line.startsWith("#")) {
                log.debug("adding note line (line starting with #)");
                if (notesBuffer.length() > 0) {
                    notesBuffer.append("\n");
                }
                String noteLine = StringUtils.trim(line.substring(1));
                notesBuffer.append(noteLine);
                continue;
            }
            if (line.startsWith("RewriteBase")) {
                log.info("RewriteBase not supported, ignored");
                continue;
            }
            if (line.startsWith("RewriteCond")) {
                Condition condition = this.processRewriteCond(line);
                if (condition == null) continue;
                conditionsBuffer.add(condition);
                continue;
            }
            if (line.startsWith("RewriteEngine")) {
                this.processRewriteEngine(conf, line);
                continue;
            }
            if (line.startsWith("RewriteLock")) {
                log.error("RewriteLock not supported, ignored");
                continue;
            }
            if (line.startsWith("RewriteLogLevel")) {
                logLevelStr = this.parseLogLevel(logLevelStr, line);
                continue;
            }
            if (line.startsWith("RewriteLog")) {
                logTypeStr = this.parseLogType(logTypeStr, line);
                continue;
            }
            if (line.startsWith("RewriteMap")) {
                log.error("RewriteMap not supported, ignored");
                continue;
            }
            if (line.startsWith("RewriteOptions")) {
                log.error("RewriteOptions not supported, ignored");
                continue;
            }
            if (!line.startsWith("RewriteRule")) continue;
            this.parseRule(conf, conditionsBuffer, notesBuffer, line);
            notesBuffer = new StringBuffer();
            conditionsBuffer = new ArrayList();
        }
        if (logTypeStr != null || logLevelStr != null) {
            String logStr = (logTypeStr == null ? "" : logTypeStr) + (logLevelStr == null ? "" : ":" + logLevelStr);
            log.debug("setting log to: " + logStr);
            Log.setLevel(logStr);
        }
        if (conditionsBuffer.size() > 0) {
            log.error("conditions left over without a rule");
        }
    }

    private void parseRule(Conf conf, List conditionsBuffer, StringBuffer notesBuffer, String line) {
        NormalRule rule = this.processRule(line);
        for (int j = 0; j < conditionsBuffer.size(); ++j) {
            Condition condition = (Condition)conditionsBuffer.get(j);
            rule.addCondition(condition);
        }
        if (notesBuffer.length() > 0) {
            rule.setNote(notesBuffer.toString());
        }
        conf.addRule(rule);
    }

    private String parseLogType(String logTypeStr, String line) {
        Matcher logTypeMatcher = this.LOG_TYPE_PATTERN.matcher(line);
        if (logTypeMatcher.matches() && (logTypeStr = StringUtils.trimToNull(logTypeMatcher.group(1))) != null) {
            logTypeStr = logTypeStr.replaceAll("\"", "");
            log.debug("RewriteLog parsed as " + logTypeStr);
        }
        return logTypeStr;
    }

    private String parseLogLevel(String logLevelStr, String line) {
        log.debug("found a RewriteLogLevel");
        Matcher logLevelMatcher = this.LOG_LEVEL_PATTERN.matcher(line);
        if (logLevelMatcher.matches()) {
            int logLevel = NumberUtils.stringToInt(logLevelMatcher.group(1));
            if (logLevel <= 1) {
                logLevelStr = "FATAL";
            } else if (logLevel == 2) {
                logLevelStr = "ERROR";
            } else if (logLevel == 3) {
                logLevelStr = "INFO";
            } else if (logLevel == 4) {
                logLevelStr = "WARN";
            } else if (logLevel >= 5) {
                logLevelStr = "DEBUG";
            }
            log.debug("RewriteLogLevel parsed as " + logLevel);
        } else {
            log.error("cannot parse " + line);
        }
        return logLevelStr;
    }

    private NormalRule processRule(String line) {
        NormalRule rule = new NormalRule();
        Matcher ruleMatcher = this.RULE_PATTERN.matcher(line);
        if (ruleMatcher.matches()) {
            String rulePartStr = StringUtils.trimToNull(ruleMatcher.group(1));
            if (rulePartStr != null) {
                log.debug("got rule " + rulePartStr);
                String[] ruleParts = rulePartStr.split(" ");
                int partCounter = 0;
                for (int j = 0; j < ruleParts.length; ++j) {
                    String part = StringUtils.trimToNull(ruleParts[j]);
                    if (part == null) continue;
                    log.debug("parsed rule part " + part);
                    if (++partCounter == 1) {
                        rule.setFrom(part);
                    }
                    if (partCounter == 2 && !"-".equals(part)) {
                        rule.setTo(part);
                    }
                    if (!part.startsWith("[") || !part.endsWith("]")) continue;
                    this.processRuleFlags(rule, part);
                }
            } else {
                log.error("could not parse rule from " + line);
            }
        } else {
            log.error("cannot parse " + line);
        }
        return rule;
    }

    private void processRewriteEngine(Conf conf, String line) {
        boolean enabled = true;
        Matcher engineMatcher = this.ENGINE_PATTERN.matcher(line);
        if (engineMatcher.matches()) {
            String enabledStr = StringUtils.trim(engineMatcher.group(1));
            log.debug("RewriteEngine value parsed as '" + enabledStr + "'");
            if ("0".equalsIgnoreCase(enabledStr) || "false".equalsIgnoreCase(enabledStr) || "no".equalsIgnoreCase(enabledStr) || "off".equalsIgnoreCase(enabledStr)) {
                enabled = false;
            }
            log.debug("RewriteEngine as boolean '" + enabled + "'");
        } else {
            log.error("cannot parse " + line);
        }
        conf.setEngineEnabled(enabled);
    }

    private void processRuleFlags(NormalRule rule, String part) {
        String rawFlags = StringUtils.trimToNull(part.substring(1, part.length() - 1));
        if (rawFlags != null) {
            String[] flags = rawFlags.split(",");
            for (int k = 0; k < flags.length; ++k) {
                SetAttribute set;
                String flag = flags[k];
                String flagValue = null;
                if (flag.indexOf("=") != -1) {
                    flagValue = flag.substring(flag.indexOf("=") + 1);
                    flag = flag.substring(0, flag.indexOf("="));
                }
                if ("chain".equalsIgnoreCase(flag = flag.toLowerCase()) || "C".equalsIgnoreCase(flag)) {
                    log.info("chain flag [C] not supported");
                }
                if ("cookie".equalsIgnoreCase(flag) || "CO".equalsIgnoreCase(flag)) {
                    int colon;
                    set = new SetAttribute();
                    set.setType("cookie");
                    String cookieName = flagValue;
                    String cookieValue = null;
                    if (flagValue != null && (colon = flagValue.indexOf(":")) != -1) {
                        cookieValue = flagValue.substring(colon + 1);
                        cookieName = flagValue.substring(0, colon);
                    }
                    set.setName(cookieName);
                    set.setValue(cookieValue);
                    rule.addSetAttribute(set);
                }
                if ("env".equalsIgnoreCase(flag) || "E".equalsIgnoreCase(flag)) {
                    log.info("env flag [E] not supported");
                }
                if ("forbidden".equalsIgnoreCase(flag) || "F".equalsIgnoreCase(flag)) {
                    set = new SetAttribute();
                    set.setType("status");
                    set.setValue("403");
                    rule.addSetAttribute(set);
                }
                if ("gone".equalsIgnoreCase(flag) || "G".equalsIgnoreCase(flag)) {
                    set = new SetAttribute();
                    set.setType("status");
                    set.setValue("410");
                    rule.addSetAttribute(set);
                }
                if ("last".equalsIgnoreCase(flag) || "L".equalsIgnoreCase(flag)) {
                    rule.setToLast("true");
                }
                if ("next".equalsIgnoreCase(flag) || "N".equalsIgnoreCase(flag)) {
                    log.info("next flag [N] not supported");
                }
                if ("nocase".equalsIgnoreCase(flag) || "NC".equalsIgnoreCase(flag)) {
                    rule.setFromCaseSensitive(false);
                }
                if ("noescape".equalsIgnoreCase(flag) || "NE".equalsIgnoreCase(flag)) {
                    rule.setEncodeToUrl(false);
                }
                if ("nosubreq".equalsIgnoreCase(flag) || "NS".equalsIgnoreCase(flag)) {
                    log.info("nosubreq flag [NS] not supported");
                }
                if ("proxy".equalsIgnoreCase(flag) || "P".equalsIgnoreCase(flag)) {
                    rule.setToType("proxy");
                }
                if ("passthrough".equalsIgnoreCase(flag) || "PT".equalsIgnoreCase(flag)) {
                    rule.setToType("forward");
                }
                if ("qsappend".equalsIgnoreCase(flag) || "QSA".equalsIgnoreCase(flag)) {
                    log.info("qsappend flag [QSA] not supported");
                }
                if ("redirect".equalsIgnoreCase(flag) || "R".equalsIgnoreCase(flag)) {
                    if ("301".equals(flagValue)) {
                        rule.setToType("permanent-redirect");
                    } else if ("302".equals(flagValue)) {
                        rule.setToType("temporary-redirect");
                    } else {
                        rule.setToType("redirect");
                    }
                }
                if ("skip".equalsIgnoreCase(flag) || "S".equalsIgnoreCase(flag)) {
                    log.info("Skip flag [S] not supported");
                }
                if (!"type".equalsIgnoreCase(flag) && !"T".equalsIgnoreCase(flag)) continue;
                set = new SetAttribute();
                set.setType("content-type");
                set.setValue(flagValue);
                rule.addSetAttribute(set);
            }
        } else {
            log.error("cannot parse flags from " + part);
        }
    }

    private Condition processRewriteCond(String rewriteCondLine) {
        log.debug("about to parse condition");
        Condition condition = new Condition();
        Matcher condMatcher = this.CONDITION_PATTERN.matcher(rewriteCondLine);
        if (condMatcher.matches()) {
            String conditionParts = StringUtils.trimToNull(condMatcher.group(1));
            if (conditionParts != null) {
                String[] condParts = conditionParts.split(" ");
                for (int i = 0; i < condParts.length; ++i) {
                    String part = StringUtils.trimToNull(condParts[i]);
                    if (part == null) continue;
                    if (part.equalsIgnoreCase("%{HTTP_USER_AGENT}")) {
                        condition.setType("header");
                        condition.setName("user-agent");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{HTTP_REFERER}")) {
                        condition.setType("header");
                        condition.setName("referer");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{HTTP_COOKIE}")) {
                        condition.setType("header");
                        condition.setName("cookie");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{HTTP_FORWARDED}")) {
                        condition.setType("header");
                        condition.setName("forwarded");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{HTTP_PROXY_CONNECTION}")) {
                        condition.setType("header");
                        condition.setName("proxy-connection");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{HTTP_ACCEPT}")) {
                        condition.setType("header");
                        condition.setName("accept");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{HTTP_HOST}")) {
                        condition.setType("server-name");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{REMOTE_ADDR}")) {
                        condition.setType("remote-addr");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{REMOTE_HOST}")) {
                        condition.setType("remote-host");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{REMOTE_USER}")) {
                        condition.setType("remote-user");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{REQUEST_METHOD}")) {
                        condition.setType("method");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{QUERY_STRING}")) {
                        condition.setType("query-string");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{TIME_YEAR}")) {
                        condition.setType("year");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{TIME_MON}")) {
                        condition.setType("month");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{TIME_DAY}")) {
                        condition.setType("dayofmonth");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{TIME_WDAY}")) {
                        condition.setType("dayofweek");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{TIME_HOUR}")) {
                        condition.setType("hourofday");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{TIME_MIN}")) {
                        condition.setType("minute");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{TIME_SEC}")) {
                        condition.setType("second");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{PATH_INFO}")) {
                        condition.setType("path-info");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{AUTH_TYPE}")) {
                        condition.setType("auth-type");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{SERVER_PORT}")) {
                        condition.setType("port");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{REQUEST_URI}")) {
                        condition.setType("request-uri");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{REQUEST_FILENAME}")) {
                        condition.setType("request-filename");
                        continue;
                    }
                    if (part.equals("-f") || part.equals("-F")) {
                        condition.setOperator("isfile");
                        continue;
                    }
                    if (part.equals("-d")) {
                        condition.setOperator("isdir");
                        continue;
                    }
                    if (part.equalsIgnoreCase("-s")) {
                        condition.setOperator("isfilewithsize");
                        continue;
                    }
                    if (part.equals("!-f") || part.equals("!-F")) {
                        condition.setOperator("notfile");
                        continue;
                    }
                    if (part.equals("!-d")) {
                        condition.setOperator("notdir");
                        continue;
                    }
                    if (part.equalsIgnoreCase("!-s")) {
                        condition.setOperator("notfilewithsize");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{REMOTE_PORT}")) {
                        log.error("REMOTE_PORT currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{REMOTE_IDENT}")) {
                        log.error("REMOTE_IDENT currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{SCRIPT_FILENAME}")) {
                        log.error("SCRIPT_FILENAME currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{DOCUMENT_ROOT}")) {
                        log.error("DOCUMENT_ROOT currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{SERVER_ADMIN}")) {
                        log.error("SERVER_ADMIN currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{SERVER_NAME}")) {
                        log.error("SERVER_NAME currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{SERVER_ADDR}")) {
                        log.error("SERVER_ADDR currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{SERVER_PROTOCOL}")) {
                        log.error("SERVER_PROTOCOL currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{SERVER_SOFTWARE}")) {
                        log.error("SERVER_SOFTWARE currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{TIME}")) {
                        log.error("TIME currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{API_VERSION}")) {
                        log.error("API_VERSION currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{THE_REQUEST}")) {
                        log.error("THE_REQUEST currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{IS_SUBREQ}")) {
                        log.error("IS_SUBREQ currently unsupported, ignoring");
                        continue;
                    }
                    if (part.equalsIgnoreCase("%{HTTPS}")) {
                        log.error("HTTPS currently unsupported, ignoring");
                        continue;
                    }
                    condition.setValue(part);
                }
            } else {
                log.error("could not parse condition from " + rewriteCondLine);
            }
        } else {
            log.error("cannot parse " + rewriteCondLine);
        }
        return condition;
    }
}


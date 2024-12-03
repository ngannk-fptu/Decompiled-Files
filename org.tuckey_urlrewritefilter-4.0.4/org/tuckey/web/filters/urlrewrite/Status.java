/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tuckey.web.filters.urlrewrite.CatchElem;
import org.tuckey.web.filters.urlrewrite.ClassRule;
import org.tuckey.web.filters.urlrewrite.Condition;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.NormalRule;
import org.tuckey.web.filters.urlrewrite.OutboundRule;
import org.tuckey.web.filters.urlrewrite.Rule;
import org.tuckey.web.filters.urlrewrite.RuleBase;
import org.tuckey.web.filters.urlrewrite.Run;
import org.tuckey.web.filters.urlrewrite.SetAttribute;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

public class Status {
    private static Log log = Log.getLog(Status.class);
    private StringBuffer buffer = new StringBuffer();
    private Conf conf;
    private UrlRewriteFilter urlRewriteFilter;

    public Status(Conf conf) {
        this.conf = conf;
    }

    public Status(Conf conf, UrlRewriteFilter urlRewriteFilter) {
        this.conf = conf;
        this.urlRewriteFilter = urlRewriteFilter;
    }

    public void displayStatusInContainer(HttpServletRequest hsRequest) {
        this.showHeader();
        this.showRunningInfo();
        this.showConf();
        this.showRequestInfo(hsRequest);
        this.showFooter();
    }

    public void displayStatusOffline() {
        this.showHeader();
        this.showConf();
        this.showFooter();
    }

    private void showRequestInfo(HttpServletRequest hsRequest) {
        this.println("<h2>Request Debug Info</h2>");
        this.println("<h4>General</h4>");
        this.println("<pre>");
        this.println("method: " + hsRequest.getMethod());
        if (hsRequest.getAuthType() != null) {
            this.println("auth-type: " + hsRequest.getAuthType());
        }
        if (hsRequest.getCharacterEncoding() != null) {
            this.println("character-encoding: " + hsRequest.getCharacterEncoding());
        }
        this.println("context-path: " + hsRequest.getContextPath());
        if (hsRequest.getPathInfo() != null) {
            this.println("path-info: " + hsRequest.getPathInfo());
        }
        if (hsRequest.getPathTranslated() != null) {
            this.println("path-translated: " + hsRequest.getPathTranslated());
        }
        this.println("port: " + hsRequest.getServerPort());
        this.println("protocol: " + hsRequest.getProtocol());
        if (hsRequest.getQueryString() != null) {
            this.println("query-string: " + hsRequest.getQueryString());
        }
        this.println("remote-addr: " + hsRequest.getRemoteAddr());
        this.println("remote-host: " + hsRequest.getRemoteHost());
        if (hsRequest.getRemoteUser() != null) {
            this.println("remote-user: " + hsRequest.getRemoteUser());
        }
        if (hsRequest.getRequestedSessionId() != null) {
            this.println("requested-session-id: " + hsRequest.getRequestedSessionId());
        }
        this.println("request-uri: " + hsRequest.getRequestURI());
        this.println("request-url: " + hsRequest.getRequestURL());
        this.println("server-name: " + hsRequest.getServerName());
        this.println("scheme: " + hsRequest.getScheme());
        this.println("</pre>");
        HttpSession session = hsRequest.getSession(false);
        if (session != null) {
            this.println("<h4>Session</h4>");
            this.println("<br />session-isnew: " + session.isNew());
            Enumeration enumer = session.getAttributeNames();
            while (enumer.hasMoreElements()) {
                String name = (String)enumer.nextElement();
                this.println("<br />session-attribute " + name + ": " + session.getAttribute(name));
            }
        }
        this.println("<h4>Request Headers</h4>");
        this.println("<pre>");
        Enumeration headers = hsRequest.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = (String)headers.nextElement();
            if ("cookie".equals(headerName)) continue;
            this.println(headerName + ": " + hsRequest.getHeader(headerName));
        }
        this.println("</pre>");
        Cookie[] cookies = hsRequest.getCookies();
        if (cookies != null && cookies.length > 0) {
            this.println("<h4>Cookies</h4>");
            for (int i = 0; i < cookies.length; ++i) {
                this.println("<h5>Cookie " + i + "</h5>");
                Cookie cookie = cookies[i];
                if (cookie == null) continue;
                this.println("<pre>");
                this.println("    name     : " + cookie.getName());
                this.println("    value    : " + cookie.getValue());
                this.println("    path     : " + cookie.getPath());
                this.println("    domain   : " + cookie.getDomain());
                this.println("    max age  : " + cookie.getMaxAge());
                this.println("    is secure: " + cookie.getSecure());
                this.println("    version  : " + cookie.getVersion());
                this.println("    comment  : " + cookie.getComment());
                this.println("</pre>");
            }
        }
        this.println("<h4>Time info</h4>");
        this.println("<pre>");
        Calendar nowCal = Calendar.getInstance();
        this.println("time: " + nowCal.getTime().getTime());
        this.println("year: " + nowCal.get(1));
        this.println("month: " + nowCal.get(2));
        this.println("dayofmonth: " + nowCal.get(5));
        this.println("dayofweek: " + nowCal.get(7));
        this.println("ampm: " + nowCal.get(9));
        this.println("hourofday: " + nowCal.get(11));
        this.println("minute: " + nowCal.get(12));
        this.println("second: " + nowCal.get(13));
        this.println("millisecond: " + nowCal.get(14));
        this.println("</pre>");
    }

    private void showConf() {
        if (this.conf == null) {
            return;
        }
        this.println("<h2>Summary");
        if (this.conf.isLoadedFromFile()) {
            this.println(" of " + this.conf.getFileName());
        }
        this.println("</h2>");
        if (!this.conf.isOk()) {
            List errors = this.conf.getErrors();
            this.println("<h4 class=\"err\">Errors During Load of " + this.conf.getFileName() + "</h4>");
            this.println("<ul>");
            if (errors.size() > 0) {
                for (int i = 0; i < errors.size(); ++i) {
                    String error = (String)errors.get(i);
                    this.println("<li class=\"err\">" + error + "</li>");
                }
            }
            this.displayRuleErrors(this.conf.getRules());
            this.displayRuleErrors(this.conf.getOutboundRules());
            this.displayCatchErrors(this.conf.getCatchElems());
            this.println("</ul>");
        }
        int conditionsCount = 0;
        List rules = this.conf.getRules();
        for (int i = 0; i < rules.size(); ++i) {
            Rule rule = (Rule)rules.get(i);
            if (!(rule instanceof NormalRule)) continue;
            conditionsCount += ((NormalRule)rule).getConditions().size();
        }
        List outboundRules = this.conf.getOutboundRules();
        for (int i = 0; i < outboundRules.size(); ++i) {
            OutboundRule rule = (OutboundRule)outboundRules.get(i);
            conditionsCount += rule.getConditions().size();
        }
        this.println("<p>In total there " + (rules.size() == 1 ? "is 1 rule" : "are " + rules.size() + " rules") + ", " + (outboundRules.size() == 1 ? "1 outbound rule" : outboundRules.size() + " outbound rules") + (conditionsCount > 0 ? " and " : "") + (conditionsCount == 1 ? conditionsCount + " condition" : "") + (conditionsCount > 1 ? conditionsCount + " conditions" : "") + " in the configuration file.</p>");
        this.showRules(rules);
        this.showOutboundRules(outboundRules);
        this.println("<hr />");
    }

    private void showRules(List rules) {
        for (int i = 0; i < rules.size(); ++i) {
            Rule rule = (Rule)rules.get(i);
            if (rule instanceof NormalRule) {
                NormalRule normalRule = (NormalRule)rule;
                this.println("<h3>" + normalRule.getDisplayName() + (normalRule.isEnabled() ? "" : " **DISABLED**") + "</h3>");
                if (!StringUtils.isBlank(normalRule.getNote())) {
                    this.println("<dl><dd><p>" + StringUtils.nl2br(normalRule.getNote()) + "</p></dd></dl>");
                }
                this.print("<p>URL's matching <code>" + normalRule.getFrom() + "</code>");
                if (normalRule.isFilter()) {
                    this.print(" (filter)");
                }
                if (!StringUtils.isBlank(normalRule.getTo())) {
                    this.print(" will ");
                    if ("forward".equals(normalRule.getToType())) {
                        this.print("be <code>forwarded</code> to");
                    } else if ("include".equals(normalRule.getToType())) {
                        this.print("<code>include</code>");
                    } else if ("redirect".equals(normalRule.getToType())) {
                        this.print("be <code>redirected</code> to");
                    } else {
                        this.print("<code>" + normalRule.getToType() + "</code> to");
                    }
                    this.print(" <code>" + normalRule.getTo() + "</code>");
                }
                this.println(".</p>");
                this.print("<p>This rule and it's conditions will use the <code>" + normalRule.getMatchType() + "</code> matching engine.</p>");
                this.showConditions(normalRule);
                this.showSets(normalRule);
                this.showRuns(normalRule);
                if (!rule.isLast()) {
                    this.println("<p>Note, other rules will be processed after this rule.</p>");
                }
            }
            if (rule instanceof ClassRule) {
                ClassRule classRule = (ClassRule)rule;
                this.println("<h3>" + classRule.getDisplayName() + (classRule.isEnabled() ? "" : " **DISABLED**") + "</h3>");
            }
            this.println();
            this.println();
        }
    }

    private void showOutboundRules(List outboundRules) {
        for (int i = 0; i < outboundRules.size(); ++i) {
            OutboundRule rule = (OutboundRule)outboundRules.get(i);
            this.println("<h3>" + rule.getDisplayName() + (rule.isEnabled() ? "" : " **DISABLED**") + "</h3>");
            if (!StringUtils.isBlank(rule.getNote())) {
                this.println("<dl><dd><p>" + StringUtils.nl2br(rule.getNote()) + "</p></dd></dl>");
            }
            this.print("<p>Outbound URL's matching <code>" + rule.getFrom() + "</code>");
            if (!StringUtils.isBlank(rule.getTo())) {
                this.print(" will be rewritten to <code>" + rule.getTo() + "</code>");
            }
            if (!rule.isEncodeFirst()) {
                this.print(", after <code>response.encodeURL()</code> has been called");
            }
            if (!rule.isEncodeToUrl()) {
                this.print(", <code>response.encodeURL()</code> will not be called");
            }
            this.println(".</p>");
            this.showConditions(rule);
            this.showSets(rule);
            this.showRuns(rule);
            if (!rule.isLast()) {
                this.println("<p>Note, other outbound rules will be processed after this rule.</p>");
            }
            this.println();
            this.println();
        }
    }

    private void showHeader() {
        SimpleDateFormat s = new SimpleDateFormat();
        this.println("<!DOCTYPE html>");
        this.println("<html lang=\"en\">");
        this.println("<head>");
        this.println("<title>UrlRewriteFilter configuration overview for " + this.conf.getFileName() + "</title>");
        this.println("<style type=\"text/css\">");
        InputStream is = Status.class.getResourceAsStream("doc/doc.css");
        if (is == null) {
            log.warn("unable to load style sheet");
        } else {
            try {
                int i = is.read();
                while (i != -1) {
                    this.buffer.append((char)i);
                    i = is.read();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.println("</style>");
        this.println("<body>");
        this.println("<h1><a href=\"http://www.tuckey.org/urlrewrite/\">UrlRewriteFilter</a> " + UrlRewriteFilter.getFullVersionString() + " configuration overview " + "(generated " + s.format(new Date()) + ")</h1>");
        this.println("<hr />");
    }

    private void showRunningInfo() {
        this.println("<h2>Running Status</h2>");
        if (this.conf == null) {
            this.println("<h3 class=\"err\">ERROR: UrlRewriteFilter failed to load config, check server log</h3>");
        }
        if (!this.conf.isOk()) {
            this.println("<h3 class=\"err\">ERROR: UrlRewriteFilter NOT ACTIVE</h3>");
        }
        this.println("<p>Conf");
        if (this.conf.isLoadedFromFile()) {
            this.println("file <code>" + this.conf.getFileName() + "</code>");
        }
        this.println("loaded <em>" + this.conf.getLoadedDate() + "</em>.</p>");
        if (this.urlRewriteFilter != null) {
            if (this.urlRewriteFilter.isConfReloadCheckEnabled()) {
                Date nextReloadCheckDate = new Date(this.urlRewriteFilter.getConfReloadLastCheck().getTime() + (long)this.urlRewriteFilter.getConfReloadCheckInterval() * 1000L);
                this.println("<p>Conf file reload check <em>enabled</em>, last modified will be checked every <em>" + this.urlRewriteFilter.getConfReloadCheckInterval() + "s</em>, last checked <em>" + this.urlRewriteFilter.getConfReloadLastCheck() + "</em>, next check at <em>" + nextReloadCheckDate + "</em> in <em>" + Math.round((double)(nextReloadCheckDate.getTime() - System.currentTimeMillis()) / 1000.0) + "s</em>.");
            } else {
                this.println("Conf file reload check <em>disabled</em>");
            }
            this.println("<p>Status path <code>" + this.urlRewriteFilter.getStatusPath() + "</code>.</p>");
        }
    }

    private void displayRuleErrors(List rules) {
        for (int i = 0; i < rules.size(); ++i) {
            OutboundRule outboundRule;
            int j;
            List ruleErrors;
            Object ruleObj = rules.get(i);
            if (ruleObj instanceof Rule) {
                Rule rule = (Rule)rules.get(i);
                if (rule.isValid()) continue;
                this.println("<li class=\"err\">Error in " + rule.getDisplayName());
                this.println("<ul>");
                ruleErrors = rule.getErrors();
                for (j = 0; j < ruleErrors.size(); ++j) {
                    this.println("<li class=\"err\">" + ruleErrors.get(j) + "</li>");
                }
                if (rule instanceof NormalRule) {
                    NormalRule normalRule = (NormalRule)rule;
                    List conditions = normalRule.getConditions();
                    List sets = normalRule.getSetAttributes();
                    List runs = normalRule.getRuns();
                    this.displayRuleCondSetRun(conditions, sets, runs);
                }
                this.println("</ul></li>");
            }
            if (!(ruleObj instanceof OutboundRule) || (outboundRule = (OutboundRule)rules.get(i)).isValid()) continue;
            this.println("<li class=\"err\">Error in " + outboundRule.getDisplayName());
            this.println("<ul>");
            ruleErrors = outboundRule.getErrors();
            for (j = 0; j < ruleErrors.size(); ++j) {
                this.println("<li class=\"err\">" + ruleErrors.get(j) + "</li>");
            }
            List conditions = outboundRule.getConditions();
            List sets = outboundRule.getSetAttributes();
            List runs = outboundRule.getRuns();
            this.displayRuleCondSetRun(conditions, sets, runs);
            this.println("</ul></li>");
        }
    }

    private void displayRuleCondSetRun(List conditions, List sets, List runs) {
        int j;
        for (j = 0; j < conditions.size(); ++j) {
            Condition condition = (Condition)conditions.get(j);
            if (condition.getError() == null) continue;
            this.println("<li class=\"err\">" + condition.getDisplayName() + " " + condition.getError() + "</li>");
        }
        for (j = 0; j < sets.size(); ++j) {
            SetAttribute setAttribute = (SetAttribute)sets.get(j);
            if (setAttribute.getError() == null) continue;
            this.println("<li class=\"err\">" + setAttribute.getDisplayName() + " " + setAttribute.getError() + "</li>");
        }
        for (j = 0; j < runs.size(); ++j) {
            Run run = (Run)runs.get(j);
            if (run.getError() == null) continue;
            this.println("<li class=\"err\">" + run.getDisplayName() + " " + run.getError() + "</li>");
        }
    }

    private void displayCatchErrors(List catchElems) {
        for (int i = 0; i < catchElems.size(); ++i) {
            CatchElem catchElem = (CatchElem)catchElems.get(i);
            if (catchElem.isValid()) continue;
            this.println("<li class=\"err\">Error in catch for " + catchElem.getClass() + "</li>");
            this.println("<ul>");
            List runs = catchElem.getRuns();
            for (int j = 0; j < runs.size(); ++j) {
                Run run = (Run)runs.get(j);
                if (run.getError() == null) continue;
                this.println("<li class=\"err\">" + run.getDisplayName() + " " + run.getError() + "</li>");
            }
            this.println("</ul></li>");
        }
    }

    private void showSets(RuleBase rule) {
        if (rule.getSetAttributes().size() == 0) {
            return;
        }
        List setAttributes = rule.getSetAttributes();
        this.println("<p>This rule will set:</p><ol>");
        for (int j = 0; j < setAttributes.size(); ++j) {
            SetAttribute setAttribute = (SetAttribute)setAttributes.get(j);
            this.println("<li>");
            if ("response-header".equals(setAttribute.getType())) {
                this.println("The <code>" + setAttribute.getName() + "</code> HTTP response header " + "to <code>" + setAttribute.getValue() + "</code>");
            } else if ("request".equals(setAttribute.getType()) || "session".equals(setAttribute.getType())) {
                this.println("An attribute on the <code>" + setAttribute.getType() + "</code> object " + "called <code>" + setAttribute.getName() + "</code> " + "to the value " + "<code>" + setAttribute.getValue() + "</code>");
            } else if ("cookie".equals(setAttribute.getType())) {
                this.println("A cookie called <code>" + setAttribute.getName() + "</code> " + " to the value " + "<code>" + setAttribute.getValue() + "</code>");
            } else if ("locale".equals(setAttribute.getType())) {
                this.println("locale to <code>" + setAttribute.getValue() + "</code>");
            } else if ("status".equals(setAttribute.getType())) {
                this.println("status to <code>" + setAttribute.getValue() + "</code>");
            } else if ("content-type".equals(setAttribute.getType())) {
                this.println("content-type to <code>" + setAttribute.getValue() + "</code>");
            } else if ("charset".equals(setAttribute.getType())) {
                this.println("charset to <code>" + setAttribute.getValue() + "</code>");
            }
            this.println("</li>");
        }
        this.println("</ol>");
    }

    private void showRuns(RuleBase rule) {
        List runs = rule.getRuns();
        if (runs.size() == 0) {
            return;
        }
        this.println("<p>This rule will run:</p><ol>");
        for (int j = 0; j < runs.size(); ++j) {
            Run run = (Run)runs.get(j);
            this.println("<li>");
            this.println(" <code>" + run.getMethodSignature() + "</code> on an instance " + "of " + "<code>" + run.getClassStr() + "</code>");
            if (run.isNewEachTime()) {
                this.println(" (a new instance will be created for each rule match)");
            }
            this.println("</li>");
        }
        this.println("</ol>");
        this.println("<small>Note, if <code>init(ServletConfig)</code> or <code>destroy()</code> is found on the above object" + (runs.size() > 1 ? "s" : "") + " they will be run at when creating or destroying an instance.</small>");
    }

    private void showConditions(RuleBase rule) {
        List conditions = rule.getConditions();
        if (conditions.size() == 0) {
            return;
        }
        this.println("<p>Given that the following condition" + (conditions.size() == 1 ? " is" : "s are") + " met.</p>" + "<ol>");
        for (int j = 0; j < conditions.size(); ++j) {
            Condition condition = (Condition)conditions.get(j);
            this.println("<li>");
            if ("header".equals(condition.getType())) {
                this.println("The <code>" + condition.getName() + "</code> HTTP header " + ("notequal".equals(condition.getOperator()) ? "does NOT match" : "matches") + " the value " + "<code>" + condition.getValue() + "</code>");
            } else {
                this.println("<code>" + condition.getType() + "</code> " + (condition.getName() == null ? "" : "<code>" + condition.getName() + "</code> ") + "is <code>" + ("greater".equals(condition.getOperator()) ? "greater than" : "") + ("less".equals(condition.getOperator()) ? "less than" : "") + ("equal".equals(condition.getOperator()) ? "equal to" : "") + ("notequal".equals(condition.getOperator()) ? "NOT equal to" : "") + ("greaterorequal".equals(condition.getOperator()) ? "is greater than or equal to" : "") + ("lessorequal".equals(condition.getOperator()) ? "is less than or equal to" : "") + "</code> the value <code>" + (StringUtils.isBlank(condition.getValue()) ? condition.getName() : condition.getValue()) + "</code>");
            }
            if (j < conditions.size() - 1) {
                this.println("<code>" + condition.getNext() + "</code>");
            }
            this.println("</li>");
        }
        this.println("</ol>");
    }

    private void showFooter() {
        this.println("<br /><br /><br />");
        this.println("</body>");
        this.println("</html>");
    }

    private void println() {
        this.buffer.append("\n");
    }

    private void print(String s) {
        this.buffer.append(s);
    }

    private void println(String s) {
        this.buffer.append(s);
        this.println();
    }

    public StringBuffer getBuffer() {
        return this.buffer;
    }
}


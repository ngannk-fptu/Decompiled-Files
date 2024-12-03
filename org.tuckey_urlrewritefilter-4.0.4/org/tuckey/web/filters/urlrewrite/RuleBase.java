/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.Condition;
import org.tuckey.web.filters.urlrewrite.ConditionMatch;
import org.tuckey.web.filters.urlrewrite.RuleChain;
import org.tuckey.web.filters.urlrewrite.RuleExecutionOutput;
import org.tuckey.web.filters.urlrewrite.Run;
import org.tuckey.web.filters.urlrewrite.Runnable;
import org.tuckey.web.filters.urlrewrite.SetAttribute;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.substitution.BackReferenceReplacer;
import org.tuckey.web.filters.urlrewrite.substitution.ChainedSubstitutionFilters;
import org.tuckey.web.filters.urlrewrite.substitution.FunctionReplacer;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;
import org.tuckey.web.filters.urlrewrite.substitution.VariableReplacer;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.RegexPattern;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingPattern;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingPatternSyntaxException;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;
import org.tuckey.web.filters.urlrewrite.utils.WildcardPattern;

public class RuleBase
implements Runnable {
    private static Log log = Log.getLog(RuleBase.class);
    private static String DEFAULT_RULE_FROM = "^(.*)$";
    protected int id;
    private boolean enabled = true;
    private boolean fromCaseSensitive;
    protected boolean initialised;
    protected boolean valid;
    protected String name;
    private String note;
    protected String from;
    protected String to;
    private boolean toEmpty;
    private String matchType;
    private boolean last = false;
    private int conditionIdCounter;
    private int runIdCounter;
    private StringMatchingPattern pattern;
    protected final List errors = new ArrayList(5);
    private final List conditions = new ArrayList(5);
    private final List runs = new ArrayList(2);
    protected final List setAttributes = new ArrayList(2);
    private boolean stopFilterChainOnMatch = false;
    private boolean noSubstitution = false;
    private boolean toContainsVariable = false;
    private boolean toContainsBackReference = false;
    private boolean toContainsFunction = false;
    public static final String MATCH_TYPE_WILDCARD = "wildcard";
    public static final String DEFAULT_MATCH_TYPE = "regex";
    private boolean filter = false;
    private ServletContext servletContext;

    protected RuleExecutionOutput matchesBase(String url, HttpServletRequest hsRequest, HttpServletResponse hsResponse, RuleChain chain) throws IOException, ServletException, InvocationTargetException {
        if (log.isDebugEnabled()) {
            String displayName = this.getDisplayName();
            log.debug(displayName + " run called with " + url);
        }
        if (!this.initialised) {
            log.debug("not initialised, skipping");
            return null;
        }
        if (!this.valid) {
            log.debug("not valid, skipping");
            return null;
        }
        if (!this.enabled) {
            log.debug("not enabled, skipping");
            return null;
        }
        if (url == null) {
            log.debug("url is null (maybe because of a previous match), skipping");
            return null;
        }
        StringMatchingMatcher matcher = this.pattern.matcher(url);
        boolean performToReplacement = false;
        if (this.toEmpty || this.stopFilterChainOnMatch) {
            if (!matcher.find()) {
                if (log.isTraceEnabled()) {
                    log.trace("no match on \"from\" (to is empty)");
                }
                return null;
            }
        } else {
            if (!matcher.find()) {
                if (log.isTraceEnabled()) {
                    log.trace("no match on \"from\" for " + this.from + " and " + url);
                }
                return null;
            }
            if (!this.toEmpty && !this.noSubstitution) {
                performToReplacement = true;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("matched \"from\"");
        }
        int conditionsSize = this.conditions.size();
        ConditionMatch lastConditionMatch = null;
        if (conditionsSize > 0) {
            boolean processNextOr = false;
            boolean currentResult = true;
            for (int i = 0; i < conditionsSize; ++i) {
                boolean conditionMatches;
                Condition condition = (Condition)this.conditions.get(i);
                ConditionMatch conditionMatch = condition.getConditionMatch(hsRequest);
                if (conditionMatch != null) {
                    lastConditionMatch = conditionMatch;
                }
                boolean bl = conditionMatches = conditionMatch != null;
                currentResult = processNextOr ? (currentResult |= conditionMatches) : (currentResult &= conditionMatches);
                processNextOr = condition.isProcessNextOr();
            }
            if (!currentResult) {
                log.debug("conditions do not match");
                return null;
            }
            log.debug("conditions match");
        }
        hsRequest.setAttribute("org.tuckey.web.filters.urlrewrite.RuleMatched", (Object)Boolean.TRUE);
        int setAttributesSize = this.setAttributes.size();
        if (setAttributesSize > 0) {
            log.trace("setting attributes");
            for (int i = 0; i < setAttributesSize; ++i) {
                SetAttribute setAttribute = (SetAttribute)this.setAttributes.get(i);
                setAttribute.execute(lastConditionMatch, matcher, hsRequest, hsResponse);
            }
        }
        int runsSize = this.runs.size();
        RewriteMatch lastRunMatch = null;
        if (runsSize > 0) {
            log.trace("performing runs");
            for (int i = 0; i < runsSize; ++i) {
                Run run = (Run)this.runs.get(i);
                lastRunMatch = run.execute(hsRequest, hsResponse, matcher, lastConditionMatch, chain);
            }
        }
        String replacedTo = null;
        if (performToReplacement && this.to != null) {
            SubstitutionContext substitutionContext = new SubstitutionContext(hsRequest, matcher, lastConditionMatch, this.to);
            SubstitutionFilterChain substitutionFilter = ChainedSubstitutionFilters.getDefaultSubstitutionChain(true, this.toContainsFunction, this.toContainsVariable, this.toContainsBackReference, this.servletContext);
            replacedTo = substitutionFilter.substitute(url, substitutionContext);
        }
        RuleExecutionOutput ruleExecutionOutput = new RuleExecutionOutput(replacedTo, true, lastRunMatch);
        if (this.toEmpty) {
            log.debug("'to' is empty, no rewrite, only 'set' and or 'run'");
            return null;
        }
        if (this.noSubstitution) {
            log.debug("'to' is '-', no substitution, passing through URL");
            ruleExecutionOutput.setNoSubstitution(true);
            ruleExecutionOutput.setReplacedUrl(url);
        }
        if (this.stopFilterChainOnMatch) {
            ruleExecutionOutput.setStopFilterMatch(true);
            ruleExecutionOutput.setReplacedUrl(null);
        }
        return ruleExecutionOutput;
    }

    public String getDisplayName() {
        return null;
    }

    public boolean initialise(ServletContext context) {
        int i;
        this.servletContext = context;
        this.initialised = true;
        boolean ok = true;
        for (i = 0; i < this.conditions.size(); ++i) {
            Condition condition = (Condition)this.conditions.get(i);
            condition.setRule(this);
            if (condition.initialise()) continue;
            ok = false;
        }
        for (i = 0; i < this.runs.size(); ++i) {
            Run run = (Run)this.runs.get(i);
            if (!run.initialise(context)) {
                ok = false;
            }
            if (!run.isFilter()) continue;
            log.debug("rule is a filtering rule");
            this.filter = true;
        }
        for (i = 0; i < this.setAttributes.size(); ++i) {
            SetAttribute setAttribute = (SetAttribute)this.setAttributes.get(i);
            if (setAttribute.initialise()) continue;
            ok = false;
        }
        if (!this.isMatchTypeWildcard()) {
            this.matchType = DEFAULT_MATCH_TYPE;
        }
        if (StringUtils.isBlank(this.from)) {
            log.debug("rule's from is blank, setting to " + DEFAULT_RULE_FROM);
            this.from = DEFAULT_RULE_FROM;
        }
        try {
            if (this.isMatchTypeWildcard()) {
                log.debug("rule match type is wildcard");
                this.pattern = new WildcardPattern(this.from);
            } else {
                this.pattern = new RegexPattern(this.from, this.fromCaseSensitive);
            }
        }
        catch (StringMatchingPatternSyntaxException e) {
            this.addError("from (" + this.from + ") is an invalid expression - " + e.getMessage());
        }
        if (StringUtils.isBlank(this.to) && this.setAttributes.size() == 0 && this.runs.size() == 0) {
            this.addError("to is not valid because it is blank (it is allowed to be blank when there is a 'set' specified)");
        } else if ("null".equalsIgnoreCase(this.to)) {
            this.stopFilterChainOnMatch = true;
        } else if ("-".equals(this.to)) {
            this.noSubstitution = true;
        } else if (StringUtils.isBlank(this.to)) {
            this.toEmpty = true;
        } else if (!StringUtils.isBlank(this.to)) {
            if (BackReferenceReplacer.containsBackRef(this.to)) {
                this.toContainsBackReference = true;
            }
            if (VariableReplacer.containsVariable(this.to)) {
                this.toContainsVariable = true;
            }
            if (FunctionReplacer.containsFunction(this.to)) {
                this.toContainsFunction = true;
            }
        }
        if (ok) {
            log.debug("loaded rule " + this.getFullDisplayName());
        } else {
            log.debug("failed to load rule");
        }
        if (this.errors.size() > 0) {
            ok = false;
        }
        this.valid = ok;
        return ok;
    }

    public boolean isMatchTypeWildcard() {
        return MATCH_TYPE_WILDCARD.equalsIgnoreCase(this.matchType);
    }

    public boolean isToContainsBackReference() {
        return this.toContainsBackReference;
    }

    public boolean isToContainsVariable() {
        return this.toContainsVariable;
    }

    public boolean isToContainsFunction() {
        return this.toContainsFunction;
    }

    public String getFullDisplayName() {
        return null;
    }

    protected void addError(String s) {
        this.errors.add(s);
        log.error(s);
    }

    public void destroy() {
        for (int i = 0; i < this.runs.size(); ++i) {
            Run run = (Run)this.runs.get(i);
            run.destroy();
        }
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        if (!StringUtils.isBlank(to)) {
            this.to = to;
        }
    }

    public void setToLast(String lastStr) {
        this.last = "true".equalsIgnoreCase(lastStr);
    }

    public boolean isLast() {
        return this.last;
    }

    public String getTo() {
        return this.to;
    }

    public int getId() {
        return this.id;
    }

    public List getErrors() {
        return this.errors;
    }

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
        condition.setId(this.conditionIdCounter++);
    }

    public void addRun(Run run) {
        this.runs.add(run);
        run.setId(this.runIdCounter++);
    }

    public void addSetAttribute(SetAttribute setAttribute) {
        this.setAttributes.add(setAttribute);
    }

    public List getSetAttributes() {
        return this.setAttributes;
    }

    public List getConditions() {
        return this.conditions;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFromCaseSensitive() {
        return this.fromCaseSensitive;
    }

    public void setFromCaseSensitive(boolean fromCaseSensitive) {
        this.fromCaseSensitive = fromCaseSensitive;
    }

    public List getRuns() {
        return this.runs;
    }

    public boolean isValid() {
        return this.valid;
    }

    public String getMatchType() {
        return this.matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = MATCH_TYPE_WILDCARD.equalsIgnoreCase(matchType = StringUtils.trimToNull(matchType)) ? MATCH_TYPE_WILDCARD : DEFAULT_MATCH_TYPE;
    }

    public boolean isFilter() {
        return this.filter;
    }

    public boolean isNoSubstitution() {
        return this.noSubstitution;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }
}


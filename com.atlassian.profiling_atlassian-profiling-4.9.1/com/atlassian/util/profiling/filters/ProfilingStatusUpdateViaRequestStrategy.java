/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.util.profiling.filters;

import com.atlassian.util.profiling.ProfilerConfiguration;
import com.atlassian.util.profiling.Timers;
import com.atlassian.util.profiling.filters.FilterConfigAware;
import com.atlassian.util.profiling.filters.StatusUpdateStrategy;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ProfilingStatusUpdateViaRequestStrategy
implements StatusUpdateStrategy,
FilterConfigAware {
    private static final Logger log = LoggerFactory.getLogger(ProfilingStatusUpdateViaRequestStrategy.class);
    private static final String DEFAULT_ON_OFF_PARAM = "profile.filter";
    private static String onOffParameter = "profile.filter";
    private static Pattern onOffParameterPattern = Pattern.compile(onOffParameter + "=([\\w\\d]+)");
    static final String ON_OFF_INIT_PARAM = "activate.param";

    @Override
    public void configure(FilterConfig filterConfig) {
        if (filterConfig.getInitParameter(ON_OFF_INIT_PARAM) != null) {
            log.debug("[Filter: {}] Using parameter [{}]", (Object)filterConfig.getFilterName(), (Object)filterConfig.getInitParameter(ON_OFF_INIT_PARAM));
            ProfilingStatusUpdateViaRequestStrategy.setOnOffParameter(filterConfig);
        }
    }

    @Override
    public void setStateViaRequest(ServletRequest request) {
        Matcher m;
        String queryString;
        String paramValue = null;
        if (request instanceof HttpServletRequest && (queryString = ((HttpServletRequest)request).getQueryString()) != null && (m = ProfilingStatusUpdateViaRequestStrategy.getOnOffParameterPattern().matcher(queryString)).find()) {
            paramValue = m.group(1);
        }
        if (paramValue != null) {
            this.setProfilingState(paramValue);
        }
    }

    protected void turnProfilingOn() {
        log.debug("Turning profiling on [{}=on]", (Object)onOffParameter);
        Timers.getConfiguration().setEnabled(true);
    }

    protected void turnProfilingOnAndSetThreshold(long minTotalTime) {
        log.debug("Turning profiling on [{}=on] with threshold {}ms", (Object)onOffParameter, (Object)minTotalTime);
        ProfilerConfiguration config = Timers.getConfiguration();
        config.setMinTraceTime(minTotalTime, TimeUnit.MILLISECONDS);
        config.setEnabled(true);
    }

    protected void turnProfilingOff() {
        log.debug("Turning profiling off [{}=off]", (Object)onOffParameter);
        ProfilerConfiguration config = Timers.getConfiguration();
        config.setMinTraceTime(0L, TimeUnit.MILLISECONDS);
        config.setEnabled(false);
    }

    static Pattern getOnOffParameterPattern() {
        return onOffParameterPattern;
    }

    private static void setOnOffParameter(FilterConfig filterConfig) {
        onOffParameter = filterConfig.getInitParameter(ON_OFF_INIT_PARAM);
        onOffParameterPattern = Pattern.compile(onOffParameter + "=([\\w\\d]+)");
    }

    private void setProfilingState(String paramValue) {
        if ("on".equals(paramValue) || "true".equals(paramValue)) {
            this.turnProfilingOn();
        } else if ("off".equals(paramValue) || "false".equals(paramValue)) {
            this.turnProfilingOff();
        } else if (paramValue.length() > 0 && Character.isDigit(paramValue.charAt(0))) {
            try {
                this.turnProfilingOnAndSetThreshold(Long.parseLong(paramValue));
            }
            catch (NumberFormatException e) {
                log.debug("Could not parse {} to Long value", (Object)paramValue);
            }
        }
    }
}


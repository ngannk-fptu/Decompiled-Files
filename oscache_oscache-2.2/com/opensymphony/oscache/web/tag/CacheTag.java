/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.jsp.JspTagException
 *  javax.servlet.jsp.tagext.BodyTagSupport
 *  javax.servlet.jsp.tagext.TryCatchFinally
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.web.tag;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.util.StringUtil;
import com.opensymphony.oscache.web.ServletCacheAdministrator;
import com.opensymphony.oscache.web.WebEntryRefreshPolicy;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CacheTag
extends BodyTagSupport
implements TryCatchFinally {
    private static final int SECOND = 1;
    private static final int MINUTE = 60;
    private static final int HOUR = 3600;
    private static final int DAY = 86400;
    private static final int WEEK = 604800;
    private static final int MONTH = 2592000;
    private static final int YEAR = 31536000;
    private static final String CACHE_TAG_COUNTER_KEY = "__oscache_tag_counter";
    private static final int ONE_MINUTE = 60;
    private static final int ONE_HOUR = 3600;
    private static final int DEFAULT_TIMEOUT = 3600;
    private static transient Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$web$tag$CacheTag == null ? (class$com$opensymphony$oscache$web$tag$CacheTag = CacheTag.class$("com.opensymphony.oscache.web.tag.CacheTag")) : class$com$opensymphony$oscache$web$tag$CacheTag));
    private static final int SILENT_MODE = 1;
    boolean cancelUpdateRequired = false;
    private Cache cache = null;
    private List groups = null;
    private ServletCacheAdministrator admin = null;
    private String actualKey = null;
    private String content = null;
    private String cron = null;
    private String key = null;
    private String language = null;
    private String refreshPolicyClass = null;
    private String refreshPolicyParam = null;
    private boolean refresh = false;
    private boolean useBody = true;
    private int mode = 0;
    private int scope = 4;
    private int time = 3600;
    static /* synthetic */ Class class$com$opensymphony$oscache$web$tag$CacheTag;

    public void setDuration(String duration) {
        try {
            this.time = this.parseDuration(duration);
        }
        catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Failed parsing simple duration format '" + duration + "' (" + ex.getMessage() + "). Trying ISO-8601 format..."));
            }
            try {
                this.time = this.parseISO_8601_Duration(duration);
            }
            catch (Exception ex1) {
                log.warn((Object)("The requested cache duration '" + duration + "' is invalid (" + ex1.getMessage() + "). Reverting to the default timeout"));
                this.time = 3600;
            }
        }
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public void setGroups(String groups) {
        this.groups = StringUtil.split(groups, ',');
    }

    void addGroup(String group) {
        if (this.groups == null) {
            this.groups = new ArrayList();
        }
        this.groups.add(group);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public void setMode(String mode) {
        this.mode = "silent".equalsIgnoreCase(mode) ? 1 : 0;
    }

    public void setRefreshpolicyclass(String refreshPolicyClass) {
        this.refreshPolicyClass = refreshPolicyClass;
    }

    public void setRefreshpolicyparam(String refreshPolicyParam) {
        this.refreshPolicyParam = refreshPolicyParam;
    }

    public void setScope(String scope) {
        this.scope = scope.equalsIgnoreCase("session") ? 3 : 4;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setUseBody(boolean useBody) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("<cache>: Set useBody to " + useBody));
        }
        this.useBody = useBody;
    }

    public int doAfterBody() throws JspTagException {
        String body = null;
        try {
            if (this.bodyContent != null && (this.useBody || this.time == 0) && (body = this.bodyContent.getString()) != null) {
                if (this.time != 0 || this.refreshPolicyClass != null) {
                    WebEntryRefreshPolicy policy;
                    block16: {
                        policy = null;
                        if (this.refreshPolicyClass != null) {
                            try {
                                policy = (WebEntryRefreshPolicy)Class.forName(this.refreshPolicyClass).newInstance();
                                policy.init(this.actualKey, this.refreshPolicyParam);
                            }
                            catch (Exception e) {
                                if (!log.isInfoEnabled()) break block16;
                                log.info((Object)("<cache>: Problem instantiating or initializing refresh policy : " + this.refreshPolicyClass));
                            }
                        }
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("<cache>: Updating cache entry with new content : " + this.actualKey));
                    }
                    this.cancelUpdateRequired = false;
                    if (this.groups == null || this.groups.isEmpty()) {
                        this.cache.putInCache(this.actualKey, (Object)body, policy);
                    } else {
                        String[] groupArray = new String[this.groups.size()];
                        this.groups.toArray(groupArray);
                        this.cache.putInCache(this.actualKey, body, groupArray, policy, null);
                    }
                }
            } else if (!this.useBody && this.content != null) {
                if (log.isInfoEnabled()) {
                    log.info((Object)("<cache>: Using cached version as instructed, useBody = false : " + this.actualKey));
                }
                body = this.content;
            } else {
                if (log.isInfoEnabled()) {
                    log.info((Object)("<cache>: Missing cached content : " + this.actualKey));
                }
                body = "Missing cached content";
            }
            if (this.mode != 1) {
                this.bodyContent.clearBody();
                this.bodyContent.write(body);
                this.bodyContent.writeOut((Writer)this.bodyContent.getEnclosingWriter());
            }
        }
        catch (IOException e) {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return 0;
    }

    public void doCatch(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public int doEndTag() throws JspTagException {
        return 6;
    }

    public void doFinally() {
        if (this.cancelUpdateRequired && this.actualKey != null) {
            this.cache.cancelUpdate(this.actualKey);
        }
        this.groups = null;
        this.scope = 4;
        this.cron = null;
        this.key = null;
        this.language = null;
        this.refreshPolicyClass = null;
        this.refreshPolicyParam = null;
        this.time = 3600;
        this.refresh = false;
        this.mode = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int doStartTag() throws JspTagException {
        this.cancelUpdateRequired = false;
        this.useBody = true;
        this.content = null;
        int returnCode = 2;
        if (this.admin == null) {
            this.admin = ServletCacheAdministrator.getInstance(this.pageContext.getServletContext());
        }
        this.cache = this.scope == 3 ? this.admin.getSessionScopeCache(((HttpServletRequest)this.pageContext.getRequest()).getSession(true)) : this.admin.getAppScopeCache(this.pageContext.getServletContext());
        String suffix = null;
        if (this.key == null) {
            ServletRequest servletRequest = this.pageContext.getRequest();
            synchronized (servletRequest) {
                Object o = this.pageContext.getRequest().getAttribute(CACHE_TAG_COUNTER_KEY);
                suffix = o == null ? "1" : Integer.toString(Integer.parseInt((String)o) + 1);
            }
            this.pageContext.getRequest().setAttribute(CACHE_TAG_COUNTER_KEY, (Object)suffix);
        }
        this.actualKey = this.admin.generateEntryKey(this.key, (HttpServletRequest)this.pageContext.getRequest(), this.scope, this.language, suffix);
        try {
            this.content = this.refresh ? (String)this.cache.getFromCache(this.actualKey, 0, this.cron) : (String)this.cache.getFromCache(this.actualKey, this.time, this.cron);
            try {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("<cache>: Using cached entry : " + this.actualKey));
                }
                if (this.content != null) {
                    if (this.mode != 1) {
                        this.pageContext.getOut().write(this.content);
                    }
                    returnCode = 0;
                }
            }
            catch (IOException e) {
                throw new JspTagException("IO Exception: " + e.getMessage());
            }
        }
        catch (NeedsRefreshException nre) {
            this.cancelUpdateRequired = true;
            this.content = (String)nre.getCacheContent();
        }
        if (returnCode == 2 && log.isDebugEnabled()) {
            log.debug((Object)("<cache>: Cached content not used: New cache entry, cache stale or scope flushed : " + this.actualKey));
        }
        return returnCode;
    }

    private int parseDuration(String duration) {
        int time = 0;
        try {
            time = Integer.parseInt(duration);
        }
        catch (Exception ex) {
            for (int i = 0; i < duration.length(); ++i) {
                if (Character.isDigit(duration.charAt(i))) continue;
                time = Integer.parseInt(duration.substring(0, i));
                switch (duration.charAt(i)) {
                    case 's': {
                        time *= 1;
                        break;
                    }
                    case 'm': {
                        time *= 60;
                        break;
                    }
                    case 'h': {
                        time *= 3600;
                        break;
                    }
                    case 'd': {
                        time *= 86400;
                        break;
                    }
                    case 'w': {
                        time *= 604800;
                        break;
                    }
                }
                break;
            }
        }
        return time;
    }

    private int parseISO_8601_Duration(String duration) throws Exception {
        int years = 0;
        int months = 0;
        int days = 0;
        int hours = 0;
        int mins = 0;
        int secs = 0;
        int index = duration.indexOf("-");
        if (index > 0) {
            throw new Exception("Invalid duration (- must be at the beginning)");
        }
        String workValue = duration.substring(index + 1);
        if (workValue.charAt(0) != 'P') {
            throw new Exception("Invalid duration (P must be at the beginning)");
        }
        if ((workValue = workValue.substring(1)).length() == 0) {
            throw new Exception("Invalid duration (nothing specified)");
        }
        index = workValue.indexOf(84);
        String timeString = "";
        if (index > 0) {
            timeString = workValue.substring(index + 1);
            if (timeString.equals("")) {
                throw new Exception("Invalid duration (T with no time)");
            }
            workValue = workValue.substring(0, index);
        } else if (index == 0) {
            timeString = workValue.substring(1);
            workValue = "";
        }
        if (!workValue.equals("")) {
            this.validateDateFormat(workValue);
            int yearIndex = workValue.indexOf(89);
            int monthIndex = workValue.indexOf(77);
            int dayIndex = workValue.indexOf(68);
            if (yearIndex != -1 && monthIndex != -1 && yearIndex > monthIndex) {
                throw new Exception("Invalid duration (Date part not properly specified)");
            }
            if (yearIndex != -1 && dayIndex != -1 && yearIndex > dayIndex) {
                throw new Exception("Invalid duration (Date part not properly specified)");
            }
            if (dayIndex != -1 && monthIndex != -1 && monthIndex > dayIndex) {
                throw new Exception("Invalid duration (Date part not properly specified)");
            }
            if (yearIndex >= 0) {
                years = new Integer(workValue.substring(0, yearIndex));
            }
            if (monthIndex >= 0) {
                months = new Integer(workValue.substring(yearIndex + 1, monthIndex));
            }
            if (dayIndex >= 0) {
                days = monthIndex >= 0 ? new Integer(workValue.substring(monthIndex + 1, dayIndex)) : (yearIndex >= 0 ? new Integer(workValue.substring(yearIndex + 1, dayIndex)).intValue() : new Integer(workValue.substring(0, dayIndex)).intValue());
            }
        }
        if (!timeString.equals("")) {
            this.validateHourFormat(timeString);
            int hourIndex = timeString.indexOf(72);
            int minuteIndex = timeString.indexOf(77);
            int secondIndex = timeString.indexOf(83);
            if (hourIndex != -1 && minuteIndex != -1 && hourIndex > minuteIndex) {
                throw new Exception("Invalid duration (Time part not properly specified)");
            }
            if (hourIndex != -1 && secondIndex != -1 && hourIndex > secondIndex) {
                throw new Exception("Invalid duration (Time part not properly specified)");
            }
            if (secondIndex != -1 && minuteIndex != -1 && minuteIndex > secondIndex) {
                throw new Exception("Invalid duration (Time part not properly specified)");
            }
            if (hourIndex >= 0) {
                hours = new Integer(timeString.substring(0, hourIndex));
            }
            if (minuteIndex >= 0) {
                mins = new Integer(timeString.substring(hourIndex + 1, minuteIndex));
            }
            if (secondIndex >= 0) {
                if (timeString.length() != secondIndex + 1) {
                    throw new Exception("Invalid duration (Time part not properly specified)");
                }
                timeString = minuteIndex >= 0 ? timeString.substring(minuteIndex + 1, timeString.length() - 1) : (hourIndex >= 0 ? timeString.substring(hourIndex + 1, timeString.length() - 1) : timeString.substring(0, timeString.length() - 1));
                if (timeString.indexOf(46) == timeString.length() - 1) {
                    throw new Exception("Invalid duration (Time part not properly specified)");
                }
                secs = new Double(timeString).intValue();
            }
        }
        return secs + mins * 60 + hours * 3600 + days * 86400 + months * 2592000 + years * 31536000;
    }

    private void validateDateFormat(String basicDate) throws Exception {
        int yearCounter = 0;
        int monthCounter = 0;
        int dayCounter = 0;
        for (int counter = 0; counter < basicDate.length(); ++counter) {
            if (!Character.isDigit(basicDate.charAt(counter)) && basicDate.charAt(counter) != 'Y' && basicDate.charAt(counter) != 'M' && basicDate.charAt(counter) != 'D') {
                throw new Exception("Invalid duration (Date part not properly specified)");
            }
            if (basicDate.charAt(counter) == 'Y') {
                ++yearCounter;
            }
            if (basicDate.charAt(counter) == 'M') {
                ++monthCounter;
            }
            if (basicDate.charAt(counter) != 'D') continue;
            ++dayCounter;
        }
        if (yearCounter > 1 || monthCounter > 1 || dayCounter > 1) {
            throw new Exception("Invalid duration (Date part not properly specified)");
        }
    }

    private void validateHourFormat(String basicHour) throws Exception {
        int minuteCounter = 0;
        int secondCounter = 0;
        int hourCounter = 0;
        for (int counter = 0; counter < basicHour.length(); ++counter) {
            if (!Character.isDigit(basicHour.charAt(counter)) && basicHour.charAt(counter) != 'H' && basicHour.charAt(counter) != 'M' && basicHour.charAt(counter) != 'S' && basicHour.charAt(counter) != '.') {
                throw new Exception("Invalid duration (Time part not properly specified)");
            }
            if (basicHour.charAt(counter) == 'H') {
                ++hourCounter;
            }
            if (basicHour.charAt(counter) == 'M') {
                ++minuteCounter;
            }
            if (basicHour.charAt(counter) != 'S') continue;
            ++secondCounter;
        }
        if (hourCounter > 1 || minuteCounter > 1 || secondCounter > 1) {
            throw new Exception("Invalid duration (Time part not properly specified)");
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


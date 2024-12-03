/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

import com.atlassian.confluence.extra.dynamictasklist2.model.MacroParameter;
import com.atlassian.confluence.extra.dynamictasklist2.model.Sort;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TaskListConfig {
    private static final String PROMPT_ON_DELETE_NAME = "promptOnDelete";
    private static final String DISPLAY_TIPS_NAME = "displayTips";
    private static final String SHOW_ASSIGNEE_NAME = "showAssignee";
    private static final String ENABLE_LOCKING_NAME = "enableLocking";
    private static final String AUTO_LOCK_ON_COMPLETE_NAME = "autoLockOnComplete";
    private static final String ENABLE_WIKI_MARKUP_NAME = "enableWikiMarkup";
    private static final String ENABLE_VERSIONING_NAME = "enableVersioning";
    private static final String SHOW_TIME_NAME = "showTime";
    private static final String SORT_BY_NAME = "sortBy";
    private static final String SORT_ASCENDING_NAME = "sortAscending";
    private static final String WIDTH_NAME = "width";
    private static final boolean PROMPT_ON_DELETE_DEFAULT = true;
    private static final boolean DISPLAY_TIPS_DEFAULT = false;
    private static final boolean SHOW_ASSIGNEE_DEFAULT = true;
    private static final boolean ENABLE_LOCKING_DEFAULT = false;
    private static final boolean AUTO_LOCK_ON_COMPLETE_DEFAULT = false;
    private static final boolean ENABLE_WIKI_MARKUP_DEFAULT = true;
    private static final boolean ENABLE_VERSIONING_DEFAULT = true;
    private static final boolean SHOW_TIME_DEFAULT = false;
    private static final Sort SORT_BY_DEFAULT = Sort.NONE;
    private static final boolean SORT_ASCENDING_DEFAULT = true;
    private static final String WIDTH_DEFAULT = "";
    private final Map config;

    public TaskListConfig() {
        TreeMap<String, MacroParameter> mutableConfig = new TreeMap<String, MacroParameter>();
        mutableConfig.put(PROMPT_ON_DELETE_NAME, new MacroParameter(PROMPT_ON_DELETE_NAME, MacroParameter.Type.BOOLEAN, true));
        mutableConfig.put(DISPLAY_TIPS_NAME, new MacroParameter(DISPLAY_TIPS_NAME, MacroParameter.Type.BOOLEAN, false));
        mutableConfig.put(SHOW_ASSIGNEE_NAME, new MacroParameter(SHOW_ASSIGNEE_NAME, MacroParameter.Type.BOOLEAN, true));
        mutableConfig.put(ENABLE_LOCKING_NAME, new MacroParameter(ENABLE_LOCKING_NAME, MacroParameter.Type.BOOLEAN, false));
        mutableConfig.put(AUTO_LOCK_ON_COMPLETE_NAME, new MacroParameter(AUTO_LOCK_ON_COMPLETE_NAME, MacroParameter.Type.BOOLEAN, false));
        mutableConfig.put(ENABLE_WIKI_MARKUP_NAME, new MacroParameter(ENABLE_WIKI_MARKUP_NAME, MacroParameter.Type.BOOLEAN, true));
        mutableConfig.put(ENABLE_VERSIONING_NAME, new MacroParameter(ENABLE_VERSIONING_NAME, MacroParameter.Type.BOOLEAN, true));
        mutableConfig.put(SHOW_TIME_NAME, new MacroParameter(SHOW_TIME_NAME, MacroParameter.Type.BOOLEAN, false));
        mutableConfig.put(SORT_BY_NAME, new MacroParameter(SORT_BY_NAME, MacroParameter.Type.SORT, SORT_BY_DEFAULT));
        mutableConfig.put(SORT_ASCENDING_NAME, new MacroParameter(SORT_ASCENDING_NAME, MacroParameter.Type.BOOLEAN, true));
        mutableConfig.put(WIDTH_NAME, new MacroParameter(WIDTH_NAME, MacroParameter.Type.STRING, WIDTH_DEFAULT));
        this.config = Collections.unmodifiableMap(mutableConfig);
    }

    public boolean getPromptOnDelete() {
        return this.getBooleanFromConfig(PROMPT_ON_DELETE_NAME);
    }

    public void setPromptOnDelete(boolean promptOnDelete) {
        this.setInConfig(PROMPT_ON_DELETE_NAME, promptOnDelete);
    }

    public boolean getDisplayTips() {
        return this.getBooleanFromConfig(DISPLAY_TIPS_NAME);
    }

    public void setDisplayTips(boolean displayTips) {
        this.setInConfig(DISPLAY_TIPS_NAME, displayTips);
    }

    public boolean getShowAssignee() {
        return this.getBooleanFromConfig(SHOW_ASSIGNEE_NAME);
    }

    public void setShowAssignee(boolean showAssignee) {
        this.setInConfig(SHOW_ASSIGNEE_NAME, showAssignee);
    }

    public boolean getEnableLocking() {
        return this.getBooleanFromConfig(ENABLE_LOCKING_NAME);
    }

    public void setEnableLocking(boolean enableLocking) {
        this.setInConfig(ENABLE_LOCKING_NAME, enableLocking);
    }

    public boolean getAutoLockOnComplete() {
        return this.getBooleanFromConfig(AUTO_LOCK_ON_COMPLETE_NAME);
    }

    public void setAutoLockOnComplete(boolean autoLockOnComplete) {
        this.setInConfig(AUTO_LOCK_ON_COMPLETE_NAME, autoLockOnComplete);
    }

    public boolean getEnableWikiMarkup() {
        return this.getBooleanFromConfig(ENABLE_WIKI_MARKUP_NAME);
    }

    public void setEnableWikiMarkup(boolean enableWikiMarkup) {
        this.setInConfig(ENABLE_WIKI_MARKUP_NAME, enableWikiMarkup);
    }

    public boolean getEnableVersioning() {
        return this.getBooleanFromConfig(ENABLE_VERSIONING_NAME);
    }

    public void setEnableVersioning(boolean enabledVersioning) {
        this.setInConfig(ENABLE_VERSIONING_NAME, enabledVersioning);
    }

    public boolean getShowTime() {
        return this.getBooleanFromConfig(SHOW_TIME_NAME);
    }

    public void setShowTime(boolean showTime) {
        this.setInConfig(SHOW_TIME_NAME, showTime);
    }

    public Sort getSort() {
        return (Sort)this.getFromConfig(SORT_BY_NAME);
    }

    public void setSort(Sort sort) {
        this.setInConfig(SORT_BY_NAME, sort);
    }

    public boolean isSortAscending() {
        return this.getBooleanFromConfig(SORT_ASCENDING_NAME);
    }

    public void setSortAscending(boolean ascending) {
        this.setInConfig(SORT_ASCENDING_NAME, ascending);
    }

    public String getWidth() {
        return this.getFromConfig(WIDTH_NAME).toString();
    }

    public void setWidth(String width) {
        this.setInConfig(WIDTH_NAME, width);
    }

    public Comparator getComparator() {
        return this.getSort().getComparator(this.isSortAscending());
    }

    public void load(Map parameters) {
        for (MacroParameter param : this.config.values()) {
            if (!parameters.containsKey(param.getName())) continue;
            if (param.getType() == MacroParameter.Type.BOOLEAN) {
                param.setValue(Boolean.valueOf((String)parameters.get(param.getName())));
                continue;
            }
            if (param.getType() == MacroParameter.Type.SORT) {
                param.setValue(Sort.valueOf((String)parameters.get(param.getName())));
                continue;
            }
            param.setValue(parameters.get(param.getName()));
        }
    }

    public String serialize() {
        StringBuffer sb = new StringBuffer();
        for (MacroParameter param : this.config.values()) {
            if (param.isDefault()) continue;
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append(param.getName());
            sb.append('=');
            sb.append(param.getValue());
        }
        return sb.toString();
    }

    private boolean getBooleanFromConfig(String paramName) {
        return (Boolean)this.getFromConfig(paramName);
    }

    private Object getFromConfig(String paramName) {
        return ((MacroParameter)this.config.get(paramName)).getValue();
    }

    private void setInConfig(String paramName, boolean value) {
        this.setInConfig(paramName, (Object)value);
    }

    private void setInConfig(String paramName, Object value) {
        ((MacroParameter)this.config.get(paramName)).setValue(value);
    }
}


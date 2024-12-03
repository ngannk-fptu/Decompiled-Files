/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.impl.AbstractMatcher;
import com.opensymphony.xwork2.util.PatternMatcher;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ActionConfigMatcher
extends AbstractMatcher<ActionConfig>
implements Serializable {
    public ActionConfigMatcher(PatternMatcher<?> patternMatcher, Map<String, ActionConfig> configs, boolean looseMatch) {
        this(patternMatcher, configs, looseMatch, true);
    }

    public ActionConfigMatcher(PatternMatcher<?> patternMatcher, Map<String, ActionConfig> configs, boolean looseMatch, boolean appendNamedParameters) {
        super(patternMatcher, appendNamedParameters);
        for (Map.Entry<String, ActionConfig> entry : configs.entrySet()) {
            this.addPattern(entry.getKey(), entry.getValue(), looseMatch);
        }
    }

    @Override
    public ActionConfig convert(String path, ActionConfig orig, Map<String, String> vars) {
        String methodName = this.convertParam(orig.getMethodName(), vars);
        if (StringUtils.isEmpty((CharSequence)methodName)) {
            methodName = "execute";
        }
        if (!orig.isAllowedMethod(methodName)) {
            return null;
        }
        String className = this.convertParam(orig.getClassName(), vars);
        String pkgName = this.convertParam(orig.getPackageName(), vars);
        Map<String, String> params = this.replaceParameters(orig.getParams(), vars);
        LinkedHashMap<String, ResultConfig> results = new LinkedHashMap<String, ResultConfig>();
        for (String name : orig.getResults().keySet()) {
            ResultConfig result = orig.getResults().get(name);
            name = this.convertParam(name, vars);
            ResultConfig r = new ResultConfig.Builder(name, this.convertParam(result.getClassName(), vars)).addParams(this.replaceParameters(result.getParams(), vars)).build();
            results.put(name, r);
        }
        ArrayList<ExceptionMappingConfig> exs = new ArrayList<ExceptionMappingConfig>();
        for (ExceptionMappingConfig ex : orig.getExceptionMappings()) {
            String name = this.convertParam(ex.getName(), vars);
            String exClassName = this.convertParam(ex.getExceptionClassName(), vars);
            String exResult = this.convertParam(ex.getResult(), vars);
            Map<String, String> exParams = this.replaceParameters(ex.getParams(), vars);
            ExceptionMappingConfig e = new ExceptionMappingConfig.Builder(name, exClassName, exResult).addParams(exParams).build();
            exs.add(e);
        }
        return ((ActionConfig.Builder)new ActionConfig.Builder(pkgName, orig.getName(), className).methodName(methodName).addParams(params).addResultConfigs(results).setStrictMethodInvocation(orig.isStrictMethodInvocation()).addAllowedMethod(orig.getAllowedMethods()).addInterceptors((List)orig.getInterceptors())).addExceptionMappings(exs).location(orig.getLocation()).build();
    }
}


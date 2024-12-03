/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.validator.AnnotationValidationConfigurationBuilder;
import com.opensymphony.xwork2.validator.DefaultActionValidatorManager;
import com.opensymphony.xwork2.validator.ValidatorConfig;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AnnotationActionValidatorManager
extends DefaultActionValidatorManager {
    @Override
    protected String buildValidatorKey(Class clazz, String context) {
        String configName;
        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        ActionProxy proxy = invocation.getProxy();
        ActionConfig config = proxy.getConfig();
        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append("/");
        if (StringUtils.isNotBlank((CharSequence)config.getPackageName())) {
            sb.append(config.getPackageName());
            sb.append("/");
        }
        if ((configName = config.getName()).contains("*") || configName.contains("{") && configName.contains("}")) {
            sb.append(configName);
            sb.append("|");
            sb.append(proxy.getMethod());
        } else {
            sb.append(context);
        }
        return sb.toString();
    }

    @Override
    protected List<ValidatorConfig> buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context.replace('/', '-') + "-validation.xml";
        return this.loadFile(fileName, aClass, checkFile);
    }

    @Override
    protected List<ValidatorConfig> buildClassValidatorConfigs(Class aClass, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-validation.xml";
        ArrayList<ValidatorConfig> result = new ArrayList<ValidatorConfig>(this.loadFile(fileName, aClass, checkFile));
        AnnotationValidationConfigurationBuilder builder = new AnnotationValidationConfigurationBuilder(this.validatorFactory);
        ArrayList<ValidatorConfig> annotationResult = new ArrayList<ValidatorConfig>(builder.buildAnnotationClassValidatorConfigs(aClass));
        result.addAll(annotationResult);
        return result;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.FieldValidator;
import com.opensymphony.xwork2.validator.ShortCircuitableValidator;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.Validator;
import com.opensymphony.xwork2.validator.ValidatorConfig;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorFactory;
import com.opensymphony.xwork2.validator.ValidatorFileParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultActionValidatorManager
implements ActionValidatorManager {
    protected static final String VALIDATION_CONFIG_SUFFIX = "-validation.xml";
    protected final Map<String, List<ValidatorConfig>> validatorCache = Collections.synchronizedMap(new HashMap());
    protected final Map<String, List<ValidatorConfig>> validatorFileCache = Collections.synchronizedMap(new HashMap());
    private static final Logger LOG = LogManager.getLogger(DefaultActionValidatorManager.class);
    protected ValidatorFactory validatorFactory;
    protected ValidatorFileParser validatorFileParser;
    protected FileManager fileManager;
    protected boolean reloadingConfigs;
    protected TextProviderFactory textProviderFactory;

    @Inject
    public void setValidatorFactory(ValidatorFactory fac) {
        this.validatorFactory = fac;
    }

    @Inject
    public void setValidatorFileParser(ValidatorFileParser parser) {
        this.validatorFileParser = parser;
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject(value="struts.configuration.xml.reload", required=false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    @Override
    public void validate(Object object, String context) throws ValidationException {
        this.validate(object, context, (String)null);
    }

    @Override
    public void validate(Object object, String context, String method) throws ValidationException {
        DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(object, this.textProviderFactory);
        this.validate(object, context, validatorContext, method);
    }

    @Override
    public void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException {
        this.validate(object, context, validatorContext, null);
    }

    protected String buildValidatorKey(Class clazz, String context) {
        return clazz.getName() + "/" + context;
    }

    protected Validator getValidatorFromValidatorConfig(ValidatorConfig config, ValueStack stack) {
        Validator validator = this.validatorFactory.getValidator(config);
        validator.setValidatorType(config.getType());
        validator.setValueStack(stack);
        return validator;
    }

    @Override
    public synchronized List<Validator> getValidators(Class clazz, String context, String method) {
        String validatorKey = this.buildValidatorKey(clazz, context);
        if (!this.validatorCache.containsKey(validatorKey)) {
            this.validatorCache.put(validatorKey, this.buildValidatorConfigs(clazz, context, false, null));
        } else if (this.reloadingConfigs) {
            this.validatorCache.put(validatorKey, this.buildValidatorConfigs(clazz, context, true, null));
        }
        ValueStack stack = ActionContext.getContext().getValueStack();
        List<ValidatorConfig> configs = this.validatorCache.get(validatorKey);
        ArrayList<Validator> validators = new ArrayList<Validator>();
        for (ValidatorConfig config : configs) {
            if (method != null && !method.equals(config.getParams().get("methodName"))) continue;
            validators.add(this.getValidatorFromValidatorConfig(config, stack));
        }
        return validators;
    }

    @Override
    public synchronized List<Validator> getValidators(Class clazz, String context) {
        return this.getValidators(clazz, context, null);
    }

    @Override
    public void validate(Object object, String context, ValidatorContext validatorContext, String method) throws ValidationException {
        List<Validator> validators = this.getValidators(object.getClass(), context, method);
        TreeSet<String> shortcircuitedFields = null;
        for (Validator validator : validators) {
            validator.setValidatorContext(validatorContext);
            LOG.debug("Running validator: {} for object {} and method {}", (Object)validator, object, (Object)method);
            FieldValidator fValidator = null;
            String fullFieldName = null;
            if (validator instanceof FieldValidator) {
                fValidator = (FieldValidator)validator;
                fullFieldName = validatorContext.getFullFieldName(fValidator.getFieldName());
                if (shortcircuitedFields != null && shortcircuitedFields.contains(fullFieldName)) {
                    LOG.debug("Short-circuited, skipping");
                    continue;
                }
            }
            if (validator instanceof ShortCircuitableValidator && ((ShortCircuitableValidator)((Object)validator)).isShortCircuit()) {
                Collection errCol;
                Collection<String> actionErrors;
                ArrayList<String> errs = null;
                if (fValidator != null) {
                    Collection fieldErrors;
                    if (validatorContext.hasFieldErrors() && (fieldErrors = (Collection)validatorContext.getFieldErrors().get(fullFieldName)) != null) {
                        errs = new ArrayList(fieldErrors);
                    }
                } else if (validatorContext.hasActionErrors() && (actionErrors = validatorContext.getActionErrors()) != null) {
                    errs = new ArrayList<String>(actionErrors);
                }
                validator.validate(object);
                if (fValidator != null) {
                    if (!validatorContext.hasFieldErrors() || (errCol = (Collection)validatorContext.getFieldErrors().get(fullFieldName)) == null || errCol.equals(errs)) continue;
                    LOG.debug("Short-circuiting on field validation");
                    if (shortcircuitedFields == null) {
                        shortcircuitedFields = new TreeSet<String>();
                    }
                    shortcircuitedFields.add(fullFieldName);
                    continue;
                }
                if (!validatorContext.hasActionErrors() || (errCol = validatorContext.getActionErrors()) == null || errCol.equals(errs)) continue;
                LOG.debug("Short-circuiting");
                break;
            }
            validator.validate(object);
        }
    }

    protected List<ValidatorConfig> buildValidatorConfigs(Class clazz, String context, boolean checkFile, Set<String> checked) {
        ArrayList<ValidatorConfig> validatorConfigs = new ArrayList<ValidatorConfig>();
        if (checked == null) {
            checked = new TreeSet<String>();
        } else if (checked.contains(clazz.getName())) {
            return validatorConfigs;
        }
        if (clazz.isInterface()) {
            for (Class<?> anInterface : clazz.getInterfaces()) {
                validatorConfigs.addAll(this.buildValidatorConfigs(anInterface, context, checkFile, checked));
            }
        } else if (!clazz.equals(Object.class)) {
            validatorConfigs.addAll(this.buildValidatorConfigs(clazz.getSuperclass(), context, checkFile, checked));
        }
        for (Class<?> anInterface1 : clazz.getInterfaces()) {
            if (checked.contains(anInterface1.getName())) continue;
            validatorConfigs.addAll(this.buildClassValidatorConfigs(anInterface1, checkFile));
            if (context != null) {
                validatorConfigs.addAll(this.buildAliasValidatorConfigs(anInterface1, context, checkFile));
            }
            checked.add(anInterface1.getName());
        }
        validatorConfigs.addAll(this.buildClassValidatorConfigs(clazz, checkFile));
        if (context != null) {
            validatorConfigs.addAll(this.buildAliasValidatorConfigs(clazz, context, checkFile));
        }
        checked.add(clazz.getName());
        return validatorConfigs;
    }

    protected List<ValidatorConfig> buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context + VALIDATION_CONFIG_SUFFIX;
        return this.loadFile(fileName, aClass, checkFile);
    }

    protected List<ValidatorConfig> buildClassValidatorConfigs(Class aClass, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;
        return this.loadFile(fileName, aClass, checkFile);
    }

    protected List<ValidatorConfig> loadFile(String fileName, Class clazz, boolean checkFile) {
        List<ValidatorConfig> retList = Collections.emptyList();
        URL fileUrl = ClassLoaderUtil.getResource(fileName, clazz);
        if (checkFile && this.fileManager.fileNeedsReloading(fileUrl) || !this.validatorFileCache.containsKey(fileName)) {
            try (InputStream is = this.fileManager.loadFile(fileUrl);){
                if (is != null) {
                    retList = new ArrayList<ValidatorConfig>(this.validatorFileParser.parseActionValidatorConfigs(this.validatorFactory, is, fileName));
                }
            }
            catch (IOException e) {
                LOG.error("Caught exception while closing file {}", (Object)fileName, (Object)e);
            }
            this.validatorFileCache.put(fileName, retList);
        } else {
            retList = this.validatorFileCache.get(fileName);
        }
        return retList;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.FastHashMap
 *  org.apache.commons.digester.Digester
 *  org.apache.commons.digester.Rule
 *  org.apache.commons.digester.xmlrules.DigesterLoader
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.FormSet;
import org.apache.commons.validator.ValidatorAction;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ValidatorResources
implements Serializable {
    private static final long serialVersionUID = -8203745881446239554L;
    private static final String VALIDATOR_RULES = "digester-rules.xml";
    private static final String[] REGISTRATIONS = new String[]{"-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0//EN", "/org/apache/commons/validator/resources/validator_1_0.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0.1//EN", "/org/apache/commons/validator/resources/validator_1_0_1.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1//EN", "/org/apache/commons/validator/resources/validator_1_1.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1.3//EN", "/org/apache/commons/validator/resources/validator_1_1_3.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.2.0//EN", "/org/apache/commons/validator/resources/validator_1_2_0.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.3.0//EN", "/org/apache/commons/validator/resources/validator_1_3_0.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.4.0//EN", "/org/apache/commons/validator/resources/validator_1_4_0.dtd"};
    private transient Log log = LogFactory.getLog(ValidatorResources.class);
    @Deprecated
    protected FastHashMap hFormSets = new FastHashMap();
    @Deprecated
    protected FastHashMap hConstants = new FastHashMap();
    @Deprecated
    protected FastHashMap hActions = new FastHashMap();
    protected static Locale defaultLocale = Locale.getDefault();
    protected FormSet defaultFormSet;
    private static final String ARGS_PATTERN = "form-validation/formset/form/field/arg";

    public ValidatorResources() {
    }

    public ValidatorResources(InputStream in) throws IOException, SAXException {
        this(new InputStream[]{in});
    }

    public ValidatorResources(InputStream[] streams) throws IOException, SAXException {
        Digester digester = this.initDigester();
        for (int i = 0; i < streams.length; ++i) {
            if (streams[i] == null) {
                throw new IllegalArgumentException("Stream[" + i + "] is null");
            }
            digester.push((Object)this);
            digester.parse(streams[i]);
        }
        this.process();
    }

    public ValidatorResources(String uri) throws IOException, SAXException {
        this(new String[]{uri});
    }

    public ValidatorResources(String[] uris) throws IOException, SAXException {
        Digester digester = this.initDigester();
        for (int i = 0; i < uris.length; ++i) {
            digester.push((Object)this);
            digester.parse(uris[i]);
        }
        this.process();
    }

    public ValidatorResources(URL url) throws IOException, SAXException {
        this(new URL[]{url});
    }

    public ValidatorResources(URL[] urls) throws IOException, SAXException {
        Digester digester = this.initDigester();
        for (int i = 0; i < urls.length; ++i) {
            digester.push((Object)this);
            digester.parse(urls[i]);
        }
        this.process();
    }

    private Digester initDigester() {
        URL rulesUrl = this.getClass().getResource(VALIDATOR_RULES);
        if (rulesUrl == null) {
            rulesUrl = ValidatorResources.class.getResource(VALIDATOR_RULES);
        }
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Loading rules from '" + rulesUrl + "'"));
        }
        Digester digester = DigesterLoader.createDigester((URL)rulesUrl);
        digester.setNamespaceAware(true);
        digester.setValidating(true);
        digester.setUseContextClassLoader(true);
        this.addOldArgRules(digester);
        for (int i = 0; i < REGISTRATIONS.length; i += 2) {
            URL url = this.getClass().getResource(REGISTRATIONS[i + 1]);
            if (url == null) continue;
            digester.register(REGISTRATIONS[i], url.toString());
        }
        return digester;
    }

    private void addOldArgRules(Digester digester) {
        Rule rule = new Rule(){

            public void begin(String namespace, String name, Attributes attributes) throws Exception {
                Arg arg = new Arg();
                arg.setKey(attributes.getValue("key"));
                arg.setName(attributes.getValue("name"));
                if ("false".equalsIgnoreCase(attributes.getValue("resource"))) {
                    arg.setResource(false);
                }
                try {
                    int length = "arg".length();
                    arg.setPosition(Integer.parseInt(name.substring(length)));
                }
                catch (Exception ex) {
                    ValidatorResources.this.getLog().error((Object)("Error parsing Arg position: " + name + " " + arg + " " + ex));
                }
                ((Field)this.getDigester().peek(0)).addArg(arg);
            }
        };
        digester.addRule("form-validation/formset/form/field/arg0", rule);
        digester.addRule("form-validation/formset/form/field/arg1", rule);
        digester.addRule("form-validation/formset/form/field/arg2", rule);
        digester.addRule("form-validation/formset/form/field/arg3", rule);
    }

    public void addFormSet(FormSet fs) {
        String key = this.buildKey(fs);
        if (key.length() == 0) {
            if (this.getLog().isWarnEnabled() && this.defaultFormSet != null) {
                this.getLog().warn((Object)"Overriding default FormSet definition.");
            }
            this.defaultFormSet = fs;
        } else {
            FormSet formset = this.getFormSets().get(key);
            if (formset == null) {
                if (this.getLog().isDebugEnabled()) {
                    this.getLog().debug((Object)("Adding FormSet '" + fs.toString() + "'."));
                }
            } else if (this.getLog().isWarnEnabled()) {
                this.getLog().warn((Object)("Overriding FormSet definition. Duplicate for locale: " + key));
            }
            this.getFormSets().put(key, fs);
        }
    }

    public void addConstant(String name, String value) {
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Adding Global Constant: " + name + "," + value));
        }
        this.hConstants.put((Object)name, (Object)value);
    }

    public void addValidatorAction(ValidatorAction va) {
        va.init();
        this.getActions().put(va.getName(), va);
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Add ValidatorAction: " + va.getName() + "," + va.getClassname()));
        }
    }

    public ValidatorAction getValidatorAction(String key) {
        return this.getActions().get(key);
    }

    public Map<String, ValidatorAction> getValidatorActions() {
        return Collections.unmodifiableMap(this.getActions());
    }

    protected String buildKey(FormSet fs) {
        return this.buildLocale(fs.getLanguage(), fs.getCountry(), fs.getVariant());
    }

    private String buildLocale(String lang, String country, String variant) {
        String key = lang != null && lang.length() > 0 ? lang : "";
        key = key + (country != null && country.length() > 0 ? "_" + country : "");
        key = key + (variant != null && variant.length() > 0 ? "_" + variant : "");
        return key;
    }

    public Form getForm(Locale locale, String formKey) {
        return this.getForm(locale.getLanguage(), locale.getCountry(), locale.getVariant(), formKey);
    }

    public Form getForm(String language, String country, String variant, String formKey) {
        FormSet formSet;
        FormSet formSet2;
        Form form = null;
        String key = this.buildLocale(language, country, variant);
        if (key.length() > 0 && (formSet2 = this.getFormSets().get(key)) != null) {
            form = formSet2.getForm(formKey);
        }
        String localeKey = key;
        if (form == null && (key = this.buildLocale(language, country, null)).length() > 0 && (formSet = this.getFormSets().get(key)) != null) {
            form = formSet.getForm(formKey);
        }
        if (form == null && (key = this.buildLocale(language, null, null)).length() > 0 && (formSet = this.getFormSets().get(key)) != null) {
            form = formSet.getForm(formKey);
        }
        if (form == null) {
            form = this.defaultFormSet.getForm(formKey);
            key = "default";
        }
        if (form == null) {
            if (this.getLog().isWarnEnabled()) {
                this.getLog().warn((Object)("Form '" + formKey + "' not found for locale '" + localeKey + "'"));
            }
        } else if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Form '" + formKey + "' found in formset '" + key + "' for locale '" + localeKey + "'"));
        }
        return form;
    }

    public void process() {
        this.hFormSets.setFast(true);
        this.hConstants.setFast(true);
        this.hActions.setFast(true);
        this.processForms();
    }

    private void processForms() {
        if (this.defaultFormSet == null) {
            this.defaultFormSet = new FormSet();
        }
        this.defaultFormSet.process(this.getConstants());
        for (String key : this.getFormSets().keySet()) {
            FormSet fs = this.getFormSets().get(key);
            fs.merge(this.getParent(fs));
        }
        for (FormSet fs : this.getFormSets().values()) {
            if (fs.isProcessed()) continue;
            fs.process(this.getConstants());
        }
    }

    private FormSet getParent(FormSet fs) {
        FormSet parent = null;
        if (fs.getType() == 2) {
            parent = this.defaultFormSet;
        } else if (fs.getType() == 3) {
            parent = this.getFormSets().get(this.buildLocale(fs.getLanguage(), null, null));
            if (parent == null) {
                parent = this.defaultFormSet;
            }
        } else if (fs.getType() == 4 && (parent = this.getFormSets().get(this.buildLocale(fs.getLanguage(), fs.getCountry(), null))) == null && (parent = this.getFormSets().get(this.buildLocale(fs.getLanguage(), null, null))) == null) {
            parent = this.defaultFormSet;
        }
        return parent;
    }

    FormSet getFormSet(String language, String country, String variant) {
        String key = this.buildLocale(language, country, variant);
        if (key.length() == 0) {
            return this.defaultFormSet;
        }
        return this.getFormSets().get(key);
    }

    protected Map<String, FormSet> getFormSets() {
        return this.hFormSets;
    }

    protected Map<String, String> getConstants() {
        return this.hConstants;
    }

    protected Map<String, ValidatorAction> getActions() {
        return this.hActions;
    }

    private Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(ValidatorResources.class);
        }
        return this.log;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.validator;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Form;

public class FormSet
implements Serializable {
    private static final long serialVersionUID = -8936513232763306055L;
    private transient Log log = LogFactory.getLog(FormSet.class);
    private boolean processed = false;
    private String language = null;
    private String country = null;
    private String variant = null;
    private final Map<String, Form> forms = new HashMap<String, Form>();
    private final Map<String, String> constants = new HashMap<String, String>();
    protected static final int GLOBAL_FORMSET = 1;
    protected static final int LANGUAGE_FORMSET = 2;
    protected static final int COUNTRY_FORMSET = 3;
    protected static final int VARIANT_FORMSET = 4;
    private boolean merged;

    protected boolean isMerged() {
        return this.merged;
    }

    protected int getType() {
        if (this.getVariant() != null) {
            if (this.getLanguage() == null || this.getCountry() == null) {
                throw new NullPointerException("When variant is specified, country and language must be specified.");
            }
            return 4;
        }
        if (this.getCountry() != null) {
            if (this.getLanguage() == null) {
                throw new NullPointerException("When country is specified, language must be specified.");
            }
            return 3;
        }
        if (this.getLanguage() != null) {
            return 2;
        }
        return 1;
    }

    protected void merge(FormSet depends) {
        if (depends != null) {
            Map<String, Form> pForms = this.getForms();
            Map<String, Form> dForms = depends.getForms();
            for (Map.Entry<String, Form> entry : dForms.entrySet()) {
                String key = entry.getKey();
                Form pForm = pForms.get(key);
                if (pForm != null) {
                    pForm.merge(entry.getValue());
                    continue;
                }
                this.addForm(entry.getValue());
            }
        }
        this.merged = true;
    }

    public boolean isProcessed() {
        return this.processed;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getVariant() {
        return this.variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public void addConstant(String name, String value) {
        if (this.constants.containsKey(name)) {
            this.getLog().error((Object)("Constant '" + name + "' already exists in FormSet[" + this.displayKey() + "] - ignoring."));
        } else {
            this.constants.put(name, value);
        }
    }

    public void addForm(Form f) {
        String formName = f.getName();
        if (this.forms.containsKey(formName)) {
            this.getLog().error((Object)("Form '" + formName + "' already exists in FormSet[" + this.displayKey() + "] - ignoring."));
        } else {
            this.forms.put(f.getName(), f);
        }
    }

    public Form getForm(String formName) {
        return this.forms.get(formName);
    }

    public Map<String, Form> getForms() {
        return Collections.unmodifiableMap(this.forms);
    }

    synchronized void process(Map<String, String> globalConstants) {
        for (Form f : this.forms.values()) {
            f.process(globalConstants, this.constants, this.forms);
        }
        this.processed = true;
    }

    public String displayKey() {
        StringBuilder results = new StringBuilder();
        if (this.language != null && this.language.length() > 0) {
            results.append("language=");
            results.append(this.language);
        }
        if (this.country != null && this.country.length() > 0) {
            if (results.length() > 0) {
                results.append(", ");
            }
            results.append("country=");
            results.append(this.country);
        }
        if (this.variant != null && this.variant.length() > 0) {
            if (results.length() > 0) {
                results.append(", ");
            }
            results.append("variant=");
            results.append(this.variant);
        }
        if (results.length() == 0) {
            results.append("default");
        }
        return results.toString();
    }

    public String toString() {
        StringBuilder results = new StringBuilder();
        results.append("FormSet: language=");
        results.append(this.language);
        results.append("  country=");
        results.append(this.country);
        results.append("  variant=");
        results.append(this.variant);
        results.append("\n");
        Iterator<Form> i = this.getForms().values().iterator();
        while (i.hasNext()) {
            results.append("   ");
            results.append(i.next());
            results.append("\n");
        }
        return results.toString();
    }

    private Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(FormSet.class);
        }
        return this.log;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.digester.AbstractObjectCreationFactory
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.validator;

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.FormSet;
import org.apache.commons.validator.ValidatorResources;
import org.xml.sax.Attributes;

public class FormSetFactory
extends AbstractObjectCreationFactory {
    private transient Log log = LogFactory.getLog(FormSetFactory.class);

    public Object createObject(Attributes attributes) throws Exception {
        ValidatorResources resources = (ValidatorResources)this.digester.peek(0);
        String language = attributes.getValue("language");
        String country = attributes.getValue("country");
        String variant = attributes.getValue("variant");
        return this.createFormSet(resources, language, country, variant);
    }

    private FormSet createFormSet(ValidatorResources resources, String language, String country, String variant) throws Exception {
        FormSet formSet = resources.getFormSet(language, country, variant);
        if (formSet != null) {
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)("FormSet[" + formSet.displayKey() + "] found - merging."));
            }
            return formSet;
        }
        formSet = new FormSet();
        formSet.setLanguage(language);
        formSet.setCountry(country);
        formSet.setVariant(variant);
        resources.addFormSet(formSet);
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("FormSet[" + formSet.displayKey() + "] created."));
        }
        return formSet;
    }

    private Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(FormSetFactory.class);
        }
        return this.log;
    }
}


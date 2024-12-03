/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.FastHashMap
 */
package org.apache.commons.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResults;

public class Form
implements Serializable {
    private static final long serialVersionUID = 6445211789563796371L;
    protected String name = null;
    protected List<Field> lFields = new ArrayList<Field>();
    @Deprecated
    protected FastHashMap hFields = new FastHashMap();
    protected String inherit = null;
    private boolean processed = false;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addField(Field f) {
        this.lFields.add(f);
        this.getFieldMap().put(f.getKey(), f);
    }

    public List<Field> getFields() {
        return Collections.unmodifiableList(this.lFields);
    }

    public Field getField(String fieldName) {
        return this.getFieldMap().get(fieldName);
    }

    public boolean containsField(String fieldName) {
        return this.getFieldMap().containsKey(fieldName);
    }

    protected void merge(Form depends) {
        ArrayList<Field> templFields = new ArrayList<Field>();
        FastHashMap temphFields = new FastHashMap();
        for (Field defaultField : depends.getFields()) {
            if (defaultField == null) continue;
            String fieldKey = defaultField.getKey();
            if (!this.containsField(fieldKey)) {
                templFields.add(defaultField);
                temphFields.put(fieldKey, defaultField);
                continue;
            }
            Field old = this.getField(fieldKey);
            this.getFieldMap().remove(fieldKey);
            this.lFields.remove(old);
            templFields.add(old);
            temphFields.put(fieldKey, old);
        }
        this.lFields.addAll(0, templFields);
        this.getFieldMap().putAll((Map<String, Field>)temphFields);
    }

    protected void process(Map<String, String> globalConstants, Map<String, String> constants, Map<String, Form> forms) {
        Form parent;
        if (this.isProcessed()) {
            return;
        }
        int n = 0;
        if (this.isExtending() && (parent = forms.get(this.inherit)) != null) {
            if (!parent.isProcessed()) {
                parent.process(constants, globalConstants, forms);
            }
            for (Field f : parent.getFields()) {
                if (this.getFieldMap().get(f.getKey()) != null) continue;
                this.lFields.add(n, f);
                this.getFieldMap().put(f.getKey(), f);
                ++n;
            }
        }
        this.hFields.setFast(true);
        ListIterator<Field> i = this.lFields.listIterator(n);
        while (i.hasNext()) {
            Field f = (Field)i.next();
            f.process(globalConstants, constants);
        }
        this.processed = true;
    }

    public String toString() {
        StringBuilder results = new StringBuilder();
        results.append("Form: ");
        results.append(this.name);
        results.append("\n");
        Iterator<Field> i = this.lFields.iterator();
        while (i.hasNext()) {
            results.append("\tField: \n");
            results.append(i.next());
            results.append("\n");
        }
        return results.toString();
    }

    ValidatorResults validate(Map<String, Object> params, Map<String, ValidatorAction> actions, int page) throws ValidatorException {
        return this.validate(params, actions, page, null);
    }

    ValidatorResults validate(Map<String, Object> params, Map<String, ValidatorAction> actions, int page, String fieldName) throws ValidatorException {
        ValidatorResults results = new ValidatorResults();
        params.put("org.apache.commons.validator.ValidatorResults", results);
        if (fieldName != null) {
            Field field = this.getFieldMap().get(fieldName);
            if (field == null) {
                throw new ValidatorException("Unknown field " + fieldName + " in form " + this.getName());
            }
            params.put("org.apache.commons.validator.Field", field);
            if (field.getPage() <= page) {
                results.merge(field.validate(params, actions));
            }
        } else {
            for (Field field : this.lFields) {
                params.put("org.apache.commons.validator.Field", field);
                if (field.getPage() > page) continue;
                results.merge(field.validate(params, actions));
            }
        }
        return results;
    }

    public boolean isProcessed() {
        return this.processed;
    }

    public String getExtends() {
        return this.inherit;
    }

    public void setExtends(String inherit) {
        this.inherit = inherit;
    }

    public boolean isExtending() {
        return this.inherit != null;
    }

    protected Map<String, Field> getFieldMap() {
        return this.hFields;
    }
}


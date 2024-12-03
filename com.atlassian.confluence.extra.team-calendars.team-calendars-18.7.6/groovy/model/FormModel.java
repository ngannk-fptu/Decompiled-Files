/*
 * Decompiled with CFR 0.152.
 */
package groovy.model;

import java.util.HashMap;
import java.util.Map;

public class FormModel {
    private Map fieldModels;

    public FormModel() {
        this(new HashMap());
    }

    public FormModel(Map fieldModels) {
        this.fieldModels = fieldModels;
    }

    public void addModel(String name, Object model) {
        this.fieldModels.put(name, model);
    }

    public Object getModel(String name) {
        return this.fieldModels.get(name);
    }
}


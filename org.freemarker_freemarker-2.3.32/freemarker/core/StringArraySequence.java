/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;

public class StringArraySequence
implements TemplateSequenceModel {
    private String[] stringArray;
    private TemplateScalarModel[] array;

    public StringArraySequence(String[] stringArray) {
        this.stringArray = stringArray;
    }

    @Override
    public TemplateModel get(int index) {
        TemplateScalarModel result;
        if (this.array == null) {
            this.array = new TemplateScalarModel[this.stringArray.length];
        }
        if ((result = this.array[index]) == null) {
            this.array[index] = result = new SimpleScalar(this.stringArray[index]);
        }
        return result;
    }

    @Override
    public int size() {
        return this.stringArray.length;
    }
}


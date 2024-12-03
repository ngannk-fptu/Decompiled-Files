/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.ext.thread;

import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

public class Total
extends ElementWrapper {
    public Total(Element internal) {
        super(internal);
    }

    public Total(Factory factory) {
        super(factory, ThreadConstants.THRTOTAL);
    }

    public int getValue() {
        String val = this.getText();
        return val != null ? Integer.parseInt(val) : -1;
    }

    public void setValue(int value) {
        this.setText(String.valueOf(value));
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.tags.form;

import org.springframework.web.servlet.tags.form.AbstractMultiCheckedElementTag;

public class RadioButtonsTag
extends AbstractMultiCheckedElementTag {
    @Override
    protected String getInputType() {
        return "radio";
    }
}


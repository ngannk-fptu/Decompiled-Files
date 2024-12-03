/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jndi;

import java.beans.PropertyEditorSupport;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;

public class JndiTemplateEditor
extends PropertyEditorSupport {
    private final PropertiesEditor propertiesEditor = new PropertiesEditor();

    @Override
    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("JndiTemplate cannot be created from null string");
        }
        if ("".equals(text)) {
            this.setValue(new JndiTemplate());
        } else {
            this.propertiesEditor.setAsText(text);
            Properties props = (Properties)this.propertiesEditor.getValue();
            this.setValue(new JndiTemplate(props));
        }
    }
}


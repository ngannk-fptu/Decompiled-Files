/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.propertyeditors.PropertiesEditor
 *  org.springframework.util.StringUtils
 */
package org.springframework.transaction.interceptor;

import java.beans.PropertyEditorSupport;
import java.util.Enumeration;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.transaction.interceptor.MethodMapTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeEditor;
import org.springframework.util.StringUtils;

public class TransactionAttributeSourceEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        MethodMapTransactionAttributeSource source = new MethodMapTransactionAttributeSource();
        if (StringUtils.hasLength((String)text)) {
            PropertiesEditor propertiesEditor = new PropertiesEditor();
            propertiesEditor.setAsText(text);
            Properties props = (Properties)propertiesEditor.getValue();
            TransactionAttributeEditor tae = new TransactionAttributeEditor();
            Enumeration<?> propNames = props.propertyNames();
            while (propNames.hasMoreElements()) {
                String name = (String)propNames.nextElement();
                String value = props.getProperty(name);
                tae.setAsText(value);
                TransactionAttribute attr = (TransactionAttribute)tae.getValue();
                source.addTransactionalMethod(name, attr);
            }
        }
        this.setValue(source);
    }
}


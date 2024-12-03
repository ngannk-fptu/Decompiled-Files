/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorWritingException;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.util.Pool;
import java.beans.PropertyEditor;

public class ThreadSafePropertyEditor {
    private final Class editorType;
    private final Pool pool;

    public ThreadSafePropertyEditor(Class type, int initialPoolSize, int maxPoolSize) {
        if (!PropertyEditor.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not a " + PropertyEditor.class.getName());
        }
        this.editorType = type;
        this.pool = new Pool(initialPoolSize, maxPoolSize, new Pool.Factory(){

            public Object newInstance() {
                ErrorWritingException ex = null;
                try {
                    return ThreadSafePropertyEditor.this.editorType.newInstance();
                }
                catch (InstantiationException e) {
                    ex = new ConversionException("Faild to call default constructor", e);
                }
                catch (IllegalAccessException e) {
                    ex = new ObjectAccessException("Cannot call default constructor", e);
                }
                ex.add("construction-type", ThreadSafePropertyEditor.this.editorType.getName());
                throw ex;
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getAsText(Object object) {
        PropertyEditor editor = this.fetchFromPool();
        try {
            editor.setValue(object);
            String string = editor.getAsText();
            return string;
        }
        finally {
            this.pool.putInPool(editor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object setAsText(String str) {
        PropertyEditor editor = this.fetchFromPool();
        try {
            editor.setAsText(str);
            Object object = editor.getValue();
            return object;
        }
        finally {
            this.pool.putInPool(editor);
        }
    }

    private PropertyEditor fetchFromPool() {
        PropertyEditor editor = (PropertyEditor)this.pool.fetchFromPool();
        return editor;
    }
}


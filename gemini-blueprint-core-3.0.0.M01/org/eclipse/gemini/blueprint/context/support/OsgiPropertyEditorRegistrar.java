/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.PropertyEditorRegistrar
 *  org.springframework.beans.PropertyEditorRegistry
 *  org.springframework.beans.propertyeditors.ClassArrayEditor
 *  org.springframework.beans.propertyeditors.ClassEditor
 *  org.springframework.beans.propertyeditors.CustomMapEditor
 *  org.springframework.beans.propertyeditors.PropertiesEditor
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.context.support;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.util.Assert;

class OsgiPropertyEditorRegistrar
implements PropertyEditorRegistrar {
    private static final Log log = LogFactory.getLog(OsgiPropertyEditorRegistrar.class);
    private static final String PROPERTIES_FILE = "/org/eclipse/gemini/blueprint/context/support/internal/default-property-editors.properties";
    private final Map<Class<?>, Class<? extends PropertyEditor>> editors;
    private final ClassLoader userClassLoader;

    OsgiPropertyEditorRegistrar(ClassLoader userClassLoader) {
        this.userClassLoader = userClassLoader;
        Properties editorsConfig = new Properties();
        InputStream stream = null;
        try {
            stream = this.getClass().getResourceAsStream(PROPERTIES_FILE);
            editorsConfig.load(stream);
        }
        catch (IOException ex) {
            throw (RuntimeException)new IllegalStateException("cannot load default propertiy editorsConfig configuration").initCause(ex);
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException iOException) {}
            }
        }
        if (log.isTraceEnabled()) {
            log.trace((Object)("Loaded property editors configuration " + editorsConfig));
        }
        this.editors = new LinkedHashMap(editorsConfig.size());
        this.createEditors(editorsConfig);
    }

    private void createEditors(Properties configuration) {
        boolean trace = log.isTraceEnabled();
        ClassLoader classLoader = this.getClass().getClassLoader();
        for (Map.Entry<Object, Object> entry : configuration.entrySet()) {
            Class<?> editorClass;
            Class<?> key;
            try {
                key = classLoader.loadClass((String)entry.getKey());
                editorClass = classLoader.loadClass((String)entry.getValue());
            }
            catch (ClassNotFoundException ex) {
                throw (RuntimeException)new IllegalArgumentException("Cannot load class").initCause(ex);
            }
            Assert.isAssignable(PropertyEditor.class, editorClass);
            if (trace) {
                log.trace((Object)("Adding property editor[" + editorClass + "] for type[" + key + "]"));
            }
            this.editors.put(key, editorClass);
        }
    }

    public void registerCustomEditors(PropertyEditorRegistry registry) {
        for (Map.Entry<Class<?>, Class<PropertyEditor>> entry : this.editors.entrySet()) {
            Class<?> type = entry.getKey();
            PropertyEditor editorInstance = (PropertyEditor)BeanUtils.instantiate(entry.getValue());
            registry.registerCustomEditor(type, editorInstance);
        }
        registry.registerCustomEditor(Dictionary.class, (PropertyEditor)new CustomMapEditor(Hashtable.class));
        registry.registerCustomEditor(Properties.class, (PropertyEditor)new PropertiesEditor());
        registry.registerCustomEditor(Class.class, (PropertyEditor)new ClassEditor(this.userClassLoader));
        registry.registerCustomEditor(Class[].class, (PropertyEditor)new ClassArrayEditor(this.userClassLoader));
    }
}


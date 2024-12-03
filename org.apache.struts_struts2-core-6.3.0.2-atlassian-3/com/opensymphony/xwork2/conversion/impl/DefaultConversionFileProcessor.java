/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultConversionFileProcessor
implements ConversionFileProcessor {
    private static final Logger LOG = LogManager.getLogger(DefaultConversionFileProcessor.class);
    private FileManager fileManager;
    private TypeConverterCreator converterCreator;

    @Inject
    public void setFileManagerFactory(FileManagerFactory factory) {
        this.fileManager = factory.getFileManager();
    }

    @Inject
    public void setTypeConverterCreator(TypeConverterCreator converterCreator) {
        this.converterCreator = converterCreator;
    }

    @Override
    public void process(Map<String, Object> mapping, Class clazz, String converterFilename) {
        try {
            InputStream is = this.fileManager.loadFile(ClassLoaderUtil.getResource(converterFilename, clazz));
            if (is != null) {
                Map.Entry<Object, Object> entry;
                String key;
                LOG.debug("Processing conversion file [{}] for class [{}]", (Object)converterFilename, (Object)clazz);
                Properties prop = new Properties();
                prop.load(is);
                Iterator<Map.Entry<Object, Object>> iterator = prop.entrySet().iterator();
                while (iterator.hasNext() && !mapping.containsKey(key = (String)(entry = iterator.next()).getKey())) {
                    if (key.startsWith("KeyProperty_") || key.startsWith("CreateIfNull_")) {
                        LOG.debug("\t{}:{} [treated as String]", (Object)key, entry.getValue());
                        mapping.put(key, entry.getValue());
                        continue;
                    }
                    if (!(key.startsWith("Element_") || key.startsWith("Key_") || key.startsWith("Collection_"))) {
                        TypeConverter _typeConverter = this.converterCreator.createTypeConverter((String)entry.getValue());
                        LOG.debug("\t{}:{} [treated as TypeConverter {}]", (Object)key, entry.getValue(), (Object)_typeConverter);
                        mapping.put(key, _typeConverter);
                        continue;
                    }
                    if (key.startsWith("Key_")) {
                        Class converterClass = ClassLoaderUtil.loadClass((String)entry.getValue(), this.getClass());
                        if (converterClass.isAssignableFrom(TypeConverter.class)) {
                            TypeConverter _typeConverter = this.converterCreator.createTypeConverter((String)entry.getValue());
                            LOG.debug("\t{}:{} [treated as TypeConverter {}]", (Object)key, entry.getValue(), (Object)_typeConverter);
                            mapping.put(key, _typeConverter);
                            continue;
                        }
                        LOG.debug("\t{}:{} [treated as Class {}]", (Object)key, entry.getValue(), (Object)converterClass);
                        mapping.put(key, converterClass);
                        continue;
                    }
                    Class _c = ClassLoaderUtil.loadClass((String)entry.getValue(), this.getClass());
                    LOG.debug("\t{}:{} [treated as Class {}]", (Object)key, entry.getValue(), (Object)_c);
                    mapping.put(key, _c);
                }
            }
        }
        catch (Exception ex) {
            LOG.error("Problem loading properties for {}", (Object)clazz.getName(), (Object)ex);
        }
    }
}


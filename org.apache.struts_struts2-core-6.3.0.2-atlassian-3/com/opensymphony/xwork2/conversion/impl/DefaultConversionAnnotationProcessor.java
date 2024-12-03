/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultConversionAnnotationProcessor
implements ConversionAnnotationProcessor {
    private static final Logger LOG = LogManager.getLogger(DefaultConversionAnnotationProcessor.class);
    private TypeConverterCreator converterCreator;
    private TypeConverterHolder converterHolder;

    @Inject
    public void setTypeConverterCreator(TypeConverterCreator converterCreator) {
        this.converterCreator = converterCreator;
    }

    @Inject
    public void setTypeConverterHolder(TypeConverterHolder converterHolder) {
        this.converterHolder = converterHolder;
    }

    @Override
    public void process(Map<String, Object> mapping, TypeConversion tc, String key) {
        LOG.debug("TypeConversion [{}/{}] with key: [{}]", (Object)tc.converter(), tc.converterClass(), (Object)key);
        if (key == null) {
            return;
        }
        try {
            if (tc.type() == ConversionType.APPLICATION) {
                if (StringUtils.isNoneEmpty((CharSequence[])new CharSequence[]{tc.converter()})) {
                    this.converterHolder.addDefaultMapping(key, this.converterCreator.createTypeConverter(tc.converter()));
                } else {
                    this.converterHolder.addDefaultMapping(key, this.converterCreator.createTypeConverter(tc.converterClass()));
                }
            } else if (tc.rule() == ConversionRule.KEY_PROPERTY || tc.rule() == ConversionRule.CREATE_IF_NULL) {
                mapping.put(key, tc.value());
            } else if (tc.rule() != ConversionRule.ELEMENT && tc.rule() != ConversionRule.KEY && tc.rule() != ConversionRule.COLLECTION) {
                if (StringUtils.isNoneEmpty((CharSequence[])new CharSequence[]{tc.converter()})) {
                    mapping.put(key, this.converterCreator.createTypeConverter(tc.converter()));
                } else {
                    mapping.put(key, this.converterCreator.createTypeConverter(tc.converterClass()));
                }
            } else if (tc.rule() == ConversionRule.KEY) {
                Class converterClass = StringUtils.isNoneEmpty((CharSequence[])new CharSequence[]{tc.converter()}) ? ClassLoaderUtil.loadClass(tc.converter(), this.getClass()) : tc.converterClass();
                LOG.debug("Converter class: [{}]", (Object)converterClass);
                if (converterClass.isAssignableFrom(TypeConverter.class)) {
                    if (StringUtils.isNoneEmpty((CharSequence[])new CharSequence[]{tc.converter()})) {
                        mapping.put(key, this.converterCreator.createTypeConverter(tc.converter()));
                    } else {
                        mapping.put(key, this.converterCreator.createTypeConverter(tc.converterClass()));
                    }
                } else {
                    mapping.put(key, converterClass);
                    LOG.debug("Object placed in mapping for key [{}] is [{}]", (Object)key, mapping.get(key));
                }
            } else if (StringUtils.isNoneEmpty((CharSequence[])new CharSequence[]{tc.converter()})) {
                mapping.put(key, ClassLoaderUtil.loadClass(tc.converter(), this.getClass()));
            } else {
                mapping.put(key, tc.converterClass());
            }
        }
        catch (Exception e) {
            LOG.debug("Got exception for {}", (Object)key, (Object)e);
        }
    }
}


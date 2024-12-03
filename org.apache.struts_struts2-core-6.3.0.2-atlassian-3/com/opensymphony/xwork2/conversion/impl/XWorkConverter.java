/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkBasicConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XWorkConverter
extends DefaultTypeConverter {
    private static final Logger LOG = LogManager.getLogger(XWorkConverter.class);
    public static final String REPORT_CONVERSION_ERRORS = "report.conversion.errors";
    public static final String CONVERSION_PROPERTY_FULLNAME = "conversion.property.fullName";
    public static final String CONVERSION_ERROR_PROPERTY_PREFIX = "invalid.fieldvalue.";
    public static final String CONVERSION_COLLECTION_PREFIX = "Collection_";
    public static final String LAST_BEAN_CLASS_ACCESSED = "last.bean.accessed";
    public static final String LAST_BEAN_PROPERTY_ACCESSED = "last.property.accessed";
    public static final String MESSAGE_INDEX_PATTERN = "\\[\\d+\\]\\.";
    public static final String MESSAGE_INDEX_BRACKET_PATTERN = "[\\[\\]\\.]";
    public static final String PERIOD = ".";
    public static final Pattern messageIndexPattern = Pattern.compile("\\[\\d+\\]\\.");
    private TypeConverter defaultTypeConverter;
    private FileManager fileManager;
    private boolean reloadingConfigs;
    private ConversionFileProcessor fileProcessor;
    private ConversionAnnotationProcessor annotationProcessor;
    private TypeConverterHolder converterHolder;

    protected XWorkConverter() {
    }

    @Inject
    public void setDefaultTypeConverter(XWorkBasicConverter converter) {
        this.defaultTypeConverter = converter;
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject(value="struts.configuration.xml.reload", required=false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    @Inject
    public void setConversionFileProcessor(ConversionFileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    @Inject
    public void setConversionAnnotationProcessor(ConversionAnnotationProcessor annotationProcessor) {
        this.annotationProcessor = annotationProcessor;
    }

    @Inject
    public void setTypeConverterHolder(TypeConverterHolder converterHolder) {
        this.converterHolder = converterHolder;
    }

    public static String getConversionErrorMessage(String propertyName, Class toClass, ValueStack stack) {
        LocalizedTextProvider localizedTextProvider = ActionContext.getContext().getContainer().getInstance(LocalizedTextProvider.class);
        String defaultMessage = localizedTextProvider.findDefaultText("xwork.default.invalid.fieldvalue", ActionContext.getContext().getLocale(), new Object[]{propertyName});
        List<String> indexValues = XWorkConverter.getIndexValues(propertyName);
        propertyName = XWorkConverter.removeAllIndexesInPropertyName(propertyName);
        String prefixedPropertyName = CONVERSION_ERROR_PROPERTY_PREFIX + propertyName;
        String getTextExpression = "getText('" + prefixedPropertyName + "')";
        String message = (String)stack.findValue(getTextExpression);
        if (message == null || prefixedPropertyName.equals(message)) {
            getTextExpression = "getText('invalid.fieldvalue." + toClass.getName() + "','" + defaultMessage + "')";
            message = (String)stack.findValue(getTextExpression);
        }
        message = message == null ? defaultMessage : MessageFormat.format(message, indexValues.toArray());
        return message;
    }

    private static String removeAllIndexesInPropertyName(String propertyName) {
        return propertyName.replaceAll(MESSAGE_INDEX_PATTERN, PERIOD);
    }

    private static List<String> getIndexValues(String propertyName) {
        Matcher matcher = messageIndexPattern.matcher(propertyName);
        ArrayList<String> indexes = new ArrayList<String>();
        while (matcher.find()) {
            Integer index = new Integer(matcher.group().replaceAll(MESSAGE_INDEX_BRACKET_PATTERN, "")) + 1;
            indexes.add(Integer.toString(index));
        }
        return indexes;
    }

    public String buildConverterFilename(Class clazz) {
        String className = clazz.getName();
        return className.replace('.', '/') + "-conversion.properties";
    }

    @Override
    public Object convertValue(Map<String, Object> map, Object o, Class aClass) {
        return this.convertValue(map, null, null, null, o, aClass);
    }

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String property, Object value, Class toClass) {
        TypeConverter tc = null;
        if (value != null && toClass == value.getClass()) {
            return value;
        }
        if (target != null) {
            Class clazz = target.getClass();
            Object[] classProp = null;
            if (target instanceof CompoundRoot && context != null) {
                classProp = this.getClassProperty(context);
            }
            if (classProp != null) {
                clazz = (Class)classProp[0];
                property = (String)classProp[1];
            }
            LOG.debug("field-level type converter for property [{}] = {}", (Object)property, (tc = (TypeConverter)this.getConverter(clazz, property)) == null ? "none found" : tc);
        }
        if (tc == null && context != null) {
            Object lastPropertyPath = context.get("current.property.path");
            Class clazz = (Class)context.get(LAST_BEAN_CLASS_ACCESSED);
            if (lastPropertyPath != null && clazz != null) {
                String path = lastPropertyPath + PERIOD + property;
                tc = (TypeConverter)this.getConverter(clazz, path);
            }
        }
        if (tc == null) {
            tc = toClass.equals(String.class) && value != null && !value.getClass().equals(String.class) && !value.getClass().equals(String[].class) ? this.lookup(value.getClass()) : this.lookup(toClass);
            if (LOG.isDebugEnabled()) {
                LOG.debug("global-level type converter for property [{}] = {} ", (Object)property, tc == null ? "none found" : tc);
            }
        }
        if (tc != null) {
            try {
                return tc.convertValue(context, target, member, property, value, toClass);
            }
            catch (Exception e) {
                LOG.debug("Unable to convert value using type converter [{}]", (Object)tc.getClass().getName(), (Object)e);
                this.handleConversionException(context, property, value, target, toClass);
                return TypeConverter.NO_CONVERSION_POSSIBLE;
            }
        }
        if (this.defaultTypeConverter != null) {
            try {
                LOG.debug("Falling back to default type converter [{}]", (Object)this.defaultTypeConverter);
                return this.defaultTypeConverter.convertValue(context, target, member, property, value, toClass);
            }
            catch (Exception e) {
                LOG.debug("Unable to convert value using type converter [{}]", (Object)this.defaultTypeConverter.getClass().getName(), (Object)e);
                this.handleConversionException(context, property, value, target, toClass);
                return TypeConverter.NO_CONVERSION_POSSIBLE;
            }
        }
        try {
            LOG.debug("Falling back to Ognl's default type conversion");
            return super.convertValue(value, toClass);
        }
        catch (Exception e) {
            LOG.debug("Unable to convert value using type converter [{}]", (Object)super.getClass().getName(), (Object)e);
            this.handleConversionException(context, property, value, target, toClass);
            return TypeConverter.NO_CONVERSION_POSSIBLE;
        }
    }

    public TypeConverter lookup(String className, boolean isPrimitive) {
        if (this.converterHolder.containsUnknownMapping(className) && !this.converterHolder.containsDefaultMapping(className)) {
            return null;
        }
        TypeConverter result = this.converterHolder.getDefaultMapping(className);
        if (result == null && !isPrimitive) {
            Class<?> clazz = null;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            }
            catch (ClassNotFoundException cnfe) {
                LOG.debug("Cannot load class {}", (Object)className, (Object)cnfe);
            }
            result = this.lookupSuper(clazz);
            if (result != null) {
                this.registerConverter(className, result);
            } else {
                this.registerConverterNotFound(className);
            }
        }
        return result;
    }

    public TypeConverter lookup(Class clazz) {
        TypeConverter result = this.lookup(clazz.getName(), clazz.isPrimitive());
        if (result == null && clazz.isPrimitive()) {
            return this.defaultTypeConverter;
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object getConverter(Class clazz, String property) {
        LOG.debug("Retrieving convert for class [{}] and property [{}]", (Object)clazz, (Object)property);
        Class clazz2 = clazz;
        synchronized (clazz2) {
            if (property != null && !this.converterHolder.containsNoMapping(clazz)) {
                try {
                    Map<String, Object> mapping = this.converterHolder.getMapping(clazz);
                    mapping = mapping == null ? this.buildConverterMapping(clazz) : this.conditionalReload(clazz, mapping);
                    Object converter = mapping.get(property);
                    if (converter == null && LOG.isDebugEnabled()) {
                        LOG.debug("Converter is null for property [{}]. Mapping size [{}]:", (Object)property, (Object)mapping.size());
                        for (Map.Entry<String, Object> entry : mapping.entrySet()) {
                            LOG.debug("{}:{}", (Object)entry.getKey(), entry.getValue());
                        }
                    }
                    return converter;
                }
                catch (Throwable t) {
                    LOG.debug("Got exception trying to resolve convert for class [{}] and property [{}]", (Object)clazz, (Object)property, (Object)t);
                    this.converterHolder.addNoMapping(clazz);
                }
            }
        }
        return null;
    }

    protected void handleConversionException(Map<String, Object> context, String property, Object value, Object object, Class toClass) {
        if (context != null && Boolean.TRUE.equals(context.get(REPORT_CONVERSION_ERRORS))) {
            ActionContext actionContext;
            Map<String, ConversionData> conversionErrors;
            String realProperty = property;
            String fullName = (String)context.get(CONVERSION_PROPERTY_FULLNAME);
            if (fullName != null) {
                realProperty = fullName;
            }
            if ((conversionErrors = (actionContext = ActionContext.of(context)).getConversionErrors()) == null) {
                conversionErrors = new HashMap<String, ConversionData>();
                actionContext.withConversionErrors(conversionErrors);
            }
            conversionErrors.put(realProperty, new ConversionData(value, toClass));
        }
    }

    public synchronized void registerConverter(String className, TypeConverter converter) {
        this.converterHolder.addDefaultMapping(className, converter);
    }

    public synchronized void registerConverterNotFound(String className) {
        this.converterHolder.addUnknownMapping(className);
    }

    private Object[] getClassProperty(Map<String, Object> context) {
        Object[] objectArray;
        Object lastClass = context.get(LAST_BEAN_CLASS_ACCESSED);
        Object lastProperty = context.get(LAST_BEAN_PROPERTY_ACCESSED);
        if (lastClass != null && lastProperty != null) {
            Object[] objectArray2 = new Object[2];
            objectArray2[0] = lastClass;
            objectArray = objectArray2;
            objectArray2[1] = lastProperty;
        } else {
            objectArray = null;
        }
        return objectArray;
    }

    protected void addConverterMapping(Map<String, Object> mapping, Class clazz) {
        TypeConversion tc;
        String converterFilename = this.buildConverterFilename(clazz);
        this.fileProcessor.process(mapping, clazz, converterFilename);
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (!(annotation instanceof Conversion)) continue;
            Conversion conversion = (Conversion)annotation;
            TypeConversion[] typeConversionArray = conversion.conversions();
            int n = typeConversionArray.length;
            for (int i = 0; i < n && !mapping.containsKey((tc = typeConversionArray[i]).key()); ++i) {
                if (LOG.isDebugEnabled()) {
                    if (StringUtils.isEmpty((CharSequence)tc.key())) {
                        LOG.debug("WARNING! key of @TypeConversion [{}/{}] applied to [{}] is empty!", (Object)tc.converter(), tc.converterClass(), (Object)clazz.getName());
                    } else {
                        LOG.debug("TypeConversion [{}/{}] with key: [{}]", (Object)tc.converter(), tc.converterClass(), (Object)tc.key());
                    }
                }
                this.annotationProcessor.process(mapping, tc, tc.key());
            }
        }
        block9: for (Method method : clazz.getMethods()) {
            for (Annotation annotation : annotations = method.getAnnotations()) {
                if (!(annotation instanceof TypeConversion)) continue;
                tc = (TypeConversion)annotation;
                String key = tc.key();
                if (StringUtils.isEmpty((CharSequence)key)) {
                    key = AnnotationUtils.resolvePropertyName(method);
                    switch (tc.rule()) {
                        case COLLECTION: {
                            key = CONVERSION_COLLECTION_PREFIX + key;
                            break;
                        }
                        case CREATE_IF_NULL: {
                            key = "CreateIfNull_" + key;
                            break;
                        }
                        case ELEMENT: {
                            key = "Element_" + key;
                            break;
                        }
                        case KEY: {
                            key = "Key_" + key;
                            break;
                        }
                        case KEY_PROPERTY: {
                            key = "KeyProperty_" + key;
                        }
                    }
                    LOG.debug("Retrieved key [{}] from method name [{}]", (Object)key, (Object)method.getName());
                }
                if (mapping.containsKey(key)) continue block9;
                this.annotationProcessor.process(mapping, tc, key);
            }
        }
    }

    protected Map<String, Object> buildConverterMapping(Class clazz) throws Exception {
        HashMap<String, Object> mapping = new HashMap<String, Object>();
        Class curClazz = clazz;
        while (!curClazz.equals(Object.class)) {
            Class<?>[] interfaces;
            this.addConverterMapping(mapping, curClazz);
            for (Class<?> anInterface : interfaces = curClazz.getInterfaces()) {
                this.addConverterMapping(mapping, anInterface);
            }
            curClazz = curClazz.getSuperclass();
        }
        if (mapping.size() > 0) {
            this.converterHolder.addMapping(clazz, mapping);
        } else {
            this.converterHolder.addNoMapping(clazz);
        }
        return mapping;
    }

    private Map<String, Object> conditionalReload(Class clazz, Map<String, Object> oldValues) throws Exception {
        URL fileUrl;
        Map<String, Object> mapping = oldValues;
        if (this.reloadingConfigs && this.fileManager.fileNeedsReloading(fileUrl = ClassLoaderUtil.getResource(this.buildConverterFilename(clazz), clazz))) {
            mapping = this.buildConverterMapping(clazz);
        }
        return mapping;
    }

    TypeConverter lookupSuper(Class clazz) {
        TypeConverter result = null;
        if (clazz != null && (result = this.converterHolder.getDefaultMapping(clazz.getName())) == null) {
            Class<?>[] interfaces;
            for (Class<?> anInterface : interfaces = clazz.getInterfaces()) {
                if (!this.converterHolder.containsDefaultMapping(anInterface.getName())) continue;
                result = this.converterHolder.getDefaultMapping(anInterface.getName());
                break;
            }
            if (result == null) {
                result = this.lookupSuper(clazz.getSuperclass());
            }
        }
        return result;
    }
}


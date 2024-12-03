/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.ConfigurablePropertyAccessor
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyAccessException
 *  org.springframework.beans.PropertyAccessorUtils
 *  org.springframework.beans.PropertyBatchUpdateException
 *  org.springframework.beans.PropertyEditorRegistry
 *  org.springframework.beans.PropertyValue
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.SimpleTypeConverter
 *  org.springframework.beans.TypeConverter
 *  org.springframework.beans.TypeMismatchException
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.PatternMatchUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.validation;

import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyBatchUpdateException;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.Formatter;
import org.springframework.format.support.FormatterPropertyEditorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingErrorProcessor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultBindingErrorProcessor;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

public class DataBinder
implements PropertyEditorRegistry,
TypeConverter {
    public static final String DEFAULT_OBJECT_NAME = "target";
    public static final int DEFAULT_AUTO_GROW_COLLECTION_LIMIT = 256;
    protected static final Log logger = LogFactory.getLog(DataBinder.class);
    @Nullable
    private final Object target;
    private final String objectName;
    @Nullable
    private AbstractPropertyBindingResult bindingResult;
    private boolean directFieldAccess = false;
    @Nullable
    private SimpleTypeConverter typeConverter;
    private boolean ignoreUnknownFields = true;
    private boolean ignoreInvalidFields = false;
    private boolean autoGrowNestedPaths = true;
    private int autoGrowCollectionLimit = 256;
    @Nullable
    private String[] allowedFields;
    @Nullable
    private String[] disallowedFields;
    @Nullable
    private String[] requiredFields;
    @Nullable
    private ConversionService conversionService;
    @Nullable
    private MessageCodesResolver messageCodesResolver;
    private BindingErrorProcessor bindingErrorProcessor = new DefaultBindingErrorProcessor();
    private final List<Validator> validators = new ArrayList<Validator>();

    public DataBinder(@Nullable Object target) {
        this(target, DEFAULT_OBJECT_NAME);
    }

    public DataBinder(@Nullable Object target, String objectName) {
        this.target = ObjectUtils.unwrapOptional((Object)target);
        this.objectName = objectName;
    }

    @Nullable
    public Object getTarget() {
        return this.target;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        Assert.state((this.bindingResult == null ? 1 : 0) != 0, (String)"DataBinder is already initialized - call setAutoGrowNestedPaths before other configuration methods");
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }

    public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
        Assert.state((this.bindingResult == null ? 1 : 0) != 0, (String)"DataBinder is already initialized - call setAutoGrowCollectionLimit before other configuration methods");
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }

    public int getAutoGrowCollectionLimit() {
        return this.autoGrowCollectionLimit;
    }

    public void initBeanPropertyAccess() {
        Assert.state((this.bindingResult == null ? 1 : 0) != 0, (String)"DataBinder is already initialized - call initBeanPropertyAccess before other configuration methods");
        this.directFieldAccess = false;
    }

    protected AbstractPropertyBindingResult createBeanPropertyBindingResult() {
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(this.getTarget(), this.getObjectName(), this.isAutoGrowNestedPaths(), this.getAutoGrowCollectionLimit());
        if (this.conversionService != null) {
            result.initConversion(this.conversionService);
        }
        if (this.messageCodesResolver != null) {
            result.setMessageCodesResolver(this.messageCodesResolver);
        }
        return result;
    }

    public void initDirectFieldAccess() {
        Assert.state((this.bindingResult == null ? 1 : 0) != 0, (String)"DataBinder is already initialized - call initDirectFieldAccess before other configuration methods");
        this.directFieldAccess = true;
    }

    protected AbstractPropertyBindingResult createDirectFieldBindingResult() {
        DirectFieldBindingResult result = new DirectFieldBindingResult(this.getTarget(), this.getObjectName(), this.isAutoGrowNestedPaths());
        if (this.conversionService != null) {
            result.initConversion(this.conversionService);
        }
        if (this.messageCodesResolver != null) {
            result.setMessageCodesResolver(this.messageCodesResolver);
        }
        return result;
    }

    protected AbstractPropertyBindingResult getInternalBindingResult() {
        if (this.bindingResult == null) {
            this.bindingResult = this.directFieldAccess ? this.createDirectFieldBindingResult() : this.createBeanPropertyBindingResult();
        }
        return this.bindingResult;
    }

    protected ConfigurablePropertyAccessor getPropertyAccessor() {
        return this.getInternalBindingResult().getPropertyAccessor();
    }

    protected SimpleTypeConverter getSimpleTypeConverter() {
        if (this.typeConverter == null) {
            this.typeConverter = new SimpleTypeConverter();
            if (this.conversionService != null) {
                this.typeConverter.setConversionService(this.conversionService);
            }
        }
        return this.typeConverter;
    }

    protected PropertyEditorRegistry getPropertyEditorRegistry() {
        if (this.getTarget() != null) {
            return this.getInternalBindingResult().getPropertyAccessor();
        }
        return this.getSimpleTypeConverter();
    }

    protected TypeConverter getTypeConverter() {
        if (this.getTarget() != null) {
            return this.getInternalBindingResult().getPropertyAccessor();
        }
        return this.getSimpleTypeConverter();
    }

    public BindingResult getBindingResult() {
        return this.getInternalBindingResult();
    }

    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }

    public boolean isIgnoreUnknownFields() {
        return this.ignoreUnknownFields;
    }

    public void setIgnoreInvalidFields(boolean ignoreInvalidFields) {
        this.ignoreInvalidFields = ignoreInvalidFields;
    }

    public boolean isIgnoreInvalidFields() {
        return this.ignoreInvalidFields;
    }

    public void setAllowedFields(String ... allowedFields) {
        this.allowedFields = PropertyAccessorUtils.canonicalPropertyNames((String[])allowedFields);
    }

    @Nullable
    public String[] getAllowedFields() {
        return this.allowedFields;
    }

    public void setDisallowedFields(String ... disallowedFields) {
        if (disallowedFields == null) {
            this.disallowedFields = null;
        } else {
            String[] fieldPatterns = new String[disallowedFields.length];
            for (int i = 0; i < fieldPatterns.length; ++i) {
                fieldPatterns[i] = PropertyAccessorUtils.canonicalPropertyName((String)disallowedFields[i]).toLowerCase();
            }
            this.disallowedFields = fieldPatterns;
        }
    }

    @Nullable
    public String[] getDisallowedFields() {
        return this.disallowedFields;
    }

    public void setRequiredFields(String ... requiredFields) {
        this.requiredFields = PropertyAccessorUtils.canonicalPropertyNames((String[])requiredFields);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("DataBinder requires binding of required fields [" + StringUtils.arrayToCommaDelimitedString((Object[])requiredFields) + "]"));
        }
    }

    @Nullable
    public String[] getRequiredFields() {
        return this.requiredFields;
    }

    public void setMessageCodesResolver(@Nullable MessageCodesResolver messageCodesResolver) {
        Assert.state((this.messageCodesResolver == null ? 1 : 0) != 0, (String)"DataBinder is already initialized with MessageCodesResolver");
        this.messageCodesResolver = messageCodesResolver;
        if (this.bindingResult != null && messageCodesResolver != null) {
            this.bindingResult.setMessageCodesResolver(messageCodesResolver);
        }
    }

    public void setBindingErrorProcessor(BindingErrorProcessor bindingErrorProcessor) {
        Assert.notNull((Object)bindingErrorProcessor, (String)"BindingErrorProcessor must not be null");
        this.bindingErrorProcessor = bindingErrorProcessor;
    }

    public BindingErrorProcessor getBindingErrorProcessor() {
        return this.bindingErrorProcessor;
    }

    public void setValidator(@Nullable Validator validator) {
        this.assertValidators(validator);
        this.validators.clear();
        if (validator != null) {
            this.validators.add(validator);
        }
    }

    private void assertValidators(Validator ... validators) {
        Object target = this.getTarget();
        for (Validator validator : validators) {
            if (validator == null || target == null || validator.supports(target.getClass())) continue;
            throw new IllegalStateException("Invalid target for Validator [" + validator + "]: " + target);
        }
    }

    public void addValidators(Validator ... validators) {
        this.assertValidators(validators);
        this.validators.addAll(Arrays.asList(validators));
    }

    public void replaceValidators(Validator ... validators) {
        this.assertValidators(validators);
        this.validators.clear();
        this.validators.addAll(Arrays.asList(validators));
    }

    @Nullable
    public Validator getValidator() {
        return !this.validators.isEmpty() ? this.validators.get(0) : null;
    }

    public List<Validator> getValidators() {
        return Collections.unmodifiableList(this.validators);
    }

    public void setConversionService(@Nullable ConversionService conversionService) {
        Assert.state((this.conversionService == null ? 1 : 0) != 0, (String)"DataBinder is already initialized with ConversionService");
        this.conversionService = conversionService;
        if (this.bindingResult != null && conversionService != null) {
            this.bindingResult.initConversion(conversionService);
        }
    }

    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    public void addCustomFormatter(Formatter<?> formatter) {
        FormatterPropertyEditorAdapter adapter = new FormatterPropertyEditorAdapter(formatter);
        this.getPropertyEditorRegistry().registerCustomEditor(adapter.getFieldType(), (PropertyEditor)adapter);
    }

    public void addCustomFormatter(Formatter<?> formatter, String ... fields) {
        FormatterPropertyEditorAdapter adapter = new FormatterPropertyEditorAdapter(formatter);
        Class<?> fieldType = adapter.getFieldType();
        if (ObjectUtils.isEmpty((Object[])fields)) {
            this.getPropertyEditorRegistry().registerCustomEditor(fieldType, (PropertyEditor)adapter);
        } else {
            for (String field : fields) {
                this.getPropertyEditorRegistry().registerCustomEditor(fieldType, field, (PropertyEditor)adapter);
            }
        }
    }

    public void addCustomFormatter(Formatter<?> formatter, Class<?> ... fieldTypes) {
        FormatterPropertyEditorAdapter adapter = new FormatterPropertyEditorAdapter(formatter);
        if (ObjectUtils.isEmpty((Object[])fieldTypes)) {
            this.getPropertyEditorRegistry().registerCustomEditor(adapter.getFieldType(), (PropertyEditor)adapter);
        } else {
            for (Class<?> fieldType : fieldTypes) {
                this.getPropertyEditorRegistry().registerCustomEditor(fieldType, (PropertyEditor)adapter);
            }
        }
    }

    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        this.getPropertyEditorRegistry().registerCustomEditor(requiredType, propertyEditor);
    }

    public void registerCustomEditor(@Nullable Class<?> requiredType, @Nullable String field, PropertyEditor propertyEditor) {
        this.getPropertyEditorRegistry().registerCustomEditor(requiredType, field, propertyEditor);
    }

    @Nullable
    public PropertyEditor findCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath) {
        return this.getPropertyEditorRegistry().findCustomEditor(requiredType, propertyPath);
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws TypeMismatchException {
        return (T)this.getTypeConverter().convertIfNecessary(value, requiredType);
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable MethodParameter methodParam) throws TypeMismatchException {
        return (T)this.getTypeConverter().convertIfNecessary(value, requiredType, methodParam);
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable Field field) throws TypeMismatchException {
        return (T)this.getTypeConverter().convertIfNecessary(value, requiredType, field);
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {
        return (T)this.getTypeConverter().convertIfNecessary(value, requiredType, typeDescriptor);
    }

    public void bind(PropertyValues pvs) {
        MutablePropertyValues mpvs = pvs instanceof MutablePropertyValues ? (MutablePropertyValues)pvs : new MutablePropertyValues(pvs);
        this.doBind(mpvs);
    }

    protected void doBind(MutablePropertyValues mpvs) {
        this.checkAllowedFields(mpvs);
        this.checkRequiredFields(mpvs);
        this.applyPropertyValues(mpvs);
    }

    protected void checkAllowedFields(MutablePropertyValues mpvs) {
        PropertyValue[] pvs;
        for (PropertyValue pv : pvs = mpvs.getPropertyValues()) {
            String field = PropertyAccessorUtils.canonicalPropertyName((String)pv.getName());
            if (this.isAllowed(field)) continue;
            mpvs.removePropertyValue(pv);
            this.getBindingResult().recordSuppressedField(field);
            if (!logger.isDebugEnabled()) continue;
            logger.debug((Object)("Field [" + field + "] has been removed from PropertyValues and will not be bound, because it has not been found in the list of allowed fields"));
        }
    }

    protected boolean isAllowed(String field) {
        Object[] allowed = this.getAllowedFields();
        Object[] disallowed = this.getDisallowedFields();
        return !(!ObjectUtils.isEmpty((Object[])allowed) && !PatternMatchUtils.simpleMatch((String[])allowed, (String)field) || !ObjectUtils.isEmpty((Object[])disallowed) && PatternMatchUtils.simpleMatch((String[])disallowed, (String)field.toLowerCase()));
    }

    protected void checkRequiredFields(MutablePropertyValues mpvs) {
        Object[] requiredFields = this.getRequiredFields();
        if (!ObjectUtils.isEmpty((Object[])requiredFields)) {
            PropertyValue[] pvs;
            HashMap<String, PropertyValue> propertyValues = new HashMap<String, PropertyValue>();
            for (PropertyValue propertyValue : pvs = mpvs.getPropertyValues()) {
                String canonicalName = PropertyAccessorUtils.canonicalPropertyName((String)propertyValue.getName());
                propertyValues.put(canonicalName, propertyValue);
            }
            for (Object object : requiredFields) {
                boolean empty;
                PropertyValue pv = (PropertyValue)propertyValues.get(object);
                boolean bl = empty = pv == null || pv.getValue() == null;
                if (!empty) {
                    if (pv.getValue() instanceof String) {
                        empty = !StringUtils.hasText((String)((String)pv.getValue()));
                    } else if (pv.getValue() instanceof String[]) {
                        String[] values = (String[])pv.getValue();
                        boolean bl2 = empty = values.length == 0 || !StringUtils.hasText((String)values[0]);
                    }
                }
                if (!empty) continue;
                this.getBindingErrorProcessor().processMissingFieldError((String)object, this.getInternalBindingResult());
                if (pv == null) continue;
                mpvs.removePropertyValue(pv);
                propertyValues.remove(object);
            }
        }
    }

    protected void applyPropertyValues(MutablePropertyValues mpvs) {
        try {
            this.getPropertyAccessor().setPropertyValues((PropertyValues)mpvs, this.isIgnoreUnknownFields(), this.isIgnoreInvalidFields());
        }
        catch (PropertyBatchUpdateException ex) {
            for (PropertyAccessException pae : ex.getPropertyAccessExceptions()) {
                this.getBindingErrorProcessor().processPropertyAccessException(pae, this.getInternalBindingResult());
            }
        }
    }

    public void validate() {
        Object target = this.getTarget();
        Assert.state((target != null ? 1 : 0) != 0, (String)"No target to validate");
        BindingResult bindingResult = this.getBindingResult();
        for (Validator validator : this.getValidators()) {
            validator.validate(target, bindingResult);
        }
    }

    public void validate(Object ... validationHints) {
        Object target = this.getTarget();
        Assert.state((target != null ? 1 : 0) != 0, (String)"No target to validate");
        BindingResult bindingResult = this.getBindingResult();
        for (Validator validator : this.getValidators()) {
            if (!ObjectUtils.isEmpty((Object[])validationHints) && validator instanceof SmartValidator) {
                ((SmartValidator)validator).validate(target, bindingResult, validationHints);
                continue;
            }
            if (validator == null) continue;
            validator.validate(target, bindingResult);
        }
    }

    public Map<?, ?> close() throws BindException {
        if (this.getBindingResult().hasErrors()) {
            throw new BindException(this.getBindingResult());
        }
        return this.getBindingResult().getModel();
    }
}


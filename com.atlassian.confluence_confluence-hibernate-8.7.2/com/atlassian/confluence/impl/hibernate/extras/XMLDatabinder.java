/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.EntityMode
 *  org.hibernate.HibernateException
 *  org.hibernate.MappingException
 *  org.hibernate.Session
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.internal.util.StringHelper
 *  org.hibernate.persister.collection.CollectionPersister
 *  org.hibernate.persister.entity.EntityPersister
 *  org.hibernate.proxy.HibernateProxy
 *  org.hibernate.proxy.LazyInitializer
 *  org.hibernate.type.BagType
 *  org.hibernate.type.BooleanType
 *  org.hibernate.type.CollectionType
 *  org.hibernate.type.ComponentType
 *  org.hibernate.type.DateType
 *  org.hibernate.type.ListType
 *  org.hibernate.type.MapType
 *  org.hibernate.type.SetType
 *  org.hibernate.type.StringRepresentableType
 *  org.hibernate.type.TimestampType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.hibernate.extras;

import com.atlassian.confluence.impl.hibernate.extras.ExportHibernateHandle;
import com.atlassian.confluence.impl.hibernate.extras.ExportProgress;
import com.atlassian.confluence.impl.hibernate.extras.HibernateTranslator;
import com.atlassian.hibernate.extras.ExportableField;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.BagType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.DateType;
import org.hibernate.type.ListType;
import org.hibernate.type.MapType;
import org.hibernate.type.SetType;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class XMLDatabinder {
    private static final Logger log = LoggerFactory.getLogger(XMLDatabinder.class);
    public static final String ISO_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    private SessionFactoryImplementor factory;
    protected Set<ExportHibernateHandle> handles = new HashSet<ExportHibernateHandle>();
    protected Set<ExportHibernateHandle> bucketHandles = new HashSet<ExportHibernateHandle>();
    protected Set<ExportHibernateHandle> excludedHandles = new HashSet<ExportHibernateHandle>();
    protected Set<ExportHibernateHandle> nextHandles = new HashSet<ExportHibernateHandle>();
    protected Set<ExportHibernateHandle> processedHandles = new HashSet<ExportHibernateHandle>();
    protected String encoding;
    protected final HibernateTranslator translator;
    private DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat isoTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private ExportProgress progress;
    protected static final String LEFT_CHEVRON = "<";
    protected static final String RIGHT_CHEVRON = ">";
    protected static final String CARRIAGE_RETURN = "\n";
    protected static final String START_CLOSE_TAG = "</";
    protected static final String END_TAG_CARRIAGE_RETURN = ">\n";
    private static final String CONST_NAME = "name";
    private static final String CONST_CLASS = "class";
    private static final String CONST_PACKAGE = "package";
    private static final String CONST_ENUM_CLASS = "enum-class";
    private static final String CONST_COMPOSITE_ELEMENT = "composite-element";
    private static final String CONST_ELEMENT = "element";
    private static final String CONST_SUBCOLLECTION = "subcollection";
    private static final String CONST_ID = "id";
    private static final String CONST_COMPOSITE_ID = "composite-id";
    private static final String CONST_OPEN_OBJECT_TAG = "<object";
    private static final String CONST_CLOSE_OBJECT_TAG = "</object>\n";
    private static final String CONST_COLLECTION = "collection";
    private static final String CONST_PROPERTY = "property";
    private static final String CONST_COMPONENT = "component";
    private static final String CONST_TYPE = "type";
    protected static final String CONST_OPEN_CDATA = "<![CDATA[";
    protected static final String CONST_CLOSE_CDATA = "]]>";
    private static final int BATCH_SIZE = Integer.parseInt(System.getProperty("export.space.batch.size", "300"));
    private Session session;
    private Collection<Class<?>> classesExcludedFromEntityExport = new ArrayList();
    private Collection<Class<?>> classesExcludedFromReferenceExport = new ArrayList();
    private Map<String, List<FieldWithType>> exportableFieldsCache = new HashMap<String, List<FieldWithType>>();

    public XMLDatabinder(SessionFactoryImplementor factory, String encoding, HibernateTranslator translator) {
        this.factory = factory;
        this.encoding = encoding;
        this.translator = translator;
    }

    protected final EntityPersister getPersister(Class clazz) throws MappingException {
        return this.factory.getMetamodel().locateEntityPersister(clazz);
    }

    protected final void objectWritten(ExportHibernateHandle handle) throws HibernateException {
        this.incrementProgress();
        this.processedHandles.add(handle);
    }

    protected void incrementProgress() throws HibernateException {
        if (this.progress.increment() % BATCH_SIZE == 0) {
            this.commit();
        }
    }

    protected void incrementProgressTotal() {
        this.progress.incrementTotal();
    }

    protected void commit() throws HibernateException {
        this.session.flush();
        this.session.clear();
        this.session.getTransaction().commit();
        this.session.beginTransaction();
    }

    private void startTxn() throws HibernateException {
        this.session = this.factory.getCurrentSession();
        if (!this.session.getTransaction().isActive()) {
            this.session.beginTransaction();
        } else {
            this.commit();
        }
    }

    public void toGenericXML(Writer writer, ExportProgress progressMeter) throws HibernateException, IOException {
        String date;
        this.progress = progressMeter;
        this.startTxn();
        writer.write("<?xml version=\"1.0\" encoding=\"" + this.encoding + "\"?>\n");
        try {
            date = TimestampType.INSTANCE.objectToSQLString(new Date(), this.factory.getDialect());
        }
        catch (Exception e) {
            throw new HibernateException((Throwable)e);
        }
        writer.write("<hibernate-generic datetime=\"" + date + "\">\n");
        this.progress.setTotal(this.handles.size() + this.bucketHandles.size() + 2);
        this.progress.setStatus("Writing export");
        while (!this.handles.isEmpty()) {
            this.writeObjects(writer, this.handles);
            this.handles = this.nextHandles;
            this.nextHandles = new HashSet<ExportHibernateHandle>();
        }
        this.writeObjects(writer, this.bucketHandles);
        writer.write("</hibernate-generic>");
        this.commit();
    }

    protected void writeObjects(Writer writer, Iterable<ExportHibernateHandle> iter) throws HibernateException, IOException {
        for (ExportHibernateHandle handle : iter) {
            if (this.isExcludedOrProcessed(handle)) {
                this.incrementProgress();
                continue;
            }
            Object object = this.translator.handleToObject(handle);
            if ((object = this.maybeInitializeIfProxy(object)) != null) {
                EntityPersister persister = this.getPersister(object.getClass());
                this.writeObject(writer, handle, object, persister);
            } else {
                log.warn("Null object found for key: {}", (Object)handle.toString());
            }
            this.objectWritten(handle);
        }
    }

    protected final void writeObject(Writer writer, ExportHibernateHandle handle, Object object, EntityPersister persister) throws IOException, HibernateException {
        writer.write(CONST_OPEN_OBJECT_TAG);
        this.addClass(writer, object.getClass(), CONST_CLASS, CONST_PACKAGE);
        writer.write(END_TAG_CARRIAGE_RETURN);
        if (log.isDebugEnabled()) {
            log.debug("Writing object: {} with ID: {} to XML.", (Object)persister.getMappedClass(), (Object)persister.getIdentifier(object));
        }
        if (persister.hasIdentifierProperty() || persister.getEntityMetamodel().getIdentifierProperty().isEmbedded()) {
            Serializable id = persister.getIdentifier(object);
            this.renderProperty(writer, persister.getIdentifierPropertyName(), persister.getIdentifierType(), id, CONST_COMPOSITE_ID, CONST_ID, null, false);
        }
        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(object);
        String[] names = persister.getPropertyNames();
        for (int i = 0; i < types.length; ++i) {
            block10: {
                try {
                    if (this.isExcludedAsProperty(types[i], values[i])) {
                    }
                    break block10;
                }
                catch (RuntimeException onfe) {
                    log.warn("Object doesn't exist for property {} of {}", new Object[]{names[i], handle.toString(), onfe.getCause()});
                }
                continue;
            }
            this.renderProperty(writer, names[i], types[i], values[i], CONST_COMPONENT, CONST_PROPERTY, CONST_COLLECTION, false);
        }
        List<String> propertyNames = Arrays.asList(names);
        Class<?> clazz = object.getClass();
        for (FieldWithType fieldWithType : this.getExportableFields(clazz)) {
            Field field = fieldWithType.getField();
            String fieldName = field.getName();
            if (propertyNames.contains(fieldName)) {
                log.warn("The field {} annotated with ExportableField has been already serialized. Please remove the annotation.", (Object)fieldName);
                continue;
            }
            try {
                Object value = fieldWithType.getFieldGetter().invoke(object, new Object[0]);
                this.renderOtherType(writer, fieldName, fieldWithType.getType(), value, CONST_PROPERTY, false);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Cannot render {} of {}.", new Object[]{fieldName, object.getClass().getName(), e});
            }
        }
        writer.write(CONST_CLOSE_OBJECT_TAG);
    }

    protected final List<FieldWithType> getExportableFields(Class<?> clazz) {
        return this.exportableFieldsCache.computeIfAbsent(clazz.getName(), className -> Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(ExportableField.class)).map(field -> {
            Method fieldGetter;
            ExportableField exportableFieldAnnotation = field.getAnnotation(ExportableField.class);
            Object type = this.createInstance(exportableFieldAnnotation.type());
            if (type == null) {
                return null;
            }
            field.setAccessible(true);
            try {
                String prefix = type instanceof BooleanType ? "is" : "get";
                fieldGetter = clazz.getMethod(prefix + this.capitalize(field.getName()), new Class[0]);
            }
            catch (NoSuchMethodException e) {
                log.error("No getter method for property {} of {} found. This property will not be serialized to XML.", new Object[]{field.getName(), clazz.getName(), e});
                return null;
            }
            return new FieldWithType((Field)field, (Type)type, fieldGetter);
        }).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }

    private Object createInstance(Class clazz) {
        try {
            return clazz.newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            log.error("Cannot create a new object of {}.", (Object)clazz.getName(), (Object)e);
            return null;
        }
    }

    protected final boolean isExcludedOrProcessed(ExportHibernateHandle handle) {
        if (this.excludedHandles.contains(handle)) {
            return true;
        }
        if (this.processedHandles.contains(handle)) {
            return true;
        }
        Class handleClass = handle.getClazz();
        if (handleClass.isEnum()) {
            return true;
        }
        return this.classExtendsOneOf(handleClass, this.classesExcludedFromEntityExport);
    }

    private boolean classExtendsOneOf(Class clazz, Collection<Class<?>> classes) {
        for (Class<?> listedClass : classes) {
            if (!listedClass.isAssignableFrom(clazz)) continue;
            return true;
        }
        return false;
    }

    protected final void addClass(Writer writer, Class clazz, String classAttributeName, String packageAttributeName) throws IOException {
        String className = clazz.getName();
        String unqualifiedClassName = StringHelper.unqualify((String)className);
        String packageName = StringHelper.qualifier((String)className);
        this.appendAttribute(writer, classAttributeName, unqualifiedClassName);
        this.appendAttribute(writer, packageAttributeName, packageName);
    }

    protected Object maybeInitializeIfProxy(Object object) {
        if (!(object instanceof HibernateProxy)) {
            return object;
        }
        LazyInitializer li = ((HibernateProxy)object).getHibernateLazyInitializer();
        return li.getImplementation();
    }

    public XMLDatabinder bind(Object object) {
        this.handles.add(this.translator.objectOrHandleToHandle(object));
        return this;
    }

    public XMLDatabinder unbind(Object object) {
        this.excludedHandles.add(this.translator.objectOrHandleToHandle(object));
        return this;
    }

    public XMLDatabinder bindAll(Collection objects) {
        objects.forEach(this::bind);
        return this;
    }

    public XMLDatabinder unbindAll(Collection objects) {
        objects.forEach(this::unbind);
        return this;
    }

    protected final void renderProperty(Writer writer, String name, Type type, Object value, String componentName, String propertyName, String collectionName, boolean doType) throws HibernateException, IOException {
        if (type.isComponentType()) {
            this.renderComponentType(writer, name, type, value, componentName);
        } else if (type.isCollectionType()) {
            this.renderCollectionType(writer, name, type, value, collectionName);
        } else if (type.isEntityType()) {
            this.renderEntityType(writer, name, value, propertyName);
            if (!this.isExcludedAsProperty(value)) {
                this.associatedObjectFound(value);
            }
        } else if (type.getReturnedClass().isEnum()) {
            this.renderEnumType(writer, name, type, value, propertyName);
        } else {
            this.renderOtherType(writer, name, type, value, propertyName, doType);
        }
    }

    protected final void renderOtherType(Writer writer, String name, Type type, Object value, String propertyName, boolean doType) throws HibernateException, IOException {
        writer.write(LEFT_CHEVRON + propertyName);
        if (name != null) {
            this.appendAttribute(writer, CONST_NAME, name);
        }
        if (doType) {
            this.appendAttribute(writer, CONST_TYPE, type.getName());
        }
        if (value != null) {
            String xmlValue;
            writer.write(RIGHT_CHEVRON);
            if (type instanceof StringRepresentableType && !this.parseCustomType(writer, type, value, xmlValue = ((StringRepresentableType)type).toString(value))) {
                if (type instanceof TimestampType) {
                    writer.write(this.isoTimestampFormat.format((Date)value));
                } else if (type instanceof DateType) {
                    writer.write(this.isoDateFormat.format((Date)value));
                } else {
                    writer.write(xmlValue);
                }
            }
            writer.write(START_CLOSE_TAG + propertyName + END_TAG_CARRIAGE_RETURN);
        } else {
            writer.write("/>");
        }
    }

    public abstract boolean parseCustomType(Writer var1, Type var2, Object var3, String var4) throws IOException;

    protected final void renderEnumType(Writer writer, String name, Type type, Object value, String propertyName) throws HibernateException, IOException {
        writer.write(LEFT_CHEVRON + propertyName);
        if (name != null) {
            this.appendAttribute(writer, CONST_NAME, name);
        }
        this.addClass(writer, type.getReturnedClass(), CONST_ENUM_CLASS, CONST_PACKAGE);
        if (value != null) {
            writer.write(RIGHT_CHEVRON);
            if (type instanceof StringRepresentableType) {
                String xmlValue = ((StringRepresentableType)type).toString(value);
                writer.write(xmlValue);
            }
            writer.write(START_CLOSE_TAG + propertyName + END_TAG_CARRIAGE_RETURN);
        } else {
            writer.write("/>");
        }
    }

    private void appendAttribute(Writer writer, String attributeName, String attributeValue) throws IOException {
        writer.write(" " + attributeName + "=\"" + attributeValue + "\"");
    }

    protected final void renderEntityType(Writer writer, String name, Object value, String propertyName) throws HibernateException, IOException {
        if ((value = this.maybeInitializeIfProxy(value)) != null) {
            writer.write(LEFT_CHEVRON + propertyName);
            if (name != null) {
                this.appendAttribute(writer, CONST_NAME, name);
            }
            this.addClass(writer, value.getClass(), CONST_CLASS, CONST_PACKAGE);
            writer.write(RIGHT_CHEVRON);
            EntityPersister persister = this.getPersister(value.getClass());
            if (persister.hasIdentifierProperty() || persister.getEntityMetamodel().getIdentifierProperty().isEmbedded()) {
                Type idType = persister.getIdentifierType();
                Serializable id = persister.getIdentifier(value);
                this.renderProperty(writer, persister.getIdentifierPropertyName(), idType, id, CONST_COMPOSITE_ID, CONST_ID, null, false);
            }
            writer.write(START_CLOSE_TAG + propertyName + END_TAG_CARRIAGE_RETURN);
        }
    }

    private void renderCollectionType(Writer writer, String name, Type type, Object value, String collectionName) throws HibernateException, IOException {
        int length;
        CollectionType collectiontype = (CollectionType)type;
        String role = collectiontype.getRole();
        CollectionPersister persister = this.factory.getCollectionPersister(role);
        if (persister.isArray() ? (length = Array.getLength(value)) == 0 : value instanceof Collection && ((Collection)value).isEmpty()) {
            return;
        }
        if (persister.isArray()) {
            collectionName = "array";
        }
        writer.write(LEFT_CHEVRON + collectionName);
        if (name != null) {
            this.appendAttribute(writer, CONST_NAME, name);
        }
        if (!persister.isArray()) {
            this.appendAttribute(writer, CONST_CLASS, type.getReturnedClass().getName());
        }
        Type elemType = persister.getElementType();
        writer.write(RIGHT_CHEVRON);
        if (persister.isArray()) {
            int length2 = Array.getLength(value);
            for (int i = 0; i < length2; ++i) {
                Object element = Array.get(value, i);
                this.renderProperty(writer, null, elemType, element, CONST_COMPOSITE_ELEMENT, CONST_ELEMENT, CONST_SUBCOLLECTION, true);
                this.associatedObjectFound(element);
            }
        } else if (type instanceof ListType || type instanceof SetType || type instanceof BagType) {
            for (Object collectionItem : (Collection)value) {
                if (this.isExcludedAsProperty(elemType, collectionItem)) continue;
                this.renderProperty(writer, null, elemType, collectionItem, CONST_COMPOSITE_ELEMENT, CONST_ELEMENT, CONST_SUBCOLLECTION, true);
            }
        } else if (type instanceof MapType) {
            Iterator iterator = ((Map)value).entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry o;
                Map.Entry e = o = iterator.next();
                Object collectionKey = e.getKey();
                Object collectionItem = e.getValue();
                log.debug("Rendering map property: {} -> {}", collectionKey, collectionItem);
                if (this.isExcludedAsProperty(elemType, collectionItem)) continue;
                this.renderProperty(writer, collectionKey.toString(), elemType, collectionItem, CONST_COMPOSITE_ELEMENT, CONST_ELEMENT, CONST_SUBCOLLECTION, true);
            }
        }
        writer.write(START_CLOSE_TAG + collectionName + END_TAG_CARRIAGE_RETURN);
    }

    protected final boolean isExcludedAsProperty(Type type, Object obj) {
        return type.isEntityType() && type.isAssociationType() && this.isExcludedAsProperty(obj);
    }

    private boolean isExcludedAsProperty(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Enum) {
            return false;
        }
        ExportHibernateHandle handle = this.translator.objectToHandle(obj);
        if (this.classExtendsOneOf(handle.getClazz(), this.classesExcludedFromReferenceExport)) {
            return true;
        }
        return this.excludedHandles.contains(handle);
    }

    protected final void renderComponentType(Writer writer, String name, Type type, Object value, String componentName) throws HibernateException, IOException {
        if (value != null) {
            ComponentType componenttype = (ComponentType)type;
            writer.write(LEFT_CHEVRON + componentName);
            if (name != null) {
                this.appendAttribute(writer, CONST_NAME, name);
            }
            writer.write(RIGHT_CHEVRON);
            String[] properties = componenttype.getPropertyNames();
            Object[] subvalues = componenttype.getPropertyValues(value, (EntityMode)null);
            Type[] subtypes = componenttype.getSubtypes();
            for (int j = 0; j < properties.length; ++j) {
                this.renderProperty(writer, properties[j], subtypes[j], subvalues[j], CONST_COMPONENT, CONST_PROPERTY, CONST_COLLECTION, true);
            }
            writer.write(START_CLOSE_TAG + componentName + END_TAG_CARRIAGE_RETURN);
        }
    }

    protected final void associatedObjectFound(Object object) {
        boolean addedHandle;
        ExportHibernateHandle handle;
        if (object == null) {
            return;
        }
        if ((object = this.maybeInitializeIfProxy(object)) != null && !this.isExcludedOrProcessed(handle = this.translator.objectToHandle(object)) && (addedHandle = this.nextHandles.add(handle))) {
            this.incrementProgressTotal();
        }
    }

    public void excludeClass(Class<?> clazz) {
        this.excludeClassFromEntityExport(clazz);
        this.excludeClassFromReferenceExport(clazz);
    }

    public void excludeClassFromReferenceExport(Class<?> clazz) {
        this.classesExcludedFromReferenceExport.add(clazz);
    }

    public void excludeClassFromEntityExport(Class<?> clazz) {
        this.classesExcludedFromEntityExport.add(clazz);
    }

    protected static final class FieldWithType {
        private Field field;
        private Type type;
        private Method fieldGetter;

        private FieldWithType(Field field, Type type, Method fieldGetter) {
            this.field = field;
            this.type = type;
            this.fieldGetter = fieldGetter;
        }

        public Field getField() {
            return this.field;
        }

        public Type getType() {
            return this.type;
        }

        public Method getFieldGetter() {
            return this.fieldGetter;
        }
    }
}


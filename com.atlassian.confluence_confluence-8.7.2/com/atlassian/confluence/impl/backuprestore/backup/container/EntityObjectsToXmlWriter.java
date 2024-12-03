/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.SpoolingBlobInputStreamType
 *  com.atlassian.core.util.XMLUtils
 *  com.google.common.base.CharMatcher
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.DocumentHelper
 *  org.dom4j.tree.AbstractCDATA
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.container;

import com.atlassian.confluence.core.persistence.hibernate.InstantType;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.hibernate.SpoolingBlobInputStreamType;
import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.confluence.security.persistence.dao.hibernate.KeyTransferBean;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.util.XMLUtils;
import com.google.common.base.CharMatcher;
import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.tree.AbstractCDATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityObjectsToXmlWriter
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(EntityObjectsToXmlWriter.class);
    private final Writer writer;
    private static final String TAB_INDENTION = "    ";
    private final int CHARACTER_LIMIT;
    private static final int DEFAULT_CHARACTER_LIMIT = Integer.getInteger("confluence.backup-restore.cdata-character-limit", 100);

    public EntityObjectsToXmlWriter(Writer writer, Instant currentTime, int characterLimit) throws IOException {
        this.writer = writer;
        this.CHARACTER_LIMIT = characterLimit;
        this.write(String.format("<hibernate-generic datetime=\"%s\">%n", currentTime.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-d HH:mm:ss", Locale.US))));
    }

    public EntityObjectsToXmlWriter(Writer writer, Instant currentTime) throws IOException {
        this(writer, currentTime, DEFAULT_CHARACTER_LIMIT);
    }

    public synchronized void serialise(Collection<EntityObjectReadyForExport> objectsToSerialise) throws IOException {
        for (EntityObjectReadyForExport entityObjectReadyForExport : objectsToSerialise) {
            this.serialise(entityObjectReadyForExport);
        }
    }

    private void serialise(EntityObjectReadyForExport object) throws IOException {
        if (object.getReason() != null) {
            this.write("<!-- reason: " + object.getReason() + "-->\n");
        }
        this.write("<object");
        this.addClass(object.getClazz());
        this.write(">\n");
        this.writeKey(object);
        this.writeProperties(object.getProperties());
        this.writeReferences(object.getReferences());
        this.writeCollections(object.getCollections());
        this.write("</object>\n\n");
    }

    private void writeKey(EntityObjectReadyForExport object) throws IOException {
        List<EntityObjectReadyForExport.Property> idProperties = object.getIds();
        if (idProperties.size() == 1) {
            this.write("", 1);
            this.writeId(idProperties.get(0));
            this.write("\n");
        } else if (idProperties.size() > 1) {
            this.writeCompositeId(idProperties);
        } else {
            throw new IllegalStateException("Requested key for the entity " + object.getClazz().toString() + " has not been set (key is missing)");
        }
    }

    private void writeProperties(Collection<EntityObjectReadyForExport.Property> properties) throws IOException {
        for (EntityObjectReadyForExport.Property property : properties) {
            if (property.getValue() == null) continue;
            this.writeProperty(property);
            this.write("\n");
        }
    }

    private void writeReferences(Collection<EntityObjectReadyForExport.Reference> references) throws IOException {
        for (EntityObjectReadyForExport.Reference reference : references) {
            if (reference.getReferencedId().getValue() == null) continue;
            this.write("<property name=\"" + reference.getPropertyName() + "\"", 1);
            this.addClass(reference.getReferencedClazz());
            this.write(">");
            this.writeId(reference.getReferencedId());
            this.write("</property>\n");
        }
    }

    private void writeCollections(Collection<EntityObjectReadyForExport.CollectionOfElements> collectionsOfElements) throws IOException {
        for (EntityObjectReadyForExport.CollectionOfElements collection : collectionsOfElements) {
            if (collection.isEmpty()) continue;
            this.write("<collection name=\"" + collection.getCollectionName() + "\" class=\"" + collection.getCollectionClazz().getName() + "\">\n", 1);
            if (collection.getCollectionClazz().equals(Map.class)) {
                this.writeMap(collection.getMap());
            } else {
                this.writeElementsOfCollection(collection.getElementValues(), collection.getReferencedClazz());
            }
            this.write("</collection>\n", 1);
        }
    }

    private void writeElementsOfCollection(Collection<Object> elementValues, Class<?> referencedClass) throws IOException {
        for (Object element : elementValues) {
            this.write("<element", 2);
            this.addClass(referencedClass);
            this.write(">");
            if (referencedClass.isEnum()) {
                this.writeValue(element);
            } else {
                this.writeIdCollectionElement(element);
            }
            this.write("</element>\n");
        }
    }

    private void writeMap(Map<Object, Object> map) throws IOException {
        for (Map.Entry<Object, Object> element : map.entrySet()) {
            this.write("<element name=\"" + element.getKey() + "\" type=\"string\">", 2);
            this.writeValue(element.getValue());
            this.write("</element>\n");
        }
    }

    private void writeIdCollectionElement(Object idProperty) throws IOException {
        if (!(idProperty instanceof EntityObjectReadyForExport.Property)) {
            idProperty = new EntityObjectReadyForExport.Property(null, "id", idProperty);
        }
        this.writeId((EntityObjectReadyForExport.Property)idProperty);
    }

    private void writeId(EntityObjectReadyForExport.Property id) throws IOException {
        this.write("<id name=\"" + id.getName() + "\">");
        this.writeValue(id.getValue());
        this.write("</id>");
    }

    private void writeCompositeId(List<EntityObjectReadyForExport.Property> idProperties) throws IOException {
        this.write("<composite-id>\n", 1);
        for (EntityObjectReadyForExport.Property property : idProperties) {
            this.write("", 2);
            this.write("<property name=\"" + property.getName() + "\" type=\"" + property.getClazz().getSimpleName().toLowerCase(Locale.ROOT) + "\">");
            this.writeValue(property.getValue());
            this.write("</property>");
            this.write("\n");
        }
        this.write("</composite-id>\n", 1);
    }

    private void writeProperty(EntityObjectReadyForExport.Property property) throws IOException {
        if (property.getClazz() == null) {
            this.writeRegularProperty(property);
            return;
        }
        if (property.getClazz().isEnum()) {
            this.writeEnumProperty(property);
        } else if (property.getClazz().equals(AliasedKey.class)) {
            this.writeKeyProperty(property);
        } else if (property.getClazz().equals(new InstantType().returnedClass()) && property.getValue() instanceof Long) {
            this.writeInstantProperty(property);
        } else if (property.getClazz().equals(new SpoolingBlobInputStreamType().returnedClass()) && property.getValue() instanceof byte[]) {
            this.writeSpoolingBlobInputStreamProperty(property);
        } else {
            this.writeRegularProperty(property);
        }
    }

    private void writeRegularProperty(EntityObjectReadyForExport.Property property) throws IOException {
        this.write("<property name=\"", 1);
        this.write(property.getName());
        this.write("\">");
        this.writeValue(property.getValue());
        this.write("</property>");
    }

    private void writeKeyProperty(EntityObjectReadyForExport.Property property) throws IOException {
        this.write("<property name=\"", 1);
        this.write(property.getName());
        this.write("\">");
        KeyTransferBean value = (KeyTransferBean)property.getValue();
        this.writeValue(String.format(" %s %s %s ", value.getAlgorithm(), value.getKeyType(), value.getEncodedKey()));
        this.write("</property>");
    }

    private void writeEnumProperty(EntityObjectReadyForExport.Property property) throws IOException {
        this.write("<property name=\"", 1);
        this.write(property.getName());
        this.write("\" enum-class=\"");
        this.write(property.getClazz().getSimpleName());
        this.write("\" package=\"");
        this.write(property.getClazz().getPackage().getName());
        this.write("\">");
        this.writeValue(property.getValue());
        this.write("</property>");
    }

    private void writeInstantProperty(EntityObjectReadyForExport.Property property) throws IOException {
        this.write("<property name=\"", 1);
        this.write(property.getName());
        this.write("\">");
        this.writeValue(property.getLongValue());
        this.write("</property>");
    }

    private void writeSpoolingBlobInputStreamProperty(EntityObjectReadyForExport.Property property) throws IOException {
        this.write("<property name=\"", 1);
        this.write(property.getName());
        this.write("\">");
        String base64EncodedValue = Base64.getEncoder().encodeToString((byte[])property.getValue());
        this.writeValue(base64EncodedValue);
        this.write("</property>");
    }

    protected final void addClass(Class<?> clazz) throws IOException {
        String className = clazz.getName();
        String unqualifiedClassName = EntityObjectsToXmlWriter.unqualify(className);
        String packageName = EntityObjectsToXmlWriter.qualifier(className);
        String classNameAttribute = clazz.isEnum() ? "enum-class" : "class";
        this.appendAttribute(classNameAttribute, unqualifiedClassName);
        this.appendAttribute("package", packageName);
    }

    private void appendAttribute(String attributeName, String attributeValue) throws IOException {
        this.write(" " + attributeName + "=\"" + attributeValue + "\"");
    }

    public static String unqualify(String qualifiedName) {
        return EntityObjectsToXmlWriter.unqualify(qualifiedName, ".");
    }

    public static String unqualify(String qualifiedName, String separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    public static String qualifier(String qualifiedName) {
        int loc = qualifiedName.lastIndexOf(".");
        if (loc < 0) {
            return "";
        }
        return qualifiedName.substring(0, loc);
    }

    @Override
    public void close() throws IOException {
        this.write(String.format("</hibernate-generic>%n", new Object[0]));
    }

    private void writeEscapedXMLValue(Object value) throws IOException {
        if (value instanceof String) {
            String escapedText = GeneralUtil.escapeCDATA((String)value);
            AbstractCDATA cdata = (AbstractCDATA)DocumentHelper.createCDATA((String)XMLUtils.escapeForCdata((String)escapedText));
            cdata.write(this.writer);
        } else if (value != null) {
            this.write(String.valueOf(value));
        }
    }

    private void write(String str) throws IOException {
        this.write(str, 0);
    }

    private void write(String str, int tabIndentionNumber) throws IOException {
        this.writer.write(StringUtils.repeat((String)TAB_INDENTION, (int)tabIndentionNumber));
        this.writer.write(str);
    }

    private void writeValue(Object value) throws IOException {
        if (value instanceof String && this.shouldEscape((String)value)) {
            this.writeEscapedXMLValue(value);
            return;
        }
        if (value instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").toPattern(), Locale.US);
            this.write(((LocalDateTime)value).format(formatter));
            return;
        }
        if (value instanceof Timestamp) {
            String valueString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(value);
            this.write(valueString);
            return;
        }
        this.write(String.valueOf(value));
    }

    private boolean shouldEscape(String str) {
        return str.length() > this.CHARACTER_LIMIT || this.containsNonAsciiCharacters(str) || this.containsEscapableXMLCharacters(str);
    }

    private boolean containsNonAsciiCharacters(String str) {
        return !CharMatcher.ascii().matchesAllOf((CharSequence)str);
    }

    private boolean containsEscapableXMLCharacters(String str) {
        char[] escapableCharacters = new char[]{'<', '>', '\'', '\"', '&'};
        return StringUtils.containsAny((CharSequence)str, (char[])escapableCharacters);
    }
}


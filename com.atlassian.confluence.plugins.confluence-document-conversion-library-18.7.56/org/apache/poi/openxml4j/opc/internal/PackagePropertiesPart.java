/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.openxml4j.opc.internal;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.apache.poi.util.LocaleUtil;

public final class PackagePropertiesPart
extends PackagePart
implements PackageProperties {
    public static final String NAMESPACE_DC_URI = "http://purl.org/dc/elements/1.1/";
    public static final String NAMESPACE_CP_URI = "http://schemas.openxmlformats.org/package/2006/metadata/core-properties";
    public static final String NAMESPACE_DCTERMS_URI = "http://purl.org/dc/terms/";
    private static final String DEFAULT_DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String[] DATE_FORMATS = new String[]{"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss.SS'Z'", "yyyy-MM-dd"};
    private final String[] TZ_DATE_FORMATS = new String[]{"yyyy-MM-dd'T'HH:mm:ssz", "yyyy-MM-dd'T'HH:mm:ss.Sz", "yyyy-MM-dd'T'HH:mm:ss.SSz", "yyyy-MM-dd'T'HH:mm:ss.SSSz"};
    private final Pattern TIME_ZONE_PAT = Pattern.compile("([-+]\\d\\d):?(\\d\\d)");
    protected Optional<String> category = Optional.empty();
    protected Optional<String> contentStatus = Optional.empty();
    protected Optional<String> contentType = Optional.empty();
    protected Optional<Date> created = Optional.empty();
    protected Optional<String> creator = Optional.empty();
    protected Optional<String> description = Optional.empty();
    protected Optional<String> identifier = Optional.empty();
    protected Optional<String> keywords = Optional.empty();
    protected Optional<String> language = Optional.empty();
    protected Optional<String> lastModifiedBy = Optional.empty();
    protected Optional<Date> lastPrinted = Optional.empty();
    protected Optional<Date> modified = Optional.empty();
    protected Optional<String> revision = Optional.empty();
    protected Optional<String> subject = Optional.empty();
    protected Optional<String> title = Optional.empty();
    protected Optional<String> version = Optional.empty();

    public PackagePropertiesPart(OPCPackage pack, PackagePartName partName) throws InvalidFormatException {
        super(pack, partName, "application/vnd.openxmlformats-package.core-properties+xml");
    }

    @Override
    public Optional<String> getCategoryProperty() {
        return this.category;
    }

    @Override
    public Optional<String> getContentStatusProperty() {
        return this.contentStatus;
    }

    @Override
    public Optional<String> getContentTypeProperty() {
        return this.contentType;
    }

    @Override
    public Optional<Date> getCreatedProperty() {
        return this.created;
    }

    public String getCreatedPropertyString() {
        return PackagePropertiesPart.getDateValue(this.created);
    }

    @Override
    public Optional<String> getCreatorProperty() {
        return this.creator;
    }

    @Override
    public Optional<String> getDescriptionProperty() {
        return this.description;
    }

    @Override
    public Optional<String> getIdentifierProperty() {
        return this.identifier;
    }

    @Override
    public Optional<String> getKeywordsProperty() {
        return this.keywords;
    }

    @Override
    public Optional<String> getLanguageProperty() {
        return this.language;
    }

    @Override
    public Optional<String> getLastModifiedByProperty() {
        return this.lastModifiedBy;
    }

    @Override
    public Optional<Date> getLastPrintedProperty() {
        return this.lastPrinted;
    }

    public String getLastPrintedPropertyString() {
        return PackagePropertiesPart.getDateValue(this.lastPrinted);
    }

    @Override
    public Optional<Date> getModifiedProperty() {
        return this.modified;
    }

    public String getModifiedPropertyString() {
        if (this.modified.isPresent()) {
            return PackagePropertiesPart.getDateValue(this.modified);
        }
        return PackagePropertiesPart.getDateValue(Optional.of(new Date()));
    }

    @Override
    public Optional<String> getRevisionProperty() {
        return this.revision;
    }

    @Override
    public Optional<String> getSubjectProperty() {
        return this.subject;
    }

    @Override
    public Optional<String> getTitleProperty() {
        return this.title;
    }

    @Override
    public Optional<String> getVersionProperty() {
        return this.version;
    }

    @Override
    public void setCategoryProperty(String category) {
        this.category = this.parseStringValue(category);
    }

    @Override
    public void setCategoryProperty(Optional<String> category) {
        this.category = category;
    }

    @Override
    public void setContentStatusProperty(String contentStatus) {
        this.contentStatus = this.parseStringValue(contentStatus);
    }

    @Override
    public void setContentStatusProperty(Optional<String> contentStatus) {
        this.contentStatus = contentStatus;
    }

    @Override
    public void setContentTypeProperty(String contentType) {
        this.contentType = this.parseStringValue(contentType);
    }

    @Override
    public void setContentTypeProperty(Optional<String> contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setCreatedProperty(String created) throws InvalidFormatException {
        this.created = this.parseDateValue(created);
    }

    @Override
    public void setCreatedProperty(Optional<Date> created) {
        this.created = created;
    }

    @Override
    public void setCreatorProperty(String creator) {
        this.creator = this.parseStringValue(creator);
    }

    @Override
    public void setCreatorProperty(Optional<String> creator) {
        this.creator = creator;
    }

    @Override
    public void setDescriptionProperty(String description) {
        this.description = this.parseStringValue(description);
    }

    @Override
    public void setDescriptionProperty(Optional<String> description) {
        this.description = description;
    }

    @Override
    public void setIdentifierProperty(String identifier) {
        this.identifier = this.parseStringValue(identifier);
    }

    @Override
    public void setIdentifierProperty(Optional<String> identifier) {
        this.identifier = identifier;
    }

    @Override
    public void setKeywordsProperty(String keywords) {
        this.keywords = this.parseStringValue(keywords);
    }

    @Override
    public void setKeywordsProperty(Optional<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public void setLanguageProperty(String language) {
        this.language = this.parseStringValue(language);
    }

    @Override
    public void setLanguageProperty(Optional<String> language) {
        this.language = language;
    }

    @Override
    public void setLastModifiedByProperty(String lastModifiedBy) {
        this.lastModifiedBy = this.parseStringValue(lastModifiedBy);
    }

    @Override
    public void setLastModifiedByProperty(Optional<String> lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public void setLastPrintedProperty(String lastPrinted) throws InvalidFormatException {
        this.lastPrinted = this.parseDateValue(lastPrinted);
    }

    @Override
    public void setLastPrintedProperty(Optional<Date> lastPrinted) {
        this.lastPrinted = lastPrinted;
    }

    @Override
    public void setModifiedProperty(String modified) throws InvalidFormatException {
        this.modified = this.parseDateValue(modified);
    }

    @Override
    public void setModifiedProperty(Optional<Date> modified) {
        this.modified = modified;
    }

    @Override
    public void setRevisionProperty(Optional<String> revision) {
        this.revision = revision;
    }

    @Override
    public void setRevisionProperty(String revision) {
        this.revision = this.parseStringValue(revision);
    }

    @Override
    public void setSubjectProperty(String subject) {
        this.subject = this.parseStringValue(subject);
    }

    @Override
    public void setSubjectProperty(Optional<String> subject) {
        this.subject = subject;
    }

    @Override
    public void setTitleProperty(String title) {
        this.title = this.parseStringValue(title);
    }

    @Override
    public void setTitleProperty(Optional<String> title) {
        this.title = title;
    }

    @Override
    public void setVersionProperty(String version) {
        this.version = this.parseStringValue(version);
    }

    @Override
    public void setVersionProperty(Optional<String> version) {
        this.version = version;
    }

    private Optional<String> parseStringValue(String s) {
        if (s == null || s.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(s);
    }

    private Optional<Date> parseDateValue(String dateStr) throws InvalidFormatException {
        String dateTzStr;
        if (dateStr == null || dateStr.isEmpty()) {
            return Optional.empty();
        }
        Matcher m = this.TIME_ZONE_PAT.matcher(dateStr);
        Date d = null;
        if (m.find()) {
            dateTzStr = dateStr.substring(0, m.start()) + m.group(1) + m.group(2);
            d = PackagePropertiesPart.parseDateFormat(this.TZ_DATE_FORMATS, dateTzStr);
        }
        if (d == null) {
            dateTzStr = dateStr.endsWith("Z") ? dateStr : dateStr + "Z";
            d = PackagePropertiesPart.parseDateFormat(DATE_FORMATS, dateTzStr);
        }
        if (d != null) {
            return Optional.of(d);
        }
        String allFormats = Stream.of(this.TZ_DATE_FORMATS, DATE_FORMATS).flatMap(Stream::of).collect(Collectors.joining(", "));
        throw new InvalidFormatException("Date " + dateStr + " not well formatted, expected format in: " + allFormats);
    }

    private static Date parseDateFormat(String[] formats, String dateTzStr) {
        for (String fStr : formats) {
            SimpleDateFormat df = new SimpleDateFormat(fStr, Locale.ROOT);
            df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
            Date d = df.parse(dateTzStr, new ParsePosition(0));
            if (d == null) continue;
            return d;
        }
        return null;
    }

    private static String getDateValue(Optional<Date> d) {
        return d.map(PackagePropertiesPart::getDateValue).orElse("");
    }

    private static String getDateValue(Date d) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATEFORMAT, Locale.ROOT);
        df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
        return df.format(d);
    }

    @Override
    protected InputStream getInputStreamImpl() {
        throw new InvalidOperationException("Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }

    @Override
    protected OutputStream getOutputStreamImpl() {
        throw new InvalidOperationException("Can't use output stream to set properties !");
    }

    @Override
    public boolean save(OutputStream zos) {
        throw new InvalidOperationException("Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }

    @Override
    public boolean load(InputStream ios) {
        throw new InvalidOperationException("Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }
}


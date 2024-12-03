/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaCodePrinter;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptionCharEscapeMap;
import org.apache.xmlbeans.impl.store.Saaj;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLReader;

public class XmlOptions
implements Serializable {
    public static final int DEFAULT_ENTITY_EXPANSION_LIMIT = 2048;
    private static final XmlOptions EMPTY_OPTIONS = new XmlOptions();
    private static final long serialVersionUID = 1L;
    private Map<XmlOptionsKeys, Object> _map = new HashMap<XmlOptionsKeys, Object>();

    public XmlOptions() {
    }

    public XmlOptions(XmlOptions other) {
        if (other != null) {
            this._map.putAll(other._map);
        }
    }

    public XmlOptions setSaveNamespacesFirst() {
        return this.setSaveNamespacesFirst(true);
    }

    public XmlOptions setSaveNamespacesFirst(boolean b) {
        return this.set(XmlOptionsKeys.SAVE_NAMESPACES_FIRST, b);
    }

    public boolean isSaveNamespacesFirst() {
        return this.hasOption(XmlOptionsKeys.SAVE_NAMESPACES_FIRST);
    }

    public XmlOptions setSavePrettyPrint() {
        return this.setSavePrettyPrint(true);
    }

    public XmlOptions setSavePrettyPrint(boolean b) {
        return this.set(XmlOptionsKeys.SAVE_PRETTY_PRINT, b);
    }

    public boolean isSavePrettyPrint() {
        return this.hasOption(XmlOptionsKeys.SAVE_PRETTY_PRINT);
    }

    public XmlOptions setSavePrettyPrintIndent(int indent) {
        return this.set(XmlOptionsKeys.SAVE_PRETTY_PRINT_INDENT, indent);
    }

    public Integer getSavePrettyPrintIndent() {
        return (Integer)this.get(XmlOptionsKeys.SAVE_PRETTY_PRINT_INDENT);
    }

    public XmlOptions setSavePrettyPrintOffset(int offset) {
        return this.set(XmlOptionsKeys.SAVE_PRETTY_PRINT_OFFSET, offset);
    }

    public Integer getSavePrettyPrintOffset() {
        return (Integer)this.get(XmlOptionsKeys.SAVE_PRETTY_PRINT_OFFSET);
    }

    public XmlOptions setCharacterEncoding(String encoding) {
        return this.set(XmlOptionsKeys.CHARACTER_ENCODING, encoding);
    }

    public String getCharacterEncoding() {
        return (String)this.get(XmlOptionsKeys.CHARACTER_ENCODING);
    }

    public XmlOptions setDocumentType(SchemaType type) {
        return this.set(XmlOptionsKeys.DOCUMENT_TYPE, type);
    }

    public SchemaType getDocumentType() {
        return (SchemaType)this.get(XmlOptionsKeys.DOCUMENT_TYPE);
    }

    public XmlOptions setErrorListener(Collection<XmlError> c) {
        return this.set(XmlOptionsKeys.ERROR_LISTENER, c);
    }

    public Collection<XmlError> getErrorListener() {
        return (Collection)this.get(XmlOptionsKeys.ERROR_LISTENER);
    }

    public XmlOptions setSaveAggressiveNamespaces() {
        return this.setSaveAggressiveNamespaces(true);
    }

    public XmlOptions setSaveAggressiveNamespaces(boolean b) {
        return this.set(XmlOptionsKeys.SAVE_AGGRESSIVE_NAMESPACES, b);
    }

    public boolean isSaveAggressiveNamespaces() {
        return this.hasOption(XmlOptionsKeys.SAVE_AGGRESSIVE_NAMESPACES);
    }

    public XmlOptions setSaveSyntheticDocumentElement(QName name) {
        return this.set(XmlOptionsKeys.SAVE_SYNTHETIC_DOCUMENT_ELEMENT, name);
    }

    public QName getSaveSyntheticDocumentElement() {
        return (QName)this.get(XmlOptionsKeys.SAVE_SYNTHETIC_DOCUMENT_ELEMENT);
    }

    public XmlOptions setUseDefaultNamespace() {
        return this.setUseDefaultNamespace(true);
    }

    public XmlOptions setUseDefaultNamespace(boolean b) {
        return this.set(XmlOptionsKeys.SAVE_USE_DEFAULT_NAMESPACE, b);
    }

    public boolean isUseDefaultNamespace() {
        return this.hasOption(XmlOptionsKeys.SAVE_USE_DEFAULT_NAMESPACE);
    }

    public XmlOptions setSaveImplicitNamespaces(Map<String, String> implicitNamespaces) {
        return this.set(XmlOptionsKeys.SAVE_IMPLICIT_NAMESPACES, implicitNamespaces);
    }

    public Map<String, String> getSaveImplicitNamespaces() {
        return (Map)this.get(XmlOptionsKeys.SAVE_IMPLICIT_NAMESPACES);
    }

    public XmlOptions setSaveSuggestedPrefixes(Map<String, String> suggestedPrefixes) {
        return this.set(XmlOptionsKeys.SAVE_SUGGESTED_PREFIXES, suggestedPrefixes);
    }

    public Map<String, String> getSaveSuggestedPrefixes() {
        return (Map)this.get(XmlOptionsKeys.SAVE_SUGGESTED_PREFIXES);
    }

    public XmlOptions setSaveFilterProcinst(String filterProcinst) {
        return this.set(XmlOptionsKeys.SAVE_FILTER_PROCINST, filterProcinst);
    }

    public String getSaveFilterProcinst() {
        return (String)this.get(XmlOptionsKeys.SAVE_FILTER_PROCINST);
    }

    public XmlOptions setSaveSubstituteCharacters(XmlOptionCharEscapeMap characterReplacementMap) {
        return this.set(XmlOptionsKeys.SAVE_SUBSTITUTE_CHARACTERS, characterReplacementMap);
    }

    public XmlOptionCharEscapeMap getSaveSubstituteCharacters() {
        return (XmlOptionCharEscapeMap)this.get(XmlOptionsKeys.SAVE_SUBSTITUTE_CHARACTERS);
    }

    public XmlOptions setSaveUseOpenFrag() {
        return this.setSaveUseOpenFrag(true);
    }

    public XmlOptions setSaveUseOpenFrag(boolean b) {
        return this.set(XmlOptionsKeys.SAVE_USE_OPEN_FRAGMENT, b);
    }

    public boolean isSaveUseOpenFrag() {
        return this.hasOption(XmlOptionsKeys.SAVE_USE_OPEN_FRAGMENT);
    }

    public XmlOptions setSaveOuter() {
        return this.setSaveOuter(true);
    }

    public XmlOptions setSaveOuter(boolean b) {
        return this.set(XmlOptionsKeys.SAVE_OUTER, b);
    }

    public boolean isSaveOuter() {
        return this.hasOption(XmlOptionsKeys.SAVE_OUTER);
    }

    public XmlOptions setSaveInner() {
        return this.setSaveInner(true);
    }

    public XmlOptions setSaveInner(boolean b) {
        return this.set(XmlOptionsKeys.SAVE_INNER, b);
    }

    public boolean isSaveInner() {
        return this.hasOption(XmlOptionsKeys.SAVE_INNER);
    }

    public XmlOptions setSaveNoXmlDecl() {
        return this.setSaveNoXmlDecl(true);
    }

    public XmlOptions setSaveNoXmlDecl(boolean b) {
        return this.set(XmlOptionsKeys.SAVE_NO_XML_DECL, b);
    }

    public boolean isSaveNoXmlDecl() {
        return this.hasOption(XmlOptionsKeys.SAVE_NO_XML_DECL);
    }

    public XmlOptions setSaveCDataLengthThreshold(int cdataLengthThreshold) {
        return this.set(XmlOptionsKeys.SAVE_CDATA_LENGTH_THRESHOLD, cdataLengthThreshold);
    }

    public Integer getSaveCDataLengthThreshold() {
        return (Integer)this.get(XmlOptionsKeys.SAVE_CDATA_LENGTH_THRESHOLD);
    }

    public XmlOptions setSaveCDataEntityCountThreshold(int cdataEntityCountThreshold) {
        return this.set(XmlOptionsKeys.SAVE_CDATA_ENTITY_COUNT_THRESHOLD, cdataEntityCountThreshold);
    }

    public Integer getSaveCDataEntityCountThreshold() {
        return (Integer)this.get(XmlOptionsKeys.SAVE_CDATA_ENTITY_COUNT_THRESHOLD);
    }

    public XmlOptions setUseCDataBookmarks() {
        return this.set(XmlOptionsKeys.LOAD_SAVE_CDATA_BOOKMARKS);
    }

    public boolean isUseCDataBookmarks() {
        return this.hasOption(XmlOptionsKeys.LOAD_SAVE_CDATA_BOOKMARKS);
    }

    public XmlOptions setSaveSaxNoNSDeclsInAttributes() {
        return this.setSaveSaxNoNSDeclsInAttributes(true);
    }

    public XmlOptions setSaveSaxNoNSDeclsInAttributes(boolean b) {
        return this.set(XmlOptionsKeys.SAVE_SAX_NO_NSDECLS_IN_ATTRIBUTES, b);
    }

    public boolean isSaveSaxNoNSDeclsInAttributes() {
        return this.hasOption(XmlOptionsKeys.SAVE_SAX_NO_NSDECLS_IN_ATTRIBUTES);
    }

    public XmlOptions setLoadReplaceDocumentElement(QName replacement) {
        return this.set(XmlOptionsKeys.LOAD_REPLACE_DOCUMENT_ELEMENT, replacement);
    }

    public QName getLoadReplaceDocumentElement() {
        return (QName)this.get(XmlOptionsKeys.LOAD_REPLACE_DOCUMENT_ELEMENT);
    }

    public XmlOptions setLoadStripWhitespace() {
        return this.setLoadStripWhitespace(true);
    }

    public XmlOptions setLoadStripWhitespace(boolean b) {
        return this.set(XmlOptionsKeys.LOAD_STRIP_WHITESPACE, b);
    }

    public boolean isSetLoadStripWhitespace() {
        return this.hasOption(XmlOptionsKeys.LOAD_STRIP_WHITESPACE);
    }

    public XmlOptions setLoadStripComments() {
        return this.setLoadStripComments(true);
    }

    public XmlOptions setLoadStripComments(boolean b) {
        return this.set(XmlOptionsKeys.LOAD_STRIP_COMMENTS, b);
    }

    public boolean isLoadStripComments() {
        return this.hasOption(XmlOptionsKeys.LOAD_STRIP_COMMENTS);
    }

    public XmlOptions setLoadStripProcinsts() {
        return this.setLoadStripProcinsts(true);
    }

    public XmlOptions setLoadStripProcinsts(boolean b) {
        return this.set(XmlOptionsKeys.LOAD_STRIP_PROCINSTS, b);
    }

    public boolean isLoadStripProcinsts() {
        return this.hasOption(XmlOptionsKeys.LOAD_STRIP_PROCINSTS);
    }

    public XmlOptions setLoadLineNumbers() {
        return this.setLoadLineNumbers(true);
    }

    public XmlOptions setLoadLineNumbers(boolean b) {
        return this.set(XmlOptionsKeys.LOAD_LINE_NUMBERS, b);
    }

    public boolean isLoadLineNumbers() {
        return this.hasOption(XmlOptionsKeys.LOAD_LINE_NUMBERS);
    }

    public XmlOptions setLoadLineNumbersEndElement() {
        return this.setLoadLineNumbersEndElement(true);
    }

    public XmlOptions setLoadLineNumbersEndElement(boolean b) {
        this.setLoadLineNumbers(true);
        return this.set(XmlOptionsKeys.LOAD_LINE_NUMBERS_END_ELEMENT, b);
    }

    public boolean isLoadLineNumbersEndElement() {
        return this.hasOption(XmlOptionsKeys.LOAD_LINE_NUMBERS_END_ELEMENT);
    }

    public XmlOptions setLoadSubstituteNamespaces(Map<String, String> substNamespaces) {
        return this.set(XmlOptionsKeys.LOAD_SUBSTITUTE_NAMESPACES, substNamespaces);
    }

    public Map<String, String> getLoadSubstituteNamespaces() {
        return (Map)this.get(XmlOptionsKeys.LOAD_SUBSTITUTE_NAMESPACES);
    }

    public XmlOptions setLoadTrimTextBuffer() {
        return this.setLoadTrimTextBuffer(true);
    }

    public XmlOptions setLoadTrimTextBuffer(boolean b) {
        return this.set(XmlOptionsKeys.LOAD_TRIM_TEXT_BUFFER, b);
    }

    public boolean isLoadTrimTextBuffer() {
        return this.hasOption(XmlOptionsKeys.LOAD_TRIM_TEXT_BUFFER);
    }

    public XmlOptions setLoadAdditionalNamespaces(Map<String, String> nses) {
        return this.set(XmlOptionsKeys.LOAD_ADDITIONAL_NAMESPACES, nses);
    }

    public Map<String, String> getLoadAdditionalNamespaces() {
        return (Map)this.get(XmlOptionsKeys.LOAD_ADDITIONAL_NAMESPACES);
    }

    public XmlOptions setLoadMessageDigest() {
        return this.setLoadMessageDigest(true);
    }

    public XmlOptions setLoadMessageDigest(boolean b) {
        return this.set(XmlOptionsKeys.LOAD_MESSAGE_DIGEST, b);
    }

    public boolean isLoadMessageDigest() {
        return this.hasOption(XmlOptionsKeys.LOAD_MESSAGE_DIGEST);
    }

    public XmlOptions setLoadUseDefaultResolver() {
        return this.setLoadUseDefaultResolver(true);
    }

    public XmlOptions setLoadUseDefaultResolver(boolean b) {
        return this.set(XmlOptionsKeys.LOAD_USE_DEFAULT_RESOLVER, b);
    }

    public boolean isLoadUseDefaultResolver() {
        return this.hasOption(XmlOptionsKeys.LOAD_USE_DEFAULT_RESOLVER);
    }

    public XmlOptions setLoadUseXMLReader(XMLReader xmlReader) {
        return this.set(XmlOptionsKeys.LOAD_USE_XMLREADER, xmlReader);
    }

    public XMLReader getLoadUseXMLReader() {
        return (XMLReader)this.get(XmlOptionsKeys.LOAD_USE_XMLREADER);
    }

    public XmlOptions setXqueryCurrentNodeVar(String varName) {
        return this.set(XmlOptionsKeys.XQUERY_CURRENT_NODE_VAR, varName);
    }

    public String getXqueryCurrentNodeVar() {
        return (String)this.get(XmlOptionsKeys.XQUERY_CURRENT_NODE_VAR);
    }

    public XmlOptions setXqueryVariables(Map<String, Object> varMap) {
        return this.set(XmlOptionsKeys.XQUERY_VARIABLE_MAP, varMap);
    }

    public Map<String, Object> getXqueryVariables() {
        return (Map)this.get(XmlOptionsKeys.XQUERY_VARIABLE_MAP);
    }

    public XmlOptions setDocumentSourceName(String documentSourceName) {
        return this.set(XmlOptionsKeys.DOCUMENT_SOURCE_NAME, documentSourceName);
    }

    public String getDocumentSourceName() {
        return (String)this.get(XmlOptionsKeys.DOCUMENT_SOURCE_NAME);
    }

    public XmlOptions setCompileSubstituteNames(Map<QName, QName> nameMap) {
        return this.set(XmlOptionsKeys.COMPILE_SUBSTITUTE_NAMES, nameMap);
    }

    public Map<QName, QName> getCompileSubstituteNames() {
        return (Map)this.get(XmlOptionsKeys.COMPILE_SUBSTITUTE_NAMES);
    }

    public XmlOptions setCompileNoValidation() {
        return this.set(XmlOptionsKeys.COMPILE_NO_VALIDATION);
    }

    public boolean isCompileNoValidation() {
        return this.hasOption(XmlOptionsKeys.COMPILE_NO_VALIDATION);
    }

    public XmlOptions setCompileNoUpaRule() {
        return this.setCompileNoUpaRule(true);
    }

    public XmlOptions setCompileNoUpaRule(boolean b) {
        return this.set(XmlOptionsKeys.COMPILE_NO_UPA_RULE, b);
    }

    public boolean isCompileNoUpaRule() {
        return this.hasOption(XmlOptionsKeys.COMPILE_NO_UPA_RULE);
    }

    public XmlOptions setCompileNoPvrRule() {
        return this.setCompileNoPvrRule(true);
    }

    public XmlOptions setCompileNoPvrRule(boolean b) {
        return this.set(XmlOptionsKeys.COMPILE_NO_PVR_RULE, b);
    }

    public boolean isCompileNoPvrRule() {
        return this.hasOption(XmlOptionsKeys.COMPILE_NO_PVR_RULE);
    }

    public XmlOptions setCompileNoAnnotations() {
        return this.setCompileNoAnnotations(true);
    }

    public XmlOptions setCompileNoAnnotations(boolean b) {
        return this.set(XmlOptionsKeys.COMPILE_NO_ANNOTATIONS, b);
    }

    public boolean isCompileNoAnnotations() {
        return this.hasOption(XmlOptionsKeys.COMPILE_NO_ANNOTATIONS);
    }

    public XmlOptions setCompileDownloadUrls() {
        return this.setCompileDownloadUrls(true);
    }

    public XmlOptions setCompileDownloadUrls(boolean b) {
        return this.set(XmlOptionsKeys.COMPILE_DOWNLOAD_URLS, b);
    }

    public boolean isCompileDownloadUrls() {
        return this.hasOption(XmlOptionsKeys.COMPILE_DOWNLOAD_URLS);
    }

    public XmlOptions setCompileMdefNamespaces(Set<String> mdefNamespaces) {
        return this.set(XmlOptionsKeys.COMPILE_MDEF_NAMESPACES, mdefNamespaces);
    }

    public Set<String> getCompileMdefNamespaces() {
        return (Set)this.get(XmlOptionsKeys.COMPILE_MDEF_NAMESPACES);
    }

    public XmlOptions setCompilePartialTypesystem() {
        return this.setCompilePartialTypesystem(true);
    }

    public XmlOptions setCompilePartialTypesystem(boolean compilePartialTypesystem) {
        return this.set(XmlOptionsKeys.COMPILE_PARTIAL_TYPESYSTEM, compilePartialTypesystem);
    }

    public boolean isCompilePartialTypesystem() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.COMPILE_PARTIAL_TYPESYSTEM);
        return flag != null && flag != false;
    }

    public XmlOptions setValidateOnSet() {
        return this.setValidateOnSet(true);
    }

    public XmlOptions setValidateOnSet(boolean b) {
        return this.set(XmlOptionsKeys.VALIDATE_ON_SET, b);
    }

    public boolean isValidateOnSet() {
        return this.hasOption(XmlOptionsKeys.VALIDATE_ON_SET);
    }

    public XmlOptions setValidateTreatLaxAsSkip() {
        return this.setValidateTreatLaxAsSkip(true);
    }

    public XmlOptions setValidateTreatLaxAsSkip(boolean b) {
        return this.set(XmlOptionsKeys.VALIDATE_TREAT_LAX_AS_SKIP, b);
    }

    public boolean isValidateTreatLaxAsSkip() {
        return this.hasOption(XmlOptionsKeys.VALIDATE_TREAT_LAX_AS_SKIP);
    }

    public XmlOptions setValidateStrict() {
        return this.setValidateStrict(true);
    }

    public XmlOptions setValidateStrict(boolean b) {
        return this.set(XmlOptionsKeys.VALIDATE_STRICT, b);
    }

    public boolean isValidateStrict() {
        return this.hasOption(XmlOptionsKeys.VALIDATE_STRICT);
    }

    public XmlOptions setValidateTextOnly() {
        return this.setValidateTextOnly(true);
    }

    public XmlOptions setValidateTextOnly(boolean b) {
        return this.set(XmlOptionsKeys.VALIDATE_TEXT_ONLY, b);
    }

    public boolean isValidateTextOnly() {
        return this.hasOption(XmlOptionsKeys.VALIDATE_TEXT_ONLY);
    }

    public XmlOptions setUnsynchronized() {
        return this.setUnsynchronized(true);
    }

    public XmlOptions setUnsynchronized(boolean b) {
        return this.set(XmlOptionsKeys.UNSYNCHRONIZED, b);
    }

    public boolean isUnsynchronized() {
        return this.hasOption(XmlOptionsKeys.UNSYNCHRONIZED);
    }

    public XmlOptions setEntityResolver(EntityResolver resolver) {
        return this.set(XmlOptionsKeys.ENTITY_RESOLVER, resolver);
    }

    public EntityResolver getEntityResolver() {
        return (EntityResolver)this.get(XmlOptionsKeys.ENTITY_RESOLVER);
    }

    public XmlOptions setBaseURI(URI baseURI) {
        return this.set(XmlOptionsKeys.BASE_URI, baseURI);
    }

    public URI getBaseURI() {
        return (URI)this.get(XmlOptionsKeys.BASE_URI);
    }

    public XmlOptions setSchemaCodePrinter(SchemaCodePrinter printer) {
        return this.set(XmlOptionsKeys.SCHEMA_CODE_PRINTER, printer);
    }

    public SchemaCodePrinter getSchemaCodePrinter() {
        return (SchemaCodePrinter)this.get(XmlOptionsKeys.SCHEMA_CODE_PRINTER);
    }

    public XmlOptions setCopyUseNewSynchronizationDomain(boolean useNewSyncDomain) {
        return this.set(XmlOptionsKeys.COPY_USE_NEW_SYNC_DOMAIN, useNewSyncDomain);
    }

    public boolean isCopyUseNewSynchronizationDomain() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.COPY_USE_NEW_SYNC_DOMAIN);
        return flag != null && flag != false;
    }

    public XmlOptions setUseSameLocale(Object localeOrXmlTokenSource) {
        return this.set(XmlOptionsKeys.USE_SAME_LOCALE, localeOrXmlTokenSource);
    }

    public Object getUseSameLocale() {
        return this.get(XmlOptionsKeys.USE_SAME_LOCALE);
    }

    public XmlOptions setLoadEntityBytesLimit(int entityBytesLimit) {
        return this.set(XmlOptionsKeys.LOAD_ENTITY_BYTES_LIMIT, entityBytesLimit);
    }

    public Integer getLoadEntityBytesLimit() {
        return (Integer)this.get(XmlOptionsKeys.LOAD_ENTITY_BYTES_LIMIT);
    }

    public XmlOptions setEntityExpansionLimit(int entityExpansionLimit) {
        return this.set(XmlOptionsKeys.ENTITY_EXPANSION_LIMIT, entityExpansionLimit);
    }

    public int getEntityExpansionLimit() {
        Integer limit = (Integer)this.get(XmlOptionsKeys.ENTITY_EXPANSION_LIMIT);
        return limit == null ? 2048 : limit;
    }

    public XmlOptions setLoadDTDGrammar(boolean loadDTDGrammar) {
        return this.set(XmlOptionsKeys.LOAD_DTD_GRAMMAR, loadDTDGrammar);
    }

    public boolean isLoadDTDGrammar() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.LOAD_DTD_GRAMMAR);
        return flag != null && flag != false;
    }

    public XmlOptions setLoadExternalDTD(boolean loadExternalDTD) {
        return this.set(XmlOptionsKeys.LOAD_EXTERNAL_DTD, loadExternalDTD);
    }

    public boolean isLoadExternalDTD() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.LOAD_EXTERNAL_DTD);
        return flag != null && flag != false;
    }

    public XmlOptions setDisallowDocTypeDeclaration(boolean disallowDocTypeDeclaration) {
        return this.set(XmlOptionsKeys.DISALLOW_DOCTYPE_DECLARATION, disallowDocTypeDeclaration);
    }

    public boolean disallowDocTypeDeclaration() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.DISALLOW_DOCTYPE_DECLARATION);
        return flag != null && flag != false;
    }

    public XmlOptions setSaveOptimizeForSpeed(boolean saveOptimizeForSpeed) {
        return this.set(XmlOptionsKeys.SAVE_OPTIMIZE_FOR_SPEED, saveOptimizeForSpeed);
    }

    public boolean isSaveOptimizeForSpeed() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.SAVE_OPTIMIZE_FOR_SPEED);
        return flag != null && flag != false;
    }

    public XmlOptions setSaaj(Saaj saaj) {
        return this.set(XmlOptionsKeys.SAAJ_IMPL, saaj);
    }

    public Saaj getSaaj() {
        return (Saaj)this.get(XmlOptionsKeys.SAAJ_IMPL);
    }

    public XmlOptions setLoadUseLocaleCharUtil(boolean useCharUtil) {
        return this.set(XmlOptionsKeys.LOAD_USE_LOCALE_CHAR_UTIL, useCharUtil);
    }

    public boolean isLoadUseLocaleCharUtil() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.LOAD_USE_LOCALE_CHAR_UTIL);
        return flag != null && flag != false;
    }

    public XmlOptions setXPathUseSaxon() {
        return this.setXPathUseSaxon(true);
    }

    public XmlOptions setXPathUseSaxon(boolean xpathUseSaxon) {
        return this.set(XmlOptionsKeys.XPATH_USE_SAXON, xpathUseSaxon);
    }

    public boolean isXPathUseSaxon() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.XPATH_USE_SAXON);
        return flag != null && flag != false;
    }

    public XmlOptions setXPathUseXmlBeans() {
        return this.setXPathUseSaxon(true);
    }

    public XmlOptions setXPathUseXmlBeans(boolean xpathUseXmlBeans) {
        return this.set(XmlOptionsKeys.XPATH_USE_XMLBEANS, xpathUseXmlBeans);
    }

    public boolean isXPathUseXmlBeans() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.XPATH_USE_XMLBEANS);
        return flag != null && flag != false;
    }

    public XmlOptions setCompileAnnotationAsJavadoc() {
        return this.setCompileAnnotationAsJavadoc(true);
    }

    public XmlOptions setCompileAnnotationAsJavadoc(boolean useAnnotationAsJavadoc) {
        return this.set(XmlOptionsKeys.COMPILE_ANNOTATION_JAVADOC, useAnnotationAsJavadoc);
    }

    public boolean isCompileAnnotationAsJavadoc() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.COMPILE_ANNOTATION_JAVADOC);
        return flag != null && flag != false;
    }

    public XmlOptions setAttributeValidationCompatMode(boolean attributeValidationCompatMode) {
        return this.set(XmlOptionsKeys.ATTRIBUTE_VALIDATION_COMPAT_MODE, attributeValidationCompatMode);
    }

    public boolean isAttributeValidationCompatMode() {
        Boolean flag = (Boolean)this.get(XmlOptionsKeys.ATTRIBUTE_VALIDATION_COMPAT_MODE);
        return flag != null && flag != false;
    }

    public Set<BeanMethod> getCompilePartialMethod() {
        return (Set)this.get(XmlOptionsKeys.COMPILE_PARTIAL_METHODS);
    }

    public void setCompilePartialMethod(Set<BeanMethod> list) {
        if (list == null || list.isEmpty()) {
            this.remove(XmlOptionsKeys.COMPILE_PARTIAL_METHODS);
        } else {
            this.set(XmlOptionsKeys.COMPILE_PARTIAL_METHODS, list);
        }
    }

    public static XmlOptions maskNull(XmlOptions o) {
        return o == null ? EMPTY_OPTIONS : o;
    }

    private XmlOptions set(XmlOptionsKeys option) {
        return this.set(option, true);
    }

    private XmlOptions set(XmlOptionsKeys option, Object value) {
        this._map.put(option, value);
        return this;
    }

    private XmlOptions set(XmlOptionsKeys option, int value) {
        return this.set(option, (Object)value);
    }

    private XmlOptions set(XmlOptionsKeys option, boolean value) {
        if (value) {
            this.set(option, Boolean.TRUE);
        } else {
            this.remove(option);
        }
        return this;
    }

    public boolean hasOption(XmlOptionsKeys option) {
        return this._map.containsKey((Object)option);
    }

    public Object get(XmlOptionsKeys option) {
        return this._map.get((Object)option);
    }

    public void remove(XmlOptionsKeys option) {
        this._map.remove((Object)option);
    }

    static {
        XmlOptions.EMPTY_OPTIONS._map = Collections.unmodifiableMap(XmlOptions.EMPTY_OPTIONS._map);
    }

    public static enum BeanMethod {
        GET,
        XGET,
        IS_SET,
        IS_NIL,
        IS_NIL_IDX,
        SET,
        SET_NIL,
        SET_NIL_IDX,
        XSET,
        UNSET,
        GET_ARRAY,
        XGET_ARRAY,
        GET_IDX,
        XGET_IDX,
        XSET_ARRAY,
        XSET_IDX,
        SIZE_OF_ARRAY,
        SET_ARRAY,
        SET_IDX,
        INSERT_IDX,
        INSERT_NEW_IDX,
        ADD,
        ADD_NEW,
        REMOVE_IDX,
        GET_LIST,
        XGET_LIST,
        SET_LIST,
        INSTANCE_TYPE;

    }

    public static enum XmlOptionsKeys {
        SAVE_NAMESPACES_FIRST,
        SAVE_SYNTHETIC_DOCUMENT_ELEMENT,
        SAVE_PRETTY_PRINT,
        SAVE_PRETTY_PRINT_INDENT,
        SAVE_PRETTY_PRINT_OFFSET,
        SAVE_AGGRESSIVE_NAMESPACES,
        SAVE_USE_DEFAULT_NAMESPACE,
        SAVE_IMPLICIT_NAMESPACES,
        SAVE_SUGGESTED_PREFIXES,
        SAVE_FILTER_PROCINST,
        SAVE_USE_OPEN_FRAGMENT,
        SAVE_OUTER,
        SAVE_INNER,
        SAVE_NO_XML_DECL,
        SAVE_SUBSTITUTE_CHARACTERS,
        SAVE_OPTIMIZE_FOR_SPEED,
        SAVE_CDATA_LENGTH_THRESHOLD,
        SAVE_CDATA_ENTITY_COUNT_THRESHOLD,
        SAVE_SAX_NO_NSDECLS_IN_ATTRIBUTES,
        LOAD_REPLACE_DOCUMENT_ELEMENT,
        LOAD_STRIP_WHITESPACE,
        LOAD_STRIP_COMMENTS,
        LOAD_STRIP_PROCINSTS,
        LOAD_LINE_NUMBERS,
        LOAD_LINE_NUMBERS_END_ELEMENT,
        LOAD_SAVE_CDATA_BOOKMARKS,
        LOAD_SUBSTITUTE_NAMESPACES,
        LOAD_TRIM_TEXT_BUFFER,
        LOAD_ADDITIONAL_NAMESPACES,
        LOAD_MESSAGE_DIGEST,
        LOAD_USE_DEFAULT_RESOLVER,
        LOAD_USE_XMLREADER,
        XQUERY_CURRENT_NODE_VAR,
        XQUERY_VARIABLE_MAP,
        CHARACTER_ENCODING,
        ERROR_LISTENER,
        DOCUMENT_TYPE,
        DOCUMENT_SOURCE_NAME,
        COMPILE_SUBSTITUTE_NAMES,
        COMPILE_NO_VALIDATION,
        COMPILE_NO_UPA_RULE,
        COMPILE_NO_PVR_RULE,
        COMPILE_NO_ANNOTATIONS,
        COMPILE_DOWNLOAD_URLS,
        COMPILE_MDEF_NAMESPACES,
        COMPILE_PARTIAL_TYPESYSTEM,
        COMPILE_PARTIAL_METHODS,
        COMPILE_ANNOTATION_JAVADOC,
        VALIDATE_ON_SET,
        VALIDATE_TREAT_LAX_AS_SKIP,
        VALIDATE_STRICT,
        VALIDATE_TEXT_ONLY,
        UNSYNCHRONIZED,
        ENTITY_RESOLVER,
        BASE_URI,
        SCHEMA_CODE_PRINTER,
        GENERATE_JAVA_VERSION,
        USE_SAME_LOCALE,
        COPY_USE_NEW_SYNC_DOMAIN,
        LOAD_ENTITY_BYTES_LIMIT,
        ENTITY_EXPANSION_LIMIT,
        LOAD_DTD_GRAMMAR,
        LOAD_EXTERNAL_DTD,
        DISALLOW_DOCTYPE_DECLARATION,
        SAAJ_IMPL,
        LOAD_USE_LOCALE_CHAR_UTIL,
        XPATH_USE_SAXON,
        XPATH_USE_XMLBEANS,
        ATTRIBUTE_VALIDATION_COMPAT_MODE;

    }
}


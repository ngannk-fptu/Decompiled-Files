/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLXML;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.core.BaseConnection;
import org.postgresql.jdbc.ResourceLock;
import org.postgresql.util.GT;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.postgresql.xml.DefaultPGXmlFactoryFactory;
import org.postgresql.xml.PGXmlFactoryFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class PgSQLXML
implements SQLXML {
    private final ResourceLock lock = new ResourceLock();
    private final BaseConnection conn;
    private @Nullable String data;
    private boolean initialized;
    private boolean active;
    private boolean freed;
    private @Nullable ByteArrayOutputStream byteArrayOutputStream;
    private @Nullable StringWriter stringWriter;
    private @Nullable DOMResult domResult;

    public PgSQLXML(BaseConnection conn) {
        this(conn, null, false);
    }

    public PgSQLXML(BaseConnection conn, @Nullable String data) {
        this(conn, data, true);
    }

    private PgSQLXML(BaseConnection conn, @Nullable String data, boolean initialized) {
        this.conn = conn;
        this.data = data;
        this.initialized = initialized;
        this.active = false;
        this.freed = false;
    }

    private PGXmlFactoryFactory getXmlFactoryFactory() throws SQLException {
        if (this.conn != null) {
            return this.conn.getXmlFactoryFactory();
        }
        return DefaultPGXmlFactoryFactory.INSTANCE;
    }

    @Override
    public void free() {
        try (ResourceLock ignore = this.lock.obtain();){
            this.freed = true;
            this.data = null;
        }
    }

    /*
     * Loose catch block
     */
    @Override
    public @Nullable InputStream getBinaryStream() throws SQLException {
        ResourceLock ignore = this.lock.obtain();
        Throwable throwable = null;
        this.checkFreed();
        this.ensureInitialized();
        if (this.data == null) {
            InputStream inputStream = null;
            return inputStream;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.conn.getEncoding().encode(this.data));
        return byteArrayInputStream;
        {
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        finally {
            if (ignore != null) {
                if (throwable != null) {
                    try {
                        ignore.close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed(throwable3);
                    }
                } else {
                    ignore.close();
                }
            }
        }
    }

    @Override
    public @Nullable Reader getCharacterStream() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkFreed();
            this.ensureInitialized();
            if (this.data == null) {
                Reader reader = null;
                return reader;
            }
            StringReader stringReader = new StringReader(this.data);
            return stringReader;
        }
    }

    @Override
    public <T extends Source> @Nullable T getSource(@Nullable Class<T> sourceClass) throws SQLException {
        Throwable throwable = null;
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkFreed();
            this.ensureInitialized();
            String data = this.data;
            if (data == null) {
                T t = null;
                return t;
            }
            if (sourceClass == null || DOMSource.class.equals(sourceClass)) {
                DOMSource domSource;
                DocumentBuilder builder = this.getXmlFactoryFactory().newDocumentBuilder();
                InputSource input = new InputSource(new StringReader(data));
                DOMSource dOMSource = domSource = new DOMSource(builder.parse(input));
                return (T)dOMSource;
            }
            if (SAXSource.class.equals(sourceClass)) {
                XMLReader reader = this.getXmlFactoryFactory().createXMLReader();
                InputSource is = new InputSource(new StringReader(data));
                Source source = (Source)sourceClass.cast(new SAXSource(reader, is));
                return (T)source;
            }
            if (StreamSource.class.equals(sourceClass)) {
                Source reader = (Source)sourceClass.cast(new StreamSource(new StringReader(data)));
                return (T)reader;
            }
            if (StAXSource.class.equals(sourceClass)) {
                XMLInputFactory xif = this.getXmlFactoryFactory().newXMLInputFactory();
                XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(data));
                Source source = (Source)sourceClass.cast(new StAXSource(xsr));
                return (T)source;
            }
            try {
                throw new PSQLException(GT.tr("Unknown XML Source class: {0}", sourceClass), PSQLState.INVALID_PARAMETER_TYPE);
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
    }

    @Override
    public @Nullable String getString() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkFreed();
            this.ensureInitialized();
            String string = this.data;
            return string;
        }
    }

    @Override
    public OutputStream setBinaryStream() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkFreed();
            this.initialize();
            this.active = true;
            ByteArrayOutputStream byteArrayOutputStream = this.byteArrayOutputStream = new ByteArrayOutputStream();
            return byteArrayOutputStream;
        }
    }

    @Override
    public Writer setCharacterStream() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkFreed();
            this.initialize();
            this.active = true;
            StringWriter stringWriter = this.stringWriter = new StringWriter();
            return stringWriter;
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    public <T extends Result> T setResult(Class<T> resultClass) throws SQLException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 26[SIMPLE_IF_TAKEN]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public void setString(String value) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.checkFreed();
            this.initialize();
            this.data = value;
        }
    }

    private void checkFreed() throws SQLException {
        if (this.freed) {
            throw new PSQLException(GT.tr("This SQLXML object has already been freed.", new Object[0]), PSQLState.OBJECT_NOT_IN_STATE);
        }
    }

    private void ensureInitialized() throws SQLException {
        if (!this.initialized) {
            throw new PSQLException(GT.tr("This SQLXML object has not been initialized, so you cannot retrieve data from it.", new Object[0]), PSQLState.OBJECT_NOT_IN_STATE);
        }
        if (!this.active) {
            return;
        }
        if (this.byteArrayOutputStream != null) {
            try {
                this.data = this.conn.getEncoding().decode(this.byteArrayOutputStream.toByteArray());
            }
            catch (IOException ioe) {
                throw new PSQLException(GT.tr("Failed to convert binary xml data to encoding: {0}.", this.conn.getEncoding().name()), PSQLState.DATA_ERROR, (Throwable)ioe);
            }
            finally {
                this.byteArrayOutputStream = null;
                this.active = false;
            }
        }
        if (this.stringWriter != null) {
            this.data = this.stringWriter.toString();
            this.stringWriter = null;
            this.active = false;
        } else if (this.domResult != null) {
            DOMResult domResult = this.domResult;
            try {
                TransformerFactory factory = this.getXmlFactoryFactory().newTransformerFactory();
                Transformer transformer = factory.newTransformer();
                DOMSource domSource = new DOMSource(domResult.getNode());
                StringWriter stringWriter = new StringWriter();
                StreamResult streamResult = new StreamResult(stringWriter);
                transformer.transform(domSource, streamResult);
                this.data = stringWriter.toString();
            }
            catch (TransformerException te) {
                throw new PSQLException(GT.tr("Unable to convert DOMResult SQLXML data to a string.", new Object[0]), PSQLState.DATA_ERROR, (Throwable)te);
            }
            finally {
                domResult = null;
                this.active = false;
            }
        }
    }

    private void initialize() throws SQLException {
        if (this.initialized) {
            throw new PSQLException(GT.tr("This SQLXML object has already been initialized, so you cannot manipulate it further.", new Object[0]), PSQLState.OBJECT_NOT_IN_STATE);
        }
        this.initialized = true;
    }
}


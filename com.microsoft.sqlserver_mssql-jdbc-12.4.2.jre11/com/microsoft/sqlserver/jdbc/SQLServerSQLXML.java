/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ByteArrayOutputStreamToInputStream;
import com.microsoft.sqlserver.jdbc.DDC;
import com.microsoft.sqlserver.jdbc.Encoding;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.PLPXMLInputStream;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerEntityResolver;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamType;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

final class SQLServerSQLXML
implements SQLXML {
    private final SQLServerConnection con;
    private final PLPXMLInputStream contents;
    private final InputStreamGetterArgs getterArgs;
    private final TypeInfo typeInfo;
    private boolean isUsed = false;
    private boolean isFreed = false;
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerSQLXML");
    private ByteArrayOutputStreamToInputStream outputStreamValue;
    private Document docValue;
    private String strValue;
    private static final AtomicInteger baseID = new AtomicInteger(0);
    private final String traceID;

    public final String toString() {
        return this.traceID;
    }

    private static int nextInstanceID() {
        return baseID.incrementAndGet();
    }

    InputStream getValue() throws SQLServerException {
        this.checkClosed();
        if (!this.isUsed) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_noDataXML"), null, true);
        }
        assert (null == this.contents);
        ByteArrayInputStream o = null;
        if (null != this.outputStreamValue) {
            o = this.outputStreamValue.getInputStream();
            assert (null == this.docValue);
            assert (null == this.strValue);
        } else if (null != this.docValue) {
            assert (null == this.outputStreamValue);
            assert (null == this.strValue);
            ByteArrayOutputStreamToInputStream strm = new ByteArrayOutputStreamToInputStream();
            try {
                TransformerFactory factory = TransformerFactory.newInstance();
                factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                factory.newTransformer().transform(new DOMSource(this.docValue), new StreamResult(strm));
            }
            catch (TransformerException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
                Object[] msgArgs = new Object[]{e.toString()};
                SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            }
            o = strm.getInputStream();
        } else {
            assert (null == this.outputStreamValue);
            assert (null == this.docValue);
            assert (null != this.strValue);
            o = new ByteArrayInputStream(this.strValue.getBytes(Encoding.UNICODE.charset()));
        }
        assert (null != o);
        this.isFreed = true;
        return o;
    }

    SQLServerSQLXML(SQLServerConnection connection) {
        this.contents = null;
        this.traceID = " SQLServerSQLXML:" + SQLServerSQLXML.nextInstanceID();
        this.con = connection;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(this.toString() + " created by (" + connection.toString() + ")");
        }
        this.getterArgs = null;
        this.typeInfo = null;
    }

    SQLServerSQLXML(InputStream stream, InputStreamGetterArgs getterArgs, TypeInfo typeInfo) {
        this.traceID = " SQLServerSQLXML:" + SQLServerSQLXML.nextInstanceID();
        this.contents = (PLPXMLInputStream)stream;
        this.con = null;
        this.getterArgs = getterArgs;
        this.typeInfo = typeInfo;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(this.toString() + " created by (null connection)");
        }
    }

    InputStream getStream() {
        return this.contents;
    }

    @Override
    public void free() throws SQLException {
        if (!this.isFreed) {
            this.isFreed = true;
            if (null != this.contents) {
                try {
                    this.contents.close();
                }
                catch (IOException e) {
                    SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
                }
            }
        }
    }

    private void checkClosed() throws SQLServerException {
        if (this.isFreed || null != this.con && this.con.isClosed()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_isFreed"));
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[]{"SQLXML"}), null, true);
        }
    }

    private void checkReadXML() throws SQLException {
        if (null == this.contents) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_writeOnlyXML"), null, true);
        }
        if (this.isUsed) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_dataHasBeenReadXML"), null, true);
        }
        try {
            this.contents.checkClosed();
        }
        catch (IOException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_isFreed"));
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[]{"SQLXML"}), null, true);
        }
    }

    void checkWriteXML() throws SQLException {
        if (null != this.contents) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_readOnlyXML"), null, true);
        }
        if (this.isUsed) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_dataHasBeenSetXML"), null, true);
        }
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        this.checkClosed();
        this.checkReadXML();
        this.isUsed = true;
        return this.contents;
    }

    @Override
    public OutputStream setBinaryStream() throws SQLException {
        this.checkClosed();
        this.checkWriteXML();
        this.isUsed = true;
        this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
        return this.outputStreamValue;
    }

    @Override
    public Writer setCharacterStream() throws SQLException {
        this.checkClosed();
        this.checkWriteXML();
        this.isUsed = true;
        this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
        return new OutputStreamWriter((OutputStream)this.outputStreamValue, Encoding.UNICODE.charset());
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        this.checkClosed();
        this.checkReadXML();
        this.isUsed = true;
        StreamType type = StreamType.CHARACTER;
        InputStreamGetterArgs newArgs = new InputStreamGetterArgs(type, this.getterArgs.isAdaptive, this.getterArgs.isStreaming, this.getterArgs.logContext);
        assert (null != this.contents);
        try {
            this.contents.read();
            this.contents.read();
        }
        catch (IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        return (Reader)DDC.convertStreamToObject(this.contents, this.typeInfo, type.getJDBCType(), newArgs);
    }

    @Override
    public String getString() throws SQLException {
        this.checkClosed();
        this.checkReadXML();
        this.isUsed = true;
        assert (null != this.contents);
        try {
            this.contents.read();
            this.contents.read();
        }
        catch (IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        byte[] byteContents = this.contents.getBytes();
        return new String(byteContents, 0, byteContents.length, Encoding.UNICODE.charset());
    }

    @Override
    public void setString(String value) throws SQLException {
        this.checkClosed();
        this.checkWriteXML();
        this.isUsed = true;
        if (null == value) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_cantSetNull"), null, true);
        }
        this.strValue = value;
    }

    @Override
    public <T extends Source> T getSource(Class<T> iface) throws SQLException {
        this.checkClosed();
        this.checkReadXML();
        if (null == iface) {
            StreamSource src = this.getSourceInternal(StreamSource.class);
            return (T)src;
        }
        return this.getSourceInternal(iface);
    }

    <T extends Source> T getSourceInternal(Class<T> iface) throws SQLException {
        this.isUsed = true;
        Source src = null;
        if (DOMSource.class == iface) {
            src = (Source)iface.cast(this.getDOMSource());
        } else if (SAXSource.class == iface) {
            src = (Source)iface.cast(this.getSAXSource());
        } else if (StAXSource.class == iface) {
            src = (Source)iface.cast(this.getStAXSource());
        } else if (StreamSource.class == iface) {
            src = (Source)iface.cast(new StreamSource(this.contents));
        } else {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_notSupported"), null, true);
        }
        return (T)src;
    }

    @Override
    public <T extends Result> T setResult(Class<T> resultClass) throws SQLException {
        this.checkClosed();
        this.checkWriteXML();
        if (null == resultClass) {
            StreamResult result = this.setResultInternal(StreamResult.class);
            return (T)result;
        }
        return this.setResultInternal(resultClass);
    }

    <T extends Result> T setResultInternal(Class<T> resultClass) throws SQLException {
        this.isUsed = true;
        Result result = null;
        if (DOMResult.class == resultClass) {
            result = (Result)resultClass.cast(this.getDOMResult());
        } else if (SAXResult.class == resultClass) {
            result = (Result)resultClass.cast(this.getSAXResult());
        } else if (StAXResult.class == resultClass) {
            result = (Result)resultClass.cast(this.getStAXResult());
        } else if (StreamResult.class == resultClass) {
            this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
            result = (Result)resultClass.cast(new StreamResult(this.outputStreamValue));
        } else {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_notSupported"), null, true);
        }
        return (T)result;
    }

    private DOMSource getDOMSource() throws SQLException {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new SQLServerEntityResolver());
            try {
                document = builder.parse(this.contents);
            }
            catch (IOException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                Object[] msgArgs = new Object[]{e.toString()};
                SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
            }
            return new DOMSource(document);
        }
        catch (ParserConfigurationException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            Object[] msgArgs = new Object[]{e.toString()};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        catch (SAXException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_failedToParseXML"));
            Object[] msgArgs = new Object[]{e.toString()};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        return null;
    }

    private SAXSource getSAXSource() throws SQLException {
        try {
            InputSource src = new InputSource(this.contents);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            return new SAXSource(reader, src);
        }
        catch (ParserConfigurationException | SAXException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_failedToParseXML"));
            Object[] msgArgs = new Object[]{e.toString()};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            return null;
        }
    }

    private StAXSource getStAXSource() throws SQLException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader r = factory.createXMLStreamReader(this.contents);
            return new StAXSource(r);
        }
        catch (XMLStreamException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            Object[] msgArgs = new Object[]{e.toString()};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            return null;
        }
    }

    private StAXResult getStAXResult() throws SQLException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
        try {
            XMLStreamWriter r = factory.createXMLStreamWriter(this.outputStreamValue);
            return new StAXResult(r);
        }
        catch (XMLStreamException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            Object[] msgArgs = new Object[]{e.toString()};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            return null;
        }
    }

    private SAXResult getSAXResult() throws SQLException {
        TransformerHandler handler = null;
        try {
            SAXTransformerFactory stf = (SAXTransformerFactory)TransformerFactory.newInstance();
            stf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            handler = stf.newTransformerHandler();
        }
        catch (ClassCastException | TransformerConfigurationException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            Object[] msgArgs = new Object[]{e.toString()};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (handler == null) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            Object[] msgArgs = new Object[]{"null"};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
        handler.setResult(new StreamResult(this.outputStreamValue));
        return new SAXResult(handler);
    }

    private DOMResult getDOMResult() throws SQLException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        assert (null == this.outputStreamValue);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.docValue = builder.newDocument();
            return new DOMResult(this.docValue);
        }
        catch (ParserConfigurationException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            Object[] msgArgs = new Object[]{e.toString()};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            return null;
        }
    }
}


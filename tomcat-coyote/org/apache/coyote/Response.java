/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.WriteListener
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.servlet.WriteListener;
import org.apache.coyote.ActionCode;
import org.apache.coyote.ActionHook;
import org.apache.coyote.OutputBuffer;
import org.apache.coyote.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.MediaType;
import org.apache.tomcat.util.res.StringManager;

public final class Response {
    private static final StringManager sm = StringManager.getManager(Response.class);
    private static final Log log = LogFactory.getLog(Response.class);
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();
    int status = 200;
    String message = null;
    final MimeHeaders headers = new MimeHeaders();
    private Supplier<Map<String, String>> trailerFieldsSupplier = null;
    OutputBuffer outputBuffer;
    final Object[] notes = new Object[32];
    volatile boolean committed = false;
    volatile ActionHook hook;
    String contentType = null;
    String contentLanguage = null;
    Charset charset = null;
    String characterEncoding = null;
    long contentLength = -1L;
    private Locale locale = DEFAULT_LOCALE;
    private long contentWritten = 0L;
    private long commitTime = -1L;
    private Exception errorException = null;
    private final AtomicInteger errorState = new AtomicInteger(0);
    Request req;
    volatile WriteListener listener;
    private boolean fireListener = false;
    private boolean registeredForWrite = false;
    private final Object nonBlockingStateLock = new Object();

    public Request getRequest() {
        return this.req;
    }

    public void setRequest(Request req) {
        this.req = req;
    }

    public void setOutputBuffer(OutputBuffer outputBuffer) {
        this.outputBuffer = outputBuffer;
    }

    public MimeHeaders getMimeHeaders() {
        return this.headers;
    }

    protected void setHook(ActionHook hook) {
        this.hook = hook;
    }

    public void setNote(int pos, Object value) {
        this.notes[pos] = value;
    }

    public Object getNote(int pos) {
        return this.notes[pos];
    }

    public void action(ActionCode actionCode, Object param) {
        if (this.hook != null) {
            if (param == null) {
                this.hook.action(actionCode, this);
            } else {
                this.hook.action(actionCode, param);
            }
        }
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCommitted() {
        return this.committed;
    }

    public void setCommitted(boolean v) {
        if (v && !this.committed) {
            this.commitTime = System.currentTimeMillis();
        }
        this.committed = v;
    }

    public long getCommitTime() {
        return this.commitTime;
    }

    public void setErrorException(Exception ex) {
        this.errorException = ex;
    }

    public Exception getErrorException() {
        return this.errorException;
    }

    public boolean isExceptionPresent() {
        return this.errorException != null;
    }

    @Deprecated
    public boolean setError() {
        return this.errorState.compareAndSet(0, 1);
    }

    public boolean isError() {
        return this.errorState.get() > 0;
    }

    public boolean isErrorReportRequired() {
        return this.errorState.get() == 1;
    }

    public boolean setErrorReported() {
        return this.errorState.compareAndSet(1, 2);
    }

    public void reset() throws IllegalStateException {
        if (this.committed) {
            throw new IllegalStateException();
        }
        this.recycle();
    }

    public boolean containsHeader(String name) {
        return this.headers.getHeader(name) != null;
    }

    public void setHeader(String name, String value) {
        char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && this.checkSpecialHeader(name, value)) {
            return;
        }
        this.headers.setValue(name).setString(value);
    }

    public void addHeader(String name, String value) {
        this.addHeader(name, value, null);
    }

    public void addHeader(String name, String value, Charset charset) {
        char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && this.checkSpecialHeader(name, value)) {
            return;
        }
        MessageBytes mb = this.headers.addValue(name);
        if (charset != null) {
            mb.setCharset(charset);
        }
        mb.setString(value);
    }

    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        AtomicBoolean trailerFieldsSupported = new AtomicBoolean(false);
        this.action(ActionCode.IS_TRAILER_FIELDS_SUPPORTED, trailerFieldsSupported);
        if (!trailerFieldsSupported.get()) {
            throw new IllegalStateException(sm.getString("response.noTrailers.notSupported"));
        }
        this.trailerFieldsSupplier = supplier;
    }

    public Supplier<Map<String, String>> getTrailerFields() {
        return this.trailerFieldsSupplier;
    }

    private boolean checkSpecialHeader(String name, String value) {
        if (name.equalsIgnoreCase("Content-Type")) {
            this.setContentType(value);
            return true;
        }
        if (name.equalsIgnoreCase("Content-Length")) {
            try {
                long cL = Long.parseLong(value);
                this.setContentLength(cL);
                return true;
            }
            catch (NumberFormatException ex) {
                return false;
            }
        }
        return false;
    }

    public void sendHeaders() {
        this.action(ActionCode.COMMIT, this);
        this.setCommitted(true);
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        if (locale == null) {
            this.locale = null;
            this.contentLanguage = null;
            return;
        }
        this.locale = locale;
        this.contentLanguage = locale.toLanguageTag();
    }

    public String getContentLanguage() {
        return this.contentLanguage;
    }

    public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
        if (this.isCommitted()) {
            return;
        }
        if (characterEncoding == null) {
            this.charset = null;
            this.characterEncoding = null;
            return;
        }
        this.characterEncoding = characterEncoding;
        this.charset = B2CConverter.getCharset((String)characterEncoding);
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setContentType(String type) {
        if (type == null) {
            this.contentType = null;
            return;
        }
        MediaType m = null;
        try {
            m = MediaType.parseMediaType(new StringReader(type));
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (m == null) {
            this.contentType = type;
            return;
        }
        this.contentType = m.toStringNoCharset();
        String charsetValue = m.getCharset();
        if (charsetValue == null) {
            this.contentType = type;
        } else {
            this.contentType = m.toStringNoCharset();
            if ((charsetValue = charsetValue.trim()).length() > 0) {
                try {
                    this.charset = B2CConverter.getCharset((String)charsetValue);
                }
                catch (UnsupportedEncodingException e) {
                    log.warn((Object)sm.getString("response.encoding.invalid", new Object[]{charsetValue}), (Throwable)e);
                }
            }
        }
    }

    public void setContentTypeNoCharset(String type) {
        this.contentType = type;
    }

    public String getContentType() {
        String ret = this.contentType;
        if (ret != null && this.charset != null) {
            ret = ret + ";charset=" + this.characterEncoding;
        }
        return ret;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public int getContentLength() {
        long length = this.getContentLengthLong();
        if (length < Integer.MAX_VALUE) {
            return (int)length;
        }
        return -1;
    }

    public long getContentLengthLong() {
        return this.contentLength;
    }

    public void doWrite(ByteBuffer chunk) throws IOException {
        int len = chunk.remaining();
        this.outputBuffer.doWrite(chunk);
        this.contentWritten += (long)(len - chunk.remaining());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recycle() {
        this.contentType = null;
        this.contentLanguage = null;
        this.locale = DEFAULT_LOCALE;
        this.charset = null;
        this.characterEncoding = null;
        this.contentLength = -1L;
        this.status = 200;
        this.message = null;
        this.committed = false;
        this.commitTime = -1L;
        this.errorException = null;
        this.errorState.set(0);
        this.headers.recycle();
        this.trailerFieldsSupplier = null;
        this.listener = null;
        Object object = this.nonBlockingStateLock;
        synchronized (object) {
            this.fireListener = false;
            this.registeredForWrite = false;
        }
        this.contentWritten = 0L;
    }

    public long getContentWritten() {
        return this.contentWritten;
    }

    public long getBytesWritten(boolean flush) {
        if (flush) {
            this.action(ActionCode.CLIENT_FLUSH, this);
        }
        return this.outputBuffer.getBytesWritten();
    }

    public WriteListener getWriteListener() {
        return this.listener;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setWriteListener(WriteListener listener) {
        if (listener == null) {
            throw new NullPointerException(sm.getString("response.nullWriteListener"));
        }
        if (this.getWriteListener() != null) {
            throw new IllegalStateException(sm.getString("response.writeListenerSet"));
        }
        AtomicBoolean result = new AtomicBoolean(false);
        this.action(ActionCode.ASYNC_IS_ASYNC, result);
        if (!result.get()) {
            throw new IllegalStateException(sm.getString("response.notAsync"));
        }
        this.listener = listener;
        if (this.isReady()) {
            Object object = this.nonBlockingStateLock;
            synchronized (object) {
                this.registeredForWrite = true;
                this.fireListener = true;
            }
            this.action(ActionCode.DISPATCH_WRITE, null);
            if (!this.req.isRequestThread()) {
                this.action(ActionCode.DISPATCH_EXECUTE, null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isReady() {
        if (this.listener == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("response.notNonBlocking"));
            }
            return false;
        }
        boolean ready = false;
        Object object = this.nonBlockingStateLock;
        synchronized (object) {
            if (this.registeredForWrite) {
                this.fireListener = true;
                return false;
            }
            ready = this.checkRegisterForWrite();
            this.fireListener = !ready;
        }
        return ready;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean checkRegisterForWrite() {
        AtomicBoolean ready = new AtomicBoolean(false);
        Object object = this.nonBlockingStateLock;
        synchronized (object) {
            if (!this.registeredForWrite) {
                this.action(ActionCode.NB_WRITE_INTEREST, ready);
                this.registeredForWrite = !ready.get();
            }
        }
        return ready.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onWritePossible() throws IOException {
        boolean fire = false;
        Object object = this.nonBlockingStateLock;
        synchronized (object) {
            this.registeredForWrite = false;
            if (this.fireListener) {
                this.fireListener = false;
                fire = true;
            }
        }
        if (fire) {
            this.listener.onWritePossible();
        }
    }
}


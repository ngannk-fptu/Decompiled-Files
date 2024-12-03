/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.pop3;

import com.sun.mail.pop3.DefaultFolder;
import com.sun.mail.pop3.POP3Message;
import com.sun.mail.pop3.POP3Store;
import com.sun.mail.pop3.Protocol;
import com.sun.mail.pop3.Status;
import com.sun.mail.pop3.TempFile;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.MailLogger;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.UIDFolder;

public class POP3Folder
extends Folder {
    private String name;
    private POP3Store store;
    private volatile Protocol port;
    private int total;
    private int size;
    private boolean exists = false;
    private volatile boolean opened = false;
    private POP3Message[] message_cache;
    private boolean doneUidl = false;
    private volatile TempFile fileCache = null;
    private boolean forceClose;
    MailLogger logger;

    protected POP3Folder(POP3Store store, String name) {
        super(store);
        this.name = name;
        this.store = store;
        if (name.equalsIgnoreCase("INBOX")) {
            this.exists = true;
        }
        this.logger = new MailLogger(this.getClass(), "DEBUG POP3", store.getSession().getDebug(), store.getSession().getDebugOut());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFullName() {
        return this.name;
    }

    @Override
    public Folder getParent() {
        return new DefaultFolder(this.store);
    }

    @Override
    public boolean exists() {
        return this.exists;
    }

    @Override
    public Folder[] list(String pattern) throws MessagingException {
        throw new MessagingException("not a directory");
    }

    @Override
    public char getSeparator() {
        return '\u0000';
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean create(int type) throws MessagingException {
        return false;
    }

    @Override
    public boolean hasNewMessages() throws MessagingException {
        return false;
    }

    @Override
    public Folder getFolder(String name) throws MessagingException {
        throw new MessagingException("not a directory");
    }

    @Override
    public boolean delete(boolean recurse) throws MessagingException {
        throw new MethodNotSupportedException("delete");
    }

    @Override
    public boolean renameTo(Folder f) throws MessagingException {
        throw new MethodNotSupportedException("renameTo");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void open(int mode) throws MessagingException {
        this.checkClosed();
        if (!this.exists) {
            throw new FolderNotFoundException(this, "folder is not INBOX");
        }
        try {
            this.port = this.store.getPort(this);
            Status s = this.port.stat();
            this.total = s.total;
            this.size = s.size;
            this.mode = mode;
            if (this.store.useFileCache) {
                try {
                    this.fileCache = new TempFile(this.store.fileCacheDir);
                }
                catch (IOException ex) {
                    this.logger.log(Level.FINE, "failed to create file cache", ex);
                    throw ex;
                }
            }
            this.opened = true;
        }
        catch (IOException ioex) {
            try {
                if (this.port != null) {
                    this.port.quit();
                }
            }
            catch (IOException iOException) {
            }
            finally {
                this.port = null;
                this.store.closePort(this);
            }
            throw new MessagingException("Open failed", ioex);
        }
        this.message_cache = new POP3Message[this.total];
        this.doneUidl = false;
        this.notifyConnectionListeners(1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void close(boolean expunge) throws MessagingException {
        this.checkOpen();
        try {
            POP3Message m;
            int i;
            if (this.store.rsetBeforeQuit && !this.forceClose) {
                this.port.rset();
            }
            if (expunge && this.mode == 2 && !this.forceClose) {
                for (i = 0; i < this.message_cache.length; ++i) {
                    m = this.message_cache[i];
                    if (m == null || !m.isSet(Flags.Flag.DELETED)) continue;
                    try {
                        this.port.dele(i + 1);
                        continue;
                    }
                    catch (IOException ioex) {
                        throw new MessagingException("Exception deleting messages during close", ioex);
                    }
                }
            }
            for (i = 0; i < this.message_cache.length; ++i) {
                m = this.message_cache[i];
                if (m == null) continue;
                m.invalidate(true);
            }
            if (this.forceClose) {
                this.port.close();
            } else {
                this.port.quit();
            }
        }
        catch (IOException iOException) {
        }
        finally {
            this.port = null;
            this.store.closePort(this);
            this.message_cache = null;
            this.opened = false;
            this.notifyConnectionListeners(3);
            if (this.fileCache != null) {
                this.fileCache.close();
                this.fileCache = null;
            }
        }
    }

    @Override
    public synchronized boolean isOpen() {
        if (!this.opened) {
            return false;
        }
        try {
            if (!this.port.noop()) {
                throw new IOException("NOOP failed");
            }
        }
        catch (IOException ioex) {
            try {
                this.close(false);
            }
            catch (MessagingException messagingException) {
                // empty catch block
            }
            return false;
        }
        return true;
    }

    @Override
    public Flags getPermanentFlags() {
        return new Flags();
    }

    @Override
    public synchronized int getMessageCount() throws MessagingException {
        if (!this.opened) {
            return -1;
        }
        this.checkReadable();
        return this.total;
    }

    @Override
    public synchronized Message getMessage(int msgno) throws MessagingException {
        this.checkOpen();
        POP3Message m = this.message_cache[msgno - 1];
        if (m == null) {
            this.message_cache[msgno - 1] = m = this.createMessage(this, msgno);
        }
        return m;
    }

    protected POP3Message createMessage(Folder f, int msgno) throws MessagingException {
        POP3Message m = null;
        Constructor<?> cons = this.store.messageConstructor;
        if (cons != null) {
            try {
                Object[] o = new Object[]{this, msgno};
                m = (POP3Message)cons.newInstance(o);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (m == null) {
            m = new POP3Message(this, msgno);
        }
        return m;
    }

    @Override
    public void appendMessages(Message[] msgs) throws MessagingException {
        throw new MethodNotSupportedException("Append not supported");
    }

    @Override
    public Message[] expunge() throws MessagingException {
        throw new MethodNotSupportedException("Expunge not supported");
    }

    @Override
    public synchronized void fetch(Message[] msgs, FetchProfile fp) throws MessagingException {
        this.checkReadable();
        if (!this.doneUidl && this.store.supportsUidl && fp.contains(UIDFolder.FetchProfileItem.UID)) {
            String[] uids = new String[this.message_cache.length];
            try {
                if (!this.port.uidl(uids)) {
                    return;
                }
            }
            catch (EOFException eex) {
                this.close(false);
                throw new FolderClosedException(this, eex.toString());
            }
            catch (IOException ex) {
                throw new MessagingException("error getting UIDL", ex);
            }
            for (int i = 0; i < uids.length; ++i) {
                if (uids[i] == null) continue;
                POP3Message m = (POP3Message)this.getMessage(i + 1);
                m.uid = uids[i];
            }
            this.doneUidl = true;
        }
        if (fp.contains(FetchProfile.Item.ENVELOPE)) {
            for (int i = 0; i < msgs.length; ++i) {
                try {
                    POP3Message msg = (POP3Message)msgs[i];
                    msg.getHeader("");
                    msg.getSize();
                    continue;
                }
                catch (MessageRemovedException messageRemovedException) {
                    // empty catch block
                }
            }
        }
    }

    public synchronized String getUID(Message msg) throws MessagingException {
        this.checkOpen();
        if (!(msg instanceof POP3Message)) {
            throw new MessagingException("message is not a POP3Message");
        }
        POP3Message m = (POP3Message)msg;
        try {
            if (!this.store.supportsUidl) {
                return null;
            }
            if (m.uid == "UNKNOWN") {
                m.uid = this.port.uidl(m.getMessageNumber());
            }
            return m.uid;
        }
        catch (EOFException eex) {
            this.close(false);
            throw new FolderClosedException(this, eex.toString());
        }
        catch (IOException ex) {
            throw new MessagingException("error getting UIDL", ex);
        }
    }

    public synchronized int getSize() throws MessagingException {
        this.checkOpen();
        return this.size;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized int[] getSizes() throws MessagingException {
        this.checkOpen();
        int[] sizes = new int[this.total];
        InputStream is = null;
        FilterInputStream lis = null;
        try {
            String line;
            is = this.port.list();
            lis = new LineInputStream(is);
            while ((line = ((LineInputStream)lis).readLine()) != null) {
                try {
                    StringTokenizer st = new StringTokenizer(line);
                    int msgnum = Integer.parseInt(st.nextToken());
                    int size = Integer.parseInt(st.nextToken());
                    if (msgnum <= 0 || msgnum > this.total) continue;
                    sizes[msgnum - 1] = size;
                }
                catch (RuntimeException runtimeException) {}
            }
        }
        catch (IOException iOException) {
        }
        finally {
            try {
                if (lis != null) {
                    lis.close();
                }
            }
            catch (IOException iOException) {}
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException iOException) {}
        }
        return sizes;
    }

    public synchronized InputStream listCommand() throws MessagingException, IOException {
        this.checkOpen();
        return this.port.list();
    }

    @Override
    protected void finalize() throws Throwable {
        this.forceClose = !this.store.finalizeCleanClose;
        try {
            if (this.opened) {
                this.close(false);
            }
        }
        finally {
            super.finalize();
            this.forceClose = false;
        }
    }

    private void checkOpen() throws IllegalStateException {
        if (!this.opened) {
            throw new IllegalStateException("Folder is not Open");
        }
    }

    private void checkClosed() throws IllegalStateException {
        if (this.opened) {
            throw new IllegalStateException("Folder is Open");
        }
    }

    private void checkReadable() throws IllegalStateException {
        if (!this.opened || this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException("Folder is not Readable");
        }
    }

    Protocol getProtocol() throws MessagingException {
        Protocol p = this.port;
        this.checkOpen();
        return p;
    }

    @Override
    protected void notifyMessageChangedListeners(int type, Message m) {
        super.notifyMessageChangedListeners(type, m);
    }

    TempFile getFileCache() {
        return this.fileCache;
    }
}


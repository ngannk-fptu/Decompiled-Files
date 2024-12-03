/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.catalina.ha.deploy.FileMessage;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.res.StringManager;

public class FileMessageFactory {
    private static final Log log = LogFactory.getLog(FileMessageFactory.class);
    private static final StringManager sm = StringManager.getManager(FileMessageFactory.class);
    public static final int READ_SIZE = 10240;
    protected final File file;
    protected final boolean openForWrite;
    protected boolean closed = false;
    protected FileInputStream in;
    protected FileOutputStream out;
    protected int nrOfMessagesProcessed = 0;
    protected long size = 0L;
    protected long totalNrOfMessages = 0L;
    protected AtomicLong lastMessageProcessed = new AtomicLong(0L);
    protected final Map<Long, FileMessage> msgBuffer = new ConcurrentHashMap<Long, FileMessage>();
    protected byte[] data = new byte[10240];
    protected boolean isWriting = false;
    @Deprecated
    protected long creationTime = 0L;
    protected long lastModified = 0L;
    protected int maxValidTime = -1;

    private FileMessageFactory(File f, boolean openForWrite) throws FileNotFoundException, IOException {
        this.file = f;
        this.openForWrite = openForWrite;
        if (log.isDebugEnabled()) {
            log.debug((Object)("open file " + f + " write " + openForWrite));
        }
        if (openForWrite) {
            if (!this.file.exists() && !this.file.createNewFile()) {
                throw new IOException(sm.getString("fileNewFail", new Object[]{this.file}));
            }
            this.out = new FileOutputStream(f);
        } else {
            this.size = this.file.length();
            this.totalNrOfMessages = this.size / 10240L + 1L;
            this.in = new FileInputStream(f);
        }
        this.creationTime = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
    }

    public static FileMessageFactory getInstance(File f, boolean openForWrite) throws FileNotFoundException, IOException {
        return new FileMessageFactory(f, openForWrite);
    }

    public FileMessage readMessage(FileMessage f) throws IllegalArgumentException, IOException {
        this.checkState(false);
        int length = this.in.read(this.data);
        if (length == -1) {
            this.cleanup();
            return null;
        }
        f.setData(this.data, length);
        f.setTotalNrOfMsgs(this.totalNrOfMessages);
        f.setMessageNumber(++this.nrOfMessagesProcessed);
        return f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean writeMessage(FileMessage msg) throws IllegalArgumentException, IOException {
        if (!this.openForWrite) {
            throw new IllegalArgumentException(sm.getString("fileMessageFactory.cannotWrite"));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Message " + msg + " data " + HexUtils.toHexString((byte[])msg.getData()) + " data length " + msg.getDataLength() + " out " + this.out));
        }
        if ((long)msg.getMessageNumber() <= this.lastMessageProcessed.get()) {
            log.warn((Object)sm.getString("fileMessageFactory.duplicateMessage", new Object[]{msg.getContextName(), msg.getFileName(), HexUtils.toHexString((byte[])msg.getData()), msg.getDataLength()}));
            return false;
        }
        FileMessage previous = this.msgBuffer.put(Long.valueOf(msg.getMessageNumber()), msg);
        if (previous != null) {
            log.warn((Object)sm.getString("fileMessageFactory.duplicateMessage", new Object[]{msg.getContextName(), msg.getFileName(), HexUtils.toHexString((byte[])msg.getData()), msg.getDataLength()}));
            return false;
        }
        this.lastModified = System.currentTimeMillis();
        FileMessage next = null;
        FileMessageFactory fileMessageFactory = this;
        synchronized (fileMessageFactory) {
            if (!this.isWriting) {
                next = this.msgBuffer.get(this.lastMessageProcessed.get() + 1L);
                if (next == null) {
                    return false;
                }
            } else {
                return false;
            }
            this.isWriting = true;
        }
        while (next != null) {
            this.out.write(next.getData(), 0, next.getDataLength());
            this.lastMessageProcessed.incrementAndGet();
            this.out.flush();
            if ((long)next.getMessageNumber() == next.getTotalNrOfMsgs()) {
                this.out.close();
                this.cleanup();
                return true;
            }
            fileMessageFactory = this;
            synchronized (fileMessageFactory) {
                next = this.msgBuffer.get(this.lastMessageProcessed.get() + 1L);
                if (next == null) {
                    this.isWriting = false;
                }
            }
        }
        return false;
    }

    public void cleanup() {
        if (this.in != null) {
            try {
                this.in.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        if (this.out != null) {
            try {
                this.out.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.in = null;
        this.out = null;
        this.size = 0L;
        this.closed = true;
        this.data = null;
        this.nrOfMessagesProcessed = 0;
        this.totalNrOfMessages = 0L;
        this.msgBuffer.clear();
        this.lastMessageProcessed = null;
    }

    protected void checkState(boolean openForWrite) throws IllegalArgumentException {
        if (this.openForWrite != openForWrite) {
            this.cleanup();
            if (openForWrite) {
                throw new IllegalArgumentException(sm.getString("fileMessageFactory.cannotWrite"));
            }
            throw new IllegalArgumentException(sm.getString("fileMessageFactory.cannotRead"));
        }
        if (this.closed) {
            this.cleanup();
            throw new IllegalArgumentException(sm.getString("fileMessageFactory.closed"));
        }
    }

    @Deprecated
    public static void main(String[] args) throws Exception {
        System.out.println("Usage: FileMessageFactory fileToBeRead fileToBeWritten");
        System.out.println("Usage: This will make a copy of the file on the local file system");
        FileMessageFactory read = FileMessageFactory.getInstance(new File(args[0]), false);
        FileMessageFactory write = FileMessageFactory.getInstance(new File(args[1]), true);
        FileMessage msg = new FileMessage(null, args[0], args[0]);
        msg = read.readMessage(msg);
        if (msg == null) {
            System.out.println("Empty input file : " + args[0]);
            return;
        }
        System.out.println("Expecting to write " + msg.getTotalNrOfMsgs() + " messages.");
        int cnt = 0;
        while (msg != null) {
            write.writeMessage(msg);
            ++cnt;
            msg = read.readMessage(msg);
        }
        System.out.println("Actually wrote " + cnt + " messages.");
    }

    public File getFile() {
        return this.file;
    }

    public boolean isValid() {
        long timeNow;
        long timeIdle;
        if (this.maxValidTime > 0 && (timeIdle = ((timeNow = System.currentTimeMillis()) - this.lastModified) / 1000L) > (long)this.maxValidTime) {
            this.cleanup();
            if (this.file.exists()) {
                if (this.file.delete()) {
                    log.warn((Object)sm.getString("fileMessageFactory.delete", new Object[]{this.file, Long.toString(this.maxValidTime)}));
                } else {
                    log.warn((Object)sm.getString("fileMessageFactory.deleteFail", new Object[]{this.file}));
                }
            }
            return false;
        }
        return true;
    }

    public int getMaxValidTime() {
        return this.maxValidTime;
    }

    public void setMaxValidTime(int maxValidTime) {
        this.maxValidTime = maxValidTime;
    }
}


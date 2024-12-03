/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.FileCopyUtils
 */
package org.springframework.jdbc.support.lob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;

public class TemporaryLobCreator
implements LobCreator {
    protected static final Log logger = LogFactory.getLog(TemporaryLobCreator.class);
    private final Set<Blob> temporaryBlobs = new LinkedHashSet<Blob>(1);
    private final Set<Clob> temporaryClobs = new LinkedHashSet<Clob>(1);

    @Override
    public void setBlobAsBytes(PreparedStatement ps, int paramIndex, @Nullable byte[] content) throws SQLException {
        if (content != null) {
            Blob blob = ps.getConnection().createBlob();
            blob.setBytes(1L, content);
            this.temporaryBlobs.add(blob);
            ps.setBlob(paramIndex, blob);
        } else {
            ps.setBlob(paramIndex, (Blob)null);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)(content != null ? "Copied bytes into temporary BLOB with length " + content.length : "Set BLOB to null"));
        }
    }

    @Override
    public void setBlobAsBinaryStream(PreparedStatement ps, int paramIndex, @Nullable InputStream binaryStream, int contentLength) throws SQLException {
        if (binaryStream != null) {
            Blob blob = ps.getConnection().createBlob();
            try {
                FileCopyUtils.copy((InputStream)binaryStream, (OutputStream)blob.setBinaryStream(1L));
            }
            catch (IOException ex) {
                throw new DataAccessResourceFailureException("Could not copy into LOB stream", (Throwable)ex);
            }
            this.temporaryBlobs.add(blob);
            ps.setBlob(paramIndex, blob);
        } else {
            ps.setBlob(paramIndex, (Blob)null);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)(binaryStream != null ? "Copied binary stream into temporary BLOB with length " + contentLength : "Set BLOB to null"));
        }
    }

    @Override
    public void setClobAsString(PreparedStatement ps, int paramIndex, @Nullable String content) throws SQLException {
        if (content != null) {
            Clob clob = ps.getConnection().createClob();
            clob.setString(1L, content);
            this.temporaryClobs.add(clob);
            ps.setClob(paramIndex, clob);
        } else {
            ps.setClob(paramIndex, (Clob)null);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)(content != null ? "Copied string into temporary CLOB with length " + content.length() : "Set CLOB to null"));
        }
    }

    @Override
    public void setClobAsAsciiStream(PreparedStatement ps, int paramIndex, @Nullable InputStream asciiStream, int contentLength) throws SQLException {
        if (asciiStream != null) {
            Clob clob = ps.getConnection().createClob();
            try {
                FileCopyUtils.copy((InputStream)asciiStream, (OutputStream)clob.setAsciiStream(1L));
            }
            catch (IOException ex) {
                throw new DataAccessResourceFailureException("Could not copy into LOB stream", (Throwable)ex);
            }
            this.temporaryClobs.add(clob);
            ps.setClob(paramIndex, clob);
        } else {
            ps.setClob(paramIndex, (Clob)null);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)(asciiStream != null ? "Copied ASCII stream into temporary CLOB with length " + contentLength : "Set CLOB to null"));
        }
    }

    @Override
    public void setClobAsCharacterStream(PreparedStatement ps, int paramIndex, @Nullable Reader characterStream, int contentLength) throws SQLException {
        if (characterStream != null) {
            Clob clob = ps.getConnection().createClob();
            try {
                FileCopyUtils.copy((Reader)characterStream, (Writer)clob.setCharacterStream(1L));
            }
            catch (IOException ex) {
                throw new DataAccessResourceFailureException("Could not copy into LOB stream", (Throwable)ex);
            }
            this.temporaryClobs.add(clob);
            ps.setClob(paramIndex, clob);
        } else {
            ps.setClob(paramIndex, (Clob)null);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)(characterStream != null ? "Copied character stream into temporary CLOB with length " + contentLength : "Set CLOB to null"));
        }
    }

    @Override
    public void close() {
        for (Blob blob : this.temporaryBlobs) {
            try {
                blob.free();
            }
            catch (SQLException ex) {
                logger.warn((Object)"Could not free BLOB", (Throwable)ex);
            }
        }
        for (Clob clob : this.temporaryClobs) {
            try {
                clob.free();
            }
            catch (SQLException ex) {
                logger.warn((Object)"Could not free CLOB", (Throwable)ex);
            }
        }
    }
}


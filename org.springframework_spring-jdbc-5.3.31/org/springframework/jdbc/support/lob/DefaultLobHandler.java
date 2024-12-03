/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support.lob;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.support.lob.AbstractLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.PassThroughBlob;
import org.springframework.jdbc.support.lob.PassThroughClob;
import org.springframework.jdbc.support.lob.TemporaryLobCreator;
import org.springframework.lang.Nullable;

public class DefaultLobHandler
extends AbstractLobHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private boolean wrapAsLob = false;
    private boolean streamAsLob = false;
    private boolean createTemporaryLob = false;

    public void setWrapAsLob(boolean wrapAsLob) {
        this.wrapAsLob = wrapAsLob;
    }

    public void setStreamAsLob(boolean streamAsLob) {
        this.streamAsLob = streamAsLob;
    }

    public void setCreateTemporaryLob(boolean createTemporaryLob) {
        this.createTemporaryLob = createTemporaryLob;
    }

    @Override
    @Nullable
    public byte[] getBlobAsBytes(ResultSet rs, int columnIndex) throws SQLException {
        this.logger.debug((Object)"Returning BLOB as bytes");
        if (this.wrapAsLob) {
            Blob blob = rs.getBlob(columnIndex);
            return blob.getBytes(1L, (int)blob.length());
        }
        return rs.getBytes(columnIndex);
    }

    @Override
    @Nullable
    public InputStream getBlobAsBinaryStream(ResultSet rs, int columnIndex) throws SQLException {
        this.logger.debug((Object)"Returning BLOB as binary stream");
        if (this.wrapAsLob) {
            Blob blob = rs.getBlob(columnIndex);
            return blob.getBinaryStream();
        }
        return rs.getBinaryStream(columnIndex);
    }

    @Override
    @Nullable
    public String getClobAsString(ResultSet rs, int columnIndex) throws SQLException {
        this.logger.debug((Object)"Returning CLOB as string");
        if (this.wrapAsLob) {
            Clob clob = rs.getClob(columnIndex);
            return clob.getSubString(1L, (int)clob.length());
        }
        return rs.getString(columnIndex);
    }

    @Override
    public InputStream getClobAsAsciiStream(ResultSet rs, int columnIndex) throws SQLException {
        this.logger.debug((Object)"Returning CLOB as ASCII stream");
        if (this.wrapAsLob) {
            Clob clob = rs.getClob(columnIndex);
            return clob.getAsciiStream();
        }
        return rs.getAsciiStream(columnIndex);
    }

    @Override
    public Reader getClobAsCharacterStream(ResultSet rs, int columnIndex) throws SQLException {
        this.logger.debug((Object)"Returning CLOB as character stream");
        if (this.wrapAsLob) {
            Clob clob = rs.getClob(columnIndex);
            return clob.getCharacterStream();
        }
        return rs.getCharacterStream(columnIndex);
    }

    @Override
    public LobCreator getLobCreator() {
        return this.createTemporaryLob ? new TemporaryLobCreator() : new DefaultLobCreator();
    }

    protected class DefaultLobCreator
    implements LobCreator {
        protected DefaultLobCreator() {
        }

        @Override
        public void setBlobAsBytes(PreparedStatement ps, int paramIndex, @Nullable byte[] content) throws SQLException {
            if (DefaultLobHandler.this.streamAsLob) {
                if (content != null) {
                    ps.setBlob(paramIndex, new ByteArrayInputStream(content), content.length);
                } else {
                    ps.setBlob(paramIndex, (Blob)null);
                }
            } else if (DefaultLobHandler.this.wrapAsLob) {
                if (content != null) {
                    ps.setBlob(paramIndex, new PassThroughBlob(content));
                } else {
                    ps.setBlob(paramIndex, (Blob)null);
                }
            } else {
                ps.setBytes(paramIndex, content);
            }
            if (DefaultLobHandler.this.logger.isDebugEnabled()) {
                DefaultLobHandler.this.logger.debug((Object)(content != null ? "Set bytes for BLOB with length " + content.length : "Set BLOB to null"));
            }
        }

        @Override
        public void setBlobAsBinaryStream(PreparedStatement ps, int paramIndex, @Nullable InputStream binaryStream, int contentLength) throws SQLException {
            if (DefaultLobHandler.this.streamAsLob) {
                if (binaryStream != null) {
                    if (contentLength >= 0) {
                        ps.setBlob(paramIndex, binaryStream, contentLength);
                    } else {
                        ps.setBlob(paramIndex, binaryStream);
                    }
                } else {
                    ps.setBlob(paramIndex, (Blob)null);
                }
            } else if (DefaultLobHandler.this.wrapAsLob) {
                if (binaryStream != null) {
                    ps.setBlob(paramIndex, new PassThroughBlob(binaryStream, contentLength));
                } else {
                    ps.setBlob(paramIndex, (Blob)null);
                }
            } else if (contentLength >= 0) {
                ps.setBinaryStream(paramIndex, binaryStream, contentLength);
            } else {
                ps.setBinaryStream(paramIndex, binaryStream);
            }
            if (DefaultLobHandler.this.logger.isDebugEnabled()) {
                DefaultLobHandler.this.logger.debug((Object)(binaryStream != null ? "Set binary stream for BLOB with length " + contentLength : "Set BLOB to null"));
            }
        }

        @Override
        public void setClobAsString(PreparedStatement ps, int paramIndex, @Nullable String content) throws SQLException {
            if (DefaultLobHandler.this.streamAsLob) {
                if (content != null) {
                    ps.setClob(paramIndex, new StringReader(content), content.length());
                } else {
                    ps.setClob(paramIndex, (Clob)null);
                }
            } else if (DefaultLobHandler.this.wrapAsLob) {
                if (content != null) {
                    ps.setClob(paramIndex, new PassThroughClob(content));
                } else {
                    ps.setClob(paramIndex, (Clob)null);
                }
            } else {
                ps.setString(paramIndex, content);
            }
            if (DefaultLobHandler.this.logger.isDebugEnabled()) {
                DefaultLobHandler.this.logger.debug((Object)(content != null ? "Set string for CLOB with length " + content.length() : "Set CLOB to null"));
            }
        }

        @Override
        public void setClobAsAsciiStream(PreparedStatement ps, int paramIndex, @Nullable InputStream asciiStream, int contentLength) throws SQLException {
            if (DefaultLobHandler.this.streamAsLob) {
                if (asciiStream != null) {
                    InputStreamReader reader = new InputStreamReader(asciiStream, StandardCharsets.US_ASCII);
                    if (contentLength >= 0) {
                        ps.setClob(paramIndex, reader, contentLength);
                    } else {
                        ps.setClob(paramIndex, reader);
                    }
                } else {
                    ps.setClob(paramIndex, (Clob)null);
                }
            } else if (DefaultLobHandler.this.wrapAsLob) {
                if (asciiStream != null) {
                    ps.setClob(paramIndex, new PassThroughClob(asciiStream, (long)contentLength));
                } else {
                    ps.setClob(paramIndex, (Clob)null);
                }
            } else if (contentLength >= 0) {
                ps.setAsciiStream(paramIndex, asciiStream, contentLength);
            } else {
                ps.setAsciiStream(paramIndex, asciiStream);
            }
            if (DefaultLobHandler.this.logger.isDebugEnabled()) {
                DefaultLobHandler.this.logger.debug((Object)(asciiStream != null ? "Set ASCII stream for CLOB with length " + contentLength : "Set CLOB to null"));
            }
        }

        @Override
        public void setClobAsCharacterStream(PreparedStatement ps, int paramIndex, @Nullable Reader characterStream, int contentLength) throws SQLException {
            if (DefaultLobHandler.this.streamAsLob) {
                if (characterStream != null) {
                    if (contentLength >= 0) {
                        ps.setClob(paramIndex, characterStream, contentLength);
                    } else {
                        ps.setClob(paramIndex, characterStream);
                    }
                } else {
                    ps.setClob(paramIndex, (Clob)null);
                }
            } else if (DefaultLobHandler.this.wrapAsLob) {
                if (characterStream != null) {
                    ps.setClob(paramIndex, new PassThroughClob(characterStream, (long)contentLength));
                } else {
                    ps.setClob(paramIndex, (Clob)null);
                }
            } else if (contentLength >= 0) {
                ps.setCharacterStream(paramIndex, characterStream, contentLength);
            } else {
                ps.setCharacterStream(paramIndex, characterStream);
            }
            if (DefaultLobHandler.this.logger.isDebugEnabled()) {
                DefaultLobHandler.this.logger.debug((Object)(characterStream != null ? "Set character stream for CLOB with length " + contentLength : "Set CLOB to null"));
            }
        }

        @Override
        public void close() {
        }
    }
}


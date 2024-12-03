/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.BinaryStream;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public abstract class BlobTypeDescriptor
implements SqlTypeDescriptor {
    public static final BlobTypeDescriptor DEFAULT = new BlobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    BlobTypeDescriptor descriptor = BLOB_BINDING;
                    if (byte[].class.isInstance(value)) {
                        descriptor = PRIMITIVE_ARRAY_BINDING;
                    } else if (options.useStreamForLobBinding()) {
                        descriptor = STREAM_BINDING;
                    }
                    descriptor.getBlobBinder(javaTypeDescriptor).doBind(st, value, index, options);
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    BlobTypeDescriptor descriptor = BLOB_BINDING;
                    if (byte[].class.isInstance(value)) {
                        descriptor = PRIMITIVE_ARRAY_BINDING;
                    } else if (options.useStreamForLobBinding()) {
                        descriptor = STREAM_BINDING;
                    }
                    descriptor.getBlobBinder(javaTypeDescriptor).doBind(st, value, name, options);
                }
            };
        }
    };
    public static final BlobTypeDescriptor PRIMITIVE_ARRAY_BINDING = new BlobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                public void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    st.setBytes(index, javaTypeDescriptor.unwrap(value, byte[].class, options));
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    st.setBytes(name, javaTypeDescriptor.unwrap(value, byte[].class, options));
                }
            };
        }
    };
    public static final BlobTypeDescriptor BLOB_BINDING = new BlobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    st.setBlob(index, javaTypeDescriptor.unwrap(value, Blob.class, options));
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    st.setBlob(name, javaTypeDescriptor.unwrap(value, Blob.class, options));
                }
            };
        }
    };
    public static final BlobTypeDescriptor STREAM_BINDING = new BlobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    BinaryStream binaryStream = javaTypeDescriptor.unwrap(value, BinaryStream.class, options);
                    st.setBinaryStream(index, binaryStream.getInputStream(), binaryStream.getLength());
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    BinaryStream binaryStream = javaTypeDescriptor.unwrap(value, BinaryStream.class, options);
                    st.setBinaryStream(name, binaryStream.getInputStream(), binaryStream.getLength());
                }
            };
        }
    };

    private BlobTypeDescriptor() {
    }

    @Override
    public int getSqlType() {
        return 2004;
    }

    @Override
    public boolean canBeRemapped() {
        return true;
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X>(javaTypeDescriptor, this){

            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(rs.getBlob(name), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getBlob(index), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getBlob(name), options);
            }
        };
    }

    protected abstract <X> BasicBinder<X> getBlobBinder(JavaTypeDescriptor<X> var1);

    public <X> BasicBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
        return this.getBlobBinder(javaTypeDescriptor);
    }
}


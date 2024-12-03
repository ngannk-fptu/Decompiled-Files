/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import java.sql.CallableStatement;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public abstract class NClobTypeDescriptor
implements SqlTypeDescriptor {
    public static final NClobTypeDescriptor DEFAULT = new NClobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getNClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    if (options.useStreamForLobBinding()) {
                        STREAM_BINDING.getNClobBinder(javaTypeDescriptor).doBind(st, value, index, options);
                    } else {
                        NCLOB_BINDING.getNClobBinder(javaTypeDescriptor).doBind(st, value, index, options);
                    }
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    if (options.useStreamForLobBinding()) {
                        STREAM_BINDING.getNClobBinder(javaTypeDescriptor).doBind(st, value, name, options);
                    } else {
                        NCLOB_BINDING.getNClobBinder(javaTypeDescriptor).doBind(st, value, name, options);
                    }
                }
            };
        }
    };
    public static final NClobTypeDescriptor NCLOB_BINDING = new NClobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getNClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    st.setNClob(index, javaTypeDescriptor.unwrap(value, NClob.class, options));
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    st.setNClob(name, javaTypeDescriptor.unwrap(value, NClob.class, options));
                }
            };
        }
    };
    public static final NClobTypeDescriptor STREAM_BINDING = new NClobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getNClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    st.setNCharacterStream(index, characterStream.asReader(), characterStream.getLength());
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    st.setNCharacterStream(name, characterStream.asReader(), characterStream.getLength());
                }
            };
        }
    };

    @Override
    public int getSqlType() {
        return 2011;
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
                return javaTypeDescriptor.wrap(rs.getNClob(name), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getNClob(index), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getNClob(name), options);
            }
        };
    }

    protected abstract <X> BasicBinder<X> getNClobBinder(JavaTypeDescriptor<X> var1);

    @Override
    public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
        return this.getNClobBinder(javaTypeDescriptor);
    }
}


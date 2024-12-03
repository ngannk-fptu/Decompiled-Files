/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import java.sql.CallableStatement;
import java.sql.Clob;
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

public abstract class ClobTypeDescriptor
implements SqlTypeDescriptor {
    public static final ClobTypeDescriptor DEFAULT = new ClobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    if (options.useStreamForLobBinding()) {
                        STREAM_BINDING.getClobBinder(javaTypeDescriptor).doBind(st, value, index, options);
                    } else {
                        CLOB_BINDING.getClobBinder(javaTypeDescriptor).doBind(st, value, index, options);
                    }
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    if (options.useStreamForLobBinding()) {
                        STREAM_BINDING.getClobBinder(javaTypeDescriptor).doBind(st, value, name, options);
                    } else {
                        CLOB_BINDING.getClobBinder(javaTypeDescriptor).doBind(st, value, name, options);
                    }
                }
            };
        }
    };
    public static final ClobTypeDescriptor CLOB_BINDING = new ClobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    st.setClob(index, javaTypeDescriptor.unwrap(value, Clob.class, options));
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    st.setClob(name, javaTypeDescriptor.unwrap(value, Clob.class, options));
                }
            };
        }
    };
    public static final ClobTypeDescriptor STREAM_BINDING = new ClobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    st.setCharacterStream(index, characterStream.asReader(), characterStream.getLength());
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    st.setCharacterStream(name, characterStream.asReader(), characterStream.getLength());
                }
            };
        }
    };
    public static final ClobTypeDescriptor STREAM_BINDING_EXTRACTING = new ClobTypeDescriptor(){

        @Override
        public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    st.setCharacterStream(index, characterStream.asReader(), characterStream.getLength());
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    st.setCharacterStream(name, characterStream.asReader(), characterStream.getLength());
                }
            };
        }

        @Override
        public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicExtractor<X>(javaTypeDescriptor, this){

                @Override
                protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(rs.getCharacterStream(name), options);
                }

                @Override
                protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getCharacterStream(index), options);
                }

                @Override
                protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getCharacterStream(name), options);
                }
            };
        }
    };

    @Override
    public int getSqlType() {
        return 2005;
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
                return javaTypeDescriptor.wrap(rs.getClob(name), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getClob(index), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getClob(name), options);
            }
        };
    }

    protected abstract <X> BasicBinder<X> getClobBinder(JavaTypeDescriptor<X> var1);

    @Override
    public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
        return this.getClobBinder(javaTypeDescriptor);
    }
}


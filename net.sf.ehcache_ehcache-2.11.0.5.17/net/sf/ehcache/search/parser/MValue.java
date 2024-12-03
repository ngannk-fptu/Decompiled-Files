/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import java.sql.Date;
import net.sf.ehcache.search.parser.CustomParseException;
import net.sf.ehcache.search.parser.ModelElement;
import net.sf.ehcache.search.parser.ParseException;
import net.sf.ehcache.search.parser.ParserSupport;
import net.sf.ehcache.search.parser.Token;

public abstract class MValue<T>
implements ModelElement<T> {
    private final String value;
    private final String typeName;
    private final Token tok;
    private final CustomParseException.Message errMessage;
    private T javaObject;

    public MValue(Token tok, String typeName, CustomParseException.Message errMessage, String value) {
        this.value = value;
        this.errMessage = errMessage;
        this.typeName = typeName;
        this.tok = tok;
    }

    @Override
    public T asEhcacheObject(ClassLoader loader) {
        return this.javaObject;
    }

    protected void cacheJavaObject() throws CustomParseException {
        try {
            this.javaObject = this.constructJavaObject();
        }
        catch (Throwable e) {
            throw CustomParseException.factory(this.tok, this.errMessage);
        }
    }

    public String toString() {
        return "(" + this.typeName + ")'" + (this.value == null ? "null" : this.value) + "'";
    }

    public String getTypeName() {
        return this.typeName;
    }

    public String getValue() {
        return this.value;
    }

    protected abstract T constructJavaObject();

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.typeName == null ? 0 : this.typeName.hashCode());
        result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MValue other = (MValue)obj;
        if (this.typeName == null ? other.typeName != null : !this.typeName.equals(other.typeName)) {
            return false;
        }
        return !(this.value == null ? other.value != null : !this.value.equals(other.value));
    }

    public static class MEnum<T extends Enum<T>>
    extends MValue<Enum<T>> {
        private final String className;

        public MEnum(Token tok, String className, String value) throws CustomParseException {
            super(tok, "enum", CustomParseException.Message.ENUM_LITERAL, value);
            this.className = className;
        }

        @Override
        protected Enum<T> constructJavaObject() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Enum<T> asEhcacheObject(ClassLoader loader) {
            return ParserSupport.makeEnumFromString(loader, this.className, this.getValue());
        }

        @Override
        public String toString() {
            return "(enum " + this.className + ")'" + this.getValue() + "'";
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = super.hashCode();
            result = 31 * result + (this.className == null ? 0 : this.className.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            MEnum other = (MEnum)obj;
            return !(this.className == null ? other.className != null : !this.className.equals(other.className));
        }
    }

    public static class MChar
    extends MValue<Character> {
        public MChar(Token tok, String value) throws CustomParseException {
            super(tok, "character", CustomParseException.Message.CHAR_LITERAL, value);
            this.cacheJavaObject();
        }

        @Override
        protected Character constructJavaObject() {
            return Character.valueOf(this.getValue().toCharArray()[0]);
        }

        @Override
        public String toString() {
            return "'" + this.getValue() + "'";
        }
    }

    public static class MString
    extends MValue<String> {
        public MString(Token tok, String value) throws CustomParseException {
            super(tok, "string", CustomParseException.Message.STRING_LITERAL, value);
            this.cacheJavaObject();
        }

        @Override
        protected String constructJavaObject() {
            return this.getValue();
        }

        @Override
        public String toString() {
            return "'" + this.getValue() + "'";
        }
    }

    public static class MSqlDate
    extends MValue<Date> {
        public MSqlDate(Token tok, String value) throws CustomParseException {
            super(tok, "sqldate", CustomParseException.Message.SQLDATE_LITERAL, value);
            this.cacheJavaObject();
        }

        @Override
        protected Date constructJavaObject() {
            try {
                java.util.Date d = ParserSupport.variantDateParse(this.getValue());
                return new Date(d.getTime());
            }
            catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class MJavaDate
    extends MValue<java.util.Date> {
        public MJavaDate(Token tok, String value) throws CustomParseException {
            super(tok, "date", CustomParseException.Message.DATE_LITERAL, value);
            this.cacheJavaObject();
        }

        @Override
        protected java.util.Date constructJavaObject() {
            try {
                return ParserSupport.variantDateParse(this.getValue());
            }
            catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class MDouble
    extends MValue<Double> {
        public MDouble(Token tok, String value) throws CustomParseException {
            super(tok, "double", CustomParseException.Message.DOUBLE_LITERAL, value);
            this.cacheJavaObject();
        }

        @Override
        protected Double constructJavaObject() {
            return Double.parseDouble(this.getValue());
        }
    }

    public static class MFloat
    extends MValue<Float> {
        public MFloat(Token tok, String value) throws CustomParseException {
            super(tok, "float", CustomParseException.Message.FLOAT_LITERAL, value);
            this.cacheJavaObject();
        }

        @Override
        protected Float constructJavaObject() {
            return Float.valueOf(Float.parseFloat(this.getValue()));
        }
    }

    public static class MLong
    extends MValue<Long> {
        public MLong(Token tok, String value) throws CustomParseException {
            super(tok, "long", CustomParseException.Message.LONG_LITERAL, value);
            this.cacheJavaObject();
        }

        @Override
        protected Long constructJavaObject() {
            return Long.parseLong(this.getValue());
        }
    }

    public static class MInt
    extends MValue<Integer> {
        public MInt(Token tok, String value) throws CustomParseException {
            super(tok, "int", CustomParseException.Message.INT_LITERAL, value);
            this.cacheJavaObject();
        }

        @Override
        protected Integer constructJavaObject() {
            return Integer.parseInt(this.getValue());
        }

        @Override
        public String toString() {
            return this.constructJavaObject().toString();
        }
    }

    public static class MShort
    extends MValue<Short> {
        public MShort(Token tok, String value) throws CustomParseException {
            super(tok, "short", CustomParseException.Message.SHORT_LITERAL, value);
            this.cacheJavaObject();
        }

        @Override
        protected Short constructJavaObject() {
            return (short)Integer.parseInt(this.getValue());
        }
    }

    public static class MBool
    extends MValue<Boolean> {
        public MBool(Token t, String value) throws CustomParseException {
            super(t, "bool", CustomParseException.Message.BOOLEAN_CAST, value);
            this.cacheJavaObject();
        }

        @Override
        protected Boolean constructJavaObject() {
            return Boolean.parseBoolean(this.getValue());
        }
    }

    public static class MByte
    extends MValue<Byte> {
        public MByte(Token tok, String value) throws CustomParseException {
            super(tok, "byte", CustomParseException.Message.BYTE_CAST, value);
            this.cacheJavaObject();
        }

        @Override
        protected Byte constructJavaObject() {
            return (byte)Integer.parseInt(this.getValue());
        }
    }
}


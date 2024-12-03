/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.BinaryType;
import org.hibernate.type.BlobType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.ByteType;
import org.hibernate.type.CalendarDateType;
import org.hibernate.type.CalendarType;
import org.hibernate.type.CharArrayType;
import org.hibernate.type.CharacterArrayType;
import org.hibernate.type.CharacterType;
import org.hibernate.type.ClassType;
import org.hibernate.type.ClobType;
import org.hibernate.type.CurrencyType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.FloatType;
import org.hibernate.type.ImageType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LocaleType;
import org.hibernate.type.LongType;
import org.hibernate.type.MaterializedBlobType;
import org.hibernate.type.MaterializedClobType;
import org.hibernate.type.MaterializedNClobType;
import org.hibernate.type.NClobType;
import org.hibernate.type.NTextType;
import org.hibernate.type.NumericBooleanType;
import org.hibernate.type.RowVersionType;
import org.hibernate.type.SerializableType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringNVarcharType;
import org.hibernate.type.StringType;
import org.hibernate.type.TextType;
import org.hibernate.type.TimeType;
import org.hibernate.type.TimeZoneType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.TrueFalseType;
import org.hibernate.type.UUIDBinaryType;
import org.hibernate.type.UUIDCharType;
import org.hibernate.type.UrlType;
import org.hibernate.type.WrapperBinaryType;
import org.hibernate.type.YesNoType;

public final class StandardBasicTypes {
    public static final BooleanType BOOLEAN = BooleanType.INSTANCE;
    public static final NumericBooleanType NUMERIC_BOOLEAN = NumericBooleanType.INSTANCE;
    public static final TrueFalseType TRUE_FALSE = TrueFalseType.INSTANCE;
    public static final YesNoType YES_NO = YesNoType.INSTANCE;
    public static final ByteType BYTE = ByteType.INSTANCE;
    public static final ShortType SHORT = ShortType.INSTANCE;
    public static final IntegerType INTEGER = IntegerType.INSTANCE;
    public static final LongType LONG = LongType.INSTANCE;
    public static final FloatType FLOAT = FloatType.INSTANCE;
    public static final DoubleType DOUBLE = DoubleType.INSTANCE;
    public static final BigIntegerType BIG_INTEGER = BigIntegerType.INSTANCE;
    public static final BigDecimalType BIG_DECIMAL = BigDecimalType.INSTANCE;
    public static final CharacterType CHARACTER = CharacterType.INSTANCE;
    public static final StringType STRING = StringType.INSTANCE;
    public static final StringNVarcharType NSTRING = StringNVarcharType.INSTANCE;
    public static final UrlType URL = UrlType.INSTANCE;
    public static final TimeType TIME = TimeType.INSTANCE;
    public static final DateType DATE = DateType.INSTANCE;
    public static final TimestampType TIMESTAMP = TimestampType.INSTANCE;
    public static final CalendarType CALENDAR = CalendarType.INSTANCE;
    public static final CalendarDateType CALENDAR_DATE = CalendarDateType.INSTANCE;
    public static final ClassType CLASS = ClassType.INSTANCE;
    public static final LocaleType LOCALE = LocaleType.INSTANCE;
    public static final CurrencyType CURRENCY = CurrencyType.INSTANCE;
    public static final TimeZoneType TIMEZONE = TimeZoneType.INSTANCE;
    public static final UUIDBinaryType UUID_BINARY = UUIDBinaryType.INSTANCE;
    public static final UUIDCharType UUID_CHAR = UUIDCharType.INSTANCE;
    public static final BinaryType BINARY = BinaryType.INSTANCE;
    public static final WrapperBinaryType WRAPPER_BINARY = WrapperBinaryType.INSTANCE;
    public static final RowVersionType ROW_VERSION = RowVersionType.INSTANCE;
    public static final ImageType IMAGE = ImageType.INSTANCE;
    public static final BlobType BLOB = BlobType.INSTANCE;
    public static final MaterializedBlobType MATERIALIZED_BLOB = MaterializedBlobType.INSTANCE;
    public static final CharArrayType CHAR_ARRAY = CharArrayType.INSTANCE;
    public static final CharacterArrayType CHARACTER_ARRAY = CharacterArrayType.INSTANCE;
    public static final TextType TEXT = TextType.INSTANCE;
    public static final NTextType NTEXT = NTextType.INSTANCE;
    public static final ClobType CLOB = ClobType.INSTANCE;
    public static final NClobType NCLOB = NClobType.INSTANCE;
    public static final MaterializedClobType MATERIALIZED_CLOB = MaterializedClobType.INSTANCE;
    public static final MaterializedNClobType MATERIALIZED_NCLOB = MaterializedNClobType.INSTANCE;
    public static final SerializableType SERIALIZABLE = SerializableType.INSTANCE;

    private StandardBasicTypes() {
    }
}


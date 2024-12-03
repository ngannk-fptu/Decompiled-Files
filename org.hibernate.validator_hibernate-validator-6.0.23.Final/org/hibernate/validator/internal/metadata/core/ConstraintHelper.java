/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 *  javax.validation.ConstraintTarget
 *  javax.validation.ConstraintValidator
 *  javax.validation.ValidationException
 *  javax.validation.constraints.AssertFalse
 *  javax.validation.constraints.AssertTrue
 *  javax.validation.constraints.DecimalMax
 *  javax.validation.constraints.DecimalMin
 *  javax.validation.constraints.Digits
 *  javax.validation.constraints.Email
 *  javax.validation.constraints.Future
 *  javax.validation.constraints.FutureOrPresent
 *  javax.validation.constraints.Max
 *  javax.validation.constraints.Min
 *  javax.validation.constraints.Negative
 *  javax.validation.constraints.NegativeOrZero
 *  javax.validation.constraints.NotBlank
 *  javax.validation.constraints.NotEmpty
 *  javax.validation.constraints.NotNull
 *  javax.validation.constraints.Null
 *  javax.validation.constraints.Past
 *  javax.validation.constraints.PastOrPresent
 *  javax.validation.constraints.Pattern
 *  javax.validation.constraints.Positive
 *  javax.validation.constraints.PositiveOrZero
 *  javax.validation.constraints.Size
 *  javax.validation.constraintvalidation.ValidationTarget
 */
package org.hibernate.validator.internal.metadata.core;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Constraint;
import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.ValidationException;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import javax.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.constraints.CodePointLength;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.constraints.Currency;
import org.hibernate.validator.constraints.EAN;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.constraints.Mod10Check;
import org.hibernate.validator.constraints.Mod11Check;
import org.hibernate.validator.constraints.ModCheck;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.ScriptAssert;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UniqueElements;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.constraints.pl.NIP;
import org.hibernate.validator.constraints.pl.PESEL;
import org.hibernate.validator.constraints.pl.REGON;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.hibernate.validator.internal.constraintvalidators.bv.AssertFalseValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.AssertTrueValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.DecimalMaxValidatorForCharSequence;
import org.hibernate.validator.internal.constraintvalidators.bv.DecimalMinValidatorForCharSequence;
import org.hibernate.validator.internal.constraintvalidators.bv.DigitsValidatorForCharSequence;
import org.hibernate.validator.internal.constraintvalidators.bv.DigitsValidatorForNumber;
import org.hibernate.validator.internal.constraintvalidators.bv.MaxValidatorForCharSequence;
import org.hibernate.validator.internal.constraintvalidators.bv.MinValidatorForCharSequence;
import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.NullValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.PatternValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.money.CurrencyValidatorForMonetaryAmount;
import org.hibernate.validator.internal.constraintvalidators.bv.money.DecimalMaxValidatorForMonetaryAmount;
import org.hibernate.validator.internal.constraintvalidators.bv.money.DecimalMinValidatorForMonetaryAmount;
import org.hibernate.validator.internal.constraintvalidators.bv.money.MaxValidatorForMonetaryAmount;
import org.hibernate.validator.internal.constraintvalidators.bv.money.MinValidatorForMonetaryAmount;
import org.hibernate.validator.internal.constraintvalidators.bv.money.NegativeOrZeroValidatorForMonetaryAmount;
import org.hibernate.validator.internal.constraintvalidators.bv.money.NegativeValidatorForMonetaryAmount;
import org.hibernate.validator.internal.constraintvalidators.bv.money.PositiveOrZeroValidatorForMonetaryAmount;
import org.hibernate.validator.internal.constraintvalidators.bv.money.PositiveValidatorForMonetaryAmount;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArray;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfBoolean;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfByte;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfChar;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfInt;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfLong;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfShort;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForCharSequence;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForCollection;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForMap;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MaxValidatorForBigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MaxValidatorForBigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MaxValidatorForDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MaxValidatorForFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MaxValidatorForLong;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MaxValidatorForNumber;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MinValidatorForBigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MinValidatorForBigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MinValidatorForDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MinValidatorForFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MinValidatorForLong;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MinValidatorForNumber;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMaxValidatorForBigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMaxValidatorForBigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMaxValidatorForDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMaxValidatorForFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMaxValidatorForLong;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMaxValidatorForNumber;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMinValidatorForBigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMinValidatorForBigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMinValidatorForDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMinValidatorForFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMinValidatorForLong;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMinValidatorForNumber;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeOrZeroValidatorForBigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeOrZeroValidatorForBigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeOrZeroValidatorForByte;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeOrZeroValidatorForDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeOrZeroValidatorForFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeOrZeroValidatorForInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeOrZeroValidatorForLong;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeOrZeroValidatorForNumber;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeOrZeroValidatorForShort;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeValidatorForBigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeValidatorForBigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeValidatorForByte;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeValidatorForDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeValidatorForFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeValidatorForInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeValidatorForLong;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeValidatorForNumber;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.NegativeValidatorForShort;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveOrZeroValidatorForBigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveOrZeroValidatorForBigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveOrZeroValidatorForByte;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveOrZeroValidatorForDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveOrZeroValidatorForFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveOrZeroValidatorForInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveOrZeroValidatorForLong;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveOrZeroValidatorForNumber;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveOrZeroValidatorForShort;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForBigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForBigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForByte;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForLong;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForNumber;
import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForShort;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArray;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArraysOfBoolean;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArraysOfByte;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArraysOfChar;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArraysOfDouble;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArraysOfFloat;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArraysOfInt;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArraysOfLong;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArraysOfShort;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForCharSequence;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForCollection;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForMap;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForCalendar;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForHijrahDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForInstant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForJapaneseDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForLocalDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForLocalDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForLocalTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForMinguoDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForMonthDay;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForOffsetDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForOffsetTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForReadableInstant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForReadablePartial;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForThaiBuddhistDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForYear;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForYearMonth;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForZonedDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForCalendar;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForHijrahDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForInstant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForJapaneseDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForLocalDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForLocalDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForLocalTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForMinguoDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForMonthDay;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForOffsetDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForOffsetTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForReadableInstant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForReadablePartial;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForThaiBuddhistDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForYear;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForYearMonth;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForZonedDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForCalendar;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForHijrahDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForInstant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForJapaneseDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForLocalDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForLocalDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForLocalTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForMinguoDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForMonthDay;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForOffsetDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForOffsetTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForReadableInstant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForReadablePartial;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForThaiBuddhistDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForYear;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForYearMonth;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForZonedDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForCalendar;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForHijrahDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForInstant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForJapaneseDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForLocalDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForLocalDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForLocalTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForMinguoDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForMonthDay;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForOffsetDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForOffsetTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForReadableInstant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForReadablePartial;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForThaiBuddhistDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForYear;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForYearMonth;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForZonedDateTime;
import org.hibernate.validator.internal.constraintvalidators.hv.CodePointLengthValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.EANValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.ISBNValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.LengthValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.LuhnCheckValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.Mod10CheckValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.Mod11CheckValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.ModCheckValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.ParameterScriptAssertValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.SafeHtmlValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.ScriptAssertValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.UniqueElementsValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CNPJValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.pl.NIPValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.pl.PESELValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.pl.REGONValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.time.DurationMaxValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.time.DurationMinValidator;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttribute;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethods;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;

public class ConstraintHelper {
    public static final String GROUPS = "groups";
    public static final String PAYLOAD = "payload";
    public static final String MESSAGE = "message";
    public static final String VALIDATION_APPLIES_TO = "validationAppliesTo";
    private static final List<String> SUPPORTED_VALID_METHODS = Collections.singletonList("validationAppliesTo");
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String JODA_TIME_CLASS_NAME = "org.joda.time.ReadableInstant";
    private static final String JAVA_MONEY_CLASS_NAME = "javax.money.MonetaryAmount";
    private final Map<Class<? extends Annotation>, List<? extends ConstraintValidatorDescriptor<?>>> builtinConstraints;
    private final ConcurrentMap<Class<? extends Annotation>, Boolean> externalConstraints = new ConcurrentHashMap<Class<? extends Annotation>, Boolean>();
    private final ConcurrentMap<Class<? extends Annotation>, Boolean> multiValueConstraints = new ConcurrentHashMap<Class<? extends Annotation>, Boolean>();
    private final ValidatorDescriptorMap validatorDescriptors = new ValidatorDescriptorMap();

    public ConstraintHelper() {
        HashMap tmpConstraints = new HashMap();
        ConstraintHelper.putConstraint(tmpConstraints, AssertFalse.class, AssertFalseValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, AssertTrue.class, AssertTrueValidator.class);
        if (ConstraintHelper.isJavaMoneyInClasspath()) {
            ConstraintHelper.putConstraints(tmpConstraints, DecimalMax.class, Arrays.asList(DecimalMaxValidatorForBigDecimal.class, DecimalMaxValidatorForBigInteger.class, DecimalMaxValidatorForDouble.class, DecimalMaxValidatorForFloat.class, DecimalMaxValidatorForLong.class, DecimalMaxValidatorForNumber.class, DecimalMaxValidatorForCharSequence.class, DecimalMaxValidatorForMonetaryAmount.class));
            ConstraintHelper.putConstraints(tmpConstraints, DecimalMin.class, Arrays.asList(DecimalMinValidatorForBigDecimal.class, DecimalMinValidatorForBigInteger.class, DecimalMinValidatorForDouble.class, DecimalMinValidatorForFloat.class, DecimalMinValidatorForLong.class, DecimalMinValidatorForNumber.class, DecimalMinValidatorForCharSequence.class, DecimalMinValidatorForMonetaryAmount.class));
        } else {
            ConstraintHelper.putConstraints(tmpConstraints, DecimalMax.class, Arrays.asList(DecimalMaxValidatorForBigDecimal.class, DecimalMaxValidatorForBigInteger.class, DecimalMaxValidatorForDouble.class, DecimalMaxValidatorForFloat.class, DecimalMaxValidatorForLong.class, DecimalMaxValidatorForNumber.class, DecimalMaxValidatorForCharSequence.class));
            ConstraintHelper.putConstraints(tmpConstraints, DecimalMin.class, Arrays.asList(DecimalMinValidatorForBigDecimal.class, DecimalMinValidatorForBigInteger.class, DecimalMinValidatorForDouble.class, DecimalMinValidatorForFloat.class, DecimalMinValidatorForLong.class, DecimalMinValidatorForNumber.class, DecimalMinValidatorForCharSequence.class));
        }
        ConstraintHelper.putConstraints(tmpConstraints, Digits.class, DigitsValidatorForCharSequence.class, DigitsValidatorForNumber.class);
        ConstraintHelper.putConstraint(tmpConstraints, javax.validation.constraints.Email.class, org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator.class);
        ArrayList futureValidators = new ArrayList(18);
        futureValidators.add(FutureValidatorForCalendar.class);
        futureValidators.add(FutureValidatorForDate.class);
        if (ConstraintHelper.isJodaTimeInClasspath()) {
            futureValidators.add(FutureValidatorForReadableInstant.class);
            futureValidators.add(FutureValidatorForReadablePartial.class);
        }
        futureValidators.add(FutureValidatorForHijrahDate.class);
        futureValidators.add(FutureValidatorForInstant.class);
        futureValidators.add(FutureValidatorForJapaneseDate.class);
        futureValidators.add(FutureValidatorForLocalDate.class);
        futureValidators.add(FutureValidatorForLocalDateTime.class);
        futureValidators.add(FutureValidatorForLocalTime.class);
        futureValidators.add(FutureValidatorForMinguoDate.class);
        futureValidators.add(FutureValidatorForMonthDay.class);
        futureValidators.add(FutureValidatorForOffsetDateTime.class);
        futureValidators.add(FutureValidatorForOffsetTime.class);
        futureValidators.add(FutureValidatorForThaiBuddhistDate.class);
        futureValidators.add(FutureValidatorForYear.class);
        futureValidators.add(FutureValidatorForYearMonth.class);
        futureValidators.add(FutureValidatorForZonedDateTime.class);
        ConstraintHelper.putConstraints(tmpConstraints, Future.class, futureValidators);
        ArrayList futureOrPresentValidators = new ArrayList(18);
        futureOrPresentValidators.add(FutureOrPresentValidatorForCalendar.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForDate.class);
        if (ConstraintHelper.isJodaTimeInClasspath()) {
            futureOrPresentValidators.add(FutureOrPresentValidatorForReadableInstant.class);
            futureOrPresentValidators.add(FutureOrPresentValidatorForReadablePartial.class);
        }
        futureOrPresentValidators.add(FutureOrPresentValidatorForHijrahDate.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForInstant.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForJapaneseDate.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForLocalDate.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForLocalDateTime.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForLocalTime.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForMinguoDate.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForMonthDay.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForOffsetDateTime.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForOffsetTime.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForThaiBuddhistDate.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForYear.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForYearMonth.class);
        futureOrPresentValidators.add(FutureOrPresentValidatorForZonedDateTime.class);
        ConstraintHelper.putConstraints(tmpConstraints, FutureOrPresent.class, futureOrPresentValidators);
        ConstraintHelper.putConstraint(tmpConstraints, ISBN.class, ISBNValidator.class);
        if (ConstraintHelper.isJavaMoneyInClasspath()) {
            ConstraintHelper.putConstraints(tmpConstraints, Max.class, Arrays.asList(MaxValidatorForBigDecimal.class, MaxValidatorForBigInteger.class, MaxValidatorForDouble.class, MaxValidatorForFloat.class, MaxValidatorForLong.class, MaxValidatorForNumber.class, MaxValidatorForCharSequence.class, MaxValidatorForMonetaryAmount.class));
            ConstraintHelper.putConstraints(tmpConstraints, Min.class, Arrays.asList(MinValidatorForBigDecimal.class, MinValidatorForBigInteger.class, MinValidatorForDouble.class, MinValidatorForFloat.class, MinValidatorForLong.class, MinValidatorForNumber.class, MinValidatorForCharSequence.class, MinValidatorForMonetaryAmount.class));
        } else {
            ConstraintHelper.putConstraints(tmpConstraints, Max.class, Arrays.asList(MaxValidatorForBigDecimal.class, MaxValidatorForBigInteger.class, MaxValidatorForDouble.class, MaxValidatorForFloat.class, MaxValidatorForLong.class, MaxValidatorForNumber.class, MaxValidatorForCharSequence.class));
            ConstraintHelper.putConstraints(tmpConstraints, Min.class, Arrays.asList(MinValidatorForBigDecimal.class, MinValidatorForBigInteger.class, MinValidatorForDouble.class, MinValidatorForFloat.class, MinValidatorForLong.class, MinValidatorForNumber.class, MinValidatorForCharSequence.class));
        }
        if (ConstraintHelper.isJavaMoneyInClasspath()) {
            ConstraintHelper.putConstraints(tmpConstraints, Negative.class, Arrays.asList(NegativeValidatorForBigDecimal.class, NegativeValidatorForBigInteger.class, NegativeValidatorForDouble.class, NegativeValidatorForFloat.class, NegativeValidatorForLong.class, NegativeValidatorForInteger.class, NegativeValidatorForShort.class, NegativeValidatorForByte.class, NegativeValidatorForNumber.class, NegativeValidatorForMonetaryAmount.class));
            ConstraintHelper.putConstraints(tmpConstraints, NegativeOrZero.class, Arrays.asList(NegativeOrZeroValidatorForBigDecimal.class, NegativeOrZeroValidatorForBigInteger.class, NegativeOrZeroValidatorForDouble.class, NegativeOrZeroValidatorForFloat.class, NegativeOrZeroValidatorForLong.class, NegativeOrZeroValidatorForInteger.class, NegativeOrZeroValidatorForShort.class, NegativeOrZeroValidatorForByte.class, NegativeOrZeroValidatorForNumber.class, NegativeOrZeroValidatorForMonetaryAmount.class));
        } else {
            ConstraintHelper.putConstraints(tmpConstraints, Negative.class, Arrays.asList(NegativeValidatorForBigDecimal.class, NegativeValidatorForBigInteger.class, NegativeValidatorForDouble.class, NegativeValidatorForFloat.class, NegativeValidatorForLong.class, NegativeValidatorForInteger.class, NegativeValidatorForShort.class, NegativeValidatorForByte.class, NegativeValidatorForNumber.class));
            ConstraintHelper.putConstraints(tmpConstraints, NegativeOrZero.class, Arrays.asList(NegativeOrZeroValidatorForBigDecimal.class, NegativeOrZeroValidatorForBigInteger.class, NegativeOrZeroValidatorForDouble.class, NegativeOrZeroValidatorForFloat.class, NegativeOrZeroValidatorForLong.class, NegativeOrZeroValidatorForInteger.class, NegativeOrZeroValidatorForShort.class, NegativeOrZeroValidatorForByte.class, NegativeOrZeroValidatorForNumber.class));
        }
        ConstraintHelper.putConstraint(tmpConstraints, javax.validation.constraints.NotBlank.class, NotBlankValidator.class);
        ArrayList notEmptyValidators = new ArrayList(11);
        notEmptyValidators.add(NotEmptyValidatorForCharSequence.class);
        notEmptyValidators.add(NotEmptyValidatorForCollection.class);
        notEmptyValidators.add(NotEmptyValidatorForArray.class);
        notEmptyValidators.add(NotEmptyValidatorForMap.class);
        notEmptyValidators.add(NotEmptyValidatorForArraysOfBoolean.class);
        notEmptyValidators.add(NotEmptyValidatorForArraysOfByte.class);
        notEmptyValidators.add(NotEmptyValidatorForArraysOfChar.class);
        notEmptyValidators.add(NotEmptyValidatorForArraysOfDouble.class);
        notEmptyValidators.add(NotEmptyValidatorForArraysOfFloat.class);
        notEmptyValidators.add(NotEmptyValidatorForArraysOfInt.class);
        notEmptyValidators.add(NotEmptyValidatorForArraysOfLong.class);
        notEmptyValidators.add(NotEmptyValidatorForArraysOfShort.class);
        ConstraintHelper.putConstraints(tmpConstraints, NotEmpty.class, notEmptyValidators);
        ConstraintHelper.putConstraint(tmpConstraints, NotNull.class, NotNullValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, Null.class, NullValidator.class);
        ArrayList pastValidators = new ArrayList(18);
        pastValidators.add(PastValidatorForCalendar.class);
        pastValidators.add(PastValidatorForDate.class);
        if (ConstraintHelper.isJodaTimeInClasspath()) {
            pastValidators.add(PastValidatorForReadableInstant.class);
            pastValidators.add(PastValidatorForReadablePartial.class);
        }
        pastValidators.add(PastValidatorForHijrahDate.class);
        pastValidators.add(PastValidatorForInstant.class);
        pastValidators.add(PastValidatorForJapaneseDate.class);
        pastValidators.add(PastValidatorForLocalDate.class);
        pastValidators.add(PastValidatorForLocalDateTime.class);
        pastValidators.add(PastValidatorForLocalTime.class);
        pastValidators.add(PastValidatorForMinguoDate.class);
        pastValidators.add(PastValidatorForMonthDay.class);
        pastValidators.add(PastValidatorForOffsetDateTime.class);
        pastValidators.add(PastValidatorForOffsetTime.class);
        pastValidators.add(PastValidatorForThaiBuddhistDate.class);
        pastValidators.add(PastValidatorForYear.class);
        pastValidators.add(PastValidatorForYearMonth.class);
        pastValidators.add(PastValidatorForZonedDateTime.class);
        ConstraintHelper.putConstraints(tmpConstraints, Past.class, pastValidators);
        ArrayList pastOrPresentValidators = new ArrayList(18);
        pastOrPresentValidators.add(PastOrPresentValidatorForCalendar.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForDate.class);
        if (ConstraintHelper.isJodaTimeInClasspath()) {
            pastOrPresentValidators.add(PastOrPresentValidatorForReadableInstant.class);
            pastOrPresentValidators.add(PastOrPresentValidatorForReadablePartial.class);
        }
        pastOrPresentValidators.add(PastOrPresentValidatorForHijrahDate.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForInstant.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForJapaneseDate.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForLocalDate.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForLocalDateTime.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForLocalTime.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForMinguoDate.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForMonthDay.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForOffsetDateTime.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForOffsetTime.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForThaiBuddhistDate.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForYear.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForYearMonth.class);
        pastOrPresentValidators.add(PastOrPresentValidatorForZonedDateTime.class);
        ConstraintHelper.putConstraints(tmpConstraints, PastOrPresent.class, pastOrPresentValidators);
        ConstraintHelper.putConstraint(tmpConstraints, Pattern.class, PatternValidator.class);
        if (ConstraintHelper.isJavaMoneyInClasspath()) {
            ConstraintHelper.putConstraints(tmpConstraints, Positive.class, Arrays.asList(PositiveValidatorForBigDecimal.class, PositiveValidatorForBigInteger.class, PositiveValidatorForDouble.class, PositiveValidatorForFloat.class, PositiveValidatorForLong.class, PositiveValidatorForInteger.class, PositiveValidatorForShort.class, PositiveValidatorForByte.class, PositiveValidatorForNumber.class, PositiveValidatorForMonetaryAmount.class));
            ConstraintHelper.putConstraints(tmpConstraints, PositiveOrZero.class, Arrays.asList(PositiveOrZeroValidatorForBigDecimal.class, PositiveOrZeroValidatorForBigInteger.class, PositiveOrZeroValidatorForDouble.class, PositiveOrZeroValidatorForFloat.class, PositiveOrZeroValidatorForLong.class, PositiveOrZeroValidatorForInteger.class, PositiveOrZeroValidatorForShort.class, PositiveOrZeroValidatorForByte.class, PositiveOrZeroValidatorForNumber.class, PositiveOrZeroValidatorForMonetaryAmount.class));
        } else {
            ConstraintHelper.putConstraints(tmpConstraints, Positive.class, Arrays.asList(PositiveValidatorForBigDecimal.class, PositiveValidatorForBigInteger.class, PositiveValidatorForDouble.class, PositiveValidatorForFloat.class, PositiveValidatorForLong.class, PositiveValidatorForInteger.class, PositiveValidatorForShort.class, PositiveValidatorForByte.class, PositiveValidatorForNumber.class));
            ConstraintHelper.putConstraints(tmpConstraints, PositiveOrZero.class, Arrays.asList(PositiveOrZeroValidatorForBigDecimal.class, PositiveOrZeroValidatorForBigInteger.class, PositiveOrZeroValidatorForDouble.class, PositiveOrZeroValidatorForFloat.class, PositiveOrZeroValidatorForLong.class, PositiveOrZeroValidatorForInteger.class, PositiveOrZeroValidatorForShort.class, PositiveOrZeroValidatorForByte.class, PositiveOrZeroValidatorForNumber.class));
        }
        ArrayList sizeValidators = new ArrayList(11);
        sizeValidators.add(SizeValidatorForCharSequence.class);
        sizeValidators.add(SizeValidatorForCollection.class);
        sizeValidators.add(SizeValidatorForArray.class);
        sizeValidators.add(SizeValidatorForMap.class);
        sizeValidators.add(SizeValidatorForArraysOfBoolean.class);
        sizeValidators.add(SizeValidatorForArraysOfByte.class);
        sizeValidators.add(SizeValidatorForArraysOfChar.class);
        sizeValidators.add(SizeValidatorForArraysOfDouble.class);
        sizeValidators.add(SizeValidatorForArraysOfFloat.class);
        sizeValidators.add(SizeValidatorForArraysOfInt.class);
        sizeValidators.add(SizeValidatorForArraysOfLong.class);
        sizeValidators.add(SizeValidatorForArraysOfShort.class);
        ConstraintHelper.putConstraints(tmpConstraints, Size.class, sizeValidators);
        ConstraintHelper.putConstraint(tmpConstraints, CNPJ.class, CNPJValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, CPF.class, CPFValidator.class);
        if (ConstraintHelper.isJavaMoneyInClasspath()) {
            ConstraintHelper.putConstraint(tmpConstraints, Currency.class, CurrencyValidatorForMonetaryAmount.class);
        }
        ConstraintHelper.putConstraint(tmpConstraints, DurationMax.class, DurationMaxValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, DurationMin.class, DurationMinValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, EAN.class, EANValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, Email.class, EmailValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, Length.class, LengthValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, CodePointLength.class, CodePointLengthValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, ModCheck.class, ModCheckValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, LuhnCheck.class, LuhnCheckValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, Mod10Check.class, Mod10CheckValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, Mod11Check.class, Mod11CheckValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, REGON.class, REGONValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, NIP.class, NIPValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, PESEL.class, PESELValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, NotBlank.class, org.hibernate.validator.internal.constraintvalidators.hv.NotBlankValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, ParameterScriptAssert.class, ParameterScriptAssertValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, SafeHtml.class, SafeHtmlValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, ScriptAssert.class, ScriptAssertValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, UniqueElements.class, UniqueElementsValidator.class);
        ConstraintHelper.putConstraint(tmpConstraints, URL.class, URLValidator.class);
        this.builtinConstraints = Collections.unmodifiableMap(tmpConstraints);
    }

    private static <A extends Annotation> void putConstraint(Map<Class<? extends Annotation>, List<ConstraintValidatorDescriptor<?>>> validators, Class<A> constraintType, Class<? extends ConstraintValidator<A, ?>> validatorType) {
        validators.put(constraintType, Collections.singletonList(ConstraintValidatorDescriptor.forClass(validatorType, constraintType)));
    }

    private static <A extends Annotation> void putConstraints(Map<Class<? extends Annotation>, List<ConstraintValidatorDescriptor<?>>> validators, Class<A> constraintType, Class<? extends ConstraintValidator<A, ?>> validatorType1, Class<? extends ConstraintValidator<A, ?>> validatorType2) {
        List descriptors = Stream.of(validatorType1, validatorType2).map(vt -> ConstraintValidatorDescriptor.forClass(vt, constraintType)).collect(Collectors.toList());
        validators.put(constraintType, CollectionHelper.toImmutableList(descriptors));
    }

    private static <A extends Annotation> void putConstraints(Map<Class<? extends Annotation>, List<ConstraintValidatorDescriptor<?>>> validators, Class<A> constraintType, List<Class<? extends ConstraintValidator<A, ?>>> validatorDescriptors) {
        List descriptors = validatorDescriptors.stream().map(vt -> ConstraintValidatorDescriptor.forClass(vt, constraintType)).collect(Collectors.toList());
        validators.put(constraintType, CollectionHelper.toImmutableList(descriptors));
    }

    private boolean isBuiltinConstraint(Class<? extends Annotation> annotationType) {
        return this.builtinConstraints.containsKey(annotationType);
    }

    public <A extends Annotation> List<ConstraintValidatorDescriptor<A>> getAllValidatorDescriptors(Class<A> annotationType) {
        Contracts.assertNotNull(annotationType, Messages.MESSAGES.classCannotBeNull());
        return this.validatorDescriptors.computeIfAbsent(annotationType, a -> this.getDefaultValidatorDescriptors((Class)a));
    }

    public <A extends Annotation> List<ConstraintValidatorDescriptor<A>> findValidatorDescriptors(Class<A> annotationType, ValidationTarget validationTarget) {
        return this.getAllValidatorDescriptors(annotationType).stream().filter(d -> this.supportsValidationTarget((ConstraintValidatorDescriptor<?>)d, validationTarget)).collect(Collectors.toList());
    }

    private boolean supportsValidationTarget(ConstraintValidatorDescriptor<?> validatorDescriptor, ValidationTarget target) {
        return validatorDescriptor.getValidationTargets().contains(target);
    }

    public <A extends Annotation> void putValidatorDescriptors(Class<A> annotationType, List<ConstraintValidatorDescriptor<A>> validatorDescriptors, boolean keepExistingClasses) {
        List<ConstraintValidatorDescriptor<A>> existingvalidatorDescriptors;
        ArrayList<ConstraintValidatorDescriptor<A>> validatorDescriptorsToAdd = new ArrayList<ConstraintValidatorDescriptor<A>>();
        if (keepExistingClasses && (existingvalidatorDescriptors = this.getAllValidatorDescriptors(annotationType)) != null) {
            validatorDescriptorsToAdd.addAll(0, existingvalidatorDescriptors);
        }
        validatorDescriptorsToAdd.addAll(validatorDescriptors);
        this.validatorDescriptors.put(annotationType, CollectionHelper.toImmutableList(validatorDescriptorsToAdd));
    }

    public boolean isMultiValueConstraint(Class<? extends Annotation> annotationType) {
        if (this.isJdkAnnotation(annotationType)) {
            return false;
        }
        return this.multiValueConstraints.computeIfAbsent(annotationType, a -> {
            Class<?> returnType;
            boolean isMultiValueConstraint = false;
            Method method = ConstraintHelper.run(GetMethod.action(a, "value"));
            if (method != null && (returnType = method.getReturnType()).isArray() && returnType.getComponentType().isAnnotation()) {
                Class<?> componentType = returnType.getComponentType();
                isMultiValueConstraint = this.isConstraintAnnotation(componentType) ? Boolean.TRUE.booleanValue() : Boolean.FALSE.booleanValue();
            }
            return isMultiValueConstraint;
        });
    }

    public <A extends Annotation> List<Annotation> getConstraintsFromMultiValueConstraint(A multiValueConstraint) {
        Annotation[] annotations = ConstraintHelper.run(GetAnnotationAttribute.action(multiValueConstraint, "value", Annotation[].class));
        return Arrays.asList(annotations);
    }

    public boolean isConstraintAnnotation(Class<? extends Annotation> annotationType) {
        if (this.isBuiltinConstraint(annotationType)) {
            return true;
        }
        if (annotationType.getAnnotation(Constraint.class) == null) {
            return false;
        }
        return this.externalConstraints.computeIfAbsent(annotationType, a -> {
            this.assertMessageParameterExists((Class<? extends Annotation>)a);
            this.assertGroupsParameterExists((Class<? extends Annotation>)a);
            this.assertPayloadParameterExists((Class<? extends Annotation>)a);
            this.assertValidationAppliesToParameterSetUpCorrectly((Class<? extends Annotation>)a);
            this.assertNoParameterStartsWithValid((Class<? extends Annotation>)a);
            return Boolean.TRUE;
        });
    }

    private void assertNoParameterStartsWithValid(Class<? extends Annotation> annotationType) {
        Method[] methods;
        for (Method m : methods = ConstraintHelper.run(GetDeclaredMethods.action(annotationType))) {
            if (!m.getName().startsWith("valid") || SUPPORTED_VALID_METHODS.contains(m.getName())) continue;
            throw LOG.getConstraintParametersCannotStartWithValidException();
        }
    }

    private void assertPayloadParameterExists(Class<? extends Annotation> annotationType) {
        try {
            Method method = ConstraintHelper.run(GetMethod.action(annotationType, PAYLOAD));
            if (method == null) {
                throw LOG.getConstraintWithoutMandatoryParameterException(PAYLOAD, annotationType.getName());
            }
            Class[] defaultPayload = (Class[])method.getDefaultValue();
            if (defaultPayload == null || defaultPayload.length != 0) {
                throw LOG.getWrongDefaultValueForPayloadParameterException(annotationType.getName());
            }
        }
        catch (ClassCastException e) {
            throw LOG.getWrongTypeForPayloadParameterException(annotationType.getName(), e);
        }
    }

    private void assertGroupsParameterExists(Class<? extends Annotation> annotationType) {
        try {
            Method method = ConstraintHelper.run(GetMethod.action(annotationType, GROUPS));
            if (method == null) {
                throw LOG.getConstraintWithoutMandatoryParameterException(GROUPS, annotationType.getName());
            }
            Class[] defaultGroups = (Class[])method.getDefaultValue();
            if (defaultGroups == null || defaultGroups.length != 0) {
                throw LOG.getWrongDefaultValueForGroupsParameterException(annotationType.getName());
            }
        }
        catch (ClassCastException e) {
            throw LOG.getWrongTypeForGroupsParameterException(annotationType.getName(), e);
        }
    }

    private void assertMessageParameterExists(Class<? extends Annotation> annotationType) {
        Method method = ConstraintHelper.run(GetMethod.action(annotationType, MESSAGE));
        if (method == null) {
            throw LOG.getConstraintWithoutMandatoryParameterException(MESSAGE, annotationType.getName());
        }
        if (method.getReturnType() != String.class) {
            throw LOG.getWrongTypeForMessageParameterException(annotationType.getName());
        }
    }

    private void assertValidationAppliesToParameterSetUpCorrectly(Class<? extends Annotation> annotationType) {
        boolean hasGenericValidators = !this.findValidatorDescriptors(annotationType, ValidationTarget.ANNOTATED_ELEMENT).isEmpty();
        boolean hasCrossParameterValidator = !this.findValidatorDescriptors(annotationType, ValidationTarget.PARAMETERS).isEmpty();
        Method method = ConstraintHelper.run(GetMethod.action(annotationType, VALIDATION_APPLIES_TO));
        if (hasGenericValidators && hasCrossParameterValidator) {
            if (method == null) {
                throw LOG.getGenericAndCrossParameterConstraintDoesNotDefineValidationAppliesToParameterException(annotationType);
            }
            if (method.getReturnType() != ConstraintTarget.class) {
                throw LOG.getValidationAppliesToParameterMustHaveReturnTypeConstraintTargetException(annotationType);
            }
            ConstraintTarget defaultValue = (ConstraintTarget)method.getDefaultValue();
            if (defaultValue != ConstraintTarget.IMPLICIT) {
                throw LOG.getValidationAppliesToParameterMustHaveDefaultValueImplicitException(annotationType);
            }
        } else if (method != null) {
            throw LOG.getValidationAppliesToParameterMustNotBeDefinedForNonGenericAndCrossParameterConstraintException(annotationType);
        }
    }

    public boolean isConstraintComposition(Class<? extends Annotation> annotationType) {
        return annotationType == ConstraintComposition.class;
    }

    public boolean isJdkAnnotation(Class<? extends Annotation> annotation) {
        Package pakkage = annotation.getPackage();
        return pakkage != null && pakkage.getName() != null && (pakkage.getName().startsWith("java.") || pakkage.getName().startsWith("jdk.internal"));
    }

    public void clear() {
        this.externalConstraints.clear();
        this.multiValueConstraints.clear();
    }

    private static boolean isJodaTimeInClasspath() {
        return ConstraintHelper.isClassPresent(JODA_TIME_CLASS_NAME);
    }

    private static boolean isJavaMoneyInClasspath() {
        return ConstraintHelper.isClassPresent(JAVA_MONEY_CLASS_NAME);
    }

    private <A extends Annotation> List<ConstraintValidatorDescriptor<A>> getDefaultValidatorDescriptors(Class<A> annotationType) {
        List<ConstraintValidatorDescriptor<A>> builtInValidators = this.builtinConstraints.get(annotationType);
        if (builtInValidators != null) {
            return builtInValidators;
        }
        Class[] validatedBy = annotationType.getAnnotation(Constraint.class).validatedBy();
        return Stream.of(validatedBy).map(c -> ConstraintValidatorDescriptor.forClass(c, annotationType)).collect(Collectors.collectingAndThen(Collectors.toList(), CollectionHelper::toImmutableList));
    }

    private static boolean isClassPresent(String className) {
        try {
            ConstraintHelper.run(LoadClass.action(className, ConstraintHelper.class.getClassLoader(), false));
            return true;
        }
        catch (ValidationException e) {
            return false;
        }
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    private static class ValidatorDescriptorMap {
        private final ConcurrentMap<Class<? extends Annotation>, List<? extends ConstraintValidatorDescriptor<?>>> constraintValidatorDescriptors = new ConcurrentHashMap();

        private ValidatorDescriptorMap() {
        }

        private <A extends Annotation> void put(Class<A> annotationType, List<ConstraintValidatorDescriptor<A>> validatorDescriptors) {
            this.constraintValidatorDescriptors.put(annotationType, validatorDescriptors);
        }

        private <A extends Annotation> List<ConstraintValidatorDescriptor<A>> computeIfAbsent(Class<A> annotationType, Function<? super Class<A>, List<ConstraintValidatorDescriptor<A>>> mappingFunction) {
            return this.constraintValidatorDescriptors.computeIfAbsent(annotationType, mappingFunction);
        }
    }
}


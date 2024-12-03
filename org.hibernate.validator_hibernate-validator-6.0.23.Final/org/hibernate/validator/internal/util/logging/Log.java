/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ClockProvider
 *  javax.validation.ConstraintDeclarationException
 *  javax.validation.ConstraintDefinitionException
 *  javax.validation.ConstraintTarget
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorFactory
 *  javax.validation.ElementKind
 *  javax.validation.GroupDefinitionException
 *  javax.validation.MessageInterpolator
 *  javax.validation.ParameterNameProvider
 *  javax.validation.Path
 *  javax.validation.TraversableResolver
 *  javax.validation.UnexpectedTypeException
 *  javax.validation.ValidationException
 *  javax.validation.spi.ValidationProvider
 *  javax.validation.valueextraction.ValueExtractor
 *  javax.validation.valueextraction.ValueExtractorDeclarationException
 *  javax.validation.valueextraction.ValueExtractorDefinitionException
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.Cause
 *  org.jboss.logging.annotations.FormatWith
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 */
package org.hibernate.validator.internal.util.logging;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;
import javax.validation.ClockProvider;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ElementKind;
import javax.validation.GroupDefinitionException;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.UnexpectedTypeException;
import javax.validation.ValidationException;
import javax.validation.spi.ValidationProvider;
import javax.validation.valueextraction.ValueExtractor;
import javax.validation.valueextraction.ValueExtractorDeclarationException;
import javax.validation.valueextraction.ValueExtractorDefinitionException;
import javax.xml.stream.XMLStreamException;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageDescriptorFormatException;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.logging.formatter.ArrayOfClassesObjectFormatter;
import org.hibernate.validator.internal.util.logging.formatter.ClassObjectFormatter;
import org.hibernate.validator.internal.util.logging.formatter.CollectionOfClassesObjectFormatter;
import org.hibernate.validator.internal.util.logging.formatter.CollectionOfObjectsToStringFormatter;
import org.hibernate.validator.internal.util.logging.formatter.DurationFormatter;
import org.hibernate.validator.internal.util.logging.formatter.ExecutableFormatter;
import org.hibernate.validator.internal.util.logging.formatter.ObjectArrayFormatter;
import org.hibernate.validator.internal.util.logging.formatter.TypeFormatter;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypePath;
import org.hibernate.validator.spi.scripting.ScriptEvaluationException;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorNotFoundException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.FormatWith;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode="HV")
public interface Log
extends BasicLogger {
    @LogMessage(level=Logger.Level.INFO)
    @Message(id=1, value="Hibernate Validator %s")
    public void version(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=2, value="Ignoring XML configuration.")
    public void ignoringXmlConfiguration();

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=3, value="Using %s as constraint validator factory.")
    public void usingConstraintValidatorFactory(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ConstraintValidatorFactory> var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=4, value="Using %s as message interpolator.")
    public void usingMessageInterpolator(@FormatWith(value=ClassObjectFormatter.class) Class<? extends MessageInterpolator> var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=5, value="Using %s as traversable resolver.")
    public void usingTraversableResolver(@FormatWith(value=ClassObjectFormatter.class) Class<? extends TraversableResolver> var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=6, value="Using %s as validation provider.")
    public void usingValidationProvider(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ValidationProvider<?>> var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=7, value="%s found. Parsing XML based configuration.")
    public void parsingXMLFile(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=8, value="Unable to close input stream.")
    public void unableToCloseInputStream();

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=10, value="Unable to close input stream for %s.")
    public void unableToCloseXMLFileInputStream(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=11, value="Unable to create schema for %1$s: %2$s")
    public void unableToCreateSchema(String var1, String var2);

    @Message(id=12, value="Unable to create annotation for configured constraint")
    public ValidationException getUnableToCreateAnnotationForConfiguredConstraintException(@Cause RuntimeException var1);

    @Message(id=13, value="The class %1$s does not have a property '%2$s' with access %3$s.")
    public ValidationException getUnableToFindPropertyWithAccessException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2, ElementType var3);

    @Message(id=16, value="%s does not represent a valid BigDecimal format.")
    public IllegalArgumentException getInvalidBigDecimalFormatException(String var1, @Cause NumberFormatException var2);

    @Message(id=17, value="The length of the integer part cannot be negative.")
    public IllegalArgumentException getInvalidLengthForIntegerPartException();

    @Message(id=18, value="The length of the fraction part cannot be negative.")
    public IllegalArgumentException getInvalidLengthForFractionPartException();

    @Message(id=19, value="The min parameter cannot be negative.")
    public IllegalArgumentException getMinCannotBeNegativeException();

    @Message(id=20, value="The max parameter cannot be negative.")
    public IllegalArgumentException getMaxCannotBeNegativeException();

    @Message(id=21, value="The length cannot be negative.")
    public IllegalArgumentException getLengthCannotBeNegativeException();

    @Message(id=22, value="Invalid regular expression.")
    public IllegalArgumentException getInvalidRegularExpressionException(@Cause PatternSyntaxException var1);

    @Message(id=23, value="Error during execution of script \"%s\" occurred.")
    public ConstraintDeclarationException getErrorDuringScriptExecutionException(String var1, @Cause Exception var2);

    @Message(id=24, value="Script \"%s\" returned null, but must return either true or false.")
    public ConstraintDeclarationException getScriptMustReturnTrueOrFalseException(String var1);

    @Message(id=25, value="Script \"%1$s\" returned %2$s (of type %3$s), but must return either true or false.")
    public ConstraintDeclarationException getScriptMustReturnTrueOrFalseException(String var1, Object var2, String var3);

    @Message(id=26, value="Assertion error: inconsistent ConfigurationImpl construction.")
    public ValidationException getInconsistentConfigurationException();

    @Message(id=27, value="Unable to find provider: %s.")
    public ValidationException getUnableToFindProviderException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=28, value="Unexpected exception during isValid call.")
    public ValidationException getExceptionDuringIsValidCallException(@Cause RuntimeException var1);

    @Message(id=29, value="Constraint factory returned null when trying to create instance of %s.")
    public ValidationException getConstraintValidatorFactoryMustNotReturnNullException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ConstraintValidator<?, ?>> var1);

    @Message(id=30, value="No validator could be found for constraint '%s' validating type '%s'. Check configuration for '%s'")
    public UnexpectedTypeException getNoValidatorFoundForTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, String var2, String var3);

    @Message(id=31, value="There are multiple validator classes which could validate the type %1$s. The validator classes are: %2$s.")
    public UnexpectedTypeException getMoreThanOneValidatorFoundForTypeException(Type var1, @FormatWith(value=CollectionOfObjectsToStringFormatter.class) Collection<Type> var2);

    @Message(id=32, value="Unable to initialize %s.")
    public ValidationException getUnableToInitializeConstraintValidatorException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ConstraintValidator> var1, @Cause RuntimeException var2);

    @Message(id=33, value="At least one custom message must be created if the default error message gets disabled.")
    public ValidationException getAtLeastOneCustomMessageMustBeCreatedException();

    @Message(id=34, value="%s is not a valid Java Identifier.")
    public IllegalArgumentException getInvalidJavaIdentifierException(String var1);

    @Message(id=35, value="Unable to parse property path %s.")
    public IllegalArgumentException getUnableToParsePropertyPathException(String var1);

    @Message(id=36, value="Type %s not supported for unwrapping.")
    public ValidationException getTypeNotSupportedForUnwrappingException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=37, value="Inconsistent fail fast configuration. Fail fast enabled via programmatic API, but explicitly disabled via properties.")
    public ValidationException getInconsistentFailFastConfigurationException();

    @Message(id=38, value="Invalid property path.")
    public IllegalArgumentException getInvalidPropertyPathException();

    @Message(id=39, value="Invalid property path. Either there is no property %2$s in entity %1$s or it is not possible to cascade to the property.")
    public IllegalArgumentException getInvalidPropertyPathException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2);

    @Message(id=40, value="Property path must provide index or map key.")
    public IllegalArgumentException getPropertyPathMustProvideIndexOrMapKeyException();

    @Message(id=41, value="Call to TraversableResolver.isReachable() threw an exception.")
    public ValidationException getErrorDuringCallOfTraversableResolverIsReachableException(@Cause RuntimeException var1);

    @Message(id=42, value="Call to TraversableResolver.isCascadable() threw an exception.")
    public ValidationException getErrorDuringCallOfTraversableResolverIsCascadableException(@Cause RuntimeException var1);

    @Message(id=43, value="Unable to expand default group list %1$s into sequence %2$s.")
    public GroupDefinitionException getUnableToExpandDefaultGroupListException(@FormatWith(value=CollectionOfObjectsToStringFormatter.class) List<?> var1, @FormatWith(value=CollectionOfObjectsToStringFormatter.class) List<?> var2);

    @Message(id=44, value="At least one group has to be specified.")
    public IllegalArgumentException getAtLeastOneGroupHasToBeSpecifiedException();

    @Message(id=45, value="A group has to be an interface. %s is not.")
    public ValidationException getGroupHasToBeAnInterfaceException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=46, value="Sequence definitions are not allowed as composing parts of a sequence.")
    public GroupDefinitionException getSequenceDefinitionsNotAllowedException();

    @Message(id=47, value="Cyclic dependency in groups definition")
    public GroupDefinitionException getCyclicDependencyInGroupsDefinitionException();

    @Message(id=48, value="Unable to expand group sequence.")
    public GroupDefinitionException getUnableToExpandGroupSequenceException();

    @Message(id=52, value="Default group sequence and default group sequence provider cannot be defined at the same time.")
    public GroupDefinitionException getInvalidDefaultGroupSequenceDefinitionException();

    @Message(id=53, value="'Default.class' cannot appear in default group sequence list.")
    public GroupDefinitionException getNoDefaultGroupInGroupSequenceException();

    @Message(id=54, value="%s must be part of the redefined default group sequence.")
    public GroupDefinitionException getBeanClassMustBePartOfRedefinedDefaultGroupSequenceException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=55, value="The default group sequence provider defined for %s has the wrong type")
    public GroupDefinitionException getWrongDefaultGroupSequenceProviderTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=56, value="Method or constructor %1$s doesn't have a parameter with index %2$d.")
    public IllegalArgumentException getInvalidExecutableParameterIndexException(@FormatWith(value=ExecutableFormatter.class) Executable var1, int var2);

    @Message(id=59, value="Unable to retrieve annotation parameter value.")
    public ValidationException getUnableToRetrieveAnnotationParameterValueException(@Cause Exception var1);

    @Message(id=62, value="Method or constructor %1$s has %2$s parameters, but the passed list of parameter meta data has a size of %3$s.")
    public IllegalArgumentException getInvalidLengthOfParameterMetaDataListException(@FormatWith(value=ExecutableFormatter.class) Executable var1, int var2, int var3);

    @Message(id=63, value="Unable to instantiate %s.")
    public ValidationException getUnableToInstantiateException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @Cause Exception var2);

    @Message(id=64, value="Unable to instantiate %1$s: %2$s.")
    public ValidationException getUnableToInstantiateException(String var1, @FormatWith(value=ClassObjectFormatter.class) Class<?> var2, @Cause Exception var3);

    @Message(id=65, value="Unable to load class: %s from %s.")
    public ValidationException getUnableToLoadClassException(String var1, ClassLoader var2, @Cause Exception var3);

    @Message(id=68, value="Start index cannot be negative: %d.")
    public IllegalArgumentException getStartIndexCannotBeNegativeException(int var1);

    @Message(id=69, value="End index cannot be negative: %d.")
    public IllegalArgumentException getEndIndexCannotBeNegativeException(int var1);

    @Message(id=70, value="Invalid Range: %1$d > %2$d.")
    public IllegalArgumentException getInvalidRangeException(int var1, int var2);

    @Message(id=71, value="A explicitly specified check digit must lie outside the interval: [%1$d, %2$d].")
    public IllegalArgumentException getInvalidCheckDigitException(int var1, int var2);

    @Message(id=72, value="'%c' is not a digit.")
    public NumberFormatException getCharacterIsNotADigitException(char var1);

    @Message(id=73, value="Parameters starting with 'valid' are not allowed in a constraint.")
    public ConstraintDefinitionException getConstraintParametersCannotStartWithValidException();

    @Message(id=74, value="%2$s contains Constraint annotation, but does not contain a %1$s parameter.")
    public ConstraintDefinitionException getConstraintWithoutMandatoryParameterException(String var1, String var2);

    @Message(id=75, value="%s contains Constraint annotation, but the payload parameter default value is not the empty array.")
    public ConstraintDefinitionException getWrongDefaultValueForPayloadParameterException(String var1);

    @Message(id=76, value="%s contains Constraint annotation, but the payload parameter is of wrong type.")
    public ConstraintDefinitionException getWrongTypeForPayloadParameterException(String var1, @Cause ClassCastException var2);

    @Message(id=77, value="%s contains Constraint annotation, but the groups parameter default value is not the empty array.")
    public ConstraintDefinitionException getWrongDefaultValueForGroupsParameterException(String var1);

    @Message(id=78, value="%s contains Constraint annotation, but the groups parameter is of wrong type.")
    public ConstraintDefinitionException getWrongTypeForGroupsParameterException(String var1, @Cause ClassCastException var2);

    @Message(id=79, value="%s contains Constraint annotation, but the message parameter is not of type java.lang.String.")
    public ConstraintDefinitionException getWrongTypeForMessageParameterException(String var1);

    @Message(id=80, value="Overridden constraint does not define an attribute with name %s.")
    public ConstraintDefinitionException getOverriddenConstraintAttributeNotFoundException(String var1);

    @Message(id=81, value="The overriding type of a composite constraint must be identical to the overridden one. Expected %1$s found %2$s.")
    public ConstraintDefinitionException getWrongAttributeTypeForOverriddenConstraintException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @FormatWith(value=ClassObjectFormatter.class) Class<?> var2);

    @Message(id=82, value="Wrong type for attribute '%2$s' of annotation %1$s. Expected: %3$s. Actual: %4$s.")
    public ValidationException getWrongAnnotationAttributeTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, String var2, @FormatWith(value=ClassObjectFormatter.class) Class<?> var3, @FormatWith(value=ClassObjectFormatter.class) Class<?> var4);

    @Message(id=83, value="The specified annotation %1$s defines no attribute '%2$s'.")
    public ValidationException getUnableToFindAnnotationAttributeException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, String var2, @Cause NoSuchMethodException var3);

    @Message(id=84, value="Unable to get attribute '%2$s' from annotation %1$s.")
    public ValidationException getUnableToGetAnnotationAttributeException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, String var2, @Cause Exception var3);

    @Message(id=85, value="No value provided for attribute '%1$s' of annotation @%2$s.")
    public IllegalArgumentException getNoValueProvidedForAnnotationAttributeException(String var1, @FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var2);

    @Message(id=86, value="Trying to instantiate annotation %1$s with unknown attribute(s): %2$s.")
    public RuntimeException getTryingToInstantiateAnnotationWithUnknownAttributesException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, Set<String> var2);

    @Message(id=87, value="Property name cannot be null or empty.")
    public IllegalArgumentException getPropertyNameCannotBeNullOrEmptyException();

    @Message(id=88, value="Element type has to be FIELD or METHOD.")
    public IllegalArgumentException getElementTypeHasToBeFieldOrMethodException();

    @Message(id=89, value="Member %s is neither a field nor a method.")
    public IllegalArgumentException getMemberIsNeitherAFieldNorAMethodException(Member var1);

    @Message(id=90, value="Unable to access %s.")
    public ValidationException getUnableToAccessMemberException(String var1, @Cause Exception var2);

    @Message(id=91, value="%s has to be a primitive type.")
    public IllegalArgumentException getHasToBeAPrimitiveTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=93, value="null is an invalid type for a constraint validator.")
    public ValidationException getNullIsAnInvalidTypeForAConstraintValidatorException();

    @Message(id=94, value="Missing actual type argument for type parameter: %s.")
    public IllegalArgumentException getMissingActualTypeArgumentForTypeParameterException(TypeVariable<?> var1);

    @Message(id=95, value="Unable to instantiate constraint factory class %s.")
    public ValidationException getUnableToInstantiateConstraintValidatorFactoryClassException(String var1, @Cause ValidationException var2);

    @Message(id=96, value="Unable to open input stream for mapping file %s.")
    public ValidationException getUnableToOpenInputStreamForMappingFileException(String var1);

    @Message(id=97, value="Unable to instantiate message interpolator class %s.")
    public ValidationException getUnableToInstantiateMessageInterpolatorClassException(String var1, @Cause Exception var2);

    @Message(id=98, value="Unable to instantiate traversable resolver class %s.")
    public ValidationException getUnableToInstantiateTraversableResolverClassException(String var1, @Cause Exception var2);

    @Message(id=99, value="Unable to instantiate validation provider class %s.")
    public ValidationException getUnableToInstantiateValidationProviderClassException(String var1, @Cause Exception var2);

    @Message(id=100, value="Unable to parse %s.")
    public ValidationException getUnableToParseValidationXmlFileException(String var1, @Cause Exception var2);

    @Message(id=101, value="%s is not an annotation.")
    public ValidationException getIsNotAnAnnotationException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=102, value="%s is not a constraint validator class.")
    public ValidationException getIsNotAConstraintValidatorClassException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=103, value="%s is configured at least twice in xml.")
    public ValidationException getBeanClassHasAlreadyBeenConfiguredInXmlException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=104, value="%1$s is defined twice in mapping xml for bean %2$s.")
    public ValidationException getIsDefinedTwiceInMappingXmlForBeanException(String var1, @FormatWith(value=ClassObjectFormatter.class) Class<?> var2);

    @Message(id=105, value="%1$s does not contain the fieldType %2$s.")
    public ValidationException getBeanDoesNotContainTheFieldException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2);

    @Message(id=106, value="%1$s does not contain the property %2$s.")
    public ValidationException getBeanDoesNotContainThePropertyException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2);

    @Message(id=107, value="Annotation of type %1$s does not contain a parameter %2$s.")
    public ValidationException getAnnotationDoesNotContainAParameterException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, String var2);

    @Message(id=108, value="Attempt to specify an array where single value is expected.")
    public ValidationException getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException();

    @Message(id=109, value="Unexpected parameter value.")
    public ValidationException getUnexpectedParameterValueException();

    public ValidationException getUnexpectedParameterValueException(@Cause ClassCastException var1);

    @Message(id=110, value="Invalid %s format.")
    public ValidationException getInvalidNumberFormatException(String var1, @Cause NumberFormatException var2);

    @Message(id=111, value="Invalid char value: %s.")
    public ValidationException getInvalidCharValueException(String var1);

    @Message(id=112, value="Invalid return type: %s. Should be a enumeration type.")
    public ValidationException getInvalidReturnTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @Cause ClassCastException var2);

    @Message(id=113, value="%s, %s, %s are reserved parameter names.")
    public ValidationException getReservedParameterNamesException(String var1, String var2, String var3);

    @Message(id=114, value="Specified payload class %s does not implement javax.validation.Payload")
    public ValidationException getWrongPayloadClassException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=115, value="Error parsing mapping file.")
    public ValidationException getErrorParsingMappingFileException(@Cause Exception var1);

    @Message(id=116, value="%s")
    public IllegalArgumentException getIllegalArgumentException(String var1);

    @Message(id=118, value="Unable to cast %s (with element kind %s) to %s")
    public ClassCastException getUnableToNarrowNodeTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, ElementKind var2, @FormatWith(value=ClassObjectFormatter.class) Class<?> var3);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=119, value="Using %s as parameter name provider.")
    public void usingParameterNameProvider(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ParameterNameProvider> var1);

    @Message(id=120, value="Unable to instantiate parameter name provider class %s.")
    public ValidationException getUnableToInstantiateParameterNameProviderClassException(String var1, @Cause ValidationException var2);

    @Message(id=121, value="Unable to parse %s.")
    public ValidationException getUnableToDetermineSchemaVersionException(String var1, @Cause XMLStreamException var2);

    @Message(id=122, value="Unsupported schema version for %s: %s.")
    public ValidationException getUnsupportedSchemaVersionException(String var1, String var2);

    @Message(id=124, value="Found multiple group conversions for source group %s: %s.")
    public ConstraintDeclarationException getMultipleGroupConversionsForSameSourceException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @FormatWith(value=CollectionOfClassesObjectFormatter.class) Collection<Class<?>> var2);

    @Message(id=125, value="Found group conversions for non-cascading element at: %s.")
    public ConstraintDeclarationException getGroupConversionOnNonCascadingElementException(Object var1);

    @Message(id=127, value="Found group conversion using a group sequence as source at: %s.")
    public ConstraintDeclarationException getGroupConversionForSequenceException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=129, value="EL expression '%s' references an unknown property")
    public void unknownPropertyInExpressionLanguage(String var1, @Cause Exception var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=130, value="Error in EL expression '%s'")
    public void errorInExpressionLanguage(String var1, @Cause Exception var2);

    @Message(id=131, value="A method return value must not be marked for cascaded validation more than once in a class hierarchy, but the following two methods are marked as such: %s, %s.")
    public ConstraintDeclarationException getMethodReturnValueMustNotBeMarkedMoreThanOnceForCascadedValidationException(@FormatWith(value=ExecutableFormatter.class) Executable var1, @FormatWith(value=ExecutableFormatter.class) Executable var2);

    @Message(id=132, value="Void methods must not be constrained or marked for cascaded validation, but method %s is.")
    public ConstraintDeclarationException getVoidMethodsMustNotBeConstrainedException(@FormatWith(value=ExecutableFormatter.class) Executable var1);

    @Message(id=133, value="%1$s does not contain a constructor with the parameter types %2$s.")
    public ValidationException getBeanDoesNotContainConstructorException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @FormatWith(value=ArrayOfClassesObjectFormatter.class) Class<?>[] var2);

    @Message(id=134, value="Unable to load parameter of type '%1$s' in %2$s.")
    public ValidationException getInvalidParameterTypeException(String var1, @FormatWith(value=ClassObjectFormatter.class) Class<?> var2);

    @Message(id=135, value="%1$s does not contain a method with the name '%2$s' and parameter types %3$s.")
    public ValidationException getBeanDoesNotContainMethodException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2, @FormatWith(value=ArrayOfClassesObjectFormatter.class) Class<?>[] var3);

    @Message(id=136, value="The specified constraint annotation class %1$s cannot be loaded.")
    public ValidationException getUnableToLoadConstraintAnnotationClassException(String var1, @Cause Exception var2);

    @Message(id=137, value="The method '%1$s' is defined twice in the mapping xml for bean %2$s.")
    public ValidationException getMethodIsDefinedTwiceInMappingXmlForBeanException(Method var1, @FormatWith(value=ClassObjectFormatter.class) Class<?> var2);

    @Message(id=138, value="The constructor '%1$s' is defined twice in the mapping xml for bean %2$s.")
    public ValidationException getConstructorIsDefinedTwiceInMappingXmlForBeanException(Constructor<?> var1, @FormatWith(value=ClassObjectFormatter.class) Class<?> var2);

    @Message(id=139, value="The constraint '%1$s' defines multiple cross parameter validators. Only one is allowed.")
    public ConstraintDefinitionException getMultipleCrossParameterValidatorClassesException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=141, value="The constraint %1$s used ConstraintTarget#IMPLICIT where the target cannot be inferred.")
    public ConstraintDeclarationException getImplicitConstraintTargetInAmbiguousConfigurationException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=142, value="Cross parameter constraint %1$s is illegally placed on a parameterless method or constructor '%2$s'.")
    public ConstraintDeclarationException getCrossParameterConstraintOnMethodWithoutParametersException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, @FormatWith(value=ExecutableFormatter.class) Executable var2);

    @Message(id=143, value="Cross parameter constraint %1$s is illegally placed on class level.")
    public ConstraintDeclarationException getCrossParameterConstraintOnClassException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=144, value="Cross parameter constraint %1$s is illegally placed on field '%2$s'.")
    public ConstraintDeclarationException getCrossParameterConstraintOnFieldException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, Member var2);

    @Message(id=146, value="No parameter nodes may be added since path %s doesn't refer to a cross-parameter constraint.")
    public IllegalStateException getParameterNodeAddedForNonCrossParameterConstraintException(Path var1);

    @Message(id=147, value="%1$s is configured multiple times (note, <getter> and <method> nodes for the same method are not allowed)")
    public ValidationException getConstrainedElementConfiguredMultipleTimesException(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=148, value="An exception occurred during evaluation of EL expression '%s'")
    public void evaluatingExpressionLanguageExpressionCausedException(String var1, @Cause Exception var2);

    @Message(id=149, value="An exception occurred during message interpolation")
    public ValidationException getExceptionOccurredDuringMessageInterpolationException(@Cause Exception var1);

    @Message(id=150, value="The constraint %1$s defines multiple validators for the type %2$s: %3$s, %4$s. Only one is allowed.")
    public UnexpectedTypeException getMultipleValidatorsForSameTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, @FormatWith(value=TypeFormatter.class) Type var2, @FormatWith(value=ClassObjectFormatter.class) Class<? extends ConstraintValidator<?, ?>> var3, @FormatWith(value=ClassObjectFormatter.class) Class<? extends ConstraintValidator<?, ?>> var4);

    @Message(id=151, value="A method overriding another method must not redefine the parameter constraint configuration, but method %2$s redefines the configuration of %1$s.")
    public ConstraintDeclarationException getParameterConfigurationAlteredInSubTypeException(@FormatWith(value=ExecutableFormatter.class) Executable var1, @FormatWith(value=ExecutableFormatter.class) Executable var2);

    @Message(id=152, value="Two methods defined in parallel types must not declare parameter constraints, if they are overridden by the same method, but methods %s and %s both define parameter constraints.")
    public ConstraintDeclarationException getParameterConstraintsDefinedInMethodsFromParallelTypesException(@FormatWith(value=ExecutableFormatter.class) Executable var1, @FormatWith(value=ExecutableFormatter.class) Executable var2);

    @Message(id=153, value="The constraint %1$s used ConstraintTarget#%2$s but is not specified on a method or constructor.")
    public ConstraintDeclarationException getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, ConstraintTarget var2);

    @Message(id=154, value="Cross parameter constraint %1$s has no cross-parameter validator.")
    public ConstraintDefinitionException getCrossParameterConstraintHasNoValidatorException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=155, value="Composed and composing constraints must have the same constraint type, but composed constraint %1$s has type %3$s, while composing constraint %2$s has type %4$s.")
    public ConstraintDefinitionException getComposedAndComposingConstraintsHaveDifferentTypesException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, @FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var2, ConstraintDescriptorImpl.ConstraintType var3, ConstraintDescriptorImpl.ConstraintType var4);

    @Message(id=156, value="Constraints with generic as well as cross-parameter validators must define an attribute validationAppliesTo(), but constraint %s doesn't.")
    public ConstraintDefinitionException getGenericAndCrossParameterConstraintDoesNotDefineValidationAppliesToParameterException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=157, value="Return type of the attribute validationAppliesTo() of the constraint %s must be javax.validation.ConstraintTarget.")
    public ConstraintDefinitionException getValidationAppliesToParameterMustHaveReturnTypeConstraintTargetException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=158, value="Default value of the attribute validationAppliesTo() of the constraint %s must be ConstraintTarget#IMPLICIT.")
    public ConstraintDefinitionException getValidationAppliesToParameterMustHaveDefaultValueImplicitException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=159, value="Only constraints with generic as well as cross-parameter validators must define an attribute validationAppliesTo(), but constraint %s does.")
    public ConstraintDefinitionException getValidationAppliesToParameterMustNotBeDefinedForNonGenericAndCrossParameterConstraintException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=160, value="Validator for cross-parameter constraint %s does not validate Object nor Object[].")
    public ConstraintDefinitionException getValidatorForCrossParameterConstraintMustEitherValidateObjectOrObjectArrayException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=161, value="Two methods defined in parallel types must not define group conversions for a cascaded method return value, if they are overridden by the same method, but methods %s and %s both define parameter constraints.")
    public ConstraintDeclarationException getMethodsFromParallelTypesMustNotDefineGroupConversionsForCascadedReturnValueException(@FormatWith(value=ExecutableFormatter.class) Executable var1, @FormatWith(value=ExecutableFormatter.class) Executable var2);

    @Message(id=162, value="The validated type %1$s does not specify the constructor/method: %2$s")
    public IllegalArgumentException getMethodOrConstructorNotDefinedByValidatedTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @FormatWith(value=ExecutableFormatter.class) Executable var2);

    @Message(id=163, value="The actual parameter type '%1$s' is not assignable to the expected one '%2$s' for parameter %3$d of '%4$s'")
    public IllegalArgumentException getParameterTypesDoNotMatchException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, Type var2, int var3, @FormatWith(value=ExecutableFormatter.class) Executable var4);

    @Message(id=164, value="%s has to be a auto-boxed type.")
    public IllegalArgumentException getHasToBeABoxedTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=165, value="Mixing IMPLICIT and other executable types is not allowed.")
    public IllegalArgumentException getMixingImplicitWithOtherExecutableTypesException();

    @Message(id=166, value="@ValidateOnExecution is not allowed on methods overriding a superclass method or implementing an interface. Check configuration for %1$s")
    public ValidationException getValidateOnExecutionOnOverriddenOrInterfaceMethodException(@FormatWith(value=ExecutableFormatter.class) Executable var1);

    @Message(id=167, value="A given constraint definition can only be overridden in one mapping file. %1$s is overridden in multiple files")
    public ValidationException getOverridingConstraintDefinitionsInMultipleMappingFilesException(String var1);

    @Message(id=168, value="The message descriptor '%1$s' contains an unbalanced meta character '%2$c'.")
    public MessageDescriptorFormatException getUnbalancedBeginEndParameterException(String var1, char var2);

    @Message(id=169, value="The message descriptor '%1$s' has nested parameters.")
    public MessageDescriptorFormatException getNestedParameterException(String var1);

    @Message(id=170, value="No JSR-223 scripting engine could be bootstrapped for language \"%s\".")
    public ConstraintDeclarationException getCreationOfScriptExecutorFailedException(String var1, @Cause Exception var2);

    @Message(id=171, value="%s is configured more than once via the programmatic constraint declaration API.")
    public ValidationException getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=172, value="Property \"%2$s\" of type %1$s is configured more than once via the programmatic constraint declaration API.")
    public ValidationException getPropertyHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2);

    @Message(id=173, value="Method %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.")
    public ValidationException getMethodHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2);

    @Message(id=174, value="Parameter %3$s of method or constructor %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.")
    public ValidationException getParameterHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @FormatWith(value=ExecutableFormatter.class) Executable var2, int var3);

    @Message(id=175, value="The return value of method or constructor %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.")
    public ValidationException getReturnValueHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @FormatWith(value=ExecutableFormatter.class) Executable var2);

    @Message(id=176, value="Constructor %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.")
    public ValidationException getConstructorHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2);

    @Message(id=177, value="Cross-parameter constraints for the method or constructor %2$s of type %1$s are declared more than once via the programmatic constraint declaration API.")
    public ValidationException getCrossParameterElementHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @FormatWith(value=ExecutableFormatter.class) Executable var2);

    @Message(id=178, value="Multiplier cannot be negative: %d.")
    public IllegalArgumentException getMultiplierCannotBeNegativeException(int var1);

    @Message(id=179, value="Weight cannot be negative: %d.")
    public IllegalArgumentException getWeightCannotBeNegativeException(int var1);

    @Message(id=180, value="'%c' is not a digit nor a letter.")
    public IllegalArgumentException getTreatCheckAsIsNotADigitNorALetterException(int var1);

    @Message(id=181, value="Wrong number of parameters. Method or constructor %1$s expects %2$d parameters, but got %3$d.")
    public IllegalArgumentException getInvalidParameterCountForExecutableException(String var1, int var2, int var3);

    @Message(id=182, value="No validation value unwrapper is registered for type '%1$s'.")
    public ValidationException getNoUnwrapperFoundForTypeException(Type var1);

    @Message(id=183, value="Unable to initialize 'javax.el.ExpressionFactory'. Check that you have the EL dependencies on the classpath, or use ParameterMessageInterpolator instead")
    public ValidationException getUnableToInitializeELExpressionFactoryException(@Cause Throwable var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=185, value="Message contains EL expression: %1s, which is not supported by the selected message interpolator")
    public void warnElIsUnsupported(String var1);

    @Message(id=189, value="The configuration of value unwrapping for property '%s' of bean '%s' is inconsistent between the field and its getter.")
    public ConstraintDeclarationException getInconsistentValueUnwrappingConfigurationBetweenFieldAndItsGetterException(String var1, @FormatWith(value=ClassObjectFormatter.class) Class<?> var2);

    @Message(id=190, value="Unable to parse %s.")
    public ValidationException getUnableToCreateXMLEventReader(String var1, @Cause Exception var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=192, value="Couldn't determine Java version from value %1s; Not enabling features requiring Java 8")
    public void unknownJvmVersion(String var1);

    @Message(id=193, value="%s is configured more than once via the programmatic constraint definition API.")
    public ValidationException getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1);

    @Message(id=194, value="An empty element is only supported when a CharSequence is expected.")
    public ValidationException getEmptyElementOnlySupportedWhenCharSequenceIsExpectedExpection();

    @Message(id=195, value="Unable to reach the property to validate for the bean %s and the property path %s. A property is null along the way.")
    public ValidationException getUnableToReachPropertyToValidateException(Object var1, Path var2);

    @Message(id=196, value="Unable to convert the Type %s to a Class.")
    public ValidationException getUnableToConvertTypeToClassException(Type var1);

    @Message(id=197, value="No value extractor found for type parameter '%2$s' of type %1$s.")
    public ConstraintDeclarationException getNoValueExtractorFoundForTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, TypeVariable<?> var2);

    @Message(id=198, value="No suitable value extractor found for type %1$s.")
    public ConstraintDeclarationException getNoValueExtractorFoundForUnwrapException(Type var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=200, value="Using %s as clock provider.")
    public void usingClockProvider(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ClockProvider> var1);

    @Message(id=201, value="Unable to instantiate clock provider class %s.")
    public ValidationException getUnableToInstantiateClockProviderClassException(String var1, @Cause ValidationException var2);

    @Message(id=202, value="Unable to get the current time from the clock provider")
    public ValidationException getUnableToGetCurrentTimeFromClockProvider(@Cause Exception var1);

    @Message(id=203, value="Value extractor type %1s fails to declare the extracted type parameter using @ExtractedValue.")
    public ValueExtractorDefinitionException getValueExtractorFailsToDeclareExtractedValueException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=204, value="Only one type parameter must be marked with @ExtractedValue for value extractor type %1s.")
    public ValueExtractorDefinitionException getValueExtractorDeclaresExtractedValueMultipleTimesException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=205, value="Invalid unwrapping configuration for constraint %2$s on %1$s. You can only define one of 'Unwrapping.Skip' or 'Unwrapping.Unwrap'.")
    public ConstraintDeclarationException getInvalidUnwrappingConfigurationForConstraintException(Member var1, @FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var2);

    @Message(id=206, value="Unable to instantiate value extractor class %s.")
    public ValidationException getUnableToInstantiateValueExtractorClassException(String var1, @Cause ValidationException var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=207, value="Adding value extractor %s.")
    public void addingValueExtractor(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ValueExtractor<?>> var1);

    @Message(id=208, value="Given value extractor %2$s handles the same type and type use as previously given value extractor %1$s.")
    public ValueExtractorDeclarationException getValueExtractorForTypeAndTypeUseAlreadyPresentException(ValueExtractor<?> var1, ValueExtractor<?> var2);

    @Message(id=209, value="A composing constraint (%2$s) must not be given directly on the composed constraint (%1$s) and using the corresponding List annotation at the same time.")
    public ConstraintDeclarationException getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var1, @FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var2);

    @Message(id=210, value="Unable to find the type parameter %2$s in class %1$s.")
    public IllegalArgumentException getUnableToFindTypeParameterInClass(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, Object var2);

    @Message(id=211, value="Given type is neither a parameterized nor an array type: %s.")
    public ValidationException getTypeIsNotAParameterizedNorArrayTypeException(@FormatWith(value=TypeFormatter.class) Type var1);

    @Message(id=212, value="Given type has no type argument with index %2$s: %1$s.")
    public ValidationException getInvalidTypeArgumentIndexException(@FormatWith(value=TypeFormatter.class) Type var1, int var2);

    @Message(id=213, value="Given type has more than one type argument, hence an argument index must be specified: %s.")
    public ValidationException getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException(@FormatWith(value=TypeFormatter.class) Type var1);

    @Message(id=214, value="The same container element type of type %1$s is configured more than once via the programmatic constraint declaration API.")
    public ValidationException getContainerElementTypeHasAlreadyBeenConfiguredViaProgrammaticApiException(@FormatWith(value=TypeFormatter.class) Type var1);

    @Message(id=215, value="Calling parameter() is not allowed for the current element.")
    public ValidationException getParameterIsNotAValidCallException();

    @Message(id=216, value="Calling returnValue() is not allowed for the current element.")
    public ValidationException getReturnValueIsNotAValidCallException();

    @Message(id=217, value="The same container element type %2$s is configured more than once for location %1$s via the XML mapping configuration.")
    public ValidationException getContainerElementTypeHasAlreadyBeenConfiguredViaXmlMappingConfigurationException(ConstraintLocation var1, ContainerElementTypePath var2);

    @Message(id=218, value="Having parallel definitions of value extractors on a given class is not allowed: %s.")
    public ValueExtractorDefinitionException getParallelDefinitionsOfValueExtractorsException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1);

    @Message(id=219, value="Unable to get the most specific value extractor for type %1$s as several most specific value extractors are declared: %2$s.")
    public ConstraintDeclarationException getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @FormatWith(value=CollectionOfClassesObjectFormatter.class) Collection<Class<? extends ValueExtractor>> var2);

    @Message(id=220, value="When @ExtractedValue is defined on a type parameter of a container type, the type attribute may not be set: %1$s.")
    public ValueExtractorDefinitionException getExtractedValueOnTypeParameterOfContainerTypeMayNotDefineTypeAttributeException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ValueExtractor> var1);

    @Message(id=221, value="An error occurred while extracting values in value extractor %1$s.")
    public ValidationException getErrorWhileExtractingValuesInValueExtractorException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ValueExtractor> var1, @Cause Exception var2);

    @Message(id=222, value="The same value extractor %s is added more than once via the XML configuration.")
    public ValueExtractorDeclarationException getDuplicateDefinitionsOfValueExtractorException(String var1);

    @Message(id=223, value="Implicit unwrapping is not allowed for type %1$s as several maximally specific value extractors marked with @UnwrapByDefault are declared: %2$s.")
    public ConstraintDeclarationException getImplicitUnwrappingNotAllowedWhenSeveralMaximallySpecificValueExtractorsMarkedWithUnwrapByDefaultDeclaredException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @FormatWith(value=CollectionOfClassesObjectFormatter.class) Collection<Class<? extends ValueExtractor>> var2);

    @Message(id=224, value="Unwrapping of ConstraintDescriptor is not supported yet.")
    public ValidationException getUnwrappingOfConstraintDescriptorNotSupportedYetException();

    @Message(id=225, value="Only unbound wildcard type arguments are supported for the container type of the value extractor: %1$s.")
    public ValueExtractorDefinitionException getOnlyUnboundWildcardTypeArgumentsSupportedForContainerTypeOfValueExtractorException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ValueExtractor> var1);

    @Message(id=226, value="Container element constraints and cascading validation are not supported on arrays: %1$s")
    public ValidationException getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(@FormatWith(value=TypeFormatter.class) Type var1);

    @Message(id=227, value="The validated type %1$s does not specify the property: %2$s")
    public IllegalArgumentException getPropertyNotDefinedByValidatedTypeException(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2);

    @Message(id=228, value="No value extractor found when narrowing down to the runtime type %3$s among the value extractors for type parameter '%2$s' of type %1$s.")
    public ConstraintDeclarationException getNoValueExtractorFoundForTypeException(@FormatWith(value=TypeFormatter.class) Type var1, TypeVariable<?> var2, @FormatWith(value=ClassObjectFormatter.class) Class<?> var3);

    @Message(id=229, value="Unable to cast %1$s to %2$s.")
    public ClassCastException getUnableToCastException(Object var1, @FormatWith(value=ClassObjectFormatter.class) Class<?> var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=230, value="Using %s as script evaluator factory.")
    public void usingScriptEvaluatorFactory(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ScriptEvaluatorFactory> var1);

    @Message(id=231, value="Unable to instantiate script evaluator factory class %s.")
    public ValidationException getUnableToInstantiateScriptEvaluatorFactoryClassException(String var1, @Cause Exception var2);

    @Message(id=232, value="No JSR 223 script engine found for language \"%s\".")
    public ScriptEvaluatorNotFoundException getUnableToFindScriptEngineException(String var1);

    @Message(id=233, value="An error occurred while executing the script: \"%s\".")
    public ScriptEvaluationException getErrorExecutingScriptException(String var1, @Cause Exception var2);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(id=234, value="Using %1$s as ValidatorFactory-scoped %2$s.")
    public void logValidatorFactoryScopedConfiguration(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, String var2);

    @Message(id=235, value="Unable to create an annotation descriptor for %1$s.")
    public ValidationException getUnableToCreateAnnotationDescriptor(@FormatWith(value=ClassObjectFormatter.class) Class<?> var1, @Cause Throwable var2);

    @Message(id=236, value="Unable to find the method required to create the constraint annotation descriptor.")
    public ValidationException getUnableToFindAnnotationDefDeclaredMethods(@Cause Exception var1);

    @Message(id=237, value="Unable to access method %3$s of class %2$s with parameters %4$s using lookup %1$s.")
    public ValidationException getUnableToAccessMethodException(MethodHandles.Lookup var1, @FormatWith(value=ClassObjectFormatter.class) Class<?> var2, String var3, @FormatWith(value=ObjectArrayFormatter.class) Object[] var4, @Cause Throwable var5);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=238, value="Temporal validation tolerance set to %1$s.")
    public void logTemporalValidationTolerance(@FormatWith(value=DurationFormatter.class) Duration var1);

    @Message(id=239, value="Unable to parse the temporal validation tolerance property %s. It should be a duration represented in milliseconds.")
    public ValidationException getUnableToParseTemporalValidationToleranceException(String var1, @Cause Exception var2);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(id=240, value="Constraint validator payload set to %1$s.")
    public void logConstraintValidatorPayload(Object var1);

    @Message(id=241, value="Encountered unsupported element %1$s while parsing the XML configuration.")
    public ValidationException logUnknownElementInXmlConfiguration(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=242, value="Unable to load or instantiate JPA aware resolver %1$s. All properties will per default be traversable.")
    public void logUnableToLoadOrInstantiateJPAAwareResolver(String var1);

    @Message(id=243, value="Constraint %2$s references constraint validator type %1$s, but this validator is defined for constraint type %3$s.")
    public ConstraintDefinitionException getConstraintValidatorDefinitionConstraintMismatchException(@FormatWith(value=ClassObjectFormatter.class) Class<? extends ConstraintValidator<?, ?>> var1, @FormatWith(value=ClassObjectFormatter.class) Class<? extends Annotation> var2, @FormatWith(value=TypeFormatter.class) Type var3);

    @Message(id=248, value="Unable to get an XML schema named %s.")
    public ValidationException unableToGetXmlSchema(String var1);
}


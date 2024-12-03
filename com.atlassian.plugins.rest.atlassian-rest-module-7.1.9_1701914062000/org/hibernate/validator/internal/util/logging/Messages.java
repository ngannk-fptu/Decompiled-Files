/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.Message$Format
 *  org.jboss.logging.annotations.MessageBundle
 */
package org.hibernate.validator.internal.util.logging;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode="HV")
public interface Messages {
    public static final Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);

    @Message(value="must not be null.", format=Message.Format.NO_FORMAT)
    public String mustNotBeNull();

    @Message(value="%s must not be null.")
    public String mustNotBeNull(String var1);

    @Message(value="The parameter \"%s\" must not be null.")
    public String parameterMustNotBeNull(String var1);

    @Message(value="The parameter \"%s\" must not be empty.")
    public String parameterMustNotBeEmpty(String var1);

    @Message(value="The bean type cannot be null.", format=Message.Format.NO_FORMAT)
    public String beanTypeCannotBeNull();

    @Message(value="null is not allowed as property path.", format=Message.Format.NO_FORMAT)
    public String propertyPathCannotBeNull();

    @Message(value="The property name must not be empty.", format=Message.Format.NO_FORMAT)
    public String propertyNameMustNotBeEmpty();

    @Message(value="null passed as group name.", format=Message.Format.NO_FORMAT)
    public String groupMustNotBeNull();

    @Message(value="The bean type must not be null when creating a constraint mapping.", format=Message.Format.NO_FORMAT)
    public String beanTypeMustNotBeNull();

    @Message(value="The method name must not be null.", format=Message.Format.NO_FORMAT)
    public String methodNameMustNotBeNull();

    @Message(value="The object to be validated must not be null.", format=Message.Format.NO_FORMAT)
    public String validatedObjectMustNotBeNull();

    @Message(value="The method to be validated must not be null.", format=Message.Format.NO_FORMAT)
    public String validatedMethodMustNotBeNull();

    @Message(value="The class cannot be null.", format=Message.Format.NO_FORMAT)
    public String classCannotBeNull();

    @Message(value="Class is null.", format=Message.Format.NO_FORMAT)
    public String classIsNull();

    @Message(value="The constructor to be validated must not be null.", format=Message.Format.NO_FORMAT)
    public String validatedConstructorMustNotBeNull();

    @Message(value="The method parameter array cannot not be null.", format=Message.Format.NO_FORMAT)
    public String validatedParameterArrayMustNotBeNull();

    @Message(value="The created instance must not be null.", format=Message.Format.NO_FORMAT)
    public String validatedConstructorCreatedInstanceMustNotBeNull();

    @Message(value="The input stream for #addMapping() cannot be null.")
    public String inputStreamCannotBeNull();

    @Message(value="Constraints on the parameters of constructors of non-static inner classes are not supported if those parameters have a generic type due to JDK bug JDK-5087240.")
    public String constraintOnConstructorOfNonStaticInnerClass();

    @Message(value="Custom parameterized types with more than one type argument are not supported and will not be checked for type use constraints.")
    public String parameterizedTypesWithMoreThanOneTypeArgument();

    @Message(value="Hibernate Validator cannot instantiate AggregateResourceBundle.CONTROL. This can happen most notably in a Google App Engine environment or when running Hibernate Validator as Java 9 named module. A PlatformResourceBundleLocator without bundle aggregation was created. This only affects you in case you are using multiple ConstraintDefinitionContributor JARs. ConstraintDefinitionContributors are a Hibernate Validator specific feature. All Bean Validation features work as expected. See also https://hibernate.atlassian.net/browse/HV-1023.")
    public String unableToUseResourceBundleAggregation();

    @Message(value="The annotation type must not be null when creating a constraint definition.", format=Message.Format.NO_FORMAT)
    public String annotationTypeMustNotBeNull();

    @Message(value="The annotation type must be annotated with @javax.validation.Constraint when creating a constraint definition.", format=Message.Format.NO_FORMAT)
    public String annotationTypeMustBeAnnotatedWithConstraint();
}


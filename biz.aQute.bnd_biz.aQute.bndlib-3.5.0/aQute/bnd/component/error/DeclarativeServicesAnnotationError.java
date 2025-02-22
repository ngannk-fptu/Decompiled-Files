/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.component.error;

public class DeclarativeServicesAnnotationError {
    public final String className;
    public final String methodName;
    public final String methodSignature;
    public final String fieldName;
    public final ErrorType errorType;

    public DeclarativeServicesAnnotationError(String className, String methodName, String methodSignature, ErrorType errorType) {
        this.className = className;
        this.methodName = methodName;
        this.methodSignature = methodSignature;
        this.fieldName = null;
        this.errorType = errorType;
    }

    public DeclarativeServicesAnnotationError(String className, String fieldName, ErrorType errorType) {
        this.className = className;
        this.methodName = null;
        this.methodSignature = null;
        this.fieldName = fieldName;
        this.errorType = errorType;
    }

    public static enum ErrorType {
        ACTIVATE_SIGNATURE_ERROR,
        DEACTIVATE_SIGNATURE_ERROR,
        MODIFIED_SIGNATURE_ERROR,
        COMPONENT_PROPERTIES_ERROR,
        INVALID_REFERENCE_BIND_METHOD_NAME,
        MULTIPLE_REFERENCES_SAME_NAME,
        UNABLE_TO_LOCATE_SUPER_CLASS,
        DYNAMIC_REFERENCE_WITHOUT_UNBIND,
        INVALID_TARGET_FILTER,
        UNSET_OR_MODIFY_WITH_WRONG_SIGNATURE,
        MIXED_USE_OF_DS_ANNOTATIONS_BND,
        MIXED_USE_OF_DS_ANNOTATIONS_STD,
        REFERENCE,
        DYNAMIC_FINAL_FIELD_WITH_REPLACE,
        INCOMPATIBLE_SERVICE,
        MISSING_REFERENCE_NAME;

    }
}


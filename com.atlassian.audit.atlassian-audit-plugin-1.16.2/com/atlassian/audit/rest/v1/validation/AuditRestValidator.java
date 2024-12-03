/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.audit.rest.v1.validation;

import com.atlassian.audit.rest.v1.validation.QueryParamValidator;
import com.atlassian.audit.rest.v1.validation.ValidationResult;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.commons.lang3.math.NumberUtils;

public class AuditRestValidator {
    private static final Integer MAX_SEARCH_TERM_LENGTH = Integer.getInteger("atlassian.audit.validation.max.search.term.length", 100);
    private static final Integer MAX_USER_ID_LENGTH = Integer.getInteger("atlassian.audit.validation.max.user.id.length", 200);
    private static final Integer MAX_CSV_SIZE = Integer.getInteger("atlassian.audit.validation.max.csv.size", 100000);

    private static boolean hasEmptyValue(String[] array) {
        return Arrays.stream(array).anyMatch(String::isEmpty);
    }

    public static class LimitValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            String limit = fieldValue;
            if (!NumberUtils.isParsable((String)limit)) {
                validationResult.add("validation.error.limit.not.number", "Limit should be a number");
            } else {
                long parsedLimit = Long.parseLong(limit);
                if (parsedLimit < 1L || parsedLimit > (long)MAX_CSV_SIZE.intValue()) {
                    validationResult.add("validation.error.limit.small", "Limit can't exceed 100 000");
                }
            }
        }
    }

    public static class FormatValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            if (!"csv".equals(fieldValue) && !"json".equals(fieldValue)) {
                validationResult.add("validation.error.format", "Format should be either csv or json");
            }
        }
    }

    public static class FromValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            try {
                Instant.parse(fieldValue);
            }
            catch (DateTimeException e) {
                validationResult.add("validation.error.from.invalid", "'from' timestamp is invalid");
            }
        }
    }

    public static class ToValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            try {
                Instant.parse(fieldValue);
            }
            catch (DateTimeException e) {
                validationResult.add("validation.error.to.invalid", "'to' timestamp is invalid");
            }
        }
    }

    public static class OffsetValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            String offset = fieldValue;
            if (!NumberUtils.isParsable((String)offset)) {
                validationResult.add("validation.error.offset.not.number", "Offset should be a number");
            } else {
                Long parsedOffset = Long.parseLong(offset);
                if (parsedOffset < 0L) {
                    validationResult.add("validation.error.to.invalid", "Offset should be greater than or equal to 0");
                }
                if (parsedOffset > Integer.MAX_VALUE) {
                    validationResult.add("validation.error.offset.too.large", "Offset exceeds 2147483647");
                }
            }
        }
    }

    public static class CursorValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            String[] cursorParts = fieldValue.split(",\\s*");
            if (cursorParts.length != 2 || !NumberUtils.isParsable((String)cursorParts[0]) || !NumberUtils.isParsable((String)cursorParts[1])) {
                validationResult.add("validation.error.cursor.invalid", "Cursor format is invalid. Should be two numbers delimited by ,");
            }
        }
    }

    public static class UserIdsValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            String[] userIds = fieldValue.split(",\\s*");
            if (!Arrays.stream(userIds).allMatch(userId -> userId.length() <= MAX_USER_ID_LENGTH)) {
                validationResult.add("validation.error.user.ids.invalid", "User ID exceeds " + MAX_USER_ID_LENGTH + " characters");
            }
        }
    }

    public static class SearchValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            String searchTerm = fieldValue;
            if (searchTerm.length() > MAX_SEARCH_TERM_LENGTH) {
                validationResult.add("validation.error.search.too.long", "Search term exceeds 100 characters");
            }
        }
    }

    public static class ScanLimitValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            String scanLimit = fieldValue;
            if (!NumberUtils.isParsable((String)scanLimit)) {
                validationResult.add("validation.error.scan.limit.not.number", "Scan limit is not a number");
            } else {
                long parsedLimit = Long.parseLong(scanLimit);
                if (parsedLimit > Integer.MAX_VALUE) {
                    validationResult.add("validation.error.scan.limit.too.large", "Scan limit exceeds 2147483647");
                }
            }
        }
    }

    public static class AffectedObjectsValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            Stream.of(fieldValue.split(";\\s*")).forEach(affectedObject -> {
                String[] affectedObjectsParts = affectedObject.split(",\\s*");
                if (affectedObjectsParts.length != 2) {
                    validationResult.add("validation.error.affected.objects.invalid", "Affected objects should be type and id seperated by comma");
                }
            });
        }
    }

    public static class CategoriesValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            String[] categoriesParts = fieldValue.split(",\\s*");
            if (AuditRestValidator.hasEmptyValue(categoriesParts)) {
                validationResult.add("validation.error.categories.empty", "Should not have empty categories");
            }
        }
    }

    public static class ActionsValidator
    implements QueryParamValidator {
        @Override
        public void validate(String fieldValue, ValidationResult validationResult) {
            String[] actionsParts = fieldValue.split(",\\s*");
            if (AuditRestValidator.hasEmptyValue(actionsParts)) {
                validationResult.add("validation.error.actions.empty", "Should not have empty actions");
            }
        }
    }
}


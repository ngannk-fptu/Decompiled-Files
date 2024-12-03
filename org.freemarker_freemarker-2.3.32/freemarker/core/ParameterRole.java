/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

final class ParameterRole {
    private final String name;
    static final ParameterRole UNKNOWN = new ParameterRole("[unknown role]");
    static final ParameterRole LEFT_HAND_OPERAND = new ParameterRole("left-hand operand");
    static final ParameterRole RIGHT_HAND_OPERAND = new ParameterRole("right-hand operand");
    static final ParameterRole ENCLOSED_OPERAND = new ParameterRole("enclosed operand");
    static final ParameterRole ITEM_VALUE = new ParameterRole("item value");
    static final ParameterRole ITEM_KEY = new ParameterRole("item key");
    static final ParameterRole ASSIGNMENT_TARGET = new ParameterRole("assignment target");
    static final ParameterRole ASSIGNMENT_OPERATOR = new ParameterRole("assignment operator");
    static final ParameterRole ASSIGNMENT_SOURCE = new ParameterRole("assignment source");
    static final ParameterRole VARIABLE_SCOPE = new ParameterRole("variable scope");
    static final ParameterRole NAMESPACE = new ParameterRole("namespace");
    static final ParameterRole ERROR_HANDLER = new ParameterRole("error handler");
    static final ParameterRole PASSED_VALUE = new ParameterRole("passed value");
    static final ParameterRole CONDITION = new ParameterRole("condition");
    static final ParameterRole VALUE = new ParameterRole("value");
    static final ParameterRole AST_NODE_SUBTYPE = new ParameterRole("AST-node subtype");
    static final ParameterRole PLACEHOLDER_VARIABLE = new ParameterRole("placeholder variable");
    static final ParameterRole EXPRESSION_TEMPLATE = new ParameterRole("expression template");
    static final ParameterRole LIST_SOURCE = new ParameterRole("list source");
    static final ParameterRole TARGET_LOOP_VARIABLE = new ParameterRole("target loop variable");
    static final ParameterRole TEMPLATE_NAME = new ParameterRole("template name");
    static final ParameterRole PARSE_PARAMETER = new ParameterRole("\"parse\" parameter");
    static final ParameterRole ENCODING_PARAMETER = new ParameterRole("\"encoding\" parameter");
    static final ParameterRole IGNORE_MISSING_PARAMETER = new ParameterRole("\"ignore_missing\" parameter");
    static final ParameterRole PARAMETER_NAME = new ParameterRole("parameter name");
    static final ParameterRole PARAMETER_DEFAULT = new ParameterRole("parameter default");
    static final ParameterRole CATCH_ALL_PARAMETER_NAME = new ParameterRole("catch-all parameter name");
    static final ParameterRole ARGUMENT_NAME = new ParameterRole("argument name");
    static final ParameterRole ARGUMENT_VALUE = new ParameterRole("argument value");
    static final ParameterRole CONTENT = new ParameterRole("content");
    static final ParameterRole EMBEDDED_TEMPLATE = new ParameterRole("embedded template");
    static final ParameterRole VALUE_PART = new ParameterRole("value part");
    static final ParameterRole MINIMUM_DECIMALS = new ParameterRole("minimum decimals");
    static final ParameterRole MAXIMUM_DECIMALS = new ParameterRole("maximum decimals");
    static final ParameterRole NODE = new ParameterRole("node");
    static final ParameterRole CALLEE = new ParameterRole("callee");
    static final ParameterRole MESSAGE = new ParameterRole("message");

    private ParameterRole(String name) {
        this.name = name;
    }

    static ParameterRole forBinaryOperatorOperand(int paramIndex) {
        switch (paramIndex) {
            case 0: {
                return LEFT_HAND_OPERAND;
            }
            case 1: {
                return RIGHT_HAND_OPERAND;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.FMParser;
import freemarker.core.LazilyGeneratedCollectionModel;
import freemarker.core.NestedContentNotSupportedException;
import freemarker.core.TemplateElement;
import freemarker.core.TemplatePostProcessorException;
import freemarker.core.TextBlock;
import freemarker.core.ThreadInterruptionSupportTemplatePostProcessor;
import freemarker.core._TemplateModelException;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.ClassUtil;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class _CoreAPI {
    public static final String ERROR_MESSAGE_HR = "----";
    public static final Set<String> ALL_BUILT_IN_DIRECTIVE_NAMES;
    public static final Set<String> LEGACY_BUILT_IN_DIRECTIVE_NAMES;
    public static final Set<String> CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES;

    private _CoreAPI() {
    }

    private static void addName(Set<String> allNames, Set<String> lcNames, Set<String> ccNames, String commonName) {
        allNames.add(commonName);
        lcNames.add(commonName);
        ccNames.add(commonName);
    }

    private static void addName(Set<String> allNames, Set<String> lcNames, Set<String> ccNames, String lcName, String ccName) {
        allNames.add(lcName);
        allNames.add(ccName);
        lcNames.add(lcName);
        ccNames.add(ccName);
    }

    public static Set<String> getSupportedBuiltInNames(int namingConvention) {
        Set<String> names;
        if (namingConvention == 10) {
            names = BuiltIn.BUILT_INS_BY_NAME.keySet();
        } else if (namingConvention == 11) {
            names = BuiltIn.SNAKE_CASE_NAMES;
        } else if (namingConvention == 12) {
            names = BuiltIn.CAMEL_CASE_NAMES;
        } else {
            throw new IllegalArgumentException("Unsupported naming convention constant: " + namingConvention);
        }
        return Collections.unmodifiableSet(names);
    }

    public static void appendInstructionStackItem(TemplateElement stackEl, StringBuilder sb) {
        Environment.appendInstructionStackItem(stackEl, sb);
    }

    public static TemplateElement[] getInstructionStackSnapshot(Environment env) {
        return env.getInstructionStackSnapshot();
    }

    public static void outputInstructionStack(TemplateElement[] instructionStackSnapshot, boolean terseMode, Writer pw) {
        Environment.outputInstructionStack(instructionStackSnapshot, terseMode, pw);
    }

    public static final void addThreadInterruptedChecks(Template template) {
        try {
            new ThreadInterruptionSupportTemplatePostProcessor().postProcess(template);
        }
        catch (TemplatePostProcessorException e) {
            throw new RuntimeException("Template post-processing failed", e);
        }
    }

    public static final void checkHasNoNestedContent(TemplateDirectiveBody body) throws NestedContentNotSupportedException {
        NestedContentNotSupportedException.check(body);
    }

    public static final void replaceText(TextBlock textBlock, String text) {
        textBlock.replaceText(text);
    }

    public static void checkSettingValueItemsType(String somethingsSentenceStart, Class<?> expectedClass, Collection<?> values) {
        if (values == null) {
            return;
        }
        for (Object value : values) {
            if (expectedClass.isInstance(value)) continue;
            throw new IllegalArgumentException(somethingsSentenceStart + " must be instances of " + ClassUtil.getShortClassName(expectedClass) + ", but one of them was a(n) " + ClassUtil.getShortClassNameOfObject(value) + ".");
        }
    }

    public static TemplateModelException ensureIsTemplateModelException(String modelOpMsg, TemplateException e) {
        if (e instanceof TemplateModelException) {
            return (TemplateModelException)e;
        }
        return new _TemplateModelException(_TemplateAPI.getBlamedExpression(e), e.getCause(), e.getEnvironment(), modelOpMsg);
    }

    public static TemplateElement getParentElement(TemplateElement te) {
        return te.getParentElement();
    }

    public static TemplateElement getChildElement(TemplateElement te, int index) {
        return te.getChild(index);
    }

    public static void setPreventStrippings(FMParser parser, boolean preventStrippings) {
        parser.setPreventStrippings(preventStrippings);
    }

    public static boolean isLazilyGeneratedSequenceModel(TemplateCollectionModel model) {
        return model instanceof LazilyGeneratedCollectionModel && ((LazilyGeneratedCollectionModel)model).isSequence();
    }

    static {
        TreeSet<String> allNames = new TreeSet<String>();
        TreeSet<String> lcNames = new TreeSet<String>();
        TreeSet<String> ccNames = new TreeSet<String>();
        _CoreAPI.addName(allNames, lcNames, ccNames, "assign");
        _CoreAPI.addName(allNames, lcNames, ccNames, "attempt");
        _CoreAPI.addName(allNames, lcNames, ccNames, "autoesc", "autoEsc");
        _CoreAPI.addName(allNames, lcNames, ccNames, "break");
        _CoreAPI.addName(allNames, lcNames, ccNames, "call");
        _CoreAPI.addName(allNames, lcNames, ccNames, "case");
        _CoreAPI.addName(allNames, lcNames, ccNames, "comment");
        _CoreAPI.addName(allNames, lcNames, ccNames, "compress");
        _CoreAPI.addName(allNames, lcNames, ccNames, "continue");
        _CoreAPI.addName(allNames, lcNames, ccNames, "default");
        _CoreAPI.addName(allNames, lcNames, ccNames, "else");
        _CoreAPI.addName(allNames, lcNames, ccNames, "elseif", "elseIf");
        _CoreAPI.addName(allNames, lcNames, ccNames, "escape");
        _CoreAPI.addName(allNames, lcNames, ccNames, "fallback");
        _CoreAPI.addName(allNames, lcNames, ccNames, "flush");
        _CoreAPI.addName(allNames, lcNames, ccNames, "foreach", "forEach");
        _CoreAPI.addName(allNames, lcNames, ccNames, "ftl");
        _CoreAPI.addName(allNames, lcNames, ccNames, "function");
        _CoreAPI.addName(allNames, lcNames, ccNames, "global");
        _CoreAPI.addName(allNames, lcNames, ccNames, "if");
        _CoreAPI.addName(allNames, lcNames, ccNames, "import");
        _CoreAPI.addName(allNames, lcNames, ccNames, "include");
        _CoreAPI.addName(allNames, lcNames, ccNames, "items");
        _CoreAPI.addName(allNames, lcNames, ccNames, "list");
        _CoreAPI.addName(allNames, lcNames, ccNames, "local");
        _CoreAPI.addName(allNames, lcNames, ccNames, "lt");
        _CoreAPI.addName(allNames, lcNames, ccNames, "macro");
        _CoreAPI.addName(allNames, lcNames, ccNames, "nested");
        _CoreAPI.addName(allNames, lcNames, ccNames, "noautoesc", "noAutoEsc");
        _CoreAPI.addName(allNames, lcNames, ccNames, "noescape", "noEscape");
        _CoreAPI.addName(allNames, lcNames, ccNames, "noparse", "noParse");
        _CoreAPI.addName(allNames, lcNames, ccNames, "nt");
        _CoreAPI.addName(allNames, lcNames, ccNames, "outputformat", "outputFormat");
        _CoreAPI.addName(allNames, lcNames, ccNames, "recover");
        _CoreAPI.addName(allNames, lcNames, ccNames, "recurse");
        _CoreAPI.addName(allNames, lcNames, ccNames, "return");
        _CoreAPI.addName(allNames, lcNames, ccNames, "rt");
        _CoreAPI.addName(allNames, lcNames, ccNames, "sep");
        _CoreAPI.addName(allNames, lcNames, ccNames, "setting");
        _CoreAPI.addName(allNames, lcNames, ccNames, "stop");
        _CoreAPI.addName(allNames, lcNames, ccNames, "switch");
        _CoreAPI.addName(allNames, lcNames, ccNames, "t");
        _CoreAPI.addName(allNames, lcNames, ccNames, "transform");
        _CoreAPI.addName(allNames, lcNames, ccNames, "visit");
        ALL_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(allNames);
        LEGACY_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(lcNames);
        CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(ccNames);
    }
}


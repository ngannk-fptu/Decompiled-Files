/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package freemarker.ext.servlet;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.core._ObjectBuilderSettingEvaluator;
import freemarker.core._SettingEvaluationEnvironment;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template._VersionInts;
import freemarker.template.utility.StringUtil;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;

final class InitParamParser {
    static final String TEMPLATE_PATH_PREFIX_CLASS = "class://";
    static final String TEMPLATE_PATH_PREFIX_CLASSPATH = "classpath:";
    static final String TEMPLATE_PATH_PREFIX_FILE = "file://";
    static final String TEMPLATE_PATH_SETTINGS_BI_NAME = "settings";
    private static final Logger LOG = Logger.getLogger("freemarker.servlet");

    private InitParamParser() {
    }

    static TemplateLoader createTemplateLoader(String templatePath, Configuration cfg, Class classLoaderClass, ServletContext srvCtx) throws IOException {
        TemplateLoader templateLoader;
        String packagePath;
        int settingAssignmentsStart = InitParamParser.findTemplatePathSettingAssignmentsStart(templatePath);
        String pureTemplatePath = (settingAssignmentsStart == -1 ? templatePath : templatePath.substring(0, settingAssignmentsStart)).trim();
        if (pureTemplatePath.startsWith(TEMPLATE_PATH_PREFIX_CLASS)) {
            packagePath = pureTemplatePath.substring(TEMPLATE_PATH_PREFIX_CLASS.length());
            packagePath = InitParamParser.normalizeToAbsolutePackagePath(packagePath);
            templateLoader = new ClassTemplateLoader(classLoaderClass, packagePath);
        } else if (pureTemplatePath.startsWith(TEMPLATE_PATH_PREFIX_CLASSPATH)) {
            packagePath = pureTemplatePath.substring(TEMPLATE_PATH_PREFIX_CLASSPATH.length());
            packagePath = InitParamParser.normalizeToAbsolutePackagePath(packagePath);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                LOG.warn("No Thread Context Class Loader was found. Falling back to the class loader of " + classLoaderClass.getName() + ".");
                classLoader = classLoaderClass.getClassLoader();
            }
            templateLoader = new ClassTemplateLoader(classLoader, packagePath);
        } else if (pureTemplatePath.startsWith(TEMPLATE_PATH_PREFIX_FILE)) {
            String filePath = pureTemplatePath.substring(TEMPLATE_PATH_PREFIX_FILE.length());
            templateLoader = new FileTemplateLoader(new File(filePath));
        } else if (pureTemplatePath.startsWith("[") && cfg.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_22) {
            if (!pureTemplatePath.endsWith("]")) {
                throw new TemplatePathParsingException("Failed to parse template path; closing \"]\" is missing.");
            }
            String commaSepItems = pureTemplatePath.substring(1, pureTemplatePath.length() - 1).trim();
            List listItems = InitParamParser.parseCommaSeparatedTemplatePaths(commaSepItems);
            TemplateLoader[] templateLoaders = new TemplateLoader[listItems.size()];
            for (int i = 0; i < listItems.size(); ++i) {
                String pathItem = (String)listItems.get(i);
                templateLoaders[i] = InitParamParser.createTemplateLoader(pathItem, cfg, classLoaderClass, srvCtx);
            }
            templateLoader = new MultiTemplateLoader(templateLoaders);
        } else {
            if (pureTemplatePath.startsWith("{") && cfg.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_22) {
                throw new TemplatePathParsingException("Template paths starting with \"{\" are reseved for future purposes");
            }
            templateLoader = new WebappTemplateLoader(srvCtx, pureTemplatePath);
        }
        if (settingAssignmentsStart != -1) {
            try {
                int nextPos = _ObjectBuilderSettingEvaluator.configureBean(templatePath, templatePath.indexOf(40, settingAssignmentsStart) + 1, templateLoader, _SettingEvaluationEnvironment.getCurrent());
                if (nextPos != templatePath.length()) {
                    throw new TemplatePathParsingException("Template path should end after the setting list in: " + templatePath);
                }
            }
            catch (Exception e) {
                throw new TemplatePathParsingException("Failed to set properties in: " + templatePath, e);
            }
        }
        return templateLoader;
    }

    static String normalizeToAbsolutePackagePath(String path) {
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return "/" + path;
    }

    static List parseCommaSeparatedList(String value) throws ParseException {
        ArrayList<String> valuesList = new ArrayList<String>();
        String[] values = StringUtil.split(value, ',');
        for (int i = 0; i < values.length; ++i) {
            String s = values[i].trim();
            if (s.length() != 0) {
                valuesList.add(s);
                continue;
            }
            if (i == values.length - 1) continue;
            throw new ParseException("Missing list item berfore a comma", -1);
        }
        return valuesList;
    }

    static List parseCommaSeparatedPatterns(String value) throws ParseException {
        List values = InitParamParser.parseCommaSeparatedList(value);
        ArrayList<Pattern> patterns = new ArrayList<Pattern>(values.size());
        for (int i = 0; i < values.size(); ++i) {
            patterns.add(Pattern.compile((String)values.get(i)));
        }
        return patterns;
    }

    static List parseCommaSeparatedTemplatePaths(String commaSepItems) {
        ArrayList<String> listItems = new ArrayList<String>();
        while (commaSepItems.length() != 0) {
            int itemSettingAssignmentsStart = InitParamParser.findTemplatePathSettingAssignmentsStart(commaSepItems);
            int pureItemEnd = itemSettingAssignmentsStart != -1 ? itemSettingAssignmentsStart : commaSepItems.length();
            int prevComaIdx = commaSepItems.lastIndexOf(44, pureItemEnd - 1);
            int itemStart = prevComaIdx != -1 ? prevComaIdx + 1 : 0;
            String item = commaSepItems.substring(itemStart).trim();
            if (item.length() != 0) {
                listItems.add(0, item);
            } else if (listItems.size() > 0) {
                throw new TemplatePathParsingException("Missing list item before a comma");
            }
            commaSepItems = prevComaIdx != -1 ? commaSepItems.substring(0, prevComaIdx).trim() : "";
        }
        return listItems;
    }

    static int findTemplatePathSettingAssignmentsStart(String s) {
        int pos;
        for (pos = s.length() - 1; pos >= 0 && Character.isWhitespace(s.charAt(pos)); --pos) {
        }
        if (pos < 0 || s.charAt(pos) != ')') {
            return -1;
        }
        --pos;
        int parLevel = 1;
        int mode = 0;
        while (parLevel > 0) {
            if (pos < 0) {
                return -1;
            }
            char c = s.charAt(pos);
            switch (mode) {
                case 0: {
                    switch (c) {
                        case '(': {
                            --parLevel;
                            break;
                        }
                        case ')': {
                            ++parLevel;
                            break;
                        }
                        case '\'': {
                            mode = 1;
                            break;
                        }
                        case '\"': {
                            mode = 2;
                        }
                    }
                    break;
                }
                case 1: {
                    if (c != '\'' || pos > 0 && s.charAt(pos - 1) == '\\') break;
                    mode = 0;
                    break;
                }
                case 2: {
                    if (c != '\"' || pos > 0 && s.charAt(pos - 1) == '\\') break;
                    mode = 0;
                }
            }
            --pos;
        }
        while (pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
            --pos;
        }
        int biNameEnd = pos + 1;
        while (pos >= 0 && Character.isJavaIdentifierPart(s.charAt(pos))) {
            --pos;
        }
        int biNameStart = pos + 1;
        if (biNameStart == biNameEnd) {
            return -1;
        }
        String biName = s.substring(biNameStart, biNameEnd);
        while (pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
            --pos;
        }
        if (pos < 0 || s.charAt(pos) != '?') {
            return -1;
        }
        if (!biName.equals(TEMPLATE_PATH_SETTINGS_BI_NAME)) {
            throw new TemplatePathParsingException(StringUtil.jQuote(biName) + " is unexpected after the \"?\". Expected \"" + TEMPLATE_PATH_SETTINGS_BI_NAME + "\".");
        }
        return pos;
    }

    private static final class TemplatePathParsingException
    extends RuntimeException {
        public TemplatePathParsingException(String message, Throwable cause) {
            super(message, cause);
        }

        public TemplatePathParsingException(String message) {
            super(message);
        }
    }
}


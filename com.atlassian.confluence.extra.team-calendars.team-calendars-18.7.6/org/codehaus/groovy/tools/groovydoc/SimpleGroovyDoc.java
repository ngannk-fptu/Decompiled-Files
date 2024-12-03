/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.groovydoc.GroovyDoc;
import org.codehaus.groovy.groovydoc.GroovyTag;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyTag;

public class SimpleGroovyDoc
implements GroovyDoc,
GroovyTokenTypes {
    private static final Pattern TAG2_PATTERN = Pattern.compile("(?s)([a-z]+)\\s+(.*)");
    private static final Pattern TAG3_PATTERN = Pattern.compile("(?s)([a-z]+)\\s+(\\S*)\\s+(.*)");
    private String name;
    private String commentText = null;
    private String rawCommentText = "";
    private String firstSentenceCommentText = null;
    private int definitionType;
    private boolean deprecated;
    private boolean isScript;
    private GroovyTag[] tags;

    public SimpleGroovyDoc(String name) {
        this.name = name;
        this.definitionType = 13;
    }

    @Override
    public String name() {
        return this.name;
    }

    public String toString() {
        return "" + this.getClass() + "(" + this.name + ")";
    }

    protected void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    protected void setFirstSentenceCommentText(String firstSentenceCommentText) {
        this.firstSentenceCommentText = firstSentenceCommentText;
    }

    @Override
    public String commentText() {
        return this.commentText;
    }

    @Override
    public String firstSentenceCommentText() {
        return this.firstSentenceCommentText;
    }

    @Override
    public String getRawCommentText() {
        return this.rawCommentText;
    }

    @Override
    public void setRawCommentText(String rawCommentText) {
        this.rawCommentText = rawCommentText;
        this.calculateTags(rawCommentText);
    }

    public void setScript(boolean script) {
        this.isScript = script;
    }

    private void calculateTags(String rawCommentText) {
        String trimmed = rawCommentText.replaceFirst("(?s).*?\\*\\s*@", "@");
        if (trimmed.equals(rawCommentText)) {
            return;
        }
        String cleaned = trimmed.replaceAll("(?m)^\\s*\\*\\s*([^*]*)$", "$1").trim();
        String[] split = cleaned.split("(?m)^@");
        ArrayList<SimpleGroovyTag> result = new ArrayList<SimpleGroovyTag>();
        for (String s : split) {
            Matcher m;
            String tagname = null;
            if (s.startsWith("param") || s.startsWith("throws")) {
                m = TAG3_PATTERN.matcher(s);
                if (m.find()) {
                    tagname = m.group(1);
                    result.add(new SimpleGroovyTag(tagname, m.group(2), m.group(3)));
                }
            } else {
                m = TAG2_PATTERN.matcher(s);
                if (m.find()) {
                    tagname = m.group(1);
                    result.add(new SimpleGroovyTag(tagname, null, m.group(2)));
                }
            }
            if (!"deprecated".equals(tagname)) continue;
            this.setDeprecated(true);
        }
        this.tags = result.toArray(new GroovyTag[result.size()]);
    }

    public static String calculateFirstSentence(String raw) {
        String text = raw.replaceAll("(?m)^\\s*\\*", "").trim();
        text = text.replaceFirst("(?ms)<p>.*", "").trim();
        text = text.replaceFirst("(?ms)\\n\\s*\\n.*", "").trim();
        text = text.replaceFirst("(?ms)\\n\\s*@(see|param|throws|return|author|since|exception|version|deprecated|todo)\\s.*", "").trim();
        BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.getDefault());
        boundary.setText(text);
        int start = boundary.first();
        int end = boundary.next();
        if (start > -1 && end > -1) {
            text = text.substring(start, end);
        }
        return text;
    }

    @Override
    public boolean isClass() {
        return this.definitionType == 13 && !this.isScript;
    }

    public boolean isScript() {
        return this.definitionType == 13 && this.isScript;
    }

    public boolean isTrait() {
        return this.definitionType == 15;
    }

    @Override
    public boolean isInterface() {
        return this.definitionType == 14;
    }

    @Override
    public boolean isAnnotationType() {
        return this.definitionType == 64;
    }

    @Override
    public boolean isEnum() {
        return this.definitionType == 61;
    }

    public String getTypeDescription() {
        if (this.isInterface()) {
            return "Interface";
        }
        if (this.isTrait()) {
            return "Trait";
        }
        if (this.isAnnotationType()) {
            return "Annotation Type";
        }
        if (this.isEnum()) {
            return "Enum";
        }
        if (this.isScript()) {
            return "Script";
        }
        return "Class";
    }

    public String getTypeSourceDescription() {
        if (this.isInterface()) {
            return "interface";
        }
        if (this.isTrait()) {
            return "trait";
        }
        if (this.isAnnotationType()) {
            return "@interface";
        }
        if (this.isEnum()) {
            return "enum";
        }
        return "class";
    }

    public void setTokenType(int t) {
        this.definitionType = t;
    }

    public int tokenType() {
        return this.definitionType;
    }

    public int compareTo(Object that) {
        if (that instanceof GroovyDoc) {
            return this.name.compareTo(((GroovyDoc)that).name());
        }
        throw new ClassCastException(String.format("Cannot compare object of type %s.", that.getClass()));
    }

    @Override
    public boolean isAnnotationTypeElement() {
        return false;
    }

    @Override
    public boolean isConstructor() {
        return false;
    }

    @Override
    public boolean isEnumConstant() {
        return false;
    }

    @Override
    public boolean isDeprecated() {
        return this.deprecated;
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isException() {
        return false;
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public boolean isIncluded() {
        return false;
    }

    @Override
    public boolean isMethod() {
        return false;
    }

    @Override
    public boolean isOrdinaryClass() {
        return false;
    }

    public GroovyTag[] tags() {
        return this.tags;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
}


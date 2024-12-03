/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.lang.EmptyRange;
import groovy.lang.GString;
import groovy.lang.IntRange;
import groovy.lang.Range;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import groovy.transform.stc.SimpleType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.RangeInfo;
import org.codehaus.groovy.runtime.RegexSupport;
import org.codehaus.groovy.runtime.StringBufferWriter;
import org.codehaus.groovy.runtime.callsite.BooleanClosureWrapper;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.util.CharSequenceReader;

public class StringGroovyMethods
extends DefaultGroovyMethodsSupport {
    static String lineSeparator = null;

    public static boolean asBoolean(CharSequence string) {
        if (null == string) {
            return false;
        }
        return string.length() > 0;
    }

    public static boolean asBoolean(Matcher matcher) {
        if (null == matcher) {
            return false;
        }
        RegexSupport.setLastMatcher(matcher);
        return matcher.find();
    }

    public static <T> T asType(CharSequence self, Class<T> c) {
        return StringGroovyMethods.asType(self.toString(), c);
    }

    public static <T> T asType(GString self, Class<T> c) {
        if (c == File.class) {
            return (T)new File(self.toString());
        }
        if (Number.class.isAssignableFrom(c) || c.isPrimitive()) {
            return StringGroovyMethods.asType(self.toString(), c);
        }
        return DefaultGroovyMethods.asType((Object)self, c);
    }

    public static <T> T asType(String self, Class<T> c) {
        if (c == List.class) {
            return (T)StringGroovyMethods.toList((CharSequence)self);
        }
        if (c == BigDecimal.class) {
            return (T)StringGroovyMethods.toBigDecimal((CharSequence)self);
        }
        if (c == BigInteger.class) {
            return (T)StringGroovyMethods.toBigInteger((CharSequence)self);
        }
        if (c == Long.class || c == Long.TYPE) {
            return (T)StringGroovyMethods.toLong((CharSequence)self);
        }
        if (c == Integer.class || c == Integer.TYPE) {
            return (T)StringGroovyMethods.toInteger((CharSequence)self);
        }
        if (c == Short.class || c == Short.TYPE) {
            return (T)StringGroovyMethods.toShort((CharSequence)self);
        }
        if (c == Byte.class || c == Byte.TYPE) {
            return (T)Byte.valueOf(self.trim());
        }
        if (c == Character.class || c == Character.TYPE) {
            return (T)StringGroovyMethods.toCharacter(self);
        }
        if (c == Double.class || c == Double.TYPE) {
            return (T)StringGroovyMethods.toDouble((CharSequence)self);
        }
        if (c == Float.class || c == Float.TYPE) {
            return (T)StringGroovyMethods.toFloat((CharSequence)self);
        }
        if (c == File.class) {
            return (T)new File(self);
        }
        if (c.isEnum()) {
            return (T)InvokerHelper.invokeMethod(c, "valueOf", new Object[]{self});
        }
        return DefaultGroovyMethods.asType((Object)self, c);
    }

    public static Pattern bitwiseNegate(CharSequence self) {
        return Pattern.compile(self.toString());
    }

    @Deprecated
    public static Pattern bitwiseNegate(String self) {
        return StringGroovyMethods.bitwiseNegate((CharSequence)self);
    }

    public static String uncapitalize(CharSequence self) {
        String s = self.toString();
        if (s == null || s.length() == 0) {
            return s;
        }
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String capitalize(CharSequence self) {
        if (self.length() == 0) {
            return "";
        }
        return "" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }

    @Deprecated
    public static String capitalize(String self) {
        return StringGroovyMethods.capitalize((CharSequence)self);
    }

    public static String center(CharSequence self, Number numberOfChars) {
        return StringGroovyMethods.center(self, numberOfChars, (CharSequence)" ");
    }

    public static String center(CharSequence self, Number numberOfChars, CharSequence padding) {
        String semiPad;
        int numChars = numberOfChars.intValue();
        if (numChars <= self.length()) {
            return self.toString();
        }
        int charsToAdd = numChars - self.length();
        String string = semiPad = charsToAdd % 2 == 1 ? StringGroovyMethods.getPadding(padding, charsToAdd / 2 + 1) : StringGroovyMethods.getPadding(padding, charsToAdd / 2);
        if (charsToAdd % 2 == 0) {
            return semiPad + self + semiPad;
        }
        return semiPad.substring(0, charsToAdd / 2) + self + semiPad;
    }

    @Deprecated
    public static String center(String self, Number numberOfChars) {
        return StringGroovyMethods.center((CharSequence)self, numberOfChars);
    }

    @Deprecated
    public static String center(String self, Number numberOfChars, String padding) {
        return StringGroovyMethods.center((CharSequence)self, numberOfChars, (CharSequence)padding);
    }

    public static boolean contains(CharSequence self, CharSequence text) {
        int idx = self.toString().indexOf(text.toString());
        return idx >= 0;
    }

    @Deprecated
    public static boolean contains(String self, String text) {
        return StringGroovyMethods.contains((CharSequence)self, (CharSequence)text);
    }

    public static int count(CharSequence self, CharSequence text) {
        int answer = 0;
        int idx = 0;
        while ((idx = self.toString().indexOf(text.toString(), idx)) >= answer) {
            ++answer;
            ++idx;
        }
        return answer;
    }

    @Deprecated
    public static int count(String self, String text) {
        return StringGroovyMethods.count((CharSequence)self, (CharSequence)text);
    }

    private static StringBufferWriter createStringBufferWriter(StringBuffer self) {
        return new StringBufferWriter(self);
    }

    private static StringWriter createStringWriter(String self) {
        StringWriter answer = new StringWriter();
        answer.write(self);
        return answer;
    }

    public static String denormalize(CharSequence self) {
        CharSequence cs;
        int len;
        if (lineSeparator == null) {
            StringWriter sw = new StringWriter(2);
            try {
                BufferedWriter bw = new BufferedWriter(sw);
                bw.newLine();
                bw.flush();
                lineSeparator = sw.toString();
            }
            catch (IOException ioe) {
                lineSeparator = "\n";
            }
        }
        if ((len = self.length()) < 1) {
            return self.toString();
        }
        StringBuilder sb = new StringBuilder(110 * len / 100);
        int i = 0;
        CharSequence charSequence = cs = self instanceof GString ? self.toString() : self;
        block6: while (i < len) {
            char ch = cs.charAt(i++);
            switch (ch) {
                case '\r': {
                    sb.append(lineSeparator);
                    if (i >= len || cs.charAt(i) != '\n') continue block6;
                    ++i;
                    continue block6;
                }
                case '\n': {
                    sb.append(lineSeparator);
                    continue block6;
                }
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    @Deprecated
    public static String denormalize(String self) {
        return StringGroovyMethods.denormalize((CharSequence)self);
    }

    public static CharSequence drop(CharSequence self, int num) {
        if (num <= 0) {
            return self;
        }
        if (self.length() <= num) {
            return self.subSequence(0, 0);
        }
        return self.subSequence(num, self.length());
    }

    public static String drop(GString self, int num) {
        return StringGroovyMethods.drop(self.toString(), num).toString();
    }

    public static CharSequence dropWhile(CharSequence self, @ClosureParams(value=SimpleType.class, options={"char"}) Closure condition) {
        int num;
        BooleanClosureWrapper bcw = new BooleanClosureWrapper(condition);
        for (num = 0; num < self.length(); ++num) {
            char value = self.charAt(num);
            if (!bcw.call(Character.valueOf(value))) break;
        }
        return StringGroovyMethods.drop(self, num);
    }

    public static String dropWhile(GString self, @ClosureParams(value=SimpleType.class, options={"char"}) Closure condition) {
        return StringGroovyMethods.dropWhile(self.toString(), condition).toString();
    }

    public static <T> T eachLine(CharSequence self, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return StringGroovyMethods.eachLine(self.toString(), 0, closure);
    }

    public static <T> T eachLine(CharSequence self, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        int count = firstLine;
        T result = null;
        for (String line : StringGroovyMethods.readLines(self.toString())) {
            result = DefaultGroovyMethods.callClosureForLine(closure, line, count);
            ++count;
        }
        return result;
    }

    @Deprecated
    public static <T> T eachLine(String self, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return StringGroovyMethods.eachLine((CharSequence)self, closure);
    }

    @Deprecated
    public static <T> T eachLine(String self, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return StringGroovyMethods.eachLine((CharSequence)self, firstLine, closure);
    }

    public static String collectReplacements(String orig, @ClosureParams(value=SimpleType.class, options={"char"}) Closure<String> transform) {
        if (orig == null) {
            return orig;
        }
        StringBuilder sb = null;
        int len = orig.length();
        for (int i = 0; i < len; ++i) {
            char ch = orig.charAt(i);
            String replacement = transform.call((Object)Character.valueOf(ch));
            if (replacement != null) {
                if (sb == null) {
                    sb = new StringBuilder((int)(1.1 * (double)len));
                    sb.append(orig.substring(0, i));
                }
                sb.append(replacement);
                continue;
            }
            if (sb == null) continue;
            sb.append(ch);
        }
        return sb == null ? orig : sb.toString();
    }

    public static <T extends CharSequence> T eachMatch(T self, CharSequence regex, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        StringGroovyMethods.eachMatch(self.toString(), regex.toString(), closure);
        return self;
    }

    public static <T extends CharSequence> T eachMatch(T self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        StringGroovyMethods.eachMatch(self.toString(), pattern, closure);
        return self;
    }

    public static String eachMatch(String self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        Matcher m = pattern.matcher(self);
        DefaultGroovyMethods.each(m, closure);
        return self;
    }

    public static String eachMatch(String self, String regex, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        return StringGroovyMethods.eachMatch(self, Pattern.compile(regex), closure);
    }

    public static String expand(CharSequence self) {
        return StringGroovyMethods.expand(self, 8);
    }

    public static String expand(CharSequence self, int tabStop) {
        if (self.length() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String line : new LineIterable(self)) {
            builder.append(StringGroovyMethods.expandLine((CharSequence)line, tabStop));
            builder.append("\n");
        }
        if (self.charAt(self.length() - 1) != '\n') {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    @Deprecated
    public static String expand(String self) {
        return StringGroovyMethods.expand((CharSequence)self);
    }

    @Deprecated
    public static String expand(String self, int tabStop) {
        return StringGroovyMethods.expand((CharSequence)self, tabStop);
    }

    public static String expandLine(CharSequence self, int tabStop) {
        int index;
        String s = self.toString();
        while ((index = s.indexOf(9)) != -1) {
            StringBuilder builder = new StringBuilder(s);
            int count = tabStop - index % tabStop;
            builder.deleteCharAt(index);
            for (int i = 0; i < count; ++i) {
                builder.insert(index, " ");
            }
            s = builder.toString();
        }
        return s;
    }

    @Deprecated
    public static String expandLine(String self, int tabStop) {
        return StringGroovyMethods.expandLine((CharSequence)self, tabStop);
    }

    public static String find(CharSequence self, CharSequence regex) {
        return StringGroovyMethods.find(self, Pattern.compile(regex.toString()));
    }

    public static String find(CharSequence self, CharSequence regex, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure closure) {
        return StringGroovyMethods.find(self, Pattern.compile(regex.toString()), closure);
    }

    public static String find(CharSequence self, Pattern pattern) {
        Matcher matcher = pattern.matcher(self.toString());
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public static String find(CharSequence self, Pattern pattern, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure closure) {
        Matcher matcher = pattern.matcher(self.toString());
        if (matcher.find()) {
            if (StringGroovyMethods.hasGroup(matcher)) {
                int count = matcher.groupCount();
                ArrayList<String> groups = new ArrayList<String>(count);
                for (int i = 0; i <= count; ++i) {
                    groups.add(matcher.group(i));
                }
                return InvokerHelper.toString(closure.call((Object)groups));
            }
            return InvokerHelper.toString(closure.call((Object)matcher.group(0)));
        }
        return null;
    }

    @Deprecated
    public static String find(String self, Pattern pattern) {
        return StringGroovyMethods.find((CharSequence)self, pattern);
    }

    @Deprecated
    public static String find(String self, Pattern pattern, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure closure) {
        return StringGroovyMethods.find((CharSequence)self, pattern, closure);
    }

    @Deprecated
    public static String find(String self, String regex) {
        return StringGroovyMethods.find((CharSequence)self, (CharSequence)regex);
    }

    @Deprecated
    public static String find(String self, String regex, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure closure) {
        return StringGroovyMethods.find((CharSequence)self, (CharSequence)regex, closure);
    }

    public static List<String> findAll(CharSequence self, CharSequence regex) {
        return StringGroovyMethods.findAll(self, Pattern.compile(regex.toString()));
    }

    public static <T> List<T> findAll(CharSequence self, CharSequence regex, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) {
        return StringGroovyMethods.findAll(self, Pattern.compile(regex.toString()), closure);
    }

    public static List<String> findAll(CharSequence self, Pattern pattern) {
        Matcher matcher = pattern.matcher(self.toString());
        boolean hasGroup = StringGroovyMethods.hasGroup(matcher);
        ArrayList<String> list = new ArrayList<String>();
        Iterator iter = StringGroovyMethods.iterator(matcher);
        while (iter.hasNext()) {
            if (hasGroup) {
                list.add((String)((List)iter.next()).get(0));
                continue;
            }
            list.add((String)iter.next());
        }
        return new ArrayList<String>(list);
    }

    public static <T> List<T> findAll(CharSequence self, Pattern pattern, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) {
        Matcher matcher = pattern.matcher(self.toString());
        return DefaultGroovyMethods.collect(matcher, closure);
    }

    @Deprecated
    public static List<String> findAll(String self, Pattern pattern) {
        return StringGroovyMethods.findAll((CharSequence)self, pattern);
    }

    @Deprecated
    public static <T> List<T> findAll(String self, Pattern pattern, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) {
        return StringGroovyMethods.findAll((CharSequence)self, pattern, closure);
    }

    @Deprecated
    public static List<String> findAll(String self, String regex) {
        return StringGroovyMethods.findAll((CharSequence)self, (CharSequence)regex);
    }

    @Deprecated
    public static <T> List<T> findAll(String self, String regex, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) {
        return StringGroovyMethods.findAll((CharSequence)self, (CharSequence)regex, closure);
    }

    private static int findMinimumLeadingSpaces(String line, int count) {
        int index;
        int length = line.length();
        for (index = 0; index < length && index < count && Character.isWhitespace(line.charAt(index)); ++index) {
        }
        return index;
    }

    public static String getAt(CharSequence self, Collection indices) {
        StringBuilder answer = new StringBuilder();
        for (Object value : indices) {
            if (value instanceof Range) {
                answer.append(StringGroovyMethods.getAt(self, (Range)value));
                continue;
            }
            if (value instanceof Collection) {
                answer.append(StringGroovyMethods.getAt(self, (Collection)value));
                continue;
            }
            int idx = DefaultTypeTransformation.intUnbox(value);
            answer.append(StringGroovyMethods.getAt(self, idx));
        }
        return answer.toString();
    }

    public static String getAt(CharSequence text, EmptyRange range) {
        return "";
    }

    public static CharSequence getAt(CharSequence text, int index) {
        index = StringGroovyMethods.normaliseIndex(index, text.length());
        return text.subSequence(index, index + 1);
    }

    public static String getAt(GString text, int index) {
        return StringGroovyMethods.getAt(text.toString(), index);
    }

    public static CharSequence getAt(CharSequence text, IntRange range) {
        return StringGroovyMethods.getAt(text, (Range)range);
    }

    public static String getAt(GString text, IntRange range) {
        return StringGroovyMethods.getAt(text, (Range)range);
    }

    public static CharSequence getAt(CharSequence text, Range range) {
        RangeInfo info = StringGroovyMethods.subListBorders(text.length(), range);
        CharSequence sequence = text.subSequence(info.from, info.to);
        return info.reverse ? StringGroovyMethods.reverse(sequence) : sequence;
    }

    public static String getAt(GString text, Range range) {
        return StringGroovyMethods.getAt(text.toString(), range);
    }

    public static List getAt(Matcher self, Collection indices) {
        ArrayList<Object> result = new ArrayList<Object>();
        if (indices instanceof IntRange) {
            int size = (int)StringGroovyMethods.size(self);
            RangeInfo info = StringGroovyMethods.subListBorders(size, (Range)indices);
            indices = new IntRange(((IntRange)indices).getInclusive(), info.from, info.to - 1);
        }
        for (Object value : indices) {
            if (value instanceof Range) {
                result.addAll(StringGroovyMethods.getAt(self, (Collection)((Range)value)));
                continue;
            }
            int idx = DefaultTypeTransformation.intUnbox(value);
            result.add(StringGroovyMethods.getAt(self, idx));
        }
        return result;
    }

    public static Object getAt(Matcher matcher, int idx) {
        try {
            int count = StringGroovyMethods.getCount(matcher);
            if (idx < -count || idx >= count) {
                throw new IndexOutOfBoundsException("index is out of range " + -count + ".." + (count - 1) + " (index = " + idx + ")");
            }
            idx = StringGroovyMethods.normaliseIndex(idx, count);
            Iterator iter = StringGroovyMethods.iterator(matcher);
            Object result = null;
            for (int i = 0; i <= idx; ++i) {
                result = iter.next();
            }
            return result;
        }
        catch (IllegalStateException ex) {
            return null;
        }
    }

    public static boolean matchesPartially(Matcher matcher) {
        return matcher.matches() || matcher.hitEnd();
    }

    @Deprecated
    public static String getAt(String self, Collection indices) {
        return StringGroovyMethods.getAt((CharSequence)self, indices);
    }

    @Deprecated
    public static String getAt(String text, EmptyRange range) {
        return StringGroovyMethods.getAt((CharSequence)text, range);
    }

    public static String getAt(String text, int index) {
        index = StringGroovyMethods.normaliseIndex(index, text.length());
        return text.substring(index, index + 1);
    }

    public static String getAt(String text, IntRange range) {
        return StringGroovyMethods.getAt(text, (Range)range);
    }

    public static String getAt(String text, Range range) {
        RangeInfo info = StringGroovyMethods.subListBorders(text.length(), range);
        String answer = text.substring(info.from, info.to);
        if (info.reverse) {
            answer = StringGroovyMethods.reverse((CharSequence)answer);
        }
        return answer;
    }

    public static char[] getChars(CharSequence self) {
        return self.toString().toCharArray();
    }

    @Deprecated
    public static char[] getChars(String self) {
        return StringGroovyMethods.getChars((CharSequence)self);
    }

    public static int getCount(Matcher matcher) {
        int counter = 0;
        matcher.reset();
        while (matcher.find()) {
            ++counter;
        }
        return counter;
    }

    private static String getPadding(CharSequence padding, int length) {
        if (padding.length() < length) {
            return StringGroovyMethods.multiply(padding, (Number)(length / padding.length() + 1)).substring(0, length);
        }
        return "" + padding.subSequence(0, length);
    }

    private static String getReplacement(Matcher matcher, Closure closure) {
        if (!StringGroovyMethods.hasGroup(matcher)) {
            return InvokerHelper.toString(closure.call((Object)matcher.group()));
        }
        int count = matcher.groupCount();
        ArrayList<String> groups = new ArrayList<String>();
        for (int i = 0; i <= count; ++i) {
            groups.add(matcher.group(i));
        }
        if (closure.getParameterTypes().length == 1 && closure.getParameterTypes()[0] == Object[].class) {
            return InvokerHelper.toString(closure.call(groups.toArray()));
        }
        return InvokerHelper.toString(closure.call((Object)groups));
    }

    public static boolean hasGroup(Matcher matcher) {
        return matcher.groupCount() > 0;
    }

    public static boolean isAllWhitespace(CharSequence self) {
        for (int i = 0; i < self.length(); ++i) {
            if (Character.isWhitespace(self.charAt(i))) continue;
            return false;
        }
        return true;
    }

    @Deprecated
    public static boolean isAllWhitespace(String self) {
        return StringGroovyMethods.isAllWhitespace((CharSequence)self);
    }

    public static boolean isBigDecimal(CharSequence self) {
        try {
            new BigDecimal(self.toString().trim());
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Deprecated
    public static boolean isBigDecimal(String self) {
        return StringGroovyMethods.isBigDecimal((CharSequence)self);
    }

    public static boolean isBigInteger(CharSequence self) {
        try {
            new BigInteger(self.toString().trim());
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Deprecated
    public static boolean isBigInteger(String self) {
        return StringGroovyMethods.isBigInteger((CharSequence)self);
    }

    public static boolean isCase(CharSequence caseValue, Object switchValue) {
        if (switchValue == null) {
            return caseValue == null;
        }
        return caseValue.toString().equals(switchValue.toString());
    }

    @Deprecated
    public static boolean isCase(GString caseValue, Object switchValue) {
        return StringGroovyMethods.isCase((CharSequence)caseValue, switchValue);
    }

    public static boolean isCase(Pattern caseValue, Object switchValue) {
        if (switchValue == null) {
            return caseValue == null;
        }
        Matcher matcher = caseValue.matcher(switchValue.toString());
        if (matcher.matches()) {
            RegexSupport.setLastMatcher(matcher);
            return true;
        }
        return false;
    }

    @Deprecated
    public static boolean isCase(String caseValue, Object switchValue) {
        return StringGroovyMethods.isCase((CharSequence)caseValue, switchValue);
    }

    public static boolean isDouble(CharSequence self) {
        try {
            Double.valueOf(self.toString().trim());
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Deprecated
    public static boolean isDouble(String self) {
        return StringGroovyMethods.isDouble((CharSequence)self);
    }

    public static boolean isFloat(CharSequence self) {
        try {
            Float.valueOf(self.toString().trim());
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Deprecated
    public static boolean isFloat(String self) {
        return StringGroovyMethods.isFloat((CharSequence)self);
    }

    public static boolean isInteger(CharSequence self) {
        try {
            Integer.valueOf(self.toString().trim());
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Deprecated
    public static boolean isInteger(String self) {
        return StringGroovyMethods.isInteger((CharSequence)self);
    }

    public static boolean isLong(CharSequence self) {
        try {
            Long.valueOf(self.toString().trim());
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Deprecated
    public static boolean isLong(String self) {
        return StringGroovyMethods.isLong((CharSequence)self);
    }

    public static boolean isNumber(CharSequence self) {
        return StringGroovyMethods.isBigDecimal(self);
    }

    @Deprecated
    public static boolean isNumber(String self) {
        return StringGroovyMethods.isNumber((CharSequence)self);
    }

    public static Iterator iterator(final Matcher matcher) {
        matcher.reset();
        return new Iterator(){
            private boolean found;
            private boolean done;

            @Override
            public boolean hasNext() {
                if (this.done) {
                    return false;
                }
                if (!this.found) {
                    this.found = matcher.find();
                    if (!this.found) {
                        this.done = true;
                    }
                }
                return this.found;
            }

            public Object next() {
                if (!this.found && !this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.found = false;
                if (StringGroovyMethods.hasGroup(matcher)) {
                    ArrayList<String> list = new ArrayList<String>(matcher.groupCount());
                    for (int i = 0; i <= matcher.groupCount(); ++i) {
                        list.add(matcher.group(i));
                    }
                    return list;
                }
                return matcher.group();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static StringBuilder leftShift(CharSequence self, Object value) {
        return new StringBuilder(self).append(value);
    }

    public static StringBuffer leftShift(String self, Object value) {
        return new StringBuffer(self).append(value);
    }

    public static StringBuffer leftShift(StringBuffer self, Object value) {
        self.append(value);
        return self;
    }

    public static StringBuilder leftShift(StringBuilder self, Object value) {
        self.append(value);
        return self;
    }

    public static boolean matches(CharSequence self, Pattern pattern) {
        return pattern.matcher(self).matches();
    }

    @Deprecated
    public static boolean matches(String self, Pattern pattern) {
        return StringGroovyMethods.matches((CharSequence)self, pattern);
    }

    public static String minus(CharSequence self, Object target) {
        String text;
        String s = self.toString();
        int index = s.indexOf(text = DefaultGroovyMethods.toString(target));
        if (index == -1) {
            return s;
        }
        int end = index + text.length();
        if (s.length() > end) {
            return s.substring(0, index) + s.substring(end);
        }
        return s.substring(0, index);
    }

    public static String minus(CharSequence self, Pattern pattern) {
        return pattern.matcher(self).replaceFirst("");
    }

    @Deprecated
    public static String minus(String self, Pattern pattern) {
        return StringGroovyMethods.minus((CharSequence)self, pattern);
    }

    @Deprecated
    public static String minus(String self, Object target) {
        return StringGroovyMethods.minus((CharSequence)self, target);
    }

    public static String multiply(CharSequence self, Number factor) {
        int size = factor.intValue();
        if (size == 0) {
            return "";
        }
        if (size < 0) {
            throw new IllegalArgumentException("multiply() should be called with a number of 0 or greater not: " + size);
        }
        StringBuilder answer = new StringBuilder(self);
        for (int i = 1; i < size; ++i) {
            answer.append(self);
        }
        return answer.toString();
    }

    @Deprecated
    public static String multiply(String self, Number factor) {
        return StringGroovyMethods.multiply((CharSequence)self, factor);
    }

    public static String next(CharSequence self) {
        StringBuilder buffer = new StringBuilder(self);
        if (buffer.length() == 0) {
            buffer.append('\u0000');
        } else {
            char last = buffer.charAt(buffer.length() - 1);
            if (last == '\uffff') {
                buffer.append('\u0000');
            } else {
                char next = last;
                next = (char)(next + '\u0001');
                buffer.setCharAt(buffer.length() - 1, next);
            }
        }
        return buffer.toString();
    }

    @Deprecated
    public static String next(String self) {
        return StringGroovyMethods.next((CharSequence)self);
    }

    public static String normalize(CharSequence self) {
        String s = self.toString();
        int nx = s.indexOf(13);
        if (nx < 0) {
            return s;
        }
        int len = s.length();
        StringBuilder sb = new StringBuilder(len);
        int i = 0;
        do {
            sb.append(s, i, nx);
            sb.append('\n');
        } while ((i = nx + 1) < len && (s.charAt(i) != '\n' || ++i < len) && (nx = s.indexOf(13, i)) > 0);
        sb.append(s, i, len);
        return sb.toString();
    }

    @Deprecated
    public static String normalize(String self) {
        return StringGroovyMethods.normalize((CharSequence)self);
    }

    public static String padLeft(CharSequence self, Number numberOfChars) {
        return StringGroovyMethods.padLeft(self, numberOfChars, (CharSequence)" ");
    }

    public static String padLeft(CharSequence self, Number numberOfChars, CharSequence padding) {
        int numChars = numberOfChars.intValue();
        if (numChars <= self.length()) {
            return self.toString();
        }
        return StringGroovyMethods.getPadding(padding.toString(), numChars - self.length()) + self;
    }

    @Deprecated
    public static String padLeft(String self, Number numberOfChars) {
        return StringGroovyMethods.padLeft((CharSequence)self, numberOfChars);
    }

    @Deprecated
    public static String padLeft(String self, Number numberOfChars, String padding) {
        return StringGroovyMethods.padLeft((CharSequence)self, numberOfChars, (CharSequence)padding);
    }

    public static String padRight(CharSequence self, Number numberOfChars) {
        return StringGroovyMethods.padRight(self, numberOfChars, (CharSequence)" ");
    }

    public static String padRight(CharSequence self, Number numberOfChars, CharSequence padding) {
        int numChars = numberOfChars.intValue();
        if (numChars <= self.length()) {
            return self.toString();
        }
        return self + StringGroovyMethods.getPadding(padding.toString(), numChars - self.length());
    }

    @Deprecated
    public static String padRight(String self, Number numberOfChars) {
        return StringGroovyMethods.padRight((CharSequence)self, numberOfChars, (CharSequence)" ");
    }

    @Deprecated
    public static String padRight(String self, Number numberOfChars, String padding) {
        return StringGroovyMethods.padRight((CharSequence)self, numberOfChars, (CharSequence)padding);
    }

    public static String plus(CharSequence left, Object value) {
        return left + DefaultGroovyMethods.toString(value);
    }

    public static String plus(Number value, String right) {
        return DefaultGroovyMethods.toString(value) + right;
    }

    @Deprecated
    public static String plus(String left, Object value) {
        return StringGroovyMethods.plus((CharSequence)left, value);
    }

    public static String plus(String left, CharSequence value) {
        return left + value;
    }

    public static String plus(StringBuffer left, String value) {
        return left + value;
    }

    public static String previous(CharSequence self) {
        StringBuilder buffer = new StringBuilder(self);
        if (buffer.length() == 0) {
            throw new IllegalArgumentException("the string is empty");
        }
        char last = buffer.charAt(buffer.length() - 1);
        if (last == '\u0000') {
            buffer.deleteCharAt(buffer.length() - 1);
        } else {
            char next = last;
            next = (char)(next - '\u0001');
            buffer.setCharAt(buffer.length() - 1, next);
        }
        return buffer.toString();
    }

    @Deprecated
    public static String previous(String self) {
        return StringGroovyMethods.previous((CharSequence)self);
    }

    public static void putAt(StringBuffer self, EmptyRange range, Object value) {
        RangeInfo info = StringGroovyMethods.subListBorders(self.length(), range);
        self.replace(info.from, info.to, value.toString());
    }

    public static void putAt(StringBuffer self, IntRange range, Object value) {
        RangeInfo info = StringGroovyMethods.subListBorders(self.length(), range);
        self.replace(info.from, info.to, value.toString());
    }

    public static List<String> readLines(CharSequence self) {
        return DefaultGroovyMethods.toList(new LineIterable(self));
    }

    @Deprecated
    public static List<String> readLines(String self) {
        return StringGroovyMethods.readLines((CharSequence)self);
    }

    public static String replaceAll(CharSequence self, CharSequence regex, CharSequence replacement) {
        return self.toString().replaceAll(regex.toString(), replacement.toString());
    }

    public static String replaceAll(CharSequence self, CharSequence regex, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        return StringGroovyMethods.replaceAll(self, Pattern.compile(regex.toString()), closure);
    }

    public static String replaceAll(CharSequence self, Pattern pattern, CharSequence replacement) {
        return pattern.matcher(self).replaceAll(replacement.toString());
    }

    public static String replaceAll(CharSequence self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        String s = self.toString();
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer(s.length() + 16);
            do {
                String replacement = StringGroovyMethods.getReplacement(matcher, closure);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            } while (matcher.find());
            matcher.appendTail(sb);
            return sb.toString();
        }
        return s;
    }

    @Deprecated
    public static String replaceAll(String self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        return StringGroovyMethods.replaceAll((CharSequence)self, pattern, closure);
    }

    @Deprecated
    public static String replaceAll(String self, Pattern pattern, String replacement) {
        return pattern.matcher(self).replaceAll(replacement);
    }

    @Deprecated
    public static String replaceAll(String self, String regex, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        return StringGroovyMethods.replaceAll((CharSequence)self, (CharSequence)regex, closure);
    }

    public static String replaceFirst(CharSequence self, CharSequence regex, CharSequence replacement) {
        return self.toString().replaceFirst(regex.toString(), replacement.toString());
    }

    public static String replaceFirst(CharSequence self, CharSequence regex, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        return StringGroovyMethods.replaceFirst(self, Pattern.compile(regex.toString()), closure);
    }

    public static String replaceFirst(CharSequence self, Pattern pattern, CharSequence replacement) {
        return pattern.matcher(self).replaceFirst(replacement.toString());
    }

    public static String replaceFirst(CharSequence self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        String s = self.toString();
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer(s.length() + 16);
            String replacement = StringGroovyMethods.getReplacement(matcher, closure);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            matcher.appendTail(sb);
            return sb.toString();
        }
        return s;
    }

    @Deprecated
    public static String replaceFirst(String self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        return StringGroovyMethods.replaceFirst((CharSequence)self, pattern, closure);
    }

    @Deprecated
    public static String replaceFirst(String self, Pattern pattern, String replacement) {
        return pattern.matcher(self).replaceFirst(replacement);
    }

    @Deprecated
    public static String replaceFirst(String self, String regex, @ClosureParams(value=FromString.class, options={"List<String>", "String[]"}) Closure closure) {
        return StringGroovyMethods.replaceFirst((CharSequence)self, (CharSequence)regex, closure);
    }

    public static String reverse(CharSequence self) {
        return new StringBuilder(self).reverse().toString();
    }

    @Deprecated
    public static String reverse(String self) {
        return StringGroovyMethods.reverse((CharSequence)self);
    }

    public static void setIndex(Matcher matcher, int idx) {
        block4: {
            block5: {
                block3: {
                    int count = StringGroovyMethods.getCount(matcher);
                    if (idx < -count || idx >= count) {
                        throw new IndexOutOfBoundsException("index is out of range " + -count + ".." + (count - 1) + " (index = " + idx + ")");
                    }
                    if (idx != 0) break block3;
                    matcher.reset();
                    break block4;
                }
                if (idx <= 0) break block5;
                matcher.reset();
                for (int i = 0; i < idx; ++i) {
                    matcher.find();
                }
                break block4;
            }
            if (idx >= 0) break block4;
            matcher.reset();
            idx += StringGroovyMethods.getCount(matcher);
            for (int i = 0; i < idx; ++i) {
                matcher.find();
            }
        }
    }

    public static int size(CharSequence text) {
        return text.length();
    }

    public static long size(Matcher self) {
        return StringGroovyMethods.getCount(self);
    }

    public static int size(String text) {
        return text.length();
    }

    public static int size(StringBuffer buffer) {
        return buffer.length();
    }

    public static String[] split(CharSequence self) {
        StringTokenizer st = new StringTokenizer(self.toString());
        String[] strings = new String[st.countTokens()];
        for (int i = 0; i < strings.length; ++i) {
            strings[i] = st.nextToken();
        }
        return strings;
    }

    @Deprecated
    public static String[] split(GString self) {
        return StringGroovyMethods.split((CharSequence)self);
    }

    @Deprecated
    public static String[] split(String self) {
        return StringGroovyMethods.split((CharSequence)self);
    }

    public static <T> T splitEachLine(CharSequence self, CharSequence regex, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return StringGroovyMethods.splitEachLine(self, Pattern.compile(regex.toString()), closure);
    }

    public static <T> T splitEachLine(CharSequence self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) {
        List<String> list = StringGroovyMethods.readLines(self);
        T result = null;
        for (String line : new LineIterable(self)) {
            List<String> vals = Arrays.asList(pattern.split(line));
            result = closure.call((Object)vals);
        }
        return result;
    }

    @Deprecated
    public static <T> T splitEachLine(String self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return StringGroovyMethods.splitEachLine((CharSequence)self, pattern, closure);
    }

    @Deprecated
    public static <T> T splitEachLine(String self, String regex, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return StringGroovyMethods.splitEachLine((CharSequence)self, (CharSequence)regex, closure);
    }

    public static String stripIndent(CharSequence self) {
        if (self.length() == 0) {
            return self.toString();
        }
        int runningCount = -1;
        for (String line : new LineIterable(self)) {
            if (StringGroovyMethods.isAllWhitespace((CharSequence)line)) continue;
            if (runningCount == -1) {
                runningCount = line.length();
            }
            if ((runningCount = StringGroovyMethods.findMinimumLeadingSpaces(line, runningCount)) != 0) continue;
            break;
        }
        return StringGroovyMethods.stripIndent(self, runningCount == -1 ? 0 : runningCount);
    }

    public static String stripIndent(CharSequence self, int numChars) {
        if (self.length() == 0 || numChars <= 0) {
            return self.toString();
        }
        StringBuilder builder = new StringBuilder();
        for (String line : new LineIterable(self)) {
            if (!StringGroovyMethods.isAllWhitespace((CharSequence)line)) {
                builder.append(StringGroovyMethods.stripIndentFromLine(line, numChars));
            }
            builder.append("\n");
        }
        if (self.charAt(self.length() - 1) != '\n') {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    @Deprecated
    public static String stripIndent(String self) {
        return StringGroovyMethods.stripIndent((CharSequence)self);
    }

    @Deprecated
    public static String stripIndent(String self, int numChars) {
        return StringGroovyMethods.stripIndent((CharSequence)self, numChars);
    }

    private static String stripIndentFromLine(String line, int numChars) {
        int length = line.length();
        return numChars <= length ? line.substring(numChars) : "";
    }

    public static String stripMargin(CharSequence self) {
        return StringGroovyMethods.stripMargin(self, '|');
    }

    public static String stripMargin(CharSequence self, char marginChar) {
        if (self.length() == 0) {
            return self.toString();
        }
        StringBuilder builder = new StringBuilder();
        for (String line : new LineIterable(self)) {
            builder.append(StringGroovyMethods.stripMarginFromLine(line, marginChar));
            builder.append("\n");
        }
        if (self.charAt(self.length() - 1) != '\n') {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public static String stripMargin(CharSequence self, CharSequence marginChar) {
        String mc = marginChar.toString();
        if (mc.length() == 0) {
            return StringGroovyMethods.stripMargin(self, '|');
        }
        return StringGroovyMethods.stripMargin(self, mc.charAt(0));
    }

    @Deprecated
    public static String stripMargin(String self) {
        return StringGroovyMethods.stripMargin((CharSequence)self);
    }

    @Deprecated
    public static String stripMargin(String self, char marginChar) {
        return StringGroovyMethods.stripMargin((CharSequence)self, marginChar);
    }

    @Deprecated
    public static String stripMargin(String self, String marginChar) {
        return StringGroovyMethods.stripMargin((CharSequence)self, (CharSequence)marginChar);
    }

    private static String stripMarginFromLine(String line, char marginChar) {
        int index;
        int length = line.length();
        for (index = 0; index < length && line.charAt(index) <= ' '; ++index) {
        }
        return index < length && line.charAt(index) == marginChar ? line.substring(index + 1) : line;
    }

    public static CharSequence take(CharSequence self, int num) {
        if (num < 0) {
            return self.subSequence(0, 0);
        }
        if (self.length() <= num) {
            return self;
        }
        return self.subSequence(0, num);
    }

    public static String take(GString self, int num) {
        return (String)StringGroovyMethods.take(self.toString(), num);
    }

    public static CharSequence takeWhile(CharSequence self, @ClosureParams(value=SimpleType.class, options={"char"}) Closure condition) {
        int num;
        BooleanClosureWrapper bcw = new BooleanClosureWrapper(condition);
        for (num = 0; num < self.length(); ++num) {
            char value = self.charAt(num);
            if (!bcw.call(Character.valueOf(value))) break;
        }
        return StringGroovyMethods.take(self, num);
    }

    public static String takeWhile(GString self, @ClosureParams(value=SimpleType.class, options={"char"}) Closure condition) {
        return (String)StringGroovyMethods.takeWhile(self.toString(), condition);
    }

    public static BigDecimal toBigDecimal(CharSequence self) {
        return new BigDecimal(self.toString().trim());
    }

    @Deprecated
    public static BigDecimal toBigDecimal(String self) {
        return StringGroovyMethods.toBigDecimal((CharSequence)self);
    }

    public static BigInteger toBigInteger(CharSequence self) {
        return new BigInteger(self.toString().trim());
    }

    @Deprecated
    public static BigInteger toBigInteger(String self) {
        return StringGroovyMethods.toBigInteger((CharSequence)self);
    }

    public static Boolean toBoolean(String self) {
        String trimmed = self.trim();
        if ("true".equalsIgnoreCase(trimmed) || "y".equalsIgnoreCase(trimmed) || "1".equals(trimmed)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Character toCharacter(String self) {
        return Character.valueOf(self.charAt(0));
    }

    public static Double toDouble(CharSequence self) {
        return Double.valueOf(self.toString().trim());
    }

    @Deprecated
    public static Double toDouble(String self) {
        return StringGroovyMethods.toDouble((CharSequence)self);
    }

    public static Float toFloat(CharSequence self) {
        return Float.valueOf(self.toString().trim());
    }

    @Deprecated
    public static Float toFloat(String self) {
        return StringGroovyMethods.toFloat((CharSequence)self);
    }

    public static Integer toInteger(CharSequence self) {
        return Integer.valueOf(self.toString().trim());
    }

    @Deprecated
    public static Integer toInteger(String self) {
        return StringGroovyMethods.toInteger((CharSequence)self);
    }

    public static List<String> tokenize(CharSequence self) {
        return InvokerHelper.asList(new StringTokenizer(self.toString()));
    }

    public static List<String> tokenize(CharSequence self, Character delimiter) {
        return StringGroovyMethods.tokenize(self, (CharSequence)delimiter.toString());
    }

    public static List<String> tokenize(CharSequence self, CharSequence delimiters) {
        return InvokerHelper.asList(new StringTokenizer(self.toString(), delimiters.toString()));
    }

    @Deprecated
    public static List<String> tokenize(String self) {
        return StringGroovyMethods.tokenize((CharSequence)self);
    }

    @Deprecated
    public static List<String> tokenize(String self, Character delimiter) {
        return StringGroovyMethods.tokenize((CharSequence)self, delimiter);
    }

    @Deprecated
    public static List<String> tokenize(String self, String delimiters) {
        return StringGroovyMethods.tokenize((CharSequence)self, (CharSequence)delimiters);
    }

    public static List<String> toList(CharSequence self) {
        String s = self.toString();
        int size = s.length();
        ArrayList<String> answer = new ArrayList<String>(size);
        for (int i = 0; i < size; ++i) {
            answer.add(s.substring(i, i + 1));
        }
        return answer;
    }

    @Deprecated
    public static List<String> toList(String self) {
        return StringGroovyMethods.toList((CharSequence)self);
    }

    public static Long toLong(CharSequence self) {
        return Long.valueOf(self.toString().trim());
    }

    @Deprecated
    public static Long toLong(String self) {
        return StringGroovyMethods.toLong((CharSequence)self);
    }

    public static Set<String> toSet(CharSequence self) {
        return new HashSet<String>(StringGroovyMethods.toList(self));
    }

    @Deprecated
    public static Set<String> toSet(String self) {
        return StringGroovyMethods.toSet((CharSequence)self);
    }

    public static Short toShort(CharSequence self) {
        return Short.valueOf(self.toString().trim());
    }

    @Deprecated
    public static Short toShort(String self) {
        return StringGroovyMethods.toShort((CharSequence)self);
    }

    public static String tr(CharSequence self, CharSequence sourceSet, CharSequence replacementSet) throws ClassNotFoundException {
        return (String)InvokerHelper.invokeStaticMethod("org.codehaus.groovy.util.StringUtil", "tr", (Object)new Object[]{self.toString(), sourceSet.toString(), replacementSet.toString()});
    }

    @Deprecated
    public static String tr(String self, String sourceSet, String replacementSet) throws ClassNotFoundException {
        return StringGroovyMethods.tr((CharSequence)self, (CharSequence)sourceSet, (CharSequence)replacementSet);
    }

    public static String unexpand(CharSequence self) {
        return StringGroovyMethods.unexpand(self, 8);
    }

    public static String unexpand(CharSequence self, int tabStop) {
        if (self.length() == 0) {
            return self.toString();
        }
        StringBuilder builder = new StringBuilder();
        for (String line : new LineIterable(self)) {
            builder.append(StringGroovyMethods.unexpandLine((CharSequence)line, tabStop));
            builder.append("\n");
        }
        if (self.charAt(self.length() - 1) != '\n') {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    @Deprecated
    public static String unexpand(String self) {
        return StringGroovyMethods.unexpand((CharSequence)self);
    }

    @Deprecated
    public static String unexpand(String self, int tabStop) {
        return StringGroovyMethods.unexpand((CharSequence)self, tabStop);
    }

    public static String unexpandLine(CharSequence self, int tabStop) {
        StringBuilder builder = new StringBuilder(self.toString());
        int index = 0;
        while (index + tabStop < builder.length()) {
            int count;
            String piece = builder.substring(index, index + tabStop);
            for (count = 0; count < tabStop && Character.isWhitespace(piece.charAt(tabStop - (count + 1))); ++count) {
            }
            if (count > 0) {
                piece = piece.substring(0, tabStop - count) + '\t';
                builder.replace(index, index + tabStop, piece);
                index = index + tabStop - (count - 1);
                continue;
            }
            index += tabStop;
        }
        return builder.toString();
    }

    @Deprecated
    public static String unexpandLine(String self, int tabStop) {
        return StringGroovyMethods.unexpandLine((CharSequence)self, tabStop);
    }

    public static boolean startsWithAny(CharSequence self, CharSequence ... prefixes) {
        String str = self.toString();
        for (CharSequence prefix : prefixes) {
            if (!str.startsWith(prefix.toString())) continue;
            return true;
        }
        return false;
    }

    public static boolean endsWithAny(CharSequence self, CharSequence ... suffixes) {
        String str = self.toString();
        for (CharSequence suffix : suffixes) {
            if (!str.endsWith(suffix.toString())) continue;
            return true;
        }
        return false;
    }

    private static final class LineIterable
    implements Iterable<String> {
        private final CharSequence delegate;

        public LineIterable(CharSequence cs) {
            this.delegate = cs instanceof GString ? cs.toString() : cs;
        }

        @Override
        public Iterator<String> iterator() {
            return IOGroovyMethods.iterator(new CharSequenceReader(this.delegate));
        }
    }
}


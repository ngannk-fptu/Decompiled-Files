/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.BuiltInForString;
import freemarker.core.Environment;
import freemarker.core.RegexpHelper;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._TemplateModelException;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BuiltInsForStringsRegexp {
    private BuiltInsForStringsRegexp() {
    }

    static class RegexMatchModel
    implements TemplateBooleanModel,
    TemplateCollectionModel,
    TemplateSequenceModel {
        final Pattern pattern;
        final String input;
        private Matcher firedEntireInputMatcher;
        private Boolean entireInputMatched;
        private TemplateSequenceModel entireInputMatchGroups;
        private ArrayList matchingInputParts;

        RegexMatchModel(Pattern pattern, String input) {
            this.pattern = pattern;
            this.input = input;
        }

        @Override
        public TemplateModel get(int i) throws TemplateModelException {
            ArrayList matchingInputParts = this.matchingInputParts;
            if (matchingInputParts == null) {
                matchingInputParts = this.getMatchingInputPartsAndStoreResults();
            }
            return (TemplateModel)matchingInputParts.get(i);
        }

        @Override
        public boolean getAsBoolean() {
            Boolean result = this.entireInputMatched;
            return result != null ? result.booleanValue() : this.isEntrieInputMatchesAndStoreResults();
        }

        TemplateModel getGroups() {
            TemplateSequenceModel entireInputMatchGroups = this.entireInputMatchGroups;
            if (entireInputMatchGroups == null) {
                Matcher t = this.firedEntireInputMatcher;
                if (t == null) {
                    this.isEntrieInputMatchesAndStoreResults();
                    t = this.firedEntireInputMatcher;
                }
                final Matcher firedEntireInputMatcher = t;
                this.entireInputMatchGroups = entireInputMatchGroups = new TemplateSequenceModel(){

                    @Override
                    public TemplateModel get(int i) throws TemplateModelException {
                        try {
                            return new SimpleScalar(firedEntireInputMatcher.group(i));
                        }
                        catch (Exception e) {
                            throw new _TemplateModelException((Throwable)e, "Failed to read regular expression match group");
                        }
                    }

                    @Override
                    public int size() throws TemplateModelException {
                        try {
                            return firedEntireInputMatcher.groupCount() + 1;
                        }
                        catch (Exception e) {
                            throw new _TemplateModelException((Throwable)e, "Failed to get regular expression match group count");
                        }
                    }
                };
            }
            return entireInputMatchGroups;
        }

        private ArrayList getMatchingInputPartsAndStoreResults() throws TemplateModelException {
            ArrayList<MatchWithGroups> matchingInputParts = new ArrayList<MatchWithGroups>();
            Matcher matcher = this.pattern.matcher(this.input);
            while (matcher.find()) {
                matchingInputParts.add(new MatchWithGroups(this.input, matcher));
            }
            this.matchingInputParts = matchingInputParts;
            return matchingInputParts;
        }

        private boolean isEntrieInputMatchesAndStoreResults() {
            Matcher matcher = this.pattern.matcher(this.input);
            boolean matches = matcher.matches();
            this.firedEntireInputMatcher = matcher;
            this.entireInputMatched = matches;
            return matches;
        }

        @Override
        public TemplateModelIterator iterator() {
            final ArrayList matchingInputParts = this.matchingInputParts;
            if (matchingInputParts == null) {
                final Matcher matcher = this.pattern.matcher(this.input);
                return new TemplateModelIterator(){
                    private int nextIdx = 0;
                    boolean hasFindInfo = matcher.find();

                    @Override
                    public boolean hasNext() {
                        ArrayList matchingInputParts = RegexMatchModel.this.matchingInputParts;
                        if (matchingInputParts == null) {
                            return this.hasFindInfo;
                        }
                        return this.nextIdx < matchingInputParts.size();
                    }

                    @Override
                    public TemplateModel next() throws TemplateModelException {
                        ArrayList matchingInputParts = RegexMatchModel.this.matchingInputParts;
                        if (matchingInputParts == null) {
                            if (!this.hasFindInfo) {
                                throw new _TemplateModelException("There were no more regular expression matches");
                            }
                            MatchWithGroups result = new MatchWithGroups(RegexMatchModel.this.input, matcher);
                            ++this.nextIdx;
                            this.hasFindInfo = matcher.find();
                            return result;
                        }
                        try {
                            return (TemplateModel)matchingInputParts.get(this.nextIdx++);
                        }
                        catch (IndexOutOfBoundsException e) {
                            throw new _TemplateModelException((Throwable)e, "There were no more regular expression matches");
                        }
                    }
                };
            }
            return new TemplateModelIterator(){
                private int nextIdx = 0;

                @Override
                public boolean hasNext() {
                    return this.nextIdx < matchingInputParts.size();
                }

                @Override
                public TemplateModel next() throws TemplateModelException {
                    try {
                        return (TemplateModel)matchingInputParts.get(this.nextIdx++);
                    }
                    catch (IndexOutOfBoundsException e) {
                        throw new _TemplateModelException((Throwable)e, "There were no more regular expression matches");
                    }
                }
            };
        }

        @Override
        public int size() throws TemplateModelException {
            ArrayList matchingInputParts = this.matchingInputParts;
            if (matchingInputParts == null) {
                matchingInputParts = this.getMatchingInputPartsAndStoreResults();
            }
            return matchingInputParts.size();
        }

        static class MatchWithGroups
        implements TemplateScalarModel {
            final String matchedInputPart;
            final SimpleSequence groupsSeq;

            MatchWithGroups(String input, Matcher matcher) {
                this.matchedInputPart = input.substring(matcher.start(), matcher.end());
                int grpCount = matcher.groupCount() + 1;
                this.groupsSeq = new SimpleSequence(grpCount, (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                for (int i = 0; i < grpCount; ++i) {
                    this.groupsSeq.add(matcher.group(i));
                }
            }

            @Override
            public String getAsString() {
                return this.matchedInputPart;
            }
        }
    }

    static class replace_reBI
    extends BuiltInForString {
        replace_reBI() {
        }

        @Override
        TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
            return new ReplaceMethod(s);
        }

        class ReplaceMethod
        implements TemplateMethodModel {
            private String s;

            ReplaceMethod(String s) {
                this.s = s;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                String result;
                long flags;
                int argCnt = args.size();
                replace_reBI.this.checkMethodArgCount(argCnt, 2, 3);
                String arg1 = (String)args.get(0);
                String arg2 = (String)args.get(1);
                long l = flags = argCnt > 2 ? RegexpHelper.parseFlagString((String)args.get(2)) : 0L;
                if ((flags & 0x100000000L) == 0L) {
                    RegexpHelper.checkNonRegexpFlags("replace", flags);
                    result = StringUtil.replace(this.s, arg1, arg2, (flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) != 0L, (flags & 0x200000000L) != 0L);
                } else {
                    Pattern pattern = RegexpHelper.getPattern(arg1, (int)flags);
                    Matcher matcher = pattern.matcher(this.s);
                    result = (flags & 0x200000000L) != 0L ? matcher.replaceFirst(arg2) : matcher.replaceAll(arg2);
                }
                return new SimpleScalar(result);
            }
        }
    }

    static class matchesBI
    extends BuiltInForString {
        matchesBI() {
        }

        @Override
        TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
            return new MatcherBuilder(s);
        }

        class MatcherBuilder
        implements TemplateMethodModel {
            String matchString;

            MatcherBuilder(String matchString) throws TemplateModelException {
                this.matchString = matchString;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                long flags;
                int argCnt = args.size();
                matchesBI.this.checkMethodArgCount(argCnt, 1, 2);
                String patternString = (String)args.get(0);
                long l = flags = argCnt > 1 ? RegexpHelper.parseFlagString((String)args.get(1)) : 0L;
                if ((flags & 0x200000000L) != 0L) {
                    RegexpHelper.logFlagWarning("?" + matchesBI.this.key + " doesn't support the \"f\" flag.");
                }
                Pattern pattern = RegexpHelper.getPattern(patternString, (int)flags);
                return new RegexMatchModel(pattern, this.matchString);
            }
        }
    }

    static class groupsBI
    extends BuiltIn {
        groupsBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel targetModel = this.target.eval(env);
            this.assertNonNull(targetModel, env);
            if (targetModel instanceof RegexMatchModel) {
                return ((RegexMatchModel)targetModel).getGroups();
            }
            if (targetModel instanceof RegexMatchModel.MatchWithGroups) {
                return ((RegexMatchModel.MatchWithGroups)targetModel).groupsSeq;
            }
            throw new UnexpectedTypeException(this.target, targetModel, "regular expression matcher", new Class[]{RegexMatchModel.class, RegexMatchModel.MatchWithGroups.class}, env);
        }
    }
}


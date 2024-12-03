/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSortedSet
 */
package com.google.template.soy.examples;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.template.soy.parseinfo.SoyFileInfo;
import com.google.template.soy.parseinfo.SoyTemplateInfo;

public final class FeaturesSoyInfo
extends SoyFileInfo {
    public static final String __NAMESPACE__ = "soy.examples.features";
    public static final DemoCommentsSoyTemplateInfo DEMO_COMMENTS = DemoCommentsSoyTemplateInfo.getInstance();
    public static final DemoLineJoiningSoyTemplateInfo DEMO_LINE_JOINING = DemoLineJoiningSoyTemplateInfo.getInstance();
    public static final DemoRawTextCommandsSoyTemplateInfo DEMO_RAW_TEXT_COMMANDS = DemoRawTextCommandsSoyTemplateInfo.getInstance();
    public static final DemoPrintSoyTemplateInfo DEMO_PRINT = DemoPrintSoyTemplateInfo.getInstance();
    public static final DemoPrintDirectivesSoyTemplateInfo DEMO_PRINT_DIRECTIVES = DemoPrintDirectivesSoyTemplateInfo.getInstance();
    public static final DemoAutoescapeTrueSoyTemplateInfo DEMO_AUTOESCAPE_TRUE = DemoAutoescapeTrueSoyTemplateInfo.getInstance();
    public static final DemoAutoescapeFalseSoyTemplateInfo DEMO_AUTOESCAPE_FALSE = DemoAutoescapeFalseSoyTemplateInfo.getInstance();
    public static final DemoMsgSoyTemplateInfo DEMO_MSG = DemoMsgSoyTemplateInfo.getInstance();
    public static final DemoIfSoyTemplateInfo DEMO_IF = DemoIfSoyTemplateInfo.getInstance();
    public static final DemoSwitchSoyTemplateInfo DEMO_SWITCH = DemoSwitchSoyTemplateInfo.getInstance();
    public static final DemoForeachSoyTemplateInfo DEMO_FOREACH = DemoForeachSoyTemplateInfo.getInstance();
    public static final DemoForSoyTemplateInfo DEMO_FOR = DemoForSoyTemplateInfo.getInstance();
    public static final DemoCallWithoutParamSoyTemplateInfo DEMO_CALL_WITHOUT_PARAM = DemoCallWithoutParamSoyTemplateInfo.getInstance();
    public static final DemoCallWithParamSoyTemplateInfo DEMO_CALL_WITH_PARAM = DemoCallWithParamSoyTemplateInfo.getInstance();
    public static final DemoCallWithParamBlockSoyTemplateInfo DEMO_CALL_WITH_PARAM_BLOCK = DemoCallWithParamBlockSoyTemplateInfo.getInstance();
    public static final DemoParamWithKindAttributeSoyTemplateInfo DEMO_PARAM_WITH_KIND_ATTRIBUTE = DemoParamWithKindAttributeSoyTemplateInfo.getInstance();
    public static final DemoExpressionsSoyTemplateInfo DEMO_EXPRESSIONS = DemoExpressionsSoyTemplateInfo.getInstance();
    public static final DemoDoubleBracesSoyTemplateInfo DEMO_DOUBLE_BRACES = DemoDoubleBracesSoyTemplateInfo.getInstance();
    public static final DemoBidiSupportSoyTemplateInfo DEMO_BIDI_SUPPORT = DemoBidiSupportSoyTemplateInfo.getInstance();
    public static final BidiGlobalDirSoyTemplateInfo BIDI_GLOBAL_DIR = BidiGlobalDirSoyTemplateInfo.getInstance();
    public static final ExampleHeaderSoyTemplateInfo EXAMPLE_HEADER = ExampleHeaderSoyTemplateInfo.getInstance();
    private static final FeaturesSoyInfo __INSTANCE__ = new FeaturesSoyInfo();

    private FeaturesSoyInfo() {
        super("features.soy", __NAMESPACE__, (ImmutableSortedSet<String>)ImmutableSortedSet.of((Comparable)((Object)"author"), (Comparable)((Object)"boo"), (Comparable)((Object)"companionName"), (Comparable)((Object)"cssClass"), (Comparable)((Object)"currentYear"), (Comparable)((Object)"destination"), (Comparable[])new String[]{"destinations", "elementId", "exampleName", "exampleNum", "italicHtml", "items", "keywords", "labsUrl", "list", "listItems", "longVarName", "message", "name", "numLines", "persons", "pi", "setMembers", "setName", "students", "title", "tripInfo", "two", "year"}), (ImmutableList<SoyTemplateInfo>)ImmutableList.of((Object)DEMO_COMMENTS, (Object)DEMO_LINE_JOINING, (Object)DEMO_RAW_TEXT_COMMANDS, (Object)DEMO_PRINT, (Object)DEMO_PRINT_DIRECTIVES, (Object)DEMO_AUTOESCAPE_TRUE, (Object)DEMO_AUTOESCAPE_FALSE, (Object)DEMO_MSG, (Object)DEMO_IF, (Object)DEMO_SWITCH, (Object)DEMO_FOREACH, (Object)DEMO_FOR, (Object[])new SoyTemplateInfo[]{DEMO_CALL_WITHOUT_PARAM, DEMO_CALL_WITH_PARAM, DEMO_CALL_WITH_PARAM_BLOCK, DEMO_PARAM_WITH_KIND_ATTRIBUTE, DEMO_EXPRESSIONS, DEMO_DOUBLE_BRACES, DEMO_BIDI_SUPPORT, BIDI_GLOBAL_DIR, EXAMPLE_HEADER}), (ImmutableMap<String, SoyFileInfo.CssTagsPrefixPresence>)ImmutableMap.of());
    }

    public static FeaturesSoyInfo getInstance() {
        return __INSTANCE__;
    }

    public static final class ExampleHeaderSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.exampleHeader";
        public static final String __PARTIAL_NAME__ = ".exampleHeader";
        public static final String EXAMPLE_NUM = "exampleNum";
        public static final String EXAMPLE_NAME = "exampleName";
        private static final ExampleHeaderSoyTemplateInfo __INSTANCE__ = new ExampleHeaderSoyTemplateInfo();

        private ExampleHeaderSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)EXAMPLE_NUM, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)EXAMPLE_NAME, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static ExampleHeaderSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class BidiGlobalDirSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.bidiGlobalDir";
        public static final String __PARTIAL_NAME__ = ".bidiGlobalDir";
        private static final BidiGlobalDirSoyTemplateInfo __INSTANCE__ = new BidiGlobalDirSoyTemplateInfo();

        private BidiGlobalDirSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.of(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static BidiGlobalDirSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoBidiSupportSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoBidiSupport";
        public static final String __PARTIAL_NAME__ = ".demoBidiSupport";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String YEAR = "year";
        public static final String KEYWORDS = "keywords";
        private static final DemoBidiSupportSoyTemplateInfo __INSTANCE__ = new DemoBidiSupportSoyTemplateInfo();

        private DemoBidiSupportSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)TITLE, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)AUTHOR, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)YEAR, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)KEYWORDS, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoBidiSupportSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoDoubleBracesSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoDoubleBraces";
        public static final String __PARTIAL_NAME__ = ".demoDoubleBraces";
        public static final String SET_NAME = "setName";
        public static final String SET_MEMBERS = "setMembers";
        private static final DemoDoubleBracesSoyTemplateInfo __INSTANCE__ = new DemoDoubleBracesSoyTemplateInfo();

        private DemoDoubleBracesSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)SET_NAME, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)SET_MEMBERS, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoDoubleBracesSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoExpressionsSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoExpressions";
        public static final String __PARTIAL_NAME__ = ".demoExpressions";
        public static final String STUDENTS = "students";
        public static final String CURRENT_YEAR = "currentYear";
        private static final DemoExpressionsSoyTemplateInfo __INSTANCE__ = new DemoExpressionsSoyTemplateInfo();

        private DemoExpressionsSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)STUDENTS, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)CURRENT_YEAR, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoExpressionsSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoParamWithKindAttributeSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoParamWithKindAttribute";
        public static final String __PARTIAL_NAME__ = ".demoParamWithKindAttribute";
        public static final String MESSAGE = "message";
        public static final String LIST = "list";
        private static final DemoParamWithKindAttributeSoyTemplateInfo __INSTANCE__ = new DemoParamWithKindAttributeSoyTemplateInfo();

        private DemoParamWithKindAttributeSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)MESSAGE, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)LIST, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoParamWithKindAttributeSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoCallWithParamBlockSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoCallWithParamBlock";
        public static final String __PARTIAL_NAME__ = ".demoCallWithParamBlock";
        public static final String NAME = "name";
        private static final DemoCallWithParamBlockSoyTemplateInfo __INSTANCE__ = new DemoCallWithParamBlockSoyTemplateInfo();

        private DemoCallWithParamBlockSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)NAME, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoCallWithParamBlockSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoCallWithParamSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoCallWithParam";
        public static final String __PARTIAL_NAME__ = ".demoCallWithParam";
        public static final String NAME = "name";
        public static final String COMPANION_NAME = "companionName";
        public static final String DESTINATIONS = "destinations";
        private static final DemoCallWithParamSoyTemplateInfo __INSTANCE__ = new DemoCallWithParamSoyTemplateInfo();

        private DemoCallWithParamSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)NAME, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)COMPANION_NAME, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)DESTINATIONS, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoCallWithParamSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoCallWithoutParamSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoCallWithoutParam";
        public static final String __PARTIAL_NAME__ = ".demoCallWithoutParam";
        public static final String NAME = "name";
        public static final String TRIP_INFO = "tripInfo";
        public static final String DESTINATION = "destination";
        private static final DemoCallWithoutParamSoyTemplateInfo __INSTANCE__ = new DemoCallWithoutParamSoyTemplateInfo();

        private DemoCallWithoutParamSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)NAME, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)TRIP_INFO, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)DESTINATION, (Object)SoyTemplateInfo.ParamRequisiteness.OPTIONAL).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoCallWithoutParamSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoForSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoFor";
        public static final String __PARTIAL_NAME__ = ".demoFor";
        public static final String NUM_LINES = "numLines";
        private static final DemoForSoyTemplateInfo __INSTANCE__ = new DemoForSoyTemplateInfo();

        private DemoForSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)NUM_LINES, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoForSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoForeachSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoForeach";
        public static final String __PARTIAL_NAME__ = ".demoForeach";
        public static final String PERSONS = "persons";
        private static final DemoForeachSoyTemplateInfo __INSTANCE__ = new DemoForeachSoyTemplateInfo();

        private DemoForeachSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)PERSONS, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoForeachSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoSwitchSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoSwitch";
        public static final String __PARTIAL_NAME__ = ".demoSwitch";
        public static final String NAME = "name";
        private static final DemoSwitchSoyTemplateInfo __INSTANCE__ = new DemoSwitchSoyTemplateInfo();

        private DemoSwitchSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)NAME, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoSwitchSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoIfSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoIf";
        public static final String __PARTIAL_NAME__ = ".demoIf";
        public static final String PI = "pi";
        private static final DemoIfSoyTemplateInfo __INSTANCE__ = new DemoIfSoyTemplateInfo();

        private DemoIfSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)PI, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoIfSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoMsgSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoMsg";
        public static final String __PARTIAL_NAME__ = ".demoMsg";
        public static final String NAME = "name";
        public static final String LABS_URL = "labsUrl";
        private static final DemoMsgSoyTemplateInfo __INSTANCE__ = new DemoMsgSoyTemplateInfo();

        private DemoMsgSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)NAME, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)LABS_URL, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoMsgSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoAutoescapeFalseSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoAutoescapeFalse";
        public static final String __PARTIAL_NAME__ = ".demoAutoescapeFalse";
        public static final String ITALIC_HTML = "italicHtml";
        private static final DemoAutoescapeFalseSoyTemplateInfo __INSTANCE__ = new DemoAutoescapeFalseSoyTemplateInfo();

        private DemoAutoescapeFalseSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)ITALIC_HTML, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoAutoescapeFalseSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoAutoescapeTrueSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoAutoescapeTrue";
        public static final String __PARTIAL_NAME__ = ".demoAutoescapeTrue";
        public static final String ITALIC_HTML = "italicHtml";
        private static final DemoAutoescapeTrueSoyTemplateInfo __INSTANCE__ = new DemoAutoescapeTrueSoyTemplateInfo();

        private DemoAutoescapeTrueSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)ITALIC_HTML, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoAutoescapeTrueSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoPrintDirectivesSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoPrintDirectives";
        public static final String __PARTIAL_NAME__ = ".demoPrintDirectives";
        public static final String LONG_VAR_NAME = "longVarName";
        public static final String ELEMENT_ID = "elementId";
        public static final String CSS_CLASS = "cssClass";
        private static final DemoPrintDirectivesSoyTemplateInfo __INSTANCE__ = new DemoPrintDirectivesSoyTemplateInfo();

        private DemoPrintDirectivesSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)LONG_VAR_NAME, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)ELEMENT_ID, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)CSS_CLASS, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoPrintDirectivesSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoPrintSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoPrint";
        public static final String __PARTIAL_NAME__ = ".demoPrint";
        public static final String BOO = "boo";
        public static final String TWO = "two";
        private static final DemoPrintSoyTemplateInfo __INSTANCE__ = new DemoPrintSoyTemplateInfo();

        private DemoPrintSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.builder().put((Object)BOO, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).put((Object)TWO, (Object)SoyTemplateInfo.ParamRequisiteness.REQUIRED).build(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoPrintSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoRawTextCommandsSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoRawTextCommands";
        public static final String __PARTIAL_NAME__ = ".demoRawTextCommands";
        private static final DemoRawTextCommandsSoyTemplateInfo __INSTANCE__ = new DemoRawTextCommandsSoyTemplateInfo();

        private DemoRawTextCommandsSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.of(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoRawTextCommandsSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoLineJoiningSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoLineJoining";
        public static final String __PARTIAL_NAME__ = ".demoLineJoining";
        private static final DemoLineJoiningSoyTemplateInfo __INSTANCE__ = new DemoLineJoiningSoyTemplateInfo();

        private DemoLineJoiningSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.of(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoLineJoiningSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class DemoCommentsSoyTemplateInfo
    extends SoyTemplateInfo {
        public static final String __NAME__ = "soy.examples.features.demoComments";
        public static final String __PARTIAL_NAME__ = ".demoComments";
        private static final DemoCommentsSoyTemplateInfo __INSTANCE__ = new DemoCommentsSoyTemplateInfo();

        private DemoCommentsSoyTemplateInfo() {
            super(__NAME__, (ImmutableMap<String, SoyTemplateInfo.ParamRequisiteness>)ImmutableMap.of(), (ImmutableSortedSet<String>)ImmutableSortedSet.of(), false, false);
        }

        public static DemoCommentsSoyTemplateInfo getInstance() {
            return __INSTANCE__;
        }
    }

    public static final class Param {
        public static final String AUTHOR = "author";
        public static final String BOO = "boo";
        public static final String COMPANION_NAME = "companionName";
        public static final String CSS_CLASS = "cssClass";
        public static final String CURRENT_YEAR = "currentYear";
        public static final String DESTINATION = "destination";
        public static final String DESTINATIONS = "destinations";
        public static final String ELEMENT_ID = "elementId";
        public static final String EXAMPLE_NAME = "exampleName";
        public static final String EXAMPLE_NUM = "exampleNum";
        public static final String ITALIC_HTML = "italicHtml";
        public static final String ITEMS = "items";
        public static final String KEYWORDS = "keywords";
        public static final String LABS_URL = "labsUrl";
        public static final String LIST = "list";
        public static final String LIST_ITEMS = "listItems";
        public static final String LONG_VAR_NAME = "longVarName";
        public static final String MESSAGE = "message";
        public static final String NAME = "name";
        public static final String NUM_LINES = "numLines";
        public static final String PERSONS = "persons";
        public static final String PI = "pi";
        public static final String SET_MEMBERS = "setMembers";
        public static final String SET_NAME = "setName";
        public static final String STUDENTS = "students";
        public static final String TITLE = "title";
        public static final String TRIP_INFO = "tripInfo";
        public static final String TWO = "two";
        public static final String YEAR = "year";

        private Param() {
        }
    }

    public static final class TemplateName {
        public static final String DEMO_COMMENTS = "soy.examples.features.demoComments";
        public static final String DEMO_LINE_JOINING = "soy.examples.features.demoLineJoining";
        public static final String DEMO_RAW_TEXT_COMMANDS = "soy.examples.features.demoRawTextCommands";
        public static final String DEMO_PRINT = "soy.examples.features.demoPrint";
        public static final String DEMO_PRINT_DIRECTIVES = "soy.examples.features.demoPrintDirectives";
        public static final String DEMO_AUTOESCAPE_TRUE = "soy.examples.features.demoAutoescapeTrue";
        public static final String DEMO_AUTOESCAPE_FALSE = "soy.examples.features.demoAutoescapeFalse";
        public static final String DEMO_MSG = "soy.examples.features.demoMsg";
        public static final String DEMO_IF = "soy.examples.features.demoIf";
        public static final String DEMO_SWITCH = "soy.examples.features.demoSwitch";
        public static final String DEMO_FOREACH = "soy.examples.features.demoForeach";
        public static final String DEMO_FOR = "soy.examples.features.demoFor";
        public static final String DEMO_CALL_WITHOUT_PARAM = "soy.examples.features.demoCallWithoutParam";
        public static final String DEMO_CALL_WITH_PARAM = "soy.examples.features.demoCallWithParam";
        public static final String DEMO_CALL_WITH_PARAM_BLOCK = "soy.examples.features.demoCallWithParamBlock";
        public static final String DEMO_PARAM_WITH_KIND_ATTRIBUTE = "soy.examples.features.demoParamWithKindAttribute";
        public static final String DEMO_EXPRESSIONS = "soy.examples.features.demoExpressions";
        public static final String DEMO_DOUBLE_BRACES = "soy.examples.features.demoDoubleBraces";
        public static final String DEMO_BIDI_SUPPORT = "soy.examples.features.demoBidiSupport";
        public static final String BIDI_GLOBAL_DIR = "soy.examples.features.bidiGlobalDir";
        public static final String EXAMPLE_HEADER = "soy.examples.features.exampleHeader";

        private TemplateName() {
        }
    }
}


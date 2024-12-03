/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.SupportedCSS;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.css.TermList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportedCSS3
implements SupportedCSS {
    private static final Logger log = LoggerFactory.getLogger(SupportedCSS3.class);
    private static final int TOTAL_SUPPORTED_DECLARATIONS = 177;
    private static final TermFactory tf = CSSFactory.getTermFactory();
    private static final CSSProperty DEFAULT_UA_FONT_FAMILY = CSSProperty.FontFamily.SANS_SERIF;
    private static final CSSProperty DEFAULT_UA_TEXT_ALIGN = CSSProperty.TextAlign.BY_DIRECTION;
    private static final Term<?> DEFAULT_UA_COLOR = tf.createColor("#000000");
    private static final Term<?> DEFAULT_UA_OPACITY = tf.createNumber(Float.valueOf(1.0f));
    private static final Term<?> DEFAULT_UA_TEXT_IDENT = tf.createLength(Float.valueOf(0.0f));
    private static final Term<?> DEFAULT_UA_TAB_SIZE = tf.createInteger(8);
    private static final Term<?> DEFAULT_UA_MARGIN = tf.createLength(Float.valueOf(0.0f));
    private static final Term<?> DEFAULT_UA_PADDING = tf.createLength(Float.valueOf(0.0f));
    private static final Term<?> DEFAULT_UA_MIN_WIDTH = tf.createLength(Float.valueOf(0.0f));
    private static final Term<?> DEFAULT_UA_MIN_HEIGHT = tf.createLength(Float.valueOf(0.0f));
    private static final Term<?> DEFAULT_BORDER_COLOR = tf.createColor(tf.createIdent("currentColor"));
    private static final Term<?> DEFAULT_BACKGROUND_COLOR = tf.createColor(tf.createIdent("transparent"));
    private static final TermList DEFAULT_UA_BACKGROUND_POSITION = tf.createList(2);
    private static final Term<?> DEFAULT_FLEX_SHRINK = tf.createNumber(Float.valueOf(1.0f));
    private static final Term<?> DEFAULT_FLEX_GROW = tf.createNumber(Float.valueOf(0.0f));
    private static final Term<?> DEFAULT_ORDER = tf.createInteger(0);
    private static final Term<?> DEFAULT_TIME = tf.createTime(Float.valueOf(0.0f));
    private static final Term<?> DEFAULT_ITERATION_COUNT = tf.createInteger(1);
    private static final TermList DEFAULT_UA_BACKGROUND_SIZE;
    private static final TermList DEFAULT_UA_BORDER_RADIUS;
    private static final TermList DEFAULT_UA_BORDER_SPACING;
    private static final TermList DEFAULT_UA_TRANSFORM_ORIGIN;
    private static final Term<?> DEFAULT_UA_WIDOWS;
    private static final Term<?> DEFAULT_UA_ORPHANS;
    private static final Term<?> DEFAULT_UA_PAUSE_BEFORE;
    private static final Term<?> DEFAULT_UA_PAUSE_AFTER;
    private static final Term<?> DEFAULT_UA_RICHNESS;
    private static final Term<?> DEFAULT_UA_PITCH_RANGE;
    private static final Term<?> DEFAULT_UA_STRESS;
    private static final CSSProperty DEFAULT_UA_VOICE_FAMILY;
    private static final SupportedCSS3 instance;
    private Map<String, CSSProperty> defaultCSSproperties;
    private Map<String, Term<?>> defaultCSSvalues;
    private Map<String, Integer> ordinals;
    private Map<Integer, String> ordinalsRev;
    private Set<String> supportedMedia;

    public static final SupportedCSS3 getInstance() {
        return instance;
    }

    private SupportedCSS3() {
        this.setSupportedCSS();
        this.setOridinals();
        this.setSupportedAtKeywords();
    }

    @Override
    public boolean isSupportedMedia(String media) {
        if (media == null) {
            return false;
        }
        return this.supportedMedia.contains(media.toLowerCase());
    }

    @Override
    public final boolean isSupportedCSSProperty(String property) {
        return this.defaultCSSproperties.get(property) != null;
    }

    @Override
    public final CSSProperty getDefaultProperty(String property) {
        CSSProperty value = this.defaultCSSproperties.get(property);
        log.debug("Asked for property {}'s default value: {}", (Object)property, (Object)value);
        return value;
    }

    @Override
    public final Term<?> getDefaultValue(String property) {
        return this.defaultCSSvalues.get(property);
    }

    @Override
    public final int getTotalProperties() {
        return this.defaultCSSproperties.size();
    }

    @Override
    public final Set<String> getDefinedPropertyNames() {
        return this.defaultCSSproperties.keySet();
    }

    @Override
    public String getRandomPropertyName() {
        Random generator = new Random();
        int o = generator.nextInt(this.getTotalProperties());
        return this.getPropertyName(o);
    }

    @Override
    public int getOrdinal(String propertyName) {
        Integer i = this.ordinals.get(propertyName);
        return i == null ? -1 : i;
    }

    @Override
    public String getPropertyName(int o) {
        return this.ordinalsRev.get(o);
    }

    private void setSupportedCSS() {
        HashMap<String, CSSProperty> props = new HashMap<String, CSSProperty>(177, 1.0f);
        HashMap values = new HashMap(177, 1.0f);
        props.put("color", CSSProperty.Color.color);
        values.put("color", DEFAULT_UA_COLOR);
        props.put("opacity", CSSProperty.Opacity.number);
        values.put("opacity", DEFAULT_UA_OPACITY);
        props.put("font", CSSProperty.Font.component_values);
        props.put("font-family", DEFAULT_UA_FONT_FAMILY);
        props.put("font-size", CSSProperty.FontSize.MEDIUM);
        props.put("font-style", CSSProperty.FontStyle.NORMAL);
        props.put("font-variant", CSSProperty.FontVariant.NORMAL);
        props.put("font-weight", CSSProperty.FontWeight.NORMAL);
        props.put("text-decoration", CSSProperty.TextDecoration.NONE);
        props.put("text-transform", CSSProperty.TextTransform.NONE);
        props.put("white-space", CSSProperty.WhiteSpace.NORMAL);
        props.put("text-align", DEFAULT_UA_TEXT_ALIGN);
        props.put("text-indent", CSSProperty.TextIndent.length);
        values.put("text-indent", DEFAULT_UA_TEXT_IDENT);
        props.put("line-height", CSSProperty.LineHeight.NORMAL);
        props.put("word-spacing", CSSProperty.WordSpacing.NORMAL);
        props.put("letter-spacing", CSSProperty.LetterSpacing.NORMAL);
        props.put("vertical-align", CSSProperty.VerticalAlign.BASELINE);
        props.put("direction", CSSProperty.Direction.LTR);
        props.put("unicode-bidi", CSSProperty.UnicodeBidi.NORMAL);
        props.put("tab-size", CSSProperty.TabSize.integer);
        values.put("tab-size", DEFAULT_UA_TAB_SIZE);
        props.put("margin", CSSProperty.Margin.component_values);
        props.put("margin-top", CSSProperty.Margin.length);
        values.put("margin-top", DEFAULT_UA_MARGIN);
        props.put("margin-right", CSSProperty.Margin.length);
        values.put("margin-right", DEFAULT_UA_MARGIN);
        props.put("margin-bottom", CSSProperty.Margin.length);
        values.put("margin-bottom", DEFAULT_UA_MARGIN);
        props.put("margin-left", CSSProperty.Margin.length);
        values.put("margin-left", DEFAULT_UA_MARGIN);
        props.put("padding", CSSProperty.Padding.component_values);
        props.put("padding-top", CSSProperty.Padding.length);
        values.put("padding-top", DEFAULT_UA_PADDING);
        props.put("padding-right", CSSProperty.Padding.length);
        values.put("padding-right", DEFAULT_UA_PADDING);
        props.put("padding-bottom", CSSProperty.Padding.length);
        values.put("padding-bottom", DEFAULT_UA_PADDING);
        props.put("padding-left", CSSProperty.Padding.length);
        values.put("padding-left", DEFAULT_UA_PADDING);
        props.put("border", CSSProperty.Border.component_values);
        props.put("border-top", CSSProperty.Border.component_values);
        props.put("border-right", CSSProperty.Border.component_values);
        props.put("border-bottom", CSSProperty.Border.component_values);
        props.put("border-left", CSSProperty.Border.component_values);
        props.put("border-width", CSSProperty.BorderWidth.component_values);
        props.put("border-top-width", CSSProperty.BorderWidth.MEDIUM);
        props.put("border-right-width", CSSProperty.BorderWidth.MEDIUM);
        props.put("border-bottom-width", CSSProperty.BorderWidth.MEDIUM);
        props.put("border-left-width", CSSProperty.BorderWidth.MEDIUM);
        props.put("border-style", CSSProperty.BorderStyle.component_values);
        props.put("border-top-style", CSSProperty.BorderStyle.NONE);
        props.put("border-right-style", CSSProperty.BorderStyle.NONE);
        props.put("border-bottom-style", CSSProperty.BorderStyle.NONE);
        props.put("border-left-style", CSSProperty.BorderStyle.NONE);
        props.put("border-color", CSSProperty.BorderColor.component_values);
        props.put("border-top-color", CSSProperty.BorderColor.color);
        values.put("border-top-color", DEFAULT_BORDER_COLOR);
        props.put("border-right-color", CSSProperty.BorderColor.color);
        values.put("border-right-color", DEFAULT_BORDER_COLOR);
        props.put("border-bottom-color", CSSProperty.BorderColor.color);
        values.put("border-bottom-color", DEFAULT_BORDER_COLOR);
        props.put("border-left-color", CSSProperty.BorderColor.color);
        values.put("border-left-color", DEFAULT_BORDER_COLOR);
        props.put("border-radius", CSSProperty.BorderRadius.component_values);
        props.put("border-top-left-radius", CSSProperty.BorderRadius.list_values);
        values.put("border-top-left-radius", DEFAULT_UA_BORDER_RADIUS);
        props.put("border-top-right-radius", CSSProperty.BorderRadius.list_values);
        values.put("border-top-right-radius", DEFAULT_UA_BORDER_RADIUS);
        props.put("border-bottom-right-radius", CSSProperty.BorderRadius.list_values);
        values.put("border-bottom-right-radius", DEFAULT_UA_BORDER_RADIUS);
        props.put("border-bottom-left-radius", CSSProperty.BorderRadius.list_values);
        values.put("border-bottom-left-radius", DEFAULT_UA_BORDER_RADIUS);
        props.put("width", CSSProperty.Width.AUTO);
        props.put("min-width", CSSProperty.MinWidth.length);
        values.put("min-width", DEFAULT_UA_MIN_WIDTH);
        props.put("max-width", CSSProperty.MaxWidth.NONE);
        props.put("height", CSSProperty.Height.AUTO);
        props.put("min-height", CSSProperty.MinHeight.length);
        values.put("min-height", DEFAULT_UA_MIN_HEIGHT);
        props.put("max-height", CSSProperty.MaxHeight.NONE);
        props.put("overflow", CSSProperty.Overflow.component_values);
        props.put("overflow-x", CSSProperty.Overflow.VISIBLE);
        props.put("overflow-y", CSSProperty.Overflow.VISIBLE);
        props.put("clip", CSSProperty.Clip.AUTO);
        props.put("box-sizing", CSSProperty.BoxSizing.CONTENT_BOX);
        props.put("box-shadow", CSSProperty.BoxShadow.NONE);
        props.put("display", CSSProperty.Display.INLINE);
        props.put("position", CSSProperty.Position.STATIC);
        props.put("top", CSSProperty.Top.AUTO);
        props.put("right", CSSProperty.Right.AUTO);
        props.put("bottom", CSSProperty.Bottom.AUTO);
        props.put("left", CSSProperty.Left.AUTO);
        props.put("float", CSSProperty.Float.NONE);
        props.put("clear", CSSProperty.Clear.NONE);
        props.put("z-index", CSSProperty.ZIndex.AUTO);
        props.put("visibility", CSSProperty.Visibility.VISIBLE);
        props.put("transform", CSSProperty.Transform.NONE);
        props.put("transform-origin", CSSProperty.TransformOrigin.list_values);
        values.put("transform-origin", DEFAULT_UA_TRANSFORM_ORIGIN);
        props.put("background", CSSProperty.Background.component_values);
        props.put("background-attachment", CSSProperty.BackgroundAttachment.SCROLL);
        props.put("background-color", CSSProperty.BackgroundColor.color);
        values.put("background-color", DEFAULT_BACKGROUND_COLOR);
        props.put("background-image", CSSProperty.BackgroundImage.NONE);
        props.put("background-position", CSSProperty.BackgroundPosition.list_values);
        values.put("background-position", DEFAULT_UA_BACKGROUND_POSITION);
        props.put("background-size", CSSProperty.BackgroundSize.list_values);
        values.put("background-size", DEFAULT_UA_BACKGROUND_SIZE);
        props.put("background-repeat", CSSProperty.BackgroundRepeat.REPEAT);
        props.put("box-shadow", CSSProperty.BoxShadow.NONE);
        props.put("list-style", CSSProperty.ListStyle.component_values);
        props.put("list-style-type", CSSProperty.ListStyleType.DISC);
        props.put("list-style-position", CSSProperty.ListStylePosition.OUTSIDE);
        props.put("list-style-image", CSSProperty.ListStyleImage.NONE);
        props.put("border-collapse", CSSProperty.BorderCollapse.SEPARATE);
        props.put("border-spacing", CSSProperty.BorderSpacing.list_values);
        values.put("border-spacing", DEFAULT_UA_BORDER_SPACING);
        props.put("empty-cells", CSSProperty.EmptyCells.SHOW);
        props.put("table-layout", CSSProperty.TableLayout.AUTO);
        props.put("caption-side", CSSProperty.CaptionSide.TOP);
        props.put("content", CSSProperty.Content.NORMAL);
        props.put("quotes", CSSProperty.Quotes.NONE);
        props.put("counter-increment", CSSProperty.CounterIncrement.NONE);
        props.put("counter-reset", CSSProperty.CounterReset.NONE);
        props.put("filter", CSSProperty.Filter.NONE);
        props.put("backdrop-filter", CSSProperty.BackdropFilter.NONE);
        props.put("cursor", CSSProperty.Cursor.AUTO);
        props.put("outline", CSSProperty.Outline.component_values);
        props.put("outline-width", CSSProperty.OutlineWidth.MEDIUM);
        props.put("outline-style", CSSProperty.OutlineStyle.NONE);
        props.put("outline-color", CSSProperty.OutlineColor.INVERT);
        props.put("page-break", CSSProperty.PageBreak.AUTO);
        props.put("page-break-before", CSSProperty.PageBreak.AUTO);
        props.put("page-break-after", CSSProperty.PageBreak.AUTO);
        props.put("page-break-inside", CSSProperty.PageBreakInside.AUTO);
        props.put("widows", CSSProperty.Widows.integer);
        values.put("widows", DEFAULT_UA_WIDOWS);
        props.put("orphans", CSSProperty.Orphans.integer);
        values.put("orphans", DEFAULT_UA_ORPHANS);
        props.put("azimuth", CSSProperty.Azimuth.CENTER);
        props.put("cue", CSSProperty.Cue.component_values);
        props.put("cue-before", CSSProperty.Cue.NONE);
        props.put("cue-after", CSSProperty.Cue.NONE);
        props.put("elevation", CSSProperty.Elevation.LEVEL);
        props.put("pause", CSSProperty.Pause.component_values);
        props.put("pause-before", CSSProperty.Pause.time);
        values.put("pause-before", DEFAULT_UA_PAUSE_BEFORE);
        props.put("pause-after", CSSProperty.Pause.time);
        values.put("pause-after", DEFAULT_UA_PAUSE_AFTER);
        props.put("pitch-range", CSSProperty.PitchRange.number);
        values.put("pitch-range", DEFAULT_UA_PITCH_RANGE);
        props.put("pitch", CSSProperty.Pitch.MEDIUM);
        props.put("play-during", CSSProperty.PlayDuring.AUTO);
        props.put("richness", CSSProperty.Richness.number);
        values.put("richness", DEFAULT_UA_RICHNESS);
        props.put("speak-header", CSSProperty.SpeakHeader.ONCE);
        props.put("speak-numeral", CSSProperty.SpeakNumeral.CONTINUOUS);
        props.put("speak-punctuation", CSSProperty.SpeakPunctuation.NONE);
        props.put("speak", CSSProperty.Speak.NORMAL);
        props.put("speech-rate", CSSProperty.SpeechRate.MEDIUM);
        props.put("stress", CSSProperty.Stress.number);
        values.put("stress", DEFAULT_UA_STRESS);
        props.put("voice-family", DEFAULT_UA_VOICE_FAMILY);
        props.put("volume", CSSProperty.Volume.MEDIUM);
        props.put("flex", CSSProperty.Flex.component_values);
        props.put("flex-flow", CSSProperty.FlexFlow.component_values);
        props.put("flex-direction", CSSProperty.FlexDirection.ROW);
        props.put("flex-wrap", CSSProperty.FlexWrap.NOWRAP);
        props.put("flex-basis", CSSProperty.FlexBasis.AUTO);
        props.put("flex-grow", CSSProperty.FlexGrow.number);
        values.put("flex-grow", DEFAULT_FLEX_GROW);
        props.put("flex-shrink", CSSProperty.FlexShrink.number);
        values.put("flex-shrink", DEFAULT_FLEX_SHRINK);
        props.put("order", CSSProperty.Order.integer);
        values.put("order", DEFAULT_ORDER);
        props.put("justify-content", CSSProperty.JustifyContent.FLEX_START);
        props.put("align-content", CSSProperty.AlignContent.STRETCH);
        props.put("align-items", CSSProperty.AlignItems.STRETCH);
        props.put("align-self", CSSProperty.AlignSelf.AUTO);
        props.put("grid", CSSProperty.Grid.component_values);
        props.put("grid-gap", CSSProperty.GridGap.component_values);
        props.put("grid-row-gap", CSSProperty.GridGap.NORMAL);
        props.put("grid-column-gap", CSSProperty.GridGap.NORMAL);
        props.put("grid-area", CSSProperty.Grid.component_values);
        props.put("grid-row", CSSProperty.Grid.component_values);
        props.put("grid-column", CSSProperty.Grid.component_values);
        props.put("grid-row-start", CSSProperty.GridStartEnd.AUTO);
        props.put("grid-column-start", CSSProperty.GridStartEnd.AUTO);
        props.put("grid-row-end", CSSProperty.GridStartEnd.AUTO);
        props.put("grid-column-end", CSSProperty.GridStartEnd.AUTO);
        props.put("grid-template", CSSProperty.Grid.component_values);
        props.put("grid-template-areas", CSSProperty.GridTemplateAreas.NONE);
        props.put("grid-template-rows", CSSProperty.GridTemplateRowsColumns.NONE);
        props.put("grid-template-columns", CSSProperty.GridTemplateRowsColumns.NONE);
        props.put("grid-auto-flow", CSSProperty.GridAutoFlow.ROW);
        props.put("grid-auto-rows", CSSProperty.GridAutoRowsColumns.AUTO);
        props.put("grid-auto-columns", CSSProperty.GridAutoRowsColumns.AUTO);
        props.put("animation", CSSProperty.Animation.component_values);
        props.put("animation-delay", CSSProperty.AnimationDelay.time);
        values.put("animation-delay", DEFAULT_TIME);
        props.put("animation-direction", CSSProperty.AnimationDirection.NORMAL);
        props.put("animation-duration", CSSProperty.AnimationDuration.time);
        values.put("animation-duration", DEFAULT_TIME);
        props.put("animation-fill-mode", CSSProperty.AnimationFillMode.NONE);
        props.put("animation-iteration-count", CSSProperty.AnimationIterationCount.number);
        values.put("animation-iteration-count", DEFAULT_ITERATION_COUNT);
        props.put("animation-name", CSSProperty.AnimationName.NONE);
        props.put("animation-play-state", CSSProperty.AnimationPlayState.RUNNING);
        props.put("animation-timing-function", CSSProperty.AnimationTimingFunction.EASE);
        props.put("transition", CSSProperty.Transition.component_values);
        props.put("transition-delay", CSSProperty.TransitionDelay.time);
        values.put("transition-delay", DEFAULT_TIME);
        props.put("transition-duration", CSSProperty.TransitionDuration.time);
        values.put("transition-duration", DEFAULT_TIME);
        props.put("transition-property", CSSProperty.TransitionProperty.ALL);
        props.put("transition-timing-function", CSSProperty.TransitionTimingFunction.EASE);
        this.defaultCSSproperties = props;
        this.defaultCSSvalues = values;
    }

    private void setOridinals() {
        HashMap<String, Integer> ords = new HashMap<String, Integer>(this.getTotalProperties(), 1.0f);
        HashMap<Integer, String> ordsRev = new HashMap<Integer, String>(this.getTotalProperties(), 1.0f);
        int i = 0;
        for (String key : this.defaultCSSproperties.keySet()) {
            ords.put(key, i);
            ordsRev.put(i, key);
            ++i;
        }
        this.ordinals = ords;
        this.ordinalsRev = ordsRev;
    }

    private void setSupportedAtKeywords() {
        HashSet<String> set = new HashSet<String>(Arrays.asList("all", "braille", "embossed", "handheld", "print", "projection", "screen", "speech", "tty", "tv"));
        this.supportedMedia = set;
    }

    static {
        DEFAULT_UA_BACKGROUND_POSITION.add(tf.createPercent(Float.valueOf(0.0f)));
        DEFAULT_UA_BACKGROUND_POSITION.add(tf.createPercent(Float.valueOf(0.0f)));
        DEFAULT_UA_BACKGROUND_SIZE = tf.createList(2);
        DEFAULT_UA_BACKGROUND_SIZE.add(tf.createIdent("auto"));
        DEFAULT_UA_BACKGROUND_SIZE.add(tf.createIdent("auto"));
        DEFAULT_UA_BORDER_RADIUS = tf.createList(2);
        DEFAULT_UA_BORDER_RADIUS.add(tf.createLength(Float.valueOf(0.0f)));
        DEFAULT_UA_BORDER_RADIUS.add(tf.createLength(Float.valueOf(0.0f)));
        DEFAULT_UA_BORDER_SPACING = tf.createList(2);
        DEFAULT_UA_BORDER_SPACING.add(tf.createLength(Float.valueOf(0.0f)));
        DEFAULT_UA_BORDER_SPACING.add(tf.createLength(Float.valueOf(0.0f)));
        DEFAULT_UA_TRANSFORM_ORIGIN = tf.createList(3);
        DEFAULT_UA_TRANSFORM_ORIGIN.add(tf.createPercent(Float.valueOf(50.0f)));
        DEFAULT_UA_TRANSFORM_ORIGIN.add(tf.createPercent(Float.valueOf(50.0f)));
        DEFAULT_UA_TRANSFORM_ORIGIN.add(tf.createLength(Float.valueOf(0.0f)));
        DEFAULT_UA_WIDOWS = tf.createInteger(2);
        DEFAULT_UA_ORPHANS = tf.createInteger(2);
        DEFAULT_UA_PAUSE_BEFORE = tf.createTime(Float.valueOf(0.0f));
        DEFAULT_UA_PAUSE_AFTER = tf.createTime(Float.valueOf(0.0f));
        DEFAULT_UA_RICHNESS = tf.createNumber(Float.valueOf(50.0f));
        DEFAULT_UA_PITCH_RANGE = tf.createNumber(Float.valueOf(50.0f));
        DEFAULT_UA_STRESS = tf.createNumber(Float.valueOf(50.0f));
        DEFAULT_UA_VOICE_FAMILY = CSSProperty.VoiceFamily.MALE;
        instance = new SupportedCSS3();
    }
}


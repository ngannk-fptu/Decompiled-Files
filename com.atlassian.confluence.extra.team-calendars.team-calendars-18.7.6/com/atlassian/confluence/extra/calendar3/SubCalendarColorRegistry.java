/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3;

import java.util.Set;

public interface SubCalendarColorRegistry {
    public static final String DEFAULT_COLOUR = "subcalendar-blue";
    public static final String COLOUR_BLUE = "subcalendar-blue";
    public static final String COLOUR_BLUE2 = "subcalendar-blue2";
    public static final String COLOUR_TURQUOISE = "subcalendar-turquoise";
    public static final String COLOUR_GRAY = "subcalendar-gray";
    public static final String COLOUR_GRAY_2 = "subcalendar-gray2";
    public static final String COLOUR_RED = "subcalendar-red";
    public static final String COLOUR_PINK = "subcalendar-pink";
    public static final String COLOUR_PURPLE = "subcalendar-purple";
    public static final String COLOUR_PURPLE_2 = "subcalendar-purple2";
    public static final String COLOUR_PURPLE_3 = "subcalendar-purple3";
    public static final String COLOUR_PURPLE_4 = "subcalendar-purple4";
    public static final String COLOUR_GREEN = "subcalendar-green";
    public static final String COLOUR_GREEN_2 = "subcalendar-green2";
    public static final String COLOUR_GREEN_3 = "subcalendar-green3";
    public static final String COLOUR_GREEN_4 = "subcalendar-green4";
    public static final String COLOUR_GREEN_5 = "subcalendar-green5";
    public static final String COLOUR_GREEN_6 = "subcalendar-green6";
    public static final String COLOUR_YELLOW = "subcalendar-yellow";
    public static final String COLOUR_ORANGE = "subcalendar-orange";
    public static final String COLOUR_ORANGE_2 = "subcalendar-orange2";
    public static final String COLOUR_BRONZE = "subcalendar-bronze";

    public Set<String> getColorClasses();

    public String getColorHex(String var1);

    public String getLightenedColorHex(String var1);

    public String getEvenMoreLightenedColorHex(String var1);

    public String getEventMoreLightenedColourScheme(String var1);

    public boolean isEventMoreLightenedColourScheme(String var1);

    public String getRandomColourClass(String ... var1);
}


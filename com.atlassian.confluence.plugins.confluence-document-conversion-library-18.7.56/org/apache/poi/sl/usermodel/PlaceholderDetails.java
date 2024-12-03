/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.usermodel;

import java.time.format.DateTimeFormatter;
import org.apache.poi.sl.usermodel.Placeholder;

public interface PlaceholderDetails {
    public Placeholder getPlaceholder();

    public void setPlaceholder(Placeholder var1);

    public boolean isVisible();

    public void setVisible(boolean var1);

    public PlaceholderSize getSize();

    public void setSize(PlaceholderSize var1);

    public String getText();

    public void setText(String var1);

    default public String getUserDate() {
        return null;
    }

    default public DateTimeFormatter getDateFormat() {
        return DateTimeFormatter.ISO_LOCAL_DATE;
    }

    public static enum PlaceholderSize {
        quarter,
        half,
        full;

    }
}


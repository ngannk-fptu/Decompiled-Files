/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.AttributedString;
import java.text.NumberFormat;
import java.util.Locale;
import org.jfree.chart.labels.AbstractPieItemLabelGenerator;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;
import org.jfree.util.ObjectList;
import org.jfree.util.PublicCloneable;

public class StandardPieSectionLabelGenerator
extends AbstractPieItemLabelGenerator
implements PieSectionLabelGenerator,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 3064190563760203668L;
    public static final String DEFAULT_SECTION_LABEL_FORMAT = "{0}";
    private ObjectList attributedLabels = new ObjectList();

    public StandardPieSectionLabelGenerator() {
        this(DEFAULT_SECTION_LABEL_FORMAT, NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
    }

    public StandardPieSectionLabelGenerator(Locale locale) {
        this(DEFAULT_SECTION_LABEL_FORMAT, locale);
    }

    public StandardPieSectionLabelGenerator(String labelFormat) {
        this(labelFormat, NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
    }

    public StandardPieSectionLabelGenerator(String labelFormat, Locale locale) {
        this(labelFormat, NumberFormat.getNumberInstance(locale), NumberFormat.getPercentInstance(locale));
    }

    public StandardPieSectionLabelGenerator(String labelFormat, NumberFormat numberFormat, NumberFormat percentFormat) {
        super(labelFormat, numberFormat, percentFormat);
    }

    public AttributedString getAttributedLabel(int section) {
        return (AttributedString)this.attributedLabels.get(section);
    }

    public void setAttributedLabel(int section, AttributedString label) {
        this.attributedLabels.set(section, label);
    }

    public String generateSectionLabel(PieDataset dataset, Comparable key) {
        return super.generateSectionLabel(dataset, key);
    }

    public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
        return this.getAttributedLabel(dataset.getIndex(key));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardPieSectionLabelGenerator)) {
            return false;
        }
        StandardPieSectionLabelGenerator that = (StandardPieSectionLabelGenerator)obj;
        if (!this.attributedLabels.equals(that.attributedLabels)) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}


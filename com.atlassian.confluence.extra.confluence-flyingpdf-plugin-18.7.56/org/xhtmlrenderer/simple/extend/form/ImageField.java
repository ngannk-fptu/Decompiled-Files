/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.InputField;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.util.XRLog;

class ImageField
extends InputField {
    public ImageField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        FSDerivedValue heightValue;
        JButton button;
        FSImage fsImage;
        BufferedImage image = null;
        if (this.hasAttribute("src") && (fsImage = this.getUserAgentCallback().getImageResource(this.getAttribute("src")).getImage()) != null) {
            image = ((AWTFSImage)fsImage).getImage();
        }
        if (image == null) {
            button = new JButton("Image unreachable. " + this.getAttribute("alt"));
        } else {
            final ImageIcon imgIcon = new ImageIcon(image, this.getAttribute("alt"));
            final Image img = imgIcon.getImage();
            button = new JButton(){

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(imgIcon.getIconWidth(), imgIcon.getIconHeight());
                }
            };
        }
        button.setUI(new BasicButtonUI());
        button.setContentAreaFilled(false);
        CalculatedStyle style = this.getStyle();
        FSDerivedValue widthValue = style.valueByName(CSSName.WIDTH);
        if (widthValue instanceof LengthValue) {
            this.intrinsicWidth = new Integer(this.getBox().getContentWidth());
        }
        if ((heightValue = style.valueByName(CSSName.HEIGHT)) instanceof LengthValue) {
            this.intrinsicHeight = new Integer(this.getBox().getHeight());
        }
        button.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent event) {
                XRLog.layout("Image pressed: Submit");
                ImageField.this.getParentForm().submit(ImageField.this.getComponent());
            }
        });
        return button;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer;

import com.atlassian.renderer.Icon;
import com.atlassian.renderer.IconManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultIconManager
implements IconManager {
    private final String[] emoticons;
    private final Map<String, Icon> iconsMap;
    private final Map<String, Icon> emoticonsMap;

    public DefaultIconManager() {
        HashMap<String, Icon> map = new HashMap<String, Icon>();
        map.put(":-)", Icon.makeEmoticon("icons/emoticons/smile.png", 16, 16));
        map.put(":)", Icon.makeEmoticon("icons/emoticons/smile.png", 16, 16));
        map.put(":P", Icon.makeEmoticon("icons/emoticons/tongue.png", 16, 16));
        map.put(":p", Icon.makeEmoticon("icons/emoticons/tongue.png", 16, 16));
        map.put(";-)", Icon.makeEmoticon("icons/emoticons/wink.png", 16, 16));
        map.put(";)", Icon.makeEmoticon("icons/emoticons/wink.png", 16, 16));
        map.put(":D", Icon.makeEmoticon("icons/emoticons/biggrin.png", 16, 16));
        map.put(":-(", Icon.makeEmoticon("icons/emoticons/sad.png", 16, 16));
        map.put(":(", Icon.makeEmoticon("icons/emoticons/sad.png", 16, 16));
        map.put("(y)", Icon.makeEmoticon("icons/emoticons/thumbs_up.png", 16, 16));
        map.put("(n)", Icon.makeEmoticon("icons/emoticons/thumbs_down.png", 16, 16));
        map.put("(i)", Icon.makeEmoticon("icons/emoticons/information.png", 16, 16));
        map.put("(/)", Icon.makeEmoticon("icons/emoticons/check.png", 16, 16));
        map.put("(x)", Icon.makeEmoticon("icons/emoticons/error.png", 16, 16));
        map.put("(+)", Icon.makeEmoticon("icons/emoticons/add.png", 16, 16));
        map.put("(-)", Icon.makeEmoticon("icons/emoticons/forbidden.png", 16, 16));
        map.put("(!)", Icon.makeEmoticon("icons/emoticons/warning.png", 16, 16));
        map.put("(?)", Icon.makeEmoticon("icons/emoticons/help_16.png", 16, 16));
        map.put("(on)", Icon.makeEmoticon("icons/emoticons/lightbulb_on.png", 16, 16));
        map.put("(off)", Icon.makeEmoticon("icons/emoticons/lightbulb.png", 16, 16));
        map.put("(*)", Icon.makeEmoticon("icons/emoticons/star_yellow.png", 16, 16));
        map.put("(*b)", Icon.makeEmoticon("icons/emoticons/star_blue.png", 16, 16));
        map.put("(*y)", Icon.makeEmoticon("icons/emoticons/star_yellow.png", 16, 16));
        map.put("(*g)", Icon.makeEmoticon("icons/emoticons/star_green.png", 16, 16));
        map.put("(*r)", Icon.makeEmoticon("icons/emoticons/star_red.png", 16, 16));
        this.emoticonsMap = Collections.unmodifiableMap(map);
        HashMap<String, Icon> tempMap = new HashMap<String, Icon>();
        tempMap.put("mailto", Icon.makeRenderIcon("icons/mail_small.gif", 1, 12, 13));
        tempMap.put("external", Icon.makeRenderIcon("icons/linkext7.gif", 1, 7, 7));
        this.iconsMap = Collections.unmodifiableMap(tempMap);
        this.emoticons = new String[this.getEmoticonsMap().size()];
        int i = 0;
        Iterator<String> it = this.getEmoticonsMap().keySet().iterator();
        while (it.hasNext()) {
            String key;
            this.emoticons[i] = key = it.next();
            ++i;
        }
    }

    @Override
    public Icon getLinkDecoration(String iconName) {
        if (this.getIconsMap().containsKey(iconName)) {
            return this.getIconsMap().get(iconName);
        }
        return Icon.NULL_ICON;
    }

    @Override
    public Icon getEmoticon(String symbol) {
        if (this.getEmoticonsMap().containsKey(symbol)) {
            return this.getEmoticonsMap().get(symbol);
        }
        return Icon.NULL_ICON;
    }

    @Override
    public String[] getEmoticonSymbols() {
        return this.emoticons;
    }

    protected Map<String, Icon> getIconsMap() {
        return this.iconsMap;
    }

    protected Map<String, Icon> getEmoticonsMap() {
        return this.emoticonsMap;
    }
}


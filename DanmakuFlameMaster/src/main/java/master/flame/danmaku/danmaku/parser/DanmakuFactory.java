/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package master.flame.danmaku.danmaku.parser;

import android.text.TextUtils;
import master.flame.danmaku.danmaku.model.*;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;

public class DanmakuFactory {

    public static float BILI_PLAYER_WIDTH = 539;

    public static float BILI_PLAYER_HEIGHT = 385;

    public static long COMMON_DANMAKU_DURATION = 3800; // B站原始分辨率下弹幕存活时间
    
    public static final int DANMAKU_MEDIUM_TEXTSIZE = 25;

    public static long REAL_DANMAKU_DURATION = -1;

    public static long MAX_DANMAKU_DURATION = -1;

    public static long MIN_DANMAKU_DURATION = 4000;

    public static long MAX_DANMAKU_DURATION_HIGH_DENSITY = 9000;
    
    public static Duration Duration_Scroll_Danmaku;
    
    public static Duration Duration_Fix_Danmaku;

    public static BaseDanmaku createDanmaku(int type, float dispWidth) {
        if (REAL_DANMAKU_DURATION == -1) {
            REAL_DANMAKU_DURATION = (long) (COMMON_DANMAKU_DURATION * (dispWidth / BILI_PLAYER_WIDTH));
            REAL_DANMAKU_DURATION = Math.min(MAX_DANMAKU_DURATION_HIGH_DENSITY, REAL_DANMAKU_DURATION);
            REAL_DANMAKU_DURATION = Math.max(MIN_DANMAKU_DURATION, REAL_DANMAKU_DURATION);
        }
        if(Duration_Scroll_Danmaku == null){
            Duration_Scroll_Danmaku = new Duration(REAL_DANMAKU_DURATION);
            Duration_Scroll_Danmaku.setFactor(DanmakuGlobalConfig.DEFAULT.scrollSpeedFactor);
        }
        if(Duration_Fix_Danmaku == null){
            Duration_Fix_Danmaku = new Duration(COMMON_DANMAKU_DURATION);
        }
        if (MAX_DANMAKU_DURATION == -1) {
            MAX_DANMAKU_DURATION = Math.max(Duration_Scroll_Danmaku.value, Duration_Fix_Danmaku.value);
            MAX_DANMAKU_DURATION = Math.max(COMMON_DANMAKU_DURATION, MAX_DANMAKU_DURATION);
            MAX_DANMAKU_DURATION = Math.max(REAL_DANMAKU_DURATION, MAX_DANMAKU_DURATION);
        }        

        BaseDanmaku instance = null;
        switch (type) {
            case 1: // 从右往左滚动
                instance = new R2LDanmaku(Duration_Scroll_Danmaku);
                break;
            case 4: // 底端固定
                instance = new FBDanmaku(Duration_Fix_Danmaku);
                break;
            case 5: // 顶端固定
                instance = new FTDanmaku(Duration_Fix_Danmaku);
                break;
            case 6: // 从左往右滚动
                instance = new L2RDanmaku(Duration_Scroll_Danmaku);
                break;
            case 7: // 特殊弹幕
                instance = new SpecialDanmaku();
                break;
        // TODO: more Danmaku type
        }
        return instance;
    }
    
    public static void updateDurationFactor(float f) {
        if (Duration_Scroll_Danmaku == null || Duration_Fix_Danmaku == null)
            return;
        Duration_Scroll_Danmaku.setFactor(f);
        MAX_DANMAKU_DURATION = Math.max(Duration_Scroll_Danmaku.value, Duration_Fix_Danmaku.value);
        MAX_DANMAKU_DURATION = Math.max(COMMON_DANMAKU_DURATION, MAX_DANMAKU_DURATION);
        MAX_DANMAKU_DURATION = Math.max(REAL_DANMAKU_DURATION, MAX_DANMAKU_DURATION);
    }
    
    public static void fillText(BaseDanmaku danmaku, String text) {
        danmaku.text = text;
        if (TextUtils.isEmpty(text) || text.indexOf(BaseDanmaku.DANMAKU_BR_CHAR) == -1) {
            return;
        }

        String[] lines = danmaku.text.split(BaseDanmaku.DANMAKU_BR_CHAR);
        if (lines.length > 1) {
            danmaku.lines = lines;
        }
    }

    /**
     * Initial translation data of the special danmaku
     * 
     * @param item
     * @param dispWidth
     * @param dispHeight
     * @param beginX
     * @param beginY
     * @param endX
     * @param endY
     * @param translationDuration
     * @param translationStartDelay
     */
    public static void fillTranslationData(BaseDanmaku item, int dispWidth, int dispHeight,
            float beginX, float beginY, float endX, float endY, long translationDuration,
            long translationStartDelay) {
        if (item.getType() != BaseDanmaku.TYPE_SPECIAL)
            return;
        float scaleX = dispWidth / BILI_PLAYER_WIDTH;
        float scaleY = dispHeight / BILI_PLAYER_HEIGHT;
        ((SpecialDanmaku) item).setTranslationData(beginX * scaleX, beginY * scaleY, endX * scaleX,
                endY * scaleY, translationDuration, translationStartDelay);
    }

    /**
     * Initial alpha data of the special danmaku
     * 
     * @param item
     * @param beginAlpha
     * @param endAlpha
     * @param alphaDuraion
     */
    public static void fillAlphaData(BaseDanmaku item, int beginAlpha, int endAlpha,
            long alphaDuraion) {
        if (item.getType() != BaseDanmaku.TYPE_SPECIAL)
            return;
        ((SpecialDanmaku) item).setAlphaData(beginAlpha, endAlpha, alphaDuraion);
    }
}

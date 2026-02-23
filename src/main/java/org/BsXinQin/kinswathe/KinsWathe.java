package org.BsXinQin.kinswathe;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KinsWathe implements ModInitializer {

    public static String MOD_ID = "kinswathe";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    // 其余代码保持不变

    @Override
    public void onInitialize() {
        //初始化游戏设置
        KinsWatheGameSettings.init();
        //初始化角色
        KinsWatheRoles.init();
        //初始化物品
        KinsWatheItems.init();
    }
}
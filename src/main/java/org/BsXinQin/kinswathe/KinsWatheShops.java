package org.BsXinQin.kinswathe;

import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.BsXinQin.kinswathe.component.ConfigWorldComponent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KinsWatheShops {

    private static final Map<String, Integer> ITEM_PRICES = new HashMap<>();
    private static ArrayList<ShopEntry> FRAMING_ROLES_SHOP;

    /// 提取其他模组商店物品价格
    static {
        for (@NotNull ShopEntry entry : GameConstants.SHOP_ENTRIES) {
            String itemKey = getItemKeyFromStack(entry.stack());
            if (itemKey != null) {
                ITEM_PRICES.put(itemKey, entry.price());
            }
        }
        if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
            try {
                Class<?> noellesRolesClass = Class.forName("org.agmas.noellesroles.Noellesroles");
                Field framingShopField = noellesRolesClass.getField("FRAMING_ROLES_SHOP");
                FRAMING_ROLES_SHOP = (ArrayList<ShopEntry>) framingShopField.get(null);
            } catch (Exception exception) {
                FRAMING_ROLES_SHOP = new ArrayList<>();
            }
        }
    }

    private static String getItemKeyFromStack(@NotNull ItemStack stack) {
        if (stack.getItem() == WatheItems.KNIFE) return "KNIFE";
        if (stack.getItem() == WatheItems.REVOLVER) return "REVOLVER";
        if (stack.getItem() == WatheItems.GRENADE) return "GRENADE";
        if (stack.getItem() == WatheItems.PSYCHO_MODE) return "PSYCHO_MODE";
        if (stack.getItem() == WatheItems.POISON_VIAL) return "POISON_VIAL";
        if (stack.getItem() == WatheItems.SCORPION) return "SCORPION";
        if (stack.getItem() == WatheItems.FIRECRACKER) return "FIRECRACKER";
        if (stack.getItem() == WatheItems.LOCKPICK) return "LOCKPICK";
        if (stack.getItem() == WatheItems.CROWBAR) return "CROWBAR";
        if (stack.getItem() == WatheItems.BODY_BAG) return "BODY_BAG";
        if (stack.getItem() == WatheItems.BLACKOUT) return "BLACKOUT";
        if (stack.getItem() == WatheItems.NOTE) return "NOTE";
        return null;
    }

    private static int getItemPrice(@NotNull String itemKey, int defaultValue) {
        return ITEM_PRICES.getOrDefault(itemKey, defaultValue);
    }

    /// 身份商店
    //制毒师商店
    public static List<ShopEntry> getDrugmakerShop(@NotNull World world) {
        return Util.make(new ArrayList<>(), (entries) -> {
            entries.add(new ShopEntry(KinsWatheItems.POISON_INJECTOR.getDefaultStack(), ConfigWorldComponent.KEY.get(world).DrugmakerPoisonInjectorPrice, ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(KinsWatheItems.BLOWGUN.getDefaultStack(), ConfigWorldComponent.KEY.get(world).DrugmakerBlowgunPrice, ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.KNIFE.getDefaultStack(), getItemPrice("KNIFE", 100) * 2, ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.POISON_VIAL.getDefaultStack(), getItemPrice("POISON_VIAL", 100) / 2, ShopEntry.Type.POISON));
            entries.add(new ShopEntry(WatheItems.SCORPION.getDefaultStack(), getItemPrice("SCORPION", 50) / 2, ShopEntry.Type.POISON));
            entries.add(new ShopEntry(WatheItems.FIRECRACKER.getDefaultStack(), getItemPrice("FIRECRACKER", 10), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.LOCKPICK.getDefaultStack(), getItemPrice("LOCKPICK", 50), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.CROWBAR.getDefaultStack(), getItemPrice("CROWBAR", 25), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.BODY_BAG.getDefaultStack(), getItemPrice("BODY_BAG", 200), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.BLACKOUT.getDefaultStack(), getItemPrice("BLACKOUT", 200), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.NOTE.getDefaultStack(), getItemPrice("NOTE", 10), ShopEntry.Type.TOOL));
        });
    }
    //追猎者商店
    public static List<ShopEntry> getHunterShop(@NotNull World world) {
        return Util.make(new ArrayList<>(), (entries) -> {
            entries.add(new ShopEntry(KinsWatheItems.HUNTING_KNIFE.getDefaultStack(), getItemPrice("KNIFE", 100), ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.KNIFE.getDefaultStack(), getItemPrice("KNIFE", 100) * 7 / 4, ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.REVOLVER.getDefaultStack(), getItemPrice("REVOLVER", 300), ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.GRENADE.getDefaultStack(), getItemPrice("GRENADE", 350), ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.PSYCHO_MODE.getDefaultStack(), getItemPrice("PSYCHO_MODE", 300), ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.POISON_VIAL.getDefaultStack(), getItemPrice("POISON_VIAL", 100), ShopEntry.Type.POISON));
            entries.add(new ShopEntry(WatheItems.SCORPION.getDefaultStack(), getItemPrice("SCORPION", 50), ShopEntry.Type.POISON));
            entries.add(new ShopEntry(WatheItems.FIRECRACKER.getDefaultStack(), getItemPrice("FIRECRACKER", 10), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.LOCKPICK.getDefaultStack(), getItemPrice("LOCKPICK", 50), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.CROWBAR.getDefaultStack(), getItemPrice("CROWBAR", 25), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.BODY_BAG.getDefaultStack(), getItemPrice("BODY_BAG", 200), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.BLACKOUT.getDefaultStack(), getItemPrice("BLACKOUT", 200), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.NOTE.getDefaultStack(), getItemPrice("NOTE", 10), ShopEntry.Type.TOOL));
        });
    }
    //绑匪商店
    public static List<ShopEntry> getKidnapperShop(@NotNull World world) {
        return Util.make(new ArrayList<>(), (entries) -> {
            entries.add(new ShopEntry(WatheItems.KNIFE.getDefaultStack(), getItemPrice("KNIFE", 100), ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(KinsWatheItems.KNOCKOUT_DRUG.getDefaultStack(), ConfigWorldComponent.KEY.get(world).KidnapperKnockoutDrugPrice, ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.REVOLVER.getDefaultStack(), getItemPrice("REVOLVER", 300), ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.GRENADE.getDefaultStack(), getItemPrice("GRENADE", 350), ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.PSYCHO_MODE.getDefaultStack(), getItemPrice("PSYCHO_MODE", 300), ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.POISON_VIAL.getDefaultStack(), getItemPrice("POISON_VIAL", 100), ShopEntry.Type.POISON));
            entries.add(new ShopEntry(WatheItems.SCORPION.getDefaultStack(), getItemPrice("SCORPION", 50), ShopEntry.Type.POISON));
            entries.add(new ShopEntry(WatheItems.FIRECRACKER.getDefaultStack(), getItemPrice("FIRECRACKER", 10), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.LOCKPICK.getDefaultStack(), getItemPrice("LOCKPICK", 50), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.CROWBAR.getDefaultStack(), getItemPrice("CROWBAR", 25), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.BODY_BAG.getDefaultStack(), getItemPrice("BODY_BAG", 200), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.BLACKOUT.getDefaultStack(), getItemPrice("BLACKOUT", 200), ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.NOTE.getDefaultStack(), getItemPrice("NOTE", 10), ShopEntry.Type.TOOL));
        });
    }
    //杀手方中立商店
    public static List<ShopEntry> getKillerNeutralRolesShop() {
        return FRAMING_ROLES_SHOP;
    }
}
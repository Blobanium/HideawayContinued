package continued.hideaway.mod.feat.shop;

import continued.hideaway.mod.HideawayPlus;
import continued.hideaway.mod.feat.keyboard.KeyboardManager;
import continued.hideaway.mod.feat.ui.FriendsListUI;
import continued.hideaway.mod.util.StaticValues;
import continued.hideaway.mod.feat.ext.AbstractContainerScreenAccessor;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Shop {

    private static boolean fill;

    public void tick() {
        if (getShopName() == null) return;
        String shopName = getShopName();

        if (GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), KeyBindingHelper.getBoundKeyOf(KeyboardManager.autoSell).getValue()) == GLFW.GLFW_PRESS) {
            fill = true;
        }

        if (("fruit".equals(shopName) || "fish".equals(shopName)) && (HideawayPlus.config().autoSell() || fill)) {
            StaticValues.shopIterationNum = 0;
            fill = false;
            List<Slot> emptyChestSlots = new ArrayList<>();
            List<Slot> playerEmptySlots = new ArrayList<>();
            AbstractContainerScreen<ChestMenu> menu = (AbstractContainerScreen<ChestMenu>) HideawayPlus.client().screen;
            ChestMenu chestMenu = menu.getMenu();

            for (Slot slot : chestMenu.slots) {
                if (slot.getItem().getItem() != Items.AIR) {
                    if (slot.container instanceof Inventory) {
                        playerEmptySlots.add(slot);
                    }
                } else if (!(slot.container instanceof Inventory)) {
                    emptyChestSlots.add(slot);
                }
            }

            for (int i = StaticValues.shopIterationNum; i < playerEmptySlots.size() && !emptyChestSlots.isEmpty() && !StaticValues.shopScreenWasFilled; i++) {
                Slot playerSlot = playerEmptySlots.get(i);

                ((AbstractContainerScreenAccessor)menu).hp$slotChange(playerSlot, emptyChestSlots.get(0).index, 0, ClickType.QUICK_MOVE);

                Iterator<Slot> chestSlotIterator = emptyChestSlots.iterator();
                while (chestSlotIterator.hasNext()) {
                    Slot chestSlot = chestSlotIterator.next();
                    if (chestSlot.getItem().getItem() != Items.AIR) {
                        chestSlotIterator.remove();
                        break;
                    }
                }

                if (emptyChestSlots.isEmpty()) StaticValues.shopIterationNum++;
                if (i == playerEmptySlots.size() -1) StaticValues.shopScreenWasFilled = true;
                if (StaticValues.shopIterationNum >= playerEmptySlots.size()) StaticValues.shopScreenWasFilled = true;
            }
        }
    }

    private String getShopName() {
        ChestMenu screen = ((AbstractContainerScreen<ChestMenu>) HideawayPlus.client().screen).getMenu();
        String screenName = HideawayPlus.client().screen.getTitle().getString();
        if (screenName.contains("\uE00C") || screenName.contains("\uE010")) { FriendsListUI.tick(); return null; }


        for (ItemStack itemStack : screen.getItems()) {
            if (!itemStack.hasTag()) continue;
            String nbtData = itemStack.getTag().getAsString();

            if (nbtData.contains("buy")) {
                if (nbtData.contains("The Marmoset Monkey Brothers")) return "fruit";
                if (nbtData.contains("Bill Beaks")) return "fish";
            } else if (nbtData.contains("same rarity")) { return "trader"; }
        }

        return null;
    }
}

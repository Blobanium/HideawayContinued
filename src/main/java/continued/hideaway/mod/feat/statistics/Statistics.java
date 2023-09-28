package continued.hideaway.mod.feat.statistics;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class Statistics {
    public static void init(ItemStack itemStack, TooltipFlag tooltipFlag, List<Component> components) {
        if(Minecraft.getInstance().screen.getTitle().getString().contains("\ue2c7")){
            String a = itemStack.getTagElement("PublicBukkitValues").getString("pixelhideawaycore:item_id");

        }
    }
}

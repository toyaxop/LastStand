package fr.iglee42.laststand;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)

public class LastStandEnchant extends Enchantment {

    public static final ResourceLocation ID = new ResourceLocation(LastStand.MODID,"last_stand");
    public LastStandEnchant() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET});
        setRegistryName(new ResourceLocation(LastStand.MODID,"last_stand"));
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinCost(int level) {
        if (level == 1) {
            return 15;
        }
        return 25;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 10;
    }

    @SubscribeEvent
    public static void onHurt(final LivingHurtEvent e) {
        if (!(e.getEntityLiving() instanceof Player player)) return;

        final int enchantmentLevels = EnchantmentHelper.getEnchantmentLevel(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getValue(ID)), player);

        if (enchantmentLevels > 0) {
            final float playerHealth = player.getHealth();
            final float healthAvailable = playerHealth - e.getAmount();

            if (healthAvailable < 1f) {
                final int xpAvailable = getPlayerXP(player);


                double xpRequired = 1D - healthAvailable;
                xpRequired *= 50;
                xpRequired /= enchantmentLevels;
                xpRequired = Math.max(1, xpRequired);


                if (xpAvailable >= xpRequired) {
                    player.setHealth(1f);
                    player.giveExperiencePoints((int) -xpRequired);
                    e.setAmount(0);
                    e.setCanceled(true);
                }
            }
        }
    }
    public static int getPlayerXP(Player player) {
        return (int)(getExperienceForLevel(player.experienceLevel) + player.totalExperience);
    }

    private static int sum(int n, int a0, int d) {
        return n * (2 * a0 + (n - 1) * d) / 2;
    }

    public static int getExperienceForLevel(int level) {
        if (level == 0) return 0;
        if (level <= 15) return sum(level, 7, 2);
        if (level <= 30) return 315 + sum(level - 15, 37, 5);
        return 1395 + sum(level - 30, 112, 9);
    }



}

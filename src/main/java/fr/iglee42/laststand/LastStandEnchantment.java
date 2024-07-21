package fr.iglee42.laststand;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;

public class LastStandEnchantment  extends Enchantment {

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registry.ENCHANTMENT, new Identifier(LastStand.MOD_ID, name), enchantment);
    }

    public static Enchantment LAST_STAND = register("last_stand", new LastStandEnchantment(Rarity.UNCOMMON,
            EnchantmentTarget.ARMOR, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.CHEST));

    public LastStandEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot... slotTypes) {
        super(weight, target, slotTypes);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if(!entity.isPlayer() || entity.getWorld().isClient()) return true;
            PlayerEntity player = (PlayerEntity) entity;
            for(ItemStack i : entity.getArmorItems()){
                int enchantmentLevels = EnchantmentHelper.getLevel(LAST_STAND, i);
                if(enchantmentLevels > 0) {
                    final float playerHealth = player.getHealth();
                    final float healthAvailable = playerHealth - amount;

                    if (healthAvailable < 1f) {
                        final int xpAvailable = getPlayerXP(player);

                        double xpRequired = 1D - healthAvailable;
                        xpRequired *= 50;
                        xpRequired /= enchantmentLevels;
                        xpRequired = Math.max(1, xpRequired);


                        if (xpAvailable >= xpRequired) {
                            player.setHealth(1f);
                            player.addExperience((int) -xpRequired);
                            return false;
                        }
                    }
                }
            }
            return true;
        });
    }

    public static int getPlayerXP(PlayerEntity player) {
        return getExperienceForLevel(player.experienceLevel) + player.totalExperience;
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

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinPower(int level) {
        if (level == 1) {
            return 15;
        }
        return 25;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 10;
    }

    public static void registerModEnchantments(){
        LastStand.LOGGER.info("Registering Enchants for " + LastStand.MOD_ID);
    }
}

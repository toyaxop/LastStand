package fr.iglee42.laststand;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastStand implements ModInitializer {

    public static final String MOD_ID = "laststand";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LastStandEnchantment.registerModEnchantments();
    }
}

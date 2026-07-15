package io.github.riiimc.riiimc_leveling

import com.mojang.logging.LogUtils
import io.github.riiimc.riiimc_leveling.registries.LevelingRegistry
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Blocks
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.server.ServerStartingEvent
import org.slf4j.Logger
import java.util.function.Consumer

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(RiiiMcLeveling.Companion.MODID)
class RiiiMcLeveling(modEventBus: IEventBus, modContainer: ModContainer) {
    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    init {
        // Register the commonSetup method for modloading
        modEventBus.addListener<FMLCommonSetupEvent?>(Consumer { event: FMLCommonSetupEvent? -> this.commonSetup(event) })

        LevelingRegistry.registerTab(modEventBus)
        LevelingRegistry.DATA_COMPONENTS.register(modEventBus)
        LevelingRegistry.BLOCKS.register(modEventBus)
        LevelingRegistry.ITEMS.register(modEventBus)
        LevelingRegistry.MENU.register(modEventBus)
        LevelingRegistry.BLOCK_ENTITY_TYPES.register(modEventBus)
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (RiiiMcLeveling) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this)


        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, LevelingConfig.SPEC)
    }

    private fun commonSetup(event: FMLCommonSetupEvent?) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP")

        if (LevelingConfig.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT))

        LOGGER.info(LevelingConfig.magicNumberIntroduction + LevelingConfig.magicNumber)

    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent?) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting")
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    object ClientModEvents {
        @JvmStatic
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent?) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP")
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName())
        }
    }

    companion object {
        // Define mod id in a common place for everything to reference
        const val MODID: String = "riiimc_leveling"

        // Directly reference a slf4j logger
        private val LOGGER: Logger = LogUtils.getLogger()

        // Create a Deferred Register to hold Blocks which will all be registered under the "riiimc_leveling" namespace

        // Creates a creative tab with the id "riiimc_leveling:example_tab" for the example item, that is placed after the combat tab
    }

}

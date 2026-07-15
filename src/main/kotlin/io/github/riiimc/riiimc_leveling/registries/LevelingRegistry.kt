package io.github.riiimc.riiimc_leveling.registries

import io.github.riiimc.riiimc_leveling.RiiiMcLeveling
import io.github.riiimc.riiimc_leveling.blockentity.LevelingTableBlockEntity
import io.github.riiimc.riiimc_leveling.blocks.LevelingTableBlock
import io.github.riiimc.riiimc_leveling.components.ToolAttributeData
import io.github.riiimc.riiimc_leveling.components.ToolLevelData
import io.github.riiimc.riiimc_leveling.items.HammerItem
import io.github.riiimc.riiimc_leveling.items.upgrades.UpgradePlateItem
import io.github.riiimc.riiimc_leveling.menu.LevelingTableMenu
import io.github.riiimc.riiimc_leveling.utils.LevelingUtils
import net.mcexpanded.fancytabsections.FancyTabSections
import net.mcexpanded.fancytabsections.Section.SectionColored
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object LevelingRegistry {
    lateinit var upgradeTab: Unit
    val DATA_COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RiiiMcLeveling.MODID)
    val BLOCKS = DeferredRegister.createBlocks(RiiiMcLeveling.MODID)
    val BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RiiiMcLeveling.MODID)

    @JvmField
    val TOOL_LEVEL = DATA_COMPONENTS.register("tool_level", Supplier {
        DataComponentType.builder<ToolLevelData>()
            .persistent(ToolLevelData.CODEC)
            .networkSynchronized(ToolLevelData.STREAM_CODEC)
            .build()
    })

    /*
    @JvmField
    val UPGRADE_MODIFIER = DATA_COMPONENTS.register("upgrade_modifier", Supplier {
        DataComponentType.builder<UpgradeModifier>()
            .persistent(UpgradeModifier.CODEC)
            .networkSynchronized(UpgradeModifier.STREAM_CODEC)
            .build()
    })
    @JvmField
    val UPGRADE_PLATE_DATA = DATA_COMPONENTS.register("upgrade_plate", Supplier {
        DataComponentType.builder<UpgradePlateData>()
            .persistent(UpgradePlateData.CODEC)
            .networkSynchronized(UpgradePlateData.STREAM_CODEC)
            .build()
    })

     */
    val LEVELING_TABLE = BLOCKS.register("leveling_table", Supplier {
        LevelingTableBlock(
            BlockBehaviour.Properties.of()
                .strength(2.5f)
                .requiresCorrectToolForDrops()
        )
    })

    val ITEMS = DeferredRegister.createItems(RiiiMcLeveling.MODID)

    val LEVELING_TABLE_ITEM = ITEMS.register("leveling_table", Supplier {
        BlockItem(
            LEVELING_TABLE.get(),
            Item.Properties()
        )
    })

    val MENU = DeferredRegister.create(Registries.MENU, RiiiMcLeveling.MODID)

    val LEVELING_TABLE_MENU = MENU.register("leveling_table", Supplier {
        IMenuTypeExtension.create { windowId, playerInv, buf ->
            val pos = buf.readBlockPos()
            val be = playerInv.player.level()
                .getBlockEntity(pos) as LevelingTableBlockEntity

            LevelingTableMenu(windowId, playerInv, be)
        }
    })
    val LEVELING_TABLE_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register("leveling_table", Supplier {
        BlockEntityType.Builder.of(
            ::LevelingTableBlockEntity,
            LEVELING_TABLE.get()
        ).build(null)
    })

    val ATTACK_UPGRADE_ITEM = registerUpgrade(
        "attack_upgrade",
        listOf(
            AttributeModifier(
                LevelingUtils.rl("attack_upgrade"),
                1.0,
                AttributeModifier.Operation.ADD_VALUE
            )
        ),
        EquipmentSlotGroup.MAINHAND,
        LevelingUtils.mcRl("generic.attack_damage")
    )
    // val a = UpgradePlateRegistry.register(ATTACK_UPGRADE_ITEM.get() as IUpgradePlate)

    val DEFENCE_UPGRADE_ITEM = registerUpgrade(
        "defence_upgrade",
        listOf(
            AttributeModifier(
                LevelingUtils.rl("defence_upgrade"),
                1.0,
                AttributeModifier.Operation.ADD_VALUE
            )
        ),
        EquipmentSlotGroup.ARMOR,
        LevelingUtils.mcRl("generic.armor")
    )

    val CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RiiiMcLeveling.MODID)

    val LEVELING_TAB = CREATIVE_TABS.register("leveling", Supplier {
        CreativeModeTab.builder().icon { ItemStack(LEVELING_TABLE_ITEM.get()) }
            .title(Component.translatable("itemGroup.riiimc_leveling.leveling"))
            .build()
    })

    fun registerTab(eventBus: IEventBus) {
        CREATIVE_TABS.register(eventBus)
        addItems()
    }

    fun registerUpgrade(
        name: String,
        attributes: List<AttributeModifier>,
        slot: EquipmentSlotGroup,
        attributeId: ResourceLocation
    ): DeferredItem<UpgradePlateItem> = ITEMS.register(name, Supplier {
        object : UpgradePlateItem(Properties()) {
            override val modifiers = attributes.mapIndexed { index, attribute ->
                ToolAttributeData(
                    attribute,
                    slot,
                    attributeId
                )
            }

            val tab = FancyTabSections.getSection(LevelingUtils.rl("upgrades")).add(this)
        }

    })
    private fun addItems() {
        FancyTabSections.addSection(LEVELING_TAB.id, SectionColored(
            LevelingUtils.rl("misc")
        )
            .setBannerColor(0xFF0000)
            .add(LEVELING_TABLE_ITEM)
            .add(MATERIAL_ITEM)
            .add(STONE_HAMMER)
        )
        upgradeTab = FancyTabSections.addSection(LEVELING_TAB.id, SectionColored(
            LevelingUtils.rl("upgrades")
        )
            .setBannerColor(0xFF0000)
        )
    }

    val MATERIAL_ITEM = ITEMS.register("basic_material", Supplier {
        Item(Item.Properties())
    })
    val OPEN_GUI_ITEM = ITEMS.register("debug1", Supplier {
        Item(Item.Properties())
    })

    val STONE_HAMMER = ITEMS.register("stone_hammer", Supplier {
        HammerItem(Item.Properties().stacksTo(1))
    })
}
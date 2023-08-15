package com.github.appable0.miningspeedboost

import com.github.appable0.miningspeedboost.Utils.skyblockID
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
@Mod(modid = "miningspeedboost", useMetadata = true)
class MiningSpeedBoost {
    private var pickaxeAbilityLastUsed: TimeMark? = null

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(Location)
    }


    @SubscribeEvent
    fun onChatReceived(event: ClientChatReceivedEvent) {
        val message = StringUtils.stripControlCodes(event.message.unformattedText)
        if (message == "You used your Mining Speed Boost Pickaxe Ability!") {
            pickaxeAbilityLastUsed = TimeSource.Monotonic.markNow()
        }
    }

    @SubscribeEvent
    fun onItemOverlay(event: ItemOverlayEvent) {
        if (!Location.inSkyblock) return
        if (!isPickaxe(event.item?.skyblockID)) return
        val lastUsed = pickaxeAbilityLastUsed ?: return
        val elapsed = lastUsed.elapsedNow().toDouble(DurationUnit.SECONDS)
        val durability = elapsed / 120
        if (durability > 1) return
        Utils.renderDurabilityBar(event.x, event.y, durability)
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        pickaxeAbilityLastUsed = TimeSource.Monotonic.markNow() - 60.seconds
    }

    // stolen from NEU
    private fun isPickaxe(skyblockId: String?): Boolean {
        if (skyblockId == null) return false
        return if (skyblockId.endsWith("_PICKAXE")) {
            true
        } else if (skyblockId.contains("_DRILL_")) {
            skyblockId[skyblockId.length - 1] in '0'..'9'
        } else listOf("GEMSTONE_GAUNTLET", "PICKONIMBUS", "DIVAN_DRILL").contains(skyblockId)
    }

}
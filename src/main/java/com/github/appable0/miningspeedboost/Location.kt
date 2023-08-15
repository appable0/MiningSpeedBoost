package com.github.appable0.miningspeedboost

import net.minecraft.client.Minecraft
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.StringUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent

// from Skytils, under AGPL 3.0
object Location {
    var onHypixel = false
        private set
    var inSkyblock = false
        private set

    private var ticks = 0


    @SubscribeEvent
    fun onConnect(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        onHypixel = Minecraft.getMinecraft().runCatching {
            !event.isLocal && ((thePlayer?.clientBrand?.lowercase()?.contains("hypixel")
                ?: currentServerData?.serverIP?.lowercase()?.contains("hypixel")) == true)
        }.getOrDefault(false)
    }

    @SubscribeEvent
    fun onDisconnect(event: FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
        onHypixel = false
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!onHypixel || event.phase != TickEvent.Phase.START) return
        if (ticks % 10 == 0) {
            val title = Minecraft.getMinecraft().theWorld?.scoreboard?.getObjectiveInDisplaySlot(1)?.displayName
            inSkyblock = title?.let {
                StringUtils.stripControlCodes(it)
            }?.contains("SKYBLOCK") == true
        }
        ticks++
    }
}
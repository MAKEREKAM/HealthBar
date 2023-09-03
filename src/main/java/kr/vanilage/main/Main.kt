package kr.vanilage.main

import org.bukkit.Bukkit
import org.bukkit.entity.Display
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

class Main : JavaPlugin(), Listener {
    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @EventHandler
    fun onSpawn(e : EntitySpawnEvent) {
        if (e.entity !is LivingEntity) return
        val entity : LivingEntity = e.entity as LivingEntity
        val display : TextDisplay = entity.world.spawn(e.location, TextDisplay::class.java)
        display.addScoreboardTag("health_bar")
        display.billboard = Display.Billboard.CENTER
        display.text = "§a⬛".repeat(entity.health.toInt())
        display.transformation = Transformation(
            Vector3f(0.0F, 1.0F, 0.0F),
            AxisAngle4f(0.0F, 0.0F, 0.0F, 0.0F),
            Vector3f(1.0F, 1.0F, 1.0F),
            AxisAngle4f(0.0F, 0.0F, 0.0F, 0.0F)
        )
        entity.addPassenger(display)
    }

    @EventHandler
    fun onDamage(e : EntityDamageEvent) {
        if (e.entity !is LivingEntity) return
        val entity : LivingEntity = e.entity as LivingEntity
        if (entity.passengers.isEmpty()) return
        for (i in entity.passengers) {
            if (i is TextDisplay) {
                val display : TextDisplay = i as TextDisplay
                if (entity.health - e.getDamage().toInt() <= 0) {
                    display.remove()
                    return
                }
                var health = 0
                if (entity.health > 0) health = entity.health.toInt()
                display.text = "§a⬛".repeat(health - e.getDamage().toInt()).plus("§c⬛".repeat(entity.maxHealth.toInt() - (health - e.getDamage().toInt())))
            }
        }
    }

    @EventHandler
    fun onLoad(e : ChunkLoadEvent) {
        val chunk = e.chunk
        for (i in chunk.entities) {
            if (i.scoreboardTags.contains("health_bar") && i.vehicle == null) i.remove()
        }
    }
}
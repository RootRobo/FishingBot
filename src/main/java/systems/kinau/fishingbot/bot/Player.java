/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.bot;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.fishing.AnnounceType;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;
import systems.kinau.fishingbot.network.protocol.play.PacketOutRespawn;
import systems.kinau.fishingbot.network.protocol.play.PacketOutTeleportConfirm;

public class Player implements Listener {

    @Getter @Setter private double x;
    @Getter @Setter private double y;
    @Getter @Setter private double z;
    @Getter @Setter private float yaw;
    @Getter @Setter private float pitch;

    @Getter @Setter private int experience;
    @Getter @Setter private int levels;

    @Getter @Setter private int health;
    @Getter @Setter private int food;
    @Getter @Setter private int saturation;

    @Getter @Setter private int heldSlot;
    @Getter @Setter private ByteArrayDataOutput slotData;

    @Getter @Setter private int entityID = -1;

    public Player() {
        FishingBot.getInstance().getEventManager().registerListener(this);
    }

    @EventHandler
    public void onPosLookChange(PosLookChangeEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        this.z = event.getZ();
        this.yaw = event.getYaw();
        this.pitch = event.getPitch();
        if (FishingBot.getInstance().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_9)
            FishingBot.getInstance().getNet().sendPacket(new PacketOutTeleportConfirm(event.getTeleportId()));

    }

    @EventHandler
    public void onUpdateXP(UpdateExperienceEvent event) {
        if(getLevels() >= 0 && getLevels() < event.getLevel()) {
            if(FishingBot.getInstance().getConfig().getAnnounceTypeConsole() != AnnounceType.NONE)
                FishingBot.getLog().info("Achieved level " + event.getLevel());
            if(!FishingBot.getInstance().getConfig().getAnnounceLvlUp().equalsIgnoreCase("false"))
                FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(FishingBot.getInstance().getConfig().getAnnounceLvlUp().replace("%lvl%", String.valueOf(event.getLevel()))));
        }

        this.levels = event.getLevel();
        this.experience = event.getExperience();
    }

    @EventHandler
    public void onUpdateHP(UpdateHealthEvent event) {
        this.health = event.getHealth();
        this.food = event.getSaturation();
        if(this.health <= 1) {
            double posX = FishingBot.getInstance().getPlayer().x;
            double posY = FishingBot.getInstance().getPlayer().y;
            double posZ = FishingBot.getInstance().getPlayer().z;
            posX = Math.round(posX);
            posY = Math.round(posY);
            posZ = Math.round(posZ);
            FishingBot.getLog().info("BOT DIED! Respawning ... (Position: " + posX + " " + posY + " " + posZ + ")");
            if(!FishingBot.getInstance().getConfig().getDeathMessage().equalsIgnoreCase("false")) {
                String deathMessage = FishingBot.getInstance().getConfig().getDeathMessage();
                deathMessage = deathMessage.replace("{X}", String.valueOf(posX));
                deathMessage = deathMessage.replace("{Y}", String.valueOf(posY));
                deathMessage = deathMessage.replace("{Z}", String.valueOf(posZ));
                FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(deathMessage));
            }

            if(FishingBot.getInstance().getConfig().isAutoRespawnEnabled()) {
                FishingBot.getInstance().getNet().sendPacket(new PacketOutRespawn());
            }
        }

        FishingBot.getLog().info("Health: " + this.health);
        FishingBot.getLog().info("Food Level: " + this.food);
        FishingBot.getLog().info("Saturation Level: " + this.saturation);
    }

    @EventHandler
    public void onSetHeldItem(SetHeldItemEvent event) {
        this.heldSlot = event.getSlot();
    }

    @EventHandler
    public void onUpdateSlot(UpdateSlotEvent event) {
        if(event.getWindowId() != 0)
            return;
        if(event.getSlotId() != getHeldSlot())
            return;
        this.slotData = event.getSlotData();
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        setEntityID(event.getEid());
    }
}

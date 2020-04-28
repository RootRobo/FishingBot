/*
 * Credits to MrKinau for the code base.
 * I've just stolen half the class lol.
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInSetHealth extends Packet {

    @Getter private int food;
    @Getter private int saturation;
    @Getter private int health;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        health = (int)in.readFloat();
        food = Packet.readVarInt(in);
        saturation = (int)in.readFloat();

        FishingBot.getLog().info("Health: " + health);
        FishingBot.getLog().info("Food: " + food);
        FishingBot.getLog().info("Saturation: " + saturation);

        FishingBot.getInstance().getEventManager().callEvent(new UpdateHealthEvent(health, food, saturation));
    }
}

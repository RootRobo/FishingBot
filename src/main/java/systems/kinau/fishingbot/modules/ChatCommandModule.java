/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChat;

import java.io.IOException;
import java.util.Arrays;

public class ChatCommandModule extends Module implements Listener {

    @Override
    public void onEnable() {
        FishingBot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        FishingBot.getInstance().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!isEnabled()) return;
        String username = FishingBot.getInstance().getAuthData().getUsername();
        String prefix = username + ".";
        String message = event.getText();
        String[] splitMessage = message.split(" ");
        String sender = splitMessage[0];

        if(!message.startsWith(sender + " " + prefix)) {
            //FishingBot.getLog().info("Mesage was not a command.");
            return;
        }
        String command = splitMessage[1].replace(prefix,"");
        String[] args = Arrays.stream(splitMessage).skip(2).toArray(String[]::new);
        sender = sender.replaceAll("<", "").replaceAll(">", "");

        if (command.equals("level")) {
            FishingBot.getInstance().getNet().sendPacket(new PacketOutChat("I have " + FishingBot.getInstance().getPlayer().getLevels() + " levels."));
        }

        if (command.equals("health")) {
            FishingBot.getInstance().getNet().sendPacket(new PacketOutChat("I am on " + FishingBot.getInstance().getPlayer().getHealth() + "health!"));
        }

        if(command.equals("say")) {
            String saythis = String.join(" ", args);
            FishingBot.getInstance().getNet().sendPacket(new PacketOutChat(saythis));
        }

        if (command.equals("stop")) {
            if(!sender.equals(FishingBot.getInstance().getConfig().getBotOwner())) {
                FishingBot.getInstance().getNet().sendPacket(new PacketOutChat("You don't have the permission to use this command!"));
                return;
            }
            FishingBot.getInstance().getNet().sendPacket(new PacketOutChat("ok bye"));
            new Thread(() -> {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FishingBot.getLog().info("Disconnecting...");
                try {
                    FishingBot.getInstance().getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FishingBot.getLog().info("Bot stopped.");
                System.exit(0);
            }).start();
        }
    }
}

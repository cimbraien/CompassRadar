package io.github.cimbraien.compassradar;

import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar
{
    private PacketPlayOutChat packet;

    public ActionBar setMessage(String text) {
        this.packet = new PacketPlayOutChat(
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), ChatMessageType.a((byte)2));

        return this;
    }

    public void send(Player pla) {
        if (this.packet == null)
            return;
        CraftPlayer p = (CraftPlayer)pla;
        (p.getHandle()).playerConnection.sendPacket(this.packet);
    }
}

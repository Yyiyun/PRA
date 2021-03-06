package com.jake.pra.Commands;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.command.*;
import net.minecraft.util.SoundCategory;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
@SuppressWarnings("ConstantConditions")
public class PokeCry extends CommandBase implements ICommand
{
    private final List<String> aliases = Lists.newArrayList("cry");

    @Nonnull
    public String getName()
    {
        return "pokecry";
    }

    public int getRequiredPermissionLevel() { return 2; }

    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return "/pokecry <player> <pokemon> [volume] [pitch] [gender]";
    }

    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }
        else
        {
            String sound = "pixelmon:pixelmon.mob." + args[1].toLowerCase();
            SoundCategory soundcategory = SoundCategory.AMBIENT;
            EntityPlayerMP player = getPlayer(server, sender, args[0]);
            double x = player.posX;
            double y = player.posY;
            double z = player.posZ;
            double volume, pitch;

            if(args.length == 3)
            {
                volume = parseDouble(args[2], 0.0D, 3.4028234663852886E38D);
                pitch = 1;
            }
            else if(args.length == 4)
            {
                volume = parseDouble(args[2], 0.0D, 3.4028234663852886E38D);
                pitch = parseDouble(args[3], 0.0D, 2.0D);
            }
            else if(args.length == 5)
            {
                volume = parseDouble(args[2], 0.0D, 3.4028234663852886E38D);
                pitch = parseDouble(args[3], 0.0D, 2.0D);
                if(args[4].toLowerCase().equals("f") || args[4].toLowerCase().equals("m"))
                {
                    sound += args[4];
                }
            }
            else
            {
                volume = 1;
                pitch = 1;
            }

            player.connection.sendPacket(new SPacketCustomSound(sound, soundcategory, x, y, z, (float)volume, (float)pitch));
            String[] players = server.getOnlinePlayerNames();
            if(getListOfStringsMatchingLastWord(players, server.getOnlinePlayerNames()).contains(sender.getName())) {
                CommandChatHandler.sendFormattedChat(sender, TextFormatting.GREEN, "Played " + args[1] + "'s cry to " + args[0]);
            }
        }
    }

    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        else if (args.length == 2)
        {
            List<String> pokes = new ArrayList<>();
            EnumSpecies[] dex = EnumSpecies.values();
            for (EnumSpecies poke : dex) {
                pokes.add(poke.name);
            }
            return getListOfStringsMatchingLastWord(args, pokes);
        }
        else if(args.length == 5) {
            List<String> genders = new ArrayList<>();
            genders.add("f");
            genders.add("m");
            return getListOfStringsMatchingLastWord(args, genders);
        }
        return null;
    }

    public boolean isUsernameIndex(@Nonnull String[] args, int index)
    {
        return index == 2;
    }
}

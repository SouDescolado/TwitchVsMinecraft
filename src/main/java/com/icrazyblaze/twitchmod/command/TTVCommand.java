package com.icrazyblaze.twitchmod.command;

import com.icrazyblaze.twitchmod.BotCommands;
import com.icrazyblaze.twitchmod.chat.ChatPicker;
import com.icrazyblaze.twitchmod.irc.BotConfig;
import com.icrazyblaze.twitchmod.irc.BotConnection;
import com.icrazyblaze.twitchmod.util.ConfigManager;
import com.icrazyblaze.twitchmod.util.TickHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.List;

public class TTVCommand extends CommandBase {

    private final List aliases;
    private final String[] autocomplete = {"key", "channel", "affects", "connect", "disconnect", "enabled", "save", "reload", "showchat", "seconds", "blacklist", "help", "test", "queue", "log"};
    private final String[] truefalse = {"true", "false"};

    public TTVCommand() {
        aliases = new ArrayList();
        aliases.add("twitch");
    }

    @Override
    public String getName() {
        return "ttv";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ttv <key/channel> [OAuth key/channel name] OR /ttv affects <username> OR /ttv <connect/disconnect> OR /ttv enabled <true/false> OR /ttv <save/reload> OR /ttv showchat <true/false> OR /ttv seconds <seconds> OR /ttv blacklist OR /ttv queue";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {

        if (args.length <= 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, autocomplete);
        } else if (args[0].equalsIgnoreCase("affects")) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args[0].equalsIgnoreCase("showchat") || args[0].equalsIgnoreCase("enabled") || args[0].equalsIgnoreCase("log")) {
            return CommandBase.getListOfStringsMatchingLastWord(args, truefalse);
        } else {
            return null;
        }
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {

            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("key") && args.length == 2) {
                    BotConfig.TWITCH_KEY = args[1];
                    sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Set Twitch OAuth key"));

                } else if (args[0].equalsIgnoreCase("channel") && args.length == 2) {
                    BotConfig.CHANNEL_NAME = args[1];
                    sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Set channel name to " + args[1]));
                } else if (args[0].equalsIgnoreCase("affects") && args.length == 2) {
                    BotCommands.username = args[1];
                    sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Set player name to " + args[1]));
                } else if (args[0].equalsIgnoreCase("connect")) {
                    try {
                        if (BotConnection.isConnected) {
                            sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Reconnecting..."));
                        } else {
                            sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Connecting..."));
                        }

                        BotConnection.main();

                    } catch (Exception e) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Could not connect: " + e.toString()));
                    }
                } else if (args[0].equalsIgnoreCase("disconnect")) {
                    try {
                        if (BotConnection.isConnected) {
                            sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Disconnecting..."));
                        } else {
                            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Bot not connected."));
                        }

                        BotConnection.disconnectBot();

                    } catch (Exception e) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + e.toString()));
                    }
                } else if (args[0].equalsIgnoreCase("save")) {
                    ConfigManager.saveConfig();
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Saved configuration."));

                } else if (args[0].equalsIgnoreCase("reload")) {
                    ConfigManager.loadConfig();
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Reloaded configuration."));

                } else if (args[0].equalsIgnoreCase("showchat") && args.length == 2) {
                    if (args[1].equalsIgnoreCase("true")) {
                        BotConfig.showChatMessages = true;
                        sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Chat is now shown."));
                    } else if (args[1].equalsIgnoreCase("false")) {
                        BotConfig.showChatMessages = false;
                        sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Chat is now hidden."));
                    } else {
                        throw new WrongUsageException(getUsage(sender));
                    }

                } else if (args[0].equalsIgnoreCase("enabled") && args.length == 2) {
                    if (args[1].equalsIgnoreCase("true")) {
                        TickHandler.enabled = true;
                        sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Twitch commands enabled."));
                    } else if (args[1].equalsIgnoreCase("false")) {
                        TickHandler.enabled = false;
                        sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Twitch commands disabled."));
                    } else {
                        throw new WrongUsageException(getUsage(sender));
                    }

                } else if (args[0].equalsIgnoreCase("log") && args.length == 2) {
                    if (args[1].equalsIgnoreCase("true")) {
                        BotConnection.setVerboseMode(true);
                        sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Bot log mode enabled."));
                    } else if (args[1].equalsIgnoreCase("false")) {
                        BotConnection.setVerboseMode(false);
                        sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Bot log mode disabled."));
                    } else {
                        throw new WrongUsageException(getUsage(sender));
                    }

                } else if (args[0].equalsIgnoreCase("seconds") && args.length == 2) {

                    try {

                        int newInt = Integer.parseInt(args[1]);

                        if (newInt >= 5 && newInt <= 120) {
                            TickHandler.chatSecondsDefault = newInt;
                            TickHandler.chatSeconds = newInt;
                            sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "The chat timer is now set to " + newInt + " seconds."));
                        } else {
                            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid value."));
                        }

                    } catch (Exception e) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid value."));
                    }

                } else if (args[0].equalsIgnoreCase("help")) {
                    TextComponentString helpmessage = new TextComponentString(TextFormatting.RED + "Use '/help ttv' for usage, or click on this message to view a list of commands.");

                    ClickEvent goLinkEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, "http://bit.ly/2UfBCiL");
                    HoverEvent goHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open the link in your browser"));
                    helpmessage.getStyle().setClickEvent(goLinkEvent);
                    helpmessage.getStyle().setHoverEvent(goHoverEvent);

                    sender.sendMessage(helpmessage);
                } else if (args[0].equalsIgnoreCase("blacklist")) {
                    sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "Blacklist: " + ChatPicker.blacklist.toString()));
                } else if (args[0].equalsIgnoreCase("test") && args.length == 3) {
                    ChatPicker.checkChat(args[1], args[2]);
                    BotCommands.player().sendMessage(new TextComponentString(TextFormatting.WHITE + "<" + TextFormatting.DARK_PURPLE + "Twitch " + TextFormatting.WHITE + args[2] + "> " + args[1]));
                } else if (args[0].equalsIgnoreCase("queue")) {
                    sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "Possible commands: " + ChatPicker.newChats.toString()));
                } else {
                    throw new WrongUsageException(getUsage(sender));
                }


            } else {

                if (BotConnection.isConnected) {
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Bot is connected."));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Bot not connected."));
                }

                sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Channel name: " + BotConfig.CHANNEL_NAME));

                sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Player affected: " + BotCommands.username));

                sender.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "A new command will be chosen every " + TickHandler.chatSecondsDefault + " seconds."));

                TextComponentString keyMessage = new TextComponentString(TextFormatting.AQUA + "Click here to get your Twitch OAuth key!");

                ClickEvent goLinkEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitchapps.com/tmi/");
                HoverEvent goHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open the link in your browser"));
                keyMessage.getStyle().setClickEvent(goLinkEvent);
                keyMessage.getStyle().setHoverEvent(goHoverEvent);

                sender.sendMessage(keyMessage);

            }
        }
    }

}

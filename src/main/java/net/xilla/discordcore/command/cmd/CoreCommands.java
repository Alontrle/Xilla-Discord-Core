package net.xilla.discordcore.command.cmd;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.xilla.core.library.manager.Manager;
import net.xilla.core.library.manager.ManagerObject;
import net.xilla.core.library.manager.XillaManager;
import net.xilla.discordcore.CoreObject;
import net.xilla.discordcore.command.CommandBuilder;
import net.xilla.discordcore.command.response.CoreCommandResponse;
import net.xilla.discordcore.core.server.CoreServer;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CoreCommands extends CoreObject {

    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss a").withZone(ZoneOffset.UTC);

    public CoreCommands() {
       coreInfo();
    }

    public void coreInfo() {
        CommandBuilder commandBuilder = new CommandBuilder("Core", "CoreInfo", true);
        commandBuilder.setPermission("core.coreinfo");
        commandBuilder.setDescription("Get the discord core's information");
        commandBuilder.setCommandExecutor((data) -> {
            EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Core Info");

            if(data.getArgs().length > 1 && data.getArgs()[0].equalsIgnoreCase("server")) {
                StringBuilder argument = new StringBuilder();
                for(int i = 1; i < data.getArgs().length; i++) {
                    argument.append(data.getArgs()[i]);
                    if(i != data.getArgs().length - 1) {
                        argument.append(" ");
                    }
                }

                CoreServer coreServer = getPlatform().getServerManager().get(argument.toString());
                if(coreServer != null && coreServer.getGuild() != null) {
                    Guild g = coreServer.getGuild();
                    embedBuilder.setThumbnail(g.getIconUrl());

                    embedBuilder.addField("Owner", g.getOwner().getAsMention(), false);
                    embedBuilder.addField("Created At", fmt.format(g.getTimeCreated()) + " UTC", false);

                    int bots = 0;
                    for(Member member : g.getMembers()) {
                        if(member.getUser().isBot()) {
                            bots++;
                        }
                    }
                    int totalUsers = g.getMembers().size();
                    int users = totalUsers - bots;

                    embedBuilder.addField("Member Count", "**Total users** " + totalUsers + "\n**Bots** " + bots + "\n**Real Users** " + users, false);
                    embedBuilder.addField("Region", g.getRegionRaw(), false);

                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i = 0; i < g.getTextChannels().size(); i++) {
                        TextChannel channel = g.getTextChannels().get(i);
                        stringBuilder.append(channel.getAsMention());
                        if(i != g.getTextChannels().size() - 1) {
                            stringBuilder.append(", ");
                        }
                    }

                    embedBuilder.addField("Text Channels (" + g.getTextChannels().size() + ")", stringBuilder.toString(), false);

                    StringBuilder stringBuilder2 = new StringBuilder();
                    for(int i = 0; i < g.getRoles().size(); i++) {
                        Role role = g.getRoles().get(i);
                        stringBuilder2.append(role.getAsMention());
                        if(i != g.getRoles().size() - 1) {
                            stringBuilder2.append(", ");
                        }
                    }

                    embedBuilder.addField("Roles (" + g.getRoles().size() + ")", stringBuilder2.toString(), false);
                } else {
                    embedBuilder.setDescription("That is not a valid server!");
                }

            } else if(data.getArgs().length >= 3 && data.getArgs()[0].equalsIgnoreCase("object")) {

                StringBuilder argument = new StringBuilder();
                for(int i = 2; i < data.getArgs().length; i++) {
                    argument.append(data.getArgs()[i]);
                    if(i != data.getArgs().length - 1) {
                        argument.append(" ");
                    }
                }

                Manager manager = XillaManager.getInstance().get(data.getArgs()[1]);
                if(manager != null) {
                    ManagerObject object = manager.get(argument.toString());
                    if(object != null) {
                        embedBuilder.setDescription("Object Data: ```" + object.getSerializedData().getJson().toJSONString() + "```");
                    } else {
                        embedBuilder.setDescription("That is not a valid object!");
                    }
                } else {
                    embedBuilder.setDescription("That is not a valid manager!");
                }
            } else if(data.getArgs().length > 1 && data.getArgs()[0].equalsIgnoreCase("manager")) {

                StringBuilder argument = new StringBuilder();
                for(int i = 1; i < data.getArgs().length; i++) {
                    argument.append(data.getArgs()[i]);
                    if(i != data.getArgs().length - 1) {
                        argument.append(" ");
                    }
                }

                Manager manager = XillaManager.getInstance().get(argument.toString());
                if(manager != null) {
                    StringBuilder str = new StringBuilder();

                    List<ManagerObject> objects = new ArrayList<>(manager.getData().values());

                    int loop = 0;
                    for(ManagerObject object : objects) {
                        str.append(object.getKey());
                        loop++;
                        if(loop != objects.size()) {
                            str.append(", ");
                        }
                    }
                    embedBuilder.setDescription("Objects (" + objects.size() + "): " + str.toString());
                } else {
                    embedBuilder.setDescription("That is not a valid manager!");
                }


            } else {
                StringBuilder stb = new StringBuilder();
                int sloop = 0;
                for (CoreServer server : new ArrayList<>(getDiscordCore().getPlatform().getServerManager().getData().values())) {
                    if (server != null && server.getGuild() != null) {
                        stb.append(server.getGuild().getName()).append(" (ID: ").append(server.getKey()).append(")");
                    }
                    sloop++;
                    if(sloop != getDiscordCore().getPlatform().getServerManager().getData().size()) {
                        stb.append(", ");
                    }
                }

                StringBuilder mtb = new StringBuilder();
                int mloop = 0;
                for (Manager manager : new ArrayList<>(XillaManager.getInstance().getData().values())) {
                    mtb.append("> ").append(manager.getName()).append(" (" ).append(manager.getData().size()).append(")");
                    mloop++;
                    if(mloop != XillaManager.getInstance().getData().size()) {
                        mtb.append("\n");
                    }
                }
                if(getDiscordCore().getPlatform().getServerManager().getData().size() > 0) {
                    embedBuilder.setDescription("Servers (" + getDiscordCore().getPlatform().getServerManager().getData().size() + "): `" + stb.toString()
                            + "`\n\nManagers (" + XillaManager.getInstance().getData().size()
                            + "): \n" + mtb.toString() + "\n\n" + getPrefix() + "coreinfo server (server name)\n"
                            + getPrefix() + "coreinfo manager (manager name)\n" + getPrefix() + "coreinfo object (manager name) (object name)");
                } else {
                    embedBuilder.setDescription("Servers (0): None\n\nManagers (" + XillaManager.getInstance().getData().size()
                            + "): \n" + mtb.toString() + "\n\n" + getPrefix() + "coreinfo server (server name)\n"
                            + getPrefix() + "coreinfo manager (manager name)\n" + getPrefix() + "coreinfo object (manager name) (object name)");
                }
            }
            embedBuilder.setColor(getColor());
            return new CoreCommandResponse(data).setEmbed(embedBuilder.build());
        });
        commandBuilder.build();
    }

}
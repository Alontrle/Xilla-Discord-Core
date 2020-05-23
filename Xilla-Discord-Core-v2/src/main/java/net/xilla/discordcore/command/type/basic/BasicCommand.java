package net.xilla.discordcore.command.type.basic;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.xilla.discordcore.command.CommandResponse;

public class BasicCommand {

    private String module;
    private String name;
    private String description;
    private int staffLevel;
    private BasicCommandExecutor executor;

    public BasicCommand(String module, String name, String description, int staffLevel, BasicCommandExecutor executor) {
        this.module = module;
        this.name = name;
        this.description = description;
        this.staffLevel = staffLevel;
        this.executor = executor;
    }

    public void run(String[] args, MessageReceivedEvent event) {
        CommandResponse response = executor.run(name, event);
        if(response != null) {
            if (event == null) {
                response.send();
            } else {
                response.send(event.getTextChannel());
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getModule() {
        return module;
    }

    public int getStaffLevel() {
        return staffLevel;
    }
}
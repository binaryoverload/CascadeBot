/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.core;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandCore;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.messaging.MessagingObjects;

import java.util.Set;

public class UsageCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.usage.specify_usage"));
            return;
        }

        ICommandMain command = CascadeBot.INS.getCommandManager().getCommand(context.getArg(0), context.getData());
        if (command == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.usage.command_not_found", context.getArg(0)));
            return;
        }

        EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed(context.getUsage(command), context.getUser());
        builder.setTitle(context.i18n("commands.usage.title", command.command(context.getLocale())));
        context.getTypedMessaging().replyInfo(builder);
    }

    @Override
    public String command() {
        return "usage";
    }

}

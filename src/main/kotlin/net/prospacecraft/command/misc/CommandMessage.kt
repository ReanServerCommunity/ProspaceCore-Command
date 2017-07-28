package net.prospacecraft.command.misc

import net.prospacecraft.ProspaceCore.message.FancyMessage

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal class CommandMessage(var message : String, var desc : MutableList<String> = ArrayList())
{
    private val messageBuilder : FancyMessage = FancyMessage(message)
    fun getMessageBuilder() : FancyMessage = messageBuilder

    fun getDescription() : MutableList<String> = desc

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun addMessage(message : String, index : Int = -1) : MutableList<String>
    {
        when (index)
        {
            -1 -> desc.add(ChatColor.translateAlternateColorCodes('&', message))
            else -> desc.add(index, message)
        }
        return desc
    }

    fun send(sender : CommandSender)
    {
        messageBuilder.tooltip(desc.asIterable())
        messageBuilder.send(sender)
    }
}
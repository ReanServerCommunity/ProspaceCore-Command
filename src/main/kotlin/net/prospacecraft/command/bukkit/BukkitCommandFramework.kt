package net.prospacecraft.command.bukkit

import net.prospacecraft.command.PCommandType

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin

class BukkitCommandFramework : Command, PluginIdentifiableCommand
{
    override fun getPlugin(): Plugin = this.command.getBundlePlugin()

    private constructor(name : String) : super(name)

    constructor(name : String, command: PCommandType) : super(name, command.getCommandDescription()[0], command.getPermission()!!.getMessage(), command.getAliasCommand())
    {
        this.command = command
    }

    lateinit private var command : PCommandType
    fun getCommand() : PCommandType = this.command

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String>?): Boolean =
            this.command.execute(sender!!, args!!.toMutableList())
}
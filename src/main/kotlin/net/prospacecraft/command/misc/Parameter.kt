package net.prospacecraft.command.misc

import net.prospacecraft.command.property.ColorSet
import net.prospacecraft.command.PCommandType

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class Parameter(var param : String, var requirement : Boolean, var allowConsole : Boolean = true, var allowPlayer  : Boolean = true)
{
    private var permission : String? = null
    fun hasPermission() : Boolean = this.permission != null
    fun checkPermission(relativeCommand : PCommandType, sender : CommandSender) : Boolean
    {
        if(relativeCommand.hasPermission())
        {
            val perm : String? = relativeCommand.getPermissionValue()
            return sender.hasPermission(perm + permission)
        }
        else
        {
            return sender.hasPermission(this.permission)
        }
    }

    fun getPermission() : String? = this.permission

    private var childParameter : Parameter? = null
    fun setChild(param : Parameter) { this.childParameter = param }
    fun hasChild() : Boolean = childParameter != null
    fun getChild(index : Int = 0) : Parameter?
    {
        return if(index <= 0) childParameter
        else
        {
            var param : Parameter? = this.getChild()
            for(i in 0..index) param = this.getChild()
            return param
        }
    }

    companion object
    {
        val REQUIREMENT_FORMAT : String = ColorSet.PARAM_REQUIREMENT_COLORSET + "<%s>"

        val OPTIONAL_FORMAT    : String = ColorSet.PARAM_OPTIONAL_COLORSET + "[&s]"
    }

    fun isAllowed(sender : CommandSender) : Boolean = when(sender)
    {
        is Player -> allowPlayer
        is ConsoleCommandSender -> allowConsole
        else -> false
    }

    fun getParamValue(target : CommandSender) : String = when(this.requirement)
    {
        true  -> String.format(REQUIREMENT_FORMAT, this.param)
        false -> String.format(OPTIONAL_FORMAT, this.param)
    }
}
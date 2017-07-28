@file:Suppress("UNCHECKED_CAST")
package net.prospacecraft.command

import net.prospacecraft.ProspaceCore.message.PluginMessage
import net.prospacecraft.ProspaceCore.plugin.ProspaceBundlePlugin
import net.prospacecraft.ProspaceCore.util.StringUtil

import net.prospacecraft.command.api.CommandExecutable
import net.prospacecraft.command.misc.CommandMessage
import net.prospacecraft.command.misc.Parameter
import net.prospacecraft.command.misc.Permission

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

import java.lang.reflect.ParameterizedType

/**
 * CommandFramework is an abstraction command skeleton that allows you to register a command
 * directly to Bukkit without any setting. This is an extended of original function, which
 * allows the developer to easily skip the complex process of registering commands in the
 * game and do it easily.<br>
 *
 * It uses a self-referencing generic to avoid errors in grammar settings. The developer can
 * determine whether the class is activated by Handle. It cannot be used even if the command
 * is registered with Bukkit when It's deactivated.<br><br>
 *
 * <b>About self-referencing generic class</b>
 * http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ206
 *
 * @author Kunonx
 * @since 1.0.0-SNAPSHOT
 * @version 1.0.0
 * @param T The class type that inherits from ProspaceCommand, Preventing Inheritance error
 */
typealias PCommandType = CommandFramework<*>
open abstract class CommandFramework<T : CommandFramework<T>> : CommandExecutable
{
    companion object
    {
        // Contains all currently registered command classes.
        // It uses this to access the command list to indirectly modify or read the information.
        @Transient private val registerCommands : MutableList<PCommandType> = ArrayList()

        val MAX_PAGE_SIZE: Short = 7

        val HEADER_MESSAGE: String = "&e====&f [&b Help commands for &e\"{0}\" &a1/{1} &bpage(s) &f] &e===="
    }

    private lateinit var bundle : ProspaceBundlePlugin
    protected fun setBundle(plugin : ProspaceBundlePlugin) { this.bundle = plugin }
    fun getBundlePlugin() : ProspaceBundlePlugin = this.bundle

    // This is a generic object of this class. It contains information about the superclass.
    private lateinit var genericClazz : Class<T>
        private get
    init { this.genericClazz = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T> }

    /**
     * Gets the generic type of the command.
     * @return The generic class type
     */
    fun getGenericType(): Class<T> { return this.genericClazz }

    /**
     * Gets an instance of this generic. It cans get the information of the upper command class.
     * @return The generic type
     * @exception InstantiationException Failed to create instance object
     * @exception IllegalAccessException Approaching an unacceptable area
     */
    fun getGenericInstance() : T
    {
        try
        {
            val generic : T = this.genericClazz.newInstance()
            return generic
        }
        catch(e : InstantiationException) { e.printStackTrace() }
        catch(e : IllegalAccessException) { e.printStackTrace() }
        return null!!
    }

    //
    protected var mainCommand : String?              = null!!

    private var commandDescription : MutableList<String>  = ArrayList()
    fun getCommandDescription() : MutableList<String> = this.commandDescription

    //
    private var aliasCommand : MutableList<String> = ArrayList()
    fun getAliasCommand() : MutableList<String> = this.aliasCommand
    fun hasAliasCommand(alias : String) : Boolean    = aliasCommand.contains(alias.toLowerCase())

    //
    private var messageBuilder : CommandMessage?     = null

    //
    private var usableConsole : Boolean              = true

    /**
     * Decide if you want to allow the player to use this command.
     * This can also affect child commands.
     */
    fun setAllowUseConsole(usable : Boolean) { this.usableConsole = usable }

    //
    private var usablePlayer : Boolean = true

    /**
     * Decide if you want to allow the player to use this command.
     * This can also affect child commands.
     */
    fun setAllowUsePlayer(usable : Boolean) { this.usablePlayer = usable }

    protected var parameter : Parameter? = null
    fun hasParameter()    : Boolean = this.parameter != null
    fun getParameterPermission(paramRoot : Parameter = this.parameter!!, index : Int = 0) : String?
    {
        val calcParam : (Parameter, Int) -> String? = { p, i -> p.getChild(i)!!.getPermission() }
        var permissionValue : String? = this.getPermissionValue()
        permissionValue ?: return calcParam(paramRoot, index)
        permissionValue.let {
            for(k in 0..index)
                permissionValue = "$permissionValue.${paramRoot.getChild(k)!!.getPermission()}"
            return permissionValue
        }
    }

    fun isAllowed(sender : CommandSender) : Boolean = sender.hasPermission(this.getPermissionValue())

    /**
     * for java method. <br>
     * Unfortunately, the Java grammar does not support initial argument values.
     * If you want to program using Java, see that method.
     */
    protected fun getRelativeCommand(target : CommandSender? = null) : String = getRelativeCommand(this, null, false, target)

    /**
     * for java method. <br>
     * Unfortunately, the Java grammar does not support initial argument values.
     * If you want to program using Java, see that method.
     */
    protected fun getRelativeCommand(isMain : Boolean, target : CommandSender? = null) : String = getRelativeCommand(this, null, isMain, target)

    /**
     * This is a command for help page.
     * It prints all the sub-commands available in that class.
     * If there is no value in Main Command, return value is null.
     * The output format is as follows: <br>
     * <code><b><ROOT_COMMAND> <SUB_MAIN_COMMAND> <SUB_SUB_MAIN_COMMAND> ..... <CURRENT_ALL_COMMAND></b></code>
     * @param command
     * @param label
     * @param isMain
     * @param target If this argument is not null, It can colorize the command based on this target
     */
    protected fun getRelativeCommand(command : PCommandType?, label : String? = null, isMain : Boolean = false, target : CommandSender? = null) : String
    {
        if(mainCommand == null)
        {
            val javaClassName : String = getGenericInstance().javaClass.name
            // You must set the main command before using this command.
            // Please use the function to set the command: this.setMainCommand(String)
        }
        var subLabel: String? = label
        command ?: throw RuntimeException("command cannot be null")
        if(command.isRoot())
            if(label == null)
            {
                subLabel = command.getAllCommands()
            }
            else
            {
                subLabel = "${command.getAllCommands()} $label"
            }
        else
            if(command.hasChildCommand())
            {
                if(isMain) subLabel = if(label == null) command.getAllCommands() else "${command.getAllCommands()} $label"
                else subLabel = if(label == null) command.mainCommand else "${command.mainCommand} $label"
            }
        return if(command.isRoot()) subLabel!! else this.getRelativeCommand(command.parentCommand, subLabel, false, target)
    }

    /**
     * This is a command for help page.
     * Returns all commands in the class with commas.
     * The output format is as follows: <br>
     * <MAIN_COMMAND>,<ALIAS_COMMAND>,<ALIAS_COMMAND2>, ... ,<ALIAS_COMMAND>
     */
    protected fun getAllCommands(target: CommandSender? = null): String
    {
        var s : String = ""
        val iter : Iterator<String> = this.aliasCommand.iterator()
        while(iter.hasNext())
        {
            val s2 = iter.next()
            s += s2
            if(iter.hasNext()) s += ","
        }
        return s
    }

    // Saves subclasses that operate on this command.
    protected var childCommand : MutableList<PCommandType> = ArrayList()
    fun hasChildCommand() : Boolean = when(childCommand.size) { 0 -> false; else -> true }
    fun getChildCommand(cmd : String) : PCommandType?
    {
        if (this.hasChildCommand()) return null
        return this.childCommand.firstOrNull { it.mainCommand.equals(cmd, true) || it.hasAliasCommand(cmd) }
    }

    fun getChildCommands() : MutableList<PCommandType> = childCommand

    protected var externalCommand : MutableList<PCommandType> = ArrayList()
    fun addExternalCommand(command: PCommandType) { this.externalCommand.add(command)}
    fun getExternalCommands() : MutableList<PCommandType> = externalCommand

    // The parent class of this class.
    // This connects the commands of that class to the parent class in tree form.
    private var parentCommand : PCommandType? = null
    fun hasParent() : Boolean = this.parentCommand != null
    private fun setParent(parent : PCommandType) { this.parentCommand = parent }
    fun isRoot(): Boolean = this.parentCommand == null

    // The Permission for this command.
    // The Permission value of the child class is used in conjunction with the parent
    // Permission in the parent class.
    private var permission : Permission? = null

    /**
     * Get the Permission for this class.
     * It takes the permission value of this command and is not affected by the parent
     * class's information.
     * @return
     */
    fun getPermission() : Permission? = this.permission

    /**
     * Gets the Permission value.
     * This is affected by the parent Permission value and it can colorize the string according to the target.
     * @param senderTarget
     * @return
     */
    fun getPermissionValue(senderTarget : CommandSender? = null) : String?
    {
        this.permission ?: return null
        this.parentCommand ?: return this.permission!!.getPermissionName(senderTarget)

        var parentsCmd : PCommandType? = this.parentCommand
        var perm : String = this.permission!!.name

        while(parentsCmd != null)
        {
            perm = "${parentsCmd.getPermission()!!.name}.$perm"
            parentsCmd = parentsCmd.parentCommand!!
        }
        return if(senderTarget == null) perm else Permission(perm, this.permission!!.defaultOP).getPermissionName(senderTarget)
    }


    /**
     * Check for Permission value. If not, this command will work without any separate permissions.
     * @return true If the permission is null, otherwise false
     */
    fun hasPermission() : Boolean = this.permission != null

    /**
     * Specifies a permission value. This can be affected by the value of the parent class.<br>
     * For example, Here is the code:<br>
     * <pre>
     * <code>
     * class ParentCommand : ProspaceCommand<ParentCommand>
     * {
     *   init
     *   {
     *       this.setCommand("ps")
     *       this.setPermission("prospacecore")
     *       this.addChildCommand(ChildCommand())
     *   }
     * }
     * class ChildCommand : ProspaceCommand<ChildCommand>
     * {
     *   init
     *   {
     *      setCommand("run")
     *      setPermission("child")
     *   }
     * }
     * ...
     * println(new ParentCommand().getChildCommand("run").getPermissionValue())
     * </code>
     * </pre>
     * The output of this code will be <code>"prospacecore.child"</code>. This shows that the value
     * can vary depending on the parent class.<br>
     * That is, the child information changes automatically according to the parent value and
     * need to enter <code>/ps run</code> to use the ChildCommand's command.<br>
     */
    protected fun setPermission(perm : Permission)
    {
        this.permission = permission
    }

    /**
     * Specifies a permission value. This can be affected by the value of the parent class.
     * @see setPermission(Permission)
     */
    protected fun setPermission(perm : String)     { return setPermission(Permission(perm, true)) }

    protected fun sendHelpPage(sender: CommandSender)
    {
        if(mainCommand == null)
        {
            val javaClassName : String = getGenericInstance().javaClass.name
            // You must set the main command before using this command.
            // Please use the function to set the command: this.setMainCommand(String)
        }

        // First, Collect commands that will appear on the help page.
        // It also takes an external command that is not related to this command.
        // The help page will sort in ascending order.
        val commands : MutableList<PCommandType> = ArrayList()

        commands.addAll(this.externalCommand)
        commands.addAll(this.childCommand)

        if(commands.size != 0)
        {
            val commandTexts : MutableList<CommandMessage> = ArrayList()
            val max_page = if(sender is ConsoleCommandSender) 1 else (commands.size / (MAX_PAGE_SIZE - 1)) + 1

            // Create a header message.
            val headerMessage : CommandMessage = CommandMessage(StringUtil.replaceValue(HEADER_MESSAGE, this.mainCommand!!, max_page))
            commandTexts.add(headerMessage)

            // Creates a message to output the information of the command registered in this class.
            // This is adding the main command (this class) at the top of the page.
            var mainCommand : String = this.getRelativeCommand(true, sender)
            if(this.hasParameter())
            {
                var param : Parameter = this.parameter!!
                while(param.hasChild())
                {
                    mainCommand = "$mainCommand ${param.getParamValue(sender)}"
                    param = param.getChild()!!
                }
            }
            commandTexts.add(CommandMessage(mainCommand, this.commandDescription))

            // The following process processes child commands and external commands
            // except the main command.

            // This is a function that shows one page.
            // Therefore, there is no reason to calculate the page size.
            val size_index : Int = when(sender)
            {
                is ConsoleCommandSender -> commands.size
                is Player -> if(commands.size >= MAX_PAGE_SIZE) MAX_PAGE_SIZE - 1 else commands.size
                else -> -1
            }
            for(index in 0..size_index)
            {
                val command : PCommandType = commands[index]
                var relativeCommand = command.getRelativeCommand(true, sender)
                if(command.hasParameter())
                {
                    var param : Parameter = command.parameter!!
                    while(param.hasChild())
                    {
                        relativeCommand = "$relativeCommand ${param.getParamValue(sender)}"
                        param = param.getChild()!!
                    }
                }
                commandTexts.add(CommandMessage(relativeCommand, command.commandDescription))
            }

            // Finally, messages are printing.
            for(e in commandTexts)
            {
                e.send(sender)
            }
        }
        else
        {
            // No provided help page.
        }
    }

    protected fun sendHelpSpecificPage(sender: CommandSender, page : Int = 1)
    {
        if(page <= 1) return sendHelpPage(sender)
        //TODO
    }

    fun execute(sender : CommandSender, args : MutableList<String>) : Boolean
    {
        val msg : PluginMessage = bundle.getPluginMessage()!!
        if(args.size == 0)
        {
            if(this.hasChildCommand())
            {
                this.sendHelpPage(sender)
                return true
            }
            else
            {
                if(this.hasParameter())
                {
                    if(parameter!!.requirement) msg.send(sender, "&cThe param \"${this.parameter!!.param}\" is required value!")
                    return false
                }
                if(! this.isAllowed(sender))
                    msg.send(sender, "&cYou're not authorized for this command. (${this.getPermission()})")

                return this.perform(sender, args)
            }
        }
        else
        {
            if(args[0].equals("?") || args[0].equals("help", true))
            {
                if(this.hasChildCommand() && !CommandExecutable::class.java.isAssignableFrom(this.getGenericType()))
                {
                    if(args.size >= 2 && StringUtil.isNumber(args[1]))
                    {
                        this.sendHelpSpecificPage(sender, Integer.parseInt(args[1]))
                        return true
                    }
                    this.sendHelpPage(sender)
                    return true
                }
                if(this.isAllowed(sender))
                {

                }
            }
            else
            {

            }
        }
        return false
    }

    override fun perform(sender: CommandSender, args: MutableList<String>?) = false
}
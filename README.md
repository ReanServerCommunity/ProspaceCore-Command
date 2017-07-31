# ProspaceCore-Command
This is the framework that takes care of the commands in ProspaceCore. It extends the functionality of the command class and helps the user to create commands easily. It was created by extracting from existing code into framework. It created this project for further modification and management of its features.

# What is ProspaceCommand?
This helps you to develop very quickly when developing with a command in a plugin. For example, when you create a Permission and then check each player and create a help page or create a child command, you write a lot of source code for these processes. So I make this plugin to improve the efficiency of the code and save time.

# Advantages of ProspaceCommand
Compared to BukkitCommand, it provides the following functions.
* Automatically check the permission
* Added a description of the command (JSON, YAML, TXT)
* Supports alias commands
* Create additional child commands
* Easy design without writing a separate class
* Automatically creates help pages and custom styles
# How to use ProspaceCommand
The source code is written in Kotlin, but it is 100% compatible with Java code. <br>
This shows that there is no problem building the plugin.<br>
First, Create a class to use as a command. Then you just inherit that framework class. For example:
```java
package net.prospacecraft.ProspacePlugin;

import net.prospacecraft.command.CommandFramework;

public class ExampleCommand extends CommandFramework<ExampleCommand>
{
    public ExampleCommand()
    {
        //TODO something...
    }
}
```
Now, This class is ready to use the command class.<br>
For inheritance safety, write the type of the class that inherits from the generic type. This is not required. <b>However, there is no guarantee that it will work 100%</b>. I recommend first when you need to exchange information of class.<br>
Next, in order to operate the command normally, you need to specify the main command.
```java
public ExampleCommand()
{
    this.setMainCommand("command")
    //TODO something...
}
```
Next, let's implement the part that runs when you type the command. Actually, the command class is already defined, but nothing is executed. This can be overridden and design your code as you like.
```java
public ProspaceCoreCommand()
{
    this.setMainCommand("command");
}

//1. General method
@Override
public boolean perform(CommandSender sender, List<String> args)
{
    //TODO execute
    return true;
}

//2. Null-Safety Guaranteed Method
@Override
public boolean perform(@NotNull CommandSender sender, @Nullable List<String> args)
{
    //TODO execute
    return true;
}

```
The method <code>perform(CommandSender, List<String>)</code> is defined in the interface within the <code>CommandFramework</code>.
The method is Null-safety guaranteed by Kotlin Compiler. It can be confirmed that the argument value is passed through the annotation check. The command class determines that nothing has been done if <code>perform(CommandSender, List<String>)</code> has not been overridden in the child class and outputs a help page. The Help page is generated automatically by <code>sendHelpPage(CommandSender)</code>.<br><br>
 These commands are automatically registered by ProspacePluginLoader. Therefore, You don't have to write about this command in <code>plugin.yml</code>.
 ```java
package net.prospacecraft.ProspacePlugin;

import net.prospacecraft.command.CommandFramework;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExampleCommand extends CommandFramework<ProspaceCoreCommand>
{
    public ExampleCommand()
    {
        this.setMainCommand("command");
        this.setPermission("examplecommand.example");
        this.addAliasCommand("cmd", "com");
        //TODO ....
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @Nullable List<String> args)
    {
        // The source code below is executed when the sender has permission to this command.
        // This is automatically checked by CommandFramework. You don't have to write code to check permissions!
        if(sender instanceof ConsoleCommandSender)
        {
            sender.sendMessage("Hello server!!");
        }
        if(sender instanceof Player)
        {
            sender.sendMessage("Hello!! " + ((Player) sender).getDisplayName());
        }
        return true;
    }
}
 ```
 You can make it very simple and fast if you do the above and modify it according to your desired attributes. <br>
 See the wiki for more information, commands, methods.
 # Reference Methods
It will up-to-date.

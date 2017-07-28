# ProspaceCore-Command
This is the framework that takes care of the commands in ProspaceCore. It extends the functionality of the command class and helps the user to create commands easily. It was created by extracting from existing code into framework. It created this project for further modification and management of its features.
# Advantages of ProspaceCommand
Compared to BukkitCommand, it provides the following functions.
* Automatically check the permission
* Added a description of the command in JSON format to the command
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
For inheritance safety, write the type of the class that inherits from the generic type. This is not required. <b>However, there is no guarantee that it will work 100%</b>. I recommend this method when you need to exchange classes and classes.<br>
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
The method is Null-safety guaranteed by Kotlin Compiler. It can be confirmed that the argument value is passed through the annotation check. 
After that, you can modify it according to your desired attributes. See below for a description of the methods you need to modify.
 # Reference Methods
It will up-to-date.

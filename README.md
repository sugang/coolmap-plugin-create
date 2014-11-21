#coolmap-plugin-create

This is a sample coolmap plugin to create data transformations. After loading plugin, you should be able to find several menu items in the plugin folder. Here's how to set up a plugin to work.

1. Get a distributed version of CoolMap. The in that folder, you should find CoolMap.jar, and a plugin folder.
2. If you wish to use a different plugin folder than default. Edit config.json in the distributed CoolMap folder. change the value of **"plugin":{"directory":"plugin", "relative":"true"}**. If **"relative"** is set other than **"true"**, the folder will be *absolute* path instead of the path relative to the CoolMap distribution folder.
3. Run CoolMap by double click on the CoolMap.jar to make sure it works.
4. Create a new Java project. Add CoolMap.jar in the classpath (in Netbeans or Eclipse, add as library)
5. Your source package must have the following java file:  
   ```
   
   coolmapplugin.impl.[YOURPLUGIN].java
   
   ```  
   Also within this file, you must have a directive like this:  
   ```
   @PluginImplementation
   public class CreatePlugin implements CoolMapPlugin { ... }
   
   ```  
   in order to make it to work.

#coolmap-plugin-create

This is a sample coolmap plugin to create data transformations. After loading plugin, you should be able to find several menu items in the plugin folder. Here's how to set up a plugin to work.

1. Get a distributed version of CoolMap. Then in that folder, you should find CoolMap.jar, and a plugin folder.
2. If you wish to use a different plugin folder than default. Edit config.json in the distributed CoolMap folder. Change the value of **"plugin":{"directory":"plugin", "relative":"true"}**. If **"relative"** is set other than **"true"**, the folder will be *absolute* path instead of the path relative to the CoolMap distribution folder.
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
   in order to make it to work. You may create other classes in other packages.
6. Exit the build ant file if you would like to do other post-jar activities such as create folders or copy files. If you are using Netbeans, edit the **build-impl.xml**, -post-jar section. If you place a **config.json** file in your plugin folder, it will be loaded by the *initialize(JSONObject config)* function into a JSON config.
7. Build the project. You should have a folder(like dist folder in NetBeans) which contains **CreatePlugin.jar**
8. Place the folder which contains your plugin jar under the CoolMap 'plugin' folder, and rename the folder as [pluginname].plugin.
9. There are several ways to let CoolMap plugin. First of all, all plugin files should go into a folder named like [pluginname].plugin. (*.plugin* extension). Then this folder should be placed within the plugin folder of CoolMap. An easy way is to create a symbolic link, pointing your dist folder under CoolMap like
   ```
   
   ln -s [path to your plugin folder] ./create.plugin
   
   ```

Now, load up CoolMap. Under the Edit menu, you should see 'Edit->Create View', 'Edit->Aggregate Data'...

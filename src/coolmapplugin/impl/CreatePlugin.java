/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmapplugin.impl;

import coolmap.application.CoolMapMaster;
import coolmap.application.plugin.CoolMapPlugin;
import java.awt.MenuItem;
import javax.swing.AbstractAction;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.json.JSONObject;
import utils.CreateFromSelection;
import utils.LogTransform;
import utils.NewAverage;
import utils.NewDiff;
import utils.NewSum;
import utils.RangeTransform;
import utils.ZTransform;

/**
 *
 * @author sugang
 */
@PluginImplementation
public class CreatePlugin implements CoolMapPlugin {

    @Override
    public void initialize(JSONObject config) {
        initMenuItems();
    }

    @Override
    public String getName() {
        return "creaters";
    }

    private void initMenuItems() {

        CoolMapMaster.getCMainFrame().addMenuSeparator("Edit");
        MenuItem item = new MenuItem("New from selected region");
        CoolMapMaster.getCMainFrame().addMenuItem("Edit/Create view", item, false, false);
        item.addActionListener(new CreateFromSelection());

        LogTransform log2 = new LogTransform(2);
        item = new MenuItem(log2.getValue(AbstractAction.NAME).toString());
        item.addActionListener(log2);
        CoolMapMaster.getCMainFrame().addMenuItem("Edit/Transform data", item, false, false);

        LogTransform log10 = new LogTransform(10);
        item = new MenuItem(log10.getValue(AbstractAction.NAME).toString());
        item.addActionListener(log10);
        CoolMapMaster.getCMainFrame().addMenuItem("Edit/Transform data", item, false, false);

        ZTransform zTransform = new ZTransform();
        item = new MenuItem(zTransform.getValue(AbstractAction.NAME).toString());
        item.addActionListener(zTransform);
        CoolMapMaster.getCMainFrame().addMenuItem("Edit/Transform data", item, false, false);

        RangeTransform rTransform = new RangeTransform();
        item = new MenuItem(rTransform.getValue(AbstractAction.NAME).toString());
        item.addActionListener(rTransform);
        CoolMapMaster.getCMainFrame().addMenuItem("Edit/Transform data", item, false, false);

        //aggreates
        NewSum sum = new NewSum();
        item = new MenuItem(sum.getValue(AbstractAction.NAME).toString());
        item.addActionListener(sum);
        CoolMapMaster.getCMainFrame().addMenuItem("Edit/Aggregate data", item, false, false);

        NewAverage avg = new NewAverage();
        item = new MenuItem(avg.getValue(AbstractAction.NAME).toString());
        item.addActionListener(avg);
        CoolMapMaster.getCMainFrame().addMenuItem("Edit/Aggregate data", item, false, false);
        
        NewDiff diff = new NewDiff();
        item = new MenuItem(diff.getValue(AbstractAction.NAME).toString());
        item.addActionListener(diff);
        CoolMapMaster.getCMainFrame().addMenuItem("Edit/Aggregate data", item, false, false);        

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.google.common.collect.Range;
import coolmap.application.CoolMapMaster;
import coolmap.application.widget.impl.console.CMConsole;
import coolmap.canvas.CoolMapView;
import coolmap.canvas.datarenderer.renderer.impl.TextRenderer;
import coolmap.canvas.datarenderer.renderer.model.ViewRenderer;
import coolmap.canvas.sidemaps.impl.ColumnLabels;
import coolmap.canvas.sidemaps.impl.ColumnTree;
import coolmap.canvas.sidemaps.impl.RowLabels;
import coolmap.canvas.sidemaps.impl.RowTree;
import coolmap.data.CoolMapObject;
import coolmap.data.aggregator.impl.PassThrough;
import coolmap.data.aggregator.model.CAggregator;
import coolmap.data.cmatrix.model.CMatrix;
import coolmap.data.cmatrixview.model.VNode;
import coolmap.data.snippet.SnippetConverter;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;

/**
 *
 * @author sugang
 */
public class CreateFromSelection extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        CoolMapObject obj = CoolMapMaster.getActiveCoolMapObject();
        if (obj == null) {
            CMConsole.logWarning("No active coolmap in view. Create new view aborted");
            return;
        }

        CoolMapView view = obj.getCoolMapView();
        ArrayList<Range<Integer>> selectedRows = view.getSelectedRows();
        ArrayList<Range<Integer>> selectedColumns = view.getSelectedColumns();

        if (selectedRows.isEmpty() || selectedColumns.isEmpty()) {
            CMConsole.logWarning("No region(s) were selected in view. Create new view aborted");
            return;
        }

        ArrayList<VNode> newRowNodes = new ArrayList<VNode>();
        ArrayList<VNode> newColumnNodes = new ArrayList<VNode>();

        for (Range<Integer> range : selectedRows) {
            for (int i = range.lowerEndpoint(); i < range.upperEndpoint(); i++) {
                newRowNodes.add(obj.getViewNodeRow(i).duplicate());
            }
        }

        for (Range<Integer> range : selectedColumns) {
            for (int i = range.lowerEndpoint(); i < range.upperEndpoint(); i++) {
                newColumnNodes.add(obj.getViewNodeColumn(i).duplicate());
            }
        }

        CoolMapObject newObject = new CoolMapObject();
        List<CMatrix> baseCMatrices = obj.getBaseCMatrices();
        for(CMatrix mx:baseCMatrices){
            newObject.addBaseCMatrix(mx);
        }
        

        newObject.insertRowNodes(newRowNodes);
        newObject.insertColumnNodes(newColumnNodes);

        if (obj.getAggregator() != null) {
            try {
                CAggregator aggr = (CAggregator)(obj.getAggregator().getClass().newInstance());
                aggr.restoreState(obj.getAggregator().getCurrentState());
                newObject.setAggregator(aggr);
            } catch (Exception ex) {
                CMConsole.logWarning("Could not initialize aggregator " + obj.getAggregator() + ", using default instead.");
                newObject.setAggregator(new PassThrough());
            }
        }
        else{
            newObject.setAggregator(new PassThrough());
        }

        if (obj.getViewRenderer() != null) {
            try {
                ViewRenderer renderer = (ViewRenderer)(obj.getViewRenderer().getClass().newInstance());
                newObject.setViewRenderer(renderer, true);
//                System.out.println("Current state:" + obj.getViewRenderer().getCurrentState());
                renderer.restoreState(obj.getViewRenderer().getCurrentState());
                
            } catch (Exception ex) {
                CMConsole.logWarning("Could not initialize aggregator " + obj.getAggregator() + ", using default instead.");
                newObject.setViewRenderer(new TextRenderer(), true);
            }

        }
        else{
            newObject.setViewRenderer(new TextRenderer(), true);
        }
        
        if( obj.getSnippetConverter() != null){
            try{
                SnippetConverter convert = obj.getSnippetConverter().getClass().newInstance();
                convert.restoreState(obj.getSnippetConverter().getCurrentState());
                newObject.setSnippetConverter(convert);
            }
            catch(Exception ex){
                //do nothing
            }
        }

        //newObject
        newObject.getCoolMapView().addColumnMap(new ColumnLabels(newObject));
        newObject.getCoolMapView().addColumnMap(new ColumnTree(newObject));
        newObject.getCoolMapView().addRowMap(new RowLabels(newObject));
        newObject.getCoolMapView().addRowMap(new RowTree(newObject));
        
        newObject.setName(obj.getName() + " subregion");

        CoolMapMaster.addNewCoolMapObject(newObject);

    }

}

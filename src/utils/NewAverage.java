/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import coolmap.application.CoolMapMaster;
import coolmap.application.utils.Messenger;
import coolmap.data.cmatrix.impl.DoubleCMatrix;
import coolmap.data.cmatrix.model.CMatrix;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sugang
 */
public class NewAverage extends AbstractAction {

    public NewAverage() {
        super("average");
    }

    private void average(ArrayList<CMatrix> selectedMatrices) {
        if (selectedMatrices.isEmpty()) {
            Messenger.showWarningMessage("Please select datasets to continue.", "Empty selection");
            return;
        }

        for (CMatrix matrix : selectedMatrices) {
            if (!(matrix instanceof DoubleCMatrix)) {
                Messenger.showWarningMessage("Dataset '" + matrix + "' is not a numeric matrix.\nOperation aborted.", "Data type error.");
                return;
            }
        }
        CMatrix firstMatrix = selectedMatrices.get(0);

        DoubleCMatrix newMatrix = new DoubleCMatrix("First matrix", firstMatrix.getNumRows(), firstMatrix.getNumColumns());

        //how do you handle missing values? if one is null, then just set it to null?
        //yes. Otherwise there's a huge bias.
        for (int i = 0; i < newMatrix.getNumRows(); i++) {
            for (int j = 0; j < newMatrix.getNumColumns(); j++) {
                newMatrix.setValue(i, j, 0d);
            }
        }

        ArrayList<String> names = new ArrayList(selectedMatrices.size());
        for (CMatrix matrix : selectedMatrices) {
            for (int i = 0; i < matrix.getNumRows(); i++) {
                for (int j = 0; j < matrix.getNumColumns(); j++) {
                    try {
                        Double v = (Double) matrix.getValue(i, j);
                        if (v == null || v.isInfinite() || v.isNaN()) {
                            newMatrix.setValue(i, j, null);
                        } else {
                            newMatrix.setValue(i, j, v + newMatrix.getValue(i, j)); //add to it
                        }
                    } catch (Exception e) {
                        newMatrix.setValue(i, j, null);
                    }
                }
            }

            names.add(matrix.getName());
        }

        int itemNum = selectedMatrices.size();
        for (int i = 0; i < newMatrix.getNumRows(); i++) {
            for (int j = 0; j < newMatrix.getNumColumns(); j++) {
                Double v = (Double) newMatrix.getValue(i, j);
                if (v == null) {
                    continue;
                } else {
                    newMatrix.setValue(i, j, v / itemNum);
                }
            }
        }

        newMatrix.setName("Average of" + names);
        //
        for (int i = 0; i < firstMatrix.getNumRows(); i++) {
            newMatrix.setRowLabel(i, firstMatrix.getRowLabel(i));
        }

        //
        for (int i = 0; i < firstMatrix.getNumColumns(); i++) {
            newMatrix.setColLabel(i, firstMatrix.getColLabel(i));
        }

        CoolMapMaster.addNewBaseMatrix(newMatrix);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<CMatrix> loadedCMatrices = CoolMapMaster.getLoadedCMatrices();
        if (loadedCMatrices == null || loadedCMatrices.isEmpty()) {
            Messenger.showWarningMessage("No datasets were imported.", "No data");
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JTable table = new JTable();
                DefaultTableModel defaultTableModel = Utils.getDefaultTableModel();
                table.setModel(defaultTableModel);
                table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));
                table.getTableHeader().setReorderingAllowed(false);

                int returnVal = JOptionPane.showConfirmDialog(CoolMapMaster.getCMainFrame(), new JScrollPane(table), "Select data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (returnVal == JOptionPane.OK_OPTION) {
                    int[] selectedRows = table.getSelectedRows();
                    ArrayList<CMatrix> selectedMatrices = new ArrayList<CMatrix>();
                    for (int row : selectedRows) {

                        int index = table.convertRowIndexToModel(row);
                        try {
                            String ID = table.getModel().getValueAt(index, 0).toString();
                            CMatrix mx = CoolMapMaster.getCMatrixByID(ID);
                            if (mx != null) {
                                selectedMatrices.add(mx);
                            }
                        } catch (Exception e) {

                        }
                    }
                    //do
                    average(selectedMatrices);
                }
            }
        });

    }
}

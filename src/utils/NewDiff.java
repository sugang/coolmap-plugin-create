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
public class NewDiff extends AbstractAction {

    public NewDiff() {
        super("difference");
    }

        private void difference(ArrayList<CMatrix> selectedMatrices) {
        if (selectedMatrices.size() != 2) {
            Messenger.showWarningMessage("Please select two datasets to continue.", "Invalid selection");
            return;
        }

        for (CMatrix matrix : selectedMatrices) {
            if (!(matrix instanceof DoubleCMatrix)) {
                Messenger.showWarningMessage("Dataset '" + matrix + "' is not a numeric matrix.\nOperation aborted.", "Data type error.");
                return;
            }
        }
        CMatrix firstMatrix = selectedMatrices.get(0);
        CMatrix secondMatrix = selectedMatrices.get(1);

        Object[] options = new Object[]{firstMatrix + " - " + secondMatrix, secondMatrix + " - " + firstMatrix};
        //show options
        int returnValue = JOptionPane.showOptionDialog(CoolMapMaster.getCMainFrame(), "Choose difference", "Create difference", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (returnValue == 0) {
            //nothing
        } else if (returnValue == 1) {
            CMatrix temp = firstMatrix;
            firstMatrix = secondMatrix;
            secondMatrix = temp;
        } else {
            return;
        }

        DoubleCMatrix newMatrix = new DoubleCMatrix(firstMatrix.getName() + " - " + secondMatrix.getName(), firstMatrix.getNumRows(), firstMatrix.getNumColumns());

        //
        for (int i = 0; i < newMatrix.getNumRows(); i++) {
            for (int j = 0; j < newMatrix.getNumColumns(); j++) {
                try {
                    Double v1 = (Double) firstMatrix.getValue(i, j);
                    Double v2 = (Double) secondMatrix.getValue(i, j);
                    newMatrix.setValue(i, j, v1 - v2);
                } catch (Exception e) {

                }
            }
        }

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
                    difference(selectedMatrices);
                }
            }
        });

    }
}

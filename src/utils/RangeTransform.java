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
public class RangeTransform extends AbstractAction {

    public RangeTransform() {
        super("[0-1]");
    }

    private void createRangeTransform(ArrayList<CMatrix> selectedMatrices) {
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

        for (CMatrix matrix : selectedMatrices) {
            if (matrix instanceof DoubleCMatrix) {
                DoubleCMatrix newMatrix = new DoubleCMatrix(matrix.getName() + " [0-1] transformed", matrix.getNumRows(), matrix.getNumColumns());

                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;

                for (int i = 0; i < matrix.getNumRows(); i++) {
                    for (int j = 0; j < matrix.getNumColumns(); j++) {
                        try {
                            Double v = (Double) matrix.getValue(i, j);
                            if (v == null || v.isInfinite() || v.isNaN()) {
                                continue;
                            } else {
                                if (v < min) {
                                    min = v;
                                }
                                if (v > max) {
                                    max = v;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }

                double range = max - min;
                if (range == 0) {
                    //Every value is the same, no need to transform
                    for (int i = 0; i < matrix.getNumRows(); i++) {
                        for (int j = 0; j < matrix.getNumColumns(); j++) {
                            try {
                                newMatrix.setValue(i, j, (Double) matrix.getValue(i, j));
                            } catch (Exception e) {
                                newMatrix.setValue(i, j, null);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < matrix.getNumRows(); i++) {
                        for (int j = 0; j < matrix.getNumColumns(); j++) {
                            try {
                                Double v = (Double) matrix.getValue(i, j);
                                newMatrix.setValue(i, j, (v - min) / range);
                            } catch (Exception e) {
                                newMatrix.setValue(i, j, null);
                            }
                        }
                    }
                }

                //
                for (int i = 0; i < matrix.getNumRows(); i++) {
                    newMatrix.setRowLabel(i, matrix.getRowLabel(i));
                }

                //
                for (int i = 0; i < matrix.getNumColumns(); i++) {
                    newMatrix.setColLabel(i, matrix.getColLabel(i));
                }

                CoolMapMaster.addNewBaseMatrix(newMatrix);
//                newMatrix.printMatrix();

            }
        }
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
                    createRangeTransform(selectedMatrices);
                }
            }
        });

    }
}

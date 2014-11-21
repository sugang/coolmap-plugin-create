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
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author sugang
 */
public class ZTransform extends AbstractAction {

    public ZTransform() {
        super("z");
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
                    createZTransform(selectedMatrices);
                }
            }
        });

    }

    private void createZTransform(ArrayList<CMatrix> selectedMatrices) {
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

        //selected matrices
        for (CMatrix matrix : selectedMatrices) {
            ArrayList<Double> values = new ArrayList<>();
            DoubleCMatrix newMatrix = new DoubleCMatrix(matrix.getName() + " z transformed", matrix.getNumRows(), matrix.getNumColumns());

            //
            for (int i = 0; i < matrix.getNumRows(); i++) {
                for (int j = 0; j < matrix.getNumColumns(); j++) {
                    try {
                        Double value = (Double) matrix.getValue(i, j);
                        if (value == null || value.isInfinite() || value.isNaN()) {
                            continue;
                        } else {
                            values.add(value);
                        }
                    } catch (Exception e) {
                    }
                }//loop of num columns

            }//loop of num rows

            double[] v = new double[values.size()];
            for (int i = 0; i < v.length; i++) {
                v[i] = values.get(i);
            }

            Mean mean = new Mean();
            StandardDeviation sd = new StandardDeviation();

            double mu = mean.evaluate(v);
            double sigma = sd.evaluate(v);

//            System.out.println("Mean: " + mu + " sd:" + sigma);
            for (int i = 0; i < matrix.getNumRows(); i++) {
                for (int j = 0; j < matrix.getNumColumns(); j++) {
                    Double value = (Double) matrix.getValue(i, j);
                    if (value == null || value.isInfinite() || value.isNaN()) {
                        continue;
                    } else {
                        newMatrix.setValue(i, j, (value - mu) / sigma);
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

        }//end of matrices

    }
}

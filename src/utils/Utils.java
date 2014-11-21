/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import coolmap.application.CoolMapMaster;
import coolmap.data.cmatrix.model.CMatrix;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sugang
 */
public class Utils {

    private final static String[] tableHeaders = new String[]{"ID", "Name", "Rows", "Columns", "Type"};

    public static DefaultTableModel getDefaultTableModel() {
        List<CMatrix> cMatrices = CoolMapMaster.getLoadedCMatrices();
        if (!cMatrices.isEmpty()) {

            Object[][] data = new Object[cMatrices.size()][5];
            for (int i = 0; i < cMatrices.size(); i++) {
                CMatrix mx = cMatrices.get(i);
                data[i][0] = mx.getID();
                data[i][1] = mx.getName();
                data[i][2] = mx.getNumRows();
                data[i][3] = mx.getNumColumns();
                data[i][4] = mx.getMemberClass().getSimpleName();
            }

            final DefaultTableModel model = new DefaultTableModel(data, tableHeaders) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

            };

            return model;

        } else {

            return new DefaultTableModel();
        }
    }
}

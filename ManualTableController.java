package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class ManualTableController {

    @FXML private TextField tableNameField, columnsField;
    @FXML private TextField insertTableField, insertColumnsField, insertValuesField;
    @FXML private TextField selectTableField, limitField;
    @FXML private TextField joinTable1Field, joinTable2Field, joinConditionField;
    @FXML private TextField updateTableField, updateSetField, updateWhereField;
    @FXML private TextField deleteTableField, deleteWhereField;
    @FXML private TextField dropTableField;
    
    @FXML private TableView<ObservableList<String>> dataTable;
    @FXML private TextArea structureArea;
    @FXML private ComboBox<String> tablesComboBox;

    @FXML
    public void initialize() {
        refreshTablesList();
        
        // When table selection changes, show its structure
        tablesComboBox.setOnAction(e -> {
            String selectedTable = tablesComboBox.getSelectionModel().getSelectedItem();
            if (selectedTable != null) {
                showTableStructure(selectedTable);
            }
        });
    }

    private void refreshTablesList() {
        List<String> tables = DBHandler.getAllTables();
        tablesComboBox.setItems(FXCollections.observableArrayList(tables));
    }

    private void showTableStructure(String tableName) {
        String structure = DBHandler.getTableStructure(tableName);
        structureArea.setText(structure);
    }

    // ✅ MANUAL TABLE CREATION
    @FXML
    private void handleCreateTable() {
        String tableName = tableNameField.getText().trim();
        String columns = columnsField.getText().trim();
        
        if (tableName.isEmpty() || columns.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Table name and columns are required!");
            return;
        }
        
        if (DBHandler.createCustomTable(tableName, columns)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Table '" + tableName + "' created successfully!");
            tableNameField.clear();
            columnsField.clear();
            refreshTablesList();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create table!");
        }
    }

    // ✅ MANUAL DATA INSERTION
    @FXML
    private void handleInsertData() {
        String tableName = insertTableField.getText().trim();
        String columnsInput = insertColumnsField.getText().trim();
        String valuesInput = insertValuesField.getText().trim();
        
        if (tableName.isEmpty() || columnsInput.isEmpty() || valuesInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are required for insertion!");
            return;
        }
        
        String[] columns = columnsInput.split(",");
        String[] values = valuesInput.split(",");
        
        // Trim whitespace from each element
        for (int i = 0; i < columns.length; i++) columns[i] = columns[i].trim();
        for (int i = 0; i < values.length; i++) values[i] = values[i].trim();
        
        if (DBHandler.insertData(tableName, columns, values)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Data inserted into '" + tableName + "'!");
            insertTableField.clear();
            insertColumnsField.clear();
            insertValuesField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to insert data!");
        }
    }

    // ✅ MANUAL DATA SELECTION WITH LIMIT
    @FXML
    private void handleSelectData() {
        String tableName = selectTableField.getText().trim();
        String limitText = limitField.getText().trim();
        
        if (tableName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Table name is required!");
            return;
        }
        
        int limit = 10; // default
        if (!limitText.isEmpty()) {
            try {
                limit = Integer.parseInt(limitText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Limit must be a number!");
                return;
            }
        }
        
        List<String[]> data = DBHandler.selectData(tableName, limit);
        displayDataInTable(data);
    }

    // ✅ MANUAL JOIN OPERATION
    @FXML
    private void handleJoin() {
        String table1 = joinTable1Field.getText().trim();
        String table2 = joinTable2Field.getText().trim();
        String condition = joinConditionField.getText().trim();
        
        if (table1.isEmpty() || table2.isEmpty() || condition.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All join fields are required!");
            return;
        }
        
        List<String[]> data = DBHandler.customJoin(table1, table2, condition, 10);
        displayDataInTable(data);
    }

    // ✅ MANUAL DATA UPDATE
    @FXML
    private void handleUpdateData() {
        String tableName = updateTableField.getText().trim();
        String setClause = updateSetField.getText().trim();
        String whereClause = updateWhereField.getText().trim();
        
        if (tableName.isEmpty() || setClause.isEmpty() || whereClause.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All update fields are required!");
            return;
        }
        
        if (DBHandler.updateData(tableName, setClause, whereClause)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Data updated in '" + tableName + "'!");
            updateTableField.clear();
            updateSetField.clear();
            updateWhereField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update data!");
        }
    }

    // ✅ MANUAL DATA DELETION
    @FXML
    private void handleDeleteData() {
        String tableName = deleteTableField.getText().trim();
        String whereClause = deleteWhereField.getText().trim();
        
        if (tableName.isEmpty() || whereClause.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Table name and WHERE clause are required!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Data");
        confirm.setContentText("This will delete data from '" + tableName + "'. Continue?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (DBHandler.deleteData(tableName, whereClause)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Data deleted from '" + tableName + "'!");
                deleteTableField.clear();
                deleteWhereField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete data!");
            }
        }
    }

    // ✅ MANUAL TABLE DROP
    @FXML
    private void handleDropTable() {
        String tableName = dropTableField.getText().trim();
        
        if (tableName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Table name is required!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Drop");
        confirm.setHeaderText("Delete Table");
        confirm.setContentText("This will permanently delete table '" + tableName + "'. Continue?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (DBHandler.dropTable(tableName)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Table '" + tableName + "' dropped!");
                dropTableField.clear();
                refreshTablesList();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to drop table!");
            }
        }
    }

    // ✅ DISPLAY DATA IN TABLE VIEW
    private void displayDataInTable(List<String[]> data) {
        dataTable.getColumns().clear();
        dataTable.getItems().clear();
        
        if (data.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data", "No data found!");
            return;
        }
        
        // Create columns from headers (first row)
        String[] headers = data.get(0);
        for (int i = 0; i < headers.length; i++) {
            final int columnIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers[i]);
            column.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().get(columnIndex)));
            dataTable.getColumns().add(column);
        }
        
        // Add data rows
        ObservableList<ObservableList<String>> tableData = FXCollections.observableArrayList();
        for (int i = 1; i < data.size(); i++) {
            tableData.add(FXCollections.observableArrayList(data.get(i)));
        }
        dataTable.setItems(tableData);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
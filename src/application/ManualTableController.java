package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualTableController {

    @FXML private TextField tableNameField, columnsField;
    @FXML private TextField selectTableField, limitField;
    @FXML private TextField joinTable1Field, joinTable2Field, joinConditionField;
    @FXML private TextField updateTableField, updateSetField, updateWhereField;
    @FXML private TextField deleteTableField, deleteWhereField;
    @FXML private TextField dropTableField;
    
    @FXML private TableView<ObservableList<String>> dataTable;
    @FXML private TextArea structureArea;
    @FXML private ComboBox<String> tablesComboBox;
    @FXML private TabPane tabPane;
    // NEW: Dynamic form components
    @FXML private VBox dynamicFormBox;
    @FXML private ComboBox<String> dataOpsTableComboBox;
    private Map<String, TextField> inputFields = new HashMap<>();

    @FXML
    public void initialize() {
        refreshTablesList();
     // Prevent tab closing
        tabPane.getTabs().forEach(tab -> tab.setClosable(false));
        // When table selection changes, show its structure
        tablesComboBox.setOnAction(e -> {
            String selectedTable = tablesComboBox.getSelectionModel().getSelectedItem();
            if (selectedTable != null) {
                showTableStructure(selectedTable);
            }
        });
        
        // Initialize data operations table combo box
        dataOpsTableComboBox.setItems(FXCollections.observableArrayList(DBHandler.getAllTables()));
    }

    private void refreshTablesList() {
        List<String> tables = DBHandler.getAllTables();
        ObservableList<String> tableList = FXCollections.observableArrayList(tables);
        tablesComboBox.setItems(tableList);
        dataOpsTableComboBox.setItems(tableList);
    }

    private void showTableStructure(String tableName) {
        String structure = DBHandler.getTableStructure(tableName);
        structureArea.setText(structure);
    }

    // ✅ DYNAMIC FORM: Load form when table is selected in Data Operations tab
    @FXML
    private void handleDataOpsTableSelected() {
        String selectedTable = dataOpsTableComboBox.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            createDynamicForm(selectedTable);
        }
    }
    
    // ✅ DYNAMIC FORM: Create input fields based on table columns
    private void createDynamicForm(String tableName) {
        dynamicFormBox.getChildren().clear();
        inputFields.clear();
        
        List<String> columns = DBHandler.getTableColumns(tableName);
        
        if (columns.isEmpty()) {
            Label noColumnsLabel = new Label("No editable columns found or table doesn't exist");
            noColumnsLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic;");
            dynamicFormBox.getChildren().add(noColumnsLabel);
            return;
        }
        
        Label formTitle = new Label("Enter data for: " + tableName);
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        dynamicFormBox.getChildren().add(formTitle);
        
        for (String column : columns) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            
            Label label = new Label(column + ":");
            label.setPrefWidth(120);
            label.setStyle("-fx-font-weight: bold;");
            
            TextField textField = new TextField();
            textField.setPromptText("Enter " + column);
            textField.setPrefWidth(200);
            
            inputFields.put(column, textField);
            
            row.getChildren().addAll(label, textField);
            dynamicFormBox.getChildren().add(row);
        }
        
        // Add some spacing
        dynamicFormBox.getChildren().add(new Label(" "));
    }

    // ✅ DYNAMIC FORM: Insert data using dynamic form
    @FXML
    private void handleDynamicInsert() {
        String selectedTable = dataOpsTableComboBox.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a table first!");
            return;
        }
        
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        
        // Collect data from input fields
        for (Map.Entry<String, TextField> entry : inputFields.entrySet()) {
            String value = entry.getValue().getText().trim();
            if (!value.isEmpty()) {
                columns.add(entry.getKey());
                values.add(value);
            }
        }
        
        if (columns.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter some data!");
            return;
        }
        
        // Convert to arrays for DBHandler
        String[] columnsArray = columns.toArray(new String[0]);
        String[] valuesArray = values.toArray(new String[0]);
        
        if (DBHandler.insertData(selectedTable, columnsArray, valuesArray)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Data inserted successfully into '" + selectedTable + "'!");
            
            // Clear all fields
            for (TextField field : inputFields.values()) {
                field.clear();
            }
            
            // Refresh the data table view
            refreshTableData(selectedTable);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to insert data!");
        }
    }
    
    // ✅ Refresh table data after insert
    private void refreshTableData(String tableName) {
        List<String[]> data = DBHandler.selectData(tableName, 50);
        displayDataInTable(data);
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

# GWT-Advanced-Table
Automatically exported from code.google.com/p/gwt-advanced-table

GWT Advanced Table
GWT table widget supporting paging, sorting and filtering

Introduction
GWT Advanced Table is GWT table widget that supports row paging, column sorting and data filtering.

It provides a table model service interface that is used as data provider that can be implemented by database, Hibernate or other back-end.

The front-end component is reusable and customizable. The full source code is available as freeware.

News
----

9-November-2007

Implemented multiple row selection feature
This is how the GWT Advanced Table looks like now: ...

8-November-2007

Fixed a bug with incorrect sizing and scrolling of the table
Now the table works correctly in Mozilla and IE with size given as pixels or as percents

5-September-2007

Fixed problem with NULL values in the data rows
Added "hide first column" feature
Added RowSelectionListener for handling the "select row" event (assuming the first column is the table primary key)
20-August-2007

The first version of the AdvancedTable widget published as open source project.

Preview
This is how the GWT Advanced Table looks like:

...

How to run the example
To run the example you need to download the Eclipse project and run it. We have provided scripts for compilation and execution.

How to use the code
1. Implement the interface TableModelService:

```java
public interface TableModelService extends RemoteService {
        public TableColumn[] getColumns();
        public int getRowsCount(DataFilter[] filters);
        public String[][] getRows(int startRow, int rowsCount,
                DataFilter[] filters, String sortColumn, boolean sortOrder);
}
```

2. Create AdvancedTable widget, assign table model and add it to the root panel:

```java
AdvancedTable table = new AdvancedTable();
TableModelServiceAsync someTableService = <create table model service async>;
table.setTableModelService(usersTableService);
RootPanel.get().add(table);
```

License
MIT license. Use it at your own risk.

Credits
(c) 2007 by Svetlin Nakov - http://www.nakov.com

# AdvancedTable #

AdvancedTable is GWT table widget that supports data paging, filtering and column sorting. Paging, filtering and sorting are done by the server side. The table uses a data provider, the class TableModelService.

## How to use it ##

```
AdvancedTable table = new AdvancedTable();
TableModelServiceAsync someTableService =
    <create table model service async>;
table.setTableModelService(usersTableService);
RootPanel.get().add(table);
```
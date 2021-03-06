/**
 * TableModelService is a service interface that provides data for displaying
 * in a GWT AdvancedTable widget. The implementing class should provide
 * paging, filtering and sorting as this interface specifies.
 * 
 * Life-cycle:
 * 1) getColumns() is called by the client to populate the table columns
 * 2) getRowsCount() is called by the client to estimate the number of
 *    available records on the server.
 * 3) getRows() is called by the client to display a particular page (a 
 *    subset of the available data)
 * The client call getRowsCount() and getRows() with the same filter. The
 * implementing class can use database or other back-end as data source.
 * 
 * The first table column is used as row identifier (primary key). It can
 * be visible in the table or can be hidden and is passed as row id when a
 * row is selected. 
 * 
 * (c) 2007 by Svetlin Nakov - http://www.nakov.com
 * National Academy for Software Development - http://academy.devbg.org 
 * This software is freeware. Use it at your own risk.
 */

package example.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TableModelServiceAsync {
	public void getColumns(AsyncCallback callback);
	public void getRowsCount(DataFilter[] filters, AsyncCallback callback);
	public void getRows(int startRow, int rowsCount,
		DataFilter[] filters, String sortColumn, boolean sortOrder, AsyncCallback callback);
}

/**
 * TableModelServiceAsync is asynchronous version of the TableModelService
 * interface.
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

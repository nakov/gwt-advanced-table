/**
 * AdvancedTable is GWT table widget that supports data paging, filtering
 * and column sorting. Paging, filtering and sorting are done by the server
 * side. The table uses a data provider, the class TableModelService. 
 * 
 * How to use it:
 * 
 * AdvancedTable table = new AdvancedTable();
 * TableModelServiceAsync someTableService =
 *     <create table model service async>;
 * table.setTableModelService(usersTableService);
 * RootPanel.get().add(table);
 * 
 * (c) 2007 by Svetlin Nakov - http://www.nakov.com
 * National Academy for Software Development - http://academy.devbg.org 
 * This software is freeware. Use it at your own risk.
 */

package example.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;

public class AdvancedTable extends Composite {
	
	private static final int DEFAULT_PAGE_SIZE = 5;
	private static final int STATUS_INFO = 1001;
	private static final int STATUS_ERROR = 1002;
	private static final int STATUS_WAIT = 1003;
	private static final String SORT_ASC_SYMBOL = " \u25b2";
	private static final String SORT_DESC_SYMBOL = " \u25bc";

	private final Grid grid;
	private final Label statusLabel;
	private final Button buttonFirstPage;
	private final Button buttonPrevPage;
	private final Button buttonNextPage;
	private final Button buttonLastPage;
	
	private int pageSize = DEFAULT_PAGE_SIZE;
	private TableModelServiceAsync tableModelService;
	private TableColumn[] columns;
	private DataFilter[] filters;
	private String[][] pageRows;
	private int totalRowsCount;
	private int currentPageRowsCount;
	private int currentPageStartRow;
	private int currentPageIndex;
	private String sortColumnName;
	private boolean sortOrder;
	
	public AdvancedTable() {
		super();
		
		final FlowPanel contentFlowPanel = new FlowPanel();
		initWidget(contentFlowPanel);
		
		grid = new Grid();
		grid.setWidth("100%");
		grid.setCellSpacing(0);
		grid.setBorderWidth(1);
		if (! GWT.isScript()) {
			// Display a preview of the table (when not in browser mode)
			grid.resize(DEFAULT_PAGE_SIZE+1, 3);
			grid.setText(0, 0, "Column 1");
			grid.setText(0, 1, "Column 2");
			grid.setText(0, 2, "Column 3");
		}
		contentFlowPanel.add(grid);
		
		final HorizontalPanel navigationPanel = new HorizontalPanel();
		contentFlowPanel.add(navigationPanel);
		navigationPanel.setWidth("100%");

		final Button buttonRefresh = new Button();
		navigationPanel.add(buttonRefresh);
		buttonRefresh.setSize("70", "23");
		navigationPanel.setCellVerticalAlignment(buttonRefresh, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonRefresh.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				AdvancedTable.this.buttonRefreshClicked();
			}
		});
		buttonRefresh.setText("Refresh");
		
		statusLabel = new Label();
		navigationPanel.add(statusLabel);
		navigationPanel.setCellHorizontalAlignment(
			statusLabel, HasHorizontalAlignment.ALIGN_RIGHT);
		navigationPanel.setCellVerticalAlignment(
			statusLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		showStatus("Table model service not available.", STATUS_ERROR);
		
		buttonFirstPage = new Button();
		navigationPanel.add(buttonFirstPage);
		buttonFirstPage.setSize("25", "23");
		navigationPanel.setCellVerticalAlignment(buttonFirstPage, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonFirstPage.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				AdvancedTable.this.buttonFirstPageClicked();
			}
		});
		navigationPanel.setCellHorizontalAlignment(
			buttonFirstPage, HasHorizontalAlignment.ALIGN_RIGHT);
		navigationPanel.setCellWidth(buttonFirstPage, "35");
		buttonFirstPage.setText("<<");
		
		buttonPrevPage = new Button();
		navigationPanel.add(buttonPrevPage);
		buttonPrevPage.setSize("20", "23");
		navigationPanel.setCellVerticalAlignment(buttonPrevPage, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPrevPage.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				AdvancedTable.this.buttonPrevPageClicked();
			}
		});
		navigationPanel.setCellHorizontalAlignment(
			buttonPrevPage, HasHorizontalAlignment.ALIGN_RIGHT);
		navigationPanel.setCellWidth(buttonPrevPage, "23");
		buttonPrevPage.setText("<");
		
		buttonNextPage = new Button();
		navigationPanel.add(buttonNextPage);
		buttonNextPage.setSize("20", "23");
		navigationPanel.setCellVerticalAlignment(buttonNextPage, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonNextPage.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				AdvancedTable.this.buttonNextPageClicked();
			}
		});
		navigationPanel.setCellHorizontalAlignment(
			buttonNextPage, HasHorizontalAlignment.ALIGN_RIGHT);
		navigationPanel.setCellWidth(buttonNextPage, "23");
		buttonNextPage.setText(">");

		buttonLastPage = new Button();
		navigationPanel.add(buttonLastPage);
		buttonLastPage.setSize("25", "23");
		navigationPanel.setCellVerticalAlignment(buttonLastPage, HasVerticalAlignment.ALIGN_MIDDLE);
		buttonLastPage.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				AdvancedTable.this.buttonLastPageClicked();
			}
		});
		navigationPanel.setCellHorizontalAlignment(
			buttonLastPage, HasHorizontalAlignment.ALIGN_RIGHT);
		navigationPanel.setCellWidth(buttonLastPage, "28");
		buttonLastPage.setText(">>");
	}
	
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * Allows modifying the default page size for this table.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public TableModelServiceAsync getTableModelService() {
		return this.tableModelService;
	}

	/**
	 * Sets a table model for this table, updates its columns and rows
	 * based on the information coming from the server and redraws the
	 * table contents (column titles and data rows).
	 */
	public void setTableModelService(TableModelServiceAsync tableModelService) {
		this.tableModelService = tableModelService;
		this.updateTableColumns(new AsyncCallback() {
			public void onFailure(Throwable caught) {
				AdvancedTable.this.showStatus(
					"Can not get table columns from the server.",
					STATUS_ERROR);
			}
			public void onSuccess(Object result) {
				AdvancedTable.this.updateTableData();
			}
		});
	}
	
	/**
	 * Updates and redraws the table columns based on the table data
	 * coming from the server.
	 */
	public void updateTableColumns(final AsyncCallback completedCallback) {
		this.tableModelService.getColumns(new AsyncCallback() {
			public void onFailure(Throwable caught) {
				completedCallback.onFailure(caught);
			}
			public void onSuccess(Object result) {
				AdvancedTable.this.columns = (TableColumn[]) result;
				AdvancedTable.this.sortColumnName = null;
				redrawTableColumns();
				completedCallback.onSuccess(result);
			}
		});
	}
	
	/**
	 * Updates and redraws the table data rows of the currently
	 * selected page based on the information from the table model
	 * coming from the server. This method should be called after
	 * each change of the data at the server side (e.g. when applying
	 * data filtering or need to refresh the table rows).
	 */
	public void updateTableData() {
		showStatus("Loading...", STATUS_WAIT);

		// Reset the active page number
		this.currentPageIndex = 0;
		
		// Initialize the number of rows in the table:
		// 1 header row + pageSize data rows
		grid.resizeRows(1 + this.pageSize);
		
		this.updateRowsCount(new AsyncCallback() {
			public void onFailure(Throwable caught) {
				AdvancedTable.this.showStatus(
					"Can not get table rows count from the server.",
					STATUS_ERROR);
			}
			public void onSuccess(Object result) {
				AdvancedTable.this.updateRows();
			}
		});
	}
	
	/**
	 * Applies a set of filters over the table data and redraws the table.
	 */
	public void applyFilters(DataFilter[] filters) {
		this.filters = filters;
		updateTableData();
	}
	
	public void applySorting(String sortColumnName, boolean sortOrder) {
		this.sortColumnName = sortColumnName;
		this.sortOrder = sortOrder;
		redrawColumnTitles();
		updateRows();
	}

	/**
	 * Retrieves the table columns from the server and redraws them.
	 */
	private void redrawTableColumns() {
		// Create the header row and adjust table columns number 
		if (grid.getRowCount() == 0) {
			grid.resizeRows(1);
		}
		grid.getRowFormatter().setStyleName(0, "advancedTableHeader");
		grid.resizeColumns(this.columns.length);
		
		// Fill the column titles
		redrawColumnTitles();
		
		// Add event handler to perform sorting on header column click 
		this.grid.addTableListener(new TableListener() {
			public void onCellClicked(SourcesTableEvents sender, 
					int row, int cell) {
				if (row == 0) {
					String column = AdvancedTable.this.columns[cell].getName();
					AdvancedTable.this.applySorting(column);
				}
			}
		});
	}

	private void applySorting(String column) {
		if (column.equals(this.sortColumnName)) {
			applySorting(this.sortColumnName, ! this.sortOrder);
		} 
		else {
			applySorting(column, true);
		}
	}

	private void redrawColumnTitles() {
		for (int col=0; col<this.columns.length; col++) {
			TableColumn column = this.columns[col];
			String title = column.getTitle();
			if (column.getName().equals(this.sortColumnName)) {
				if (this.sortOrder) {
					title = title + SORT_ASC_SYMBOL;
				}
				else
				{
					title = title + SORT_DESC_SYMBOL;
				}
			}
			grid.setText(0, col, title);
		}
	}
	
	private void updateRowsCount(final AsyncCallback completedCallback) {
		this.tableModelService.getRowsCount(
				this.filters, new AsyncCallback() {
			public void onFailure(Throwable caught) {
				completedCallback.onFailure(caught);
			}
			public void onSuccess(Object result) {
				int count = ((Integer) result).intValue();
				AdvancedTable.this.totalRowsCount = count;
				completedCallback.onSuccess(result);
			}
		});
	}
	
	private void updateRows() {
		showStatus("Loading...", STATUS_WAIT);
		
		// Check for empty table - it is special case
		if (this.totalRowsCount == 0) {
			drawEmptyTable();
			return;
		}
		
		// Calculate current page index
		int pagesCount = calcPagesCount();
		if (this.currentPageIndex >= pagesCount) {
			this.currentPageIndex = pagesCount-1;
		}
		
		// Calculate start row and rows count for the server request 
		this.currentPageStartRow = this.currentPageIndex * this.pageSize;
		this.currentPageRowsCount = this.pageSize;
		if (this.currentPageStartRow+this.currentPageRowsCount > this.totalRowsCount) {
			this.currentPageRowsCount = this.totalRowsCount % this.pageSize;
		}
		
		// Asynchronously get rows from the server
		this.tableModelService.getRows(
				this.currentPageStartRow, this.currentPageRowsCount,
				this.filters, this.sortColumnName, this.sortOrder, 
				new AsyncCallback() {
			public void onFailure(Throwable caught) {
				AdvancedTable.this.showStatus(
					"Can not get table rows data from the server.",
					STATUS_ERROR);
			}
			public void onSuccess(Object result) {
				AdvancedTable.this.pageRows = (String[][]) result;
				AdvancedTable.this.redrawRows();
			}
		});
	}
	
	private int calcPagesCount() {
		int pagesCount = 
			(this.totalRowsCount + pageSize - 1) / this.pageSize;
		return pagesCount;
	}
	
	private void redrawNavigationArea() {
		int startRow = this.currentPageIndex * this.pageSize;
		String rowsInfo = "Rows " + (startRow+1) + "-" + 
			(startRow+this.currentPageRowsCount) + " of " +
			this.totalRowsCount;
		showStatus(rowsInfo, STATUS_INFO);
		
		int pagesCount = calcPagesCount();
		boolean enabledPrevFirstPage = 
			(pagesCount>0) && (this.currentPageIndex > 0);
		this.buttonFirstPage.setEnabled(enabledPrevFirstPage);
		this.buttonPrevPage.setEnabled(enabledPrevFirstPage);

		boolean enabledNextLastPage = 
			(pagesCount>0) && (this.currentPageIndex < pagesCount-1);
		this.buttonNextPage.setEnabled(enabledNextLastPage);
		this.buttonLastPage.setEnabled(enabledNextLastPage);		
	}
	
	private void redrawRows() {
		for (int row=0; row<this.pageSize; row++) {
			if (row < this.currentPageRowsCount) {
				// Fill data row in the table
				for (int col=0; col<this.columns.length; col++) {
					String cellValue = this.pageRows[row][col];
					grid.setText(row+1, col, cellValue);
				}
			} else {
				// Fill empty row in the table
				for (int col=0; col<this.columns.length; col++) {
					grid.setHTML(row+1, col, "&nbsp;");
				}
			}
		}

		redrawNavigationArea();		
	}
	
	private void drawEmptyTable() {
		for (int row=0; row<this.pageSize; row++) {
			for (int col=0; col<this.columns.length; col++) {
				grid.setText(row+1, col, " ");
			}
		}
		redrawNavigationArea();
		showStatus("No data found.", STATUS_INFO);		
	}

	private void buttonFirstPageClicked() {
		this.currentPageIndex = 0;
		this.updateRows();		
	}

	private void buttonPrevPageClicked() {
		if (this.currentPageIndex > 0) {
			this.currentPageIndex--;
			this.updateRows();	
		}			
	}

	private void buttonNextPageClicked() {
		int pagesCount = calcPagesCount();
		if (this.currentPageIndex < pagesCount-1) {
			this.currentPageIndex++;
			this.updateRows();	
		}
	}

	private void buttonLastPageClicked() {
		int pagesCount = calcPagesCount();
		this.currentPageIndex = pagesCount;
		this.updateRows();
	}
	
	private void buttonRefreshClicked() {
		this.updateTableData();
	}
	
	private void showStatus(String text, int statusLevel) {
		if (statusLevel == STATUS_INFO) {
			this.statusLabel.setText(text);
		} 
		else if (statusLevel == STATUS_WAIT) {
			this.statusLabel.setText(text);
		} 
		else if (statusLevel == STATUS_ERROR) {
			this.statusLabel.setText("Error: " + text);
		} 
		else {
			throw new IllegalArgumentException("Illegal statusLevel.");
		}		
	}

}

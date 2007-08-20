/**
 * Example GWT module that shows how to use the GWT AdvancedTable widget
 * and its data provider - the TableModelService interface.
 * 
 * (c) 2007 by Svetlin Nakov - http://www.nakov.com
 * National Academy for Software Development - http://academy.devbg.org 
 * This software is freeware. Use it at your own risk.
 */

package example.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class GWTAdvancedTableExample implements EntryPoint {

	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();

		final AdvancedTable table = new AdvancedTable();
		TableModelServiceAsync usersTableService =
			ServiceUtils.getTableModelServiceAsync();
		table.setTableModelService(usersTableService);
		
		rootPanel.add(table, 11, 65);
		table.setSize("402px", "127px");

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		rootPanel.add(horizontalPanel, 10, 34);
		horizontalPanel.setSize("403px", "23px");

		final Label labelFilter = new Label("Filter:");
		horizontalPanel.add(labelFilter);
		horizontalPanel.setCellVerticalAlignment(labelFilter, HasVerticalAlignment.ALIGN_MIDDLE);
		labelFilter.setWidth("50");

		final TextBox textBoxFilter = new TextBox();
		horizontalPanel.add(textBoxFilter);
		textBoxFilter.setWidth("100%");
		horizontalPanel.setCellWidth(textBoxFilter, "100%");

		final Button buttonApplyFilter = new Button();
		horizontalPanel.add(buttonApplyFilter);
		buttonApplyFilter.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				DataFilter filter = new DataFilter("keyword", 
					textBoxFilter.getText());
				DataFilter[] filters = {filter};
				table.applyFilters(filters);
			}
		});
		buttonApplyFilter.setWidth("100");
		horizontalPanel.setCellWidth(buttonApplyFilter, "100");
		horizontalPanel.setCellHorizontalAlignment(buttonApplyFilter, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonApplyFilter.setText("Apply Filter");

		final Button clearFilterButton = new Button();
		horizontalPanel.add(clearFilterButton);
		clearFilterButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				table.applyFilters(null);
				textBoxFilter.setText("");
			}
		});
		clearFilterButton.setWidth("100");
		horizontalPanel.setCellWidth(clearFilterButton, "100");
		clearFilterButton.setText("Clear Filter");

		final Label labelTitle = new Label("Advanced GWT Table (Freeware)");
		rootPanel.add(labelTitle, 10, 10);

		final Label labelCopyright = new Label("(c) 2007 by Svetlin Nakov");
		rootPanel.add(labelCopyright, 236, 10);
		labelCopyright.setSize("176px", "19px");
		labelCopyright.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	}

}

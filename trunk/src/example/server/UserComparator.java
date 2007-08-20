/**
 * Coparator for sorting instances of the sample User entity class.
 * 
 * (c) 2007 by Svetlin Nakov - http://www.nakov.com
 * National Academy for Software Development - http://academy.devbg.org 
 * This software is freeware. Use it at your own risk.
 */

package example.server;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {
	
	private String sortColumn;
	private boolean sortingOrder;
	
	public UserComparator(String sortColumn, boolean sortingOrder) {
		this.sortColumn = sortColumn;
		this.sortingOrder = sortingOrder;
	}
	
	@SuppressWarnings("unchecked")
	public int compare(User user1, User user2) {
		Comparable column1 = (Comparable)
			ReflectionUtils.getPropertyValue(
			user1, this.sortColumn);
		Comparable column2 = (Comparable)
			ReflectionUtils.getPropertyValue(
			user2, this.sortColumn);
		int compareResult = column1.compareTo(column2);
		if (!this.sortingOrder) {
			compareResult = -1 * compareResult;
		}
		return compareResult;
	}
	
}

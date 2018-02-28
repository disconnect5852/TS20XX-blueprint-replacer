import java.util.List;

import javax.swing.table.AbstractTableModel;


public class RWTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -162903277521189656L;
	private List<Replaceable> list;
	private static final String[] header={"Type", "Provider", "Product", "BlueprintID", "targetProvider", "targetProduct", "targetBlueprintID", "heightOffset"};
	

	public RWTableModel(List<Replaceable> list) {
		super();
		this.list = list;
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		Replaceable entity= list.get(row);
	    switch (column) {
	    
	    case 0:
	     return entity.getType();
	    case 1:
	     return entity.getCurrentProvider();
	    case 2:
	     return entity.getCurrentProduct();
	    case 3:
	     return entity.getCurrentBlueprintID();
	    case 4:
	     return entity.getTargetProvider();
	    case 5:
		 return entity.getTargetProduct();
	    case 6:
		 return entity.getTargetBlueprintID();
	    case 7:
			 return entity.getHeightOffset();
	    default :
	     return "";
	    }
	}

	@Override
	public String getColumnName(int column) {
		return header[column];
	}
	

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column>=4 /*&& column<=6*/) return true;
		return false;
	}
	
	

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		Replaceable entity= list.get(row);
	    switch (column) {
	    case 4:
	    	 entity.setTargetProvider((String) aValue);
	    	 break;
	    case 5:
	    	 entity.setTargetProduct((String) aValue);
	    	 break;
	    case 6:
	    	 entity.setTargetBlueprintID((String) aValue);
	    	 break;
	    case 7:
	    	try {
	    		 entity.setHeightOffset(Double.parseDouble(((String) aValue)));
			} catch (Exception e) {
				//entity.setHeightOffset(0f);
			}
	    }
	    //System.out.println("set:"+ aValue+" at: "+row+":"+column);
		//list.set(row, entity);
	}
	public void setValuesAtRow(String provider, String product, String blueprintID, int row) {
		Replaceable entity= list.get(row);
		entity.setTargetProvider(provider);
		entity.setTargetProduct(product);
		entity.setTargetBlueprintID(blueprintID);
		//list.set(row, entity);
	}
	public void clearData() {
		list.clear();
		fireTableChanged(null);
	}
	
	

}

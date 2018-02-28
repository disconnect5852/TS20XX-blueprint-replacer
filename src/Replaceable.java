import java.io.Serializable;



public class Replaceable implements Serializable  {

	private static final long serialVersionUID = 578883424351045868L;
	private String type;
	private String currentProvider;
	private String currentProduct;
	private String currentBlueprintID;
	private String desiredProvider;
	private String desiredProduct;
	private String desiredBlueprintID;
	private double heightOffset;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCurrentProvider() {
		return currentProvider;
	}
	public void setCurrentProvider(String currentProvider) {
		this.currentProvider = currentProvider;
	}
	public String getCurrentProduct() {
		return currentProduct;
	}
	public void setCurrentProduct(String currentPproduct) {
		this.currentProduct = currentPproduct;
	}
	public String getCurrentBlueprintID() {
		return currentBlueprintID;
	}
	public void setCurrentBlueprintID(String currentBlueprinID) {
		this.currentBlueprintID = currentBlueprinID;
	}
	public String getDesiredProvider() {
		return desiredProvider;
	}
	public void setDesiredProvider(String desiredProvider) {
		this.desiredProvider = desiredProvider;
	}
	public String getDesiredProduct() {
		return desiredProduct;
	}
	public void setDesiredProduct(String desiredPproduct) {
		this.desiredProduct = desiredPproduct;
	}
	public String getDesiredBlueprintID() {
		return desiredBlueprintID;
	}
	public void setDesiredBlueprintID(String desiredBlueprinID) {
		this.desiredBlueprintID = desiredBlueprinID;
	}
	
	public double getHeightOffset() {
		return heightOffset;
	}
	public void setHeightOffset(double heightOffset) {
		this.heightOffset = heightOffset;
	}
	public Replaceable(String type, String currentProvider,
			String currentPproduct, String currentBlueprinID,
			String desiredProvider, String desiredPproduct,
			String desiredBlueprinID, double heightOffset) {
		super();
		this.type = type;
		this.currentProvider = currentProvider;
		this.currentProduct = currentPproduct;
		this.currentBlueprintID = currentBlueprinID;
		this.desiredProvider = desiredProvider;
		this.desiredProduct = desiredPproduct;
		this.desiredBlueprintID = desiredBlueprinID;
		this.heightOffset=heightOffset;
	}
	
	public Replaceable(String type, String currentProvider, String currentPproduct, String currentBlueprinID) {
		super();
		this.type=type;
		this.currentProvider = currentProvider;
		this.currentProduct = currentPproduct;
		this.currentBlueprintID = currentBlueprinID;
		this.desiredProvider = null;
		this.desiredProduct = null;
		this.desiredBlueprintID = null;;
	}
	
	public Replaceable() {
		super();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((currentBlueprintID == null) ? 0 : currentBlueprintID
						.hashCode());
		result = prime * result
				+ ((currentProduct == null) ? 0 : currentProduct.hashCode());
		result = prime * result
				+ ((currentProvider == null) ? 0 : currentProvider.hashCode());
		result = prime
				* result
				+ ((desiredBlueprintID == null) ? 0 : desiredBlueprintID
						.hashCode());
		result = prime * result
				+ ((desiredProduct == null) ? 0 : desiredProduct.hashCode());
		result = prime * result
				+ ((desiredProvider == null) ? 0 : desiredProvider.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Replaceable other = (Replaceable) obj;
		if (currentBlueprintID == null) {
			if (other.currentBlueprintID != null)
				return false;
		} else if (!currentBlueprintID.equals(other.currentBlueprintID))
			return false;
		if (currentProduct == null) {
			if (other.currentProduct != null)
				return false;
		} else if (!currentProduct.equals(other.currentProduct))
			return false;
		if (currentProvider == null) {
			if (other.currentProvider != null)
				return false;
		} else if (!currentProvider.equals(other.currentProvider))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Replaceable [type=").append(type)
				.append(", currentProvider=").append(currentProvider)
				.append(", currentProduct=").append(currentProduct)
				.append(", currentBlueprintID=").append(currentBlueprintID)
				.append(", desiredProvider=").append(desiredProvider)
				.append(", desiredProduct=").append(desiredProduct)
				.append(", desiredBlueprintID=").append(desiredBlueprintID)
				.append(", heightOffset=").append(heightOffset).append("]");
		return builder.toString();
	}
	
	
}

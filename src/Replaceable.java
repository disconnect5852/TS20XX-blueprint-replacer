import java.io.Serializable;



public class Replaceable implements Serializable  {

	private static final long serialVersionUID = 578883424351045868L;
	private String type;
	private String currentProvider;
	private String currentProduct;
	private String currentBlueprintID;
	private String targetProvider;
	private String targetProduct;
	private String targetBlueprintID;
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
	public String getTargetProvider() {
		return targetProvider;
	}
	public void setTargetProvider(String targetProvider) {
		this.targetProvider = targetProvider;
	}
	public String getTargetProduct() {
		return targetProduct;
	}
	public void setTargetProduct(String targetPproduct) {
		this.targetProduct = targetPproduct;
	}
	public String getTargetBlueprintID() {
		return targetBlueprintID;
	}
	public void setTargetBlueprintID(String targetBlueprinID) {
		this.targetBlueprintID = targetBlueprinID;
	}
	
	public double getHeightOffset() {
		return heightOffset;
	}
	public void setHeightOffset(double heightOffset) {
		this.heightOffset = heightOffset;
	}
	public Replaceable(String type, String currentProvider,
			String currentPproduct, String currentBlueprinID,
			String targetProvider, String targetPproduct,
			String targetBlueprinID, double heightOffset) {
		super();
		this.type = type;
		this.currentProvider = currentProvider;
		this.currentProduct = currentPproduct;
		this.currentBlueprintID = currentBlueprinID;
		this.targetProvider = targetProvider;
		this.targetProduct = targetPproduct;
		this.targetBlueprintID = targetBlueprinID;
		this.heightOffset=heightOffset;
	}
	
	public Replaceable(String type, String currentProvider, String currentPproduct, String currentBlueprinID) {
		super();
		this.type=type;
		this.currentProvider = currentProvider;
		this.currentProduct = currentPproduct;
		this.currentBlueprintID = currentBlueprinID;
		this.targetProvider = null;
		this.targetProduct = null;
		this.targetBlueprintID = null;;
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
				+ ((targetBlueprintID == null) ? 0 : targetBlueprintID
						.hashCode());
		result = prime * result
				+ ((targetProduct == null) ? 0 : targetProduct.hashCode());
		result = prime * result
				+ ((targetProvider == null) ? 0 : targetProvider.hashCode());
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
				.append(", targetProvider=").append(targetProvider)
				.append(", targetProduct=").append(targetProduct)
				.append(", targetBlueprintID=").append(targetBlueprintID)
				.append(", heightOffset=").append(heightOffset).append("]");
		return builder.toString();
	}
	
	
}

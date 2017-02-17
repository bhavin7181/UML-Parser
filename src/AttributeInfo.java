
public class AttributeInfo {
	private String name="";
	private String dataType="";
	private String dataTypeArg="";
	private String rankSpecifier="";
	private String accessModifier="";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getAccessModifier() {
		return accessModifier;
	}
	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}
	public String getRankSpecifier() {
		return rankSpecifier;
	}
	public void setRankSpecifier(String rankSpecifier) {
		this.rankSpecifier = rankSpecifier;
	}
	public String getDataTypeArg() {
		return dataTypeArg;
	}
	public void setDataTypeArg(String dataTypeArg) {
		this.dataTypeArg = dataTypeArg;
	}
	@Override
	public boolean equals(Object obj) {
		AttributeInfo arg = (AttributeInfo)obj;
		if(this.name.equals(arg.getName()) && this.dataType.equals(arg.getDataType()) && this.dataTypeArg.equals(arg.getDataTypeArg()) 
				&& this.rankSpecifier.equals(arg.getRankSpecifier()) && this.accessModifier.equals(arg.getAccessModifier()))
		{
			return true;
		}
		return false;
	}
	
	
}

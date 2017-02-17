import java.util.ArrayList;
import java.util.List;

public class InterfaceInfo {
	
	private String name;
	private String accessModifier;
	private List<AttributeInfo> attributes;
	private List<MethodInfo> methods;
	private List<ClassInfo> implementers;
	
	public InterfaceInfo() {
		attributes = new ArrayList<>();
		methods = new ArrayList<>();
		implementers = new ArrayList<>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccessModifier() {
		return accessModifier;
	}
	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}
	public List<AttributeInfo> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<AttributeInfo> attributes) {
		this.attributes = attributes;
	}
	public List<MethodInfo> getMethods() {
		return methods;
	}
	public void setMethods(List<MethodInfo> methods) {
		this.methods = methods;
	}
	public List<ClassInfo> getImplementers() {
		return implementers;
	}
	public void setImplementers(List<ClassInfo> implementers) {
		this.implementers = implementers;
	}
}

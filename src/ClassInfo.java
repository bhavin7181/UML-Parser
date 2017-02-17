import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
	
	private String name;
	private String accessModifier;
	private List<AttributeInfo> attributes;
	private List<MethodInfo> methods;
	private ClassInfo parentClass;
	private List<InterfaceInfo> interfaces;
	
	public ClassInfo() {
		attributes = new ArrayList<AttributeInfo>();
		methods = new ArrayList<MethodInfo>();
		//parentClass = new ClassInfo();
		interfaces = new ArrayList<InterfaceInfo>();
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
	public List<InterfaceInfo> getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(List<InterfaceInfo> interfaces) {
		this.interfaces = interfaces;
	}
	public ClassInfo getParentClass() {
		return parentClass;
	}
	public void setParentClass(ClassInfo parentClass) {
		this.parentClass = parentClass;
	}
	
}
